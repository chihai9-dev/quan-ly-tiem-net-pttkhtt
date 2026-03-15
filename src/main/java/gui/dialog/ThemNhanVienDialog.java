package gui.dialog;

import bus.NhanVienBUS;
import entity.NhanVien;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ThemNhanVienDialog implements Initializable {

    // --- CÁC ID KHỚP 100% VỚI FILE FXML CỦA BẠN ---
    @FXML private Label lblDialogTitle;
    @FXML private TextField txtHo;
    @FXML private TextField txtTen;
    @FXML private TextField txtSDT;
    @FXML private TextField txtTenDangNhap;
    @FXML private PasswordField txtMatKhau;
    @FXML private ComboBox<String> cboChucVu;
    @FXML private DatePicker datNgayVaoLam;
    @FXML private Label lblError;
    @FXML private Button btnSave;
    @FXML private Button btnCancel;

    private final NhanVienBUS nhanVienBUS = new NhanVienBUS();
    private NhanVien entity;
    private boolean isEditMode = false;
    private Runnable onSaveCallback;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (cboChucVu != null) {
            cboChucVu.getItems().setAll("NHANVIEN", "QUANLY", "THUNGAN");
            cboChucVu.setValue("NHANVIEN");
        }
    }

    public void setEntity(NhanVien nv) {
        this.entity = nv;
        this.isEditMode = (nv != null);
        if (isEditMode) {
            if (lblDialogTitle != null) lblDialogTitle.setText("SỬA THÔNG TIN NHÂN VIÊN");
            if (txtHo != null) txtHo.setText(nv.getHo() != null ? nv.getHo() : "");
            if (txtTen != null) txtTen.setText(nv.getTen() != null ? nv.getTen() : "");
            if (txtTenDangNhap != null) {
                txtTenDangNhap.setText(nv.getTendangnhap() != null ? nv.getTendangnhap() : "");
                txtTenDangNhap.setDisable(true); // Không cho phép đổi tên đăng nhập
            }
            if (txtMatKhau != null) txtMatKhau.setPromptText("Bỏ trống nếu không đổi");
            if (cboChucVu != null) cboChucVu.setValue(nv.getChucvu());
        } else {
            if (lblDialogTitle != null) lblDialogTitle.setText("THÊM NHÂN VIÊN MỚI");
        }
    }

    public void setOnSaveCallback(Runnable cb) { this.onSaveCallback = cb; }

    @FXML
    public void handleSave() {
        clearError();
        String ho = txtHo != null ? txtHo.getText().trim() : "";
        String ten = txtTen != null ? txtTen.getText().trim() : "";
        if (ho.isEmpty() || ten.isEmpty()) { setError("Họ và tên không được để trống!"); return; }

        String tenDN = txtTenDangNhap != null ? txtTenDangNhap.getText().trim() : "";
        if (!isEditMode) {
            if (tenDN.length() < 4) { setError("Tên đăng nhập tối thiểu 4 ký tự!"); return; }
        }

        String matKhau = txtMatKhau != null ? txtMatKhau.getText() : "";
        if (!isEditMode && matKhau.length() < 6) {
            setError("Mật khẩu tối thiểu 6 ký tự!"); return;
        }
        if (isEditMode && !matKhau.isEmpty() && matKhau.length() < 6) {
            setError("Mật khẩu mới tối thiểu 6 ký tự!"); return;
        }

        NhanVien nv = isEditMode ? entity : new NhanVien();
        nv.setHo(ho);
        nv.setTen(ten);
        if (!isEditMode) nv.setTendangnhap(tenDN);
        if (!isEditMode || !matKhau.isEmpty()) nv.setMatkhau(matKhau);
        nv.setChucvu(cboChucVu != null ? cboChucVu.getValue() : "NHANVIEN");

        // Mặc định nhân viên mới thêm là ĐANG LÀM VIỆC
        if (!isEditMode) nv.setTrangthai("DANGLAMVIEC");

        try {
            if (isEditMode) {
                nhanVienBUS.suaNhanVien(nv);
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Cập nhật nhân viên thành công!");
            } else {
                nhanVienBUS.themNhanVien(nv);
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Thêm nhân viên thành công!");
            }
            if (onSaveCallback != null) onSaveCallback.run();
            closeDialog();
        } catch (Exception e) {
            setError(e.getMessage());
        }
    }

    @FXML public void handleCancel() { closeDialog(); }

    private void setError(String msg) { if (lblError != null) lblError.setText(msg); }
    private void clearError() { if (lblError != null) lblError.setText(""); }
    private void closeDialog() {
        if (btnCancel != null && btnCancel.getScene() != null)
            ((Stage) btnCancel.getScene().getWindow()).close();
    }

    // Dùng Alert gốc của JavaFX để cực kỳ ổn định, không lo lỗi Location is not set
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}