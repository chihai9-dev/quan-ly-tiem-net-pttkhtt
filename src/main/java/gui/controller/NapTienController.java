package gui.controller;

import bus.KhachHangBUS;
import bus.KhuyenMaiBUS;
import bus.NapTienBUS;
import entity.ChuongTrinhKhuyenMai;
import entity.KhachHang;
import entity.LichSuNapTien;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import utils.ThongBaoDialogHelper;
import utils.SessionManager;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import javafx.beans.property.SimpleStringProperty;
import java.util.List;
import java.util.ResourceBundle;

public class NapTienController implements Initializable {

    @FXML private TextField txtTimKH;
    @FXML private VBox vboxKHInfo;
    @FXML private Label lblKHAvatar;
    @FXML private Label lblKHTen;
    @FXML private Label lblKHSDT;
    @FXML private Label lblKHSoDu;
    @FXML private Label lblSoDuHienTai;

    @FXML private TextField txtSoTien;
    @FXML private ComboBox<ChuongTrinhKhuyenMai> cboCTKM;
    @FXML private ComboBox<ChuongTrinhKhuyenMai> cbKhuyenMai;
    @FXML private VBox vboxKM;
    @FXML private Label lblTienKM;
    @FXML private Label lblTienKhuyenMai;
    @FXML private Label lblSoTienFmt;
    @FXML private Label lblTienKMFmt;
    @FXML private Label lblTongCong;
    @FXML private Label lblTongTienCong;
    @FXML private Label lblTenKH;
    @FXML private Label lblError;
    @FXML private Button btnNapTien;

    @FXML private TableView<LichSuNapTien> tblLichSuNap;
    @FXML private TableView<LichSuNapTien> tableHistory;
    @FXML private TableColumn<LichSuNapTien, String> colNgayNap;
    @FXML private TableColumn<LichSuNapTien, String> colSoTienNap;
    @FXML private TableColumn<LichSuNapTien, String> colKhuyenMaiHist;
    @FXML private TableColumn<LichSuNapTien, String> colTongCongHist;
    @FXML private TableColumn<LichSuNapTien, String> colPhuongThuc;

    private final NapTienBUS    napTienBUS    = new NapTienBUS();
    private final KhachHangBUS  khachHangBUS  = new KhachHangBUS();
    private final KhuyenMaiBUS  khuyenMaiBUS  = new KhuyenMaiBUS();

    private KhachHang currentKH = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableHistory();
        loadCTKM();
        hideKHInfo();
        if (cboCTKM    != null) cboCTKM.setOnAction(e -> recalculate());
        if (cbKhuyenMai!= null) cbKhuyenMai.setOnAction(e -> recalculate());
        if (txtSoTien  != null) txtSoTien.textProperty().addListener((obs, o, n) -> recalculate());
        loadLichSuNap();
    }

    // ===== LOAD DATA =====
    private void loadCTKM() {
        ComboBox<ChuongTrinhKhuyenMai> combo = getActiveCombo();
        if (combo == null) return;
        try {
            List<ChuongTrinhKhuyenMai> list = khuyenMaiBUS.getKhuyenMaiConHieuLuc();
            combo.getItems().clear();
            combo.getItems().add(null); // Không áp dụng
            combo.getItems().addAll(list);
            combo.setCellFactory(lv -> new ListCell<>() {
                @Override protected void updateItem(ChuongTrinhKhuyenMai item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : (item == null ? "-- Không áp dụng --" : item.getTenCT() + " (" + item.getGiaTriKMFormatted() + ")"));
                }
            });
            combo.setButtonCell(new ListCell<>() {
                @Override protected void updateItem(ChuongTrinhKhuyenMai item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : (item == null ? "-- Không áp dụng --" : item.getTenCT()));
                }
            });
        } catch (Exception ignored) {}
    }

    private void setupTableHistory() {
        TableView<LichSuNapTien> table = getActiveTable();
        if (table == null) return;
        if (colNgayNap != null) colNgayNap.setCellValueFactory(c -> {
            var d = c.getValue().getNgayNap();
            return new SimpleStringProperty(d != null ? d.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "");
        });
        if (colSoTienNap != null) colSoTienNap.setCellValueFactory(c ->
                new SimpleStringProperty(String.format("%,.0f ₫", c.getValue().getSoTienNap())));
        if (colKhuyenMaiHist != null) colKhuyenMaiHist.setCellValueFactory(c ->
                new SimpleStringProperty(String.format("%,.0f ₫", c.getValue().getKhuyenMai())));
        if (colTongCongHist != null) colTongCongHist.setCellValueFactory(c ->
                new SimpleStringProperty(String.format("%,.0f ₫", c.getValue().getTongTienCong())));
        if (colPhuongThuc != null) colPhuongThuc.setCellValueFactory(
                new PropertyValueFactory<>("phuongThuc"));
    }

    // ===== TÌM KHÁCH HÀNG =====

    @FXML
    public void handleTimKH() {
        String keyword = txtTimKH != null ? txtTimKH.getText().trim() : "";
        if (keyword.isEmpty()) { showError("Vui lòng nhập mã KH hoặc tên đăng nhập"); return; }
        try {
            List<KhachHang> results = khachHangBUS.timKiemKhachHang(keyword);
            if (results == null || results.isEmpty()) {
                showError("Không tìm thấy khách hàng: " + keyword);
                hideKHInfo();
                return;
            }
            currentKH = results.get(0);
            showKHInfo(currentKH);
            clearError();
            loadLichSuNap();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void showKHInfo(KhachHang kh) {
        String hoTen = trim(kh.getHo()) + " " + trim(kh.getTen());
        hoTen = hoTen.trim();
        if (lblKHAvatar   != null) lblKHAvatar.setText(hoTen.isEmpty() ? "K" :
                String.valueOf(hoTen.charAt(0)).toUpperCase());
        if (lblKHTen      != null) lblKHTen.setText(hoTen.isEmpty() ? "(Không có tên)" : hoTen);
        if (lblTenKH      != null) lblTenKH.setText(hoTen.isEmpty() ? "(Không có tên)" : hoTen);
        if (lblKHSDT      != null) lblKHSDT.setText(kh.getSodienthoai() != null ? kh.getSodienthoai() : "-");
        String soDu = String.format("%,.0f ₫", kh.getSodu());
        if (lblKHSoDu     != null) lblKHSoDu.setText(soDu);
        if (lblSoDuHienTai!= null) lblSoDuHienTai.setText(soDu);
        if (vboxKHInfo    != null) { vboxKHInfo.setVisible(true); vboxKHInfo.setManaged(true); }
    }

    private void hideKHInfo() {
        currentKH = null;
        if (vboxKHInfo != null) { vboxKHInfo.setVisible(false); vboxKHInfo.setManaged(false); }
    }

    // ===== TÍNH TOÁN =====

    @FXML public void handleSoTienChanged() { recalculate(); }
    @FXML public void handleCTKMChanged()   { recalculate(); }

    private void recalculate() {
        try {
            String raw = txtSoTien != null ? txtSoTien.getText().replace(",", "").trim() : "";
            if (raw.isEmpty()) { resetCalc(); return; }
            double soTien = Double.parseDouble(raw);

            double tienKM = 0;
            ChuongTrinhKhuyenMai km = getSelectedCTKM();
            if (km != null) {
                tienKM = napTienBUS.tinhKhuyenMai(soTien, km.getMaCTKM());
                if (vboxKM != null) { vboxKM.setVisible(true); vboxKM.setManaged(true); }
            } else {
                if (vboxKM != null) { vboxKM.setVisible(false); vboxKM.setManaged(false); }
            }

            double tongCong = soTien + tienKM;
            if (lblTienKM      != null) lblTienKM.setText(String.format("%,.0f ₫", tienKM));
            if (lblTienKhuyenMai!= null) lblTienKhuyenMai.setText(String.format("%,.0f ₫", tienKM));
            if (lblSoTienFmt   != null) lblSoTienFmt.setText(String.format("%,.0f ₫", soTien));
            if (lblTienKMFmt   != null) lblTienKMFmt.setText(String.format("%,.0f ₫", tienKM));
            if (lblTongCong    != null) lblTongCong.setText(String.format("%,.0f ₫", tongCong));
            if (lblTongTienCong!= null) lblTongTienCong.setText(String.format("%,.0f ₫", tongCong));
        } catch (NumberFormatException e) {
            resetCalc();
        } catch (Exception e) {
            // Ignore KM calc errors silently
        }
    }

    private void resetCalc() {
        if (lblSoTienFmt    != null) lblSoTienFmt.setText("0 ₫");
        if (lblTienKMFmt    != null) lblTienKMFmt.setText("0 ₫");
        if (lblTongCong     != null) lblTongCong.setText("0 ₫");
        if (lblTongTienCong != null) lblTongTienCong.setText("0 ₫");
        if (vboxKM          != null) { vboxKM.setVisible(false); vboxKM.setManaged(false); }
    }

    @FXML public void handleQuickAmount50()  { setQuick("50000"); }
    @FXML public void handleQuickAmount100() { setQuick("100000"); }
    @FXML public void handleQuickAmount200() { setQuick("200000"); }
    @FXML public void handleQuickAmount500() { setQuick("500000"); }

    private void setQuick(String amount) {
        if (txtSoTien != null) txtSoTien.setText(amount);
        recalculate();
    }

    // ===== NẠP TIỀN =====

    @FXML
    public void handleNapTien() {
        if (currentKH == null) { showError("Vui lòng chọn khách hàng"); return; }
        String soTienStr = txtSoTien != null ? txtSoTien.getText().replace(",", "").trim() : "";
        if (soTienStr.isEmpty()) { showError("Vui lòng nhập số tiền"); return; }

        double soTien;
        try { soTien = Double.parseDouble(soTienStr); }
        catch (NumberFormatException e) { showError("Số tiền không hợp lệ"); return; }
        if (soTien <= 0) { showError("Số tiền phải lớn hơn 0"); return; }
        ChuongTrinhKhuyenMai km = getSelectedCTKM();
        String maCTKM = km != null ? km.getMaCTKM() : null;
        String maNV = SessionManager.getCurrentMaNV();

        try {
          napTienBUS.napTien(
                    currentKH.getMakh(),
                    soTien,
                    maCTKM,
                    maNV,
                    "TIENMAT",
                    null
            );
            clearError();

            refreshKhachHangInfo();
            if (txtSoTien   != null) txtSoTien.clear();
            setComboValue(null);
            resetCalc();
            loadLichSuNap();
            ThongBaoDialogHelper.showSuccess(
                    txtSoTien != null ? txtSoTien.getScene() : null,
                    String.format("Đã nạp %,.0f ₫ cho %s!", soTien,
                            trim(currentKH.getHo()) + " " + trim(currentKH.getTen()))
            );
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void refreshKhachHangInfo() {
        if (currentKH == null) return;
        try {
            List<KhachHang> results = khachHangBUS.timKiemKhachHang(currentKH.getMakh());
            if (results != null && !results.isEmpty()) {
                currentKH = results.get(0);
                showKHInfo(currentKH);
            }
        } catch (Exception ignored) {}
    }

    @FXML
    public void handleFilterHistory() { loadLichSuNap(); }

    public void loadLichSuNap() {
        TableView<LichSuNapTien> table = getActiveTable();
        if (table == null) return;
        try {
            List<LichSuNapTien> list = currentKH != null
                    ? napTienBUS.getLichSuNapTien(currentKH.getMakh())
                    : new java.util.ArrayList<>();
            table.setItems(FXCollections.observableArrayList(list));
        } catch (Exception ignored) {}
    }

    private ComboBox<ChuongTrinhKhuyenMai> getActiveCombo() {
        if (cbKhuyenMai != null) return cbKhuyenMai;
        return cboCTKM;
    }

    private ChuongTrinhKhuyenMai getSelectedCTKM() {
        if (cbKhuyenMai != null) return cbKhuyenMai.getValue();
        if (cboCTKM     != null) return cboCTKM.getValue();
        return null;
    }

    private void setComboValue(ChuongTrinhKhuyenMai val) {
        if (cbKhuyenMai != null) cbKhuyenMai.setValue(val);
        if (cboCTKM     != null) cboCTKM.setValue(val);
    }

    private TableView<LichSuNapTien> getActiveTable() {
        if (tblLichSuNap != null) return tblLichSuNap;
        return tableHistory;
    }

    private void showError(String msg) { if (lblError != null) lblError.setText(msg); }
    private void clearError()          { if (lblError != null) lblError.setText(""); }
    private String trim(String s)      { return s != null ? s : ""; }
}