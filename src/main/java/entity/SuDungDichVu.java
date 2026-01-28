package entity;

import java.time.LocalDateTime;

public class SuDungDichVu {
    private String masd;
    private String maphien;
    private String madv;
    private int soluong;
    private double dongia;
    private double thanhtien;
    private LocalDateTime thoigian;

    public SuDungDichVu() {
    }

    public SuDungDichVu(String masd, String maphien, String madv, int soluong,
                        double dongia, double thanhtien, LocalDateTime thoigian) {
        this.masd = masd;
        this.maphien = maphien;
        this.madv = madv;
        this.soluong = soluong;
        this.dongia = dongia;
        this.thanhtien = thanhtien;
        this.thoigian = thoigian;
    }

    public String getMasd() {
        return this.masd;
    }

    public void setMasd(String masd) {
        this.masd = masd;
    }

    public String getMaphien() {
        return this.maphien;
    }

    public void setMaphien(String maphien) {
        this.maphien = maphien;
    }

    public String getMadv() {
        return this.madv;
    }

    public void setMadv(String madv) {
        this.madv = madv;
    }

    public int getSoluong() {
        return this.soluong;
    }

    public void setSoluong(int soluong) {
        this.soluong = soluong;
    }

    public double getDongia() {
        return this.dongia;
    }

    public void setDongia(double dongia) {
        this.dongia = dongia;
    }

    public double getThanhtien() {
        return this.thanhtien;
    }

    public void setThanhtien(double thanhtien) {
        this.thanhtien = thanhtien;
    }

    public LocalDateTime getThoigian() {
        return this.thoigian;
    }

    public void setThoigian(LocalDateTime thoigian) {
        this.thoigian = thoigian;
    }
}