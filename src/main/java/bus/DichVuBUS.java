package bus;
import entity.DichVu;
import dao.DichVuDAO;
import dao.DBConnection;
import dao.NhanVienDAO;
import dao.ConnectionManager;
import untils.*;

import java.sql.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

/* CÁC METHOD
    1. List<DichVu> getAll(): lấy tất cả các dịch vụ. (Phân quyền: User)
    2. List<DichVu> getDichVuConHang(): lấy các dịch vụ còn hàng. (kể cả NGƯNG BÁN). (Phân quyền: KHACHHANG)
    3. void themDichVu(DichVu newDichVu): thêm một dịch vụ. (Phân quyền: QUANLY)
    4. void suaDichVu(DichVu updateDV): cập nhập thông tin dịch vụ. (Phân quyền: QUANLY)
    5. void xoaDichVu(String maDV): xóa một dịch vụ. (Phân quyền: QUANLY)
    6. void khoPhucLaiDichVu(String maDV): khôi phục lại trạng thái của dịch vụ. (Phân quyền: QUANLY)

    CÁC METHOD PRIVATE.
    1. String chuanHoaTen(String ten): chuẩn hóa tên của dịch vụ.
    2. boolean checkTrungTenDichVu(String tenDV, String oldName): kiểm tra tên bị trùng.
    3. void ValidationDichVu(DichVu checkDV, String oldName): kiểm tra validation. (dùng cho update và insert).
*/
public class DichVuBUS{

    private final DichVuDAO dvDAO = new DichVuDAO();

    // LẤY THÔNG TIN TẤT CẢ CÁC DỊCH VỤ.
    public List<DichVu> getAll() throws Exception{
        // check login
        PermissionHelper.requireLogin();
        // kiểm tra phân quyền (quản lí/ nhân viên)
        PermissionHelper.requireNhanVien();

        // gọi xuống DAO
        List<DichVu> result = new ArrayList<>();
        result = this.dvDAO.getAll();
        System.out.println("Đã lấy được " + result.size() + " dịch vụ.");
        return result;
    }

    // LẤY CÁC DỊCH VỤ CÒN HÀNG
    public List<DichVu> getDichVuConHang() throws Exception{
        // check login
        PermissionHelper.requireLogin();
        // kiểm tra phân quyền (user)
        PermissionHelper.requireKhachHang();

        // lọc các dịch vụ có số lượng <= 0
        List<DichVu> list = new ArrayList<>();
        list = this.dvDAO.getAll();
        // chuyển qua dùng iterator để cập nhập vị trí tốt hơn
        Iterator<DichVu> result = list.iterator();
        while(result.hasNext()){
            DichVu item = result.next();
            if(item.getSoluongton()<=0){
                result.remove();
            }
        }
        return list;
    }

    // CHUẨN HÓA TÊN CỦA DỊCH VỤ
    private String chuanHoaTen(String ten) {
        if (ten == null) return "";
        return ten.trim().replaceAll("\\s+", " ").toLowerCase();
    }

    // KIỂM TRA TÊN KHI THÊM HOẶC SỬA KHÔNG ĐƯỢC TRÙNG VỚI CÁC TÊN DỊCH VỤ ĐÃ CÓ.
    private boolean checkTrungTenDichVu(String tenDV, String oldName){
       List<DichVu> listCurrent = new ArrayList<>();
       listCurrent = dvDAO.getAll();
        for( DichVu item : listCurrent ){
            if( oldName.equals(item.getTendv()) ){ continue; }
            if( chuanHoaTen(tenDV).equals(chuanHoaTen(item.getTendv())) ){
                return false;
            }
        }
        return true;
    }

    // KIỂM TRA VALAIDATION
    private void ValidationDichVu(DichVu checkDV, String oldName) throws Exception{

        // kiểm tra tên dịch vụ
        if( checkDV.getTendv() == null ){
            throw new Exception("Tên dịch vụ không được để trống");
        }
        else if( !this.checkTrungTenDichVu(checkDV.getTendv(), oldName) ){ throw new Exception("Tên dịch vụ này đã có rồi!!!"); }

        // kiểm tra loại dịch vụ không được để trống
        if( checkDV.getLoaidv() == null ){ throw new Exception("Loại dịch vụ không được để trống!!!"); }
        else if( checkDV.getLoaidv().equals("DOUONG") && checkDV.getLoaidv().equals("THUCPHAM")
                && checkDV.getLoaidv().equals("KHAC") );{
            // vì nó không thuộc loại nào trong này hết nên chuyển thành "KHAC" luôn
            checkDV.setLoaidv("KHAC");
        }

        // kiểm tra giá dịch vụ
        if( checkDV.getDongia() <= 0.0){ throw new Exception("Đơn giá không được nhỏ hơn hoặc bằng 0!!!"); }

        // gán trạng thái là HETHANG và số lượng tồn bằng 0 (ép buộc)
        checkDV.setSoluongton(0);
        checkDV.setTrangthai("HETHANG");
    }

    // THÊM MỘT DỊCH VỤ
    public void themDichVu(DichVu newDichVu) throws Exception{
        // check login
        PermissionHelper.requireLogin();
        // kiểm tra phân quyền (quản lý)
        PermissionHelper.requireQuanLy();

        // gọi method VALIDATION
        this.ValidationDichVu(newDichVu, "");

        // GỌI DAO ĐỂ THÊM DỊCH VỤ
        try{
            ConnectionManager.beginTransaction();

            boolean isSuccess = dvDAO.insert(newDichVu);  // insert sẽ trả về true/false

            if(isSuccess){  // nếu insert thành công
                ConnectionManager.commit();
                System.out.println("Thêm dịch vụ thành công");
            }
            else{
                throw new Exception("Thêm dịch vụ không thành công");
            }
        }catch(Exception e){
            ConnectionManager.rollback();
            throw new Exception("Lỗi hệ thống: " + e.getMessage());
        }finally{
            ConnectionManager.close();
        }
    }

    // CẬP NHẬP THÔNG TIN SẢN PHẨM
    public void suaDichVu(DichVu updateDV) throws Exception{
        // check login
        PermissionHelper.requireLogin();
        // kiểm tra phân quyền( quản lý)
        PermissionHelper.requireQuanLy();

        // kiểm tra xem mã dịch vụ này có không
        DichVu check = new DichVu();
        check = dvDAO.getByID(updateDV.getMadv());
        if(check == null){
            throw new Exception("Không tìm thấy mã dịch vụ này để sửa");
        }

        // check VALIDATION
        this.ValidationDichVu(updateDV, check.getTendv());

        // gọi xuống DAO
        try{
            ConnectionManager.beginTransaction();

            boolean isSuccess = dvDAO.update(updateDV);  // insert sẽ trả về true/false

            if(isSuccess){  // nếu insert thành công
                ConnectionManager.commit();
                System.out.println("Sửa dịch vụ thành công");
            }
            else{
                throw new Exception("Sửa dịch vụ không thành công");
            }
        }catch(Exception e){
            ConnectionManager.rollback();
            throw new Exception("Lỗi hệ thống: " + e.getMessage());
        }finally{
            ConnectionManager.close();
        }
    }

    // XÓA DỊCH VỤ => CHUYỂN SANG TRẠNG THÁI NGỪNG BÁN
    public void xoaDichVu(String maDV) throws Exception{
        // check login
        PermissionHelper.requireLogin();
        // kiểm tra phân quyền (quản lý)
        PermissionHelper.requireQuanLy();

        // VALIDATION
        if( maDV == null){ throw new Exception("Mã dịch vụ cần xóa không được để trống!!!"); }
        // kiểm tra mã cần xóa có tồn tại không
        if( dvDAO.getByID(maDV) == null ){ throw new Exception("Không tồn tại mã dịch vụ này!!!"); }

        // gọi xuống DAO
        try{
            ConnectionManager.beginTransaction();

            boolean isSuccess = dvDAO.delete(maDV); // insert sẽ trả về true/false

            if(isSuccess){  // nếu insert thành công
                ConnectionManager.commit();
                System.out.println("Xóa dịch vụ thành công");
            }
            else{
                throw new Exception("Xóa dịch vụ không thành công");
            }
        }catch(Exception e){
            ConnectionManager.rollback();
            throw new Exception("Lỗi hệ thống: " + e.getMessage());
        }finally{
            ConnectionManager.close();
        }
    }

    // KHÔI PHỤC LẠI DỊCH VỤ
    public void khoiPhucLaiDichVu(String maDV) throws Exception{
        // check login
        PermissionHelper.requireLogin();
        // kiểm tra phân quyền
        PermissionHelper.requireQuanLy();

        // VALIDATION
        if( maDV == null){ throw new Exception("Mã dịch vụ cần khôi phục không được để trống!!!"); }
        // kiểm tra mã cần xóa có tồn tại không
        DichVu check = new DichVu();
        check = dvDAO.getByID(maDV);
        if( check == null ){ throw new Exception("Không tồn tại mã dịch vụ này!!!"); }

        // gọi xuống DAO
        try{
            ConnectionManager.beginTransaction();

            boolean isSuccess = dvDAO.cancelDelete(check);

            if(isSuccess){
                ConnectionManager.commit();
                System.out.println("Khôi phục dịch vụ thành công");
            }
            else{
                throw new Exception("Khôi phục dịch vụ không thành công");
            }
        }catch(Exception e){
            ConnectionManager.rollback();
            throw new Exception("Lỗi hệ thống: " + e.getMessage());
        }finally{
            ConnectionManager.close();
        }
    }
}