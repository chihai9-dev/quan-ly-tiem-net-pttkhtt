package test.bus;

import entity.NhanVien;
import entity.KhachHang;
import dao.NhanVienDAO;
import dao.LichSuNapTienDAO;
import bus.NapTienBUS;
import untils.SessionManager;

public class TestNapTienBUS {
    public static void main(String[] args) {
        NapTienBUS napTienBUS = new NapTienBUS();
        LichSuNapTienDAO napTienDAO = new LichSuNapTienDAO();
        NhanVienDAO nvDAO = new NhanVienDAO();
        KhachHangDAO khDAO = new KhachHangDAO();

        // tạo instance khách hàng
        KhachHang kh = new KhachHang();
        kh = khDAO.login("hoangnam", "123456");

        // tạo instance quản lí
        NhanVien nv = new NhanVien();
        nv = nvDAO.login("admin", "password_hash_1");

        // tạo instance nhân viên
        NhanVien nv1 = new NhanVien();
        nv1 = nvDAO.login("kythuat01", "password_hash_3");

        // bắt đầu đăng nhập
        SessionManager.setCurrentUser(nv);

        // ===== TEST NẠP TIỀN =====

        // Test 1: Nạp tiền thành công không có khuyến mãi
//        try {
//            String maKH = "KH001";
//            double soTienNap = 100000;
//            String maNV = nv.getMaNV();
//            String phuongThuc = "TIENMAT";
//
//            LichSuNapTien lichSu = napTienBUS.napTien(maKH, soTienNap, null, maNV, phuongThuc, null);
//
//            System.out.println("✓ Nạp tiền thành công!");
//            System.out.println("Mã nạp: " + lichSu.getMaNap());
//            System.out.println("Số tiền nạp: " + String.format("%,.0f VND", lichSu.getSoTienNap()));
//            System.out.println("Khuyến mãi: " + String.format("%,.0f VND", lichSu.getKhuyenMai()));
//            System.out.println("Tổng tiền cộng: " + String.format("%,.0f VND", lichSu.getTongTienCong()));
//        } catch (Exception e) {
//            System.err.println("Có lỗi: " + e.getMessage());
//        }

        // Test 2: Nạp tiền có khuyến mãi tự động
//        try {
//            String maKH = "KH001";
//            double soTienNap = 500000; // Nạp 500k để đủ điều kiện khuyến mãi
//            String maNV = nv.getMaNV();
//            String phuongThuc = "MOMO";
//            String maGiaoDich = "MOMO" + System.currentTimeMillis();
//
//            // Tìm chương trình khuyến mãi tốt nhất trước
//            ChuongTrinhKhuyenMai chuongTrinh = napTienBUS.layChuongTrinhTotNhat(soTienNap);
//            if (chuongTrinh != null) {
//                System.out.println("Chương trình khuyến mãi: " + chuongTrinh.getTenCT());
//                System.out.println("Loại KM: " + chuongTrinh.getLoaiKM());
//            }
//
//            LichSuNapTien lichSu = napTienBUS.napTien(maKH, soTienNap, null, maNV, phuongThuc, maGiaoDich);
//
//            System.out.println("\n✓ Nạp tiền thành công!");
//            System.out.println("Số tiền nạp: " + String.format("%,.0f VND", lichSu.getSoTienNap()));
//            System.out.println("Khuyến mãi: " + String.format("%,.0f VND", lichSu.getKhuyenMai()));
//            System.out.println("Tổng tiền cộng: " + String.format("%,.0f VND", lichSu.getTongTienCong()));
//        } catch (Exception e) {
//            System.err.println("Có lỗi: " + e.getMessage());
//        }

        // Test 3: Nạp tiền với mã khuyến mãi cụ thể
//        try {
//            String maKH = "KH002";
//            double soTienNap = 1000000;
//            String maCTKM = "KM001"; // Giả sử có chương trình KM001
//            String maNV = nv.getMaNV();
//            String phuongThuc = "CHUYENKHOAN";
//
//            // Tính trước khuyến mãi
//            double khuyenMai = napTienBUS.tinhTruocKhuyenMai(soTienNap, maCTKM);
//            System.out.println("Khuyến mãi dự kiến: " + String.format("%,.0f VND", khuyenMai));
//
//            LichSuNapTien lichSu = napTienBUS.napTien(maKH, soTienNap, maCTKM, maNV, phuongThuc, "CK123456");
//
//            System.out.println("\n✓ Nạp tiền thành công!");
//            System.out.println("Mã CTKM áp dụng: " + lichSu.getMaCTKM());
//            System.out.println("Khuyến mãi thực tế: " + String.format("%,.0f VND", lichSu.getKhuyenMai()));
//        } catch (Exception e) {
//            System.err.println("Có lỗi: " + e.getMessage());
//        }

        // ===== TEST VALIDATE =====

        // Test 4: Validate số tiền nạp <= 0
//        try {
//            napTienBUS.napTien("KH001", 0, null, nv.getMaNV(), "TIENMAT", null);
//            System.err.println("✗ Không bắt được lỗi!");
//        } catch (Exception e) {
//            System.out.println("✓ Bắt lỗi thành công: " + e.getMessage());
//        }

        // Test 5: Validate phương thức thanh toán không hợp lệ
//        try {
//            napTienBUS.napTien("KH001", 100000, null, nv.getMaNV(), "BITCOIN", null);
//            System.err.println("✗ Không bắt được lỗi!");
//        } catch (Exception e) {
//            System.out.println("✓ Bắt lỗi thành công: " + e.getMessage());
//        }

        // ===== TEST XEM LỊCH SỬ =====

        // Test 6: Xem lịch sử nạp tiền của khách hàng
//        try {
//            String maKH = "KH001";
//            List<LichSuNapTien> lichSu = napTienBUS.layLichSuNap(maKH);
//
//            System.out.println("✓ Lấy lịch sử thành công!");
//            System.out.println("Tổng số giao dịch: " + lichSu.size());
//
//            if (lichSu.size() > 0) {
//                System.out.println("\n5 giao dịch gần nhất:");
//                for (int i = 0; i < Math.min(5, lichSu.size()); i++) {
//                    LichSuNapTien ls = lichSu.get(i);
//                    System.out.println((i+1) + ". " + ls.getMaNap() + " - " +
//                                     ls.getSoTienNapFormatted() + " - " +
//                                     ls.getNgayNapFormatted());
//                }
//            }
//        } catch (Exception e) {
//            System.err.println("Có lỗi: " + e.getMessage());
//        }

        // Test 7: Xem giao dịch gần nhất
//        try {
//            String maKH = "KH001";
//            LichSuNapTien lichSu = napTienBUS.layGiaoDichGanNhat(maKH);
//
//            if (lichSu != null) {
//                System.out.println("✓ Giao dịch gần nhất:");
//                System.out.println("Mã nạp: " + lichSu.getMaNap());
//                System.out.println("Ngày nạp: " + lichSu.getNgayNapFormatted());
//                System.out.println("Số tiền: " + lichSu.getSoTienNapFormatted());
//                System.out.println("Khuyến mãi: " + lichSu.getKhuyenMaiFormatted());
//            }
//        } catch (Exception e) {
//            System.err.println("Có lỗi: " + e.getMessage());
//        }

        // ===== TEST THỐNG KÊ =====

        // Test 8: Thống kê tổng tiền nạp theo thời gian
//        try {
//            LocalDateTime tuNgay = LocalDateTime.now().minusDays(30);
//            LocalDateTime denNgay = LocalDateTime.now();
//
//            double tongTienNap = napTienBUS.thongKeTongTienNap(tuNgay, denNgay);
//            double tongKhuyenMai = napTienBUS.thongKeTongKhuyenMai(tuNgay, denNgay);
//
//            System.out.println("✓ Thống kê 30 ngày gần nhất:");
//            System.out.println("Tổng tiền nạp: " + String.format("%,.0f VND", tongTienNap));
//            System.out.println("Tổng khuyến mãi: " + String.format("%,.0f VND", tongKhuyenMai));
//        } catch (Exception e) {
//            System.err.println("Có lỗi: " + e.getMessage());
//        }

        // Test 9: Top khách hàng nạp nhiều
//        try {
//            LocalDateTime tuNgay = LocalDateTime.now().minusDays(30);
//            LocalDateTime denNgay = LocalDateTime.now();
//
//            List<Object[]> topKH = napTienBUS.topKhachHangNapNhieu(5, tuNgay, denNgay);
//
//            System.out.println("✓ Top 5 khách hàng nạp nhiều nhất:");
//            for (int i = 0; i < topKH.size(); i++) {
//                Object[] row = topKH.get(i);
//                System.out.println((i+1) + ". " + row[1] + " " + row[2] +
//                                 " - " + String.format("%,.0f VND", (Double)row[3]));
//            }
//        } catch (Exception e) {
//            System.err.println("Có lỗi: " + e.getMessage());
//        }

        // Test 10: Thống kê theo phương thức thanh toán
//        try {
//            LocalDateTime tuNgay = LocalDateTime.now().minusDays(30);
//            LocalDateTime denNgay = LocalDateTime.now();
//
//            String[] phuongThuc = {"TIENMAT", "MOMO", "CHUYENKHOAN", "VNPAY", "THE"};
//
//            System.out.println("✓ Số lượt nạp theo phương thức (30 ngày):");
//            for (String pt : phuongThuc) {
//                int soLuot = napTienBUS.demSoLuotNapTheoPhuongThuc(pt, tuNgay, denNgay);
//                System.out.println(pt + ": " + soLuot + " lượt");
//            }
//        } catch (Exception e) {
//            System.err.println("Có lỗi: " + e.getMessage());
//        }

        // ===== TEST KHUYẾN MÃI =====

        // Test 11: Lấy danh sách chương trình phù hợp
//        try {
//            double soTienNap = 500000;
//            List<ChuongTrinhKhuyenMai> danhSach = napTienBUS.layChuongTrinhPhuHop(soTienNap);
//
//            System.out.println("✓ Các chương trình phù hợp với số tiền " +
//                             String.format("%,.0f VND", soTienNap) + ":");
//
//            for (ChuongTrinhKhuyenMai ct : danhSach) {
//                System.out.println("- " + ct.getTenCT() + " (" + ct.getLoaiKM() + ")");
//            }
//        } catch (Exception e) {
//            System.err.println("Có lỗi: " + e.getMessage());
//        }

        // Test 12: Tính tiền khuyến mãi (method mới)
//        try {
//            double soTien = 500000;
//            String maCTKM = "KM001";
//
//            double khuyenMai = napTienBUS.tinhKhuyenMai(soTien, maCTKM);
//            System.out.println("✓ Khuyến mãi nhận được: " + String.format("%,.0f VND", khuyenMai));
//        } catch (Exception e) {
//            System.err.println("Có lỗi: " + e.getMessage());
//        }

        // Test 13: Lấy lịch sử nạp tiền (method mới)
//        try {
//            String maKH = "KH001";
//            List<LichSuNapTien> lichSu = napTienBUS.getLichSuNapTien(maKH);
//
//            System.out.println("✓ Số giao dịch: " + lichSu.size());
//        } catch (Exception e) {
//            System.err.println("Có lỗi: " + e.getMessage());
//        }
    }
}