package gui.controller;

import bus.DichVuBUS;
import entity.DichVu;
import gui.dialog.ThongBaoDialog;
import gui.dialog.XacNhanDialog;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DichVuController implements Initializable {

    // === TABLE ===
    @FXML private TableView<DichVu> tableView;
    @FXML private TableColumn<DichVu, String>  colMaDV, colTenDV, colDonVi, colTrangThai;
    @FXML private TableColumn<DichVu, Double>  colDonGia;
    @FXML private TableColumn<DichVu, Integer> colTonKho;

    // === TOOLBAR ===
    @FXML private TextField        txtSearch;
    @FXML private ComboBox<String> cboTrangThai;
    @FXML private Button           btnThem, btnXoa, btnKhoiPhuc, btnLamMoi;

    // === FORM ===
    @FXML private ScrollPane       paneDetail;
    @FXML private TextField        detailMaDV, detailTenDV, detailDonGia, detailDonVi, detailTonKho;
    @FXML private ComboBox<String> cboLoaiDV;
    @FXML private Button           btnLuu, btnHuy;

    // === LABELS trong form (để set tiếng Việt từ Java) ===
    @FXML private Label lblFormTitle;
    @FXML private Label lblMaDV, lblTenDV, lblLoaiDV, lblDonGia, lblDonViTinh, lblSoLuongTon;

    // === LABELS chung ===
    @FXML private Label lblSubtitle, lblTotal;

    // === LABELS tiêu đề cột (để set tiếng Việt từ Java) ===
    @FXML private Label lblHeader;

    private final DichVuBUS bus = new DichVuBUS();
    private final ObservableList<DichVu> masterList = FXCollections.observableArrayList();
    private FilteredList<DichVu> filteredList;

    private enum FormMode { NONE, THEM, SUA }
    private FormMode formMode = FormMode.NONE;
    private DichVu selectedDV = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupVietnameseText();   // Set tất cả text tiếng Việt từ Java
        setupTableColumns();
        setupRowColors();
        setupComboBoxes();
        hideForm();
        loadData();

        tableView.setOnMouseClicked(e -> {
            DichVu sel = tableView.getSelectionModel().getSelectedItem();
            if (sel == null) resetForm();
            else handleRowClick();
        });
    }

    // Set toàn bộ text tiếng Việt có dấu từ Java (tránh lỗi encoding FXML trên Windows)
    private void setupVietnameseText() {
        // Tiêu đề cột bảng
        if (colMaDV      != null) colMaDV.setText("Mã DV");
        if (colTenDV     != null) colTenDV.setText("Tên dịch vụ");
        if (colDonGia    != null) colDonGia.setText("Đơn giá");
        if (colTonKho    != null) colTonKho.setText("Tồn kho");
        if (colDonVi     != null) colDonVi.setText("Đơn vị");
        if (colTrangThai != null) colTrangThai.setText("Trạng thái");

        // Toolbar
        if (txtSearch    != null) txtSearch.setPromptText("🔍 Tìm theo tên, mã...");
        if (btnThem      != null) btnThem.setText("+ Thêm");
        if (btnXoa       != null) btnXoa.setText("Xóa");
        if (btnKhoiPhuc  != null) btnKhoiPhuc.setText("Khôi phục");
        if (btnLamMoi    != null) btnLamMoi.setText("Làm mới");

        // Labels trong form
        if (lblFormTitle  != null) lblFormTitle.setText("Thông tin dịch vụ");
        if (lblMaDV       != null) lblMaDV.setText("Mã dịch vụ");
        if (lblTenDV      != null) lblTenDV.setText("Tên dịch vụ");
        if (lblLoaiDV     != null) lblLoaiDV.setText("Loại dịch vụ");
        if (lblDonGia     != null) lblDonGia.setText("Đơn giá (VNĐ)");
        if (lblDonViTinh  != null) lblDonViTinh.setText("Đơn vị tính");
        if (lblSoLuongTon != null) lblSoLuongTon.setText("Số lượng tồn");

        // Buttons form
        if (btnLuu != null) btnLuu.setText("Lưu");
        if (btnHuy != null) btnHuy.setText("Hủy");

        // Prompt text cho các ô nhập
        if (detailMaDV   != null) detailMaDV.setPromptText("Tự động");
        if (detailTenDV  != null) detailTenDV.setPromptText("Nhập tên dịch vụ...");
        if (detailDonGia != null) detailDonGia.setPromptText("Ví dụ: 50000");
        if (detailDonVi  != null) detailDonVi.setPromptText("Ví dụ: ly, phần, cái...");
        if (detailTonKho != null) detailTonKho.setPromptText("0");
    }

    private void setupTableColumns() {
        colMaDV.setCellValueFactory(new PropertyValueFactory<>("madv"));
        colTenDV.setCellValueFactory(new PropertyValueFactory<>("tendv"));
        colDonGia.setCellValueFactory(new PropertyValueFactory<>("dongia"));
        colTonKho.setCellValueFactory(new PropertyValueFactory<>("soluongton"));
        colDonVi.setCellValueFactory(new PropertyValueFactory<>("donvitinh"));
        colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangthai"));

        // Canh giữa tất cả các cột + hiển thị tiếng Việt cho cột Trạng thái
        setCenterCell(colMaDV,      v -> v);
        setCenterCell(colTenDV,     v -> v);
        setCenterCellDouble(colDonGia);
        setCenterCellInt(colTonKho);
        setCenterCell(colDonVi,     v -> v);
        setCenterCell(colTrangThai, v -> switch (v) {
            case "CONHANG"  -> "Còn hàng";
            case "HETHANG"  -> "Hết hàng";
            case "NGUNGBAN" -> "Ngừng bán";
            default         -> v;
        });
    }

    // Helper canh giữa cho cột String
    private void setCenterCell(TableColumn<DichVu, String> col,
                               java.util.function.Function<String, String> mapper) {
        col.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(String v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || v == null) { setText(null); }
                else { setText(mapper.apply(v)); setStyle("-fx-alignment:CENTER;"); }
            }
        });
    }

    // Helper canh giữa cho cột Double
    private void setCenterCellDouble(TableColumn<DichVu, Double> col) {
        col.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(Double v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || v == null) { setText(null); }
                else { setText(String.format("%,.0f", v)); setStyle("-fx-alignment:CENTER;"); }
            }
        });
    }

    // Helper canh giữa cho cột Integer
    private void setCenterCellInt(TableColumn<DichVu, Integer> col) {
        col.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(Integer v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || v == null) { setText(null); }
                else { setText(String.valueOf(v)); setStyle("-fx-alignment:CENTER;"); }
            }
        });
    }

    private void setupRowColors() {
        tableView.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(DichVu item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setStyle(""); return; }
                if (isSelected()) {
                    switch (item.getTrangthai()) {
                        case "CONHANG"  -> setStyle("-fx-background-color:#1b5e20; -fx-text-fill:white;");
                        case "HETHANG"  -> setStyle("-fx-background-color:#b71c1c; -fx-text-fill:white;");
                        case "NGUNGBAN" -> setStyle("-fx-background-color:#37474f; -fx-text-fill:white;");
                        default         -> setStyle("-fx-background-color:#1565c0; -fx-text-fill:white;");
                    }
                } else {
                    switch (item.getTrangthai()) {
                        case "CONHANG"  -> setStyle("-fx-background-color:#dcedc8; -fx-text-fill:#1b5e20;");
                        case "HETHANG"  -> setStyle("-fx-background-color:#ffcdd2; -fx-text-fill:#b71c1c;");
                        case "NGUNGBAN" -> setStyle("-fx-background-color:#cfd8dc; -fx-text-fill:#263238;");
                        default         -> setStyle("");
                    }
                }
            }
        });
        tableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, o, n) -> tableView.refresh()
        );
    }

    private void setupComboBoxes() {
        // ComboBox lọc trạng thái — dùng tiếng Việt
        if (cboTrangThai != null) {
            cboTrangThai.getItems().setAll("Tất cả", "CONHANG", "HETHANG", "NGUNGBAN");
            cboTrangThai.setValue("Tất cả");
            cboTrangThai.setOnAction(e -> handleSearch());

            // Hiển thị tiếng Việt trong dropdown
            cboTrangThai.setCellFactory(lv -> new ListCell<>() {
                @Override protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : trangThaiText(item));
                }
            });
            cboTrangThai.setButtonCell(new ListCell<>() {
                @Override protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : trangThaiText(item));
                }
            });
        }

        if (cboLoaiDV != null) {
            cboLoaiDV.getItems().setAll("DOUONG", "THUCPHAM", "KHAC");
            // Hiển thị tiếng Việt cho loại dịch vụ
            cboLoaiDV.setCellFactory(lv -> new ListCell<>() {
                @Override protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : loaiDVText(item));
                }
            });
            cboLoaiDV.setButtonCell(new ListCell<>() {
                @Override protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : loaiDVText(item));
                }
            });
        }
    }

    // Chuyển mã trạng thái -> tiếng Việt
    private String trangThaiText(String code) {
        return switch (code) {
            case "CONHANG"  -> "Còn hàng";
            case "HETHANG"  -> "Hết hàng";
            case "NGUNGBAN" -> "Ngừng bán";
            case "Tất cả"   -> "Tất cả";
            default         -> code;
        };
    }

    // Chuyển mã loại -> tiếng Việt
    private String loaiDVText(String code) {
        return switch (code) {
            case "DOUONG"   -> "Đồ uống";
            case "THUCPHAM" -> "Thực phẩm";
            case "KHAC"     -> "Khác";
            default         -> code;
        };
    }

    @FXML
    public void loadData() {
        try {
            List<DichVu> data = bus.getAll();
            masterList.setAll(data);
            filteredList = new FilteredList<>(masterList, p -> true);
            tableView.setItems(filteredList);
            if (lblSubtitle != null) lblSubtitle.setText("Tổng cộng: " + data.size() + " dịch vụ");
            if (lblTotal    != null) lblTotal.setText("Tổng: " + data.size() + " bản ghi");
            handleSearch();
        } catch (Exception e) {
            showError("Lỗi tải dữ liệu: " + e.getMessage());
        }
    }

    @FXML
    public void handleSearch() {
        if (filteredList == null) return;
        String kw     = txtSearch    != null ? txtSearch.getText().toLowerCase().trim() : "";
        String status = cboTrangThai != null ? cboTrangThai.getValue() : "Tất cả";
        filteredList.setPredicate(dv -> {
            boolean matchKw = kw.isEmpty()
                    || dv.getTendv().toLowerCase().contains(kw)
                    || dv.getMadv().toLowerCase().contains(kw);
            boolean matchStatus = "Tất cả".equals(status) || dv.getTrangthai().equals(status);
            return matchKw && matchStatus;
        });
    }

    @FXML
    public void handleRowClick() {
        DichVu sel = tableView.getSelectionModel().getSelectedItem();
        if (sel == null || formMode == FormMode.THEM) return;
        selectedDV = sel;
        formMode   = FormMode.SUA;
        fillForm(sel);
        showForm();
        btnLuu.setVisible(true);
        btnHuy.setVisible(true);
        btnXoa.setDisable(false);
        btnKhoiPhuc.setDisable(false);
    }

    @FXML
    public void handleThem() {
        formMode   = FormMode.THEM;
        selectedDV = null;
        clearForm();
        showForm();
        btnLuu.setVisible(true);
        btnHuy.setVisible(true);
        btnXoa.setDisable(true);
        btnKhoiPhuc.setDisable(true);
        tableView.getSelectionModel().clearSelection();
    }

    @FXML
    public void handleLuu() {
        Stage owner = (Stage) tableView.getScene().getWindow();
        try {
            DichVu dv = buildFromForm();
            if (formMode == FormMode.THEM) {
                bus.themDichVu(dv);
                ThongBaoDialog.showSuccess(owner, "Thêm dịch vụ thành công!");
            } else {
                bus.suaDichVu(dv);
                ThongBaoDialog.showSuccess(owner, "Cập nhật thành công!");
            }
            resetForm();
            loadData();
        } catch (Exception e) {
            showError(e.getMessage()); // Giữ nguyên form khi lỗi
        }
    }

    @FXML
    public void handleHuy() {
        resetForm();
    }

    @FXML
    public void handleXoa() {
        if (selectedDV == null) return;
        Stage owner = (Stage) tableView.getScene().getWindow();
        boolean ok = XacNhanDialog.showDelete(owner, selectedDV.getTendv());
        if (!ok) return;
        try {
            bus.xoaDichVu(selectedDV.getMadv());
            ThongBaoDialog.showSuccess(owner, "Đã chuyển sang trạng thái Ngừng bán.");
            resetForm();
            loadData();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    public void handleKhoiPhuc() {
        if (selectedDV == null) return;
        Stage owner = (Stage) tableView.getScene().getWindow();
        boolean ok = XacNhanDialog.show(owner, "Khôi phục",
                "Khôi phục dịch vụ: " + selectedDV.getTendv() + "?");
        if (!ok) return;
        try {
            bus.khoiPhucLaiDichVu(selectedDV.getMadv());
            ThongBaoDialog.showSuccess(owner, "Khôi phục thành công!");
            resetForm();
            loadData();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    public void handleLamMoi() {
        if (txtSearch    != null) txtSearch.clear();
        if (cboTrangThai != null) cboTrangThai.setValue("Tất cả");
        resetForm();
        loadData();
    }

    // === HELPERS ===

    private void fillForm(DichVu dv) {
        if (detailMaDV   != null) detailMaDV.setText(dv.getMadv());
        if (detailTenDV  != null) detailTenDV.setText(dv.getTendv());
        if (detailDonGia != null) detailDonGia.setText(String.valueOf(dv.getDongia()));
        if (detailDonVi  != null) detailDonVi.setText(dv.getDonvitinh());
        if (detailTonKho != null) detailTonKho.setText(String.valueOf(dv.getSoluongton()));
        if (cboLoaiDV    != null) cboLoaiDV.setValue(dv.getLoaidv());
    }

    private void clearForm() {
        if (detailMaDV   != null) detailMaDV.clear();
        if (detailTenDV  != null) detailTenDV.clear();
        if (detailDonGia != null) detailDonGia.clear();
        if (detailDonVi  != null) detailDonVi.clear();
        if (detailTonKho != null) detailTonKho.setText("0");
        if (cboLoaiDV    != null) cboLoaiDV.setValue(null);
    }

    private DichVu buildFromForm() throws Exception {
        String ten   = detailTenDV  != null ? detailTenDV.getText().trim()  : "";
        String donVi = detailDonVi  != null ? detailDonVi.getText().trim()  : "";
        String loai  = cboLoaiDV    != null ? cboLoaiDV.getValue()          : null;
        double gia;
        try {
            gia = Double.parseDouble(detailDonGia != null ? detailDonGia.getText().trim() : "0");
        } catch (NumberFormatException ex) {
            throw new Exception("Đơn giá phải là số hợp lệ!");
        }
        String maDV = (formMode == FormMode.SUA && selectedDV != null) ? selectedDV.getMadv() : "";
        DichVu dv = new DichVu();
        dv.setMadv(maDV);
        dv.setTendv(ten);
        dv.setLoaidv(loai);
        dv.setDongia(gia);
        dv.setDonvitinh(donVi);
        dv.setSoluongton(0);
        dv.setTrangthai("HETHANG");
        return dv;
    }

    private void showForm() {
        if (paneDetail != null) { paneDetail.setVisible(true);  paneDetail.setManaged(true); }
    }

    private void hideForm() {
        if (paneDetail != null) { paneDetail.setVisible(false); paneDetail.setManaged(false); }
    }

    private void resetForm() {
        formMode   = FormMode.NONE;
        selectedDV = null;
        clearForm();
        hideForm();
        if (btnLuu      != null) btnLuu.setVisible(false);
        if (btnHuy      != null) btnHuy.setVisible(false);
        if (btnXoa      != null) btnXoa.setDisable(true);
        if (btnKhoiPhuc != null) btnKhoiPhuc.setDisable(true);
        tableView.getSelectionModel().clearSelection();
    }

    private void showError(String msg) {
        Stage owner = tableView != null && tableView.getScene() != null
                ? (Stage) tableView.getScene().getWindow() : null;
        ThongBaoDialog.showError(owner, msg);
    }
}