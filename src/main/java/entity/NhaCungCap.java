package entity;

public class NhaCungCap {
    private String mancc;
    private String tenncc;
    private String sodienthoai;
    private String email;
    private String diachi;
    private String nguoilienhe;
    private String trangthai;

    public NhaCungCap() {
    }

    public NhaCungCap(String mancc, String tenncc, String sodienthoai, String email,
                      String diachi, String nguoilienhe, String trangthai) {
        this.mancc = mancc;
        this.tenncc = tenncc;
        this.sodienthoai = sodienthoai;
        this.email = email;
        this.diachi = diachi;
        this.nguoilienhe = nguoilienhe;
        this.trangthai = trangthai;
    }

    public String getMancc() {
        return mancc;
    }

    public void setMancc(String mancc) {
        this.mancc = mancc;
    }

    public String getTenncc() {
        return tenncc;
    }

    public void setTenncc(String tenncc) {
        this.tenncc = tenncc;
    }

    public String getSodienthoai() {
        return sodienthoai;
    }

    public void setSodienthoai(String sodienthoai) {
        this.sodienthoai = sodienthoai;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDiachi() {
        return diachi;
    }

    public void setDiachi(String diachi) {
        this.diachi = diachi;
    }

    public String getNguoilienhe() {
        return nguoilienhe;
    }

    public void setNguoilienhe(String nguoilienhe) {
        this.nguoilienhe = nguoilienhe;
    }

    public String getTrangthai() {
        return trangthai;
    }

    public void setTrangthai(String trangthai) {
        this.trangthai = trangthai;
    }
}