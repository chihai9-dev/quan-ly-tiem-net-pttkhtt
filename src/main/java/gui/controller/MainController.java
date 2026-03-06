package gui.controller;

import entity.NhanVien;
import entity.KhachHang;
import untils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
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

    @FXML private Label lblUserName;
    @FXML private Label lblUserRole;
    @FXML private Label lblPageTitle;
    @FXML private Label lblCurrentUser;
    @FXML private Label lblCurrentTime;
    @FXML private StackPane contentPane;

    // Khai báo các nút Sidebar
    @FXML private Button btnSodoMay;
    @FXML private Button btnMayTinh;
    @FXML private Button btnKhuMay;
    @FXML private Button btnPhienSuDung;
    @FXML private Button btnDichVu;
    @FXML private Button btnGoiDichVu;
    @FXML private Button btnKhuyenMai;
    @FXML private Button btnKhachHang;
    @FXML private Button btnNapTien;
    @FXML private Button btnNhanVien;
    @FXML private Button btnHoaDon;
    @FXML private Button btnNhapHang;
    @FXML private Button btnThongKe;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        updateHeader();
        setupMenuByRole();
        showSodoMay(); // Load trang mặc định
    }

    private void updateHeader() {
        // Sử dụng các hàm kiểm tra trạng thái có sẵn trong SessionManager
        if (SessionManager.isNhanVien() || SessionManager.isQuanLy()) {
            NhanVien nv = SessionManager.getCurrentNhanVien();
            if (nv != null) {
                lblUserName.setText(nv.getHo() + " " + nv.getTen());
                lblUserRole.setText(nv.getChucvu());
                lblCurrentUser.setText("Mã NV: " + nv.getManv());
            }
        } else if (SessionManager.isKhachHang()) {
            KhachHang kh = SessionManager.getCurrentKhachHang();
            if (kh != null) {
                lblUserName.setText(kh.getHo() + " " + kh.getTen());
                lblUserRole.setText("KHÁCH HÀNG");
                lblCurrentUser.setText("Số dư: " + String.format("%,.0f đ", kh.getSodu()));
            }
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        lblCurrentTime.setText(dtf.format(LocalDateTime.now()));
    }

    private void setupMenuByRole() {
        // Tắt hết các nút quản lý cấp cao nếu không phải QUANLY
        if (!SessionManager.isQuanLy()) {
            btnNhanVien.setVisible(false);
            btnNhanVien.setManaged(false);
            btnThongKe.setVisible(false);
            btnThongKe.setManaged(false);
            btnNhapHang.setVisible(false);
            btnNhapHang.setManaged(false);
            btnKhuyenMai.setVisible(false);
            btnKhuyenMai.setManaged(false);
        }

        // Nếu là Khách Hàng (Tự đăng nhập máy trạm) thì tắt các nút nghiệp vụ
        if (SessionManager.isKhachHang()) {
            btnMayTinh.setVisible(false); btnMayTinh.setManaged(false);
            btnKhuMay.setVisible(false); btnKhuMay.setManaged(false);
            btnKhachHang.setVisible(false); btnKhachHang.setManaged(false);
        }
    }

    private void loadView(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/view/" + fxmlPath));
            Node node = loader.load();
            contentPane.getChildren().setAll(node);
            lblPageTitle.setText(title.toUpperCase());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML public void showSodoMay() { loadView("sodo-may-view.fxml", "Sơ đồ máy"); }
    @FXML public void showMayTinh() { loadView("maytinh-list-view.fxml", "Quản lý máy tính"); }
    @FXML public void showKhuMay() { loadView("khumay-view.fxml", "Quản lý khu máy"); }
    @FXML public void showPhienSuDung() { loadView("phiensudung-view.fxml", "Quản lý phiên sử dụng"); }
    @FXML public void showDichVu() { loadView("dichvu-list-view.fxml", "Quản lý dịch vụ"); }
    @FXML public void showGoiDichVu() { loadView("goidichvu-list-view.fxml", "Quản lý gói dịch vụ"); }
    @FXML public void showKhuyenMai() { loadView("khuyenmai-view.fxml", "Chương trình khuyến mãi"); }
    @FXML public void showKhachHang() { loadView("khachhang-view.fxml", "Quản lý khách hàng"); }
    @FXML public void showNapTien() { loadView("naptien-view.fxml", "Nạp tiền"); }
    @FXML public void showNhanVien() { loadView("nhanvien-view.fxml", "Quản lý nhân viên"); }
    @FXML public void showHoaDon() { loadView("hoadon-view.fxml", "Quản lý hóa đơn"); }
    @FXML public void showNhapHang() { loadView("nhaphang-view.fxml", "Nhập hàng"); }
    @FXML public void showThongKe() { loadView("thongke-view.fxml", "Báo cáo thống kê"); }

    @FXML
    private void handleLogout() {
        SessionManager.clearSession();
        try {
            Stage currentStage = (Stage) btnSodoMay.getScene().getWindow();
            currentStage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/view/login-view.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Đăng nhập");
            stage.setScene(new Scene(loader.load()));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}