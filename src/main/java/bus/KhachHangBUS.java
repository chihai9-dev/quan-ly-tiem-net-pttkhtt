package bus;

import entity.KhachHang;
import untils.PermissionHelper;
import untils.SessionManager;
import untils.PasswordEncoder;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class KhachHangBUS {
    private final KhachHangDAO khachHangDAO;

    public KhachHangBUS() {
        this.khachHangDAO = new KhachHangDAO();
    }

    public KhachHang dangKy(KhachHang kh) throws Exception {
        chuanHoaDuLieuKhachHang(kh);
        validateKhachHangToanDien(kh, true);

        String encodedPass = PasswordEncoder.encode(kh.getMatkhau());
        kh.setMatkhau(encodedPass);

        try {
            boolean isSuccess = khachHangDAO.insert(kh);
            if (!isSuccess) {
                throw new Exception("Đăng ký thất bại do lỗi hệ thống CSDL.");
            }
        } catch (RuntimeException e) {
            throw new Exception(e.getMessage());
        }
        return kh;
    }

    public KhachHang dangNhap(String tenDangNhap, String matKhau) throws Exception {
        if (tenDangNhap == null || tenDangNhap.trim().isEmpty()) {
            throw new Exception("Tên đăng nhập không được để trống!");
        }
        if (matKhau == null || matKhau.trim().isEmpty()) {
            throw new Exception("Mật khẩu không được để trống!");
        }

        KhachHang kh = khachHangDAO.getByTenDangNhap(tenDangNhap);

        if (kh == null) {
            throw new Exception("Tên đăng nhập không tồn tại trong hệ thống!");
        }
        if (kh.isNgung()) {
            throw new Exception("Tài khoản của bạn đã bị khóa. Vui lòng liên hệ Quản lý!");
        }

        boolean isMatch = PasswordEncoder.matches(matKhau, kh.getMatkhau());
        if (!isMatch) {
            throw new Exception("Mật khẩu không chính xác!");
        }

        SessionManager.setCurrentUser(kh);
        return kh;
    }

    public boolean doiMatKhau(String maKH, String mkCu, String mkMoi) throws Exception {
        PermissionHelper.canEditKhachHang(maKH);
        validateMatKhau(mkMoi);

        KhachHang kh = khachHangDAO.getById(maKH);
        if (kh == null) throw new Exception("Không tìm thấy khách hàng!");

        if (!SessionManager.isQuanLy()) {
            if (!PasswordEncoder.matches(mkCu, kh.getMatkhau())) {
                throw new Exception("Mật khẩu cũ không chính xác!");
            }
        }

        kh.setMatkhau(PasswordEncoder.encode(mkMoi));

        try {
            boolean result = khachHangDAO.update(kh);
            if (result) PermissionHelper.logAction("Đổi mật khẩu khách hàng", maKH);
            return result;
        } catch (RuntimeException e) {
            throw new Exception("Lỗi khi cập nhật mật khẩu: " + e.getMessage());
        }
    }

    public boolean resetMatKhau(String maKH, String defaultPass) throws Exception {
        PermissionHelper.requireQuanLy();

        KhachHang kh = khachHangDAO.getById(maKH);
        if (kh == null) throw new Exception("Khách hàng không tồn tại!");

        kh.setMatkhau(PasswordEncoder.encode(defaultPass));
        boolean result = khachHangDAO.update(kh);

        if (result) {
            PermissionHelper.logAction("Reset mật khẩu về '" + defaultPass + "'", maKH);
        }
        return result;
    }

    public List<KhachHang> getAllKhachHang() throws Exception {
        PermissionHelper.requireNhanVien();
        return khachHangDAO.getAll();
    }

    public KhachHang getKhachHangById(String maKH) throws Exception {
        PermissionHelper.canViewKhachHang(maKH);

        KhachHang kh = khachHangDAO.getById(maKH);
        if (kh == null) {
            throw new Exception("Không tìm thấy dữ liệu khách hàng với mã: " + maKH);
        }
        return kh;
    }

    public boolean themKhachHang(KhachHang kh) throws Exception {
        PermissionHelper.requireQuanLy();

        chuanHoaDuLieuKhachHang(kh);
        validateKhachHangToanDien(kh, true);

        if (kh.getMatkhau() != null) {
            kh.setMatkhau(PasswordEncoder.encode(kh.getMatkhau()));
        } else {
            kh.setMatkhau(PasswordEncoder.encode("123456"));
        }

        try {
            boolean result = khachHangDAO.insert(kh);
            if (result) PermissionHelper.logAction("Admin thêm khách hàng", kh.getMakh());
            return result;
        } catch (RuntimeException e) {
            throw new Exception("Lỗi khi thêm: " + e.getMessage());
        }
    }

    public boolean suaKhachHang(KhachHang kh) throws Exception {
        PermissionHelper.canEditKhachHang(kh.getMakh());

        chuanHoaDuLieuKhachHang(kh);
        validateKhachHangToanDien(kh, false);

        try {
            boolean result = khachHangDAO.update(kh);
            if (result) PermissionHelper.logAction("Cập nhật thông tin", kh.getMakh());
            return result;
        } catch (RuntimeException e) {
            throw new Exception("Lỗi khi cập nhật: " + e.getMessage());
        }
    }

    public boolean xoaKhachHang(String maKH) throws Exception {
        PermissionHelper.requireQuanLy();

        try {
            boolean result = khachHangDAO.delete(maKH);
            if (result) PermissionHelper.logAction("Xóa (Khóa) khách hàng", maKH);
            return result;
        } catch (RuntimeException e) {
            if (e.getMessage().contains("phiên chơi")) {
                throw new Exception("Lỗi Nghiệp vụ: Khách hàng đang có phiên sử dụng, không thể khóa lúc này.");
            }
            throw new Exception(e.getMessage());
        }
    }

    public boolean khoiPhucKhachHang(String maKH) throws Exception {
        PermissionHelper.requireQuanLy();
        try {
            boolean result = khachHangDAO.restore(maKH);
            if (result) PermissionHelper.logAction("Khôi phục khách hàng", maKH);
            return result;
        } catch (RuntimeException e) {
            throw new Exception(e.getMessage());
        }
    }

    public String getCanhBaoXoa(String maKH) throws Exception {
        PermissionHelper.requireQuanLy();
        return khachHangDAO.getDeleteWarning(maKH);
    }

    public double kiemTraSoDu(String maKH) throws Exception {
        PermissionHelper.canViewKhachHang(maKH);
        KhachHang kh = khachHangDAO.getById(maKH);
        if (kh == null) throw new Exception("Khách hàng không tồn tại!");
        return kh.getSodu();
    }

    public List<KhachHang> timKiemKhachHang(String keyword) throws Exception {
        PermissionHelper.requireNhanVien();
        List<KhachHang> allList = khachHangDAO.getAll();

        if (keyword == null || keyword.trim().isEmpty()) {
            return allList;
        }

        String key = keyword.toLowerCase().trim();
        return allList.stream()
                .filter(kh ->
                        (kh.getTen() != null && kh.getTen().toLowerCase().contains(key)) ||
                                (kh.getHo() != null && kh.getHo().toLowerCase().contains(key)) ||
                                (kh.getSodienthoai() != null && kh.getSodienthoai().contains(key)) ||
                                (kh.getMakh() != null && kh.getMakh().toLowerCase().contains(key)) ||
                                (kh.getTendangnhap() != null && kh.getTendangnhap().toLowerCase().contains(key))
                )
                .collect(Collectors.toList());
    }

    public List<KhachHang> timKiemNangCao(String keyword, double minSoDu, String trangThai) throws Exception {
        PermissionHelper.requireNhanVien();
        List<KhachHang> ketQua = timKiemKhachHang(keyword);

        return ketQua.stream()
                .filter(kh -> kh.getSodu() >= minSoDu)
                .filter(kh -> trangThai.equals("TATC") || kh.getTrangthai().equals(trangThai))
                .collect(Collectors.toList());
    }

    public List<KhachHang> getTopKhachHangVIP(int top) throws Exception {
        PermissionHelper.requireQuanLy();
        List<KhachHang> all = khachHangDAO.getAll();

        return all.stream()
                .filter(kh -> !kh.isNgung())
                .sorted(Comparator.comparingDouble(KhachHang::getSodu).reversed())
                .limit(top)
                .collect(Collectors.toList());
    }

    public int khoaTaiKhoanRongHangLoat() throws Exception {
        PermissionHelper.requireQuanLy();
        List<KhachHang> all = khachHangDAO.getAll();
        int count = 0;

        for (KhachHang kh : all) {
            if (kh.getSodu() == 0 && !kh.isNgung()) {
                try {
                    khachHangDAO.delete(kh.getMakh());
                    count++;
                } catch (Exception e) {
                    System.err.println("Bỏ qua KH " + kh.getMakh() + ": " + e.getMessage());
                }
            }
        }
        PermissionHelper.logAction("Khóa hàng loạt " + count + " tài khoản 0đ", "HỆ THỐNG");
        return count;
    }

    public String xuatDanhSachCSV() throws Exception {
        PermissionHelper.requireQuanLy();
        List<KhachHang> all = khachHangDAO.getAll();

        StringBuilder csv = new StringBuilder();
        csv.append("Mã KH,Họ,Tên,SĐT,Tên Đăng Nhập,Số Dư,Trạng Thái\n");

        for (KhachHang kh : all) {
            csv.append(kh.getMakh()).append(",")
                    .append(kh.getHo()).append(",")
                    .append(kh.getTen()).append(",")
                    .append(kh.getSodienthoai() != null ? kh.getSodienthoai() : "").append(",")
                    .append(kh.getTendangnhap()).append(",")
                    .append(String.format("%.0f", kh.getSodu())).append(",")
                    .append(kh.getTrangthai()).append("\n");
        }
        return csv.toString();
    }

    private void validateKhachHangToanDien(KhachHang kh, boolean isInsert) throws Exception {
        validateHoTen(kh.getHo(), kh.getTen());
        validateSoDienThoai(kh.getSodienthoai());

        if (isInsert) {
            validateTenDangNhap(kh.getTendangnhap());
            validateMatKhau(kh.getMatkhau());
            if (khachHangDAO.isTenDangNhapExists(kh.getTendangnhap())) {
                throw new Exception("Lỗi: Tên đăng nhập '" + kh.getTendangnhap() + "' đã được sử dụng!");
            }
        }
    }

    private void validateHoTen(String ho, String ten) throws Exception {
        if (ho == null || ho.trim().isEmpty()) {
            throw new Exception("Họ khách hàng không được để trống!");
        }
        if (ten == null || ten.trim().isEmpty()) {
            throw new Exception("Tên khách hàng không được để trống!");
        }
        if (ho.length() > 50 || ten.length() > 50) {
            throw new Exception("Họ và Tên không được vượt quá 50 ký tự!");
        }
    }

    private void validateSoDienThoai(String sdt) throws Exception {
        if (sdt != null && !sdt.trim().isEmpty()) {
            if (!sdt.matches("^0\\d{9}$")) {
                throw new Exception("Số điện thoại không hợp lệ! Vui lòng nhập đúng 10 số và bắt đầu bằng số 0.");
            }
        }
    }

    private void validateTenDangNhap(String username) throws Exception {
        if (username == null || username.trim().length() < 4) {
            throw new Exception("Tên đăng nhập phải có ít nhất 4 ký tự!");
        }
        if (username.trim().length() > 30) {
            throw new Exception("Tên đăng nhập không được vượt quá 30 ký tự!");
        }
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            throw new Exception("Tên đăng nhập chỉ được chứa chữ cái không dấu, số và dấu gạch dưới (_).");
        }
    }

    private void validateMatKhau(String password) throws Exception {
        if (password == null || password.trim().isEmpty()) {
            throw new Exception("Mật khẩu không được để trống!");
        }
        if (password.length() < 6) {
            throw new Exception("Mật khẩu quá yếu! Yêu cầu tối thiểu 6 ký tự.");
        }
    }

    private void chuanHoaDuLieuKhachHang(KhachHang kh) {
        if (kh.getHo() != null) {
            kh.setHo(chuanHoaChuoi(kh.getHo()));
        }
        if (kh.getTen() != null) {
            kh.setTen(chuanHoaChuoi(kh.getTen()));
        }
        if (kh.getTendangnhap() != null) {
            kh.setTendangnhap(kh.getTendangnhap().trim().toLowerCase());
        }
        if (kh.getSodienthoai() != null) {
            kh.setSodienthoai(kh.getSodienthoai().trim());
        }
    }

    private String chuanHoaChuoi(String input) {
        if (input == null || input.trim().isEmpty()) return "";

        String[] words = input.trim().replaceAll("\\s+", " ").split(" ");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            result.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1).toLowerCase())
                    .append(" ");
        }
        return result.toString().trim();
    }
}