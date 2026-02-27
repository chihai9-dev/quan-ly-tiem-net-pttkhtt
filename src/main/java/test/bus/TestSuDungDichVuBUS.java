package test.bus;

import entity.KhachHang;
import entity.SuDungDichVu;
import entity.NhanVien;
import dao.NhanVienDAO;
import untils.SessionManager;

public class TestSuDungDichVuBUS {

    public static void main(String[] args){

        SuDungDichVuDAO sddvDAO = new SuDungDichVuDAO();
        DichVuDAO dvDAO = new DichVuDAO();
        SuDungDichVuBUS sddvBUS = new SuDungDichVuBUS();
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

        // test phương thức order dịch vụ
        try {
            SuDungDichVu order = new SuDungDichVu();
            order = sddvBUS.orderDichVu("PS007", "DV001", 95);
        }catch(Exception e){
            System.err.println("Có lỗi: " + e.getMessage());
        }

        // test phương thức huyOrder
//        try{
//            sddvBUS.huyOrder("SD010");
//        }catch(Exception e){
//            System.err.println("Có lỗi: " +  e.getMessage());
//        }

        // test phương thức getOrderbyPhien
//        List<SuDungDichVu> result = new ArrayList<>();
//        try{
//            result = sddvBUS.getOrderbyPhien("PS001");
//        }catch(Exception e){
//            System.err.println("Có lỗi: " + e.getMessage());
//        }
//
//        for(SuDungDichVu item : result){
//            sddvDAO.Print(item);
//        }
    }
}
