package entity;

public class NhanVien {
    private String manv;
    private String ho;
    private String ten;
    private String chucvu;
    private String tendangnhap;
    private String matkhau;
    private String trangthai;

    public NhanVien() {
    }

    public NhanVien(String manv, String ho, String ten, String chucvu,
                    String tendangnhap, String matkhau, String trangthai) {
        this.manv = manv;
        this.ho = ho;
        this.ten = ten;
        this.chucvu = chucvu;
        this.tendangnhap = tendangnhap;
        this.matkhau = matkhau;
        this.trangthai = trangthai;
    }

    public String getManv() {
        return manv;
    }

    public void setManv(String manv) {
        this.manv = manv;
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

    public String getChucvu() {
        return chucvu;
    }

    public void setChucvu(String chucvu) {
        this.chucvu = chucvu;
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

    public String getTrangthai() {
        return trangthai;
    }

    public void setTrangthai(String trangthai) {
        this.trangthai = trangthai;
    }
}