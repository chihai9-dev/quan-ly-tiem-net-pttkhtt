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

    @FXML private Label        lblTitle;
    @FXML private TextField    txtHo;
    @FXML private TextField    txtTen;
    @FXML private TextField    txtSDT;
    @FXML private TextField    txtTenDangNhap;
    @FXML private PasswordField txtMatKhau;
    @FXML private ComboBox<String> cboChucVu;
    @FXML private ComboBox<String> cboTrangThai;
    @FXML private Label        lblError;
    @FXML private Button       btnSave;
    @FXML private Button       btnCancel;

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
        if (cboTrangThai != null) {
            cboTrangThai.getItems().setAll("DANGLAMVIEC", "NGHIVIEC");
            cboTrangThai.setValue("DANGLAMVIEC");
        }
    }

    public void setEntity(NhanVien nv) {
        this.entity     = nv;
        this.isEditMode = (nv != null);
        if (isEditMode) {
            if (lblTitle  != null) lblTitle.setText("Sửa Nhân Viên");
            if (txtHo     != null) txtHo.setText(nv.getHo() != null ? nv.getHo() : "");
            if (txtTen    != null) txtTen.setText(nv.getTen() != null ? nv.getTen() : "");
            if (txtTenDangNhap != null) {
                txtTenDangNhap.setText(nv.getTendangnhap() != null ? nv.getTendangnhap() : "");
                txtTenDangNhap.setDisable(true);
            }
            if (txtMatKhau != null)
                txtMatKhau.setPromptText("Để trống nếu không đổi mật khẩu");
            if (cboChucVu   != null) cboChucVu.setValue(nv.getChucvu());
            if (cboTrangThai != null) cboTrangThai.setValue(nv.getTrangthai());
        } else {
            if (lblTitle != null) lblTitle.setText("Thêm Nhân Viên");
        }
    }

    public void setOnSaveCallback(Runnable cb) { this.onSaveCallback = cb; }

    @FXML
    public void handleSave() {
        clearError();
        String ho  = txtHo  != null ? txtHo.getText().trim()  : "";
        String ten = txtTen != null ? txtTen.getText().trim() : "";
        if (ho.isEmpty() || ten.isEmpty()) { setError("Họ và tên không được để trống"); return; }

        String tenDN = txtTenDangNhap != null ? txtTenDangNhap.getText().trim() : "";
        if (!isEditMode) {
            if (tenDN.length() < 4) { setError("Tên đăng nhập phải có ít nhất 4 ký tự"); return; }
        }

        String matKhau = txtMatKhau != null ? txtMatKhau.getText() : "";
        if (!isEditMode && matKhau.length() < 6) {
            setError("Mật khẩu phải có ít nhất 6 ký tự"); return;
        }
        if (isEditMode && !matKhau.isEmpty() && matKhau.length() < 6) {
            setError("Mật khẩu mới phải có ít nhất 6 ký tự"); return;
        }

        NhanVien nv = isEditMode ? entity : new NhanVien();
        nv.setHo(ho);
        nv.setTen(ten);
        if (!isEditMode) nv.setTendangnhap(tenDN);
        if (!isEditMode || !matKhau.isEmpty()) nv.setMatkhau(matKhau);
        nv.setChucvu(cboChucVu   != null ? cboChucVu.getValue()    : "NHANVIEN");
        nv.setTrangthai(cboTrangThai != null ? cboTrangThai.getValue() : "DANGLAMVIEC");
        if (txtSDT != null) {
            // NhanVien có thể có field sodienthoai - set nếu field tồn tại
            try { nv.setSodienthoai(txtSDT.getText().trim()); } catch (Exception ignored) {}
        }

        try {
            if (isEditMode) {
                nhanVienBUS.suaNhanVien(nv);
                ThongBaoDialog.showSuccess(getStage(), "Cập nhật nhân viên thành công!");
            } else {
                nhanVienBUS.themNhanVien(nv);
                ThongBaoDialog.showSuccess(getStage(), "Thêm nhân viên thành công!");
            }
            if (onSaveCallback != null) onSaveCallback.run();
            closeDialog();
        } catch (Exception e) {
            setError(e.getMessage());
        }
    }

    @FXML public void handleCancel() { closeDialog(); }

    private void setError(String msg)  { if (lblError != null) lblError.setText(msg); }
    private void clearError()          { if (lblError != null) lblError.setText(""); }
    private void closeDialog()         { if (btnCancel != null && btnCancel.getScene() != null)
                                            ((Stage) btnCancel.getScene().getWindow()).close(); }
    private Stage getStage()           { return btnSave != null && btnSave.getScene() != null
                                            ? (Stage) btnSave.getScene().getWindow() : null; }
}
