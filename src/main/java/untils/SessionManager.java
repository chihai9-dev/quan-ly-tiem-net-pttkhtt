package untils;



import entity.*;

/**
 * SessionManager - Quản lý phiên đăng nhập của người dùng
 *
 * Class này lưu trữ thông tin người dùng hiện tại đang đăng nhập vào hệ thống.
 * Hỗ trợ cả Nhân viên (QUANLY, NHANVIEN) và Khách hàng (KHACHHANG).
 *
 * @author QuanLyTiemNet Team
 * @version 1.0
 * @since 2026-02-01
 */
public class SessionManager {

    // ============== BIẾN STATIC LƯU THÔNG TIN NGƯỜI DÙNG ==============

    /**
     * Nhân viên đang đăng nhập (QUANLY hoặc NHANVIEN)
     */
    private static NhanVien currentNhanVien = null;

    /**
     * Khách hàng đang đăng nhập
     */
    private static KhachHang currentKhachHang = null;

    /**
     * Loại tài khoản: "QUANLY" | "NHANVIEN" | "KHACHHANG"
     */
    private static String loaiTaiKhoan = null;

    /**
     * Thời điểm đăng nhập (dùng để tính thời gian session)
     */
    private static long loginTimestamp = 0;

    // ============== CONSTRUCTOR ==============

    /**
     * Private constructor để ngăn khởi tạo instance
     * SessionManager là Utility class, chỉ sử dụng static methods
     */
    private SessionManager() {
        throw new AssertionError("SessionManager không thể khởi tạo instance");
    }

    // ============== METHODS ĐĂNG NHẬP ==============

    /**
     * Lưu thông tin nhân viên đăng nhập
     *
     * @param nv Nhân viên đăng nhập (QUANLY hoặc NHANVIEN)
     * @throws IllegalArgumentException nếu nv null hoặc chức vụ không hợp lệ
     */
    public static void setCurrentUser(NhanVien nv) {
        if (nv == null) {
            throw new IllegalArgumentException("NhanVien không được null");
        }

        String chucVu = nv.getChucvu();
        if (chucVu == null || (!chucVu.equals("QUANLY") && !chucVu.equals("NHANVIEN"))) {
            throw new IllegalArgumentException("Chức vụ không hợp lệ: " + chucVu);
        }

        currentNhanVien = nv;
        currentKhachHang = null;
        loaiTaiKhoan = chucVu; // "QUANLY" hoặc "NHANVIEN"
        loginTimestamp = System.currentTimeMillis();

        System.out.println("[SessionManager] Đăng nhập thành công: " + nv.getTen() + " (" + chucVu + ")");
    }

    /**
     * Lưu thông tin khách hàng đăng nhập
     *
     * @param kh Khách hàng đăng nhập
     * @throws IllegalArgumentException nếu kh null
     */
    public static void setCurrentUser(KhachHang kh) {
        if (kh == null) {
            throw new IllegalArgumentException("KhachHang không được null");
        }

        currentKhachHang = kh;
        currentNhanVien = null;
        loaiTaiKhoan = "KHACHHANG";
        loginTimestamp = System.currentTimeMillis();

        System.out.println("[SessionManager] Đăng nhập thành công: " + kh.getTen() + " (KHACHHANG)");
    }

    // ============== METHODS LẤY THÔNG TIN ==============

    /**
     * Lấy thông tin nhân viên hiện tại
     *
     * @return NhanVien đang đăng nhập hoặc null nếu không có
     */
    public static NhanVien getCurrentNhanVien() {
        return currentNhanVien;
    }

    /**
     * Lấy thông tin khách hàng hiện tại
     *
     * @return KhachHang đang đăng nhập hoặc null nếu không có
     */
    public static KhachHang getCurrentKhachHang() {
        return currentKhachHang;
    }

    /**
     * Lấy loại tài khoản hiện tại
     *
     * @return "QUANLY" | "NHANVIEN" | "KHACHHANG" | null
     */
    public static String getLoaiTaiKhoan() {
        return loaiTaiKhoan;
    }

    /**
     * Lấy mã nhân viên hiện tại (dùng để ghi log, lưu vào DB)
     *
     * @return Mã nhân viên hoặc null nếu không phải nhân viên
     */
    public static String getCurrentMaNV() {
        return currentNhanVien != null ? currentNhanVien.getManv() : null;
    }

    /**
     * Lấy mã khách hàng hiện tại
     *
     * @return Mã khách hàng hoặc null nếu không phải khách hàng
     */
    public static String getCurrentMaKH() {
        return currentKhachHang != null ? currentKhachHang.getMakh() : null;
    }

    /**
     * Lấy họ tên người dùng hiện tại
     *
     * @return Họ tên người dùng hoặc "Chưa đăng nhập" nếu chưa đăng nhập
     */
    public static String getCurrentUserName() {
        if (currentNhanVien != null) {
            return currentNhanVien.getTen();
        }
        if (currentKhachHang != null) {
            return currentKhachHang.getTen();
        }
        return "Chưa đăng nhập";
    }

    /**
     * Lấy thời gian đã đăng nhập (tính bằng phút)
     *
     * @return Số phút đã đăng nhập, hoặc 0 nếu chưa đăng nhập
     */
    public static long getSessionDurationMinutes() {
        if (!isLoggedIn() || loginTimestamp == 0) {
            return 0;
        }
        return (System.currentTimeMillis() - loginTimestamp) / 60000; // Convert to minutes
    }

    // ============== METHODS KIỂM TRA TRẠNG THÁI ==============

    /**
     * Kiểm tra có người dùng đăng nhập không
     *
     * @return true nếu có người dùng đang đăng nhập
     */
    public static boolean isLoggedIn() {
        return currentNhanVien != null || currentKhachHang != null;
    }

    /**
     * Kiểm tra người dùng hiện tại có phải QUANLY không
     *
     * @return true nếu là QUANLY
     */
    public static boolean isQuanLy() {
        return "QUANLY".equals(loaiTaiKhoan);
    }

    /**
     * Kiểm tra người dùng hiện tại có phải NHANVIEN không (bao gồm cả QUANLY)
     *
     * @return true nếu là NHANVIEN hoặc QUANLY
     */
    public static boolean isNhanVien() {
        return "QUANLY".equals(loaiTaiKhoan) || "NHANVIEN".equals(loaiTaiKhoan);
    }

    /**
     * Kiểm tra người dùng hiện tại có phải KHACHHANG không
     *
     * @return true nếu là KHACHHANG
     */
    public static boolean isKhachHang() {
        return "KHACHHANG".equals(loaiTaiKhoan);
    }

    /**
     * Kiểm tra người dùng có quyền quản lý không (chỉ QUANLY)
     *
     * @return true nếu có quyền quản lý
     */
    public static boolean hasAdminPermission() {
        return isQuanLy();
    }

    /**
     * Kiểm tra người dùng có quyền nhân viên không (QUANLY hoặc NHANVIEN)
     *
     * @return true nếu có quyền nhân viên
     */
    public static boolean hasStaffPermission() {
        return isNhanVien();
    }

    // ============== METHODS ĐĂNG XUẤT ==============

    /**
     * Đăng xuất - Xóa toàn bộ thông tin người dùng hiện tại
     */
    public static void logout() {
        String userName = getCurrentUserName();
        long sessionDuration = getSessionDurationMinutes();

        currentNhanVien = null;
        currentKhachHang = null;
        loaiTaiKhoan = null;
        loginTimestamp = 0;

        System.out.println("[SessionManager] Đăng xuất: " + userName +
                " (Thời gian: " + sessionDuration + " phút)");
    }

    /**
     * Clear session - Tương tự logout nhưng không in log
     * Dùng khi cần reset session trong test hoặc xử lý lỗi
     */
    public static void clearSession() {
        currentNhanVien = null;
        currentKhachHang = null;
        loaiTaiKhoan = null;
        loginTimestamp = 0;
    }

    // ============== METHODS DEBUG & UTILITY ==============

    /**
     * In thông tin session hiện tại ra console (dùng để debug)
     */
    public static void printSessionInfo() {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║       THÔNG TIN SESSION HIỆN TẠI      ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.println("║ Trạng thái: " + (isLoggedIn() ? "Đã đăng nhập" : "Chưa đăng nhập"));
        System.out.println("║ Loại TK:    " + (loaiTaiKhoan != null ? loaiTaiKhoan : "N/A"));
        System.out.println("║ Người dùng: " + getCurrentUserName());
        System.out.println("║ Mã NV:      " + (getCurrentMaNV() != null ? getCurrentMaNV() : "N/A"));
        System.out.println("║ Mã KH:      " + (getCurrentMaKH() != null ? getCurrentMaKH() : "N/A"));
        System.out.println("║ Thời gian:  " + getSessionDurationMinutes() + " phút");
        System.out.println("╚════════════════════════════════════════╝");
    }

    /**
     * Lấy thông tin session dạng String (dùng cho log file)
     *
     * @return Chuỗi thông tin session
     */
    public static String getSessionInfoString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Session[");
        sb.append("LoggedIn=").append(isLoggedIn());
        sb.append(", Type=").append(loaiTaiKhoan != null ? loaiTaiKhoan : "NONE");
        sb.append(", User=").append(getCurrentUserName());
        sb.append(", Duration=").append(getSessionDurationMinutes()).append("min");
        sb.append("]");
        return sb.toString();
    }

    /**
     * Kiểm tra tính hợp lệ của session
     *
     * @return true nếu session hợp lệ (đã đăng nhập và có đầy đủ thông tin)
     */
    public static boolean isSessionValid() {
        if (!isLoggedIn()) {
            return false;
        }

        if (currentNhanVien != null) {
            return currentNhanVien.getManv() != null &&
                    currentNhanVien.getChucvu() != null;
        }

        if (currentKhachHang != null) {
            return currentKhachHang.getMakh() != null;
        }

        return false;
    }

    /**
     * Refresh thông tin user (load lại từ DB nếu cần)
     * Method này nên được gọi từ BUS layer với updated entity
     *
     * @param updatedNV NhanVien đã cập nhật từ DB
     */
    public static void refreshCurrentNhanVien(NhanVien updatedNV) {
        if (currentNhanVien != null && updatedNV != null) {
            if (currentNhanVien.getManv().equals(updatedNV.getManv())) {
                currentNhanVien = updatedNV;
                loaiTaiKhoan = updatedNV.getChucvu();
                System.out.println("[SessionManager] Đã cập nhật thông tin nhân viên");
            }
        }
    }

    /**
     * Refresh thông tin khách hàng
     *
     * @param updatedKH KhachHang đã cập nhật từ DB
     */
    public static void refreshCurrentKhachHang(KhachHang updatedKH) {
        if (currentKhachHang != null && updatedKH != null) {
            if (currentKhachHang.getMakh().equals(updatedKH.getMakh())) {
                currentKhachHang = updatedKH;
                System.out.println("[SessionManager] Đã cập nhật thông tin khách hàng");
            }
        }
    }
}
