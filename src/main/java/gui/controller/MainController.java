package gui.controller;

import bus.PhienSuDungBUS;
import dao.*;
import entity.KhachHang;
import entity.NhanVien;
import entity.PhienSuDung;
import utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

/**
 * ============================================================
 *  MainController  –  Màn hình chính sau khi đăng nhập
 * ============================================================
 *
 *  Thêm mới so với bản cũ:
 *    → initialize() gọi kiemTraPhienQuaHanSauDangNhap()
 *      để tự động kết thúc các phiên bị bỏ dở (app tắt đột ngột,
 *      khách hết tiền từ lần trước chưa được xử lý…)
 *
 *  Toàn bộ phần còn lại GIỮ NGUYÊN như bản cũ của bạn.
 */
public class MainController implements Initializable {

    // ================================================================
    //  PHẦN 1: FXML COMPONENTS  (giữ nguyên như cũ)
    // ================================================================

    @FXML private Label     lblAvatarChar;
    @FXML private Label     lblUserName;
    @FXML private Label     lblUserRole;
    @FXML private Label     lblPageTitle;
    @FXML private Label     lblCurrentTime;
    @FXML private StackPane contentPane;

    @FXML private Button btnSoDoMay, btnPhienSuDung, btnNapTien, btnHoaDon;
    @FXML private Button btnDichVu, btnGoiDichVu, btnKhuyenMai, btnNhapHang;
    @FXML private Button btnKhachHang, btnNhanVien, btnMayTinh, btnKhuMay, btnThongKe;

    // ================================================================
    //  PHẦN 2: BUS ĐỂ KIỂM TRA PHIÊN QUÁ HẠN  ← THÊM MỚI
    // ================================================================

    /**
     * BUS chỉ dùng 1 lần khi khởi động (kiểm tra phiên quá hạn).
     * Sau đó PhienSuDungController sẽ có instance riêng của nó.
     */
    private final PhienSuDungBUS phienBUS = new PhienSuDungBUS(
            new PhienSuDungDAO(), new MayTinhDAO(), new KhachHangDAO(),
            new GoiDichVuKhachHangDAO(), new SuDungDichVuDAO(), new HoaDonDAO());

    // ================================================================
    //  PHẦN 3: KHỞI TẠO  (thêm gọi kiểm tra phiên, giữ nguyên phần còn lại)
    // ================================================================

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            updateHeader();
            setupMenuByRole();
            loadSoDoMay(); // trang mặc định

            // ✅ MỚI: Kiểm tra và kết thúc các phiên quá hạn ngay khi đăng nhập
            // Chạy sau khi UI đã sẵn sàng để không block màn hình
            kiemTraPhienQuaHanSauDangNhap();

        } catch (Exception e) {
            System.err.println("Lỗi khởi tạo màn hình Main: " + e.getMessage());
        }
    }

    /**
     * Kiểm tra và kết thúc phiên quá hạn ngay khi đăng nhập.
     *
     * Trường hợp cần xử lý:
     *   • App bị tắt đột ngột (mất điện, crash) → các phiên vẫn DANGCHOI trong DB
     *   • Khách đã hết tiền từ hôm trước nhưng chưa kết thúc phiên
     *
     * Không hiển thị thông báo popup ở đây để không làm phiền ngay lúc đăng nhập.
     * Chỉ ghi ra console để theo dõi.
     */
    private void kiemTraPhienQuaHanSauDangNhap() {
        try {
            List<PhienSuDung> danhSachDaKetThuc = phienBUS.kiemTraVaKetThucPhienQuaHan();

            if (!danhSachDaKetThuc.isEmpty()) {
                // Chỉ in ra console (không popup), nhân viên sẽ thấy kết quả khi vào màn hình Phiên
                System.out.printf("[Đăng nhập] Đã tự động kết thúc %d phiên quá hạn.%n",
                        danhSachDaKetThuc.size());
            } else {
                System.out.println("[Đăng nhập] Không có phiên nào quá hạn.");
            }

        } catch (Exception e) {
            // Không crash app nếu bước này lỗi
            System.err.println("[Đăng nhập] Lỗi kiểm tra phiên: " + e.getMessage());
        }
    }

    // ================================================================
    //  PHẦN 4: CÁC HÀM GIỮ NGUYÊN NHƯ BẢN CŨ
    // ================================================================

    private void updateHeader() {
        if (SessionManager.isNhanVien() || SessionManager.isQuanLy()) {
            NhanVien nv = SessionManager.getCurrentNhanVien();
            if (nv != null) {
                if (lblUserName != null) lblUserName.setText(nv.getHo() + " " + nv.getTen());
                if (lblUserRole != null) lblUserRole.setText(nv.getChucvu());
                if (lblAvatarChar != null && nv.getTen() != null && !nv.getTen().isEmpty())
                    lblAvatarChar.setText(String.valueOf(nv.getTen().charAt(0)).toUpperCase());
            }
        } else if (SessionManager.isKhachHang()) {
            KhachHang kh = SessionManager.getCurrentKhachHang();
            if (kh != null) {
                if (lblUserName != null) lblUserName.setText(kh.getHo() + " " + kh.getTen());
                if (lblUserRole != null) lblUserRole.setText("KHACHHANG");
                if (lblAvatarChar != null && kh.getTen() != null && !kh.getTen().isEmpty())
                    lblAvatarChar.setText(String.valueOf(kh.getTen().charAt(0)).toUpperCase());
            }
        }
        if (lblCurrentTime != null) {
            lblCurrentTime.setText(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").format(LocalDateTime.now()));
        }
    }

    private void setupMenuByRole() {
        if (!SessionManager.isQuanLy()) {
            hide(btnNhanVien); hide(btnThongKe);
            hide(btnNhapHang); hide(btnKhuyenMai);
        }
        if (SessionManager.isKhachHang()) {
            hide(btnMayTinh); hide(btnKhuMay); hide(btnKhachHang);
        }
    }

    /** Ẩn một nút và không chiếm layout */
    private void hide(Button btn) {
        if (btn != null) { btn.setVisible(false); btn.setManaged(false); }
    }

    private void loadView(String fxmlPath, String title) {
        try {
            if (contentPane == null) return;
            var fileUrl = getClass().getResource("/fxml/" + fxmlPath);
            if (fileUrl == null) {
                System.err.println("⚠️ Không tìm thấy file /fxml/" + fxmlPath);
                return;
            }
            FXMLLoader loader = new FXMLLoader(fileUrl);
            Node node = loader.load();
            contentPane.getChildren().setAll(node);
            if (lblPageTitle != null) lblPageTitle.setText(title.toUpperCase());
        } catch (Exception e) {
            System.err.println("⛔ Lỗi load " + fxmlPath + ": " + e.getMessage());
        }
    }

    // Menu actions (giữ nguyên như cũ)
    @FXML public void loadSoDoMay()     { loadView("sodoMay.fxml",      "Sơ đồ máy"); }
    @FXML public void loadPhienSuDung() { loadView("phienSuDung.fxml",  "Quản lý phiên sử dụng"); }
    @FXML public void loadNapTien()     { loadView("napTien.fxml",       "Nạp tiền"); }
    @FXML public void loadHoaDon()      { loadView("hoaDon.fxml",        "Quản lý hóa đơn"); }
    @FXML public void loadDichVu()      { loadView("dichVu.fxml",        "Quản lý dịch vụ"); }
    @FXML public void loadGoiDichVu()   { loadView("goiDichVu.fxml",     "Quản lý gói dịch vụ"); }
    @FXML public void loadKhuyenMai()   { loadView("khuyenMai.fxml",     "Chương trình khuyến mãi"); }
    @FXML public void loadNhapHang()    { loadView("nhapHang.fxml",      "Nhập hàng"); }
    @FXML public void loadKhachHang()   { loadView("khachHang.fxml",     "Quản lý khách hàng"); }
    @FXML public void loadNhanVien()    { loadView("nhanVien.fxml",      "Quản lý nhân viên"); }
    @FXML public void loadMayTinh()     { loadView("mayTinh.fxml",       "Quản lý máy tính"); }
    @FXML public void loadKhuMay()      { loadView("khuMay.fxml",        "Quản lý khu máy"); }
    @FXML public void loadThongKe()     { loadView("thongKe.fxml",       "Báo cáo thống kê"); }

    @FXML
    private void handleLogout() {
        SessionManager.clearSession();
        try {
            Stage currentStage = (Stage) btnSoDoMay.getScene().getWindow();
            currentStage.close();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Đăng nhập");
            stage.setScene(new Scene(loader.load()));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}