package bus;

import dao.NhanVienDAO;
import entity.NhanVien;
import utils.PasswordEncoder;
import utils.PermissionHelper;
import utils.SessionManager;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NhanVienBUS {
    private final NhanVienDAO nhanVienDAO;

    public NhanVienBUS() {
        this.nhanVienDAO = new NhanVienDAO();
    }

    public NhanVien dangNhap(String tenDangNhap, String matKhau) throws Exception {
        if (tenDangNhap == null || tenDangNhap.trim().isEmpty()) {
            throw new Exception("Vui lòng nhập tên đăng nhập!");
        }
        if (matKhau == null || matKhau.trim().isEmpty()) {
            throw new Exception("Vui lòng nhập mật khẩu!");
        }

        NhanVien nv = nhanVienDAO.getByTenDangNhap(tenDangNhap);

        if (nv == null) {
            throw new Exception("Tên đăng nhập không tồn tại trong hệ thống!");
        }
        if (nv.isNghiViec()) {
            throw new Exception("Tài khoản nhân viên này đã bị vô hiệu hóa!");
        }

        if (!matKhau.equals(nv.getMatkhau())) {
            throw new Exception("Mật khẩu không chính xác!");
        }

        SessionManager.setCurrentUser(nv);
        return nv;
    }

    public boolean doiMatKhau(String maNV, String mkCu, String mkMoi) throws Exception {
        PermissionHelper.canEditNhanVien(maNV);
        validateMatKhau(mkMoi);

        NhanVien nv = nhanVienDAO.getById(maNV);
        if (nv == null) {
            throw new Exception("Không tìm thấy thông tin nhân viên!");
        }

        if (!SessionManager.isQuanLy() || (mkCu != null && !mkCu.trim().isEmpty())) {
            if (!PasswordEncoder.matches(mkCu, nv.getMatkhau())) {
                throw new Exception("Mật khẩu cũ không chính xác!");
            }
        }

        nv.setMatkhau(PasswordEncoder.encode(mkMoi));

        try {
            boolean result = nhanVienDAO.update(nv, SessionManager.getCurrentNhanVien());
            if (result) PermissionHelper.logAction("Đổi mật khẩu nhân viên", maNV);
            return result;
        } catch (RuntimeException e) {
            throw new Exception("Lỗi hệ thống khi đổi mật khẩu: " + e.getMessage());
        }
    }

    public boolean resetMatKhau(String maNV, String defaultPass) throws Exception {
        PermissionHelper.requireQuanLy();
        validateMatKhau(defaultPass);

        NhanVien nv = nhanVienDAO.getById(maNV);
        if (nv == null) {
            throw new Exception("Nhân viên không tồn tại!");
        }

        nv.setMatkhau(PasswordEncoder.encode(defaultPass));

        try {
            boolean result = nhanVienDAO.update(nv, SessionManager.getCurrentNhanVien());
            if (result) PermissionHelper.logAction("Reset mật khẩu nhân viên", maNV);
            return result;
        } catch (RuntimeException e) {
            throw new Exception("Lỗi hệ thống khi reset mật khẩu: " + e.getMessage());
        }
    }

    public List<NhanVien> getAllNhanVienDangLamViec() throws Exception {
        PermissionHelper.requireQuanLy();
        return nhanVienDAO.getAllDangLamViec();
    }

    public List<NhanVien> getAllNhanVienDaNghiViec() throws Exception {
        PermissionHelper.requireQuanLy();
        return nhanVienDAO.getAllDaNghiViec();
    }

    public NhanVien getNhanVienById(String maNV) throws Exception {
        PermissionHelper.requireQuanLy();
        NhanVien nv = nhanVienDAO.getById(maNV);
        if (nv == null) {
            throw new Exception("Không tìm thấy dữ liệu nhân viên với mã: " + maNV);
        }
        return nv;
    }

    public List<NhanVien> timKiemNhanVien(String keyword) throws Exception {
        PermissionHelper.requireQuanLy();
        return nhanVienDAO.search(keyword);
    }

    public List<NhanVien> locNhanVienTheoChucVu(String chucVu) throws Exception {
        PermissionHelper.requireQuanLy();
        List<NhanVien> listAll = nhanVienDAO.getAllDangLamViec();

        if (chucVu == null || chucVu.trim().isEmpty() || chucVu.equals("TATC")) {
            return listAll;
        }

        return listAll.stream()
                .filter(nv -> nv.getChucvu().equalsIgnoreCase(chucVu.trim()))
                .collect(Collectors.toList());
    }

    public boolean themNhanVien(NhanVien nv) throws Exception {
        PermissionHelper.requireQuanLy();

        chuanHoaDuLieuNhanVien(nv);
        validateNhanVienToanDien(nv, true);

        if (nhanVienDAO.isTenDangNhapExists(nv.getTendangnhap())) {
            throw new Exception("Tên đăng nhập '" + nv.getTendangnhap() + "' đã tồn tại!");
        }

        if (nv.getMatkhau() != null) {
            nv.setMatkhau(PasswordEncoder.encode(nv.getMatkhau()));
        }

        try {
            boolean result = nhanVienDAO.insert(nv, SessionManager.getCurrentNhanVien());
            if (result) PermissionHelper.logAction("Thêm nhân viên mới", nv.getManv());
            return result;
        } catch (RuntimeException e) {
            throw new Exception("Lỗi hệ thống khi thêm nhân viên: " + e.getMessage());
        }
    }

    public boolean suaNhanVien(NhanVien nv) throws Exception {
        PermissionHelper.canEditNhanVien(nv.getManv());

        chuanHoaDuLieuNhanVien(nv);
        validateNhanVienToanDien(nv, false);

        try {
            boolean result = nhanVienDAO.update(nv, SessionManager.getCurrentNhanVien());
            if (result) {
                if (nv.getManv().equals(SessionManager.getCurrentMaNV())) {
                    SessionManager.refreshCurrentNhanVien(nv);
                }
                PermissionHelper.logAction("Cập nhật thông tin nhân viên", nv.getManv());
            }
            return result;
        } catch (RuntimeException e) {
            throw new Exception("Lỗi hệ thống khi cập nhật: " + e.getMessage());
        }
    }

    public boolean xoaNhanVien(String maNV) throws Exception {
        PermissionHelper.requireQuanLy();

        try {
            boolean result = nhanVienDAO.delete(maNV, SessionManager.getCurrentNhanVien());
            if (result) PermissionHelper.logAction("Xóa nhân viên", maNV);
            return result;
        } catch (RuntimeException e) {
            if (e.getMessage().contains("duy nhất")) {
                throw new Exception("Không thể xóa quản lý duy nhất còn lại đang làm việc trong hệ thống!");
            }
            throw new Exception(e.getMessage());
        }
    }

    public boolean khoiPhucNhanVien(String maNV) throws Exception {
        PermissionHelper.requireQuanLy();
        try {
            boolean result = nhanVienDAO.restore(maNV, SessionManager.getCurrentNhanVien());
            if (result) PermissionHelper.logAction("Khôi phục nhân viên", maNV);
            return result;
        } catch (RuntimeException e) {
            throw new Exception("Lỗi khi khôi phục nhân viên: " + e.getMessage());
        }
    }

    public String getCanhBaoXoaNhanVien(String maNV) throws Exception {
        PermissionHelper.requireQuanLy();
        return nhanVienDAO.getDeleteWarning(maNV, SessionManager.getCurrentNhanVien());
    }

    public Map<String, Long> thongKeSoLuongTheoChucVu() throws Exception {
        PermissionHelper.requireQuanLy();
        List<NhanVien> listAll = nhanVienDAO.getAllDangLamViec();

        return listAll.stream()
                .collect(Collectors.groupingBy(NhanVien::getChucvu, Collectors.counting()));
    }

    public String xuatDanhSachCSV() throws Exception {
        PermissionHelper.requireQuanLy();
        List<NhanVien> all = nhanVienDAO.getAll();

        StringBuilder csv = new StringBuilder();
        csv.append("Mã NV,Họ,Tên,Chức Vụ,Tên Đăng Nhập,Trạng Thái\n");

        for (NhanVien nv : all) {
            csv.append(nv.getManv()).append(",")
                    .append(nv.getHo()).append(",")
                    .append(nv.getTen()).append(",")
                    .append(nv.getChucvu()).append(",")
                    .append(nv.getTendangnhap()).append(",")
                    .append(nv.getTrangthai()).append("\n");
        }
        return csv.toString();
    }

    private void validateNhanVienToanDien(NhanVien nv, boolean isInsert) throws Exception {
        validateHoTen(nv.getHo(), nv.getTen());
        validateChucVu(nv.getChucvu());

        if (isInsert) {
            validateTenDangNhap(nv.getTendangnhap());
            validateMatKhau(nv.getMatkhau());
        }
    }

    private void validateHoTen(String ho, String ten) throws Exception {
        if (ho == null || ho.trim().isEmpty()) {
            throw new Exception("Họ nhân viên không được để trống!");
        }
        if (ten == null || ten.trim().isEmpty()) {
            throw new Exception("Tên nhân viên không được để trống!");
        }
        if (ho.length() > 50 || ten.length() > 50) {
            throw new Exception("Họ và Tên nhân viên không được vượt quá 50 ký tự!");
        }
    }

    private void validateChucVu(String chucVu) throws Exception {
        if (chucVu == null || chucVu.trim().isEmpty()) {
            throw new Exception("Chức vụ không được để trống!");
        }
        String cv = chucVu.trim().toUpperCase();
        if (!cv.equals("QUANLY") && !cv.equals("NHANVIEN") && !cv.equals("THUNGAN")) {
            throw new Exception("Chức vụ không hợp lệ. Chỉ chấp nhận: QUANLY, NHANVIEN, THUNGAN.");
        }
    }

    private void validateTenDangNhap(String username) throws Exception {
        if (username == null || username.trim().length() < 4) {
            throw new Exception("Tên đăng nhập phải có ít nhất 4 ký tự!");
        }
        if (username.trim().length() > 50) {
            throw new Exception("Tên đăng nhập không được vượt quá 50 ký tự!");
        }
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            throw new Exception("Tên đăng nhập chỉ được chứa chữ cái không dấu, số và dấu gạch dưới (_).");
        }
    }

    private void validateMatKhau(String password) throws Exception {
        if (password == null || password.trim().isEmpty()) {
            throw new Exception("Mật khẩu không được để trống!");
        }
        if (password.length() < 6) {
            throw new Exception("Mật khẩu phải có tối thiểu 6 ký tự để đảm bảo an toàn.");
        }
    }

    private void chuanHoaDuLieuNhanVien(NhanVien nv) {
        if (nv.getHo() != null) {
            nv.setHo(chuanHoaChuoi(nv.getHo()));
        }
        if (nv.getTen() != null) {
            nv.setTen(chuanHoaChuoi(nv.getTen()));
        }
        if (nv.getTendangnhap() != null) {
            nv.setTendangnhap(nv.getTendangnhap().trim().toLowerCase());
        }
        if (nv.getChucvu() != null) {
            nv.setChucvu(nv.getChucvu().trim().toUpperCase());
        }
    }

    private String chuanHoaChuoi(String input) {
        if (input == null || input.trim().isEmpty()) return "";

        String[] words = input.trim().replaceAll("\\s+", " ").split(" ");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            result.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1).toLowerCase())
                    .append(" ");
        }
        return result.toString().trim();
    }
}