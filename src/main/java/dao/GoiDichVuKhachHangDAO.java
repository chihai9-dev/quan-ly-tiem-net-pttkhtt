package dao;
import entity.GoiDichVuKhachHang;

import dao.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class GoiDichVuKhachHangDAO{
    // Connect với database
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    /*
    Phương thức getByKhachHang: lấy tất cả các ghi có mã khách hàng đó.
    paramter: MaKH.
    return: List<GoiDichVuKhachHang>/ null(nếu không có).
    */
    public List<GoiDichVuKhachHang> getByKhachHang(String maKH) {
        List<GoiDichVuKhachHang> danhSach = new ArrayList<>();
        String sql = "SELECT MaGoiKH, MaKH, MaGoi, MaNV, SoGioBanDau, SoGioConLai, NgayMua, " +
                "NgayHetHan, GiaMua, TrangThai " + "FROM goidichvu_khachhang WHERE MaKH = ?";

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

    /*
    Phương thức insert: tạo thêm một ghi.
    paramter: GoiDichVuKhachHang newGDVKH.
    return: true/false
    */
    public boolean insert(GoiDichVuKhachHang newGDVKH) {
        String sql = "INSERT INTO goidichvu_khachhang (MaGoiKH, MaKH, MaGoi, MaNV, SoGioBanDau, SoGioConLai" +
                ", NgayMua, NgayHetHan, GiaMua, TrangThai) " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);

            ps.setString(1, this.generateNextMaGoiKH());
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

            return rowAffected > 0;
        } catch (SQLException e) {
            System.err.println("[LỖI INSERT - GoiDichVuKhachHangDAO]: " + e.getMessage());
            return false;
        } finally {
            DBConnection.closeConnection();
        }
    }

    private String generateNextMaGoiKH() {
        String sql = "SELECT MaGoiKH FROM goidichvu_khachhang ORDER BY MaGoiKH DESC LIMIT 1";
        String nextID = "GOIKH001";

        try {
            PreparedStatement ps1 = conn.prepareStatement(sql);
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

    /*
    Phương thức update: cho phép sửa SoGioConLai và TrangThai
    parameter: GoiDichVuKhachHang.
    return: true/false.
    */
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

    /*
    Phương thức getConHieuLuc: Kiểm tra gói dịch vụ có còn hạn và chưa hết thời gian không.
    parameter: maGoiKH (Mã gói dịch vụ của khách hàng).
    return:
        - Boolean.TRUE: Nếu tìm thấy mã và còn hiệu lực.
        - Boolean.FALSE: Nếu tìm thấy mã nhưng đã hết hạn/hết thời gian.
        - null: Nếu không tìm thấy mã gói này trong hệ thống.
    */
    public Boolean getConHieuLuc(String maGoiKH) {
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
                return null;
            }

        } catch (SQLException e) {
            System.err.println("[LỖI GETCONHIEULUC - GoiDichVuKhachHangDAO]: " + e.getMessage());
            return null;
        } finally {
            DBConnection.closeConnection();
        }
    }

    public void print(GoiDichVuKhachHang gdv) {
        System.out.println("MaGoiKH: " + gdv.getMagoi() + " | MaKH: " + gdv.getMakh()
                + " | MaGoi: " + gdv.getMagoi() + " | MaNV: " + gdv.getManv()
                + " | SoGioBanDau: " + gdv.getSogiobandau() + " | SoGioConLai: " + gdv.getSogioconlai()
                + " | NgayMua: " + gdv.getNgaymua() + " | NgayHetHan: " + gdv.getNgayhethan() + " | GiaMua: "
                + gdv.getGiamua() + " | TrangThai: " + gdv.getTrangthai());
    }
}
