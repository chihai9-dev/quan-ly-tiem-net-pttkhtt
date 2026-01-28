package entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LichSuNapTien {
    private String maNap;
    private String maKH;
    private String maNV;
    private String maCTKM;
    private double soTienNap;
    private double khuyenMai;
    private double tongTienCong;
    private double soDuTruoc;
    private double soDuSau;
    private String phuongThuc;
    private String maGiaoDich;
    private LocalDateTime ngayNap;

    public LichSuNapTien() {
        this.ngayNap = LocalDateTime.now();
        this.soTienNap = 0.0;
        this.khuyenMai = 0.0;
        this.tongTienCong = 0.0;
        this.soDuTruoc = 0.0;
        this.soDuSau = 0.0;
    }

    public LichSuNapTien(String maNap, String maKH, String maNV, String maCTKM,
                         double soTienNap, double khuyenMai, double tongTienCong,
                         double soDuTruoc, double soDuSau, String phuongThuc,
                         String maGiaoDich, LocalDateTime ngayNap) {
        this.maNap = maNap;
        this.maKH = maKH;
        this.maNV = maNV;
        this.maCTKM = maCTKM;
        this.soTienNap = soTienNap;
        this.khuyenMai = khuyenMai;
        this.tongTienCong = tongTienCong;
        this.soDuTruoc = soDuTruoc;
        this.soDuSau = soDuSau;
        this.phuongThuc = phuongThuc;
        this.maGiaoDich = maGiaoDich;
        this.ngayNap = ngayNap;
    }

    // Getters
    public String getMaNap() {
        return maNap;
    }

    public String getMaKH() {
        return maKH;
    }

    public String getMaNV() {
        return maNV;
    }

    public String getMaCTKM() {
        return maCTKM;
    }

    public double getSoTienNap() {
        return soTienNap;
    }

    public double getKhuyenMai() {
        return khuyenMai;
    }

    public double getTongTienCong() {
        return tongTienCong;
    }

    public double getSoDuTruoc() {
        return soDuTruoc;
    }

    public double getSoDuSau() {
        return soDuSau;
    }

    public String getPhuongThuc() {
        return phuongThuc;
    }

    public String getMaGiaoDich() {
        return maGiaoDich;
    }

    public LocalDateTime getNgayNap() {
        return ngayNap;
    }

    // Setters
    public void setMaNap(String maNap) {
        this.maNap = maNap;
    }

    public void setMaKH(String maKH) {
        this.maKH = maKH;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public void setMaCTKM(String maCTKM) {
        this.maCTKM = maCTKM;
    }

    public void setSoTienNap(double soTienNap) {
        this.soTienNap = soTienNap;
    }

    public void setKhuyenMai(double khuyenMai) {
        this.khuyenMai = khuyenMai;
    }

    public void setTongTienCong(double tongTienCong) {
        this.tongTienCong = tongTienCong;
    }

    public void setSoDuTruoc(double soDuTruoc) {
        this.soDuTruoc = soDuTruoc;
    }

    public void setSoDuSau(double soDuSau) {
        this.soDuSau = soDuSau;
    }

    public void setPhuongThuc(String phuongThuc) {
        this.phuongThuc = phuongThuc;
    }

    public void setMaGiaoDich(String maGiaoDich) {
        this.maGiaoDich = maGiaoDich;
    }

    public void setNgayNap(LocalDateTime ngayNap) {
        this.ngayNap = ngayNap;
    }

    // Phương thức tính toán tự động
    public void tinhTongTienCong() {
        this.tongTienCong = this.soTienNap + this.khuyenMai;
    }

    public void tinhSoDuSau() {
        this.soDuSau = this.soDuTruoc + this.tongTienCong;
    }

    // Format hiển thị tiền
    public String getSoTienNapFormatted() {
        return String.format("%,.0f VND", soTienNap);
    }

    public String getKhuyenMaiFormatted() {
        return String.format("%,.0f VND", khuyenMai);
    }

    public String getTongTienCongFormatted() {
        return String.format("%,.0f VND", tongTienCong);
    }

    public String getSoDuTruocFormatted() {
        return String.format("%,.0f VND", soDuTruoc);
    }

    public String getSoDuSauFormatted() {
        return String.format("%,.0f VND", soDuSau);
    }

    // Format hiển thị ngày
    public String getNgayNapFormatted() {
        if (ngayNap != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            return ngayNap.format(formatter);
        }
        return "";
    }

    // Lấy tên phương thức thanh toán hiển thị
    public String getTenPhuongThuc() {
        if (phuongThuc == null) return "";

        switch (phuongThuc) {
            case "TIENMAT":
                return "Tiền mặt";
            case "MOMO":
                return "MoMo";
            case "CHUYENKHOAN":
                return "Chuyển khoản";
            case "VNPAY":
                return "VNPay";
            case "THE":
                return "Thẻ ngân hàng";
            default:
                return phuongThuc;
        }
    }

    @Override
    public String toString() {
        return "LichSuNapTien{" +
                "maNap='" + maNap + '\'' +
                ", maKH='" + maKH + '\'' +
                ", maNV='" + maNV + '\'' +
                ", maCTKM='" + maCTKM + '\'' +
                ", soTienNap=" + soTienNap +
                ", khuyenMai=" + khuyenMai +
                ", tongTienCong=" + tongTienCong +
                ", soDuTruoc=" + soDuTruoc +
                ", soDuSau=" + soDuSau +
                ", phuongThuc='" + phuongThuc + '\'' +
                ", maGiaoDich='" + maGiaoDich + '\'' +
                ", ngayNap=" + ngayNap +
                '}';
    }
}