package entity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class PhieuNhapHang {
    private String maPhieuNhap;
    private String maNCC;
    private String maNV;
    private Timestamp ngayNhap;
    private double tongTien;

    private String trangThai; // CHODUYET, DANHAP, DAHUY

    private List<ChiTietPhieuNhap> chiTietList = new ArrayList<>();

    public PhieuNhapHang() {}

    public String getMaPhieuNhap() {
        return maPhieuNhap;
    }

    public void setMaPhieuNhap(String maPhieuNhap) {
        this.maPhieuNhap = maPhieuNhap;
    }

    public String getMaNCC() {
        return maNCC;
    }

    public void setMaNCC(String maNCC) {
        this.maNCC = maNCC;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public Timestamp getNgayNhap() {
        return ngayNhap;
    }

    public void setNgayNhap(Timestamp ngayNhap) {
        this.ngayNhap = ngayNhap;
    }

    public double getTongTien() {
        return tongTien;
    }

    public void setTongTien(double tongTien) {
        this.tongTien = tongTien;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public List<ChiTietPhieuNhap> getChiTietList() {
        return chiTietList;
    }

    public void setChiTietList(List<ChiTietPhieuNhap> chiTietList) {
        this.chiTietList = chiTietList;
    }
}