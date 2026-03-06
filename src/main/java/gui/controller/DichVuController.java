package gui.controller;

import bus.DichVuBUS;
import entity.DichVu;
import gui.dialog.ThemDichVuDialog;
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

public class DichVuController implements Initializable {

    @FXML private TableView<DichVu> tableView;
    @FXML private TableColumn<DichVu, String>  colMa;
    @FXML private TableColumn<DichVu, String>  colTen;
    @FXML private TableColumn<DichVu, String>  colLoai;
    @FXML private TableColumn<DichVu, Double>  colDonGia;
    @FXML private TableColumn<DichVu, String>  colDonViTinh;
    @FXML private TableColumn<DichVu, Integer> colSoLuong;
    @FXML private TableColumn<DichVu, String>  colTrangThai;

    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cboTrangThai;
    @FXML private Label lblSubtitle;
    @FXML private Label lblTotal;
    @FXML private Button btnSua;
    @FXML private Button btnXoa;

    private final DichVuBUS dichVuBUS = new DichVuBUS();
    private ObservableList<DichVu> dataList = FXCollections.observableArrayList();
    private FilteredList<DichVu> filteredList;
    private DichVu selectedItem;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        setupTableSelection();
        if (cboTrangThai != null) {
            cboTrangThai.getItems().setAll("Tất cả", "DANGBAN", "NGUNGBAN");
            cboTrangThai.setValue("Tất cả");
            cboTrangThai.setOnAction(e -> applyFilter());
        }
        loadData();
    }

    private void setupTableColumns() {
        if (colMa        != null) colMa.setCellValueFactory(new PropertyValueFactory<>("maDV"));
        if (colTen       != null) colTen.setCellValueFactory(new PropertyValueFactory<>("tenDV"));
        if (colLoai      != null) colLoai.setCellValueFactory(new PropertyValueFactory<>("loaiDV"));
        if (colDonGia    != null) {
            colDonGia.setCellValueFactory(new PropertyValueFactory<>("donGia"));
            colDonGia.setCellFactory(col -> new TableCell<>() {
                @Override protected void updateItem(Double v, boolean empty) {
                    super.updateItem(v, empty);
                    setText(empty || v == null ? null : String.format("%,.0f ₫", v));
                }
            });
        }
        if (colDonViTinh != null) colDonViTinh.setCellValueFactory(new PropertyValueFactory<>("donViTinh"));
        if (colSoLuong   != null) colSoLuong.setCellValueFactory(new PropertyValueFactory<>("soLuongTon"));
        if (colTrangThai != null) colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
    }

    private void setupTableSelection() {
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedItem = newVal;
            boolean has = newVal != null;
            if (btnSua != null) btnSua.setDisable(!has);
            if (btnXoa != null) btnXoa.setDisable(!has);
        });
        if (btnSua != null) btnSua.setDisable(true);
        if (btnXoa != null) btnXoa.setDisable(true);
    }

    public void loadData() {
        try {
            List<DichVu> list = dichVuBUS.getDichVuConHang();
            dataList.setAll(list);
            filteredList = new FilteredList<>(dataList, p -> true);
            tableView.setItems(filteredList);
            updateSubtitle();
        } catch (Exception e) {
            ThongBaoDialogHelper.showError(tableView.getScene(), "Lỗi tải dữ liệu: " + e.getMessage());
        }
    }

    @FXML
    public void handleSearch() {
        applyFilter();
    }

    private void applyFilter() {
        String keyword = txtSearch != null ? txtSearch.getText().toLowerCase().trim() : "";
        String tt      = cboTrangThai != null ? cboTrangThai.getValue() : "Tất cả";
        if (filteredList == null) return;
        filteredList.setPredicate(item -> {
            boolean matchKw = keyword.isEmpty()
                || item.getMadv().toLowerCase().contains(keyword)
                || item.getTendv().toLowerCase().contains(keyword);
            boolean matchTT = tt == null || "Tất cả".equals(tt)
                || tt.equals(item.getTrangthai());
            return matchKw && matchTT;
        });
        updateSubtitle();
    }

    @FXML
    public void handleThem() {
        openDialog(null);
    }

    @FXML
    public void handleSua() {
        if (selectedItem == null) return;
        openDialog(selectedItem);
    }

    @FXML
    public void handleXoa() {
        if (selectedItem == null) return;
        Stage owner = (Stage) tableView.getScene().getWindow();
        boolean confirmed = gui.dialog.XacNhanDialog.showDelete(owner, selectedItem.getTendv());
        if (!confirmed) return;
        try {
            dichVuBUS.xoaDichVu(selectedItem.getMadv());
            ThongBaoDialogHelper.showSuccess(tableView.getScene(), "Đã xóa dịch vụ thành công!");
            loadData();
        } catch (Exception e) {
            ThongBaoDialogHelper.showError(tableView.getScene(), "Lỗi xóa: " + e.getMessage());
        }
    }

    @FXML
    public void handleLamMoi() {
        if (txtSearch != null) txtSearch.clear();
        if (cboTrangThai != null) cboTrangThai.setValue("Tất cả");
        loadData();
    }

    private void openDialog(DichVu entity) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dialogs/themDichVu.fxml"));
            Parent root = loader.load();
            ThemDichVuDialog ctrl = loader.getController();
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
