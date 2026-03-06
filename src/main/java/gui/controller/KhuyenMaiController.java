package gui.controller;

import bus.KhuyenMaiBUS;
import entity.ChuongTrinhKhuyenMai;
import gui.dialog.ThemKhuyenMaiDialog;
import javafx.beans.property.SimpleStringProperty;
import java.time.format.DateTimeFormatter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import utils.ThongBaoDialogHelper;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class KhuyenMaiController implements Initializable {

    @FXML private TableView<ChuongTrinhKhuyenMai> tableView;
    @FXML private TableColumn<ChuongTrinhKhuyenMai, String> colMa;
    @FXML private TableColumn<ChuongTrinhKhuyenMai, String> colTen;
    @FXML private TableColumn<ChuongTrinhKhuyenMai, String> colLoai;
    @FXML private TableColumn<ChuongTrinhKhuyenMai, String> colGiaTri;
    @FXML private TableColumn<ChuongTrinhKhuyenMai, String> colDieuKien;
    @FXML private TableColumn<ChuongTrinhKhuyenMai, String> colNgayBD;
    @FXML private TableColumn<ChuongTrinhKhuyenMai, String> colNgayKT;
    @FXML private TableColumn<ChuongTrinhKhuyenMai, String> colTrangThai;

    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cboTrangThai;
    @FXML private Label lblSubtitle;
    @FXML private Label lblTotal;
    @FXML private Button btnSua;
    @FXML private Button btnXoa;

    private final KhuyenMaiBUS khuyenMaiBUS = new KhuyenMaiBUS();
    private ObservableList<ChuongTrinhKhuyenMai> dataList = FXCollections.observableArrayList();
    private FilteredList<ChuongTrinhKhuyenMai> filteredList;
    private ChuongTrinhKhuyenMai selectedItem;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        setupTableSelection();
        if (cboTrangThai != null) {
            cboTrangThai.getItems().setAll("Tất cả", "HOATDONG", "NGUNG", "HETHAN");
            cboTrangThai.setValue("Tất cả");
            cboTrangThai.setOnAction(e -> applyFilter());
        }
        loadData();
    }

    private static final DateTimeFormatter FMT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private void setupTableColumns() {
        if (colMa != null) colMa.setCellValueFactory(new PropertyValueFactory<>("maCTKM"));
        if (colTen != null) colTen.setCellValueFactory(new PropertyValueFactory<>("tenCT"));
        if (colLoai != null) colLoai.setCellValueFactory(c -> {
            String loai = c.getValue().getLoaiKM();
            return new SimpleStringProperty(loai == null ? "" : switch (loai) {
                case "PHANTRAM" -> "Phần trăm (%)";
                case "SOTIEN"   -> "Số tiền (₫)";
                case "TANGGIO"  -> "Tặng giờ";
                default -> loai;
            });
        });
        if (colGiaTri != null) colGiaTri.setCellValueFactory(c -> {
            var km = c.getValue();
            String loai = km.getLoaiKM();
            double val = km.getGiaTriKM();
            return new SimpleStringProperty(switch (loai != null ? loai : "") {
                case "PHANTRAM" -> String.format("%.0f%%", val);
                case "TANGGIO"  -> String.format("%.1f giờ", val);
                default         -> String.format("%,.0f ₫", val);
            });
        });
        if (colDieuKien != null) colDieuKien.setCellValueFactory(c ->
            new SimpleStringProperty(String.format("%,.0f ₫", c.getValue().getDieuKienToiThieu())));
        if (colNgayBD != null) colNgayBD.setCellValueFactory(c -> {
            var d = c.getValue().getNgayBatDau();
            return new SimpleStringProperty(d != null ? d.format(FMT_DATE) : "");
        });
        if (colNgayKT != null) colNgayKT.setCellValueFactory(c -> {
            var d = c.getValue().getNgayKetThuc();
            return new SimpleStringProperty(d != null ? d.format(FMT_DATE) : "");
        });
        if (colTrangThai != null) colTrangThai.setCellValueFactory(c -> {
            String tt = c.getValue().getTrangThai();
            return new SimpleStringProperty(tt == null ? "" : switch (tt) {
                case "HOATDONG" -> "Đang hoạt động";
                case "HETHAN"   -> "Hết hạn";
                case "CHUABD"   -> "Chưa bắt đầu";
                case "NGUNG"    -> "Đã ngừng";
                default -> tt;
            });
        });
    }

    private void setupTableSelection() {
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            selectedItem = n;
            boolean has = n != null;
            if (btnSua != null) btnSua.setDisable(!has);
            if (btnXoa != null) btnXoa.setDisable(!has);
        });
        if (btnSua != null) btnSua.setDisable(true);
        if (btnXoa != null) btnXoa.setDisable(true);
    }

    public void loadData() {
        try {
            List<ChuongTrinhKhuyenMai> list = khuyenMaiBUS.getAllKhuyenMai();
            dataList.setAll(list);
            filteredList = new FilteredList<>(dataList, p -> true);
            tableView.setItems(filteredList);
            updateSubtitle();
        } catch (Exception e) {
            ThongBaoDialogHelper.showError(tableView.getScene(), "Lỗi tải dữ liệu: " + e.getMessage());
        }
    }

    @FXML
    public void handleSearch() { applyFilter(); }

    private void applyFilter() {
        String keyword = txtSearch != null ? txtSearch.getText().toLowerCase().trim() : "";
        String tt = cboTrangThai != null ? cboTrangThai.getValue() : "Tất cả";
        if (filteredList == null) return;
        filteredList.setPredicate(item -> {
            boolean matchKw = keyword.isEmpty()
                || (item.getMaCTKM() != null && item.getMaCTKM().toLowerCase().contains(keyword))
                || (item.getTenCT()  != null && item.getTenCT().toLowerCase().contains(keyword));
            boolean matchTT = tt == null || "Tất cả".equals(tt) || tt.equals(item.getTrangThai());
            return matchKw && matchTT;
        });
        updateSubtitle();
    }

    @FXML
    public void handleThem() { openDialog(null); }

    @FXML
    public void handleSua() {
        if (selectedItem == null) return;
        openDialog(selectedItem);
    }

    @FXML
    public void handleXoa() {
        if (selectedItem == null) return;
        Stage owner = (Stage) tableView.getScene().getWindow();
        if (!gui.dialog.XacNhanDialog.showDelete(owner, selectedItem.getTenCT())) return;
        try {
            khuyenMaiBUS.xoaKhuyenMai(selectedItem.getMaCTKM());
            ThongBaoDialogHelper.showSuccess(tableView.getScene(), "Đã xóa chương trình khuyến mãi!");
            loadData();
        } catch (Exception e) {
            ThongBaoDialogHelper.showError(tableView.getScene(), "Lỗi xóa: " + e.getMessage());
        }
    }

    @FXML
    public void handleLamMoi() {
        if (txtSearch    != null) txtSearch.clear();
        if (cboTrangThai != null) cboTrangThai.setValue("Tất cả");
        loadData();
    }

    private void openDialog(ChuongTrinhKhuyenMai entity) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dialogs/themKhuyenMai.fxml"));
            Parent root = loader.load();
            ThemKhuyenMaiDialog ctrl = loader.getController();
            ctrl.setEntity(entity);
            ctrl.setOnSaveCallback(this::loadData);

            Stage stage = new Stage(StageStyle.UNDECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(tableView.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (Exception e) {
            ThongBaoDialogHelper.showError(tableView.getScene(), "Không thể mở dialog: " + e.getMessage());
        }
    }

    private void updateSubtitle() {
        int total = filteredList != null ? filteredList.size() : 0;
        if (lblSubtitle != null) lblSubtitle.setText("Tổng: " + total + " bản ghi");
        if (lblTotal    != null) lblTotal.setText("Tổng: " + total + " bản ghi");
    }
}
