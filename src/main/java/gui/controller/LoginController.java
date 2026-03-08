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

    @FXML private TextField txtTenDangNhap; // Đổi từ txtUsername
    @FXML private PasswordField pfMatKhau;  // Đổi từ txtPassword
    @FXML private ComboBox<String> cbLoaiTaiKhoan;
    @FXML private Label lblThongBao;
    @FXML private Button btnDangNhap;

    private final KhachHangBUS khachHangBUS = new KhachHangBUS();
    private final NhanVienBUS nhanVienBUS = new NhanVienBUS();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbLoaiTaiKhoan.getItems().addAll("Nhân viên", "Khách hàng");
        cbLoaiTaiKhoan.getSelectionModel().selectFirst();
        lblThongBao.setText("");
    }

    @FXML
    private void handleDangNhap() {
        String username = txtTenDangNhap.getText();
        String password = pfMatKhau.getText();
        String role = cbLoaiTaiKhoan.getValue();

        try {
            if ("Khách hàng".equals(role)) {
                KhachHang kh = khachHangBUS.dangNhap(username, password);
                if (kh != null) chuyenHuongMain("Khách hàng");
            } else {
                NhanVien nv = nhanVienBUS.dangNhap(username, password);
                if (nv != null) chuyenHuongMain("Nhân viên");
            }
        } catch (Exception e) {
            lblThongBao.setText(e.getMessage());
            lblThongBao.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void handleDangKy() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/view/register.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Đăng ký tài khoản");
            stage.setScene(new Scene(loader.load()));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void chuyenHuongMain(String role) throws Exception {
        Stage currentStage = (Stage) btnDangNhap.getScene().getWindow();
        currentStage.close();

        // Load đúng tên file main.fxml theo docx
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/view/main.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Hệ Thống Quản Lý Tiệm Net - " + role);
        stage.setScene(new Scene(loader.load(), 1280, 800));
        stage.setMaximized(true);
        stage.show();
    }
}