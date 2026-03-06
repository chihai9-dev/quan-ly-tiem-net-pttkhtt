package bus;

import dao.ThongkeDAO;
import entity.NhanVien;
import utils.SessionManager;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThongKeBUS {

    private final ThongkeDAO thongkeDAO = new ThongkeDAO();

    private NhanVien requireQuanLy() throws Exception {
        if (!SessionManager.isLoggedIn()) throw new Exception("Chưa đăng nhập");

        NhanVien current = SessionManager.getCurrentNhanVien();
        if (current == null) throw new Exception("Tài khoản không có quyền (không phải nhân viên)");

        if (!SessionManager.hasAdminPermission()) throw new Exception("Không có quyền thực hiện");
        return current;
    }

    private NhanVien requireQuanLyOrNhanVien() throws Exception {
        if (!SessionManager.isLoggedIn()) throw new Exception("Chưa đăng nhập");

        NhanVien current = SessionManager.getCurrentNhanVien();
        if (current == null) throw new Exception("Tài khoản không có quyền (không phải nhân viên)");

        if (!SessionManager.hasStaffPermission()) throw new Exception("Không có quyền thực hiện");
        return current;
    }

    public Map<String, Object> thongKeDoanhThu(LocalDate tuNgay, LocalDate denNgay) throws Exception {
        requireQuanLy();

        if (tuNgay == null || denNgay == null) throw new Exception("Ngày thống kê không hợp lệ");
        if (tuNgay.isAfter(denNgay)) throw new Exception("Từ ngày phải nhỏ hơn hoặc bằng đến ngày");

        Map<String, Object> hoaDonPart = thongkeDAO.thongKeDoanhThuTongHop(tuNgay, denNgay);
        double tongDoanhThu = (double) hoaDonPart.get("TongDoanhThu");
        double tongTienGioChoi = (double) hoaDonPart.get("TongTienGioChoi");
        double tongTienDichVu = (double) hoaDonPart.get("TongTienDichVu");
        int soHoaDon = (int) hoaDonPart.get("SoHoaDon");

        long soNgay = ChronoUnit.DAYS.between(tuNgay, denNgay) + 1;
        double doanhThuTB = (soNgay <= 0) ? 0.0 : (tongDoanhThu / soNgay);

        double tongNhapHang = thongkeDAO.tongNhapHang(tuNgay, denNgay);
        double loiNhuan = tongDoanhThu - tongNhapHang;

        Map<String, Object> result = new HashMap<>();
        result.put("TuNgay", tuNgay);
        result.put("DenNgay", denNgay);
        result.put("TongDoanhThu", tongDoanhThu);
        result.put("TongTienGioChoi", tongTienGioChoi);
        result.put("TongTienDichVu", tongTienDichVu);
        result.put("TongNhapHang", tongNhapHang);
        result.put("LoiNhuan", loiNhuan);
        result.put("SoHoaDon", soHoaDon);
        result.put("SoNgay", (int) soNgay);
        result.put("DoanhThuTrungBinh", doanhThuTB);

        return result;
    }

    public Map<String, Object> thongKeDoanhThuTheoThang(int thang, int nam) throws Exception {
        requireQuanLy();

        if (thang < 1 || thang > 12) throw new Exception("Tháng không hợp lệ");
        if (nam < 2000) throw new Exception("Năm không hợp lệ");

        YearMonth ym = YearMonth.of(nam, thang);
        LocalDate tu = ym.atDay(1);
        LocalDate den = ym.atEndOfMonth();

        return thongKeDoanhThu(tu, den);
    }

    public List<Map<String, Object>> thongKeDichVuBanChay(LocalDate tuNgay, LocalDate denNgay, int top) throws Exception {
        requireQuanLy();

        if (tuNgay == null || denNgay == null) throw new Exception("Ngày thống kê không hợp lệ");
        if (tuNgay.isAfter(denNgay)) throw new Exception("Từ ngày phải nhỏ hơn hoặc bằng đến ngày");

        return thongkeDAO.thongKeDichVuBanChay(tuNgay, denNgay, top);
    }

    public Map<String, Object> thongKeTongQuan() throws Exception {
        requireQuanLyOrNhanVien();
        return thongkeDAO.thongKeTongQuan();
    }

    public void xuatBaoCaoExcel(LocalDate tuNgay, LocalDate denNgay) {
        throw new UnsupportedOperationException("Chưa implement (tuỳ chọn).");
    }
}