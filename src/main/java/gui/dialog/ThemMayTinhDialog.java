package gui.dialog;

import bus.KhuMayBUS;
import bus.MayTinhBUS;
import entity.KhuMay;
import entity.MayTinh;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ThemMayTinhDialog implements Initializable {

    @FXML private Label    lblTitle;
    @FXML private TextField txtMaMay;
    @FXML private TextField txtTenMay;
    @FXML private TextField txtCauHinh;
    @FXML private ComboBox<KhuMay> cboKhuMay;
    @FXML private TextField txtGiaMoiGio;
    @FXML private ComboBox<String> cboTrangThai;
    @FXML private Label    lblError;
    @FXML private Button   btnSave;
    @FXML private Button   btnCancel;

    private final MayTinhBUS mayTinhBUS = new MayTinhBUS();
    private final KhuMayBUS  khuMayBUS  = new KhuMayBUS();
    private MayTinh entity;
    private boolean isEditMode = false;
    private Runnable onSaveCallback;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (cboTrangThai != null) {
            cboTrangThai.getItems().setAll("TRONG", "BAOTRI");
            cboTrangThai.setValue("TRONG");
        }
        loadKhuMay();
    }

    private void loadKhuMay() {
        if (cboKhuMay == null) return;
        cboKhuMay.setConverter(new StringConverter<>() {
            @Override public String toString(KhuMay km) {
                return km == null ? "" : km.getMakhu() + " - " + km.getTenkhu();
            }
            @Override public KhuMay fromString(String s) { return null; }
        });
        try {
            List<KhuMay> list = khuMayBUS.getAllKhuMay();
            cboKhuMay.getItems().setAll(list);
        } catch (Exception e) {
            if (lblError != null) lblError.setText("Lỗi tải khu máy");
        }
    }

    public void setEntity(MayTinh mt) {
        this.entity     = mt;
        this.isEditMode = (mt != null);
        if (isEditMode) {
            if (lblTitle  != null) lblTitle.setText("Sửa Máy Tính");
            if (txtMaMay  != null) { txtMaMay.setText(mt.getMamay()); txtMaMay.setDisable(true); }
            if (txtTenMay != null) txtTenMay.setText(mt.getTenmay());
            if (txtCauHinh != null) txtCauHinh.setText(mt.getCauhinh());
            if (txtGiaMoiGio != null) txtGiaMoiGio.setText(String.valueOf(mt.getGiamoigio()));
            if (cboTrangThai != null) cboTrangThai.setValue(mt.getTrangthai());
            // Select khu
            if (cboKhuMay != null && mt.getMakhu() != null) {
                cboKhuMay.getItems().stream()
                    .filter(k -> mt.getMakhu().equals(k.getMakhu()))
                    .findFirst()
                    .ifPresent(cboKhuMay::setValue);
            }
        } else {
            if (lblTitle != null) lblTitle.setText("Thêm Máy Tính");
        }
    }

    public void setOnSaveCallback(Runnable cb) { this.onSaveCallback = cb; }

    @FXML
    public void handleSave() {
        clearError();
        String maMay  = txtMaMay  != null ? txtMaMay.getText().trim()  : "";
        String tenMay = txtTenMay != null ? txtTenMay.getText().trim() : "";
        if (!isEditMode && maMay.isEmpty()) { setError("Mã máy không được để trống"); return; }
        if (tenMay.isEmpty()) { setError("Tên máy không được để trống"); return; }
        double gia;
        try {
            gia = Double.parseDouble(txtGiaMoiGio != null ? txtGiaMoiGio.getText().replace(",","").trim() : "0");
            if (gia < 0) { setError("Giá phải >= 0"); return; }
        } catch (NumberFormatException e) { setError("Giá không hợp lệ"); return; }

        KhuMay khu = cboKhuMay != null ? cboKhuMay.getValue() : null;
        String maKhu = khu != null ? khu.getMakhu() : "";

        MayTinh mt = isEditMode ? entity : new MayTinh();
        if (!isEditMode) mt.setMamay(maMay);
        mt.setTenmay(tenMay);
        mt.setCauhinh(txtCauHinh != null ? txtCauHinh.getText().trim() : "");
        mt.setMakhu(maKhu);
        mt.setGiamoigio(gia);
        mt.setTrangthai(cboTrangThai != null ? cboTrangThai.getValue() : "TRONG");

        try {
            if (isEditMode) {
                mayTinhBUS.suaMayTinh(mt);
                ThongBaoDialog.showSuccess(getStage(), "Cập nhật máy tính thành công!");
            } else {
                mayTinhBUS.themMayTinh(mt);
                ThongBaoDialog.showSuccess(getStage(), "Thêm máy tính thành công!");
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
