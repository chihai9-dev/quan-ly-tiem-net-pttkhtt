package untils;


import entity.*;

/**
 * PermissionHelper - Hỗ trợ kiểm tra phân quyền
 *
 * Class tiện ích cung cấp các phương thức kiểm tra quyền hạn người dùng.
 * Được sử dụng trong các lớp BUS để đảm bảo tính bảo mật.
 *
 * @author QuanLyTiemNet Team
 * @version 1.0
 * @since 2026-02-01
 */
public class PermissionHelper {

    // ============== CONSTRUCTOR ==============

    /**
     * Private constructor để ngăn khởi tạo instance
     * PermissionHelper là Utility class, chỉ sử dụng static methods
     */
    private PermissionHelper() {
        throw new AssertionError("PermissionHelper không thể khởi tạo instance");
    }

    // ============== METHODS KIỂM TRA CƠ BẢN ==============

    /**
     * Yêu cầu phải đăng nhập (bất kỳ loại tài khoản nào)
     *
     * @throws Exception nếu chưa đăng nhập
     */
    public static void requireLogin() throws Exception {
        if (!SessionManager.isLoggedIn()) {
            throw new Exception("Vui lòng đăng nhập để thực hiện chức năng này");
        }

        if (!SessionManager.isSessionValid()) {
            throw new Exception("Phiên đăng nhập không hợp lệ. Vui lòng đăng nhập lại");
        }
    }

    /**
     * Yêu cầu phải là QUANLY
     *
     * @throws Exception nếu không phải QUANLY
     */
    public static void requireQuanLy() throws Exception {
        if (!SessionManager.isLoggedIn()) {
            throw new Exception("Vui lòng đăng nhập để thực hiện chức năng này");
        }

        if (!SessionManager.isQuanLy()) {
            throw new Exception("Không có quyền thực hiện. Chỉ Quản lý mới được phép");
        }
    }

    /**
     * Yêu cầu phải là QUANLY hoặc NHANVIEN
     *
     * @throws Exception nếu không phải nhân viên
     */
    public static void requireNhanVien() throws Exception {
        if (!SessionManager.isLoggedIn()) {
            throw new Exception("Vui lòng đăng nhập để thực hiện chức năng này");
        }

        if (!SessionManager.isNhanVien()) {
            throw new Exception("Không có quyền thực hiện. Chỉ nhân viên mới được phép");
        }
    }

    /**
     * Yêu cầu phải là KHACHHANG
     *
     * @throws Exception nếu không phải khách hàng
     */
    public static void requireKhachHang() throws Exception {
        if (!SessionManager.isLoggedIn()) {
            throw new Exception("Vui lòng đăng nhập để thực hiện chức năng này");
        }

        if (!SessionManager.isKhachHang()) {
            throw new Exception("Chức năng này chỉ dành cho khách hàng");
        }
    }

    // ============== METHODS LẤY THÔNG TIN VỚI VALIDATION ==============

    /**
     * Lấy thông tin nhân viên hiện tại (đã validate)
     *
     * @return NhanVien đang đăng nhập
     * @throws Exception nếu không phải nhân viên hoặc chưa đăng nhập
     */
    public static NhanVien getCurrentNhanVien() throws Exception {
        requireNhanVien();
        return SessionManager.getCurrentNhanVien();
    }

    /**
     * Lấy mã nhân viên hiện tại (đã validate)
     * Dùng để ghi log hoặc lưu vào DB
     *
     * @return Mã nhân viên (đảm bảo không null)
     * @throws Exception nếu không phải nhân viên hoặc chưa đăng nhập
     */
    public static String getCurrentMaNV() throws Exception {
        requireNhanVien();
        String maNV = SessionManager.getCurrentMaNV();
        if (maNV == null || maNV.trim().isEmpty()) {
            throw new Exception("Không thể xác định mã nhân viên");
        }
        return maNV;
    }

    /**
     * Lấy thông tin khách hàng hiện tại (đã validate)
     *
     * @return KhachHang đang đăng nhập
     * @throws Exception nếu không phải khách hàng hoặc chưa đăng nhập
     */
    public static KhachHang getCurrentKhachHang() throws Exception {
        requireKhachHang();
        return SessionManager.getCurrentKhachHang();
    }

    /**
     * Lấy mã khách hàng hiện tại (đã validate)
     *
     * @return Mã khách hàng (đảm bảo không null)
     * @throws Exception nếu không phải khách hàng hoặc chưa đăng nhập
     */
    public static String getCurrentMaKH() throws Exception {
        requireKhachHang();
        String maKH = SessionManager.getCurrentMaKH();
        if (maKH == null || maKH.trim().isEmpty()) {
            throw new Exception("Không thể xác định mã khách hàng");
        }
        return maKH;
    }

    // ============== METHODS KIỂM TRA PHÂN QUYỀN ĐẶC BIỆT ==============

    /**
     * Kiểm tra người dùng có quyền sửa thông tin nhân viên không
     * - QUANLY: Có quyền sửa tất cả nhân viên
     * - NHANVIEN: Chỉ sửa được chính mình
     *
     * @param maNV Mã nhân viên cần kiểm tra
     * @return true nếu có quyền
     * @throws Exception nếu không có quyền
     */
    public static boolean canEditNhanVien(String maNV) throws Exception {
        requireNhanVien();

        if (maNV == null || maNV.trim().isEmpty()) {
            throw new Exception("Mã nhân viên không hợp lệ");
        }

        // QUANLY có quyền sửa tất cả
        if (SessionManager.isQuanLy()) {
            return true;
        }

        // NHANVIEN chỉ sửa được chính mình
        String currentMaNV = SessionManager.getCurrentMaNV();
        if (currentMaNV != null && currentMaNV.equals(maNV)) {
            return true;
        }

        throw new Exception("Không có quyền sửa thông tin nhân viên khác. Bạn chỉ có thể sửa thông tin của chính mình");
    }

    /**
     * Kiểm tra người dùng có quyền xóa nhân viên không
     * Chỉ QUANLY mới có quyền xóa nhân viên
     *
     * @param maNV Mã nhân viên cần xóa
     * @return true nếu có quyền
     * @throws Exception nếu không có quyền
     */
    public static boolean canDeleteNhanVien(String maNV) throws Exception {
        requireQuanLy();

        if (maNV == null || maNV.trim().isEmpty()) {
            throw new Exception("Mã nhân viên không hợp lệ");
        }

        // Không được xóa chính mình
        String currentMaNV = SessionManager.getCurrentMaNV();
        if (currentMaNV != null && currentMaNV.equals(maNV)) {
            throw new Exception("Không thể xóa tài khoản của chính bạn");
        }

        return true;
    }

    /**
     * Kiểm tra người dùng có quyền xem thông tin khách hàng không
     * - QUANLY: Có quyền xem tất cả
     * - NHANVIEN: Có quyền xem tất cả
     * - KHACHHANG: Chỉ xem được chính mình
     *
     * @param maKH Mã khách hàng cần xem
     * @return true nếu có quyền
     * @throws Exception nếu không có quyền
     */
    public static boolean canViewKhachHang(String maKH) throws Exception {
        requireLogin();

        if (maKH == null || maKH.trim().isEmpty()) {
            throw new Exception("Mã khách hàng không hợp lệ");
        }

        // Nhân viên có quyền xem tất cả
        if (SessionManager.isNhanVien()) {
            return true;
        }

        // Khách hàng chỉ xem được chính mình
        if (SessionManager.isKhachHang()) {
            String currentMaKH = SessionManager.getCurrentMaKH();
            if (currentMaKH != null && currentMaKH.equals(maKH)) {
                return true;
            }
            throw new Exception("Bạn chỉ có thể xem thông tin của chính mình");
        }

        return false;
    }

    /**
     * Kiểm tra người dùng có quyền sửa thông tin khách hàng không
     * - QUANLY: Có quyền sửa tất cả
     * - NHANVIEN: Không có quyền sửa
     * - KHACHHANG: Chỉ sửa được chính mình
     *
     * @param maKH Mã khách hàng cần sửa
     * @return true nếu có quyền
     * @throws Exception nếu không có quyền
     */
    public static boolean canEditKhachHang(String maKH) throws Exception {
        requireLogin();

        if (maKH == null || maKH.trim().isEmpty()) {
            throw new Exception("Mã khách hàng không hợp lệ");
        }

        // QUANLY có quyền sửa tất cả
        if (SessionManager.isQuanLy()) {
            return true;
        }

        // NHANVIEN không có quyền sửa thông tin khách hàng
        if (SessionManager.isNhanVien() && !SessionManager.isQuanLy()) {
            throw new Exception("Nhân viên không có quyền sửa thông tin khách hàng");
        }

        // KHACHHANG chỉ sửa được chính mình
        if (SessionManager.isKhachHang()) {
            String currentMaKH = SessionManager.getCurrentMaKH();
            if (currentMaKH != null && currentMaKH.equals(maKH)) {
                return true;
            }
            throw new Exception("Bạn chỉ có thể sửa thông tin của chính mình");
        }

        return false;
    }

    /**
     * Kiểm tra người dùng có quyền xem báo cáo thống kê không
     * - QUANLY: Xem tất cả báo cáo
     * - NHANVIEN: Xem một số báo cáo cơ bản (tùy chức năng)
     * - KHACHHANG: Không có quyền
     *
     * @param loaiBaoCao Loại báo cáo: "DOANHTHU" | "TONGQUAN" | "DICHVU" | "MAYTINH"
     * @return true nếu có quyền
     * @throws Exception nếu không có quyền
     */
    public static boolean canViewBaoCao(String loaiBaoCao) throws Exception {
        requireLogin();

        // Khách hàng không có quyền xem báo cáo
        if (SessionManager.isKhachHang()) {
            throw new Exception("Khách hàng không có quyền xem báo cáo");
        }

        // QUANLY xem tất cả
        if (SessionManager.isQuanLy()) {
            return true;
        }

        // NHANVIEN chỉ xem báo cáo tổng quan
        if (SessionManager.isNhanVien()) {
            if ("TONGQUAN".equals(loaiBaoCao)) {
                return true;
            }
            throw new Exception("Nhân viên chỉ được xem báo cáo tổng quan. Báo cáo chi tiết yêu cầu quyền Quản lý");
        }

        return false;
    }

    // ============== METHODS KIỂM TRA QUYỀN THEO CHỨC NĂNG ==============

    /**
     * Kiểm tra quyền mở phiên chơi (QUANLY hoặc NHANVIEN)
     */
    public static void requireMoPhien() throws Exception {
        requireNhanVien();
    }

    /**
     * Kiểm tra quyền kết thúc phiên (QUANLY hoặc NHANVIEN)
     */
    public static void requireKetThucPhien() throws Exception {
        requireNhanVien();
    }

    /**
     * Kiểm tra quyền nạp tiền (QUANLY hoặc NHANVIEN)
     */
    public static void requireNapTien() throws Exception {
        requireNhanVien();
    }

    /**
     * Kiểm tra quyền order dịch vụ (QUANLY hoặc NHANVIEN)
     */
    public static void requireOrderDichVu() throws Exception {
        requireNhanVien();
    }

    /**
     * Kiểm tra quyền quản lý nhập hàng (chỉ QUANLY)
     */
    public static void requireNhapHang() throws Exception {
        requireQuanLy();
    }

    /**
     * Kiểm tra quyền quản lý khuyến mãi (chỉ QUANLY)
     */
    public static void requireQuanLyKhuyenMai() throws Exception {
        requireQuanLy();
    }

    // ============== UTILITY METHODS ==============

    /**
     * Kiểm tra quyền với custom message
     *
     * @param condition Điều kiện phải thỏa mãn
     * @param errorMessage Message lỗi nếu không thỏa mãn
     * @throws Exception nếu condition = false
     */
    public static void require(boolean condition, String errorMessage) throws Exception {
        if (!condition) {
            throw new Exception(errorMessage);
        }
    }

    /**
     * Ghi log hành động (dùng để audit)
     *
     * @param action Hành động thực hiện
     * @param target Đối tượng bị tác động
     */
    public static void logAction(String action, String target) {
        String user = SessionManager.getCurrentUserName();
        String role = SessionManager.getLoaiTaiKhoan();
        System.out.println("[PERMISSION LOG] " + user + " (" + role + ") - " + action + " - " + target);
    }

    /**
     * In thông tin quyền hạn hiện tại (dùng để debug)
     */
    public static void printPermissionInfo() {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║       THÔNG TIN QUYỀN HẠN HIỆN TẠI    ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.println("║ Người dùng:    " + SessionManager.getCurrentUserName());
        System.out.println("║ Loại TK:       " + SessionManager.getLoaiTaiKhoan());
        System.out.println("║ Là QUANLY:     " + SessionManager.isQuanLy());
        System.out.println("║ Là NHANVIEN:   " + SessionManager.isNhanVien());
        System.out.println("║ Là KHACHHANG:  " + SessionManager.isKhachHang());
        System.out.println("║ Quyền Admin:   " + SessionManager.hasAdminPermission());
        System.out.println("║ Quyền Staff:   " + SessionManager.hasStaffPermission());
        System.out.println("╚════════════════════════════════════════╝");
    }
}