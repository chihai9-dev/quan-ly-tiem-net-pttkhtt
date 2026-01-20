package entity;

import java.time.LocalDateTime;

public class PhieuNhapHang {
    private String maphieunhap;
    private String mancc;
    private String manv;
    private LocalDateTime ngaynhap;
    private double tongtien;
    private String trangthai;

    public PhieuNhapHang() {
    }

    public PhieuNhapHang(String maphieunhap, String mancc, String manv, LocalDateTime ngaynhap,
                         double tongtien, String trangthai) {
        this.maphieunhap = maphieunhap;
        this.mancc = mancc;
        this.manv = manv;
        this.ngaynhap = ngaynhap;
        this.tongtien = tongtien;
        this.trangthai = trangthai;
    }

    public String getMaphieunhap() {
        return maphieunhap;
    }

    public void setMaphieunhap(String maphieunhap) {
        this.maphieunhap = maphieunhap;
    }

    public String getMancc() {
        return mancc;
    }

    public void setMancc(String mancc) {
        this.mancc = mancc;
    }

    public String getManv() {
        return manv;
    }

    public void setManv(String manv) {
        this.manv = manv;
    }

    public LocalDateTime getNgaynhap() {
        return ngaynhap;
    }

    public void setNgaynhap(LocalDateTime ngaynhap) {
        this.ngaynhap = ngaynhap;
    }

    public double getTongtien() {
        return tongtien;
    }

    public void setTongtien(double tongtien) {
        this.tongtien = tongtien;
    }

    public String getTrangthai() {
        return trangthai;
    }

    public void setTrangthai(String trangthai) {
        this.trangthai = trangthai;
    }
}