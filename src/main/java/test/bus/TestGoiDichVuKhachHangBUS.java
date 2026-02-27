package test.bus;

import entity.KhachHang;
import entity.NhanVien;
import dao.NhanVienDAO;

import untils.SessionManager;

public class TestGoiDichVuKhachHangBUS {
    public static void main(String[] args){

        GoiDichVuDAO gdvDAO = new GoiDichVuDAO();
        GoiDichVuBUS gdvBUS = new GoiDichVuBUS();
        GoiDichVuKhachHangDAO gdvkhDAO= new GoiDichVuKhachHangDAO();
        GoiDichVuKhachHangBUS gdvkhBUS = new GoiDichVuKhachHangBUS();
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

        // test chức năng mua gói
//        try{
//            gdvkhBUS.muaGoi("GOI001", "KH002");
//        }catch(Exception e){
//            System.err.println("Có lỗi: " + e.getMessage());
//        }

        // test chức năng lấy các gói còn hạn
//        try{
//            List<GoiDichVuKhachHang> result = gdvkhBUS.getGoiConHieuLuc("KH002");
//            System.out.println("THÔNG TIN CÁC GÓI CON HIỆU LỰC CỦA QUÝ KHÁCH LÀ: ");
//            for(GoiDichVuKhachHang item : result){
//                gdvkhDAO.print(item);
//            }
//        }catch(Exception e){
//            System.err.println("Có lỗi: " + e.getMessage());
//        }

        // test chức năng gói còn hiệu lực
        try{
            gdvkhBUS.kiemTraGoiHopLe("GOIKH002");
        }catch(Exception e){
            System.err.println("Có lỗi: " + e.getMessage());
        }
    }
}
