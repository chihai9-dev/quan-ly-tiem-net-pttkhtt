<div align="center">

```
 ██████╗ ██╗   ██╗ █████╗ ███╗   ██╗    ██╗  ██╗   ██╗    ████████╗██╗███████╗███╗   ███╗    ███╗   ██╗███████╗████████╗
██╔═══██╗██║   ██║██╔══██╗████╗  ██║    ██║  ╚██╗ ██╔╝    ╚══██╔══╝██║██╔════╝████╗ ████║    ████╗  ██║██╔════╝╚══██╔══╝
██║   ██║██║   ██║███████║██╔██╗ ██║    ██║   ╚████╔╝        ██║   ██║█████╗  ██╔████╔██║    ██╔██╗ ██║█████╗     ██║   
██║▄▄ ██║██║   ██║██╔══██║██║╚██╗██║    ██║    ╚██╔╝         ██║   ██║██╔══╝  ██║╚██╔╝██║    ██║╚██╗██║██╔══╝     ██║   
╚██████╔╝╚██████╔╝██║  ██║██║ ╚████║    ███████╗██║          ██║   ██║███████╗██║ ╚═╝ ██║    ██║ ╚████║███████╗   ██║   
 ╚══▀▀═╝  ╚═════╝ ╚═╝  ╚═╝╚═╝  ╚═══╝   ╚══════╝╚═╝          ╚═╝   ╚═╝╚══════╝╚═╝     ╚═╝    ╚═╝  ╚═══╝╚══════╝   ╚═╝   
```

# Quản Lý Tiệm Net

**Hệ thống quản lý quán Internet toàn diện — JavaFX & MySQL**

---

[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/21/)
[![JavaFX](https://img.shields.io/badge/JavaFX-21-0078D7?style=for-the-badge&logo=java&logoColor=white)](https://openjfx.io/)
[![MySQL](https://img.shields.io/badge/MySQL-9.5-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Maven](https://img.shields.io/badge/Maven-3.x-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg?style=for-the-badge)](LICENSE)

</div>

---

## Mục Lục

<details open>
<summary><b>Xem nội dung</b></summary>

- [Giới Thiệu](#giới-thiệu)
- [Tính Năng](#tính-năng)
- [Công Nghệ](#công-nghệ)
- [Yêu Cầu Hệ Thống](#yêu-cầu-hệ-thống)
- [Cài Đặt & Chạy](#cài-đặt--chạy)
- [Cơ Sở Dữ Liệu](#cơ-sở-dữ-liệu)
- [Kiến Trúc Dự Án](#kiến-trúc-dự-án)
- [Phân Quyền](#phân-quyền)
- [Dữ Liệu Mẫu](#dữ-liệu-mẫu)

</details>

---

## Giới Thiệu

> **Quản Lý Tiệm Net** là ứng dụng desktop quản lý quán Internet, phát triển trong khuôn khổ môn học **Phân Tích Thiết Kế Hệ Thống Thông Tin (PTTKHTT)**.

Hệ thống hỗ trợ toàn bộ vòng đời hoạt động của một quán net:

```
[Khách đến] --> [Mở phiên trên máy] --> [Chơi / Đặt dịch vụ]
                                                  |
[Xuất PDF] <---- [Trừ số dư tài khoản] <---- [Kết thúc phiên]
```

---

## Tính Năng

<table>
<tr>
<td width="50%" valign="top">

### Quản Lý (Admin)
- Sơ đồ máy – Xem trực quan khu và trạng thái máy
- Quản lý máy tính, phân khu
- Quản lý nhân viên & phân quyền chức vụ
- Quản lý dịch vụ (thực đơn + tồn kho tự động)
- Gói giờ chơi (Combo, Ngày, Tuần, Tháng)
- Chương trình khuyến mãi theo % / số tiền / tặng giờ
- Phiếu nhập hàng từ nhà cung cấp
- Thống kê doanh thu & xuất file Excel

</td>
<td width="50%" valign="top">

### Thu Ngân / Nhân Viên
- Mở / kết thúc phiên từ sơ đồ máy
- Ghi nhận đặt dịch vụ trong phiên
- Nạp tiền tài khoản, áp khuyến mãi tự động
- Xem hóa đơn & xuất PDF

### Khách Hàng
- Xem thông tin tài khoản & số dư
- Lịch sử phiên sử dụng máy
- Xem gói giờ còn lại
- Tự đặt dịch vụ trong phiên hiện tại

</td>
</tr>
</table>

---

## Công Nghệ

<div align="center">

| Thành Phần | Công Nghệ | Phiên Bản |
|:---:|:---:|:---:|
| Ngôn ngữ | **Java** | `21` |
| Giao diện | **JavaFX + FXML** | `21` |
| Cơ sở dữ liệu | **MySQL** | `9.5` |
| JDBC Driver | **MySQL Connector/J** | `8.3.0` |
| Build Tool | **Apache Maven** | `3.x` |
| Xuất PDF | **iText PDF** | `5.5.13.3` |
| Xuất Excel | **Apache POI** | `5.5.1` |
| Icon UI | **Ikonli Material Design 2** | `12.3.1` |

</div>

---

## Yêu Cầu Hệ Thống

```
- JDK 21 trở lên
- MySQL 8.0 hoặc 9.x
- Apache Maven 3.6 trở lên
- RAM tối thiểu 512 MB
- Windows 10/11 / Linux / macOS
```

---

## Cài Đặt & Chạy

### Bước 1 — Clone dự án

```bash
git clone https://github.com/duyddawngnef/quan-ly-tiem-net-pttkhtt.git
cd quan-ly-tiem-net-pttkhtt
```

### Bước 2 — Tạo cơ sở dữ liệu

```bash
mysql -u root -p < database/quanlytiemnet.sql
```

> Hoặc mở `database/quanlytiemnet.sql` bằng **HeidiSQL** / **MySQL Workbench** rồi Execute.  
> Script sẽ tự tạo database `quanlytiemnet_simple` với đầy đủ bảng và dữ liệu mẫu.

### Bước 3 — Cấu hình kết nối

Mở `src/main/resources/config.properties` và chỉnh sửa:

```properties
db.url=jdbc:mysql://localhost:3306/quanlytiemnet_simple?useUnicode=true&characterEncoding=utf8
db.username=root
db.password=YOUR_PASSWORD_HERE
```

### Bước 4 — Chạy ứng dụng

```bash
mvn clean javafx:run
```

---

## Cơ Sở Dữ Liệu

**Database:** `quanlytiemnet_simple` &nbsp;|&nbsp; **Charset:** `utf8mb4` &nbsp;|&nbsp; **Engine:** InnoDB

<details>
<summary><b>Sơ đồ quan hệ các bảng</b></summary>

```
            ┌──────────┐        ┌──────────────────────┐
            │ nhanvien │        │      khachhang        │
            └────┬─────┘        └───────┬──────────────┘
                 │                      │
     ┌───────────┼──────────────────────┤
     │      ┌────▼─────────────────────┐│
     │      │       phiensudung        ││
     │      └────┬─────────────────────┘│
     │           │                      │
     │    ┌──────┴──────┐   ┌───────────▼───────────┐
     │    │sudungdichvu │   │     lichsunaptien      │
     │    └──────┬──────┘   │  + chuongtrinhkm      │
     │           │           └───────────────────────┘
     │      ┌────▼───┐
     │      │ dichvu │◄──────── chitietphieunhap
     │      └────────┘               │
     │                        phieunhaphang ───► nhacungcap
     │
     │    ┌──────────────────────────────────┐
     │    │              hoadon              │
     │    └──────────────┬───────────────────┘
     │                   │
     │            chitiethoadon (GIOCHOI / DICHVU)
     │
     │    ┌───────────────────────────┐
     └───►│  goidichvu_khachhang      │◄── goidichvu
          └───────────────────────────┘

┌──────────┐     ┌─────────┐
│  khumay  │◄────│ maytinh │
└──────────┘     └─────────┘
```

</details>

<details>
<summary><b>Danh sách 16 bảng</b></summary>

| # | Bảng | Mô tả |
|---|---|---|
| 1 | `nhanvien` | Nhân viên (Quản lý / Thu ngân / Nhân viên) |
| 2 | `khachhang` | Khách hàng & số dư tài khoản |
| 3 | `maytinh` | Danh sách máy tính |
| 4 | `khumay` | Phân khu & giá mỗi giờ |
| 5 | `phiensudung` | Phiên sử dụng máy |
| 6 | `dichvu` | Danh mục dịch vụ (đồ uống, thức ăn...) |
| 7 | `sudungdichvu` | Dịch vụ đặt trong mỗi phiên |
| 8 | `goidichvu` | Gói giờ chơi |
| 9 | `goidichvu_khachhang` | Gói của từng khách hàng |
| 10 | `hoadon` | Hóa đơn thanh toán |
| 11 | `chitiethoadon` | Chi tiết hóa đơn |
| 12 | `lichsunaptien` | Lịch sử nạp tiền |
| 13 | `chuongtrinhkhuyenmai` | Chương trình khuyến mãi |
| 14 | `nhacungcap` | Nhà cung cấp hàng hóa |
| 15 | `phieunhaphang` | Phiếu nhập hàng |
| 16 | `chitietphieunhap` | Chi tiết phiếu nhập hàng |

</details>

---

## Kiến Trúc Dự Án

Dự án theo mô hình **3-Tier Architecture**:

```
+-----------------------------------------------------------+
|                   PRESENTATION TIER                       |
|     JavaFX (FXML + CSS)  |  Controller  |  Dialog        |
+-----------------------------------------------------------+
|                  BUSINESS LOGIC TIER                      |
|             BUS — Validation, Tính toán, Rules            |
+-----------------------------------------------------------+
|                    DATA ACCESS TIER                       |
|        DAO (SQL)   |   Entity (POJO)   |   MySQL          |
+-----------------------------------------------------------+
```

```
quan-ly-tiem-net-pttkhtt/
|
+-- database/
|   +-- quanlytiemnet.sql          (Script CSDL + dữ liệu mẫu)
|
+-- docs/
|   +-- BaoCao_PTTKHTT.docx        (Tài liệu phân tích nghiệp vụ)
|
+-- src/main/
|   +-- java/
|   |   +-- Main.java
|   |   +-- entity/                (17 POJO classes)
|   |   +-- dao/                   (18 DAO classes - SQL)
|   |   +-- bus/                   (15 BUS classes - Logic)
|   |   +-- gui/
|   |   |   +-- controller/        (22 Controllers)
|   |   |   +-- dialog/
|   |   +-- utils/                 (PDF, Excel, Session, Permission...)
|   |
|   +-- resources/
|       +-- config.properties
|       +-- fxml/                  (22 màn hình FXML)
|       +-- css/
|       +-- fonts/
|       +-- images/
|
+-- pom.xml
```

### Các lớp tiện ích (Utils)

| Lớp | Chức năng |
|---|---|
| `SessionManager` | Lưu trạng thái đăng nhập và thông tin người dùng hiện tại |
| `PermissionHelper` | Kiểm soát quyền truy cập theo vai trò |
| `HoaDonExporter` | Xuất hóa đơn ra file **PDF** (iText) |
| `ThongKeExcelExporter` | Xuất báo cáo thống kê ra file **Excel** (Apache POI) |
| `PasswordEncoder` | Mã hóa mật khẩu **SHA-256** |
| `DBConnection` | Singleton quản lý kết nối MySQL |

---

## Phân Quyền

```
+--------------------------------------------------------------------+
|  QUANLY                            (Tài khoản nhân viên)          |
|  > Toàn quyền truy cập tất cả chức năng                           |
|  > Thống kê, báo cáo, cấu hình hệ thống                           |
+--------------------------------------------------------------------+
|  THUNGAN / NHANVIEN                (Tài khoản nhân viên)          |
|  > Mở/Kết thúc phiên, nạp tiền, bán gói, đặt dịch vụ             |
|  > Quản lý hóa đơn và khách hàng                                   |
|  x Không có thống kê và cấu hình hệ thống                         |
+--------------------------------------------------------------------+
|  KHACHHANG                         (Tài khoản khách hàng)         |
|  > Xem thông tin cá nhân, lịch sử phiên, gói giờ                  |
|  > Đặt dịch vụ trong phiên hiện tại                                |
|  x Không có bất kỳ chức năng quản trị nào                         |
+--------------------------------------------------------------------+
```

---

## Dữ Liệu Mẫu

### Tài Khoản Nhân Viên

| Tên Đăng Nhập | Mật Khẩu | Vai Trò |
|---|---|---|
| `admin` | `123456` | Quản lý |
| `thungan01` | `123456` | Thu ngân |
| `thungan02` | `123456` | Thu ngân |
| `kythuat01` | `123456` | Nhân viên |
| `phucvu02` | `123456` | Nhân viên |

### Tài Khoản Khách Hàng

| Tên Đăng Nhập | Mật Khẩu | Số Dư |
|---|---|---|
| `hoangnam` | `123456` | 83,800 đ |
| `hongle` | `123456` | 168,000 đ |
| `hieugaming` | `123456` | 630,000 đ |
| `long_dragon` | `123456` | 160,000 đ |

> **Lưu ý:** Mật khẩu lưu dạng **SHA-256** trong môi trường production. Dữ liệu mẫu dùng plaintext cho mục đích phát triển.

### Các Khu Máy

| Khu | Tên | Giá/Giờ | Số Máy Tối Đa |
|---|---|---|---|
| KHU001 | Khu Thường (A) | 5,000 đ | 20 |
| KHU002 | Khu VIP (B) | 8,000 đ | 15 |
| KHU003 | Khu Thi Đấu (S) | 10,000 đ | 10 |
| KHU004 | Khu Couple (C) | 12,000 đ | 5 |
| KHU005 | Khu Hút Thuốc (D) | 6,000 đ | 15 |

### Gói Giờ Chơi

| Mã | Tên Gói | Số Giờ | Giá Gói |
|---|---|---|---|
| GOI001 | Combo Sáng (3h) | 3h | 12,000 đ |
| GOI002 | Combo Đêm (7h) | 7h | 25,000 đ |
| GOI003 | Gói Ngày (10h) | 10h | 40,000 đ |
| GOI004 | Gói Tuần (50h) | 50h | 200,000 đ |
| GOI005 | Gói VIP Tháng | 80h | 500,000 đ |

### Hình Thức Thanh Toán

[![](https://img.shields.io/badge/Tien_Mat-gray?style=flat-square)]()
[![](https://img.shields.io/badge/Chuyen_Khoan-gray?style=flat-square)]()
[![](https://img.shields.io/badge/MoMo-A926B0?style=flat-square&logo=data:image/png;base64,)]()
[![](https://img.shields.io/badge/VNPay-0066B3?style=flat-square)]()
[![](https://img.shields.io/badge/ZaloPay-0068ff?style=flat-square)]()
[![](https://img.shields.io/badge/Tai_Khoan_He_Thong-2196F3?style=flat-square)]()

---

## Thành Viên Nhóm

<div align="center">

| STT | Họ và Tên | MSSV | Vai Trò |
|:---:|---|:---:|:---:|
| 1 | Phạm Duy Đăng | `3124410068` | Nhóm Trưởng |
| 2 | Nguyễn Phúc Thắng | `3124410331` | Thành viên |
| 3 | Nguyễn Vương Hoàng Tịnh | `3124560091` | Thành viên |
| 4 | Nguyễn Thành Long | `3124410195` | Thành viên |
| 5 | Nguyễn Phú Tài | `3121410435` | Thành viên |
| 6 | Châu Chí Hải | `3124410076` | Thành viên |

</div>

---

<div align="center">

Môn học: **Phân Tích Thiết Kế Hệ Thống Thông Tin** &nbsp;|&nbsp; Phiên bản: `1.0-SNAPSHOT`

[![MIT License](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)](LICENSE)

</div>
