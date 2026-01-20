package entity;

import java.math.BigDecimal;

public class ChiTietPhieuNhap {
    private String mactpn;
    private String maphieunhap;
    private String madv;
    private int soluong;
    private double gianhap;
    private double thanhtien;

    public ChiTietPhieuNhap() {
    }

    public ChiTietPhieuNhap(String mactpn, String maphieunhap, String madv, int soluong,
                            double gianhap, double thanhtien) {
        this.mactpn = mactpn;
        this.maphieunhap = maphieunhap;
        this.madv = madv;
        this.soluong = soluong;
        this.gianhap = gianhap;
        this.thanhtien = thanhtien;
    }

    public String getMactpn() {
        return mactpn;
    }

    public void setMactpn(String mactpn) {
        this.mactpn = mactpn;
    }

    public String getMaphieunhap() {
        return maphieunhap;
    }

    public void setMaphieunhap(String maphieunhap) {
        this.maphieunhap = maphieunhap;
    }

    public String getMadv() {
        return madv;
    }

    public void setMadv(String madv) {
        this.madv = madv;
    }

    public int getSoluong() {
        return soluong;
    }

    public void setSoluong(int soluong) {
        this.soluong = soluong;
    }

    public double getGianhap() {
        return gianhap;
    }

    public void setGianhap(double gianhap) {
        this.gianhap = gianhap;
    }

    public double getThanhtien() {
        return thanhtien;
    }

    public void setThanhtien(double thanhtien) {
        this.thanhtien = thanhtien;
    }
}