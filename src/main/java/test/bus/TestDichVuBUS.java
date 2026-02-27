package test.bus;

import entity.DichVu;
import entity.NhanVien;
import entity.KhachHang;
import dao.NhanVienDAO;
import untils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class TestDichVuBUS {
    public static void main(String[] args){
        DichVuBUS dv = new DichVuBUS();
        DichVuDAO dvDAO = new DichVuDAO();
        NhanVienDAO nvDAO = new NhanVienDAO();
        KhachHangDAO khDAO = new KhachHangDAO();

        // tạo instance khách hàng
        KhachHang kh = new KhachHang();
        kh = khDAO.login("hoangnam", "123456");

        // tạo instance quản lí
        NhanVien nv = new NhanVien();
        nv = nvDAO.login("admin", "password_hash_1");

        // tạo instance nhân viên
        NhanVien nv1 = new NhanVien();
        nv1 = nvDAO.login("kythuat01", "password_hash_3");

        // bắt đầu đăng nhập
        SessionManager.setCurrentUser(nv);


        List<DichVu> result = new ArrayList<>();
        // test phương thức lất tất cả
//        try {
//            result = dv.getAll();
//            for (DichVu item : result) {
//                dvDAO.PrintDV(item);
//            }
//        }catch(Exception e){
//            System.err.println("Có lỗi: " + e.getMessage());
//        }

        // test phương thúc getDichVuConHang
//        try {
//            result = dv.getDichVuConHang();
//            for (DichVu item : result) {
//                dvDAO.PrintDV(item);
//            }
//        }catch(Exception e){
//            System.err.println("Có lỗi: " + e.getMessage());
//        }

        // test phương thức insert
//        DichVu newDichVu = new DichVu("", "Test nhé 111", "Kem", 11000);
//        try{
//            dv.themDichVu(newDichVu);
//        }catch(Exception e){
//            System.err.println("Có lỗi: " + e.getMessage());
//        }

        // test phương thức update
//        DichVu updateDV = new DichVu("DV012", "Test nhé", "Trứng", 11000);
//        try{
//            dv.suaDichVu(updateDV);
//        }catch(Exception e){
//            System.err.println("Có lỗi: " +  e.getMessage());
//        }

        // test phương thức delete
//        try {
//            dv.xoaDichVu("DV010");
//        }catch(Exception e){
//            System.err.println("Có lỗi: " + e.getMessage());
//        }

        // test phương thức cancelDelete
//        try{
//            dv.khoiPhucLaiDichVu("DV010");
//        }catch(Exception e){
//            System.err.println("Có lỗi: " + e.getMessage());
//        }

    }
}
