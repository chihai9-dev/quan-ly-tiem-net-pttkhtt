package gui.controller;

import entity.NhanVien;
import entity.KhachHang;
import utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML private Label lblTenNguoiDung; // Đổi từ lblUserName
    @FXML private Label lblChucVu;       // Đổi từ lblUserRole
    @FXML private Label lblPageTitle;
    @FXML private Label lblCurrentTime;
    @FXML private StackPane contentArea; // Đổi từ contentPane

    @FXML private Button btnSodoMay, btnMayTinh, btnKhuMay, btnPhienSuDung, btnDichVu;
    @FXML private Button btnGoiDichVu, btnKhuyenMai, btnKhachHang, btnNapTien, btnNhanVien;
    @FXML private Button btnHoaDon, btnNhapHang, btnThongKe;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        updateHeader();
        setupMenuByRole();
        showSodoMay();
    }

    private void updateHeader() {
        if (SessionManager.isNhanVien() || SessionManager.isQuanLy()) {
            NhanVien nv = SessionManager.getCurrentNhanVien();
            if (nv != null) {
                lblTenNguoiDung.setText(nv.getHo() + " " + nv.getTen());
                lblChucVu.setText(nv.getChucvu());
            }
        } else if (SessionManager.isKhachHang()) {
            KhachHang kh = SessionManager.getCurrentKhachHang();
            if (kh != null) {
                lblTenNguoiDung.setText(kh.getHo() + " " + kh.getTen());
                lblChucVu.setText("KHACHHANG");
            }
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        if(lblCurrentTime != null) lblCurrentTime.setText(dtf.format(LocalDateTime.now()));
    }

    private void setupMenuByRole() {
        if (!SessionManager.isQuanLy()) {
            if(btnNhanVien != null) { btnNhanVien.setVisible(false); btnNhanVien.setManaged(false); }
            if(btnThongKe != null) { btnThongKe.setVisible(false); btnThongKe.setManaged(false); }
            if(btnNhapHang != null) { btnNhapHang.setVisible(false); btnNhapHang.setManaged(false); }
            if(btnKhuyenMai != null) { btnKhuyenMai.setVisible(false); btnKhuyenMai.setManaged(false); }
        }

        if (SessionManager.isKhachHang()) {
            if(btnMayTinh != null) { btnMayTinh.setVisible(false); btnMayTinh.setManaged(false); }
            if(btnKhuMay != null) { btnKhuMay.setVisible(false); btnKhuMay.setManaged(false); }
            if(btnKhachHang != null) { btnKhachHang.setVisible(false); btnKhachHang.setManaged(false); }
        }
    }

    private void loadView(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/view/" + fxmlPath));
            Node node = loader.load();
            contentArea.getChildren().setAll(node);
            if(lblPageTitle != null) lblPageTitle.setText(title.toUpperCase());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load đúng tên file fxml trong docx
    @FXML public void showSodoMay() { loadView("sodoMay.fxml", "Sơ đồ máy"); }
    @FXML public void showMayTinh() { loadView("mayTinh.fxml", "Quản lý máy tính"); }
    @FXML public void showKhuMay() { loadView("khuMay.fxml", "Quản lý khu máy"); }
    @FXML public void showPhienSuDung() { loadView("phienSuDung.fxml", "Quản lý phiên sử dụng"); }
    @FXML public void showDichVu() { loadView("dichVu.fxml", "Quản lý dịch vụ"); }
    @FXML public void showGoiDichVu() { loadView("goiDichVu.fxml", "Quản lý gói dịch vụ"); }
    @FXML public void showKhuyenMai() { loadView("khuyenMai.fxml", "Chương trình khuyến mãi"); }
    @FXML public void showKhachHang() { loadView("khachHang.fxml", "Quản lý khách hàng"); }
    @FXML public void showNapTien() { loadView("napTien.fxml", "Nạp tiền"); }
    @FXML public void showNhanVien() { loadView("nhanVien.fxml", "Quản lý nhân viên"); }
    @FXML public void showHoaDon() { loadView("hoaDon.fxml", "Quản lý hóa đơn"); }
    @FXML public void showNhapHang() { loadView("nhapHang.fxml", "Nhập hàng"); }
    @FXML public void showThongKe() { loadView("thongKe.fxml", "Báo cáo thống kê"); }

    @FXML
    private void handleLogout() {
        SessionManager.clearSession();
        try {
            Stage currentStage = (Stage) btnSodoMay.getScene().getWindow();
            currentStage.close();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/view/login.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Đăng nhập");
            stage.setScene(new Scene(loader.load()));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}