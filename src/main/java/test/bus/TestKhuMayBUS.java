package test.bus;

import bus.KhuMayBUS;
import entity.KhuMay;
import entity.NhanVien;
import untils.SessionManager;

import java.util.List;

/**
 * KhuMayBUSTest - Test lớp KhuMayBUS
 *
 * Sử dụng: Java thuần (không cần JUnit, Mockito)
 * Kết nối: DB thật
 * Chạy: Run trực tiếp file này
 *
 * Dữ liệu DB hiện có:
 * - Khu HOATDONG: KHU004, KHU3, KHU002, KHU006
 * - Khu NGUNG: KHU005, KHU001
 */
public class TestKhuMayBUS {

    private static KhuMayBUS khuMayBUS;
    private static NhanVien quanLy;
    private static NhanVien nhanVien;

    // ============== BIẾN ĐẾM KẾT QUẢ ==============
    private static int totalTests = 0;
    private static int passedTests = 0;
    private static int failedTests = 0;

    // ============== DỮ LIỆU TỪ DB ==============
    private static final String MA_KHU_HOATDONG = "KHU004";
    private static final String MA_KHU_NGUNG = "KHU005";

    // ============== MAIN ==============
    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║         TEST KhuMayBUS - BẮT ĐẦU                ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
        System.out.println();

        // Khởi tạo
        setup();

        // Chạy test
        testGetAllKhuMay();
        testThemKhuMay();
        testSuaKhuMay();
        testXoaKhuMay();
        testDemSoMayTrongKhu();

        // In kết quả tổng
        printSummary();
    }

    // ============== SETUP ==============
    private static void setup() {
        khuMayBUS = new KhuMayBUS();

        quanLy = new NhanVien();
        quanLy.setManv("NV001");
        quanLy.setTen("Nguyễn Văn A");
        quanLy.setChucvu("QUANLY");

        nhanVien = new NhanVien();
        nhanVien.setManv("NV002");
        nhanVien.setTen("Trần Văn B");
        nhanVien.setChucvu("NHANVIEN");
    }

    // ============== HÀM HỖ TRỢ ==============

    private static void printTestHeader(String testName) {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("  📝 " + testName);
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }

    private static void assertPass(String testCase) {
        totalTests++;
        passedTests++;
        System.out.println("  ✅ PASS: " + testCase);
    }

    private static void assertFail(String testCase, String reason) {
        totalTests++;
        failedTests++;
        System.out.println("  ❌ FAIL: " + testCase + " → " + reason);
    }

    private static void loginQuanLy() {
        SessionManager.clearSession();
        SessionManager.setCurrentUser(quanLy);
    }

    private static void loginNhanVien() {
        SessionManager.clearSession();
        SessionManager.setCurrentUser(nhanVien);
    }

    private static void logout() {
        SessionManager.clearSession();
    }

    // ============================================================
    // 1. TEST getAllKhuMay()
    // ============================================================
    private static void testGetAllKhuMay() {
        printTestHeader("1. TEST getAllKhuMay()");

        // --- ✅ Test 1.1: QUANLY lấy tất cả khu ---
        try {
            loginQuanLy();
            List<KhuMay> list = khuMayBUS.getAllKhuMay();
            if (list != null && list.size() > 0) {
                assertPass("QUANLY - Lấy tất cả khu thành công (" + list.size() + " khu)");
            } else {
                assertFail("QUANLY - Lấy tất cả khu", "Danh sách rỗng hoặc null");
            }
        } catch (Exception e) {
            assertFail("QUANLY - Lấy tất cả khu", e.getMessage());
        } finally {
            logout();
        }

        // --- ✅ Test 1.2: NHANVIEN lấy tất cả khu ---
        try {
            loginNhanVien();
            List<KhuMay> list = khuMayBUS.getAllKhuMay();
            if (list != null && list.size() > 0) {
                assertPass("NHANVIEN - Lấy tất cả khu thành công (" + list.size() + " khu)");
            } else {
                assertFail("NHANVIEN - Lấy tất cả khu", "Danh sách rỗng hoặc null");
            }
        } catch (Exception e) {
            assertFail("NHANVIEN - Lấy tất cả khu", e.getMessage());
        } finally {
            logout();
        }

        // --- ❌ Test 1.3: Chưa đăng nhập ---
        try {
            logout();
            khuMayBUS.getAllKhuMay();
            assertFail("Chưa đăng nhập", "Không throw Exception");
        } catch (Exception e) {
            assertPass("Chưa đăng nhập → Exception: " + e.getMessage());
        }

        System.out.println();
    }

    // ============================================================
    // 2. TEST themKhuMay()
    // ============================================================
    private static void testThemKhuMay() {
        printTestHeader("2. TEST themKhuMay()");

        String maKhuDaThem = null;

        // --- ✅ Test 2.1: Thêm khu thành công ---
        try {
            loginQuanLy();
            KhuMay khuMoi = new KhuMay();
            khuMoi.setTenkhu("KhuTest_" + System.currentTimeMillis());
            khuMoi.setGiacoso(12000);
            khuMoi.setSomaytoida(15);

            KhuMay result = khuMayBUS.themKhuMay(khuMoi);
            if (result != null && result.getMakhu() != null) {
                maKhuDaThem = result.getMakhu();
                assertPass("Thêm khu thành công (Mã: " + result.getMakhu() + ", Tên: " + result.getTenkhu() + ")");
            } else {
                assertFail("Thêm khu", "Kết quả null hoặc không có mã khu");
            }
        } catch (Exception e) {
            assertFail("Thêm khu", e.getMessage());
        } finally {
            logout();
        }

        // --- ❌ Test 2.2: NHANVIEN thêm khu (không có quyền) ---
        try {
            loginNhanVien();
            KhuMay khuMoi = new KhuMay();
            khuMoi.setTenkhu("KhuTestNV_" + System.currentTimeMillis());
            khuMoi.setGiacoso(10000);
            khuMoi.setSomaytoida(10);

            khuMayBUS.themKhuMay(khuMoi);
            assertFail("NHANVIEN thêm khu", "Không throw Exception");
        } catch (Exception e) {
            assertPass("NHANVIEN thêm khu → Exception: " + e.getMessage());
        } finally {
            logout();
        }

        // --- ❌ Test 2.3: Dữ li��u null ---
        try {
            loginQuanLy();
            khuMayBUS.themKhuMay(null);
            assertFail("Dữ liệu null", "Không throw Exception");
        } catch (Exception e) {
            assertPass("Dữ liệu null → Exception: " + e.getMessage());
        } finally {
            logout();
        }

        // --- ❌ Test 2.4: Số máy tối đa vượt quá 100 ---
        try {
            loginQuanLy();
            KhuMay khuMoi = new KhuMay();
            khuMoi.setTenkhu("KhuTestMax_" + System.currentTimeMillis());
            khuMoi.setGiacoso(10000);
            khuMoi.setSomaytoida(150);

            khuMayBUS.themKhuMay(khuMoi);
            assertFail("Số máy > 100", "Không throw Exception");
        } catch (Exception e) {
            assertPass("Số máy > 100 → Exception: " + e.getMessage());
        } finally {
            logout();
        }

        // --- ❌ Test 2.5: Giá cơ sở <= 0 (DAO validate) ---
        try {
            loginQuanLy();
            KhuMay khuMoi = new KhuMay();
            khuMoi.setTenkhu("KhuTestGia0_" + System.currentTimeMillis());
            khuMoi.setGiacoso(0);
            khuMoi.setSomaytoida(10);

            khuMayBUS.themKhuMay(khuMoi);
            assertFail("Giá cơ sở <= 0", "Không throw Exception");
        } catch (Exception e) {
            assertPass("Giá cơ sở <= 0 → Exception: " + e.getMessage());
        } finally {
            logout();
        }

        // --- ❌ Test 2.6: Tên khu rỗng (DAO validate) ---
        try {
            loginQuanLy();
            KhuMay khuMoi = new KhuMay();
            khuMoi.setTenkhu("");
            khuMoi.setGiacoso(10000);
            khuMoi.setSomaytoida(10);

            khuMayBUS.themKhuMay(khuMoi);
            assertFail("Tên khu rỗng", "Không throw Exception");
        } catch (Exception e) {
            assertPass("Tên khu rỗng → Exception: " + e.getMessage());
        } finally {
            logout();
        }

        // --- ❌ Test 2.7: Chưa đăng nhập ---
        try {
            logout();
            KhuMay khuMoi = new KhuMay();
            khuMoi.setTenkhu("KhuTestNoLogin_" + System.currentTimeMillis());
            khuMoi.setGiacoso(10000);
            khuMoi.setSomaytoida(10);

            khuMayBUS.themKhuMay(khuMoi);
            assertFail("Chưa đăng nhập", "Không throw Exception");
        } catch (Exception e) {
            assertPass("Chưa đăng nhập → Exception: " + e.getMessage());
        }

        // --- 🧹 DỌN DẸP ---
        cleanupKhu(maKhuDaThem);

        System.out.println();
    }

    // ============================================================
    // 3. TEST suaKhuMay()
    // ============================================================
    private static void testSuaKhuMay() {
        printTestHeader("3. TEST suaKhuMay()");

        String maKhuTestSua = null;

        // --- ✅ Test 3.1: Sửa khu HOATDONG thành công ---
        try {
            loginQuanLy();

            // Thêm khu test trước
            KhuMay khuTest = new KhuMay();
            khuTest.setTenkhu("KhuTestSua_" + System.currentTimeMillis());
            khuTest.setGiacoso(10000);
            khuTest.setSomaytoida(20);
            KhuMay khuDaThem = khuMayBUS.themKhuMay(khuTest);
            maKhuTestSua = khuDaThem.getMakhu();

            // Sửa thông tin
            khuDaThem.setTenkhu("KhuTestSua_Updated_" + System.currentTimeMillis());
            khuDaThem.setGiacoso(15000);
            khuDaThem.setSomaytoida(25);

            KhuMay result = khuMayBUS.suaKhuMay(khuDaThem);
            if (result != null) {
                assertPass("Sửa khu HOATDONG thành công (Mã: " + result.getMakhu() + ")");
            } else {
                assertFail("Sửa khu HOATDONG", "Kết quả null");
            }
        } catch (Exception e) {
            assertFail("Sửa khu HOATDONG", e.getMessage());
        } finally {
            logout();
        }

        // --- ❌ Test 3.2: NHANVIEN sửa khu (không có quyền) ---
        try {
            loginNhanVien();
            KhuMay khuSua = new KhuMay();
            khuSua.setMakhu(MA_KHU_HOATDONG);
            khuSua.setTenkhu("Updated");
            khuSua.setGiacoso(15000);
            khuSua.setSomaytoida(20);

            khuMayBUS.suaKhuMay(khuSua);
            assertFail("NHANVIEN sửa khu", "Không throw Exception");
        } catch (Exception e) {
            assertPass("NHANVIEN sửa khu → Exception: " + e.getMessage());
        } finally {
            logout();
        }

        // --- ❌ Test 3.3: Dữ liệu null ---
        try {
            loginQuanLy();
            khuMayBUS.suaKhuMay(null);
            assertFail("Dữ liệu null", "Không throw Exception");
        } catch (Exception e) {
            assertPass("Dữ liệu null → Exception: " + e.getMessage());
        } finally {
            logout();
        }

        // --- ❌ Test 3.4: Mã khu rỗng ---
        try {
            loginQuanLy();
            KhuMay khuSua = new KhuMay();
            khuSua.setMakhu("");
            khuSua.setTenkhu("Test");
            khuSua.setGiacoso(10000);
            khuSua.setSomaytoida(10);

            khuMayBUS.suaKhuMay(khuSua);
            assertFail("Mã khu rỗng", "Không throw Exception");
        } catch (Exception e) {
            assertPass("Mã khu rỗng → Exception: " + e.getMessage());
        } finally {
            logout();
        }

        // --- �� Test 3.5: Khu không tồn tại ---
        try {
            loginQuanLy();
            KhuMay khuSua = new KhuMay();
            khuSua.setMakhu("KHU999");
            khuSua.setTenkhu("Test");
            khuSua.setGiacoso(10000);
            khuSua.setSomaytoida(10);

            khuMayBUS.suaKhuMay(khuSua);
            assertFail("Khu không tồn tại", "Không throw Exception");
        } catch (Exception e) {
            assertPass("Khu không tồn tại → Exception: " + e.getMessage());
        } finally {
            logout();
        }

        // --- ❌ Test 3.6: Sửa khu đã NGUNG ---
        try {
            loginQuanLy();
            KhuMay khuSua = new KhuMay();
            khuSua.setMakhu(MA_KHU_NGUNG);
            khuSua.setTenkhu("Updated");
            khuSua.setGiacoso(15000);
            khuSua.setSomaytoida(20);

            khuMayBUS.suaKhuMay(khuSua);
            assertFail("Sửa khu NGUNG", "Không throw Exception");
        } catch (Exception e) {
            assertPass("Sửa khu NGUNG → Exception: " + e.getMessage());
        } finally {
            logout();
        }

        // --- ❌ Test 3.7: Số máy tối đa < số máy hiện có ---
        try {
            loginQuanLy();
            // Dùng khu HOATDONG có máy trong đó, đặt somaytoida = 0
            KhuMay khuSua = new KhuMay();
            khuSua.setMakhu(MA_KHU_HOATDONG);
            khuSua.setTenkhu("Updated_" + System.currentTimeMillis());
            khuSua.setGiacoso(15000);
            khuSua.setSomaytoida(0); // nhỏ hơn số máy hiện có

            khuMayBUS.suaKhuMay(khuSua);
            assertFail("SoMayToiDa < SoMayHienCo", "Không throw Exception");
        } catch (Exception e) {
            assertPass("SoMayToiDa < SoMayHienCo → Exception: " + e.getMessage());
        } finally {
            logout();
        }

        // --- ❌ Test 3.8: Chưa đăng nhập ---
        try {
            logout();
            KhuMay khuSua = new KhuMay();
            khuSua.setMakhu(MA_KHU_HOATDONG);
            khuSua.setTenkhu("Updated");
            khuSua.setGiacoso(10000);
            khuSua.setSomaytoida(10);

            khuMayBUS.suaKhuMay(khuSua);
            assertFail("Chưa đăng nhập", "Không throw Exception");
        } catch (Exception e) {
            assertPass("Chưa đăng nhập → Exception: " + e.getMessage());
        }

        // --- 🧹 DỌN DẸP ---
        cleanupKhu(maKhuTestSua);

        System.out.println();
    }

    // ============================================================
    // 4. TEST xoaKhuMay()
    // ============================================================
    private static void testXoaKhuMay() {
        printTestHeader("4. TEST xoaKhuMay()");

        // --- ✅ Test 4.1: Xóa khu HOATDONG (không có máy) thành công ---
        try {
            loginQuanLy();

            // Thêm khu test trước để xóa
            KhuMay khuTest = new KhuMay();
            khuTest.setTenkhu("KhuTestXoa_" + System.currentTimeMillis());
            khuTest.setGiacoso(10000);
            khuTest.setSomaytoida(10);
            KhuMay khuDaThem = khuMayBUS.themKhuMay(khuTest);

            // Xóa khu vừa thêm
            boolean result = khuMayBUS.xoaKhuMay(khuDaThem.getMakhu());
            if (result) {
                assertPass("Xóa khu HOATDONG thành công (Mã: " + khuDaThem.getMakhu() + ")");
            } else {
                assertFail("Xóa khu HOATDONG", "Kết quả false");
            }
        } catch (Exception e) {
            assertFail("Xóa khu HOATDONG", e.getMessage());
        } finally {
            logout();
        }

        // --- ❌ Test 4.2: NHANVIEN xóa khu (không có quyền) ---
        try {
            loginNhanVien();
            khuMayBUS.xoaKhuMay(MA_KHU_HOATDONG);
            assertFail("NHANVIEN xóa khu", "Không throw Exception");
        } catch (Exception e) {
            assertPass("NHANVIEN xóa khu → Exception: " + e.getMessage());
        } finally {
            logout();
        }

        // --- ❌ Test 4.3: Mã khu null ---
        try {
            loginQuanLy();
            khuMayBUS.xoaKhuMay(null);
            assertFail("Mã khu null", "Không throw Exception");
        } catch (Exception e) {
            assertPass("Mã khu null → Exception: " + e.getMessage());
        } finally {
            logout();
        }

        // --- ❌ Test 4.4: Mã khu rỗng ---
        try {
            loginQuanLy();
            khuMayBUS.xoaKhuMay("  ");
            assertFail("Mã khu rỗng", "Không throw Exception");
        } catch (Exception e) {
            assertPass("Mã khu rỗng → Exception: " + e.getMessage());
        } finally {
            logout();
        }

        // --- ❌ Test 4.5: Khu không tồn tại ---
        try {
            loginQuanLy();
            khuMayBUS.xoaKhuMay("KHU999");
            assertFail("Khu không tồn tại", "Không throw Exception");
        } catch (Exception e) {
            assertPass("Khu không tồn tại → Exception: " + e.getMessage());
        } finally {
            logout();
        }

        // --- ❌ Test 4.6: Khu đã NGUNG ---
        try {
            loginQuanLy();
            khuMayBUS.xoaKhuMay(MA_KHU_NGUNG);
            assertFail("Khu đã NGUNG", "Không throw Exception");
        } catch (Exception e) {
            assertPass("Khu đã NGUNG → Exception: " + e.getMessage());
        } finally {
            logout();
        }

        // --- ❌ Test 4.7: Chưa đăng nhập ---
        try {
            logout();
            khuMayBUS.xoaKhuMay(MA_KHU_HOATDONG);
            assertFail("Chưa đăng nhập", "Không throw Exception");
        } catch (Exception e) {
            assertPass("Chưa đăng nhập → Exception: " + e.getMessage());
        }

        System.out.println();
    }

    // ============================================================
    // 5. TEST demSoMayTrongKhu()
    // ============================================================
    private static void testDemSoMayTrongKhu() {
        printTestHeader("5. TEST demSoMayTrongKhu()");

        // --- ✅ Test 5.1: QUANLY đếm máy trong khu HOATDONG ---
        try {
            loginQuanLy();
            int count = khuMayBUS.demSoMayTrongKhu(MA_KHU_HOATDONG);
            assertPass("QUANLY - Đếm máy trong khu " + MA_KHU_HOATDONG + ": " + count + " máy");
        } catch (Exception e) {
            assertFail("QUANLY - Đếm máy trong khu", e.getMessage());
        } finally {
            logout();
        }

        // --- ✅ Test 5.2: NHANVIEN đếm máy ---
        try {
            loginNhanVien();
            int count = khuMayBUS.demSoMayTrongKhu(MA_KHU_HOATDONG);
            assertPass("NHANVIEN - Đếm máy trong khu " + MA_KHU_HOATDONG + ": " + count + " máy");
        } catch (Exception e) {
            assertFail("NHANVIEN - Đếm máy trong khu", e.getMessage());
        } finally {
            logout();
        }

        // --- ✅ Test 5.3: Đếm máy khu NGUNG (vẫn đếm được) ---
        try {
            loginQuanLy();
            int count = khuMayBUS.demSoMayTrongKhu(MA_KHU_NGUNG);
            assertPass("Đếm máy trong khu NGUNG " + MA_KHU_NGUNG + ": " + count + " máy");
        } catch (Exception e) {
            assertFail("Đếm máy trong khu NGUNG", e.getMessage());
        } finally {
            logout();
        }

        // --- ❌ Test 5.4: Mã khu null ---
        try {
            loginQuanLy();
            khuMayBUS.demSoMayTrongKhu(null);
            assertFail("Mã khu null", "Không throw Exception");
        } catch (Exception e) {
            assertPass("Mã khu null → Exception: " + e.getMessage());
        } finally {
            logout();
        }

        // --- ❌ Test 5.5: Mã khu rỗng ---
        try {
            loginQuanLy();
            khuMayBUS.demSoMayTrongKhu("  ");
            assertFail("Mã khu rỗng", "Không throw Exception");
        } catch (Exception e) {
            assertPass("Mã khu rỗng → Exception: " + e.getMessage());
        } finally {
            logout();
        }

        // --- ❌ Test 5.6: Khu không tồn tại ---
        try {
            loginQuanLy();
            khuMayBUS.demSoMayTrongKhu("KHU999");
            assertFail("Khu không tồn tại", "Không throw Exception");
        } catch (Exception e) {
            assertPass("Khu không tồn tại → Exception: " + e.getMessage());
        } finally {
            logout();
        }

        // --- ❌ Test 5.7: Chưa đăng nhập ---
        try {
            logout();
            khuMayBUS.demSoMayTrongKhu(MA_KHU_HOATDONG);
            assertFail("Chưa đăng nhập", "Không throw Exception");
        } catch (Exception e) {
            assertPass("Chưa đăng nhập → Exception: " + e.getMessage());
        }

        System.out.println();
    }

    // ============================================================
    // DỌN DẸP DỮ LIỆU TEST
    // ============================================================
    private static void cleanupKhu(String maKhu) {
        if (maKhu == null) return;
        try {
            loginQuanLy();
            khuMayBUS.xoaKhuMay(maKhu);
            System.out.println("  🧹 Dọn dẹp: Đã xóa khu test " + maKhu);
        } catch (Exception e) {
            System.out.println("  🧹 Dọn dẹp: " + maKhu + " (" + e.getMessage() + ")");
        } finally {
            logout();
        }
    }

    // ============================================================
    // IN KẾT QUẢ TỔNG
    // ============================================================
    private static void printSummary() {
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║              KẾT QUẢ TỔNG HỢP                   ║");
        System.out.println("╠══════════════════════════════════════���═══════════╣");
        System.out.printf("║  Tổng test:    %-33d ║%n", totalTests);
        System.out.printf("║  ✅ PASS:      %-33d ║%n", passedTests);
        System.out.printf("║  ❌ FAIL:      %-33d ║%n", failedTests);
        System.out.printf("║  Tỷ lệ:       %-33s ║%n",
                (totalTests > 0 ? (passedTests * 100 / totalTests) + "%" : "N/A"));
        System.out.println("╠══════════════════════════════════════════════════╣");
        if (failedTests == 0) {
            System.out.println("║  🎉 TẤT CẢ TEST ĐỀU PASS!                      ║");
        } else {
            System.out.printf("║  ⚠️  CÓ %d TEST FAIL - CẦN KIỂM TRA LẠI!        ║%n", failedTests);
        }
        System.out.println("╚══════════════════════════════════════════════════╝");
    }
}