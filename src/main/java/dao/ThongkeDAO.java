package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThongkeDAO {

    // ====== 20.2 Thống kê doanh thu tổng hợp trong khoảng ngày ======
    // Trả về: TongDoanhThu, TongTienGioChoi, TongTienDichVu, SoHoaDon
    public Map<String, Object> thongKeDoanhThuTongHop(LocalDate tuNgay, LocalDate denNgay) {
        String sql =
                "SELECT " +
                        "  COALESCE(SUM(ThanhToan),0)    AS TongDoanhThu, " +
                        "  COALESCE(SUM(TienGioChoi),0)  AS TongTienGioChoi, " +
                        "  COALESCE(SUM(TienDichVu),0)   AS TongTienDichVu, " +
                        "  COUNT(*)                      AS SoHoaDon " +
                        "FROM hoadon " +
                        "WHERE TrangThai='DATHANHTOAN' AND DATE(NgayLap) BETWEEN ? AND ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(tuNgay));
            ps.setDate(2, Date.valueOf(denNgay));

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();

                Map<String, Object> map = new HashMap<>();
                map.put("TongDoanhThu", rs.getDouble("TongDoanhThu"));
                map.put("TongTienGioChoi", rs.getDouble("TongTienGioChoi"));
                map.put("TongTienDichVu", rs.getDouble("TongTienDichVu"));
                map.put("SoHoaDon", rs.getInt("SoHoaDon"));
                return map;
            }

        } catch (SQLException e) {
            throw new RuntimeException("ThongkeDAO.thongKeDoanhThuTongHop error", e);
        }
    }

    // (phần bạn đã có) Tổng tiền nhập hàng (chỉ phiếu DANHAP) trong khoảng ngày
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

    // ====== 20.3 Top dịch vụ bán chạy trong khoảng ngày ======
    // Trả về list map: MaDV, TenDV, TongSoLuong, TongDoanhThu
    public List<Map<String, Object>> thongKeDichVuBanChay(LocalDate tuNgay, LocalDate denNgay, int top) {
        if (top <= 0) top = 10;

        String sql =
                "SELECT dv.MaDV, dv.TenDV, " +
                        "       COALESCE(SUM(sd.SoLuong),0)   AS TongSoLuong, " +
                        "       COALESCE(SUM(sd.ThanhTien),0) AS TongDoanhThu " +
                        "FROM sudungdichvu sd " +
                        "JOIN dichvu dv ON sd.MaDV = dv.MaDV " +
                        "JOIN phiensudung ps ON sd.MaPhien = ps.MaPhien " +
                        "WHERE ps.GioKetThuc IS NOT NULL AND DATE(ps.GioKetThuc) BETWEEN ? AND ? " +
                        "GROUP BY dv.MaDV, dv.TenDV " +
                        "ORDER BY TongSoLuong DESC " +
                        "LIMIT ?";

        List<Map<String, Object>> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(tuNgay));
            ps.setDate(2, Date.valueOf(denNgay));
            ps.setInt(3, top);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("MaDV", rs.getString("MaDV"));
                    row.put("TenDV", rs.getString("TenDV"));
                    row.put("TongSoLuong", rs.getInt("TongSoLuong"));
                    row.put("TongDoanhThu", rs.getDouble("TongDoanhThu"));
                    list.add(row);
                }
            }

            return list;

        } catch (SQLException e) {
            throw new RuntimeException("ThongkeDAO.thongKeDichVuBanChay error", e);
        }
    }

    // ====== 20.4 Thống kê tổng quan (Map) ======
    // Tổng số máy / đang dùng / trống, tổng KH / KH hoạt động, số phiên đang chơi,
    // doanh thu hôm nay, doanh thu tháng này
    public Map<String, Object> thongKeTongQuan() {
        Map<String, Object> map = new HashMap<>();

        String qTongMay = "SELECT COUNT(*) FROM maytinh";
        String qMayDangDung = "SELECT COUNT(*) FROM maytinh WHERE TrangThai='DANGDUNG'";
        String qMayTrong = "SELECT COUNT(*) FROM maytinh WHERE TrangThai='TRONG'";

        String qTongKH = "SELECT COUNT(*) FROM khachhang";
        String qKHHoatDong = "SELECT COUNT(*) FROM khachhang WHERE TrangThai='HOATDONG'";

        String qPhienDangChoi = "SELECT COUNT(*) FROM phiensudung WHERE TrangThai='DANGCHOI'";

        String qDoanhThuHomNay =
                "SELECT COALESCE(SUM(ThanhToan),0) FROM hoadon " +
                        "WHERE TrangThai='DATHANHTOAN' AND DATE(NgayLap)=CURDATE()";

        String qDoanhThuThangNay =
                "SELECT COALESCE(SUM(ThanhToan),0) FROM hoadon " +
                        "WHERE TrangThai='DATHANHTOAN' " +
                        "AND YEAR(NgayLap)=YEAR(CURDATE()) AND MONTH(NgayLap)=MONTH(CURDATE())";

        try (Connection conn = DBConnection.getConnection()) {
            map.put("TongSoMay", scalarInt(conn, qTongMay));
            map.put("SoMayDangDung", scalarInt(conn, qMayDangDung));
            map.put("SoMayTrong", scalarInt(conn, qMayTrong));

            map.put("TongSoKH", scalarInt(conn, qTongKH));
            map.put("SoKHHoatDong", scalarInt(conn, qKHHoatDong));

            map.put("SoPhienDangChoi", scalarInt(conn, qPhienDangChoi));

            map.put("DoanhThuHomNay", scalarDouble(conn, qDoanhThuHomNay));
            map.put("DoanhThuThangNay", scalarDouble(conn, qDoanhThuThangNay));

            return map;

        } catch (SQLException e) {
            throw new RuntimeException("ThongkeDAO.thongKeTongQuan error", e);
        }
    }

    public List<Map<String, Object>> thongKeTheo12Thang(int nam) {
        Map<Integer, Double> doanhThuMap = new HashMap<>();
        Map<Integer, Double> chiPhiMap = new HashMap<>();

        String sqlDoanhThu =
                "SELECT MONTH(NgayLap) AS Thang, COALESCE(SUM(ThanhToan),0) AS TongDoanhThu " +
                        "FROM hoadon " +
                        "WHERE TrangThai='DATHANHTOAN' AND YEAR(NgayLap)=? " +
                        "GROUP BY MONTH(NgayLap)";

        String sqlChiPhi =
                "SELECT MONTH(NgayNhap) AS Thang, COALESCE(SUM(TongTien),0) AS TongChiPhi " +
                        "FROM phieunhaphang " +
                        "WHERE TrangThai='DANHAP' AND YEAR(NgayNhap)=? " +
                        "GROUP BY MONTH(NgayNhap)";

        try (Connection conn = DBConnection.getConnection()) {

            try (PreparedStatement ps = conn.prepareStatement(sqlDoanhThu)) {
                ps.setInt(1, nam);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        doanhThuMap.put(rs.getInt("Thang"), rs.getDouble("TongDoanhThu"));
                    }
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(sqlChiPhi)) {
                ps.setInt(1, nam);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        chiPhiMap.put(rs.getInt("Thang"), rs.getDouble("TongChiPhi"));
                    }
                }
            }

            List<Map<String, Object>> list = new ArrayList<>();
            for (int thang = 1; thang <= 12; thang++) {
                double thu = doanhThuMap.getOrDefault(thang, 0.0);
                double chi = chiPhiMap.getOrDefault(thang, 0.0);
                double loiNhuan = thu - chi;

                Map<String, Object> row = new HashMap<>();
                row.put("Thang", thang);
                row.put("ThoiGian", "Tháng " + thang + "/" + nam);
                row.put("TongDoanhThu", thu);
                row.put("TongNhapHang", chi);
                row.put("LoiNhuan", loiNhuan);

                list.add(row);
            }

            return list;

        } catch (SQLException e) {
            throw new RuntimeException("ThongkeDAO.thongKeTheo12Thang error", e);
        }
    }

    private int scalarInt(Connection conn, String sql) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }

    private double scalarDouble(Connection conn, String sql) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getDouble(1);
        }
    }
}


