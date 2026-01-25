package test.dao;

import dao.PhienSuDungDAO;
import entity.PhienSuDung;
import java.time.LocalDateTime;
import java.util.List;

public class TestPhienSuDungDAO {
    public static void main(String[] args) {
        PhienSuDungDAO dao = new PhienSuDungDAO();
        System.out.println("--- BẮT ĐẦU TEST PHIENSUDUNG DAO ---");

        // 1. Test sinh mã tự động
        try {
            System.out.println("\n[1] Test Sinh Mã Phiên Tự Động:");
            String newId = dao.generateMaPhien();
            System.out.println("=> Mã phiên tiếp theo dự kiến: " + newId);
        } catch (Exception e) {
            System.err.println("Lỗi sinh mã: " + e.getMessage());
        }

        // 2. Test Lấy danh sách (Get All)
        try {
            System.out.println("\n[2] Test Lấy Danh Sách Phiên:");
            List<PhienSuDung> list = dao.getAll();
            System.out.println("=> Tổng số phiên hiện có: " + list.size());
            if (!list.isEmpty()) {
                PhienSuDung latest = list.get(0);
                System.out.println("   Phiên mới nhất: " + latest.getMaphien()
                        + " | Máy: " + latest.getMamay()
                        + " | Trạng thái: " + latest.getTrangthai());
            }
        } catch (Exception e) {
            System.err.println("Lỗi getAll: " + e.getMessage());
        }

        // 3. Test Mở Phiên Mới (INSERT)
        // Lưu ý: Cần đảm bảo mã máy "MAY001" (hoặc mã khác) tồn tại trong bảng MayTinh và chưa có người chơi.
        /*
        try {
            System.out.println("\n[3] Test Mở Phiên Mới (Insert):");
            PhienSuDung newPhien = new PhienSuDung();
            newPhien.setMamay("MAY001"); // Đảm bảo mã máy này có thật trong DB
            newPhien.setManv("NV001");   // Đảm bảo mã NV này có thật
            newPhien.setGiamoigio(5000.0);
            newPhien.setGiobatdau(LocalDateTime.now());

            // DAO sẽ tự set trạng thái là DANGCHOI, ở đây set thêm thông tin phụ
            newPhien.setLoaithanhtoan("TAIKHOAN");

            boolean isInserted = dao.insert(newPhien);
            if (isInserted) {
                // Lưu lại mã phiên vừa tạo để dùng cho các test sau
                System.out.println("=> Thành công: Đã mở phiên mới cho máy " + newPhien.getMamay()
                        + " với mã phiên: " + newPhien.getMaphien());
            }
        } catch (Exception e) {
             System.err.println("=> Lỗi Insert (Có thể do máy đang bận hoặc sai mã): " + e.getMessage());
        }
        */

        // 4. Test Kiểm Tra Phiên Đang Chạy
        try {
            System.out.println("\n[4] Test Kiểm Tra Phiên Đang Chạy:");
            String testMay = "MAY001"; // Thử kiểm tra máy vừa mở
            PhienSuDung p = dao.getPhienDangChay(testMay);
            if (p != null) {
                System.out.println("=> Máy " + testMay + " ĐANG CÓ NGƯỜI CHƠI. Mã phiên: " + p.getMaphien());
            } else {
                System.out.println("=> Máy " + testMay + " hiện đang TRỐNG.");
            }
        } catch (Exception e) {
            System.err.println("Lỗi getPhienDangChay: " + e.getMessage());
        }

        // 5. Test Cập Nhật Tiền Dịch Vụ
        /*
        try {
            System.out.println("\n[5] Test Cập Nhật Tiền Dịch Vụ:");
            String maPhienTest = "PS001"; // Thay bằng mã phiên đang chạy thực tế
            double tienDichVuMoi = 25000.0;

            boolean updated = dao.updateTienDichVu(maPhienTest, tienDichVuMoi);
            if (updated) {
                System.out.println("=> Thành công: Đã cập nhật tiền dịch vụ cho phiên " + maPhienTest);
            }
        } catch (Exception e) {
            System.err.println("Lỗi updateTienDichVu: " + e.getMessage());
        }
        */

        // 6. Test Kết Thúc Phiên
        /*
        try {
            System.out.println("\n[6] Test Kết Thúc Phiên:");
            String maPhienEnd = "PS001"; // Thay bằng mã phiên muốn kết thúc

            // Lấy thông tin phiên hiện tại trước khi đóng
            PhienSuDung phien = dao.getById(maPhienEnd);
            if (phien != null && "DANGCHOI".equals(phien.getTrangthai())) {
                phien.setGioketthuc(LocalDateTime.now());
                phien.setTonggio(2.5); // Giả lập chơi 2.5 tiếng
                phien.setTiengiochoi(12500.0);
                phien.setTongtien(12500.0 + phien.getTiendichvu());

                boolean ended = dao.ketThucPhien(phien);
                if (ended) {
                    System.out.println("=> Thành công: Đã kết thúc phiên " + maPhienEnd);
                }
            } else {
                 System.out.println("=> Không tìm thấy phiên hoặc phiên đã kết thúc/không tồn tại.");
            }
        } catch (Exception e) {
            System.err.println("Lỗi ketThucPhien: " + e.getMessage());
        }
        */

        System.out.println("\n--- KẾT THÚC TEST ---");
    }
}