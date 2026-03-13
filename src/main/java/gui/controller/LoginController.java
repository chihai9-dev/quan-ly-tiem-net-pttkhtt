package gui.controller;

import bus.NhanVienBUS;
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

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblError;
    @FXML private Button btnLogin;

    private final NhanVienBUS nhanVienBUS = new NhanVienBUS();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (lblError != null) lblError.setText("");
    }

    @FXML
    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu.");
            lblError.setStyle("-fx-text-fill: red;");
            return;
        }

        try {
            NhanVien nv = nhanVienBUS.dangNhap(username, password);
            if (nv != null) {
                SessionManager.setCurrentUser(nv);
                chuyenHuongMain();
            } else {
                lblError.setText("Tên đăng nhập hoặc mật khẩu không đúng.");
                lblError.setStyle("-fx-text-fill: red;");
            }
        } catch (Exception e) {
            lblError.setText(e.getMessage());
            lblError.setStyle("-fx-text-fill: red;");
        }
    }

    private void chuyenHuongMain() throws Exception {
        Stage currentStage = (Stage) btnLogin.getScene().getWindow();
        currentStage.close();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setTitle("Hệ Thống Quản Lý Tiệm Net");
        stage.setScene(new Scene(root, 1280, 800));
        stage.setMaximized(true);
        stage.show();
    }
}