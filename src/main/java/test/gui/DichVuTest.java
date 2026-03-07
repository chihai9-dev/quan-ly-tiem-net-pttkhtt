package test.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DichVuTest extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // 1. Tải file giao diện từ thư mục resources/fxml
            // Hãy đảm bảo tên file fxml trong resources/fxml khớp chính xác (ví dụ: dichVu.fxml)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dichVu.fxml"));

            Parent root = loader.load();

            // 2. Gắn giao diện vào một "Cảnh" (Scene)
            Scene scene = new Scene(root);

            // 3. Cấu hình và hiển thị "Sân khấu" (Stage)
            primaryStage.setTitle("Hệ thống Test - Quản lý Dịch vụ");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            System.err.println("Không thể khởi động bản demo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Kích hoạt môi trường JavaFX
        launch(args);
    }
}