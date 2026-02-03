package dao;

import entity.NhaCungCap;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NhaCungCapDAO {

    // ===================== Helpers =====================

    private String emptyToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private void validate(NhaCungCap ncc) {
        if (ncc == null) throw new IllegalArgumentException("NhaCungCapDTO null");

        if (ncc.getTenNCC() == null || ncc.getTenNCC().trim().isEmpty())
            throw new IllegalArgumentException("TenNCC không được rỗng");

        // Số điện thoại hợp lệ nếu có (9-15 chữ số)
        String sdt = ncc.getSoDienThoai();
        if (sdt != null && !sdt.trim().isEmpty() && !sdt.trim().matches("^\\d{9,15}$"))
            throw new IllegalArgumentException("SoDienThoai không hợp lệ");

        // Email hợp lệ nếu có
        String email = ncc.getEmail();
        if (email != null && !email.trim().isEmpty()
                && !email.trim().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"))
            throw new IllegalArgumentException("Email không hợp lệ");
    }

    /**
     * Sinh mã NCC dạng NCC001, NCC002...
     * (đúng tinh thần generateId("NCC","nhacungcap","MaNCC"))
     */
    private String genMaNCC(Connection conn) throws SQLException {
        String sql = "SELECT MAX(MaNCC) FROM nhacungcap WHERE MaNCC LIKE 'NCC%'";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                String max = rs.getString(1);
                if (max != null) {
                    int num = Integer.parseInt(max.substring(3)); // bỏ "NCC"
                    return "NCC" + String.format("%03d", num + 1);
                }
            }
            return "NCC001";
        }
    }

    private NhaCungCap map(ResultSet rs) throws SQLException {
        return new NhaCungCap(
                rs.getString("MaNCC"),
                rs.getString("TenNCC"),
                rs.getString("SoDienThoai"),
                rs.getString("Email"),
                rs.getString("DiaChi"),
                rs.getString("NguoiLienHe"),
                rs.getString("TrangThai")
        );
    }

    // ===================== CRUD =====================

    /**
     * Lấy danh sách NCC
     *
     * @param includeNgung false => chỉ HOATDONG ; true => tất cả
     */
    public List<NhaCungCap> getAll(boolean includeNgung) {
        List<NhaCungCap> list = new ArrayList<>();

        String sql = "SELECT * FROM nhacungcap";
        if (!includeNgung) sql += " WHERE TrangThai='HOATDONG'";
        sql += " ORDER BY MaNCC";

        Connection conn = DBConnection.getConnection(); // ✅ không try-with-resources
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(map(rs));
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("NhaCungCapDAO.getAll error", e);
        }
    }

    public NhaCungCap getById(String maNCC) {
        String sql = "SELECT * FROM nhacungcap WHERE MaNCC=?";

        Connection conn = DBConnection.getConnection(); // ✅ không try-with-resources
        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maNCC);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("NhaCungCapDAO.getById error", e);
        }
    }

    /**
     * THÊM NCC:
     * - TenNCC không rỗng
     * - SĐT/Email hợp lệ nếu có
     * - MaNCC tự sinh
     * - TrangThai = HOATDONG
     */
    public String insert(NhaCungCap ncc) {
        validate(ncc);

        String sql = "INSERT INTO nhacungcap(MaNCC, TenNCC, SoDienThoai, Email, DiaChi, NguoiLienHe, TrangThai) " +
                "VALUES(?,?,?,?,?,?,?)";

        Connection conn = DBConnection.getConnection(); // ✅ không try-with-resources
        try {
            conn.setAutoCommit(false);

            String maNCC = genMaNCC(conn);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, maNCC);
                ps.setString(2, ncc.getTenNCC().trim());
                ps.setString(3, emptyToNull(ncc.getSoDienThoai()));
                ps.setString(4, emptyToNull(ncc.getEmail()));
                ps.setString(5, emptyToNull(ncc.getDiaChi()));
                ps.setString(6, emptyToNull(ncc.getNguoiLienHe()));
                ps.setString(7, "HOATDONG"); // nghiệp vụ
                ps.executeUpdate();
            }

            conn.commit();
            return maNCC;

        } catch (Exception e) {
            try { conn.rollback(); } catch (SQLException ignored) {}
            throw new RuntimeException("NhaCungCapDAO.insert error", e);
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
        }
    }

    /**
     * SỬA: sửa tất cả trừ MaNCC
     */
    public boolean update(NhaCungCap ncc) {
        validate(ncc);

        if (ncc.getMaNCC() == null || ncc.getMaNCC().trim().isEmpty())
            throw new IllegalArgumentException("MaNCC không hợp lệ");

        String sql = "UPDATE nhacungcap " +
                "SET TenNCC=?, SoDienThoai=?, Email=?, DiaChi=?, NguoiLienHe=?, TrangThai=? " +
                "WHERE MaNCC=?";

        Connection conn = DBConnection.getConnection(); // ✅ không try-with-resources
        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ncc.getTenNCC().trim());
            ps.setString(2, emptyToNull(ncc.getSoDienThoai()));
            ps.setString(3, emptyToNull(ncc.getEmail()));
            ps.setString(4, emptyToNull(ncc.getDiaChi()));
            ps.setString(5, emptyToNull(ncc.getNguoiLienHe()));
            ps.setString(6, (ncc.getTrangThai() == null ? "HOATDONG" : ncc.getTrangThai()));
            ps.setString(7, ncc.getMaNCC());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("NhaCungCapDAO.update error", e);
        }
    }

    /**
     * XÓA (Soft delete):
     * - Kiểm tra không có phiếu nhập CHODUYET của NCC này
     * - UPDATE TrangThai='NGUNG'
     */
    public boolean softDelete(String maNCC) {
        if (maNCC == null || maNCC.trim().isEmpty())
            throw new IllegalArgumentException("MaNCC không hợp lệ");

        String check = "SELECT COUNT(*) FROM phieunhaphang WHERE MaNCC=? AND TrangThai='CHODUYET'";
        String del = "UPDATE nhacungcap SET TrangThai='NGUNG' WHERE MaNCC=?";

        Connection conn = DBConnection.getConnection(); // ✅ không try-with-resources
        try {
            conn.setAutoCommit(false);

            // check phiếu nhập CHODUYET
            try (PreparedStatement ps = conn.prepareStatement(check)) {
                ps.setString(1, maNCC);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    int count = rs.getInt(1);
                    if (count > 0) {
                        throw new IllegalStateException("Không thể NGƯNG: còn phiếu nhập CHODUYET của NCC này");
                    }
                }
            }

            // update trạng thái NGUNG
            try (PreparedStatement ps = conn.prepareStatement(del)) {
                ps.setString(1, maNCC);
                boolean ok = ps.executeUpdate() > 0;
                conn.commit();
                return ok;
            }

        } catch (Exception e) {
            try { conn.rollback(); } catch (SQLException ignored) {}
            throw new RuntimeException("NhaCungCapDAO.softDelete error", e);
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
        }
    }
}
