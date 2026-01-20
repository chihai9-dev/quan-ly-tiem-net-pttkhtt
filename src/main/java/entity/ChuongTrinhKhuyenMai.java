package entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ChuongTrinhKhuyenMai {
    private String mactkm;
    private String tenct;
    private String loaikm;
    private BigDecimal giatrikm;
    private BigDecimal dieukientoithieu;
    private LocalDateTime ngaybatdau;
    private LocalDateTime ngayketthuc;
    private String trangthai;

    public ChuongTrinhKhuyenMai() {
    }

    public ChuongTrinhKhuyenMai(String mactkm, String tenct, String loaikm, BigDecimal giatrikm,
                                BigDecimal dieukientoithieu, LocalDateTime ngaybatdau,
                                LocalDateTime ngayketthuc, String trangthai) {
        this.mactkm = mactkm;
        this.tenct = tenct;
        this.loaikm = loaikm;
        this.giatrikm = giatrikm;
        this.dieukientoithieu = dieukientoithieu;
        this.ngaybatdau = ngaybatdau;
        this.ngayketthuc = ngayketthuc;
        this.trangthai = trangthai;
    }

    public String getMactkm() {
        return mactkm;
    }

    public void setMactkm(String mactkm) {
        this.mactkm = mactkm;
    }

    public String getTenct() {
        return tenct;
    }

    public void setTenct(String tenct) {
        this.tenct = tenct;
    }

    public String getLoaikm() {
        return loaikm;
    }

    public void setLoaikm(String loaikm) {
        this.loaikm = loaikm;
    }

    public BigDecimal getGiatrikm() {
        return giatrikm;
    }

    public void setGiatrikm(BigDecimal giatrikm) {
        this.giatrikm = giatrikm;
    }

    public BigDecimal getDieukientoithieu() {
        return dieukientoithieu;
    }

    public void setDieukientoithieu(BigDecimal dieukientoithieu) {
        this.dieukientoithieu = dieukientoithieu;
    }

    public LocalDateTime getNgaybatdau() {
        return ngaybatdau;
    }

    public void setNgaybatdau(LocalDateTime ngaybatdau) {
        this.ngaybatdau = ngaybatdau;
    }

    public LocalDateTime getNgayketthuc() {
        return ngayketthuc;
    }

    public void setNgayketthuc(LocalDateTime ngayketthuc) {
        this.ngayketthuc = ngayketthuc;
    }

    public String getTrangthai() {
        return trangthai;
    }

    public void setTrangthai(String trangthai) {
        this.trangthai = trangthai;
    }
}