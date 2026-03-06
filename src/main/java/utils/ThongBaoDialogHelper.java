package utils;

import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * ThongBaoDialogHelper - Wrapper tiện ích để hiển thị dialog thông báo.
 * Nhận Scene thay vì Stage, tương thích với cách dùng trong controllers.
 */
public class ThongBaoDialogHelper {

    private ThongBaoDialogHelper() {}

    public static void showSuccess(Scene scene, String message) {
        Stage owner = getStage(scene);
        gui.dialog.ThongBaoDialog.showSuccess(owner, message);
    }

    public static void showError(Scene scene, String message) {
        Stage owner = getStage(scene);
        gui.dialog.ThongBaoDialog.showError(owner, message);
    }

    public static void showWarning(Scene scene, String message) {
        Stage owner = getStage(scene);
        gui.dialog.ThongBaoDialog.showWarning(owner, message);
    }

    public static void showInfo(Scene scene, String message) {
        Stage owner = getStage(scene);
        gui.dialog.ThongBaoDialog.showInfo(owner, message);
    }

    private static Stage getStage(Scene scene) {
        if (scene == null) return null;
        return scene.getWindow() instanceof Stage ? (Stage) scene.getWindow() : null;
    }
}
