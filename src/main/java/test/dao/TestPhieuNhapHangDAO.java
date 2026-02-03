package test.dao;

import dao.PhieuNhapHangDAO;
import entity.ChiTietPhieuNhap;
import entity.PhieuNhapHang;

import java.util.ArrayList;
import java.util.List;

public class TestPhieuNhapHangDAO {


    public static void main(String[] args) {


        PhieuNhapHangDAO pnDAO = new PhieuNhapHangDAO();

        String maNCC = TestUtil.anyMaNCC();
        String maNV  = TestUtil.anyMaNV();
        String maDV  = TestUtil.anyMaDV();

        if (maNCC == null || maNV == null || maDV == null) {
            System.out.println("Thiếu dữ liệu test.");
            System.out.println("maNCC=" + maNCC + ", maNV=" + maNV + ", maDV=" + maDV);
            return;
        }

        Integer tonTruoc = TestUtil.soLuongTonOf(maDV);
        System.out.println("Ton kho truoc (" + maDV + "): " + tonTruoc);

        // ===== tạo chi tiết =====
        List<ChiTietPhieuNhap> list = new ArrayList<>();
        ChiTietPhieuNhap ct = new ChiTietPhieuNhap();
        ct.setMaDV(maDV);
        ct.setSoLuong(3);
        ct.setGiaNhap(7000.0);
        list.add(ct);

        // 1) Tạo phiếu nhập
        String maPN = pnDAO.createPhieuNhap(maNCC, maNV, list);
        System.out.println("✅ Created PN = " + maPN);

        PhieuNhapHang pn1 = pnDAO.getById(maPN);
        System.out.println("After create: TrangThai=" + pn1.getTrangThai()
                + ", TongTien=" + pn1.getTongTien()
                + ", SoCT=" + (pn1.getChiTietList() == null ? 0 : pn1.getChiTietList().size()));

        Integer tonSauTao = TestUtil.soLuongTonOf(maDV);
        System.out.println("Ton kho sau tao (phai khong doi): " + tonSauTao);

        // 2) Duyệt phiếu
        pnDAO.duyetPhieu(maPN);
        PhieuNhapHang pn2 = pnDAO.getById(maPN);
        System.out.println("After duyet: TrangThai=" + pn2.getTrangThai());

        Integer tonSauDuyet = TestUtil.soLuongTonOf(maDV);
        System.out.println("Ton kho sau duyet (phai tang): " + tonSauDuyet);

        // 3) Hủy phiếu
        pnDAO.huyPhieu(maPN);
        PhieuNhapHang pn3 = pnDAO.getById(maPN);
        System.out.println("After huy: TrangThai=" + pn3.getTrangThai());

        Integer tonSauHuy = TestUtil.soLuongTonOf(maDV);
        System.out.println("Ton kho sau huy (phai ve gan nhu cu): " + tonSauHuy);

        System.out.println("=== TEST PhieuNhapHangDAO OK ===");

        // (tuỳ chọn) nếu nhóm bạn muốn đóng kết nối sau khi test xong:
        //  DBConnection.closeConnection();
    }
}