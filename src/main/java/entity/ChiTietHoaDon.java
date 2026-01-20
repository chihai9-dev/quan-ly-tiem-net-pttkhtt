package entity;

public class ChiTietHoaDon {
    private String macthd;
    private String mahd;
    private String loaichitiet;
    private String mota;
    private double soluong;
    private double dongia;
    private double thanhtien;

    public ChiTietHoaDon() {
    }

    public ChiTietHoaDon( String macthd,  String mahd, String loaichitiet, String mota,
                         double soluong, double dongia, double thanhtien) {
        this.macthd = macthd;
        this.mahd = mahd;
        this.loaichitiet = loaichitiet;
        this.mota = mota;
        this.soluong = soluong;
        this.dongia = dongia;
        this.thanhtien = thanhtien;
    }

    public  String getMacthd() {
        return macthd;
    }

    public void setMacthd( String macthd) {
        this.macthd = macthd;
    }

    public  String getMahd() {
        return mahd;
    }

    public void setMahd(String mahd) {
        this.mahd = mahd;
    }

    public String getLoaichitiet() {
        return loaichitiet;
    }

    public void setLoaichitiet(String loaichitiet) {
        this.loaichitiet = loaichitiet;
    }

    public String getMota() {
        return mota;
    }

    public void setMota(String mota) {
        this.mota = mota;
    }

    public double getSoluong() {
        return soluong;
    }

    public void setSoluong(double soluong) {
        this.soluong = soluong;
    }

    public double getDongia() {
        return dongia;
    }

    public void setDongia(double dongia) {
        this.dongia = dongia;
    }

    public double getThanhtien() {
        return thanhtien;
    }

    public void setThanhtien(double thanhtien) {
        this.thanhtien = thanhtien;
    }
}