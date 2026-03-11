package bus;

import entity.DichVu;
import utils.PermissionHelper;
import dao.DichVuDAO;

import java.util.Iterator;
import java.util.List;

public class DichVuBUS {

    private final DichVuDAO dvDAO = new DichVuDAO();

    // LẤY TẤT CẢ DỊCH VỤ (Phân quyền: Nhân viên trở lên)
    public List<DichVu> getAll() throws Exception {
        // KÍCH HOẠT PHÂN QUYỀN
        PermissionHelper.requireLogin();
        PermissionHelper.requireNhanVien();

        List<DichVu> result = dvDAO.getAll();
        System.out.println("Đã lấy được " + result.size() + " dịch vụ.");
        return result;
    }

    // LẤY CÁC DỊCH VỤ CÒN HÀNG (Phân quyền: Khách hàng)
    public List<DichVu> getDichVuConHang() throws Exception {
        // KÍCH HOẠT PHÂN QUYỀN
        PermissionHelper.requireLogin();
        PermissionHelper.requireKhachHang();

        List<DichVu> list = dvDAO.getAll();
        Iterator<DichVu> it = list.iterator();
        while(it.hasNext()) {
            if (it.next().getSoluongton() <= 0) it.remove();
        }
        return list;
    }

    // CHUẨN HÓA TÊN DỊCH VỤ
    private String chuanHoaTen(String ten) {
        if (ten == null) return "";
        return ten.trim().replaceAll("\\s+", " ").toLowerCase();
    }

    // KIỂM TRA TÊN KHÔNG ĐƯỢC TRÙNG VỚI CÁC DỊCH VỤ ĐÃ CÓ
    private boolean checkTrungTenDichVu(String tenDV, String oldName) {
        for (DichVu item : dvDAO.getAll()) {
            if (oldName != null && oldName.equals(item.getTendv())) continue;
            if (chuanHoaTen(tenDV).equals(chuanHoaTen(item.getTendv()))) return false;
        }
        return true;
    }

    // KIỂM TRA VALIDATION (dùng cho insert và update)
    private void validateDichVu(DichVu dv, String oldName) throws Exception {
        if (dv.getTendv() == null || dv.getTendv().trim().isEmpty())
            throw new Exception("Tên dịch vụ không được để trống!");

        if (!checkTrungTenDichVu(dv.getTendv(), oldName))
            throw new Exception("Tên dịch vụ này đã tồn tại!");

        if (dv.getLoaidv() == null || dv.getLoaidv().trim().isEmpty())
            throw new Exception("Loại dịch vụ không được để trống!");

        if (!dv.getLoaidv().equals("DOUONG") && !dv.getLoaidv().equals("THUCPHAM") && !dv.getLoaidv().equals("KHAC"))
            dv.setLoaidv("KHAC");

        if (dv.getDongia() <= 0.0)
            throw new Exception("Đơn giá phải lớn hơn 0!");
    }

    // THÊM DỊCH VỤ (Phân quyền: Quản lý)
    public void themDichVu(DichVu newDichVu) throws Exception {
        // KÍCH HOẠT PHÂN QUYỀN
        PermissionHelper.requireLogin();
        PermissionHelper.requireQuanLy();

        validateDichVu(newDichVu, "");

        boolean ok = dvDAO.insert(newDichVu);
        if (!ok) throw new Exception("Thêm dịch vụ không thành công!");
        System.out.println("Thêm dịch vụ thành công.");
    }

    // SỬA DỊCH VỤ (Phân quyền: Quản lý)
    public void suaDichVu(DichVu updateDV) throws Exception {
        // KÍCH HOẠT PHÂN QUYỀN
        PermissionHelper.requireLogin();
        PermissionHelper.requireQuanLy();

        DichVu existing = dvDAO.getByID(updateDV.getMadv());
        if (existing == null) throw new Exception("Không tìm thấy mã dịch vụ cần sửa!");

        validateDichVu(updateDV, existing.getTendv());

        boolean ok = dvDAO.update(updateDV);
        if (!ok) throw new Exception("Sửa dịch vụ không thành công!");
        System.out.println("Sửa dịch vụ thành công.");
    }

    // XÓA DỊCH VỤ => CHUYỂN SANG TRẠNG THÁI NGỪNG BÁN (Phân quyền: Quản lý)
    public void xoaDichVu(String maDV) throws Exception {
        // KÍCH HOẠT PHÂN QUYỀN
        PermissionHelper.requireLogin();
        PermissionHelper.requireQuanLy();

        if (maDV == null) throw new Exception("Mã dịch vụ không được để trống!");
        if (dvDAO.getByID(maDV) == null) throw new Exception("Không tồn tại mã dịch vụ này!");

        boolean ok = dvDAO.delete(maDV);
        if (!ok) throw new Exception("Xóa dịch vụ không thành công!");
        System.out.println("Xóa dịch vụ thành công.");
    }

    // KHÔI PHỤC DỊCH VỤ (Phân quyền: Quản lý)
    public void khoiPhucLaiDichVu(String maDV) throws Exception {
        // KÍCH HOẠT PHÂN QUYỀN
        PermissionHelper.requireLogin();
        PermissionHelper.requireQuanLy();

        if (maDV == null) throw new Exception("Mã dịch vụ không được để trống!");
        DichVu check = dvDAO.getByID(maDV);
        if (check == null) throw new Exception("Không tồn tại mã dịch vụ này!");

        boolean ok = dvDAO.cancelDelete(check);
        if (!ok) throw new Exception("Khôi phục dịch vụ không thành công!");
        System.out.println("Khôi phục dịch vụ thành công.");
    }
}