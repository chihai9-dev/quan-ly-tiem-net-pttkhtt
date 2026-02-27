package dao;
import dao.DBConnection;
import entity.DichVu;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/* ========= CÁC METHOD ===========
    1. List<DichVu> getAll(): lấy toàn bộ dịch vụ.
    2. boolean insert(DichVu dv, Connection conn1): thêm một dịch vụ.
    2.1. String generateNextMaDV(Connection conn1): sinh mã dịch vụ tự động.
    3. boolean update(DichVu dv, Connection conn1): cập nhập thông tin của một dịch vụ.
    4. boolean delete(String maDichVu, Connection conn1): chuyển sang trạng thái "NGUNGBAN".
    5. boolean cancelDelete(DichVu dv, Connection conn1): hủy bỏ trạng thái "NGUNGBAN" về "CONHANG" || "HETHANG"
    6. boolean updateSoLuongTon(String maDichVu, int soLuongCanTangGiam, Connection conn1): tăng giảm số lượng khi nhập hàng
, mua hàng, hoàn hàng.
    7. int getSoLuongTon(String maDV): lấy số lượng tồn của dịch vụ đó.
    8. void PrintDV(DichVu object): in ra thông tin của dịch vụ. (phục vụ cho việc test).
*/


public class DichVuDAO {
    // connect với database
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    // LẤY TOÀN BỘ DỊCH VỤ.
    public List<DichVu> getAll() {
        List<DichVu> list = new ArrayList<>();
        String query = "SELECT * FROM dichvu";
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new DichVu(rs.getString("MaDV"), rs.getString("TenDV"),
                        rs.getString("LoaiDV"), rs.getDouble("DonGia"),
                        rs.getString("DonViTinh"), rs.getInt("SoLuongTon"),
                        rs.getString("TrangThai")));
            }
        } catch (Exception e) {
            System.err.println("[Lỗi GETALL - DichVuDAO]:" + e.getMessage());
        } finally {
            DBConnection.closeConnection();
        }
        return list;
    }

    // LẤY DỊCH VỤ THEO ID
    public DichVu getByID(String maDichVu) {
        String query = "SELECT * FROM dichvu WHERE dichvu.MaDV = ?";
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, maDichVu);
            rs = ps.executeQuery();
            if (!rs.next()) {
                System.out.println("Không tìm thấy dịch vụ nào có mã dịch vụ " + maDichVu);
                return null;
            } else {
                System.out.println("Đã tìm thấy dịch vụ có mã dịch vụ " + maDichVu);
                DichVu ketQua = new DichVu(rs.getString("MaDV"), rs.getString("TenDV"),
                        rs.getString("LoaiDV"), rs.getDouble("DonGia"),
                        rs.getString("DonViTinh"), rs.getInt("SoLuongTon"),
                        rs.getString("TrangThai"));
                return ketQua;
            }
        } catch (Exception e) {
            System.err.println("[LỖI GETBYID - DichVuDAO]:" + e.getMessage());
        } finally {
            DBConnection.closeConnection();
        }
        return null;
    }

    // THÊM DỊCH VỤ
    public boolean insert(DichVu dv, Connection conn1) {
        String sql = "INSERT INTO dichvu (MaDV, TenDV, LoaiDV, DonGia, DonViTinh, SoLuongTon, TrangThai) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try {
            ps = conn1.prepareStatement(sql);

            ps.setString(1, this.generateNextMaDV(conn1));   // tăng mã tự động
            ps.setString(2, dv.getTendv());
            ps.setString(3, dv.getLoaidv());
            ps.setDouble(4, dv.getDongia());
            ps.setString(5, dv.getDonvitinh());
            ps.setInt(6, 0);
            ps.setString(7, "HETHANG");

            int rowAffected = ps.executeUpdate();

            return rowAffected > 0; // Trả về true nếu chèn thành công ít nhất 1 dòng
        } catch (Exception e) {
            System.err.println("[LỖI INSERT - DichVuDAO]: " + e.getMessage());
            return false;
        }
    }

    // SINH MÃ DỊCH VỤ TỰ ĐỘNG
    private String generateNextMaDV(Connection conn1) {
        String sql = "SELECT MaDV FROM dichvu ORDER BY MaDV DESC LIMIT 1";
        String nextID = "DV001"; // Mặc định nếu bảng trống

        try (PreparedStatement ps = conn1.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                String lastID = rs.getString("MaDV"); // Ví dụ: "DV005"
                int number = Integer.parseInt(lastID.substring(2));
                number++;
                nextID = String.format("DV%03d", number);
            }
        } catch (Exception e) {
            System.err.println("[LỖI TỰ TĂNG MÃ - DichVuDAO]: " + e.getMessage());
        }
        return nextID;
    }

    // CẬP NHẬP THÔNG TIN
    public boolean update(DichVu dv, Connection conn1) {
        String sql = "UPDATE dichvu SET TenDV = ?, LoaiDV = ?, DonGia = ?, DonViTinh = ?, SoLuongTon = ?, TrangThai = ? WHERE MaDV = ?";
        try {
            ps = conn1.prepareStatement(sql);

            ps.setString(1, dv.getTendv());
            ps.setString(2, dv.getLoaidv());
            ps.setDouble(3, dv.getDongia());
            ps.setString(4, dv.getDonvitinh());
            ps.setInt(5, dv.getSoluongton());
            ps.setString(6, dv.getTrangthai());
            ps.setString(7, dv.getMadv());

            int rowAffected = ps.executeUpdate();
            return rowAffected > 0;
        } catch (Exception e) {
            System.err.println("[LỖI UPDATE - DichVuDAO]: " + e.getMessage());
            return false;
        }
    }

    // HỦY DỊCH VỤ
    public boolean delete(String maDichVu, Connection conn1){
        String sql = "UPDATE dichvu SET TrangThai = ? WHERE MaDV = ?";
        try{
            ps = conn1.prepareStatement(sql);
            ps.setString(1, "NGUNGBAN");
            ps.setString(2, maDichVu);

            int rowAffected = ps.executeUpdate();
            return rowAffected > 0;
        }catch(Exception e) {
            System.err.println("[Lỗi DELETE - DichVuDAO]: " + e.getMessage());
            return false;
        }
    }

    // KHÔI PHỤC DỊCH VỤ
    public boolean cancelDelete(DichVu dv, Connection conn1){
        String sql = "UPDATE dichvu SET TrangThai = ? WHERE MaDV = ?";
        try{
            ps = conn1.prepareStatement(sql);

            if( dv.getSoluongton() > 0){ ps.setString(1, "CONHANG");}
            if( dv.getSoluongton() == 0){ ps.setString(1, "HETHANG");}

            ps.setString(2, dv.getMadv());

            int rowAffected = ps.executeUpdate();
            return rowAffected > 0;
        }catch(Exception e){
            System.err.println("Lỗi cancelDelete - DichVuDAO: " + e.getMessage());
            return false;
        }
    }

    // CẬP NHẬP SỐ LƯỢNG TỒN
    public boolean updateSoLuongTon(String maDichVu, int soLuongCanTangGiam, Connection conn1 ) {
        String sqlSelect = "SELECT SoLuongTon FROM dichvu WHERE MaDV = ?";
        String sqlUpdate = "UPDATE dichvu SET SoLuongTon = ?, TrangThai = ? WHERE MaDV = ?";
        try {
            // Bước 1: Lấy số lượng hiện có
            ps = conn1.prepareStatement(sqlSelect);
            ps.setString(1, maDichVu);
            rs = ps.executeQuery();

            int soLuongHienCo = 0;
            if (rs.next()) {
                soLuongHienCo = rs.getInt("SoLuongTon");
            } else {
                return false; // Không tìm thấy mã dịch vụ
            }

            // Bước 2: Tính toán và cập nhật
            int soLuongMoi = soLuongHienCo + soLuongCanTangGiam;
            if (soLuongMoi < 0) return false; // Tránh trường hợp số lượng âm

            ps = conn1.prepareStatement(sqlUpdate);
            ps.setInt(1, soLuongMoi);
            if( soLuongMoi == 0){ ps.setString(2, "HETHANG"); }
            else { ps.setString(2, "CONHANG"); }
            ps.setString(3, maDichVu);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("[LỖI UPDATE SOLUONG - DichVuDAO]: " + e.getMessage());
            return false;
        }
    }

    // LẤY SỐ LƯỢNG TỒN CỦA MỘT DỊCH VỤ
    public int getSoLuongTon(String maDV){
        String sql = "SELECT SoLuongTon FROM dichvu WHERE MaDV = ?";
        int soLuongTon = 0;
        try{
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, "maDV");
            rs = ps.executeQuery();
            soLuongTon = rs.getInt("SoLuongTon");
        }catch(Exception e){
            System.err.println("Lỗi getSoLuongTon - DichVuDAO: " + e.getMessage());
        }finally{
            DBConnection.closeConnection();
        }
        return soLuongTon;
    }

    // IN RA THÔNG TIN CỦA MỘT DỊCH VỤ
    public void PrintDV(DichVu object) {
        System.out.println("MaDV: " + object.getMadv() + " |TenDV: " + object.getTendv()
                + " |LoaiDV: " + object.getLoaidv() + " |DonGia: " + object.getDongia()
                + " |DonViTinh: " + object.getDonvitinh() + " |SoLuongTon: " + object.getSoluongton()
                + " |Trạng thái: " + object.getTrangthai());
    }
}