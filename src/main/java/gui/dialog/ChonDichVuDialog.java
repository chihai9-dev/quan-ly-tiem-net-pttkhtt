package gui.dialog;

import bus.DichVuBUS;
import bus.SuDungDichVuBUS;
import entity.DichVu;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Dialog: Chọn dịch vụ để order cho phiên đang chơi.
 * Sử dụng:
 *   controller.setPhien(maPhien, "KH001 | MAY01");
 *   controller.setOnOrderCallback(() -> parentController.loadData());
 */
public class ChonDichVuDialog implements Initializable {

    @FXML private Label     lblPhienInfo;
    @FXML private TextField txtSearch;

    // Bảng chọn dịch vụ
    @FXML private TableView<DichVu>            tableDichVu;
    @FXML private TableColumn<DichVu, String>  colDVMa;
    @FXML private TableColumn<DichVu, String>  colDVTen;
    @FXML private TableColumn<DichVu, Double>  colDVGia;
    @FXML private TableColumn<DichVu, Integer> colDVTon;

    @FXML private Label     lblSelectedDV;
    @FXML private TextField txtSoLuong;

    // Giỏ hàng
    @FXML private TableView<CartItem>            tableCart;
    @FXML private TableColumn<CartItem, String>  colCartTen;
    @FXML private TableColumn<CartItem, Integer> colCartSL;
    @FXML private TableColumn<CartItem, Double>  colCartDonGia;
    @FXML private TableColumn<CartItem, Double>  colCartThanhTien;

    @FXML private Label  lblTongTienCart;
    @FXML private Label  lblError;
    @FXML private Button btnXacNhan;

    private final DichVuBUS       dichVuBUS   = new DichVuBUS();
    private final SuDungDichVuBUS suDungDVBUS = new SuDungDichVuBUS();

    private ObservableList<DichVu>   dichVuList = FXCollections.observableArrayList();
    private ObservableList<CartItem> cartList   = FXCollections.observableArrayList();
    private DichVu selectedDV;
    private String maPhien;
    private Runnable onOrderCallback;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupDichVuTable();
        setupCartTable();
        if (tableCart != null) tableCart.setItems(cartList);
        if (txtSoLuong != null) txtSoLuong.setText("1");
        loadDichVu();
        setupTableSelection();
    }

    private void setupDichVuTable() {
        if (tableDichVu == null) return;
        if (colDVMa != null)  colDVMa.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("maDV"));
        if (colDVTen != null) colDVTen.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("tenDV"));
        if (colDVGia != null) {
            colDVGia.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("donGia"));
            colDVGia.setCellFactory(col -> new TableCell<>() {
                @Override protected void updateItem(Double v, boolean e) {
                    super.updateItem(v, e);
                    setText(e || v == null ? null : String.format("%,.0f ₫", v));
                }
            });
        }
        if (colDVTon != null) colDVTon.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("soLuongTon"));
    }

    private void setupCartTable() {
        if (tableCart == null) return;
        if (colCartTen != null)
            colCartTen.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTenDV()));
        if (colCartSL != null)
            colCartSL.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getSoLuong()).asObject());
        if (colCartDonGia != null) {
            colCartDonGia.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getDonGia()).asObject());
            colCartDonGia.setCellFactory(col -> new TableCell<>() {
                @Override protected void updateItem(Double v, boolean e) {
                    super.updateItem(v, e);
                    setText(e || v == null ? null : String.format("%,.0f ₫", v));
                }
            });
        }
        if (colCartThanhTien != null) {
            colCartThanhTien.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getThanhTien()).asObject());
            colCartThanhTien.setCellFactory(col -> new TableCell<>() {
                @Override protected void updateItem(Double v, boolean e) {
                    super.updateItem(v, e);
                    setText(e || v == null ? null : String.format("%,.0f ₫", v));
                }
            });
        }
    }

    // setPhien nhận String maPhien (khác với version int cũ)
    public void setPhien(String maPhien, String phienInfo) {
        this.maPhien = maPhien;
        if (lblPhienInfo != null)
            lblPhienInfo.setText("Phiên: " + (phienInfo != null ? phienInfo : maPhien));
    }

    public void setOnOrderCallback(Runnable cb) { this.onOrderCallback = cb; }

    private void loadDichVu() {
        try {
            // getDanhSachDV() hoặc getAvailable() - dùng getDanhSachDV() an toàn hơn
            List<DichVu> list = dichVuBUS.getDanhSachDV();
            // Chỉ lấy dịch vụ DANGBAN và còn hàng
            list = list.stream()
                .filter(d -> "DANGBAN".equals(d.getTrangThai()) && d.getSoLuongTon() > 0)
                .toList();
            dichVuList.setAll(list);
            if (tableDichVu != null) tableDichVu.setItems(dichVuList);
        } catch (Exception e) {
            if (lblError != null) lblError.setText("Lỗi tải dịch vụ: " + e.getMessage());
        }
    }

    private void setupTableSelection() {
        if (tableDichVu == null) return;
        tableDichVu.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            selectedDV = n;
            if (lblSelectedDV != null)
                lblSelectedDV.setText(n != null ? n.getTenDV() + " - " +
                    String.format("%,.0f ₫", n.getDonGia()) : "Chưa chọn dịch vụ");
        });
    }

    @FXML
    public void handleSearch() {
        if (tableDichVu == null) return;
        String kw = txtSearch != null ? txtSearch.getText().toLowerCase().trim() : "";
        tableDichVu.setItems(dichVuList.filtered(d ->
            kw.isEmpty() ||
            d.getTenDV().toLowerCase().contains(kw) ||
            d.getMaDV().toLowerCase().contains(kw)));
    }

    @FXML
    public void handleIncrease() {
        try {
            int qty = Integer.parseInt(txtSoLuong != null ? txtSoLuong.getText() : "1") + 1;
            if (txtSoLuong != null) txtSoLuong.setText(String.valueOf(qty));
        } catch (NumberFormatException e) {
            if (txtSoLuong != null) txtSoLuong.setText("1");
        }
    }

    @FXML
    public void handleDecrease() {
        try {
            int qty = Math.max(1, Integer.parseInt(txtSoLuong != null ? txtSoLuong.getText() : "1") - 1);
            if (txtSoLuong != null) txtSoLuong.setText(String.valueOf(qty));
        } catch (NumberFormatException e) {
            if (txtSoLuong != null) txtSoLuong.setText("1");
        }
    }

    @FXML
    public void handleAddToCart() {
        if (selectedDV == null) {
            setError("Vui lòng chọn dịch vụ");
            return;
        }
        int qty;
        try {
            qty = Integer.parseInt(txtSoLuong != null ? txtSoLuong.getText() : "1");
            if (qty <= 0) qty = 1;
        } catch (NumberFormatException e) { qty = 1; }

        // String comparison (maDV là String trong DichVu entity)
        final int finalQty = qty;
        final String maDV  = selectedDV.getMaDV();
        cartList.stream()
            .filter(item -> maDV.equals(item.getMaDV()))
            .findFirst().ifPresentOrElse(
                item -> item.setSoLuong(item.getSoLuong() + finalQty),
                () -> cartList.add(new CartItem(selectedDV, finalQty))
            );
        if (tableCart != null) tableCart.refresh();
        updateCartTotal();
        clearError();
    }

    @FXML
    public void handleRemoveFromCart() {
        if (tableCart == null) return;
        CartItem selected = tableCart.getSelectionModel().getSelectedItem();
        if (selected != null) {
            cartList.remove(selected);
            updateCartTotal();
        }
    }

    @FXML
    public void handleXacNhan() {
        if (cartList.isEmpty()) { setError("Giỏ hàng trống"); return; }
        if (maPhien == null)    { setError("Chưa có thông tin phiên"); return; }
        try {
            for (CartItem item : cartList) {
                // orderDichVu(String maPhien, String maDV, int soLuong)
                suDungDVBUS.orderDichVu(maPhien, item.getMaDV(), item.getSoLuong());
            }
            if (onOrderCallback != null) onOrderCallback.run();
            closeDialog();
        } catch (Exception e) {
            setError("Lỗi order dịch vụ: " + e.getMessage());
        }
    }

    @FXML public void handleCancel() { closeDialog(); }

    private void updateCartTotal() {
        double total = cartList.stream().mapToDouble(CartItem::getThanhTien).sum();
        if (lblTongTienCart != null)
            lblTongTienCart.setText(String.format("%,.0f ₫", total));
    }

    private void setError(String msg)  { if (lblError != null) lblError.setText(msg); }
    private void clearError()          { if (lblError != null) lblError.setText(""); }

    private void closeDialog() {
        if (btnXacNhan != null && btnXacNhan.getScene() != null)
            ((Stage) btnXacNhan.getScene().getWindow()).close();
    }

    // ===== Inner CartItem class =====
    public static class CartItem {
        private final DichVu dichVu;
        private int soLuong;

        public CartItem(DichVu dv, int qty) {
            this.dichVu   = dv;
            this.soLuong  = qty;
        }

        // maDV trả về String (khớp với DichVu entity)
        public String getMaDV()        { return dichVu.getMaDV(); }
        public String getTenDV()       { return dichVu.getTenDV(); }
        public int    getSoLuong()     { return soLuong; }
        public void   setSoLuong(int q){ this.soLuong = q; }
        // getDonGia() trả về double (khớp với DichVu entity)
        public double getDonGia()      { return dichVu.getDonGia(); }
        public double getThanhTien()   { return dichVu.getDonGia() * soLuong; }
    }
}
