package bus;

import dao.NhanVienDAO;
import entity.NhanVien;
import untils.PasswordEncoder;
import untils.PermissionHelper;
import untils.SessionManager;

import java.util.List;

public class NhanVienBUS {
    private final NhanVienDAO nhanVienDAO;

    public NhanVienBUS() {
        this.nhanVienDAO = new NhanVienDAO();
    }

    // ================== 7.2 ĐĂNG NHẬP & TÀI KHOẢN ==================

    public NhanVien dangNhap(String tenDangNhap, String matKhau) throws Exception {
        // Kiểm tra dữ liệu đầu vào
        if (tenDangNhap == null || tenDangNhap.trim().isEmpty()) {
            throw new Exception("Chưa nhập tên đăng nhập!");
        }
        if (matKhau == null || matKhau.trim().isEmpty()) {
            throw new Exception("Chưa nhập mật khẩu!");
        }

        // Lấy thông tin nhân viên từ DB
        NhanVien nv = nhanVienDAO.getByTenDangNhap(tenDangNhap);

        // Kiểm tra tồn tại
        if (nv == null) {
            throw new Exception("Tên đăng nhập không chính xác!");
        }

        // So sánh mật khẩu (đã mã hóa)
        if (!PasswordEncoder.matches(matKhau, nv.getMatkhau())) {
            throw new Exception("Mật khẩu không chính xác!");
        }

        // Kiểm tra trạng thái làm việc
        if (nv.isNghiViec()) {
            throw new Exception("Nhân viên đã nghỉ việc, không thể đăng nhập!");
        }

        // Lưu thông tin vào phiên làm việc
        SessionManager.setCurrentUser(nv);
        return nv;
    }

    public boolean doiMatKhau(String maNV, String mkCu, String mkMoi) throws Exception {
        // 1. Kiểm tra quyền
        PermissionHelper.canEditNhanVien(maNV);

        // 2. Validate mật khẩu mới
        if (mkMoi == null || mkMoi.length() < 6) {
            throw new Exception("Mật khẩu mới phải có ít nhất 6 ký tự!");
        }

        NhanVien nv = nhanVienDAO.getById(maNV);
        if (nv == null) {
            throw new Exception("Nhân viên không tồn tại!");
        }

        // 3. Kiểm tra mật khẩu cũ
        // SỬA LẠI DÒNG NÀY: Dùng SessionManager.isQuanLy() thay vì PermissionHelper
        if (!SessionManager.isQuanLy() || (mkCu != null && !mkCu.trim().isEmpty())) {
            if (!PasswordEncoder.matches(mkCu, nv.getMatkhau())) {
                throw new Exception("Mật khẩu cũ không chính xác!");
            }
        }

        // 4. Cập nhật mật khẩu mới (Mã hóa)
        nv.setMatkhau(PasswordEncoder.encode(mkMoi));

        // 5. Gọi DAO update
        return nhanVienDAO.update(nv, SessionManager.getCurrentNhanVien());
    }

    // ================== 7.2 QUẢN LÝ NHÂN VIÊN ==================

    public List<NhanVien> getAllNhanVien() throws Exception {
        // Chỉ Quản lý mới xem được danh sách
        PermissionHelper.requireQuanLy();
        return nhanVienDAO.getAllDangLamViec();
    }

    public NhanVien getNhanVienById(String maNV) throws Exception {
        PermissionHelper.requireQuanLy();
        return nhanVienDAO.getById(maNV);
    }

    public boolean themNhanVien(NhanVien nv) throws Exception {
        // Chỉ Quản lý mới được thêm
        PermissionHelper.requireQuanLy();

        // Kiểm tra trùng tên đăng nhập
        if (nhanVienDAO.isTenDangNhapExists(nv.getTendangnhap())) {
            throw new Exception("Tên đăng nhập đã tồn tại!");
        }

        // Mã hóa mật khẩu
        if (nv.getMatkhau() != null) {
            nv.setMatkhau(PasswordEncoder.encode(nv.getMatkhau()));
        }

        try {
            // Gọi DAO thêm mới (truyền người thực hiện để DAO check lần cuối)
            boolean result = nhanVienDAO.insert(nv, SessionManager.getCurrentNhanVien());
            if (result) {
                PermissionHelper.logAction("Thêm nhân viên", nv.getManv());
            }
            return result;
        } catch (RuntimeException e) {
            throw new Exception(e.getMessage());
        }
    }

    public boolean suaNhanVien(NhanVien nv) throws Exception {
        // Kiểm tra quyền (Quản lý sửa tất cả, NV chỉ sửa mình)
        PermissionHelper.canEditNhanVien(nv.getManv());

        try {
            boolean result = nhanVienDAO.update(nv, SessionManager.getCurrentNhanVien());
            if (result) {
                // Nếu tự sửa mình thì cập nhật lại Session
                SessionManager.refreshCurrentNhanVien(nv);
                PermissionHelper.logAction("Sửa nhân viên", nv.getManv());
            }
            return result;
        } catch (RuntimeException e) {
            throw new Exception(e.getMessage());
        }
    }

    // ================== 7.3 XÓA NHÂN VIÊN (Logic Đặc Biệt) ==================

    public boolean xoaNhanVien(String maNV) throws Exception {
        // Chỉ Quản lý mới được xóa
        PermissionHelper.requireQuanLy();

        try {
            // Gọi DAO xóa
            // (DAO đã có logic chặn xóa nếu là Quản lý duy nhất)
            boolean result = nhanVienDAO.delete(maNV, SessionManager.getCurrentNhanVien());

            if (result) {
                PermissionHelper.logAction("Xóa nhân viên", maNV);
            }
            return result;
        } catch (RuntimeException e) {
            // Bắt lỗi từ DAO (VD: "Không thể xóa quản lý duy nhất") ném ra ngoài
            throw new Exception(e.getMessage());
        }
    }

    public boolean khoiPhucNhanVien(String maNV) throws Exception {
        PermissionHelper.requireQuanLy();
        try {
            boolean result = nhanVienDAO.restore(maNV, SessionManager.getCurrentNhanVien());
            if (result) {
                PermissionHelper.logAction("Khôi phục nhân viên", maNV);
            }
            return result;
        } catch (RuntimeException e) {
            throw new Exception(e.getMessage());
        }
    }

    public List<NhanVien> timKiemNhanVien(String keyword) throws Exception {
        PermissionHelper.requireQuanLy();
        return nhanVienDAO.search(keyword);
    }
}