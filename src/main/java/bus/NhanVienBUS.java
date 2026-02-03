package bus;

import dao.NhanVienDAO;
import entity.NhanVien;
import java.util.List;

public class NhanVienBUS {
    private final NhanVienDAO nhanVienDAO;

    public NhanVienBUS() {
        this.nhanVienDAO = new NhanVienDAO();
    }

    // Lấy danh sách nhân viên đang làm việc (Phân quyền: QUANLY)
    public List<NhanVien> getAllNhanVien(NhanVien nguoiThucHien) throws Exception {
        if (!nguoiThucHien.isQuanLy()) {
            throw new Exception("Chỉ Quản lý mới có quyền xem danh sách nhân viên!");
        }
        return nhanVienDAO.getAllDangLamViec();
    }

    // Đăng nhập hệ thống
    public NhanVien dangNhap(String tenDangNhap, String matKhau) throws Exception {
        if (tenDangNhap == null || tenDangNhap.trim().isEmpty()) {
            throw new Exception("Tên đăng nhập không được để trống!");
        }
        if (matKhau == null || matKhau.trim().isEmpty()) {
            throw new Exception("Mật khẩu không được để trống!");
        }

        NhanVien nv = nhanVienDAO.login(tenDangNhap, matKhau);
        if (nv == null) {
            throw new Exception("Tên đăng nhập hoặc mật khẩu không chính xác!");
        }
        if (nv.isNghiViec()) {
            throw new Exception("Tài khoản này đã bị vô hiệu hóa (Nghỉ việc)!");
        }
        return nv;
    }

    // Thêm nhân viên mới (Phân quyền: QUANLY)
    public boolean themNhanVien(NhanVien nv, NhanVien nguoiThucHien) throws Exception {
        // 1. Kiểm tra phân quyền
        if (!nguoiThucHien.isQuanLy()) {
            throw new Exception("Truy cập bị từ chối: Chỉ Quản lý mới được thêm nhân viên!");
        }

        // 2. Validate dữ liệu
        if (nhanVienDAO.isTenDangNhapExists(nv.getTendangnhap())) {
            throw new Exception("Tên đăng nhập '" + nv.getTendangnhap() + "' đã tồn tại!");
        }

        // Validate password độ dài, v.v. (Logic bổ sung cho DAO)
        if (nv.getMatkhau().length() < 6) {
            throw new Exception("Mật khẩu phải có ít nhất 6 ký tự!");
        }

        // 3. Gọi DAO thực hiện
        try {
            return nhanVienDAO.insert(nv, nguoiThucHien);
        } catch (RuntimeException e) {
            throw new Exception(e.getMessage());
        }
    }

    // Cập nhật thông tin nhân viên
    public boolean suaNhanVien(NhanVien nv, NhanVien nguoiThucHien) throws Exception {
        // DAO đã kiểm tra logic: NV thường chỉ sửa được chính mình, không sửa được chức vụ.
        // BUS bọc lại exception để hiển thị thông báo thân thiện hơn.
        try {
            return nhanVienDAO.update(nv, nguoiThucHien);
        } catch (RuntimeException e) {
            throw new Exception(e.getMessage());
        }
    }

    // Cho nghỉ việc (Xóa mềm) (Phân quyền: QUANLY)
    public boolean xoaNhanVien(String maNV, NhanVien nguoiThucHien) throws Exception {
        // 1. Kiểm tra phân quyền
        if (!nguoiThucHien.isQuanLy()) {
            throw new Exception("Truy cập bị từ chối: Chỉ Quản lý mới được xóa nhân viên!");
        }

        // 2. Kiểm tra logic nghiệp vụ đặc biệt (Quản lý cuối cùng)
        // DAO đã tích hợp sẵn logic này trong hàm delete (ném RuntimeException)
        // Nhưng BUS nên gọi getDeleteWarning trước nếu cần hiển thị confirm trên GUI

        try {
            return nhanVienDAO.delete(maNV, nguoiThucHien);
        } catch (RuntimeException e) {
            throw new Exception(e.getMessage());
        }
    }

    // Khôi phục nhân viên (Phân quyền: QUANLY)
    public boolean khoiPhucNhanVien(String maNV, NhanVien nguoiThucHien) throws Exception {
        if (!nguoiThucHien.isQuanLy()) {
            throw new Exception("Truy cập bị từ chối: Chỉ Quản lý mới được khôi phục nhân viên!");
        }
        try {
            return nhanVienDAO.restore(maNV, nguoiThucHien);
        } catch (RuntimeException e) {
            throw new Exception(e.getMessage());
        }
    }

    // Tìm kiếm nhân viên
    public List<NhanVien> timKiemNhanVien(String keyword, NhanVien nguoiThucHien) throws Exception {
        if (!nguoiThucHien.isQuanLy()) {
            throw new Exception("Chỉ Quản lý mới có quyền tìm kiếm nhân viên!");
        }
        return nhanVienDAO.search(keyword);
    }

    // Lấy cảnh báo trước khi xóa (Để hiển thị Yes/No dialog)
    public String getCanhBaoXoa(String maNV, NhanVien nguoiThucHien) {
        return nhanVienDAO.getDeleteWarning(maNV, nguoiThucHien);
    }
}