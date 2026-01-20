package entity;

public class KhuMay {
    private String makhu;
    private String tenkhu;
    private double giacoso;
    private int somaytoida;
    private String trangthai;

    public KhuMay() {
    }

    public KhuMay(String makhu, String tenkhu, double giacoso, int somaytoida, String trangthai) {
        this.makhu = makhu;
        this.tenkhu = tenkhu;
        this.giacoso = giacoso;
        this.somaytoida = somaytoida;
        this.trangthai = trangthai;
    }

    public String getMakhu() {
        return makhu;
    }

    public void setMakhu(String makhu) {
        this.makhu = makhu;
    }

    public String getTenkhu() {
        return tenkhu;
    }

    public void setTenkhu(String tenkhu) {
        this.tenkhu = tenkhu;
    }

    public double getGiacoso() {
        return giacoso;
    }

    public void setGiacoso(double giacoso) {
        this.giacoso = giacoso;
    }

    public int getSomaytoida() {
        return somaytoida;
    }

    public void setSomaytoida(int somaytoida) {
        this.somaytoida = somaytoida;
    }

    public String getTrangthai() {
        return trangthai;
    }

    public void setTrangthai(String trangthai) {
        this.trangthai = trangthai;
    }
}