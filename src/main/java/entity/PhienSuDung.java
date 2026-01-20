package entity;

import java.time.LocalDateTime;

public class PhienSuDung {
    private String maphien;
    private String makh;
    private String mamay;
    private String manv;
    private String magoikh;
    private LocalDateTime giobatdau;
    private LocalDateTime gioketthuc;
    private double tonggio;
    private double giosudungtugoi;
    private double giosudungtutaikhoan;
    private double giamoigio;
    private double tiengiochoi;
    private String loaithanhtoan;
    private String trangthai;

    public PhienSuDung() {
    }

    public PhienSuDung(String maphien, String makh, String mamay, String manv, String magoikh,
                       LocalDateTime giobatdau, LocalDateTime gioketthuc, double tonggio,
                       double giosudungtugoi, double giosudungtutaikhoan, double giamoigio,
                       double tiengiochoi, String loaithanhtoan, String trangthai) {
        this.maphien = maphien;
        this.makh = makh;
        this.mamay = mamay;
        this.manv = manv;
        this.magoikh = magoikh;
        this.giobatdau = giobatdau;
        this.gioketthuc = gioketthuc;
        this.tonggio = tonggio;
        this.giosudungtugoi = giosudungtugoi;
        this.giosudungtutaikhoan = giosudungtutaikhoan;
        this.giamoigio = giamoigio;
        this.tiengiochoi = tiengiochoi;
        this.loaithanhtoan = loaithanhtoan;
        this.trangthai = trangthai;
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

    public String getMamay() {
        return mamay;
    }

    public void setMamay(String mamay) {
        this.mamay = mamay;
    }

    public String getManv() {
        return manv;
    }

    public void setManv(String manv) {
        this.manv = manv;
    }

    public String getMagoikh() {
        return magoikh;
    }

    public void setMagoikh(String magoikh) {
        this.magoikh = magoikh;
    }

    public LocalDateTime getGiobatdau() {
        return giobatdau;
    }

    public void setGiobatdau(LocalDateTime giobatdau) {
        this.giobatdau = giobatdau;
    }

    public LocalDateTime getGioketthuc() {
        return gioketthuc;
    }

    public void setGioketthuc(LocalDateTime gioketthuc) {
        this.gioketthuc = gioketthuc;
    }

    public double getTonggio() {
        return tonggio;
    }

    public void setTonggio(double tonggio) {
        this.tonggio = tonggio;
    }

    public double getGiosudungtugoi() {
        return giosudungtugoi;
    }

    public void setGiosudungtugoi(double giosudungtugoi) {
        this.giosudungtugoi = giosudungtugoi;
    }

    public double getGiosudungtutaikhoan() {
        return giosudungtutaikhoan;
    }

    public void setGiosudungtutaikhoan(double giosudungtutaikhoan) {
        this.giosudungtutaikhoan = giosudungtutaikhoan;
    }

    public double getGiamoigio() {
        return giamoigio;
    }

    public void setGiamoigio(double giamoigio) {
        this.giamoigio = giamoigio;
    }

    public double getTiengiochoi() {
        return tiengiochoi;
    }

    public void setTiengiochoi(double tiengiochoi) {
        this.tiengiochoi = tiengiochoi;
    }

    public String getLoaithanhtoan() {
        return loaithanhtoan;
    }

    public void setLoaithanhtoan(String loaithanhtoan) {
        this.loaithanhtoan = loaithanhtoan;
    }

    public String getTrangthai() {
        return trangthai;
    }

    public void setTrangthai(String trangthai) {
        this.trangthai = trangthai;
    }
}