package bus;

import entity.GoiDichVu;
import utils.PermissionHelper;
import dao.GoiDichVuDAO;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

public class GoiDichVuBUS {

    private final GoiDichVuDAO gdvDAO = new GoiDichVuDAO();

    // CHỨC NĂNG getAll (Phân quyền: Nhân viên trở lên)
    public List<GoiDichVu> getAll() throws Exception {
        // KÍCH HOẠT KIỂM TRA
        PermissionHelper.requireLogin();
        PermissionHelper.requireNhanVien();

        try {
            return gdvDAO.getAll();
        } catch (Exception e) {
            throw new Exception("Lỗi khi lấy danh sách gói dịch vụ: " + e.getMessage());
        }
    }

    // CHỨC NĂNG getGoiHoatDong (Phân quyền: Nhân viên trở lên)
    public List<GoiDichVu> getGoiHoatDong() throws Exception {
        // KÍCH HOẠT KIỂM TRA
        PermissionHelper.requireLogin();
        PermissionHelper.requireNhanVien();

        try {
            List<GoiDichVu> result = gdvDAO.getAll();
            Iterator<GoiDichVu> iterator = result.iterator();
            while (iterator.hasNext()) {
                GoiDichVu item = iterator.next();
                if ("NGUNG".equalsIgnoreCase(item.getTrangthai())) {
                    iterator.remove();
                }
            }
            return result;
        } catch (Exception e) {
            throw new Exception("Lỗi khi lọc gói hoạt động: " + e.getMessage());
        }
    }

    // KIỂM TRA VALIDATION
    private void checkVALIDATION(GoiDichVu gdv) throws Exception {
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

        if (gdv.getSogio() <= 0) throw new Exception("Số giờ phải lớn hơn 0!");
        if (gdv.getSongayhieuluc() <= 0) throw new Exception("Số ngày hiệu lực phải lớn hơn 0!");
        if (gdv.getGiagoc() <= 0) throw new Exception("Giá gốc phải lớn hơn 0!");
        if (gdv.getGiagoi() <= 0) throw new Exception("Giá gói phải lớn hơn 0!");

        if (gdv.getGiagoi() > gdv.getGiagoc()) {
            throw new Exception("Giá gói không được lớn hơn giá gốc!");
        }
    }

    // CHỨC NĂNG THÊM GÓI DỊCH VỤ (Phân quyền: Quản lý)
    public void themGoiDichVu(GoiDichVu newgdv) throws Exception {
        // KÍCH HOẠT KIỂM TRA
        PermissionHelper.requireLogin();
        PermissionHelper.requireQuanLy();

        this.checkVALIDATION(newgdv);

        try {
            boolean isSuccess = this.gdvDAO.insert(newgdv);
            if (!isSuccess) throw new Exception("Thêm một gói dịch vụ thất bại!");
            System.out.println("Thêm một gói dịch vụ thành công!");
        } catch (Exception e) {
            throw new Exception("Lỗi hệ thống khi thêm: " + e.getMessage());
        }
    }

    // CHỨC NĂNG SỬA GÓI DỊCH VỤ (Phân quyền: Quản lý)
    public void suaGoiDichVu(GoiDichVu updategdv) throws Exception {
        // KÍCH HOẠT KIỂM TRA
        PermissionHelper.requireLogin();
        PermissionHelper.requireQuanLy();

        if (updategdv.getMagoi() == null) throw new Exception("Mã gói dịch vụ không được để trống!");

        if (this.gdvDAO.getByID(updategdv.getMagoi()) == null) {
            throw new Exception("Mã gói dịch vụ này không tồn tại!");
        }

        this.checkVALIDATION(updategdv);

        try {
            boolean isSuccess = this.gdvDAO.update(updategdv);
            if (!isSuccess) throw new Exception("Sửa một gói dịch vụ thất bại!");
            System.out.println("Sửa một gói dịch vụ thành công!");
        } catch (Exception e) {
            throw new Exception("Lỗi hệ thống khi sửa: " + e.getMessage());
        }
    }

    // CHỨC NĂNG XÓA GÓI DỊCH VỤ (Phân quyền: Quản lý)
    public void xoaGoiDichVu(String maGDV) throws Exception {
        // KÍCH HOẠT KIỂM TRA
        PermissionHelper.requireLogin();
        PermissionHelper.requireQuanLy();

        if (maGDV == null) throw new Exception("Mã gói dịch vụ không được để trống!");
        if (this.gdvDAO.getByID(maGDV) == null) throw new Exception("Mã gói dịch vụ này không tồn tại!");

        try {
            boolean isSuccess = this.gdvDAO.delete(maGDV);
            if (!isSuccess) throw new Exception("Xóa gói dịch vụ thất bại!");
            System.out.println("Xóa gói dịch vụ thành công!");
        } catch (Exception e) {
            throw new Exception("Lỗi hệ thống khi xóa: " + e.getMessage());
        }
    }

    // CHỨC NĂNG KHÔI PHỤC LẠI GÓI DỊCH VỤ (Phân quyền: Quản lý)
    public void khoiPhucGDV(String maGDV) throws Exception {
        // KÍCH HOẠT KIỂM TRA
        PermissionHelper.requireLogin();
        PermissionHelper.requireQuanLy();

        if (maGDV == null) throw new Exception("Mã gói dịch vụ không được để trống!");
        if (this.gdvDAO.getByID(maGDV) == null) throw new Exception("Mã gói dịch vụ này không tồn tại!");

        try {
            boolean isSuccess = this.gdvDAO.cancelDelete(maGDV);
            if (!isSuccess) throw new Exception("Khôi phục gói dịch vụ thất bại!");
            System.out.println("Khôi phục gói dịch vụ thành công!");
        } catch (Exception e) {
            throw new Exception("Lỗi hệ thống khi khôi phục: " + e.getMessage());
        }
    }
}