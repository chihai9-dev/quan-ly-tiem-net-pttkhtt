package gui.dialog;

import bus.DichVuBUS;
import entity.DichVu;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ThemDichVuDialog implements Initializable {

    @FXML private Label    lblTitle;
    @FXML private TextField txtMaDV;
    @FXML private TextField txtTenDV;
    @FXML private TextField txtGia;
    @FXML private TextField txtDonVi;
    @FXML private TextField txtTonKho;
    @FXML private ComboBox<String> cboTrangThai;
    @FXML private TextArea  txtMoTa;
    @FXML private Label     lblError;
    @FXML private Button    btnSave;
    @FXML private Button    btnCancel;

    private final DichVuBUS dichVuBUS = new DichVuBUS();
    private DichVu entity;
    private boolean isEditMode = false;
    private Runnable onSaveCallback;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (cboTrangThai != null) {
            cboTrangThai.getItems().setAll("DANGBAN", "NGUNGBAN");
            cboTrangThai.setValue("DANGBAN");
        }
    }

    public void setEntity(DichVu dv) {
        this.entity = dv;
        this.isEditMode = (dv != null);
        if (isEditMode) {
            if (lblTitle    != null) lblTitle.setText("Sửa Dịch Vụ");
            if (txtMaDV     != null) { txtMaDV.setText(dv.getMaDV()); txtMaDV.setDisable(true); }
            if (txtTenDV    != null) txtTenDV.setText(dv.getTenDV());
            if (txtGia      != null) txtGia.setText(String.valueOf(dv.getDonGia()));
            if (txtDonVi    != null) txtDonVi.setText(dv.getDonViTinh());
            if (txtTonKho   != null) txtTonKho.setText(String.valueOf(dv.getSoLuongTon()));
            if (cboTrangThai != null) cboTrangThai.setValue(dv.getTrangThai());
        } else {
            if (lblTitle != null) lblTitle.setText("Thêm Dịch Vụ");
        }
    }

    public void setOnSaveCallback(Runnable cb) { this.onSaveCallback = cb; }

    @FXML
    public void handleSave() {
        clearError();
        // Validate
        String tenDV = txtTenDV != null ? txtTenDV.getText().trim() : "";
        if (tenDV.isEmpty()) { setError("Tên dịch vụ không được để trống"); return; }
        double gia;
        try {
            gia = Double.parseDouble(txtGia != null ? txtGia.getText().replace(",","").trim() : "0");
            if (gia < 0) { setError("Giá phải >= 0"); return; }
        } catch (NumberFormatException e) { setError("Giá không hợp lệ"); return; }
        int tonKho;
        try {
            tonKho = Integer.parseInt(txtTonKho != null ? txtTonKho.getText().trim() : "0");
            if (tonKho < 0) { setError("Tồn kho phải >= 0"); return; }
        } catch (NumberFormatException e) { setError("Tồn kho không hợp lệ"); return; }

        DichVu dv = isEditMode ? entity : new DichVu();
        if (!isEditMode && txtMaDV != null) dv.setMaDV(txtMaDV.getText().trim());
        dv.setTenDV(tenDV);
        dv.setDonGia(gia);
        dv.setDonViTinh(txtDonVi != null ? txtDonVi.getText().trim() : "");
        dv.setSoLuongTon(tonKho);
        dv.setTrangThai(cboTrangThai != null ? cboTrangThai.getValue() : "DANGBAN");

        try {
            if (isEditMode) {
                dichVuBUS.suaDichVu(dv);
                ThongBaoDialog.showSuccess(getStage(), "Cập nhật dịch vụ thành công!");
            } else {
                dichVuBUS.themDichVu(dv);
                ThongBaoDialog.showSuccess(getStage(), "Thêm dịch vụ thành công!");
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
