package gui.controller;

import bus.KhuMayBUS;
import entity.KhuMay;
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

public class KhuMayController implements Initializable {

    @FXML private TableView<KhuMay> tableView;
    @FXML private TableColumn<KhuMay, String>  colMa;
    @FXML private TableColumn<KhuMay, String>  colTen;
    @FXML private TableColumn<KhuMay, Double>  colGiaCoso;
    @FXML private TableColumn<KhuMay, Integer> colSoMayToiDa;
    @FXML private TableColumn<KhuMay, String>  colTrangThai;

    @FXML private TextField txtSearch;
    @FXML private Label lblSubtitle;
    @FXML private Label lblTotal;
    @FXML private Button btnSua;
    @FXML private Button btnXoa;

    private final KhuMayBUS khuMayBUS = new KhuMayBUS();
    private ObservableList<KhuMay> dataList = FXCollections.observableArrayList();
    private FilteredList<KhuMay> filteredList;
    private KhuMay selectedItem;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        setupTableSelection();
        loadData();
    }

    private void setupTableColumns() {
        if (colMa         != null) colMa.setCellValueFactory(new PropertyValueFactory<>("maKhu"));
        if (colTen        != null) colTen.setCellValueFactory(new PropertyValueFactory<>("tenKhu"));
        if (colGiaCoso    != null) {
            colGiaCoso.setCellValueFactory(new PropertyValueFactory<>("giacoso"));
            colGiaCoso.setCellFactory(col -> new TableCell<>() {
                @Override protected void updateItem(Double v, boolean empty) {
                    super.updateItem(v, empty);
                    setText(empty || v == null ? null : String.format("%,.0f ₫/giờ", v));
                }
            });
        }
        if (colSoMayToiDa != null) colSoMayToiDa.setCellValueFactory(new PropertyValueFactory<>("somaytoida"));
        if (colTrangThai  != null) colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangthai"));
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
            List<KhuMay> list = khuMayBUS.getAllKhuMay();
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
        String keyword = txtSearch != null ? txtSearch.getText().toLowerCase().trim() : "";
        if (filteredList == null) return;
        filteredList.setPredicate(item -> keyword.isEmpty()
            || (item.getMaKhu()   != null && item.getMaKhu().toLowerCase().contains(keyword))
            || (item.getTenKhu()  != null && item.getTenKhu().toLowerCase().contains(keyword)));
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
        if (!gui.dialog.XacNhanDialog.showDelete(owner, selectedItem.getTenKhu())) return;
        try {
            khuMayBUS.xoaKhuMay(selectedItem.getMaKhu());
            ThongBaoDialogHelper.showSuccess(tableView.getScene(), "Đã xóa khu máy!");
            loadData();
        } catch (Exception e) {
            ThongBaoDialogHelper.showError(tableView.getScene(), "Lỗi xóa: " + e.getMessage());
        }
    }

    @FXML
    public void handleLamMoi() {
        if (txtSearch != null) txtSearch.clear();
        loadData();
    }

    private void openDialog(KhuMay entity) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dialogs/themKhuMay.fxml"));
            Parent root = loader.load();
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
