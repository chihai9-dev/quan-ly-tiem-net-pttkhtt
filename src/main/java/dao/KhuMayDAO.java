package dao;
import entity.KhachHang;
import entity.KhuMay;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.net.ConnectException;
public class   KhuMayDAO {
    public List<KhuMay> getAll() {
        List<KhuMay> list = new ArrayList<>();
        String sql = "SELECT * FROM KhuMay ORDER BY MaKhu DESC";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                KhuMay km = mapResultSetToEntity(rs);
                list.add(km);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi getALL KhuMay: " + e.getMessage());

        }
        return list;
    }
    private KhuMay mapResultSetToEntity(ResultSet rs) throws SQLException {
        KhuMay km = new KhuMay();
        km.setMakhu(rs.getString("MaKhu"));
        km.setTenkhu(rs.getString("TenKhu"));
        km.setGiacoso(rs.getDouble("GiaCoSo"));
        km.setSomaytoida(rs.getInt("SoMayToiDa"));
        km.setTrangthai(rs.getString("TrangThai"));;

        return km;
    }
// ham tao ma khu may
    public  String generateMaKhu(){
        String sql = "SELECT MaKhu FROM khumay "+
                "ORDER BY MaKhu DESC LIMIT 1";
        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            if(rs.next()){
                String maKhu = rs.getString("MaKhu");
                //LẤY TỪ VỊ TRÍ THỨ 2
                int num = Integer.parseInt(maKhu.substring(3));
                //FORMAT CHO MÃ KHU

                conn.close();
                stmt.close();

                return String.format("KHU%03d" ,num + 1);
            }

            conn.close();
            stmt.close();
        }catch (SQLException e){
            throw new RuntimeException("Lỗi generateMaKhu" + e.getMessage());

        }
        //CHƯA CÓ DATABASE
        return  "KHU001";
    }
    public KhuMay getById(String MaKhu) {
        KhuMay km = null;
        String sql = "SELECT * FROM khumay WHERE MaKhu = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);


            pstmt.setString(1, MaKhu);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                km = mapResultSetToEntity(rs);
            }

            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi getById KhuMay: " + e.getMessage());

        }
        return km;
    }
    //===================== THEM MAY=================
    public boolean insert (KhuMay km) {
        ValidateInsert( km );
        if(isTenKhuExist(km.getTenkhu())) {
            throw new RuntimeException("Tên khu đã tồn tại !");

        }
        String sql="INSERT INTO khumay (MaKhu,TenKhu,GiaCoSo,SoMayToiDa,TrangThai)"+"VALUES (?,?,?,?,?)";
        String makhumay=generateMaKhu();
        km.setMakhu(makhumay);
        try{
            Connection conn=DBConnection.getConnection();
            PreparedStatement pstmt=conn.prepareStatement(sql);


            pstmt.setString(1,km.getMakhu());
            pstmt.setString(2,km.getTenkhu());
            pstmt.setDouble(3,km.getGiacoso());
            pstmt.setInt(4,km.getSomaytoida());
            pstmt.setString(5, "HOATDONG");
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi update KhuMay : " + e.getMessage());
        }

    }






    //================= VALIDATION ==========================
    public void ValidateInsert(KhuMay km )  {
        //ktra ten khu
        if(km.getTenkhu()==null || km.getTenkhu().trim().isEmpty()) {
            throw new RuntimeException("ten khu khong duoc de trong!");
        }
        if(km.getGiacoso()<=0) {
            throw new RuntimeException("gia co so khong duoc namg hoac nho hon 0!");
        }
        if(km.getSomaytoida()<0) {
            throw new RuntimeException("so may toi da phai lon hon 0!");
        }
    }
    public boolean isTenKhuExist( String tenKhu)  {
        String sql = "SELECT COUNT(*) FROM khumay WHERE TenKhu = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, tenKhu);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi isTenKhuExists  " + e.getMessage());
        }
        return false;
    }
//========================KIEM TRA TEN KHU TRONG KHI SUA===========(kiem tra ten nhung ma loai tru ten chinh no)
    public boolean isTenKhuExistExceptId(String tenKhu, String maKhu) {
        String sql = "SELECT COUNT(*) FROM khumay WHERE TenKhu = ? AND MaKhu <> ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, tenKhu);
            pstmt.setString(2, maKhu);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            pstmt.close();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi kiểm tra trùng tên khu: " + e.getMessage());
        }
        return false;
    }


    public boolean update (KhuMay km){
        KhuMay a=getById(km.getMakhu());
        if(a==null) {
            throw new RuntimeException("khu may khong ton tai!");
        }
        if (isTenKhuExistExceptId(km.getTenkhu(), km.getMakhu())) {
            throw new RuntimeException("Tên khu đã tồn tại!");
        }
        String sql = "UPDATE khumay SET TenKhu=?, GiaCoSo=?, SoMayToiDa=?, TrangThai=? WHERE MaKhu=?";
        try {
            Connection conn=DBConnection.getConnection();
            PreparedStatement pstmt=conn.prepareStatement(sql);
            pstmt.setString(1, km.getTenkhu());
            pstmt.setDouble(2, km.getGiacoso());
            pstmt.setInt(3, km.getSomaytoida());
            pstmt.setString(4, "HOATDONG");
            pstmt.setString(5, km.getMakhu());
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi update KhuMay : " + e.getMessage());
        }
        return true;
    }
    //====================== XOA=====================

    public boolean delete (String MaKhu){
        KhuMay km = getById(MaKhu);
        if(km == null){
            throw new RuntimeException("Lỗi khu máy không tồn tại !");
        }

        if(hasActiveSession(MaKhu)){
            throw new RuntimeException("Không thể xóa khu máy đang có phiên chơi !");
        }

        String sqlkhu = "UPDATE khumay SET TrangThai = ? WHERE MaKhu = ?";
        String sqlmay="UPDATE maytinh SET MaKhu= NULL WHERE MaKhu= ?";
        try{
            Connection conn = DBConnection.getConnection();
            PreparedStatement psmay=conn.prepareStatement(sqlmay);
            psmay.setString(1,MaKhu);
            psmay.executeUpdate();
            psmay.close();

            PreparedStatement pskhu = conn.prepareStatement(sqlkhu);
            pskhu.setString(1,"NGUNG");
            pskhu.setString(2,MaKhu);
            int row = pskhu.executeUpdate();
            conn.close();
            pskhu.close();
            return row > 0;
        }catch (SQLException e){
            throw new RuntimeException("Lỗi delete KhuMay " + e.getMessage());
        }

    }


    private  boolean hasActiveSession (String MaKhu ){
        String sql = "SELECT COUNT(*) FROM maytinh WHERE MaKhu = ? AND TrangThai = 'DANGCHOI'";
        try{
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,MaKhu);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){
                return rs.getInt(1) > 0 ;
            }
            rs.close();
            pstmt.close();
            conn.close();
        }catch (SQLException e){
            throw new RuntimeException("Lỗi hasActiveSession KhuMay " + e.getMessage());
        }
        return  false;
    }


}