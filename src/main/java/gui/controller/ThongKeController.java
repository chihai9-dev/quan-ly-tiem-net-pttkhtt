package gui.controller;

import bus.HoaDonBUS;
import bus.ThongKeBUS;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import utils.SessionManager;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class ThongKeController implements Initializable {

    // Summary cards
    @FXML private Label lblTongThu;
    @FXML private Label lblTongChi;
    @FXML private Label lblLoiNhuan;
    @FXML private Label lblSoPhien;

    // Tab 1 - Doanh thu
    @FXML private ComboBox<String> cboLoaiTK;
    @FXML private DatePicker dateFrom;
    @FXML private DatePicker dateTo;
    @FXML private TableView<Map<String, Object>> tableThongKe;
    @FXML private TableColumn<Map<String, Object>, String> colThoiGian;
    @FXML private TableColumn<Map<String, Object>, String> colDoanhThu;
    @FXML private TableColumn<Map<String, Object>, String> colNhapHang;
    @FXML private TableColumn<Map<String, Object>, String> colLoiNhuanCol;
    @FXML private StackPane chartContainer;
    @FXML private Label lblChartPlaceholder;

    // Tab 2 - Thu Chi
    @FXML private ComboBox<String> cboThoiGian;
    @FXML private DatePicker dateTCFrom;
    @FXML private DatePicker dateTCTo;
    @FXML private TableView<Map<String, Object>> tableThuChi;
    @FXML private TableColumn<Map<String, Object>, String> colTCThoiGian;
    @FXML private TableColumn<Map<String, Object>, String> colTCThu;
    @FXML private TableColumn<Map<String, Object>, String> colTCChi;
    @FXML private TableColumn<Map<String, Object>, String> colTCLoiNhuan;

    // Tab 3 - Top KH
    @FXML private ComboBox<Integer> cboNamTop;
    @FXML private ComboBox<String> cboTopN;
    @FXML private TableView<Object[]> tableTop;

    private final ThongKeBUS thongKeBUS = new ThongKeBUS();
    private final HoaDonBUS  hoaDonBUS  = new HoaDonBUS();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (dateFrom   != null) dateFrom.setValue(LocalDate.now().withDayOfMonth(1));
        if (dateTo     != null) dateTo.setValue(LocalDate.now());
        if (dateTCFrom != null) dateTCFrom.setValue(LocalDate.now().withDayOfMonth(1));
        if (dateTCTo   != null) dateTCTo.setValue(LocalDate.now());

        if (cboLoaiTK != null) {
            cboLoaiTK.getItems().setAll("Từ ngày đến ngày", "Theo tháng");
            cboLoaiTK.setValue("Từ ngày đến ngày");
        }
        if (cboThoiGian != null) {
            cboThoiGian.getItems().setAll("Hôm nay", "Tuần này", "Tháng này", "Quý này", "Năm nay", "Tùy chỉnh");
            cboThoiGian.setValue("Tháng này");
        }
        if (cboTopN != null) {
            cboTopN.getItems().setAll("Top 5", "Top 10", "Top 20");
            cboTopN.setValue("Top 10");
        }
        setupTableColumns();
        loadYearCombo();
        loadSummaryCards();
    }

    private void setupTableColumns() {
        // Tab 1 - Map-based
        if (colThoiGian    != null) colThoiGian.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(String.valueOf(c.getValue().getOrDefault("ThoiGian",""))));
        if (colDoanhThu    != null) colDoanhThu.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(fmtMoney(c.getValue().get("TongDoanhThu"))));
        if (colNhapHang    != null) colNhapHang.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(fmtMoney(c.getValue().get("TongNhapHang"))));
        if (colLoiNhuanCol != null) colLoiNhuanCol.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(fmtMoney(c.getValue().get("LoiNhuan"))));
        // Tab 2
        if (colTCThoiGian  != null) colTCThoiGian.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(String.valueOf(c.getValue().getOrDefault("ThoiGian",""))));
        if (colTCThu       != null) colTCThu.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(fmtMoney(c.getValue().get("Thu"))));
        if (colTCChi       != null) colTCChi.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(fmtMoney(c.getValue().get("Chi"))));
        if (colTCLoiNhuan  != null) colTCLoiNhuan.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(fmtMoney(c.getValue().get("LoiNhuan"))));
    }

    private String fmtMoney(Object val) {
        if (val == null) return "0 ₫";
        try { return String.format("%,.0f ₫", ((Number) val).doubleValue()); }
        catch (Exception e) { return String.valueOf(val); }
    }

    private void loadYearCombo() {
        if (cboNamTop == null) return;
        int year = LocalDate.now().getYear();
        cboNamTop.getItems().setAll(year, year - 1, year - 2, year - 3);
        cboNamTop.setValue(year);
    }

    private void loadSummaryCards() {
        try {
            String vaiTro = getVaiTro();
            // thongKeTongQuan(vaiTro) -> Map<String, Object>: SoPhien, TongDoanhThu, etc.
            Map<String, Object> tq = thongKeBUS.thongKeTongQuan(vaiTro);
            double thu     = getDouble(tq, "TongDoanhThu");
            double soPhien = getDouble(tq, "SoPhien");

            if (lblTongThu  != null) lblTongThu.setText(String.format("%,.0f ₫", thu));
            if (lblTongChi  != null) lblTongChi.setText("N/A");
            if (lblLoiNhuan != null) {
                lblLoiNhuan.setText(String.format("%,.0f ₫", thu));
                lblLoiNhuan.setStyle("-fx-text-fill:#388E3C; -fx-font-weight:bold; -fx-font-size:18px;");
            }
            if (lblSoPhien  != null) lblSoPhien.setText(String.format("%.0f", soPhien));
        } catch (Exception e) {
            if (lblTongThu != null) lblTongThu.setText("N/A");
            if (lblSoPhien != null) lblSoPhien.setText("0");
        }
    }

    @FXML
    public void handleThongKe() {
        try {
            LocalDate from = dateFrom != null ? dateFrom.getValue() : LocalDate.now().withDayOfMonth(1);
            LocalDate to   = dateTo   != null ? dateTo.getValue()   : LocalDate.now();
            if (from == null || to == null) return;
            String vaiTro = getVaiTro();

            // thongKeDoanhThu(LocalDate, LocalDate, String vaiTro)
            Map<String, Object> data = thongKeBUS.thongKeDoanhThu(from, to, vaiTro);

            // Wrap vào list 1 dòng cho bảng
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("ThoiGian", from + " → " + to);
            row.put("TongDoanhThu", data.get("TongDoanhThu"));
            row.put("TongNhapHang", data.getOrDefault("TongNhapHang", 0));
            double thu = getDouble(data, "TongDoanhThu");
            double chi = getDouble(data, "TongNhapHang");
            row.put("LoiNhuan", thu - chi);

            if (tableThongKe != null)
                tableThongKe.setItems(FXCollections.observableArrayList(List.of(row)));
            if (lblChartPlaceholder != null)
                lblChartPlaceholder.setVisible(false);
        } catch (Exception e) {
            if (lblChartPlaceholder != null) {
                lblChartPlaceholder.setText("Lỗi: " + e.getMessage());
                lblChartPlaceholder.setVisible(true);
            }
        }
    }

    @FXML
    public void handleThongKeThuChi() {
        try {
            LocalDate from = dateTCFrom != null ? dateTCFrom.getValue() : LocalDate.now().withDayOfMonth(1);
            LocalDate to   = dateTCTo   != null ? dateTCTo.getValue()   : LocalDate.now();
            if (from == null || to == null) return;

            // Dùng HoaDonBUS để lấy doanh thu
            LocalDateTime dtFrom = from.atStartOfDay();
            LocalDateTime dtTo   = to.plusDays(1).atStartOfDay();
            double thu = hoaDonBUS.thongKeDoanhThu(dtFrom, dtTo);
            double gioChoi = hoaDonBUS.thongKeDoanhThuGioChoi(dtFrom, dtTo);
            double dichVu  = hoaDonBUS.thongKeDoanhThuDichVu(dtFrom, dtTo);

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("ThoiGian", from + " → " + to);
            row.put("Thu", thu);
            row.put("Chi", 0.0); // Chi phí nhập hàng chưa có source
            row.put("LoiNhuan", thu);

            if (tableThuChi != null)
                tableThuChi.setItems(FXCollections.observableArrayList(List.of(row)));
        } catch (Exception ignored) {}
    }

    @FXML
    public void handleThongKeTop() {
        try {
            Integer nam = cboNamTop != null ? cboNamTop.getValue() : LocalDate.now().getYear();
            String topNStr = cboTopN != null ? cboTopN.getValue() : "Top 10";
            int n = topNStr != null ? Integer.parseInt(topNStr.replace("Top ", "")) : 10;
            if (nam == null) return;

            LocalDateTime from = LocalDate.of(nam, 1, 1).atStartOfDay();
            LocalDateTime to   = LocalDate.of(nam, 12, 31).atTime(23, 59, 59);
            // topKhachHangChiTieu(int n, LocalDateTime from, LocalDateTime to)
            List<Object[]> data = hoaDonBUS.topKhachHangChiTieu(n, from, to);
            if (tableTop != null)
                tableTop.setItems(FXCollections.observableArrayList(data));
        } catch (Exception ignored) {}
    }

    @FXML
    public void handleXuatExcel() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText("Chức năng xuất Excel đang phát triển.");
        alert.showAndWait();
    }

    @FXML
    public void handleRefresh() { loadSummaryCards(); }

    private String getVaiTro() {
        try {
            String role = SessionManager.getLoaiTaiKhoan();
            return role != null ? role : "QUANLY";
        } catch (Exception e) { return "QUANLY"; }
    }

    private double getDouble(Map<String, Object> map, String key) {
        if (map == null || !map.containsKey(key)) return 0.0;
        Object val = map.get(key);
        if (val instanceof Number) return ((Number) val).doubleValue();
        try { return Double.parseDouble(String.valueOf(val)); }
        catch (Exception e) { return 0.0; }
    }
}
