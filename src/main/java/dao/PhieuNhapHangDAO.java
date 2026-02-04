package dao;

import entity.ChiTietPhieuNhap;
import entity.PhieuNhapHang;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PhieuNhapHangDAO {

    private final ChiTietPhieuNhapDAO ctDAO = new ChiTietPhieuNhapDAO();

    /**
     * Sinh mã phiếu nhập theo dạng timestamp để KHÔNG phụ thuộc dữ liệu cũ trong DB.
     * Ví dụ: PN260201_154312
     *
     * Lý do: DB  đã có mã dạng PNyyMMdd_HHmmss nên cách tăng PN001 sẽ bị NumberFormatException.
     */
    private String genMaPhieuNhap(Connection conn) {
        String base = "PN" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd_HHmmss"));

        // nếu cực hiếm bị trùng trong cùng 1 giây, thêm suffix _01, _02...
        String checkSql = "SELECT 1 FROM phieunhaphang WHERE MaPhieuNhap=? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
            ps.setString(1, base);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return base;
            }

            for (int i = 1; i <= 99; i++) {
                String candidate = base + "_" + String.format("%02d", i);
                ps.setString(1, candidate);
                try (ResultSet rs2 = ps.executeQuery()) {
                    if (!rs2.next()) return candidate;
                }
            }

            // fallback cuối cùng
            return base + "_" + System.nanoTime();

        } catch (SQLException e) {
            // Nếu check lỗi thì vẫn trả base để không chặn nghiệp vụ
            return base;
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

                // ✅ đổi sang LocalDateTime (logic vẫn là "thời điểm hiện tại")
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

                // Giữ logic gần giống bản BigDecimal:
                // - bản cũ: nếu tt == null mới tự tính
                // - bản double: coi tt == 0 nhưng giaNhap != 0 và soLuong > 0 là "chưa set" -> tự tính fallback
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
            // rollback nếu lỗi
            // (try-with-resources sẽ auto close connection)
            throw new RuntimeException("PhieuNhapHangDAO.createPhieuNhap error", e);
        }
    }

    /**
     * DUYỆT PHIẾU
     * 1) kiểm tra TrangThai = "CHODUYET"
     * 2) cộng tồn kho theo từng chi tiết
     * 3) UPDATE TrangThai = "DANHAP"
     */
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

            // dùng cùng conn
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

    /**
     * HỦY PHIẾU
     * - từ CHODUYET: không làm gì tồn kho, set DAHUY
     * - từ DANHAP: trừ lại tồn kho, set DAHUY
     */
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
                // dùng cùng conn
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

    // Không cho phép xóa
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
