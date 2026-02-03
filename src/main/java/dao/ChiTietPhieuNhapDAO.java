package dao;

import entity.ChiTietPhieuNhap;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChiTietPhieuNhapDAO {

    // ====== Helpers ======
    private String genMaCTPN(Connection conn) throws SQLException {
        // Format: CTPN001, CTPN002,...
        String sql = "SELECT MAX(MaCTPN) FROM chitietphieunhap WHERE MaCTPN LIKE 'CTPN%'";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                String max = rs.getString(1);
                if (max != null) {
                    int num = Integer.parseInt(max.substring(4)); // bỏ "CTPN"
                    return "CTPN" + String.format("%03d", num + 1);
                }
            }
            return "CTPN001";
        }
    }

    private void validateForInsert(ChiTietPhieuNhap ct) {
        if (ct == null) throw new IllegalArgumentException("ChiTietPhieuNhap null");

        if (ct.getMaPhieuNhap() == null || ct.getMaPhieuNhap().trim().isEmpty())
            throw new IllegalArgumentException("MaPhieuNhap không hợp lệ");

        if (ct.getMaDV() == null || ct.getMaDV().trim().isEmpty())
            throw new IllegalArgumentException("MaDV không hợp lệ");

        if (ct.getSoLuong() <= 0)
            throw new IllegalArgumentException("SoLuong phải > 0");

        // double: chỉ check >= 0
        if (ct.getGiaNhap() < 0)
            throw new IllegalArgumentException("GiaNhap phải >= 0");
    }

    // ====== Query ======
    public List<ChiTietPhieuNhap> getByMaPhieuNhap(Connection conn, String maPN) {
        String sql = "SELECT MaCTPN, MaPhieuNhap, MaDV, SoLuong, GiaNhap, ThanhTien " +
                "FROM chitietphieunhap WHERE MaPhieuNhap=? ORDER BY MaCTPN";

        List<ChiTietPhieuNhap> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maPN);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("ChiTietPhieuNhapDAO.getByMaPhieuNhap(conn) error", e);
        }
    }

    public ChiTietPhieuNhap getById(String maCTPN) {
        String sql = "SELECT MaCTPN, MaPhieuNhap, MaDV, SoLuong, GiaNhap, ThanhTien " +
                "FROM chitietphieunhap WHERE MaCTPN=?";

        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maCTPN);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("ChiTietPhieuNhapDAO.getById error", e);
        }
    }

    // ====== Insert ======
    /**
     * Insert 1 chi tiết (dùng trong transaction của PhieuNhapHangDAO)
     * - MaCTPN tự sinh
     * - ThanhTien = SoLuong * GiaNhap
     */
    public String insert(Connection conn, ChiTietPhieuNhap ct) throws SQLException {
        validateForInsert(ct);

        String maCTPN = genMaCTPN(conn);

        // double: tính tiền trực tiếp
        double thanhTien = ct.getGiaNhap() * ct.getSoLuong();

        String sql = "INSERT INTO chitietphieunhap(MaCTPN, MaPhieuNhap, MaDV, SoLuong, GiaNhap, ThanhTien) " +
                "VALUES(?,?,?,?,?,?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maCTPN);
            ps.setString(2, ct.getMaPhieuNhap());
            ps.setString(3, ct.getMaDV());
            ps.setInt(4, ct.getSoLuong());
            ps.setDouble(5, ct.getGiaNhap());
            ps.setDouble(6, thanhTien);
            ps.executeUpdate();
        }

        // gán lại vào entity để bên ngoài dùng tiếp
        ct.setMaCTPN(maCTPN);
        ct.setThanhTien(thanhTien);
        return maCTPN;
    }

    /**
     * Insert nhiều chi tiết (cũng dùng trong transaction)
     */
    public void insertBatch(Connection conn, List<ChiTietPhieuNhap> list) throws SQLException {
        if (list == null || list.isEmpty())
            throw new IllegalArgumentException("Danh sách chi tiết rỗng");

        for (ChiTietPhieuNhap ct : list) {
            insert(conn, ct);
        }
    }

    // ====== Not allowed per nghiệp vụ ======
    public void updateNotAllowed() {
        throw new UnsupportedOperationException("Không cho phép sửa ChiTietPhieuNhap. Chỉ thao tác qua PhieuNhapHangDAO.");
    }

    public void deleteNotAllowed() {
        throw new UnsupportedOperationException("Không cho phép xóa ChiTietPhieuNhap. Chỉ thao tác qua PhieuNhapHangDAO.");
    }

    // ====== Mapper ======
    private ChiTietPhieuNhap map(ResultSet rs) throws SQLException {
        ChiTietPhieuNhap ct = new ChiTietPhieuNhap();
        ct.setMaCTPN(rs.getString("MaCTPN"));
        ct.setMaPhieuNhap(rs.getString("MaPhieuNhap"));
        ct.setMaDV(rs.getString("MaDV"));
        ct.setSoLuong(rs.getInt("SoLuong"));

        // double: lấy dữ liệu bằng getDouble
        ct.setGiaNhap(rs.getDouble("GiaNhap"));
        ct.setThanhTien(rs.getDouble("ThanhTien"));
        return ct;
    }
}
