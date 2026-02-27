package bus;

import dao.KhuMayDAO;
import dao.MayTinhDAO;
import entity.KhuMay;
import entity.MayTinh;
import untils.PermissionHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * MayTinhBUS - Lớp xử lý nghiệp vụ cho Máy Tính
 *
 * Chức năng chính: Quản lý máy tính, cập nhật trạng thái
 * DAO sử dụng: MayTinhDAO, KhuMayDAO
 *
 * Cấu trúc method:
 * 1. Kiểm tra phân quyền
 * 2. Validate dữ liệu đầu vào
 * 3. Kiểm tra các ràng buộc nghiệp vụ
 * 4. Gọi các DAO để xử lý dữ liệu
 * 5. Trả về Object kết quả hoặc throw Exception

 */
public class MayTinhBUS {

    // ============== DAO ==============
    private final MayTinhDAO mayTinhDAO;
    private final KhuMayDAO khuMayDAO;

    // ============== CONSTRUCTOR ==============
    public MayTinhBUS() {
        this.mayTinhDAO = new MayTinhDAO();
        this.khuMayDAO = new KhuMayDAO();
    }
    public MayTinhBUS(MayTinhDAO mayTinhDAO, KhuMayDAO khuMayDAO) {
        this.mayTinhDAO = mayTinhDAO;
        this.khuMayDAO = khuMayDAO;
    }

    // ============== 1. GET ALL MÁY TÍNH ==============

    /**
     * Lấy tất cả máy tính
     * Phân quyền: QUANLY / NHANVIEN
     *
     * @return Danh sách tất cả máy tính
     * @throws Exception nếu chưa đăng nhập hoặc không có quyền
     */
    public List<MayTinh> getAllMayTinh() throws Exception {
        // check login
        PermissionHelper.requireLogin();
        // 1. Kiểm tra phân quyền (QUANLY hoặc NHANVIEN)
        PermissionHelper.requireNhanVien();

        // 2. Gọi DAO lấy dữ liệu
        List<MayTinh> list = mayTinhDAO.getAll();

        // 3. Trả về kết quả
        return list;
    }

    // ============== 2. GET MÁY TRỐNG ==============

    /**
     * Lấy danh sách máy trống (TrangThai = 'TRONG')
     * Phân quyền: QUANLY / NHANVIEN
     *
     * @return Danh sách máy tính đang trống
     * @throws Exception nếu chưa đăng nhập hoặc không có quyền
     */
    public List<MayTinh> getMayTrong() throws Exception {
        // check login
        PermissionHelper.requireLogin();
        // 1. Kiểm tra phân quyền (QUANLY hoặc NHANVIEN)
        PermissionHelper.requireNhanVien();

        // 2. Gọi DAO lấy tất cả máy
        List<MayTinh> allMayTinh = mayTinhDAO.getAll();

        // 3. Lọc máy có trạng thái TRONG
        List<MayTinh> mayTrongList = new ArrayList<>();
        for (MayTinh mt : allMayTinh) {
            if ("TRONG".equals(mt.getTrangthai())) {
                mayTrongList.add(mt);
            }
        }

        // 4. Trả về kết quả
        return mayTrongList;
    }

    // ============== 3. THÊM MÁY TÍNH ==============

    /**
     * Thêm máy tính mới
     * Phân quyền: QUANLY
     *
     * Logic xử lý:
     * 1. Kiểm tra phân quyền QUANLY
     * 2. Kiểm tra ràng buộc nghiệp vụ liên bảng (khu máy tồn tại & HOATDONG)
     * 3. Kiểm tra số máy trong khu chưa vượt quá số máy tối đa
     * 4. Gọi DAO insert (DAO tự validate format, trùng tên, generate mã)
     * 5. Trả về MayTinh đã thêm
     *
     * @param may MayTinh cần thêm
     * @return MayTinh đã được thêm thành công (có mã máy)
     * @throws Exception nếu không có quyền hoặc vi phạm nghiệp vụ
     */
    public MayTinh themMayTinh(MayTinh may) throws Exception {
        // check login
        PermissionHelper.requireLogin();
        // 1. Kiểm tra phân quyền QUANLY
        PermissionHelper.requireQuanLy();

        // 2. Kiểm tra dữ liệu đầu vào không null
        if (may == null) {
            throw new Exception("Thông tin máy tính không được để trống");
        }

        // 3. Kiểm tra ràng buộc nghiệp vụ liên bảng: Khu máy tồn tại và đang HOATDONG
        if (may.getMakhu() != null && !may.getMakhu().trim().isEmpty()) {
            KhuMay khuMay = khuMayDAO.getById(may.getMakhu());
            if (khuMay == null) {
                throw new Exception("Khu máy không tồn tại: " + may.getMakhu());
            }
            if (!"HOATDONG".equals(khuMay.getTrangthai())) {
                throw new Exception("Khu máy đang ở trạng thái: " + khuMay.getTrangthai()
                        + ". Chỉ thêm máy vào khu đang HOATDONG");
            }

            // 4. Kiểm tra số máy trong khu chưa vượt quá số máy tối đa
            int soMayHienCo = demSoMayTrongKhu(may.getMakhu());
            if (soMayHienCo >= khuMay.getSomaytoida()) {
                throw new Exception("Khu máy " + khuMay.getTenkhu() + " đã đầy ("
                        + soMayHienCo + "/" + khuMay.getSomaytoida() + " máy). Không thể thêm máy mới");
            }
        }

        // 5. Gọi DAO insert (DAO sẽ validate format, trùng tên, generate mã, set trạng thái TRONG)
        boolean result = mayTinhDAO.Insert(may);

        if (!result) {
            throw new Exception("Thêm máy tính thất bại");
        }

        // 6. Ghi log hành động
        PermissionHelper.logAction("THEM_MAY_TINH", "Thêm máy tính: " + may.getTenmay() + " (Mã: " + may.getMamay() + ")");

        // 7. Trả về MayTinh đã thêm (đã có mã máy từ DAO)
        return may;
    }

    // ============== 4. SỬA MÁY TÍNH ==============

    /**
     * Sửa thông tin máy tính
     * Phân quyền: QUANLY
     *
     * Logic xử lý:
     * 1. Kiểm tra phân quyền QUANLY
     * 2. Kiểm tra máy tính tồn tại
     * 3. Kiểm tra trạng thái máy (không sửa máy đang DANGDUNG)
     * 4. Kiểm tra ràng buộc liên bảng (khu mới hợp lệ, chưa đầy)
     * 5. Gọi DAO update
     * 6. Trả về MayTinh đã sửa
     *
     * @param may MayTinh với thông tin cập nhật
     * @return MayTinh đã được cập nhật
     * @throws Exception nếu không có quyền hoặc vi phạm nghiệp vụ
     */
    public MayTinh suaMayTinh(MayTinh may) throws Exception {
        // check login
        PermissionHelper.requireLogin();
        // 1. Kiểm tra phân quyền QUANLY
        PermissionHelper.requireQuanLy();

        // 2. Kiểm tra dữ liệu đầu vào không null
        if (may == null) {
            throw new Exception("Thông tin máy tính không được để trống");
        }

        if (may.getMamay() == null || may.getMamay().trim().isEmpty()) {
            throw new Exception("Mã máy tính không được để trống");
        }

        // 3. Kiểm tra máy tính tồn tại
        MayTinh existing = mayTinhDAO.getById(may.getMamay());
        if (existing == null) {
            throw new Exception("Máy tính không tồn tại: " + may.getMamay());
        }

        // 4. Kiểm tra trạng thái máy (không sửa máy đang DANGDUNG)
        if ("DANGDUNG".equals(existing.getTrangthai())) {
            throw new Exception("Không thể sửa máy tính đang được sử dụng (DANGDUNG)");
        }

        // 5. Kiểm tra ràng buộc liên bảng: nếu đổi khu, kiểm tra khu mới hợp lệ và chưa đầy
        if (may.getMakhu() != null && !may.getMakhu().trim().isEmpty()) {
            KhuMay khuMoi = khuMayDAO.getById(may.getMakhu());
            if (khuMoi == null) {
                throw new Exception("Khu máy không tồn tại: " + may.getMakhu());
            }
            if (!"HOATDONG".equals(khuMoi.getTrangthai())) {
                throw new Exception("Khu máy đang ở trạng thái: " + khuMoi.getTrangthai()
                        + ". Chỉ chuyển máy vào khu đang HOATDONG");
            }

            // Nếu đổi khu (khu mới khác khu cũ), kiểm tra số máy tối đa
            boolean isChangingKhu = existing.getMakhu() == null || !existing.getMakhu().equals(may.getMakhu());
            if (isChangingKhu) {
                int soMayHienCo = demSoMayTrongKhu(may.getMakhu());
                if (soMayHienCo >= khuMoi.getSomaytoida()) {
                    throw new Exception("Khu máy " + khuMoi.getTenkhu() + " đã đầy ("
                            + soMayHienCo + "/" + khuMoi.getSomaytoida() + " máy). Không thể chuyển máy vào");
                }
            }
        }

        // 6. Gọi DAO update thông tin khác
        boolean result = mayTinhDAO.UpdateThongTinKhac(may);

        if (!result) {
            throw new Exception("Cập nhật máy tính thất bại");
        }

        // 7. Nếu có cập nhật giá mỗi giờ
        if (may.getGiamoigio() != null && !may.getGiamoigio().equals(existing.getGiamoigio())) {
            mayTinhDAO.UpdateGiaMoiGio(may.getMamay(), may.getGiamoigio());
        }

        // 8. Ghi log hành động
        PermissionHelper.logAction("SUA_MAY_TINH", "Sửa máy tính: " + may.getMamay());

        // 9. Trả về MayTinh đã cập nhật
        return may;
    }

    // ============== 5. XÓA MÁY TÍNH ==============

    /**
     * Xóa (ngưng hoạt động) máy tính - Soft Delete
     * Phân quyền: QUANLY
     *
     * Logic xử lý:
     * 1. Kiểm tra phân quyền QUANLY
     * 2. Kiểm tra máy tính tồn tại
     * 3. Kiểm tra trạng thái máy (không xóa máy DANGDUNG)
     * 4. Gọi DAO delete (soft delete → NGUNG)
     * 5. Trả về true
     *
     * @param maMay Mã máy tính cần xóa
     * @return true nếu xóa thành công
     * @throws Exception nếu không có quyền hoặc vi phạm nghiệp vụ
     */
    public boolean xoaMayTinh(String maMay) throws Exception {
        // check login
        PermissionHelper.requireLogin();
        // 1. Kiểm tra phân quyền QUANLY
        PermissionHelper.requireQuanLy();

        // 2. Kiểm tra mã máy không rỗng
        if (maMay == null || maMay.trim().isEmpty()) {
            throw new Exception("Mã máy tính không được để trống");
        }

        // 3. Kiểm tra máy tính tồn tại
        MayTinh existing = mayTinhDAO.getById(maMay);
        if (existing == null) {
            throw new Exception("Máy tính không tồn tại: " + maMay);
        }

        // 4. Kiểm tra trạng thái máy (không xóa máy đã NGUNG)
        if ("NGUNG".equals(existing.getTrangthai())) {
            throw new Exception("Máy tính đã được ngưng hoạt động trước đó");
        }

        // 5. Gọi DAO delete (DAO sẽ kiểm tra máy DANGDUNG, soft delete → NGUNG)
        boolean result = mayTinhDAO.delete(maMay);

        if (!result) {
            throw new Exception("Xóa máy tính thất bại");
        }

        // 6. Ghi log hành động
        PermissionHelper.logAction("XOA_MAY_TINH", "Xóa (ngưng) máy tính: " + maMay);

        // 7. Trả về true
        return true;
    }

    // ============== 6. CHUYỂN TRẠNG THÁI ==============

    /**
     * Chuyển trạng thái máy tính
     * Phân quyền: QUANLY / NHANVIEN

     * @param maMay Mã máy tính cần chuyển trạng thái
     * @param ttMoi Trạng thái mới: "TRONG" | "BAOTRI"
     * @return true nếu chuyển trạng thái thành công
     * @throws Exception nếu không có quyền hoặc vi phạm quy tắc chuyển trạng thái
     */
    public boolean chuyenTrangThai(String maMay, String ttMoi) throws Exception {
        // check login
        PermissionHelper.requireLogin();
        // 1. Kiểm tra phân quyền (QUANLY hoặc NHANVIEN)
        PermissionHelper.requireNhanVien();

        // 2. Kiểm tra dữ liệu đầu vào
        if (maMay == null || maMay.trim().isEmpty()) {
            throw new Exception("Mã máy tính không được để trống");
        }

        if (ttMoi == null || ttMoi.trim().isEmpty()) {
            throw new Exception("Trạng thái mới không được để trống");
        }

        // 3. Kiểm tra máy tính tồn tại
        MayTinh existing = mayTinhDAO.getById(maMay);
        if (existing == null) {
            throw new Exception("Máy tính không tồn tại: " + maMay);
        }

        String ttHienTai = existing.getTrangthai();

        // 4. Không cho phép chuyển sang DANGDUNG (chỉ qua moPhienChoi)
        if ("DANGDUNG".equals(ttMoi)) {
            throw new Exception("Không thể chuyển trạng thái sang DANGDUNG. Chỉ thực hiện qua chức năng Mở phiên chơi");
        }

        // 5. Không cho phép chuyển từ DANGDUNG (chỉ qua ketThucPhien)
        if ("DANGDUNG".equals(ttHienTai)) {
            throw new Exception("Máy đang được sử dụng (DANGDUNG). Chỉ có thể chuyển trạng thái qua chức năng Kết thúc phiên");
        }

        // 6. Kiểm tra máy đã NGUNG
        if ("NGUNG".equals(ttHienTai)) {
            throw new Exception("Máy tính đã ngưng hoạt động. Không thể chuyển trạng thái");
        }

        // 7. Xử lý chuyển trạng thái theo quy tắc
        boolean result = false;

        if ("TRONG".equals(ttHienTai) && "BAOTRI".equals(ttMoi)) {
            // TRONG → BAOTRI: Luôn được phép
            result = mayTinhDAO.duaVaoBaoTri(maMay);
            if (!result) {
                throw new Exception("Chuyển trạng thái TRONG → BAOTRI thất bại");
            }

        } else if ("BAOTRI".equals(ttHienTai) && "TRONG".equals(ttMoi)) {
            // BAOTRI → TRONG: Luôn được phép
            result = mayTinhDAO.hoanTatBaoTri(maMay);
            if (!result) {
                throw new Exception("Chuyển trạng thái BAOTRI → TRONG thất bại");
            }

        } else {
            throw new Exception("Không thể chuyển trạng thái từ " + ttHienTai + " sang " + ttMoi
                    + ". Chỉ cho phép: TRONG ↔ BAOTRI");
        }

        // 8. Ghi log hành động
        PermissionHelper.logAction("CHUYEN_TRANG_THAI_MAY",
                "Chuyển trạng thái máy " + maMay + ": " + ttHienTai + " → " + ttMoi);

        // 9. Trả về true
        return true;
    }

    // ============== PRIVATE HELPER METHODS ==============

    /**
     * Đếm số máy trong khu (internal - không kiểm tra phân quyền)
     * Dùng nội bộ trong BUS để kiểm tra ràng buộc nghiệp vụ
     *
     * @param maKhu Mã khu máy
     * @return Số lượng máy tính trong khu
     */
    private int demSoMayTrongKhu(String maKhu) {
        List<MayTinh> allMayTinh = mayTinhDAO.getAll();
        int count = 0;

        for (MayTinh mt : allMayTinh) {
            if (maKhu.equals(mt.getMakhu())) {
                count++;
            }
        }

        return count;
    }
}