package dao;
import entity.GoiDichVu;

import dao.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GoiDichVuDAO{
    // Connect với database
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    /*
    Phương thức getAll lấy tất cả các ghi trong bảng goidichvu
    parameter: không có.
    return: List<GoiDichVu>
    */
    public List<GoiDichVu> getAll() {
        List<GoiDichVu> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM goidichvu";

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                String magoi = rs.getString("MaGoi");
                String tengoi = rs.getString("TenGoi");
                String loaigoi = rs.getString("LoaiGoi");
                double sogio = rs.getDouble("SoGio");
                int songayhieuluc = rs.getInt("SoNgayHieuLuc");
                double giagoc = rs.getDouble("GiaGoc");
                double giagoi = rs.getDouble("GiaGoi");
                String apdungchokhu = rs.getString("ApDungChoKhu");
                String trangthai = rs.getString("TrangThai");

                GoiDichVu goi = new GoiDichVu(magoi, tengoi, loaigoi, sogio,
                        songayhieuluc, giagoc, giagoi, apdungchokhu, trangthai);
                danhSach.add(goi);
            }

        }catch (SQLException e) {
            System.err.println("Lỗi getAll - GoiDichVuDAO: " + e.getMessage());
        }finally{
            DBConnection.closeConnection();
        }

        return danhSach;
    }

    /*
    Phương thức getByID: lấy ghi theo mã gói dịch vụ
    paramter: String idGoiDichVu.
    return: GoiDichVu/null(nếu bị lỗi hoặc không tìm thấy.
    */
    public GoiDichVu getByID(String idGoiDichVu) {
        String sql = "SELECT * FROM goidichvu WHERE MaGoi = ?";

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, idGoiDichVu);
            rs = ps.executeQuery();

            if (!rs.next()) {
                return null;
            } else {
                GoiDichVu ketQua = new GoiDichVu(
                        rs.getString("MaGoi"), rs.getString("TenGoi"),
                        rs.getString("LoaiGoi"), rs.getDouble("SoGio"),
                        rs.getInt("SoNgayHieuLuc"), rs.getDouble("GiaGoc"),
                        rs.getDouble("GiaGoi"), rs.getString("ApDungChoKhu"),
                        rs.getString("TrangThai")
                );
                return ketQua;
            }
        } catch (SQLException e) {
            System.err.println("[LỖI GETBYID - GoiDichVuDAO]: " + e.getMessage());
        } finally {
            DBConnection.closeConnection();
        }
        return null;
    }

    /*
    Phương thức insert: thêm một ghi vô database.
    parameter: GoiDichVu newGDV.
    return: true/false.
    */
    public boolean insert(GoiDichVu newGDV) {
        String sql = "INSERT INTO goidichvu (MaGoi, TenGoi, LoaiGoi, SoGio, SoNgayHieuLuc" +
                ", GiaGoc, GiaGoi, ApDungChoKhu, TrangThai) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);

            ps.setString(1, this.generateNextMaGoi());
            ps.setString(2, newGDV.getTengoi());
            ps.setString(3, newGDV.getLoaigoi());
            ps.setDouble(4, newGDV.getSogio());
            ps.setInt(5, newGDV.getSongayhieuluc());
            ps.setDouble(6, newGDV.getGiagoc());
            ps.setDouble(7, newGDV.getGiagoi());
            ps.setString(8, newGDV.getApdungchokhu());
            ps.setString(9, "HOATDONG");

            int rowAffected = ps.executeUpdate();

            return rowAffected > 0;
        } catch (SQLException e) {
            System.err.println("[LỖI INSERT - GoiDichVuDAO]: " + e.getMessage());
            return false;
        } finally {
            DBConnection.closeConnection();
        }
    }

    // Phương thức nội bộ tăng mã.
    private String generateNextMaGoi() {
        String sql = "SELECT MaGoi FROM goidichvu ORDER BY MaGoi DESC LIMIT 1";
        String nextID = "GDV001";

        try {
            PreparedStatement ps1 = conn.prepareStatement(sql);
            ResultSet rs1 = ps1.executeQuery();

            if (rs1.next()) {
                String lastID = rs1.getString("MaGoi");
                int number = Integer.parseInt(lastID.substring(3));
                number++;
                nextID = String.format("GOI%03d", number);
            }
        } catch (SQLException e) {
            System.err.println("[LỖI TỰ TĂNG MÃ - GoiDichVuDAO]: " + e.getMessage());
        }
        return nextID;
    }

    /*
    Phương thức update: cập nhập.
    paramter: GoiDichVu updateGDV.
    return: true/false.
    */
    public boolean update(GoiDichVu updateGDV) {
        String sql = "UPDATE goidichvu SET TenGoi = ?, LoaiGoi = ?, SoGio = ?, SoNgayHieuLuc = ?, " +
                "GiaGoc = ?, GiaGoi = ?, ApDungChoKhu = ?, TrangThai = ? WHERE MaGoi = ?";
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);

            ps.setString(1, updateGDV.getTengoi());
            ps.setString(2, updateGDV.getLoaigoi());
            ps.setDouble(3, updateGDV.getSogio());
            ps.setInt(4, updateGDV.getSongayhieuluc());
            ps.setDouble(5, updateGDV.getGiagoc());
            ps.setDouble(6, updateGDV.getGiagoi());
            ps.setString(7, updateGDV.getApdungchokhu());
            ps.setString(8, updateGDV.getTrangthai());
            ps.setString(9, updateGDV.getMagoi());

            int rowAffected = ps.executeUpdate();

            return rowAffected > 0;
        } catch (SQLException e) {
            System.err.println("[LỖI UPDATE - GoiDichVuDAO]: " + e.getMessage());
            return false;
        } finally {
            DBConnection.closeConnection();
        }
    }

    /*
    Phương thức delete: chuyển trạn thái sang NGUNG
    paramter: String maGDV.
    return: true/false.
    */
    public boolean delete(String maGDV) {
        String sql = "UPDATE goidichvu SET TrangThai = ? WHERE MaGoi = ?";
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);

            ps.setString(1, "NGUNG");
            ps.setString(2, maGDV);

            int rowAffected = ps.executeUpdate();

            return rowAffected > 0;
        } catch (SQLException e) {
            System.err.println("[LỖI DELETE - GoiDichVuDAO]: " + e.getMessage());
            return false;
        } finally {
            DBConnection.closeConnection();
        }
    }

    public void Print(GoiDichVu gdv){
        System.out.println("Mã gói: " + gdv.getMagoi() + " Tên gói: " + gdv.getTengoi()
                + " Loại gói: " + gdv.getLoaigoi() + " Số giờ: " + gdv.getSogio()
                + " SoNgayHieuLuc: " + gdv.getSongayhieuluc() + " Giá gốc: " + gdv.getGiagoc()
                + "Giá gói: " + gdv.getGiagoi() + " Áp dụng cho khu: " + gdv.getApdungchokhu()
                + " Trạng thái: " + gdv.getTrangthai());
    }

    public static void main(String[] args){
        GoiDichVuDAO gdvDAO = new GoiDichVuDAO();

        // Test phương thức getALL
//        List<GoiDichVu> resultList = new ArrayList<>();
//        resultList = gdvDAO.getAll();
//        for(GoiDichVu item : resultList){
//            gdvDAO.Print(item);
//        }

        // Test phương thức getByID
//        GoiDichVu goal = gdvDAO.getByID("GOI002");
//        gdvDAO.Print(goal);

        // Test phương thức inset.
//        GoiDichVu newgdv = new GoiDichVu("", "Gói lẻ", "THEOGIO", 1, 30
//                , 4000, 2500, "KHU001", "");
//        gdvDAO.insert(newgdv);

        // Test phương thức update
//        GoiDichVu updateGDV = new GoiDichVu("GOI006", "Gói ngắn hạn", "THEOGIO", 1, 30
//                , 4000, 2500, "KHU001", "NGUNG");
//        gdvDAO.update(updateGDV);

        // Test phương thức delete
//        gdvDAO.delete("GOI006");
    }
}