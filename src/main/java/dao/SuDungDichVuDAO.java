package dao;
import entity.SuDungDichVu;
import dao.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class SuDungDichVuDAO{
    // Connect với database
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    /*
    Phương thức getByPhien, có chức năng lấy các thông tin của các ghi có mã phiên giống với mã phiên được truyền vô.
    parameter: mã phiên.
    return: List<SuDungDichVu> (dù ko có ghi nào thì nó vẫn trả về list) , lỗi trả về null;
    */
    public List<SuDungDichVu> geyByPhien (String maPhien){
        String sql = "SELECT * FROM sudungdichvu WHERE MaPhien = ?";
        List<SuDungDichVu> listResult = new ArrayList<>();
        try{
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, maPhien);
            rs = ps.executeQuery();
            while(rs.next()){
                listResult.add( new SuDungDichVu(rs.getString("MaSD"), rs.getString("MaPhien")
                        , rs.getString("MaDV"), rs.getInt("SoLuong"), rs.getDouble("DonGia")
                        , rs.getDouble("ThanhTien"), rs.getTimestamp("ThoiGian").toLocalDateTime()));
            }
        }catch(Exception e){
            System.err.println("[Lỗi getByPhien - SuDungDichVuDAO]: " + e.getMessage());
            return null;
        }finally{
            DBConnection.closeConnection();
        }
        return listResult;
    }

    /*
    Phương thức insert có chức năng tạo thêm một ghi.
    parameter: SuDungDichVu sddv. (chỉ cần có: mã phiên, mã dịch vụ, số lượng, đơn giá, thành tiền)
    return: true/fasle.
    */
    public boolean insert(SuDungDichVu sddv){
        String sql = "Insert sudungdichvu (MaSD, MaPhien, MaDV, SoLuong, DonGia, ThanhTien, ThoiGian)"
                + "VALUES( ?, ?, ?, ?, ?, ?, ?)";
        try{
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, this.generateNextSuDungDichVu());
            ps.setString(2, sddv.getMaphien());
            ps.setString(3, sddv.getMadv());
            ps.setInt(4, sddv.getSoluong());
            ps.setDouble(5, sddv.getDongia());
            ps.setDouble(6, sddv.getThanhtien());
            ps.setObject(7, LocalDateTime.now() );

            int rowAffected = ps.executeUpdate();

            return rowAffected > 0; // Trả về true nếu chèn thành công ít nhất 1 dòng
        }catch(Exception e){
            System.err.println("[ LỖI insert - SuDungDichVuDAO: " + e.getMessage());
            return false;
        }finally{
            DBConnection.closeConnection();
        }
    }

    /*
      Phương thức nội bộ dùng để tạo mã sử dụng dịch vụ mới tự động tăng (bổ trợ cho phương thức insert).
      Định dạng: SD + 3 chữ số (SD001, SD002,...)
      @return String mã mới đã được tăng lên 1
    */
    private String generateNextSuDungDichVu() {
        String sql = "SELECT MaSD FROM sudungdichvu ORDER BY MaSD DESC LIMIT 1";
        String nextID = "SD001"; // Mặc định nếu bảng trống

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                String lastID = rs.getString("MaSD"); // Ví dụ: "SD005"
                int number = Integer.parseInt(lastID.substring(2));
                number++;
                nextID = String.format("SD%03d", number);
            }
        } catch (Exception e) {
            System.err.println("[LỖI TỰ TĂNG MÃ - SuDungDichVuDAO]: " + e.getMessage());
        }
        return nextID;
    }

    /*
       Phương thức delete dùng để hủy dịch vụ (yêu cầu kiểm tra khách còn đang chơi không trước khi sử dụng)
       parameter: MaSD
       return: true, false
    */
    public boolean delete(String maSD) {
        String sql = "DELETE FROM sudungdichvu WHERE MaSD = ?";
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, maSD);

            int rowAffected = ps.executeUpdate();
            return rowAffected > 0; // Trả về true nếu xóa thành công
        } catch (Exception e) {
            System.err.println("[ LỖI delete - SuDungDichVuDAO ]: " + e.getMessage());
            return false;
        } finally {
            DBConnection.closeConnection();
        }
    }

    /*
    Phương thức getALl dùng để lấy tất cả các ghi sử dụng dịch vụ
    parameter: không có.
    return: List<SuDungDichVu>
    */
    public List<SuDungDichVu> getALl(){
        String sql = "SELECT * FROM sudungdichvu";
        List<SuDungDichVu> resultList = new ArrayList<>();
        try{
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while( rs.next()){
                resultList.add( new SuDungDichVu(
                        rs.getString("MaSD"),
                        rs.getString("MaPhien"),
                        rs.getString("MaDV"),
                        rs.getInt("SoLuong"),
                        rs.getDouble("DonGia"),
                        rs.getDouble("ThanhTien"),
                        rs.getTimestamp("ThoiGian").toLocalDateTime()));
            }
        }catch(Exception e){
            System.err.println("[Lỗi getAll - SuDungDichVuDAO: " + e.getMessage());
        }finally{
            DBConnection.closeConnection();
        }
        return resultList;
    }

    // Phương thức in ra dịch vụ
    public void Print(SuDungDichVu sddv){
        System.out.println("Mã sử dụng: " + sddv.getMadv() + " Mã phiên: " + sddv.getMaphien() + " Mã dịch vụ: "
                + sddv.getMadv( ) + " Số lượng: " +sddv.getSoluong() + " Đơn giá: " + sddv.getDongia() + " Thành tiền: "
                + sddv.getThanhtien() + " Thời gian: " + sddv.getThoigian());
    }
}