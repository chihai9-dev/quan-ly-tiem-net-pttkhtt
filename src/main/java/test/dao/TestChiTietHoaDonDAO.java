package test.dao;

import dao.ChiTietHoaDonDAO;
import entity.ChiTietHoaDon;
import java.util.ArrayList;
import java.util.List;

public class TestChiTietHoaDonDAO {

    private static ChiTietHoaDonDAO dao = new ChiTietHoaDonDAO();

    public static void main(String[] args) {
        System.out.println("=== BẮT ĐẦU TEST CHI TIẾT HÓA ĐƠN DAO ===\n");

        // Test 1: Tạo mã tự động
        testTaoMaTuDong();

        // Test 2: Thêm chi tiết hóa đơn
        testThem();

        // Test 3: Thêm nhiều chi tiết cùng lúc
        testThemNhieu();

        // Test 4: Cập nhật chi tiết
        testCapNhat();

        // Test 5: Tìm theo mã
        testTimTheoMa();

        // Test 6: Lấy danh sách theo hóa đơn
        testTimTheoHoaDon();

        // Test 7: Lấy danh sách theo loại chi tiết
        testTimTheoLoaiChiTiet();

        // Test 8: Lấy tất cả
        testLayTatCa();

        // Test 9: Tính tổng tiền giờ chơi
        testTongTienGioChoi();

        // Test 10: Tính tổng tiền dịch vụ
        testTongTienDichVu();

        // Test 11: Tính tổng thành tiền
        testTongThanhTien();

        // Test 12: Đếm số chi tiết
        testDemChiTiet();

        // Test 13: Đếm số chi tiết theo loại
        testDemChiTietTheoLoai();

        // Test 14: Lấy chi tiết với thông tin hóa đơn
        testLayChiTietVoiHoaDon();

        // Test 15: Kiểm tra tồn tại
        testKiemTraTonTai();

        // Test 16: Tính thành tiền
        testTinhThanhTien();

        // Test 17: Xóa chi tiết
        testXoa();

        // Test 18: Xóa tất cả chi tiết của hóa đơn
        testXoaTheoHoaDon();

        System.out.println("\n=== KẾT THÚC TEST ===");
    }

    // ===== TEST 1: TẠO MÃ TỰ ĐỘNG =====
    private static void testTaoMaTuDong() {
        System.out.println("--- Test 1: Tạo mã chi tiết tự động ---");
        try {
            String maMoi = dao.taoMaChiTietTuDong();
            System.out.println("✓ Mã chi tiết mới: " + maMoi);
        } catch (Exception e) {
            System.out.println("✗ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    // ===== TEST 2: THÊM CHI TIẾT - ĐÃ SỬA =====
    private static void testThem() {
        System.out.println("--- Test 2: Thêm chi tiết hóa đơn ---");
        try {
            ChiTietHoaDon chiTiet = new ChiTietHoaDon();

            // SỬA: Tạo mã mới NGAY TRƯỚC KHI THÊM
            chiTiet.setMaCTHD(dao.taoMaChiTietTuDong());
            chiTiet.setMaHD("HD001");
            chiTiet.setLoaiChiTiet("GIOCHOI");
            chiTiet.setMoTa("Tiền giờ chơi");
            chiTiet.setSoLuong(2.5);
            chiTiet.setDonGia(5000);
            chiTiet.tinhThanhTien();

            boolean ketQua = dao.them(chiTiet);

            if (ketQua) {
                System.out.println("✓ Thêm chi tiết thành công!");
                System.out.println("  Mã chi tiết: " + chiTiet.getMaCTHD());
                System.out.println("  Loại: " + chiTiet.getTenLoaiChiTiet());
                System.out.println("  Mô tả: " + chiTiet.getMoTa());
                System.out.println("  Số lượng: " + chiTiet.getSoLuong());
                System.out.println("  Đơn giá: " + chiTiet.getDonGiaFormatted());
                System.out.println("  Thành tiền: " + chiTiet.getThanhTienFormatted());
            } else {
                System.out.println("✗ Thêm chi tiết thất bại!");
            }
        } catch (Exception e) {
            System.out.println("✗ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    // ===== TEST 3: THÊM NHIỀU CHI TIẾT - ĐÃ SỬA =====
    private static void testThemNhieu() {
        System.out.println("--- Test 3: Thêm nhiều chi tiết cùng lúc ---");
        try {
            List<ChiTietHoaDon> danhSach = new ArrayList<>();

            // SỬA: Tạo mã mới cho TỪNG chi tiết
            // Chi tiết 1: Giờ chơi
            ChiTietHoaDon ct1 = new ChiTietHoaDon();
            ct1.setMaCTHD(dao.taoMaChiTietTuDong());  // ← Tạo mã 1
            ct1.setMaHD("HD002");
            ct1.setLoaiChiTiet("GIOCHOI");
            ct1.setMoTa("Tiền giờ chơi");
            ct1.setSoLuong(3.0);
            ct1.setDonGia(6000);
            ct1.tinhThanhTien();
            danhSach.add(ct1);

            // Đợi 1ms để đảm bảo mã khác nhau
            Thread.sleep(10);

            // Chi tiết 2: Dịch vụ - Coca
            ChiTietHoaDon ct2 = new ChiTietHoaDon();
            ct2.setMaCTHD(dao.taoMaChiTietTuDong());  // ← Tạo mã 2
            ct2.setMaHD("HD002");
            ct2.setLoaiChiTiet("DICHVU");
            ct2.setMoTa("Coca Cola");
            ct2.setSoLuong(2.0);
            ct2.setDonGia(12000);
            ct2.tinhThanhTien();
            danhSach.add(ct2);

            // Đợi 1ms
            Thread.sleep(10);

            // Chi tiết 3: Dịch vụ - Mì
            ChiTietHoaDon ct3 = new ChiTietHoaDon();
            ct3.setMaCTHD(dao.taoMaChiTietTuDong());  // ← Tạo mã 3
            ct3.setMaHD("HD002");
            ct3.setLoaiChiTiet("DICHVU");
            ct3.setMoTa("Mì tôm trứng");
            ct3.setSoLuong(1.0);
            ct3.setDonGia(25000);
            ct3.tinhThanhTien();
            danhSach.add(ct3);

            boolean ketQua = dao.themNhieu(danhSach);

            if (ketQua) {
                System.out.println("✓ Thêm " + danhSach.size() + " chi tiết thành công!");
                double tongTien = danhSach.stream()
                        .mapToDouble(ChiTietHoaDon::getThanhTien)
                        .sum();
                System.out.println("  Tổng thành tiền: " + String.format("%,.0f VND", tongTien));
            } else {
                System.out.println("✗ Thêm nhiều chi tiết thất bại!");
            }
        } catch (Exception e) {
            System.out.println("✗ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    // ===== TEST 4: CẬP NHẬT CHI TIẾT - SỬA LẤY MÃ CÓ TRONG DB =====
    private static void testCapNhat() {
        System.out.println("--- Test 4: Cập nhật chi tiết hóa đơn ---");
        try {
            // SỬA: Lấy chi tiết có trong DB
            List<ChiTietHoaDon> danhSach = dao.layTatCa();

            if (!danhSach.isEmpty()) {
                ChiTietHoaDon chiTiet = danhSach.get(0);

                chiTiet.setSoLuong(3.0);
                chiTiet.tinhThanhTien();

                boolean ketQua = dao.capNhat(chiTiet);

                if (ketQua) {
                    System.out.println("✓ Cập nhật chi tiết thành công!");
                    System.out.println("  Số lượng mới: " + chiTiet.getSoLuong());
                    System.out.println("  Thành tiền mới: " + chiTiet.getThanhTienFormatted());
                } else {
                    System.out.println("✗ Cập nhật chi tiết thất bại!");
                }
            } else {
                System.out.println("✗ Không có chi tiết nào để test");
            }
        } catch (Exception e) {
            System.out.println("✗ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    // ===== TEST 5: TÌM THEO MÃ =====
    private static void testTimTheoMa() {
        System.out.println("--- Test 5: Tìm chi tiết theo mã ---");
        try {
            String maCTHD = "CTHD001";
            ChiTietHoaDon chiTiet = dao.timTheoMa(maCTHD);

            if (chiTiet != null) {
                System.out.println("✓ Tìm thấy chi tiết!");
                inThongTinChiTiet(chiTiet);
            } else {
                System.out.println("✗ Không tìm thấy chi tiết: " + maCTHD);
            }
        } catch (Exception e) {
            System.out.println("✗ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    // ===== TEST 6: LẤY DANH SÁCH THEO HÓA ĐƠN =====
    private static void testTimTheoHoaDon() {
        System.out.println("--- Test 6: Lấy chi tiết theo hóa đơn ---");
        try {
            String maHD = "HD001";
            List<ChiTietHoaDon> danhSach = dao.timTheoHoaDon(maHD);

            System.out.println("✓ Tìm thấy " + danhSach.size() + " chi tiết của hóa đơn " + maHD);

            if (!danhSach.isEmpty()) {
                System.out.println("\n  Chi tiết:");
                for (ChiTietHoaDon ct : danhSach) {
                    System.out.println("  - " + ct.getTenLoaiChiTiet() + ": " +
                            ct.getMoTa() + " x " + ct.getSoLuong() +
                            " = " + ct.getThanhTienFormatted());
                }

                double tongTien = danhSach.stream()
                        .mapToDouble(ChiTietHoaDon::getThanhTien)
                        .sum();
                System.out.println("\n  Tổng cộng: " + String.format("%,.0f VND", tongTien));
            }
        } catch (Exception e) {
            System.out.println("✗ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    // ===== CÁC TEST CÒN LẠI GIỮ NGUYÊN =====

    private static void testTimTheoLoaiChiTiet() {
        System.out.println("--- Test 7: Lấy chi tiết theo loại ---");
        try {
            String maHD = "HD001";

            List<ChiTietHoaDon> gioChoi = dao.timTheoLoaiChiTiet(maHD, "GIOCHOI");
            List<ChiTietHoaDon> dichVu = dao.timTheoLoaiChiTiet(maHD, "DICHVU");

            System.out.println("✓ Chi tiết hóa đơn " + maHD + ":");
            System.out.println("  Giờ chơi: " + gioChoi.size() + " mục");
            System.out.println("  Dịch vụ: " + dichVu.size() + " mục");
        } catch (Exception e) {
            System.out.println("✗ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    private static void testLayTatCa() {
        System.out.println("--- Test 8: Lấy tất cả chi tiết ---");
        try {
            List<ChiTietHoaDon> danhSach = dao.layTatCa();

            System.out.println("✓ Tổng số chi tiết: " + danhSach.size());

            if (!danhSach.isEmpty()) {
                long gioChoi = danhSach.stream()
                        .filter(ct -> "GIOCHOI".equals(ct.getLoaiChiTiet()))
                        .count();
                long dichVu = danhSach.stream()
                        .filter(ct -> "DICHVU".equals(ct.getLoaiChiTiet()))
                        .count();

                System.out.println("  Giờ chơi: " + gioChoi);
                System.out.println("  Dịch vụ: " + dichVu);
            }
        } catch (Exception e) {
            System.out.println("✗ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    private static void testTongTienGioChoi() {
        System.out.println("--- Test 9: Tính tổng tiền giờ chơi ---");
        try {
            String maHD = "HD001";
            double tongTien = dao.tongTienGioChoi(maHD);

            System.out.println("✓ Tổng tiền giờ chơi của hóa đơn " + maHD + ": " +
                    String.format("%,.0f VND", tongTien));
        } catch (Exception e) {
            System.out.println("✗ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    private static void testTongTienDichVu() {
        System.out.println("--- Test 10: Tính tổng tiền dịch vụ ---");
        try {
            String maHD = "HD001";
            double tongTien = dao.tongTienDichVu(maHD);

            System.out.println("✓ Tổng tiền dịch vụ của hóa đơn " + maHD + ": " +
                    String.format("%,.0f VND", tongTien));
        } catch (Exception e) {
            System.out.println("✗ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    private static void testTongThanhTien() {
        System.out.println("--- Test 11: Tính tổng thành tiền ---");
        try {
            String maHD = "HD001";
            double tongTien = dao.tongThanhTien(maHD);

            System.out.println("✓ Tổng thành tiền của hóa đơn " + maHD + ": " +
                    String.format("%,.0f VND", tongTien));
        } catch (Exception e) {
            System.out.println("✗ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    private static void testDemChiTiet() {
        System.out.println("--- Test 12: Đếm số chi tiết ---");
        try {
            String maHD = "HD001";
            int soLuong = dao.demChiTiet(maHD);

            System.out.println("✓ Hóa đơn " + maHD + " có " + soLuong + " chi tiết");
        } catch (Exception e) {
            System.out.println("✗ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    private static void testDemChiTietTheoLoai() {
        System.out.println("--- Test 13: Đếm chi tiết theo loại ---");
        try {
            String maHD = "HD001";

            int gioChoi = dao.demChiTietTheoLoai(maHD, "GIOCHOI");
            int dichVu = dao.demChiTietTheoLoai(maHD, "DICHVU");

            System.out.println("✓ Hóa đơn " + maHD + ":");
            System.out.println("  Giờ chơi: " + gioChoi + " mục");
            System.out.println("  Dịch vụ: " + dichVu + " mục");
        } catch (Exception e) {
            System.out.println("✗ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    private static void testLayChiTietVoiHoaDon() {
        System.out.println("--- Test 14: Lấy chi tiết kèm thông tin hóa đơn ---");
        try {
            String maHD = "HD001";
            List<Object[]> danhSach = dao.layChiTietVoiHoaDon(maHD);

            System.out.println("✓ Tìm thấy " + danhSach.size() + " chi tiết kèm thông tin");

            if (!danhSach.isEmpty()) {
                System.out.println("\n  Chi tiết:");
                for (Object[] row : danhSach) {
                    System.out.println("  - " + row[2] + ": " + row[3] +
                            " (" + String.format("%,.0f VND", (double)row[6]) + ")");
                }
            }
        } catch (Exception e) {
            System.out.println("✗ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    private static void testKiemTraTonTai() {
        System.out.println("--- Test 15: Kiểm tra tồn tại chi tiết ---");
        try {
            boolean tonTai = dao.kiemTraTonTai("CTHD001");

            if (tonTai) {
                System.out.println("✓ Chi tiết CTHD001 tồn tại");
            } else {
                System.out.println("✗ Chi tiết CTHD001 không tồn tại");
            }
        } catch (Exception e) {
            System.out.println("✗ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    private static void testTinhThanhTien() {
        System.out.println("--- Test 16: Test hàm tính thành tiền ---");
        try {
            double soLuong = 2.5;
            double donGia = 6000;
            double thanhTien = ChiTietHoaDonDAO.tinhThanhTien(soLuong, donGia);

            System.out.println("✓ Tính thành tiền:");
            System.out.println("  Số lượng: " + soLuong);
            System.out.println("  Đơn giá: " + String.format("%,.0f VND", donGia));
            System.out.println("  Thành tiền: " + String.format("%,.0f VND", thanhTien));
        } catch (Exception e) {
            System.out.println("✗ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    private static void testXoa() {
        System.out.println("--- Test 17: Xóa chi tiết ---");
        try {
            // Tạo chi tiết test để xóa
            ChiTietHoaDon ctTest = new ChiTietHoaDon();
            ctTest.setMaCTHD("CTHD_TEST_DELETE");
            ctTest.setMaHD("HD001");
            ctTest.setLoaiChiTiet("DICHVU");
            ctTest.setMoTa("Test delete");
            ctTest.setSoLuong(1.0);
            ctTest.setDonGia(1000);
            ctTest.tinhThanhTien();

            dao.them(ctTest);

            // Xóa chi tiết
            boolean ketQua = dao.xoa("CTHD_TEST_DELETE");

            if (ketQua) {
                System.out.println("✓ Xóa chi tiết thành công!");
            } else {
                System.out.println("✗ Xóa chi tiết thất bại!");
            }
        } catch (Exception e) {
            System.out.println("✗ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    // ===== TEST 18: XÓA THEO HÓA ĐƠN - ĐÃ SỬA =====
    private static void testXoaTheoHoaDon() {
        System.out.println("--- Test 18: Xóa tất cả chi tiết của hóa đơn ---");
        try {
            // SỬA: Dùng mã hóa đơn CÓ TRONG DB
            String maHDTest = "HD002";  // ← Dùng HD có sẵn

            // Xóa tất cả chi tiết
            boolean ketQua = dao.xoaTheoHoaDon(maHDTest);

            if (ketQua) {
                System.out.println("✓ Xóa tất cả chi tiết của hóa đơn thành công!");
            } else {
                System.out.println("✗ Xóa chi tiết thất bại!");
            }
        } catch (Exception e) {
            System.out.println("✗ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    // ===== PHƯƠNG THỨC HỖ TRỢ =====
    private static void inThongTinChiTiet(ChiTietHoaDon ct) {
        System.out.println("  Mã chi tiết: " + ct.getMaCTHD());
        System.out.println("  Mã hóa đơn: " + ct.getMaHD());
        System.out.println("  Loại: " + ct.getTenLoaiChiTiet());
        System.out.println("  Mô tả: " + ct.getMoTa());
        System.out.println("  Số lượng: " + ct.getSoLuong());
        System.out.println("  Đơn giá: " + ct.getDonGiaFormatted());
        System.out.println("  Thành tiền: " + ct.getThanhTienFormatted());
    }
}