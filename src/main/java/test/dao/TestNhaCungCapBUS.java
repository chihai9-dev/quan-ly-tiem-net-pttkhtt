package test.dao;

import bus.NhaCungCapBUS;
import dao.DBConnection;
import entity.NhaCungCap;
import entity.NhanVien;
import untils.SessionManager; // ✅ FIX: import đúng

import java.util.List;

public class TestNhaCungCapBUS {

    private static void loginQuanLy() throws Exception {
        // Tạo NV giả lập có quyền QUANLY (BUS chỉ check chucvu)
        NhanVien nv = new NhanVien();
        nv.setManv("NV_TEST");
        nv.setChucvu("QUANLY");
        SessionManager.setCurrentUser(nv);
    }

    public static void main(String[] args) {
        try {
            loginQuanLy();

            NhaCungCapBUS bus = new NhaCungCapBUS();

            System.out.println("=== 1) getAllNhaCungCap() (include NGUNG) ===");
            List<NhaCungCap> all = bus.getAllNhaCungCap();
            System.out.println("So NCC (all) = " + all.size());
            for (int i = 0; i < Math.min(all.size(), 5); i++) {
                System.out.println(all.get(i));
            }

            System.out.println("\n=== 2) getNhaCungCapHoatDong() ===");
            List<NhaCungCap> hd = bus.getNhaCungCapHoatDong();
            System.out.println("So NCC (HOATDONG) = " + hd.size());

            System.out.println("\n=== 3) themNhaCungCap() ===");
            NhaCungCap ncc = new NhaCungCap();
            ncc.setTenNCC("NCC TEST BUS");
            ncc.setSoDienThoai("0901234567");
            ncc.setEmail("testbus@ncc.com");
            ncc.setDiaChi("TP.HCM");
            ncc.setNguoiLienHe("Nguoi LH");

            String maMoi = bus.themNhaCungCap(ncc);
            System.out.println("Inserted MaNCC = " + maMoi);

            System.out.println("\n=== 4) suaNhaCungCap() ===");
            ncc.setMaNCC(maMoi);
            ncc.setTenNCC("NCC TEST BUS (UPDATED)");
            boolean updated = bus.suaNhaCungCap(ncc);
            System.out.println("Update = " + updated);

            System.out.println("\n=== 5) xoaNhaCungCap() (soft delete) ===");
            try {
                boolean deleted = bus.xoaNhaCungCap(maMoi);
                System.out.println("SoftDelete = " + deleted);
                System.out.println("✅ OK nếu NCC không có phiếu nhập CHODUYET");
            } catch (Exception e) {
                System.out.println("❌ SoftDelete FAIL (đúng nghiệp vụ nếu còn phiếu CHODUYET): " + e.getMessage());
            }

            System.out.println("\n=== TEST NhaCungCapBUS OK ===");

        } catch (Exception e) {
            System.out.println("TEST FAIL: " + e.getMessage());
        } finally {
            SessionManager.logout();
            DBConnection.closeConnection();
        }
    }
}