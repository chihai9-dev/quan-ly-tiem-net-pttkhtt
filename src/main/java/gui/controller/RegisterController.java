package gui.controller;

import bus.KhachHangBUS;
import entity.KhachHang;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {

    @FXML private TextField txtHo;
    @FXML private TextField txtTen;
    @FXML private TextField txtSoDienThoai;
    @FXML private TextField txtTenDangNhap;
    @FXML private PasswordField txtMatKhau;
    @FXML private PasswordField txtXacNhanMk;
    @FXML private Label lblUserError;
    @FXML private Label lblPassError;
    @FXML private Label lblError;
    @FXML private Button btnDangKy;

    private final KhachHangBUS khachHangBUS = new KhachHangBUS();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Real-time username validation
        txtTenDangNhap.textProperty().addListener((obs, o, n) -> {
            if (n.length() > 0 && n.length() < 6) {
                lblUserError.setText("Tên đăng nhập phải có ít nhất 6 ký tự");
            } else {
                lblUserError.setText("");
            }
        });

        // Real-time password match validation
        txtXacNhanMk.textProperty().addListener((obs, o, n) -> validatePasswords());
        txtMatKhau.textProperty().addListener((obs, o, n) -> validatePasswords());
    }

    private void validatePasswords() {
        String mk = txtMatKhau.getText();
        String xacNhan = txtXacNhanMk.getText();
        if (!xacNhan.isEmpty() && !mk.equals(xacNhan)) {
            lblPassError.setText("Mật khẩu xác nhận không khớp");
        } else {
            lblPassError.setText("");
        }
    }

    @FXML
    public void handleDangKy() {
        clearErrors();

        // Validate
        if (txtHo.getText().trim().isEmpty() || txtTen.getText().trim().isEmpty()) {
            showError("Vui lòng nhập đầy đủ họ và tên");
            return;
        }
        if (txtSoDienThoai.getText().trim().isEmpty()) {
            showError("Vui lòng nhập số điện thoại");
            return;
        }
        if (txtTenDangNhap.getText().trim().length() < 6) {
            showError("Tên đăng nhập phải có ít nhất 6 ký tự");
            return;
        }
        if (txtMatKhau.getText().length() < 6) {
            showError("Mật khẩu phải có ít nhất 6 ký tự");
            return;
        }
        if (!txtMatKhau.getText().equals(txtXacNhanMk.getText())) {
            showError("Mật khẩu xác nhận không khớp");
            return;
        }

        KhachHang kh = new KhachHang();
        kh.setHo(txtHo.getText().trim());
        kh.setTen(txtTen.getText().trim());
        kh.setSodienthoai(txtSoDienThoai.getText().trim());
        kh.setTendangnhap(txtTenDangNhap.getText().trim());
        kh.setMatkhau(txtMatKhau.getText());

        btnDangKy.setDisable(true);
        try {
            khachHangBUS.dangKy(kh);
            showSuccessAndGoBack();
        } catch (Exception e) {
            showError(e.getMessage());
        } finally {
            btnDangKy.setDisable(false);
        }
    }

    @FXML
    public void handleBackToLogin() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Stage stage = (Stage) btnDangKy.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 700));
        } catch (Exception e) {
            showError("Lỗi: " + e.getMessage());
        }
    }

    private void showSuccessAndGoBack() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Đăng ký thành công");
        alert.setHeaderText(null);
        alert.setContentText("Tài khoản đã được tạo thành công! Vui lòng đăng nhập.");
        alert.showAndWait();
        handleBackToLogin();
    }

    private void showError(String msg) {
        lblError.setText(msg);
    }

    private void clearErrors() {
        lblError.setText("");
        lblUserError.setText("");
        lblPassError.setText("");
    }
}
