package gui.controller;

import bus.HoaDonBUS;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import utils.HoaDonExporter;
import utils.ThongBaoDialogHelper;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class HoaDonController implements Initializable {

    @FXML private TableView<HoaDon> tableView;
    @FXML private TableColumn<HoaDon, String> colMaHD;
    @FXML private TableColumn<HoaDon, String> colKhachHang;
    @FXML private TableColumn<HoaDon, String> colMaMay;
    @FXML private TableColumn<HoaDon, String> colNhanVien;
    @FXML private TableColumn<HoaDon, String> colNgayHD;
    @FXML private TableColumn<HoaDon, String> colTongTien;
    @FXML private TableColumn<HoaDon, String> colTrangThai;

    @FXML private DatePicker dateFrom;
    @FXML private DatePicker dateTo;
    @FXML private TextField  txtSearch;
    @FXML private Button     btnXuatPDF;
    @FXML private Label      lblTotal;
    @FXML private Label      lblTongTienAll;

    @FXML private VBox   vboxDetail;
    @FXML private Label  lblNoSelection;
    @FXML private Label  lblDetMaHD;
    @FXML private Label  lblDetKH;
    @FXML private Label  lblDetMay;
    @FXML private Label  lblDetThoiGian;
    @FXML private Label  lblDetTienMay;
    @FXML private Label  lblDetTongTien;
    @FXML private Label  lblDetGiamGia;
    @FXML private Label  lblDetThanhToan;
    @FXML private Button btnXacNhanThanhToan;  // nút mới

    @FXML private TableView<ChiTietHoaDon>           tableChiTiet;
    @FXML private TableColumn<ChiTietHoaDon, String> colCTDV;
    @FXML private TableColumn<ChiTietHoaDon, String> colCTSL;
    @FXML private TableColumn<ChiTietHoaDon, String> colCTDonGia;
    @FXML private TableColumn<ChiTietHoaDon, String> colCTThanhTien;

    private final HoaDonBUS hoaDonBUS = new HoaDonBUS();
    private final ObservableList<HoaDon> dataList = FXCollections.observableArrayList();
    private SortedList<HoaDon> sortedList ;
    private List<ChiTietHoaDon> currentChiTiet;
    private HoaDon selectedHD;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (dateFrom != null) dateFrom.setValue(LocalDate.now().withDayOfMonth(1));
        if (dateTo   != null) dateTo.setValue(LocalDate.now());
        setupTableColumns();
        setupTableSelection();
        hideDetail();
        loadData();
    }

    private void setupTableColumns() {
        if (colMaHD      != null) colMaHD.setCellValueFactory(new PropertyValueFactory<>("maHD"));
        if (colKhachHang != null) colKhachHang.setCellValueFactory(new PropertyValueFactory<>("maKH"));
        if (colMaMay     != null) colMaMay.setCellValueFactory(new PropertyValueFactory<>("maPhien"));
        if (colNhanVien  != null) colNhanVien.setCellValueFactory(new PropertyValueFactory<>("maNV"));
        if (colNgayHD    != null) colNgayHD.setCellValueFactory(new PropertyValueFactory<>("ngayLapFormatted"));
        if (colTongTien  != null){
            colTongTien.setCellValueFactory(new PropertyValueFactory<>("tongTienFormatted"));
            colTongTien.setComparator((String s1 , String s2 ) -> {

                Double d1 = Double.parseDouble(s1.replaceAll("[^0-9]","").trim());
                Double d2 = Double.parseDouble(s2.replaceAll("[^0-9]","").trim());
                return d1.compareTo(d2);

            });
        }

        if (colTrangThai != null) colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
        if (colCTDV        != null) colCTDV.setCellValueFactory(new PropertyValueFactory<>("moTa"));
        if (colCTSL        != null) colCTSL.setCellValueFactory(new PropertyValueFactory<>("soLuong"));
        if (colCTDonGia    != null) colCTDonGia.setCellValueFactory(new PropertyValueFactory<>("donGiaFormatted"));
        if (colCTThanhTien != null) colCTThanhTien.setCellValueFactory(new PropertyValueFactory<>("thanhTienFormatted"));
    }

    private void setupTableSelection() {
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            selectedHD = n;
            if (btnXuatPDF != null) btnXuatPDF.setDisable(n == null);
            if (n != null) showDetail(n);
            else hideDetail();
        });
    }

    public void loadHoaDon() { loadData(); }

    public void loadData() {
        try {
            LocalDate from = (dateFrom != null && dateFrom.getValue() != null)
                    ? dateFrom.getValue() : LocalDate.now().withDayOfMonth(1);
            LocalDate to = (dateTo != null && dateTo.getValue() != null)
                    ? dateTo.getValue() : LocalDate.now();

            LocalDateTime dtFrom = from.atStartOfDay();
            LocalDateTime dtTo   = to.atTime(LocalTime.MAX);

            List<HoaDon> list;
            try {
                list = hoaDonBUS.getHoaDonsByDateRange(dtFrom, dtTo);
            } catch (Exception ex) {
                list = hoaDonBUS.getAllHoaDon().stream()
                        .filter(h -> h.getNgayLap() != null
                                && !h.getNgayLap().isBefore(dtFrom)
                                && !h.getNgayLap().isAfter(dtTo))
                        .collect(java.util.stream.Collectors.toList());
            }

            String kw = (txtSearch != null) ? txtSearch.getText().toLowerCase().trim() : "";
            if (!kw.isEmpty()) {
                list = list.stream()
                        .filter(h -> h.getMaHD().toLowerCase().contains(kw)
                                || (h.getMaKH() != null && h.getMaKH().toLowerCase().contains(kw))
                                || (h.getMaNV() != null && h.getMaNV().toLowerCase().contains(kw)))
                        .collect(java.util.stream.Collectors.toList());
            }

            dataList.setAll(list);
            sortedList = new SortedList<>(dataList);
            //bind comparator khi user nhấn vào header tự động sort
            sortedList.comparatorProperty().bind(tableView.comparatorProperty());
            // set data cho table
            tableView.setItems(sortedList);
            updateFooter(list);
        } catch (Exception e) {
            if (lblTotal != null) lblTotal.setText("Lỗi: " + e.getMessage());
        }
    }

    private void updateFooter(List<HoaDon> list) {
        if (lblTotal     != null) lblTotal.setText("Tổng: " + list.size() + " hóa đơn");
        double sum = list.stream().mapToDouble(HoaDon::getThanhToan).sum();
        if (lblTongTienAll != null) lblTongTienAll.setText(String.format("Tổng tiền: %,.0f ₫", sum));
    }

    private void showDetail(HoaDon hd) {
        if (lblNoSelection != null) { lblNoSelection.setVisible(false); lblNoSelection.setManaged(false); }
        if (vboxDetail     != null) { vboxDetail.setVisible(true);      vboxDetail.setManaged(true); }

        if (lblDetMaHD     != null) lblDetMaHD.setText(hd.getMaHD());
        if (lblDetKH       != null) lblDetKH.setText(hd.getMaKH() != null ? hd.getMaKH() : "-");
        if (lblDetMay      != null) lblDetMay.setText(hd.getMaPhien() != null ? hd.getMaPhien() : "-");
        if (lblDetThoiGian != null) {
            try { lblDetThoiGian.setText(hd.getNgayLapFormatted()); }
            catch (Exception e) { lblDetThoiGian.setText(hd.getNgayLap() != null ? hd.getNgayLap().toString() : "-"); }
        }
        if (lblDetTienMay  != null) lblDetTienMay.setText(String.format("%,.0f VND", hd.getTienGioChoi()));
        if (lblDetTongTien != null) lblDetTongTien.setText(String.format("%,.0f VND", hd.getTongTien()));
        if (lblDetGiamGia  != null) lblDetGiamGia.setText(
                hd.getGiamGia() > 0 ? "- " + String.format("%,.0f VND", hd.getGiamGia()) : "0 VND");
        if (lblDetThanhToan != null) lblDetThanhToan.setText(String.format("%,.0f VND", hd.getThanhToan()));

        updateThanhToanButton(hd);
        loadChiTietHoaDon(hd.getMaHD());
    }

    private void updateThanhToanButton(HoaDon hd) {
        if (btnXacNhanThanhToan == null) return;
        String tt = hd.getTrangThai();
        boolean chuaTT = tt != null && (tt.equals("CHUA") || tt.equals("CHUATHANHTOAN") || tt.toUpperCase().contains("CHUA"));
        btnXacNhanThanhToan.setVisible(chuaTT);
        btnXacNhanThanhToan.setManaged(chuaTT);
    }

    private void hideDetail() {
        if (vboxDetail     != null) { vboxDetail.setVisible(false);    vboxDetail.setManaged(false); }
        if (lblNoSelection != null) { lblNoSelection.setVisible(true); lblNoSelection.setManaged(true); }
        if (btnXacNhanThanhToan != null) {
            btnXacNhanThanhToan.setVisible(false);
            btnXacNhanThanhToan.setManaged(false);
        }
        currentChiTiet = null;
    }

    private void loadChiTietHoaDon(String maHD) {
        if (tableChiTiet == null) return;
        try {
            currentChiTiet = hoaDonBUS.xemChiTietHoaDon(maHD);
            tableChiTiet.setItems(FXCollections.observableArrayList(currentChiTiet));
        } catch (Exception e) {
            e.printStackTrace();
            currentChiTiet = java.util.Collections.emptyList();
            tableChiTiet.setItems(FXCollections.observableArrayList());
        }
    }

    @FXML public void handleSearch()     { loadData(); }
    @FXML public void handleSearchText() { loadData(); }
    @FXML public void handleRefresh()    { loadData(); }

    @FXML
    public void handleXacNhanThanhToan() {
        if (selectedHD == null) return;
        ChoiceDialog<String> dialog = new ChoiceDialog<>("TAIKHOAN", "TAIKHOAN", "TIENMAT");
        dialog.setTitle("Xác nhận thanh toán");
        dialog.setHeaderText("Hóa đơn: " + selectedHD.getMaHD() + "   |   Thanh toán: " + String.format("%,.0f VND", selectedHD.getThanhToan()));
        dialog.setContentText("Chọn phương thức thanh toán:");

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) return;
        String phuongThuc = result.get();

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận");
        confirm.setHeaderText("Thanh toán hóa đơn " + selectedHD.getMaHD() + "?");
        confirm.setContentText(
                "Khách hàng: " + selectedHD.getMaKH() + "\n"
                        + "Số tiền: " + String.format("%,.0f VND", selectedHD.getThanhToan()) + "\n"
                        + "Phương thức: " + (phuongThuc.equals("TAIKHOAN") ? "Tài khoản" : "Tiền mặt"));
        confirm.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
        Optional<ButtonType> btn = confirm.showAndWait();
        if (btn.isEmpty() || btn.get() != ButtonType.OK) return;

        try {
            hoaDonBUS.thanhToanHoaDon(selectedHD.getMaHD(), phuongThuc);
            ThongBaoDialogHelper.showSuccess(tableView.getScene(), "Thanh toán thành công!\nHóa đơn: " + selectedHD.getMaHD());
            loadData(); // refresh bảng
        } catch (Exception e) {
            ThongBaoDialogHelper.showError(tableView.getScene(), "Lỗi thanh toán: " + e.getMessage());
        }
    }

    @FXML
    public void handleXuatPDF() {
        if (selectedHD == null) {
            ThongBaoDialogHelper.showWarning(tableView.getScene(), "Vui lòng chọn một hóa đơn để xuất PDF.");
            return;
        }
        Window window = tableView.getScene().getWindow();
        FileChooser fc = new FileChooser();
        fc.setTitle("Lưu hóa đơn PDF");
        fc.setInitialFileName("HoaDon_" + selectedHD.getMaHD() + ".pdf");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File file = fc.showSaveDialog(window);
        if (file == null) return;
        try {
            List<ChiTietHoaDon> ct = (currentChiTiet != null) ? currentChiTiet : hoaDonBUS.xemChiTietHoaDon(selectedHD.getMaHD());
            HoaDonExporter.xuatPDFHoaDon(selectedHD, ct, file.getAbsolutePath());
            ThongBaoDialogHelper.showSuccess(tableView.getScene(), "Xuất PDF thành công!\nFile: " + file.getName());
        } catch (Exception e) {
            ThongBaoDialogHelper.showError(tableView.getScene(), "Lỗi xuất PDF: " + e.getMessage());
        }
    }


    @FXML public void handleXuatPDFChiTiet() { handleXuatPDF(); }

    @FXML
    public void handleXuatExcel() {
        if (dataList.isEmpty()) {
            ThongBaoDialogHelper.showWarning(tableView.getScene(), "Không có dữ liệu để xuất Excel.");
            return;
        }
        Window window = tableView.getScene().getWindow();
        FileChooser fc = new FileChooser();
        fc.setTitle("Lưu danh sách hóa đơn Excel");
        fc.setInitialFileName("DanhSachHoaDon.xlsx");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        File file = fc.showSaveDialog(window);
        if (file == null) return;
        try {
            HoaDonExporter.xuatExcelDanhSach(dataList, file.getAbsolutePath());
            ThongBaoDialogHelper.showSuccess(tableView.getScene(),
                    "Xuất Excel thành công!\nFile: " + file.getName()
                            + "\nTổng: " + dataList.size() + " hóa đơn");
        } catch (Exception e) {
            ThongBaoDialogHelper.showError(tableView.getScene(), "Lỗi xuất Excel: " + e.getMessage());
        }
    }
}