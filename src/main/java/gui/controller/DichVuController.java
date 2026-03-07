package gui.controller;

import bus.DichVuBUS;
import entity.DichVu;
import gui.dialog.ThongBaoDialog;
import gui.dialog.XacNhanDialog;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DichVuController implements Initializable {

    // === TABLE ===
    @FXML private TableView<DichVu> tableView;
    @FXML private TableColumn<DichVu, String>  colMaDV, colTenDV, colDonVi, colTrangThai;
    @FXML private TableColumn<DichVu, Double>  colGia;
    @FXML private TableColumn<DichVu, Integer> colTonKho;

    // === TOOLBAR ===
    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cboTrangThai;
    @FXML private Button btnThem, btnSua, btnXoa, btnKhoiPhuc, btnLamMoi;

    // === FORM ===
    @FXML private Label lblSubtitle, lblTotal;
    @FXML private TextField txtMaDV, txtTenDV, txtGia, txtDonVi, txtTonKho;
    @FXML private ComboBox<String> cboLoaiDV;
    @FXML private Button btnLuu, btnHuy;

    private final DichVuBUS dichVuBUS = new DichVuBUS();
    private ObservableList<DichVu> dataList = FXCollections.observableArrayList();
    private FilteredList<DichVu> filteredList;

    // Trạng thái form: NONE | THEM | SUA
    private enum FormMode { NONE, THEM, SUA }
    private FormMode formMode = FormMode.NONE;
    private DichVu selectedDV = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        setupTableRowColors();
        setupComboBoxes();
        lockFormFields();
        setFormVisible(false);
        loadData();
    }

    @FXML
    public void handleRowSelect() {
        DichVu n = tableView.getSelectionModel().getSelectedItem();
        if (n == null || formMode == FormMode.THEM) return;
        selectedDV = n;
        formMode = FormMode.SUA;
        fillForm(n);
        setEditableForm(true);
        txtMaDV.setEditable(false);
        txtTonKho.setEditable(false);
        btnSua.setVisible(false);
        btnLuu.setVisible(true);
        btnHuy.setVisible(true);
        btnXoa.setDisable(false);
        btnKhoiPhuc.setDisable(false);
    }

    private void setupTableColumns() {
        colMaDV.setCellValueFactory(new PropertyValueFactory<>("madv"));
        colTenDV.setCellValueFactory(new PropertyValueFactory<>("tendv"));
        colGia.setCellValueFactory(new PropertyValueFactory<>("dongia"));
        colTonKho.setCellValueFactory(new PropertyValueFactory<>("soluongton"));
        colDonVi.setCellValueFactory(new PropertyValueFactory<>("donvitinh"));
        colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangthai"));
    }

    private void setupTableRowColors() {
        tableView.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(DichVu item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                    return;
                }
                // Khi đang được chọn thì dùng màu đậm hơn, chữ trắng rõ ràng
                if (isSelected()) {
                    switch (item.getTrangthai()) {
                        case "CONHANG"  -> setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white;");
                        case "HETHANG"  -> setStyle("-fx-background-color: #b71c1c; -fx-text-fill: white;");
                        case "NGUNGBAN" -> setStyle("-fx-background-color: #546e7a; -fx-text-fill: white;");
                        default         -> setStyle("-fx-background-color: #1565c0; -fx-text-fill: white;");
                    }
                } else {
                    switch (item.getTrangthai()) {
                        case "CONHANG"  -> setStyle("-fx-background-color: #c8e6c9; -fx-text-fill: #1b5e20;");
                        case "HETHANG"  -> setStyle("-fx-background-color: #ef9a9a; -fx-text-fill: #7f0000;");
                        case "NGUNGBAN" -> setStyle("-fx-background-color: #b0bec5; -fx-text-fill: #212121;");
                        default         -> setStyle("");
                    }
                }
            }
        });

        // Buộc row cập nhật màu khi selection thay đổi
        tableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, o, n) -> tableView.refresh()
        );
    }

    private void setupComboBoxes() {
        if (cboTrangThai != null) {
            cboTrangThai.getItems().setAll("Tất cả", "CONHANG", "HETHANG", "NGUNGBAN");
            cboTrangThai.setValue("Tất cả");
            cboTrangThai.setOnAction(e -> handleSearch());
        }
        if (cboLoaiDV != null) {
            cboLoaiDV.getItems().setAll("DOUONG", "THUCPHAM", "KHAC");
        }
    }

    private void lockFormFields() {
        if (txtMaDV != null) {
            txtMaDV.setEditable(false);
            txtMaDV.setStyle("-fx-opacity: 0.7; -fx-background-color: #f0f0f0;");
        }
        if (txtTonKho != null) {
            txtTonKho.setEditable(false);
            txtTonKho.setStyle("-fx-opacity: 0.7; -fx-background-color: #f0f0f0;");
        }
    }

    @FXML
    public void loadData() {
        try {
            List<DichVu> list = dichVuBUS.getAll();
            dataList.setAll(list);
            filteredList = new FilteredList<>(dataList, p -> true);
            tableView.setItems(filteredList);
            if (lblTotal != null) lblTotal.setText("Tổng: " + list.size() + " bản ghi");
            if (lblSubtitle != null) lblSubtitle.setText("Có " + list.size() + " dịch vụ");
            // Reapply bộ lọc tìm kiếm hiện tại sau khi load lại
            handleSearch();
        } catch (Exception e) {
            showError("Lỗi tải dữ liệu: " + e.getMessage());
        }
    }

    @FXML
    public void handleSearch() {
        String keyword = txtSearch != null ? txtSearch.getText().toLowerCase().trim() : "";
        String status  = cboTrangThai != null ? cboTrangThai.getValue() : "Tất cả";

        if (filteredList != null) {
            filteredList.setPredicate(dv -> {
                boolean matchKeyword = keyword.isEmpty()
                        || dv.getTendv().toLowerCase().contains(keyword)
                        || dv.getMadv().toLowerCase().contains(keyword);
                boolean matchStatus = "Tất cả".equals(status) || dv.getTrangthai().equals(status);
                return matchKeyword && matchStatus;
            });
        }
    }

    @FXML
    public void handleThem() {
        formMode = FormMode.THEM;
        clearForm();
        txtTonKho.setText("0");
        setEditableForm(true);
        txtMaDV.setEditable(false);
        txtTonKho.setEditable(false);
        // Chế độ thêm: ẩn nút Sửa, hiện Lưu/Hủy
        btnSua.setVisible(false);
        btnLuu.setVisible(true);
        btnHuy.setVisible(true);
        btnXoa.setDisable(true);
        btnKhoiPhuc.setDisable(true);
        tableView.getSelectionModel().clearSelection();
    }

    @FXML
    public void handleSua() {
        // Không dùng nữa - giữ lại để FXML không báo lỗi nếu còn bind
    }

    @FXML
    public void handleXoa() {
        if (selectedDV == null) return;
        Stage owner = (Stage) tableView.getScene().getWindow();
        boolean ok = XacNhanDialog.showDelete(owner, selectedDV.getTendv());
        if (!ok) return;
        try {
            dichVuBUS.xoaDichVu(selectedDV.getMadv());
            ThongBaoDialog.showSuccess(owner, "Đã chuyển dịch vụ sang trạng thái NGƯNG BÁN.");
            resetForm();
            loadData();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    public void handleKhoiPhuc() {
        if (selectedDV == null) return;
        Stage owner = (Stage) tableView.getScene().getWindow();
        boolean ok = XacNhanDialog.show(owner, "Khôi phục", "Khôi phục dịch vụ: " + selectedDV.getTendv() + "?");
        if (!ok) return;
        try {
            dichVuBUS.khoiPhucLaiDichVu(selectedDV.getMadv());
            ThongBaoDialog.showSuccess(owner, "Đã khôi phục dịch vụ thành công.");
            resetForm();
            loadData();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    public void handleLuu() {
        Stage owner = (Stage) tableView.getScene().getWindow();
        try {
            DichVu dv = buildFromForm();
            if (formMode == FormMode.THEM) {
                dichVuBUS.themDichVu(dv);
                ThongBaoDialog.showSuccess(owner, "Thêm dịch vụ thành công!");
                resetForm();
                loadData();
            } else if (formMode == FormMode.SUA) {
                dichVuBUS.suaDichVu(dv);
                ThongBaoDialog.showSuccess(owner, "Cập nhật dịch vụ thành công!");
                resetForm();
                loadData();
            }
        } catch (Exception e) {
            // Hiện lỗi nhưng KHÔNG reset form, giữ lại dữ liệu người dùng đang nhập
            showError(e.getMessage());
        }
    }

    @FXML
    public void handleHuy() {
        resetForm();
    }

    @FXML
    public void handleLamMoi() {
        if (txtSearch != null) txtSearch.clear();
        if (cboTrangThai != null) cboTrangThai.setValue("Tất cả");
        resetForm();
        loadData();
    }

    // === HELPERS ===

    private void fillForm(DichVu dv) {
        if (txtMaDV   != null) txtMaDV.setText(dv.getMadv());
        if (txtTenDV  != null) txtTenDV.setText(dv.getTendv());
        if (txtGia    != null) txtGia.setText(String.valueOf(dv.getDongia()));
        if (txtDonVi  != null) txtDonVi.setText(dv.getDonvitinh());
        if (txtTonKho != null) txtTonKho.setText(String.valueOf(dv.getSoluongton()));
        if (cboLoaiDV != null) cboLoaiDV.setValue(dv.getLoaidv());
    }

    private void clearForm() {
        if (txtMaDV   != null) txtMaDV.clear();
        if (txtTenDV  != null) txtTenDV.clear();
        if (txtGia    != null) txtGia.clear();
        if (txtDonVi  != null) txtDonVi.clear();
        if (txtTonKho != null) txtTonKho.setText("0");
        if (cboLoaiDV != null) cboLoaiDV.setValue(null);
    }

    private DichVu buildFromForm() throws Exception {
        String ten   = txtTenDV  != null ? txtTenDV.getText().trim()  : "";
        String donVi = txtDonVi  != null ? txtDonVi.getText().trim()  : "";
        String loai  = cboLoaiDV != null ? cboLoaiDV.getValue()       : null;
        double gia;
        try {
            gia = Double.parseDouble(txtGia != null ? txtGia.getText().trim() : "0");
        } catch (NumberFormatException e) {
            throw new Exception("Đơn giá phải là số hợp lệ!");
        }
        String maDV = formMode == FormMode.SUA && selectedDV != null ? selectedDV.getMadv() : "";
        DichVu dv = new DichVu();
        dv.setMadv(maDV);
        dv.setTendv(ten);
        dv.setLoaidv(loai);
        dv.setDongia(gia);
        dv.setDonvitinh(donVi);
        dv.setSoluongton(0);
        dv.setTrangthai("HETHANG");
        return dv;
    }

    private void setFormVisible(boolean visible) {
        if (btnLuu != null) btnLuu.setVisible(visible);
        if (btnHuy != null) btnHuy.setVisible(visible);
    }

    private void setEditableForm(boolean editable) {
        if (txtTenDV  != null) txtTenDV.setEditable(editable);
        if (txtGia    != null) txtGia.setEditable(editable);
        if (txtDonVi  != null) txtDonVi.setEditable(editable);
        if (cboLoaiDV != null) cboLoaiDV.setDisable(!editable);
    }

    private void resetForm() {
        formMode = FormMode.NONE;
        selectedDV = null;
        clearForm();
        setFormVisible(false);
        setEditableForm(false);
        if (btnSua != null) btnSua.setVisible(false);
        if (btnXoa != null) btnXoa.setDisable(true);
        if (btnKhoiPhuc != null) btnKhoiPhuc.setDisable(true);
        tableView.getSelectionModel().clearSelection();
    }

    private void showError(String msg) {
        Stage owner = tableView != null && tableView.getScene() != null
                ? (Stage) tableView.getScene().getWindow() : null;
        ThongBaoDialog.showError(owner, msg);
    }
}