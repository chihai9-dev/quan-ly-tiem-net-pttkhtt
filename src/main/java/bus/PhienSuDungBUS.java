package bus;

import dao.*;
import entity.*;
import untils.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Business Logic Layer cho Phiên Sử Dụng
 * Xử lý logic nghiệp vụ: Mở phiên, kết thúc phiên, tính tiền
 *
 * @author QuanLyTiemNet Team
 * @version 2.1 - Extended
 */
public class PhienSuDungBUS {

    // ==================== DEPENDENCIES ====================
    private final PhienSuDungDAO phienSuDungDAO;
    private final MayTinhDAO mayTinhDAO;
    private final KhachHangDAO khachHangDAO;
    private final GoiDichVuKhachHangDAO goiDichVuKhachHangDAO;
    private final SuDungDichVuDAO suDungDichVuDAO;
    private final HoaDonDAO hoaDonDAO;

    // ==================== CONSTRUCTOR ====================
    /**
     * Constructor với Dependency Injection
     */
    public PhienSuDungBUS(
            PhienSuDungDAO phienSuDungDAO,
            MayTinhDAO mayTinhDAO,
            KhachHangDAO khachHangDAO,
            GoiDichVuKhachHangDAO goiDichVuKhachHangDAO,
            SuDungDichVuDAO suDungDichVuDAO,
            HoaDonDAO hoaDonDAO) {
        this.phienSuDungDAO = phienSuDungDAO;
        this.mayTinhDAO = mayTinhDAO;
        this.khachHangDAO = khachHangDAO;
        this.goiDichVuKhachHangDAO = goiDichVuKhachHangDAO;
        this.suDungDichVuDAO = suDungDichVuDAO;
        this.hoaDonDAO = hoaDonDAO;
    }

    // ==================== CORE BUSINESS METHODS ====================

    /**
     * Mở phiên chơi mới cho khách hàng
     *
     * Quy trình:
     * 1. Kiểm tra quyền: QUANLY hoặc NHANVIEN
     * 2. Validate: Khách hàng tồn tại, máy khả dụng
     * 3. Kiểm tra: Khách hàng chưa có phiên đang chơi
     * 4. Kiểm tra: Máy chưa có người sử dụng
     * 5. Kiểm tra: Khách hàng có thể chơi (có tiền hoặc có gói)
     * 6. Tạo phiên mới với trạng thái = DANGCHOI
     * 7. Cập nhật máy: TrangThai = DANGDUNG
     *
     * @param maKH Mã khách hàng
     * @param maMay Mã máy tính
     * @return PhienSuDung vừa tạo
     * @throws Exception Nếu có lỗi xảy ra
     */
    public PhienSuDung moPhienMoi(String maKH, String maMay) throws Exception {
        // 1. Kiểm tra phân quyền
        PermissionHelper.requireMoPhien();
        String maNV = PermissionHelper.getCurrentMaNV();

        // 2. Validate dữ liệu đầu vào
        validateMoPhienInput(maKH, maMay);

        // 3. Kiểm tra khách hàng chưa có phiên đang chơi
        if (phienSuDungDAO.hasPhienDangChoi(maKH)) {
            throw new Exception(
                    "Khách hàng đang có phiên chơi khác.\n" +
                            "Vui lòng kết thúc phiên cũ trước khi mở phiên mới."
            );
        }

        // 4. Kiểm tra máy chưa có người sử dụng
        if (phienSuDungDAO.isMayDangSuDung(maMay)) {
            throw new Exception(
                    "Máy " + maMay + " đang có người sử dụng.\n" +
                            "Vui lòng chọn máy khác."
            );
        }

        // 5. Lấy thông tin khách hàng
        KhachHang kh = khachHangDAO.getById(maKH);
        if (kh == null) {
            throw new Exception("Không tìm thấy thông tin khách hàng: " + maKH);
        }

        // 6. Kiểm tra khách hàng có thể chơi
        checkKhachHangCoTheChoi(kh);

        // 7. Lấy giá máy
        MayTinh may = mayTinhDAO.getById(maMay);
        double giaMoiGio = may.getGiamoigio();

        // 8. Tạo phiên mới
        PhienSuDung phienMoi = new PhienSuDung();
        phienMoi.setMaPhien(phienSuDungDAO.generateMaPhien());
        phienMoi.setMaKH(maKH);
        phienMoi.setMaMay(maMay);
        phienMoi.setMaNV(maNV);
        phienMoi.setGioBatDau(LocalDateTime.now());
        phienMoi.setGiaMoiGio(giaMoiGio);
        phienMoi.setTrangThai("DANGCHOI");

        // 9. Xác định loại thanh toán (ưu tiên gói trước)
        setLoaiThanhToan(phienMoi, maKH);

        // 10. Lưu phiên vào DB
        if (!phienSuDungDAO.insert(phienMoi)) {
            throw new Exception(
                    "Không thể tạo phiên mới.\n" +
                            "Vui lòng thử lại hoặc liên hệ quản trị viên."
            );
        }

        // 11. Cập nhật trạng thái máy
        may.setTrangthai("DANGDUNG");
        mayTinhDAO.chuyenDangDung(maMay.trim());

        // 12. Log action
        PermissionHelper.logAction("MO_PHIEN",
                "MaPhien=" + phienMoi.getMaPhien() +
                        ", MaKH=" + maKH +
                        ", MaMay=" + maMay +
                        ", LoaiThanhToan=" + phienMoi.getLoaiThanhToan()
        );

        return phienMoi;
    }

    /**
     * Kết thúc phiên chơi
     *
     * @param maPhien Mã phiên cần kết thúc
     * @return PhienSuDung đã kết thúc
     * @throws Exception Nếu có lỗi xảy ra
     */
    public PhienSuDung ketThucPhien(String maPhien) throws Exception {
        // 1. Kiểm tra phân quyền
        PermissionHelper.requireKetThucPhien();

        // 2. Lấy thông tin phiên
        PhienSuDung phienSuDung = phienSuDungDAO.getByMaPhien(maPhien);

        if (phienSuDung == null) {
            throw new Exception("Không tìm thấy phiên với mã: " + maPhien);
        }

        if (!phienSuDung.isDangChoi()) {
            throw new Exception(
                    "Phiên này đã kết thúc hoặc không hợp lệ.\n" +
                            "Trạng thái hiện tại: " + phienSuDung.getTrangThai()
            );
        }

        // 3. Tính tổng giờ chơi
        LocalDateTime gioKetThuc = LocalDateTime.now();
        phienSuDung.setGioKetThuc(gioKetThuc);
        double tongGio = tinhTongGio(phienSuDung.getGioBatDau(), gioKetThuc);

        // 4. Tính giờ từ gói và từ tài khoản
        GioSuDungResult gioResult = tinhGioSuDung(phienSuDung, tongGio);

        phienSuDung.setGioSuDungTuGoi(gioResult.gioTuGoi);
        phienSuDung.setGioSuDungTuTaiKhoan(gioResult.gioTuTaiKhoan);
        phienSuDung.setTongGio(tongGio);

        // 5. Xác định loại thanh toán cuối cùng
        if (gioResult.gioTuGoi > 0 && gioResult.gioTuTaiKhoan > 0) {
            phienSuDung.setLoaiThanhToan("KETHOP");
        } else if (gioResult.gioTuGoi > 0) {
            phienSuDung.setLoaiThanhToan("GOI");
        } else {
            phienSuDung.setLoaiThanhToan("TAIKHOAN");
        }

        // 6. Cập nhật trạng thái phiên
        phienSuDung.setTrangThai("DAKETTHUC");
        if (!phienSuDungDAO.update(phienSuDung)) {
            throw new Exception("Không thể cập nhật phiên. Vui lòng thử lại.");
        }

        // 7. Cập nhật trạng thái máy về TRONG
        mayTinhDAO.chuyenTrong(phienSuDung.getMaMay());

        // 8. Tính tiền và tạo hóa đơn
        double giaMoiGio = mayTinhDAO.getById(phienSuDung.getMaMay()).getGiamoigio();
        double tienGioChoi = gioResult.gioTuTaiKhoan * giaMoiGio;

        phienSuDung.setGiaMoiGio(giaMoiGio);
        phienSuDung.setTienGioChoi(tienGioChoi);

        // 9. Lấy thông tin khách hàng
        KhachHang kh = khachHangDAO.getById(phienSuDung.getMaKH());
        if (kh == null) {
            throw new Exception("Không tìm thấy thông tin khách hàng");
        }

        // 10. Tạo hóa đơn
        HoaDon hd = taoHoaDon(phienSuDung, tienGioChoi, kh);

        // 11. Lưu hóa đơn
        hoaDonDAO.them(hd);

        // 12. Log action
        PermissionHelper.logAction("KET_THUC_PHIEN",
                "MaPhien=" + maPhien +
                        ", TongGio=" + String.format("%.2f", tongGio) +
                        ", TongTien=" + String.format("%.0f", hd.getThanhToan()) +
                        ", TrangThaiHD=" + hd.getTrangThai()
        );

        return phienSuDung;
    }

    /**
     * Chuyển máy cho khách hàng đang chơi
     *
     * @param maPhien Mã phiên đang chơi
     * @param maMayMoi Mã máy mới
     * @return PhienSuDung đã cập nhật
     * @throws Exception Nếu có lỗi
     */
    public PhienSuDung chuyenMay(String maPhien, String maMayMoi) throws Exception {
        PermissionHelper.requireMoPhien();

        // Kiểm tra phiên
        PhienSuDung phien = phienSuDungDAO.getByMaPhien(maPhien);
        if (phien == null) {
            throw new Exception("Không tìm thấy phiên: " + maPhien);
        }

        if (!phien.isDangChoi()) {
            throw new Exception("Phiên này đã kết thúc. Không thể chuyển máy.");
        }

        // Kiểm tra máy mới
        MayTinh mayMoi = mayTinhDAO.getById(maMayMoi);
        if (mayMoi == null) {
            throw new Exception("Không tìm thấy máy: " + maMayMoi);
        }

        if (!"TRONG".equals(mayMoi.getTrangthai())) {
            throw new Exception(
                    "Máy " + maMayMoi + " không khả dụng.\n" +
                            "Vui lòng chọn máy khác."
            );
        }

        // Lưu máy cũ
        String mayCu = phien.getMaMay();

        // Cập nhật phiên
        phien.setMaMay(maMayMoi);
        phien.setGiaMoiGio(mayMoi.getGiamoigio());

        if (!phienSuDungDAO.update(phien)) {
            throw new Exception("Không thể cập nhật phiên");
        }

        // Cập nhật trạng thái máy cũ -> TRONG
        mayTinhDAO.chuyenTrong(mayCu);

        // Cập nhật trạng thái máy mới -> DANGDUNG
        mayTinhDAO.chuyenDangDung(maMayMoi);

        PermissionHelper.logAction("CHUYEN_MAY",
                "MaPhien=" + maPhien + ", MayCu=" + mayCu + ", MayMoi=" + maMayMoi
        );

        return phien;
    }




    // ==================== QUERY & STATISTICS METHODS ====================

    /**
     * Kiểm tra khách hàng có phiên đang chơi không
     */
    public boolean hasPhienDangChoi(String maKH) throws Exception {
        return phienSuDungDAO.hasPhienDangChoi(maKH);
    }

    /**
     * Lấy phiên đang chơi của khách hàng
     */
    public PhienSuDung getPhienDangChoiByKhachHang(String maKH) throws Exception {
        return phienSuDungDAO.getPhienDangChoiByKhachHang(maKH);
    }

    /**
     * Lấy phiên đang chơi trên máy
     */
    public PhienSuDung getPhienDangChoiByMay(String maMay) throws Exception {
        return phienSuDungDAO.getPhienDangChoiByMay(maMay);
    }

    /**
     * Lấy tất cả phiên đang chơi
     */
    public List<PhienSuDung> getAllPhienDangChoi() throws Exception {
        PermissionHelper.requireNhanVien();
        return phienSuDungDAO.getAllPhienDangChoi();
    }

    /**
     * Lấy lịch sử phiên của khách hàng
     */
    public List<PhienSuDung> getLichSuPhienByKhachHang(String maKH) throws Exception {
        PermissionHelper.requireNhanVien();
        return phienSuDungDAO.getPhienByKhachHang(maKH);
    }

    /**
     * Lấy lịch sử phiên theo máy
     */
    public List<PhienSuDung> getLichSuPhienByMay(String maMay) throws Exception {
        PermissionHelper.requireNhanVien();
        return phienSuDungDAO.getPhienByMay(maMay);
    }

    /**
     * Lấy tất cả phiên sử dụng
     */
    public List<PhienSuDung> getAllPhien() throws Exception {
        PermissionHelper.requireNhanVien();
        return phienSuDungDAO.getAll();
    }

    /**
     * Lấy phiên theo khoảng thời gian
     */
    public List<PhienSuDung> getPhienByDateRange(LocalDateTime tuNgay, LocalDateTime denNgay)
            throws Exception {
        PermissionHelper.requireNhanVien();
        return phienSuDungDAO.getPhienByDateRange(tuNgay, denNgay);
    }

    /**
     * Lấy phiên đã kết thúc trong khoảng thời gian
     */
    public List<PhienSuDung> getPhienDaKetThucByDateRange(
            LocalDateTime tuNgay, LocalDateTime denNgay) throws Exception {
        PermissionHelper.requireNhanVien();
        return phienSuDungDAO.getPhienDaKetThucByDateRange(tuNgay, denNgay);
    }


    /**
     * Lấy thông tin chi tiết phiên
     */
    public PhienSuDung getPhienById(String maPhien) throws Exception {
        PhienSuDung phien = phienSuDungDAO.getByMaPhien(maPhien);

        if (phien == null) {
            throw new Exception("Không tìm thấy phiên: " + maPhien);
        }

        return phien;
    }

    /**
     * Đếm số phiên đang chơi
     */
    public int countPhienDangChoi() throws Exception {
        return phienSuDungDAO.countByTrangThai("DANGCHOI");
    }

    /**
     * Đếm số phiên theo trạng thái
     */
    public int countPhienByTrangThai(String trangThai) throws Exception {
        return phienSuDungDAO.countByTrangThai(trangThai);
    }

    /**
     * Tính tổng giờ chơi của khách hàng
     */
    public double getTongGioChoiByKhachHang(String maKH) throws Exception {
        return phienSuDungDAO.getTongGioChoiByKhachHang(maKH);
    }

    /**
     * Tính tổng doanh thu từ giờ chơi trong khoảng thời gian
     */
    public double getTongDoanhThuGioChoi(LocalDateTime tuNgay, LocalDateTime denNgay)
            throws Exception {
        PermissionHelper.requireNhanVien();
        return phienSuDungDAO.getTongDoanhThuGioChoi(tuNgay, denNgay);
    }

    /**
     * Lấy top máy được sử dụng nhiều nhất
     */
    public List<Map<String, Object>> getTopMaySuDung(int top) throws Exception {
        PermissionHelper.requireNhanVien();

        List<Map<String, Object>> result = new ArrayList<>();
        List<PhienSuDung> allPhien = phienSuDungDAO.getAll();

        // Đếm số lần sử dụng của mỗi máy
        Map<String, Integer> countMap = new HashMap<>();
        Map<String, Double> gioMap = new HashMap<>();

        for (PhienSuDung phien : allPhien) {
            if ("DAKETTHUC".equals(phien.getTrangThai())) {
                String maMay = phien.getMaMay();
                countMap.put(maMay, countMap.getOrDefault(maMay, 0) + 1);
                gioMap.put(maMay, gioMap.getOrDefault(maMay, 0.0) + phien.getTongGio());
            }
        }

        // Tạo danh sách kết quả
        for (Map.Entry<String, Integer> entry : countMap.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            String maMay = entry.getKey();

            try {
                MayTinh may = mayTinhDAO.getById(maMay);
                if (may != null) {
                    item.put("maMay", maMay);
                    item.put("tenMay", may.getTenmay());
                    item.put("soLanDung", entry.getValue());
                    item.put("tongGio", gioMap.get(maMay));
                    result.add(item);
                }
            } catch (Exception e) {
                // Skip nếu không tìm thấy máy
            }
        }

        // Sắp xếp theo số lần dùng
        result.sort((a, b) ->
                Integer.compare((Integer)b.get("soLanDung"), (Integer)a.get("soLanDung"))
        );

        // Lấy top
        return result.size() > top ? result.subList(0, top) : result;
    }

    /**
     * Thống kê phiên theo ngày
     */
    public Map<String, Object> thongKePhienTheoNgay(LocalDateTime ngay) throws Exception {
        PermissionHelper.requireNhanVien();

        LocalDateTime batDauNgay = ngay.toLocalDate().atStartOfDay();
        LocalDateTime ketThucNgay = batDauNgay.plusDays(1).minusSeconds(1);

        List<PhienSuDung> phienTrongNgay = phienSuDungDAO.getPhienByDateRange(
                batDauNgay, ketThucNgay
        );

        Map<String, Object> thongKe = new HashMap<>();
        thongKe.put("ngay", ngay.toLocalDate().toString());
        thongKe.put("tongSoPhien", phienTrongNgay.size());

        int soPhienDaKetThuc = 0;
        int soPhienDangChoi = 0;
        double tongGioChoi = 0.0;
        double tongDoanhThu = 0.0;

        for (PhienSuDung phien : phienTrongNgay) {
            if ("DAKETTHUC".equals(phien.getTrangThai())) {
                soPhienDaKetThuc++;
                tongGioChoi += phien.getTongGio();
                tongDoanhThu += phien.getTienGioChoi();
            } else if ("DANGCHOI".equals(phien.getTrangThai())) {
                soPhienDangChoi++;
            }
        }

        thongKe.put("soPhienDaKetThuc", soPhienDaKetThuc);
        thongKe.put("soPhienDangChoi", soPhienDangChoi);
        thongKe.put("tongGioChoi", tongGioChoi);
        thongKe.put("tongDoanhThu", tongDoanhThu);
        thongKe.put("gioChoiTrungBinh",
                soPhienDaKetThuc > 0 ? tongGioChoi / soPhienDaKetThuc : 0.0
        );

        return thongKe;
    }

    /**
     * Kiểm tra khách hàng có đủ tiền để chơi thêm X giờ không
     */
    public boolean kiemTraDuTienChoGio(String maKH, double soGio) throws Exception {
        KhachHang kh = khachHangDAO.getById(maKH);
        if (kh == null) {
            throw new Exception("Không tìm thấy khách hàng");
        }

        // Giả sử giá trung bình 10,000 VNĐ/giờ
        double giaUocTinh = 10000.0;
        double tienCanThiet = soGio * giaUocTinh;

        return kh.getSodu() >= tienCanThiet;
    }

    /**
     * Ước tính thời gian có thể chơi với số dư hiện tại
     */
    public double uocTinhGioCoTheChoi(String maKH) throws Exception {
        KhachHang kh = khachHangDAO.getById(maKH);
        if (kh == null) {
            throw new Exception("Không tìm thấy khách hàng");
        }

        // Kiểm tra gói còn hạn
        List<GoiDichVuKhachHang> listGoi = goiDichVuKhachHangDAO.getByKhachHang(maKH);
        double gioTuGoi = 0.0;

        for (GoiDichVuKhachHang goi : listGoi) {
            if (goi != null && goi.getSogioconlai() > 0) {
                gioTuGoi += goi.getSogioconlai();
            }
        }

        // Ước tính giờ từ tiền (giá trung bình 10,000/giờ)
        double giaUocTinh = 10000.0;
        double gioTuTien = kh.getSodu() / giaUocTinh;

        return gioTuGoi + gioTuTien;
    }

    // ==================== HELPER METHODS ====================

    /**
     * Xác định loại thanh toán cho phiên mới
     * Ưu tiên sử dụng gói còn hạn trước
     */
    private void setLoaiThanhToan(PhienSuDung phienMoi, String maKH) throws Exception {
        try {
            List<GoiDichVuKhachHang> listGoiConHan = goiDichVuKhachHangDAO.getByKhachHang(maKH);
            boolean isConHan = false;

            for (GoiDichVuKhachHang goiConHan : listGoiConHan) {
                if (goiConHan != null && goiConHan.getSogioconlai() > 0) {
                    phienMoi.setLoaiThanhToan("GOI");
                    phienMoi.setMaGoiKH(goiConHan.getMagoikh());
                    isConHan = true;
                    break;
                }
            }

            if (!isConHan) {
                phienMoi.setLoaiThanhToan("TAIKHOAN");
            }
        } catch (Exception e) {
            phienMoi.setLoaiThanhToan("TAIKHOAN");
        }
    }

    /**
     * Tính giờ sử dụng từ gói và từ tài khoản
     */
    private GioSuDungResult tinhGioSuDung(PhienSuDung phienSuDung, double tongGio)
            throws Exception {

        double gioTuGoi = 0.0;
        double gioTuTaiKhoan = 0.0;

        if (phienSuDung.getMaGoiKH() != null) {
            GoiDichVuKhachHang goi = goiDichVuKhachHangDAO.getByMaGoiKhachHang(
                    phienSuDung.getMaGoiKH()
            );

            if (goi != null && goi.getSogioconlai() > 0) {
                gioTuGoi = Math.min(tongGio, goi.getSogioconlai());
                gioTuTaiKhoan = tongGio - gioTuGoi;

                goi.setSogioconlai(goi.getSogioconlai() - gioTuGoi);
                goiDichVuKhachHangDAO.update(goi);
            } else {
                gioTuTaiKhoan = tongGio;
            }
        } else {
            gioTuTaiKhoan = tongGio;
        }

        return new GioSuDungResult(gioTuGoi, gioTuTaiKhoan);
    }

    /**
     * Tạo hóa đơn cho phiên vừa kết thúc
     */
    private HoaDon taoHoaDon(PhienSuDung phienSuDung, double tienGioChoi, KhachHang kh)
            throws Exception {

        HoaDon hd = new HoaDon();

        double tongTienDichVu = suDungDichVuDAO.tinhTongTienKhachHang(phienSuDung.getMaKH());
        double thanhTien = tongTienDichVu + tienGioChoi;

        hd.setMaHD(hoaDonDAO.taoMaHoaDonTuDong());
        hd.setMaPhien(phienSuDung.getMaPhien());
        hd.setMaKH(phienSuDung.getMaKH());
        hd.setMaNV(phienSuDung.getMaNV());
        hd.setNgayLap(phienSuDung.getGioKetThuc());
        hd.setTienGioChoi(tienGioChoi);
        hd.setTienDichVu(tongTienDichVu);
        hd.setGiamGia(0.0);

        if (thanhTien <= kh.getSodu()) {
            hd.setThanhToan(thanhTien);
            hd.setTrangThai("DATHANHTOAN");

            kh.setSodu(kh.getSodu() - thanhTien);
            khachHangDAO.update(kh);
        } else {
            hd.setThanhToan(0.0);
            hd.setTrangThai("CHUATHANHTOAN");
        }

        return hd;
    }

    /**
     * Validate dữ liệu đầu vào khi mở phiên
     */
    private void validateMoPhienInput(String maKH, String maMay) throws Exception {
        if (maKH == null || maKH.trim().isEmpty()) {
            throw new Exception("Vui lòng chọn khách hàng");
        }

        if (maMay == null || maMay.trim().isEmpty()) {
            throw new Exception("Vui lòng chọn máy tính");
        }

        MayTinh may = mayTinhDAO.getById(maMay);
        if (may == null) {
            throw new Exception("Không tìm thấy máy tính với mã: " + maMay);
        }

        if (!"TRONG".equals(may.getTrangthai())) {
            throw new Exception(
                    "Máy tính " + maMay + " không khả dụng.\n" +
                            "Trạng thái hiện tại: " + may.getTrangthai() + "\n" +
                            "Vui lòng chọn máy khác."
            );
        }

        KhachHang kh = khachHangDAO.getById(maKH);
        if (kh == null) {
            throw new Exception("Không tìm thấy khách hàng với mã: " + maKH);
        }

        if (!"HOATDONG".equals(kh.getTrangthai())) {
            throw new Exception(
                    "Tài khoản khách hàng đã bị khóa.\n" +
                            "Trạng thái: " + kh.getTrangthai() + "\n" +
                            "Vui lòng liên hệ quản lý để kích hoạt lại tài khoản."
            );
        }
    }

    /**
     * Kiểm tra khách hàng có thể chơi hay không
     */
    private void checkKhachHangCoTheChoi(KhachHang kh) throws Exception {
        boolean goiConHan = false;

        try {
            List<GoiDichVuKhachHang> listGoi = goiDichVuKhachHangDAO.getByKhachHang(kh.getMakh());

            for (GoiDichVuKhachHang goi : listGoi) {
                if (goi != null && goi.getSogioconlai() > 0) {
                    goiConHan = true;
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi kiểm tra gói: " + e.getMessage());
        }

        boolean coTien = kh.getSodu() > 0;

        if (!goiConHan && !coTien) {
            throw new Exception(
                    "Khách hàng " + kh.getTendangnhap() + " không thể chơi.\n\n" +
                            "Lý do:\n" +
                            "- Không có gói dịch vụ còn hạn\n" +
                            "- Số dư tài khoản: 0 VNĐ\n\n" +
                            "Vui lòng nạp tiền hoặc mua gói dịch vụ trước khi chơi."
            );
        }
    }

    // ==================== UTILITY METHODS ====================

    /**
     * Tính tổng số giờ giữa 2 thời điểm
     */
    private double tinhTongGio(LocalDateTime gioBatDau, LocalDateTime gioKetThuc) {
        long totalMinutes = ChronoUnit.MINUTES.between(gioBatDau, gioKetThuc);
        return totalMinutes / 60.0;
    }

    // ==================== INNER CLASS ====================

    /**
     * Class helper để trả về kết quả tính giờ sử dụng
     */
    private static class GioSuDungResult {
        final double gioTuGoi;
        final double gioTuTaiKhoan;

        GioSuDungResult(double gioTuGoi, double gioTuTaiKhoan) {
            this.gioTuGoi = gioTuGoi;
            this.gioTuTaiKhoan = gioTuTaiKhoan;
        }
    }
}