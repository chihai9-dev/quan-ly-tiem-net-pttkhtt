package gui.controller;

import bus.HoaDonBUS;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import utils.ThongBaoDialogHelper;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
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

    @FXML private VBox  vboxDetail;
    @FXML private Label lblNoSelection;
    @FXML private Label lblDetMaHD;
    @FXML private Label lblDetKH;
    @FXML private Label lblDetMay;
    @FXML private Label lblDetThoiGian;
    @FXML private Label lblDetTienMay;
    @FXML private Label lblDetTongTien;
    @FXML private Label lblDetGiamGia;    // mới
    @FXML private Label lblDetThanhToan;  // mới

    @FXML private TableView<ChiTietHoaDon>           tableChiTiet;
    @FXML private TableColumn<ChiTietHoaDon, String> colCTDV;
    @FXML private TableColumn<ChiTietHoaDon, String> colCTSL;
    @FXML private TableColumn<ChiTietHoaDon, String> colCTDonGia;
    @FXML private TableColumn<ChiTietHoaDon, String> colCTThanhTien;

    private final HoaDonBUS hoaDonBUS = new HoaDonBUS();
    private final ObservableList<HoaDon> dataList = FXCollections.observableArrayList();
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
        if (colTongTien  != null) colTongTien.setCellValueFactory(new PropertyValueFactory<>("tongTienFormatted"));
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
            tableView.setItems(dataList);
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

        if (lblDetGiamGia != null) {
            if (hd.getGiamGia() > 0) {
                lblDetGiamGia.setText("- " + String.format("%,.0f VND", hd.getGiamGia()));
            } else {
                lblDetGiamGia.setText("0 VND");
            }
        }
        if (lblDetThanhToan != null) lblDetThanhToan.setText(String.format("%,.0f VND", hd.getThanhToan()));

        loadChiTietHoaDon(hd.getMaHD());
    }

    private void hideDetail() {
        if (vboxDetail     != null) { vboxDetail.setVisible(false);    vboxDetail.setManaged(false); }
        if (lblNoSelection != null) { lblNoSelection.setVisible(true); lblNoSelection.setManaged(true); }
    }

    private void loadChiTietHoaDon(String maHD) {
        if (tableChiTiet == null) return;
        try {
            List<ChiTietHoaDon> ctList = hoaDonBUS.xemChiTietHoaDon(maHD);
            tableChiTiet.setItems(FXCollections.observableArrayList(ctList));
        } catch (Exception ignored) {}
    }

    @FXML public void handleSearch()     { loadData(); }
    @FXML public void handleSearchText() { loadData(); }
    @FXML public void handleRefresh()    { loadData(); }

    @FXML
    public void handleXuatPDF() {
        if (selectedHD == null) {
            ThongBaoDialogHelper.showWarning(tableView.getScene(), "Vui lòng chọn một hóa đơn để xuất PDF.");
            return;
        }
        try {
            ThongBaoDialogHelper.showInfo(tableView.getScene(),
                    "Đang xuất PDF hóa đơn " + selectedHD.getMaHD() + "...\n(Chức năng đang hoàn thiện)");
        } catch (Exception e) {
            ThongBaoDialogHelper.showError(tableView.getScene(), "Lỗi xuất PDF: " + e.getMessage());
        }
    }

    @FXML public void handleXuatPDFChiTiet() { handleXuatPDF(); }

    @FXML
    public void handleXuatExcel() {
        ThongBaoDialogHelper.showInfo(tableView.getScene(), "Chức năng xuất Excel đang phát triển.");
    }
}
