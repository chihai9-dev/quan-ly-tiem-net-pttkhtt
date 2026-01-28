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
        return this.madv;
    }

    public void setMadv(String madv) {
        this.madv = madv;
    }

    public String getTendv() {
        return this.tendv;
    }

    public void setTendv(String tendv) {
        this.tendv = tendv;
    }

    public String getLoaidv() {
        return this.loaidv;
    }

    public void setLoaidv(String loaidv) {
        this.loaidv = loaidv;
    }

    public double getDongia() {
        return this.dongia;
    }

    public void setDongia(double dongia) {
        this.dongia = dongia;
    }

    public String getDonvitinh() {
        return this.donvitinh;
    }

    public void setDonvitinh(String donvitinh) {
        this.donvitinh = donvitinh;
    }

    public int getSoluongton() {
        return this.soluongton;
    }

    public void setSoluongton(int soluongton) {
        this.soluongton = soluongton;
    }

    public String getTrangthai() {
        return this.trangthai;
    }

    public void setTrangthai(String trangthai) {
        this.trangthai = trangthai;
    }
}