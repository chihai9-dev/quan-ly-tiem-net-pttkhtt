package utils;

import entity.KhachHang;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class KhachHangExporter {

    public KhachHangExporter() {
    }

    public static void exportKhachHang(File file,
                                       List<KhachHang> list){
        try(Workbook workbook = new XSSFWorkbook()){
            Sheet sheet = workbook.createSheet("DanhSachKhachHang");

            generatedHeadher(sheet, workbook);

            setColumnWidths(sheet);

            addList(list, sheet,workbook);

            try(FileOutputStream fo = new FileOutputStream(file)){
                workbook.write(fo);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }










    private static CellStyle getMoneyStyle(Workbook workbook) {
        DataFormat dataFormat = workbook.createDataFormat();
        CellStyle moneyStyle = workbook.createCellStyle();
        moneyStyle.setDataFormat(dataFormat.getFormat("#,##0"));
        return moneyStyle;
    }

    private static void addList(List<KhachHang> list, Sheet sheet,Workbook workbook) {

        for(int i = 1; i < list.size(); i++){
            Row row = sheet.createRow(i);
            row.createCell(0).setCellValue(list.get(i).getMakh());
            row.createCell(1).setCellValue(list.get(i).getHo()+ list.get(i).getTen());
            row.createCell(2).setCellValue(list.get(i).getSodienthoai());
            Cell sd = row.createCell(3);
            sd.setCellValue(list.get(i).getSodu());
            sd.setCellStyle(getMoneyStyle(workbook));

            row.createCell(4).setCellValue(list.get(i).getTrangthai());

        }
    }

    private static void setColumnWidths(Sheet sheet) {
        sheet.setColumnWidth(0,5_000);
        sheet.setColumnWidth(1,5_000);
        sheet.setColumnWidth(2,5_000);
        sheet.setColumnWidth(3,5_000);
        sheet.setColumnWidth(4,5_000);
    }

    private static void generatedHeadher(Sheet sheet, Workbook workbook) {
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Mã SV","Họ tên","SĐT","Số Dư","Trạng thái"};
        for(int i = 0 ; i < headers.length ; i++){
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(getCellStyle(workbook));
        }
    }

    private static CellStyle getCellStyle(Workbook workbook) {
        CellStyle header = workbook.createCellStyle();
        Font boldFont = workbook.createFont();
        boldFont.setBold(true);
        header.setFont(boldFont);


        //trả về vị trí màu trong excel
        //pattern là chấm bi thì nó là màu của chấm bi
        header.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());

        //thiết lập họa tiết full ô
        header.setFillPattern(FillPatternType.SOLID_FOREGROUND);


        return header;
    }




}
