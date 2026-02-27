package bus;
import entity.GoiDichVu;
import dao.GoiDichVuDAO;
import dao.DBConnection;
import dao.ConnectionManager;
import untils.PermissionHelper;

import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import java.util.Iterator;

/* CÁC METHOD.
    1. List<GoiDichVu> DichVuBUS(): lấy tất cả các gói dịch vụ (phân quyền: quản lý, nhân viên)
    2. List<GoiDichVu> getGoiHoatDong(): lấy các gói có trạng thái là hoạt động. (phân quyền: quản lý, nhân viên)'
    3. void themGoiDichVu(GoiDichVu newgdv): thêm một gói dịch vụ. (phân quyền: quản lý)
    4. void suaGoiDichVu(GoiDichVu updategdv): sửa lại thông tin của gói dịch vụ. (phân quyền: quản lý)
    5. void XoaGoiDichVu(String maGDV): xóa một gói dịch vụ. (phân quyền: quản lý)
    6. void khoiPhucGDV(String maGDV): khôi phục lại một gói dịch vụ.(phân quyền: quản lý).
*/
public class GoiDichVuBUS{

    private final GoiDichVuDAO gdvDAO = new GoiDichVuDAO();

    // CHỨC NĂNG getAll
    public List<GoiDichVu> getAll() throws Exception{
        // check login
        PermissionHelper.requireLogin();

        // check phân quyền( quản lý/ nhân viên)
        PermissionHelper.requireNhanVien();

        // gọi xuống DAO
        List<GoiDichVu> result = new ArrayList<>();
        try{
            result = gdvDAO.getAll();
        }catch(Exception e){
            throw new Exception(e);
        }
        return result;
    }

    // CHỨC NĂNG getGoiHoatDong
    public List<GoiDichVu> getGoiHoatDong() throws Exception{
        // check login
        PermissionHelper.requireLogin();

        // check phân quyền( quản lý/ nhân viên)
        PermissionHelper.requireNhanVien();

        // gọi xuống DAO
        List<GoiDichVu> result = new ArrayList<>();
        try{
            result = gdvDAO.getAll();
            Iterator<GoiDichVu> list = result.iterator();
            while(list.hasNext()){
                GoiDichVu item = list.next();
                if(item.getTrangthai().equals("NGUNG")){
                    list.remove();
                }
            }
        }catch(Exception e){
            throw new Exception(e);
        }
        return result;
    }

    // KIỂM TRA VALIDATION
    private void checkVALIDATION(GoiDichVu gdv) throws Exception {
        // Kiểm tra Loại Gói (TenGoi)
        if (gdv.getLoaigoi() == null || gdv.getLoaigoi().trim().isEmpty()) {
            throw new Exception("Loại gói không được để trống!");
        }

        String loaiGoi = gdv.getLoaigoi().trim().toUpperCase();
        if (!loaiGoi.equals("THEOGIO") &&
                !loaiGoi.equals("THEONGAY") &&
                !loaiGoi.equals("THEOTUAN") &&
                !loaiGoi.equals("THEOTHANG")) {
            throw new Exception("Loại gói phải là: THEOGIO, THEONGAY, THEOTUAN hoặc THEOTHANG!");
        }

        // Kiểm tra Số Giờ (phải > 0)
        if (gdv.getSogio() <= 0) {
            throw new Exception("Số giờ phải lớn hơn 0!");
        }

        // Kiểm tra Số Ngày Hiệu Lực (phải > 0)
        if (gdv.getSongayhieuluc() <= 0) {
            throw new Exception("Số ngày hiệu lực phải lớn hơn 0!");
        }

        // Kiểm tra Giá Gốc (phải > 0)
        if (gdv.getGiagoc() <= 0) {
            throw new Exception("Giá gốc phải lớn hơn 0!");
        }

        // Kiểm tra Giá Gói (phải > 0)
        if (gdv.getGiagoc() <= 0) {
            throw new Exception("Giá gói phải lớn hơn 0!");
        }

        // Kiểm tra logic: Giá gói phải bé hơn bằng giá gốc.
        if (gdv.getGiagoi() > gdv.getGiagoc()) {
            throw new Exception("Giá gói không nên lớn hơn giá gốc!");
        }
    }

    // CHỨC NĂNG THÊM GÓI DỊCH VỤ
    public void themGoiDichVu(GoiDichVu newgdv) throws Exception{
        // check login
        PermissionHelper.requireLogin();

        // kiểm tra phân quyền.
        PermissionHelper.requireQuanLy();

        // check VALIDATION
        this.checkVALIDATION(newgdv);

        // gọi xuống DAO
        try{
            ConnectionManager.beginTransaction();

            boolean isSuccess = this.gdvDAO.insert(newgdv);
            if(isSuccess){
                ConnectionManager.commit();
                System.out.println("Thêm một gói dịch vụ thành công!");
            }
            else {
                System.out.println("Thêm một gói dịch vụ thất bại!");
            }
        }catch(Exception e){
            ConnectionManager.rollback();
            throw new Exception("Lỗi hệ thống: " + e.getMessage());
        }finally{
            ConnectionManager.close();
        }
    }

    // CHỨC NĂNG SỬA GÓI DỊCH VỤ
    public void suaGoiDichVu(GoiDichVu updategdv) throws Exception{
        // check login
        PermissionHelper.requireLogin();

        // kiểm tra phân quyền.
        PermissionHelper.requireQuanLy();

        // kiểm tra mã gói dịch vụ không được để trống.
        if( updategdv.getMagoi() == null){
            throw new Exception("Mã gói dịch vụ không được để trống!!!");
        }
        // kiểm tra sự tồn tại của mã đó
        if( this.gdvDAO.getByID(updategdv.getMagoi()) == null ){
            throw new Exception("Mã gói dịch vụ này không tồn tại!!!");
        }
        // check VALIDATION
        this.checkVALIDATION(updategdv);

        // gọi xuống DAO.
        try{
            ConnectionManager.beginTransaction();

            boolean isSuccess = this.gdvDAO.update(updategdv);
            if(isSuccess){
                ConnectionManager.commit();
                System.out.println("Sửa một gói dịch vụ thành công!");
            }
            else {
                System.out.println("Sửa một gói dịch vụ thất bại!");
            }
        }catch(Exception e){
            ConnectionManager.rollback();
            throw new Exception("Lỗi hệ thống: " + e.getMessage());
        }finally{
            ConnectionManager.close();
        }
    }

    // CHỨC NĂNG XÓA GÓI DỊCH VỤ
    public void xoaGoiDichVu(String maGDV) throws Exception{
        // check login
        PermissionHelper.requireLogin();

        // kiểm tra phân quyền.
        PermissionHelper.requireQuanLy();

        // kiểm tra mã gói dịch vụ không được để trống.
        if( maGDV == null){
            throw new Exception("Mã gói dịch vụ không được để trống!!!");
        }
        // kiểm tra sự tồn tại của mã đó
        if( this.gdvDAO.getByID(maGDV) ==  null){
            throw new Exception("Mã gói dịch vụ này không tồn tại!!!");
        }

        // gọi xuống DAO
        try{
            ConnectionManager.beginTransaction();

            boolean isSuccess = this.gdvDAO.delete(maGDV);
            if(isSuccess){
                ConnectionManager.commit();
                System.out.println("Xóa gói dịch vụ thành công!");
            }
            else {
                System.out.println("Xóa một gói dịch vụ thất bại!");
            }
        }catch(Exception e){
            ConnectionManager.rollback();
            throw new Exception("Lỗi hệ thống: " + e.getMessage());
        }finally{
            ConnectionManager.close();
        }
    }

    // CHỨC NĂNG KHÔI PHỤC LẠI GÓI DỊCH VỤ
    public void khoiPhucGDV(String maGDV) throws Exception{
        // check login
        PermissionHelper.requireLogin();

        // kiểm tra phân quyền.
        PermissionHelper.requireQuanLy();

        // kiểm tra mã gói dịch vụ không được để trống.
        if( maGDV == null){
            throw new Exception("Mã gói dịch vụ không được để trống!!!");
        }
        // kiểm tra sự tồn tại của mã đó
        if( this.gdvDAO.getByID(maGDV) ==  null){
            throw new Exception("Mã gói dịch vụ này không tồn tại!!!");
        }

        // gọi xuống DAO
        try{
            ConnectionManager.beginTransaction();

            boolean isSuccess = this.gdvDAO.cancelDelete(maGDV);
            if(isSuccess){
                ConnectionManager.commit();
                System.out.println("Khôi phục gói dịch vụ thành công!");
            }
            else {
                System.out.println("Khôi phục một gói dịch vụ thất bại!");
            }
        }catch(Exception e){
            ConnectionManager.rollback();
            throw new Exception("Lỗi hệ thống: " + e.getMessage());
        }finally{
            ConnectionManager.close();
        }
    }

}