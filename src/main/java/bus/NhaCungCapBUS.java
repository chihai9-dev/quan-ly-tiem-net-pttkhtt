package bus;

import dao.NhaCungCapDAO;
import entity.NhaCungCap;
import entity.NhanVien;
import untils.SessionManager;

import java.util.List;

public class NhaCungCapBUS {

    private final NhaCungCapDAO dao = new NhaCungCapDAO();

    private NhanVien requireQuanLy() throws Exception {
        if (!SessionManager.isLoggedIn()) throw new Exception("Chưa đăng nhập");

        NhanVien current = SessionManager.getCurrentNhanVien();
        if (current == null) throw new Exception("Tài khoản không có quyền (không phải nhân viên)");

        if (!SessionManager.hasAdminPermission()) throw new Exception("Không có quyền thực hiện");
        return current;
    }

    public List<NhaCungCap> getAllNhaCungCap() throws Exception {
        requireQuanLy();
        return dao.getAll(true);
    }

    public List<NhaCungCap> getNhaCungCapHoatDong() throws Exception {
        requireQuanLy();
        return dao.getAll(false);
    }

    public String themNhaCungCap(NhaCungCap ncc) throws Exception {
        requireQuanLy();
        return dao.insert(ncc);
    }

    public boolean suaNhaCungCap(NhaCungCap ncc) throws Exception {
        requireQuanLy();
        return dao.update(ncc);
    }

    public boolean xoaNhaCungCap(String maNCC) throws Exception {
        requireQuanLy();
        return dao.softDelete(maNCC);
    }
}