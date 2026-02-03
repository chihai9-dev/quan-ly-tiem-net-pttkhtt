package test.dao;

import dao.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TestUtil {

    /** Lấy 1 giá trị String đầu tiên từ query. Nếu lỗi hoặc không có dữ liệu => null */
    public static String firstStringOrNull(String sql) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getString(1);
            return null;

        } catch (SQLException e) {
            return null;
        }
    }

    /** Lấy 1 MaNCC bất kỳ */
    public static String anyMaNCC() {
        String id = firstStringOrNull("SELECT MaNCC FROM nhacungcap WHERE TrangThai='HOATDONG' LIMIT 1");
        if (id == null) id = firstStringOrNull("SELECT MaNCC FROM nhacungcap LIMIT 1");
        if (id == null) id = firstStringOrNull("SELECT MaNCC FROM phieunhaphang LIMIT 1");
        return id;
    }

    /** Lấy 1 MaNV bất kỳ. Nếu bảng nhanvien không tồn tại thì fallback qua phieunhaphang */
    public static String anyMaNV() {
        String id = firstStringOrNull("SELECT MaNV FROM nhanvien LIMIT 1");
        if (id == null) id = firstStringOrNull("SELECT MaNV FROM phieunhaphang LIMIT 1");
        return id;
    }

    /** Lấy 1 MaDV bất kỳ */
    public static String anyMaDV() {
        String id = firstStringOrNull("SELECT MaDV FROM dichvu LIMIT 1");
        if (id == null) id = firstStringOrNull("SELECT MaDV FROM chitietphieunhap LIMIT 1");
        return id;
    }

    /** Lấy tồn kho hiện tại của dịch vụ */
    public static Integer soLuongTonOf(String maDV) {
        if (maDV == null) return null;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT SoLuongTon FROM dichvu WHERE MaDV=?")) {
            ps.setString(1, maDV);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
                return null;
            }
        } catch (SQLException e) {
            return null;
        }
    }
}
