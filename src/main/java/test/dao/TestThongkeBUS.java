package test.dao;

import bus.ThongKeBUS;
import dao.DBConnection;
import entity.NhanVien;
import utils.SessionManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class TestThongkeBUS {

    private static void loginAs(String maNV, String chucVu) {
        NhanVien nv = new NhanVien();
        nv.setManv(maNV);
        nv.setChucvu(chucVu); // "QUANLY" hoặc "NHANVIEN"
        SessionManager.setCurrentUser(nv);
    }

    private static void printMap(String title, Map<String, Object> map) {
        System.out.println("----- " + title + " -----");
        for (Map.Entry<String, Object> e : map.entrySet()) {
            System.out.println(e.getKey() + " = " + e.getValue());
        }
    }

    public static void main(String[] args) {
        try {
            ThongKeBUS bus = new ThongKeBUS();

            LocalDate den = LocalDate.now();
            LocalDate tu = den.minusDays(30);

            System.out.println("=== CASE 1: Login QUANLY -> chạy full chức năng ===");
            loginAs("NV_TEST_QUANLY", "QUANLY");

            try {
                Map<String, Object> doanhThu = bus.thongKeDoanhThu(tu, den);
                printMap("thongKeDoanhThu(" + tu + " -> " + den + ")", doanhThu);
                System.out.println(" thongKeDoanhThu OK");
            } catch (Exception e) {
                System.out.println(" thongKeDoanhThu FAIL: " + e.getMessage());
            }

            try {
                Map<String, Object> theoThang = bus.thongKeDoanhThuTheoThang(den.getMonthValue(), den.getYear());
                printMap("thongKeDoanhThuTheoThang(" + den.getMonthValue() + "/" + den.getYear() + ")", theoThang);
                System.out.println(" thongKeDoanhThuTheoThang OK");
            } catch (Exception e) {
                System.out.println(" thongKeDoanhThuTheoThang FAIL: " + e.getMessage());
            }

            try {
                List<Map<String, Object>> top = bus.thongKeDichVuBanChay(tu, den, 5);
                System.out.println("----- thongKeDichVuBanChay top 5 -----");
                for (Map<String, Object> r : top) {
                    System.out.println(
                            r.get("MaDV") + " | " + r.get("TenDV")
                                    + " | SL=" + r.get("TongSoLuong")
                                    + " | DT=" + r.get("TongDoanhThu")
                    );
                }
                System.out.println(" thongKeDichVuBanChay OK");
            } catch (Exception e) {
                System.out.println(" thongKeDichVuBanChay FAIL: " + e.getMessage());
            }

            try {
                Map<String, Object> tq = bus.thongKeTongQuan();
                printMap("thongKeTongQuan()", tq);
                System.out.println("thongKeTongQuan OK");
            } catch (Exception e) {
                System.out.println("thongKeTongQuan FAIL: " + e.getMessage());
            }

            System.out.println("\n=== CASE 2: Validate ngày sai (tu > den) -> phải FAIL đúng nghiệp vụ ===");
            try {
                bus.thongKeDoanhThu(den, tu);
                System.out.println("FAIL: đáng lẽ phải throw do tuNgay > denNgay");
            } catch (Exception e) {
                System.out.println("OK: throw đúng nghiệp vụ: " + e.getMessage());
            }

            System.out.println("\n=== CASE 3: Login NHANVIEN -> chỉ xem thongKeTongQuan ===");
            loginAs("NV_TEST_NHANVIEN", "NHANVIEN");

            try {
                Map<String, Object> tq2 = bus.thongKeTongQuan();
                printMap("thongKeTongQuan() - NHANVIEN", tq2);
                System.out.println(" thongKeTongQuan (NHANVIEN) OK");
            } catch (Exception e) {
                System.out.println(" thongKeTongQuan (NHANVIEN) FAIL: " + e.getMessage());
            }

            try {
                bus.thongKeDoanhThu(tu, den);
                System.out.println(" FAIL: NHANVIEN không được gọi thongKeDoanhThu");
            } catch (Exception e) {
                System.out.println(" OK: NHANVIEN bị chặn đúng nghiệp vụ: " + e.getMessage());
            }

            try {
                bus.thongKeDichVuBanChay(tu, den, 5);
                System.out.println("FAIL: NHANVIEN không được gọi thongKeDichVuBanChay");
            } catch (Exception e) {
                System.out.println("OK: NHANVIEN bị chặn đúng nghiệp vụ: " + e.getMessage());
            }

            System.out.println("\n=== TEST ThongkeBUS DONE ===");

        } finally {
            SessionManager.logout();
            DBConnection.closeConnection();
        }
    }
}