package gui.controller;

import bus.NhanVienBUS;
import entity.NhanVien;
import gui.dialog.ThemNhanVienDialog;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class NhanVienController implements Initializable {

    // --- CÁC ID KHỚP 100% VỚI NHANVIEN.FXML ---
    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cboChucVu;
    @FXML private ComboBox<String> cboTrangThai;

    @FXML private Button btnThem;
    @FXML private Button btnSua;
    @FXML private Button btnXoa;
    @FXML private Button btnLamMoi;

    @FXML private TableView<NhanVien> tableView;
    @FXML private TableColumn<NhanVien, String> colMaNV, colHoTen, colSDT, colTenDN, colChucVu, colTrangThai, colNgayVaoLam;

    @FXML private Label lblTotal;

    private final NhanVienBUS nhanVienBUS = new NhanVienBUS();
    private final ObservableList<NhanVien> listNhanVien = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTable();
        setupComboBoxes();

        // Lắng nghe sự kiện click vào bảng để bật/tắt nút Sửa, Xóa
        if (tableView != null) {
            tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                boolean hasSelection = (newSelection != null);
                if (btnSua != null) btnSua.setDisable(!hasSelection);
                if (btnXoa != null) btnXoa.setDisable(!hasSelection);
            });
        }

        loadData();
    }

    private void setupTable() {
        if (colMaNV != null) colMaNV.setCellValueFactory(new PropertyValueFactory<>("manv"));

        // Nối cột Họ và Tên lại với nhau cho đẹp
        if (colHoTen != null) {
            colHoTen.setCellValueFactory(cellData -> {
                NhanVien nv = cellData.getValue();
                String hoTen = (nv.getHo() != null ? nv.getHo() + " " : "") + (nv.getTen() != null ? nv.getTen() : "");
                return new SimpleStringProperty(hoTen.trim());
            });
        }

        if (colSDT != null) colSDT.setCellValueFactory(new PropertyValueFactory<>("sodienthoai"));
        if (colTenDN != null) colTenDN.setCellValueFactory(new PropertyValueFactory<>("tendangnhap"));
        if (colChucVu != null) colChucVu.setCellValueFactory(new PropertyValueFactory<>("chucvu"));
        if (colTrangThai != null) colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangthai"));
        if (colNgayVaoLam != null) colNgayVaoLam.setCellValueFactory(new PropertyValueFactory<>("ngayvaolam"));
    }

    private void setupComboBoxes() {
        if (cboChucVu != null) {
            cboChucVu.getItems().addAll("TATC", "QUANLY", "NHANVIEN", "THUNGAN");
            cboChucVu.getSelectionModel().selectFirst();
            // Tự động lọc lại bảng ngay khi bạn chọn chức vụ mới
            cboChucVu.setOnAction(e -> loadData());
        }
        if (cboTrangThai != null) {
            cboTrangThai.getItems().addAll("TATC", "DANGLAMVIEC", "NGHIVIEC");
            cboTrangThai.getSelectionModel().selectFirst();
            // Tự động lọc lại bảng ngay khi bạn chọn trạng thái mới
            cboTrangThai.setOnAction(e -> loadData());
        }
    }

    public void loadData() {
        try {
            // 1. Lấy TẤT CẢ nhân viên bằng cách gộp 2 danh sách lại
            List<NhanVien> listAll = new java.util.ArrayList<>();
            listAll.addAll(nhanVienBUS.getAllNhanVienDangLamViec());
            listAll.addAll(nhanVienBUS.getAllNhanVienDaNghiViec());

            // 2. Lấy giá trị từ các bộ lọc trên giao diện
            String chucVu = cboChucVu != null ? cboChucVu.getValue() : "TATC";
            String trangThai = cboTrangThai != null ? cboTrangThai.getValue() : "TATC";
            String keyword = txtSearch != null ? txtSearch.getText().trim().toLowerCase() : "";

            // 3. Tiến hành lọc dữ liệu
            List<NhanVien> listFiltered = listAll.stream()
                    .filter(nv -> "TATC".equals(chucVu) || nv.getChucvu().equalsIgnoreCase(chucVu))
                    .filter(nv -> "TATC".equals(trangThai) || nv.getTrangthai().equalsIgnoreCase(trangThai))
                    .filter(nv -> keyword.isEmpty() ||
                            nv.getManv().toLowerCase().contains(keyword) ||
                            nv.getTen().toLowerCase().contains(keyword) ||
                            (nv.getHo() != null && nv.getHo().toLowerCase().contains(keyword)))
                    .collect(java.util.stream.Collectors.toList());

            // 4. Hiển thị lên bảng
            listNhanVien.setAll(listFiltered);
            if (tableView != null) tableView.setItems(listNhanVien);
            if (lblTotal != null) lblTotal.setText("Tổng: " + listNhanVien.size() + " bản ghi");

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi tải dữ liệu", e.getMessage());
        }
    }

    @FXML
    private void handleSearch() {
        // Chỉ cần gọi lại loadData() là xong, vì logic tìm kiếm đã được tích hợp sẵn ở trên
        loadData();
    }

    @FXML
    private void handleLamMoi() {
        if (txtSearch != null) txtSearch.clear();
        if (cboChucVu != null) cboChucVu.getSelectionModel().selectFirst();
        if (cboTrangThai != null) cboTrangThai.getSelectionModel().selectFirst();
        loadData();
    }

    @FXML
    private void handleThem() {
        openDialog(null); // null tức là trạng thái Thêm mới
    }

    @FXML
    private void handleSua() {
        if (tableView == null) return;
        NhanVien selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn nhân viên cần sửa!");
            return;
        }
        openDialog(selected); // Chuyển đối tượng vào để Sửa
    }

    @FXML
    private void handleXoa() {
        if (tableView == null) return;
        NhanVien selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Khóa tài khoản nhân viên này?", ButtonType.YES, ButtonType.NO);
        if (confirm.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            try {
                if (nhanVienBUS.xoaNhanVien(selected.getManv())) {
                    showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã khóa nhân viên thành công!");
                    loadData();
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Lỗi xóa", e.getMessage());
            }
        }
    }

    // --- XỬ LÝ MỞ POPUP (DIALOG) ---
    private void openDialog(NhanVien nv) {
        try {
            // Đã sửa lại đường dẫn chuẩn, loại bỏ chữ src/main/resources
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dialogs/themNhanVien.fxml"));
            Parent root = loader.load();

            ThemNhanVienDialog ctrl = loader.getController();
            ctrl.setEntity(nv);
            ctrl.setOnSaveCallback(this::loadData); // Gọi hàm loadData() tự động làm mới bảng sau khi Lưu

            Stage stage = new Stage(StageStyle.UNDECORATED); // Bỏ thanh viền cửa sổ cho đẹp
            stage.initModality(Modality.APPLICATION_MODAL);
            if (tableView != null && tableView.getScene() != null) {
                stage.initOwner(tableView.getScene().getWindow());
            }
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi mở form", e.getMessage());
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