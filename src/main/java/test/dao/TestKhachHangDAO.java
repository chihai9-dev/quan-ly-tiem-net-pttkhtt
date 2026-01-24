package test.dao;

import dao.KhachHangDAO;
import entity.KhachHang;
import java.util.List;

public class TestKhachHangDAO {
    public static void main(String[] args) {
        KhachHangDAO dao = new KhachHangDAO();

        System.out.println("--- BẮT ĐẦU TEST KHACHHANGDAO ---");

        // 1. Test chức năng INSERT (Thêm mới)
//        try {
//            KhachHang newKh = new KhachHang();
//            newKh.setHo("Nguyễn");
//            newKh.setTen("Văn A");
//            newKh.setSodienthoai("0987654321");
//            newKh.setTendangnhap("testuser_" + System.currentTimeMillis() / 1000); // Tạo tên ngẫu nhiên để tránh trùng
//            newKh.setMatkhau("123456");
//
//            boolean isInserted = dao.insert(newKh);
//            if (isInserted) {
//                System.out.println("=> Thành công: Đã thêm khách hàng mới với mã: " + newKh.getMakh());
//            }
//        } catch (RuntimeException e) {
//            System.err.println("=> Lỗi Insert: " + e.getMessage());
//        }

        // 2. Test chức năng LOGIN (Đăng nhập)
        try {
            System.out.println("\n--- Test Đăng nhập ---");
            // Thay bằng tên đăng nhập và mật khẩu có thật trong DB của bạn
            KhachHang loginKh = dao.login("hoangnam", "123456");
            if (loginKh != null) {
                System.out.println("=> Thành công: Chào mừng " + loginKh.getHo() + " " + loginKh.getTen());
            } else {
                System.out.println("=> Thất bại: Sai tài khoản hoặc mật khẩu.");
            }
        } catch (RuntimeException e) {
            System.err.println("=> Lỗi Login: " + e.getMessage());
        }

        // 3. Test chức năng VALIDATION (Kiểm tra lỗi nhập liệu)
//        try {
//            System.out.println("\n--- Test Validation (Cố tình nhập sai SĐT) ---");
//            KhachHang errorKh = new KhachHang();
//            errorKh.setHo("Lỗi");
//            errorKh.setTen("SĐT");
//            errorKh.setSodienthoai("123"); // SĐT không bắt đầu bằng 0 và không đủ 10 số
//            dao.insert(errorKh);
//        } catch (RuntimeException e) {
//            System.out.println("=> Thành công (Bắt được lỗi mong đợi): " + e.getMessage());
//        }

        // 4. Test chức năng GET ALL (Lấy danh sách)
//        try {
//            System.out.println("\n--- Test Lấy tất cả khách hàng ---");
//            List<KhachHang> list = dao.getAll();
//            System.out.println("=> Tổng số khách hàng hiện có: " + list.size());
//            if (!list.isEmpty()) {
//                System.out.println("Khách hàng mới nhất: " + list.get(0).getTen());
//            }
//        } catch (RuntimeException e) {
//            System.err.println("=> Lỗi GetAll: " + e.getMessage());
//        }

        // 5. Test chức năng SOFT DELETE (Xóa tạm)
        try {
            System.out.println("\n--- Test Xóa khách hàng ---");
            // Thay "KH001" bằng một mã khách hàng đang có trong DB của bạn
            String maDelele = "KH016";

            // Xem cảnh báo trước khi xóa
            String warning = dao.getDeleteWarning(maDelele);
            if(warning != null) {
                System.out.println("Cảnh báo trước khi xóa: " + warning);
            }

            boolean isDeleted = dao.delete(maDelele);
            if (isDeleted) {
                System.out.println("=> Thành công: Đã chuyển trạng thái khách hàng sang NGUNG.");
            }
        } catch (RuntimeException e) {
            System.err.println("=> Lỗi Delete: " + e.getMessage());
        }

        System.out.println("\n--- KẾT THÚC TEST ---");
    }
}