package test.bus;

import bus.KhachHangBUS;
import entity.KhachHang;
import entity.NhanVien;
import utils.SessionManager;

import java.util.List;

public class TestKhachHangBUS {
    public static void main(String[] args) {
        KhachHangBUS bus = new KhachHangBUS();
        System.out.println("=== BẮT ĐẦU TEST KHACHHANGBUS ===");

        System.out.println("\n[0] Giả lập đăng nhập Quản lý...");
        NhanVien admin = new NhanVien();
        admin.setManv("NV_TEST");
        admin.setTen("Admin Test");
        admin.setChucvu("QUANLY");
        SessionManager.setCurrentUser(admin);

        String maKHMoi = "";

        try {
            System.out.println("\n[1] Test Lấy Danh Sách & Tìm Kiếm:");
            List<KhachHang> list = bus.getAllKhachHang();
            System.out.println("=> Tổng số khách hàng hiện tại: " + list.size());

            String keyword = "Nam";
            List<KhachHang> searchResult = bus.timKiemKhachHang(keyword);
            System.out.println("=> Tìm thấy " + searchResult.size() + " khách hàng chứa từ khóa '" + keyword + "'");
        } catch (Exception e) {
            System.err.println("=> Lỗi [1]: " + e.getMessage());
        }

        try {
            System.out.println("\n[2] Test Thêm Khách Hàng Mới:");
            KhachHang khNew = new KhachHang();
            khNew.setHo("Lê");
            khNew.setTen("Test BUS");
            khNew.setSodienthoai("0999888777");

            String username = "testbus_" + System.currentTimeMillis();
            khNew.setTendangnhap(username);
            khNew.setMatkhau("123456");

            if (bus.themKhachHang(khNew)) {
                maKHMoi = khNew.getMakh();
                System.out.println("=> THÀNH CÔNG: Đã thêm khách hàng mã " + maKHMoi + " | Username: " + username);
            }
        } catch (Exception e) {
            System.err.println("=> Lỗi [2]: " + e.getMessage());
        }

        if (!maKHMoi.isEmpty()) {
            try {
                System.out.println("\n[3] Test Cập Nhật Thông Tin:");
                KhachHang khUpdate = bus.getKhachHangById(maKHMoi);

                khUpdate.setHo("Lê (Đã sửa)");
                khUpdate.setSodienthoai("0911222333");

                if (bus.suaKhachHang(khUpdate)) {
                    System.out.println("=> THÀNH CÔNG: Đã cập nhật SĐT và Họ cho " + maKHMoi);
                }
            } catch (Exception e) {
                System.err.println("=> Lỗi [3]: " + e.getMessage());
            }
        }

        if (!maKHMoi.isEmpty()) {
            try {
                System.out.println("\n[4] Test Khách Hàng Đăng Nhập:");

                SessionManager.logout();

                dao.KhachHangDAO dao = new dao.KhachHangDAO();
                KhachHang tmp = dao.getById(maKHMoi);

                KhachHang loginResult = bus.dangNhap(tmp.getTendangnhap(), "123456");
                if (loginResult != null) {
                    System.out.println("=> THÀNH CÔNG: Khách hàng " + tmp.getTendangnhap() + " đã đăng nhập qua mã hóa Password.");
                }

            } catch (Exception e) {
                System.err.println("=> Lỗi [4]: " + e.getMessage());
            }
        }

        if (!maKHMoi.isEmpty()) {
            try {
                System.out.println("\n[5] Test Xóa Khách Hàng:");

                SessionManager.setCurrentUser(admin);

                String warning = bus.getCanhBaoXoa(maKHMoi);
                if (warning != null) {
                    System.out.println("   Cảnh báo từ hệ thống: " + warning);
                } else {
                    System.out.println("   Hệ thống: Khách hàng an toàn để xóa.");
                }

                if (bus.xoaKhachHang(maKHMoi)) {
                    System.out.println("=> THÀNH CÔNG: Đã khóa/xóa tạm khách hàng " + maKHMoi);
                }
            } catch (Exception e) {
                System.err.println("=> Lỗi [5]: " + e.getMessage());
            }
        }

        System.out.println("\n=== KẾT THÚC TEST KHACHHANGBUS ===");
    }
}