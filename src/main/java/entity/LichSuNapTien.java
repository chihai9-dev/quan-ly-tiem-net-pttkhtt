package entity;

import java.time.LocalDateTime;

public class LichSuNapTien {
    private String manap;
    private String makh;
    private String manv;
    private String mactkm;
    private double sotiennap;
    private double khuyenmai;
    private double tongtiencong;
    private double sodutruoc;
    private double sodusau;
    private String phuongthuc;
    private String magiaodich;
    private LocalDateTime ngaynap;

    public LichSuNapTien() {
    }

    public LichSuNapTien(String manap, String makh, String manv, String mactkm, double sotiennap,
                         double khuyenmai, double tongtiencong, double sodutruoc,
                         double sodusau, String phuongthuc, String magiaodich, LocalDateTime ngaynap) {
        this.manap = manap;
        this.makh = makh;
        this.manv = manv;
        this.mactkm = mactkm;
        this.sotiennap = sotiennap;
        this.khuyenmai = khuyenmai;
        this.tongtiencong = tongtiencong;
        this.sodutruoc = sodutruoc;
        this.sodusau = sodusau;
        this.phuongthuc = phuongthuc;
        this.magiaodich = magiaodich;
        this.ngaynap = ngaynap;
    }

    public String getManap() {
        return manap;
    }

    public void setManap(String manap) {
        this.manap = manap;
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

    public String getMactkm() {
        return mactkm;
    }

    public void setMactkm(String mactkm) {
        this.mactkm = mactkm;
    }

    public double getSotiennap() {
        return sotiennap;
    }

    public void setSotiennap(double sotiennap) {
        this.sotiennap = sotiennap;
    }

    public double getKhuyenmai() {
        return khuyenmai;
    }

    public void setKhuyenmai(double khuyenmai) {
        this.khuyenmai = khuyenmai;
    }

    public double getTongtiencong() {
        return tongtiencong;
    }

    public void setTongtiencong(double tongtiencong) {
        this.tongtiencong = tongtiencong;
    }

    public double getSodutruoc() {
        return sodutruoc;
    }

    public void setSodutruoc(double sodutruoc) {
        this.sodutruoc = sodutruoc;
    }

    public double getSodusau() {
        return sodusau;
    }

    public void setSodusau(double sodusau) {
        this.sodusau = sodusau;
    }

    public String getPhuongthuc() {
        return phuongthuc;
    }

    public void setPhuongthuc(String phuongthuc) {
        this.phuongthuc = phuongthuc;
    }

    public String getMagiaodich() {
        return magiaodich;
    }

    public void setMagiaodich(String magiaodich) {
        this.magiaodich = magiaodich;
    }

    public LocalDateTime getNgaynap() {
        return ngaynap;
    }

    public void setNgaynap(LocalDateTime ngaynap) {
        this.ngaynap = ngaynap;
    }
}