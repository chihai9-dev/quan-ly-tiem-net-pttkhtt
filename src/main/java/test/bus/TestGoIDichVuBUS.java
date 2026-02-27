package test.bus;

import entity.KhachHang;
import entity.NhanVien;
import dao.NhanVienDAO;

import untils.SessionManager;


public class TestGoIDichVuBUS {
    public static void main(String[] args){

        GoiDichVuDAO gdvDAO = new GoiDichVuDAO();
        GoiDichVuBUS gdvBUS = new GoiDichVuBUS();
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

        // test chức năng getAll
//        List<GoiDichVu> result = new ArrayList<>();
//        try{
//            result = gdvBUS.getAll();
//            // in ra kết quả
//            for(GoiDichVu item : result){
//                gdvDAO.Print(item);
//            }
//        }catch(Exception e){
//            System.err.println("Có lỗi: " + e);
//        }

        // test chức năng lấy các gói còn hoạt động
//        List<GoiDichVu> result = new ArrayList<>();
//        try{
//            result = gdvBUS.getGoiHoatDong();
//            // in ra kết quả
//            for(GoiDichVu item : result){
//                gdvDAO.Print(item);
//            }
//        }catch(Exception e){
//            System.err.println("Có lỗi: " + e);
//        }

        // test chức năng thêm một gói dịch vụ.
//        try{
//            GoiDichVu newgdv = new GoiDichVu("", "Combo Học Tập (5h)", "THEOGIO", 5.0, 30, 25000.0
//                    , 20000.0, null, "HOATDONG");
//            gdvBUS.themGoiDichVu(newgdv);
//        }catch(Exception e){
//            System.err.println("Có lỗi: " + e.getMessage());
//        }

        // test chức năng sửa gói dịch vụ
//        try{
//            GoiDichVu updategdv = new GoiDichVu( "GOI007", "Combo Học Tập (10h)", "THEOGIO", 5.0, 30, 25000.0
//                    , 20000.0, null, "HOATDONG");
//            gdvBUS.suaGoiDichVu(updategdv);
//        }catch(Exception e){
//            System.err.println("Có lỗi: " + e.getMessage());
//        }

        // test chức năng xóa gói dịch vụ
//        try{
//            gdvBUS.xoaGoiDichVu(null);
//        }catch(Exception e){
//            System.err.println("Có lỗi: " + e.getMessage());
//        }

        // test chức năng khôi phục lại gói dịch vụ
//        try{
//            gdvBUS.khoiPhucGDV(null);
//        }catch(Exception e){
//            System.err.println("Có lỗi: " + e.getMessage());
//        }
    }

}
