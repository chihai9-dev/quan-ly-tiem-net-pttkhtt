package dao;
import entity.GoiDichVuKhachHang;

import dao.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

/* CÁC METHOD.
    1. List<GoiDichVuKhachHang> getByKhachHang(String maKH): lấy tất cả các dịch vụ bằng mã khách hàng.
    2. boolean insert(GoiDichVuKhachHang newGDVKH, Connection conn1): thêm một gói dịch vụ khách hàng.
    2.1 String generateNextMaGoiKH(Connection conn1): tăng mã tự động.
    3. boolean update(GoiDichVuKhachHang updateGDVKH): chỉnh sửa thông tin gói dịch vụ khách hàng.
    4. boolean getConHieuLuc(String maGoiKH): kiểm tra gói còn hiệu lực không.
    5. GoiDichVuKhachHang getByID(String maGKH): lấy gói dịch vụ bằng mã.
    6. void print(GoiDichVuKhachHang gdv)
*/

public class GoiDichVuKhachHangDAO{
    // Connect với database
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    // LẤY CÁC GOI DỊCH VỤ KHÁCH BẰNG MÃ KHÁCH HÀNG
    public List<GoiDichVuKhachHang> getByKhachHang(String maKH) {
        List<GoiDichVuKhachHang> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM goidichvu_khachhang WHERE MaKH = ?";

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, maKH);
            rs = ps.executeQuery();

            while (rs.next()) {
                String magoikh = rs.getString("MaGoiKH");
                String makh = rs.getString("MaKH");
                String magoi = rs.getString("MaGoi");
                String manv = rs.getString("MaNV");
                double sogiobandau = rs.getDouble("SoGioBanDau");
                double sogioconlai = rs.getDouble("SoGioConLai");
                LocalDateTime ngaymua = rs.getTimestamp("NgayMua").toLocalDateTime();
                LocalDateTime ngayhethan = rs.getTimestamp("NgayHetHan").toLocalDateTime();
                double giamua = rs.getDouble("GiaMua");
                String trangthai = rs.getString("TrangThai");

                GoiDichVuKhachHang gdvkh = new GoiDichVuKhachHang(magoikh, makh, magoi, manv,
                        sogiobandau, sogioconlai, ngaymua, ngayhethan, giamua, trangthai);
                danhSach.add(gdvkh);
            }

            if (danhSach.isEmpty()) {
                return null;
            }

        } catch (SQLException e) {
            System.err.println("[LỖI GETBYKHACHHANG - GoiDichVuKhachHangDAO]: " + e.getMessage());
            return null;
        } finally {
            DBConnection.closeConnection();
        }

        return danhSach;
    }

    public GoiDichVuKhachHang getByMaGoiKhachHang(String maGoiKH) {
        GoiDichVuKhachHang goiKH = new GoiDichVuKhachHang();
        String sql = "SELECT MaGoiKH, MaKH, MaGoi, MaNV, SoGioBanDau, SoGioConLai, NgayMua, " +
                "NgayHetHan, GiaMua, TrangThai " + "FROM goidichvu_khachhang WHERE MaGoiKH = ?";

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, maGoiKH);
            rs = ps.executeQuery();

            if (rs.next()) {
                String magoikh = rs.getString("MaGoiKH");
                String makh = rs.getString("MaKH");
                String magoi = rs.getString("MaGoi");
                String manv = rs.getString("MaNV");
                double sogiobandau = rs.getDouble("SoGioBanDau");
                double sogioconlai = rs.getDouble("SoGioConLai");
                LocalDateTime ngaymua = rs.getTimestamp("NgayMua").toLocalDateTime();
                LocalDateTime ngayhethan = rs.getTimestamp("NgayHetHan").toLocalDateTime();
                double giamua = rs.getDouble("GiaMua");
                String trangthai = rs.getString("TrangThai");

                GoiDichVuKhachHang gdvkh = new GoiDichVuKhachHang(magoikh, makh, magoi, manv,
                        sogiobandau, sogioconlai, ngaymua, ngayhethan, giamua, trangthai);
            }

        } catch (SQLException e) {
            System.err.println("[LỖI GETBYMAGOIKHACHHANG - GoiDichVuKhachHangDAO]: " + e.getMessage());
            return null;
        } finally {
            DBConnection.closeConnection();
        }

        return goiKH;
    }

    /*
    Phương thức insert: tạo thêm một ghi.
    paramter: GoiDichVuKhachHang newGDVKH.
    return: true/false
    */
    public boolean insert(GoiDichVuKhachHang newGDVKH, Connection conn1) {
        String sql = "INSERT INTO goidichvu_khachhang (MaGoiKH, MaKH, MaGoi, MaNV, SoGioBanDau, SoGioConLai" +
                ", NgayMua, NgayHetHan, GiaMua, TrangThai) " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            // Sử dụng conn1 được truyền từ BUS xuống thay vì tạo kết nối mới
            PreparedStatement ps = conn1.prepareStatement(sql);

            ps.setString(1, this.generateNextMaGoiKH(conn1));
            ps.setString(2, newGDVKH.getMakh());
            ps.setString(3, newGDVKH.getMagoi());
            ps.setString(4, newGDVKH.getManv());
            ps.setDouble(5, newGDVKH.getSogiobandau());
            ps.setDouble(6, newGDVKH.getSogioconlai());
            ps.setTimestamp(7, Timestamp.valueOf(newGDVKH.getNgaymua()));
            ps.setTimestamp(8, Timestamp.valueOf(newGDVKH.getNgayhethan()));
            ps.setDouble(9, newGDVKH.getGiamua());
            ps.setString(10, "CONHAN");

            int rowAffected = ps.executeUpdate();

            // Lưu ý: Không được đóng ps hoặc conn1 ở đây vì nó đang dùng chung Transaction với BUS
            return rowAffected > 0;

        } catch (SQLException e) {
            System.err.println("[LỖI INSERT - GoiDichVuKhachHangDAO]: " + e.getMessage());
            return false;
        }
    }

    // TĂNG MÃ TỰ ĐỘNG
    private String generateNextMaGoiKH(Connection conn1) {
        String sql = "SELECT MaGoiKH FROM goidichvu_khachhang ORDER BY MaGoiKH DESC LIMIT 1";
        String nextID = "GOIKH001";

        try {
            PreparedStatement ps1 = conn1.prepareStatement(sql);
            ResultSet rs1 = ps1.executeQuery();

            if (rs1.next()) {
                String lastID = rs1.getString("MaGoiKH");
                int number = Integer.parseInt(lastID.substring(5));
                number++;
                nextID = String.format("GOIKH%03d", number);
            }
        } catch (SQLException e) {
            System.err.println("[LỖI TỰ TĂNG MÃ - GoiDichVuKhachHangDAO]: " + e.getMessage());
        }
        return nextID;
    }

    // CHỈNH SỬA THÔNG TIN
    public boolean update(GoiDichVuKhachHang updateGDVKH) {
        String sql = "UPDATE goidichvu_khachhang SET SoGioConLai = ?" +
                ", TrangThai = ? WHERE MaGoiKH = ?";
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);

            ps.setDouble(1, updateGDVKH.getSogioconlai());
            ps.setString(2, updateGDVKH.getTrangthai());
            ps.setString(3, updateGDVKH.getMagoikh());

            int rowAffected = ps.executeUpdate();

            return rowAffected > 0;
        } catch (SQLException e) {
            System.err.println("[LỖI UPDATE - GoiDichVuKhachHangDAO]: " + e.getMessage());
            return false;
        } finally {
            DBConnection.closeConnection();
        }
    }

    // KIỂM TRA HIỆU LỰC CỦA GÓI
    public boolean getConHieuLuc(String maGoiKH) {
        String sql = "SELECT NgayHetHan, TrangThai FROM goidichvu_khachhang WHERE MaGoiKH = ?";

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, maGoiKH);
            rs = ps.executeQuery();

            if (rs.next()) {
                Timestamp ngayHetHanTs = rs.getTimestamp("NgayHetHan");
                String trangThai = rs.getString("TrangThai");

                // Nếu không có dữ liệu ngày hoặc trạng thái (đề phòng dữ liệu lỗi)
                if (ngayHetHanTs == null || trangThai == null) return false;

                LocalDateTime ngayHetHan = ngayHetHanTs.toLocalDateTime();
                LocalDateTime bayGio = LocalDateTime.now();

                // Điều kiện còn hiệu lực: Trạng thái là CONHAN và thời gian hiện tại trước ngày hết hạn
                if (trangThai.equalsIgnoreCase("CONHAN") && bayGio.isBefore(ngayHetHan)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                // Không tìm thấy mã gói trong database
                return false;
            }

        } catch (SQLException e) {
            System.err.println("[LỖI GETCONHIEULUC - GoiDichVuKhachHangDAO]: " + e.getMessage());
            return false;
        } finally {
            DBConnection.closeConnection();
        }
    }

    // LẤY GÓI DỊCH VỤ KHÁCH HÀNG BẰNG MÃ
    public GoiDichVuKhachHang getByID(String maGKH){
        String sql = "SELECT * FROM goidichvu_khachhang WHERE MaGoiKH = ?";
        GoiDichVuKhachHang result = null;
        try{
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, maGKH);
            rs = ps.executeQuery();
            if(rs.next()){
                String magoikh = rs.getString("MaGoiKH");
                String makh = rs.getString("MaKH");
                String magoi = rs.getString("MaGoi");
                String manv = rs.getString("MaNV");
                double sogiobandau = rs.getDouble("SoGioBanDau");
                double sogioconlai = rs.getDouble("SoGioConLai");
                LocalDateTime ngaymua = rs.getTimestamp("NgayMua").toLocalDateTime();
                LocalDateTime ngayhethan = rs.getTimestamp("NgayHetHan").toLocalDateTime();
                double giamua = rs.getDouble("GiaMua");
                String trangthai = rs.getString("TrangThai");

                result = new GoiDichVuKhachHang(magoikh, makh, magoi, manv,
                        sogiobandau, sogioconlai, ngaymua, ngayhethan, giamua, trangthai);
            }
        }catch(Exception e){
            System.out.println("Lỗi getByID -GoiDichVuKhachHangDAO: " + e.getMessage());
        }finally{
            DBConnection.closeConnection();
        }
        return result;
    }

    //  IN THÔNG TIN
    public void print(GoiDichVuKhachHang gdv) {
        System.out.println("MaGoiKH: " + gdv.getMagoikh() + " | MaKH: " + gdv.getMakh()
                + " | MaGoi: " + gdv.getMagoi() + " | MaNV: " + gdv.getManv()
                + " | SoGioBanDau: " + gdv.getSogiobandau() + " | SoGioConLai: " + gdv.getSogioconlai()
                + " | NgayMua: " + gdv.getNgaymua() + " | NgayHetHan: " + gdv.getNgayhethan() + " | GiaMua: "
                + gdv.getGiamua() + " | TrangThai: " + gdv.getTrangthai());
    }
}