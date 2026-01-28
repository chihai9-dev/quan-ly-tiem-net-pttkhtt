package entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HoaDon {
    private String maHD;
    private String maPhien;
    private String maKH;
    private String maNV;
    private LocalDateTime ngayLap;
    private double tienGioChoi;
    private double tienDichVu;
    private double tongTien;
    private double giamGia;
    private double thanhToan;
    private String phuongThucTT;
    private String trangThai;

    public HoaDon() {
        this.ngayLap = LocalDateTime.now();
        this.tienGioChoi = 0.0;
        this.tienDichVu = 0.0;
        this.tongTien = 0.0;
        this.giamGia = 0.0;
        this.thanhToan = 0.0;
        this.trangThai = "CHUA";
    }

    public HoaDon(String maHD, String maPhien, String maKH, String maNV,
                  LocalDateTime ngayLap, double tienGioChoi, double tienDichVu,
                  double tongTien, double giamGia, double thanhToan,
                  String phuongThucTT, String trangThai) {
        this.maHD = maHD;
        this.maPhien = maPhien;
        this.maKH = maKH;
        this.maNV = maNV;
        this.ngayLap = ngayLap;
        this.tienGioChoi = tienGioChoi;
        this.tienDichVu = tienDichVu;
        this.tongTien = tongTien;
        this.giamGia = giamGia;
        this.thanhToan = thanhToan;
        this.phuongThucTT = phuongThucTT;
        this.trangThai = trangThai;
    }

    // Getters
    public String getMaHD() {
        return maHD;
    }

    public String getMaPhien() {
        return maPhien;
    }

    public String getMaKH() {
        return maKH;
    }

    public String getMaNV() {
        return maNV;
    }

    public LocalDateTime getNgayLap() {
        return ngayLap;
    }

    public double getTienGioChoi() {
        return tienGioChoi;
    }

    public double getTienDichVu() {
        return tienDichVu;
    }

    public double getTongTien() {
        return tongTien;
    }

    public double getGiamGia() {
        return giamGia;
    }

    public double getThanhToan() {
        return thanhToan;
    }

    public String getPhuongThucTT() {
        return phuongThucTT;
    }

    public String getTrangThai() {
        return trangThai;
    }

    // Setters
    public void setMaHD(String maHD) {
        this.maHD = maHD;
    }

    public void setMaPhien(String maPhien) {
        this.maPhien = maPhien;
    }

    public void setMaKH(String maKH) {
        this.maKH = maKH;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public void setNgayLap(LocalDateTime ngayLap) {
        this.ngayLap = ngayLap;
    }

    public void setTienGioChoi(double tienGioChoi) {
        this.tienGioChoi = tienGioChoi;
    }

    public void setTienDichVu(double tienDichVu) {
        this.tienDichVu = tienDichVu;
    }

    public void setTongTien(double tongTien) {
        this.tongTien = tongTien;
    }

    public void setGiamGia(double giamGia) {
        this.giamGia = giamGia;
    }

    public void setThanhToan(double thanhToan) {
        this.thanhToan = thanhToan;
    }

    public void setPhuongThucTT(String phuongThucTT) {
        this.phuongThucTT = phuongThucTT;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    // Phương thức tính toán tự động
    public void tinhTongTien() {
        this.tongTien = this.tienGioChoi + this.tienDichVu;
    }

    public void tinhThanhToan() {
        this.thanhToan = this.tongTien - this.giamGia;
    }

    // Format hiển thị tiền
    public String getTienGioChoiFormatted() {
        return String.format("%,.0f VND", tienGioChoi);
    }

    public String getTienDichVuFormatted() {
        return String.format("%,.0f VND", tienDichVu);
    }

    public String getTongTienFormatted() {
        return String.format("%,.0f VND", tongTien);
    }

    public String getGiamGiaFormatted() {
        return String.format("%,.0f VND", giamGia);
    }

    public String getThanhToanFormatted() {
        return String.format("%,.0f VND", thanhToan);
    }

    // Format hiển thị ngày
    public String getNgayLapFormatted() {
        if (ngayLap != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            return ngayLap.format(formatter);
        }
        return "";
    }

    @Override
    public String toString() {
        return "HoaDon{" +
                "maHD='" + maHD + '\'' +
                ", maPhien='" + maPhien + '\'' +
                ", maKH='" + maKH + '\'' +
                ", maNV='" + maNV + '\'' +
                ", ngayLap=" + ngayLap +
                ", tienGioChoi=" + tienGioChoi +
                ", tienDichVu=" + tienDichVu +
                ", tongTien=" + tongTien +
                ", giamGia=" + giamGia +
                ", thanhToan=" + thanhToan +
                ", phuongThucTT='" + phuongThucTT + '\'' +
                ", trangThai='" + trangThai + '\'' +
                '}';
    }
}