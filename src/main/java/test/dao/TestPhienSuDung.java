//package test.dao;
//
//
//
//import bus.PhienSuDungBUS;
//import entity.PhienSuDung;
//import entity.NhanVien;
//import untils.SessionManager;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//
///**
// * TestPhienSuDung - Class test và ví dụ sử dụng PhienSuDungBUS
// *
// * Minh họa các use case phổ biến trong hệ thống quản lý tiệm net
// *
// * @author QuanLyTiemNet Team
// * @version 1.0
// * @since 2026-02-03
// */
//public class TestPhienSuDung {
//
//    private static PhienSuDungBUS phienBUS = new PhienSuDungBUS();
//
//    public static void main(String[] args) {
//        System.out.println("╔══════════════════════════════════════════════════╗");
//        System.out.println("║   PHIENSUDUNG BUS - TEST & DEMO                  ║");
//        System.out.println("╚══════════════════════════════════════════════════╝\n");
//
//        // Setup - Giả lập đăng nhập nhân viên
//        setupSession();
//
//        // Test các chức năng
//        testMoPhienMoi();
//        testXemPhienDangChoi();
//        testTinhTienDuKien();
//        testKetThucPhien();
//        testLichSuPhien();
//        testThongKe();
//    }
//
//    /**
//     * Setup - Giả lập đăng nhập nhân viên
//     */
//    private static void setupSession() {
//        System.out.println(">>> SETUP: Đăng nhập nhân viên");
//        try {
//            // Tạo nhân viên giả lập
//            NhanVien nv = new NhanVien();
//            nv.setManv("NV001");
//            nv.setTen("Nguyễn Văn A");
//            nv.setChucvu("NHANVIEN");
//
//            SessionManager.setCurrentUser(nv);
//            SessionManager.printSessionInfo();
//            System.out.println("✓ Đăng nhập thành công\n");
//        } catch (Exception e) {
//            System.err.println("✗ Lỗi setup: " + e.getMessage() + "\n");
//        }
//    }
//
//    /**
//     * USE CASE 1: Nhân viên mở phiên chơi cho khách hàng
//     */
//    private static void testMoPhienMoi() {
//        System.out.println("╔══════════════════════════════════════════════════╗");
//        System.out.println("║   TEST 1: MỞ PHIÊN MỚI                           ║");
//        System.out.println("╚══════════════════════════════════════════════════╝");
//
//        try {
//            String maKH = "KH001";
//            String maMay = "MAY01";
//
//            System.out.println("Mở phiên mới:");
//            System.out.println("  - Khách hàng: " + maKH);
//            System.out.println("  - Máy: " + maMay);
//
//            PhienSuDung phien = phienBUS.moPhienMoi(maKH, maMay);
//
//            System.out.println("\n✓ Mở phiên thành công!");
//            System.out.println("  - Mã phiên: " + phien.getMaPhien());
//            System.out.println("  - Giờ bắt đầu: " + phien.getGioBatDau());
//            System.out.println("  - Giá mỗi giờ: " + phienBUS.formatTien(phien.getGiaMoiGio()));
//            System.out.println("  - Loại thanh toán: " + phien.getLoaiThanhToan());
//            System.out.println("  - Trạng thái: " + phien.getTrangThai());
//
//        } catch (Exception e) {
//            System.err.println("✗ Lỗi: " + e.getMessage());
//        }
//        System.out.println();
//    }
//
//    /**
//     * USE CASE 2: Xem phiên đang chơi của khách hàng
//     */
//    private static void testXemPhienDangChoi() {
//        System.out.println("╔══════════════════════════════════════════════════╗");
//        System.out.println("║   TEST 2: XEM PHIÊN ĐANG CHƠI                    ║");
//        System.out.println("╚══════════════════════════════════════════════════╝");
//
//        try {
//            String maKH = "KH001";
//
//            PhienSuDung phien = phienBUS.getPhienDangChoiByKhachHang(maKH);
//
//            if (phien != null) {
//                System.out.println("✓ Tìm thấy phiên đang chơi:");
//                System.out.println("  - Mã phiên: " + phien.getMaPhien());
//                System.out.println("  - Máy: " + phien.getMaMay());
//                System.out.println("  - Giờ bắt đầu: " + phien.getGioBatDau());
//
//                double thoiGianChoi = phienBUS.getThoiGianChoiHienTai(phien.getMaPhien());
//                System.out.println("  - Thời gian chơi: " + phienBUS.formatThoiGian(thoiGianChoi));
//            } else {
//                System.out.println("○ Khách hàng không có phiên đang chơi");
//            }
//
//        } catch (Exception e) {
//            System.err.println("✗ Lỗi: " + e.getMessage());
//        }
//        System.out.println();
//    }
//
//    /**
//     * USE CASE 3: Tính tiền dự kiến cho khách hàng
//     */
//    private static void testTinhTienDuKien() {
//        System.out.println("╔══════════════════════════════════════════════════╗");
//        System.out.println("║   TEST 3: TÍNH TIỀN DỰ KIẾN                      ║");
//        System.out.println("╚══════════════════════════════════════════════════╝");
//
//        try {
//            String maKH = "KH001";
//
//            PhienSuDung phien = phienBUS.getPhienDangChoiByKhachHang(maKH);
//
//            if (phien != null) {
//                double thoiGianChoi = phienBUS.getThoiGianChoiHienTai(phien.getMaPhien());
//                double tienDuKien = phienBUS.getTienDuKien(phien.getMaPhien());
//
//                System.out.println("Thông tin tính tiền:");
//                System.out.println("  - Thời gian chơi: " + phienBUS.formatThoiGian(thoiGianChoi));
//                System.out.println("  - Giá mỗi giờ: " + phienBUS.formatTien(phien.getGiaMoiGio()));
//                System.out.println("  - Tiền dự kiến: " + phienBUS.formatTien(tienDuKien));
//
//                if (phien.getMaGoiKH() != null) {
//                    System.out.println("  - Ghi chú: Khách hàng có gói, ưu tiên dùng giờ từ gói");
//                }
//
//            } else {
//                System.out.println("○ Không có phiên đang chơi");
//            }
//
//        } catch (Exception e) {
//            System.err.println("✗ Lỗi: " + e.getMessage());
//        }
//        System.out.println();
//    }
//
//    /**
//     * USE CASE 4: Kết thúc phiên chơi
//     */
//    private static void testKetThucPhien() {
//        System.out.println("╔══════════════════════════════════════════════════╗");
//        System.out.println("║   TEST 4: KẾT THÚC PHIÊN                         ║");
//        System.out.println("╚══════════════════════════════════════════════════╝");
//
//        try {
//            String maKH = "KH001";
//
//            // Lấy phiên đang chơi
//            PhienSuDung phienDangChoi = phienBUS.getPhienDangChoiByKhachHang(maKH);
//
//            if (phienDangChoi != null) {
//                String maPhien = phienDangChoi.getMaPhien();
//
//                System.out.println("Kết thúc phiên: " + maPhien);
//
//                // Kết thúc phiên
//                PhienSuDung phienKetThuc = phienBUS.ketThucPhien(maPhien);
//
//                System.out.println("\n✓ Kết thúc phiên thành công!");
//                System.out.println("  - Giờ bắt đầu: " + phienKetThuc.getGioBatDau());
//                System.out.println("  - Giờ kết thúc: " + phienKetThuc.getGioKetThuc());
//                System.out.println("  - Tổng giờ: " + phienBUS.formatThoiGian(phienKetThuc.getTongGio()));
//                System.out.println("  - Giờ từ gói: " + phienBUS.formatThoiGian(phienKetThuc.getGioSuDungTuGoi()));
//                System.out.println("  - Giờ từ tài khoản: " + phienBUS.formatThoiGian(phienKetThuc.getGioSuDungTuTaiKhoan()));
//                System.out.println("  - Tiền giờ chơi: " + phienBUS.formatTien(phienKetThuc.getTienGioChoi()));
//                System.out.println("  - Loại thanh toán: " + phienKetThuc.getLoaiThanhToan());
//
//            } else {
//                System.out.println("○ Không có phiên đang chơi để kết thúc");
//            }
//
//        } catch (Exception e) {
//            System.err.println("✗ Lỗi: " + e.getMessage());
//        }
//        System.out.println();
//    }
//
//    /**
//     * USE CASE 5: Xem lịch sử phiên của khách hàng
//     */
//    private static void testLichSuPhien() {
//        System.out.println("╔══════════════════════════════════════════════════╗");
//        System.out.println("║   TEST 5: LỊCH SỬ PHIÊN                          ║");
//        System.out.println("╚══════════════════════════════════════════════════╝");
//
//        try {
//            String maKH = "KH001";
//
//            ArrayList<PhienSuDung> lichSu = phienBUS.getLichSuPhienByKhachHang(maKH);
//
//            System.out.println("Lịch sử phiên của khách hàng " + maKH + ":");
//            System.out.println("Tổng số phiên: " + lichSu.size());
//
//            if (!lichSu.isEmpty()) {
//                System.out.println("\n5 phiên gần nhất:");
//                int count = 0;
//                for (PhienSuDung phien : lichSu) {
//                    if (count >= 5) break;
//
//                    System.out.println("\n  " + (count + 1) + ". " + phien.getMaPhien());
//                    System.out.println("     - Máy: " + phien.getMaMay());
//                    System.out.println("     - Thời gian: " + phien.getGioBatDau() + " - " +
//                            (phien.getGioKetThuc() != null ? phien.getGioKetThuc() : "Đang chơi"));
//                    System.out.println("     - Tổng giờ: " + phienBUS.formatThoiGian(phien.getTongGio()));
//                    System.out.println("     - Tiền: " + phienBUS.formatTien(phien.getTienGioChoi()));
//                    System.out.println("     - Trạng thái: " + phien.getTrangThai());
//
//                    count++;
//                }
//            }
//
//        } catch (Exception e) {
//            System.err.println("✗ Lỗi: " + e.getMessage());
//        }
//        System.out.println();
//    }
//
//    /**
//     * USE CASE 6: Thống kê (chỉ quản lý)
//     */
//    private static void testThongKe() {
//        System.out.println("╔══════════════════════════════════════════════════╗");
//        System.out.println("║   TEST 6: THỐNG KÊ                               ║");
//        System.out.println("╚══════════════════════════════════════════════════╝");
//
//        try {
//            // Chuyển sang tài khoản quản lý
//            NhanVien ql = new NhanVien();
//            ql.setManv("QL001");
//            ql.setTen("Quản lý");
//            ql.setChucvu("QUANLY");
//            SessionManager.setCurrentUser(ql);
//
//            // Thống kê tháng này
//            LocalDateTime tuNgay = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);
//            LocalDateTime denNgay = LocalDateTime.now();
//
//            double tongDoanhThu = phienBUS.getTongDoanhThuGioChoi(tuNgay, denNgay);
//
//            System.out.println("Thống kê tháng " + LocalDateTime.now().getMonthValue() + ":");
//            System.out.println("  - Từ ngày: " + tuNgay.toLocalDate());
//            System.out.println("  - Đến ngày: " + denNgay.toLocalDate());
//            System.out.println("  - Tổng doanh thu giờ chơi: " + phienBUS.formatTien(tongDoanhThu));
//
//            // Thống kê khách hàng
//            String maKH = "KH001";
//            double tongGioChoi = phienBUS.getTongGioChoiByKhachHang(maKH);
//            System.out.println("\nThống kê khách hàng " + maKH + ":");
//            System.out.println("  - Tổng giờ chơi: " + phienBUS.formatThoiGian(tongGioChoi));
//
//        } catch (Exception e) {
//            System.err.println("✗ Lỗi: " + e.getMessage());
//        }
//        System.out.println();
//    }
//}
