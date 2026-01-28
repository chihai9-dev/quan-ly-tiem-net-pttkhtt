package dao;

import entity.HoaDon;
import dao.DBConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HoaDonDAO {

    // ===== 1. THÊM HÓA ĐƠN =====
    public boolean them(HoaDon hoaDon) {
        String sql = "INSERT INTO hoadon (MaHD, MaPhien, MaKH, MaNV, NgayLap, " +
                "TienGioChoi, TienDichVu, TongTien, GiamGia, ThanhToan, PhuongThucTT, TrangThai) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, hoaDon.getMaHD());
            pstmt.setString(2, hoaDon.getMaPhien());
            pstmt.setString(3, hoaDon.getMaKH());
            pstmt.setString(4, hoaDon.getMaNV());
            pstmt.setTimestamp(5, Timestamp.valueOf(hoaDon.getNgayLap()));
            pstmt.setDouble(6, hoaDon.getTienGioChoi());
            pstmt.setDouble(7, hoaDon.getTienDichVu());
            pstmt.setDouble(8, hoaDon.getTongTien());
            pstmt.setDouble(9, hoaDon.getGiamGia());
            pstmt.setDouble(10, hoaDon.getThanhToan());
            pstmt.setString(11, hoaDon.getPhuongThucTT());
            pstmt.setString(12, hoaDon.getTrangThai());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== 2. CẬP NHẬT HÓA ĐƠN =====
    public boolean capNhat(HoaDon hoaDon) {
        String sql = "UPDATE hoadon SET MaPhien=?, MaKH=?, MaNV=?, NgayLap=?, " +
                "TienGioChoi=?, TienDichVu=?, TongTien=?, GiamGia=?, ThanhToan=?, " +
                "PhuongThucTT=?, TrangThai=? WHERE MaHD=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, hoaDon.getMaPhien());
            pstmt.setString(2, hoaDon.getMaKH());
            pstmt.setString(3, hoaDon.getMaNV());
            pstmt.setTimestamp(4, Timestamp.valueOf(hoaDon.getNgayLap()));
            pstmt.setDouble(5, hoaDon.getTienGioChoi());
            pstmt.setDouble(6, hoaDon.getTienDichVu());
            pstmt.setDouble(7, hoaDon.getTongTien());
            pstmt.setDouble(8, hoaDon.getGiamGia());
            pstmt.setDouble(9, hoaDon.getThanhToan());
            pstmt.setString(10, hoaDon.getPhuongThucTT());
            pstmt.setString(11, hoaDon.getTrangThai());
            pstmt.setString(12, hoaDon.getMaHD());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== 3. XÓA HÓA ĐƠN =====
    public boolean xoa(String maHD) {
        String sql = "DELETE FROM hoadon WHERE MaHD = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maHD);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== 4. TÌM KIẾM THEO MÃ =====
    public HoaDon timTheoMa(String maHD) {
        String sql = "SELECT * FROM hoadon WHERE MaHD = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maHD);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return taoDoiTuongTuResultSet(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ===== 5. TÌM THEO PHIÊN SỬ DỤNG =====
    public HoaDon timTheoPhien(String maPhien) {
        String sql = "SELECT * FROM hoadon WHERE MaPhien = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maPhien);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return taoDoiTuongTuResultSet(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ===== 6. LẤY DANH SÁCH THEO KHÁCH HÀNG =====
    public List<HoaDon> timTheoKhachHang(String maKH) {
        List<HoaDon> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM hoadon WHERE MaKH = ? ORDER BY NgayLap DESC";

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

    // ===== 7. LẤY DANH SÁCH THEO NHÂN VIÊN =====
    public List<HoaDon> timTheoNhanVien(String maNV) {
        List<HoaDon> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM hoadon WHERE MaNV = ? ORDER BY NgayLap DESC";

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

    // ===== 8. LẤY DANH SÁCH THEO TRẠNG THÁI =====
    public List<HoaDon> timTheoTrangThai(String trangThai) {
        List<HoaDon> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM hoadon WHERE TrangThai = ? ORDER BY NgayLap DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, trangThai);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                danhSach.add(taoDoiTuongTuResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    // ===== 9. LẤY DANH SÁCH THEO KHOẢNG THỜI GIAN =====
    public List<HoaDon> timTheoKhoangThoiGian(LocalDateTime tuNgay, LocalDateTime denNgay) {
        List<HoaDon> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM hoadon WHERE NgayLap BETWEEN ? AND ? ORDER BY NgayLap DESC";

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

    // ===== 10. LẤY TẤT CẢ =====
    public List<HoaDon> layTatCa() {
        List<HoaDon> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM hoadon ORDER BY NgayLap DESC";

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

    // ===== 11. THANH TOÁN HÓA ĐƠN =====
    public boolean thanhToan(String maHD, String phuongThucTT) {
        String sql = "UPDATE hoadon SET TrangThai='DATHANHTOAN', PhuongThucTT=? WHERE MaHD=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, phuongThucTT);
            pstmt.setString(2, maHD);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== 12. THỐNG KÊ DOANH THU THEO THỜI GIAN =====
    public double tongDoanhThu(LocalDateTime tuNgay, LocalDateTime denNgay) {
        String sql = "SELECT COALESCE(SUM(ThanhToan), 0) FROM hoadon " +
                "WHERE TrangThai='DATHANHTOAN' AND NgayLap BETWEEN ? AND ?";

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

    // ===== 13. THỐNG KÊ DOANH THU GIỜ CHƠI =====
    public double tongDoanhThuGioChoi(LocalDateTime tuNgay, LocalDateTime denNgay) {
        String sql = "SELECT COALESCE(SUM(TienGioChoi), 0) FROM hoadon " +
                "WHERE TrangThai='DATHANHTOAN' AND NgayLap BETWEEN ? AND ?";

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

    // ===== 14. THỐNG KÊ DOANH THU DỊCH VỤ =====
    public double tongDoanhThuDichVu(LocalDateTime tuNgay, LocalDateTime denNgay) {
        String sql = "SELECT COALESCE(SUM(TienDichVu), 0) FROM hoadon " +
                "WHERE TrangThai='DATHANHTOAN' AND NgayLap BETWEEN ? AND ?";

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

    // ===== 15. THỐNG KÊ TỔNG GIẢM GIÁ =====
    public double tongGiamGia(LocalDateTime tuNgay, LocalDateTime denNgay) {
        String sql = "SELECT COALESCE(SUM(GiamGia), 0) FROM hoadon " +
                "WHERE NgayLap BETWEEN ? AND ?";

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

    // ===== 16. ĐẾM SỐ HÓA ĐƠN THEO TRẠNG THÁI =====
    public int demHoaDonTheoTrangThai(String trangThai, LocalDateTime tuNgay, LocalDateTime denNgay) {
        String sql = "SELECT COUNT(*) FROM hoadon " +
                "WHERE TrangThai = ? AND NgayLap BETWEEN ? AND ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, trangThai);
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

    // ===== 17. TOP KHÁCH HÀNG CHI TIÊU NHIỀU =====
    public List<Object[]> topKhachHangChiTieu(int soLuong, LocalDateTime tuNgay, LocalDateTime denNgay) {
        List<Object[]> result = new ArrayList<>();
        String sql = "SELECT kh.MaKH, kh.Ho, kh.Ten, COALESCE(SUM(hd.ThanhToan), 0) as TongChi " +
                "FROM khachhang kh " +
                "LEFT JOIN hoadon hd ON kh.MaKH = hd.MaKH " +
                "AND hd.TrangThai='DATHANHTOAN' AND hd.NgayLap BETWEEN ? AND ? " +
                "GROUP BY kh.MaKH, kh.Ho, kh.Ten " +
                "ORDER BY TongChi DESC LIMIT ?";

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
                row[3] = rs.getDouble("TongChi");
                result.add(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // ===== 18. DOANH THU THEO NHÂN VIÊN =====
    public List<Object[]> doanhThuTheoNhanVien(LocalDateTime tuNgay, LocalDateTime denNgay) {
        List<Object[]> result = new ArrayList<>();
        String sql = "SELECT nv.MaNV, nv.Ho, nv.Ten, " +
                "COUNT(hd.MaHD) as SoHoaDon, COALESCE(SUM(hd.ThanhToan), 0) as TongDoanhThu " +
                "FROM nhanvien nv " +
                "LEFT JOIN hoadon hd ON nv.MaNV = hd.MaNV " +
                "AND hd.TrangThai='DATHANHTOAN' AND hd.NgayLap BETWEEN ? AND ? " +
                "GROUP BY nv.MaNV, nv.Ho, nv.Ten " +
                "ORDER BY TongDoanhThu DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setTimestamp(1, Timestamp.valueOf(tuNgay));
            pstmt.setTimestamp(2, Timestamp.valueOf(denNgay));
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Object[] row = new Object[5];
                row[0] = rs.getString("MaNV");
                row[1] = rs.getString("Ho");
                row[2] = rs.getString("Ten");
                row[3] = rs.getInt("SoHoaDon");
                row[4] = rs.getDouble("TongDoanhThu");
                result.add(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // ===== 19. TẠO MÃ HÓA ĐƠN TỰ ĐỘNG =====
    public String taoMaHoaDonTuDong() {
        String sql = "SELECT MaHD FROM hoadon ORDER BY MaHD DESC LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                String maCuoi = rs.getString("MaHD");
                int soThuTu = Integer.parseInt(maCuoi.substring(2)) + 1;
                return String.format("HD%03d", soThuTu);
            } else {
                return "HD001";
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return "HD001";
        }
    }

    // ===== PHƯƠNG THỨC HỖ TRỢ =====
    private HoaDon taoDoiTuongTuResultSet(ResultSet rs) throws SQLException {
        HoaDon hoaDon = new HoaDon();

        hoaDon.setMaHD(rs.getString("MaHD"));
        hoaDon.setMaPhien(rs.getString("MaPhien"));
        hoaDon.setMaKH(rs.getString("MaKH"));
        hoaDon.setMaNV(rs.getString("MaNV"));

        Timestamp timestamp = rs.getTimestamp("NgayLap");
        if (timestamp != null) {
            hoaDon.setNgayLap(timestamp.toLocalDateTime());
        }

        hoaDon.setTienGioChoi(rs.getDouble("TienGioChoi"));
        hoaDon.setTienDichVu(rs.getDouble("TienDichVu"));
        hoaDon.setTongTien(rs.getDouble("TongTien"));
        hoaDon.setGiamGia(rs.getDouble("GiamGia"));
        hoaDon.setThanhToan(rs.getDouble("ThanhToan"));
        hoaDon.setPhuongThucTT(rs.getString("PhuongThucTT"));
        hoaDon.setTrangThai(rs.getString("TrangThai"));

        return hoaDon;
    }
}