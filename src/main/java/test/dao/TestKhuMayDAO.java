package test.dao;
import dao.KhuMayDAO;
import entity.KhuMay;
import java.util.List;

public class TestKhuMayDAO {

    public static void main(String[] args) {
        KhuMayDAO dao = new KhuMayDAO();

        System.out.println("===== TEST getAll() =====");
        List<KhuMay> list = dao.getAll();
        for (KhuMay km : list) {
            System.out.println(
                    km.getMakhu() + " | " +
                            km.getTenkhu() + " | " +
                            km.getGiacoso() + " | " +
                            km.getSomaytoida() + " | " +
                            km.getTrangthai()
            );
        }

        System.out.println("\n===== TEST getById() =====");
        KhuMay km1 = dao.getById("KHU001");
        if (km1 != null) {
            System.out.println("Tìm thấy: " + km1.getTenkhu());
        } else {
            System.out.println("Không tìm thấy KHU001");
        }

        System.out.println("\n===== TEST insert() =====");
        try {
            KhuMay kmNew = new KhuMay();
            kmNew.setTenkhu("Khu Test Insert");
            kmNew.setGiacoso(7000);
            kmNew.setSomaytoida(12);

            dao.insert(kmNew);
            System.out.println("Insert thành công!");
        } catch (Exception e) {
            System.out.println("Insert lỗi: " + e.getMessage());
        }

        System.out.println("\n===== TEST update() =====");
        try {
            KhuMay kmUpdate = dao.getById("KHU005"); // Khu Hút Thuốc (D)
            if (kmUpdate != null) {
                kmUpdate.setTenkhu("Khu Hút Thuốc - Updated");
                kmUpdate.setGiacoso(6500);
                kmUpdate.setSomaytoida(18);

                dao.update(kmUpdate);
                System.out.println("Update thành công!");
            }
        } catch (Exception e) {
            System.out.println("Update lỗi: " + e.getMessage());
        }

        System.out.println("\n===== TEST delete() =====");

        // ❌ Trường hợp có máy đang chơi (KHU001 → KHU004)
        try {
            dao.delete("KHU001");
            System.out.println("Xóa KHU001 thành công (❌ sai logic)");
        } catch (Exception e) {
            System.out.println("KHU001: " + e.getMessage());
        }

        // ⚠️ Trường hợp có máy nhưng KHÔNG DANGCHOI (KHU005)
        try {
            dao.delete("KHU005");
            System.out.println("Xóa mềm KHU005 thành công!");
        } catch (Exception e) {
            System.out.println("KHU005: " + e.getMessage());
        }

        System.out.println("\n===== TEST KẾT THÚC =====");
    }
}
