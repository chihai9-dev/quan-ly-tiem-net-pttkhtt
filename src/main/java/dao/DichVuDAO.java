package dao;
import dao.DBConnection;
import entity.DichVu;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DichVuDAO {
    // connect với database
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    /*
    Phương thức lấy tất cả dịch vụ trong database.
    parameter: không có.
    return: trả về một list<DichVu>.
    */
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

    /*
    Phương thức lấy đối tượng dịch vụ bằng mã dịch vụ.
    parmeter: id của dịch vụ cần lấy.
    return: nếu có trong database thì trả về đối tượng có id đó và ngược lại thì trả về null.
    */
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

    /*
    Phương thức thêm một dịch vụ.
    parameter: một đối tượng DichVu. (Chỉ cần có tên dịch vụ, loại dịch vụ, đơn giá, đơn vị tính, trạng thái)
    return: void.
    */
    public boolean insert(DichVu dv) {
        String sql = "INSERT INTO dichvu (MaDV, TenDV, LoaiDV, DonGia, DonViTinh, SoLuongTon, TrangThai) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);

            ps.setString(1, this.generateNextMaDV());   // tăng mã tự động
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
        } finally {
            DBConnection.closeConnection();
        }
    }

    /*
    Phương thức nội bộ dùng để tạo mã dịch vụ mới tự động tăng (bổ trợ cho phương thức insert).
    Định dạng: DV + 3 chữ số (DV001, DV002,...)
    @return String mã mới đã được tăng lên 1
    */
    private String generateNextMaDV() {
        String sql = "SELECT MaDV FROM dichvu ORDER BY MaDV DESC LIMIT 1";
        String nextID = "DV001"; // Mặc định nếu bảng trống

        try (PreparedStatement ps = conn.prepareStatement(sql);
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

    /*
    Phương thức có chức năng cập nhập thông tin của một dịch vụ.
    parameter: đối tượng DichVu.s
    return: true/false.
    */
    public boolean update(DichVu dv) {
        String sql = "UPDATE dichvu SET TenDV = ?, LoaiDV = ?, DonGia = ?, DonViTinh = ?, SoLuongTon = ?, TrangThai = ? WHERE MaDV = ?";
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);

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
        } finally {
            DBConnection.closeConnection();
        }
    }

    /*
    Phương thức delete này không xóa dòng ghi đó trong database chỉ chuyển trạng thái sang NGUNGBAN thôi.
    parameter: mã dịch vụ cần deleta.
    return: true/false.
    */
    public boolean delete(String maDichVu){
         String sql = "UPDATE dichvu SET TrangThai = ? WHERE MaDV = ?";
         try{
             conn = DBConnection.getConnection();
             ps = conn.prepareStatement(sql);
             ps.setString(1, "NGUNGBAN");
             ps.setString(2, maDichVu);

             int rowAffected = ps.executeUpdate();
             return rowAffected > 0;
         }catch(Exception e){
             System.err.println("[Lỗi DELETE - DichVuDAO]: " + e.getMessage());
             return false;
         }finally{
             DBConnection.closeConnection();
         }
    }

    /*
    Phương thức update số lượng tồn
    parameter: mã dịch vụ, số cần trừ hoặc cộng (ví dụ: 10, -1)
    return: true/fasle
    */
    public boolean updateSoLuongTon(String maDichVu, int soLuongCanTangGiam) {
        String sqlSelect = "SELECT SoLuongTon FROM dichvu WHERE MaDV = ?";
        String sqlUpdate = "UPDATE dichvu SET SoLuongTon = ? WHERE MaDV = ?";
        try {
            conn = DBConnection.getConnection();

            // Bước 1: Lấy số lượng hiện có
            ps = conn.prepareStatement(sqlSelect);
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

            ps = conn.prepareStatement(sqlUpdate);
            ps.setInt(1, soLuongMoi);
            ps.setString(2, maDichVu);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("[LỖI UPDATE SOLUONG - DichVuDAO]: " + e.getMessage());
            return false;
        } finally {
            DBConnection.closeConnection();
        }
    }

    // Phương thức in ra dịch vụ phục vụ cho việc test
    public void PrintDV(DichVu object) {
        System.out.println("MaDV: " + object.getMadv() + " TenDV: " + object.getTendv() + " LoaiDV: " + object.getLoaidv()
                + " DonGia: " + object.getDongia() + " DonViTinh: " + object.getDonvitinh() + " SoLuongTon: " + object.getSoluongton()
                + " Trạng thái: " + object.getTrangthai());
    }
}