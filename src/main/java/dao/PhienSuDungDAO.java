package dao;

import entity.PhienSuDung;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * PhienSuDungDAO - Data Access Object cho bảng phiensudung
 *
 * Class này xử lý tất cả các thao tác CRUD với database cho PhienSuDung.
 * Bao gồm: tạo phiên mới, kết thúc phiên, cập nhật thông tin, truy vấn...
 *
 * @author QuanLyTiemNet Team
 * @version 1.0
 * @since 2026-02-03
 */
public class PhienSuDungDAO {

    // ============== CONSTRUCTOR ==============

    /**
     * Constructor mặc định
     */
    public PhienSuDungDAO() {
        // Constructor công khai - có thể tạo nhiều instance
    }

    // ============== CRUD METHODS ==============

    /**
     * Lấy tất cả phiên sử dụng
     *
     * @return Danh sách tất cả phiên sử dụng
     * @throws SQLException nếu có lỗi database
     */
    public ArrayList<PhienSuDung> getAll() throws SQLException {
        ArrayList<PhienSuDung> list = new ArrayList<>();
        String sql = "SELECT * FROM phiensudung ORDER BY gioBatDau DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToPhienSuDung(rs));
            }
        }

        return list;
    }

    /**
     * Lấy phiên sử dụng theo mã phiên
     *
     * @param maPhien Mã phiên cần tìm
     * @return PhienSuDung hoặc null nếu không tìm thấy
     * @throws SQLException nếu có lỗi database
     */
    public PhienSuDung getByMaPhien(String maPhien) throws SQLException {
        String sql = "SELECT * FROM phiensudung WHERE maPhien = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, maPhien);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPhienSuDung(rs);
                }
            }
        }

        return null;
    }

    /**
     * Thêm phiên sử dụng mới
     *
     * @param phien PhienSuDung cần thêm
     * @return true nếu thêm thành công
     * @throws SQLException nếu có lỗi database
     */
    public boolean insert(PhienSuDung phien) throws SQLException {
        String sql = "INSERT INTO phiensudung (maPhien, maKH, maMay, maNV, maGoiKH, " +
                "gioBatDau, gioKetThuc, tongGio, gioSuDungTuGoi, gioSuDungTuTaiKhoan, " +
                "giaMoiGio, tienGioChoi, loaiThanhToan, trangThai) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            setPhienSuDungParameters(pst, phien);

            return pst.executeUpdate() > 0;
        }
    }

    /**
     * Cập nhật phiên sử dụng
     *
     * @param phien PhienSuDung cần cập nhật
     * @return true nếu cập nhật thành công
     * @throws SQLException nếu có lỗi database
     */
    public boolean update(PhienSuDung phien) throws SQLException {
        String sql = "UPDATE phiensudung SET maKH = ?, maMay = ?, maNV = ?, maGoiKH = ?, " +
                "gioBatDau = ?, gioKetThuc = ?, tongGio = ?, gioSuDungTuGoi = ?, " +
                "gioSuDungTuTaiKhoan = ?, giaMoiGio = ?, tienGioChoi = ?, " +
                "loaiThanhToan = ?, trangThai = ? WHERE maPhien = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            setPhienSuDungParameters(pst, phien);
            pst.setString(14, phien.getMaPhien());

            return pst.executeUpdate() > 0;
        }
    }

    /**
     * Xóa phiên sử dụng
     *
     * @param maPhien Mã phiên cần xóa
     * @return true nếu xóa thành công
     * @throws SQLException nếu có lỗi database
     */
    public boolean delete(String maPhien) throws SQLException {
        String sql = "DELETE FROM phiensudung WHERE maPhien = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, maPhien);

            return pst.executeUpdate() > 0;
        }
    }

    // ============== BUSINESS QUERY METHODS ==============

    /**
     * Lấy phiên đang chơi của khách hàng
     *
     * @param maKH Mã khách hàng
     * @return PhienSuDung đang chơi hoặc null
     * @throws SQLException nếu có lỗi database
     */
    public PhienSuDung getPhienDangChoiByKhachHang(String maKH) throws SQLException {
        String sql = "SELECT * FROM phiensudung WHERE maKH = ? AND trangThai = 'DANGCHOI'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, maKH);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPhienSuDung(rs);
                }
            }
        }

        return null;
    }

    /**
     * Lấy phiên đang chơi trên máy
     *
     * @param maMay Mã máy tính
     * @return PhienSuDung đang chơi hoặc null
     * @throws SQLException nếu có lỗi database
     */
    public PhienSuDung getPhienDangChoiByMay(String maMay) throws SQLException {
        String sql = "SELECT * FROM phiensudung WHERE maMay = ? AND trangThai = 'DANGCHOI'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, maMay);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPhienSuDung(rs);
                }
            }
        }

        return null;
    }

    /**
     * Lấy tất cả phiên đang chơi
     *
     * @return Danh sách các phiên đang chơi
     * @throws SQLException nếu có lỗi database
     */
    public ArrayList<PhienSuDung> getAllPhienDangChoi() throws SQLException {
        ArrayList<PhienSuDung> list = new ArrayList<>();
        String sql = "SELECT * FROM phiensudung WHERE trangThai = 'DANGCHOI' ORDER BY gioBatDau DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToPhienSuDung(rs));
            }
        }

        return list;
    }

    /**
     * Lấy lịch sử phiên của khách hàng
     *
     * @param maKH Mã khách hàng
     * @return Danh sách phiên của khách hàng
     * @throws SQLException nếu có lỗi database
     */
    public ArrayList<PhienSuDung> getPhienByKhachHang(String maKH) throws SQLException {
        ArrayList<PhienSuDung> list = new ArrayList<>();
        String sql = "SELECT * FROM phiensudung WHERE maKH = ? ORDER BY gioBatDau DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, maKH);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToPhienSuDung(rs));
                }
            }
        }

        return list;
    }

    /**
     * Lấy lịch sử phiên theo máy
     *
     * @param maMay Mã máy tính
     * @return Danh sách phiên trên máy
     * @throws SQLException nếu có lỗi database
     */
    public ArrayList<PhienSuDung> getPhienByMay(String maMay) throws SQLException {
        ArrayList<PhienSuDung> list = new ArrayList<>();
        String sql = "SELECT * FROM phiensudung WHERE maMay = ? ORDER BY gioBatDau DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, maMay);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToPhienSuDung(rs));
                }
            }
        }

        return list;
    }

    /**
     * Lấy phiên theo khoảng thời gian
     *
     * @param tuNgay Từ ngày
     * @param denNgay Đến ngày
     * @return Danh sách phiên trong khoảng thời gian
     * @throws SQLException nếu có lỗi database
     */
    public ArrayList<PhienSuDung> getPhienByDateRange(LocalDateTime tuNgay, LocalDateTime denNgay) throws SQLException {
        ArrayList<PhienSuDung> list = new ArrayList<>();
        String sql = "SELECT * FROM phiensudung WHERE gioBatDau BETWEEN ? AND ? ORDER BY gioBatDau DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setTimestamp(1, Timestamp.valueOf(tuNgay));
            pst.setTimestamp(2, Timestamp.valueOf(denNgay));

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToPhienSuDung(rs));
                }
            }
        }

        return list;
    }

    /**
     * Lấy phiên đã kết thúc trong khoảng thời gian
     *
     * @param tuNgay Từ ngày
     * @param denNgay Đến ngày
     * @return Danh sách phiên đã kết thúc
     * @throws SQLException nếu có lỗi database
     */
    public ArrayList<PhienSuDung> getPhienDaKetThucByDateRange(LocalDateTime tuNgay, LocalDateTime denNgay) throws SQLException {
        ArrayList<PhienSuDung> list = new ArrayList<>();
        String sql = "SELECT * FROM phiensudung WHERE trangThai = 'DAKETTHUC' " +
                "AND gioKetThuc BETWEEN ? AND ? ORDER BY gioKetThuc DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setTimestamp(1, Timestamp.valueOf(tuNgay));
            pst.setTimestamp(2, Timestamp.valueOf(denNgay));

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToPhienSuDung(rs));
                }
            }
        }

        return list;
    }

    /**
     * Kết thúc phiên - cập nhật trạng thái và thời gian kết thúc
     *
     * @param maPhien Mã phiên cần kết thúc
     * @param gioKetThuc Thời gian kết thúc
     * @param tongGio Tổng giờ chơi
     * @param gioSuDungTuGoi Giờ sử dụng từ gói
     * @param gioSuDungTuTaiKhoan Giờ sử dụng từ tài khoản
     * @param tienGioChoi Tiền giờ chơi
     * @return true nếu kết thúc thành công
     * @throws SQLException nếu có lỗi database
     */
    public boolean ketThucPhien(String maPhien, LocalDateTime gioKetThuc, double tongGio,
                                double gioSuDungTuGoi, double gioSuDungTuTaiKhoan, double tienGioChoi) throws SQLException {
        String sql = "UPDATE phiensudung SET gioKetThuc = ?, tongGio = ?, " +
                "gioSuDungTuGoi = ?, gioSuDungTuTaiKhoan = ?, tienGioChoi = ?, " +
                "trangThai = 'DAKETTHUC' WHERE maPhien = ?";

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

    // ============== STATISTICS METHODS ==============

    /**
     * Đếm số phiên theo trạng thái
     *
     * @param trangThai Trạng thái cần đếm
     * @return Số lượng phiên
     * @throws SQLException nếu có lỗi database
     */
    public int countByTrangThai(String trangThai) throws SQLException {
        String sql = "SELECT COUNT(*) FROM phiensudung WHERE trangThai = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, trangThai);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return 0;
    }

    /**
     * Tính tổng giờ chơi của khách hàng
     *
     * @param maKH Mã khách hàng
     * @return Tổng giờ chơi
     * @throws SQLException nếu có lỗi database
     */
    public double getTongGioChoiByKhachHang(String maKH) throws SQLException {
        String sql = "SELECT SUM(tongGio) FROM phiensudung WHERE maKH = ? AND trangThai = 'DAKETTHUC'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, maKH);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        }

        return 0.0;
    }

    /**
     * Tính tổng doanh thu từ giờ chơi trong khoảng thời gian
     *
     * @param tuNgay Từ ngày
     * @param denNgay Đến ngày
     * @return Tổng doanh thu
     * @throws SQLException nếu có lỗi database
     */
    public double getTongDoanhThuGioChoi(LocalDateTime tuNgay, LocalDateTime denNgay) throws SQLException {
        String sql = "SELECT SUM(tienGioChoi) FROM phiensudung " +
                "WHERE trangThai = 'DAKETTHUC' AND gioKetThuc BETWEEN ? AND ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setTimestamp(1, Timestamp.valueOf(tuNgay));
            pst.setTimestamp(2, Timestamp.valueOf(denNgay));

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        }

        return 0.0;
    }

    /**
     * Kiểm tra khách hàng có phiên đang chơi không
     *
     * @param maKH Mã khách hàng
     * @return true nếu có phiên đang chơi
     * @throws SQLException nếu có lỗi database
     */
    public boolean hasPhienDangChoi(String maKH) throws SQLException {
        return getPhienDangChoiByKhachHang(maKH) != null;
    }

    /**
     * Kiểm tra máy có phiên đang chơi không
     *
     * @param maMay Mã máy
     * @return true nếu có phiên đang chơi
     * @throws SQLException nếu có lỗi database
     */
    public boolean isMayDangSuDung(String maMay) throws SQLException {
        return getPhienDangChoiByMay(maMay) != null;
    }

    /**
     * Sinh mã phiên tự động
     * Format: PS + timestamp
     *
     * @return Mã phiên mới
     */
    public String generateMaPhien() {
        return "PS" + System.currentTimeMillis();
    }

    /**
     * Kiểm tra mã phiên đã tồn tại chưa
     *
     * @param maPhien Mã phiên cần kiểm tra
     * @return true nếu đã tồn tại
     * @throws SQLException nếu có lỗi database
     */
    public boolean isMaPhienExists(String maPhien) throws SQLException {
        return getByMaPhien(maPhien) != null;
    }



    /*

     */
    // ============== HELPER METHODS ==============

    /**
     * Map ResultSet sang PhienSuDung object
     */
    private PhienSuDung mapResultSetToPhienSuDung(ResultSet rs) throws SQLException {
        PhienSuDung phien = new PhienSuDung();

        phien.setMaPhien(rs.getString("maPhien"));
        phien.setMaKH(rs.getString("maKH"));
        phien.setMaMay(rs.getString("maMay"));
        phien.setMaNV(rs.getString("maNV"));
        phien.setMaGoiKH(rs.getString("maGoiKH"));

        Timestamp gioBatDau = rs.getTimestamp("gioBatDau");
        if (gioBatDau != null) {
            phien.setGioBatDau(gioBatDau.toLocalDateTime());
        }

        Timestamp gioKetThuc = rs.getTimestamp("gioKetThuc");
        if (gioKetThuc != null) {
            phien.setGioKetThuc(gioKetThuc.toLocalDateTime());
        }

        phien.setTongGio(rs.getDouble("tongGio"));
        phien.setGioSuDungTuGoi(rs.getDouble("gioSuDungTuGoi"));
        phien.setGioSuDungTuTaiKhoan(rs.getDouble("gioSuDungTuTaiKhoan"));
        phien.setGiaMoiGio(rs.getDouble("giaMoiGio"));
        phien.setTienGioChoi(rs.getDouble("tienGioChoi"));
        phien.setLoaiThanhToan(rs.getString("loaiThanhToan"));
        phien.setTrangThai(rs.getString("trangThai"));

        return phien;
    }

    /**
     * Set parameters cho PreparedStatement
     */
    private void setPhienSuDungParameters(PreparedStatement pst, PhienSuDung phien) throws SQLException {
        pst.setString(1, phien.getMaPhien());
        pst.setString(2, phien.getMaKH());
        pst.setString(3, phien.getMaMay());
        pst.setString(4, phien.getMaNV());
        pst.setString(5, phien.getMaGoiKH());

        if (phien.getGioBatDau() != null) {
            pst.setTimestamp(6, Timestamp.valueOf(phien.getGioBatDau()));
        } else {
            pst.setNull(6, Types.TIMESTAMP);
        }

        if (phien.getGioKetThuc() != null) {
            pst.setTimestamp(7, Timestamp.valueOf(phien.getGioKetThuc()));
        } else {
            pst.setNull(7, Types.TIMESTAMP);
        }

        pst.setDouble(8, phien.getTongGio());
        pst.setDouble(9, phien.getGioSuDungTuGoi());
        pst.setDouble(10, phien.getGioSuDungTuTaiKhoan());
        pst.setDouble(11, phien.getGiaMoiGio());
        pst.setDouble(12, phien.getTienGioChoi());
        pst.setString(13, phien.getLoaiThanhToan());
        pst.setString(14, phien.getTrangThai());
    }
}