package gui.controller;

import bus.NhanVienBUS;
import entity.NhanVien;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class NhanVienController implements Initializable {

    @FXML private TextField txtTimKiem; // Đổi từ txtSearch
    @FXML private ComboBox<String> cmbChucVu;

    @FXML private TableView<NhanVien> tableNhanVien;
    @FXML private TableColumn<NhanVien, String> colMaNV, colHoTen, colChucVu, colTrangThai;

    private final NhanVienBUS nhanVienBUS = new NhanVienBUS();
    private final ObservableList<NhanVien> listNhanVien = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTable();
        cmbChucVu.getItems().addAll("TATC", "QUANLY", "NHANVIEN", "THUNGAN");
        cmbChucVu.getSelectionModel().selectFirst();
        loadData();
    }

    private void setupTable() {
        colMaNV.setCellValueFactory(new PropertyValueFactory<>("manv"));
        colHoTen.setCellValueFactory(new PropertyValueFactory<>("ten"));
        colChucVu.setCellValueFactory(new PropertyValueFactory<>("chucvu"));
        colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangthai"));
    }

    public void loadData() {
        try {
            List<NhanVien> list = nhanVienBUS.getAllNhanVienDangLamViec();
            listNhanVien.setAll(list);
            tableNhanVien.setItems(listNhanVien);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi tải dữ liệu", e.getMessage());
        }
    }

    @FXML
    private void handleSearch() {
        try {
            List<NhanVien> list = nhanVienBUS.timKiemNhanVien(txtTimKiem.getText());
            listNhanVien.setAll(list);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi tìm kiếm", e.getMessage());
        }
    }

    @FXML
    private void handleThem() {
        openDialog(null); // Truyền null để báo là thêm mới
    }

    @FXML
    private void handleSua() {
        NhanVien selected = tableNhanVien.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn nhân viên cần sửa!");
            return;
        }
        openDialog(selected); // Truyền object để sửa
    }

    @FXML
    private void handleXoa() {
        NhanVien selected = tableNhanVien.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Khóa tài khoản nhân viên này?", ButtonType.YES, ButtonType.NO);
        if (confirm.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            try {
                if (nhanVienBUS.xoaNhanVien(selected.getManv())) {
                    showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã khóa nhân viên!");
                    loadData();
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Lỗi xóa", e.getMessage());
            }
        }
    }

    private void openDialog(NhanVien nv) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/view/dialogs/themNhanVien.fxml"));
            Parent root = loader.load();

            ThemNhanVienDialog controller = loader.getController();
            controller.setNhanVien(nv); // Truyền dữ liệu
            controller.setParentController(this); // Để gọi lại hàm loadData() sau khi lưu xong

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(nv == null ? "Thêm Nhân Viên" : "Sửa Nhân Viên");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi mở Form", e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}