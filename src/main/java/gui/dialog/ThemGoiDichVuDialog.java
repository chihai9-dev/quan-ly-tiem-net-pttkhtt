package gui.dialog;

import bus.GoiDichVuBUS;
import entity.GoiDichVu;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ThemGoiDichVuDialog implements Initializable {

    @FXML private Label    lblTitle;
    @FXML private TextField txtMaGoi;
    @FXML private TextField txtTenGoi;
    @FXML private TextField txtGia;
    @FXML private TextField txtSoGio;
    @FXML private TextField txtSoNgayHieuLuc;
    @FXML private TextArea  txtMoTa;
    @FXML private ComboBox<String> cboTrangThai;
    @FXML private ComboBox<String> cboLoaiGoi;
    @FXML private Label     lblError;
    @FXML private Button    btnSave;
    @FXML private Button    btnCancel;

    private final GoiDichVuBUS goiDichVuBUS = new GoiDichVuBUS();
    private GoiDichVu entity;
    private boolean isEditMode = false;
    private Runnable onSaveCallback;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (cboTrangThai != null) {
            cboTrangThai.getItems().setAll("HOATDONG", "NGUNG");
            cboTrangThai.setValue("HOATDONG");
        }
        if (cboLoaiGoi != null) {
            cboLoaiGoi.getItems().setAll("THUONG", "VIP", "SINH_VIEN", "CUOI_TUAN");
            cboLoaiGoi.setValue("THUONG");
        }
    }

    public void setEntity(GoiDichVu gdv) {
        this.entity     = gdv;
        this.isEditMode = (gdv != null);
        if (isEditMode) {
            if (lblTitle    != null) lblTitle.setText("Sửa Gói Dịch Vụ");
            if (txtMaGoi    != null) { txtMaGoi.setText(gdv.getMaGoi()); txtMaGoi.setDisable(true); }
            if (txtTenGoi   != null) txtTenGoi.setText(gdv.getTenGoi());
            if (txtGia      != null) txtGia.setText(String.valueOf(gdv.getGiaGoi()));
            if (txtSoGio    != null) txtSoGio.setText(String.valueOf(gdv.getSoGio()));
            if (txtSoNgayHieuLuc != null) txtSoNgayHieuLuc.setText(String.valueOf(gdv.getSoNgayHieuLuc()));
            if (cboLoaiGoi  != null) cboLoaiGoi.setValue(gdv.getLoaiGoi());
            if (cboTrangThai != null) cboTrangThai.setValue(gdv.getTrangThai());
        } else {
            if (lblTitle != null) lblTitle.setText("Thêm Gói Dịch Vụ");
        }
    }

    public void setOnSaveCallback(Runnable cb) { this.onSaveCallback = cb; }

    @FXML
    public void handleSave() {
        clearError();
        String tenGoi = txtTenGoi != null ? txtTenGoi.getText().trim() : "";
        if (tenGoi.isEmpty()) { setError("Tên gói không được để trống"); return; }
        double gia;
        try {
            gia = Double.parseDouble(txtGia != null ? txtGia.getText().replace(",","").trim() : "0");
            if (gia <= 0) { setError("Giá phải > 0"); return; }
        } catch (NumberFormatException e) { setError("Giá không hợp lệ"); return; }
        double soGio;
        try {
            soGio = Double.parseDouble(txtSoGio != null ? txtSoGio.getText().trim() : "0");
            if (soGio <= 0) { setError("Số giờ phải > 0"); return; }
        } catch (NumberFormatException e) { setError("Số giờ không hợp lệ"); return; }
        int soNgay;
        try {
            soNgay = Integer.parseInt(txtSoNgayHieuLuc != null ? txtSoNgayHieuLuc.getText().trim() : "30");
        } catch (NumberFormatException e) { soNgay = 30; }

        GoiDichVu gdv = isEditMode ? entity : new GoiDichVu();
        if (!isEditMode && txtMaGoi != null) gdv.setMaGoi(txtMaGoi.getText().trim());
        gdv.setTenGoi(tenGoi);
        gdv.setGiaGoi(gia);
        gdv.setSoGio(soGio);
        gdv.setSoNgayHieuLuc(soNgay);
        gdv.setLoaiGoi(cboLoaiGoi != null ? cboLoaiGoi.getValue() : "THUONG");
        gdv.setTrangThai(cboTrangThai != null ? cboTrangThai.getValue() : "HOATDONG");

        try {
            if (isEditMode) {
                goiDichVuBUS.suaGoiDichVu(gdv);
                ThongBaoDialog.showSuccess(getStage(), "Cập nhật gói dịch vụ thành công!");
            } else {
                goiDichVuBUS.themGoiDichVu(gdv);
                ThongBaoDialog.showSuccess(getStage(), "Thêm gói dịch vụ thành công!");
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
