package entity;

public class ThongkeDoanhThu {

    private String thoiGian;
    private double tongDoanhThu;
    private double tongNhapHang;
    private double loiNhuan;

    public ThongkeDoanhThu() {}

    public ThongkeDoanhThu(String thoiGian, double tongDoanhThu, double tongNhapHang) {
        this.thoiGian = thoiGian;
        this.tongDoanhThu = tongDoanhThu;
        this.tongNhapHang = tongNhapHang;
        this.loiNhuan = tongDoanhThu - tongNhapHang;
    }

    public String getThoiGian() {
        return thoiGian;
    }

    public void setThoiGian(String thoiGian) {
        this.thoiGian = thoiGian;
    }

    public double getTongDoanhThu() {
        return tongDoanhThu;
    }

    public void setTongDoanhThu(double tongDoanhThu) {
        this.tongDoanhThu = tongDoanhThu;
        recalc();
    }

    public double getTongNhapHang() {
        return tongNhapHang;
    }

    public void setTongNhapHang(double tongNhapHang) {
        this.tongNhapHang = tongNhapHang;
        recalc();
    }

    public double getLoiNhuan() {
        return loiNhuan;
    }

    private void recalc() {
        this.loiNhuan = this.tongDoanhThu - this.tongNhapHang;
    }
}