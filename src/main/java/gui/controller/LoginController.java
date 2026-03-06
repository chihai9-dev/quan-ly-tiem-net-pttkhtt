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

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private ComboBox<String> cmbRole;
    @FXML private Label lblError;

    private KhachHangBUS khachHangBUS = new KhachHangBUS();
    private NhanVienBUS nhanVienBUS = new NhanVienBUS();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Khởi tạo ComboBox loại đăng nhập
        cmbRole.getItems().addAll("Nhân viên", "Khách hàng");
        cmbRole.getSelectionModel().selectFirst();
        lblError.setText("");
    }

    @FXML
    private void handleLogin() {
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        String role = cmbRole.getValue();

        try {
            if ("Khách hàng".equals(role)) {
                KhachHang kh = khachHangBUS.dangNhap(username, password);
                if (kh != null) {
                    chuyenHuongMain("Khách hàng");
                }
            } else {
                NhanVien nv = nhanVienBUS.dangNhap(username, password);
                if (nv != null) {
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
        // Logic mở màn hình Register theo tài liệu
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/view/register-view.fxml"));
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
        Stage currentStage = (Stage) txtUsername.getScene().getWindow();
        currentStage.close();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/view/main-view.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setTitle("Hệ Thống Quản Lý Tiệm Net - " + role);
        stage.setScene(new Scene(root, 1200, 800));
        stage.setMaximized(true);
        stage.show();
    }
}