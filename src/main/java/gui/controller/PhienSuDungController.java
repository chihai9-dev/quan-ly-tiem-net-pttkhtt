package gui.controller;

import bus.PhienSuDungBUS;
import dao.*;
import entity.KhachHang;
import entity.MayTinh;
import entity.PhienSuDung;
import gui.dialog.ChonDichVuDialog;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import utils.ThongBaoDialogHelper;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * ============================================================
 *  PhienSuDungController  –  Tầng UI cho Phiên Sử Dụng
 * ============================================================
 *
 *  Controller chỉ làm 3 việc:
 *    1. Nhận sự kiện từ người dùng (click, nhập text…)
 *    2. Gọi BUS để xử lý nghiệp vụ
 *    3. Cập nhật giao diện theo kết quả
 *
 *  Không có Timer/Scheduler ở đây.
 *  Kiểm tra phiên quá hạn xảy ra khi nhấn Refresh.
 */
public class PhienSuDungController implements Initializable {

    // ================================================================
    //  PHẦN 1: CÁC FXML COMPONENT
    // ================================================================

    @FXML private TableView<PhienSuDung>           tableView;
    @FXML private TableColumn<PhienSuDung, String> colMaPhien;
    @FXML private TableColumn<PhienSuDung, String> colMaMay;
    @FXML private TableColumn<PhienSuDung, String> colKhachHang;
    @FXML private TableColumn<PhienSuDung, String> colNhanVien;
    @FXML private TableColumn<PhienSuDung, String> colGioBD;
    @FXML private TableColumn<PhienSuDung, String> colThoiGian;
    @FXML private TableColumn<PhienSuDung, String> colTienTam;
    @FXML private TableColumn<PhienSuDung, String> colTrangThai;

    @FXML private TextField        txtSearch;
    @FXML private ComboBox<String> cboTrangThai;
    @FXML private Label            lblSubtitle;
    @FXML private Label            lblTotal;
    @FXML private Label            lblLiveTime;
    @FXML private Label            lblDangChoi;
    @FXML private Label            lblDoanhThuNgay;
    @FXML private Label            lblPhienHomNay;
    @FXML private Button           btnKetThuc;
    @FXML private Button           btnOrderDV;

    // ================================================================
    //  PHẦN 2: BUS VÀ DAO PHỤ
    // ================================================================

    private final PhienSuDungBUS phienBUS = new PhienSuDungBUS(
            new PhienSuDungDAO(), new MayTinhDAO(), new KhachHangDAO(),
            new GoiDichVuKhachHangDAO(), new SuDungDichVuDAO(), new HoaDonDAO());

    // DAO phụ chỉ để lấy danh sách hiển thị trong dialog
    private final KhachHangDAO khachHangDAO = new KhachHangDAO();
    private final MayTinhDAO   mayTinhDAO   = new MayTinhDAO();

    // ================================================================
    //  PHẦN 3: STATE NỘI BỘ
    // ================================================================

    private final ObservableList<PhienSuDung> dataList     = FXCollections.observableArrayList();
    private FilteredList<PhienSuDung>         filteredList;
    private PhienSuDung                       selectedPhien;
    private Timeline                          clockTimeline;
    private Timeline                          cellRefreshTimeline;

    // ================================================================
    //  PHẦN 4: KHỞI TẠO
    // ================================================================

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupComboBox();
        setupTableColumns();
        setupTableSelection();
        loadData();  // tải dữ liệu (KHÔNG kiểm tra quá hạn ở đây, để MainController làm)
        startClock();
        startCellRefresh();
    }

    private void setupComboBox() {
        if (cboTrangThai != null) {
            cboTrangThai.getItems().setAll("Tất cả", "DANGCHOI", "DAKETTHUC");
            cboTrangThai.setValue("Tất cả");
            cboTrangThai.setOnAction(e -> applyFilter());
        }
    }

    // ================================================================
    //  PHẦN 5: CẤU HÌNH CỘT BẢNG
    // ================================================================

    private void setupTableColumns() {
        if (colMaPhien   != null) colMaPhien.setCellValueFactory(new PropertyValueFactory<>("maPhien"));
        if (colMaMay     != null) colMaMay.setCellValueFactory(new PropertyValueFactory<>("maMay"));
        if (colKhachHang != null) colKhachHang.setCellValueFactory(new PropertyValueFactory<>("maKH"));
        if (colNhanVien  != null) colNhanVien.setCellValueFactory(new PropertyValueFactory<>("maNV"));

        // Cột giờ bắt đầu: định dạng lại từ LocalDateTime
        if (colGioBD != null) {
            colGioBD.setCellValueFactory(cell -> {
                PhienSuDung p = cell.getValue();
                return new SimpleStringProperty(p.getGioBatDau() != null
                        ? p.getGioBatDau().format(DateTimeFormatter.ofPattern("dd/MM HH:mm")) : "");
            });
        }

        // Cột thời gian: cố định nếu đã kết thúc, realtime nếu đang chơi
        if (colThoiGian != null) {
            colThoiGian.setCellValueFactory(cell -> {
                PhienSuDung p = cell.getValue();
                if ("DAKETTHUC".equals(p.getTrangThai()) && p.getTongGio() > 0)
                    return new SimpleStringProperty(String.format("%.2f giờ", p.getTongGio()));
                if (p.getGioBatDau() != null) {
                    long phut = ChronoUnit.MINUTES.between(p.getGioBatDau(), LocalDateTime.now());
                    return new SimpleStringProperty(String.format("%dh%02dm", phut / 60, phut % 60));
                }
                return new SimpleStringProperty("--");
            });
        }

        // Cột tiền tạm: realtime cho phiên đang chơi
        if (colTienTam != null) {
            colTienTam.setCellValueFactory(cell -> {
                PhienSuDung p = cell.getValue();
                double tien = p.getTienGioChoi();
                if ("DANGCHOI".equals(p.getTrangThai()) && p.getGioBatDau() != null && p.getGiaMoiGio() > 0) {
                    long phut = ChronoUnit.MINUTES.between(p.getGioBatDau(), LocalDateTime.now());
                    tien = (phut / 60.0) * p.getGiaMoiGio();
                }
                return new SimpleStringProperty(String.format("%,.0f ₫", tien));
            });
        }

        // Cột trạng thái: icon + màu sắc
        if (colTrangThai != null) {
            colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
            colTrangThai.setCellFactory(col -> new TableCell<>() {
                @Override protected void updateItem(String val, boolean empty) {
                    super.updateItem(val, empty);
                    if (empty || val == null) { setText(null); setStyle(""); return; }
                    if ("DANGCHOI".equals(val)) {
                        setText("🟢 Đang chơi");
                        setStyle("-fx-text-fill:#1565C0; -fx-font-weight:bold;");
                    } else {
                        setText("⬜ Đã kết thúc");
                        setStyle("-fx-text-fill:#555555;");
                    }
                }
            });
        }
    }

    /** Xử lý khi click vào hàng: lưu phiên đang chọn, bật/tắt nút */
    private void setupTableSelection() {
        if (tableView == null) return;
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, cu, moi) -> {
            selectedPhien = moi;
            boolean dangChoi = moi != null && "DANGCHOI".equals(moi.getTrangThai());
            if (btnKetThuc != null) btnKetThuc.setDisable(!dangChoi);
            if (btnOrderDV != null) btnOrderDV.setDisable(!dangChoi);
        });
        if (btnKetThuc != null) btnKetThuc.setDisable(true);
        if (btnOrderDV != null) btnOrderDV.setDisable(true);
    }

    // ================================================================
    //  PHẦN 6: TẢI VÀ HIỂN THỊ DỮ LIỆU
    // ================================================================

    public void loadData() {
        try {
            List<PhienSuDung> danhSach = phienBUS.getAllPhien();
            dataList.setAll(danhSach);
            filteredList = new FilteredList<>(dataList, p -> true);
            tableView.setItems(filteredList);
            applyFilter();
            updateStats(danhSach);
        } catch (Exception e) {
            e.printStackTrace();
            if (lblSubtitle != null) lblSubtitle.setText("Lỗi: " + e.getMessage());
        }
    }

    private void updateStats(List<PhienSuDung> ds) {
        long soDangChoi = ds.stream().filter(p -> "DANGCHOI".equals(p.getTrangThai())).count();
        double doanhThu = ds.stream()
                .filter(p -> "DAKETTHUC".equals(p.getTrangThai()))
                .mapToDouble(PhienSuDung::getTienGioChoi).sum();
        if (lblDangChoi     != null) lblDangChoi.setText(String.valueOf(soDangChoi));
        if (lblPhienHomNay  != null) lblPhienHomNay.setText(String.valueOf(ds.size()));
        if (lblDoanhThuNgay != null) lblDoanhThuNgay.setText(String.format("%,.0f ₫", doanhThu));
    }

    private void updateLabels() {
        int total = filteredList != null ? filteredList.size() : 0;
        if (lblTotal    != null) lblTotal.setText("Tổng: " + total + " phiên");
        if (lblSubtitle != null) lblSubtitle.setText("Tổng " + total + " bản ghi");
    }

    // ================================================================
    //  PHẦN 7: LỌC DỮ LIỆU
    // ================================================================

    @FXML public void handleSearch() { applyFilter(); }
    @FXML public void handleFilter() { applyFilter(); }

    private void applyFilter() {
        String kw = txtSearch    != null ? txtSearch.getText().toLowerCase().trim() : "";
        String st = cboTrangThai != null ? cboTrangThai.getValue() : "Tất cả";
        if (filteredList == null) return;
        filteredList.setPredicate(p -> {
            boolean hopKw = kw.isEmpty()
                    || (p.getMaPhien() != null && p.getMaPhien().toLowerCase().contains(kw))
                    || (p.getMaMay()   != null && p.getMaMay().toLowerCase().contains(kw))
                    || (p.getMaKH()    != null && p.getMaKH().toLowerCase().contains(kw));
            boolean hopSt = "Tất cả".equals(st) || st.equals(p.getTrangThai());
            return hopKw && hopSt;
        });
        updateLabels();
    }

    // ================================================================
    //  PHẦN 8: MỞ PHIÊN
    // ================================================================

    @FXML
    public void handleMoPhien() {
        List<KhachHang> dsKH;
        List<MayTinh>   dsMay;
        try {
            dsKH  = khachHangDAO.getAll().stream()
                    .filter(kh -> "HOATDONG".equals(kh.getTrangthai())).collect(Collectors.toList());
            dsMay = mayTinhDAO.getAll().stream()
                    .filter(m -> "TRONG".equals(m.getTrangthai())).collect(Collectors.toList());
        } catch (Exception e) {
            ThongBaoDialogHelper.showError(tableView.getScene(), "Lỗi tải dữ liệu:\n" + e.getMessage());
            return;
        }
        if (dsKH.isEmpty())  { ThongBaoDialogHelper.showError(tableView.getScene(), "Không có khách hàng nào đang hoạt động."); return; }
        if (dsMay.isEmpty()) { ThongBaoDialogHelper.showError(tableView.getScene(), "Không có máy nào đang trống."); return; }

        Stage dialog = makeDialog();

        ComboBox<KhachHang> cboKH = new ComboBox<>();
        cboKH.getItems().setAll(dsKH);
        cboKH.setMaxWidth(Double.MAX_VALUE);
        cboKH.setPromptText("-- Chọn khách hàng --");
        cboKH.setCellFactory(lv -> khachHangCell());
        cboKH.setButtonCell(khachHangCell());

        Label lblSoDu = new Label("");
        lblSoDu.setStyle("-fx-text-fill:#388E3C; -fx-font-size:12px;");
        cboKH.setOnAction(e -> {
            KhachHang kh = cboKH.getValue();
            lblSoDu.setText(kh != null ? "Số dư: " + String.format("%,.0f ₫", kh.getSodu()) : "");
        });

        ComboBox<MayTinh> cboMay = new ComboBox<>();
        cboMay.getItems().setAll(dsMay);
        cboMay.setMaxWidth(Double.MAX_VALUE);
        cboMay.setPromptText("-- Chọn máy --");
        cboMay.setCellFactory(lv -> mayTinhCell());
        cboMay.setButtonCell(mayTinhCell());

        Label  lblLoi = errLabel();
        Button btnOk  = primaryBtn("✔  Mở phiên", "#1565C0");
        Button btnHuy = secondaryBtn();
        btnHuy.setOnAction(e -> dialog.close());

        VBox body = new VBox(10, boldLabel("Khách hàng:"), cboKH, lblSoDu,
                boldLabel("Máy tính trống:"), cboMay, lblLoi);

        btnOk.setOnAction(e -> {
            KhachHang kh  = cboKH.getValue();
            MayTinh   may = cboMay.getValue();
            if (kh  == null) { lblLoi.setText("⚠ Vui lòng chọn khách hàng!"); return; }
            if (may == null) { lblLoi.setText("⚠ Vui lòng chọn máy!");         return; }
            try {
                PhienSuDung phien = phienBUS.moPhienMoi(kh.getMakh(), may.getMamay());
                dialog.close();
                ThongBaoDialogHelper.showSuccess(tableView.getScene(),
                        "✔ Đã mở phiên " + phien.getMaPhien()
                                + "\nKhách: " + kh.getHo() + " " + kh.getTen()
                                + "  |  Máy: " + may.getTenmay());
                loadData();
            } catch (Exception ex) {
                lblLoi.setText("⚠ " + ex.getMessage());
            }
        });

        dialog.setScene(new Scene(buildRoot("▶  Mở Phiên Mới", "#1565C0", body, btnOk, btnHuy)));
        dialog.showAndWait();
    }

    // ================================================================
    //  PHẦN 9: KẾT THÚC PHIÊN THỦ CÔNG
    // ================================================================

    @FXML
    public void handleKetThucPhien() {
        if (selectedPhien == null) {
            ThongBaoDialogHelper.showError(tableView.getScene(), "Vui lòng chọn một phiên trong bảng.");
            return;
        }
        if (!"DANGCHOI".equals(selectedPhien.getTrangThai())) {
            ThongBaoDialogHelper.showError(tableView.getScene(), "Phiên này đã kết thúc rồi.");
            return;
        }
        showKetThucDialog(selectedPhien);
    }

    private void showKetThucDialog(PhienSuDung phien) {
        long phut = phien.getGioBatDau() != null
                ? ChronoUnit.MINUTES.between(phien.getGioBatDau(), LocalDateTime.now()) : 0;
        double tienTam = (phut / 60.0) * phien.getGiaMoiGio();

        Stage dialog = makeDialog();

        GridPane grid = new GridPane();
        grid.setHgap(16); grid.setVgap(8);
        grid.setStyle("-fx-background-color:#FFF8F8; -fx-padding:12; -fx-background-radius:6;");
        addRow(grid, 0, "Mã phiên:",       phien.getMaPhien());
        addRow(grid, 1, "Khách hàng:",     phien.getMaKH());
        addRow(grid, 2, "Máy:",            phien.getMaMay());
        addRow(grid, 3, "Giờ bắt đầu:",   phien.getGioBatDau() != null
                ? phien.getGioBatDau().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "--");
        addRow(grid, 4, "Thời gian chơi:", phut / 60 + "h " + String.format("%02dm", phut % 60));
        addRow(grid, 5, "Tiền tạm tính:",
                String.format("%,.0f ₫  (@ %,.0f ₫/giờ)", tienTam, phien.getGiaMoiGio()));

        Label  lblLoi = errLabel();
        Button btnOk  = primaryBtn("■  Xác nhận kết thúc", "#C62828");
        Button btnHuy = secondaryBtn();
        btnHuy.setOnAction(e -> dialog.close());

        btnOk.setOnAction(e -> {
            try {
                PhienSuDung kq = phienBUS.ketThucPhien(phien.getMaPhien());
                dialog.close();
                ThongBaoDialogHelper.showSuccess(tableView.getScene(),
                        String.format("✔ Kết thúc phiên %s\nTổng giờ: %.2f  |  Tiền: %,.0f ₫",
                                kq.getMaPhien(), kq.getTongGio(), kq.getTienGioChoi()));
                loadData();
            } catch (Exception ex) {
                lblLoi.setText("⚠ " + ex.getMessage());
            }
        });

        dialog.setScene(new Scene(buildRoot("■  Kết Thúc Phiên", "#C62828",
                new VBox(10, grid, lblLoi), btnOk, btnHuy)));
        dialog.showAndWait();
    }

    // ================================================================
    //  PHẦN 10: REFRESH  ←  ĐIỂM GỌI KIỂM TRA PHIÊN QUÁ HẠN
    // ================================================================

    /**
     * Nhân viên bấm nút Refresh:
     *   1. Kiểm tra và kết thúc các phiên quá hạn
     *   2. Hiển thị thông báo số lượng phiên bị kết thúc (nếu có)
     *   3. Tải lại bảng dữ liệu
     */
    @FXML
    public void handleRefresh() {
        // Reset bộ lọc
        if (txtSearch    != null) txtSearch.clear();
        if (cboTrangThai != null) cboTrangThai.setValue("Tất cả");

        // Kiểm tra phiên quá hạn và kết thúc
        List<PhienSuDung> danhSachDaKetThuc = phienBUS.kiemTraVaKetThucPhienQuaHan();

        // Thông báo nếu có phiên bị kết thúc tự động
        if (!danhSachDaKetThuc.isEmpty()) {
            ThongBaoDialogHelper.showWarning(tableView.getScene(),
                    "⚠ Tự động kết thúc " + danhSachDaKetThuc.size()
                            + " phiên do khách hàng hết tiền.\n"
                            + "Hóa đơn đã được tạo tự động.");
        }

        // Tải lại bảng
        loadData();
    }

    // ================================================================
    //  PHẦN 11: ORDER DỊCH VỤ
    // ================================================================

    @FXML
    public void handleOrderDichVu() {
        if (selectedPhien == null) return;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dialogs/chonDichVu.fxml"));
            Parent root = loader.load();
            ChonDichVuDialog ctrl = loader.getController();
            ctrl.setPhien(selectedPhien.getMaPhien(),
                    selectedPhien.getMaKH() + " | " + selectedPhien.getMaMay());
            ctrl.setOnOrderCallback(this::loadData);
            Stage stage = new Stage(StageStyle.UNDECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(tableView.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (Exception e) {
            ThongBaoDialogHelper.showError(tableView.getScene(), "Không thể mở dialog:\n" + e.getMessage());
        }
    }

    // ================================================================
    //  PHẦN 12: TIMER GIAO DIỆN
    // ================================================================

    /** Đồng hồ realtime HH:mm:ss, cập nhật mỗi giây */
    private void startClock() {
        if (lblLiveTime == null) return;
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm:ss");
        clockTimeline = new Timeline(new KeyFrame(Duration.seconds(1),
                e -> lblLiveTime.setText("🕐 " + LocalDateTime.now().format(fmt))));
        clockTimeline.setCycleCount(Timeline.INDEFINITE);
        clockTimeline.play();
    }

    /** Refresh cột thời gian & tiền tạm mỗi 30 giây (không gọi DB) */
    private void startCellRefresh() {
        cellRefreshTimeline = new Timeline(new KeyFrame(Duration.seconds(30),
                e -> { if (tableView != null) tableView.refresh(); }));
        cellRefreshTimeline.setCycleCount(Timeline.INDEFINITE);
        cellRefreshTimeline.play();
    }

    // ================================================================
    //  PHẦN 13: UI HELPERS
    // ================================================================

    private Stage makeDialog() {
        Stage s = new Stage(StageStyle.UNDECORATED);
        s.initModality(Modality.APPLICATION_MODAL);
        s.initOwner(tableView.getScene().getWindow());
        return s;
    }

    private VBox buildRoot(String title, String color, VBox body, Button btnOk, Button btnHuy) {
        VBox root = new VBox(14);
        root.setPadding(new Insets(24));
        root.setPrefWidth(500);
        root.setStyle("-fx-background-color:white; -fx-border-color:#E0E0E0; "
                + "-fx-border-width:1; -fx-border-radius:8; -fx-background-radius:8;");
        Label lbl = new Label(title);
        lbl.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill:" + color + ";");
        HBox btnRow = new HBox(10, btnOk, btnHuy);
        btnRow.setAlignment(Pos.CENTER_RIGHT);
        root.getChildren().addAll(lbl, new Separator(), body, new Separator(), btnRow);
        return root;
    }

    private Label boldLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-weight:bold; -fx-font-size:13px;");
        return l;
    }

    private Label errLabel() {
        Label l = new Label("");
        l.setStyle("-fx-text-fill:#C62828; -fx-font-size:12px; -fx-wrap-text:true;");
        l.setMaxWidth(Double.MAX_VALUE);
        return l;
    }

    private Button primaryBtn(String text, String color) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color:" + color + "; -fx-text-fill:white; "
                + "-fx-font-weight:bold; -fx-pref-height:36px; -fx-pref-width:190px; -fx-background-radius:6;");
        return b;
    }

    private Button secondaryBtn() {
        Button b = new Button("✖  Hủy");
        b.setStyle("-fx-background-color:#E0E0E0; -fx-pref-height:36px; "
                + "-fx-pref-width:90px; -fx-background-radius:6;");
        return b;
    }

    private void addRow(GridPane grid, int row, String label, String value) {
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill:#757575; -fx-font-size:12px;");
        Label val = new Label(value != null ? value : "--");
        val.setStyle("-fx-font-weight:bold; -fx-font-size:13px;");
        grid.add(lbl, 0, row);
        grid.add(val, 1, row);
        GridPane.setHgrow(val, Priority.ALWAYS);
    }

    private ListCell<KhachHang> khachHangCell() {
        return new ListCell<>() {
            @Override protected void updateItem(KhachHang kh, boolean empty) {
                super.updateItem(kh, empty);
                if (empty || kh == null) { setText(null); return; }
                setText(String.format("%s  |  %s %s  |  %,.0f ₫",
                        kh.getMakh(), kh.getHo(), kh.getTen(), kh.getSodu()));
            }
        };
    }

    private ListCell<MayTinh> mayTinhCell() {
        return new ListCell<>() {
            @Override protected void updateItem(MayTinh m, boolean empty) {
                super.updateItem(m, empty);
                if (empty || m == null) { setText(null); return; }
                setText(String.format("%s  |  %s  |  %,.0f ₫/giờ",
                        m.getMamay(), m.getTenmay(), m.getGiamoigio()));
            }
        };
    }
}