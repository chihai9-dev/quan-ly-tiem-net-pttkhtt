package gui.controller;

import bus.NhaCungCapBUS;
import bus.NhapHangBUS;
import dao.DBConnection;
import entity.ChiTietPhieuNhap;
import entity.NhaCungCap;
import entity.PhieuNhapHang;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utils.ThongBaoDialogHelper;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class NhapHangController implements Initializable {

    @FXML private TableView<PhieuNhapHang> tablePhieu;
    @FXML private TableColumn<PhieuNhapHang, String> colMaPhieu;
    @FXML private TableColumn<PhieuNhapHang, String> colNCC;
    @FXML private TableColumn<PhieuNhapHang, String> colNgayNhap;
    @FXML private TableColumn<PhieuNhapHang, Double> colTongTien;
    @FXML private TableColumn<PhieuNhapHang, String> colNguoiTao;
    @FXML private TableColumn<PhieuNhapHang, String> colTrangThai;

    @FXML private TableView<ChiTietPhieuNhap> tableChiTiet;
    @FXML private TableColumn<ChiTietPhieuNhap, String> colCTDV;
    @FXML private TableColumn<ChiTietPhieuNhap, Integer> colCTSL;
    @FXML private TableColumn<ChiTietPhieuNhap, Double> colCTDonGia;
    @FXML private TableColumn<ChiTietPhieuNhap, Double> colCTThanhTien;

    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cboTrangThai;
    @FXML private DatePicker dateFrom;
    @FXML private DatePicker dateTo;
    @FXML private Label lblTotal;

    @FXML private Label lblDetMaPhieu;
    @FXML private Label lblDetNCC;
    @FXML private Label lblDetNgay;
    @FXML private Label lblDetGhiChu;
    @FXML private Label lblDetTongTien;
    @FXML private Label lblTrangThaiPhieu;
    @FXML private Button btnDuyet;
    @FXML private Button btnHuy;

    private final NhapHangBUS nhapHangBUS = new NhapHangBUS();
    private final NhaCungCapBUS nhaCungCapBUS = new NhaCungCapBUS();

    private final ObservableList<PhieuNhapHang> dataList = FXCollections.observableArrayList();
    private PhieuNhapHang selected;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (cboTrangThai != null) {
            cboTrangThai.getItems().setAll("Tất cả", "CHODUYET", "DANHAP", "DAHUY");
            cboTrangThai.setValue("Tất cả");
            cboTrangThai.setOnAction(e -> loadData());
        }

        if (dateFrom != null) dateFrom.setValue(LocalDate.now().withDayOfMonth(1));
        if (dateTo != null) dateTo.setValue(LocalDate.now());

        if (txtSearch != null) {
            txtSearch.setOnAction(e -> loadData());
        }

        setupTableColumns();
        setupTableSelection();
        loadData();
    }

    private void setupTableColumns() {
        if (colMaPhieu != null) {
            colMaPhieu.setCellValueFactory(new PropertyValueFactory<>("maPhieuNhap"));
        }

        if (colNCC != null) {
            colNCC.setCellValueFactory(new PropertyValueFactory<>("maNCC"));
        }

        if (colNgayNhap != null) {
            colNgayNhap.setCellValueFactory(c -> {
                LocalDateTime ngayTime = c.getValue().getNgayNhap();
                String val = ngayTime != null
                        ? ngayTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                        : "";
                return new SimpleStringProperty(val);
            });
        }

        if (colTongTien != null) {
            colTongTien.setCellValueFactory(new PropertyValueFactory<>("tongTien"));
            colTongTien.setCellFactory(col -> new TableCell<>() {
                @Override
                protected void updateItem(Double v, boolean empty) {
                    super.updateItem(v, empty);
                    setText(empty || v == null ? null : String.format("%,.0f ₫", v));
                }
            });
        }

        if (colNguoiTao != null) {
            colNguoiTao.setCellValueFactory(new PropertyValueFactory<>("maNV"));
        }

        if (colTrangThai != null) {
            colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
        }

        if (colCTDV != null) {
            colCTDV.setCellValueFactory(new PropertyValueFactory<>("maDV"));
        }

        if (colCTSL != null) {
            colCTSL.setCellValueFactory(new PropertyValueFactory<>("soLuong"));
        }

        if (colCTDonGia != null) {
            colCTDonGia.setCellValueFactory(new PropertyValueFactory<>("giaNhap"));
            colCTDonGia.setCellFactory(col -> new TableCell<>() {
                @Override
                protected void updateItem(Double v, boolean empty) {
                    super.updateItem(v, empty);
                    setText(empty || v == null ? null : String.format("%,.0f ₫", v));
                }
            });
        }

        if (colCTThanhTien != null) {
            colCTThanhTien.setCellValueFactory(new PropertyValueFactory<>("thanhTien"));
            colCTThanhTien.setCellFactory(col -> new TableCell<>() {
                @Override
                protected void updateItem(Double v, boolean empty) {
                    super.updateItem(v, empty);
                    setText(empty || v == null ? null : String.format("%,.0f ₫", v));
                }
            });
        }
    }

    private void setupTableSelection() {
        if (tablePhieu == null) return;

        tablePhieu.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            selected = n;
            if (n != null) {
                showDetail(n);
                boolean isChoDuyet = "CHODUYET".equals(n.getTrangThai());

                if (btnDuyet != null) btnDuyet.setDisable(!isChoDuyet);
                if (btnHuy != null) btnHuy.setDisable(!isChoDuyet);

                loadChiTiet(n);
            } else {
                if (btnDuyet != null) btnDuyet.setDisable(true);
                if (btnHuy != null) btnHuy.setDisable(true);
                clearDetail();
            }
        });

        if (btnDuyet != null) btnDuyet.setDisable(true);
        if (btnHuy != null) btnHuy.setDisable(true);
    }

    public void loadData() {
        try {
            List<PhieuNhapHang> list = nhapHangBUS.getAllPhieuNhap();

            String keyword = txtSearch != null && txtSearch.getText() != null
                    ? txtSearch.getText().trim().toLowerCase()
                    : "";

            String tt = cboTrangThai != null ? cboTrangThai.getValue() : "Tất cả";
            LocalDate from = dateFrom != null ? dateFrom.getValue() : null;
            LocalDate to = dateTo != null ? dateTo.getValue() : null;

            list = list.stream()
                    .filter(p -> {
                        if (tt != null && !"Tất cả".equals(tt) && !tt.equals(p.getTrangThai())) {
                            return false;
                        }

                        // Có từ khóa thì ưu tiên lọc theo từ khóa, bỏ lọc ngày
                        if (!keyword.isEmpty()) {
                            String maPhieu = safe(p.getMaPhieuNhap()).toLowerCase();
                            String maNCC = safe(p.getMaNCC()).toLowerCase();
                            String maNV = safe(p.getMaNV()).toLowerCase();
                            String trangThai = safe(p.getTrangThai()).toLowerCase();

                            return maPhieu.contains(keyword)
                                    || maNCC.contains(keyword)
                                    || maNV.contains(keyword)
                                    || trangThai.contains(keyword);
                        }

                        if (from != null && to != null) {
                            if (p.getNgayNhap() == null) return false;
                            LocalDate ngay = p.getNgayNhap().toLocalDate();
                            return !ngay.isBefore(from) && !ngay.isAfter(to);
                        }

                        return true;
                    })
                    .collect(Collectors.toList());

            dataList.setAll(list);
            if (tablePhieu != null) tablePhieu.setItems(dataList);
            if (lblTotal != null) lblTotal.setText("Tổng: " + list.size() + " phiếu");
        } catch (Exception e) {
            if (tablePhieu != null) tablePhieu.setItems(FXCollections.observableArrayList());
            if (lblTotal != null) lblTotal.setText("Lỗi: " + e.getMessage());
        }
    }

    @FXML
    public void handleSearch() {
        loadData();
    }

    @FXML
    public void handleFilter() {
        loadData();
    }

    @FXML
    public void handleRefresh() {
        if (txtSearch != null) txtSearch.clear();
        if (cboTrangThai != null) cboTrangThai.setValue("Tất cả");
        if (dateFrom != null) dateFrom.setValue(LocalDate.now().withDayOfMonth(1));
        if (dateTo != null) dateTo.setValue(LocalDate.now());
        loadData();
    }

    @FXML
    public void handleTaoPhieu() {
        try {
            List<NhaCungCap> dsNCC = nhaCungCapBUS.getNhaCungCapHoatDong();
            if (dsNCC == null || dsNCC.isEmpty()) {
                ThongBaoDialogHelper.showError(tablePhieu.getScene(), "Không có nhà cung cấp hoạt động để tạo phiếu.");
                return;
            }

            List<DichVuItem> dsDichVu = getDanhSachDichVuConHang();
            if (dsDichVu.isEmpty()) {
                ThongBaoDialogHelper.showError(tablePhieu.getScene(), "Không có dịch vụ còn hàng để chọn.");
                return;
            }

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Tạo phiếu nhập");
            dialog.setHeaderText("Nhập thông tin phiếu nhập mới");

            ButtonType btnTao = new ButtonType("Tạo phiếu", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(btnTao, ButtonType.CANCEL);

            ComboBox<NhaCungCap> cboNCC = new ComboBox<>();
            cboNCC.setItems(FXCollections.observableArrayList(dsNCC));
            cboNCC.setPrefWidth(340);
            cboNCC.setCellFactory(cb -> new ListCell<>() {
                @Override
                protected void updateItem(NhaCungCap item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "" : item.getMaNCC() + " - " + item.getTenNCC());
                }
            });
            cboNCC.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(NhaCungCap item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "" : item.getMaNCC() + " - " + item.getTenNCC());
                }
            });

            ComboBox<DichVuItem> cboDV = new ComboBox<>();
            cboDV.setItems(FXCollections.observableArrayList(dsDichVu));
            cboDV.setPrefWidth(280);
            cboDV.setCellFactory(cb -> new ListCell<>() {
                @Override
                protected void updateItem(DichVuItem item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "" : item.getMaDV() + " - " + item.getTenDV());
                }
            });
            cboDV.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(DichVuItem item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "" : item.getMaDV() + " - " + item.getTenDV());
                }
            });

            TextField txtSoLuong = new TextField();
            txtSoLuong.setPromptText("Số lượng");
            txtSoLuong.setPrefWidth(90);

            TextField txtGiaNhap = new TextField();
            txtGiaNhap.setPromptText("Giá nhập");
            txtGiaNhap.setPrefWidth(110);

            Label lblGoiY = new Label("Chọn dịch vụ, nhập số lượng và giá nhập rồi bấm Thêm dòng.");
            lblGoiY.setWrapText(true);

            ObservableList<NhapHangRow> rowData = FXCollections.observableArrayList();
            TableView<NhapHangRow> tableTemp = new TableView<>(rowData);
            tableTemp.setPrefHeight(220);

            TableColumn<NhapHangRow, String> c1 = new TableColumn<>("Mã DV");
            c1.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getMaDV()));
            c1.setPrefWidth(90);

            TableColumn<NhapHangRow, String> c2 = new TableColumn<>("Tên dịch vụ");
            c2.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTenDV()));
            c2.setPrefWidth(180);

            TableColumn<NhapHangRow, Number> c3 = new TableColumn<>("SL");
            c3.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getSoLuong()));
            c3.setPrefWidth(60);

            TableColumn<NhapHangRow, Number> c4 = new TableColumn<>("Giá nhập");
            c4.setCellValueFactory(d -> new SimpleDoubleProperty(d.getValue().getGiaNhap()));
            c4.setPrefWidth(110);
            c4.setCellFactory(col -> new TableCell<>() {
                @Override
                protected void updateItem(Number item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : String.format("%,.0f ₫", item.doubleValue()));
                }
            });

            TableColumn<NhapHangRow, Number> c5 = new TableColumn<>("Thành tiền");
            c5.setCellValueFactory(d -> new SimpleDoubleProperty(d.getValue().getThanhTien()));
            c5.setPrefWidth(120);
            c5.setCellFactory(col -> new TableCell<>() {
                @Override
                protected void updateItem(Number item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : String.format("%,.0f ₫", item.doubleValue()));
                }
            });

            tableTemp.getColumns().addAll(c1, c2, c3, c4, c5);

            Button btnThemDong = new Button("Thêm dòng");
            Button btnXoaDong = new Button("Xóa dòng");

            btnThemDong.setOnAction(e -> {
                try {
                    DichVuItem dv = cboDV.getValue();
                    if (dv == null) throw new IllegalArgumentException("Vui lòng chọn dịch vụ.");

                    int soLuong;
                    double giaNhap;

                    try {
                        soLuong = Integer.parseInt(txtSoLuong.getText().trim());
                    } catch (Exception ex) {
                        throw new IllegalArgumentException("Số lượng không hợp lệ.");
                    }

                    try {
                        giaNhap = Double.parseDouble(txtGiaNhap.getText().trim());
                    } catch (Exception ex) {
                        throw new IllegalArgumentException("Giá nhập không hợp lệ.");
                    }

                    if (soLuong <= 0) throw new IllegalArgumentException("Số lượng phải > 0.");
                    if (giaNhap < 0) throw new IllegalArgumentException("Giá nhập phải >= 0.");

                    rowData.add(new NhapHangRow(dv, soLuong, giaNhap));

                    cboDV.setValue(null);
                    txtSoLuong.clear();
                    txtGiaNhap.clear();
                } catch (Exception ex) {
                    ThongBaoDialogHelper.showError(tablePhieu.getScene(), ex.getMessage());
                }
            });

            btnXoaDong.setOnAction(e -> {
                NhapHangRow row = tableTemp.getSelectionModel().getSelectedItem();
                if (row != null) {
                    rowData.remove(row);
                }
            });

            HBox inputRow = new HBox(8, cboDV, txtSoLuong, txtGiaNhap, btnThemDong, btnXoaDong);
            HBox.setHgrow(cboDV, Priority.ALWAYS);

            VBox content = new VBox(10);
            content.setPadding(new Insets(10));
            content.getChildren().addAll(
                    new Label("Nhà cung cấp:"),
                    cboNCC,
                    lblGoiY,
                    inputRow,
                    tableTemp
            );

            dialog.getDialogPane().setContent(content);

            Node okButton = dialog.getDialogPane().lookupButton(btnTao);
            okButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
                try {
                    if (cboNCC.getValue() == null) {
                        throw new IllegalArgumentException("Vui lòng chọn nhà cung cấp.");
                    }
                    if (rowData.isEmpty()) {
                        throw new IllegalArgumentException("Vui lòng thêm ít nhất 1 dòng hàng hóa.");
                    }
                } catch (Exception ex) {
                    event.consume();
                    ThongBaoDialogHelper.showError(tablePhieu.getScene(), ex.getMessage());
                }
            });

            dialog.showAndWait().ifPresent(result -> {
                if (result == btnTao) {
                    try {
                        NhaCungCap ncc = cboNCC.getValue();
                        List<ChiTietPhieuNhap> chiTietList = new ArrayList<>();

                        for (NhapHangRow row : rowData) {
                            ChiTietPhieuNhap ct = new ChiTietPhieuNhap();
                            ct.setMaDV(row.getMaDV());
                            ct.setSoLuong(row.getSoLuong());
                            ct.setGiaNhap(row.getGiaNhap());
                            chiTietList.add(ct);
                        }

                        String maPhieu = nhapHangBUS.taoPhieuNhap(ncc.getMaNCC(), chiTietList);
                        ThongBaoDialogHelper.showSuccess(tablePhieu.getScene(), "Đã tạo phiếu nhập: " + maPhieu);
                        loadData();
                    } catch (Exception ex) {
                        ThongBaoDialogHelper.showError(tablePhieu.getScene(), "Tạo phiếu thất bại: " + ex.getMessage());
                    }
                }
            });

        } catch (Exception e) {
            ThongBaoDialogHelper.showError(tablePhieu.getScene(), "Không mở được form tạo phiếu: " + e.getMessage());
        }
    }

    @FXML
    public void handleDuyetPhieu() {
        if (selected == null) return;

        Stage owner = (Stage) tablePhieu.getScene().getWindow();
        boolean confirmed = gui.dialog.XacNhanDialog.show(
                owner,
                "Duyệt phiếu nhập",
                "Duyệt phiếu " + selected.getMaPhieuNhap() + "?"
        );
        if (!confirmed) return;

        try {
            nhapHangBUS.duyetPhieu(selected.getMaPhieuNhap());
            ThongBaoDialogHelper.showSuccess(tablePhieu.getScene(), "Đã duyệt phiếu nhập!");
            loadData();
        } catch (Exception e) {
            ThongBaoDialogHelper.showError(tablePhieu.getScene(), "Lỗi duyệt: " + e.getMessage());
        }
    }

    @FXML
    public void handleHuyPhieu() {
        if (selected == null) return;

        Stage owner = (Stage) tablePhieu.getScene().getWindow();
        boolean confirmed = gui.dialog.XacNhanDialog.show(
                owner,
                "Hủy phiếu nhập",
                "Hủy phiếu " + selected.getMaPhieuNhap() + "?",
                "Hành động này không thể hoàn tác",
                gui.dialog.XacNhanDialog.Type.DELETE
        );
        if (!confirmed) return;

        try {
            nhapHangBUS.huyPhieu(selected.getMaPhieuNhap());
            ThongBaoDialogHelper.showSuccess(tablePhieu.getScene(), "Đã hủy phiếu nhập!");
            loadData();
        } catch (Exception e) {
            ThongBaoDialogHelper.showError(tablePhieu.getScene(), "Lỗi hủy: " + e.getMessage());
        }
    }

    private void showDetail(PhieuNhapHang phieu) {
        if (lblDetMaPhieu != null) lblDetMaPhieu.setText(safe(phieu.getMaPhieuNhap()));
        if (lblDetNCC != null) lblDetNCC.setText(safe(phieu.getMaNCC()));

        if (lblDetNgay != null) {
            String ngay = phieu.getNgayNhap() != null
                    ? phieu.getNgayNhap().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                    : "-";
            lblDetNgay.setText(ngay);
        }

        if (lblDetGhiChu != null) {
            lblDetGhiChu.setText("Người tạo: " + safe(phieu.getMaNV()));
        }

        if (lblDetTongTien != null) {
            lblDetTongTien.setText(String.format("%,.0f ₫", phieu.getTongTien()));
        }

        if (lblTrangThaiPhieu != null) {
            updateTrangThaiStyle(phieu.getTrangThai());
        }
    }

    private void updateTrangThaiStyle(String tt) {
        if (lblTrangThaiPhieu == null) return;

        lblTrangThaiPhieu.setText(tt);
        String style = switch (tt != null ? tt : "") {
            case "CHODUYET" -> "-fx-background-color:#FFF9C4; -fx-text-fill:#F57F17;";
            case "DANHAP" -> "-fx-background-color:#C8E6C9; -fx-text-fill:#1B5E20;";
            case "DAHUY" -> "-fx-background-color:#FFCDD2; -fx-text-fill:#B71C1C;";
            default -> "";
        };
        lblTrangThaiPhieu.setStyle(style + "-fx-background-radius:12; -fx-padding:3 10;");
    }

    private void loadChiTiet(PhieuNhapHang phieu) {
        if (tableChiTiet == null) return;

        try {
            List<ChiTietPhieuNhap> ctList = nhapHangBUS.getChiTiet(phieu.getMaPhieuNhap());
            tableChiTiet.setItems(FXCollections.observableArrayList(ctList));
        } catch (Exception ignored) {
            tableChiTiet.setItems(FXCollections.observableArrayList());
        }
    }

    private void clearDetail() {
        if (lblDetMaPhieu != null) lblDetMaPhieu.setText("-");
        if (lblDetNCC != null) lblDetNCC.setText("-");
        if (lblDetNgay != null) lblDetNgay.setText("-");
        if (lblDetGhiChu != null) lblDetGhiChu.setText("-");
        if (lblDetTongTien != null) lblDetTongTien.setText("0 ₫");
        if (lblTrangThaiPhieu != null) {
            lblTrangThaiPhieu.setText("");
            lblTrangThaiPhieu.setStyle("");
        }
        if (tableChiTiet != null) {
            tableChiTiet.setItems(FXCollections.observableArrayList());
        }
    }

    private String safe(String s) {
        return s == null ? "-" : s;
    }

    private List<DichVuItem> getDanhSachDichVuConHang() throws Exception {
        String sql = "SELECT MaDV, TenDV, DonGia, DonViTinh, SoLuongTon, TrangThai " +
                "FROM dichvu WHERE TrangThai = 'CONHANG' ORDER BY MaDV";

        List<DichVuItem> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new DichVuItem(
                        rs.getString("MaDV"),
                        rs.getString("TenDV"),
                        rs.getDouble("DonGia"),
                        rs.getString("DonViTinh"),
                        rs.getInt("SoLuongTon"),
                        rs.getString("TrangThai")
                ));
            }
        } catch (Exception e) {
            throw new Exception("Lỗi lấy danh sách dịch vụ: " + e.getMessage());
        }

        return list;
    }

    private static class DichVuItem {
        private final String maDV;
        private final String tenDV;
        private final double donGia;
        private final String donViTinh;
        private final int soLuongTon;
        private final String trangThai;

        public DichVuItem(String maDV, String tenDV, double donGia, String donViTinh, int soLuongTon, String trangThai) {
            this.maDV = maDV;
            this.tenDV = tenDV;
            this.donGia = donGia;
            this.donViTinh = donViTinh;
            this.soLuongTon = soLuongTon;
            this.trangThai = trangThai;
        }

        public String getMaDV() {
            return maDV;
        }

        public String getTenDV() {
            return tenDV;
        }

        public double getDonGia() {
            return donGia;
        }

        public String getDonViTinh() {
            return donViTinh;
        }

        public int getSoLuongTon() {
            return soLuongTon;
        }

        public String getTrangThai() {
            return trangThai;
        }

        @Override
        public String toString() {
            return maDV + " - " + tenDV;
        }
    }

    private static class NhapHangRow {
        private final DichVuItem dichVu;
        private final int soLuong;
        private final double giaNhap;

        public NhapHangRow(DichVuItem dichVu, int soLuong, double giaNhap) {
            this.dichVu = dichVu;
            this.soLuong = soLuong;
            this.giaNhap = giaNhap;
        }

        public String getMaDV() {
            return dichVu != null ? dichVu.getMaDV() : "";
        }

        public String getTenDV() {
            return dichVu != null ? dichVu.getTenDV() : "";
        }

        public int getSoLuong() {
            return soLuong;
        }

        public double getGiaNhap() {
            return giaNhap;
        }

        public double getThanhTien() {
            return soLuong * giaNhap;
        }
    }
}