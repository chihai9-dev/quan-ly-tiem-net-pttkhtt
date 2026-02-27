package test.bus;

import bus.NhanVienBUS;
import entity.NhanVien;
import untils.SessionManager;

import java.util.List;

public class TestNhanVienBUS {
    public static void main(String[] args) {
        NhanVienBUS bus = new NhanVienBUS();
        System.out.println("=== BẮT ĐẦU TEST NHANVIENBUS ===");

        System.out.println("\n[0] Giả lập đăng nhập Quản lý...");
        NhanVien admin = new NhanVien();
        admin.setManv("NV_TEST_ADMIN");
        admin.setTen("Admin Test");
        admin.setChucvu("QUANLY");
        SessionManager.setCurrentUser(admin);

        String maNVMoi = "";
        String username = "staff_" + System.currentTimeMillis();

        try {
            System.out.println("\n[1] Test Lấy Danh Sách & Tìm Kiếm:");
            List<NhanVien> list = bus.getAllNhanVienDangLamViec();
            System.out.println("=> Tổng số NV đang làm việc: " + list.size());

            List<NhanVien> searchResult = bus.timKiemNhanVien("a");
            System.out.println("=> Tìm thấy " + searchResult.size() + " NV chứa từ khóa 'a'");
        } catch (Exception e) {
            System.err.println("=> Lỗi [1]: " + e.getMessage());
        }

        try {
            System.out.println("\n[2] Test Thêm Nhân Viên Mới:");
            NhanVien nvNew = new NhanVien();
            nvNew.setHo("Trần");
            nvNew.setTen("Test Nhân Viên");
            nvNew.setChucvu("NHANVIEN");
            nvNew.setTendangnhap(username);
            nvNew.setMatkhau("123456");

            if (bus.themNhanVien(nvNew)) {
                maNVMoi = nvNew.getManv();
                System.out.println("=> THÀNH CÔNG: Đã thêm NV mã " + maNVMoi + " | Username: " + username);
            }
        } catch (Exception e) {
            System.err.println("=> Lỗi [2]: " + e.getMessage());
        }

        if (!maNVMoi.isEmpty()) {
            try {
                System.out.println("\n[3] Test Cập Nhật Thông Tin:");
                NhanVien nvUpdate = bus.getNhanVienById(maNVMoi);
                nvUpdate.setHo("Trần (Đã sửa)");

                if (bus.suaNhanVien(nvUpdate)) {
                    System.out.println("=> THÀNH CÔNG: Đã cập nhật Họ cho NV " + maNVMoi);
                }
            } catch (Exception e) {
                System.err.println("=> Lỗi [3]: " + e.getMessage());
            }
        }

        if (!maNVMoi.isEmpty()) {
            try {
                System.out.println("\n[4] Test Đăng Nhập Nhân Viên:");

                SessionManager.clearSession();

                NhanVien loginResult = bus.dangNhap(username, "123456");
                if (loginResult != null) {
                    System.out.println("=> THÀNH CÔNG: NV " + username + " đã đăng nhập thành công qua mã hóa.");
                }
            } catch (Exception e) {
                System.err.println("=> Lỗi [4]: " + e.getMessage());
            }
        }

        if (!maNVMoi.isEmpty()) {
            try {
                System.out.println("\n[5] Test Xóa (Cho Nghỉ Việc) Nhân Viên:");

                SessionManager.setCurrentUser(admin);

                String warning = bus.getCanhBaoXoaNhanVien(maNVMoi);
                if (warning != null) {
                    System.out.println("   Cảnh báo từ hệ thống: " + warning);
                }

                if (bus.xoaNhanVien(maNVMoi)) {
                    System.out.println("=> THÀNH CÔNG: Đã cho NV " + maNVMoi + " nghỉ việc.");
                }
            } catch (Exception e) {
                System.err.println("=> Lỗi [5]: " + e.getMessage());
            }
        }

        System.out.println("\n=== KẾT THÚC TEST NHANVIENBUS ===");
    }
}