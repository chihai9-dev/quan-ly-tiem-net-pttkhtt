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

    @FXML private TextField txtSoTien;
    @FXML private ComboBox<ChuongTrinhKhuyenMai> cboCTKM;
    @FXML private VBox vboxKM;
    @FXML private Label lblTienKM;
    @FXML private Label lblSoTienFmt;
    @FXML private Label lblTienKMFmt;
    @FXML private Label lblTongCong;
    @FXML private Label lblError;
    @FXML private Button btnNapTien;

    @FXML private TableView<LichSuNapTien> tableHistory;
    @FXML private TableColumn<LichSuNapTien, String> colNgayNap;
    @FXML private TableColumn<LichSuNapTien, String> colSoTienNap;
    @FXML private TableColumn<LichSuNapTien, String> colKhuyenMaiHist;
    @FXML private TableColumn<LichSuNapTien, String> colTongCongHist;
    @FXML private TableColumn<LichSuNapTien, String> colPhuongThuc;

    private final NapTienBUS napTienBUS   = new NapTienBUS();
    private final KhachHangBUS khachHangBUS = new KhachHangBUS();
    private final KhuyenMaiBUS khuyenMaiBUS = new KhuyenMaiBUS();

    private KhachHang currentKH = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableHistory();
        loadCTKM();
        hideKHInfo();
        if (cboCTKM != null) cboCTKM.setOnAction(e -> recalculate());
        if (txtSoTien != null) txtSoTien.textProperty().addListener((obs, o, n) -> recalculate());
        loadHistory();
    }

    // ===== LOAD DATA =====

    private void loadCTKM() {
        if (cboCTKM == null) return;
        try {
            List<ChuongTrinhKhuyenMai> list = khuyenMaiBUS.getKhuyenMaiConHieuLuc();
            cboCTKM.getItems().clear();
            cboCTKM.getItems().add(null); // Không áp dụng
            cboCTKM.getItems().addAll(list);
            // Hiển thị tên trong combo
            cboCTKM.setCellFactory(lv -> new ListCell<>() {
                @Override protected void updateItem(ChuongTrinhKhuyenMai item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : (item == null ? "-- Không áp dụng --" : item.getTenCT() + " (" + item.getGiaTriKMFormatted() + ")"));
                }
            });
            cboCTKM.setButtonCell(new ListCell<>() {
                @Override protected void updateItem(ChuongTrinhKhuyenMai item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : (item == null ? "-- Không áp dụng --" : item.getTenCT()));
                }
            });
        } catch (Exception ignored) {}
    }

    private void setupTableHistory() {
        if (tableHistory == null) return;
        // LichSuNapTien entity - dùng lambda cho formatted output
        if (colNgayNap != null) colNgayNap.setCellValueFactory(c -> {
            var d = c.getValue().getNgayNap();
            return new SimpleStringProperty(d != null
                ? d.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "");
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
            loadHistory();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void showKHInfo(KhachHang kh) {
        String hoTen = (kh.getHo() != null ? kh.getHo() : "") + " " + (kh.getTen() != null ? kh.getTen() : "");
        hoTen = hoTen.trim();
        if (lblKHAvatar != null) lblKHAvatar.setText(hoTen.isEmpty() ? "K" : String.valueOf(hoTen.charAt(0)).toUpperCase());
        if (lblKHTen    != null) lblKHTen.setText(hoTen.isEmpty() ? "(Không có tên)" : hoTen);
        if (lblKHSDT    != null) lblKHSDT.setText(kh.getSodienthoai() != null ? kh.getSodienthoai() : "-");
        if (lblKHSoDu   != null) lblKHSoDu.setText(String.format("%,.0f ₫", kh.getSodu()));
        if (vboxKHInfo  != null) { vboxKHInfo.setVisible(true); vboxKHInfo.setManaged(true); }
    }

    private void hideKHInfo() {
        currentKH = null;
        if (vboxKHInfo != null) { vboxKHInfo.setVisible(false); vboxKHInfo.setManaged(false); }
    }

    // ===== TÍNH TOÁN =====

    @FXML
    public void handleSoTienChanged() { recalculate(); }

    @FXML
    public void handleCTKMChanged() { recalculate(); }

    private void recalculate() {
        try {
            String raw = txtSoTien != null ? txtSoTien.getText().replace(",", "").trim() : "";
            if (raw.isEmpty()) { resetCalc(); return; }
            double soTien = Double.parseDouble(raw);

            double tienKM = 0;
            ChuongTrinhKhuyenMai km = cboCTKM != null ? cboCTKM.getValue() : null;
            if (km != null) {
                tienKM = napTienBUS.tinhKhuyenMai(soTien, km.getMaCTKM());
                if (vboxKM != null) { vboxKM.setVisible(true); vboxKM.setManaged(true); }
            } else {
                if (vboxKM != null) { vboxKM.setVisible(false); vboxKM.setManaged(false); }
            }

            if (lblTienKM    != null) lblTienKM.setText(String.format("%,.0f ₫", tienKM));
            if (lblSoTienFmt != null) lblSoTienFmt.setText(String.format("%,.0f ₫", soTien));
            if (lblTienKMFmt != null) lblTienKMFmt.setText(String.format("%,.0f ₫", tienKM));
            if (lblTongCong  != null) lblTongCong.setText(String.format("%,.0f ₫", soTien + tienKM));
        } catch (NumberFormatException e) {
            resetCalc();
        } catch (Exception e) {
            // Ignore KM calc errors
        }
    }

    private void resetCalc() {
        if (lblSoTienFmt != null) lblSoTienFmt.setText("0 ₫");
        if (lblTienKMFmt != null) lblTienKMFmt.setText("0 ₫");
        if (lblTongCong  != null) lblTongCong.setText("0 ₫");
        if (vboxKM       != null) { vboxKM.setVisible(false); vboxKM.setManaged(false); }
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

        ChuongTrinhKhuyenMai km = cboCTKM != null ? cboCTKM.getValue() : null;
        String maCTKM = km != null ? km.getMaCTKM() : null;

        try {
            // napTien(maKH, soTienNap, maCTKM) - maNV được lấy từ SessionManager trong BUS
            napTienBUS.napTien(currentKH.getMakh(), soTien, maCTKM);
            clearError();
            // Refresh KH info
            currentKH = khachHangBUS.getKhachHangById(currentKH.getMakh());
            showKHInfo(currentKH);
            if (txtSoTien  != null) txtSoTien.clear();
            if (cboCTKM    != null) cboCTKM.setValue(null);
            resetCalc();
            loadHistory();
            ThongBaoDialogHelper.showSuccess(
                txtSoTien != null ? txtSoTien.getScene() : null,
                String.format("Đã nạp %,.0f ₫ cho %s!", soTien, currentKH.getHo() + " " + currentKH.getTen())
            );
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    public void handleFilterHistory() { loadHistory(); }

    private void loadHistory() {
        if (tableHistory == null) return;
        try {
            List<LichSuNapTien> list = currentKH != null
                ? napTienBUS.getLichSuNapTien(currentKH.getMakh())
                : new java.util.ArrayList<>(); // Chưa chọn KH - hiển thị rỗng
            tableHistory.setItems(FXCollections.observableArrayList(list));
        } catch (Exception ignored) {}
    }

    private void showError(String msg) { if (lblError != null) lblError.setText(msg); }
    private void clearError()          { if (lblError != null) lblError.setText(""); }
}
