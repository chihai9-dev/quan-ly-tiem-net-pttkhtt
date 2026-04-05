package utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Documented;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Phaser;

public class ThongKeExporter {
    private static final NumberFormat VND = NumberFormat.getInstance(new Locale("vi","VN"));
    private static final DateTimeFormatter  FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private ThongKeExporter() {
    }

    public static void exportThongKe(File file,
                                     String tieuDe,
                                     String kieuThongKe,
                                     LocalDate tuNgay,
                                     LocalDate denNgay,
                                     List<Map<String, Object>> data,
                                     double tongDoanhThu,
                                     double tongChiPhi,
                                     double tongLoiNhuan,
                                     int soPhienDangChoi) throws Exception {

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("ThongKe");

            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);

            CellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            setBorder(headerStyle);

            CellStyle textStyle = workbook.createCellStyle();
            setBorder(textStyle);

            DataFormat dataFormat = workbook.createDataFormat();
            CellStyle moneyStyle = workbook.createCellStyle();
            setBorder(moneyStyle);
            moneyStyle.setDataFormat(dataFormat.getFormat("#,##0"));

            int rowIndex = 0;

            Row titleRow = sheet.createRow(rowIndex++);
            titleRow.createCell(0).setCellValue(tieuDe);
            titleRow.getCell(0).setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

            Row infoRow1 = sheet.createRow(rowIndex++);
            infoRow1.createCell(0).setCellValue("Kiểu thống kê:");
            infoRow1.createCell(1).setCellValue(kieuThongKe);
            infoRow1.createCell(2).setCellValue("Xuất lúc:");
            infoRow1.createCell(3).setCellValue(
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
            );

            Row infoRow2 = sheet.createRow(rowIndex++);
            infoRow2.createCell(0).setCellValue("Từ ngày:");
            infoRow2.createCell(1).setCellValue(
                    tuNgay != null ? tuNgay.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : ""
            );
            infoRow2.createCell(2).setCellValue("Đến ngày:");
            infoRow2.createCell(3).setCellValue(
                    denNgay != null ? denNgay.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : ""
            );

            Row infoRow3 = sheet.createRow(rowIndex++);
            infoRow3.createCell(0).setCellValue("Tổng doanh thu:");
            infoRow3.createCell(1).setCellValue(tongDoanhThu);
            infoRow3.createCell(1).setCellStyle(moneyStyle);

            infoRow3.createCell(2).setCellValue("Tổng chi phí:");
            infoRow3.createCell(3).setCellValue(tongChiPhi);
            infoRow3.createCell(3).setCellStyle(moneyStyle);

            Row infoRow4 = sheet.createRow(rowIndex++);
            infoRow4.createCell(0).setCellValue("Tổng lợi nhuận:");
            infoRow4.createCell(1).setCellValue(tongLoiNhuan);
            infoRow4.createCell(1).setCellStyle(moneyStyle);

            infoRow4.createCell(2).setCellValue("Phiên đang chơi:");
            infoRow4.createCell(3).setCellValue(soPhienDangChoi);

            rowIndex++;

            Row headerRow = sheet.createRow(rowIndex++);
            String[] headers = {"Thời gian", "Doanh thu", "Nhập hàng / Chi phí", "Lợi nhuận"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            if (data != null) {
                for (Map<String, Object> rowData : data) {
                    Row row = sheet.createRow(rowIndex++);

                    Cell c0 = row.createCell(0);
                    c0.setCellValue(String.valueOf(rowData.getOrDefault("ThoiGian", "")));
                    c0.setCellStyle(textStyle);

                    Cell c1 = row.createCell(1);
                    c1.setCellValue(toDouble(rowData.get("TongDoanhThu"), rowData.get("Thu")));
                    c1.setCellStyle(moneyStyle);

                    Cell c2 = row.createCell(2);
                    c2.setCellValue(toDouble(rowData.get("TongNhapHang"), rowData.get("Chi")));
                    c2.setCellStyle(moneyStyle);

                    Cell c3 = row.createCell(3);
                    c3.setCellValue(toDouble(rowData.get("LoiNhuan"), 0));
                    c3.setCellStyle(moneyStyle);
                }
            }

            for (int i = 0; i < 4; i++) {
                sheet.autoSizeColumn(i);
                sheet.setColumnWidth(i, Math.min(sheet.getColumnWidth(i) + 1024, 20000));
            }

            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }
        }
    }




    public static  void xuatPDFThongKe(String filePath,
                                       String tieuDe,
                                       String kieuThongKe,
                                       LocalDate tuNgay,
                                       LocalDate denNgay,
                                       List<Map<String, Object>> data,
                                       double tongDoanhThu,
                                       double tongChiPhi,
                                       double tongLoiNhuan,
                                       int soPhienDangChoi) throws IOException, DocumentException {

        Document doc = new Document(PageSize.A4,36,36,36,36);

        PdfWriter.getInstance(doc,new FileOutputStream(filePath));

        doc.open();

        BaseFont bf;
        try{
            bf = BaseFont.createFont("c:/windows/fonts/arial.ttf",
                    BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

        } catch (Exception e) {
            bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            throw new RuntimeException(e);
        }
        com.itextpdf.text.Font fontTitle   = new com.itextpdf.text.Font(bf, 16, com.itextpdf.text.Font.BOLD);
        com.itextpdf.text.Font fontBold    = new com.itextpdf.text.Font(bf, 10, com.itextpdf.text.Font.BOLD);
        com.itextpdf.text.Font fontNormal  = new com.itextpdf.text.Font(bf, 10);
        com.itextpdf.text.Font fontSmall   = new com.itextpdf.text.Font(bf,  9);
        com.itextpdf.text.Font fontRed     = new com.itextpdf.text.Font(bf, 10, com.itextpdf.text.Font.BOLD, new BaseColor(229, 57, 53));
        com.itextpdf.text.Font fontGreen   = new com.itextpdf.text.Font(bf, 10, com.itextpdf.text.Font.BOLD,new BaseColor(21,  101, 192));
        //Grey
        com.itextpdf.text.Font fSub     = new com.itextpdf.text.Font(bf,  8, com.itextpdf.text.Font.NORMAL, new BaseColor(97,   97,  97));


        Paragraph title = new Paragraph(tieuDe,fontTitle);
        title.setAlignment(Element.ALIGN_CENTER);
        doc.add(title);
        doc.add(new Paragraph(" "));


        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setWidths(new float[]{1f,1f});
        infoTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);


        addInfoCell(infoTable,"Kiểu thống kê:",kieuThongKe,fontBold,fontNormal);
        addInfoCell(infoTable,"Xuất lúc:",LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")), fontBold,fontNormal);
        addInfoCell(infoTable,"Từ ngày:",tuNgay.format(FMT),fontBold,fontNormal);
        addInfoCell(infoTable,"Đến ngày:",denNgay.format(FMT),fontBold,fontNormal);


        doc.add(infoTable);
        doc.add(new Paragraph(" "));

        //Line Separator
        addLineSeparator(doc);


        doc.add(new Paragraph(" "));



        Paragraph tkTitle = new Paragraph("Bảng thống kê",fontTitle);
        doc.add(tkTitle);
        doc.add(new Paragraph(" "));


        //table

        String[] headers = {"Thời gian","Doanh thu","Nhập hàng/Chi phí","Lợi nhuân"};
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1f,1f,1f,1f});

        for(String x : headers){
            PdfPCell cell = new PdfPCell(new Phrase(x,fontBold));
            cell.setBackgroundColor(new BaseColor(21, 101, 192));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(6);
            com.itextpdf.text.Font fWhite = new com.itextpdf.text.Font(bf, 10,
                    com.itextpdf.text.Font.BOLD, BaseColor.WHITE);
            cell.setPhrase(new Phrase(x,fWhite));
            table.addCell(cell);
        }

        boolean odd = true;
        for(Map<String,Object> x : data){
            BaseColor rowColor = odd ? BaseColor.WHITE : new BaseColor(240, 245, 255);
            String c0Key = "ThoiGian";
            String c1Key = "TongDoanhThu";
            String c2Key = "TongNhapHang";
            String c3Key = "LoiNhuan";

            PdfPCell c0 = new PdfPCell(new Phrase(String.valueOf(x.getOrDefault(c0Key,"")),fontNormal));
            c0.setBackgroundColor(rowColor);
            c0.setPadding(5);
            c0.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(c0);


            addCell(x, c1Key, fontNormal,fontRed, rowColor, table);
            addCell(x, c2Key, fontNormal, fontRed,rowColor, table);
            addCell(x, c3Key, fontNormal,fontRed, rowColor, table);


            odd = !odd;

        }

        PdfPCell conSum = new PdfPCell(new Phrase("Tổng cộng",fontGreen));
        conSum.setHorizontalAlignment(Element.ALIGN_LEFT);
        conSum.setPadding(5);
        table.addCell(conSum);
        addCellConSum(tongDoanhThu, fontGreen, fontRed, table);
        addCellConSum(tongChiPhi, fontGreen, fontRed, table);
        addCellConSum(tongLoiNhuan, fontGreen, fontRed, table);


        doc.add(table);




        doc.add(new Paragraph(" "));

        addLineSeparator(doc);

        Paragraph pConclude = new Paragraph("Báo cáo được tạo bởi Hệ Thống Quản Lý Tiệm Net",fSub);
        pConclude.setAlignment(Element.ALIGN_CENTER);
        doc.add(pConclude);

        doc.close();


    }

    private static void addLineSeparator(Document doc) throws DocumentException {
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
    }

    private static void addCellConSum(double tong, com.itextpdf.text.Font fontGreen, com.itextpdf.text.Font fontRed, PdfPTable table) {
        PdfPCell conSum;
        if(tong >= 0 ){
            conSum = new PdfPCell(new Phrase(VND.format(tong), fontGreen));
        }
        else{
            conSum = new PdfPCell(new Phrase(VND.format(tong), fontRed));
        }
        conSum.setPadding(5);
        conSum.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(conSum);
    }

    private static void addCell(Map<String, Object> x, String c1Key,
                                com.itextpdf.text.Font fontNormal,
                                com.itextpdf.text.Font fontRed,
                                BaseColor rowColor, PdfPTable table) {


        PdfPCell c1 ;
        double tong = (Double)x.getOrDefault(c1Key,"");
        if(tong >= 0 ){
            c1 = new PdfPCell(new Phrase(VND.format(tong), fontNormal));
        }
        else{
            c1 = new PdfPCell(new Phrase(VND.format(tong), fontRed));
        }
        c1.setBackgroundColor(rowColor);
        c1.setPadding(5);
        c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(c1);
    }

    private static PdfPCell getLsCell() {
        PdfPCell lsCell = new PdfPCell();
        lsCell.setBorderWidthTop(1f);
        lsCell.setBorderColorTop(BaseColor.LIGHT_GRAY);
        lsCell.setBorderWidthBottom(0);
        lsCell.setBorderWidthLeft(0);
        lsCell.setBorderWidthRight(0);
        lsCell.setPadding(0);
        return lsCell;
    }

    private static void addInfoCell(PdfPTable t , String label , String value,
                                    com.itextpdf.text.Font fBold,
                                    com.itextpdf.text.Font fNormal){
        Phrase p = new Phrase();
        p.add(new Chunk(label,fBold));
        p.add(new Chunk(value,fNormal));
        PdfPCell cell = new PdfPCell(p);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(4);
        t.addCell(cell);

    }
    private static void addInfoCellConclude(PdfPTable t , String label , String value,
                                    com.itextpdf.text.Font fBold,
                                    com.itextpdf.text.Font fNormal){

        PdfPCell lbl = new PdfPCell(new Phrase(label,fBold));
        lbl.setPaddingBottom(3);
        lbl.setBorder(Rectangle.NO_BORDER);
        t.addCell(lbl);


        PdfPCell cell = new PdfPCell(new Phrase(value,fNormal));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setPaddingBottom(3);
        t.addCell(cell);

    }

    private static double toDouble(Object value, Object fallback) {
        Object actual = value != null ? value : fallback;
        if (actual instanceof Number) {
            return ((Number) actual).doubleValue();
        }
        try {
            return Double.parseDouble(String.valueOf(actual));
        } catch (Exception e) {
            return 0.0;
        }
    }

    private static void setBorder(CellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
    }
}