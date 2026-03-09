package test.gui;

import entity.NhanVien;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utils.SessionManager;

public class NhanVienTest extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Giả lập đăng nhập Quản lý để test không bị lỗi phân quyền
            NhanVien admin = new NhanVien();
            admin.setManv("NV_TEST");
            admin.setTen("Admin Test");
            admin.setChucvu("QUANLY");
            SessionManager.setCurrentUser(admin);

            // 1. Tải file giao diện từ thư mục resources
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dialogs/nhanVien.fxml"));
            Parent root = loader.load();

            // 2. Gắn giao diện vào một "Cảnh" (Scene)
            Scene scene = new Scene(root);

            // 3. Cấu hình và hiển thị "Sân khấu" (Stage)
            primaryStage.setTitle("Hệ thống Test - Quản lý Nhân viên");
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