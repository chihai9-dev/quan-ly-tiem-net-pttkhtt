package test.dao;
import dao.GoiDichVuKhachHangDAO;
import entity.GoiDichVuKhachHang;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class TestGoiDichVuKhachHangDAO {
    public static void main(String[] args ){
        GoiDichVuKhachHangDAO gkhDAO= new GoiDichVuKhachHangDAO();

        // Test phương thức getByKhachHang
        List<GoiDichVuKhachHang> resultList = new ArrayList<>();
        resultList = gkhDAO.getByKhachHang("KH001");
        for(GoiDichVuKhachHang item : resultList){
            gkhDAO.print(item);
        }

        // Test phương thức insert
//        GoiDichVuKhachHang newGoi = new GoiDichVuKhachHang(
//                "", "KH001", "GOI001", "NV002",
//                10.0, // SoGioBanDau (Ví dụ: Gói 10 giờ)
//                10.0, // SoGioConLai (Mới mua nên còn nguyên 10 giờ)
//                LocalDateTime.now(),
//                LocalDateTime.parse("2026-02-01T08:23:21"), // Đã dùng chuẩn ISO có chữ T
//                15000.0, ""
//        );
//        gkhDAO.insert(newGoi);

        // Test phương thức update
//        GoiDichVuKhachHang updateGoi = new GoiDichVuKhachHang(
//                "GOIKH005", "KH001", "GOI001", "NV002",
//                10.0, // SoGioBanDau (Ví dụ: Gói 10 giờ)
//                5.0, // SoGioConLai (Mới mua nên còn nguyên 10 giờ)
//                LocalDateTime.now(),
//                LocalDateTime.parse("2026-02-01T08:23:21"), // Đã dùng chuẩn ISO có chữ T
//                15000.0, "CONHAN");
//        gkhDAO.update(updateGoi);


        // Test phương thức kiểm tra xem còn hiệu lực hay không
        gkhDAO.getConHieuLuc("GOIKH001");
    }
}
