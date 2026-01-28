package dao;

import entity.ChiTietHoaDon;
import dao.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChiTietHoaDonDAO {

    // Counter để tạo mã unique trong 1 phiên
    private static int counter = 0;
    private static String lastGeneratedBase = "";

    // ===== 1. THÊM CHI TIẾT HÓA ĐƠN =====
    public boolean them(ChiTietHoaDon chiTiet) {
        String sql = "INSERT INTO chitiethoadon (MaCTHD, MaHD, LoaiChiTiet, MoTa, " +
                "SoLuong, DonGia, ThanhTien) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, chiTiet.getMaCTHD());
            pstmt.setString(2, chiTiet.getMaHD());
            pstmt.setString(3, chiTiet.getLoaiChiTiet());
            pstmt.setString(4, chiTiet.getMoTa());
            pstmt.setDouble(5, chiTiet.getSoLuong());
            pstmt.setDouble(6, chiTiet.getDonGia());
            pstmt.setDouble(7, chiTiet.getThanhTien());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== 2. THÊM NHIỀU CHI TIẾT CÙNG LÚC =====
    public boolean themNhieu(List<ChiTietHoaDon> danhSachChiTiet) {
        String sql = "INSERT INTO chitiethoadon (MaCTHD, MaHD, LoaiChiTiet, MoTa, " +
                "SoLuong, DonGia, ThanhTien) VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            pstmt = conn.prepareStatement(sql);

            for (ChiTietHoaDon chiTiet : danhSachChiTiet) {
                pstmt.setString(1, chiTiet.getMaCTHD());
                pstmt.setString(2, chiTiet.getMaHD());
                pstmt.setString(3, chiTiet.getLoaiChiTiet());
                pstmt.setString(4, chiTiet.getMoTa());
                pstmt.setDouble(5, chiTiet.getSoLuong());
                pstmt.setDouble(6, chiTiet.getDonGia());
                pstmt.setDouble(7, chiTiet.getThanhTien());
                pstmt.addBatch();
            }

            pstmt.executeBatch();
            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // ===== 3. CẬP NHẬT CHI TIẾT =====
    public boolean capNhat(ChiTietHoaDon chiTiet) {
        String sql = "UPDATE chitiethoadon SET MaHD=?, LoaiChiTiet=?, MoTa=?, " +
                "SoLuong=?, DonGia=?, ThanhTien=? WHERE MaCTHD=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, chiTiet.getMaHD());
            pstmt.setString(2, chiTiet.getLoaiChiTiet());
            pstmt.setString(3, chiTiet.getMoTa());
            pstmt.setDouble(4, chiTiet.getSoLuong());
            pstmt.setDouble(5, chiTiet.getDonGia());
            pstmt.setDouble(6, chiTiet.getThanhTien());
            pstmt.setString(7, chiTiet.getMaCTHD());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== 4. XÓA CHI TIẾT =====
    public boolean xoa(String maCTHD) {
        String sql = "DELETE FROM chitiethoadon WHERE MaCTHD = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maCTHD);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== 5. XÓA TẤT CẢ CHI TIẾT CỦA HÓA ĐƠN =====
    public boolean xoaTheoHoaDon(String maHD) {
        String sql = "DELETE FROM chitiethoadon WHERE MaHD = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maHD);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== 6. TÌM KIẾM THEO MÃ =====
    public ChiTietHoaDon timTheoMa(String maCTHD) {
        String sql = "SELECT * FROM chitiethoadon WHERE MaCTHD = ?";

        try (Connection conn =DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maCTHD);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return taoDoiTuongTuResultSet(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ===== 7. LẤY DANH SÁCH THEO HÓA ĐƠN =====
    public List<ChiTietHoaDon> timTheoHoaDon(String maHD) {
        List<ChiTietHoaDon> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM chitiethoadon WHERE MaHD = ? ORDER BY LoaiChiTiet, MaCTHD";

        try (Connection conn =DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maHD);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                danhSach.add(taoDoiTuongTuResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    // ===== 8. LẤY DANH SÁCH THEO LOẠI CHI TIẾT =====
    public List<ChiTietHoaDon> timTheoLoaiChiTiet(String maHD, String loaiChiTiet) {
        List<ChiTietHoaDon> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM chitiethoadon WHERE MaHD = ? AND LoaiChiTiet = ?";

        try (Connection conn =DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maHD);
            pstmt.setString(2, loaiChiTiet);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                danhSach.add(taoDoiTuongTuResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    // ===== 9. LẤY TẤT CẢ =====
    public List<ChiTietHoaDon> layTatCa() {
        List<ChiTietHoaDon> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM chitiethoadon ORDER BY MaHD, LoaiChiTiet";

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

    // ===== 10. TÍNH TỔNG TIỀN GIỜ CHƠI THEO HÓA ĐƠN =====
    public double tongTienGioChoi(String maHD) {
        String sql = "SELECT COALESCE(SUM(ThanhTien), 0) FROM chitiethoadon " +
                "WHERE MaHD = ? AND LoaiChiTiet = 'GIOCHOI'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maHD);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ===== 11. TÍNH TỔNG TIỀN DỊCH VỤ THEO HÓA ĐƠN =====
    public double tongTienDichVu(String maHD) {
        String sql = "SELECT COALESCE(SUM(ThanhTien), 0) FROM chitiethoadon " +
                "WHERE MaHD = ? AND LoaiChiTiet = 'DICHVU'";

        try (Connection conn =DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maHD);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ===== 12. TÍNH TỔNG TIỀN TẤT CẢ CHI TIẾT =====
    public double tongThanhTien(String maHD) {
        String sql = "SELECT COALESCE(SUM(ThanhTien), 0) FROM chitiethoadon WHERE MaHD = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maHD);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ===== 13. ĐẾM SỐ CHI TIẾT THEO HÓA ĐƠN =====
    public int demChiTiet(String maHD) {
        String sql = "SELECT COUNT(*) FROM chitiethoadon WHERE MaHD = ?";

        try (Connection conn =DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maHD);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ===== 14. ĐẾM SỐ CHI TIẾT THEO LOẠI =====
    public int demChiTietTheoLoai(String maHD, String loaiChiTiet) {
        String sql = "SELECT COUNT(*) FROM chitiethoadon WHERE MaHD = ? AND LoaiChiTiet = ?";

        try (Connection conn =DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maHD);
            pstmt.setString(2, loaiChiTiet);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ===== 15. LẤY CHI TIẾT VỚI THÔNG TIN HÓA ĐƠN =====
    public List<Object[]> layChiTietVoiHoaDon(String maHD) {
        List<Object[]> result = new ArrayList<>();
        String sql = "SELECT ct.*, hd.NgayLap, hd.MaKH, hd.TrangThai " +
                "FROM chitiethoadon ct " +
                "JOIN hoadon hd ON ct.MaHD = hd.MaHD " +
                "WHERE ct.MaHD = ? ORDER BY ct.LoaiChiTiet, ct.MaCTHD";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maHD);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Object[] row = new Object[11];
                row[0] = rs.getString("MaCTHD");
                row[1] = rs.getString("MaHD");
                row[2] = rs.getString("LoaiChiTiet");
                row[3] = rs.getString("MoTa");
                row[4] = rs.getDouble("SoLuong");
                row[5] = rs.getDouble("DonGia");
                row[6] = rs.getDouble("ThanhTien");
                row[7] = rs.getTimestamp("NgayLap");
                row[8] = rs.getString("MaKH");
                row[9] = rs.getString("TrangThai");
                result.add(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // ===== 16. KIỂM TRA TỒN TẠI CHI TIẾT =====
    public boolean kiemTraTonTai(String maCTHD) {
        String sql = "SELECT COUNT(*) FROM chitiethoadon WHERE MaCTHD = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maCTHD);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ===== 17. TẠO MÃ CHI TIẾT TỰ ĐỘNG - SỬA DÙNG SYNCHRONIZED COUNTER =====
    public synchronized String taoMaChiTietTuDong() {
        String sql = "SELECT MaCTHD FROM chitiethoadon ORDER BY MaCTHD DESC LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            int soThuTu = 1; // Mặc định

            if (rs.next()) {
                String maCuoi = rs.getString("MaCTHD");

                if (maCuoi != null && !maCuoi.isEmpty()) {
                    // Loại bỏ tất cả ký tự không phải số
                    String soPhan = maCuoi.replaceAll("[^0-9]", "");

                    if (!soPhan.isEmpty()) {
                        try {
                            // Chỉ lấy 3 chữ số cuối để tránh overflow
                            if (soPhan.length() > 3) {
                                soThuTu = Integer.parseInt(soPhan.substring(soPhan.length() - 3)) + 1;
                            } else {
                                soThuTu = Integer.parseInt(soPhan) + 1;
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Lỗi parse mã: " + maCuoi);
                            soThuTu = 1;
                        }
                    }
                }
            }

            // Tạo base code
            String baseCode = String.format("CTHD%03d", soThuTu);

            // Nếu trùng với lần tạo trước, tăng counter
            if (baseCode.equals(lastGeneratedBase)) {
                counter++;
                // Nếu vượt quá 999, reset về 1
                if (soThuTu + counter > 999) {
                    counter = 0;
                    soThuTu = 1;
                }
                baseCode = String.format("CTHD%03d", soThuTu + counter);
            } else {
                // Mã mới, reset counter
                counter = 0;
                lastGeneratedBase = baseCode;
            }

            return baseCode;

        } catch (SQLException e) {
            e.printStackTrace();
            // Fallback: dùng timestamp
            return "CTHD" + (System.currentTimeMillis() % 1000);
        }
    }

    // ===== 18. TÍNH THÀNH TIỀN TỰ ĐỘNG =====
    public static double tinhThanhTien(double soLuong, double donGia) {
        return soLuong * donGia;
    }

    // ===== PHƯƠNG THỨC HỖ TRỢ =====
    private ChiTietHoaDon taoDoiTuongTuResultSet(ResultSet rs) throws SQLException {
        ChiTietHoaDon chiTiet = new ChiTietHoaDon();

        chiTiet.setMaCTHD(rs.getString("MaCTHD"));
        chiTiet.setMaHD(rs.getString("MaHD"));
        chiTiet.setLoaiChiTiet(rs.getString("LoaiChiTiet"));
        chiTiet.setMoTa(rs.getString("MoTa"));
        chiTiet.setSoLuong(rs.getDouble("SoLuong"));
        chiTiet.setDonGia(rs.getDouble("DonGia"));
        chiTiet.setThanhTien(rs.getDouble("ThanhTien"));

        return chiTiet;
    }
}