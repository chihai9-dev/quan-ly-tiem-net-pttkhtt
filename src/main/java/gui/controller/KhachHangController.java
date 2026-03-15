package gui.controller;

import bus.KhachHangBUS;
import entity.KhachHang;
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

public class KhachHangController implements Initializable {

    @FXML private TableView<KhachHang> tableView;
    @FXML private TableColumn<KhachHang, String> colMaKH;
    @FXML private TableColumn<KhachHang, String> colHoTen;
    @FXML private TableColumn<KhachHang, String> colSDT;
    @FXML private TableColumn<KhachHang, Double> colSoDu;
    @FXML private TableColumn<KhachHang, String> colTrangThai;

    @FXML private TextField txtSearch;

    @FXML private ComboBox<String> cboTrangThai;
    @FXML private Label lblSubtitle;
    @FXML private Label lblTotal;
    @FXML private Button btnSua;
    @FXML private Button btnXoa;

    private final KhachHangBUS khachHangBUS = new KhachHangBUS();
    private ObservableList<KhachHang> dataList = FXCollections.observableArrayList();
    private FilteredList<KhachHang> filteredList;
    private KhachHang selectedItem;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        setupTableSelection();
        if (cboTrangThai != null) {
            cboTrangThai.getItems().setAll("Tất cả", "HOATDONG", "NGUNG");
            cboTrangThai.setValue("Tất cả");
            cboTrangThai.setOnAction(e -> applyFilter());
        }
        loadData();
    }

    private void setupTableColumns() {
        if (colMaKH != null) colMaKH.setCellValueFactory(new PropertyValueFactory<>("makh"));

        // Nối cột Họ và Tên lại với nhau
        if (colHoTen != null) {
            colHoTen.setCellValueFactory(cellData -> {
                KhachHang kh = cellData.getValue();
                String hoTen = (kh.getHo() != null ? kh.getHo() + " " : "") + (kh.getTen() != null ? kh.getTen() : "");
                return new javafx.beans.property.SimpleStringProperty(hoTen.trim());
            });
        }

        if (colSDT != null) colSDT.setCellValueFactory(new PropertyValueFactory<>("sodienthoai"));

        // Format hiển thị tiền VNĐ
        if (colSoDu != null) {
            colSoDu.setCellValueFactory(new PropertyValueFactory<>("sodu"));
            colSoDu.setCellFactory(col -> new TableCell<KhachHang, Double>() {
                @Override protected void updateItem(Double v, boolean empty) {
                    super.updateItem(v, empty);
                    setText(empty || v == null ? null : String.format("%,.0f ₫", v));
                }
            });
        }

        if (colTrangThai != null) colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangthai"));
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
            List<KhachHang> list = khachHangBUS.getAllKhachHang();
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
                || (item.getMakh()         != null && item.getMakh().toLowerCase().contains(keyword))
                || (item.getTen()          != null && item.getTen().toLowerCase().contains(keyword))
                || (item.getHo()           != null && item.getHo().toLowerCase().contains(keyword))
                || (item.getSodienthoai()  != null && item.getSodienthoai().contains(keyword))
                || (item.getTendangnhap()  != null && item.getTendangnhap().toLowerCase().contains(keyword));
            boolean matchTT = tt == null || "Tất cả".equals(tt) || tt.equals(item.getTrangthai());
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
        String tenKH = (selectedItem.getHo() != null ? selectedItem.getHo() : "")
                     + " " + (selectedItem.getTen() != null ? selectedItem.getTen() : "");
        if (!gui.dialog.XacNhanDialog.showDelete(owner, tenKH.trim())) return;
        try {
            khachHangBUS.xoaKhachHang(selectedItem.getMakh());
            ThongBaoDialogHelper.showSuccess(tableView.getScene(), "Đã xóa khách hàng!");
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

    private void openDialog(KhachHang entity) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dialogs/themKhachHang.fxml"));
            Parent root = loader.load();
            // Dùng generic setEntity nếu dialog có (tương tự Them*Dialog pattern)
            Object ctrl = loader.getController();
            try {
                ctrl.getClass().getMethod("setEntity", Object.class).invoke(ctrl, entity);
                ctrl.getClass().getMethod("setOnSaveCallback", Runnable.class).invoke(ctrl, (Runnable) this::loadData);
            } catch (NoSuchMethodException ignored) {}

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
