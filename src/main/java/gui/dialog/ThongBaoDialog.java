package gui.dialog;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Reusable notification dialog.
 * Usage:
 *   ThongBaoDialog.showSuccess(stage, "Thêm thành công!");
 *   ThongBaoDialog.showError(stage,   "Lỗi: ...");
 *   ThongBaoDialog.showWarning(stage, "Cảnh báo: ...");
 *   ThongBaoDialog.showInfo(stage,    "Thông báo: ...");
 */
public class ThongBaoDialog implements Initializable {

    @FXML private HBox  headerBox;
    @FXML private Label lblHeaderIcon;
    @FXML private Label lblTitle;
    @FXML private Label lblBigIcon;
    @FXML private Label lblMessage;
    @FXML private Button btnOK;

    public enum Type { SUCCESS, ERROR, WARNING, INFO }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    public void setup(String title, String message, Type type) {
        lblTitle.setText(title);
        lblMessage.setText(message);
        switch (type) {
            case SUCCESS -> {
                lblHeaderIcon.setText("✔");
                lblBigIcon.setText("✅");
                headerBox.setStyle("-fx-background-color:#388E3C; -fx-background-radius: 10 10 0 0; -fx-padding:16 24;");
            }
            case ERROR -> {
                lblHeaderIcon.setText("✕");
                lblBigIcon.setText("❌");
                headerBox.setStyle("-fx-background-color:#C62828; -fx-background-radius: 10 10 0 0; -fx-padding:16 24;");
            }
            case WARNING -> {
                lblHeaderIcon.setText("⚠");
                lblBigIcon.setText("⚠️");
                headerBox.setStyle("-fx-background-color:#F57C00; -fx-background-radius: 10 10 0 0; -fx-padding:16 24;");
            }
            default -> {
                lblHeaderIcon.setText("ℹ");
                lblBigIcon.setText("ℹ️");
                headerBox.setStyle("-fx-background-color:#1565C0; -fx-background-radius: 10 10 0 0; -fx-padding:16 24;");
            }
        }
    }

    @FXML
    public void handleOK() {
        ((Stage) btnOK.getScene().getWindow()).close();
    }

    // ===== Static factory methods =====

    public static void showSuccess(Stage owner, String msg) {
        show(owner, "Thành công", msg, Type.SUCCESS);
    }

    public static void showError(Stage owner, String msg) {
        show(owner, "Lỗi", msg, Type.ERROR);
    }

    public static void showWarning(Stage owner, String msg) {
        show(owner, "Cảnh báo", msg, Type.WARNING);
    }

    public static void showInfo(Stage owner, String msg) {
        show(owner, "Thông báo", msg, Type.INFO);
    }

    public static void show(Stage owner, String title, String message, Type type) {
        try {
            FXMLLoader loader = new FXMLLoader(
                ThongBaoDialog.class.getResource("/fxml/dialogs/thongBao.fxml"));
            Parent root = loader.load();
            ThongBaoDialog controller = loader.getController();
            controller.setup(title, message, type);

            Stage stage = new Stage(StageStyle.UNDECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            if (owner != null) stage.initOwner(owner);
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.showAndWait();
        } catch (Exception e) {
            // Fallback to standard JavaFX Alert
            Alert.AlertType alertType = switch (type) {
                case SUCCESS -> Alert.AlertType.INFORMATION;
                case ERROR   -> Alert.AlertType.ERROR;
                case WARNING -> Alert.AlertType.WARNING;
                default      -> Alert.AlertType.INFORMATION;
            };
            Alert alert = new Alert(alertType);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        }
    }
}
