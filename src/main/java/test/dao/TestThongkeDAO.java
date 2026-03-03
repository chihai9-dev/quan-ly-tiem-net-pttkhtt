package test.dao;

import dao.DBConnection;
import dao.ThongkeDAO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class TestThongkeDAO {
    public static void main(String[] args) {
        try {
            ThongkeDAO dao = new ThongkeDAO();

            LocalDate den = LocalDate.now();
            LocalDate tu = den.minusDays(30);

            System.out.println("=== TEST thongKeDoanhThuTongHop (" + tu + " -> " + den + ") ===");
            Map<String, Object> tongHop = dao.thongKeDoanhThuTongHop(tu, den);
            System.out.println("TongDoanhThu   = " + tongHop.get("TongDoanhThu"));
            System.out.println("TongTienGioChoi= " + tongHop.get("TongTienGioChoi"));
            System.out.println("TongTienDichVu = " + tongHop.get("TongTienDichVu"));
            System.out.println("SoHoaDon       = " + tongHop.get("SoHoaDon"));

            double tongNhap = dao.tongNhapHang(tu, den);
            System.out.println("TongNhapHang (phieu DANHAP) = " + tongNhap);

            System.out.println("\n=== TEST thongKeDichVuBanChay top 5 ===");
            List<Map<String, Object>> top = dao.thongKeDichVuBanChay(tu, den, 5);
            for (Map<String, Object> r : top) {
                System.out.println(
                        r.get("MaDV") + " | " + r.get("TenDV")
                                + " | SL=" + r.get("TongSoLuong")
                                + " | DT=" + r.get("TongDoanhThu")
                );
            }

            System.out.println("\n=== TEST thongKeTongQuan ===");
            Map<String, Object> tq = dao.thongKeTongQuan();
            System.out.println(tq);

            System.out.println("\n=== TEST ThongkeDAO OK ===");

        } finally {
            DBConnection.closeConnection();
        }
    }
}
