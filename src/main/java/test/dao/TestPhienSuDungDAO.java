//package test.dao;
//
//import dao.PhienSuDungDAO;
//import entity.PhienSuDung;
//import java.time.LocalDateTime;
//import java.util.List;
//
//public class TestPhienSuDungDAO {
//    public static void main(String[] args) {
//        PhienSuDungDAO dao = new PhienSuDungDAO();
//        System.out.println("--- BẮT ĐẦU TEST PHIENSUDUNG DAO ---");
//
//        // Biến lưu mã phiên vừa tạo để dùng cho các test sau
//        String maPhienVuaTao = "";
//
//        // ========================================================
//        // 1. TEST SINH MÃ & LẤY DANH SÁCH
//        // ========================================================
//        try {
//            System.out.println("\n[1] Kiểm tra dữ liệu chung:");
//            System.out.println("- Mã phiên dự kiến tiếp theo: " + dao.generateMaPhien());
//
//            List<PhienSuDung> list = dao.getAll();
//            System.out.println("- Tổng số phiên trong CSDL: " + list.size());
//        } catch (Exception e) {
//            System.err.println("Lỗi phần 1: " + e.getMessage());
//        }
//
//        // ========================================================
//        // 2. TEST MỞ PHIÊN MỚI (INSERT)
//        // ========================================================
//        try {
//            System.out.println("\n[2] Test Mở Phiên Mới:");
//
//            // GIẢ LẬP DỮ LIỆU ĐẦU VÀO
//            // LƯU Ý: 'MAY001', 'NV002' phải ĐANG TỒN TẠI trong database của bạn
//            PhienSuDung p = new PhienSuDung();
//            p.setMamay("MAY001"); // Đảm bảo máy này chưa có ai ngồi
//            p.setManv("NV002");   // NV002 có trong filexuat.sql
//            p.setMakh("KH001");   // KH001 có trong filexuat.sql
//            p.setGiamoigio(5000.0);
//            p.setGiobatdau(LocalDateTime.now());
//            p.setLoaithanhtoan("TAIKHOAN");
//
//            // Thực hiện insert
//            boolean isInserted = dao.insert(p);
//
//            if (isInserted) {
//                maPhienVuaTao = p.getMaphien(); // Lấy mã phiên DAO vừa sinh ra (VD: PS011)
//                System.out.println("=> THÀNH CÔNG: Đã mở phiên " + maPhienVuaTao + " trên máy " + p.getMamay());
//            }
//        } catch (Exception e) {
//            System.err.println("=> THẤT BẠI (Có thể do Máy Bận hoặc Sai mã FK): " + e.getMessage());
//        }
//
//        // ========================================================
//        // 3. TEST KIỂM TRA MÁY & TÍNH TIỀN DỊCH VỤ
//        // ========================================================
//        if (!maPhienVuaTao.isEmpty()) {
//            try {
//                System.out.println("\n[3] Test Kiểm tra trạng thái & Lấy tiền dịch vụ:");
//
//                // Kiểm tra máy có đang báo bận không
//                PhienSuDung dangChay = dao.getPhienDangChay("MAY001");
//                if (dangChay != null) {
//                    System.out.println("- Máy MAY001 đang chạy phiên: " + dangChay.getMaphien());
//                }
//
//                // Test lấy tổng tiền dịch vụ (tính từ bảng sudungdichvu)
//                // Lưu ý: Phiên mới tạo chưa có dịch vụ nên kết quả sẽ là 0
//                double tienDV = dao.getTongTienDichVu(maPhienVuaTao);
//                System.out.println("- Tổng tiền dịch vụ hiện tại: " + tienDV);
//
//            } catch (Exception e) {
//                System.err.println("Lỗi phần 3: " + e.getMessage());
//            }
//        }
//
//        // ========================================================
//        // 4. TEST KẾT THÚC PHIÊN (THANH TOÁN)
//        // ========================================================
//        if (!maPhienVuaTao.isEmpty()) {
//            try {
//                System.out.println("\n[4] Test Kết Thúc Phiên (Thanh Toán):");
//
//                // Lấy thông tin phiên hiện tại để tính toán
//                PhienSuDung phienCanDong = dao.getById(maPhienVuaTao);
//
//                if (phienCanDong != null) {
//                    // --- GIẢ LẬP TÍNH TOÁN ---
//                    phienCanDong.setGioketthuc(LocalDateTime.now().plusHours(2)); // Giả vờ chơi 2 tiếng
//                    phienCanDong.setTonggio(2.0);
//                    phienCanDong.setTiengiochoi(10000.0); // 2h * 5k
//
//                    // Load lại tiền dịch vụ thực tế
//                    double tienDVthucTe = dao.getTongTienDichVu(maPhienVuaTao);
//                    phienCanDong.setTiendichvu(tienDVthucTe); // Đã sửa: dùng double
//
//                    // Tính tổng tiền (Cộng trực tiếp số double)
//                    double tongTien = phienCanDong.getTiengiochoi() + tienDVthucTe;
//                    phienCanDong.setTongtien(tongTien); // Đã sửa: dùng double
//
//                    // --- GỌI DAO ĐỂ UPDATE ---
//                    boolean isEnded = dao.ketThucPhien(phienCanDong);
//                    if (isEnded) {
//                        System.out.println("=> THÀNH CÔNG: Đã đóng phiên " + maPhienVuaTao);
//                        // In định dạng số nguyên cho đẹp
//                        System.out.printf("   Tiền giờ: %.0f \n", phienCanDong.getTiengiochoi());
//                        System.out.printf("   Tổng cộng: %.0f (Lưu ý: DB chỉ lưu Tiền Giờ)\n", phienCanDong.getTongtien());
//                    }
//                } else {
//                    System.out.println("=> Lỗi: Không tìm thấy phiên để đóng.");
//                }
//
//            } catch (Exception e) {
//                System.err.println("=> Lỗi kết thúc phiên: " + e.getMessage());
//            }
//        }
//
//        System.out.println("\n--- KẾT THÚC TEST ---");
//    }
//}