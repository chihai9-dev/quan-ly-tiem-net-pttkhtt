package gui.controller;

import bus.KhachHangBUS;
import bus.NhanVienBUS;
import entity.KhachHang;
import entity.NhanVien;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private ToggleButton btnRoleKhachHang;
    @FXML private ToggleButton btnRoleNhanVien;
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnLogin;
    @FXML private Label lblError;

    private final KhachHangBUS khachHangBUS = new KhachHangBUS();
    private final NhanVienBUS nhanVienBUS = new NhanVienBUS();
    private boolean isNhanVienMode = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ToggleGroup roleGroup = new ToggleGroup();
        btnRoleKhachHang.setToggleGroup(roleGroup);
        btnRoleNhanVien.setToggleGroup(roleGroup);
        btnRoleKhachHang.setSelected(true);

        // Enter key triggers login
        txtPassword.setOnAction(e -> handleLogin());
        txtUsername.setOnAction(e -> txtPassword.requestFocus());

        // Clear error when typing
        txtUsername.textProperty().addListener((obs, o, n) -> clearError());
        txtPassword.textProperty().addListener((obs, o, n) -> clearError());
    }

    @FXML
    public void handleRoleToggle() {
        isNhanVienMode = btnRoleNhanVien.isSelected();
        clearError();
    }

    @FXML
    public void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        // Basic validation
        if (username.isEmpty()) {
            showError("Vui lòng nhập tên đăng nhập");
            txtUsername.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            showError("Vui lòng nhập mật khẩu");
            txtPassword.requestFocus();
            return;
        }

        btnLogin.setDisable(true);
        btnLogin.setText("Đang đăng nhập...");

        try {
            if (isNhanVienMode) {
                NhanVien nv = nhanVienBUS.dangNhap(username, password);
                utils.SessionManager.setCurrentUser(nv);
                openMain();
            } else {
                KhachHang kh = khachHangBUS.dangNhap(username, password);
                utils.SessionManager.setCurrentUser(kh);
                openMain();
            }
        } catch (Exception e) {
            showError(e.getMessage());
            txtPassword.clear();
            txtPassword.requestFocus();
        } finally {
            btnLogin.setDisable(false);
            btnLogin.setText("ĐĂNG NHẬP");
        }
    }

    @FXML
    public void handleRegister() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/register.fxml"));
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 700));
        } catch (Exception e) {
            showError("Không thể mở trang đăng ký: " + e.getMessage());
        }
    }

    private void openMain() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/main.fxml"));
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            stage.setScene(new Scene(root, 1280, 760));
            stage.setTitle("Hệ Thống Quản Lý Tiệm Internet");
            stage.centerOnScreen();
        } catch (Exception e) {
            showError("Lỗi mở giao diện chính: " + e.getMessage());
        }
    }

    private void showError(String msg) {
        lblError.setText(msg);
        lblError.setVisible(true);
        lblError.setManaged(true);
    }

    private void clearError() {
        lblError.setText("");
        lblError.setVisible(false);
        lblError.setManaged(false);
    }
}
