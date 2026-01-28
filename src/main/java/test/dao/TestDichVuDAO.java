package test.dao;
import dao.DichVuDAO;
import entity.DichVu;

import java.util.ArrayList;
import java.util.List;

public class TestDichVuDAO {
    public static void main(String[] args) {
        DichVuDAO dvDAO = new DichVuDAO();

        // Test phương thức getAll()
//        List<DichVu> ListDichVu = dvDAO.getAll();
//        Integer stt = 0;
//        for (DichVu item : ListDichVu) {
//            System.out.print(stt + 1);
//            dvDAO.PrintDV(item);
//            stt++;
//        }

        // Test phương thức getByID()
//        DichVu ketQua = dvDAO.getByID("DV005");
//        if (ketQua != null) {
//            dvDAO.PrintDV(ketQua);
//        }

        // Test phương thức insert
//        DichVu newDichVu = new DichVu("", "Kẹo ngọt", "THUCPHAM"
//                , 10000, "Gói", 10, "CONHANG");
//        dvDAO.insert(newDichVu);

        // Test phương thức update
//        DichVu updateDichVu = new DichVu("DV011", "Bánh ngọt", "THUCPHAM"
//                , 10000, "Gói", 10, "CONHANG");
//        dvDAO.update(updateDichVu);

        // Test phương thức delete
//        dvDAO.delete("DV011");

        // Test phương thức updateSoLuong
        System.out.println(dvDAO.updateSoLuongTon("DV011", 5));
    }
}

