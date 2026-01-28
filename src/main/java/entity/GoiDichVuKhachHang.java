package entity;

import java.time.LocalDateTime;

public class GoiDichVuKhachHang {
    private String magoikh;
    private String makh;
    private String magoi;
    private String manv;
    private double sogiobandau;
    private double sogioconlai;
    private LocalDateTime ngaymua;
    private LocalDateTime ngayhethan;
    private double giamua;
    private String trangthai;

    public GoiDichVuKhachHang() {
    }

    public GoiDichVuKhachHang(String magoikh, String makh, String magoi, String manv,
                              double sogiobandau, double sogioconlai,
                              LocalDateTime ngaymua, LocalDateTime ngayhethan,
                              double giamua, String trangthai) {
        this.magoikh = magoikh;
        this.makh = makh;
        this.magoi = magoi;
        this.manv = manv;
        this.sogiobandau = sogiobandau;
        this.sogioconlai = sogioconlai;
        this.ngaymua = ngaymua;
        this.ngayhethan = ngayhethan;
        this.giamua = giamua;
        this.trangthai = trangthai;
    }

    public String getMagoikh() {
        return this.magoikh;
    }

    public void setMagoikh(String magoikh) {
        this.magoikh = magoikh;
    }

    public String getMakh() {
        return this.makh;
    }

    public void setMakh(String makh) {
        this.makh = makh;
    }

    public String getMagoi() {
        return this.magoi;
    }

    public void setMagoi(String magoi) {
        this.magoi = magoi;
    }

    public String getManv() {
        return this.manv;
    }

    public void setManv(String manv) {
        this.manv = manv;
    }

    public double getSogiobandau() {
        return this.sogiobandau;
    }

    public void setSogiobandau(double sogiobandau) {
        this.sogiobandau = sogiobandau;
    }

    public double getSogioconlai() {
        return this.sogioconlai;
    }

    public void setSogioconlai(double sogioconlai) {
        this.sogioconlai = sogioconlai;
    }

    public LocalDateTime getNgaymua() {
        return this.ngaymua;
    }

    public void setNgaymua(LocalDateTime ngaymua) {
        this.ngaymua = ngaymua;
    }

    public LocalDateTime getNgayhethan() {
        return this.ngayhethan;
    }

    public void setNgayhethan(LocalDateTime ngayhethan) {
        this.ngayhethan = ngayhethan;
    }

    public double getGiamua() {
        return this.giamua;
    }

    public void setGiamua(double giamua) {
        this.giamua = giamua;
    }

    public String getTrangthai() {
        return this.trangthai;
    }

    public void setTrangthai(String trangthai) {
        this.trangthai = trangthai;
    }
}