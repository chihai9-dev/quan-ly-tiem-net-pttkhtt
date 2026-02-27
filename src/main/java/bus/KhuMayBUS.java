package bus;

import dao.KhuMayDAO;
import dao.MayTinhDAO;
import entity.KhuMay;
import entity.MayTinh;
import untils.PermissionHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * KhuMayBUS - Lớp xử lý nghiệp vụ cho Khu Máy
 *
 * Chức năng chính: Quản lý khu máy, phân loại khu
 * DAO sử dụng: KhuMayDAO, MayTinhDAO
 *
 * Cấu trúc method:
 * 1. Kiểm tra phân quyền
 * 2. Lấy thông tin người dùng hiện tại từ SessionManager
 * 3. Validate dữ liệu đầu vào
 * 4. Kiểm tra các ràng buộc nghiệp vụ
 * 5. Gọi các DAO để xử lý dữ liệu
 * 6. Trả về Object kết quả hoặc throw Exception
 */
public class KhuMayBUS {

    // ============== DAO ==============
    private final KhuMayDAO khuMayDAO;
    private final MayTinhDAO mayTinhDAO;

    // ============== CONSTRUCTOR ==============
    public KhuMayBUS() {
        this.khuMayDAO = new KhuMayDAO();
        this.mayTinhDAO = new MayTinhDAO();
    }

    // ============== 1. GET ALL KHU MÁY ==============

    /**
     * Lấy tất cả khu máy
     * Phân quyền: QUANLY / NHANVIEN
     *
     * @return Danh sách tất cả khu máy
     * @throws Exception nếu chưa đăng nhập hoặc không có quyền
     */
    public List<KhuMay> getAllKhuMay() throws Exception {
        // check login
        PermissionHelper.requireLogin();
        // 1. Kiểm tra phân quyền (QUANLY hoặc NHANVIEN)
        PermissionHelper.requireNhanVien();

        // 2. Gọi DAO lấy dữ liệu
        List<KhuMay> list = khuMayDAO.getAll();

        // 3. Trả về kết quả
        return list;
    }

    // ============== 2. THÊM KHU MÁY ==============

    /**
     * Thêm khu máy mới
     * Phân quyền: QUANLY
     *
     * Logic xử lý:
     * 1. Kiểm tra phân quyền QUANLY
     * 2. Kiểm tra ràng buộc nghiệp vụ (trạng thái)
     * 3. Gọi DAO insert (DAO tự validate format, trùng tên, generate mã)
     * 4. Trả về KhuMay đã thêm
     *
     * @param khu KhuMay cần thêm
     * @return KhuMay đã được thêm thành công (có mã khu)
     * @throws Exception nếu không có quyền hoặc vi phạm nghiệp vụ
     */
    public KhuMay themKhuMay(KhuMay khu) throws Exception {
        // check login
        PermissionHelper.requireLogin();
        // 1. Kiểm tra phân quyền QUANLY
        PermissionHelper.requireQuanLy();

        // 2. Kiểm tra dữ liệu đầu vào không null
        if (khu == null) {
            throw new Exception("Thông tin khu máy không được để trống");
        }

        // 3. Kiểm tra ràng buộc nghiệp vụ: số máy tối đa phải hợp lý
        if (khu.getSomaytoida() > 100) {
            throw new Exception("Số máy tối đa trong một khu không được vượt quá 100");
        }

        // 4. Gọi DAO insert (DAO sẽ validate format, trùng tên, generate mã, set trạng thái HOATDONG)
        boolean result = khuMayDAO.insert(khu);

        if (!result) {
            throw new Exception("Thêm khu máy thất bại");
        }

        // 5. Ghi log hành động
        PermissionHelper.logAction("THEM_KHU_MAY", "Thêm khu máy: " + khu.getTenkhu() + " (Mã: " + khu.getMakhu() + ")");

        // 6. Trả về KhuMay đã thêm (đã có mã khu từ DAO)
        return khu;
    }

    // ============== 3. SỬA KHU MÁY ==============

    /**
     * Sửa thông tin khu máy
     * Phân quyền: QUANLY
     *
     * Logic xử lý:
     * 1. Kiểm tra phân quyền QUANLY
     * 2. Kiểm tra khu máy tồn tại
     * 3. Kiểm tra trạng thái khu máy (chỉ sửa khu HOATDONG)
     * 4. Kiểm tra ràng buộc nghiệp vụ liên bảng (số máy tối đa >= số máy hiện có)
     * 5. Gọi DAO update (DAO validate trùng tên)
     * 6. Trả về KhuMay đã sửa
     *
     * @param khu KhuMay với thông tin cập nhật
     * @return KhuMay đã được cập nhật
     * @throws Exception nếu không có quyền hoặc vi phạm nghiệp vụ
     */
    public KhuMay suaKhuMay(KhuMay khu) throws Exception {
        // check login
        PermissionHelper.requireLogin();
        // 1. Kiểm tra phân quyền QUANLY
        PermissionHelper.requireQuanLy();

        // 2. Kiểm tra dữ liệu đầu vào không null
        if (khu == null) {
            throw new Exception("Thông tin khu máy không được để trống");
        }

        if (khu.getMakhu() == null || khu.getMakhu().trim().isEmpty()) {
            throw new Exception("Mã khu máy không được để trống");
        }

        // 3. Kiểm tra khu máy tồn tại
        KhuMay existing = khuMayDAO.getById(khu.getMakhu());
        if (existing == null) {
            throw new Exception("Khu máy không tồn tại: " + khu.getMakhu());
        }

        // 4. Kiểm tra trạng thái khu máy (chỉ sửa khu đang HOATDONG)
        if (!"HOATDONG".equals(existing.getTrangthai())) {
            throw new Exception("Không thể sửa khu máy đang ở trạng thái: " + existing.getTrangthai()
                    + ". Chỉ sửa được khu máy đang HOATDONG");
        }

        // 5. Kiểm tra ràng buộc nghiệp vụ liên bảng: số máy tối đa >= số máy hiện có trong khu
        int soMayHienCo = demSoMayTrongKhuInternal(khu.getMakhu());
        if (khu.getSomaytoida() < soMayHienCo) {
            throw new Exception("Số máy tối đa (" + khu.getSomaytoida()
                    + ") không được nhỏ hơn số máy hiện có trong khu (" + soMayHienCo + ")");
        }

        // 6. Gọi DAO update (DAO sẽ validate trùng tên)
        boolean result = khuMayDAO.update(khu);

        if (!result) {
            throw new Exception("Cập nhật khu máy thất bại");
        }

        // 7. Ghi log hành động
        PermissionHelper.logAction("SUA_KHU_MAY", "Sửa khu máy: " + khu.getMakhu());

        // 8. Trả về KhuMay đã cập nhật
        return khu;
    }

    // ============== 4. XÓA KHU MÁY ==============

    /**
     * Xóa (ngưng hoạt động) khu máy - Soft Delete
     * Phân quyền: QUANLY
     *
     * Logic xử lý:
     * 1. Kiểm tra phân quyền QUANLY
     * 2. Kiểm tra khu máy tồn tại
     * 3. Kiểm tra trạng thái khu máy
     * 4. Gọi DAO delete (DAO kiểm tra máy DANGCHOI, SET MaKhu = NULL, soft delete)
     * 5. Trả về true
     *
     * Điều kiện:
     * - Có máy DANGDUNG/DANGCHOI trong khu → ❌ Throw lỗi
     * - Có máy khác trong khu → ✓ SET MaKhu = NULL cho các máy đó
     * - Soft delete: SET TrangThai = 'NGUNG'
     *
     * @param maKhu Mã khu máy cần xóa
     * @return true nếu xóa thành công
     * @throws Exception nếu không có quyền hoặc vi phạm nghiệp vụ
     */
    public boolean xoaKhuMay(String maKhu) throws Exception {
        // 1. Kiểm tra phân quyền QUANLY
        PermissionHelper.requireQuanLy();

        // 2. Kiểm tra mã khu không rỗng
        if (maKhu == null || maKhu.trim().isEmpty()) {
            throw new Exception("Mã khu máy không được để trống");
        }

        // 3. Kiểm tra khu máy tồn tại
        KhuMay existing = khuMayDAO.getById(maKhu);
        if (existing == null) {
            throw new Exception("Khu máy không tồn tại: " + maKhu);
        }

        // 4. Kiểm tra trạng thái khu máy (không xóa khu đã NGUNG)
        if ("NGUNG".equals(existing.getTrangthai())) {
            throw new Exception("Khu máy đã được ngưng hoạt động trước đó");
        }

        // 5. Gọi DAO delete (DAO sẽ kiểm tra máy DANGCHOI, SET MaKhu = NULL, soft delete → NGUNG)
        boolean result = khuMayDAO.delete(maKhu);

        if (!result) {
            throw new Exception("Xóa khu máy thất bại");
        }

        // 6. Ghi log hành động
        PermissionHelper.logAction("XOA_KHU_MAY", "Xóa (ngưng) khu máy: " + maKhu);

        // 7. Trả về true
        return true;
    }

    // ============== 5. ĐẾM SỐ MÁY TRONG KHU ==============

    /**
     * Đếm số máy tính hiện có trong khu
     * Phân quyền: QUANLY / NHANVIEN
     *
     * Logic: Lấy tất cả máy tính, lọc theo MaKhu
     *
     * @param maKhu Mã khu máy cần đếm
     * @return Số lượng máy tính trong khu
     * @throws Exception nếu không có quyền hoặc khu không tồn tại
     */
    public int demSoMayTrongKhu(String maKhu) throws Exception {
        // 1. Kiểm tra phân quyền (QUANLY hoặc NHANVIEN)
        PermissionHelper.requireNhanVien();

        // 2. Kiểm tra mã khu không rỗng
        if (maKhu == null || maKhu.trim().isEmpty()) {
            throw new Exception("Mã khu máy không được để trống");
        }

        // 3. Kiểm tra khu máy tồn tại
        KhuMay existing = khuMayDAO.getById(maKhu);
        if (existing == null) {
            throw new Exception("Khu máy không tồn tại: " + maKhu);
        }

        // 4. Đếm số máy trong khu
        return demSoMayTrongKhuInternal(maKhu);
    }

    // ============== PRIVATE HELPER METHODS ==============

    /**
     * Đếm số máy trong khu (internal - không kiểm tra phân quyền)
     * Dùng nội bộ trong BUS để kiểm tra ràng buộc nghiệp vụ
     *
     * @param maKhu Mã khu máy
     * @return Số lượng máy tính trong khu
     */
    private int demSoMayTrongKhuInternal(String maKhu) {
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