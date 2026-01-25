package dao;

import entity.PhienSuDung;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PhienSuDungDAO {

    // Lấy tất cả danh sách phiên sử dụng (Lịch sử hoạt động)
    public List<PhienSuDung> getAll() {
        List<PhienSuDung> list = new ArrayList<>();
        String sql = "SELECT * FROM phiensudung ORDER BY MaPhien DESC";
        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                list.add(mapResultSetToEntity(rs));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi getAll PhienSuDung: " + e.getMessage());
        }
        return list;
    }

    // Lấy phiên đang chơi của một máy cụ thể (Dùng để kiểm tra trạng thái máy)
    public PhienSuDung getPhienDangChoiByMaMay(String maMay) {
        PhienSuDung phien = null;
        String sql = "SELECT * FROM phiensudung WHERE MaMay = ? AND TrangThai = 'DANGCHOI'";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, maMay);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                phien = mapResultSetToEntity(rs);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy phiên đang chơi: " + e.getMessage());
        }
        return phien;
    }

    // Mở phiên mới (Check-in)
    public boolean insert(PhienSuDung phien) {
        // 1. Kiểm tra máy đó có đang ai ngồi không
        if (getPhienDangChoiByMaMay(phien.getMamay()) != null) {
            throw new RuntimeException("Máy này đang có người sử dụng!");
        }

        String sql = "INSERT INTO phiensudung (MaPhien, MaKH, MaMay, MaNV, GioBatDau, TrangThai) VALUES (?, ?, ?, ?, ?, ?)";

        // Tạo mã tự động
        String maPhien = generateMaPhien();
        phien.setMaphien(maPhien);
        phien.setTrangthai("DANGCHOI");

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, phien.getMaphien());

            // Xử lý MaKH có thể null (Khách vãng lai)
            if (phien.getMakh() != null && !phien.getMakh().isEmpty()) {
                pstmt.setString(2, phien.getMakh());
            } else {
                pstmt.setNull(2, Types.VARCHAR);
            }

            pstmt.setString(3, phien.getMamay());
            pstmt.setString(4, phien.getManv());

            // Set giờ bắt đầu là hiện tại
            pstmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            pstmt.setString(6, "DANGCHOI");

            int row = pstmt.executeUpdate();
            conn.close();
            pstmt.close();
            return row > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi mở phiên (Insert): " + e.getMessage());
        }
    }

    // Đóng phiên (Check-out / Thanh toán)
    public boolean ketThucPhien(String maPhien, double tongTien) {
        // Kiểm tra phiên tồn tại
        PhienSuDung phien = getById(maPhien);
        if (phien == null) {
            throw new RuntimeException("Phiên sử dụng không tồn tại!");
        }
        if (phien.getTrangthai().equals("DAKETTHUC")) {
            throw new RuntimeException("Phiên này đã kết thúc rồi!");
        }

        String sql = "UPDATE phiensudung SET GioKetThuc = ?, TongTien = ?, TrangThai = 'DAKETTHUC' WHERE MaPhien = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            // Set giờ kết thúc là hiện tại
            pstmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            pstmt.setDouble(2, tongTien);
            pstmt.setString(3, maPhien);

            int row = pstmt.executeUpdate();
            conn.close();
            pstmt.close();
            return row > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi kết thúc phiên: " + e.getMessage());
        }
    }

    // Tìm theo ID
    public PhienSuDung getById(String maPhien) {
        PhienSuDung phien = null;
        String sql = "SELECT * FROM phiensudung WHERE MaPhien = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, maPhien);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                phien = mapResultSetToEntity(rs);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi getById PhienSuDung: " + e.getMessage());
        }
        return phien;
    }

    // Map ResultSet sang Entity
    private PhienSuDung mapResultSetToEntity(ResultSet rs) throws SQLException {
        PhienSuDung phien = new PhienSuDung();
        phien.setMaphien(rs.getString("MaPhien"));
        phien.setMakh(rs.getString("MaKH"));
        phien.setMamay(rs.getString("MaMay"));
        phien.setManv(rs.getString("MaNV"));

        // Chuyển đổi Timestamp sang LocalDateTime hoặc Date tùy theo Entity của bạn
        // Ở đây tôi lấy Timestamp cho đơn giản
        phien.setGiobatdau(rs.getTimestamp("GioBatDau"));
        phien.setGioketthuc(rs.getTimestamp("GioKetThuc"));

        phien.setTongtien(rs.getDouble("TongTien"));
        phien.setTrangthai(rs.getString("TrangThai"));
        return phien;
    }

    // Tạo mã tự động: PS001, PS002...
    public String generateMaPhien() {
        String sql = "SELECT MaPhien FROM phiensudung ORDER BY MaPhien DESC LIMIT 1";
        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                String lastMa = rs.getString("MaPhien");
                // Cắt chuỗi từ vị trí thứ 2 (bỏ chữ PS)
                int num = Integer.parseInt(lastMa.substring(2));
                conn.close();
                stmt.close();
                return String.format("PS%03d", num + 1);
            }
            conn.close();
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi generateMaPhien: " + e.getMessage());
        }
        // Mặc định nếu chưa có dữ liệu
        return "PS001";
    }
}