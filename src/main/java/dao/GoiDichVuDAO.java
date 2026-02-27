package dao;
import entity.GoiDichVu;

import dao.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/*  CÁC METHOD
    1. List<GoiDichVu> getAll(): lất tất cả các dịch vụ
    2. GoiDichVu getBbyID(String idGoiDichVu): lấy gói dịch vụ bằng mã
    3. boolean insert(goiDichVu newGDV, Connection conn1): thêm một gói dịch vụ
    3.1 String generateNextMaGoi(Connection conn1): tạo mã tụ động
    4. boolean update(GoiDichVu updateGDV, Connection conn1): sửa thông tin gói dịch vụ
    5. boolean delete(String maGDV, Connection conn1): xóa một gói dịch vụ => chuyển sang trạng thái "NGUNG"
    6. boolean cancelDelete(String maGDV, Connection conn1): khôi phục lại một gói dịch vụ => chuyển sang
trạng thái "HOATDONG"
    7. void Print(GoiDichVu gdv): in thông tin của gói dịch vụ.
*/

public class GoiDichVuDAO{
    // Connect với database
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    // CHỨC NĂNG LẤT TẤT CẢ CÁC GÓI DỊCH VỤ
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

    // CHỨC NĂNG LẤT GÓI DỊCH VỤ BẰNG MÃ
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

    // CHỨC NĂNG THÊM GÓI DỊCH VỤ
    public boolean insert(GoiDichVu newGDV, Connection conn1) {
        String sql = "INSERT INTO goidichvu (MaGoi, TenGoi, LoaiGoi, SoGio, SoNgayHieuLuc" +
                ", GiaGoc, GiaGoi, ApDungChoKhu, TrangThai) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            ps = conn1.prepareStatement(sql);

            ps.setString(1, this.generateNextMaGoi(conn1));
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
        }
    }

    // TĂNG MÃ TỰ ĐỘNG
    private String generateNextMaGoi(Connection conn1) {
        String sql = "SELECT MaGoi FROM goidichvu ORDER BY MaGoi DESC LIMIT 1";
        String nextID = "GDV001";

        try {
            PreparedStatement ps1 = conn1.prepareStatement(sql);
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

    // CHỨC NĂNG SỬA THÔNG TIN GÓI DỊCH VỤ
    public boolean update(GoiDichVu updateGDV, Connection conn1) {
        String sql = "UPDATE goidichvu SET TenGoi = ?, LoaiGoi = ?, SoGio = ?, SoNgayHieuLuc = ?, " +
                "GiaGoc = ?, GiaGoi = ?, ApDungChoKhu = ?, TrangThai = ? WHERE MaGoi = ?";
        try {
            ps = conn1.prepareStatement(sql);

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
        }
    }

    // CHỨC NĂNG XÓA MỘT GÓI DỊCH VỤ
    public boolean delete(String maGDV, Connection conn1) {
        String sql = "UPDATE goidichvu SET TrangThai = ? WHERE MaGoi = ?";
        try {
            ps = conn1.prepareStatement(sql);

            ps.setString(1, "NGUNG");
            ps.setString(2, maGDV);

            int rowAffected = ps.executeUpdate();

            return rowAffected > 0;
        } catch (SQLException e) {
            System.err.println("[LỖI DELETE - GoiDichVuDAO]: " + e.getMessage());
            return false;
        }
    }

    // CHỨC NĂNG KHÔI PHỤC MỘT GÓI DỊCH VỤ
    public boolean cancelDelete(String maGDV, Connection conn1){
        String sql = "UPDATE goidichvu SET TrangThai = ? WHERE MaGoi = ?";
        try {
            ps = conn1.prepareStatement(sql);

            ps.setString(1, "HOATDONG");
            ps.setString(2, maGDV);

            int rowAffected = ps.executeUpdate();

            return rowAffected > 0;
        } catch (SQLException e) {
            System.err.println("[LỖI DELETE - GoiDichVuDAO]: " + e.getMessage());
            return false;
        }
    }

    public void Print(GoiDichVu gdv) {
        System.out.println("Mã gói: " + gdv.getMagoi() + " Tên gói: " + gdv.getTengoi()
                + " Loại gói: " + gdv.getLoaigoi() + " Số giờ: " + gdv.getSogio()
                + " SoNgayHieuLuc: " + gdv.getSongayhieuluc() + " Giá gốc: " + gdv.getGiagoc()
                + "Giá gói: " + gdv.getGiagoi() + " Áp dụng cho khu: " + gdv.getApdungchokhu()
                + " Trạng thái: " + gdv.getTrangthai());
    }
}