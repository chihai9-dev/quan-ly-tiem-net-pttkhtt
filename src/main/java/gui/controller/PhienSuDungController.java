package gui.controller;

import bus.KhachHangBUS;
import bus.MayTinhBUS;
import bus.PhienSuDungBUS;
import dao.PhienSuDungDAO;
import dao.MayTinhDAO;
import dao.KhachHangDAO;
import dao.GoiDichVuKhachHangDAO;
import dao.SuDungDichVuDAO;
import dao.HoaDonDAO;
import entity.KhachHang;
import entity.MayTinh;
import entity.PhienSuDung;
import gui.dialog.ChonDichVuDialog;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import utils.ThongBaoDialogHelper;
import utils.SessionManager;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class PhienSuDungController implements Initializable {

    @FXML private TableView<PhienSuDung> tableView;
    @FXML private TableColumn<PhienSuDung, String> colMaPhien;
    @FXML private TableColumn<PhienSuDung, String> colMaMay;
    @FXML private TableColumn<PhienSuDung, String> colKhachHang;
    @FXML private TableColumn<PhienSuDung, String> colGioBD;
    @FXML private TableColumn<PhienSuDung, Double> colTongGio;
    @FXML private TableColumn<PhienSuDung, String> colTienTam;
    @FXML private TableColumn<PhienSuDung, String> colTrangThai;

    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cboTrangThai;
    @FXML private Label lblSubtitle;
    @FXML private Label lblTotal;
    @FXML private Label lblLiveTime;
    @FXML private Label lblDangChoi;
    @FXML private Label lblDoanhThuNgay;
    @FXML private Label lblPhienHomNay;
    @FXML private Button btnKetThuc;
    @FXML private Button btnOrderDV;

    private final PhienSuDungBUS phienBUS = new PhienSuDungBUS(
        new PhienSuDungDAO(), new MayTinhDAO(), new KhachHangDAO(),
        new GoiDichVuKhachHangDAO(), new SuDungDichVuDAO(), new HoaDonDAO());
    private ObservableList<PhienSuDung> dataList = FXCollections.observableArrayList();
    private FilteredList<PhienSuDung> filteredList;
    private PhienSuDung selectedPhien;
    private Timeline clockTimeline;
    private Timeline dataTimeline;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (cboTrangThai != null) {
            cboTrangThai.getItems().setAll("Tất cả", "DANGCHOI", "DAKETTHUC");
            cboTrangThai.setValue("Tất cả");
            cboTrangThai.setOnAction(e -> applyFilter());
        }
        setupTableColumns();
        setupTableSelection();
        loadData();
        startClock();
        startDataRefresh();
    }

    private void setupTableColumns() {
        if (colMaPhien   != null) colMaPhien.setCellValueFactory(new PropertyValueFactory<>("maPhien"));
        if (colMaMay     != null) colMaMay.setCellValueFactory(new PropertyValueFactory<>("maMay"));
        if (colKhachHang != null) colKhachHang.setCellValueFactory(new PropertyValueFactory<>("maKH"));
        if (colGioBD     != null) {
            colGioBD.setCellValueFactory(c -> {
                PhienSuDung p = c.getValue();
                String val = p.getGioBatDau() != null
                    ? p.getGioBatDau().format(DateTimeFormatter.ofPattern("dd/MM HH:mm")) : "";
                return new javafx.beans.property.SimpleStringProperty(val);
            });
        }
        if (colTongGio   != null) colTongGio.setCellValueFactory(new PropertyValueFactory<>("tongGio"));
        if (colTienTam   != null) {
            colTienTam.setCellValueFactory(c -> {
                double tien = c.getValue().getTienGioChoi();
                return new javafx.beans.property.SimpleStringProperty(String.format("%,.0f ₫", tien));
            });
        }
        if (colTrangThai != null) {
            colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
            colTrangThai.setCellFactory(col -> new TableCell<>() {
                @Override protected void updateItem(String val, boolean empty) {
                    super.updateItem(val, empty);
                    if (empty || val == null) { setText(null); setStyle(""); return; }
                    setText("DANGCHOI".equals(val) ? "Đang chơi" : "Đã kết thúc");
                    setStyle("DANGCHOI".equals(val)
                        ? "-fx-text-fill:#1565C0; -fx-font-weight:bold;"
                        : "-fx-text-fill:#555555;");
                }
            });
        }
    }

    private void setupTableSelection() {
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            selectedPhien = n;
            boolean isDangChoi = n != null && "DANGCHOI".equals(n.getTrangThai());
            if (btnKetThuc != null) btnKetThuc.setDisable(!isDangChoi);
            if (btnOrderDV != null) btnOrderDV.setDisable(!isDangChoi);
        });
        if (btnKetThuc != null) btnKetThuc.setDisable(true);
        if (btnOrderDV != null) btnOrderDV.setDisable(true);
    }

    public void loadData() {
        try {
            List<PhienSuDung> list = phienBUS.getAllPhien();
            dataList.setAll(list);
            filteredList = new FilteredList<>(dataList, p -> true);
            tableView.setItems(filteredList);
            updateStats(list);
            updateLabels();
        } catch (Exception e) {
            if (lblSubtitle != null) lblSubtitle.setText("Lỗi: " + e.getMessage());
        }
    }

    private void updateStats(List<PhienSuDung> list) {
        long dangChoi = list.stream().filter(p -> "DANGCHOI".equals(p.getTrangThai())).count();
        if (lblDangChoi     != null) lblDangChoi.setText(String.valueOf(dangChoi));
        if (lblPhienHomNay  != null) lblPhienHomNay.setText(String.valueOf(list.size()));
        double doanhThu = list.stream()
            .filter(p -> "DAKETTHUC".equals(p.getTrangThai()))
            .mapToDouble(PhienSuDung::getTienGioChoi).sum();
        if (lblDoanhThuNgay != null) lblDoanhThuNgay.setText(String.format("%,.0f ₫", doanhThu));
    }

    private void updateLabels() {
        int total = filteredList != null ? filteredList.size() : 0;
        if (lblTotal    != null) lblTotal.setText("Tổng: " + total + " phiên");
        if (lblSubtitle != null) lblSubtitle.setText("Tổng " + total + " bản ghi");
    }

    @FXML public void handleSearch() { applyFilter(); }
    @FXML public void handleFilter() { applyFilter(); }

    private void applyFilter() {
        String keyword = txtSearch    != null ? txtSearch.getText().toLowerCase().trim() : "";
        String status  = cboTrangThai != null ? cboTrangThai.getValue() : "Tất cả";
        if (filteredList == null) return;
        filteredList.setPredicate(item -> {
            boolean matchKw = keyword.isEmpty()
                || (item.getMaPhien() != null && item.getMaPhien().toLowerCase().contains(keyword))
                || (item.getMaMay()   != null && item.getMaMay().toLowerCase().contains(keyword))
                || (item.getMaKH()    != null && item.getMaKH().toLowerCase().contains(keyword));
            boolean matchStatus = status == null || "Tất cả".equals(status) || status.equals(item.getTrangThai());
            return matchKw && matchStatus;
        });
        updateLabels();
    }

    @FXML
    public void handleMoPhien() {
        // Dialog chọn Khách hàng
        TextInputDialog khDialog = new TextInputDialog();
        khDialog.setTitle("Mở phiên mới");
        khDialog.setHeaderText("Nhập mã khách hàng:");
        khDialog.setContentText("Mã KH:");
        Optional<String> maKH = khDialog.showAndWait();
        if (maKH.isEmpty() || maKH.get().trim().isEmpty()) return;

        TextInputDialog mayDialog = new TextInputDialog();
        mayDialog.setTitle("Mở phiên mới");
        mayDialog.setHeaderText("Nhập mã máy:");
        mayDialog.setContentText("Mã máy:");
        Optional<String> maMay = mayDialog.showAndWait();
        if (maMay.isEmpty() || maMay.get().trim().isEmpty()) return;

        try {
            PhienSuDung phien = phienBUS.moPhienMoi(maKH.get().trim(), maMay.get().trim());
            ThongBaoDialogHelper.showSuccess(tableView.getScene(),
                "Đã mở phiên " + phien.getMaPhien() + " cho KH " + phien.getMaKH());
            loadData();
        } catch (Exception e) {
            ThongBaoDialogHelper.showError(tableView.getScene(), "Lỗi mở phiên: " + e.getMessage());
        }
    }

    @FXML
    public void handleKetThucPhien() {
        if (selectedPhien == null) return;
        Stage owner = (Stage) tableView.getScene().getWindow();
        boolean confirmed = gui.dialog.XacNhanDialog.show(owner,
            "Kết thúc phiên",
            "Bạn có chắc muốn kết thúc phiên " + selectedPhien.getMaPhien() + "?");
        if (!confirmed) return;
        try {
            PhienSuDung ketQua = phienBUS.ketThucPhien(selectedPhien.getMaPhien());
            ThongBaoDialogHelper.showSuccess(tableView.getScene(),
                String.format("Kết thúc phiên %s | Tổng giờ: %.2f | Tiền: %,.0f ₫",
                    ketQua.getMaPhien(), ketQua.getTongGio(), ketQua.getTienGioChoi()));
            loadData();
        } catch (Exception e) {
            ThongBaoDialogHelper.showError(tableView.getScene(), "Lỗi kết thúc phiên: " + e.getMessage());
        }
    }

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
            ThongBaoDialogHelper.showError(tableView.getScene(), "Không thể mở dialog: " + e.getMessage());
        }
    }

    @FXML
    public void handleRefresh() {
        if (txtSearch    != null) txtSearch.clear();
        if (cboTrangThai != null) cboTrangThai.setValue("Tất cả");
        loadData();
    }

    private void startClock() {
        if (lblLiveTime == null) return;
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm:ss");
        clockTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e ->
            lblLiveTime.setText("🕐 " + LocalDateTime.now().format(fmt))));
        clockTimeline.setCycleCount(Timeline.INDEFINITE);
        clockTimeline.play();
    }

    private void startDataRefresh() {
        dataTimeline = new Timeline(new KeyFrame(Duration.minutes(1), e -> loadData()));
        dataTimeline.setCycleCount(Timeline.INDEFINITE);
        dataTimeline.play();
    }
}
