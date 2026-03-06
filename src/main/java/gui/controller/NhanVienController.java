package gui.controller;

import bus.NhanVienBUS;
import entity.NhanVien;
import gui.dialog.ThemNhanVienDialog;
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

public class NhanVienController implements Initializable {

    @FXML private TableView<NhanVien> tableView;
    @FXML private TableColumn<NhanVien, String> colMa;
    @FXML private TableColumn<NhanVien, String> colHo;
    @FXML private TableColumn<NhanVien, String> colTen;
    @FXML private TableColumn<NhanVien, String> colChucVu;
    @FXML private TableColumn<NhanVien, String> colTDN;
    @FXML private TableColumn<NhanVien, String> colTrangThai;

    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cboChucVu;
    @FXML private ComboBox<String> cboTrangThai;
    @FXML private Label lblSubtitle;
    @FXML private Label lblTotal;
    @FXML private Button btnSua;
    @FXML private Button btnXoa;

    private final NhanVienBUS nhanVienBUS = new NhanVienBUS();
    private ObservableList<NhanVien> dataList = FXCollections.observableArrayList();
    private FilteredList<NhanVien> filteredList;
    private NhanVien selectedItem;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        setupTableSelection();
        if (cboChucVu != null) {
            cboChucVu.getItems().setAll("Tất cả", "QUANLY", "NHANVIEN", "THUNGAN");
            cboChucVu.setValue("Tất cả");
            cboChucVu.setOnAction(e -> applyFilter());
        }
        if (cboTrangThai != null) {
            cboTrangThai.getItems().setAll("Tất cả", "DANGLAMVIEC", "NGHIVIEC");
            cboTrangThai.setValue("Tất cả");
            cboTrangThai.setOnAction(e -> applyFilter());
        }
        loadData();
    }

    private void setupTableColumns() {
        if (colMa       != null) colMa.setCellValueFactory(new PropertyValueFactory<>("manv"));
        if (colHo       != null) colHo.setCellValueFactory(new PropertyValueFactory<>("ho"));
        if (colTen      != null) colTen.setCellValueFactory(new PropertyValueFactory<>("ten"));
        if (colChucVu   != null) colChucVu.setCellValueFactory(new PropertyValueFactory<>("chucvu"));
        if (colTDN      != null) colTDN.setCellValueFactory(new PropertyValueFactory<>("tendangnhap"));
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
            // Lấy tất cả: đang làm việc + đã nghỉ
            List<NhanVien> active  = nhanVienBUS.getAllNhanVienDangLamViec();
            List<NhanVien> nghiviec = nhanVienBUS.getAllNhanVienDaNghiViec();
            dataList.clear();
            dataList.addAll(active);
            dataList.addAll(nghiviec);
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
        String keyword = txtSearch    != null ? txtSearch.getText().toLowerCase().trim() : "";
        String cv      = cboChucVu   != null ? cboChucVu.getValue() : "Tất cả";
        String tt      = cboTrangThai!= null ? cboTrangThai.getValue() : "Tất cả";
        if (filteredList == null) return;
        filteredList.setPredicate(item -> {
            boolean matchKw = keyword.isEmpty()
                || (item.getManv()        != null && item.getManv().toLowerCase().contains(keyword))
                || (item.getHo()          != null && item.getHo().toLowerCase().contains(keyword))
                || (item.getTen()         != null && item.getTen().toLowerCase().contains(keyword))
                || (item.getTendangnhap() != null && item.getTendangnhap().toLowerCase().contains(keyword));
            boolean matchCV = cv == null || "Tất cả".equals(cv) || cv.equals(item.getChucvu());
            boolean matchTT = tt == null || "Tất cả".equals(tt) || tt.equals(item.getTrangthai());
            return matchKw && matchCV && matchTT;
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
        String tenNV = (selectedItem.getHo() != null ? selectedItem.getHo() : "")
                     + " " + (selectedItem.getTen() != null ? selectedItem.getTen() : "");
        if (!gui.dialog.XacNhanDialog.showDelete(owner, tenNV.trim())) return;
        try {
            nhanVienBUS.xoaNhanVien(selectedItem.getManv());
            ThongBaoDialogHelper.showSuccess(tableView.getScene(), "Đã xóa nhân viên!");
            loadData();
        } catch (Exception e) {
            ThongBaoDialogHelper.showError(tableView.getScene(), "Lỗi xóa: " + e.getMessage());
        }
    }

    @FXML
    public void handleLamMoi() {
        if (txtSearch    != null) txtSearch.clear();
        if (cboChucVu   != null) cboChucVu.setValue("Tất cả");
        if (cboTrangThai != null) cboTrangThai.setValue("Tất cả");
        loadData();
    }

    private void openDialog(NhanVien entity) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dialogs/themNhanVien.fxml"));
            Parent root = loader.load();
            ThemNhanVienDialog ctrl = loader.getController();
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
