package bus;

import dao.PhieuNhapHangDAO;
import entity.ChiTietPhieuNhap;
import entity.NhanVien;
import entity.PhieuNhapHang;
import utils.SessionManager;

import java.util.List;

public class NhapHangBUS {

    private final PhieuNhapHangDAO pnDAO = new PhieuNhapHangDAO();

    private NhanVien requireQuanLy() throws Exception {
        if (!SessionManager.isLoggedIn()) throw new Exception("Chưa đăng nhập");

        NhanVien current = SessionManager.getCurrentNhanVien();
        if (current == null) throw new Exception("Tài khoản không có quyền (không phải nhân viên)");

        if (!SessionManager.hasAdminPermission()) throw new Exception("Không có quyền thực hiện");
        return current;
    }

    public String taoPhieuNhap(String maNCC, List<ChiTietPhieuNhap> chiTietList) throws Exception {
        NhanVien current = requireQuanLy();
        String maNV = current.getManv();

        if (maNCC == null || maNCC.trim().isEmpty()) throw new Exception("Nhà cung cấp không hợp lệ");
        if (chiTietList == null || chiTietList.isEmpty()) throw new Exception("Chi tiết phiếu nhập không được rỗng");

        return pnDAO.createPhieuNhap(maNCC, maNV, chiTietList);
    }

    public void duyetPhieu(String maPhieu) throws Exception {
        requireQuanLy();
        if (maPhieu == null || maPhieu.trim().isEmpty()) throw new Exception("Mã phiếu không hợp lệ");
        pnDAO.duyetPhieu(maPhieu);
    }

    public void huyPhieu(String maPhieu) throws Exception {
        requireQuanLy();
        if (maPhieu == null || maPhieu.trim().isEmpty()) throw new Exception("Mã phiếu không hợp lệ");
        pnDAO.huyPhieu(maPhieu);
    }

    public List<PhieuNhapHang> getAllPhieuNhap() throws Exception {
        requireQuanLy();
        return pnDAO.getAll();
    }
}