package gui.dialog;

import bus.KhuyenMaiBUS;
import entity.ChuongTrinhKhuyenMai;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ResourceBundle;

public class ThemKhuyenMaiDialog implements Initializable {

    @FXML private Label    lblTitle;
    @FXML private TextField  txtMaKM;
    @FXML private TextField  txtTenKM;
    @FXML private ComboBox<String> cboLoai;
    @FXML private TextField  txtGiaTri;
    @FXML private TextField  txtNapToiThieu;
    @FXML private DatePicker datTuNgay;
    @FXML private DatePicker datDenNgay;
    @FXML private TextArea   txtMoTa;
    @FXML private ComboBox<String> cboTrangThai;
    @FXML private Label      lblGiaTriUnit;
    @FXML private Label      lblError;
    @FXML private Button     btnSave;
    @FXML private Button     btnCancel;

    private final KhuyenMaiBUS khuyenMaiBUS = new KhuyenMaiBUS();
    private ChuongTrinhKhuyenMai entity;
    private boolean isEditMode = false;
    private Runnable onSaveCallback;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (cboLoai != null) {
            cboLoai.getItems().setAll("PHANTRAM", "SOTIEN", "TANGGIO");
            cboLoai.setValue("PHANTRAM");
            cboLoai.setOnAction(e -> updateUnit());
        }
        if (cboTrangThai != null) {
            cboTrangThai.getItems().setAll("HOATDONG", "CHUABD", "HETHAN");
            cboTrangThai.setValue("HOATDONG");
        }
        if (datTuNgay  != null) datTuNgay.setValue(LocalDate.now());
        if (datDenNgay != null) datDenNgay.setValue(LocalDate.now().plusMonths(1));
        updateUnit();
    }

    private void updateUnit() {
        if (cboLoai == null || lblGiaTriUnit == null) return;
        lblGiaTriUnit.setText(switch (cboLoai.getValue() != null ? cboLoai.getValue() : "") {
            case "PHANTRAM" -> "%";
            case "SOTIEN"   -> "₫";
            case "TANGGIO"  -> "giờ";
            default -> "";
        });
    }

    public void setEntity(ChuongTrinhKhuyenMai km) {
        this.entity     = km;
        this.isEditMode = (km != null);
        if (isEditMode) {
            if (lblTitle    != null) lblTitle.setText("Sửa Khuyến Mãi");
            if (txtMaKM     != null) { txtMaKM.setText(km.getMaCTKM()); txtMaKM.setDisable(true); }
            if (txtTenKM    != null) txtTenKM.setText(km.getTenCT());
            if (cboLoai     != null) cboLoai.setValue(km.getLoaiKM());
            if (txtGiaTri   != null) txtGiaTri.setText(String.valueOf(km.getGiaTriKM()));
            if (txtNapToiThieu != null) txtNapToiThieu.setText(String.valueOf(km.getDieuKienToiThieu()));
            if (datTuNgay != null) {
                LocalDateTime ngayBD = km.getNgayBatDau();
                datTuNgay.setValue(ngayBD != null ? ngayBD.toLocalDate() : LocalDate.now());
            }
            if (datDenNgay != null) {
                LocalDateTime ngayKT = km.getNgayKetThuc();
                datDenNgay.setValue(ngayKT != null ? ngayKT.toLocalDate() : LocalDate.now().plusMonths(1));
            }
            if (cboTrangThai != null) cboTrangThai.setValue(km.getTrangThai());
            if (txtMoTa      != null) txtMoTa.setText(""); // Nếu entity có getMoTa() thì set ở đây
            updateUnit();
        } else {
            if (lblTitle != null) lblTitle.setText("Thêm Khuyến Mãi");
        }
    }

    public void setOnSaveCallback(Runnable cb) { this.onSaveCallback = cb; }

    @FXML
    public void handleSave() {
        clearError();
        String tenKM = txtTenKM != null ? txtTenKM.getText().trim() : "";
        if (tenKM.isEmpty()) { setError("Tên khuyến mãi không được để trống"); return; }
        double giaTri;
        try {
            String rawGT = txtGiaTri != null ? txtGiaTri.getText().replace(",", "").trim() : "0";
            giaTri = Double.parseDouble(rawGT);
            if (giaTri <= 0) { setError("Giá trị KM phải > 0"); return; }
        } catch (NumberFormatException e) { setError("Giá trị không hợp lệ"); return; }
        double napMin = 0;
        try {
            String rawMin = txtNapToiThieu != null ? txtNapToiThieu.getText().replace(",", "").trim() : "0";
            napMin = Double.parseDouble(rawMin.isEmpty() ? "0" : rawMin);
        } catch (NumberFormatException ignored) {}

        LocalDate tuNgayDate  = datTuNgay  != null ? datTuNgay.getValue()  : LocalDate.now();
        LocalDate denNgayDate = datDenNgay != null ? datDenNgay.getValue() : LocalDate.now().plusMonths(1);
        if (tuNgayDate  == null) tuNgayDate  = LocalDate.now();
        if (denNgayDate == null) denNgayDate = tuNgayDate.plusMonths(1);
        if (!denNgayDate.isAfter(tuNgayDate)) {
            setError("Ngày kết thúc phải sau ngày bắt đầu");
            return;
        }

        LocalDateTime tuNgayDT  = tuNgayDate.atStartOfDay();
        LocalDateTime denNgayDT = denNgayDate.atTime(LocalTime.of(23, 59, 59));

        ChuongTrinhKhuyenMai km = isEditMode ? entity : new ChuongTrinhKhuyenMai();
        if (!isEditMode && txtMaKM != null) {
            String maKM = txtMaKM.getText().trim();
            if (!maKM.isEmpty()) km.setMaCTKM(maKM);
        }
        km.setTenCT(tenKM);
        km.setLoaiKM(cboLoai != null ? cboLoai.getValue() : "PHANTRAM");
        km.setGiaTriKM(giaTri);
        km.setDieuKienToiThieu(napMin);
        km.setNgayBatDau(tuNgayDT);   // ✅ LocalDateTime
        km.setNgayKetThuc(denNgayDT); // ✅ LocalDateTime
        km.setTrangThai(cboTrangThai != null ? cboTrangThai.getValue() : "HOATDONG");

        try {
            if (isEditMode) {
                khuyenMaiBUS.suaKhuyenMai(km);
                ThongBaoDialog.showSuccess(getStage(), "Cập nhật khuyến mãi thành công!");
            } else {
                khuyenMaiBUS.themKhuyenMai(km);
                ThongBaoDialog.showSuccess(getStage(), "Thêm khuyến mãi thành công!");
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
    private void closeDialog() {
        if (btnCancel != null && btnCancel.getScene() != null)
            ((Stage) btnCancel.getScene().getWindow()).close();
    }
    private Stage getStage() {
        return btnSave != null && btnSave.getScene() != null
                ? (Stage) btnSave.getScene().getWindow() : null;
    }
}