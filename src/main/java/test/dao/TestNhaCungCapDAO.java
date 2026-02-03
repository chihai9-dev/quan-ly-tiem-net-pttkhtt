package test.dao;

import dao.NhaCungCapDAO;
import entity.NhaCungCap;

import java.util.List;

public class TestNhaCungCapDAO {
    public static void main(String[] args) {
        NhaCungCapDAO dao = new NhaCungCapDAO();

        System.out.println("=== TEST getAll(HOATDONG) ===");
        List<NhaCungCap> list = dao.getAll(false);
        System.out.println("So NCC = " + list.size());
        for (NhaCungCap ncc : list) {
            System.out.println(ncc);
        }

        System.out.println("\n=== TEST insert() ===");
        NhaCungCap newNcc = new NhaCungCap();
        newNcc.setTenNCC("NCC Test DAO");
        newNcc.setSoDienThoai("0909999999");
        newNcc.setEmail("testdao@ncc.com");
        newNcc.setDiaChi("TP.HCM");
        newNcc.setNguoiLienHe("Anh Test");
        // TrangThai sẽ auto HOATDONG theo nghiệp vụ

        String maMoi = dao.insert(newNcc);
        System.out.println("Inserted MaNCC = " + maMoi);

        System.out.println("\n=== TEST getById() ===");
        NhaCungCap found = dao.getById(maMoi);
        System.out.println(found == null ? "Khong tim thay" : found.toString());

        System.out.println("\n=== TEST update() ===");
        found.setTenNCC("NCC Test DAO - Updated");
        boolean updated = dao.update(found);
        System.out.println("Update = " + updated);
        System.out.println("After update: " + dao.getById(maMoi));

        System.out.println("\n=== TEST softDelete() ===");
        try {
            boolean deleted = dao.softDelete(maMoi);
            System.out.println("SoftDelete = " + deleted);
            System.out.println("After delete: " + dao.getById(maMoi));
        } catch (Exception e) {
            System.out.println("SoftDelete FAIL: " + e.getMessage());
        }
    }
}
