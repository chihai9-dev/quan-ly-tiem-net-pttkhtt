package dao;

import entity.ChiTietPhieuNhap;
import entity.PhieuNhapHang;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PhieuNhapHangDAO {

    private final ChiTietPhieuNhapDAO ctDAO = new ChiTietPhieuNhapDAO();

    /**
     * Sinh mã phiếu nhập tăng dần: PN001, PN002, ...
     *
     * Lưu ý: DB có thể có mã kiểu timestamp (PNyyMMdd_HHmmss) hoặc PN_TEST_...
     * => chỉ lấy MAX trong nhóm mã đúng regex ^PN[0-9]+$
     * => dùng FOR UPDATE để hạn chế trùng mã khi tạo đồng thời
     */
    private String genMaPhieuNhap(Connection conn) {
        String sql =
                "SELECT MaPhieuNhap " +
                        "FROM phieunhaphang " +
                        "WHERE MaPhieuNhap REGEXP '^PN[0-9]+$' " +
                        "ORDER BY CAST(SUBSTRING(MaPhieuNhap, 3) AS UNSIGNED) DESC " +
                        "LIMIT 1 FOR UPDATE";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                String max = rs.getString(1);      // VD: PN004
                int num = Integer.parseInt(max.substring(2)); // bỏ "PN"
                return "PN" + String.format("%03d", num + 1);
            }
            return "PN001";

        } catch (Exception e) {
            // fallback để không chặn nghiệp vụ nếu DB lỗi/regex không hỗ trợ
            return "PN001";
        }
    }

    public PhieuNhapHang getById(String maPN) {
        String sql = "SELECT * FROM phieunhaphang WHERE MaPhieuNhap=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maPN);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                PhieuNhapHang pn = map(rs);

                // dùng cùng conn (không để DAO con tự mở/đóng conn)
                pn.setChiTietList(ctDAO.getByMaPhieuNhap(conn, maPN));
                return pn;
            }

        } catch (SQLException e) {
            throw new RuntimeException("PhieuNhapHangDAO.getById error", e);
        }
    }

    public List<PhieuNhapHang> getAll() {
        String sql = "SELECT * FROM phieunhaphang ORDER BY NgayNhap DESC";
        List<PhieuNhapHang> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(map(rs));
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("PhieuNhapHangDAO.getAll error", e);
        }
    }

    /**
     * THÊM - Tạo phiếu
     * 1) MaPhieuNhap = genMaPhieuNhap()
     * 2) TrangThai = "CHODUYET"
     * 3) Tạo các ChiTietPhieuNhap
     * 4) TongTien = SUM(chi tiết)
     * 5) Tồn kho CHƯA thay đổi
     */
    public String createPhieuNhap(String maNCC, String maNV, List<ChiTietPhieuNhap> chiTiet) {
        if (maNCC == null || maNCC.trim().isEmpty()) throw new IllegalArgumentException("MaNCC không hợp lệ");
        if (maNV == null || maNV.trim().isEmpty()) throw new IllegalArgumentException("MaNV không hợp lệ");
        if (chiTiet == null || chiTiet.isEmpty()) throw new IllegalArgumentException("Chi tiết phiếu nhập không được rỗng");

        String insertPN =
                "INSERT INTO phieunhaphang(MaPhieuNhap,MaNCC,MaNV,NgayNhap,TongTien,TrangThai) VALUES(?,?,?,?,?,?)";
        String updateTong =
                "UPDATE phieunhaphang SET TongTien=? WHERE MaPhieuNhap=?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            String maPN = genMaPhieuNhap(conn);

            // tạo phiếu CHODUYET, TongTien=0
            try (PreparedStatement ps = conn.prepareStatement(insertPN)) {
                ps.setString(1, maPN);
                ps.setString(2, maNCC);
                ps.setString(3, maNV);

                // ✅ LocalDateTime (logic vẫn là "thời điểm hiện tại")
                ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));

                ps.setDouble(5, 0.0);
                ps.setString(6, "CHODUYET");
                ps.executeUpdate();
            }

            // tạo chi tiết + tính tổng tiền
            double tong = 0.0;
            for (ChiTietPhieuNhap ct : chiTiet) {
                ct.setMaPhieuNhap(maPN);

                // insert dùng cùng conn (DAO con sẽ tính & set ThanhTien)
                ctDAO.insert(conn, ct);

                double tt = ct.getThanhTien();
                if (tt == 0.0 && ct.getGiaNhap() != 0.0 && ct.getSoLuong() > 0) {
                    tt = ct.getGiaNhap() * ct.getSoLuong();
                    ct.setThanhTien(tt);
                }

                tong += tt;
            }

            // update tổng tiền
            try (PreparedStatement ps = conn.prepareStatement(updateTong)) {
                ps.setDouble(1, tong);
                ps.setString(2, maPN);
                ps.executeUpdate();
            }

            conn.commit();
            return maPN;

        } catch (Exception e) {
            throw new RuntimeException("PhieuNhapHangDAO.createPhieuNhap error", e);
        }
    }

    public void duyetPhieu(String maPN) {
        String lock = "SELECT TrangThai FROM phieunhaphang WHERE MaPhieuNhap=? FOR UPDATE";
        String updateTon = "UPDATE dichvu SET SoLuongTon = SoLuongTon + ? WHERE MaDV=?";
        String updateTT = "UPDATE phieunhaphang SET TrangThai='DANHAP' WHERE MaPhieuNhap=?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            String trangThai;
            try (PreparedStatement ps = conn.prepareStatement(lock)) {
                ps.setString(1, maPN);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) throw new IllegalStateException("Không tồn tại phiếu: " + maPN);
                    trangThai = rs.getString(1);
                }
            }

            if (!"CHODUYET".equals(trangThai)) {
                throw new IllegalStateException("Chỉ duyệt phiếu khi trạng thái = CHODUYET");
            }

            List<ChiTietPhieuNhap> cts = ctDAO.getByMaPhieuNhap(conn, maPN);

            try (PreparedStatement ps = conn.prepareStatement(updateTon)) {
                for (ChiTietPhieuNhap ct : cts) {
                    ps.setInt(1, ct.getSoLuong());
                    ps.setString(2, ct.getMaDV());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            try (PreparedStatement ps = conn.prepareStatement(updateTT)) {
                ps.setString(1, maPN);
                ps.executeUpdate();
            }

            conn.commit();

        } catch (Exception e) {
            throw new RuntimeException("PhieuNhapHangDAO.duyetPhieu error", e);
        }
    }

    public void huyPhieu(String maPN) {
        String lock = "SELECT TrangThai FROM phieunhaphang WHERE MaPhieuNhap=? FOR UPDATE";
        String truTon = "UPDATE dichvu SET SoLuongTon = SoLuongTon - ? WHERE MaDV=?";
        String updateTT = "UPDATE phieunhaphang SET TrangThai='DAHUY' WHERE MaPhieuNhap=?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            String trangThai;
            try (PreparedStatement ps = conn.prepareStatement(lock)) {
                ps.setString(1, maPN);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) throw new IllegalStateException("Không tồn tại phiếu: " + maPN);
                    trangThai = rs.getString(1);
                }
            }

            if ("DAHUY".equals(trangThai)) {
                throw new IllegalStateException("Phiếu đã hủy rồi");
            }

            if ("DANHAP".equals(trangThai)) {
                List<ChiTietPhieuNhap> cts = ctDAO.getByMaPhieuNhap(conn, maPN);

                try (PreparedStatement ps = conn.prepareStatement(truTon)) {
                    for (ChiTietPhieuNhap ct : cts) {
                        ps.setInt(1, ct.getSoLuong());
                        ps.setString(2, ct.getMaDV());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            } else if (!"CHODUYET".equals(trangThai)) {
                throw new IllegalStateException("Trạng thái không hợp lệ: " + trangThai);
            }

            try (PreparedStatement ps = conn.prepareStatement(updateTT)) {
                ps.setString(1, maPN);
                ps.executeUpdate();
            }

            conn.commit();

        } catch (Exception e) {
            throw new RuntimeException("PhieuNhapHangDAO.huyPhieu error", e);
        }
    }

    public void deleteNotAllowed() {
        throw new UnsupportedOperationException("Phiếu nhập là chứng từ, KHÔNG được xóa.");
    }

    private PhieuNhapHang map(ResultSet rs) throws SQLException {
        PhieuNhapHang pn = new PhieuNhapHang();
        pn.setMaPhieuNhap(rs.getString("MaPhieuNhap"));
        pn.setMaNCC(rs.getString("MaNCC"));
        pn.setMaNV(rs.getString("MaNV"));

        Timestamp ts = rs.getTimestamp("NgayNhap");
        pn.setNgayNhap(ts == null ? null : ts.toLocalDateTime());

        pn.setTongTien(rs.getDouble("TongTien"));
        pn.setTrangThai(rs.getString("TrangThai"));
        return pn;
    }
}
