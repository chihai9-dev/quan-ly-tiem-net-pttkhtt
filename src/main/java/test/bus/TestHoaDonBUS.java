package test.bus;

import entity.NhanVien;
import entity.KhachHang;
import dao.NhanVienDAO;
import bus.HoaDonBUS;
import untils.SessionManager;

public class TestHoaDonBUS {
    public static void main(String[] args) {
        HoaDonBUS hoaDonBUS = new HoaDonBUS();
        NhanVienDAO nvDAO = new NhanVienDAO();
        KhachHangDAO khDAO = new KhachHangDAO();

        // tạo instance khách hàng
        KhachHang kh = khDAO.login("hoangnam", "123456");

        // tạo instance quản lí
        NhanVien nv = nvDAO.login("admin", "password_hash_1");

        // tạo instance nhân viên
        NhanVien nv1 = nvDAO.login("kythuat01", "password_hash_3");

        // bắt đầu đăng nhập
        SessionManager.setCurrentUser(nv);

        // Test 1: Tạo hóa đơn tự động
//        try {
//            HoaDon hoaDon = hoaDonBUS.taoHoaDonTuDong("PHIEN001", "NV001");
//            System.out.println("✓ Tạo hóa đơn: " + hoaDon.getMaHD());
//            System.out.println("Tổng tiền: " + hoaDon.getTongTien());
//        } catch (Exception e) {
//            System.err.println("Có lỗi: " + e.getMessage());
//        }

        // Test 2: Xem hóa đơn
//        try {
//            HoaDon hoaDon = hoaDonBUS.xemHoaDon("HD001");
//            System.out.println("✓ Hóa đơn: " + hoaDon.getMaHD());
//            System.out.println("Khách hàng: " + hoaDon.getMaKH());
//            System.out.println("Tổng tiền: " + hoaDon.getTongTien());
//        } catch (Exception e) {
//            System.err.println("Có lỗi: " + e.getMessage());
//        }

        // Test 3: Xem chi tiết hóa đơn
//        try {
//            List<ChiTietHoaDon> chiTiet = hoaDonBUS.xemChiTietHoaDon("HD001");
//            System.out.println("✓ Số chi tiết: " + chiTiet.size());
//            for (ChiTietHoaDon ct : chiTiet) {
//                System.out.println("- " + ct.getMoTa() + ": " + ct.getThanhTien());
//            }
//        } catch (Exception e) {
//            System.err.println("Có lỗi: " + e.getMessage());
//        }

        // Test 4: Thanh toán hóa đơn
//        try {
//            boolean kq = hoaDonBUS.thanhToanHoaDon("HD001", "TIENMAT");
//            System.out.println("✓ Thanh toán: " + (kq ? "Thành công" : "Thất bại"));
//        } catch (Exception e) {
//            System.err.println("Có lỗi: " + e.getMessage());
//        }

        // Test 5: Lấy tất cả hóa đơn
//        try {
//            List<HoaDon> dsHoaDon = hoaDonBUS.getAllHoaDon();
//            System.out.println("✓ Tổng số hóa đơn: " + dsHoaDon.size());
//        } catch (Exception e) {
//            System.err.println("Có lỗi: " + e.getMessage());
//        }

        // Test 6: Lấy hóa đơn theo phiên
//        try {
//            HoaDon hoaDon = hoaDonBUS.getHoaDonByPhien("PHIEN001");
//            System.out.println("✓ Hóa đơn của phiên: " + hoaDon.getMaHD());
//        } catch (Exception e) {
//            System.err.println("Có lỗi: " + e.getMessage());
//        }

        // Test 7: Lấy hóa đơn theo khoảng thời gian
//        try {
//            LocalDateTime tuNgay = LocalDateTime.now().minusDays(30);
//            LocalDateTime denNgay = LocalDateTime.now();
//            List<HoaDon> dsHoaDon = hoaDonBUS.getHoaDonsByDateRange(tuNgay, denNgay);
//            System.out.println("✓ Số hóa đơn 30 ngày: " + dsHoaDon.size());
//        } catch (Exception e) {
//            System.err.println("Có lỗi: " + e.getMessage());
//        }

        // Test 8: Xuất hóa đơn PDF
//        try {
//            String pdfPath = hoaDonBUS.xuatHoaDonPDF("HD001");
//            System.out.println("✓ Xuất PDF: " + pdfPath);
//        } catch (Exception e) {
//            System.err.println("Có lỗi: " + e.getMessage());
//        }

        // Test 9: Thống kê doanh thu
//        try {
//            LocalDateTime tuNgay = LocalDateTime.now().minusDays(30);
//            LocalDateTime denNgay = LocalDateTime.now();
//            double doanhThu = hoaDonBUS.thongKeDoanhThu(tuNgay, denNgay);
//            System.out.println("✓ Doanh thu 30 ngày: " + doanhThu);
//        } catch (Exception e) {
//            System.err.println("Có lỗi: " + e.getMessage());
//        }

        // Test 10: Top khách hàng chi tiêu
//        try {
//            LocalDateTime tuNgay = LocalDateTime.now().minusDays(30);
//            LocalDateTime denNgay = LocalDateTime.now();
//            List<Object[]> top = hoaDonBUS.topKhachHangChiTieu(5, tuNgay, denNgay);
//            System.out.println("✓ Top 5 khách hàng:");
//            for (Object[] row : top) {
//                System.out.println("- " + row[1] + " " + row[2] + ": " + row[3]);
//            }
//        } catch (Exception e) {
//            System.err.println("Có lỗi: " + e.getMessage());
//        }

        // Test 11: Validate thanh toán hóa đơn đã thanh toán
//        try {
//            hoaDonBUS.thanhToanHoaDon("HD001", "TIENMAT");
//            System.err.println("✗ Không bắt được lỗi!");
//        } catch (Exception e) {
//            System.out.println("✓ Bắt lỗi: " + e.getMessage());
//        }

        // Test 12: Validate tạo hóa đơn cho phiên chưa kết thúc
//        try {
//            hoaDonBUS.taoHoaDonTuDong("PHIEN999", "NV001");
//            System.err.println("✗ Không bắt được lỗi!");
//        } catch (Exception e) {
//            System.out.println("✓ Bắt lỗi: " + e.getMessage());
//        }
    }
}