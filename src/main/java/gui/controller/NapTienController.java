
package gui.controller;

import bus.KhachHangBUS;
import bus.KhuyenMaiBUS;
import bus.NapTienBUS;
import entity.ChuongTrinhKhuyenMai;
import entity.KhachHang;
import entity.LichSuNapTien;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import utils.ThongBaoDialogHelper;
import utils.SessionManager;

import java.net.URL;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.beans.property.SimpleStringProperty;
import java.util.List;
import java.util.ResourceBundle;

public class NapTienController implements Initializable {

    @FXML private ComboBox<KhachHang> cboKhachHang;
    @FXML private VBox      vboxKHInfo;
    @FXML private Label     lblKHAvatar;
    @FXML private Label     lblKHTen;
    @FXML private Label     lblKHSDT;
    @FXML private Label     lblKHSoDu;
    @FXML private TextField txtSoTien;

    @FXML private ComboBox<ChuongTrinhKhuyenMai> cboCTKM;
    @FXML private VBox      vboxKM;
    @FXML private Label     lblTienKM;
    @FXML private Label     lblSoTienFmt;
    @FXML private Label     lblTienKMFmt;
    @FXML private Label     lblTongCong;
    @FXML private Label     lblGoiY;
    @FXML private Label     lblLiveTime;
    @FXML private Label     lblError;
    @FXML private Button    btnNapTien;
    @FXML private DatePicker dateFilter;

    @FXML private TableView<LichSuNapTien>           tableHistory;
    @FXML private TableColumn<LichSuNapTien, String> colMaNT;
    @FXML private TableColumn<LichSuNapTien, String> colKHHist;
    @FXML private TableColumn<LichSuNapTien, String> colSoTienHist;
    @FXML private TableColumn<LichSuNapTien, String> colKMHist;
    @FXML private TableColumn<LichSuNapTien, String> colTongHist;
    @FXML private TableColumn<LichSuNapTien, String> colNVHist;
    @FXML private TableColumn<LichSuNapTien, String> colNgayHist;

    private final NapTienBUS   napTienBUS   = new NapTienBUS();
    private final KhachHangBUS khachHangBUS = new KhachHangBUS();
    private final KhuyenMaiBUS khuyenMaiBUS = new KhuyenMaiBUS();

    private KhachHang currentKH = null;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private Timeline clockTimeline;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableHistory();
        loadDanhSachKH();
        loadCTKM();
        hideKHInfo();
        startClock();
        if (cboCTKM   != null) cboCTKM.setOnAction(e -> recalculate());
        if (txtSoTien != null) txtSoTien.textProperty().addListener((obs, o, n) -> recalculate());
        if (btnNapTien != null) btnNapTien.setDisable(true);
        if (cboKhachHang != null) {
            cboKhachHang.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal == null) {
                    hideKHInfo();
                    if (btnNapTien != null) btnNapTien.setDisable(true);
                } else {
                    currentKH = newVal;
                    showKHInfo(currentKH);
                    clearError();
                    loadLichSuNap();
                    if (btnNapTien != null) btnNapTien.setDisable(false);
                }
            });
        }
    }

    private void loadDanhSachKH() {
        if (cboKhachHang == null) return;
        try {
            List<KhachHang> list = khachHangBUS.timKiemKhachHang("");
            cboKhachHang.getItems().clear();
            cboKhachHang.getItems().add(null);
            if (list != null) cboKhachHang.getItems().addAll(list);
            cboKhachHang.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(KhachHang kh, boolean empty) {
                    super.updateItem(kh, empty);
                    if (empty || kh == null) setText("-- Chọn khách hàng --");
                    else {
                        String hoTen = (trim(kh.getHo()) + " " + trim(kh.getTen())).trim();
                        String sdt   = kh.getSodienthoai() != null ? " (" + kh.getSodienthoai() + ")" : "";
                        setText(kh.getMakh() + " - " + hoTen + sdt);
                    }
                }
            });
            cboKhachHang.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(KhachHang kh, boolean empty) {
                    super.updateItem(kh, empty);
                    if (empty || kh == null) setText("-- Chọn khách hàng --");
                    else {
                        String hoTen = (trim(kh.getHo()) + " " + trim(kh.getTen())).trim();
                        setText(kh.getMakh() + " - " + hoTen);
                    }
                }
            });
        } catch (Exception ignored) {}
    }

    @FXML
    public void handleChonKH() {
        if (cboKhachHang == null) return;
        KhachHang kh = cboKhachHang.getValue();
        if (kh == null) { hideKHInfo(); return; }
        currentKH = kh;
        showKHInfo(currentKH);
        clearError();
        loadLichSuNap();
    }

    @FXML public void handleTimKH() { handleChonKH(); }

    private void setupTableHistory() {
        if (colMaNT      != null) colMaNT.setCellValueFactory(new PropertyValueFactory<>("maNap"));
        if (colKHHist    != null) colKHHist.setCellValueFactory(new PropertyValueFactory<>("maKH"));
        if (colSoTienHist!= null) colSoTienHist.setCellValueFactory(c -> new SimpleStringProperty(String.format("%,.0f ₫", c.getValue().getSoTienNap())));
        if (colKMHist    != null) colKMHist.setCellValueFactory(c -> new SimpleStringProperty(String.format("%,.0f ₫", c.getValue().getKhuyenMai())));
        if (colTongHist  != null) colTongHist.setCellValueFactory(c -> new SimpleStringProperty(String.format("%,.0f ₫", c.getValue().getTongTienCong())));
        if (colNVHist    != null) colNVHist.setCellValueFactory(new PropertyValueFactory<>("maNV"));
        if (colNgayHist  != null) colNgayHist.setCellValueFactory(c -> {
            var d = c.getValue().getNgayNap();
            return new SimpleStringProperty(d != null ? d.format(FMT) : "");
        });
    }

    private void loadCTKM() {
        if (cboCTKM == null) return;
        try {
            List<ChuongTrinhKhuyenMai> list = khuyenMaiBUS.getKhuyenMaiConHieuLuc();
            cboCTKM.getItems().clear();
            cboCTKM.getItems().add(null);
            cboCTKM.getItems().addAll(list);
            cboCTKM.setCellFactory(lv -> new ListCell<>() {
                @Override protected void updateItem(ChuongTrinhKhuyenMai item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : (item == null
                            ? "-- Không áp dụng --"
                            : item.getTenCT() + " (" + item.getGiaTriKMFormatted() + ")"));
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

    private void showKHInfo(KhachHang kh) {
        String hoTen = (trim(kh.getHo()) + " " + trim(kh.getTen())).trim();
        if (lblKHAvatar != null) lblKHAvatar.setText(hoTen.isEmpty() ? "K" : String.valueOf(hoTen.charAt(0)).toUpperCase());
        if (lblKHTen   != null) lblKHTen.setText(hoTen.isEmpty() ? "(Không có tên)" : hoTen);
        if (lblKHSDT   != null) lblKHSDT.setText(kh.getSodienthoai() != null ? kh.getSodienthoai() : "-");
        if (lblKHSoDu  != null) lblKHSoDu.setText(String.format("%,.0f ₫", kh.getSodu()));
        if (vboxKHInfo != null) { vboxKHInfo.setVisible(true); vboxKHInfo.setManaged(true); }
    }

    private void hideKHInfo() {
        currentKH = null;
        if (vboxKHInfo != null) { vboxKHInfo.setVisible(false); vboxKHInfo.setManaged(false); }
    }

    @FXML public void handleSoTienChanged() { recalculate(); }
    @FXML public void handleCTKMChanged()   { recalculate(); }

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
            double tongCong = soTien + tienKM;
            if (lblTienKM    != null) lblTienKM.setText(String.format("%,.0f ₫", tienKM));
            if (lblSoTienFmt != null) lblSoTienFmt.setText(String.format("%,.0f ₫", soTien));
            if (lblTienKMFmt != null) lblTienKMFmt.setText(String.format("%,.0f ₫", tienKM));
            if (lblTongCong  != null) lblTongCong.setText(String.format("%,.0f ₫", tongCong));
            goiYKhuyenMaiLoiNhat(soTien);
        } catch (NumberFormatException e) {
            resetCalc();
        } catch (Exception ignored) {}
    }



    //==========================================
    //      TIMER
    //=========================================
    private void startClock() {
        if (lblLiveTime == null) return;
        //tạo bộ định dạng thời gian
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm:ss");
        //
        clockTimeline = new Timeline(new KeyFrame(Duration.seconds(1),
                e -> lblLiveTime.setText(LocalDateTime.now().format(fmt))));
        clockTimeline.setCycleCount(Timeline.INDEFINITE);
        clockTimeline.play();
    }


    private void goiYKhuyenMaiLoiNhat(double soTien) {
        if (lblGoiY == null) return;
        try {
            ChuongTrinhKhuyenMai kmDangChon = cboCTKM != null ? cboCTKM.getValue() : null;
            ChuongTrinhKhuyenMai kmTotNhat  = khuyenMaiBUS.timChuongTrinhTotNhat(soTien);
            if (kmTotNhat == null) {
                lblGoiY.setText("ℹ️ Không có KM phù hợp với số tiền này");
                lblGoiY.setStyle("-fx-text-fill:#888888; -fx-font-size:11px;"
                        + "-fx-background-color:#F5F5F5; -fx-background-radius:4; -fx-padding:5 10;");
            } else if (kmDangChon != null
                    && kmDangChon.getMaCTKM().equals(kmTotNhat.getMaCTKM())) {
                lblGoiY.setText("✅ Bạn đang dùng KM tốt nhất!");
                lblGoiY.setStyle("-fx-text-fill:#2E7D32; -fx-font-size:11px; -fx-font-weight:bold;"
                        + "-fx-background-color:#E8F5E9; -fx-background-radius:4; -fx-padding:5 10;");
            } else {
                double bonusMax = khuyenMaiBUS.tinhGiaTriKhuyenMai(kmTotNhat.getMaCTKM(), soTien);
                lblGoiY.setText(String.format("💡 Gợi ý: \"%s\" — thêm %,.0f ₫",
                        kmTotNhat.getTenCT(), bonusMax));
                lblGoiY.setStyle("-fx-text-fill:#E65100; -fx-font-size:11px; -fx-font-weight:bold;"
                        + "-fx-background-color:#FFF3E0; -fx-background-radius:4; -fx-padding:5 10;");
            }
            lblGoiY.setVisible(true);
            lblGoiY.setManaged(true);
        } catch (Exception e) {
            lblGoiY.setVisible(false);
            lblGoiY.setManaged(false);
        }
    }

    private void resetCalc() {
        if (lblSoTienFmt != null) lblSoTienFmt.setText("0 ₫");
        if (lblTienKMFmt != null) lblTienKMFmt.setText("0 ₫");
        if (lblTongCong  != null) lblTongCong.setText("0 ₫");
        if (lblTienKM    != null) lblTienKM.setText("0 ₫");
        if (vboxKM   != null) { vboxKM.setVisible(false);   vboxKM.setManaged(false); }
        if (lblGoiY  != null) { lblGoiY.setVisible(false);  lblGoiY.setManaged(false); }
    }

    @FXML public void handleQuickAmount50()  { setQuick("50000"); }
    @FXML public void handleQuickAmount100() { setQuick("100000"); }
    @FXML public void handleQuickAmount200() { setQuick("200000"); }
    @FXML public void handleQuickAmount500() { setQuick("500000"); }
    private void setQuick(String v) { if (txtSoTien != null) txtSoTien.setText(v); recalculate(); }

    @FXML
    public void handleNapTien() {
        if (currentKH == null && cboKhachHang != null) currentKH = cboKhachHang.getValue();
        if (currentKH == null) {
            showError("Vui lòng chọn khách hàng từ danh sách");
            if (cboKhachHang != null) cboKhachHang.requestFocus();
            return;
        }
        String soTienStr = txtSoTien != null ? txtSoTien.getText().replace(",", "").trim() : "";
        if (soTienStr.isEmpty()) { showError("Vui lòng nhập số tiền"); return; }
        double soTien;
        try { soTien = Double.parseDouble(soTienStr); }
        catch (NumberFormatException e) { showError("Số tiền không hợp lệ"); return; }
        if (soTien <= 0) { showError("Số tiền phải lớn hơn 0"); return; }
        ChuongTrinhKhuyenMai km = cboCTKM != null ? cboCTKM.getValue() : null;
        String maCTKM = km != null ? km.getMaCTKM() : null;
        String maNV   = SessionManager.getCurrentMaNV();
        try {
            napTienBUS.napTien(currentKH.getMakh(), soTien, maCTKM, maNV, "TIENMAT", null);
            clearError();
            refreshKhachHangInfo();
            if (txtSoTien != null) txtSoTien.clear();
            if (cboCTKM   != null) cboCTKM.setValue(null);
            resetCalc();
            loadLichSuNap();
            ThongBaoDialogHelper.showSuccess(
                    txtSoTien != null ? txtSoTien.getScene() : null,
                    String.format("Đã nạp %,.0f ₫ cho %s!", soTien,
                            (trim(currentKH.getHo()) + " " + trim(currentKH.getTen())).trim())
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

    @FXML public void handleFilterHistory() { loadLichSuNap(); }

    public void loadLichSuNap() {
        if (tableHistory == null) return;
        try {
            List<LichSuNapTien> list = currentKH != null ? napTienBUS.getLichSuNapTien(currentKH.getMakh()) : new java.util.ArrayList<>();
            if (dateFilter != null && dateFilter.getValue() != null) {
                LocalDate filterDate = dateFilter.getValue();
                list = list.stream()
                        .filter(n -> n.getNgayNap() != null
                                && !n.getNgayNap().toLocalDate().isBefore(filterDate))
                        .collect(java.util.stream.Collectors.toList());
            }
            tableHistory.setItems(FXCollections.observableArrayList(list));
        } catch (Exception e) {
            showError("Lỗi tải lịch sử: " + e.getMessage());
        }
    }

    private void showError(String msg) { if (lblError != null) lblError.setText(msg); }
    private void clearError()          { if (lblError != null) lblError.setText(""); }
    private String trim(String s)      { return s != null ? s : ""; }
}
