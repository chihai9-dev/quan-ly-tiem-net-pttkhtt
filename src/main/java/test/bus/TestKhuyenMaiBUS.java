package test.bus;

import entity.NhanVien;
import entity.KhachHang;
import dao.NhanVienDAO;
import bus.KhuyenMaiBUS;
import untils.SessionManager;

public class TestKhuyenMaiBUS {
    public static void main(String[] args) {
        KhuyenMaiBUS khuyenMaiBUS = new KhuyenMaiBUS();
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

        // Test 1: Thêm khuyến mãi phần trăm
//        try {
//            ChuongTrinhKhuyenMai km = new ChuongTrinhKhuyenMai();
//            km.setTenCT("Khuyến mãi tháng 2");
//            km.setLoaiKM("PHANTRAM");
//            km.setGiaTriKM(10);
//            km.setDieuKienToiThieu(200000);
//            km.setNgayBatDau(LocalDateTime.now());
//            km.setNgayKetThuc(LocalDateTime.now().plusDays(30));
//
//            boolean kq = khuyenMaiBUS.themKhuyenMai(km);
//            System.out.println("✓ Thêm KM: " + (kq ? km.getMaCTKM() : "Thất bại"));
//        } catch (Exception e) {
//            System.err.println("Có lỗi: " + e.getMessage());
//        }

        // Test 2: Thêm khuyến mãi số tiền
//        try {
//            ChuongTrinhKhuyenMai km = new ChuongTrinhKhuyenMai();
//            km.setTenCT("Tặng 50k");
//            km.setLoaiKM("SOTIEN");
//            km.setGiaTriKM(50000);
//            km.setDieuKienToiThieu(500000);
//            km.setNgayBatDau(LocalDateTime.now());
//            km.setNgayKetThuc(LocalDateTime.now().plusMonths(1));
//
//            boolean kq = khuyenMaiBUS.themChuongTrinh(km);
//            System.out.println("✓ Thêm KM: " + km.getMaCTKM());
//        } catch (Exception e) {
//            System.err.println("Có lỗi: " + e.getMessage());
//        }

        // Test 3: Lấy tất cả khuyến mãi
//        try {
//            List<ChuongTrinhKhuyenMai> dsKM = khuyenMaiBUS.getAllKhuyenMai();
//            System.out.println("✓ Tổng số KM: " + dsKM.size());
//        } catch (Exception e) {
//            System.err.println("Có lỗi: " + e.getMessage());
//        }

        // Test 4: Lấy khuyến mãi còn hiệu lực
//        try {
//            List<ChuongTrinhKhuyenMai> dsKM = khuyenMaiBUS.getKhuyenMaiConHieuLuc();
//            System.out.println("✓ KM còn hiệu lực: " + dsKM.size());
//            for (ChuongTrinhKhuyenMai km : dsKM) {
//                System.out.println("- " + km.getMaCTKM() + ": " + km.getTenCT());
//            }
//        } catch (Exception e) {
//            System.err.println("Có lỗi: " + e.getMessage());
//        }

        // Test 5: Sửa khuyến mãi
//        try {
//            ChuongTrinhKhuyenMai km = khuyenMaiBUS.timTheoMa("KM001");
//            if (km != null) {
//                km.setGiaTriKM(15);
//                boolean kq = khuyenMaiBUS.suaKhuyenMai(km);
//                System.out.println("✓ Sửa KM: " + (kq ? "Thành công" : "Thất bại"));
//            }
//        } catch (Exception e) {
//            System.err.println("Có lỗi: " + e.getMessage());
//        }

        // Test 6: Xóa khuyến mãi
//        try {
//            boolean kq = khuyenMaiBUS.xoaKhuyenMai("KM002");
//            System.out.println("✓ Xóa KM: " + (kq ? "Thành công" : "Thất bại"));
//        } catch (Exception e) {
//            System.err.println("Có lỗi: " + e.getMessage());
//        }

        // Test 7: Tìm chương trình phù hợp
//        try {
//            List<ChuongTrinhKhuyenMai> dsKM = khuyenMaiBUS.timChuongTrinhPhuHop(500000);
//            System.out.println("✓ Số chương trình phù hợp: " + dsKM.size());
//            for (ChuongTrinhKhuyenMai km : dsKM) {
//                System.out.println("- " + km.getTenCT());
//            }
//        } catch (Exception e) {
//            System.err.println("Có lỗi: " + e.getMessage());
//        }

        // Test 8: Tìm chương trình tốt nhất
//        try {
//            ChuongTrinhKhuyenMai km = khuyenMaiBUS.timChuongTrinhTotNhat(1000000);
//            if (km != null) {
//                System.out.println("✓ Chương trình tốt nhất: " + km.getTenCT());
//            }
//        } catch (Exception e) {
//            System.err.println("Có lỗi: " + e.getMessage());
//        }

        // Test 9: Kiểm tra điều kiện
//        try {
//            boolean kq = khuyenMaiBUS.kiemTraDieuKien("KM001", 300000);
//            System.out.println("✓ Đủ điều kiện: " + (kq ? "Có" : "Không"));
//        } catch (Exception e) {
//            System.err.println("Có lỗi: " + e.getMessage());
//        }

        // Test 10: Kiểm tra còn hiệu lực
//        try {
//            boolean kq = khuyenMaiBUS.kiemTraConHieuLuc("KM001");
//            System.out.println("✓ Còn hiệu lực: " + (kq ? "Có" : "Không"));
//        } catch (Exception e) {
//            System.err.println("Có lỗi: " + e.getMessage());
//        }

        // Test 11: Tính giá trị khuyến mãi
//        try {
//            double giaTriKM = khuyenMaiBUS.tinhGiaTriKhuyenMai("KM001", 500000);
//            System.out.println("✓ Giá trị KM: " + giaTriKM);
//        } catch (Exception e) {
//            System.err.println("Có lỗi: " + e.getMessage());
//        }

        // Test 12: Bật/tắt chương trình
//        try {
//            boolean tat = khuyenMaiBUS.tatChuongTrinh("KM001");
//            System.out.println("✓ Tắt KM: " + (tat ? "Thành công" : "Thất bại"));
//
//            boolean bat = khuyenMaiBUS.batChuongTrinh("KM001");
//            System.out.println("✓ Bật KM: " + (bat ? "Thành công" : "Thất bại"));
//        } catch (Exception e) {
//            System.err.println("Có lỗi: " + e.getMessage());
//        }

        // Test 13: Thống kê chương trình
//        try {
//            LocalDateTime tuNgay = LocalDateTime.now().minusDays(30);
//            LocalDateTime denNgay = LocalDateTime.now();
//            List<Object[]> thongKe = khuyenMaiBUS.thongKeChuongTrinh(tuNgay, denNgay);
//            System.out.println("✓ Thống kê:");
//            for (Object[] row : thongKe) {
//                System.out.println("- " + row[0] + ": " + row[3] + " lượt");
//            }
//        } catch (Exception e) {
//            System.err.println("Có lỗi: " + e.getMessage());
//        }

        // Test 14: Đếm số lượt sử dụng
//        try {
//            int soLuot = khuyenMaiBUS.demSoLuotSuDung("KM001");
//            System.out.println("✓ Số lượt sử dụng: " + soLuot);
//        } catch (Exception e) {
//            System.err.println("Có lỗi: " + e.getMessage());
//        }

        // Test 15: Validate tên rỗng
//        try {
//            ChuongTrinhKhuyenMai km = new ChuongTrinhKhuyenMai();
//            km.setTenCT("");
//            km.setLoaiKM("PHANTRAM");
//            khuyenMaiBUS.themChuongTrinh(km);
//            System.err.println("✗ Không bắt được lỗi!");
//        } catch (Exception e) {
//            System.out.println("✓ Bắt lỗi: " + e.getMessage());
//        }

        // Test 16: Validate phần trăm > 100
//        try {
//            ChuongTrinhKhuyenMai km = new ChuongTrinhKhuyenMai();
//            km.setTenCT("Test");
//            km.setLoaiKM("PHANTRAM");
//            km.setGiaTriKM(150);
//            khuyenMaiBUS.themChuongTrinh(km);
//            System.err.println("✗ Không bắt được lỗi!");
//        } catch (Exception e) {
//            System.out.println("✓ Bắt lỗi: " + e.getMessage());
//        }
    }
}