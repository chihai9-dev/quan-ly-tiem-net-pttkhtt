package bus;

import dao.*;
import entity.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

/** Chức năng chính:
 * - Tạo hóa đơn tự động khi kết thúc phiên
 * - Thanh toán hóa đơn
 * - Xem lịch sử hóa đơn
 * - Thống kê doanh thu
 */
public class HoaDonBUS {
    private HoaDonDAO hoaDonDAO;
    private ChiTietHoaDonDAO chiTietHoaDonDAO;
    private PhienSuDungDAO phienSuDungDAO;
    private SuDungDichVuDAO suDungDichVuDAO;
    private KhachHangDAO khachHangDAO;

    public HoaDonBUS() {
        this.hoaDonDAO = new HoaDonDAO();
        this.chiTietHoaDonDAO = new ChiTietHoaDonDAO();
        this.phienSuDungDAO = new PhienSuDungDAO();
        this.suDungDichVuDAO = new SuDungDichVuDAO();
        this.khachHangDAO = new KhachHangDAO();
    }

    // ===== 1. TẠO HÓA ĐƠN TỰ ĐỘNG =====
    /**Logic:
     * 1. Kiểm tra phiên đã kết thúc
     * 2. Kiểm tra chưa có hóa đơn cho phiên này
     * 3. Lấy thông tin phiên
     * 4. Lấy danh sách dịch vụ đã sử dụng
     * 5. Tạo hóa đơn với chi tiết
     */
    public HoaDon taoHoaDonTuDong(String maPhien, String maNV) throws Exception {
        // 1. Validate đầu vào
        if (maPhien == null || maPhien.trim().isEmpty())
            throw new Exception("Mã phiên không hợp lệ");
        if (maNV == null || maNV.trim().isEmpty()) {
            throw new Exception("Mã nhân viên không hợp lệ");
        }

        // 2. Kiểm tra phiên tồn tại
        PhienSuDung phien = phienSuDungDAO.getByMaPhien(maPhien);
        if (phien == null) {
            throw new Exception("Không tìm thấy phiên sử dụng");
        }

        // 3. Kiểm tra phiên đã kết thúc
        if (!"DAKETTHUC".equals(phien.getTrangThai())) {
            throw new Exception("Phiên chưa kết thúc. Vui lòng kết thúc phiên trước khi tạo hóa đơn.");
        }

        // 4. Kiểm tra chưa có hóa đơn
        HoaDon existingHD = hoaDonDAO.timTheoPhien(maPhien);
        if (existingHD != null) {
            throw new Exception("Phiên này đã có hóa đơn");
        }

        // 5. Tạo mã hóa đơn tự động
        String maHD = hoaDonDAO.taoMaHoaDonTuDong();

        // 6. Tạo hóa đơn mới
        HoaDon hoaDon = new HoaDon();
        hoaDon.setMaHD(maHD);
        hoaDon.setMaPhien(maPhien);
        hoaDon.setMaKH(phien.getMaKH());
        hoaDon.setMaNV(maNV);
        hoaDon.setNgayLap(LocalDateTime.now());
        hoaDon.setTienGioChoi(phien.getTienGioChoi());

        // 7. Tính tiền dịch vụ từ các dịch vụ đã sử dụng
        double tienDichVu = chiTietHoaDonDAO.tongTienDichVu(maHD);
        hoaDon.setTienDichVu(tienDichVu);

        // 8. Tính tổng tiền và thành toán
        hoaDon.tinhTongTien();
        hoaDon.setGiamGia(0); // Mặc định không giảm giá
        hoaDon.tinhThanhToan();

        // 9. Xác định phương thức thanh toán từ loại thanh toán của phiên
        String phuongThucTT = "TAIKHOAN"; // Mặc định thanh toán từ tài khoản
        if ("GOI".equals(phien.getLoaiThanhToan())) {
            phuongThucTT = "GOI";
        } else if ("KETHOP".equals(phien.getLoaiThanhToan())) {
            phuongThucTT = "TAIKHOAN"; // Phần tiền thanh toán từ tài khoản
        }
        hoaDon.setPhuongThucTT(phuongThucTT);

        // 10. Trạng thái: đã thanh toán nếu là gói, chưa thanh toán nếu cần trả tiền
        if ("GOI".equals(phuongThucTT) && hoaDon.getThanhToan() == 0) {
            hoaDon.setTrangThai("DATHANHTOAN");
        } else {
            hoaDon.setTrangThai("CHUA");
        }

        // 11. Lưu hóa đơn
        boolean success = hoaDonDAO.them(hoaDon);
        if (!success) {
            throw new Exception("Không thể tạo hóa đơn");
        }

        // 12. Tạo chi tiết hóa đơn
        List<ChiTietHoaDon> danhSachChiTiet = new ArrayList<>();

        // 12.1. Chi tiết giờ chơi
        if (hoaDon.getTienGioChoi() > 0) {
            ChiTietHoaDon ctGioChoi = new ChiTietHoaDon();
            ctGioChoi.setMaCTHD(chiTietHoaDonDAO.taoMaChiTietTuDong());
            ctGioChoi.setMaHD(maHD);
            ctGioChoi.setLoaiChiTiet("GIOCHOI");

            // Mô tả chi tiết
            String moTa = String.format("Giờ chơi máy %s - %.2f giờ x %,.0f VND",
                    phien.getMaMay(),
                    phien.getGioSuDungTuTaiKhoan(),
                    phien.getGiaMoiGio());
            ctGioChoi.setMoTa(moTa);
            ctGioChoi.setSoLuong(phien.getGioSuDungTuTaiKhoan());
            ctGioChoi.setDonGia(phien.getGiaMoiGio());
            ctGioChoi.tinhThanhTien();

            danhSachChiTiet.add(ctGioChoi);
        }

        // 12.2. Chi tiết dịch vụ đã sử dụng
        List<SuDungDichVu> danhSachDV = suDungDichVuDAO.geyByPhien(maPhien, DBConnection.getConnection());
        if (danhSachDV != null && !danhSachDV.isEmpty()) {
            for (SuDungDichVu sdDV : danhSachDV) {
                ChiTietHoaDon ctDV = new ChiTietHoaDon();
                ctDV.setMaCTHD(chiTietHoaDonDAO.taoMaChiTietTuDong());
                ctDV.setMaHD(maHD);
                ctDV.setLoaiChiTiet("DICHVU");
                ctDV.setMoTa("Dịch vụ: " + sdDV.getMadv());
                ctDV.setSoLuong(sdDV.getSoluong());
                ctDV.setDonGia(sdDV.getDongia());
                ctDV.tinhThanhTien();

                danhSachChiTiet.add(ctDV);
            }
        }

        // 13. Lưu tất cả chi tiết
        if (!danhSachChiTiet.isEmpty()) {
            boolean chiTietSuccess = chiTietHoaDonDAO.themNhieu(danhSachChiTiet);
            if (!chiTietSuccess) {
                throw new Exception("Không thể tạo chi tiết hóa đơn");
            }
        }
        return hoaDon;
    }

    // ===== 2. THANH TOÁN HÓA ĐƠN =====
    public boolean thanhToanHoaDon(String maHD, String phuongThucTT) throws Exception {
        // 1. Validate
        if (maHD == null || maHD.trim().isEmpty()) {
            throw new Exception("Mã hóa đơn không hợp lệ");
        }
        if (phuongThucTT == null || phuongThucTT.trim().isEmpty()) {
            throw new Exception("Phương thức thanh toán không hợp lệ");
        }

        // 2. Kiểm tra hóa đơn tồn tại
        HoaDon hoaDon = hoaDonDAO.timTheoMa(maHD);
        if (hoaDon == null) {
            throw new Exception("Không tìm thấy hóa đơn");
        }

        // 3. Kiểm tra trạng thái
        if ("DATHANHTOAN".equals(hoaDon.getTrangThai())) {
            throw new Exception("Hóa đơn đã được thanh toán");
        }

        // 4. Nếu thanh toán bằng tài khoản, kiểm tra số dư
        if ("TAIKHOAN".equals(phuongThucTT)) {
            KhachHang kh = khachHangDAO.getById(hoaDon.getMaKH());
            if (kh == null) {
                throw new Exception("Không tìm thấy khách hàng");
            }
            if (kh.getSodu() < hoaDon.getThanhToan()) {
                throw new Exception(String.format(
                        "Số dư không đủ. Cần: %,.0f VND, Còn: %,.0f VND",
                        hoaDon.getThanhToan(),
                        kh.getSodu()
                ));
            }

            // Trừ tiền từ tài khoản
            kh.setSodu(kh.getSodu() - hoaDon.getThanhToan());
            khachHangDAO.update(kh);
        }

        // 5. Cập nhật trạng thái hóa đơn
        boolean success = hoaDonDAO.thanhToan(maHD, phuongThucTT);
        if (!success) {
            throw new Exception("Không thể cập nhật trạng thái hóa đơn");
        }
        return true;
    }

    // ===== 3. XEM HÓA ĐƠN =====
    //Lấy hóa đơn theo mã
    public HoaDon xemHoaDon(String maHD) throws Exception {
        if (maHD == null || maHD.trim().isEmpty()) {
            throw new Exception("Mã hóa đơn không hợp lệ");
        }
        HoaDon hoaDon = hoaDonDAO.timTheoMa(maHD);
        if (hoaDon == null) {
            throw new Exception("Không tìm thấy hóa đơn");
        }
        return hoaDon;
    }

    //Lấy chi tiết hóa đơn
    public List<ChiTietHoaDon> xemChiTietHoaDon(String maHD) throws Exception {
        if (maHD == null || maHD.trim().isEmpty()) {
            throw new Exception("Mã hóa đơn không hợp lệ");
        }
        return chiTietHoaDonDAO.timTheoHoaDon(maHD);
    }

   // Lấy danh sách hóa đơn của khách hàng
    public List<HoaDon> layDanhSachHoaDonKhachHang(String maKH) throws Exception {
        if (maKH == null || maKH.trim().isEmpty()) {
            throw new Exception("Mã khách hàng không hợp lệ");
        }
        return hoaDonDAO.timTheoKhachHang(maKH);
    }

    // ===== 4. THỐNG KÊ =====
    // Thống kê doanh thu theo khoảng thời gian
    public double thongKeDoanhThu(LocalDateTime tuNgay, LocalDateTime denNgay) throws Exception {
        if (tuNgay == null || denNgay == null) {
            throw new Exception("Khoảng thời gian không hợp lệ");
        }
        if (tuNgay.isAfter(denNgay)) {
            throw new Exception("Ngày bắt đầu phải trước ngày kết thúc");
        }
        return hoaDonDAO.tongDoanhThu(tuNgay, denNgay);
    }

    // Thống kê doanh thu giờ chơi
    public double thongKeDoanhThuGioChoi(LocalDateTime tuNgay, LocalDateTime denNgay) throws Exception {
        if (tuNgay == null || denNgay == null) {
            throw new Exception("Khoảng thời gian không hợp lệ");
        }
        return hoaDonDAO.tongDoanhThuGioChoi(tuNgay, denNgay);
    }

    // Thống kê doanh thu dịch vụ
    public double thongKeDoanhThuDichVu(LocalDateTime tuNgay, LocalDateTime denNgay) throws Exception {
        if (tuNgay == null || denNgay == null) {
            throw new Exception("Khoảng thời gian không hợp lệ");
        }
        return hoaDonDAO.tongDoanhThuDichVu(tuNgay, denNgay);
    }

    //Top khách hàng chi tiêu nhiều
    public List<Object[]> topKhachHangChiTieu(int soLuong, LocalDateTime tuNgay, LocalDateTime denNgay)
            throws Exception {
        if (soLuong <= 0) {
            throw new Exception("Số lượng phải lớn hơn 0");
        }
        if (tuNgay == null || denNgay == null) {
            throw new Exception("Khoảng thời gian không hợp lệ");
        }
        return hoaDonDAO.topKhachHangChiTieu(soLuong, tuNgay, denNgay);
    }

    // Doanh thu theo nhân viên
    public List<Object[]> doanhThuTheoNhanVien(LocalDateTime tuNgay, LocalDateTime denNgay)
            throws Exception {
        if (tuNgay == null || denNgay == null) {
            throw new Exception("Khoảng thời gian không hợp lệ");
        }
        return hoaDonDAO.doanhThuTheoNhanVien(tuNgay, denNgay);
    }

    // ===== 5. TIỆN ÍCH =====
    public HoaDon taoHoaDon(PhienSuDung phien, double tienGioChoi, double tienDichVu,
                            double tienGiamGia) throws Exception {
        // Lấy MaNV từ session hiện tại (giả sử có currentUser)
        // Trong thực tế, MaNV có thể được truyền từ GUI
        String maNV = "NV001"; // Default, nên được truyền từ GUI

        return taoHoaDonTuDong(phien.getMaPhien(), maNV);
    }

     // Lấy tất cả hóa đơn
    public List<HoaDon> getAllHoaDon() {
        return layTatCaHoaDon();
    }

   //Lấy hóa đơn theo phiên
    public HoaDon getHoaDonByPhien(String maPhien) throws Exception {
        if (maPhien == null || maPhien.trim().isEmpty()) {
            throw new Exception("Mã phiên không hợp lệ");
        }
        HoaDon hoaDon = hoaDonDAO.timTheoPhien(maPhien);
        if (hoaDon == null) {
            throw new Exception("Không tìm thấy hóa đơn cho phiên: " + maPhien);
        }
        return hoaDon;
    }

    //Lấy hóa đơn theo khoảng thời gian
    public List<HoaDon> getHoaDonsByDateRange(LocalDateTime tuNgay, LocalDateTime denNgay)
            throws Exception {
        return layHoaDonTheoKhoangThoiGian(tuNgay, denNgay);
    }

    /**Xuất hóa đơn ra file PDF
     * Logic:
     * 1. Kiểm tra hóa đơn tồn tại
     * 2. Lấy thông tin hóa đơn, chi tiết, khách hàng
     * 3. Tạo file PDF với định dạng hóa đơn
     * 4. Trả về đường dẫn file PDF
     */
    public String xuatHoaDonPDF(String maHD) throws Exception {
        // 1. Kiểm tra hóa đơn tồn tại
        HoaDon hoaDon = xemHoaDon(maHD);
        if (hoaDon == null) {
            throw new Exception("Không tìm thấy hóa đơn: " + maHD);
        }

        // 2. Lấy chi tiết hóa đơn
        List<ChiTietHoaDon> chiTiet = xemChiTietHoaDon(maHD);

        // 3. Lấy thông tin khách hàng
        KhachHang khachHang = khachHangDAO.getById(hoaDon.getMaKH());

        // 4. Lấy thông tin phiên (nếu cần)
        PhienSuDung phien = null;
        if (hoaDon.getMaPhien() != null) {
            phien = phienSuDungDAO.getByMaPhien(hoaDon.getMaPhien());
        }

        // 5. Tạo file PDF (sử dụng thư viện iText hoặc Apache PDFBox)
        // NOTE: Cần import thư viện PDF vào project
        String duongDanPDF = "exports/hoadon_" + maHD + "_" + System.currentTimeMillis() + ".pdf";
        try {
            // Tạo PDF với iText hoặc PDFBox

            // === THÔNG TIN HEADER ===
            // - Logo quán net
            // - Tên quán, địa chỉ, số điện thoại
            // - Tiêu đề: HÓA ĐƠN THANH TOÁN

            // === THÔNG TIN HÓA ĐƠN ===
            // - Mã hóa đơn: hoaDon.getMaHD()
            // - Ngày lập: hoaDon.getNgayLapFormatted()
            // - Nhân viên: hoaDon.getMaNV()

            // === THÔNG TIN KHÁCH HÀNG ===
            // - Mã KH: khachHang.getMaKH()
            // - Họ tên: khachHang.getHoTen()
            // - SĐT: khachHang.getSoDienThoai()

            // === BẢNG CHI TIẾT ===
            // Header: STT | Mô tả | Số lượng | Đơn giá | Thành tiền
            // - Giờ chơi: phien info
            // - Các dịch vụ: loop chiTiet

            // === TỔNG CỘNG ===
            // - Tiền giờ chơi: hoaDon.getTienGioChoiFormatted()
            // - Tiền dịch vụ: hoaDon.getTienDichVuFormatted()
            // - Tổng tiền: hoaDon.getTongTienFormatted()
            // - Giảm giá: hoaDon.getGiamGiaFormatted()
            // - Thành toán: hoaDon.getThanhToanFormatted()

            // === FOOTER ===
            // - Chữ ký nhân viên, khách hàng
            // - Lời cảm ơn

            // Lưu file PDF
            System.out.println("Đã xuất hóa đơn PDF: " + duongDanPDF);

            // NOTE: Đây là placeholder code
            // Trong thực tế cần implement đầy đủ với thư viện PDF
            // Ví dụ: iText 7, Apache PDFBox, hoặc JasperReports
        } catch (Exception e) {
            throw new Exception("Lỗi khi xuất PDF: " + e.getMessage());
        }
        return duongDanPDF;
    }

    //Lấy tất cả hóa đơn
    public List<HoaDon> layTatCaHoaDon() {
        return hoaDonDAO.layTatCa();
    }
    // Lấy hóa đơn theo khoảng thời gian
    public List<HoaDon> layHoaDonTheoKhoangThoiGian(LocalDateTime tuNgay, LocalDateTime denNgay)
            throws Exception {
        if (tuNgay == null || denNgay == null) {
            throw new Exception("Khoảng thời gian không hợp lệ");
        }
        return hoaDonDAO.timTheoKhoangThoiGian(tuNgay, denNgay);
    }

    //Kiểm tra hóa đơn đã thanh toán
    public boolean kiemTraDaThanhToan(String maHD) throws Exception {
        HoaDon hoaDon = xemHoaDon(maHD);
        return "DATHANHTOAN".equals(hoaDon.getTrangThai());
    }
}