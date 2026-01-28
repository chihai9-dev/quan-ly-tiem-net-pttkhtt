package test.dao;

import dao.LichSuNapTienDAO;
import entity.LichSuNapTien;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Test class cho LichSuNapTienDAO
 * Test tất cả các chức năng CRUD và thống kê
 */
public class TestLichSuNapTienDAO {

    private static LichSuNapTienDAO dao = new LichSuNapTienDAO();

    public static void main(String[] args) {
        System.out.println("=== BẮT ĐẦU TEST LỊCH SỬ NẠP TIỀN DAO ===\n");

        // Test 1: Tạo mã tự động
        testTaoMaTuDong();

        // Test 2: Thêm lịch sử nạp tiền
        testThem();

        // Test 3: Tìm theo mã
        testTimTheoMa();

        // Test 4: Tìm theo khách hàng
        testTimTheoKhachHang();

        // Test 5: Tìm theo nhân viên
        testTimTheoNhanVien();

        // Test 6: Tìm theo phương thức
        testTimTheoPhuongThuc();

        // Test 7: Tìm theo khoảng thời gian
        testTimTheoKhoangThoiGian();

        // Test 8: Lấy tất cả
        testLayTatCa();

        // Test 9: Thống kê tổng tiền nạp theo khách hàng
        testTongTienNapTheoKhachHang();

        // Test 10: Thống kê tổng tiền nạp theo thời gian
        testTongTienNapTheoThoiGian();

        // Test 11: Thống kê tổng khuyến mãi
        testTongKhuyenMaiDaTang();

        // Test 12: Đếm số lượt nạp theo phương thức
        testDemSoLuotNapTheoPhuongThuc();

        // Test 13: Lấy giao dịch gần nhất
        testLayGiaoDichGanNhat();

        // Test 14: Top khách hàng nạp nhiều
        testTopKhachHangNapNhieu();

        System.out.println("\n=== KẾT THÚC TEST ===");
    }

    // ===== TEST 1: TẠO MÃ TỰ ĐỘNG =====
    private static void testTaoMaTuDong() {
        System.out.println("--- Test 1: Tạo mã nạp tiền tự động ---");
        try {
            String maMoi = dao.taoMaNapTuDong();
            System.out.println("✓ Mã nạp tiền mới: " + maMoi);
        } catch (Exception e) {
            System.out.println("✗ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    // ===== TEST 2: THÊM LỊCH SỬ NẠP TIỀN - ĐÃ SỬA =====
    private static void testThem() {
        System.out.println("--- Test 2: Thêm lịch sử nạp tiền ---");
        try {
            LichSuNapTien lichSu = new LichSuNapTien();
            lichSu.setMaNap(dao.taoMaNapTuDong());
            lichSu.setMaKH("KH001");
            lichSu.setMaNV("NV001");

            // SỬA: Đặt MaCTKM = null nếu không có chương trình khuyến mãi
            lichSu.setMaCTKM(null);  // ← ĐÃ SỬA: null thay vì "CTKM001"

            lichSu.setSoTienNap(100000);
            lichSu.setKhuyenMai(0);  // ← Không có khuyến mãi
            lichSu.setTongTienCong(100000);  // ← Chỉ có tiền nạp
            lichSu.setSoDuTruoc(50000);
            lichSu.setSoDuSau(150000);
            lichSu.setPhuongThuc("TIENMAT");
            lichSu.setMaGiaoDich("GD" + System.currentTimeMillis());
            lichSu.setNgayNap(LocalDateTime.now());

            boolean ketQua = dao.them(lichSu);

            if (ketQua) {
                System.out.println("✓ Thêm thành công!");
                System.out.println("  Mã nạp: " + lichSu.getMaNap());
                System.out.println("  Số tiền nạp: " + lichSu.getSoTienNapFormatted());
                System.out.println("  Khuyến mãi: " + lichSu.getKhuyenMaiFormatted());
                System.out.println("  Tổng tiền cộng: " + lichSu.getTongTienCongFormatted());
            } else {
                System.out.println("✗ Thêm thất bại!");
            }
        } catch (Exception e) {
            System.out.println("✗ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    // ===== TEST 3: TÌM THEO MÃ =====
    private static void testTimTheoMa() {
        System.out.println("--- Test 3: Tìm lịch sử theo mã ---");
        try {
            // Sửa: Tìm mã thực tế có trong DB
            String maNap = "NAP001";  // ← Dùng mã có trong DB
            LichSuNapTien lichSu = dao.timTheoMa(maNap);

            if (lichSu != null) {
                System.out.println("✓ Tìm thấy lịch sử!");
                inThongTinLichSu(lichSu);
            } else {
                System.out.println("✗ Không tìm thấy lịch sử với mã: " + maNap);
            }
        } catch (Exception e) {
            System.out.println("✗ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    // ===== TEST 4: TÌM THEO KHÁCH HÀNG =====
    private static void testTimTheoKhachHang() {
        System.out.println("--- Test 4: Tìm lịch sử theo khách hàng ---");
        try {
            String maKH = "KH001";
            List<LichSuNapTien> danhSach = dao.timTheoKhachHang(maKH);

            System.out.println("✓ Tìm thấy " + danhSach.size() + " lịch sử nạp tiền");

            if (!danhSach.isEmpty()) {
                System.out.println("  5 giao dịch gần nhất:");
                for (int i = 0; i < Math.min(5, danhSach.size()); i++) {
                    LichSuNapTien ls = danhSach.get(i);
                    System.out.println("  - " + ls.getMaNap() + ": " +
                            ls.getTongTienCongFormatted() + " (" +
                            ls.getNgayNapFormatted() + ")");
                }
            }
        } catch (Exception e) {
            System.out.println("✗ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    // ===== TEST 5: TÌM THEO NHÂN VIÊN =====
    private static void testTimTheoNhanVien() {
        System.out.println("--- Test 5: Tìm lịch sử theo nhân viên ---");
        try {
            String maNV = "NV001";
            List<LichSuNapTien> danhSach = dao.timTheoNhanVien(maNV);

            System.out.println("✓ Nhân viên " + maNV + " đã xử lý " +
                    danhSach.size() + " giao dịch nạp tiền");

            if (!danhSach.isEmpty()) {
                double tongTien = danhSach.stream()
                        .mapToDouble(LichSuNapTien::getTongTienCong)
                        .sum();
                System.out.println("  Tổng tiền đã xử lý: " +
                        String.format("%,.0f VND", tongTien));
            }
        } catch (Exception e) {
            System.out.println("✗ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    // ===== TEST 6: TÌM THEO PHƯƠNG THỨC =====
    private static void testTimTheoPhuongThuc() {
        System.out.println("--- Test 6: Tìm lịch sử theo phương thức thanh toán ---");
        try {
            String phuongThuc = "TIENMAT";
            List<LichSuNapTien> danhSach = dao.timTheoPhuongThuc(phuongThuc);

            System.out.println("✓ Tìm thấy " + danhSach.size() +
                    " giao dịch thanh toán bằng " + phuongThuc);

            if (!danhSach.isEmpty()) {
                double tongTien = danhSach.stream()
                        .mapToDouble(LichSuNapTien::getTongTienCong)
                        .sum();
                System.out.println("  Tổng tiền: " + String.format("%,.0f VND", tongTien));
            }
        } catch (Exception e) {
            System.out.println("✗ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    // ===== TEST 7: TÌM THEO KHOẢNG THỜI GIAN =====
    private static void testTimTheoKhoangThoiGian() {
        System.out.println("--- Test 7: Tìm lịch sử theo khoảng thời gian ---");
        try {
            LocalDateTime tuNgay = LocalDateTime.now().minusDays(7);
            LocalDateTime denNgay = LocalDateTime.now();

            List<LichSuNapTien> danhSach = dao.timTheoKhoangThoiGian(tuNgay, denNgay);

            System.out.println("✓ Tìm thấy " + danhSach.size() + " giao dịch trong 7 ngày qua");

            if (!danhSach.isEmpty()) {
                double tongTien = danhSach.stream()
                        .mapToDouble(LichSuNapTien::getTongTienCong)
                        .sum();
                double tongKM = danhSach.stream()
                        .mapToDouble(LichSuNapTien::getKhuyenMai)
                        .sum();
                System.out.println("  Tổng tiền nạp: " + String.format("%,.0f VND", tongTien));
                System.out.println("  Tổng khuyến mãi: " + String.format("%,.0f VND", tongKM));
            }
        } catch (Exception e) {
            System.out.println("✗ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    // ===== TEST 8: LẤY TẤT CẢ =====
    private static void testLayTatCa() {
        System.out.println("--- Test 8: Lấy tất cả lịch sử nạp tiền ---");
        try {
            List<LichSuNapTien> danhSach = dao.layTatCa();

            System.out.println("✓ Tổng số giao dịch: " + danhSach.size());

            if (!danhSach.isEmpty()) {
                System.out.println("  Giao dịch mới nhất:");
                inThongTinLichSu(danhSach.get(0));
            }
        } catch (Exception e) {
            System.out.println("✗ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    // ===== TEST 9: TỔNG TIỀN NẠP THEO KHÁCH HÀNG =====
    private static void testTongTienNapTheoKhachHang() {
        System.out.println("--- Test 9: Thống kê tổng tiền nạp theo khách hàng ---");
        try {
            String maKH = "KH001";
            double tongTien = dao.tongTienNapTheoKhachHang(maKH);

            System.out.println("✓ Khách hàng " + maKH + " đã nạp tổng: " +
                    String.format("%,.0f VND", tongTien));
        } catch (Exception e) {
            System.out.println("✗ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    // ===== TEST 10: TỔNG TIỀN NẠP THEO THỜI GIAN =====
    private static void testTongTienNapTheoThoiGian() {
        System.out.println("--- Test 10: Thống kê tổng tiền nạp theo thời gian ---");
        try {
            LocalDateTime tuNgay = LocalDateTime.now().minusMonths(1);
            LocalDateTime denNgay = LocalDateTime.now();

            double tongTien = dao.tongTienNapTheoThoiGian(tuNgay, denNgay);

            System.out.println("✓ Tổng tiền nạp trong 1 tháng qua: " +
                    String.format("%,.0f VND", tongTien));
        } catch (Exception e) {
            System.out.println("✗ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    // ===== TEST 11: TỔNG KHUYẾN MÃI ĐÃ TẶNG =====
    private static void testTongKhuyenMaiDaTang() {
        System.out.println("--- Test 11: Thống kê tổng khuyến mãi đã tặng ---");
        try {
            LocalDateTime tuNgay = LocalDateTime.now().minusMonths(1);
            LocalDateTime denNgay = LocalDateTime.now();

            double tongKM = dao.tongKhuyenMaiDaTang(tuNgay, denNgay);

            System.out.println("✓ Tổng khuyến mãi đã tặng trong 1 tháng: " +
                    String.format("%,.0f VND", tongKM));
        } catch (Exception e) {
            System.out.println("✗ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    // ===== TEST 12: ĐẾM SỐ LƯỢT NẠP THEO PHƯƠNG THỨC =====
    private static void testDemSoLuotNapTheoPhuongThuc() {
        System.out.println("--- Test 12: Đếm số lượt nạp theo phương thức ---");
        try {
            LocalDateTime tuNgay = LocalDateTime.now().minusMonths(1);
            LocalDateTime denNgay = LocalDateTime.now();

            String[] phuongThuc = {"TIENMAT", "MOMO", "CHUYENKHOAN"};

            for (String pt : phuongThuc) {
                int soLuot = dao.demSoLuotNapTheoPhuongThuc(pt, tuNgay, denNgay);
                System.out.println("  " + pt + ": " + soLuot + " lượt");
            }
        } catch (Exception e) {
            System.out.println("✗ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    // ===== TEST 13: LẤY GIAO DỊCH GẦN NHẤT =====
    private static void testLayGiaoDichGanNhat() {
        System.out.println("--- Test 13: Lấy giao dịch gần nhất của khách hàng ---");
        try {
            String maKH = "KH001";
            LichSuNapTien lichSu = dao.layGiaoDichGanNhat(maKH);

            if (lichSu != null) {
                System.out.println("✓ Giao dịch gần nhất của " + maKH + ":");
                inThongTinLichSu(lichSu);
            } else {
                System.out.println("✗ Không tìm thấy giao dịch nào");
            }
        } catch (Exception e) {
            System.out.println("✗ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    // ===== TEST 14: TOP KHÁCH HÀNG NẠP NHIỀU =====
    private static void testTopKhachHangNapNhieu() {
        System.out.println("--- Test 14: Top khách hàng nạp tiền nhiều nhất ---");
        try {
            LocalDateTime tuNgay = LocalDateTime.now().minusMonths(1);
            LocalDateTime denNgay = LocalDateTime.now();

            List<Object[]> top = dao.topKhachHangNapNhieu(10, tuNgay, denNgay);

            System.out.println("✓ Top 10 khách hàng nạp nhiều nhất:");
            for (int i = 0; i < top.size(); i++) {
                Object[] row = top.get(i);
                System.out.println("  " + (i+1) + ". " + row[0] + " - " +
                        row[1] + " " + row[2] + ": " +
                        String.format("%,.0f VND", (double)row[3]));
            }
        } catch (Exception e) {
            System.out.println("✗ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    // ===== PHƯƠNG THỨC HỖ TRỢ =====
    private static void inThongTinLichSu(LichSuNapTien ls) {
        System.out.println("  Mã nạp: " + ls.getMaNap());
        System.out.println("  Khách hàng: " + ls.getMaKH());
        System.out.println("  Nhân viên: " + ls.getMaNV());
        System.out.println("  Số tiền nạp: " + ls.getSoTienNapFormatted());
        System.out.println("  Khuyến mãi: " + ls.getKhuyenMaiFormatted());
        System.out.println("  Tổng tiền cộng: " + ls.getTongTienCongFormatted());
        System.out.println("  Số dư trước: " + ls.getSoDuTruocFormatted());
        System.out.println("  Số dư sau: " + ls.getSoDuSauFormatted());
        System.out.println("  Phương thức: " + ls.getTenPhuongThuc());
        System.out.println("  Ngày nạp: " + ls.getNgayNapFormatted());
    }
}