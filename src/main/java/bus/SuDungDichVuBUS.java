package bus;

import entity.DichVu;
import entity.SuDungDichVu;
import entity.PhienSuDung;
import dao.DBConnection;
import dao.DichVuDAO;
import dao.SuDungDichVuDAO;
import dao.PhienSuDungDAO;
import untils.PermissionHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import java.time.LocalDateTime;

/*  CÁC METHOD
    1. SuDungDichVu orderDichVu(String maPhien, String maDV, int SoLuong): order dịch vụ
=> tạo phiếu sử dụng dịch vụ.
    2. huyOrder(String maSDDV): hủy order bằng cách mã sử dụng dịch vụ.
    3. List<SuDungDichVu> getOrderbyPhien(String maPhien): lấy tất cả các dịch vụ đã sử dụng trong phiên đó.
*/

public class SuDungDichVuBUS{

    private final DichVuDAO dvDAO = new DichVuDAO();
    private final SuDungDichVuDAO sddvDAO = new SuDungDichVuDAO();
    private final PhienSuDungDAO psdDAO = new PhienSuDungDAO();

    // CHỨC NĂNG ORDER DỊCH VỤ ( YÊU CẦU PHIÊN CÒN CHƠI).
    public SuDungDichVu orderDichVu(String maPhien, String maDV, int SoLuong) throws Exception{
        // check login
        PermissionHelper.requireLogin();
        // kiểm tra phân quyền ( quản lý/ nhân viên)
        PermissionHelper.requireNhanVien();

        // VALIDATION
        // check sự tồn tại của maPhien
        PhienSuDung psd = new PhienSuDung();
        psd = this.psdDAO.getByMaPhien(maPhien);
        if(psd == null){ throw new Exception("Không tồn tại mã phiên này!!!"); }
        // check mã phiện này có đang chơi không
        if( !psd.getTrangThai().equals("DANGCHOI") ){ throw new Exception("Phiên này đang không chơi!!!"); }

        // check sự tồn tại của dịch vụ
        DichVu dv = new DichVu();
        dv = this.dvDAO.getByID(maDV);
        if( dv == null ){ throw new Exception("Không tồn tại mã dịch vụ này!!!"); }
        // check số lượng và trạng thái
        if(dv.getSoluongton() < SoLuong || dv.getTrangthai().equals("NGUNGBAN")){ throw new Exception("Số lượng muốn " +
                "mua lớn hơn số lượng tồn hiện có hoặc dịch vụ này đã ngừng bán!!!"); }

        // gọi xuống DAO ( yêu cầu trừ số lượng dịch vụ, tạo một dòng sử dụng dịch vụ)
        SuDungDichVu sddv = new SuDungDichVu("", psd.getMaPhien(), dv.getMadv(), Math.abs(SoLuong), dv.getDongia()
                , dv.getDongia()*Math.abs(SoLuong), LocalDateTime.now() );

        Connection conn = null;
        try{
            conn = DBConnection.getConnection();    // Connect
            conn.setAutoCommit(false);  // điều chỉnh commit thử công

            boolean isUpdate = this.dvDAO.updateSoLuongTon(dv.getMadv(), (-1)*SoLuong, conn );
            boolean isInsert = this.sddvDAO.insert(sddv, conn);

            if(isUpdate && isInsert){
                conn.commit();
                System.out.println("Order dịch vụ thành công");
            }
            else{
                throw new Exception("Order dịch vụ không thành công");
            }
        }catch(Exception e){
            if(conn != null){
                try { conn.rollback(); } catch(SQLException ex){ ex.printStackTrace(); }
            }
            throw new Exception("Lỗi hệ thống: " + e.getMessage());
        }finally{
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    DBConnection.closeConnection();
                } catch (SQLException e) { e.printStackTrace(); }
            }
        }
        return sddv;
    }

    // HỦY ORDER ( YÊU CẦU: PHIÊN CÒN CHƠI)
    public void huyOrder(String maSDDV) throws Exception{
        // check login
        PermissionHelper.requireLogin();
        // không cần kiểm tra phân quyền
        PermissionHelper.requireNhanVien();

        //VALIDATION
        //kiểm tra sự tồn tại của mã này
        SuDungDichVu sddv = new SuDungDichVu();
        sddv = this.sddvDAO.getByID(maSDDV);
        if(sddv==null){ throw new Exception("Mã sử dụng này không tồn tại!!!"); }
        // kiểm trạng thái 'DANGCHOI' của phiên
        PhienSuDung psd = psdDAO.getByMaPhien(sddv.getMaphien());
        if(psd.getTrangThai().equals("DAKETTHUC")){ throw new Exception("Phiên này đã kết thức chơi!!!"); }


        // gọi xuống DAO
        Connection conn = null;
        try{
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            boolean isSuccess = this.sddvDAO.delete(maSDDV, conn);
            boolean isUpdate = this.dvDAO.updateSoLuongTon(sddv.getMadv(), sddv.getSoluong(), conn);
            if(isSuccess && isUpdate){ System.out.println("Hủy dịch vụ thành công!!!"); }
            else { System.out.println("Hủy dịch vụ không thành công!!!"); }
        }catch(Exception e){
            if(conn != null){
                try { conn.rollback(); } catch(SQLException ex){ ex.printStackTrace(); }
            }
            throw new Exception("Lỗi hệ thống: " + e.getMessage());
        }finally{
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    DBConnection.closeConnection();
                } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    // LẤY TẤT CẢ CÁC DÒNG DỮ LIỆU CỦA PHIÊN ĐÓ.
    public List<SuDungDichVu> getOrderbyPhien(String maPhien) throws Exception{
        // check login
        PermissionHelper.requireLogin();
        // kiểm tra phân quyền (ai cũng có thể xem)

        // VALIDATION
        PhienSuDung psd = new PhienSuDung();
        psd = psdDAO.getByMaPhien(maPhien);
        // check mã phiên
        if(psd == null){ throw new Exception("Mã phiên này không tồn tại!!!"); }

        // gọi xuống DAO
        List<SuDungDichVu> result = new ArrayList<>();
        Connection conn = null;
        try{
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            result = this.sddvDAO.getByPhien(maPhien, conn);
        }catch(Exception e){
            if(conn != null){
                try { conn.rollback(); } catch(SQLException ex){ ex.printStackTrace(); }
            }
            throw new Exception("Lỗi hệ thống: " + e.getMessage());
        }finally{
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    DBConnection.closeConnection();
                } catch (SQLException e) { e.printStackTrace(); }
            }
        }
        return result;
    }
}