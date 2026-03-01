package test.dao;

import bus.NhapHangBUS;
import bus.SessionManager;
import dao.DBConnection;
import entity.ChiTietPhieuNhap;
import entity.NhanVien;
import test.dao.TestUtil;

import java.util.ArrayList;
import java.util.List;

public class TestNhapHangBUS {

    private static void loginQuanLyWithMaNVInDB() throws Exception {
        // Lấy MaNV có thật trong DB để tránh lỗi FK khi insert phieunhaphang
        String maNV = TestUtil.anyMaNV();
        if (maNV == null) {
            throw new Exception("Không tìm thấy MaNV trong DB để test. Hãy thêm dữ liệu bảng nhanvien/phieunhaphang.");
        }
        NhanVien nv = new NhanVien();
        nv.setManv(maNV);
        nv.setChucvu("QUANLY");
        SessionManager.setCurrentUser(nv);
    }

    public static void main(String[] args) {
        try {
            loginQuanLyWithMaNVInDB();
            NhapHangBUS bus = new NhapHangBUS();
            // ===== Chuẩn bị dữ liệu test =====
            String maNCC = TestUtil.anyMaNCC();
            String maDV  = TestUtil.anyMaDV();

            if (maNCC == null || maDV == null) {
                System.out.println(" Thiếu dữ liệu test. Cần có nhacungcap và dichvu trong DB.");
                return;
            }

            Integer tonTruoc = TestUtil.soLuongTonOf(maDV);
            System.out.println("Ton kho truoc (" + maDV + ") = " + tonTruoc);

            // ===== Tạo chi tiết =====
            List<ChiTietPhieuNhap> ctList = new ArrayList<>();
            ChiTietPhieuNhap ct = new ChiTietPhieuNhap();
            ct.setMaDV(maDV);
            ct.setSoLuong(3);
            ct.setGiaNhap(7000.0);
            ctList.add(ct);

            // ===== 1) Tạo phiếu nhập (CHODUYET, chưa cộng tồn kho) =====
            System.out.println("\n=== 1) taoPhieuNhap() ===");
            String maPN = bus.taoPhieuNhap(maNCC, ctList);
            System.out.println(" Created MaPhieuNhap = " + maPN);

            Integer tonSauTao = TestUtil.soLuongTonOf(maDV);
            System.out.println("Ton kho sau tao (phai KHONG DOI) = " + tonSauTao);

            // ===== 2) Duyệt phiếu (cộng tồn kho, TrangThai = DANHAP) =====
            System.out.println("\n=== 2) duyetPhieu() ===");
            bus.duyetPhieu(maPN);

            Integer tonSauDuyet = TestUtil.soLuongTonOf(maDV);
            System.out.println("Ton kho sau duyet (phai TANG) = " + tonSauDuyet);

            // ===== 3) Hủy phiếu (nếu đã DANHAP -> trừ lại tồn kho, TrangThai=DAHUY) =====
//            System.out.println("\n=== 3) huyPhieu() ===");
//            bus.huyPhieu(maPN);
//
//            Integer tonSauHuy = TestUtil.soLuongTonOf(maDV);
//            System.out.println("Ton kho sau huy (phai VE GAN nhu cu) = " + tonSauHuy);
//
//            System.out.println("\n=== TEST NhapHangBUS OK ===");

        } catch (Exception e) {
            System.out.println("TEST FAIL: " + e.getMessage());
        } finally {
            SessionManager.logout();
            DBConnection.closeConnection();
        }
    }
}
