package bus;

import dao.*;
import entity.*;
import utils.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * ============================================================
 *  PhienSuDungBUS  –  Tầng Business Logic cho Phiên Sử Dụng
 * ============================================================
 *
 *  Cách hoạt động ĐƠN GIẢN (không dùng Timer/Scheduler):
 *
 *    Mở phiên   →  lưu DB, xong (không đặt timer gì cả)
 *    Kết thúc   →  thủ công (nhân viên bấm nút)
 *    Kiểm tra   →  gọi kiemTraVaKetThucPhienQuaHan() khi:
 *                    [1] Nhân viên bấm Refresh
 *                    [2] Vừa đăng nhập (MainController.initialize)
 *
 *  Điều kiện phiên "quá hạn":
 *    [A] soDu = 0 VÀ không còn gói nào có giờ → hết sạch ngay
 *    [B] giờ hiện tại > giờ bắt đầu + (gioTuGoi + soDu/giaMoiGio) × 3600 giây
 *    → Thỏa [A] HOẶC [B] → kết thúc phiên + tạo hóa đơn đầy đủ
 */
public class PhienSuDungBUS {

    // ================================================================
    //  PHẦN 1: CÁC DAO
    // ================================================================

    private final PhienSuDungDAO        phienSuDungDAO;
    private final MayTinhDAO            mayTinhDAO;
    private final KhachHangDAO          khachHangDAO;
    private final GoiDichVuKhachHangDAO goiDichVuKhachHangDAO;
    private final SuDungDichVuDAO       suDungDichVuDAO;
    private final HoaDonDAO             hoaDonDAO;

    // ================================================================
    //  PHẦN 2: CONSTRUCTOR
    // ================================================================

    public PhienSuDungBUS(
            PhienSuDungDAO phienSuDungDAO,
            MayTinhDAO mayTinhDAO,
            KhachHangDAO khachHangDAO,
            GoiDichVuKhachHangDAO goiDichVuKhachHangDAO,
            SuDungDichVuDAO suDungDichVuDAO,
            HoaDonDAO hoaDonDAO) {

        this.phienSuDungDAO        = phienSuDungDAO;
        this.mayTinhDAO            = mayTinhDAO;
        this.khachHangDAO          = khachHangDAO;
        this.goiDichVuKhachHangDAO = goiDichVuKhachHangDAO;
        this.suDungDichVuDAO       = suDungDichVuDAO;
        this.hoaDonDAO             = hoaDonDAO;
    }

    // ================================================================
    //  PHẦN 3: KIỂM TRA VÀ KẾT THÚC PHIÊN QUÁ HẠN  ← TÍNH NĂNG CHÍNH
    // ================================================================

    /**
     * Quét tất cả phiên DANGCHOI, tìm phiên quá hạn và kết thúc chúng.
     *
     * Được gọi từ:
     *   • PhienSuDungController.handleRefresh()   → nhân viên bấm nút Refresh
     *   • MainController.initialize()             → mỗi lần đăng nhập vào hệ thống
     *
     * @return danh sách phiên vừa bị kết thúc (Controller dùng để hiện thông báo)
     */
    public List<PhienSuDung> kiemTraVaKetThucPhienQuaHan() {
        List<PhienSuDung> danhSachDaKetThuc = new ArrayList<>();

        // Bước 1: lấy tất cả phiên đang chơi từ DB
        List<PhienSuDung> danhSachDangChoi;
        try {
            danhSachDangChoi = phienSuDungDAO.getAllPhienDangChoi();
        } catch (Exception e) {
            System.err.println("[KiemTra] Lỗi lấy danh sách phiên: " + e.getMessage());
            return danhSachDaKetThuc; // trả về rỗng, không crash app
        }

        // Bước 2: xét từng phiên
        for (PhienSuDung phien : danhSachDangChoi) {
            try {
                if (laPhienQuaHan(phien)) {
                    System.out.printf("[KiemTra] Phiên %s (KH: %s) quá hạn → kết thúc%n",
                            phien.getMaPhien(), phien.getMaKH());

                    PhienSuDung ketQua = thucHienKetThuc(phien.getMaPhien());
                    danhSachDaKetThuc.add(ketQua);
                }
            } catch (Exception e) {
                // Ghi log lỗi nhưng tiếp tục xử lý các phiên còn lại
                System.err.println("[KiemTra] Lỗi kết thúc phiên "
                        + phien.getMaPhien() + ": " + e.getMessage());
            }
        }

        System.out.printf("[KiemTra] Xong: %d/%d phiên bị kết thúc tự động.%n",
                danhSachDaKetThuc.size(), danhSachDangChoi.size());

        return danhSachDaKetThuc;
    }

    /**
     * Kiểm tra một phiên có quá hạn không.
     *
     * Tổng giờ tối đa = giờ còn trong gói + soDu / giaMoiGio
     * Thời điểm hết tiền = gioBatDau + tổng giờ tối đa
     *
     * Quá hạn nếu:
     *   [A] Tổng giờ tối đa <= 0   (hết sạch tiền và gói)
     *   [B] Giờ hiện tại > thời điểm hết tiền
     */
    private boolean laPhienQuaHan(PhienSuDung phien) {
        try {
            KhachHang khachHang = khachHangDAO.getById(phien.getMaKH());
            if (khachHang == null) return false;

            double giaMoiGio = phien.getGiaMoiGio();
            if (giaMoiGio <= 0) return false; // máy miễn phí → không kết thúc tự động

            // Tính tổng giờ còn lại trong các gói
            double gioTuGoi = 0;
            try {
                for (GoiDichVuKhachHang goi :
                        goiDichVuKhachHangDAO.getByKhachHang(khachHang.getMakh())) {
                    if (goi != null && goi.getSogioconlai() > 0) {
                        gioTuGoi += goi.getSogioconlai();
                    }
                }
            } catch (Exception ignored) {}

            // Tính giờ có thể chơi từ số dư tiền
            double gioTuTien    = khachHang.getSodu() / giaMoiGio;
            double tongGioToiDa = gioTuGoi + gioTuTien;

            // ── Điều kiện [A]: hết sạch cả gói lẫn tiền ──────────────────
            if (tongGioToiDa <= 0) {
                System.out.printf("[KiemTra] ĐK[A] KH %s: hết tiền và hết gói%n",
                        khachHang.getMakh());

                khachHangDAO.updateSoDu(khachHang.getMakh(),0);
                return true;
            }

            // ── Điều kiện [B]: giờ hiện tại vượt thời điểm hết tiền ──────
            //    thoiDiemHetTien = gioBatDau + tongGioToiDa (giờ → giây)
            if (phien.getGioBatDau() != null) {
                long tongGiayToiDa = (long)(tongGioToiDa * 3600);
                LocalDateTime thoiDiemHetTien =
                        phien.getGioBatDau().plusSeconds(tongGiayToiDa);

                if (LocalDateTime.now().isAfter(thoiDiemHetTien)) {
                    System.out.printf("[KiemTra] ĐK[B] Phiên %s hết tiền lúc %s%n",
                            phien.getMaPhien(), thoiDiemHetTien);
                    return true;
                }
            }

            return false; // chưa quá hạn

        } catch (Exception e) {
            System.err.println("[KiemTra] Lỗi kiểm tra phiên "
                    + phien.getMaPhien() + ": " + e.getMessage());
            return false; // nếu lỗi → không tự ý kết thúc
        }
    }

    // ================================================================
    //  PHẦN 4: MỞ PHIÊN MỚI
    // ================================================================

    /**
     * Mở một phiên chơi mới cho khách hàng.
     *
     *   1. Kiểm tra quyền
     *   2. Validate input
     *   3. Kiểm tra khách chưa có phiên khác, máy đang trống
     *   4. Kiểm tra khách còn tiền / gói để chơi
     *   5. Tạo phiên, lưu DB, cập nhật trạng thái máy
     */
    public PhienSuDung moPhienMoi(String maKH, String maMay) throws Exception {
        PermissionHelper.requireMoPhien();
        String maNV = PermissionHelper.getCurrentMaNV();

        validateMoPhienInput(maKH, maMay);

        if (phienSuDungDAO.hasPhienDangChoi(maKH)) {
            throw new Exception("Khách hàng đang có phiên chơi khác.\nVui lòng kết thúc phiên cũ trước.");
        }
        if (phienSuDungDAO.isMayDangSuDung(maMay)) {
            throw new Exception("Máy " + maMay + " đang có người sử dụng.\nVui lòng chọn máy khác.");
        }

        KhachHang khachHang = khachHangDAO.getById(maKH);
        if (khachHang == null) throw new Exception("Không tìm thấy khách hàng: " + maKH);
        checkKhachHangCoTheChoi(khachHang);

        MayTinh mayTinh = mayTinhDAO.getById(maMay);

        // Tạo đối tượng phiên (các giá trị tính toán = 0, cập nhật khi kết thúc)
        PhienSuDung phienMoi = new PhienSuDung();
        phienMoi.setMaPhien(phienSuDungDAO.generateMaPhien());
        phienMoi.setMaKH(maKH);
        phienMoi.setMaMay(maMay);
        phienMoi.setMaNV(maNV);
        phienMoi.setGioBatDau(LocalDateTime.now());
        phienMoi.setGiaMoiGio(mayTinh.getGiamoigio());
        phienMoi.setTrangThai("DANGCHOI");
        phienMoi.setTongGio(0);
        phienMoi.setGioSuDungTuGoi(0);
        phienMoi.setGioSuDungTuTaiKhoan(0);
        phienMoi.setTienGioChoi(0);
        setLoaiThanhToan(phienMoi, maKH);

        if (!phienSuDungDAO.insert(phienMoi)) {
            throw new Exception("Không thể tạo phiên mới. Vui lòng thử lại.");
        }
        mayTinhDAO.chuyenDangDung(maMay.trim());

        PermissionHelper.logAction("MO_PHIEN",
                "MaPhien=" + phienMoi.getMaPhien() + ", MaKH=" + maKH + ", MaMay=" + maMay);
        return phienMoi;
    }

    // ================================================================
    //  PHẦN 5: KẾT THÚC PHIÊN
    // ================================================================

    /** Kết thúc phiên thủ công (nhân viên bấm nút "Kết thúc"). */
    public PhienSuDung ketThucPhien(String maPhien) throws Exception {
        PermissionHelper.requireKetThucPhien();
        return thucHienKetThuc(maPhien);
    }

    /**
     * Hàm TRUNG TÂM xử lý kết thúc phiên.
     * Dùng chung cho cả thủ công và tự động (kiemTraVaKetThucPhienQuaHan).
     *
     *   1. Lấy phiên từ DB, kiểm tra còn đang chơi
     *   2. Tính tổng giờ chơi
     *   3. Phân tách giờ: từ gói / từ tài khoản, cập nhật số giờ còn lại trong gói
     *   4. Tính tiền (chỉ phần từ tài khoản)
     *   5. Cập nhật DB (trangThai → DAKETTHUC)
     *   6. Trả máy về TRONG
     *   7. Tạo hóa đơn và trừ tiền khách
     */
    private PhienSuDung thucHienKetThuc(String maPhien) throws Exception {

        // Bước 1
        PhienSuDung phien = phienSuDungDAO.getByMaPhien(maPhien);
        if (phien == null)       throw new Exception("Không tìm thấy phiên: " + maPhien);
        if (!phien.isDangChoi()) throw new Exception("Phiên này đã kết thúc rồi.");

        // Bước 2: tính tổng giờ chơi
        LocalDateTime gioKetThuc = LocalDateTime.now();
        double tongGio = tinhTongGio(phien.getGioBatDau(), gioKetThuc);

        // Bước 3: phân tách giờ từ gói vs từ tài khoản
        GioSuDungResult gioResult = tinhGioSuDung(phien, tongGio);

        // Bước 4: tính tiền (chỉ phần giờ từ tài khoản mới tính tiền)
        double giaMoiGio   = mayTinhDAO.getById(phien.getMaMay()).getGiamoigio();
        double tienGioChoi = gioResult.gioTuTaiKhoan * giaMoiGio;

        if (gioResult.gioTuGoi > 0 && gioResult.gioTuTaiKhoan > 0)
            phien.setLoaiThanhToan("KETHOP");
        else if (gioResult.gioTuGoi > 0)
            phien.setLoaiThanhToan("GOI");
        else
            phien.setLoaiThanhToan("TAIKHOAN");

        // Bước 5: cập nhật DB
        boolean ok = phienSuDungDAO.ketThucPhien(maPhien, gioKetThuc, tongGio,
                gioResult.gioTuGoi, gioResult.gioTuTaiKhoan, tienGioChoi);
        if (!ok) throw new Exception("Không thể cập nhật phiên. Vui lòng thử lại.");

        // Bước 6: trả máy về trống
        mayTinhDAO.chuyenTrong(phien.getMaMay());

        // Cập nhật object để trả về UI
        phien.setGioKetThuc(gioKetThuc);
        phien.setTongGio(tongGio);
        phien.setGioSuDungTuGoi(gioResult.gioTuGoi);
        phien.setGioSuDungTuTaiKhoan(gioResult.gioTuTaiKhoan);
        phien.setGiaMoiGio(giaMoiGio);
        phien.setTienGioChoi(tienGioChoi);
        phien.setTrangThai("DAKETTHUC");

        // Bước 7: tạo hóa đơn và trừ tiền
        KhachHang khachHang = khachHangDAO.getById(phien.getMaKH());
        if (khachHang == null) throw new Exception("Không tìm thấy khách hàng.");

        HoaDon hoaDon = tinhTienVaTaoHoaDon(phien, tienGioChoi, khachHang);
        hoaDonDAO.them(hoaDon);

        PermissionHelper.logAction("KET_THUC_PHIEN",
                "MaPhien="     + maPhien
                        + ", TongGio=" + String.format("%.2f", tongGio)
                        + ", Tien="    + String.format("%.0f", tienGioChoi)
                        + ", HD="      + hoaDon.getTrangThai());
        return phien;
    }

    // ================================================================
    //  PHẦN 6: TÍNH TIỀN VÀ TẠO HÓA ĐƠN
    // ================================================================

    /**
     * Tính tổng tiền, trừ số dư khách, tạo hóa đơn.
     *
     * thanhTien = tienGioChoi + tienDichVu
     *
     * Quy tắc trừ tiền:
     *   soDu >= thanhTien → trừ đủ        → hóa đơn DATHANHTOAN
     *   soDu <  thanhTien → trừ hết soDu  → hóa đơn CHUATHANHTOAN
     *   soDu KHÔNG BAO GIỜ xuống âm
     */
    private HoaDon tinhTienVaTaoHoaDon(PhienSuDung phien, double tienGioChoi,
                                       KhachHang khachHang) throws Exception {
        double tienDichVu = 0;
        try {
            tienDichVu = suDungDichVuDAO.tinhTongTienKhachHang(phien.getMaKH());
        } catch (Exception e) {
            System.err.println("[TinhTien] Lỗi tính tiền dịch vụ: " + e.getMessage());
        }

        double thanhTien   = tienGioChoi + tienDichVu;
        double soDuHienTai = khachHang.getSodu();

        HoaDon hoaDon = new HoaDon();
        hoaDon.setMaHD(hoaDonDAO.taoMaHoaDonTuDong());
        hoaDon.setMaPhien(phien.getMaPhien());
        hoaDon.setMaKH(phien.getMaKH());
        hoaDon.setMaNV(phien.getMaNV() != null ? phien.getMaNV() : "");
        hoaDon.setNgayLap(phien.getGioKetThuc() != null ? phien.getGioKetThuc() : LocalDateTime.now());
        hoaDon.setTienGioChoi(tienGioChoi);
        hoaDon.setTienDichVu(tienDichVu);
        hoaDon.setGiamGia(0.0);
        try { hoaDon.setTongTien(thanhTien);      } catch (Exception ignored) {}
        try { hoaDon.setPhuongThucTT("TAIKHOAN"); } catch (Exception ignored) {}

        if (soDuHienTai >= thanhTien) {
            // ✅ Đủ tiền
            hoaDon.setThanhToan(thanhTien);
            hoaDon.setTrangThai("DATHANHTOAN");
            khachHangDAO.updateSoDu(khachHang.getMakh(), soDuHienTai - thanhTien);
            System.out.printf("[TinhTien] KH %s: %.0f - %.0f = %.0f ₫ (ĐỦ)%n",
                    khachHang.getMakh(), soDuHienTai, thanhTien, soDuHienTai - thanhTien);
        } else {
            // ⚠ Không đủ tiền: trừ hết soDu, phần còn lại ghi nợ
            double tienThucTra = Math.max(0, soDuHienTai);
            hoaDon.setThanhToan(tienThucTra);
            hoaDon.setTrangThai("CHUATHANHTOAN");
            khachHangDAO.updateSoDu(khachHang.getMakh(), 0);
            System.out.printf("[TinhTien] KH %s: thiếu %.0f ₫ (NỢ)%n",
                    khachHang.getMakh(), thanhTien - tienThucTra);
        }

        return hoaDon;
    }

    // ================================================================
    //  PHẦN 7: CÁC HÀM QUERY
    // ================================================================

    public List<PhienSuDung> getAllPhien() throws Exception {
        PermissionHelper.requireNhanVien();
        return phienSuDungDAO.getAll();
    }

    public List<PhienSuDung> getAllPhienDangChoi() throws Exception {
        PermissionHelper.requireNhanVien();
        return phienSuDungDAO.getAllPhienDangChoi();
    }

    public boolean hasPhienDangChoi(String maKH) throws Exception {
        return phienSuDungDAO.hasPhienDangChoi(maKH);
    }

    public PhienSuDung getPhienDangChoiByKhachHang(String maKH) throws Exception {
        return phienSuDungDAO.getPhienDangChoiByKhachHang(maKH);
    }

    public PhienSuDung getPhienDangChoiByMay(String maMay) throws Exception {
        return phienSuDungDAO.getPhienDangChoiByMay(maMay);
    }

    public List<PhienSuDung> getLichSuPhienByKhachHang(String maKH) throws Exception {
        PermissionHelper.requireNhanVien();
        return phienSuDungDAO.getPhienByKhachHang(maKH);
    }

    public PhienSuDung getPhienById(String maPhien) throws Exception {
        PhienSuDung p = phienSuDungDAO.getByMaPhien(maPhien);
        if (p == null) throw new Exception("Không tìm thấy phiên: " + maPhien);
        return p;
    }

    public int countPhienDangChoi() throws Exception {
        return phienSuDungDAO.countByTrangThai("DANGCHOI");
    }

    public double getTongGioChoiByKhachHang(String maKH) throws Exception {
        return phienSuDungDAO.getTongGioChoiByKhachHang(maKH);
    }

    public double getTongDoanhThuGioChoi(LocalDateTime tuNgay, LocalDateTime denNgay) throws Exception {
        PermissionHelper.requireNhanVien();
        return phienSuDungDAO.getTongDoanhThuGioChoi(tuNgay, denNgay);
    }

    public List<PhienSuDung> getPhienByDateRange(LocalDateTime tuNgay, LocalDateTime denNgay) throws Exception {
        PermissionHelper.requireNhanVien();
        return phienSuDungDAO.getPhienByDateRange(tuNgay, denNgay);
    }

    public PhienSuDung chuyenMay(String maPhien, String maMayMoi) throws Exception {
        PermissionHelper.requireMoPhien();
        PhienSuDung phien = phienSuDungDAO.getByMaPhien(maPhien);
        if (phien == null)       throw new Exception("Không tìm thấy phiên: " + maPhien);
        if (!phien.isDangChoi()) throw new Exception("Phiên này đã kết thúc.");
        MayTinh mayMoi = mayTinhDAO.getById(maMayMoi);
        if (mayMoi == null || !"TRONG".equals(mayMoi.getTrangthai()))
            throw new Exception("Máy " + maMayMoi + " không khả dụng.");
        String mayCu = phien.getMaMay();
        phien.setMaMay(maMayMoi);
        phien.setGiaMoiGio(mayMoi.getGiamoigio());
        if (!phienSuDungDAO.update(phien)) throw new Exception("Không thể cập nhật phiên.");
        mayTinhDAO.chuyenTrong(mayCu);
        mayTinhDAO.chuyenDangDung(maMayMoi);
        PermissionHelper.logAction("CHUYEN_MAY",
                "MaPhien=" + maPhien + ", MayCu=" + mayCu + ", MayMoi=" + maMayMoi);
        return phien;
    }

    // ================================================================
    //  PHẦN 8: CÁC HÀM HELPER PRIVATE
    // ================================================================

    /** Xác định loại thanh toán khi mở phiên: GOI hoặc TAIKHOAN */
    private void setLoaiThanhToan(PhienSuDung phienMoi, String maKH) {
        try {
            for (GoiDichVuKhachHang goi : goiDichVuKhachHangDAO.getByKhachHang(maKH)) {
                if (goi != null && goi.getSogioconlai() > 0) {
                    phienMoi.setLoaiThanhToan("GOI");
                    phienMoi.setMaGoiKH(goi.getMagoikh());
                    return;
                }
            }
        } catch (Exception ignored) {}
        phienMoi.setLoaiThanhToan("TAIKHOAN");
    }

    /**
     * Phân tách tổng giờ thành giờ từ gói và giờ từ tài khoản.
     * Ưu tiên dùng gói trước. Cập nhật số giờ còn lại của gói trong DB.
     */
    private GioSuDungResult tinhGioSuDung(PhienSuDung phien, double tongGio) {
        double gioTuGoi = 0.0;
        if (phien.getMaGoiKH() != null) {
            try {
                GoiDichVuKhachHang goi =
                        goiDichVuKhachHangDAO.getByMaGoiKhachHang(phien.getMaGoiKH());
                if (goi != null && goi.getSogioconlai() > 0) {
                    gioTuGoi = Math.min(tongGio, goi.getSogioconlai());
                    goi.setSogioconlai(goi.getSogioconlai() - gioTuGoi);
                    goiDichVuKhachHangDAO.update(goi);
                }
            } catch (Exception ignored) {}
        }
        return new GioSuDungResult(gioTuGoi, tongGio - gioTuGoi);
    }

    /** Validate input khi mở phiên: rỗng, máy trống, khách hoạt động */
    private void validateMoPhienInput(String maKH, String maMay) throws Exception {
        if (maKH  == null || maKH.trim().isEmpty())  throw new Exception("Vui lòng chọn khách hàng.");
        if (maMay == null || maMay.trim().isEmpty())  throw new Exception("Vui lòng chọn máy tính.");
        MayTinh mayTinh = mayTinhDAO.getById(maMay);
        if (mayTinh == null)
            throw new Exception("Không tìm thấy máy: " + maMay);
        if (!"TRONG".equals(mayTinh.getTrangthai()))
            throw new Exception("Máy " + maMay + " không khả dụng (" + mayTinh.getTrangthai() + ").");
        KhachHang kh = khachHangDAO.getById(maKH);
        if (kh == null)
            throw new Exception("Không tìm thấy khách hàng: " + maKH);
        if (!"HOATDONG".equals(kh.getTrangthai()))
            throw new Exception("Tài khoản khách hàng đã bị khóa.");
    }

    /** Kiểm tra khách còn đủ điều kiện chơi: có gói giờ HOẶC soDu > 0 */
    private void checkKhachHangCoTheChoi(KhachHang kh) throws Exception {
        boolean conGoi = false;
        try {
            for (GoiDichVuKhachHang goi : goiDichVuKhachHangDAO.getByKhachHang(kh.getMakh()))
                if (goi != null && goi.getSogioconlai() > 0) { conGoi = true; break; }
        } catch (Exception ignored) {}
        if (!conGoi && kh.getSodu() <= 0)
            throw new Exception("Khách hàng không thể chơi.\n"
                    + "Số dư: 0 ₫ và không có gói dịch vụ.\n"
                    + "Vui lòng nạp tiền hoặc mua gói trước.");
    }

    /** Tính tổng giờ chơi. Ví dụ: 90 phút → 1.5 giờ */
    private double tinhTongGio(LocalDateTime batDau, LocalDateTime ketThuc) {
        return ChronoUnit.MINUTES.between(batDau, ketThuc) / 60.0;
    }

    // ================================================================
    //  PHẦN 9: CLASS NỘI BỘ
    // ================================================================

    /**
     * Kết quả phân tách giờ sử dụng.
     * Dùng vì Java không có tuple, cần trả 2 giá trị cùng lúc.
     */
    private static class GioSuDungResult {
        final double gioTuGoi;      // giờ trừ từ gói, không tính tiền
        final double gioTuTaiKhoan; // giờ tính tiền từ số dư

        GioSuDungResult(double gioTuGoi, double gioTuTaiKhoan) {
            this.gioTuGoi      = gioTuGoi;
            this.gioTuTaiKhoan = gioTuTaiKhoan;
        }
    }
}