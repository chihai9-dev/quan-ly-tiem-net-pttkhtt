package test.dao;

import dao.ChuongTrinhKhuyenMaiDAO;
import entity.ChuongTrinhKhuyenMai;
import java.time.LocalDateTime;
import java.util.List;

public class TestChuongTrinhKhuyenMaiDAO {

    private static ChuongTrinhKhuyenMaiDAO dao = new ChuongTrinhKhuyenMaiDAO();

    public static void main(String[] args) {
        System.out.println("=== BẮT ĐẦU TEST CHƯƠNG TRÌNH KHUYẾN MÃI DAO ===\n");

        // Test 1: Tạo mã tự động
        testTaoMaTuDong();

        // Test 2: Thêm chương trình khuyến mãi
        testThem();

        // Test 3: Cập nhật chương trình
        testCapNhat();

        // Test 4: Tìm theo mã
        testTimTheoMa();

        // Test 5: Tìm theo tên
        testTimTheoTen();

        // Test 6: Tìm theo trạng thái
        testTimTheoTrangThai();

        // Test 7: Tìm theo loại
        testTimTheoLoai();

        // Test 8: Lấy tất cả
        testLayTatCa();

        // Test 9: Lấy chương trình đang hoạt động
        testLayChuongTrinhDangHoatDong();

        // Test 10: Tìm chương trình phù hợp
        testTimChuongTrinhPhuHop();

        // Test 11: Tìm chương trình tốt nhất
        testTimChuongTrinhTotNhat();

        // Test 12: Kiểm tra còn hiệu lực
        testKiemTraConHieuLuc();

        // Test 13: Kiểm tra điều kiện
        testKiemTraDieuKien();

        // Test 14: Tính giá trị khuyến mãi
        testTinhGiaTriKhuyenMai();

        // Test 15: Cập nhật trạng thái
        testCapNhatTrangThai();

        // Test 16: Tắt chương trình
        testTatChuongTrinh();

        // Test 17: Bật chương trình
        testBatChuongTrinh();

        // Test 18: Cập nhật chương trình hết hạn tự động
        testCapNhatChuongTrinhHetHan();

        // Test 19: Đếm số lượt sử dụng
        testDemSoLuotSuDung();

        // Test 20: Tổng khuyến mãi đã tặng
        testTongKhuyenMaiDaTang();

        // Test 21: Thống kê chương trình
        testThongKeChuongTrinh();

        // Test 22: Xóa chương trình
        testXoa();

        System.out.println("\n=== KẾT THÚC TEST ===");
    }

    // ===== TEST 1: TẠO MÃ TỰ ĐỘNG =====
    private static void testTaoMaTuDong() {
        System.out.println("--- Test 1: Tạo mã chương trình tự động ---");
        try {
            String maMoi = dao.taoMaChuongTrinhTuDong();
            System.out.println("✓ Mã chương trình mới: " + maMoi);
        } catch (Exception e) {
            System.out.println("✗ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    // ===== TEST 2: THÊM CHƯƠNG TRÌNH - ĐÃ SỬA =====
    private static void testThem() {
        System.out.println("--- Test 2: Thêm chương trình khuyến mãi ---");
        try {
            ChuongTrinhKhuyenMai ctkm = new ChuongTrinhKhuyenMai();

            // SỬA: Tạo mã mới NGAY TRƯỚC KHI THÊM
            String maMoi = dao.taoMaChuongTrinhTuDong();
            ctkm.setMaCTKM(maMoi);

            ctkm.setTenCT("Khuyến mãi test " + System.currentTimeMillis());
            ctkm.setLoaiKM("PHANTRAM");
            ctkm.setGiaTriKM(10.0);  // 10%
            ctkm.setDieuKienToiThieu(100000);
            ctkm.setNgayBatDau(LocalDateTime.now());
            ctkm.setNgayKetThuc(LocalDateTime.now().plusMonths(1));
            ctkm.setTrangThai("HOATDONG");

            boolean ketQua = dao.them(ctkm);

            if (ketQua) {
                System.out.println("✓ Thêm chương trình thành công!");
                System.out.println("  Mã CTKM: " + ctkm.getMaCTKM());
                System.out.println("  Tên: " + ctkm.getTenCT());
                System.out.println("  Loại: " + ctkm.getTenLoaiKM());
                System.out.println("  Giá trị: " + ctkm.getGiaTriKMFormatted());
                System.out.println("  Điều kiện: " + ctkm.getDieuKienToiThieuFormatted());
            } else {
                System.out.println("✗ Thêm chương trình thất bại!");
            }
        } catch (Exception e) {
            System.out.println("✗ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    // ===== TEST 3: CẬP NHẬT - SỬA TÌM MÃ CÓ TRONG DB =====
    private static void testCapNhat() {
        System.out.println("--- Test 3: Cập nhật chương trình ---");
        try {
            // SỬA: Lấy mã chương trình có trong DB
            List<ChuongTrinhKhuyenMai> danhSach = dao.layTatCa();

            if (!danhSach.isEmpty()) {
                ChuongTrinhKhuyenMai ctkm = danhSach.get(0);

                ctkm.setGiaTriKM(15.0);  // Tăng lên 15%
                ctkm.setDieuKienToiThieu(150000);

                boolean ketQua = dao.capNhat(ctkm);

                if (ketQua) {
                    System.out.println("✓ Cập nhật chương trình thành công!");
                    System.out.println("  Mã: " + ctkm.getMaCTKM());
                    System.out.println("  Giá trị mới: " + ctkm.getGiaTriKMFormatted());
                    System.out.println("  Điều kiện mới: " + ctkm.getDieuKienToiThieuFormatted());
                } else {
                    System.out.println("✗ Cập nhật chương trình thất bại!");
                }
            } else {
                System.out.println("✗ Không có chương trình nào để test");
            }
        } catch (Exception e) {
            System.out.println("✗ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    // ===== TEST 4: TÌM THEO MÃ - SỬA =====
    private static void testTimTheoMa() {
        System.out.println("--- Test 4: Tìm chương trình theo mã ---");
        try {
            // SỬA: Lấy mã có trong DB
            List<ChuongTrinhKhuyenMai> danhSach = dao.layTatCa();

            if (!danhSach.isEmpty()) {
                String maCTKM = danhSach.get(0).getMaCTKM();
                ChuongTrinhKhuyenMai ctkm = dao.timTheoMa(maCTKM);

                if (ctkm != null) {
                    System.out.println("✓ Tìm thấy chương trình!");
                    inThongTinChuongTrinh(ctkm);
                } else {
                    System.out.println("✗ Không tìm thấy chương trình: " + maCTKM);
                }
            } else {
                System.out.println("✗ Không có chương trình nào để test");
            }
        } catch (Exception e) {
            System.out.println("✗ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    // ===== PHẦN CÒN LẠI GIỮ NGUYÊN =====
    // (Các test khác không cần sửa vì không bị lỗi)

    private static void testTimTheoTen() {
        System.out.println("--- Test 5: Tìm chương trình theo tên ---");
        try {
            String tenCT = "nạp";
            List<ChuongTrinhKhuyenMai> danhSach = dao.timTheoTen(tenCT);

            System.out.println("✓ Tìm thấy " + danhSach.size() +
                    " chương trình có từ khóa: " + tenCT);

            for (ChuongTrinhKhuyenMai ct : danhSach) {
                System.out.println("  - " + ct.getMaCTKM() + ": " + ct.getTenCT());
            }
        } catch (Exception e) {
            System.out.println("✗ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    private static void testTimTheoTrangThai() {
        System.out.println("--- Test 6: Tìm chương trình theo trạng thái ---");
        try {
            String[] trangThai = {"HOATDONG", "NGUNG", "HETHAN"};

            for (String tt : trangThai) {
                List<ChuongTrinhKhuyenMai> danhSach = dao.timTheoTrangThai(tt);
                System.out.println("  " + tt + ": " + danhSach.size() + " chương trình");
            }
        } catch (Exception e) {
            System.out.println("✗ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    private static void testTimTheoLoai() {
        System.out.println("--- Test 7: Tìm chương trình theo loại ---");
        try {
            String[] loaiKM = {"PHANTRAM", "SOTIEN", "TANGGIO"};

            for (String loai : loaiKM) {
                List<ChuongTrinhKhuyenMai> danhSach = dao.timTheoLoai(loai);
                System.out.println("  " + loai + ": " + danhSach.size() + " chương trình");
            }
        } catch (Exception e) {
            System.out.println("✗ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    private static void testLayTatCa() {
        System.out.println("--- Test 8: Lấy tất cả chương trình ---");
        try {
            List<ChuongTrinhKhuyenMai> danhSach = dao.layTatCa();

            System.out.println("✓ Tổng số chương trình: " + danhSach.size());

            if (!danhSach.isEmpty()) {
                System.out.println("  Chương trình mới nhất:");
                ChuongTrinhKhuyenMai ct = danhSach.get(0);
                System.out.println("  - " + ct.getMaCTKM() + ": " + ct.getTenCT() +
                        " (" + ct.getTenTrangThai() + ")");
            }
        } catch (Exception e) {
            System.out.println("✗ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    private static void testLayChuongTrinhDangHoatDong() {
        System.out.println("--- Test 9: Lấy chương trình đang hoạt động ---");
        try {
            List<ChuongTrinhKhuyenMai> danhSach = dao.layChuongTrinhDangHoatDong();

            System.out.println("✓ Có " + danhSach.size() + " chương trình đang hoạt động");

            for (ChuongTrinhKhuyenMai ct : danhSach) {
                System.out.println("  - " + ct.getTenCT() +
                        " (" + ct.getGiaTriKMFormatted() + ")");
            }
        } catch (Exception e) {
            System.out.println("✗ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    private static void testTimChuongTrinhPhuHop() {
        System.out.println("--- Test 10: Tìm chương trình phù hợp với số tiền ---");
        try {
            double soTienNap = 200000;
            List<ChuongTrinhKhuyenMai> danhSach = dao.timChuongTrinhPhuHop(soTienNap);

            System.out.println("✓ Với số tiền " + String.format("%,.0f VND", soTienNap));
            System.out.println("  Có " + danhSach.size() + " chương trình phù hợp:");

            for (ChuongTrinhKhuyenMai ct : danhSach) {
                double khuyenMai = ct.tinhKhuyenMai(soTienNap);
                System.out.println("  - " + ct.getTenCT() +
                        " → Khuyến mãi: " + String.format("%,.0f VND", khuyenMai));
            }
        } catch (Exception e) {
            System.out.println("✗ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    private static void testTimChuongTrinhTotNhat() {
        System.out.println("--- Test 11: Tìm chương trình tốt nhất ---");
        try {
            double soTienNap = 250000;
            ChuongTrinhKhuyenMai ctTotNhat = dao.timChuongTrinhTotNhat(soTienNap);

            if (ctTotNhat != null) {
                System.out.println("✓ Chương trình tốt nhất cho " +
                        String.format("%,.0f VND", soTienNap) + ":");
                System.out.println("  " + ctTotNhat.getTenCT());
                System.out.println("  Giá trị: " + ctTotNhat.getGiaTriKMFormatted());

                double khuyenMai = dao.tinhGiaTriKhuyenMai(ctTotNhat.getMaCTKM(), soTienNap);
                System.out.println("  Khuyến mãi: " + String.format("%,.0f VND", khuyenMai));
            } else {
                System.out.println("✗ Không tìm thấy chương trình phù hợp");
            }
        } catch (Exception e) {
            System.out.println("✗ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    // ===== TEST 12-22: GIỮ NGUYÊN LOGIC CŨ =====
    // (Những test này sửa để lấy mã từ DB thay vì hardcode)

    private static void testKiemTraConHieuLuc() {
        System.out.println("--- Test 12: Kiểm tra chương trình còn hiệu lực ---");
        try {
            List<ChuongTrinhKhuyenMai> danhSach = dao.layTatCa();
            if (!danhSach.isEmpty()) {
                String maCTKM = danhSach.get(0).getMaCTKM();
                boolean conHieuLuc = dao.kiemTraConHieuLuc(maCTKM);

                if (conHieuLuc) {
                    System.out.println("✓ Chương trình " + maCTKM + " còn hiệu lực");
                } else {
                    System.out.println("✗ Chương trình " + maCTKM + " hết hiệu lực");
                }
            }
        } catch (Exception e) {
            System.out.println("✗ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    private static void testKiemTraDieuKien() {
        System.out.println("--- Test 13: Kiểm tra điều kiện áp dụng ---");
        try {
            List<ChuongTrinhKhuyenMai> danhSach = dao.layTatCa();
            if (!danhSach.isEmpty()) {
                String maCTKM = danhSach.get(0).getMaCTKM();
                double[] soTien = {50000, 100000, 200000};

                for (double tien : soTien) {
                    boolean duDieuKien = dao.kiemTraDieuKien(maCTKM, tien);
                    System.out.println("  " + String.format("%,.0f VND", tien) +
                            ": " + (duDieuKien ? "✓ Đủ điều kiện" : "✗ Không đủ"));
                }
            }
        } catch (Exception e) {
            System.out.println("✗ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    private static void testTinhGiaTriKhuyenMai() {
        System.out.println("--- Test 14: Tính giá trị khuyến mãi ---");
        try {
            List<ChuongTrinhKhuyenMai> danhSach = dao.layTatCa();
            if (!danhSach.isEmpty()) {
                String maCTKM = danhSach.get(0).getMaCTKM();
                double soTienNap = 200000;

                double khuyenMai = dao.tinhGiaTriKhuyenMai(maCTKM, soTienNap);

                System.out.println("✓ Nạp " + String.format("%,.0f VND", soTienNap));
                System.out.println("  Khuyến mãi: " + String.format("%,.0f VND", khuyenMai));
                System.out.println("  Tổng nhận: " +
                        String.format("%,.0f VND", soTienNap + khuyenMai));
            }
        } catch (Exception e) {
            System.out.println("✗ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    private static void testCapNhatTrangThai() {
        System.out.println("--- Test 15: Cập nhật trạng thái ---");
        try {
            List<ChuongTrinhKhuyenMai> danhSach = dao.layTatCa();
            if (!danhSach.isEmpty()) {
                String maCTKM = danhSach.get(0).getMaCTKM();
                boolean ketQua = dao.capNhatTrangThai(maCTKM, "HOATDONG");

                if (ketQua) {
                    System.out.println("✓ Cập nhật trạng thái thành công!");
                } else {
                    System.out.println("✗ Cập nhật trạng thái thất bại!");
                }
            }
        } catch (Exception e) {
            System.out.println("✗ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    private static void testTatChuongTrinh() {
        System.out.println("--- Test 16: Tắt chương trình ---");
        try {
            List<ChuongTrinhKhuyenMai> danhSach = dao.layTatCa();
            if (danhSach.size() > 1) {
                String maCTKM = danhSach.get(1).getMaCTKM();
                boolean ketQua = dao.tatChuongTrinh(maCTKM);

                if (ketQua) {
                    System.out.println("✓ Tắt chương trình thành công!");
                    ChuongTrinhKhuyenMai ct = dao.timTheoMa(maCTKM);
                    if (ct != null) {
                        System.out.println("  Trạng thái mới: " + ct.getTenTrangThai());
                    }
                } else {
                    System.out.println("✗ Tắt chương trình thất bại!");
                }
            }
        } catch (Exception e) {
            System.out.println("✗ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    private static void testBatChuongTrinh() {
        System.out.println("--- Test 17: Bật chương trình ---");
        try {
            List<ChuongTrinhKhuyenMai> danhSach = dao.layTatCa();
            if (danhSach.size() > 1) {
                String maCTKM = danhSach.get(1).getMaCTKM();
                boolean ketQua = dao.batChuongTrinh(maCTKM);

                if (ketQua) {
                    System.out.println("✓ Bật chương trình thành công!");
                    ChuongTrinhKhuyenMai ct = dao.timTheoMa(maCTKM);
                    if (ct != null) {
                        System.out.println("  Trạng thái mới: " + ct.getTenTrangThai());
                    }
                } else {
                    System.out.println("✗ Bật chương trình thất bại!");
                }
            }
        } catch (Exception e) {
            System.out.println("✗ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    private static void testCapNhatChuongTrinhHetHan() {
        System.out.println("--- Test 18: Cập nhật chương trình hết hạn tự động ---");
        try {
            int soLuong = dao.capNhatChuongTrinhHetHan();

            System.out.println("✓ Đã cập nhật " + soLuong + " chương trình hết hạn");
        } catch (Exception e) {
            System.out.println("✗ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    private static void testDemSoLuotSuDung() {
        System.out.println("--- Test 19: Đếm số lượt sử dụng ---");
        try {
            List<ChuongTrinhKhuyenMai> danhSach = dao.layTatCa();
            if (!danhSach.isEmpty()) {
                String maCTKM = danhSach.get(0).getMaCTKM();
                int soLuot = dao.demSoLuotSuDung(maCTKM);

                System.out.println("✓ Chương trình " + maCTKM +
                        " đã được sử dụng " + soLuot + " lần");
            }
        } catch (Exception e) {
            System.out.println("✗ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    private static void testTongKhuyenMaiDaTang() {
        System.out.println("--- Test 20: Tổng khuyến mãi đã tặng ---");
        try {
            List<ChuongTrinhKhuyenMai> danhSach = dao.layTatCa();
            if (!danhSach.isEmpty()) {
                String maCTKM = danhSach.get(0).getMaCTKM();
                double tongKM = dao.tongKhuyenMaiDaTang(maCTKM);

                System.out.println("✓ Chương trình " + maCTKM);
                System.out.println("  Tổng khuyến mãi đã tặng: " +
                        String.format("%,.0f VND", tongKM));
            }
        } catch (Exception e) {
            System.out.println("✗ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    private static void testThongKeChuongTrinh() {
        System.out.println("--- Test 21: Thống kê chương trình theo thời gian ---");
        try {
            LocalDateTime tuNgay = LocalDateTime.now().minusMonths(1);
            LocalDateTime denNgay = LocalDateTime.now();

            List<Object[]> thongKe = dao.thongKeChuongTrinh(tuNgay, denNgay);

            System.out.println("✓ Thống kê chương trình trong 1 tháng:");
            for (Object[] row : thongKe) {
                System.out.println("  " + row[0] + " - " + row[1] + ":");
                System.out.println("    Loại: " + row[2]);
                System.out.println("    Số lượt dùng: " + row[3]);
                System.out.println("    Tổng KM: " + String.format("%,.0f VND", (double)row[4]));
            }
        } catch (Exception e) {
            System.out.println("✗ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    private static void testXoa() {
        System.out.println("--- Test 22: Xóa chương trình ---");
        try {
            // Tạo chương trình test để xóa
            ChuongTrinhKhuyenMai ctTest = new ChuongTrinhKhuyenMai();
            ctTest.setMaCTKM("CTKM_TEST_DELETE");
            ctTest.setTenCT("Test Delete");
            ctTest.setLoaiKM("PHANTRAM");
            ctTest.setGiaTriKM(5.0);
            ctTest.setDieuKienToiThieu(50000);
            ctTest.setNgayBatDau(LocalDateTime.now());
            ctTest.setNgayKetThuc(LocalDateTime.now().plusDays(7));
            ctTest.setTrangThai("HOATDONG");

            dao.them(ctTest);

            // Xóa chương trình
            boolean ketQua = dao.xoa("CTKM_TEST_DELETE");

            if (ketQua) {
                System.out.println("✓ Xóa chương trình thành công!");
            } else {
                System.out.println("✗ Xóa chương trình thất bại!");
            }
        } catch (Exception e) {
            System.out.println("✗ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    // ===== PHƯƠNG THỨC HỖ TRỢ =====
    private static void inThongTinChuongTrinh(ChuongTrinhKhuyenMai ct) {
        System.out.println("  Mã CTKM: " + ct.getMaCTKM());
        System.out.println("  Tên: " + ct.getTenCT());
        System.out.println("  Loại: " + ct.getTenLoaiKM());
        System.out.println("  Giá trị: " + ct.getGiaTriKMFormatted());
        System.out.println("  Điều kiện: " + ct.getDieuKienToiThieuFormatted());
        System.out.println("  Ngày bắt đầu: " + ct.getNgayBatDauFormatted());
        System.out.println("  Ngày kết thúc: " + ct.getNgayKetThucFormatted());
        System.out.println("  Trạng thái: " + ct.getTenTrangThai());
        System.out.println("  Còn hiệu lực: " + (ct.conHieuLuc() ? "Có" : "Không"));
    }
}