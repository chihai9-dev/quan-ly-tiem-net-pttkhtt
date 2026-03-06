package gui.controller;

import bus.KhuMayBUS;
import bus.MayTinhBUS;
import entity.KhuMay;
import entity.MayTinh;
import gui.dialog.ThemMayTinhDialog;
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

public class MayTinhController implements Initializable {

    @FXML private TableView<MayTinh> tableView;
    @FXML private TableColumn<MayTinh, String> colMa;
    @FXML private TableColumn<MayTinh, String> colTen;
    @FXML private TableColumn<MayTinh, String> colKhu;
    @FXML private TableColumn<MayTinh, String> colCauHinh;
    @FXML private TableColumn<MayTinh, Double> colGiaMoiGio;
    @FXML private TableColumn<MayTinh, String> colTrangThai;

    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cboTrangThai;
    @FXML private ComboBox<String> cboKhu;
    @FXML private Label lblSubtitle;
    @FXML private Label lblTotal;
    @FXML private Button btnSua;
    @FXML private Button btnXoa;

    private final MayTinhBUS mayTinhBUS = new MayTinhBUS();
    private final KhuMayBUS khuMayBUS   = new KhuMayBUS();
    private ObservableList<MayTinh> dataList = FXCollections.observableArrayList();
    private FilteredList<MayTinh> filteredList;
    private MayTinh selectedItem;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        setupTableSelection();
        if (cboTrangThai != null) {
            cboTrangThai.getItems().setAll("Tất cả", "TRONG", "DANGDUNG", "BAOTRI", "TATMAY");
            cboTrangThai.setValue("Tất cả");
            cboTrangThai.setOnAction(e -> applyFilter());
        }
        loadKhuCombo();
        loadData();
    }

    private void loadKhuCombo() {
        if (cboKhu == null) return;
        try {
            List<KhuMay> khuList = khuMayBUS.getAllKhuMay();
            cboKhu.getItems().clear();
            cboKhu.getItems().add("Tất cả");
            khuList.forEach(k -> cboKhu.getItems().add(k.getMakhu()));
            cboKhu.setValue("Tất cả");
            cboKhu.setOnAction(e -> applyFilter());
        } catch (Exception ignored) {}
    }

    private void setupTableColumns() {
        if (colMa       != null) colMa.setCellValueFactory(new PropertyValueFactory<>("mamay"));
        if (colTen      != null) colTen.setCellValueFactory(new PropertyValueFactory<>("tenmay"));
        if (colKhu      != null) colKhu.setCellValueFactory(new PropertyValueFactory<>("makhu"));
        if (colCauHinh  != null) colCauHinh.setCellValueFactory(new PropertyValueFactory<>("cauhinh"));
        if (colGiaMoiGio!= null) {
            colGiaMoiGio.setCellValueFactory(new PropertyValueFactory<>("giamoigio"));
            colGiaMoiGio.setCellFactory(col -> new TableCell<>() {
                @Override protected void updateItem(Double v, boolean empty) {
                    super.updateItem(v, empty);
                    setText(empty || v == null ? null : String.format("%,.0f ₫/giờ", v));
                }
            });
        }
        if (colTrangThai!= null) colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangthai"));
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
            List<MayTinh> list = mayTinhBUS.getAllMayTinh();
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
        String keyword = txtSearch   != null ? txtSearch.getText().toLowerCase().trim() : "";
        String tt      = cboTrangThai!= null ? cboTrangThai.getValue() : "Tất cả";
        String khu     = cboKhu      != null ? cboKhu.getValue() : "Tất cả";
        if (filteredList == null) return;
        filteredList.setPredicate(item -> {
            boolean matchKw = keyword.isEmpty()
                || (item.getMamay()  != null && item.getMamay().toLowerCase().contains(keyword))
                || (item.getTenmay() != null && item.getTenmay().toLowerCase().contains(keyword));
            boolean matchTT  = tt  == null || "Tất cả".equals(tt)  || tt.equals(item.getTrangthai());
            boolean matchKhu = khu == null || "Tất cả".equals(khu) || khu.equals(item.getMakhu());
            return matchKw && matchTT && matchKhu;
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
        if (!gui.dialog.XacNhanDialog.showDelete(owner, selectedItem.getTenmay())) return;
        try {
            mayTinhBUS.xoaMayTinh(selectedItem.getMamay());
            ThongBaoDialogHelper.showSuccess(tableView.getScene(), "Đã xóa máy tính!");
            loadData();
        } catch (Exception e) {
            ThongBaoDialogHelper.showError(tableView.getScene(), "Lỗi xóa: " + e.getMessage());
        }
    }

    @FXML
    public void handleLamMoi() {
        if (txtSearch    != null) txtSearch.clear();
        if (cboTrangThai != null) cboTrangThai.setValue("Tất cả");
        if (cboKhu       != null) cboKhu.setValue("Tất cả");
        loadData();
    }

    private void openDialog(MayTinh entity) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dialogs/themMayTinh.fxml"));
            Parent root = loader.load();
            ThemMayTinhDialog ctrl = loader.getController();
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
