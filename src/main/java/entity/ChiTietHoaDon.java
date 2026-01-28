package entity;

public class ChiTietHoaDon {
    private String maCTHD;
    private String maHD;
    private String loaiChiTiet;  // GIOCHOI hoặc DICHVU
    private String moTa;
    private double soLuong;
    private double donGia;
    private double thanhTien;

    public ChiTietHoaDon() {
        this.soLuong = 1.0;
        this.donGia = 0.0;
        this.thanhTien = 0.0;
    }

    public ChiTietHoaDon(String maCTHD, String maHD, String loaiChiTiet,
                         String moTa, double soLuong, double donGia, double thanhTien) {
        this.maCTHD = maCTHD;
        this.maHD = maHD;
        this.loaiChiTiet = loaiChiTiet;
        this.moTa = moTa;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.thanhTien = thanhTien;
    }

    // Getters
    public String getMaCTHD() {
        return maCTHD;
    }

    public String getMaHD() {
        return maHD;
    }

    public String getLoaiChiTiet() {
        return loaiChiTiet;
    }

    public String getMoTa() {
        return moTa;
    }

    public double getSoLuong() {
        return soLuong;
    }

    public double getDonGia() {
        return donGia;
    }

    public double getThanhTien() {
        return thanhTien;
    }

    // Setters
    public void setMaCTHD(String maCTHD) {
        this.maCTHD = maCTHD;
    }

    public void setMaHD(String maHD) {
        this.maHD = maHD;
    }

    public void setLoaiChiTiet(String loaiChiTiet) {
        this.loaiChiTiet = loaiChiTiet;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public void setSoLuong(double soLuong) {
        this.soLuong = soLuong;
    }

    public void setDonGia(double donGia) {
        this.donGia = donGia;
    }

    public void setThanhTien(double thanhTien) {
        this.thanhTien = thanhTien;
    }

    // Phương thức tính toán
    public void tinhThanhTien() {
        this.thanhTien = this.soLuong * this.donGia;
    }

    // Format hiển thị tiền
    public String getDonGiaFormatted() {
        return String.format("%,.0f VND", donGia);
    }

    public String getThanhTienFormatted() {
        return String.format("%,.0f VND", thanhTien);
    }

    // Lấy tên loại chi tiết hiển thị
    public String getTenLoaiChiTiet() {
        if (loaiChiTiet == null) return "";

        switch (loaiChiTiet) {
            case "GIOCHOI":
                return "Giờ chơi";
            case "DICHVU":
                return "Dịch vụ";
            default:
                return loaiChiTiet;
        }
    }

    @Override
    public String toString() {
        return "ChiTietHoaDon{" +
                "maCTHD='" + maCTHD + '\'' +
                ", maHD='" + maHD + '\'' +
                ", loaiChiTiet='" + loaiChiTiet + '\'' +
                ", moTa='" + moTa + '\'' +
                ", soLuong=" + soLuong +
                ", donGia=" + donGia +
                ", thanhTien=" + thanhTien +
                '}';
    }
}