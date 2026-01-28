package dao;

import entity.ChuongTrinhKhuyenMai;
import dao.DBConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChuongTrinhKhuyenMaiDAO {

    // ===== 1. THÊM CHƯƠNG TRÌNH KHUYẾN MÃI =====
    public boolean them(ChuongTrinhKhuyenMai ctkm) {
        String sql = "INSERT INTO chuongtrinhkhuyenmai (MaCTKM, TenCT, LoaiKM, GiaTriKM, " +
                "DieuKienToiThieu, NgayBatDau, NgayKetThuc, TrangThai) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn =DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, ctkm.getMaCTKM());
            pstmt.setString(2, ctkm.getTenCT());
            pstmt.setString(3, ctkm.getLoaiKM());
            pstmt.setDouble(4, ctkm.getGiaTriKM());
            pstmt.setDouble(5, ctkm.getDieuKienToiThieu());
            pstmt.setTimestamp(6, Timestamp.valueOf(ctkm.getNgayBatDau()));
            pstmt.setTimestamp(7, Timestamp.valueOf(ctkm.getNgayKetThuc()));
            pstmt.setString(8, ctkm.getTrangThai());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== 2. CẬP NHẬT CHƯƠNG TRÌNH =====
    public boolean capNhat(ChuongTrinhKhuyenMai ctkm) {
        String sql = "UPDATE chuongtrinhkhuyenmai SET TenCT=?, LoaiKM=?, GiaTriKM=?, " +
                "DieuKienToiThieu=?, NgayBatDau=?, NgayKetThuc=?, TrangThai=? WHERE MaCTKM=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, ctkm.getTenCT());
            pstmt.setString(2, ctkm.getLoaiKM());
            pstmt.setDouble(3, ctkm.getGiaTriKM());
            pstmt.setDouble(4, ctkm.getDieuKienToiThieu());
            pstmt.setTimestamp(5, Timestamp.valueOf(ctkm.getNgayBatDau()));
            pstmt.setTimestamp(6, Timestamp.valueOf(ctkm.getNgayKetThuc()));
            pstmt.setString(7, ctkm.getTrangThai());
            pstmt.setString(8, ctkm.getMaCTKM());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== 3. XÓA CHƯƠNG TRÌNH =====
    public boolean xoa(String maCTKM) {
        String sql = "DELETE FROM chuongtrinhkhuyenmai WHERE MaCTKM = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maCTKM);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== 4. TÌM KIẾM THEO MÃ =====
    public ChuongTrinhKhuyenMai timTheoMa(String maCTKM) {
        String sql = "SELECT * FROM chuongtrinhkhuyenmai WHERE MaCTKM = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maCTKM);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return taoDoiTuongTuResultSet(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ===== 5. TÌM THEO TÊN =====
    public List<ChuongTrinhKhuyenMai> timTheoTen(String tenCT) {
        List<ChuongTrinhKhuyenMai> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM chuongtrinhkhuyenmai WHERE TenCT LIKE ? ORDER BY NgayBatDau DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + tenCT + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                danhSach.add(taoDoiTuongTuResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    // ===== 6. LẤY DANH SÁCH THEO TRẠNG THÁI =====
    public List<ChuongTrinhKhuyenMai> timTheoTrangThai(String trangThai) {
        List<ChuongTrinhKhuyenMai> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM chuongtrinhkhuyenmai WHERE TrangThai = ? ORDER BY NgayBatDau DESC";

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

    // ===== 7. LẤY DANH SÁCH THEO LOẠI KHUYẾN MÃI =====
    public List<ChuongTrinhKhuyenMai> timTheoLoai(String loaiKM) {
        List<ChuongTrinhKhuyenMai> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM chuongtrinhkhuyenmai WHERE LoaiKM = ? ORDER BY NgayBatDau DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, loaiKM);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                danhSach.add(taoDoiTuongTuResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    // ===== 8. LẤY TẤT CẢ =====
    public List<ChuongTrinhKhuyenMai> layTatCa() {
        List<ChuongTrinhKhuyenMai> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM chuongtrinhkhuyenmai ORDER BY NgayBatDau DESC";

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

    // ===== 9. LẤY CHƯƠNG TRÌNH ĐANG HOẠT ĐỘNG =====
    public List<ChuongTrinhKhuyenMai> layChuongTrinhDangHoatDong() {
        List<ChuongTrinhKhuyenMai> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM chuongtrinhkhuyenmai " +
                "WHERE TrangThai = 'HOATDONG' " +
                "AND NgayBatDau <= NOW() AND NgayKetThuc >= NOW() " +
                "ORDER BY GiaTriKM DESC";

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

    // ===== 10. LẤY CHƯƠNG TRÌNH PHÙ HỢP VỚI SỐ TIỀN =====
    public List<ChuongTrinhKhuyenMai> timChuongTrinhPhuHop(double soTienNap) {
        List<ChuongTrinhKhuyenMai> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM chuongtrinhkhuyenmai " +
                "WHERE TrangThai = 'HOATDONG' " +
                "AND NgayBatDau <= NOW() AND NgayKetThuc >= NOW() " +
                "AND DieuKienToiThieu <= ? " +
                "ORDER BY GiaTriKM DESC";

        try (Connection conn =DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, soTienNap);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                danhSach.add(taoDoiTuongTuResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    // ===== 11. LẤY CHƯƠNG TRÌNH TỐT NHẤT CHO SỐ TIỀN =====
    public ChuongTrinhKhuyenMai timChuongTrinhTotNhat(double soTienNap) {
        String sql = "SELECT * FROM chuongtrinhkhuyenmai " +
                "WHERE TrangThai = 'HOATDONG' " +
                "AND NgayBatDau <= NOW() AND NgayKetThuc >= NOW() " +
                "AND DieuKienToiThieu <= ? " +
                "ORDER BY GiaTriKM DESC LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, soTienNap);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return taoDoiTuongTuResultSet(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ===== 12. KIỂM TRA CHƯƠNG TRÌNH CÒN HIỆU LỰC =====
    public boolean kiemTraConHieuLuc(String maCTKM) {
        String sql = "SELECT COUNT(*) FROM chuongtrinhkhuyenmai " +
                "WHERE MaCTKM = ? AND TrangThai = 'HOATDONG' " +
                "AND NgayBatDau <= NOW() AND NgayKetThuc >= NOW()";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maCTKM);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ===== 13. KIỂM TRA ĐỦ ĐIỀU KIỆN ÁP DỤNG =====
    public boolean kiemTraDieuKien(String maCTKM, double soTienNap) {
        String sql = "SELECT DieuKienToiThieu FROM chuongtrinhkhuyenmai WHERE MaCTKM = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maCTKM);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                double dieuKien = rs.getDouble("DieuKienToiThieu");
                return soTienNap >= dieuKien;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ===== 14. TÍNH GIÁ TRỊ KHUYẾN MÃI =====
    public double tinhGiaTriKhuyenMai(String maCTKM, double soTienNap) {
        ChuongTrinhKhuyenMai ctkm = timTheoMa(maCTKM);

        if (ctkm == null || !kiemTraDieuKien(maCTKM, soTienNap)) {
            return 0;
        }

        String loaiKM = ctkm.getLoaiKM();
        double giaTriKM = ctkm.getGiaTriKM();

        switch (loaiKM) {
            case "PHANTRAM":
                return soTienNap * giaTriKM / 100;
            case "SOTIEN":
                return giaTriKM;
            case "TANGGIO":
                // Trả về số giờ được tặng (sẽ được xử lý ở tầng BLL)
                return giaTriKM;
            default:
                return 0;
        }
    }

    // ===== 15. CẬP NHẬT TRẠNG THÁI =====
    public boolean capNhatTrangThai(String maCTKM, String trangThaiMoi) {
        String sql = "UPDATE chuongtrinhkhuyenmai SET TrangThai = ? WHERE MaCTKM = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, trangThaiMoi);
            pstmt.setString(2, maCTKM);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== 16. TẮT CHƯƠNG TRÌNH =====
    public boolean tatChuongTrinh(String maCTKM) {
        return capNhatTrangThai(maCTKM, "NGUNG");
    }

    // ===== 17. BẬT CHƯƠNG TRÌNH =====
    public boolean batChuongTrinh(String maCTKM) {
        return capNhatTrangThai(maCTKM, "HOATDONG");
    }

    // ===== 18. CẬP NHẬT TRẠNG THÁI HẾT HẠN TỰ ĐỘNG =====
    public int capNhatChuongTrinhHetHan() {
        String sql = "UPDATE chuongtrinhkhuyenmai SET TrangThai = 'HETHAN' " +
                "WHERE TrangThai = 'HOATDONG' AND NgayKetThuc < NOW()";

        try (Connection conn =DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            return stmt.executeUpdate(sql);

        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    // ===== 19. ĐẾM SỐ LƯỢT SỬ DỤNG CHƯƠNG TRÌNH =====
    public int demSoLuotSuDung(String maCTKM) {
        String sql = "SELECT COUNT(*) FROM lichsunaptien WHERE MaCTKM = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maCTKM);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ===== 20. TỔNG KHUYẾN MÃI ĐÃ TẶNG =====
    public double tongKhuyenMaiDaTang(String maCTKM) {
        String sql = "SELECT COALESCE(SUM(KhuyenMai), 0) FROM lichsunaptien WHERE MaCTKM = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maCTKM);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ===== 21. THỐNG KÊ CHƯƠNG TRÌNH THEO THỜI GIAN =====
    public List<Object[]> thongKeChuongTrinh(LocalDateTime tuNgay, LocalDateTime denNgay) {
        List<Object[]> result = new ArrayList<>();
        String sql = "SELECT ct.MaCTKM, ct.TenCT, ct.LoaiKM, " +
                "COUNT(ls.MaNap) as SoLuotDung, COALESCE(SUM(ls.KhuyenMai), 0) as TongKM " +
                "FROM chuongtrinhkhuyenmai ct " +
                "LEFT JOIN lichsunaptien ls ON ct.MaCTKM = ls.MaCTKM " +
                "AND ls.NgayNap BETWEEN ? AND ? " +
                "WHERE ct.NgayBatDau <= ? AND ct.NgayKetThuc >= ? " +
                "GROUP BY ct.MaCTKM, ct.TenCT, ct.LoaiKM " +
                "ORDER BY TongKM DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setTimestamp(1, Timestamp.valueOf(tuNgay));
            pstmt.setTimestamp(2, Timestamp.valueOf(denNgay));
            pstmt.setTimestamp(3, Timestamp.valueOf(denNgay));
            pstmt.setTimestamp(4, Timestamp.valueOf(tuNgay));
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Object[] row = new Object[5];
                row[0] = rs.getString("MaCTKM");
                row[1] = rs.getString("TenCT");
                row[2] = rs.getString("LoaiKM");
                row[3] = rs.getInt("SoLuotDung");
                row[4] = rs.getDouble("TongKM");
                result.add(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // ===== 22. TẠO MÃ CHƯƠNG TRÌNH TỰ ĐỘNG - ĐÃ SỬA THÀNH FORMAT KM =====
    public String taoMaChuongTrinhTuDong() {
        String sql = "SELECT MaCTKM FROM chuongtrinhkhuyenmai ORDER BY MaCTKM DESC LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                String maCuoi = rs.getString("MaCTKM");

                // Kiểm tra và xử lý các format khác nhau
                if (maCuoi == null || maCuoi.isEmpty()) {
                    return "KM001";  // ← SỬA: KM thay vì CTKM
                }

                // Loại bỏ tất cả ký tự không phải số
                String soPhan = maCuoi.replaceAll("[^0-9]", "");

                if (soPhan.isEmpty()) {
                    return "KM001";  // ← SỬA: KM thay vì CTKM
                }

                try {
                    int soThuTu = Integer.parseInt(soPhan) + 1;
                    return String.format("KM%03d", soThuTu);  // ← SỬA: KM thay vì CTKM
                } catch (NumberFormatException e) {
                    System.out.println("Lỗi parse mã: " + maCuoi + " -> " + soPhan);
                    return "KM001";  // ← SỬA: KM thay vì CTKM
                }
            } else {
                return "KM001";  // ← SỬA: KM thay vì CTKM
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return "KM001";  // ← SỬA: KM thay vì CTKM
        }
    }

    // ===== PHƯƠNG THỨC HỖ TRỢ =====
    private ChuongTrinhKhuyenMai taoDoiTuongTuResultSet(ResultSet rs) throws SQLException {
        ChuongTrinhKhuyenMai ctkm = new ChuongTrinhKhuyenMai();

        ctkm.setMaCTKM(rs.getString("MaCTKM"));
        ctkm.setTenCT(rs.getString("TenCT"));
        ctkm.setLoaiKM(rs.getString("LoaiKM"));
        ctkm.setGiaTriKM(rs.getDouble("GiaTriKM"));
        ctkm.setDieuKienToiThieu(rs.getDouble("DieuKienToiThieu"));

        Timestamp tsBatDau = rs.getTimestamp("NgayBatDau");
        if (tsBatDau != null) {
            ctkm.setNgayBatDau(tsBatDau.toLocalDateTime());
        }

        Timestamp tsKetThuc = rs.getTimestamp("NgayKetThuc");
        if (tsKetThuc != null) {
            ctkm.setNgayKetThuc(tsKetThuc.toLocalDateTime());
        }

        ctkm.setTrangThai(rs.getString("TrangThai"));

        return ctkm;
    }
}