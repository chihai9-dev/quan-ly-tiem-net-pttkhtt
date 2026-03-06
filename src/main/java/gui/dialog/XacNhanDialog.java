package gui.dialog;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Reusable confirm dialog.
 * Usage:
 *   boolean result = XacNhanDialog.show(ownerStage, "Xóa dữ liệu", "Bạn có chắc muốn xóa?");
 *   boolean result = XacNhanDialog.showDelete(ownerStage, "Tên mục cần xóa");
 */
public class XacNhanDialog implements Initializable {

    @FXML private Label  lblHeaderIcon;
    @FXML private Label  lblDialogTitle;
    @FXML private Label  lblIcon;
    @FXML private Label  lblMessage;
    @FXML private Label  lblSubMessage;
    @FXML private Button btnCancel;
    @FXML private Button btnConfirm;

    private boolean confirmed = false;

    public enum Type { CONFIRM, DELETE, WARNING }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    public void setup(String title, String message, String subMessage, Type type) {
        lblDialogTitle.setText(title);
        lblMessage.setText(message);
        if (subMessage != null && !subMessage.isEmpty()) {
            lblSubMessage.setText(subMessage);
            lblSubMessage.setVisible(true);
        } else {
            lblSubMessage.setVisible(false);
        }
        switch (type) {
            case DELETE -> {
                lblHeaderIcon.setText("🗑");
                lblIcon.setText("🗑");
                btnConfirm.getStyleClass().removeIf(s -> s.equals("btn-primary"));
                btnConfirm.getStyleClass().add("btn-danger");
                btnConfirm.setText("✕  Xóa");
            }
            case WARNING -> {
                lblHeaderIcon.setText("⚠");
                lblIcon.setText("⚠");
                btnConfirm.setText("✔  Tiếp tục");
            }
            default -> {
                lblHeaderIcon.setText("❓");
                lblIcon.setText("❓");
                btnConfirm.setText("✔  Xác nhận");
            }
        }
    }

    @FXML
    public void handleConfirm() {
        confirmed = true;
        closeDialog();
    }

    @FXML
    public void handleCancel() {
        confirmed = false;
        closeDialog();
    }

    private void closeDialog() {
        ((Stage) btnConfirm.getScene().getWindow()).close();
    }

    public boolean isConfirmed() { return confirmed; }

    // ===== Static factory methods =====

    public static boolean show(Stage owner, String title, String message) {
        return show(owner, title, message, null, Type.CONFIRM);
    }

    public static boolean showDelete(Stage owner, String itemName) {
        return show(owner, "Xác nhận xóa",
            "Bạn có chắc muốn xóa: " + itemName + "?",
            "Hành động này không thể hoàn tác.", Type.DELETE);
    }

    public static boolean show(Stage owner, String title, String message,
                                String subMessage, Type type) {
        AtomicBoolean result = new AtomicBoolean(false);
        try {
            FXMLLoader loader = new FXMLLoader(
                XacNhanDialog.class.getResource("/fxml/dialogs/xacNhan.fxml"));
            Parent root = loader.load();
            XacNhanDialog controller = loader.getController();
            controller.setup(title, message, subMessage, type);

            Stage stage = new Stage(StageStyle.UNDECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            if (owner != null) stage.initOwner(owner);
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.showAndWait();
            result.set(controller.isConfirmed());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.get();
    }
}
