package entity;

public class KhachHang {
    private String makh;
    private String ho;
    private String ten;
    private String sodienthoai;
    private String tendangnhap;
    private String matkhau;
    private double sodu;

    public KhachHang() {
    }

    public KhachHang(String makh, String ho, String ten, String sodienthoai,
                     String tendangnhap, String matkhau, double sodu) {
        this.makh = makh;
        this.ho = ho;
        this.ten = ten;
        this.sodienthoai = sodienthoai;
        this.tendangnhap = tendangnhap;
        this.matkhau = matkhau;
        this.sodu = sodu;
    }

    public String getMakh() {
        return makh;
    }

    public void setMakh(String makh) {
        this.makh = makh;
    }

    public String getHo() {
        return ho;
    }

    public void setHo(String ho) {
        this.ho = ho;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getSodienthoai() {
        return sodienthoai;
    }

    public void setSodienthoai(String sodienthoai) {
        this.sodienthoai = sodienthoai;
    }

    public String getTendangnhap() {
        return tendangnhap;
    }

    public void setTendangnhap(String tendangnhap) {
        this.tendangnhap = tendangnhap;
    }

    public String getMatkhau() {
        return matkhau;
    }

    public void setMatkhau(String matkhau) {
        this.matkhau = matkhau;
    }

    public double getSodu() {
        return sodu;
    }

    public void setSodu(double sodu) {
        this.sodu = sodu;
    }
}