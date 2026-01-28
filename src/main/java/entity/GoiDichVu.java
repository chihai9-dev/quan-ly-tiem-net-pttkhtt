package entity;

public class GoiDichVu {
    private String magoi;
    private String tengoi;
    private String loaigoi;
    private double sogio;
    private int songayhieuluc;
    private double giagoc;
    private double giagoi;
    private String apdungchokhu;
    private String trangthai;

    public GoiDichVu() {
    }

    public GoiDichVu( String magoi, String tengoi, String loaigoi, double sogio,
                      int songayhieuluc, double giagoc, double giagoi,
                      String apdungchokhu, String trangthai) {
        this.magoi = magoi;
        this.tengoi = tengoi;
        this.loaigoi = loaigoi;
        this.sogio = sogio;
        this.songayhieuluc = songayhieuluc;
        this.giagoc = giagoc;
        this.giagoi = giagoi;
        this.apdungchokhu = apdungchokhu;
        this.trangthai = trangthai;
    }

    public String getMagoi() {
        return this.magoi;
    }

    public void setMagoi(String magoi) {
        this.magoi = magoi;
    }

    public String getTengoi() {
        return this.tengoi;
    }

    public void setTengoi(String tengoi) {
        this.tengoi = tengoi;
    }

    public String getLoaigoi() {
        return this.loaigoi;
    }

    public void setLoaigoi(String loaigoi) {
        this.loaigoi = loaigoi;
    }

    public double getSogio() {
        return this.sogio;
    }

    public void setSogio(double sogio) {
        this.sogio = sogio;
    }

    public int getSongayhieuluc() {
        return this.songayhieuluc;
    }

    public void setSongayhieuluc(int songayhieuluc) {
        this.songayhieuluc = songayhieuluc;
    }

    public double getGiagoc() {
        return this.giagoc;
    }

    public void setGiagoc(double giagoc) {
        this.giagoc = giagoc;
    }

    public double getGiagoi() {
        return this.giagoi;
    }

    public void setGiagoi(double giagoi) {
        this.giagoi = giagoi;
    }

    public String getApdungchokhu() {
        return this.apdungchokhu;
    }

    public void setApdungchokhu(String apdungchokhu) {
        this.apdungchokhu = apdungchokhu;
    }

    public String getTrangthai() {
        return this.trangthai;
    }

    public void setTrangthai(String trangthai) {
        this.trangthai = trangthai;
    }
}