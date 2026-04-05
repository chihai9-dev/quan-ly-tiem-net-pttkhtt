package gui.controller;

import bus.HoaDonBUS;
import bus.ThongKeBUS;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.application.Platform;
import javafx.stage.FileChooser;
import java.io.File;

import javafx.stage.Window;
import utils.ThongKeExporter;


import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

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
    @FXML private TableColumn<Object[], String> colTopSTT;
    @FXML private TableColumn<Object[], String> colTopTen;
    @FXML private TableColumn<Object[], String> colTopSoPhien;
    @FXML private TableColumn<Object[], String> colTopThoiGian;
    @FXML private TableColumn<Object[], String> colTopTongTien;



    @FXML private Button btnXuatPDF;

    private final ThongKeBUS thongKeBUS = new ThongKeBUS();
    private final HoaDonBUS hoaDonBUS = new HoaDonBUS();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (dateFrom != null) dateFrom.setValue(LocalDate.now().withDayOfMonth(1));
        if (dateTo != null) dateTo.setValue(LocalDate.now());

        if (dateTCFrom != null) dateTCFrom.setValue(LocalDate.now().withDayOfMonth(1));
        if (dateTCTo != null) dateTCTo.setValue(LocalDate.now());
        if(btnXuatPDF != null){
            btnXuatPDF.setDisable(true);
        }
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
        handleLoaiTKChanged();
        loadSummaryCards();
        renderEmptyChartState();
    }

    private void setupTableColumns() {
        // Tab 1
        if (colThoiGian != null) {
            colThoiGian.setCellValueFactory(c ->
                    new SimpleStringProperty(String.valueOf(c.getValue().getOrDefault("ThoiGian", ""))));
        }

        if (colDoanhThu != null) {
            colDoanhThu.setCellValueFactory(c ->
                    new SimpleStringProperty(fmtMoney(c.getValue().get("TongDoanhThu"))));
        }

        if (colNhapHang != null) {
            colNhapHang.setCellValueFactory(c ->
                    new SimpleStringProperty(fmtMoney(c.getValue().get("TongNhapHang"))));
        }

        if (colLoiNhuanCol != null) {
            colLoiNhuanCol.setCellValueFactory(c ->
                    new SimpleStringProperty(fmtMoney(c.getValue().get("LoiNhuan"))));
        }

        // Tab 2
        if (colTCThoiGian != null) {
            colTCThoiGian.setCellValueFactory(c ->
                    new SimpleStringProperty(String.valueOf(c.getValue().getOrDefault("ThoiGian", ""))));
        }

        if (colTCThu != null) {
            colTCThu.setCellValueFactory(c ->
                    new SimpleStringProperty(fmtMoney(c.getValue().get("Thu"))));
        }

        if (colTCChi != null) {
            colTCChi.setCellValueFactory(c ->
                    new SimpleStringProperty(fmtMoney(c.getValue().get("Chi"))));
        }

        if (colTCLoiNhuan != null) {
            colTCLoiNhuan.setCellValueFactory(c ->
                    new SimpleStringProperty(fmtMoney(c.getValue().get("LoiNhuan"))));
        }

        // Tab 3
        // Tab 3
        if (colTopSTT != null) {
            colTopSTT.setCellValueFactory(c ->
                    new SimpleStringProperty(readArray(c.getValue(), 0)));
        }

        if (colTopTen != null) {
            colTopTen.setCellValueFactory(c ->
                    new SimpleStringProperty(readArray(c.getValue(), 1)));
        }

        if (colTopSoPhien != null) {
            colTopSoPhien.setCellValueFactory(c ->
                    new SimpleStringProperty(readArray(c.getValue(), 2)));
        }

        if (colTopThoiGian != null) {
            colTopThoiGian.setCellValueFactory(c ->
                    new SimpleStringProperty(fmtHours(readArrayObject(c.getValue(), 3))));
        }

        if (colTopTongTien != null) {
            colTopTongTien.setCellValueFactory(c ->
                    new SimpleStringProperty(fmtMoney(readArrayObject(c.getValue(), 4))));
        }
    }

    private String fmtMoney(Object val) {
        if (val == null) return "0 ₫";
        try {
            return String.format("%,.0f ₫", ((Number) val).doubleValue());
        } catch (Exception e) {
            return String.valueOf(val);
        }
    }

    private String readArray(Object[] arr, int index) {
        if (arr == null || index < 0 || index >= arr.length || arr[index] == null) return "";
        return String.valueOf(arr[index]);
    }

    private Object readArrayObject(Object[] arr, int index) {
        if (arr == null || index < 0 || index >= arr.length) return 0;
        return arr[index];
    }

    private void loadYearCombo() {
        if (cboNamTop == null) return;
        int year = LocalDate.now().getYear();
        cboNamTop.getItems().setAll(year, year - 1, year - 2, year - 3);
        cboNamTop.setValue(year);
    }

    private void loadSummaryCards() {
        try {
            Map<String, Object> tq = thongKeBUS.thongKeTongQuan();

            double doanhThuHomNay = getDouble(tq, "DoanhThuHomNay");
            double doanhThuThangNay = getDouble(tq, "DoanhThuThangNay");
            double soPhienDangChoi = getDouble(tq, "SoPhienDangChoi");

            if (lblTongThu != null) lblTongThu.setText(fmtMoney(doanhThuThangNay));
            if (lblTongChi != null) lblTongChi.setText("0 ₫");
            if (lblLoiNhuan != null) lblLoiNhuan.setText(fmtMoney(doanhThuHomNay));
            if (lblSoPhien != null) lblSoPhien.setText(String.format("%.0f", soPhienDangChoi));

        } catch (Exception e) {
            if (lblTongThu != null) lblTongThu.setText("N/A");
            if (lblTongChi != null) lblTongChi.setText("N/A");
            if (lblLoiNhuan != null) lblLoiNhuan.setText("N/A");
            if (lblSoPhien != null) lblSoPhien.setText("0");
        }
    }

    @FXML
    public void handleLoaiTKChanged() {
        if (cboLoaiTK == null) return;

        boolean theoThang = "Theo tháng".equals(cboLoaiTK.getValue());

        // Theo tháng: dùng năm của dateFrom, nên chỉ khóa dateTo
        if (dateFrom != null) dateFrom.setDisable(false);
        if (dateTo != null) dateTo.setDisable(theoThang);

        if (dateFrom != null && dateFrom.getValue() == null) {
            dateFrom.setValue(LocalDate.now());
        }

        if (theoThang && dateTo != null) {
            dateTo.setValue(dateFrom != null && dateFrom.getValue() != null
                    ? dateFrom.getValue()
                    : LocalDate.now());
        }

        if( cboLoaiTK.getValue().equals("Từ ngày đến ngày")){
            btnXuatPDF.setDisable(true);
        }
        else {
            btnXuatPDF.setDisable(false);
        }

    }

    @FXML
    public void handleThongKe() {
        try {
            String loai = cboLoaiTK != null ? cboLoaiTK.getValue() : "Từ ngày đến ngày";

            if ("Theo tháng".equals(loai)) {
                LocalDate mocNam = dateFrom != null ? dateFrom.getValue() : LocalDate.now();
                int nam = mocNam.getYear();

                List<Map<String, Object>> data12Thang = thongKeBUS.thongKeTheo12Thang(nam);

                if (tableThongKe != null) {
                    tableThongKe.setItems(FXCollections.observableArrayList(data12Thang));
                }

                renderMonthlyLineChart(data12Thang, nam);

            } else {
                LocalDate from = dateFrom != null ? dateFrom.getValue() : LocalDate.now().withDayOfMonth(1);
                LocalDate to = dateTo != null ? dateTo.getValue() : LocalDate.now();

                if (from == null || to == null) return;

                Map<String, Object> data = thongKeBUS.thongKeDoanhThu(from, to);

                Map<String, Object> row = new LinkedHashMap<>();
                row.put("ThoiGian", from + " → " + to);
                row.put("TongDoanhThu", data.get("TongDoanhThu"));
                row.put("TongNhapHang", data.getOrDefault("TongNhapHang", 0));
                row.put("LoiNhuan", data.getOrDefault("LoiNhuan", 0));

                if (tableThongKe != null) {
                    tableThongKe.setItems(FXCollections.observableArrayList(List.of(row)));
                }

                double thu = getDouble(data, "TongDoanhThu");
                double chi = getDouble(data, "TongNhapHang");
                double loiNhuan = getDouble(data, "LoiNhuan");

                renderChartSummary(thu, chi, loiNhuan);
            }

        } catch (Exception e) {
            showError("Lỗi thống kê", e.getMessage());
            renderErrorChartState(e.getMessage());
        }
    }

    @FXML
    public void handleThongKeThuChi() {
        try {
            LocalDate from = dateTCFrom != null ? dateTCFrom.getValue() : LocalDate.now().withDayOfMonth(1);
            LocalDate to = dateTCTo != null ? dateTCTo.getValue() : LocalDate.now();

            if (from == null || to == null) return;

            LocalDateTime dtFrom = from.atStartOfDay();
            LocalDateTime dtTo = to.plusDays(1).atStartOfDay();

            double thu = hoaDonBUS.thongKeDoanhThu(dtFrom, dtTo);
            double chi = 0.0;

            try {
                Map<String, Object> data = thongKeBUS.thongKeDoanhThu(from, to);
                chi = getDouble(data, "TongNhapHang");
            } catch (Exception ignored) {
            }

            double loiNhuan = thu - chi;

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("ThoiGian", from + " → " + to);
            row.put("Thu", thu);
            row.put("Chi", chi);
            row.put("LoiNhuan", loiNhuan);

            if (tableThuChi != null) {
                tableThuChi.setItems(FXCollections.observableArrayList(List.of(row)));
            }

        } catch (Exception e) {
            showError("Lỗi thống kê thu chi", e.getMessage());
        }
    }

    @FXML
    public void handleThongKeTop() {
        try {
            Integer nam = cboNamTop != null ? cboNamTop.getValue() : LocalDate.now().getYear();
            String topNStr = cboTopN != null ? cboTopN.getValue() : "Top 10";
            int n = topNStr != null ? Integer.parseInt(topNStr.replace("Top ", "").trim()) : 10;

            if (nam == null) return;

            List<Object[]> data = thongKeBUS.thongKeTopKhachHang(nam, n);

            if (tableTop != null) {
                tableTop.setItems(FXCollections.observableArrayList(data));
            }

        } catch (Exception e) {
            showError("Lỗi thống kê top khách hàng", e.getMessage());
        }
    }

    @FXML
    public void handleXuatExcel() {
        try {
            String loai = "Theo tháng";

            LocalDate from;
            LocalDate to;
            List<Map<String, Object>> exportData;
            double tongThu;
            double tongChi;
            double tongLoiNhuan;

            LocalDate mocNam = (dateFrom != null && dateFrom.getValue() != null)
                    ? dateFrom.getValue()
                    : LocalDate.now();

            int nam = mocNam.getYear();
            from = LocalDate.of(nam, 1, 1);
            to = LocalDate.of(nam, 12, 31);

            exportData = thongKeBUS.thongKeTheo12Thang(nam);

            tongThu = exportData.stream()
                    .mapToDouble(r -> getDouble(r, "TongDoanhThu"))
                    .sum();

            tongChi = exportData.stream()
                    .mapToDouble(r -> getDouble(r, "TongNhapHang"))
                    .sum();

            tongLoiNhuan = exportData.stream()
                    .mapToDouble(r -> getDouble(r, "LoiNhuan"))
                    .sum();


            Map<String, Object> tongQuan = thongKeBUS.thongKeTongQuan();
            int soPhienDangChoi = (int) Math.round(getDouble(tongQuan, "SoPhienDangChoi"));

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Lưu báo cáo Excel");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Excel Workbook (*.xlsx)", "*.xlsx")
            );
            fileChooser.setInitialFileName(
                    "BaoCaoThongKe_" + LocalDate.now().toString() + ".xlsx"
            );

            File file = fileChooser.showSaveDialog(
                    chartContainer != null && chartContainer.getScene() != null
                            ? chartContainer.getScene().getWindow()
                            : null
            );

            if (file == null) {
                return;
            }

            if (!file.getName().toLowerCase().endsWith(".xlsx")) {
                file = new File(file.getAbsolutePath() + ".xlsx");
            }

            ThongKeExporter.exportThongKe(
                    file,
                    "BÁO CÁO THỐNG KÊ TIỆM NET",
                    loai,
                    from,
                    to,
                    exportData,
                    tongThu,
                    tongChi,
                    tongLoiNhuan,
                    soPhienDangChoi
            );

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thành công");
            alert.setHeaderText(null);
            alert.setContentText("Xuất file Excel thành công:\n" + file.getAbsolutePath());
            alert.showAndWait();

        } catch (Exception e) {
            showError("Lỗi xuất Excel", e.getMessage());
        }
    }

    @FXML
    public void handXuatPDF(){
        try {


            String loai =  cboLoaiTK.getValue();

            LocalDate from;
            LocalDate to;
            List<Map<String, Object>> exportData;
            double tongThu;
            double tongChi;
            double tongLoiNhuan;

            LocalDate mocNam = (dateFrom != null && dateFrom.getValue() != null) ?
                    dateFrom.getValue() : LocalDate.now();
            int nam = mocNam.getYear();
            from = LocalDate.of(nam, 1, 1);
            to = LocalDate.of(nam, 12, 31);

            exportData = thongKeBUS.thongKeTheo12Thang(nam);

            tongThu = exportData.stream().
                    mapToDouble(r -> getDouble(r,"TongDanhThu")).sum();
            tongChi = exportData.stream().
                    mapToDouble(r -> getDouble(r,"TongNhapHang")).sum();
            tongLoiNhuan = exportData.stream().
                    mapToDouble(r -> getDouble(r,"LoiNhuan")).sum();




            Map<String,Object> tongQuan = thongKeBUS.thongKeTongQuan();
            int soPhienDangChoi = (int)Math.round(getDouble(tongQuan,"SoPhienDangChoi"));


            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Lưu báo cáo PDF");
            fileChooser.setInitialFileName("BaoCao_" + LocalDate.now() + ".pdf");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

            File file = fileChooser.showSaveDialog(
                    chartContainer != null && chartContainer.getScene() != null
                            ? chartContainer.getScene().getWindow()
                            : null
            );

            if (file == null) return;

            ThongKeExporter.xuatPDFThongKe(file.getAbsolutePath(),
                    "BÁO CÁO THỐNG KÊ TIỆM NET",
                    loai,
                    from,
                    to,
                    exportData,
                    tongThu,
                    tongChi,
                    tongLoiNhuan,
                    soPhienDangChoi);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thành công");
            alert.setHeaderText(null);
//            alert.setContentText("Xuất file PDF thành công:\n" + file.getAbsolutePath());
            alert.showAndWait();

        } catch (Exception e) {
            showError("Lỗi xuất PDF", e.getMessage());
        }



    }

    @FXML
    public void handleRefresh() {
        loadSummaryCards();
        renderEmptyChartState();
    }

    private void renderEmptyChartState() {
        if (chartContainer == null) return;

        chartContainer.getChildren().clear();

        VBox box = new VBox(10);
        box.setPadding(new Insets(16));

        Label title = new Label("Biểu đồ");
        title.setStyle("-fx-font-size:16px; -fx-font-weight:bold; -fx-text-fill:#1565C0;");

        Label thu = new Label("Doanh thu: Chưa có dữ liệu");
        Label chi = new Label("Chi phí: Chưa có dữ liệu");
        Label ln = new Label("Lợi nhuận: Chưa có dữ liệu");

        Label note = new Label("Nhấn 'Thống kê' để hiển thị biểu đồ doanh thu / chi phí / lợi nhuận.");
        note.setWrapText(true);
        note.setStyle("-fx-text-fill:#777777;");

        box.getChildren().addAll(title, thu, chi, ln, note);
        chartContainer.getChildren().add(box);

        if (lblChartPlaceholder != null) {
            lblChartPlaceholder.setVisible(false);
            lblChartPlaceholder.setManaged(false);
        }
    }

    private void renderErrorChartState(String message) {
        if (chartContainer == null) return;

        chartContainer.getChildren().clear();

        VBox box = new VBox(10);
        box.setPadding(new Insets(16));

        Label title = new Label("Biểu đồ");
        title.setStyle("-fx-font-size:16px; -fx-font-weight:bold; -fx-text-fill:#C62828;");

        Label err = new Label("Không thể tải biểu đồ");
        err.setStyle("-fx-text-fill:#C62828; -fx-font-weight:bold;");

        Label detail = new Label(message);
        detail.setWrapText(true);
        detail.setStyle("-fx-text-fill:#777777;");

        box.getChildren().addAll(title, err, detail);
        chartContainer.getChildren().add(box);

        if (lblChartPlaceholder != null) {
            lblChartPlaceholder.setVisible(false);
            lblChartPlaceholder.setManaged(false);
        }
    }

    private void renderChartSummary(double thu, double chi, double loiNhuan) {
        if (chartContainer == null) return;

        chartContainer.getChildren().clear();

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Chi tiêu");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Số tiền (₫)");
        yAxis.setForceZeroInRange(false);

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setLegendVisible(false);
        barChart.setAnimated(false);
        barChart.setCategoryGap(40);
        barChart.setBarGap(10);
        barChart.setTitle("Biểu đồ cột doanh thu - chi phí - lợi nhuận");
        barChart.setPrefHeight(320);
        barChart.setMinHeight(320);
        barChart.setMaxWidth(Double.MAX_VALUE);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        XYChart.Data<String, Number> d1 = new XYChart.Data<>("Doanh thu", thu);
        XYChart.Data<String, Number> d2 = new XYChart.Data<>("Chi phí", chi);
        XYChart.Data<String, Number> d3 = new XYChart.Data<>("Lợi nhuận", loiNhuan);

        series.getData().addAll(d1, d2, d3);
        barChart.getData().add(series);

        Platform.runLater(() -> {
            if (d1.getNode() != null) {
                d1.getNode().setStyle("-fx-bar-fill: #E53935;"); // đỏ = doanh thu
            }
            if (d2.getNode() != null) {
                d2.getNode().setStyle("-fx-bar-fill: #FBC02D;"); // vàng = chi phí
            }
            if (d3.getNode() != null) {
                d3.getNode().setStyle("-fx-bar-fill: #43A047;"); // xanh = lợi nhuận
            }
        });

        // Gắn nhãn số tiền trên cột
        addValueLabel(d1);
        addValueLabel(d2);
        addValueLabel(d3);

        Label title = new Label("Biểu đồ");
        title.setStyle("-fx-font-size:16px; -fx-font-weight:bold; -fx-text-fill:#1565C0;");

        Label chartType = new Label("Loại biểu đồ: Biểu đồ cột");
        chartType.setStyle("-fx-font-weight:bold; -fx-text-fill:#444444;");

        Label summary = new Label(
                "Doanh thu: " + fmtMoney(thu) +
                        "   |   Chi phí: " + fmtMoney(chi) +
                        "   |   Lợi nhuận: " + fmtMoney(loiNhuan)
        );
        summary.setWrapText(true);
        summary.setStyle("-fx-text-fill:#555555;");

        String ketQua;
        if (loiNhuan > 0) ketQua = "Kết quả: Có lãi";
        else if (loiNhuan < 0) ketQua = "Kết quả: Lỗ";
        else ketQua = "Kết quả: Hòa vốn";

        Label lbTrend = new Label(ketQua);
        lbTrend.setStyle("-fx-font-weight:bold; -fx-text-fill:#222222;");

        VBox content = new VBox(10);
        content.setPadding(new Insets(12));
        content.getChildren().addAll(title, chartType, summary, lbTrend, barChart);
        VBox.setVgrow(barChart, Priority.ALWAYS);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background-color:transparent; -fx-background:transparent;");

        chartContainer.getChildren().add(scrollPane);

        if (lblChartPlaceholder != null) {
            lblChartPlaceholder.setVisible(false);
            lblChartPlaceholder.setManaged(false);
        }
    }

    private void addValueLabel(XYChart.Data<String, Number> data) {
        data.nodeProperty().addListener((obs, oldNode, node) -> {
            if (node != null) {
                Label label = new Label(fmtMoney(data.getYValue()));
                label.setStyle(
                        "-fx-font-size:11px;" +
                                "-fx-font-weight:bold;" +
                                "-fx-background-color: rgba(255,255,255,0.85);" +
                                "-fx-padding:2 6 2 6;" +
                                "-fx-background-radius:4;"
                );

                chartContainer.getChildren().add(label);

                node.parentProperty().addListener((o, oldParent, parent) -> {
                    if (parent != null) {
                        node.boundsInParentProperty().addListener((ob, oldBounds, bounds) -> {
                            label.setLayoutX(
                                    node.getBoundsInParent().getMinX()
                                            + node.getBoundsInParent().getWidth() / 2
                                            - label.prefWidth(-1) / 2
                            );
                            label.setLayoutY(
                                    node.getBoundsInParent().getMinY() - 25
                            );
                        });
                    }
                });
            }
        });
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void renderMonthlyLineChart(List<Map<String, Object>> data12Thang, int nam) {
        if (chartContainer == null) return;

        chartContainer.getChildren().clear();

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Tháng");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Số tiền (₫)");
        yAxis.setForceZeroInRange(false);

        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Biểu đồ OXY doanh thu - chi phí - lợi nhuận theo tháng năm " + nam);
        lineChart.setAnimated(false);
        lineChart.setCreateSymbols(true);
        lineChart.setLegendVisible(true);
        lineChart.setPrefHeight(340);
        lineChart.setMinHeight(340);

        XYChart.Series<String, Number> seriesThu = new XYChart.Series<>();
        seriesThu.setName("Doanh thu");

        XYChart.Series<String, Number> seriesChi = new XYChart.Series<>();
        seriesChi.setName("Chi phí");

        XYChart.Series<String, Number> seriesLN = new XYChart.Series<>();
        seriesLN.setName("Lợi nhuận");

        double tongThu = 0;
        double tongChi = 0;
        double tongLN = 0;

        for (Map<String, Object> row : data12Thang) {
            int thang = ((Number) row.get("Thang")).intValue();
            String thangLabel = "T" + thang;

            double thu = getDouble(row, "TongDoanhThu");
            double chi = getDouble(row, "TongNhapHang");
            double ln = getDouble(row, "LoiNhuan");

            tongThu += thu;
            tongChi += chi;
            tongLN += ln;

            seriesThu.getData().add(new XYChart.Data<>(thangLabel, thu));
            seriesChi.getData().add(new XYChart.Data<>(thangLabel, chi));
            seriesLN.getData().add(new XYChart.Data<>(thangLabel, ln));
        }

        lineChart.getData().addAll(seriesThu, seriesChi, seriesLN);

        Label title = new Label("Biểu đồ");
        title.setStyle("-fx-font-size:16px; -fx-font-weight:bold; -fx-text-fill:#1565C0;");

        Label chartType = new Label("Loại biểu đồ: OXY theo tháng");
        chartType.setStyle("-fx-font-weight:bold; -fx-text-fill:#444444;");

        Label guide1 = new Label("Trục ngang (OX): Từng tháng trong năm");
        Label guide2 = new Label("Trục dọc (OY): Số tiền");
        guide1.setStyle("-fx-text-fill:#555555;");
        guide2.setStyle("-fx-text-fill:#555555;");

        Label summary = new Label(
                "Tổng doanh thu năm: " + fmtMoney(tongThu)
                        + "   |   Tổng chi phí năm: " + fmtMoney(tongChi)
                        + "   |   Tổng lợi nhuận năm: " + fmtMoney(tongLN)
        );
        summary.setWrapText(true);
        summary.setStyle("-fx-text-fill:#555555;");

        Label colorNote1 = new Label("Đường màu đỏ: Doanh thu");
        Label colorNote2 = new Label("Đường màu vàng: Chi phí");
        Label colorNote3 = new Label("Đường màu xanh: Lợi nhuận");

        colorNote1.setStyle("-fx-text-fill:#C62828; -fx-font-weight:bold;");
        colorNote2.setStyle("-fx-text-fill:#F9A825; -fx-font-weight:bold;");
        colorNote3.setStyle("-fx-text-fill:#2E7D32; -fx-font-weight:bold;");

        VBox content = new VBox(10);
        content.setPadding(new Insets(12));
        content.getChildren().addAll(
                title,
                chartType,
                guide1,
                guide2,
                summary,
                colorNote1,
                colorNote2,
                colorNote3,
                lineChart
        );
        VBox.setVgrow(lineChart, Priority.ALWAYS);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background-color:transparent; -fx-background:transparent;");

        chartContainer.getChildren().add(scrollPane);

        if (lblChartPlaceholder != null) {
            lblChartPlaceholder.setVisible(false);
            lblChartPlaceholder.setManaged(false);
        }
    }

    private double getDouble(Map<String, Object> map, String key) {
        if (map == null || !map.containsKey(key)) return 0.0;
        Object val = map.get(key);
        if (val instanceof Number) return ((Number) val).doubleValue();
        try {
            return Double.parseDouble(String.valueOf(val));
        } catch (Exception e) {
            return 0.0;
        }
    }
    private String fmtHours(Object val) {
        if (val == null) return "0.00";
        try {
            return String.format("%.2f", ((Number) val).doubleValue());
        } catch (Exception e) {
            return String.valueOf(val);
        }
    }
}

