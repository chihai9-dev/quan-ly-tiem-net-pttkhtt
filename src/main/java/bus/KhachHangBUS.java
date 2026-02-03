package bus;

import dao.KhachHangDAO;
import entity.KhachHang;
import untils.PermissionHelper;
import untils.SessionManager;

import java.util.List;
import java.util.stream.Collectors;

public class KhachHangBUS {
    private final KhachHangDAO khachHangDAO;

    public KhachHangBUS() {
        this.khachHangDAO = new KhachHangDAO();
    }

    public KhachHang dangNhap(String tenDangNhap, String matKhau) throws Exception {
        try {
            KhachHang kh = khachHangDAO.login(tenDangNhap, matKhau);
            if (kh == null) {
                throw new Exception("Sai tên đăng nhập hoặc mật khẩu!");
            }
            // Lưu session khách hàng
            SessionManager.setCurrentUser(kh);
            return kh;
        } catch (RuntimeException e) {
            throw new Exception(e.getMessage());
        }
    }

    public List<KhachHang> getAllKhachHang() throws Exception {
        // Cả Quản lý và Nhân viên đều xem được danh sách KH
        PermissionHelper.requireNhanVien();
        return khachHangDAO.getAll();
    }

    public boolean themKhachHang(KhachHang kh) throws Exception {
        // Theo tài liệu nghiệp vụ: Thêm KH cần quyền QUANLY
        // Nếu thực tế NHANVIEN được thêm, hãy sửa requireQuanLy() thành requireNhanVien() ở đây
        PermissionHelper.requireQuanLy();

        // Validate nghiệp vụ bổ sung
        if (khachHangDAO.isTenDangNhapExists(kh.getTendangnhap())) {
            throw new Exception("Tên đăng nhập '" + kh.getTendangnhap() + "' đã tồn tại!");
        }

        try {
            boolean result = khachHangDAO.insert(kh);
            if (result) {
                PermissionHelper.logAction("Thêm khách hàng", kh.getMakh());
            }
            return result;
        } catch (RuntimeException e) {
            throw new Exception(e.getMessage());
        }
    }

    public boolean suaKhachHang(KhachHang kh) throws Exception {
        // Kiểm tra quyền sửa (Helper logic: QUANLY sửa all, NHANVIEN không được sửa)
        if (!PermissionHelper.canEditKhachHang(kh.getMakh())) {
            // Nếu Helper trả về false (do là NHANVIEN), ta ném lỗi rõ ràng
            throw new Exception("Chỉ Quản lý mới có quyền sửa thông tin khách hàng!");
        }

        try {
            boolean result = khachHangDAO.update(kh);
            if (result) {
                PermissionHelper.logAction("Sửa khách hàng", kh.getMakh());
            }
            return result;
        } catch (RuntimeException e) {
            throw new Exception(e.getMessage());
        }
    }

    public boolean xoaKhachHang(String maKH) throws Exception {
        PermissionHelper.requireQuanLy();

        try {
            // DAO đã kiểm tra phiên chơi (hasActiveSession)
            boolean result = khachHangDAO.delete(maKH);
            if (result) {
                PermissionHelper.logAction("Xóa khách hàng", maKH);
            }
            return result;
        } catch (RuntimeException e) {
            throw new Exception(e.getMessage());
        }
    }

    public boolean khoiPhucKhachHang(String maKH) throws Exception {
        PermissionHelper.requireQuanLy();
        try {
            boolean result = khachHangDAO.restore(maKH);
            if (result) {
                PermissionHelper.logAction("Khôi phục khách hàng", maKH);
            }
            return result;
        } catch (RuntimeException e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Tìm kiếm khách hàng (In-memory search)
     * Vì DAO chưa có hàm search SQL, ta lọc từ list all
     */
    public List<KhachHang> timKiem(String keyword) throws Exception {
        PermissionHelper.requireNhanVien(); // NV và QL đều được tìm

        List<KhachHang> allList = khachHangDAO.getAll();

        if (keyword == null || keyword.trim().isEmpty()) {
            return allList;
        }

        String key = keyword.toLowerCase().trim();

        return allList.stream()
                .filter(kh ->
                        (kh.getTen() != null && kh.getTen().toLowerCase().contains(key)) ||
                                (kh.getHo() != null && kh.getHo().toLowerCase().contains(key)) ||
                                (kh.getSodienthoai() != null && kh.getSodienthoai().contains(key)) ||
                                (kh.getMakh() != null && kh.getMakh().toLowerCase().contains(key)) ||
                                (kh.getTendangnhap() != null && kh.getTendangnhap().toLowerCase().contains(key))
                )
                .collect(Collectors.toList());
    }

    // Lấy cảnh báo (Số dư, Gói...) để hiện popup confirm
    public String getCanhBaoXoa(String maKH) throws Exception {
        PermissionHelper.requireQuanLy();
        return khachHangDAO.getDeleteWarning(maKH);
    }
}