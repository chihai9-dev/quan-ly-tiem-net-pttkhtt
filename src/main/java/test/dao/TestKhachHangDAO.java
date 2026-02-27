package test.dao;

import entity.KhachHang;
import java.util.List;

public class TestKhachHangDAO {
    public static void main(String[] args) {
        KhachHangDAO dao = new KhachHangDAO();
        System.out.println("--- BẮT ĐẦU TEST TOÀN DIỆN KHACHHANGDAO ---");

        String maKhMoi = ""; // Biến lưu mã để dùng xuyên suốt các bài test

        // ==========================================
        // 1. TEST INSERT (THÊM MỚI)
        // ==========================================
        try {
            System.out.println("\n[1] Test Thêm Mới (Insert):");
            KhachHang newKh = new KhachHang();
            newKh.setHo("Phạm");
            newKh.setTen("Văn Test");
            newKh.setSodienthoai("0912345678");
            // Tạo user ngẫu nhiên để không bị trùng khi chạy lại nhiều lần
            newKh.setTendangnhap("user_" + System.currentTimeMillis());
            newKh.setMatkhau("123456");

            if (dao.insert(newKh)) {
                maKhMoi = newKh.getMakh(); // Lưu lại mã (VD: KH016)
                System.out.println("=> THÀNH CÔNG: Đã thêm khách hàng mã: " + maKhMoi);
            }
        } catch (Exception e) {
            System.err.println("=> THẤT BẠI: " + e.getMessage());
        }

        // ==========================================
        // 2. TEST UPDATE (CẬP NHẬT) - MỚI THÊM
        // ==========================================
        if (!maKhMoi.isEmpty()) {
            try {
                System.out.println("\n[2] Test Cập Nhật (Update):");
                // Lấy thông tin cũ
                KhachHang khUpdate = new KhachHang();
                khUpdate.setMakh(maKhMoi);
                khUpdate.setHo("Phạm (Đã Sửa)");
                khUpdate.setTen("Văn Update");
                khUpdate.setSodienthoai("0999888777");
                khUpdate.setMatkhau("654321");

                // Gọi hàm update
                if (dao.update(khUpdate)) {
                    System.out.println("=> THÀNH CÔNG: Đã cập nhật thông tin cho " + maKhMoi);

                    // Kiểm tra lại xem đã đổi thật chưa
                    KhachHang check = dao.login(khUpdate.getTendangnhap(), "654321"); // Thử login mật khẩu mới (nếu có logic get)
                    // Hoặc dùng hàm getAll để check tên
                }
            } catch (Exception e) {
                System.err.println("=> THẤT BẠI: " + e.getMessage());
            }
        }

        // ==========================================
        // 3. TEST LOGIN (ĐĂNG NHẬP)
        // ==========================================
        try {
            System.out.println("\n[3] Test Đăng Nhập (Login):");
            // Test user có sẵn trong file sql (KH001)
            KhachHang kh = dao.login("hoangnam", "123456");
            if (kh != null) {
                System.out.println("=> THÀNH CÔNG: Đăng nhập được user cũ: " + kh.getHo() + " " + kh.getTen());
            }

            // Test user mới tạo (nếu có)
            // Lưu ý: user mới tạo ở bước 1 không biết tên đăng nhập là gì vì random,
            // nhưng trong thực tế bạn có thể lưu lại biến tên đăng nhập để test.
        } catch (Exception e) {
            System.err.println("=> THẤT BẠI: " + e.getMessage());
        }

        // ==========================================
        // 4. TEST GET ALL (LẤY DANH SÁCH)
        // ==========================================
        try {
            System.out.println("\n[4] Test Lấy Danh Sách (GetAll):");
            List<KhachHang> list = dao.getAll();
            System.out.println("=> Tổng số khách hàng: " + list.size());
            if(!list.isEmpty()){
                System.out.println("   Khách hàng mới nhất: " + list.get(0).getMakh() + " - " + list.get(0).getTen());
            }
        } catch (Exception e) {
            System.err.println("=> THẤT BẠI: " + e.getMessage());
        }

        // ==========================================
        // 5. TEST SOFT DELETE (XÓA TẠM)
        // ==========================================
        if (!maKhMoi.isEmpty()) {
            try {
                System.out.println("\n[5] Test Xóa Khách Hàng (Soft Delete):");

                // Check cảnh báo
                String warning = dao.getDeleteWarning(maKhMoi);
                if (warning != null) System.out.println("   Cảnh báo: " + warning);

                if (dao.delete(maKhMoi)) {
                    System.out.println("=> THÀNH CÔNG: Đã xóa (NGUNG) khách hàng " + maKhMoi);
                }
            } catch (Exception e) {
                System.err.println("=> THẤT BẠI: " + e.getMessage());
            }
        }

        // ==========================================
        // 6. TEST RESTORE (KHÔI PHỤC) - MỚI THÊM
        // ==========================================
        if (!maKhMoi.isEmpty()) {
            try {
                System.out.println("\n[6] Test Khôi Phục (Restore):");
                if (dao.restore(maKhMoi)) {
                    System.out.println("=> THÀNH CÔNG: Đã khôi phục (HOATDONG) lại khách hàng " + maKhMoi);
                }
            } catch (Exception e) {
                System.err.println("=> THẤT BẠI: " + e.getMessage());
            }
        }

        System.out.println("\n--- KẾT THÚC TEST ---");
    }
}