package gui.controller;

import bus.KhachHangBUS;
import bus.NhanVienBUS;
import entity.KhachHang;
import entity.NhanVien;
import utils.SessionManager;
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

    // Khai báo đúng 100% tên biến theo file FXML của bạn
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private ToggleButton btnRoleKhachHang;
    @FXML private ToggleButton btnRoleNhanVien;
    @FXML private Label lblError;
    @FXML private Button btnLogin;

    private KhachHangBUS khachHangBUS = new KhachHangBUS();
    private NhanVienBUS nhanVienBUS = new NhanVienBUS();

    // Nhóm 2 nút Toggle lại để chỉ chọn được 1 trong 2
    private ToggleGroup roleGroup = new ToggleGroup();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (btnRoleKhachHang != null && btnRoleNhanVien != null) {
            btnRoleKhachHang.setToggleGroup(roleGroup);
            btnRoleNhanVien.setToggleGroup(roleGroup);
        }
        if(lblError != null) lblError.setText("");
    }

    @FXML
    private void handleRoleToggle() {
        // Đảm bảo người dùng không thể bỏ chọn cả 2 nút
        if (roleGroup.getSelectedToggle() == null) {
            btnRoleKhachHang.setSelected(true);
        }
    }

    @FXML
    private void handleLogin() {
        String username = txtUsername.getText();
        String password = txtPassword.getText().trim();

        // Kiểm tra xem nút Khách hàng có đang được nhấn không
        boolean isKhachHang = btnRoleKhachHang.isSelected();

        try {
            if (isKhachHang) {
                KhachHang kh = khachHangBUS.dangNhap(username, password);
                if (kh != null) {
                    SessionManager.setCurrentUser(kh);
                    chuyenHuongMain("Khách hàng");
                }
            } else {
                NhanVien nv = nhanVienBUS.dangNhap(username, password);
                if (nv != null) {
                    SessionManager.setCurrentUser(nv);
                    chuyenHuongMain("Nhân viên");
                }
            }
        } catch (Exception e) {
            lblError.setText(e.getMessage());
            lblError.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void handleRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/register.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Đăng ký tài khoản");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void chuyenHuongMain(String role) throws Exception {
        Stage currentStage = (Stage) btnLogin.getScene().getWindow();
        currentStage.close();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setTitle("Hệ Thống Quản Lý Tiệm Net - " + role);
        stage.setScene(new Scene(root, 1280, 800));
        stage.setMaximized(true);
        stage.show();
    }
}