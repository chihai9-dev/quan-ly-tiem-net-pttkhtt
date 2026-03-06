package gui.controller;

import bus.GoiDichVuBUS;
import entity.GoiDichVu;
import gui.dialog.ThemGoiDichVuDialog;
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

public class GoiDichVuController implements Initializable {

    @FXML private TableView<GoiDichVu> tableView;
    @FXML private TableColumn<GoiDichVu, String> colMa;
    @FXML private TableColumn<GoiDichVu, String> colTen;
    @FXML private TableColumn<GoiDichVu, String> colLoai;
    @FXML private TableColumn<GoiDichVu, Double> colSoGio;
    @FXML private TableColumn<GoiDichVu, Integer> colNgayHL;
    @FXML private TableColumn<GoiDichVu, Double> colGiaGoc;
    @FXML private TableColumn<GoiDichVu, Double> colGiaGoi;
    @FXML private TableColumn<GoiDichVu, String> colKhu;
    @FXML private TableColumn<GoiDichVu, String> colTrangThai;

    @FXML private TextField txtSearch;
    @FXML private Label lblSubtitle;
    @FXML private Label lblTotal;
    @FXML private Button btnSua;
    @FXML private Button btnXoa;

    private final GoiDichVuBUS goiDichVuBUS = new GoiDichVuBUS();
    private ObservableList<GoiDichVu> dataList = FXCollections.observableArrayList();
    private FilteredList<GoiDichVu> filteredList;
    private GoiDichVu selectedItem;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        setupTableSelection();
        loadData();
    }

    private void setupTableColumns() {
        if (colMa       != null) colMa.setCellValueFactory(new PropertyValueFactory<>("maGoi"));
        if (colTen      != null) colTen.setCellValueFactory(new PropertyValueFactory<>("tenGoi"));
        if (colLoai     != null) colLoai.setCellValueFactory(new PropertyValueFactory<>("loaiGoi"));
        if (colSoGio    != null) colSoGio.setCellValueFactory(new PropertyValueFactory<>("soGio"));
        if (colNgayHL   != null) colNgayHL.setCellValueFactory(new PropertyValueFactory<>("soNgayHieuLuc"));
        if (colGiaGoc   != null) {
            colGiaGoc.setCellValueFactory(new PropertyValueFactory<>("giaGoc"));
            colGiaGoc.setCellFactory(col -> new TableCell<>() {
                @Override protected void updateItem(Double v, boolean empty) {
                    super.updateItem(v, empty);
                    setText(empty || v == null ? null : String.format("%,.0f ₫", v));
                }
            });
        }
        if (colGiaGoi   != null) {
            colGiaGoi.setCellValueFactory(new PropertyValueFactory<>("giaGoi"));
            colGiaGoi.setCellFactory(col -> new TableCell<>() {
                @Override protected void updateItem(Double v, boolean empty) {
                    super.updateItem(v, empty);
                    setText(empty || v == null ? null : String.format("%,.0f ₫", v));
                }
            });
        }
        if (colKhu      != null) colKhu.setCellValueFactory(new PropertyValueFactory<>("apDungChoKhu"));
        if (colTrangThai!= null) colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
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
            List<GoiDichVu> list = goiDichVuBUS.getDanhSachGoiDV();
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
            || item.getMaGoi().toLowerCase().contains(keyword)
            || item.getTenGoi().toLowerCase().contains(keyword));
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
        if (!gui.dialog.XacNhanDialog.showDelete(owner, selectedItem.getTengoi())) return;
        try {
            goiDichVuBUS.xoaGoiDichVu(selectedItem.getMagoi());
            ThongBaoDialogHelper.showSuccess(tableView.getScene(), "Đã xóa gói dịch vụ!");
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

    private void openDialog(GoiDichVu entity) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dialogs/themGoiDichVu.fxml"));
            Parent root = loader.load();
            ThemGoiDichVuDialog ctrl = loader.getController();
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
