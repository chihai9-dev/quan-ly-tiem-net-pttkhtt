package test.dao;

import dao.DBConnection;
import dao.ThongkeDAO;

import java.time.LocalDate;

public class TestThongkeDAO {
    public static void main(String[] args) {
        try {
            ThongkeDAO tkDAO = new ThongkeDAO();

            LocalDate to = LocalDate.now();
            LocalDate from = to.minusDays(30);

            double doanhThu = tkDAO.doanhThu(from, to);
            double tongNhap = tkDAO.tongNhapHang(from, to);

            System.out.println("=== THONG KE (" + from + " -> " + to + ") ===");
            System.out.println("Doanh thu (hoa don DATHANHTOAN): " + doanhThu);
            System.out.println("Tong nhap hang (phieu DANHAP): " + tongNhap);

        } finally {
            DBConnection.closeConnection();
        }
    }
}