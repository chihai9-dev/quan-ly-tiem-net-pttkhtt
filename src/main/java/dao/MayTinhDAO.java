package  dao;
import dao.DBConnection;

import entity.KhuMay;
import entity.MayTinh;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MayTinhDAO {
    public List<MayTinh> getAll() {
        List<MayTinh> list = new ArrayList<>();
        String sql = "SELECT * FROM MayTinh ORDER BY MaMay DESC";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                MayTinh mt = mapResultSetToEntity(rs);
                list.add(mt);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi getALL MayTinh: " + e.getMessage());

        }
        return list;
    }
    private MayTinh mapResultSetToEntity(ResultSet rs) throws SQLException {
        MayTinh mt = new MayTinh();
        mt.setMamay(rs.getString("MaMay"));
        mt.setTenmay(rs.getString("TenMay"));
        mt.setMakhu(rs.getString("MaKhu"));
        mt.setCauhinh(rs.getString("CauHinh"));
        mt.setGiamoigio(rs.getDouble("GiaMoiGio"));
        mt.setTrangthai(rs.getString("TrangThai"));;

        return mt;
    }
    public MayTinh getById (String MaMay) {
        MayTinh mt=null;
        String sql="SELECT * FROM maytinh WHERE MaMay=?";
        try {
            Connection conn=DBConnection.getConnection();
            PreparedStatement pstmt=conn.prepareStatement(sql);
            pstmt.setString(1,MaMay);
            ResultSet rs=pstmt.executeQuery();
            if (rs.next()) {
                 mt = mapResultSetToEntity(rs);
            }
            conn.close();;
            pstmt.close();
            rs.close();
        }catch(SQLException e) {
            throw new RuntimeException("lỗi getByID MayTinh: "+e.getMessage());
        }
        return mt;
    }
    // ham tao ma may
    public  String generateMaMay(){
        String sql = "SELECT MaMay FROM maytinh "+
                "ORDER BY MaMay DESC LIMIT 1";
        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            if(rs.next()){
                String maMay = rs.getString("MaMay");
                //LẤY TỪ VỊ TRÍ THỨ 2
                int num = Integer.parseInt(maMay.substring(3));
                //FORMAT CHO MÃ KHU

                conn.close();
                stmt.close();

                return String.format("MAY%03d" ,num + 1);
            }

            conn.close();
            stmt.close();
        }catch (SQLException e){
            throw new RuntimeException("Lỗi generateMaMay" + e.getMessage());

        }
        //CHƯA CÓ DATABASE
        return  "MAY001";
    }
    //====================THEM====================
    public boolean Insert (MayTinh mt) {
        ValidateMayTinh( mt );
        if(IsTenMayExist(mt.getTenmay())) {
            throw new RuntimeException("Tên khu đã tồn tại !");

        }
        if (!ValidateMaKhu(mt.getMakhu())) {

            throw new IllegalArgumentException("Mã khu không tồn tại hoặc khu không hoạt động");
        }
        String sql="INSERT INTO maytinh (MaMay,TenMay,MaKhu,CauHinh,GiaMoiGio,TrangThai)"+"VALUES (?,?,?,?,?,?)";
        String mamay=generateMaMay();
        mt.setMamay(mamay);
        mt.setTrangthai("TRONG");
        try{
            Connection conn=DBConnection.getConnection();
            PreparedStatement pstmt=conn.prepareStatement(sql);

            pstmt.setString(1,mt.getMamay());
            pstmt.setString(2,mt.getTenmay());
            pstmt.setString(3,mt.getMakhu());
            pstmt.setString(4,mt.getCauhinh());
            pstmt.setDouble(5,mt.getGiamoigio());
            pstmt.setString(6,mt.getTrangthai());
            int rowupdate= pstmt.executeUpdate();
            conn.close();
            pstmt.close();
            return rowupdate > 0 ;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi insert MayTinh : " + e.getMessage());
        }
    }
    // trong GUI co the tach nut sua gia va sua thong tin khac rieng voi nhau
    public boolean UpdateGiaMoiGio (String mamay,Double GiaMoiGioMoi) {
        if(hasActiveSession(mamay)) {
            throw new IllegalArgumentException("may dang dung ko the sua gia moi gio !");
        }
        String sql="UPDATE maytinh SET GiaMoiGio=? WHERE MaMay=?";
        try {
            Connection conn=DBConnection.getConnection();
            PreparedStatement pstmt= conn.prepareStatement(sql);
            pstmt.setDouble(1,GiaMoiGioMoi);
            pstmt.setString(2,mamay);
            pstmt.executeUpdate();
            pstmt.close();
        }catch (SQLException e) {
            throw new RuntimeException("Lỗi update GiaMoiGio : " + e.getMessage());
        }
        return true;
    }
    public boolean UpdateThongTinKhac(MayTinh mt) {
        if(!ValidateMaKhu(mt.getMakhu())) {
            throw new IllegalArgumentException("Mã khu không tồn tại hoặc khu không hoạt động");
        }
        String sql="UPDATE maytinh SET TenMay=?, MaKhu=?, CauHinh=?, TrangThai=? ";
        try{
            Connection conn=DBConnection.getConnection();
            PreparedStatement pstmt= conn.prepareStatement(sql);
            pstmt.setString(1,mt.getTenmay());
            pstmt.setString(2,mt.getMakhu());
            pstmt.setString(3,mt.getCauhinh());
            pstmt.setString(4,mt.getTrangthai());
            pstmt.executeUpdate();
            pstmt.close();
        }catch(SQLException e) {
            throw new RuntimeException("Lỗi update ThongTinKhac : " + e.getMessage());
        }
        return true;
    }
    public boolean delete(String mamay) {
        MayTinh mt=getById(mamay);
        if(mt==null) {
            throw new RuntimeException("Lỗi máy không tồn tại !");
        }
        if(hasActiveSession(mamay)) {
            throw new RuntimeException("máy đang đuợc sử dụng !");
        }
        String sql="UPDATE maytinh SET TrangThai='NGUNG' WHERE MaMay=? ";
        try {
            Connection conn=DBConnection.getConnection();
            PreparedStatement pstmt=conn.prepareStatement(sql);
            pstmt.setString(1,mamay);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi delete MayTinh: " + e.getMessage());
        }
        return true;
    }

    private boolean updateTrangThai(String maMay, String fromTrangThai, String toTrangThai) {
        String sql = "UPDATE MayTinh SET TrangThai = ? WHERE MaMay = ? AND TrangThai = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, toTrangThai);
            ps.setString(2, maMay);
            ps.setString(3, fromTrangThai);

            int rows = ps.executeUpdate();
            return rows > 0;  // true = có đổi trạng thái
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi updateTrangThai: " + e.getMessage(), e);
        }
    }

    public boolean duaVaoBaoTri(String maMay) {
        // Chỉ máy đang TRONG mới đưa vào bảo trì được
        return updateTrangThai(maMay, "TRONG", "BAOTRI");
    }

    public boolean hoanTatBaoTri(String maMay) {
        // Chỉ máy đang BAOTRI mới hoàn tất bảo trì
        return updateTrangThai(maMay, "BAOTRI", "TRONG");
    }

    public void chuyenDangDung(String maMay) {
        // chuyển trạng thái TRONG sang DANGDUNG
        updateTrangThai(maMay, "TRONG", "DANGDUNG");
    }
    public void chuyenTrong(String maMay) {
        // chuyển trạng thái DANGDUNG sang TRONG
        updateTrangThai(maMay, "DANGDUNG", "TRONG");
    }

    public boolean ngungSuDung(String maMay) {
        String sql = "UPDATE MayTinh SET TrangThai = 'NGUNG' " +
                "WHERE MaMay = ? AND (TrangThai = 'TRONG' OR TrangThai = 'BAOTRI')";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maMay);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi ngungSuDung: " + e.getMessage(), e);
        }
    }

    public boolean khoiPhuc(String maMay) {
        // Chỉ máy NGUNG mới khôi phục về TRONG
        return updateTrangThai(maMay, "NGUNG", "TRONG");
    }


    //tac dung cua ham nay:

    //nut dua vao bao tri:
//    if (mayTinhDAO.duaVaoBaoTri(maMay)) {
//        JOptionPane.showMessageDialog(this, "Đã đưa máy vào bảo trì");
//    } else {
//        JOptionPane.showMessageDialog(this, "Chỉ đưa vào bảo trì được khi máy đang TRONG");
//    }

    //nut hoan tat bao tri:
//
//    if (mayTinhDAO.hoanTatBaoTri(maMay)) {
//        JOptionPane.showMessageDialog(this, "Đã hoàn tất bảo trì");
//    } else {
//        JOptionPane.showMessageDialog(this, "Chỉ hoàn tất bảo trì được khi máy đang BAOTRI");
//    }


    //=================VALIDATION===================
    public boolean ValidateMaKhu ( String makhu) {
        if (makhu == null || makhu.trim().isEmpty()) {
            return true; // không chọn khu -> hợp lệ
        }
        else  {
            String sqlktra="SELECT COUNT(*) FROM KhuMay WHERE MaKhu=? AND TrangThai='HOATDONG'";
            try{
                Connection conn=DBConnection.getConnection();
                PreparedStatement pstmt=conn.prepareStatement(sqlktra);
                pstmt.setString(1,makhu);
                ResultSet rs=pstmt.executeQuery();
                if(rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0;
                }
            }catch (SQLException e) {
                throw new RuntimeException("Lỗi ValidateMaKhu" + e.getMessage());
            }
        }
        return false;
    }
    public void ValidateMayTinh(MayTinh mt) {
        if(mt.getTenmay()==null) {
            throw new RuntimeException("ten may khong duoc de trong !");
        }
        if(IsTenMayExist(mt.getTenmay())) {
            throw new RuntimeException("ten may da bi trung !");
        }
        if(mt.getGiamoigio()<=0) {
            throw new RuntimeException("gia tien khong duoc be hon hoac bang 0 !");
        }

    }

    //ham kiem tra trung ten
    public boolean IsTenMayExist(String tenmay) {
        String sql="SELECT COUNT(*) FROM maytinh WHERE TenMay=?";
        try {
            Connection conn=DBConnection.getConnection();
            PreparedStatement pstmt=conn.prepareStatement(sql);
            pstmt.setString(1,tenmay);
            ResultSet rs= pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1)>0;
            }

        }catch (SQLException e) {
            throw new RuntimeException("Lỗi IsTenMayExist" + e.getMessage());
        }
        return false;
    }
    // ham kiem tra may dang hoat dong
    public boolean hasActiveSession(String MaMay) {
        String sql = "SELECT COUNT(*) FROM maytinh WHERE MaMay = ? AND TrangThai = 'DANGDUNG'";
        try{
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,MaMay);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){
                return rs.getInt(1) > 0 ;
            }
            rs.close();
            pstmt.close();
            conn.close();
        }catch (SQLException e){
            throw new RuntimeException("Lỗi hasActiveSession MayTinh " + e.getMessage());
        }
        return  false;
    }
}
