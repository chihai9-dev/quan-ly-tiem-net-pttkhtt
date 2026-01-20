package entity;

import java.math.BigDecimal;

public class DichVu {
    private String madv;
    private String tendv;
    private String loaidv;
    private double dongia;
    private String donvitinh;
    private int soluongton;
    private String trangthai;

    public DichVu() {
    }

    public DichVu(String madv, String tendv, String loaidv, double dongia,
                  String donvitinh, int soluongton, String trangthai) {
        this.madv = madv;
        this.tendv = tendv;
        this.loaidv = loaidv;
        this.dongia = dongia;
        this.donvitinh = donvitinh;
        this.soluongton = soluongton;
        this.trangthai = trangthai;
    }

    public String getMadv() {
        return madv;
    }

    public void setMadv(String madv) {
        this.madv = madv;
    }

    public String getTendv() {
        return tendv;
    }

    public void setTendv(String tendv) {
        this.tendv = tendv;
    }

    public String getLoaidv() {
        return loaidv;
    }

    public void setLoaidv(String loaidv) {
        this.loaidv = loaidv;
    }

    public double getDongia() {
        return dongia;
    }

    public void setDongia(double dongia) {
        this.dongia = dongia;
    }

    public String getDonvitinh() {
        return donvitinh;
    }

    public void setDonvitinh(String donvitinh) {
        this.donvitinh = donvitinh;
    }

    public int getSoluongton() {
        return soluongton;
    }

    public void setSoluongton(int soluongton) {
        this.soluongton = soluongton;
    }

    public String getTrangthai() {
        return trangthai;
    }

    public void setTrangthai(String trangthai) {
        this.trangthai = trangthai;
    }
}