package entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChuongTrinhKhuyenMai {
    private String maCTKM;
    private String tenCT;
    private String loaiKM;  // PHANTRAM, SOTIEN, TANGGIO
    private double giaTriKM;
    private double dieuKienToiThieu;
    private LocalDateTime ngayBatDau;
    private LocalDateTime ngayKetThuc;
    private String trangThai;  // HOATDONG, NGUNG, HETHAN

    public ChuongTrinhKhuyenMai() {
        this.giaTriKM = 0.0;
        this.dieuKienToiThieu = 0.0;
        this.ngayBatDau = LocalDateTime.now();
        this.ngayKetThuc = LocalDateTime.now().plusDays(30);
        this.trangThai = "HOATDONG";
        this.loaiKM = "PHANTRAM";
    }

    public ChuongTrinhKhuyenMai(String maCTKM, String tenCT, String loaiKM,
                                double giaTriKM, double dieuKienToiThieu,
                                LocalDateTime ngayBatDau, LocalDateTime ngayKetThuc,
                                String trangThai) {
        this.maCTKM = maCTKM;
        this.tenCT = tenCT;
        this.loaiKM = loaiKM;
        this.giaTriKM = giaTriKM;
        this.dieuKienToiThieu = dieuKienToiThieu;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
        this.trangThai = trangThai;
    }

    // Getters
    public String getMaCTKM() {
        return maCTKM;
    }

    public String getTenCT() {
        return tenCT;
    }

    public String getLoaiKM() {
        return loaiKM;
    }

    public double getGiaTriKM() {
        return giaTriKM;
    }

    public double getDieuKienToiThieu() {return dieuKienToiThieu;
    }

    public LocalDateTime getNgayBatDau() {
        return ngayBatDau;
    }

    public LocalDateTime getNgayKetThuc() {
        return ngayKetThuc;
    }

    public String getTrangThai() {
        return trangThai;
    }

    // Setters
    public void setMaCTKM(String maCTKM) {
        this.maCTKM = maCTKM;
    }

    public void setTenCT(String tenCT) {
        this.tenCT = tenCT;
    }

    public void setLoaiKM(String loaiKM) {
        this.loaiKM = loaiKM;
    }

    public void setGiaTriKM(double giaTriKM) {
        this.giaTriKM = giaTriKM;
    }

    public void setDieuKienToiThieu(double dieuKienToiThieu) {
        this.dieuKienToiThieu = dieuKienToiThieu;
    }

    public void setNgayBatDau(LocalDateTime ngayBatDau) {
        this.ngayBatDau = ngayBatDau;
    }

    public void setNgayKetThuc(LocalDateTime ngayKetThuc) {
        this.ngayKetThuc = ngayKetThuc;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    // Phương thức kiểm tra
    public boolean conHieuLuc() {
        LocalDateTime now = LocalDateTime.now();
        return trangThai.equals("HOATDONG") &&
                now.isAfter(ngayBatDau) &&
                now.isBefore(ngayKetThuc);
    }

    public boolean kiemTraDieuKien(double soTienNap) {
        return soTienNap >= dieuKienToiThieu;
    }

    // Tính giá trị khuyến mãi
    public double tinhKhuyenMai(double soTienNap) {
        if (!kiemTraDieuKien(soTienNap)) {
            return 0;
        }

        switch (loaiKM) {
            case "PHANTRAM":
                return soTienNap * giaTriKM / 100;
            case "SOTIEN":
                return giaTriKM;
            case "TANGGIO":
                return giaTriKM; // Trả về số giờ
            default:
                return 0;
        }
    }

    // Format hiển thị
    public String getGiaTriKMFormatted() {
        if (loaiKM == null) return "";

        switch (loaiKM) {
            case "PHANTRAM":
                return String.format("%.0f%%", giaTriKM);
            case "SOTIEN":
                return String.format("%,.0f VND", giaTriKM);
            case "TANGGIO":
                return String.format("%.1f giờ", giaTriKM);
            default:
                return String.valueOf(giaTriKM);
        }
    }

    public String getDieuKienToiThieuFormatted() {
        return String.format("%,.0f VND", dieuKienToiThieu);
    }

    public String getNgayBatDauFormatted() {
        if (ngayBatDau != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return ngayBatDau.format(formatter);
        }
        return "";
    }

    public String getNgayKetThucFormatted() {
        if (ngayKetThuc != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return ngayKetThuc.format(formatter);
        }
        return "";
    }

    // Lấy tên hiển thị
    public String getTenLoaiKM() {
        if (loaiKM == null) return "";

        switch (loaiKM) {
            case "PHANTRAM":
                return "Phần trăm";
            case "SOTIEN":
                return "Số tiền";
            case "TANGGIO":
                return "Tặng giờ";
            default:
                return loaiKM;
        }
    }

    public String getTenTrangThai() {
        if (trangThai == null) return "";

        switch (trangThai) {
            case "HOATDONG":
                return "Hoạt động";
            case "NGUNG":
                return "Ngừng";
            case "HETHAN":
                return "Hết hạn";
            default:
                return trangThai;
        }
    }

    @Override
    public String toString() {
        return "ChuongTrinhKhuyenMai{" +
                "maCTKM='" + maCTKM + '\'' +
                ", tenCT='" + tenCT + '\'' +
                ", loaiKM='" + loaiKM + '\'' +
                ", giaTriKM=" + giaTriKM +
                ", dieuKienToiThieu=" + dieuKienToiThieu +
                ", ngayBatDau=" + ngayBatDau +
                ", ngayKetThuc=" + ngayKetThuc +
                ", trangThai='" + trangThai + '\'' +
                '}';
    }
}