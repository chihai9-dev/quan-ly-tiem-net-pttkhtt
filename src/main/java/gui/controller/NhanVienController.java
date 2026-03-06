package gui.controller;

import bus.NhanVienBUS;
import entity.NhanVien;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class NhanVienController implements Initializable {

    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cmbChucVu;
    @FXML private Button btnSua;
    @FXML private Button btnXoa;

    @FXML private TableView<NhanVien> tableNhanVien;
    @FXML private TableColumn<NhanVien, String> colMaNV;
    @FXML private TableColumn<NhanVien, String> colHoTen;
    @FXML private TableColumn<NhanVien, String> colChucVu;
    @FXML private TableColumn<NhanVien, String> colTrangThai;

    @FXML private Label lblFormTitle;
    @FXML private TextField txtMaNV;
    @FXML private TextField txtHoTen;
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private ComboBox<String> cmbChucVuForm;

    private NhanVienBUS nhanVienBUS = new NhanVienBUS();
    private ObservableList<NhanVien> listNhanVien = FXCollections.observableArrayList();
    private boolean isInsertMode = true;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTable();
        cmbChucVu.getItems().addAll("TATC", "QUANLY", "NHANVIEN", "THUNGAN");
        cmbChucVu.getSelectionModel().selectFirst();
        cmbChucVuForm.getItems().addAll("QUANLY", "NHANVIEN", "THUNGAN");

        loadData();
    }

    private void setupTable() {
        colMaNV.setCellValueFactory(new PropertyValueFactory<>("manv"));
        // Vì Entity tách Họ và Tên, bạn có thể tự implement lấy fullName nếu cần
        colHoTen.setCellValueFactory(new PropertyValueFactory<>("ten"));
        colChucVu.setCellValueFactory(new PropertyValueFactory<>("chucvu"));
        colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangthai"));
    }

    private void loadData() {
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
            String keyword = txtSearch.getText();
            List<NhanVien> list = nhanVienBUS.timKiemNhanVien(keyword);
            listNhanVien.setAll(list);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi tìm kiếm", e.getMessage());
        }
    }

    @FXML
    private void handleRowSelect() {
        NhanVien selected = tableNhanVien.getSelectionModel().getSelectedItem();
        if (selected != null) {
            isInsertMode = false;
            lblFormTitle.setText("CẬP NHẬT NHÂN VIÊN");
            txtMaNV.setText(selected.getManv());
            txtHoTen.setText(selected.getHo() + " " + selected.getTen());
            txtUsername.setText(selected.getTendangnhap());
            txtPassword.setDisable(true); // Sửa thì không cho nhập pass ở đây
            cmbChucVuForm.setValue(selected.getChucvu());
        }
    }

    @FXML
    private void handleThem() {
        isInsertMode = true;
        lblFormTitle.setText("THÊM NHÂN VIÊN");
        handleCancel(); // Clear form
        txtPassword.setDisable(false);
    }

    @FXML
    private void handleSua() {
        // Logic khi click nút sửa trên thanh công cụ (Focus vào bảng bên form)
        if (tableNhanVien.getSelectionModel().getSelectedItem() == null) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn nhân viên cần sửa!");
        }
    }

    @FXML
    private void handleXoa() {
        NhanVien selected = tableNhanVien.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn nhân viên cần xóa!");
            return;
        }

        try {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Bạn có chắc muốn xóa nhân viên này?", ButtonType.YES, ButtonType.NO);
            if (confirm.showAndWait().get() == ButtonType.YES) {
                if (nhanVienBUS.xoaNhanVien(selected.getManv())) {
                    showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã khóa nhân viên thành công!");
                    loadData();
                    handleCancel();
                }
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi xóa", e.getMessage());
        }
    }

    @FXML
    private void handleSave() {
        try {
            NhanVien nv = new NhanVien();
            // Xử lý tách họ và tên tạm thời (giả sử tên là từ cuối cùng)
            String[] parts = txtHoTen.getText().trim().split(" ");
            nv.setTen(parts[parts.length - 1]);
            nv.setHo(txtHoTen.getText().replace(nv.getTen(), "").trim());

            nv.setChucvu(cmbChucVuForm.getValue());
            nv.setTendangnhap(txtUsername.getText());

            if (isInsertMode) {
                nv.setMatkhau(txtPassword.getText());
                nhanVienBUS.themNhanVien(nv);
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Thêm nhân viên thành công!");
            } else {
                nv.setManv(txtMaNV.getText());
                nhanVienBUS.suaNhanVien(nv);
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Cập nhật nhân viên thành công!");
            }
            loadData();
            handleCancel();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi lưu dữ liệu", e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        txtMaNV.clear();
        txtHoTen.clear();
        txtUsername.clear();
        txtPassword.clear();
        cmbChucVuForm.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}