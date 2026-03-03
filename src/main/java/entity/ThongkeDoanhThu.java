package entity;

public class ThongkeDoanhThu {
    private String thoiGian;      // VD: "2026-02-01" hoặc "02/2026"
    private double tongDoanhThu;  // doanh thu từ hóa đơn
    private double tongNhapHang;  // tổng nhập hàng (phiếu DANHAP)
    private double loiNhuan;      // = doanh thu - nhập hàng

    public ThongkeDoanhThu() {}

    public ThongkeDoanhThu(String thoiGian, double tongDoanhThu, double tongNhapHang) {
        this.thoiGian = thoiGian;
        this.tongDoanhThu = tongDoanhThu;
        this.tongNhapHang = tongNhapHang;
        recalc();
    }

    public String getThoiGian() { return thoiGian; }
    public void setThoiGian(String thoiGian) { this.thoiGian = thoiGian; }

    public double getTongDoanhThu() { return tongDoanhThu; }
    public void setTongDoanhThu(double tongDoanhThu) {
        this.tongDoanhThu = tongDoanhThu;
        recalc();
    }

    public double getTongNhapHang() { return tongNhapHang; }
    public void setTongNhapHang(double tongNhapHang) {
        this.tongNhapHang = tongNhapHang;
        recalc();
    }

    public double getLoiNhuan() { return loiNhuan; }

    private void recalc() {
        this.loiNhuan = this.tongDoanhThu - this.tongNhapHang;
    }

    @Override
    public String toString() {
        return thoiGian + " | DoanhThu=" + tongDoanhThu + " | NhapHang=" + tongNhapHang + " | LoiNhuan=" + loiNhuan;
    }
}
