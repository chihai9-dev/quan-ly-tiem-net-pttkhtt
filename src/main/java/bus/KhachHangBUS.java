package bus;

import dao.KhachHangDAO;
import entity.KhachHang;
import untils.PermissionHelper;
import untils.SessionManager;
import untils.PasswordEncoder;

import java.util.List;
import java.util.stream.Collectors;

public class KhachHangBUS {
    private final KhachHangDAO khachHangDAO;

    public KhachHangBUS() {
        this.khachHangDAO = new KhachHangDAO();
    }

    public KhachHang dangKy(KhachHang kh) throws Exception {
        if (kh.getHo() == null || kh.getHo().trim().length() < 2 ||
                kh.getTen() == null || kh.getTen().trim().length() < 2) {
            throw new Exception("Họ tên không hợp lệ");
        }
        if (kh.getSodienthoai() == null || !kh.getSodienthoai().matches("^0\\d{9}$")) {
            throw new Exception("Số điện thoại không đúng định dạng");
        }
        if (kh.getTendangnhap() == null || kh.getTendangnhap().trim().length() < 4) {
            throw new Exception("Tên đăng nhập phải từ 4 ký tự");
        }
        if (kh.getMatkhau() == null || kh.getMatkhau().length() < 6) {
            throw new Exception("Mật khẩu quá ngắn (tối thiểu 6 ký tự)");
        }
        if (khachHangDAO.isTenDangNhapExists(kh.getTendangnhap())) {
            throw new Exception("Tên đăng nhập đã tồn tại");
        }

        String encodedPass = PasswordEncoder.encode(kh.getMatkhau());
        kh.setMatkhau(encodedPass);

        try {
            boolean isSuccess = khachHangDAO.insert(kh);
            if (!isSuccess) throw new Exception("Đăng ký thất bại");
        } catch (RuntimeException e) {
            throw new Exception(e.getMessage());
        }
        return kh;
    }

    public KhachHang dangNhap(String tenDangNhap, String matKhau) throws Exception {
        if (tenDangNhap == null || tenDangNhap.trim().isEmpty()) {
            throw new Exception("Tên đăng nhập không được để trống!");
        }
        if (matKhau == null || matKhau.trim().isEmpty()) {
            throw new Exception("Mật khẩu không được để trống!");
        }

        KhachHang kh = khachHangDAO.getByTenDangNhap(tenDangNhap);
        if (kh == null) {
            throw new Exception("Tên đăng nhập không tồn tại!");
        }
        if (kh.isNgung()) {
            throw new Exception("Tài khoản đã bị khóa!");
        }

        boolean isMatch = PasswordEncoder.matches(matKhau, kh.getMatkhau());
        if (!isMatch) {
            throw new Exception("Mật khẩu không chính xác!");
        }

        SessionManager.setCurrentUser(kh);
        return kh;
    }

    public List<KhachHang> getAllKhachHang() throws Exception {
        PermissionHelper.requireNhanVien();
        return khachHangDAO.getAll();
    }

    public boolean themKhachHang(KhachHang kh) throws Exception {
        PermissionHelper.requireQuanLy();
        if (khachHangDAO.isTenDangNhapExists(kh.getTendangnhap())) {
            throw new Exception("Tên đăng nhập đã tồn tại!");
        }
        try {
            if(kh.getMatkhau() != null) {
                kh.setMatkhau(PasswordEncoder.encode(kh.getMatkhau()));
            }
            boolean result = khachHangDAO.insert(kh);
            if (result) PermissionHelper.logAction("Thêm khách hàng", kh.getMakh());
            return result;
        } catch (RuntimeException e) {
            throw new Exception(e.getMessage());
        }
    }

    public boolean suaKhachHang(KhachHang kh) throws Exception {
        if (!PermissionHelper.canEditKhachHang(kh.getMakh())) {
            throw new Exception("Chỉ Quản lý mới có quyền sửa thông tin khách hàng!");
        }
        try {
            boolean result = khachHangDAO.update(kh);
            if (result) PermissionHelper.logAction("Sửa khách hàng", kh.getMakh());
            return result;
        } catch (RuntimeException e) {
            throw new Exception(e.getMessage());
        }
    }

    // === FUNCTION 6.5: XÓA KHÁCH HÀNG ===
    public boolean xoaKhachHang(String maKH) throws Exception {
        // 1. Phân quyền
        PermissionHelper.requireQuanLy();

        try {
            // 2-5. Kiểm tra phiên & Xóa (DAO handle)
            boolean result = khachHangDAO.delete(maKH);

            // 6. Log & Return
            if (result) {
                PermissionHelper.logAction("Xóa khách hàng", maKH);
            }
            return result;
        } catch (RuntimeException e) {
            if (e.getMessage().contains("phiên chơi")) {
                throw new Exception("Khách hàng đang có phiên sử dụng");
            }
            throw new Exception(e.getMessage());
        }
    }

    // Hàm hỗ trợ lấy cảnh báo (Bước check SoDu và Gói)
    public String getCanhBaoXoa(String maKH) throws Exception {
        PermissionHelper.requireQuanLy();
        return khachHangDAO.getDeleteWarning(maKH);
    }

    public boolean khoiPhucKhachHang(String maKH) throws Exception {
        PermissionHelper.requireQuanLy();
        try {
            boolean result = khachHangDAO.restore(maKH);
            if (result) PermissionHelper.logAction("Khôi phục khách hàng", maKH);
            return result;
        } catch (RuntimeException e) {
            throw new Exception(e.getMessage());
        }
    }
    // ================== KIỂM TRA SỐ DƯ ==================
    public double kiemTraSoDu(String maKH) throws Exception {
        // 1. Kiểm tra quyền: Chỉ Nhân viên hoặc Quản lý mới được xem
        PermissionHelper.requireNhanVien();

        // 2. Gọi DAO lấy thông tin khách hàng
        KhachHang kh = khachHangDAO.getById(maKH);

        // 3. Kiểm tra tồn tại
        if (kh == null) {
            throw new Exception("Khách hàng không tồn tại!");
        }

        // 4. Trả về số dư
        return kh.getSodu();
    }

    public List<KhachHang> timKiemKhachHang(String keyword) throws Exception {
        PermissionHelper.requireNhanVien();
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
}
