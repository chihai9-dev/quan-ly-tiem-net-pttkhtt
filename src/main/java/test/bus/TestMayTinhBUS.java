package test.bus;

import bus.MayTinhBUS;
import entity.MayTinh;
import entity.NhanVien;
import untils.SessionManager;

import java.util.List;

/**
 * TestMayTinhBUS - Test lớp MayTinhBUS
 *
 * Sử dụng: Java thuần (không cần JUnit, Mockito)
 * Kết nối: DB thật
 * Chạy: Run trực tiếp file này
 *
 * Dữ liệu DB hiện có:
 * - Khu HOATDONG: KHU004, KHU3, KHU002, KHU006
 * - Khu NGUNG: KHU005, KHU001
 * - Máy TRONG: MAY001, MAY002, MAY005, MAY006, MAY008, MAY009, MAY011, MAY012, MAY014, MAY015
 * - Máy DANGDUNG: MAY003, MAY007, MAY010, MAY013
 */
public class TestMayTinhBUS {

    private static MayTinhBUS mayTinhBUS;
    private static NhanVien quanLy;
    private static NhanVien nhanVien;

    // ============== BIẾN ĐẾM KẾT QUẢ ==============
    private static int totalTests = 0;
    private static int passedTests = 0;
    private static int failedTests = 0;

    // ============== DỮ LIỆU TỪ DB ==============
    private static final String MA_KHU_HOATDONG = "KHU004";
    private static final String MA_KHU_NGUNG = "KHU005";
    private static final String MA_MAY_TRONG = "MAY001";
    private static final String MA_MAY_DANGDUNG = "MAY003";

    // ============== MAIN ==============
    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║         TEST MayTinhBUS - BẮT ĐẦU               ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
        System.out.println();

        // Khởi tạo
        setup();

        // Chạy test
        testGetAllMayTinh();
        testGetMayTrong();
        testThemMayTinh();
        testSuaMayTinh();
        testXoaMayTinh();
        testChuyenTrangThai();

        // In kết quả tổng
        printSummary();
    }

    // ============== SETUP ==============
    private static void setup() {
        mayTinhBUS = new MayTinhBUS();

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
    // 1. TEST getAllMayTinh()
    // ============================================================
    private static void testGetAllMayTinh() {
        printTestHeader("1. TEST getAllMayTinh()");

        // --- ✅ Test 1.1: QUANLY lấy tất cả máy ---
        try {
            loginQuanLy();
            List<MayTinh> list = mayTinhBUS.getAllMayTinh();
            if (list != null && list.size() > 0) {
                assertPass("QUANLY - Lấy tất cả máy thành công (" + list.size() + " máy)");
            } else {
                assertFail("QUANLY - Lấy tất cả máy", "Danh sách rỗng hoặc null");
            }
        } catch (Exception e) {
            assertFail("QUANLY - Lấy tất cả máy", e.getMessage());
        } finally {
            logout();
        }

        // --- ✅ Test 1.2: NHANVIEN lấy tất cả máy ---
        try {
            loginNhanVien();
            List<MayTinh> list = mayTinhBUS.getAllMayTinh();
            if (list != null && list.size() > 0) {
                assertPass("NHANVIEN - Lấy tất cả máy thành công (" + list.size() + " máy)");
            } else {
                assertFail("NHANVIEN - Lấy tất cả máy", "Danh sách rỗng hoặc null");
            }
        } catch (Exception e) {
            assertFail("NHANVIEN - Lấy tất cả máy", e.getMessage());
        } finally {
            logout();
        }

        // --- ❌ Test 1.3: Chưa đăng nhập ---
        try {
            logout();
            mayTinhBUS.getAllMayTinh();
            assertFail("Chưa đăng nhập", "Không throw Exception");
        } catch (Exception e) {
            assertPass("Chưa đăng nhập → Exception: " + e.getMessage());
        }

        System.out.println();
    }

    // ============================================================
    // 2. TEST getMayTrong()
    // ============================================================
    private static void testGetMayTrong() {
        printTestHeader("2. TEST getMayTrong()");

        // --- ✅ Test 2.1: Lọc đúng máy trống ---
        try {
            loginQuanLy();
            List<MayTinh> list = mayTinhBUS.getMayTrong();
            if (list != null && list.size() > 0) {
                boolean allTrong = true;
                for (MayTinh mt : list) {
                    if (!"TRONG".equals(mt.getTrangthai())) {
                        allTrong = false;
                        break;
                    }
                }
                if (allTrong) {
                    assertPass("Lọc đúng máy trống (" + list.size() + " máy TRONG)");
                } else {
                    assertFail("Lọc máy trống", "Có máy không phải TRONG trong danh sách");
                }
            } else {
                assertFail("Lọc máy trống", "Danh sách rỗng");
            }
        } catch (Exception e) {
            assertFail("Lọc máy tr���ng", e.getMessage());
        } finally {
            logout();
        }

        // --- ✅ Test 2.2: NHANVIEN lấy máy trống ---
        try {
            loginNhanVien();
            List<MayTinh> list = mayTinhBUS.getMayTrong();
            if (list != null) {
                assertPass("NHANVIEN - Lấy máy trống thành công (" + list.size() + " máy)");
            } else {
                assertFail("NHANVIEN - Lấy máy trống", "Danh sách null");
            }
        } catch (Exception e) {
            assertFail("NHANVIEN - Lấy máy trống", e.getMessage());
        } finally {
            logout();
        }

        // --- ❌ Test 2.3: Chưa đăng nhập ---
        try {
            logout();
            mayTinhBUS.getMayTrong();
            assertFail("Chưa đăng nhập", "Không throw Exception");
        } catch (Exception e) {
            assertPass("Chưa đăng nhập → Exception: " + e.getMessage());
        }

        System.out.println();
    }

    // ============================================================
    // 3. TEST themMayTinh()
    // ============================================================
    private static void testThemMayTinh() {
        printTestHeader("3. TEST themMayTinh()");

        String maMayDaThemCoKhu = null;
        String maMayDaThemKhongKhu = null;

        // --- ✅ Test 3.1: Thêm máy thành công (có khu HOATDONG) ---
        try {
            loginQuanLy();
            MayTinh mayMoi = new MayTinh();
            mayMoi.setTenmay("MayTestCoKhu_" + System.currentTimeMillis());
            mayMoi.setMakhu(MA_KHU_HOATDONG);
            mayMoi.setCauhinh("i7/16GB/SSD512");
            mayMoi.setGiamoigio(15000.0);

            MayTinh result = mayTinhBUS.themMayTinh(mayMoi);
            if (result != null && result.getMamay() != null) {
                maMayDaThemCoKhu = result.getMamay();
                assertPass("Thêm máy có khu thành công (Mã: " + result.getMamay() + ")");
            } else {
                assertFail("Thêm máy có khu", "Kết quả null hoặc không có mã máy");
            }
        } catch (Exception e) {
            assertFail("Thêm máy có khu", e.getMessage());
        } finally {
            logout();
        }

        // --- ✅ Test 3.2: Thêm máy thành công (không có khu) ---
        try {
            loginQuanLy();
            MayTinh mayMoi = new MayTinh();
            mayMoi.setTenmay("MayTestKhongKhu_" + System.currentTimeMillis());
            mayMoi.setMakhu(null);
            mayMoi.setCauhinh("i5/8GB/SSD256");
            mayMoi.setGiamoigio(10000.0);

            MayTinh result = mayTinhBUS.themMayTinh(mayMoi);
            if (result != null && result.getMamay() != null) {
                maMayDaThemKhongKhu = result.getMamay();
                assertPass("Thêm máy không khu thành công (Mã: " + result.getMamay() + ")");
            } else {
                assertFail("Thêm máy không khu", "Kết quả null hoặc không có mã máy");
            }
        } catch (Exception e) {
            assertFail("Thêm máy không khu", e.getMessage());
        } finally {
            logout();
        }

        // --- ❌ Test 3.3: NHANVIEN thêm máy (không có quyền) ---
        try {
            loginNhanVien();
            MayTinh mayMoi = new MayTinh();
            mayMoi.setTenmay("MayTestNV_" + System.currentTimeMillis());
            mayMoi.setCauhinh("i5/8GB");
            mayMoi.setGiamoigio(10000.0);

            mayTinhBUS.themMayTinh(mayMoi);
            assertFail("NHANVIEN thêm máy", "Không throw Exception");
        } catch (Exception e) {
            assertPass("NHANVIEN thêm máy → Exception: " + e.getMessage());
        } finally {
            logout();
        }

        // --- ❌ Test 3.4: Dữ liệu null ---
        try {
            loginQuanLy();
            mayTinhBUS.themMayTinh(null);
            assertFail("Dữ liệu null", "Không throw Exception");
        } catch (Exception e) {
            assertPass("Dữ liệu null → Exception: " + e.getMessage());
        } finally {
            logout();
        }

        // --- ❌ Test 3.5: Khu máy không tồn tại ---
        try {
            loginQuanLy();
            MayTinh mayMoi = new MayTinh();
            mayMoi.setTenmay("MayTestKhuSai_" + System.currentTimeMillis());
            mayMoi.setMakhu("KHU999");
            mayMoi.setCauhinh("i5/8GB");
            mayMoi.setGiamoigio(10000.0);

            mayTinhBUS.themMayTinh(mayMoi);
            assertFail("Khu không tồn tại", "Không throw Exception");
        } catch (Exception e) {
            assertPass("Khu không tồn tại → Exception: " + e.getMessage());
        } finally {
            logout();
        }

        // --- ❌ Test 3.6: Khu máy đã NGUNG ---
        try {
            loginQuanLy();
            MayTinh mayMoi = new MayTinh();
            mayMoi.setTenmay("MayTestKhuNgung_" + System.currentTimeMillis());
            mayMoi.setMakhu(MA_KHU_NGUNG);
            mayMoi.setCauhinh("i5/8GB");
            mayMoi.setGiamoigio(10000.0);

            mayTinhBUS.themMayTinh(mayMoi);
            assertFail("Khu đã NGUNG", "Không throw Exception");
        } catch (Exception e) {
            assertPass("Khu đã NGUNG → Exception: " + e.getMessage());
        } finally {
            logout();
        }

        // --- ❌ Test 3.7: Chưa đăng nhập ---
        try {
            logout();
            MayTinh mayMoi = new MayTinh();
            mayMoi.setTenmay("MayTestNoLogin_" + System.currentTimeMillis());
            mayMoi.setCauhinh("i5/8GB");
            mayMoi.setGiamoigio(10000.0);

            mayTinhBUS.themMayTinh(mayMoi);
            assertFail("Chưa đăng nhập", "Không throw Exception");
        } catch (Exception e) {
            assertPass("Chưa đăng nhập → Exception: " + e.getMessage());
        }

        // --- 🧹 DỌN DẸP ---
        cleanupMay(maMayDaThemCoKhu);
        cleanupMay(maMayDaThemKhongKhu);

        System.out.println();
    }

    // ============================================================
    // 4. TEST suaMayTinh()
    // ============================================================
    private static void testSuaMayTinh() {
        printTestHeader("4. TEST suaMayTinh()");

        String maMayTestSua = null;

        // --- ✅ Test 4.1: Sửa máy TRONG thành công ---
        try {
            loginQuanLy();

            // Thêm máy test trước
            MayTinh mayTest = new MayTinh();
            mayTest.setTenmay("MayTestSua_" + System.currentTimeMillis());
            mayTest.setMakhu(MA_KHU_HOATDONG);
            mayTest.setCauhinh("i5/8GB");
            mayTest.setGiamoigio(10000.0);
            MayTinh mayDaThem = mayTinhBUS.themMayTinh(mayTest);
            maMayTestSua = mayDaThem.getMamay();

            // Sửa thông tin
            mayDaThem.setTenmay("MayTestSua_Updated_" + System.currentTimeMillis());
            mayDaThem.setCauhinh("i7/32GB/SSD1TB");
            mayDaThem.setGiamoigio(20000.0);
            mayDaThem.setTrangthai("TRONG");

            MayTinh result = mayTinhBUS.suaMayTinh(mayDaThem);
            if (result != null) {
                assertPass("Sửa máy TRONG thành công (Mã: " + result.getMamay() + ")");
            } else {
                assertFail("Sửa máy TRONG", "Kết quả null");
            }
        } catch (Exception e) {
            assertFail("Sửa máy TRONG", e.getMessage());
        } finally {
            logout();
        }

        // --- ❌ Test 4.2: NHANVIEN sửa máy (không có quyền) ---
        try {
            loginNhanVien();
            MayTinh maySua = new MayTinh();
            maySua.setMamay(MA_MAY_TRONG);
            maySua.setTenmay("Updated");
            maySua.setMakhu(MA_KHU_HOATDONG);
            maySua.setCauhinh("i7/16GB");
            maySua.setGiamoigio(15000.0);
            maySua.setTrangthai("TRONG");

            mayTinhBUS.suaMayTinh(maySua);
            assertFail("NHANVIEN sửa máy", "Không throw Exception");
        } catch (Exception e) {
            assertPass("NHANVIEN sửa máy → Exception: " + e.getMessage());
        } finally {
            logout();
        }

        // --- ❌ Test 4.3: Dữ liệu null ---
        try {
            loginQuanLy();
            mayTinhBUS.suaMayTinh(null);
            assertFail("Dữ liệu null", "Không throw Exception");
        } catch (Exception e) {
            assertPass("Dữ liệu null → Exception: " + e.getMessage());
        } finally {
            logout();
        }

        // --- ❌ Test 4.4: Mã máy rỗng ---
        try {
            loginQuanLy();
            MayTinh maySua = new MayTinh();
            maySua.setMamay("");
            maySua.setTenmay("Test");
            maySua.setGiamoigio(10000.0);

            mayTinhBUS.suaMayTinh(maySua);
            assertFail("Mã máy rỗng", "Không throw Exception");
        } catch (Exception e) {
            assertPass("Mã máy rỗng → Exception: " + e.getMessage());
        } finally {
            logout();
        }

        // --- ❌ Test 4.5: Máy không tồn tại ---
        try {
            loginQuanLy();
            MayTinh maySua = new MayTinh();
            maySua.setMamay("MAY999");
            maySua.setTenmay("Test");
            maySua.setMakhu(MA_KHU_HOATDONG);
            maySua.setCauhinh("i5/8GB");
            maySua.setGiamoigio(10000.0);
            maySua.setTrangthai("TRONG");

            mayTinhBUS.suaMayTinh(maySua);
            assertFail("Máy không tồn tại", "Không throw Exception");
        } catch (Exception e) {
            assertPass("Máy không tồn tại → Exception: " + e.getMessage());
        } finally {
            logout();
        }

        // --- ❌ Test 4.6: Máy đang DANGDUNG ---
        try {
            loginQuanLy();
            MayTinh maySua = new MayTinh();
            maySua.setMamay(MA_MAY_DANGDUNG);
            maySua.setTenmay("Updated");
            maySua.setMakhu(MA_KHU_HOATDONG);
            maySua.setCauhinh("i7/16GB");
            maySua.setGiamoigio(15000.0);
            maySua.setTrangthai("DANGDUNG");

            mayTinhBUS.suaMayTinh(maySua);
            assertFail("Máy đang DANGDUNG", "Không throw Exception");
        } catch (Exception e) {
            assertPass("Máy đang DANGDUNG → Exception: " + e.getMessage());
        } finally {
            logout();
        }

        // --- ❌ Test 4.7: Đổi sang khu NGUNG ---
        try {
            loginQuanLy();
            MayTinh maySua = new MayTinh();
            maySua.setMamay(MA_MAY_TRONG);
            maySua.setTenmay("Updated");
            maySua.setMakhu(MA_KHU_NGUNG);
            maySua.setCauhinh("i5/8GB");
            maySua.setGiamoigio(10000.0);
            maySua.setTrangthai("TRONG");

            mayTinhBUS.suaMayTinh(maySua);
            assertFail("Đổi sang khu NGUNG", "Không throw Exception");
        } catch (Exception e) {
            assertPass("Đổi sang khu NGUNG → Exception: " + e.getMessage());
        } finally {
            logout();
        }

        // --- ❌ Test 4.8: Chưa đăng nhập ---
        try {
            logout();
            MayTinh maySua = new MayTinh();
            maySua.setMamay(MA_MAY_TRONG);
            maySua.setTenmay("Updated");
            maySua.setGiamoigio(10000.0);

            mayTinhBUS.suaMayTinh(maySua);
            assertFail("Chưa đăng nhập", "Không throw Exception");
        } catch (Exception e) {
            assertPass("Chưa đăng nhập → Exception: " + e.getMessage());
        }

        // --- 🧹 DỌN DẸP ---
        cleanupMay(maMayTestSua);

        System.out.println();
    }

    // ============================================================
    // 5. TEST xoaMayTinh()
    // ============================================================
    private static void testXoaMayTinh() {
        printTestHeader("5. TEST xoaMayTinh()");

        // --- ✅ Test 5.1: Xóa máy TRONG thành công ---
        try {
            loginQuanLy();

            // Thêm máy test trước để xóa
            MayTinh mayTest = new MayTinh();
            mayTest.setTenmay("MayTestXoa_" + System.currentTimeMillis());
            mayTest.setMakhu(MA_KHU_HOATDONG);
            mayTest.setCauhinh("i5/8GB");
            mayTest.setGiamoigio(10000.0);
            MayTinh mayDaThem = mayTinhBUS.themMayTinh(mayTest);

            // Xóa máy vừa thêm
            boolean result = mayTinhBUS.xoaMayTinh(mayDaThem.getMamay());
            if (result) {
                assertPass("Xóa máy TRONG thành công (Mã: " + mayDaThem.getMamay() + ")");
            } else {
                assertFail("Xóa máy TRONG", "Kết quả false");
            }
        } catch (Exception e) {
            assertFail("Xóa máy TRONG", e.getMessage());
        } finally {
            logout();
        }

        // --- ❌ Test 5.2: NHANVIEN xóa máy (không có quyền) ---
        try {
            loginNhanVien();
            mayTinhBUS.xoaMayTinh(MA_MAY_TRONG);
            assertFail("NHANVIEN xóa máy", "Không throw Exception");
        } catch (Exception e) {
            assertPass("NHANVIEN xóa máy → Exception: " + e.getMessage());
        } finally {
            logout();
        }

        // --- ❌ Test 5.3: Mã máy null ---
        try {
            loginQuanLy();
            mayTinhBUS.xoaMayTinh(null);
            assertFail("Mã máy null", "Không throw Exception");
        } catch (Exception e) {
            assertPass("Mã máy null → Exception: " + e.getMessage());
        } finally {
            logout();
        }

        // --- ❌ Test 5.4: Mã máy rỗng ---
        try {
            loginQuanLy();
            mayTinhBUS.xoaMayTinh("  ");
            assertFail("Mã máy rỗng", "Không throw Exception");
        } catch (Exception e) {
            assertPass("Mã máy rỗng → Exception: " + e.getMessage());
        } finally {
            logout();
        }

        // --- ❌ Test 5.5: Máy không tồn tại ---
        try {
            loginQuanLy();
            mayTinhBUS.xoaMayTinh("MAY999");
            assertFail("Máy không tồn tại", "Không throw Exception");
        } catch (Exception e) {
            assertPass("Máy không tồn tại → Exception: " + e.getMessage());
        } finally {
            logout();
        }

        // --- ❌ Test 5.6: Máy đang DANGDUNG ---
        try {
            loginQuanLy();
            mayTinhBUS.xoaMayTinh(MA_MAY_DANGDUNG);
            assertFail("Máy đang DANGDUNG", "Không throw Exception");
        } catch (Exception e) {
            assertPass("Máy đang DANGDUNG → Exception: " + e.getMessage());
        } finally {
            logout();
        }

        // --- ❌ Test 5.7: Chưa đăng nhập ---
        try {
            logout();
            mayTinhBUS.xoaMayTinh(MA_MAY_TRONG);
            assertFail("Chưa đăng nhập", "Không throw Exception");
        } catch (Exception e) {
            assertPass("Chưa đăng nhập → Exception: " + e.getMessage());
        }

        System.out.println();
    }

    // ============================================================
    // 6. TEST chuyenTrangThai()
    // ============================================================
    private static void testChuyenTrangThai() {
        printTestHeader("6. TEST chuyenTrangThai()");

        String maMayTestTT = null;

        // --- Tạo máy test cho chuyển trạng thái ---
        try {
            loginQuanLy();
            MayTinh mayTest = new MayTinh();
            mayTest.setTenmay("MayTestTT_" + System.currentTimeMillis());
            mayTest.setMakhu(MA_KHU_HOATDONG);
            mayTest.setCauhinh("i5/8GB");
            mayTest.setGiamoigio(10000.0);
            MayTinh mayDaThem = mayTinhBUS.themMayTinh(mayTest);
            maMayTestTT = mayDaThem.getMamay();
            System.out.println("  🔧 Tạo máy test: " + maMayTestTT + " (TRONG)");
        } catch (Exception e) {
            System.out.println("  ⚠️ Không tạo được máy test: " + e.getMessage());
            System.out.println();
            return;
        } finally {
            logout();
        }

        // --- ✅ Test 6.1: TRONG → BAOTRI (QUANLY) ---
        try {
            loginQuanLy();
            boolean result = mayTinhBUS.chuyenTrangThai(maMayTestTT, "BAOTRI");
            if (result) {
                assertPass("TRONG → BAOTRI thành công (QUANLY, Mã: " + maMayTestTT + ")");
            } else {
                assertFail("TRONG → BAOTRI (QUANLY)", "Kết quả false");
            }
        } catch (Exception e) {
            assertFail("TRONG → BAOTRI (QUANLY)", e.getMessage());
        } finally {
            logout();
        }

        // --- ✅ Test 6.2: BAOTRI → TRONG (QUANLY) ---
        try {
            loginQuanLy();
            boolean result = mayTinhBUS.chuyenTrangThai(maMayTestTT, "TRONG");
            if (result) {
                assertPass("BAOTRI → TRONG thành công (QUANLY, Mã: " + maMayTestTT + ")");
            } else {
                assertFail("BAOTRI → TRONG (QUANLY)", "Kết quả false");
            }
        } catch (Exception e) {
            assertFail("BAOTRI → TRONG (QUANLY)", e.getMessage());
        } finally {
            logout();
        }

        // --- ✅ Test 6.3: TRONG → BAOTRI (NHANVIEN) ---
        try {
            loginNhanVien();
            boolean result = mayTinhBUS.chuyenTrangThai(maMayTestTT, "BAOTRI");
            if (result) {
                assertPass("TRONG → BAOTRI thành công (NHANVIEN)");
            } else {
                assertFail("TRONG → BAOTRI (NHANVIEN)", "Kết quả false");
            }
        } catch (Exception e) {
            assertFail("TRONG → BAOTRI (NHANVIEN)", e.getMessage());
        } finally {
            logout();
        }

        // --- ✅ Test 6.4: BAOTRI → TRONG (NHANVIEN) ---
        try {
            loginNhanVien();
            boolean result = mayTinhBUS.chuyenTrangThai(maMayTestTT, "TRONG");
            if (result) {
                assertPass("BAOTRI → TRONG thành công (NHANVIEN)");
            } else {
                assertFail("BAOTRI → TRONG (NHANVIEN)", "Kết quả false");
            }
        } catch (Exception e) {
            assertFail("BAOTRI → TRONG (NHANVIEN)", e.getMessage());
        } finally {
            logout();
        }

        // --- ❌ Test 6.5: Chuyển sang DANGDUNG (không cho phép) ---
        try {
            loginQuanLy();
            mayTinhBUS.chuyenTrangThai(maMayTestTT, "DANGDUNG");
            assertFail("Chuyển sang DANGDUNG", "Không throw Exception");
        } catch (Exception e) {
            assertPass("Chuyển sang DANGDUNG → Exception: " + e.getMessage());
        } finally {
            logout();
        }

        // --- ❌ Test 6.6: Chuyển từ DANGDUNG sang TRONG (không cho phép) ---
        try {
            loginQuanLy();
            mayTinhBUS.chuyenTrangThai(MA_MAY_DANGDUNG, "TRONG");
            assertFail("Chuyển từ DANGDUNG", "Không throw Exception");
        } catch (Exception e) {
            assertPass("Chuyển từ DANGDUNG → Exception: " + e.getMessage());
        } finally {
            logout();
        }

        // --- ❌ Test 6.7: Mã máy null ---
        try {
            loginQuanLy();
            mayTinhBUS.chuyenTrangThai(null, "BAOTRI");
            assertFail("Mã máy null", "Không throw Exception");
        } catch (Exception e) {
            assertPass("Mã máy null → Exception: " + e.getMessage());
        } finally {
            logout();
        }

        // --- ❌ Test 6.8: Trạng thái mới rỗng ---
        try {
            loginQuanLy();
            mayTinhBUS.chuyenTrangThai(maMayTestTT, "");
            assertFail("Trạng thái rỗng", "Không throw Exception");
        } catch (Exception e) {
            assertPass("Trạng thái rỗng → Exception: " + e.getMessage());
        } finally {
            logout();
        }

        // --- ❌ Test 6.9: Máy không tồn tại ---
        try {
            loginQuanLy();
            mayTinhBUS.chuyenTrangThai("MAY999", "BAOTRI");
            assertFail("Máy không tồn tại", "Không throw Exception");
        } catch (Exception e) {
            assertPass("Máy không tồn tại → Exception: " + e.getMessage());
        } finally {
            logout();
        }

        // --- ❌ Test 6.10: Chưa đăng nhập ---
        try {
            logout();
            mayTinhBUS.chuyenTrangThai(maMayTestTT, "BAOTRI");
            assertFail("Chưa đăng nhập", "Không throw Exception");
        } catch (Exception e) {
            assertPass("Chưa đăng nhập → Exception: " + e.getMessage());
        }

        // --- 🧹 DỌN DẸP ---
        cleanupMay(maMayTestTT);

        System.out.println();
    }

    // ============================================================
    // DỌN DẸP DỮ LIỆU TEST
    // ============================================================
    private static void cleanupMay(String maMay) {
        if (maMay == null) return;
        try {
            loginQuanLy();
            mayTinhBUS.xoaMayTinh(maMay);
            System.out.println("  🧹 Dọn dẹp: Đã xóa máy test " + maMay);
        } catch (Exception e) {
            System.out.println("  🧹 Dọn dẹp: " + maMay + " (" + e.getMessage() + ")");
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
        System.out.println("╠══════════════════════════════════════════════════╣");
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