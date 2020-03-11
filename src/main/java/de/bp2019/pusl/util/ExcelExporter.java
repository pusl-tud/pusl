package de.bp2019.pusl.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.function.ValueProvider;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


/**
 * 
 * @param <T>
 * @author Luca Dinies, Leon Chemnitz
 */
public class ExcelExporter<T> {

    private List<ValueProvider<T, String>> valueProviders;
    private List<T> items;
    private List<String> headers;

    public ExcelExporter(){
        valueProviders = new ArrayList<ValueProvider<T, String>>();
        items = new ArrayList<T>();
        headers = new ArrayList<String>();
    }
    
    public void addColumn(String header, ValueProvider<T, String> valueProvider){
        headers.add(header);
        valueProviders.add(valueProvider);
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public void write(OutputStream outputStream) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet worksheet = workbook.createSheet();

        XSSFCellStyle headerStyle = workbook.createCellStyle();
        XSSFFont headerFont = workbook.createFont();
        headerFont.setFontName("Arial");
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        XSSFRow headerRow = worksheet.createRow(0);
        for(int i = 0; i < headers.size(); i++){
            XSSFCell cell = headerRow.createCell(i);
            cell.setCellValue(headers.get(i));
            cell.setCellStyle(headerStyle);
        }

        for(int i = 0; i < items.size(); i++){
            /* +1 because of header */
            XSSFRow row = worksheet.createRow(i + 1);            
            for(int j = 0; j < valueProviders.size(); j++){
                XSSFCell cell = row.createCell(j);

                String value = valueProviders.get(j).apply(items.get(i));
                cell.setCellValue(value);
            }
        }

        for(int i = 0; i < headers.size(); i++){
            worksheet.autoSizeColumn(i);
        }

        workbook.write(outputStream);
        workbook.close();
    }
}