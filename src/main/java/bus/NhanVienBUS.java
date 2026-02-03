package bus;

import dao.NhanVienDAO;
import entity.NhanVien;
import untils.PermissionHelper;
import untils.SessionManager;

import java.util.List;

public class NhanVienBUS {
    private final NhanVienDAO nhanVienDAO;

    public NhanVienBUS() {
        this.nhanVienDAO = new NhanVienDAO();
    }

    /**
     * Xử lý đăng nhập nhân viên
     */
    public NhanVien dangNhap(String tenDangNhap, String matKhau) throws Exception {
        try {
            NhanVien nv = nhanVienDAO.login(tenDangNhap, matKhau);
            if (nv == null) {
                throw new Exception("Tên đăng nhập hoặc mật khẩu không chính xác!");
            }
            // Lưu session
            SessionManager.setCurrentUser(nv);
            return nv;
        } catch (RuntimeException e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Đăng xuất
     */
    public void dangXuat() {
        SessionManager.logout();
    }

    public List<NhanVien> getAllDangLamViec() throws Exception {
        // Chỉ Quản lý mới được xem danh sách nhân viên
        PermissionHelper.requireQuanLy();
        return nhanVienDAO.getAllDangLamViec();
    }

    public List<NhanVien> getAllDaNghiViec() throws Exception {
        PermissionHelper.requireQuanLy();
        return nhanVienDAO.getAllDaNghiViec();
    }

    public boolean themNhanVien(NhanVien nv) throws Exception {
        // 1. Kiểm tra quyền (Helper tự throw Exception nếu không phải QUANLY)
        PermissionHelper.requireQuanLy();

        // 2. Lấy người thực hiện từ Session (đảm bảo không null vì đã qua requireQuanLy)
        NhanVien nguoiThucHien = SessionManager.getCurrentNhanVien();

        // 3. Logic nghiệp vụ bổ sung (nếu DAO chưa có)
        if (nhanVienDAO.isTenDangNhapExists(nv.getTendangnhap())) {
            throw new Exception("Tên đăng nhập đã tồn tại!");
        }

        try {
            boolean result = nhanVienDAO.insert(nv, nguoiThucHien);
            if (result) {
                PermissionHelper.logAction("Thêm nhân viên", nv.getManv());
            }
            return result;
        } catch (RuntimeException e) {
            throw new Exception(e.getMessage());
        }
    }

    public boolean suaNhanVien(NhanVien nv) throws Exception {
        // 1. Kiểm tra xem người dùng hiện tại có quyền sửa nhân viên này không
        // (Helper tự check: QUANLY sửa all, NV chỉ sửa chính mình)
        PermissionHelper.canEditNhanVien(nv.getManv());

        NhanVien nguoiThucHien = SessionManager.getCurrentNhanVien();

        try {
            boolean result = nhanVienDAO.update(nv, nguoiThucHien);
            if (result) {
                // Nếu tự sửa thông tin chính mình, cần refresh lại session
                SessionManager.refreshCurrentNhanVien(nv);
                PermissionHelper.logAction("Sửa nhân viên", nv.getManv());
            }
            return result;
        } catch (RuntimeException e) {
            throw new Exception(e.getMessage());
        }
    }

    public boolean xoaNhanVien(String maNV) throws Exception {
        // 1. Kiểm tra quyền xóa (Chỉ QUANLY và không được xóa chính mình)
        PermissionHelper.canDeleteNhanVien(maNV);

        NhanVien nguoiThucHien = SessionManager.getCurrentNhanVien();

        try {
            // Trước khi xóa, nên kiểm tra cảnh báo nghiệp vụ
            String warning = nhanVienDAO.getDeleteWarning(maNV, nguoiThucHien);
            if (warning != null && warning.contains("Không thể xóa")) {
                throw new Exception(warning);
            }

            boolean result = nhanVienDAO.delete(maNV, nguoiThucHien);
            if (result) {
                PermissionHelper.logAction("Xóa nhân viên", maNV);
            }
            return result;
        } catch (RuntimeException e) {
            throw new Exception(e.getMessage());
        }
    }

    public boolean khoiPhucNhanVien(String maNV) throws Exception {
        PermissionHelper.requireQuanLy();
        NhanVien nguoiThucHien = SessionManager.getCurrentNhanVien();

        try {
            boolean result = nhanVienDAO.restore(maNV, nguoiThucHien);
            if (result) {
                PermissionHelper.logAction("Khôi phục nhân viên", maNV);
            }
            return result;
        } catch (RuntimeException e) {
            throw new Exception(e.getMessage());
        }
    }

    public List<NhanVien> timKiem(String keyword) throws Exception {
        PermissionHelper.requireQuanLy();
        return nhanVienDAO.search(keyword);
    }

    // Hỗ trợ GUI lấy cảnh báo trước khi hiện Dialog confirm
    public String getCanhBaoXoa(String maNV) throws Exception {
        PermissionHelper.requireQuanLy();
        return nhanVienDAO.getDeleteWarning(maNV, SessionManager.getCurrentNhanVien());
    }
}