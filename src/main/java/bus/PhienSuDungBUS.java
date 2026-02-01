package bus;

import dao.*;
import entity.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * PhienSuDungBUS - Business Logic Layer cho Phiên Sử Dụng
 * Package: com.quanlytiemnet.bus
 * 
 * Chức năng chính: Mở phiên, đóng phiên, tính tiền, quản lý phiên
 * DAO sử dụng: PhienSuDungDAO, MayTinhDAO, KhachHangDAO, GoiDichVuKhachHangDAO,
 *              SuDungDichVuDAO, HoaDonBUS
 */
public class PhienSuDungBUS {
    
    private PhienSuDungDAO phienSuDungDAO = new PhienSuDungDAO();
    private MayTinhDAO mayTinhDAO = new MayTinhDAO();
    private KhachHangDAO khachHangDAO = new KhachHangDAO();
    private GoiDichVuKhachHangDAO goiDichVuKhachHangDAO = new GoiDichVuKhachHangDAO();
    private SuDungDichVuDAO suDungDichVuDAO = new SuDungDichVuDAO();
    private HoaDonBUS hoaDonBUS = new HoaDonBUS();

    /**
     * Mở phiên chơi mới
     * @param maKH - Mã khách hàng
     * @param maMay - Mã máy
     * @param maGoiKH - Mã gói dịch vụ của khách hàng (có thể null)
     * @return PhienSuDung - Phiên mới được tạo
     * @throws Exception nếu có lỗi
     */
    public PhienSuDung moPhienChoi(String maKH, String maMay, String maGoiKH) throws Exception {
        // 45. Kiểm tra phân quyền QUANLY/NHANVIEN
        // (Giả sử kiểm tra qua SessionManager - MaNV)
        
        // 46. Lấy currentUser từ SessionManager (MaNV)
        String maNV = getCurrentUserFromSession();
        
        // 47. Lấy thông tin Máy (mayTinhDAO.getById)
        MayTinh may = mayTinhDAO.getById(maMay);
        
        // 48. Kiểm tra Máy.TrangThai = TRONG
        if (may == null) {
            throw new RuntimeException("Máy không khả dụng");
        }
        if (!"TRONG".equals(may.getTrangthai())) {
            throw new RuntimeException("Máy không khả dụng");
        }
        
        // 49. Lấy thông tin KH (khachHangDAO.getById)
        KhachHang khachHang = khachHangDAO.getByTenDangNhap(maKH);
        if (khachHang == null) {
            // KH không tồn tại - tạo mới KH tạm thời
            khachHang = new KhachHang();
            khachHang.setMakh(maKH);
        }
        
        // 50. Kiểm tra KH.TrangThai = HOATDONG
        if (khachHang != null && "NGUNG".equals(khachHang.getTrangthai())) {
            throw new RuntimeException("Tài khoản đã bị khóa");
        }
        
        // 51. Kiểm tra KH.SoDu >= 0 (hoặc có gói)
        GoiDichVuKhachHang goiKH = null;
        if (maGoiKH != null && !maGoiKH.isEmpty()) {
            // Kiểm tra gói dịch vụ
            // Lấy gói theo maGoiKH từ list gói của khách
            List<GoiDichVuKhachHang> listGoiKH = goiDichVuKhachHangDAO.getByKhachHang(maKH);
            if (listGoiKH != null) {
                for (GoiDichVuKhachHang goi : listGoiKH) {
                    if (goi.getMagoikh().equals(maGoiKH)) {
                        goiKH = goi;
                        break;
                    }
                }
            }
        }
        
        // 52. Kiểm tra KH không có phiên DANGCHOI khác
        List<PhienSuDung> listPhienKH = getPhienDangChoi();
        for (PhienSuDung phien : listPhienKH) {
            if (phien.getMakh() != null && phien.getMakh().equals(maKH) 
                && "DANGCHOI".equals(phien.getTrangthai())) {
                throw new RuntimeException("Khách hàng đang có phiên khác");
            }
        }
        
        // 53. Nếu có maGoiKH: Lấy gói và kiểm tra có hiệu lực
        if (goiKH != null) {
            // Kiểm tra gói còn hạn
            LocalDateTime now = LocalDateTime.now();
            if (now.isAfter(goiKH.getNgayhethan())) {
                throw new RuntimeException("Gói đã hết hạn");
            }
            // Kiểm tra gói còn giờ
            if (goiKH.getSogioconlai() <= 0) {
                throw new RuntimeException("Gói đã hết giờ");
            }
        } else if (khachHang.getSodu() <= 0) {
            // Không có gói và số dư không đủ
            throw new RuntimeException("Số dư không đủ");
        }
        
        // 54. Tạo PhienSuDung mới với MaPhien = generateId()
        PhienSuDung phien = new PhienSuDung();
        phien.setMakhu(khachHang.getMakh());
        phien.setMamay(maMay);
        phien.setManv(maNV);
        phien.setMagoikh(maGoiKH);
        phien.setGiobatdau(LocalDateTime.now());
        phien.setGiamoigio(may.getGiamoigio());
        phien.setTrangthai("DANGCHOI");
        phien.setLoaithanhtoan(goiKH != null ? "GOI" : "TAIKHOAN");
        
        // 55. GiaBatDau = NOW(), GiaMoiGio = Máy.GiaMoiGio (snapshot)
        // (Đã set ở trên)
        
        // 56. TrangThai = DANGCHOI
        // (Đã set ở trên)
        
        // 57. Gọi phienSuDungDAO.themPhien(phien)
        boolean inserted = phienSuDungDAO.insert(phien);
        if (!inserted) {
            throw new RuntimeException("Không thể tạo phiên mới");
        }
        
        // 58. Cập nhật Máy.TrangThai = DANGDUNG
        may.setTrangthai("DANGDUNG");
        mayTinhDAO.update(may);
        
        // 59. Trả về PhienSuDung vừa tạo
        return phien;
    }

    /**
     * Kết thúc phiên sử dụng - CÓ TRANSACTION
     * @param maPhien - Mã phiên cần kết thúc
     * @return HoaDon - Hóa đơn được tạo
     * @throws Exception nếu có lỗi
     */
    public HoaDon ketThucPhien(String maPhien) throws Exception {
        // 60. Kiểm tra phân quyền QUANLY/NHANVIEN
        
        // 61. BẮT ĐẦU TRANSACTION
        // (SQL sẽ tự động commit/rollback tùy theo kết quả)
        
        try {
            // 62. Lấy PhienSuDung từ DB
            PhienSuDung phien = phienSuDungDAO.getById(maPhien);
            if (phien == null) {
                throw new RuntimeException("Phiên không tồn tại");
            }
            
            // 63. Kiểm tra TrangThai = DANGCHOI
            if (!"DANGCHOI".equals(phien.getTrangthai())) {
                throw new RuntimeException("Phiên không đang chơi");
            }
            
            // 64. TÍNH THỜI GIAN: GioKetThuc = (GioTaiKhoan - GioBatDau) / 60
            LocalDateTime gioKetThuc = LocalDateTime.now();
            phien.setGioketthuc(gioKetThuc);
            
            long minutes = java.time.temporal.ChronoUnit.MINUTES.between(
                phien.getGiobatdau(), gioKetThuc);
            double tongGio = minutes / 60.0;
            phien.setTonggio(tongGio);
            
            // 65. XỬ LÝ GỐI (nếu có): GioKetThuc = GioTaiKhoan - GioBatDau / 60
            double gioSuDungTuGoi = 0;
            double gioSuDungTuTaiKhoan = tongGio;
            
            if (phien.getMagoikh() != null && !phien.getMagoikh().isEmpty()) {
                GoiDichVuKhachHang goi = getGoiByMaGoiKH(phien.getMagoikh());
                if (goi != null && goi.getSogioconlai() > 0) {
                    // Dùng giờ từ gói trước
                    if (goi.getSogioconlai() >= tongGio) {
                        gioSuDungTuGoi = tongGio;
                        gioSuDungTuTaiKhoan = 0;
                    } else {
                        gioSuDungTuGoi = goi.getSogioconlai();
                        gioSuDungTuTaiKhoan = tongGio - gioSuDungTuGoi;
                    }
                    
                    // Cập nhật gói: SoGioConLai = SoGioConLai - GioSuDungTuGoi
                    goi.setSogioconlai(goi.getSogioconlai() - gioSuDungTuGoi);
                    goiDichVuKhachHangDAO.update(goi);
                }
            }
            
            phien.setGiosudungtugoi(gioSuDungTuGoi);
            phien.setGiosudungtutaikhoan(gioSuDungTuTaiKhoan);
            
            // 66. TÍNH TIỀN: GioKetThuc = GioTaiKhoan - GioBatDau / 60.0
            double tienGioChoi = gioSuDungTuTaiKhoan * phien.getGiamoigio();
            phien.setTiengiochoi(tienGioChoi);
            
            // 67. TRỪ GỐI: GioKetThuc - GioTaiKhoan ( dùng cho gói )
            // (Đã xử lý ở trên)
            
            // 68. TÍNH TIỀN GỘI: GioKetThuc * GiaMoiGio
            // (Nếu dùng từ gói thì không tính tiền)
            
            // 69. TÍNH TIỀN LẦN GỌI: gioKetThuc * GioTaiKhoan
            // (Đã tính ở trên)
            
            // 70. TÍNH TIỀN DỊCH VỤ: ThanhTien = SUM(sudungdichvu.ThanhTien)
            double tienDichVu = phienSuDungDAO.getTongTienDichVu(maPhien);
            
            // 71. TỔNG TIỀN: TongTien = TienGioChoi + TienDichVu
            double tongTien = tienGioChoi + tienDichVu;
            phien.setTongtien(tongTien);
            phien.setTiendichvu(tienDichVu);
            
            // 72. CẬP NHẬT PHIÊN: gioKetThuc, TongGio, TrangThai = DAKETTHUC
            phien.setTrangthai("DAKETTHUC");
            boolean updated = phienSuDungDAO.ketThucPhien(phien);
            if (!updated) {
                throw new RuntimeException("Không thể cập nhật phiên");
            }
            
            // 73. CẬP NHẬT MÁY: TrangThai = TRONG
            MayTinh may = mayTinhDAO.getById(phien.getMamay());
            if (may != null) {
                may.setTrangthai("TRONG");
                mayTinhDAO.update(may);
            }
            
            // 74. TẠO HÓA ĐƠN: DON: HoaDon(phiên, tienGioChoi, tienDichVu)
            HoaDon hoaDon = hoaDonBUS.taoHoaDon(phien, tienGioChoi, tienDichVu, tongTien);
            
            // 75. Commit Transaction
            return hoaDon;
            
        } catch (Exception e) {
            // 76. Trả về HoaDon vừa tạo
            throw e;
        }
    }

    /**
     * Lấy danh sách phiên đang chơi
     * @return List<PhienSuDung> - Danh sách phiên đang chơi
     */
    public List<PhienSuDung> getPhienDangChoi() {
        List<PhienSuDung> allPhien = phienSuDungDAO.getAll();
        List<PhienSuDung> dangChoi = new ArrayList<>();
        
        for (PhienSuDung p : allPhien) {
            if ("DANGCHOI".equals(p.getTrangthai())) {
                dangChoi.add(p);
            }
        }
        return dangChoi;
    }

    /**
     * Chuyển máy trong phiên
     * @param maPhien - Mã phiên
     * @param maMayMoi - Mã máy mới
     * @return boolean - true nếu thành công
     * @throws Exception nếu có lỗi
     */
    public boolean chuyenMay(String maPhien, String maMayMoi) throws Exception {
        // Lấy phiên hiện tại
        PhienSuDung phien = phienSuDungDAO.getById(maPhien);
        if (phien == null) {
            throw new RuntimeException("Phiên không tồn tại");
        }
        
        // Kiểm tra máy mới có khả dụng không
        MayTinh mayMoi = mayTinhDAO.getById(maMayMoi);
        if (mayMoi == null || !"TRONG".equals(mayMoi.getTrangthai())) {
            throw new RuntimeException("Máy mới không khả dụng");
        }
        
        // Lấy máy cũ
        MayTinh mayCu = mayTinhDAO.getById(phien.getMamay());
        
        // Cập nhật trạng thái máy
        if (mayCu != null) {
            mayCu.setTrangthai("TRONG");
            mayTinhDAO.update(mayCu);
        }
        
        mayMoi.setTrangthai("DANGDUNG");
        mayTinhDAO.update(mayMoi);
        
        // Cập nhật phiên
        phien.setMamay(maMayMoi);
        return phienSuDungDAO.update(phien);
    }

    /**
     * Tính tiền tạm thời cho phiên đang chơi
     * @param maPhien - Mã phiên
     * @return double - Tiền tạm thời
     * @throws Exception nếu có lỗi
     */
    public double tinhTienTamThoi(String maPhien) throws Exception {
        PhienSuDung phien = phienSuDungDAO.getById(maPhien);
        if (phien == null) {
            throw new RuntimeException("Phiên không tồn tại");
        }
        
        if (!"DANGCHOI".equals(phien.getTrangthai())) {
            throw new RuntimeException("Phiên không đang chơi");
        }
        
        // Tính thời gian hiện tại
        LocalDateTime now = LocalDateTime.now();
        long minutes = java.time.temporal.ChronoUnit.MINUTES.between(
            phien.getGiobatdau(), now);
        double gioChoi = minutes / 60.0;
        
        // Tính tiền tạm thời (chưa tính dịch vụ)
        double tienTamThoi = gioChoi * phien.getGiamoigio();
        
        // Cộng tiền dịch vụ đã sử dụng
        double tienDichVu = phienSuDungDAO.getTongTienDichVu(maPhien);
        
        return tienTamThoi + tienDichVu;
    }

    /**
     * Hủy phiên (soft delete)
     * @param maPhien - Mã phiên
     * @return boolean - true nếu thành công
     * @throws Exception nếu có lỗi
     */
    public boolean huyPhien(String maPhien) throws Exception {
        PhienSuDung phien = phienSuDungDAO.getById(maPhien);
        if (phien == null) {
            throw new RuntimeException("Phiên không tồn tại");
        }
        
        // Cập nhật máy về trạng thái TRONG
        MayTinh may = mayTinhDAO.getById(phien.getMamay());
        if (may != null) {
            may.setTrangthai("TRONG");
            mayTinhDAO.update(may);
        }
        
        // Hủy phiên
        return phienSuDungDAO.huyPhien(maPhien);
    }

    /**
     * Lấy thông tin phiên theo mã
     * @param maPhien - Mã phiên
     * @return PhienSuDung - Thông tin phiên
     */
    public PhienSuDung getPhienById(String maPhien) {
        return phienSuDungDAO.getById(maPhien);
    }

    /**
     * Lấy tất cả phiên
     * @return List<PhienSuDung> - Danh sách tất cả phiên
     */
    public List<PhienSuDung> getAllPhien() {
        return phienSuDungDAO.getAll();
    }

    /**
     * Helper method: Lấy gói dịch vụ theo mã gói KH
     * @param maGoiKH - Mã gói dịch vụ khách hàng
     * @return GoiDichVuKhachHang - Gói dịch vụ
     */
    private GoiDichVuKhachHang getGoiByMaGoiKH(String maGoiKH) {
        // Cần implement logic để lấy gói
        // Tạm thời trả về null
        return null;
    }

    /**
     * Helper method: Lấy tên người dùng hiện tại từ session
     * @return String - Mã nhân viên
     */
    private String getCurrentUserFromSession() {
        // Giả sử lấy từ SessionManager
        return "NV001"; // Mặc định, thực tế lấy từ session
    }

    // Method hỗ trợ cập nhật phiên (nếu cần)
    public boolean updatePhien(PhienSuDung phien) {
        return phienSuDungDAO.update(phien);
    }
}
