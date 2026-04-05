package utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class HoaDonExporter {

    private static final NumberFormat VND = NumberFormat.getInstance(new Locale("vi", "VN"));
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static void xuatPDFHoaDon(HoaDon hd, List<ChiTietHoaDon> chiTiet, String filePath)
            throws DocumentException, IOException {
        Document doc = new Document(PageSize.A5, 36, 36, 36, 36);
        PdfWriter.getInstance(doc, new FileOutputStream(filePath));
        doc.open();
        // Font Unicode hỗ trợ tiếng Việt
        BaseFont bf;
        try {
            bf = BaseFont.createFont("c:/windows/fonts/arial.ttf",
                    BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        } catch (Exception e) {
            bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
        }
        com.itextpdf.text.Font fontTitle   = new com.itextpdf.text.Font(bf, 16, com.itextpdf.text.Font.BOLD);
        com.itextpdf.text.Font fontBold    = new com.itextpdf.text.Font(bf, 10, com.itextpdf.text.Font.BOLD);
        com.itextpdf.text.Font fontNormal  = new com.itextpdf.text.Font(bf, 10);
        com.itextpdf.text.Font fontSmall   = new com.itextpdf.text.Font(bf,  9);
        com.itextpdf.text.Font fontRed     = new com.itextpdf.text.Font(bf, 10, com.itextpdf.text.Font.BOLD, new BaseColor(229, 57, 53));
        com.itextpdf.text.Font fontGreen   = new com.itextpdf.text.Font(bf, 12, com.itextpdf.text.Font.BOLD, new BaseColor(21, 101, 192));
        //Header
        Paragraph title = new Paragraph("TIỆM NET - HÓA ĐƠN THANH TOÁN", fontTitle);
        title.setAlignment(Element.ALIGN_CENTER);
        doc.add(title);
        doc.add(new Paragraph(" "));
        //Thông tin hóa đơn
        PdfPTable infoTable = new PdfPTable(2);
        // width = 100%
        infoTable.setWidthPercentage(100);
        //chi bảng ra 2 phần đều nhau
        infoTable.setWidths(new float[]{1f, 1f});
        infoTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
        //chỉ áp dụng cho .addCell("văn bản ) , PdfCell thì cần định dạng

        addInfoCell(infoTable, "Mã hóa đơn: ", hd.getMaHD(), fontBold, fontNormal);
        addInfoCell(infoTable, "Ngày lập: ", hd.getNgayLap() != null ? hd.getNgayLap().format(FMT) : "-", fontBold, fontNormal);
        addInfoCell(infoTable, "Khách hàng: ", hd.getMaKH() != null ? hd.getMaKH() : "-", fontBold, fontNormal);
        addInfoCell(infoTable, "Nhân viên: ", hd.getMaNV() != null ? hd.getMaNV() : "-", fontBold, fontNormal);
        addInfoCell(infoTable, "Máy/Phiên: ", hd.getMaPhien() != null ? hd.getMaPhien() : "-", fontBold, fontNormal);
        addInfoCell(infoTable, "Trạng thái: ", hd.getTrangThai() != null ? hd.getTrangThai() : "-", fontBold, fontNormal);
        doc.add(infoTable);

        doc.add(new Paragraph(" "));
        //gạch ngang
        PdfPTable ls = new PdfPTable(1);
        ls.setWidthPercentage(100);
        PdfPCell lsCell = new PdfPCell();
        lsCell.setBorderWidthTop(1f);
        lsCell.setBorderColorTop(BaseColor.LIGHT_GRAY);
        lsCell.setBorderWidthBottom(0);
        lsCell.setBorderWidthLeft(0);
        lsCell.setBorderWidthRight(0);
        lsCell.setPadding(0);
        ls.addCell(lsCell);
        doc.add(ls);
        doc.add(new Paragraph(" "));

        Paragraph dvTitle = new Paragraph("Chi tiết dịch vụ", fontBold);
        doc.add(dvTitle);
        doc.add(new Paragraph(" "));
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3f, 1f, 2f, 2f});

        String[] headers = {"Dịch vụ", "SL", "Đơn giá", "Thành tiền"};
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, fontBold));
            cell.setBackgroundColor(new BaseColor(21, 101, 192));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(6);
            com.itextpdf.text.Font fWhite = new com.itextpdf.text.Font(bf, 10,
                    com.itextpdf.text.Font.BOLD, BaseColor.WHITE);
            cell.setPhrase(new Phrase(h, fWhite));
            table.addCell(cell);
        }

        if (chiTiet != null && !chiTiet.isEmpty()) {
            boolean odd = true;
            for (ChiTietHoaDon ct : chiTiet) {
                BaseColor rowColor = odd ? BaseColor.WHITE : new BaseColor(240, 245, 255);
                addTableRow(table, fontNormal, rowColor,
                        ct.getMoTa() != null ? ct.getMoTa() : "-",
                        String.valueOf((int) ct.getSoLuong()),
                        VND.format((long) ct.getDonGia()) + " ₫",
                        VND.format((long) ct.getThanhTien()) + " ₫");
                odd = !odd;
            }
        } else {
            PdfPCell empty = new PdfPCell(new Phrase("Không có dịch vụ", fontSmall));
            empty.setColspan(4);
            empty.setHorizontalAlignment(Element.ALIGN_CENTER);
            empty.setPadding(8);
            table.addCell(empty);
        }
        doc.add(table);
        doc.add(new Paragraph(" "));

        // ---- Tổng kết ----
        PdfPTable sumTable = new PdfPTable(2);
        sumTable.setWidthPercentage(60);
        sumTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
        sumTable.setWidths(new float[]{2f, 2f});
        sumTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

        addSumRow(sumTable, "Tiền giờ chơi:", VND.format((long) hd.getTienGioChoi()) + " ₫", fontNormal, fontNormal);
        addSumRow(sumTable, "Tổng tiền:", VND.format((long) hd.getTongTien()) + " ₫", fontBold, fontBold);
        if (hd.getGiamGia() > 0) addSumRow(sumTable, "Giảm giá:", "- " + VND.format((long) hd.getGiamGia()) + " ₫", fontNormal, fontRed);

        // Thanh toán row với nền xanh
        PdfPCell lblTT = new PdfPCell(new Phrase("THANH TOÁN:", fontGreen));
        lblTT.setBorder(Rectangle.TOP);
        lblTT.setPaddingTop(6);
        lblTT.setBorderColorTop(new BaseColor(21, 101, 192));
        sumTable.addCell(lblTT);
        PdfPCell valTT = new PdfPCell(new Phrase(VND.format((long) hd.getThanhToan()) + " ₫", fontGreen));
        valTT.setBorder(Rectangle.TOP);
        valTT.setPaddingTop(6);
        valTT.setBorderColorTop(new BaseColor(21, 101, 192));
        valTT.setHorizontalAlignment(Element.ALIGN_RIGHT);
        sumTable.addCell(valTT);

        doc.add(sumTable);

        // ---- Footer ----
        doc.add(new Paragraph(" "));
        Paragraph footer = new Paragraph("Cảm ơn quý khách đã sử dụng dịch vụ!", fontSmall);
        footer.setAlignment(Element.ALIGN_CENTER);
        doc.add(footer);

        doc.close();
    }

    // ===================== XUẤT EXCEL DANH SÁCH HÓA ĐƠN =====================

    public static void xuatExcelDanhSach(List<HoaDon> list, String filePath) throws IOException {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Danh sách hóa đơn");

            // Style tiêu đề
            CellStyle headerStyle = wb.createCellStyle();
            Font hFont = wb.createFont();
            hFont.setBold(true);
            hFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(hFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setBorderBottom(BorderStyle.THIN);

            // Style số tiền
            CellStyle moneyStyle = wb.createCellStyle();
            DataFormat fmt = wb.createDataFormat();
            moneyStyle.setDataFormat(fmt.getFormat("#,##0"));

            // Style xen kẽ
            CellStyle altStyle = wb.createCellStyle();
            altStyle.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.getIndex());
            altStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Header row
            String[] cols = {"Mã HĐ", "Khách hàng", "Máy/Phiên", "Nhân viên",
                    "Ngày lập", "Tiền giờ", "Tổng tiền", "Giảm giá", "Thanh toán", "Trạng thái"};
            Row hRow = sheet.createRow(0);
            for (int i = 0; i < cols.length; i++) {
                Cell c = hRow.createCell(i);
                c.setCellValue(cols[i]);
                c.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (HoaDon hd : list) {
                Row row = sheet.createRow(rowIdx);
                boolean isAlt = (rowIdx % 2 == 0);
                setCell(row, 0, hd.getMaHD(), isAlt ? altStyle : null);
                setCell(row, 1, hd.getMaKH() != null ? hd.getMaKH() : "-", isAlt ? altStyle : null);
                setCell(row, 2, hd.getMaPhien() != null ? hd.getMaPhien() : "-", isAlt ? altStyle : null);
                setCell(row, 3, hd.getMaNV() != null ? hd.getMaNV() : "-", isAlt ? altStyle : null);
                setCell(row, 4, hd.getNgayLap() != null ? hd.getNgayLap().format(FMT) : "-", isAlt ? altStyle : null);
                CellStyle ms = wb.createCellStyle();
                ms.cloneStyleFrom(moneyStyle);
                if (isAlt) ms.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.getIndex());
                setCellMoney(row, 5, hd.getTienGioChoi(), ms);
                setCellMoney(row, 6, hd.getTongTien(), ms);
                setCellMoney(row, 7, hd.getGiamGia(), ms);
                setCellMoney(row, 8, hd.getThanhToan(), ms);
                setCell(row, 9, hd.getTrangThai() != null ? hd.getTrangThai() : "-", isAlt ? altStyle : null);

                rowIdx++;
            }

            // Tổng cộng row
            Row totalRow = sheet.createRow(rowIdx + 1);
            CellStyle totalStyle = wb.createCellStyle();
            Font tf = wb.createFont();
            tf.setBold(true);
            totalStyle.setFont(tf);
            totalStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
            totalStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            totalStyle.setDataFormat(fmt.getFormat("#,##0"));

            Cell lblSum = totalRow.createCell(7);
            lblSum.setCellValue("TỔNG THANH TOÁN:");
            lblSum.setCellStyle(totalStyle);

            double totalThanhToan = list.stream().mapToDouble(HoaDon::getThanhToan).sum();
            Cell valSum = totalRow.createCell(8);
            valSum.setCellValue(totalThanhToan);
            valSum.setCellStyle(totalStyle);

            // Auto-size columns
            for (int i = 0; i < cols.length; i++) {
                sheet.autoSizeColumn(i);
                sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 512);
            }
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                wb.write(fos);
            }
        }
    }

    private static void addInfoCell(PdfPTable t, String label, String value,
                                    com.itextpdf.text.Font fBold,
                                    com.itextpdf.text.Font fNormal) {
        Phrase p = new Phrase();
        p.add(new Chunk(label, fBold));
        p.add(new Chunk(value, fNormal));
        PdfPCell cell = new PdfPCell(p);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPaddingBottom(4);
        t.addCell(cell);
    }

    private static void addTableRow(PdfPTable t, com.itextpdf.text.Font font, BaseColor bg, String... values) {
        boolean first = true;
        for (String v : values) {
            PdfPCell cell = new PdfPCell(new Phrase(v, font));
            cell.setBackgroundColor(bg);
            cell.setPadding(5);
            if (!first) cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            first = false;
            t.addCell(cell);
        }
    }

    private static void addSumRow(PdfPTable t, String label, String value, com.itextpdf.text.Font fLabel, com.itextpdf.text.Font fValue) {
        PdfPCell lbl = new PdfPCell(new Phrase(label, fLabel));
        lbl.setBorder(Rectangle.NO_BORDER);
        lbl.setPaddingBottom(3);
        t.addCell(lbl);
        PdfPCell val = new PdfPCell(new Phrase(value, fValue));
        val.setBorder(Rectangle.NO_BORDER);
        val.setHorizontalAlignment(Element.ALIGN_RIGHT);
        val.setPaddingBottom(3);
        t.addCell(val);
    }

    private static void setCell(Row row, int col, String value, CellStyle style) {
        Cell c = row.createCell(col);
        c.setCellValue(value);
        if (style != null) c.setCellStyle(style);
    }

    private static void setCellMoney(Row row, int col, double value, CellStyle style) {
        Cell c = row.createCell(col);
        c.setCellValue(value);
        if (style != null) c.setCellStyle(style);
    }
}