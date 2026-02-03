package bus;

import dao.KhachHangDAO;
import entity.KhachHang;
import java.util.List;
import java.util.stream.Collectors;

public class KhachHangBUS {
    private final KhachHangDAO khachHangDAO;

    public KhachHangBUS() {
        this.khachHangDAO = new KhachHangDAO();
    }

    // Lấy danh sách tất cả khách hàng
    public List<KhachHang> getAllKhachHang() {
        return khachHangDAO.getAll();
    }

    // Tìm kiếm khách hàng (Tìm theo Tên hoặc SĐT)
    // Vì DAO chưa có hàm search, BUS sẽ xử lý lọc list (theo nguyên tắc Layered Architecture)
    public List<KhachHang> timKiemKhachHang(String keyword) {
        List<KhachHang> all = khachHangDAO.getAll();
        if (keyword == null || keyword.isEmpty()) {
            return all;
        }
        String key = keyword.toLowerCase();
        return all.stream()
                .filter(kh -> kh.getTen().toLowerCase().contains(key) ||
                        kh.getHo().toLowerCase().contains(key) ||
                        (kh.getSodienthoai() != null && kh.getSodienthoai().contains(key)))
                .collect(Collectors.toList());
    }

    // Đăng nhập dành cho Khách hàng (Tại máy trạm)
    public KhachHang dangNhap(String tenDangNhap, String matKhau) throws Exception {
        try {
            KhachHang kh = khachHangDAO.login(tenDangNhap, matKhau);
            if (kh == null) {
                throw new Exception("Tên đăng nhập hoặc mật khẩu không đúng!");
            }
            if (kh.isNgung()) {
                throw new Exception("Tài khoản khách hàng đã bị khóa!");
            }
            return kh;
        } catch (RuntimeException e) {
            throw new Exception(e.getMessage());
        }
    }

    // Thêm khách hàng mới (Đăng ký)
    public boolean themKhachHang(KhachHang kh) throws Exception {
        // 1. Validate dữ liệu đầu vào
        if (kh.getHo() == null || kh.getTen() == null) {
            throw new Exception("Họ và tên không được để trống!");
        }

        // Kiểm tra SĐT (10 số, bắt đầu bằng 0)
        if (kh.getSodienthoai() != null && !kh.getSodienthoai().matches("^0\\d{9}$")) {
            throw new Exception("Số điện thoại không đúng định dạng (phải có 10 số)!");
        }

        // Kiểm tra trùng tên đăng nhập
        if (khachHangDAO.isTenDangNhapExists(kh.getTendangnhap())) {
            throw new Exception("Tên đăng nhập '" + kh.getTendangnhap() + "' đã có người sử dụng!");
        }

        try {
            return khachHangDAO.insert(kh);
        } catch (RuntimeException e) {
            throw new Exception("Lỗi thêm khách hàng: " + e.getMessage());
        }
    }

    // Cập nhật thông tin khách hàng
    public boolean suaKhachHang(KhachHang kh) throws Exception {
        // Validate cơ bản
        if (kh.getMatkhau() != null && kh.getMatkhau().length() < 6) {
            throw new Exception("Mật khẩu mới phải có ít nhất 6 ký tự!");
        }

        try {
            return khachHangDAO.update(kh);
        } catch (RuntimeException e) {
            throw new Exception("Lỗi cập nhật: " + e.getMessage());
        }
    }

    // Xóa khách hàng (Khóa tài khoản)
    public boolean xoaKhachHang(String maKH) throws Exception {
        // 1. Kiểm tra logic nghiệp vụ [cite: 123]
        // DAO delete() đã kiểm tra hasActiveSession (Khách đang chơi) và ném exception nếu có.

        try {
            return khachHangDAO.delete(maKH);
        } catch (RuntimeException e) {
            // Chuyển RuntimeException từ DAO thành Exception có message rõ ràng cho GUI
            throw new Exception(e.getMessage());
        }
    }

    // Khôi phục khách hàng
    public boolean khoiPhucKhachHang(String maKH) throws Exception {
        try {
            return khachHangDAO.restore(maKH);
        } catch (RuntimeException e) {
            throw new Exception(e.getMessage());
        }
    }

    // Lấy cảnh báo trước khi xóa (Check số dư, gói còn hạn...) [cite: 123]
    public String getCanhBaoXoa(String maKH) {
        return khachHangDAO.getDeleteWarning(maKH);
    }

    // Lấy khách hàng theo ID
    public KhachHang getKhachHangById(String maKH) throws Exception {
        // Vì DAO không public getById, ta dùng getAll filter hoặc thêm hàm getById public vào DAO.
        // Ở đây giả định ta sẽ tìm trong getAll để không sửa DAO.
        return khachHangDAO.getAll().stream()
                .filter(kh -> kh.getMakh().equals(maKH))
                .findFirst()
                .orElse(null);
    }
}