package test.dao;

import dao.ChiTietPhieuNhapDAO;
import dao.DBConnection;
import dao.PhieuNhapHangDAO;
import entity.ChiTietPhieuNhap;

import java.util.ArrayList;
import java.util.List;

public class TestChiTietPhieuNhapDAO {
    public static void main(String[] args) {
        ChiTietPhieuNhapDAO ctDAO = new ChiTietPhieuNhapDAO();
        PhieuNhapHangDAO pnDAO = new PhieuNhapHangDAO();

        String maNCC = TestUtil.anyMaNCC();
        String maNV  = TestUtil.anyMaNV();
        String maDV  = TestUtil.anyMaDV();

        if (maNCC == null || maNV == null || maDV == null) {
            System.out.println("Thiếu dữ liệu test.");
            System.out.println("maNCC=" + maNCC + ", maNV=" + maNV + ", maDV=" + maDV);
            System.out.println("=> DB cần có dữ liệu ở nhacungcap, dichvu và nhanvien.");
            return;
        }

        // ===== Tạo 1 chi tiết =====
        List<ChiTietPhieuNhap> list = new ArrayList<>();
        ChiTietPhieuNhap ct = new ChiTietPhieuNhap();
        ct.setMaDV(maDV);
        ct.setSoLuong(2);
        ct.setGiaNhap(5000.0); //  double
        list.add(ct);

        // ===== Tạo phiếu nhập =====
        String maPN = pnDAO.createPhieuNhap(maNCC, maNV, list);
        System.out.println(" Created PN = " + maPN);

        // ===== Lấy lại chi tiết theo mã phiếu =====
        List<ChiTietPhieuNhap> cts =
                ctDAO.getByMaPhieuNhap(DBConnection.getConnection(), maPN);

        System.out.println("So CT = " + cts.size());

        for (ChiTietPhieuNhap x : cts) {
            System.out.println(
                    x.getMaCTPN() + " | " + x.getMaPhieuNhap() + " | " + x.getMaDV()
                            + " | SL=" + x.getSoLuong()
                            + " | Gia=" + x.getGiaNhap()
                            + " | TT=" + x.getThanhTien()
            );
        }

        System.out.println("=== TEST ChiTietPhieuNhapDAO OK ===");
    }
}
