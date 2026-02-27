package dao;

import entity.KhachHang;
import entity.NhanVien;

import java.net.ConnectException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KhachHangDAO {
    public List<KhachHang> getAll(){
        List<KhachHang> list = new ArrayList<>();
        String sql = "SELECT * FROM khachhang ORDER BY MaKH DESC";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                KhachHang kh = mapResultSetToEntity(rs);
                list.add(kh);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi getALL KhachHang: "+ e.getMessage());

        }
        return list;
    }
    public  KhachHang login(String tendangnhap,String matkhau){
        KhachHang kh = null;

        if(tendangnhap == null || tendangnhap.trim().isEmpty()){
            throw new RuntimeException("Tên đăng nhập không được để trống !");
        }
        if(matkhau == null || matkhau.trim().isEmpty()){
            throw new RuntimeException("Mật khẩu không được để trống !");
        }


        String sql = "SELECT * FROM khachhang "+
                "WHERE TenDangNhap = ? AND MatKhau = ?";
        try{
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1,tendangnhap);
            pstmt.setString(2,matkhau);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){
                kh = mapResultSetToEntity(rs);
                if(kh.isNgung()){
                    throw new RuntimeException("Khách hàng hiện tại đã bị xóa !");
                }
            }
            rs.close();
            pstmt.close();
        }
        catch (SQLException e){
            throw new RuntimeException("Lỗi login: " + e.getMessage());

        }
        return kh;
    }
    public KhachHang getByTenDangNhap(String tenDN ){
        KhachHang kh = null;
        String sql = "SELECT * FROM khachhang "+
                "WHERE TenDangNhap = ?";
        try{
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1,tenDN);

            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){
                kh = mapResultSetToEntity(rs);
            }
            conn.close();
            pstmt.close();

        }catch (SQLException e){
            throw new RuntimeException("Lỗi tìm tên đăng nhập : "+e.getMessage());

        }
        return kh;
    }
    public boolean insert(KhachHang kh ){
        validateKhachHang(kh,true);

        if(isTenDangNhapExists(kh.getTendangnhap())){
            throw new RuntimeException("Tên đăng nhập đã tồn tại !");
        }


        String sql = "INSERT INTO khachhang (MaKH,Ho,Ten,SoDienThoai,TenDangNhap,MatKhau,SoDu,TrangThai) " +
                "VALUES (?,?,?,?,?,?,?,?)";


        //tạo mã khách hàng tự động
        String maKH = generateMaKH();
        kh.setMakh(maKH);

        //set số dư = 0  && trạng thái = hoạt động
        kh.setSodu(0);
        kh.setTrangthai("HOATDONG");

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, kh.getMakh());
            pstmt.setString(2,kh.getHo());
            pstmt.setString(3,kh.getTen());
            pstmt.setString(4,kh.getSodienthoai());
            pstmt.setString(5,kh.getTendangnhap());
            pstmt.setString(6,kh.getMatkhau());
            pstmt.setDouble(7,kh.getSodu());
            pstmt.setString(8,kh.getTrangthai());

            int rowUpdate = pstmt.executeUpdate();

            conn.close();
            pstmt.close();
            return rowUpdate > 0 ;
        }catch (SQLException e){
            throw new RuntimeException("Lỗi insert KhachHang : " + e.getMessage());

        }
    }
    //cập nhật dữ liệu cho khách hàng
    public boolean update (KhachHang kh){

        KhachHang existing = getById(kh.getMakh());

        //kiểm tra khách hàng tồn tại
        if(existing == null){
            throw new RuntimeException("Lỗi khách hàng không tồn tại !");
        }

        //khách hàng đã bị xóa trước đó
        if(existing.isNgung()){
            throw new RuntimeException("Khách hàng đã bị xóa !");
        }

        //kiểm tra Valid
        validateKhachHang(kh,false);


        String sql = "UPDATE khachhang SET Ho = ? , Ten = ? , SoDienThoai = ? , MatKhau = ? WHERE MaKH = ? AND TrangThai = ?";

        try{

            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1,kh.getHo());
            pstmt.setString(2,kh.getTen());
            pstmt.setString(3,kh.getSodienthoai());
            pstmt.setString(4,kh.getMatkhau());
            pstmt.setString(5,kh.getMakh());
            pstmt.setString(6,"HOATDONG");

            pstmt.executeUpdate();
            pstmt.close();

        }catch (SQLException e){
            throw new RuntimeException("Lỗi update KhachHang : " + e.getMessage());
        }

        return true;
    }

    //Soft Delete theo mã
    public boolean delete (String MaKH){
        KhachHang kh = getById(MaKH);

        //khách hàng không tồn tại
        if(kh == null){
            throw new RuntimeException("Lỗi khách hàng không tồn tại !");
        }
        //đã xóa trước đó
        if(kh.isNgung()){
            throw new RuntimeException("Khách hàng đã được xóa trước đó !");
        }

        if(hasActiveSession(MaKH)){
            throw new RuntimeException("Không thể xóa khách hàng đang có phiên chơi !");
        }

        String sql = "UPDATE khachhang SET TrangThai = ? WHERE MaKH = ?";
        try{
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1,"NGUNG");
            pstmt.setString(2,MaKH);

            int row = pstmt.executeUpdate();


            conn.close();
            pstmt.close();


            return row > 0;


        }catch (SQLException e){
            throw new RuntimeException("Lỗi delete KhachHang " + e.getMessage());
        }

    }

    public String getDeleteWarning (String MaKH ){
        KhachHang kh = getById(MaKH);

        if(kh == null){
            return  "Khách hàng không tồn tại !";
        }

        StringBuilder warning = new StringBuilder();

        if(kh.getSodu() > 0 ){
            warning.append("Khách hàng còn số dư : ")
                    .append(String.format("%,.0f",kh.getSodu()))
                    .append(" đồng. \n ");

        }
        if(hasActivePackage(MaKH)){
            warning.append("Khách hàng còn gói dịch vụ chưa sử dụng hết. \n");
        }

        if(warning.length() > 0 ) {
            warning.append("Bạn có muốn chắc khóa tài khoản này ? ");
            return warning.toString();
        }
        return  null;

    }

    public boolean restore (String MaKH ){
        KhachHang kh = getById(MaKH);

        if(kh == null) {
            throw new RuntimeException("Khách hàng không tồn tại !");
        }
        //chưa xóa trước đó
        if(!kh.isNgung()){
            throw new RuntimeException("Khách hàng trước đó chưa được xóa ");
        }
        String sql = "UPDATE khachhang SET TrangThai = 'HOATDONG' WHERE MaKH = ?";
        try{
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);


            pstmt.setString(1,MaKH);

            int row = pstmt.executeUpdate();


            conn.close();
            pstmt.close();


            return row > 0;


        }catch (SQLException e){
            throw new RuntimeException("Lỗi restore KhachHang " + e.getMessage());
        }

    }

    //Tạo mã tự động
    public  String generateMaKH(){
        String sql = "SELECT MaKH FROM khachhang "+
                "ORDER BY MaKH DESC LIMIT 1";
        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            if(rs.next()){
                String maKH = rs.getString("MaKH");
                //LẤY TỪ VỊ TRÍ THỨ 2
                int num = Integer.parseInt(maKH.substring(2));
                //FORMAT CHO MÃ KHÁCH HÀNG

                conn.close();
                stmt.close();

                return String.format("KH%03d" ,num + 1);
            }

            conn.close();
            stmt.close();
        }catch (SQLException e){
            throw new RuntimeException("Lỗi generateMaKH" + e.getMessage());

        }
        //CHƯA CÓ DATABASE
        return  "KH001";
    }


    /*
    ======================VALIDATION=============
     */
    private void validateKhachHang(KhachHang kh , boolean isInsert){
        // Validate Ho
        if (kh.getHo() == null || kh.getHo().trim().isEmpty()) {
            throw new RuntimeException("Họ không được để trống");
        }
        if (kh.getHo().trim().length() > 50) {
            throw new RuntimeException("Họ không được vượt quá 50 ký tự");
        }

        // Validate Ten
        if (kh.getTen() == null || kh.getTen().trim().isEmpty()) {
            throw new RuntimeException("Tên không được để trống");
        }
        if (kh.getTen().trim().length() > 50) {
            throw new RuntimeException("Tên không được vượt quá 50 ký tự");
        }

        // Validate SoDienThoai (nếu có)
        if (kh.getSodienthoai()!= null && !kh.getSodienthoai().trim().isEmpty()) {
            String sdt = kh.getSodienthoai().trim();
            if (!sdt.matches("^0\\d{9}$")) {
                throw new RuntimeException("Số điện thoại không hợp lệ (phải có 10 số, bắt đầu bằng 0)");
            }
        }

        // Validate TenDangNhap (chỉ khi Insert)
        if (isInsert) {
            if (kh.getTendangnhap() == null || kh.getTendangnhap().trim().isEmpty()) {
                throw new RuntimeException("Tên đăng nhập không được để trống");
            }
            String tenDN = kh.getTendangnhap().trim();
            if (tenDN.length() < 4) {
                throw new RuntimeException("Tên đăng nhập phải có ít nhất 4 ký tự");
            }
            if (tenDN.length() > 50) {
                throw new RuntimeException("Tên đăng nhập không được vượt quá 50 ký tự");
            }
            if (!tenDN.matches("^[a-zA-Z0-9_]+$")) {
                throw new RuntimeException("Tên đăng nhập chỉ được chứa chữ cái, số và dấu gạch dưới");
            }
        }

        // Validate MatKhau
        if (kh.getMatkhau() == null || kh.getMatkhau().isEmpty()) {
            throw new RuntimeException("Mật khẩu không được để trống");
        }
        if (kh.getMatkhau().length() < 6) {
            throw new RuntimeException("Mật khẩu phải có ít nhất 6 ký tự");
        }


    }

    public boolean isTenDangNhapExists(String tendangnhap) {
        if (tendangnhap == null || tendangnhap.trim().isEmpty()) {
            return false;
        }

        String sql = "SELECT COUNT(*) FROM khachhang WHERE TenDangNhap = ?";
        boolean exists = false; // Biến trung gian để lưu kết quả

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, tendangnhap);
            ResultSet rs = pstmt.executeQuery();

            // 1. ĐỌC DỮ LIỆU TRƯỚC
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }

            // 2. SAU ĐÓ MỚI ĐÓNG KẾT NỐI
            rs.close();
            pstmt.close();
            conn.close();

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi isTenDangNhapExists KhachHang " + e.getMessage());
        }

        return exists; // Trả về kết quả đã lấy được
    }

    private boolean hasActiveSession(String MaKH) {
        String sql = "SELECT COUNT(*) FROM phiensudung WHERE MaKH = ? AND TrangThai = 'DANGCHOI'";
        boolean hasSession = false; // Biến lưu kết quả

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, MaKH);
            ResultSet rs = pstmt.executeQuery();

            // 1. ĐỌC DỮ LIỆU TRƯỚC
            if (rs.next()) {
                hasSession = rs.getInt(1) > 0;
            }

            // 2. SAU ĐÓ MỚI ĐÓNG KẾT NỐI
            rs.close();
            pstmt.close();
            conn.close();

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi hasActiveSession KhachHang " + e.getMessage());
        }
        return hasSession;
    }
    //Tìm khách hàng theo Id
    public KhachHang getById(String MaKH){
        KhachHang kh = null;
        String sql = "SELECT * FROM khachhang WHERE MaKH = ?";

        try{
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);


            pstmt.setString(1,MaKH);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()){
                kh = mapResultSetToEntity(rs);
            }


            conn.close();
            pstmt.close();
        }catch (SQLException e){
            throw new RuntimeException("Lỗi getById KhachHang : " +e.getMessage());

        }
        return kh;
    }



    //update số dư sử dụng cho đăng nhập

    public boolean updateSoDu (String makh ,double sodu){

        KhachHang existing = getById(makh);

        //kiểm tra khách hàng tồn tại
        if(existing == null){
            throw new RuntimeException("Lỗi khách hàng không tồn tại !");
        }

        //khách hàng đã bị xóa trước đó
        if(existing.isNgung()){
            throw new RuntimeException("Khách hàng đã bị xóa !");
        }

        //kiểm tra Valid
        validateKhachHang(existing,false);


        String sql = "UPDATE khachhang SET SoDu = ? WHERE MaKH = ?";

        try{

            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

           pstmt.setDouble(1,sodu);
           pstmt.setString(2,makh);

            pstmt.executeUpdate();
            pstmt.close();

        }catch (SQLException e){
            throw new RuntimeException("Lỗi update KhachHang : " + e.getMessage());
        }

        return true;
    }

    //Chuyển từ ResultSet -> KhachHang
    private KhachHang mapResultSetToEntity(ResultSet rs) throws SQLException {
        KhachHang kh = new KhachHang();
        kh.setMakh(rs.getString("MaKH"));
        kh.setHo(rs.getString("Ho"));
        kh.setTen(rs.getString("Ten"));
        kh.setSodienthoai(rs.getString("SoDienThoai"));
        kh.setTendangnhap(rs.getString("TenDangNhap"));
        kh.setMatkhau(rs.getString("MatKhau"));
        kh.setSodu(rs.getDouble("SoDu"));


        try{
            kh.setTrangthai(rs.getString("TrangThai"));
        }
        catch (SQLException e){
            kh.setTrangthai("HOATDONG");
        }
        return kh;
    }
    private boolean hasActivePackage(String maKH) {
        String sql = "SELECT COUNT(*) FROM goidichvu_khachhang " +
                "WHERE MaKH = ? AND TrangThai = 'CONHAN' AND SoGioConLai > 0 AND NgayHetHan > NOW()";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, maKH);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            // Bảng có thể chưa có, bỏ qua lỗi
        }

        return false;
    }

    // update lại số dư của khách hàng (dùng trong hàm insert của GoiDichVuKhachHangBUS)
    public boolean updateSoDuKhiMuaGoi(KhachHang kh) throws Exception{
        Connection conn1 = ConnectionManager.getConnection();
        String sql = "UPDATE khachhang SET SoDu = ? WHERE MaKH = ?";
        try (PreparedStatement ps = conn1.prepareStatement(sql)) {
            ps.setDouble(1, kh.getSodu());
            ps.setString(2, kh.getMakh());
            return ps.executeUpdate() > 0;
        }catch(Exception e){
            throw new Exception("Lỗi updateSoDuKhiMuaGoi - KhachHangDAO: " + e.getMessage());
        }
    }
}