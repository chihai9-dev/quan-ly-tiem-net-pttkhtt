package dao;

import java.sql.*;
import java.time.LocalDate;

public class ThongkeDAO {

    // Doanh thu hóa đơn (đã thanh toán) theo ngày
    public double doanhThu(LocalDate from, LocalDate to) {
        String sql = "SELECT COALESCE(SUM(ThanhToan),0) FROM hoadon " +
                "WHERE TrangThai='DATHANHTOAN' AND DATE(NgayLap) BETWEEN ? AND ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(from));
            ps.setDate(2, Date.valueOf(to));

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getDouble(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException("ThongkeDAO.doanhThu error", e);
        }
    }

    // Tổng tiền nhập hàng (chỉ phiếu DANHAP) theo ngày
    public double tongNhapHang(LocalDate from, LocalDate to) {
        String sql = "SELECT COALESCE(SUM(TongTien),0) FROM phieunhaphang " +
                "WHERE TrangThai='DANHAP' AND DATE(NgayNhap) BETWEEN ? AND ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(from));
            ps.setDate(2, Date.valueOf(to));

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getDouble(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException("ThongkeDAO.tongNhapHang error", e);
        }
    }
}

