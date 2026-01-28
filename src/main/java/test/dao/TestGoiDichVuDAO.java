package test.dao;
import dao.GoiDichVuDAO;
import entity.GoiDichVu;

import java.util.ArrayList;
import java.util.List;

public class TestGoiDichVuDAO {
    public static void main(String[] args){
        GoiDichVuDAO gdvDAO = new GoiDichVuDAO();

        // Test phương thức getALL
        List<GoiDichVu> resultList = new ArrayList<>();
        resultList = gdvDAO.getAll();
        for(GoiDichVu item : resultList){
            gdvDAO.Print(item);
        }

        // Test phương thức getByID
//        GoiDichVu goal = gdvDAO.getByID("GOI002");
//        gdvDAO.Print(goal);

        // Test phương thức inset.
//        GoiDichVu newgdv = new GoiDichVu("", "Gói lẻ", "THEOGIO", 1, 30
//                , 4000, 2500, "KHU001", "");
//        gdvDAO.insert(newgdv);

        // Test phương thức update
//        GoiDichVu updateGDV = new GoiDichVu("GOI006", "Gói ngắn hạn", "THEOGIO", 1, 30
//                , 4000, 2500, "KHU001", "NGUNG");
//        gdvDAO.update(updateGDV);

        // Test phương thức delete
//        gdvDAO.delete("GOI006");
    }
}
