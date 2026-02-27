package dao;
import entity.SuDungDichVu;
import dao.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

/* CÁC METHOD.
   1. List<SuDungDichVu> getByPhien(String maPhien, Connection conn1): lấy sử dụng dịch vụ bằng mã phiên.
   2. boolean insert(SuDungDichVu sddv, Connection conn1): thêm một dòng sử dụng dịch vụ.
   2.1 sinh mã tự động.
   3. boolean delete(String maSD): xóa dịch vụ sử dụng.
   4. List<SuDungDichVu> getALl(): lất tất cả các dòng dữ liệu.
   5. SuDungDichVu getByID(String maSDDV): lấy dữ liệu bằng mã sử dụng dịch vụ.
   6. vodi Print(SuDungDichVu sddv): in ra thông tin.
*/
public class SuDungDichVuDAO{
    // Connect với database
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    // LẤY SỬ DỤNG DỊCH VỤ BẰNG MÃ PHIÊN
    public List<SuDungDichVu> getByPhien (String maPhien, Connection conn1){
        String sql = "SELECT * FROM sudungdichvu WHERE MaPhien = ?";
        List<SuDungDichVu> listResult = new ArrayList<>();
        try{
            ps = conn1.prepareStatement(sql);
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
        }
        return listResult;
    }

    // THÊM MỘT DỊCH VỤ
    public boolean insert(SuDungDichVu sddv, Connection conn1){
        String sql = "Insert sudungdichvu (MaSD, MaPhien, MaDV, SoLuong, DonGia, ThanhTien, ThoiGian)"
                + "VALUES( ?, ?, ?, ?, ?, ?, ?)";
        try{
            ps = conn1.prepareStatement(sql);
            ps.setString(1, this.generateNextSuDungDichVu(conn1));
            ps.setString(2, sddv.getMaphien());
            ps.setString(3, sddv.getMadv());
            ps.setInt(4, sddv.getSoluong());
            ps.setDouble(5, sddv.getDongia());
            ps.setDouble(6, sddv.getThanhtien());
            ps.setObject(7, sddv.getThoigian() );

            int rowAffected = ps.executeUpdate();

            return rowAffected > 0; // Trả về true nếu chèn thành công ít nhất 1 dòng
        }catch(Exception e) {
            System.err.println("[ LỖI insert - SuDungDichVuDAO: " + e.getMessage());
            return false;
        }
    }

    // SINH MÃ TỰ ĐỘNG
    private String generateNextSuDungDichVu(Connection conn1) {
        String sql = "SELECT MaSD FROM sudungdichvu ORDER BY MaSD DESC LIMIT 1";
        String nextID = "SD001"; // Mặc định nếu bảng trống

        try (PreparedStatement ps = conn1.prepareStatement(sql);
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

    // XÓA DÒNG SỬ DỤNG DỊCH VỤ ĐÓ.
    public boolean delete(String maSD, Connection conn1) {
        String sql = "DELETE FROM sudungdichvu WHERE MaSD = ?";
        try {
            ps = conn1.prepareStatement(sql);
            ps.setString(1, maSD);

            int rowAffected = ps.executeUpdate();
            return rowAffected > 0; // Trả về true nếu xóa thành công
        } catch (Exception e) {
            System.err.println("[ LỖI delete - SuDungDichVuDAO ]: " + e.getMessage());
            return false;
        }
    }

    // LẤY TẤT CẢ CÁC DÒNG SỬ DỤNG DỊCH VỤ
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

    // LẤY BẰNG MÃ
    public SuDungDichVu getByID(String maSDDV){
        String sql = "SELECT * FROM sudungdichvu WHERE MaSD = ?";
        SuDungDichVu result = new SuDungDichVu();
        try{
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, maSDDV);
            rs = ps.executeQuery();
            if( rs.next() ){
                result.setMasd(rs.getString("MaSD"));
                result.setMaphien(rs.getString("MaPhien"));
                result.setMadv(rs.getString("MaDV"));
                result.setSoluong(rs.getInt("SoLuong"));
                result.setDongia(rs.getDouble("DonGia"));
                result.setThanhtien(rs.getDouble("ThanhTien"));
                result.setThoigian(rs.getTimestamp("ThoiGian").toLocalDateTime());
            }else{
                result = null;
            }
        }catch(Exception e){
            System.err.println("Lỗi getByID - SuDungDichVuDAO: " + e.getMessage());
        }finally{
            DBConnection.closeConnection();
        }
        return result;
    }

    // IN THÔNG TIN CỦA DÒNG DỮ LIỆU ĐÓ.
    public void Print(SuDungDichVu sddv){
        System.out.println("Mã sử dụng: " + sddv.getMadv() + " Mã phiên: " + sddv.getMaphien() + " Mã dịch vụ: "
                + sddv.getMadv( ) + " Số lượng: " +sddv.getSoluong() + " Đơn giá: " + sddv.getDongia() + " Thành tiền: "
                + sddv.getThanhtien() + " Thời gian: " + sddv.getThoigian());
    }


    //tính tống tiền sử dụng dịch vụ
    public  double tinhTongTienKhachHang(String maKH ) throws Exception {
        String sql = "SELECT SUM(DonGia) FROM sudungdichvu" +
                "WHERE MaPhien = ?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
        ){
            pstmt.setString(1,maKH);

            try (ResultSet rs = pstmt.executeQuery()){
                if(rs.next()){
                    return  rs.getDouble(1);
                }
            }
        }
        catch (SQLException e ){
            throw  new Exception("Lỗi tính tổng tiền khách hàng " + e.getMessage());
        }
        return  0.0;
    }
}