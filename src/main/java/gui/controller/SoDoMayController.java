package gui.controller;

import bus.MayTinhBUS;
import bus.KhuMayBUS;
import bus.PhienSuDungBUS;
import dao.PhienSuDungDAO;
import dao.MayTinhDAO;
import dao.KhachHangDAO;
import dao.GoiDichVuKhachHangDAO;
import dao.SuDungDichVuDAO;
import dao.HoaDonDAO;
import entity.MayTinh;
import entity.KhuMay;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class SoDoMayController implements Initializable {

    @FXML private VBox vboxKhuMay;
    @FXML private ComboBox<String> cboKhu;
    @FXML private Label lblMayTrong;
    @FXML private Label lblMayDangDung;
    @FXML private Label lblMayBaoTri;
    @FXML private Label lblTongMay;
    @FXML private Label lblSelectedMay;
    @FXML private Label lblAutoRefresh;
    @FXML private Button btnMoPhien;
    @FXML private Button btnKetThuc;

    private final MayTinhBUS mayTinhBUS = new MayTinhBUS();
    private final KhuMayBUS khuMayBUS = new KhuMayBUS();
    private final PhienSuDungBUS phienBUS = new PhienSuDungBUS(
        new PhienSuDungDAO(), new MayTinhDAO(), new KhachHangDAO(),
        new GoiDichVuKhachHangDAO(), new SuDungDichVuDAO(), new HoaDonDAO());
    private MayTinh selectedMay = null;
    private Timeline autoRefresh;
    private int refreshCountdown = 30;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadKhuCombo();
        loadSoDo();
        startAutoRefresh();
    }

    private void loadKhuCombo() {
        try {
            List<KhuMay> khuList = khuMayBUS.getAllKhuMay();
            cboKhu.getItems().clear();
            cboKhu.getItems().add("Tất cả");
            khuList.forEach(k -> cboKhu.getItems().add(k.getTenkhu()));
            cboKhu.setValue("Tất cả");
        } catch (Exception e) {
            cboKhu.getItems().setAll("Tất cả");
        }
    }

    public void loadSoDo() {
        try {
            List<MayTinh> allMay = mayTinhBUS.getAllMayTinh();
            List<KhuMay> allKhu = khuMayBUS.getAllKhuMay();
            updateStats(allMay);
            renderSoDo(allMay, allKhu);
        } catch (Exception e) {
            vboxKhuMay.getChildren().clear();
            Label errLbl = new Label("⚠  Lỗi tải dữ liệu: " + e.getMessage());
            errLbl.setStyle("-fx-text-fill:#C62828;");
            vboxKhuMay.getChildren().add(errLbl);
        }
    }

    private void updateStats(List<MayTinh> list) {
        long trong    = list.stream().filter(m -> "TRONG".equals(m.getTrangThai())).count();
        long dangDung = list.stream().filter(m -> "DANGDUNG".equals(m.getTrangThai())).count();
        long baoTri   = list.stream().filter(m -> "BAOTRI".equals(m.getTrangThai())).count();
        lblMayTrong.setText(String.valueOf(trong));
        lblMayDangDung.setText(String.valueOf(dangDung));
        lblMayBaoTri.setText(String.valueOf(baoTri));
        lblTongMay.setText(String.valueOf(list.size()));
    }

    private void renderSoDo(List<MayTinh> allMay, List<KhuMay> allKhu) {
        String filterKhu = cboKhu.getValue();
        vboxKhuMay.getChildren().clear();

        // Group by khu
        Map<String, List<MayTinh>> grouped = allMay.stream()
            .collect(Collectors.groupingBy(m -> m.getMakhu() == null ? "none" : m.getMakhu()));

        for (KhuMay khu : allKhu) {
            if (!"Tất cả".equals(filterKhu) && !khu.getTenkhu().equals(filterKhu)) continue;

            List<MayTinh> mayInKhu = grouped.getOrDefault(khu.getMakhu(), List.of());

            VBox khuBox = new VBox(10);
            khuBox.setStyle("-fx-background-color:#FFFFFF; -fx-background-radius:10; -fx-padding:16;" +
                            "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.06),6,0,0,2);");

            Label khuLabel = new Label("📍 " + khu.getTenkhu() +
                    "  (" + mayInKhu.size() + " máy)");
            khuLabel.setStyle("-fx-font-size:14px; -fx-font-weight:bold; -fx-text-fill:#1565C0;");
            khuBox.getChildren().add(khuLabel);

            FlowPane flowPane = new FlowPane(12, 12);
            flowPane.setPadding(new Insets(8, 0, 0, 0));

            for (MayTinh may : mayInKhu) {
                VBox card = buildMachineCard(may);
                flowPane.getChildren().add(card);
            }

            khuBox.getChildren().add(flowPane);
            vboxKhuMay.getChildren().add(khuBox);
        }
    }

    private VBox buildMachineCard(MayTinh may) {
        VBox card = new VBox(5);
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(115);
        card.setMaxWidth(115);
        card.setPrefHeight(95);
        card.setCursor(javafx.scene.Cursor.HAND);

        String styleClass = switch (may.getTrangThai()) {
            case "TRONG"    -> "machine-free";
            case "DANGDUNG" -> "machine-using";
            case "BAOTRI"   -> "machine-maintain";
            default          -> "machine-off";
        };
        card.getStyleClass().addAll("machine-card", styleClass);

        String icon = switch (may.getTrangThai()) {
            case "TRONG"    -> "💚";
            case "DANGDUNG" -> "💙";
            case "BAOTRI"   -> "🔧";
            default          -> "⚫";
        };

        Label iconLbl = new Label(icon);
        iconLbl.setStyle("-fx-font-size:22px;");
        Label nameLbl = new Label(may.getTenmay());
        nameLbl.getStyleClass().add("machine-name");
        Label statusLbl = new Label(may.getTrangThai());
        statusLbl.getStyleClass().add("machine-status-label");
        statusLbl.setStyle("-fx-font-size:10px; -fx-text-fill:#555555;");

        card.getChildren().addAll(iconLbl, nameLbl, statusLbl);
        card.setOnMouseClicked(e -> handleMachineClick(may, card));

        return card;
    }

    private void handleMachineClick(MayTinh may, VBox card) {
        selectedMay = may;
        lblSelectedMay.setText("Đã chọn: " + may.getTenmay() + " | Trạng thái: " + may.getTrangThai());

        boolean isTrong    = "TRONG".equals(may.getTrangThai());
        boolean isDangDung = "DANGDUNG".equals(may.getTrangThai());
        btnMoPhien.setDisable(!isTrong);
        btnKetThuc.setDisable(!isDangDung);
    }

    @FXML
    public void handleFilterKhu() { loadSoDo(); }

    @FXML
    public void handleRefresh() {
        selectedMay = null;
        lblSelectedMay.setText("Chưa chọn máy");
        btnMoPhien.setDisable(true);
        btnKetThuc.setDisable(true);
        loadSoDo();
    }

    @FXML
    public void handleMoPhien() {
        if (selectedMay == null) return;
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Mở phiên mới");
        dialog.setHeaderText("Máy: " + selectedMay.getTenmay());
        dialog.setContentText("Nhập mã khách hàng:");
        dialog.showAndWait().ifPresent(maKH -> {
            if (maKH.trim().isEmpty()) return;
            try {
                phienBUS.moPhienMoi(maKH.trim(), selectedMay.getMamay());
                // Refresh
                selectedMay = null;
                btnMoPhien.setDisable(true);
                btnKetThuc.setDisable(true);
                lblSelectedMay.setText("Chưa chọn máy");
                loadSoDo();
            } catch (Exception e) {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setContentText("Lỗi mở phiên: " + e.getMessage());
                alert.showAndWait();
            }
        });
    }

    @FXML
    public void handleKetThucPhien() {
        if (selectedMay == null) return;
        try {
            entity.PhienSuDung phien = phienBUS.getPhienDangChoiByMay(selectedMay.getMamay());
            if (phien == null) {
                javafx.scene.control.Alert a = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.WARNING);
                a.setHeaderText(null);
                a.setContentText("Máy " + selectedMay.getTenmay() + " không có phiên đang chơi.");
                a.showAndWait(); return;
            }
            phienBUS.ketThucPhien(phien.getMaPhien());
            selectedMay = null;
            btnMoPhien.setDisable(true);
            btnKetThuc.setDisable(true);
            lblSelectedMay.setText("Chưa chọn máy");
            loadSoDo();
        } catch (Exception e) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Lỗi kết thúc phiên: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void startAutoRefresh() {
        autoRefresh = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            refreshCountdown--;
            lblAutoRefresh.setText("Tự động cập nhật: " + refreshCountdown + "s");
            if (refreshCountdown <= 0) {
                refreshCountdown = 30;
                loadSoDo();
            }
        }));
        autoRefresh.setCycleCount(Timeline.INDEFINITE);
        autoRefresh.play();
    }
}
