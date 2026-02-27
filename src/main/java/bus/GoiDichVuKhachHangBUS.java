package bus;

import dao.DBConnection;
import entity.GoiDichVuKhachHang;
import entity.GoiDichVu;
import entity.KhachHang;
import entity.NhanVien;
import dao.GoiDichVuKhachHangDAO;
import dao.GoiDichVuDAO;
import dao.KhachHangDAO;
import dao.ConnectionManager;
import untils.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.time.LocalDateTime;
/* CÁC METHOD.
    1. void muaGoi(String maGDV, String maKH): mua gói dịch vụ. phân quyền(quản lý, nhân viên).
    2. List<GoiDichVuKhachHang> getGoiConHieuLuc(String maKH): lấy tất cả các gói còn hiệu lực của khách hàng đó.
phân quyền( quản lý, nhân viên).
    3. boolean kiemTraGoiHopLe(String maGoiKH): kiểm tra gói có hợp lệ. phân quyền(quản lý, nhân viên).
*/

public class GoiDichVuKhachHangBUS{

    private final GoiDichVuKhachHangDAO gdvkhDAO = new GoiDichVuKhachHangDAO();
    private final GoiDichVuDAO gdvDAO = new GoiDichVuDAO();
    private final KhachHangDAO khDAO = new KhachHangDAO();

    // CHỨC NĂNG MUA GÓI DỊCH VỤ
    public void muaGoi(String maGDV, String maKH) throws Exception{
        // check login
        PermissionHelper.requireLogin();

        // kiểm tra phân quyền.
        PermissionHelper.requireQuanLy();

        // kiểm tra VALIDATION
        // check mã gói dịch vụ
        if(maGDV == null ){ throw new Exception("Mã gói dịch vụ cần mua không được để trống!!!"); }
        GoiDichVu gdv = gdvDAO.getByID(maGDV);
        if(  gdv == null ){ throw new Exception("Mã gói dịch vụ này không tồn tại!!!"); }
        // kiểm tra trạng thái của gói dịch vụ
        if( gdv.getTrangthai().equals("NGUNG")){ throw new Exception("Gói dịch vụ này hiện tại đã ngừng!!!"); }

        // kiểm tra khách hàng
        if( maKH == null){ throw new Exception("Mã khách hàng mua gói dịch vụ không được để trống!!!"); }
        List<KhachHang> list = new ArrayList<>();
        list = khDAO.getAll();
        KhachHang kh = null;
        for(KhachHang item : list){
            if(item.getMakh().equals(maKH)){
                kh = item;
                break;
            }
        }
        if(kh == null){ throw new Exception("Mã khách hàng này không tồn tại!!!"); }
        if(kh.getTrangthai().equals("NGUNG")){ throw new Exception("Tài khoản của khách này đã ngừng hoạt động!!!"); }

        // kiểm tra số dư
        if( kh.getSodu() < gdv.getGiagoi() ){ throw new Exception("Số dư của khách không đủ để mua gói dịch vụ này!!!");}

        // lấy thông tin của nhân viên thực hiện mua gói cho khách
        NhanVien nv = SessionManager.getCurrentNhanVien();
        // tạo thông tin gói dịch vụ khách hàng
        GoiDichVuKhachHang gdvkh = new GoiDichVuKhachHang("", kh.getMakh(), gdv.getMagoi(), nv.getManv(), gdv.getSogio()
                , gdv.getSogio(), LocalDateTime.now(), LocalDateTime.now().plusDays(gdv.getSongayhieuluc()), gdv.getGiagoi()
                , "CONHAN");

        // gọi xuống DAO
        try{
            ConnectionManager.beginTransaction();

            // trừ tiền khách hàng
            kh.setSodu( kh.getSodu() - gdv.getGiagoi());
            boolean truTien = this.khDAO.updateSoDuKhiMuaGoi(kh);

            // thêm một dòng gói dịch vụ khách hàng
            boolean isSucess = this.gdvkhDAO.insert(gdvkh);
            if(truTien && isSucess){
                ConnectionManager.commit();
                System.out.println("Mua gói dịch vụ thành công!!!");
            }else{
                System.out.println("Mua gói dịch vụ không thành công!!!");
            }
        }catch(Exception e){
                ConnectionManager.rollback();
            throw new Exception("Lỗi hệ thống: " + e.getMessage());
        }finally{
            ConnectionManager.close();
        }
    }

    // LẤY NHỮNG GÓI DỊCH VỤ CÒN HẠN CỦA MỘT KHÁCH HÀNG
    public List<GoiDichVuKhachHang> getGoiConHieuLuc(String maKH) throws Exception{
        // check login
        PermissionHelper.requireLogin();

        // kiểm tra phân quyền.
        PermissionHelper.requireQuanLy();

        // KIỂM TRA VALIDATION
        // kiểm tra khách hàng
        if( maKH == null){ throw new Exception("Mã khách hàng mua gói dịch vụ không được để trống!!!"); }
        List<KhachHang> list = new ArrayList<>();
        list = khDAO.getAll();
        KhachHang kh = null;
        for(KhachHang item : list){
            if(item.getMakh().equals(maKH)){
                kh = item;
                break;
            }
        }
        if(kh == null){ throw new Exception("Mã khách hàng này không tồn tại!!!"); }
        if(kh.getTrangthai().equals("NGUNG")){ throw new Exception("Tài khoản của khách này đã ngừng hoạt động!!!"); }

        // gọi xuống DAO
        List<GoiDichVuKhachHang> result = new ArrayList<>();
        result = this.gdvkhDAO.getByKhachHang(maKH);
        // lấy tất cả
        Iterator<GoiDichVuKhachHang> l = result.iterator();
        while(l.hasNext()) {
            GoiDichVuKhachHang item = l.next();
            if(item.getTrangthai().equals("HETHAN") || item.getTrangthai().equals("DAHETGIO")){
                l.remove();
            }
        }

        return result;
    }

    // CHỨC NĂNG KIỂM TRA GÓI HỢP LỆ
    public boolean kiemTraGoiHopLe(String maGoiKH) throws Exception{
        // check login
        PermissionHelper.requireLogin();

        // kiểm tra phân quyền.
        PermissionHelper.requireQuanLy();

        // kiểm tra VALIDATION
        if( maGoiKH == null ){ throw new Exception("Mã gói khách hàng không được để trống!!!"); }
        GoiDichVuKhachHang gdvkh = null;
        gdvkh = this.gdvkhDAO.getByID(maGoiKH);
        if(gdvkh == null){ throw new Exception("Không tồn tại mã gói khách hàng này!!!"); }

        // kiểm tra kết quả
        if( gdvkh.getTrangthai().equals("CONHAN") ){
            System.out.println("Gói " + maGoiKH + " còn dùng được!!!");
            return true;
        }
        else{
            System.out.println("Gói " + maGoiKH + " không còn dùng được!!!");
            return false;
        }
    }
}
