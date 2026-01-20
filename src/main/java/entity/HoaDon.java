package entity;

import java.time.LocalDateTime;

public class HoaDon {
    private String mahd;
    private String maphien;
    private String makh;
    private String manv;
    private LocalDateTime ngaylap;
    private double tiengiochoi;
    private double tiendichvu;
    private double tongtien;
    private double giamgia;
    private double thanhtoan;
    private String phuongthuctt;
    private String trangthai;

    public HoaDon() {
    }

    public HoaDon(String mahd, String maphien, String makh, String manv, LocalDateTime ngaylap,
                  double tiengiochoi, double tiendichvu, double tongtien,
                  double giamgia, double thanhtoan, String phuongthuctt, String trangthai) {
        this.mahd = mahd;
        this.maphien = maphien;
        this.makh = makh;
        this.manv = manv;
        this.ngaylap = ngaylap;
        this.tiengiochoi = tiengiochoi;
        this.tiendichvu = tiendichvu;
        this.tongtien = tongtien;
        this.giamgia = giamgia;
        this.thanhtoan = thanhtoan;
        this.phuongthuctt = phuongthuctt;
        this.trangthai = trangthai;
    }

    public String getMahd() {
        return mahd;
    }

    public void setMahd(String mahd) {
        this.mahd = mahd;
    }

    public String getMaphien() {
        return maphien;
    }

    public void setMaphien(String maphien) {
        this.maphien = maphien;
    }

    public String getMakh() {
        return makh;
    }

    public void setMakh(String makh) {
        this.makh = makh;
    }

    public String getManv() {
        return manv;
    }

    public void setManv(String manv) {
        this.manv = manv;
    }

    public LocalDateTime getNgaylap() {
        return ngaylap;
    }

    public void setNgaylap(LocalDateTime ngaylap) {
        this.ngaylap = ngaylap;
    }

    public double getTiengiochoi() {
        return tiengiochoi;
    }

    public void setTiengiochoi(double tiengiochoi) {
        this.tiengiochoi = tiengiochoi;
    }

    public double getTiendichvu() {
        return tiendichvu;
    }

    public void setTiendichvu(double tiendichvu) {
        this.tiendichvu = tiendichvu;
    }

    public double getTongtien() {
        return tongtien;
    }

    public void setTongtien(double tongtien) {
        this.tongtien = tongtien;
    }

    public double getGiamgia() {
        return giamgia;
    }

    public void setGiamgia(double giamgia) {
        this.giamgia = giamgia;
    }

    public double getThanhtoan() {
        return thanhtoan;
    }

    public void setThanhtoan(double thanhtoan) {
        this.thanhtoan = thanhtoan;
    }

    public String getPhuongthuctt() {
        return phuongthuctt;
    }

    public void setPhuongthuctt(String phuongthuctt) {
        this.phuongthuctt = phuongthuctt;
    }

    public String getTrangthai() {
        return trangthai;
    }

    public void setTrangthai(String trangthai) {
        this.trangthai = trangthai;
    }
}