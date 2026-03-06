package test.dao;

import bus.NhapHangBUS;
import dao.DBConnection;
import entity.ChiTietPhieuNhap;
import entity.NhanVien;
import utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class TestNhapHangBUS {

    private static void loginQuanLyWithMaNVInDB() throws Exception {
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

            String maNCC = TestUtil.anyMaNCC();
            String maDV  = TestUtil.anyMaDV();

            if (maNCC == null || maDV == null) {
                System.out.println(" Thiếu dữ liệu test. Cần có nhacungcap và dichvu trong DB.");
                return;
            }

            Integer tonTruoc = TestUtil.soLuongTonOf(maDV);
            System.out.println("Ton kho truoc (" + maDV + ") = " + tonTruoc);

            List<ChiTietPhieuNhap> ctList = new ArrayList<>();
            ChiTietPhieuNhap ct = new ChiTietPhieuNhap();
            ct.setMaDV(maDV);
            ct.setSoLuong(3);
            ct.setGiaNhap(7000.0);
            ctList.add(ct);

            System.out.println("\n=== 1) taoPhieuNhap() ===");
            String maPN = bus.taoPhieuNhap(maNCC, ctList);
            System.out.println(" Created MaPhieuNhap = " + maPN);

            Integer tonSauTao = TestUtil.soLuongTonOf(maDV);
            System.out.println("Ton kho sau tao (phai KHONG DOI) = " + tonSauTao);

            System.out.println("\n=== 2) duyetPhieu() ===");
            bus.duyetPhieu(maPN);

            Integer tonSauDuyet = TestUtil.soLuongTonOf(maDV);
            System.out.println("Ton kho sau duyet (phai TANG) = " + tonSauDuyet);

        } catch (Exception e) {
            System.out.println("TEST FAIL: " + e.getMessage());
        } finally {
            SessionManager.logout();
            DBConnection.closeConnection();
        }
    }
}