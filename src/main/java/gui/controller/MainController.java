package gui.controller;

import utils.SessionManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML private Label lblUserName;
    @FXML private Label lblUserRole;
    @FXML private Label lblAvatarChar;
    @FXML private Label lblPageTitle;
    @FXML private Label lblCurrentTime;
    @FXML private StackPane contentPane;

    // Menu buttons
    @FXML private Button btnSoDoMay;
    @FXML private Button btnPhienSuDung;
    @FXML private Button btnNapTien;
    @FXML private Button btnHoaDon;
    @FXML private Button btnDichVu;
    @FXML private Button btnGoiDichVu;
    @FXML private Button btnKhuyenMai;
    @FXML private Button btnNhapHang;
    @FXML private Button btnKhachHang;
    @FXML private Button btnNhanVien;
    @FXML private Button btnMayTinh;
    @FXML private Button btnKhuMay;
    @FXML private Button btnThongKe;

    private Button activeMenuButton = null;
    private Timeline clockTimeline;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupUserInfo();
        startClock();
        // Load default page
        loadSoDoMay();
    }

    private void setupUserInfo() {
        String hoTen = "Người dùng";
        String role  = "Chưa xác định";
        try {
            if (SessionManager.isLoggedIn()) {
                hoTen = SessionManager.getCurrentUserName();
                role  = SessionManager.getLoaiTaiKhoan();
                // Hiển thị tên role đẹp hơn
                role = switch (role != null ? role : "") {
                    case "QUANLY"   -> "Quản lý";
                    case "NHANVIEN" -> "Nhân viên";
                    case "THUNGAN"  -> "Thu ngân";
                    case "KHACHHANG"-> "Khách hàng";
                    default -> role;
                };
            }
        } catch (Exception ignored) {}
        if (lblUserName   != null) lblUserName.setText(hoTen);
        if (lblUserRole   != null) lblUserRole.setText(role);
        if (lblAvatarChar != null && !hoTen.isEmpty()) {
            lblAvatarChar.setText(String.valueOf(hoTen.charAt(0)).toUpperCase());
        }
    }

    private void startClock() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm:ss - dd/MM/yyyy");
        clockTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            lblCurrentTime.setText(LocalDateTime.now().format(fmt));
        }));
        clockTimeline.setCycleCount(Timeline.INDEFINITE);
        clockTimeline.play();
    }

    private void setActiveButton(Button btn) {
        if (activeMenuButton != null) {
            activeMenuButton.getStyleClass().remove("active");
        }
        btn.getStyleClass().add("active");
        activeMenuButton = btn;
    }

    private void loadPage(String fxmlPath, String title, Button menuBtn) {
        try {
            Node page = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentPane.getChildren().setAll(page);
            lblPageTitle.setText(title);
            setActiveButton(menuBtn);
        } catch (Exception e) {
            showErrorPage("Không thể tải trang: " + e.getMessage());
        }
    }

    private void showErrorPage(String msg) {
        Label lbl = new Label("⚠  " + msg);
        lbl.setStyle("-fx-text-fill: #C62828; -fx-font-size: 14px;");
        contentPane.getChildren().setAll(lbl);
    }

    // ===== MENU HANDLERS =====
    @FXML public void loadSoDoMay()      { loadPage("/fxml/sodoMay.fxml",     "Sơ Đồ Máy",          btnSoDoMay); }
    @FXML public void loadPhienSuDung()  { loadPage("/fxml/phienSuDung.fxml", "Phiên Sử Dụng",      btnPhienSuDung); }
    @FXML public void loadNapTien()      { loadPage("/fxml/napTien.fxml",     "Nạp Tiền",            btnNapTien); }
    @FXML public void loadHoaDon()       { loadPage("/fxml/hoaDon.fxml",      "Hóa Đơn",             btnHoaDon); }
    @FXML public void loadDichVu()       { loadPage("/fxml/dichVu.fxml",      "Dịch Vụ",             btnDichVu); }
    @FXML public void loadGoiDichVu()    { loadPage("/fxml/goiDichVu.fxml",   "Gói Dịch Vụ",        btnGoiDichVu); }
    @FXML public void loadKhuyenMai()    { loadPage("/fxml/khuyenMai.fxml",   "Khuyến Mãi",          btnKhuyenMai); }
    @FXML public void loadNhapHang()     { loadPage("/fxml/nhapHang.fxml",    "Nhập Hàng",           btnNhapHang); }
    @FXML public void loadKhachHang()    { loadPage("/fxml/khachHang.fxml",   "Quản Lý Khách Hàng", btnKhachHang); }
    @FXML public void loadNhanVien()     { loadPage("/fxml/nhanVien.fxml",    "Quản Lý Nhân Viên",  btnNhanVien); }
    @FXML public void loadMayTinh()      { loadPage("/fxml/mayTinh.fxml",     "Quản Lý Máy Tính",   btnMayTinh); }
    @FXML public void loadKhuMay()       { loadPage("/fxml/khuMay.fxml",      "Quản Lý Khu Máy",    btnKhuMay); }
    @FXML public void loadThongKe()      { loadPage("/fxml/thongKe.fxml",     "Thống Kê & Báo Cáo", btnThongKe); }

    @FXML
    public void handleLogout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Đăng xuất");
        confirm.setHeaderText(null);
        confirm.setContentText("Bạn có chắc muốn đăng xuất?");
        confirm.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                if (clockTimeline != null) clockTimeline.stop();
                SessionManager.clearSession();
                try {
                    Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
                    Stage stage = (Stage) contentPane.getScene().getWindow();
                    stage.setScene(new Scene(root, 900, 700));
                    stage.centerOnScreen();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
