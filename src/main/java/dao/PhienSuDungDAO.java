package dao;

import entity.PhienSuDung;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * PhienSuDungDAO - Data Access Object cho bảng phiensudung
 */
public class PhienSuDungDAO {

    public PhienSuDungDAO() {}

    // ============== CRUD ==============

    public ArrayList<PhienSuDung> getAll() throws SQLException {
        ArrayList<PhienSuDung> list = new ArrayList<>();
        String sql = "SELECT * FROM phiensudung ORDER BY gioBatDau DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public PhienSuDung getByMaPhien(String maPhien) throws SQLException {
        String sql = "SELECT * FROM phiensudung WHERE maPhien = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, maPhien);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    /**
     * INSERT - params thứ tự: maPhien, maKH, maMay, maNV, maGoiKH,
     *          gioBatDau, gioKetThuc, tongGio, gioSuDungTuGoi, gioSuDungTuTaiKhoan,
     *          giaMoiGio, tienGioChoi, loaiThanhToan, trangThai  (14 params)
     */
    public boolean insert(PhienSuDung phien) throws SQLException {
        String sql = "INSERT INTO phiensudung " +
                "(maPhien, maKH, maMay, maNV, maGoiKH, " +
                "gioBatDau, gioKetThuc, tongGio, gioSuDungTuGoi, gioSuDungTuTaiKhoan, " +
                "giaMoiGio, tienGioChoi, loaiThanhToan, trangThai) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            setInsertParams(pst, phien);   // index 1-14 bắt đầu từ maPhien
            return pst.executeUpdate() > 0;
        }
    }


    public boolean update(PhienSuDung phien) throws SQLException {
        String sql = "UPDATE phiensudung SET " +
                "maKH = ?, maMay = ?, maNV = ?, maGoiKH = ?, " +
                "gioBatDau = ?, gioKetThuc = ?, tongGio = ?, " +
                "gioSuDungTuGoi = ?, gioSuDungTuTaiKhoan = ?, " +
                "giaMoiGio = ?, tienGioChoi = ?, loaiThanhToan = ?, trangThai = ? " +
                "WHERE maPhien = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            setUpdateParams(pst, phien);   // index 1-13 = SET fields, index 14 = WHERE
            return pst.executeUpdate() > 0;
        }
    }

    public boolean delete(String maPhien) throws SQLException {
        String sql = "DELETE FROM phiensudung WHERE maPhien = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, maPhien);
            return pst.executeUpdate() > 0;
        }
    }

    // ============== BUSINESS QUERIES ==============

    public PhienSuDung getPhienDangChoiByKhachHang(String maKH) throws SQLException {
        String sql = "SELECT * FROM phiensudung WHERE maKH = ? AND trangThai = 'DANGCHOI' LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, maKH);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public PhienSuDung getPhienDangChoiByMay(String maMay) throws SQLException {
        String sql = "SELECT * FROM phiensudung WHERE maMay = ? AND trangThai = 'DANGCHOI' LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, maMay);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public ArrayList<PhienSuDung> getAllPhienDangChoi() throws SQLException {
        ArrayList<PhienSuDung> list = new ArrayList<>();
        String sql = "SELECT * FROM phiensudung WHERE trangThai = 'DANGCHOI' ORDER BY gioBatDau DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public ArrayList<PhienSuDung> getPhienByKhachHang(String maKH) throws SQLException {
        ArrayList<PhienSuDung> list = new ArrayList<>();
        String sql = "SELECT * FROM phiensudung WHERE maKH = ? ORDER BY gioBatDau DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, maKH);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public ArrayList<PhienSuDung> getPhienByMay(String maMay) throws SQLException {
        ArrayList<PhienSuDung> list = new ArrayList<>();
        String sql = "SELECT * FROM phiensudung WHERE maMay = ? ORDER BY gioBatDau DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, maMay);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public ArrayList<PhienSuDung> getPhienByDateRange(
            LocalDateTime tuNgay, LocalDateTime denNgay) throws SQLException {
        ArrayList<PhienSuDung> list = new ArrayList<>();
        String sql = "SELECT * FROM phiensudung WHERE gioBatDau BETWEEN ? AND ? ORDER BY gioBatDau DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setTimestamp(1, Timestamp.valueOf(tuNgay));
            pst.setTimestamp(2, Timestamp.valueOf(denNgay));
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public ArrayList<PhienSuDung> getPhienDaKetThucByDateRange(
            LocalDateTime tuNgay, LocalDateTime denNgay) throws SQLException {
        ArrayList<PhienSuDung> list = new ArrayList<>();
        String sql = "SELECT * FROM phiensudung WHERE trangThai = 'DAKETTHUC' " +
                "AND gioKetThuc BETWEEN ? AND ? ORDER BY gioKetThuc DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setTimestamp(1, Timestamp.valueOf(tuNgay));
            pst.setTimestamp(2, Timestamp.valueOf(denNgay));
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }


    public boolean ketThucPhien(String maPhien, LocalDateTime gioKetThuc, double tongGio,
                                double gioSuDungTuGoi, double gioSuDungTuTaiKhoan,
                                double tienGioChoi) throws SQLException {
        String sql = "UPDATE phiensudung SET " +
                "gioKetThuc = ?, tongGio = ?, gioSuDungTuGoi = ?, " +
                "gioSuDungTuTaiKhoan = ?, tienGioChoi = ?, trangThai = 'DAKETTHUC' " +
                "WHERE maPhien = ? AND trangThai = 'DANGCHOI'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setTimestamp(1, Timestamp.valueOf(gioKetThuc));
            pst.setDouble(2, tongGio);
            pst.setDouble(3, gioSuDungTuGoi);
            pst.setDouble(4, gioSuDungTuTaiKhoan);
            pst.setDouble(5, tienGioChoi);
            pst.setString(6, maPhien);
            return pst.executeUpdate() > 0;
        }
    }

    // ============== STATISTICS ==============

    public boolean hasPhienDangChoi(String maKH) throws SQLException {
        return getPhienDangChoiByKhachHang(maKH) != null;
    }

    public boolean isMayDangSuDung(String maMay) throws SQLException {
        return getPhienDangChoiByMay(maMay) != null;
    }

    public String generateMaPhien() {
        String sql = "SELECT MaPhien FROM phiensudung ORDER BY MaPhien DESC LIMIT 1";

        try (
                Connection conn = DBConnection.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)
        ) {
            if (rs.next()) {
                String lastMa = rs.getString("MaPhien");
                int num = Integer.parseInt(lastMa.substring(2));
                return String.format("PS%03d", num + 1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "PS001";  // Nếu chưa có dữ liệu
    }


    public boolean isMaPhienExists(String maPhien) throws SQLException {
        return getByMaPhien(maPhien) != null;
    }

    public int countByTrangThai(String trangThai) throws SQLException {
        String sql = "SELECT COUNT(*) FROM phiensudung WHERE trangThai = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, trangThai);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    public double getTongGioChoiByKhachHang(String maKH) throws SQLException {
        String sql = "SELECT SUM(tongGio) FROM phiensudung WHERE maKH = ? AND trangThai = 'DAKETTHUC'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, maKH);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        }
        return 0.0;
    }

    public double getTongDoanhThuGioChoi(LocalDateTime tuNgay, LocalDateTime denNgay) throws SQLException {
        String sql = "SELECT SUM(tienGioChoi) FROM phiensudung " +
                "WHERE trangThai = 'DAKETTHUC' AND gioKetThuc BETWEEN ? AND ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setTimestamp(1, Timestamp.valueOf(tuNgay));
            pst.setTimestamp(2, Timestamp.valueOf(denNgay));
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        }
        return 0.0;
    }

    // ============== PRIVATE HELPERS ==============

    /** Map ResultSet → PhienSuDung */
    private PhienSuDung mapRow(ResultSet rs) throws SQLException {
        PhienSuDung p = new PhienSuDung();
        p.setMaPhien(rs.getString("maPhien"));
        p.setMaKH(rs.getString("maKH"));
        p.setMaMay(rs.getString("maMay"));
        p.setMaNV(rs.getString("maNV"));
        p.setMaGoiKH(rs.getString("maGoiKH"));

        Timestamp gioBD = rs.getTimestamp("gioBatDau");
        if (gioBD != null) p.setGioBatDau(gioBD.toLocalDateTime());

        Timestamp gioKT = rs.getTimestamp("gioKetThuc");
        if (gioKT != null) p.setGioKetThuc(gioKT.toLocalDateTime());

        p.setTongGio(rs.getDouble("tongGio"));
        p.setGioSuDungTuGoi(rs.getDouble("gioSuDungTuGoi"));
        p.setGioSuDungTuTaiKhoan(rs.getDouble("gioSuDungTuTaiKhoan"));
        p.setGiaMoiGio(rs.getDouble("giaMoiGio"));
        p.setTienGioChoi(rs.getDouble("tienGioChoi"));
        p.setLoaiThanhToan(rs.getString("loaiThanhToan"));
        p.setTrangThai(rs.getString("trangThai"));
        return p;
    }


    private void setInsertParams(PreparedStatement pst, PhienSuDung p) throws SQLException {
        pst.setString(1,  p.getMaPhien());
        pst.setString(2,  p.getMaKH());
        pst.setString(3,  p.getMaMay());
        pst.setString(4,  p.getMaNV());
        pst.setString(5,  p.getMaGoiKH());
        pst.setTimestamp(6,  p.getGioBatDau()  != null ? Timestamp.valueOf(p.getGioBatDau())  : null);
        pst.setTimestamp(7,  p.getGioKetThuc() != null ? Timestamp.valueOf(p.getGioKetThuc()) : null);
        pst.setDouble(8,  p.getTongGio());
        pst.setDouble(9,  p.getGioSuDungTuGoi());
        pst.setDouble(10, p.getGioSuDungTuTaiKhoan());
        pst.setDouble(11, p.getGiaMoiGio());
        pst.setDouble(12, p.getTienGioChoi());
        pst.setString(13, p.getLoaiThanhToan());
        pst.setString(14, p.getTrangThai());
    }


    private void setUpdateParams(PreparedStatement pst, PhienSuDung p) throws SQLException {
        pst.setString(1,  p.getMaKH());
        pst.setString(2,  p.getMaMay());
        pst.setString(3,  p.getMaNV());
        pst.setString(4,  p.getMaGoiKH());
        pst.setTimestamp(5,  p.getGioBatDau()  != null ? Timestamp.valueOf(p.getGioBatDau())  : null);
        pst.setTimestamp(6,  p.getGioKetThuc() != null ? Timestamp.valueOf(p.getGioKetThuc()) : null);
        pst.setDouble(7,  p.getTongGio());
        pst.setDouble(8,  p.getGioSuDungTuGoi());
        pst.setDouble(9,  p.getGioSuDungTuTaiKhoan());
        pst.setDouble(10, p.getGiaMoiGio());
        pst.setDouble(11, p.getTienGioChoi());
        pst.setString(12, p.getLoaiThanhToan());
        pst.setString(13, p.getTrangThai());
        pst.setString(14, p.getMaPhien());  // WHERE clause
    }
}
