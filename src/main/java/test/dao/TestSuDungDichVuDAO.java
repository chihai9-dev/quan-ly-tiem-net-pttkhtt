package test.dao;
import dao.SuDungDichVuDAO;
import entity.SuDungDichVu;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class TestSuDungDichVuDAO {
    public static void main(String[] args) {
        SuDungDichVuDAO sddvDAO = new SuDungDichVuDAO();

        // Test phương thức getByPhien
        List<SuDungDichVu> listResult = new ArrayList<SuDungDichVu>();
        listResult = sddvDAO.geyByPhien("PS001");
        for( SuDungDichVu item : listResult){
            sddvDAO.Print(item);
        }

//        // Test phương thức insert
//        SuDungDichVu newsddv = new SuDungDichVu("", "PS001", "DV001"
//                , 2, 12000, 24000, null);
//        sddvDAO.insert(newsddv);

        // Test phương thức delete
//        System.out.println( sddvDAO.delete("SD009")) ;

        //Test phương thức getAll
//        List<SuDungDichVu> listResult = new ArrayList<SuDungDichVu>();
//        listResult = sddvDAO.getALl();
//        for( SuDungDichVu item : listResult){
//            sddvDAO.Print(item);
//        }
    }
}
