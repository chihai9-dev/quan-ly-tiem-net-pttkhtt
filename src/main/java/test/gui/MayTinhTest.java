package test.gui;

import entity.NhanVien;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utils.SessionManager;

public class MayTinhTest extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // 1. Giả lập đăng nhập QUANLY để có đủ quyền test
            NhanVien quanLy = new NhanVien();
            quanLy.setManv("NV001");
            quanLy.setTen("Nguyễn Văn A");
            quanLy.setChucvu("QUANLY");
            SessionManager.setCurrentUser(quanLy);

            // 2. Tải file giao diện
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/mayTinh.fxml"));
            Parent root = loader.load();

            // 3. Gắn giao diện vào Scene
            Scene scene = new Scene(root);

            // 4. Cấu hình và hiển thị Stage
            primaryStage.setTitle("Hệ thống Test - Quản lý Máy Tính");
            primaryStage.setWidth(1000);
            primaryStage.setHeight(650);
            primaryStage.setScene(scene);
            primaryStage.show();

            System.out.println("╔══════════════════════════════════════════════════════╗");
            System.out.println("║      TEST MayTinhController - GIAO DIỆN             ║");
            System.out.println("╠══════════════════════════════════════════════════════╣");
            System.out.println("║  Đăng nhập: QUANLY (NV001)                          ║");
            System.out.println("║  Giao diện đã mở → Test thủ công theo checklist     ║");
            System.out.println("╠══════════════════════════════════════════════════════╣");
            System.out.println("║  CHECKLIST TEST:                                     ║");
            System.out.println("║  1. Bảng hiển thị danh sách máy tính                ║");
            System.out.println("║  2. Cột: Mã máy | Tên máy | Cấu hình | Khu |       ║");
            System.out.println("║          Giá/giờ | Trạng thái                        ║");
            System.out.println("║  3. Tìm kiếm theo tên/mã máy                        ║");
            System.out.println("║  4. Lọc theo Khu (ComboBox)                          ║");
            System.out.println("║  5. Lọc theo Trạng thái (ComboBox)                   ║");
            System.out.println("║  6. Thêm máy tính mới                                ║");
            System.out.println("║  7. Sửa máy tính đang chọn                           ║");
            System.out.println("║  8. Xóa máy tính đang chọn                           ║");
            System.out.println("║  9. Bảo trì: chọn máy TRONG → nhấn Bảo trì          ║");
            System.out.println("║ 10. Khôi phục: chọn máy BAOTRI → nhấn Khôi phục     ║");
            System.out.println("║ 11. Làm mới: reset filter + reload dữ liệu           ║");
            System.out.println("║ 12. Chọn dòng → btnBaoTri/btnKhoiPhuc enable/disable ║");
            System.out.println("╚══════════════════════════════════════════════════════╝");

        } catch (Exception e) {
            System.err.println("Không thể khởi động bản demo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}