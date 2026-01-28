package dao;

import entity.LichSuNapTien;
import dao.DBConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LichSuNapTienDAO {

    // ===== 1. THÊM LỊCH SỬ NẠP TIỀN =====
    public boolean them(LichSuNapTien lichSu) {
        String sql = "INSERT INTO lichsunaptien (MaNap, MaKH, MaNV, MaCTKM, SoTienNap, " +
                "KhuyenMai, TongTienCong, SoDuTruoc, SoDuSau, PhuongThuc, MaGiaoDich, NgayNap) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, lichSu.getMaNap());
            pstmt.setString(2, lichSu.getMaKH());
            pstmt.setString(3, lichSu.getMaNV());
            pstmt.setString(4, lichSu.getMaCTKM());
            pstmt.setDouble(5, lichSu.getSoTienNap());
            pstmt.setDouble(6, lichSu.getKhuyenMai());
            pstmt.setDouble(7, lichSu.getTongTienCong());
            pstmt.setDouble(8, lichSu.getSoDuTruoc());
            pstmt.setDouble(9, lichSu.getSoDuSau());
            pstmt.setString(10, lichSu.getPhuongThuc());
            pstmt.setString(11, lichSu.getMaGiaoDich());
            pstmt.setTimestamp(12, Timestamp.valueOf(lichSu.getNgayNap()));

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== 2. TÌM KIẾM THEO MÃ =====
    public LichSuNapTien timTheoMa(String maNap) {
        String sql = "SELECT * FROM lichsunaptien WHERE MaNap = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maNap);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return taoDoiTuongTuResultSet(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ===== 3. LẤY DANH SÁCH THEO KHÁCH HÀNG =====
    public List<LichSuNapTien> timTheoKhachHang(String maKH) {
        List<LichSuNapTien> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM lichsunaptien WHERE MaKH = ? ORDER BY NgayNap DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maKH);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                danhSach.add(taoDoiTuongTuResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    // ===== 4. LẤY DANH SÁCH THEO NHÂN VIÊN =====
    public List<LichSuNapTien> timTheoNhanVien(String maNV) {
        List<LichSuNapTien> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM lichsunaptien WHERE MaNV = ? ORDER BY NgayNap DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maNV);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                danhSach.add(taoDoiTuongTuResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    // ===== 5. LẤY DANH SÁCH THEO PHƯƠNG THỨC THANH TOÁN =====
    public List<LichSuNapTien> timTheoPhuongThuc(String phuongThuc) {
        List<LichSuNapTien> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM lichsunaptien WHERE PhuongThuc = ? ORDER BY NgayNap DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, phuongThuc);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                danhSach.add(taoDoiTuongTuResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    // ===== 6. LẤY DANH SÁCH THEO KHOẢNG THỜI GIAN =====
    public List<LichSuNapTien> timTheoKhoangThoiGian(LocalDateTime tuNgay, LocalDateTime denNgay) {
        List<LichSuNapTien> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM lichsunaptien WHERE NgayNap BETWEEN ? AND ? ORDER BY NgayNap DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setTimestamp(1, Timestamp.valueOf(tuNgay));
            pstmt.setTimestamp(2, Timestamp.valueOf(denNgay));
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                danhSach.add(taoDoiTuongTuResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    // ===== 7. LẤY TẤT CẢ =====
    public List<LichSuNapTien> layTatCa() {
        List<LichSuNapTien> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM lichsunaptien ORDER BY NgayNap DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                danhSach.add(taoDoiTuongTuResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    // ===== 8. THỐNG KÊ TỔNG TIỀN NẠP THEO KHÁCH HÀNG =====
    public double tongTienNapTheoKhachHang(String maKH) {
        String sql = "SELECT COALESCE(SUM(TongTienCong), 0) FROM lichsunaptien WHERE MaKH = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maKH);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ===== 9. THỐNG KÊ TỔNG TIỀN NẠP THEO KHOẢNG THỜI GIAN =====
    public double tongTienNapTheoThoiGian(LocalDateTime tuNgay, LocalDateTime denNgay) {
        String sql = "SELECT COALESCE(SUM(TongTienCong), 0) FROM lichsunaptien " +
                "WHERE NgayNap BETWEEN ? AND ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setTimestamp(1, Timestamp.valueOf(tuNgay));
            pstmt.setTimestamp(2, Timestamp.valueOf(denNgay));
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ===== 10. THỐNG KÊ TỔNG KHUYẾN MÃI ĐÃ TẶNG =====
    public double tongKhuyenMaiDaTang(LocalDateTime tuNgay, LocalDateTime denNgay) {
        String sql = "SELECT COALESCE(SUM(KhuyenMai), 0) FROM lichsunaptien " +
                "WHERE NgayNap BETWEEN ? AND ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setTimestamp(1, Timestamp.valueOf(tuNgay));
            pstmt.setTimestamp(2, Timestamp.valueOf(denNgay));
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ===== 11. ĐẾM SỐ LƯỢT NẠP THEO PHƯƠNG THỨC =====
    public int demSoLuotNapTheoPhuongThuc(String phuongThuc, LocalDateTime tuNgay, LocalDateTime denNgay) {
        String sql = "SELECT COUNT(*) FROM lichsunaptien " +
                "WHERE PhuongThuc = ? AND NgayNap BETWEEN ? AND ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, phuongThuc);
            pstmt.setTimestamp(2, Timestamp.valueOf(tuNgay));
            pstmt.setTimestamp(3, Timestamp.valueOf(denNgay));
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ===== 12. LẤY GIAO DỊCH NẠP GẦN NHẤT CỦA KHÁCH HÀNG =====
    public LichSuNapTien layGiaoDichGanNhat(String maKH) {
        String sql = "SELECT * FROM lichsunaptien WHERE MaKH = ? " +
                "ORDER BY NgayNap DESC LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maKH);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return taoDoiTuongTuResultSet(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ===== 13. TOP KHÁCH HÀNG NẠP NHIỀU NHẤT =====
    public List<Object[]> topKhachHangNapNhieu(int soLuong, LocalDateTime tuNgay, LocalDateTime denNgay) {
        List<Object[]> result = new ArrayList<>();
        String sql = "SELECT kh.MaKH, kh.Ho, kh.Ten, COALESCE(SUM(ls.TongTienCong), 0) as TongNap " +
                "FROM khachhang kh " +
                "LEFT JOIN lichsunaptien ls ON kh.MaKH = ls.MaKH AND ls.NgayNap BETWEEN ? AND ? " +
                "GROUP BY kh.MaKH, kh.Ho, kh.Ten " +
                "ORDER BY TongNap DESC LIMIT ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setTimestamp(1, Timestamp.valueOf(tuNgay));
            pstmt.setTimestamp(2, Timestamp.valueOf(denNgay));
            pstmt.setInt(3, soLuong);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Object[] row = new Object[4];
                row[0] = rs.getString("MaKH");
                row[1] = rs.getString("Ho");
                row[2] = rs.getString("Ten");
                row[3] = rs.getDouble("TongNap");
                result.add(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // ===== 14. TẠO MÃ NẠP TỰ ĐỘNG - ĐÃ SỬA THÀNH FORMAT NAP =====
    public String taoMaNapTuDong() {
        String sql = "SELECT MaNap FROM lichsunaptien ORDER BY MaNap DESC LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                String maCuoi = rs.getString("MaNap");

                // Kiểm tra và xử lý các format khác nhau
                if (maCuoi == null || maCuoi.isEmpty()) {
                    return "NAP001";  // ← SỬA: NAP thay vì NT
                }

                // Loại bỏ tất cả ký tự không phải số
                String soPhan = maCuoi.replaceAll("[^0-9]", "");

                if (soPhan.isEmpty()) {
                    return "NAP001";  // ← SỬA: NAP thay vì NT
                }

                try {
                    int soThuTu = Integer.parseInt(soPhan) + 1;
                    return String.format("NAP%03d", soThuTu);  // ← SỬA: NAP thay vì NT
                } catch (NumberFormatException e) {
                    System.out.println("Lỗi parse mã: " + maCuoi + " -> " + soPhan);
                    return "NAP001";  // ← SỬA: NAP thay vì NT
                }
            } else {
                return "NAP001";  // ← SỬA: NAP thay vì NT
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return "NAP001";  // ← SỬA: NAP thay vì NT
        }
    }

    // ===== PHƯƠNG THỨC HỖ TRỢ =====
    private LichSuNapTien taoDoiTuongTuResultSet(ResultSet rs) throws SQLException {
        LichSuNapTien lichSu = new LichSuNapTien();

        lichSu.setMaNap(rs.getString("MaNap"));
        lichSu.setMaKH(rs.getString("MaKH"));
        lichSu.setMaNV(rs.getString("MaNV"));
        lichSu.setMaCTKM(rs.getString("MaCTKM"));
        lichSu.setSoTienNap(rs.getDouble("SoTienNap"));
        lichSu.setKhuyenMai(rs.getDouble("KhuyenMai"));
        lichSu.setTongTienCong(rs.getDouble("TongTienCong"));
        lichSu.setSoDuTruoc(rs.getDouble("SoDuTruoc"));
        lichSu.setSoDuSau(rs.getDouble("SoDuSau"));
        lichSu.setPhuongThuc(rs.getString("PhuongThuc"));
        lichSu.setMaGiaoDich(rs.getString("MaGiaoDich"));

        Timestamp timestamp = rs.getTimestamp("NgayNap");
        if (timestamp != null) {
            lichSu.setNgayNap(timestamp.toLocalDateTime());
        }

        return lichSu;
    }
}