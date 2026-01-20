package entity;

import java.math.BigDecimal;

public class MayTinh {
    private String mamay;
    private String tenmay;
    private String makhu;
    private String cauhinh;
    private Double giamoigio;
    private String trangthai;

    public MayTinh() {
    }

    public MayTinh(String mamay, String tenmay, String makhu, String cauhinh,
                   Double giamoigio, String trangthai) {
        this.mamay = mamay;
        this.tenmay = tenmay;
        this.makhu = makhu;
        this.cauhinh = cauhinh;
        this.giamoigio = giamoigio;
        this.trangthai = trangthai;
    }

    public String getMamay() {
        return mamay;
    }

    public void setMamay(String mamay) {
        this.mamay = mamay;
    }

    public String getTenmay() {
        return tenmay;
    }

    public void setTenmay(String tenmay) {
        this.tenmay = tenmay;
    }

    public String getMakhu() {
        return makhu;
    }

    public void setMakhu(String makhu) {
        this.makhu = makhu;
    }

    public String getCauhinh() {
        return cauhinh;
    }

    public void setCauhinh(String cauhinh) {
        this.cauhinh = cauhinh;
    }

    public Double getGiamoigio() {
        return giamoigio;
    }

    public void setGiamoigio(Double giamoigio) {
        this.giamoigio = giamoigio;
    }

    public String getTrangthai() {
        return trangthai;
    }

    public void setTrangthai(String trangthai) {
        this.trangthai = trangthai;
    }
}