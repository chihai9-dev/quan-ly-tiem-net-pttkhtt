package gui.controller;

import bus.PhienSuDungBUS;
import dao.*;
import entity.KhachHang;
import entity.MayTinh;
import entity.PhienSuDung;
import gui.dialog.ChonDichVuDialog;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import utils.ThongBaoDialogHelper;

import javax.swing.text.DateFormatter;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class PhienSuDungController implements Initializable {

    // ================================================================
    //  PHẦN 1: FXML COMPONENTS
    // ================================================================

    @FXML private TableView<PhienSuDung>           tableView;
    @FXML private TableColumn<PhienSuDung, String> colMaPhien;
    @FXML private TableColumn<PhienSuDung, String> colMaMay;
    @FXML private TableColumn<PhienSuDung, String> colKhachHang;
    @FXML private TableColumn<PhienSuDung, String> colNhanVien;
    @FXML private TableColumn<PhienSuDung, String> colGioBD;
    @FXML private TableColumn<PhienSuDung, String> colThoiGian;
    @FXML private TableColumn<PhienSuDung, String> colTienTam;
    @FXML private TableColumn<PhienSuDung, String> colTrangThai;

    @FXML private TextField        txtSearch;
    @FXML private ComboBox<String> cboTrangThai;
    @FXML private Label            lblSubtitle;
    @FXML private Label            lblTotal;
    @FXML private Label            lblLiveTime;
    @FXML private Label            lblDangChoi;
    @FXML private Label            lblDoanhThuNgay;
    @FXML private Label            lblPhienHomNay;
    @FXML private Label            lblTimeKetThucPhien;
    @FXML private Button           btnKetThuc;
    @FXML private Button           btnOrderDV;

    // ================================================================
    //  PHẦN 2: BUS & DAO
    // ================================================================

    private final PhienSuDungBUS phienBUS = new PhienSuDungBUS(
            new PhienSuDungDAO(), new MayTinhDAO(), new KhachHangDAO(),
            new GoiDichVuKhachHangDAO(), new SuDungDichVuDAO(), new HoaDonDAO());

    private final KhachHangDAO khachHangDAO = new KhachHangDAO();
    private final MayTinhDAO   mayTinhDAO   = new MayTinhDAO();
    private final SuDungDichVuDAO suDungDichVuDAO = new SuDungDichVuDAO();

    // ================================================================
    //  PHẦN 3: STATE
    // ================================================================

    private final ObservableList<PhienSuDung> dataList     = FXCollections.observableArrayList();
    private FilteredList<PhienSuDung>         filteredList;
    private PhienSuDung                       selectedPhien;
    private Timeline                          clockTimeline;
    private Timeline                          cellRefreshTimeline;
    private Timeline                          refreshDataBase;

    // Cache để tìm kiếm theo tên
    private List<KhachHang> cacheKhachHang = new java.util.ArrayList<>();
    private List<MayTinh>   cacheMayTinh   = new java.util.ArrayList<>();

    private static final DateTimeFormatter FMT     = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FMT_SHORT = DateTimeFormatter.ofPattern("dd/MM HH:mm");

    // ================================================================
    //  PHẦN 4: KHỞI TẠO
    // ================================================================

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupComboBox();
        setupTableColumns();
        setupTableSelection();
        loadCache();
        loadData();
        startClock();
        startCellRefresh();
        startRefreshKetThucPhien();
    }

    private void loadCache() {
        try { cacheKhachHang = khachHangDAO.getAll(); } catch (Exception e) { cacheKhachHang = new java.util.ArrayList<>(); }
        try { cacheMayTinh   = mayTinhDAO.getAll();   } catch (Exception e) { cacheMayTinh   = new java.util.ArrayList<>(); }
    }

    private void setupComboBox() {
        if (cboTrangThai != null) {
            cboTrangThai.getItems().setAll("Tất cả", "DANGCHOI", "DAKETTHUC");
            cboTrangThai.setValue("Tất cả");
            cboTrangThai.setOnAction(e -> applyFilter());
        }
    }

    // ================================================================
    //  PHẦN 5: CỘT BẢNG
    // ================================================================

    private void setupTableColumns() {
        if (colMaPhien  != null) colMaPhien.setCellValueFactory(new PropertyValueFactory<>("maPhien"));
        if (colMaMay    != null) colMaMay.setCellValueFactory(new PropertyValueFactory<>("maMay"));
        if (colNhanVien != null) colNhanVien.setCellValueFactory(new PropertyValueFactory<>("maNV"));

        // Cột KH: hiện "MaKH - Họ Tên"
        if (colKhachHang != null) {
            colKhachHang.setCellValueFactory(cell -> {
                PhienSuDung p = cell.getValue();
                String ten = cacheKhachHang.stream()
                        .filter(kh -> kh.getMakh().equals(p.getMaKH()))
                        .map(kh -> kh.getHo() + " " + kh.getTen())
                        .findFirst().orElse("");
                return new SimpleStringProperty(ten.isBlank() ? p.getMaKH() : p.getMaKH() + " - " + ten.trim());
            });
        }

        if (colGioBD != null) {
            colGioBD.setCellValueFactory(cell -> {
                PhienSuDung p = cell.getValue();
                return new SimpleStringProperty(p.getGioBatDau() != null
                        ? p.getGioBatDau().format(FMT_SHORT) : "");
            });
        }

        if (colThoiGian != null) {
            colThoiGian.setCellValueFactory(cell -> {
                PhienSuDung p = cell.getValue();
                if ("DAKETTHUC".equals(p.getTrangThai()) && p.getTongGio() > 0)
                    return new SimpleStringProperty(String.format("%.2f giờ", p.getTongGio()));
                if (p.getGioBatDau() != null) {
                    long phut = ChronoUnit.MINUTES.between(p.getGioBatDau(), LocalDateTime.now());
                    return new SimpleStringProperty(String.format("%dh%02dm", phut / 60, phut % 60));
                }
                return new SimpleStringProperty("--");
            });
        }

        if (colTienTam != null) {
            colTienTam.setCellValueFactory(cell -> {
                PhienSuDung p = cell.getValue();
                double tien = p.getTienGioChoi();
                if ("DANGCHOI".equals(p.getTrangThai()) && p.getGioBatDau() != null && p.getGiaMoiGio() > 0) {
                    long phut = ChronoUnit.MINUTES.between(p.getGioBatDau(), LocalDateTime.now());
                    tien = (phut / 60.0) * p.getGiaMoiGio();
                }
                return new SimpleStringProperty(String.format("%,.0f ₫", tien));
            });
        }

        if (colTrangThai != null) {
            colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
            colTrangThai.setCellFactory(col -> new TableCell<>() {
                @Override protected void updateItem(String val, boolean empty) {
                    super.updateItem(val, empty);
                    if (empty || val == null) { setText(null); setStyle(""); return; }
                    if ("DANGCHOI".equals(val)) {
                        setText("🟢 Đang chơi");
                        setStyle("-fx-text-fill:#1565C0; -fx-font-weight:bold;");
                    } else {
                        setText("⚫ Đã kết thúc");
                        setStyle("-fx-text-fill:#555555;");
                    }
                }
            });
        }
    }

    private void setupTableSelection() {
        if (tableView == null) return;
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, cu, moi) -> {
            selectedPhien = moi;
            boolean dangChoi = moi != null && "DANGCHOI".equals(moi.getTrangThai());
            if (btnKetThuc != null) btnKetThuc.setDisable(!dangChoi);
            if (btnOrderDV != null) btnOrderDV.setDisable(!dangChoi);
        });
        if (btnKetThuc != null) btnKetThuc.setDisable(true);
        if (btnOrderDV != null) btnOrderDV.setDisable(true);
    }

    // ================================================================
    //  PHẦN 6: TẢI DỮ LIỆU
    // ================================================================

    public void loadData() {
        try {
            List<PhienSuDung> danhSach = phienBUS.getAllPhien();
            dataList.setAll(danhSach);
            filteredList = new FilteredList<>(dataList, p -> true);
            tableView.setItems(filteredList);
            applyFilter();
            updateStats(danhSach);
        } catch (Exception e) {
            e.printStackTrace();
            if (lblSubtitle != null) lblSubtitle.setText("Lỗi: " + e.getMessage());
        }
    }

    private void updateStats(List<PhienSuDung> ds) {
        long soDangChoi = ds.stream().filter(p -> "DANGCHOI".equals(p.getTrangThai())).count();
        double doanhThu = ds.stream()
                .filter(p -> "DAKETTHUC".equals(p.getTrangThai()))
                .mapToDouble(PhienSuDung::getTienGioChoi).sum();
        if (lblDangChoi     != null) lblDangChoi.setText(String.valueOf(soDangChoi));
        if (lblPhienHomNay  != null) lblPhienHomNay.setText(String.valueOf(ds.size()));
        if (lblDoanhThuNgay != null) lblDoanhThuNgay.setText(String.format("%,.0f ₫", doanhThu));
    }

    private void updateLabels() {
        int total = filteredList != null ? filteredList.size() : 0;
        if (lblTotal    != null) lblTotal.setText("Tổng: " + total + " phiên");
        if (lblSubtitle != null) lblSubtitle.setText("Tổng " + total + " bản ghi");
    }

    // ================================================================
    //  PHẦN 7: TÌM KIẾM & LỌC
    // ================================================================

    @FXML public void handleSearch() { applyFilter(); }
    @FXML public void handleFilter() { applyFilter(); }

    private void applyFilter() {
        String kw = txtSearch    != null ? txtSearch.getText().toLowerCase().trim() : "";
        String st = cboTrangThai != null ? cboTrangThai.getValue() : "Tất cả";
        if (filteredList == null) return;

        filteredList.setPredicate(p -> {
            boolean hopSt = "Tất cả".equals(st) || st.equals(p.getTrangThai());
            if (!hopSt) return false;
            if (kw.isEmpty()) return true;
            if (p.getMaPhien() != null && p.getMaPhien().toLowerCase().contains(kw)) return true;
            if (p.getMaKH()    != null && p.getMaKH().toLowerCase().contains(kw)) return true;
            if (p.getMaMay()   != null && p.getMaMay().toLowerCase().contains(kw)) return true;
            boolean matchTenKH = cacheKhachHang.stream()
                    .filter(kh -> kh.getMakh().equals(p.getMaKH()))
                    .anyMatch(kh -> ((kh.getHo() != null ? kh.getHo() : "") + " "
                            + (kh.getTen() != null ? kh.getTen() : "")).toLowerCase().contains(kw));
            if (matchTenKH) return true;
            return cacheMayTinh.stream()
                    .filter(m -> m.getMamay().equals(p.getMaMay()))
                    .anyMatch(m -> m.getTenmay() != null && m.getTenmay().toLowerCase().contains(kw));
        });
        updateLabels();
    }

    // ================================================================
    //  PHẦN 8: MỞ PHIÊN (về code gốc)
    // ================================================================

    @FXML
    public void handleMoPhien() {
        List<KhachHang> dsKH;
        List<MayTinh>   dsMay;
        try {
            dsKH  = khachHangDAO.getAll().stream()
                    .filter(kh -> "HOATDONG".equals(kh.getTrangthai())).collect(Collectors.toList());
            dsMay = mayTinhDAO.getAll().stream()
                    .filter(m -> "TRONG".equals(m.getTrangthai())).collect(Collectors.toList());
        } catch (Exception e) {
            ThongBaoDialogHelper.showError(tableView.getScene(), "Lỗi tải dữ liệu: " + e.getMessage());
            return;
        }
        if (dsKH.isEmpty()) { ThongBaoDialogHelper.showError(tableView.getScene(), "Không có khách hàng nào đang hoạt động."); return; }
        if (dsMay.isEmpty()) { ThongBaoDialogHelper.showError(tableView.getScene(), "Không có máy nào đang trống."); return; }

        Stage dialog = makeDialog();

        ComboBox<KhachHang> cboKH = new ComboBox<>();
        cboKH.getItems().setAll(dsKH);
        cboKH.setMaxWidth(Double.MAX_VALUE);
        cboKH.setPromptText("-- Chọn khách hàng --");
        cboKH.setCellFactory(lv -> khachHangCell());
        cboKH.setButtonCell(khachHangCell());

        Label lblSoDu = new Label("");
        lblSoDu.setStyle("-fx-text-fill:#388E3C; -fx-font-size:12px;");
        cboKH.setOnAction(e -> {
            KhachHang kh = cboKH.getValue();
            lblSoDu.setText(kh != null ? "Số dư: " + String.format("%,.0f ₫", kh.getSodu()) : "");
        });

        ComboBox<MayTinh> cboMay = new ComboBox<>();
        cboMay.getItems().setAll(dsMay);
        cboMay.setMaxWidth(Double.MAX_VALUE);
        cboMay.setPromptText("-- Chọn máy --");
        cboMay.setCellFactory(lv -> mayTinhCell());
        cboMay.setButtonCell(mayTinhCell());

        Label  lblLoi = errLabel();
        Button btnOk  = primaryBtn("▶  Mở phiên", "#1565C0");
        Button btnHuy = secondaryBtn();
        btnHuy.setOnAction(e -> dialog.close());

        VBox body = new VBox(10, boldLabel("Khách hàng:"), cboKH, lblSoDu,
                boldLabel("Máy tính trống:"), cboMay, lblLoi);

        btnOk.setOnAction(e -> {
            KhachHang kh  = cboKH.getValue();
            MayTinh   may = cboMay.getValue();
            if (kh  == null) { lblLoi.setText("⚠ Vui lòng chọn khách hàng!"); return; }
            if (may == null) { lblLoi.setText("⚠ Vui lòng chọn máy!"); return; }
            try {
                PhienSuDung phien = phienBUS.moPhienMoi(kh.getMakh(), may.getMamay());
                dialog.close();
                ThongBaoDialogHelper.showSuccess(tableView.getScene(),
                        "✔ Đã mở phiên " + phien.getMaPhien()
                                + "\nKhách: " + kh.getHo() + " " + kh.getTen()
                                + "  |  Máy: " + may.getTenmay());
                loadCache();
                loadData();
            } catch (Exception ex) {
                lblLoi.setText("⚠ " + ex.getMessage());
            }
        });

        dialog.setScene(new Scene(buildRoot("▶  Mở Phiên Mới", "#1565C0", body, btnOk, btnHuy)));
        dialog.showAndWait();
    }

    // ================================================================
    //  PHẦN 9: KẾT THÚC PHIÊN — hiện dialog thanh toán
    // ================================================================

    @FXML
    public void handleKetThucPhien() {
        if (selectedPhien == null) {
            ThongBaoDialogHelper.showError(tableView.getScene(), "Vui lòng chọn một phiên trong bảng.");
            return;
        }
        if (!"DANGCHOI".equals(selectedPhien.getTrangThai())) {
            ThongBaoDialogHelper.showError(tableView.getScene(), "Phiên này đã kết thúc rồi.");
            return;
        }
        showThanhToanDialog(selectedPhien);
    }

    private void showThanhToanDialog(PhienSuDung phien) {
        long phut = phien.getGioBatDau() != null
                ? ChronoUnit.MINUTES.between(phien.getGioBatDau(), LocalDateTime.now()) : 0;
        double tienGio = (phut / 60.0) * phien.getGiaMoiGio();

        // Lấy tên KH
        String tenKH = cacheKhachHang.stream()
                .filter(kh -> kh.getMakh().equals(phien.getMaKH()))
                .map(kh -> kh.getHo() + " " + kh.getTen())
                .findFirst().orElse(phien.getMaKH());

        // Lấy số dư KH (reload từ DB để lấy giá trị mới nhất)
        double soDuKH = 0;
        try {
            KhachHang khTuDB = khachHangDAO.getById(phien.getMaKH());
            if (khTuDB != null) soDuKH = khTuDB.getSodu();
        } catch (Exception e) {
            soDuKH = cacheKhachHang.stream()
                    .filter(kh -> kh.getMakh().equals(phien.getMaKH()))
                    .mapToDouble(KhachHang::getSodu).findFirst().orElse(0);
        }

        // Lấy danh sách dịch vụ đã dùng
        List<entity.SuDungDichVu> dsDichVu = new java.util.ArrayList<>();
        double tienDichVu = 0;
        try {
            dsDichVu = suDungDichVuDAO.geyByPhien(phien.getMaPhien());
            if (dsDichVu == null) dsDichVu = new java.util.ArrayList<>();
            for (entity.SuDungDichVu sv : dsDichVu) tienDichVu += sv.getThanhtien();
        } catch (Exception e) {
            System.err.println("[ThanhToan] Loi lay dich vu: " + e.getMessage());
        }
        double tongCong;

        Stage dialog = makeDialogWide();

        // ── THÔNG TIN PHIÊN ──────────────────────────────────────────
        VBox secPhien = section("▣  Thông tin phiên");
        GridPane gridPhien = infoGrid();
        addInfoRow(gridPhien, 0, "Mã phiên:",    phien.getMaPhien());
        addInfoRow(gridPhien, 1, "Khách hàng:",  phien.getMaKH() + " - " + tenKH.trim());
        addInfoRow(gridPhien, 2, "Máy:",         phien.getMaMay());
        addInfoRow(gridPhien, 3, "Giờ bắt đầu:", phien.getGioBatDau() != null ? phien.getGioBatDau().format(FMT) : "--");
        addInfoRow(gridPhien, 4, "Thời gian:",   phut / 60 + "h " + String.format("%02dm", phut % 60));

        double gioConLaiTrongGoi = 0;
        if (phien.getMaGoiKH() != null) {
            try {
                entity.GoiDichVuKhachHang goiHT = new dao.GoiDichVuKhachHangDAO().getByID(phien.getMaGoiKH());
                if (goiHT != null) gioConLaiTrongGoi = goiHT.getSogioconlai();
            } catch (Exception ignored) {}
        }

        double gioTuGoi        = Math.min(phut / 60.0, gioConLaiTrongGoi);
        double gioTuMay        = Math.max(0, phut / 60.0 - gioTuGoi);
        double tienTheoMayTam  = gioTuMay * phien.getGiaMoiGio();

        if (phien.getMaGoiKH() != null) {
            addInfoRow(gridPhien, 5, "Gói sử dụng:", phien.getMaGoiKH());
            long gioG = (long) gioTuGoi;
            long phutG = (long) Math.round((gioTuGoi - gioG) * 60);
            addInfoRow(gridPhien, 6, "Giờ từ gói:", gioG + "h" + String.format("%02d", phutG) + "m  (miễn phí)");
            double conDu = Math.max(0, gioConLaiTrongGoi - gioTuGoi);
            long gioD = (long) conDu; long phutD = (long) Math.round((conDu - gioD) * 60);
            addInfoRow(gridPhien, 7, "Giờ còn dư trong gói:", gioD + "h" + String.format("%02d", phutD) + "m");
            long gioM = (long) gioTuMay; long phutM = (long) Math.round((gioTuMay - gioM) * 60);
            addInfoRow(gridPhien, 8, "Giờ tính theo máy:", gioM + "h" + String.format("%02d", phutM) + "m"
                    + "  @ " + String.format("%,.0f ₫/giờ", phien.getGiaMoiGio()));
            addInfoRow(gridPhien, 9, "Tiền theo máy:", gioTuMay > 0
                    ? String.format("%,.0f ₫", tienTheoMayTam)
                    : "0 ₫  (chưa vượt giờ gói)");
            tienGio = tienTheoMayTam;
        } else {
            addInfoRow(gridPhien, 5, "Đơn giá:",  String.format("%,.0f ₫/giờ", phien.getGiaMoiGio()));
            addInfoRow(gridPhien, 6, "Giờ từ gói:", "0h00m");
            addInfoRow(gridPhien, 7, "Giờ tính theo máy:", phut / 60 + "h" + String.format("%02dm", phut % 60));
            addInfoRow(gridPhien, 8, "Tiền giờ:", String.format("%,.0f ₫", tienGio));
        }
        secPhien.getChildren().add(gridPhien);
        tongCong = tienGio + tienDichVu;

        // ── DỊCH VỤ ─────────────────────────────────────────────────
        VBox secDV = section("☆  Dịch vụ sử dụng");
        if (dsDichVu.isEmpty()) {
            Label lblNoDV = new Label("Không có sử dụng dịch vụ");
            lblNoDV.setStyle("-fx-text-fill:#888; -fx-font-style:italic; -fx-font-size:12px;");
            secDV.getChildren().add(lblNoDV);
        } else {
            GridPane gridDV = infoGrid();
            addHeaderRow(gridDV, "Tên dịch vụ", "Số lượng", "Đơn giá", "Thành tiền");
            int rowDV = 1;
            for (entity.SuDungDichVu sv : dsDichVu) {
                addDichVuRow(gridDV, rowDV++, sv);
            }
            secDV.getChildren().add(gridDV);
            Label lblTienDV = new Label("Tổng dịch vụ: " + String.format("%,.0f ₫", tienDichVu));
            lblTienDV.setStyle("-fx-font-weight:bold; -fx-font-size:13px; -fx-text-fill:#e65100;");
            lblTienDV.setAlignment(Pos.CENTER_RIGHT);
            lblTienDV.setMaxWidth(Double.MAX_VALUE);
            secDV.getChildren().add(lblTienDV);
        }

        // ── TỔNG CỘNG ────────────────────────────────────────────────
        final double tongCongFinal = tongCong;
        final double soDuFinal     = soDuKH;
        Label lblTong = new Label(String.format("TỔNG CỘNG:  %,.0f ₫", tongCongFinal));
        lblTong.setStyle("-fx-font-size:16px; -fx-font-weight:bold; -fx-text-fill:white; "
                + "-fx-background-color:#1565C0; -fx-padding:12 16; -fx-background-radius:8;");
        lblTong.setMaxWidth(Double.MAX_VALUE);

        // ── THANH TOÁN TỰ ĐỘNG QUA TÀI KHOẢN ──────────────────────
        VBox secTT = section("▸  Thanh toán tự động");

        VBox panelTaiKhoan = new VBox(8);
        boolean duTien = soDuFinal >= tongCongFinal;

        if (duTien) {
            panelTaiKhoan.setStyle("-fx-background-color:#E8F5E9; -fx-padding:14; -fx-background-radius:8;"
                    + " -fx-border-color:#a5d6a7; -fx-border-radius:8; -fx-border-width:1;");
            Label lblSoDu = new Label("✅  Số dư hiện tại: " + String.format("%,.0f ₫", soDuFinal));
            lblSoDu.setStyle("-fx-font-size:13px; -fx-text-fill:#2e7d32; -fx-font-weight:bold;");
            double conLai = soDuFinal - tongCongFinal;
            Label lblConLai = new Label("Sau thanh toán còn lại: " + String.format("%,.0f ₫", conLai));
            lblConLai.setStyle("-fx-font-size:12px; -fx-text-fill:#388E3C;");
            Label lblNote = new Label("Hệ thống sẽ tự động trừ số tiền từ tài khoản khi xác nhận.");
            lblNote.setStyle("-fx-font-size:12px; -fx-text-fill:#555; -fx-font-style:italic;");
            panelTaiKhoan.getChildren().addAll(lblSoDu, lblConLai, lblNote);
        } else {
            panelTaiKhoan.setStyle("-fx-background-color:#FFEBEE; -fx-padding:14; -fx-background-radius:8;"
                    + " -fx-border-color:#ef9a9a; -fx-border-radius:8; -fx-border-width:1;");
            Label lblSoDu = new Label("⚠  Số dư hiện tại: " + String.format("%,.0f ₫", soDuFinal));
            lblSoDu.setStyle("-fx-font-size:13px; -fx-text-fill:#C62828; -fx-font-weight:bold;");
            double conThieu = tongCongFinal - soDuFinal;
            Label lblThieu = new Label("Thiếu: " + String.format("%,.0f ₫", conThieu));
            lblThieu.setStyle("-fx-font-size:13px; -fx-text-fill:#C62828; -fx-font-weight:bold;");
            Label lblNote = new Label("❌ Không thể kết thúc phiên! Số dư không đủ để thanh toán.\nVui lòng yêu cầu khách nạp thêm tiền trước khi kết thúc.");
            lblNote.setStyle("-fx-font-size:12px; -fx-text-fill:#b71c1c; -fx-font-weight:bold; -fx-wrap-text:true;");
            lblNote.setMaxWidth(Double.MAX_VALUE);
            panelTaiKhoan.getChildren().addAll(lblSoDu, lblThieu, lblNote);
        }

        secTT.getChildren().add(panelTaiKhoan);

        Label  lblLoi     = errLabel();
        Button btnXacNhan = primaryBtn("✔  Xác nhận thanh toán", "#1b5e20");
        Button btnHuy     = secondaryBtn();
        btnHuy.setId("btnHuyTT");
        btnHuy.setOnAction(e -> dialog.close());

        if (!duTien) {
            btnXacNhan.setDisable(true);
            btnXacNhan.setStyle("-fx-background-color:#BDBDBD; -fx-text-fill:#757575; "
                    + "-fx-font-weight:bold; -fx-pref-height:36px; -fx-pref-width:210px; -fx-background-radius:6;");
        }

        btnXacNhan.setOnAction(e -> doConfirmPayment(phien, dialog, lblLoi));

        // Layout dialog
        VBox body = new VBox(14, secPhien, new Separator(), secDV, new Separator(),
                lblTong, new Separator(), secTT, lblLoi);
        body.setPadding(new Insets(0));

        ScrollPane scroll = new ScrollPane(body);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setStyle("-fx-background-color:transparent; -fx-background:transparent;");
        scroll.setPrefHeight(480);
        scroll.setMaxHeight(javafx.stage.Screen.getPrimary().getVisualBounds().getHeight() * 0.7);

        VBox root = new VBox(14);
        root.setPadding(new Insets(24));
        root.setPrefWidth(580);
        root.setStyle("-fx-background-color:white; -fx-border-color:#E0E0E0; "
                + "-fx-border-width:1; -fx-border-radius:10; -fx-background-radius:10;");
        Label titleLbl = new Label("■  Kết Thúc & Thanh Toán");
        titleLbl.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill:#C62828;");
        HBox btnRow = new HBox(10, btnXacNhan, btnHuy);
        btnRow.setAlignment(Pos.CENTER_RIGHT);
        root.getChildren().addAll(titleLbl, new Separator(), scroll, new Separator(), btnRow);

        dialog.setScene(new Scene(root));
        dialog.showAndWait();
    }

    // ================================================================
    //  PHẦN 10: REFRESH
    // ================================================================

    @FXML
    public void handleRefresh() {
        if (txtSearch    != null) txtSearch.clear();
        if (cboTrangThai != null) cboTrangThai.setValue("Tất cả");
        List<PhienSuDung> danhSachDaKetThuc = phienBUS.kiemTraVaKetThucPhienQuaHan();
        if (!danhSachDaKetThuc.isEmpty()) {
            ThongBaoDialogHelper.showWarning(tableView.getScene(),
                    "⚠ Tự động kết thúc " + danhSachDaKetThuc.size()
                            + " phiên do khách hàng hết tiền.\nHóa đơn đã được tạo tự động.");
        }
        loadCache();
        loadData();
    }

    private  void loadKetThucPhien(){
        List<PhienSuDung> danhSachDaKetThuc = phienBUS.kiemTraVaKetThucPhienQuaHan();
        if (!danhSachDaKetThuc.isEmpty()) {
            ThongBaoDialogHelper.showWarning(tableView.getScene(),
                    "⚠ Tự động kết thúc " + danhSachDaKetThuc.size()
                            + " phiên do khách hàng hết tiền.\nHóa đơn đã được tạo tự động.");
        }
        loadCache();
        loadData();
    }

    // ================================================================
    //  PHẦN 11: ORDER DỊCH VỤ
    // ================================================================

    @FXML
    public void handleOrderDichVu() {
        if (selectedPhien == null) return;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dialogs/chonDichVu.fxml"));
            Parent root = loader.load();
            ChonDichVuDialog ctrl = loader.getController();
            // Reload KhachHang từ DB để lấy số dư mới nhất
            KhachHang khMoiNhat = null;
            try { khMoiNhat = khachHangDAO.getById(selectedPhien.getMaKH()); } catch (Exception ignored) {}
            if (khMoiNhat == null) {
                khMoiNhat = cacheKhachHang.stream()
                        .filter(kh -> kh.getMakh().equals(selectedPhien.getMaKH()))
                        .findFirst().orElse(null);
            }
            ctrl.setPhienFull(selectedPhien, khMoiNhat,
                    selectedPhien.getMaKH() + " | " + selectedPhien.getMaMay());
            ctrl.setOnOrderCallback(this::loadData);
            Stage stage = new Stage(StageStyle.UNDECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(tableView.getScene().getWindow());
            Scene orderScene = new Scene(root);
            // ESC để đóng + tìm nút Hủy trong dialog và gán action đóng stage
            orderScene.setOnKeyPressed(ke -> {
                if (ke.getCode() == javafx.scene.input.KeyCode.ESCAPE) stage.close();
            });
            // Đảm bảo mọi Button có text chứa "Hủy"/"huy"/"Cancel" đều đóng được stage
            javafx.application.Platform.runLater(() -> {
                root.lookupAll(".button").stream()
                        .filter(n -> n instanceof Button)
                        .map(n -> (Button) n)
                        .filter(b -> b.getText() != null && (
                                b.getText().toLowerCase().contains("h") &&
                                        (b.getText().contains("ủy") || b.getText().contains("uy")
                                                || b.getText().toLowerCase().contains("cancel"))))
                        .forEach(b -> b.setOnAction(ev -> stage.close()));
            });
            stage.setScene(orderScene);
            stage.showAndWait();
        } catch (Exception e) {
            ThongBaoDialogHelper.showError(tableView.getScene(), "Không thể mở dialog:\n" + e.getMessage());
        }
    }

    // ================================================================
    //  PHẦN 12: TIMER
    // ================================================================

    private void startClock() {
        if (lblLiveTime == null) return;
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm:ss");
        clockTimeline = new Timeline(new KeyFrame(Duration.seconds(1),
                e -> lblLiveTime.setText(LocalDateTime.now().format(fmt))));
        clockTimeline.setCycleCount(Timeline.INDEFINITE);
        clockTimeline.play();
    }

    private void startCellRefresh() {
        cellRefreshTimeline = new Timeline(new KeyFrame(Duration.seconds(30),
                e -> { if (tableView != null) tableView.refresh(); }));
        cellRefreshTimeline.setCycleCount(Timeline.INDEFINITE);
        cellRefreshTimeline.play();
    }







    //
    private void startRefreshKetThucPhien(){
        DateTimeFormatter fm = DateTimeFormatter.ofPattern("HH:mm:ss");
        refreshDataBase = new Timeline(new KeyFrame(Duration.seconds(60),
                e -> {
                    startRefreshKetThucPhien();
                }));
        refreshDataBase.setCycleCount(Timeline.INDEFINITE);
        refreshDataBase.play();
        if(lblTimeKetThucPhien != null){
            lblTimeKetThucPhien.setText(LocalDateTime.now().format(fm));
        }
    }



//    ===================================================
//    THỐNG KÊ
//    ==================================================




    // ================================================================
    //  PHẦN 13: UI HELPERS
    // ================================================================

    private Stage makeDialog() {
        Stage s = new Stage(StageStyle.UNDECORATED);
        s.initModality(Modality.APPLICATION_MODAL);
        s.initOwner(tableView.getScene().getWindow());
        return s;
    }

    private Stage makeDialogWide() {
        Stage s = new Stage(StageStyle.UNDECORATED);
        s.initModality(Modality.APPLICATION_MODAL);
        s.initOwner(tableView.getScene().getWindow());
        return s;
    }

    private VBox section(String title) {
        VBox box = new VBox(8);
        Label lbl = new Label(title);
        lbl.setStyle("-fx-font-size:14px; -fx-font-weight:bold; -fx-text-fill:#1565C0; -fx-padding:0 0 4 0;");
        box.getChildren().add(lbl);
        return box;
    }

    private GridPane infoGrid() {
        GridPane g = new GridPane();
        g.setHgap(16); g.setVgap(8);
        g.setPadding(new Insets(10));
        g.setStyle("-fx-background-color:#f5f7fa; -fx-background-radius:6; -fx-border-color:#e0e0e0; -fx-border-radius:6; -fx-border-width:1;");
        ColumnConstraints c1 = new ColumnConstraints(150);
        ColumnConstraints c2 = new ColumnConstraints();
        c2.setHgrow(Priority.ALWAYS);
        g.getColumnConstraints().addAll(c1, c2, c2, c2);
        return g;
    }

    private void addInfoRow(GridPane g, int row, String label, String value) {
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill:#616161; -fx-font-size:12px; -fx-min-width:140;");
        Label val = new Label(value != null ? value : "--");
        val.setStyle("-fx-font-weight:bold; -fx-font-size:13px; -fx-text-fill:#212121;");
        val.setWrapText(true);
        g.add(lbl, 0, row);
        g.add(val, 1, row);
        GridPane.setColumnSpan(val, 3);
    }

    private void addHeaderRow(GridPane g, String c1, String c2, String c3, String c4) {
        String style = "-fx-font-weight:bold; -fx-font-size:12px; -fx-text-fill:#333; -fx-padding:0 0 4 0;";
        Label l1 = new Label(c1); l1.setStyle(style);
        Label l2 = new Label(c2); l2.setStyle(style);
        Label l3 = new Label(c3); l3.setStyle(style);
        Label l4 = new Label(c4); l4.setStyle(style);
        g.add(l1, 0, 0); g.add(l2, 1, 0); g.add(l3, 2, 0); g.add(l4, 3, 0);
    }

    private void addDichVuRow(GridPane g, int row, entity.SuDungDichVu sv) {
        String style = "-fx-font-size:12px; -fx-text-fill:#212121;";
        Label lTen = new Label(sv.getMadv() != null ? sv.getMadv() : "--");
        lTen.setStyle(style);
        Label lSL  = new Label(String.valueOf(sv.getSoluong()));
        lSL.setStyle(style + " -fx-alignment:CENTER;");
        Label lGia = new Label(String.format("%,.0f ₫", sv.getDongia()));
        lGia.setStyle(style);
        Label lTT  = new Label(String.format("%,.0f ₫", sv.getThanhtien()));
        lTT.setStyle("-fx-font-size:12px; -fx-font-weight:bold; -fx-text-fill:#e65100;");
        g.add(lTen, 0, row); g.add(lSL, 1, row); g.add(lGia, 2, row); g.add(lTT, 3, row);
    }

    /**
     * Flow QR:
     * 1. Hiện QR → nút đổi thành "Chọn cách thanh toán này"
     * 2. Nhấn → nút đổi "Đang chờ chuyển khoản..." đếm 5s
     * 3. Sau 5s → kết thúc phiên + xuất HĐ + hiện toast + nút "Đã nhận tiền" + btnHuy thành "Thoát"
     */
    private void setupQRButton(Button btn, Button btnHuyRef, Timeline[] timerRef,
                               PhienSuDung phien, Stage dialog,
                               double tongTien, String phuongThuc, String tenKH) {
        if (timerRef[0] != null) { timerRef[0].stop(); timerRef[0] = null; }
        // Reset về trạng thái mặc định của nút Hủy
        if (btnHuyRef != null) {
            btnHuyRef.setText("✖  Hủy");
            btnHuyRef.setStyle("-fx-background-color:#E0E0E0; -fx-pref-height:36px; "
                    + "-fx-pref-width:90px; -fx-background-radius:6;");
            btnHuyRef.setOnAction(e -> dialog.close());
        }

        // Bước 1: nút đổi thành "Chọn cách thanh toán này"
        btn.setDisable(false);
        btn.setText("💳  Chọn cách thanh toán này");
        btn.setStyle("-fx-background-color:#1565C0; -fx-text-fill:white; "
                + "-fx-font-weight:bold; -fx-pref-height:36px; -fx-pref-width:230px; -fx-background-radius:6;");

        btn.setOnAction(ev -> {
            // Bước 2: đếm ngược "Đang chờ chuyển khoản..."
            btn.setDisable(true);
            btn.setText("⏳  Đang chờ chuyển khoản...");
            btn.setStyle("-fx-background-color:#78909C; -fx-text-fill:white; "
                    + "-fx-font-weight:bold; -fx-pref-height:36px; -fx-pref-width:230px; -fx-background-radius:6;");

            final int[] cd = {5};
            timerRef[0] = new Timeline(new KeyFrame(Duration.seconds(1), e2 -> {
                cd[0]--;
                if (cd[0] > 0) {
                    btn.setText("⏳  Đang chờ... " + cd[0] + "s");
                } else {
                    timerRef[0].stop();

                    // Bước 3: kết thúc phiên + xuất HĐ ngay lập tức
                    try {
                        phienBUS.ketThucPhien(phien.getMaPhien());
                    } catch (Exception ex) {
                        System.err.println("[QR] Loi ket thuc: " + ex.getMessage());
                    }

                    // Hiện toast thông báo nhận tiền
                    showPaymentToast(dialog, phien.getMaPhien(), tongTien, phuongThuc, tenKH);

                    // Nút xác nhận → "Đã nhận tiền"
                    btn.setDisable(false);
                    btn.setText("✅  Đã nhận tiền thành công");
                    btn.setStyle("-fx-background-color:#2e7d32; -fx-text-fill:white; "
                            + "-fx-font-weight:bold; -fx-pref-height:36px; -fx-pref-width:230px; -fx-background-radius:6;");
                    btn.setOnAction(e3 -> { dialog.close(); loadCache(); loadData(); });

                    // Nút Hủy → "Thoát"
                    if (btnHuyRef != null) {
                        btnHuyRef.setText("🚪  Thoát");
                        btnHuyRef.setStyle("-fx-background-color:#455A64; -fx-text-fill:white; "
                                + "-fx-font-weight:bold; -fx-pref-height:36px; -fx-pref-width:90px; -fx-background-radius:6;");
                        btnHuyRef.setOnAction(e3 -> { dialog.close(); loadCache(); loadData(); });
                    }
                }
            }));
            timerRef[0].setCycleCount(5);
            timerRef[0].play();
        });
    }



    /** Toast thông báo nhận tiền */
    private void showPaymentToast(Stage owner, String maPhien, double soTien,
                                  String phuongThuc, String tenKH) {
        Stage toast = new Stage(StageStyle.UNDECORATED);
        toast.initOwner(owner);
        VBox box = new VBox(6);
        box.setPadding(new Insets(14, 20, 14, 20));
        box.setStyle("-fx-background-color:#1b5e20; -fx-background-radius:10;"
                + " -fx-border-color:#a5d6a7; -fx-border-radius:10; -fx-border-width:1;"
                + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 12, 0, 0, 4);");
        box.setAlignment(Pos.CENTER_LEFT);
        Label l1 = new Label("Đã nhận thanh toán");
        l1.setStyle("-fx-font-size:14px; -fx-font-weight:bold; -fx-text-fill:white;");
        Label l2 = new Label("Khách hàng: " + (tenKH != null ? tenKH.trim() : "--"));
        l2.setStyle("-fx-font-size:13px; -fx-text-fill:#c8e6c9; -fx-font-weight:bold;");
        Label l3 = new Label("Số tiền: " + String.format("%,.0f ₫", soTien));
        l3.setStyle("-fx-font-size:13px; -fx-text-fill:#c8e6c9;");
        Label l4 = new Label("Nội dung: Thanh toán hóa đơn " + maPhien);
        l4.setStyle("-fx-font-size:12px; -fx-text-fill:#a5d6a7;");
        Label l5 = new Label("Phương thức: " + phuongThuc);
        l5.setStyle("-fx-font-size:12px; -fx-text-fill:#a5d6a7;");
        box.getChildren().addAll(l1, l2, l3, l4, l5);
        toast.setScene(new Scene(box));
        toast.setOpacity(1.0);
        javafx.geometry.Rectangle2D screen = javafx.stage.Screen.getPrimary().getVisualBounds();
        toast.setX(screen.getMaxX() - 380);
        toast.setY(screen.getMaxY() - 200);
        toast.show();
        Timeline fadeTimer = new Timeline(
                new KeyFrame(Duration.seconds(3.5)),
                new KeyFrame(Duration.seconds(3.8), ev -> toast.setOpacity(0.6)),
                new KeyFrame(Duration.seconds(4.1), ev -> toast.setOpacity(0.2)),
                new KeyFrame(Duration.seconds(4.4), ev -> toast.close())
        );
        fadeTimer.play();
    }

    /** Thực hiện xác nhận thanh toán */
    private void doConfirmPayment(PhienSuDung phien, Stage dialog, Label lblLoi) {
        try {
            phienBUS.ketThucPhien(phien.getMaPhien());
            dialog.close();
            loadCache();
            loadData();
        } catch (Exception ex) {
            if (lblLoi != null) lblLoi.setText("Lỗi: " + ex.getMessage());
        }
    }

    /** Tạo QR đẹp */
    private StackPane buildFakeQR(double size) {
        StackPane pane = new StackPane();
        pane.setPrefSize(size, size); pane.setMaxSize(size, size);
        pane.setStyle("-fx-background-color:white; -fx-border-color:#444; -fx-border-width:4;"
                + " -fx-background-radius:8; -fx-border-radius:8;"
                + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 2);");
        int cells = 25;
        double cs = (size - 8) / cells;
        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setTranslateX(4); grid.setTranslateY(4);
        java.util.Random rnd = new java.util.Random(77);
        for (int r = 0; r < cells; r++) {
            for (int c = 0; c < cells; c++) {
                javafx.scene.shape.Rectangle rect = new javafx.scene.shape.Rectangle(cs - 0.5, cs - 0.5);
                rect.setArcWidth(1); rect.setArcHeight(1);
                boolean inTL = r < 7 && c < 7, inTR = r < 7 && c >= cells - 7, inBL = r >= cells - 7 && c < 7;
                if (inTL || inTR || inBL) {
                    int lr = inTL ? r : (inTR ? r : r - (cells - 7));
                    int lc = inTL ? c : (inTR ? c - (cells - 7) : c);
                    boolean ob = lr == 0 || lr == 6 || lc == 0 || lc == 6;
                    boolean ib = lr >= 2 && lr <= 4 && lc >= 2 && lc <= 4;
                    rect.setFill(ob || ib ? Color.BLACK : Color.WHITE);
                } else {
                    rect.setFill(rnd.nextInt(10) < 6 ? Color.BLACK : Color.WHITE);
                }
                grid.add(rect, c, r);
            }
        }
        double ls = size * 0.18;
        StackPane logo = new StackPane();
        javafx.scene.shape.Circle circle = new javafx.scene.shape.Circle(ls / 2);
        circle.setFill(Color.web("#1565C0")); circle.setStroke(Color.WHITE); circle.setStrokeWidth(2);
        Label ll = new Label("QR");
        ll.setStyle("-fx-font-size:" + (int)(ls * 0.38) + "px; -fx-font-weight:bold; -fx-text-fill:white;");
        logo.getChildren().addAll(circle, ll);
        pane.getChildren().addAll(grid, logo);
        return pane;
    }

    private VBox buildRoot(String title, String color, VBox body, Button btnOk, Button btnHuy) {
        VBox root = new VBox(14);
        root.setPadding(new Insets(24)); root.setPrefWidth(500);
        root.setStyle("-fx-background-color:white; -fx-border-color:#E0E0E0; "
                + "-fx-border-width:1; -fx-border-radius:8; -fx-background-radius:8;");
        Label lbl = new Label(title);
        lbl.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill:" + color + ";");
        HBox btnRow = new HBox(10, btnOk, btnHuy);
        btnRow.setAlignment(Pos.CENTER_RIGHT);
        root.getChildren().addAll(lbl, new Separator(), body, new Separator(), btnRow);
        return root;
    }

    private Label boldLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-weight:bold; -fx-font-size:13px;");
        return l;
    }

    private Label errLabel() {
        Label l = new Label("");
        l.setStyle("-fx-text-fill:#C62828; -fx-font-size:12px; -fx-wrap-text:true;");
        l.setMaxWidth(Double.MAX_VALUE);
        return l;
    }

    private Button primaryBtn(String text, String color) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color:" + color + "; -fx-text-fill:white; "
                + "-fx-font-weight:bold; -fx-pref-height:36px; -fx-pref-width:210px; -fx-background-radius:6;");
        return b;
    }

    private Button secondaryBtn() {
        Button b = new Button("✖  Hủy");
        b.setStyle("-fx-background-color:#E0E0E0; -fx-pref-height:36px; "
                + "-fx-pref-width:90px; -fx-background-radius:6;");
        return b;
    }

    private ListCell<KhachHang> khachHangCell() {
        return new ListCell<>() {
            @Override protected void updateItem(KhachHang kh, boolean empty) {
                super.updateItem(kh, empty);
                if (empty || kh == null) { setText(null); return; }
                setText(String.format("%s  |  %s %s  |  %,.0f ₫",
                        kh.getMakh(), kh.getHo(), kh.getTen(), kh.getSodu()));
            }
        };
    }

    private ListCell<MayTinh> mayTinhCell() {
        return new ListCell<>() {
            @Override protected void updateItem(MayTinh m, boolean empty) {
                super.updateItem(m, empty);
                if (empty || m == null) { setText(null); return; }
                setText(String.format("%s  |  %s  |  %,.0f ₫/giờ",
                        m.getMamay(), m.getTenmay(), m.getGiamoigio()));
            }
        };
    }
}