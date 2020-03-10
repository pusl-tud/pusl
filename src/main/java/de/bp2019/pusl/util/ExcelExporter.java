package de.bp2019.pusl.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.function.ValueProvider;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelExporter<T> {

    private List<ValueProvider<T, String>> valueProviders;
    private List<T> items;

    public ExcelExporter(){
        valueProviders = new ArrayList<ValueProvider<T, String>>();
        items = new ArrayList<T>();
    }
    
    public void addColumn(ValueProvider<T, String> valueProvider){
        valueProviders.add(valueProvider);
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public void write(OutputStream outputStream) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet worksheet = workbook.createSheet();

        for(int i = 0; i < items.size(); i++){
            XSSFRow row = worksheet.createRow(i);            
            for(int j = 0; j < valueProviders.size(); j++){
                XSSFCell cell = row.createCell(j);

                String value = valueProviders.get(j).apply(items.get(i));
                cell.setCellValue(value);
            }
        }

        workbook.write(outputStream);
        workbook.close();
    }

    // public void onNewRow() {
    //     row = worksheet.createRow(rowNr);
    //     rowNr++;
    //     colNr = 0;
    // }

    // public void onNewCell() {
    //     cell = row.createCell(colNr);
    //     colNr++;
    // }

    // public void buildHeader(List<String> keylist) {
    //     onNewRow();
    //     onNewCell();
    //     keylist.forEach(item -> {
    //         switch (item) {
    //             case ("matrNum"):
    //                 cell.setCellValue("Matrikel-Nummer");
    //                 break;
    //             case ("grades"):
    //                 cell.setCellValue("Note");
    //                 break;
    //         }
    //         onNewCell();
    //     });
    // }

    // private void buildRow(FinalGrade finalGrade) {
    //     onNewRow();
    //     onNewCell();
    //     cell.setCellValue(finalGrade.getMatrikelNumber());
    //     onNewCell();
    //     cell.setCellValue(finalGrade.getFinalGrade());
    // }

    // private void resetContent() {
    //     workbook = new XSSFWorkbook();
    //     worksheet = workbook.createSheet();
    //     colNr = 0;
    //     rowNr = 0;
    //     row = null;
    //     cell = null;
    //     headerFont = workbook.createFont();
    //     headerFont.setFontName("Arial");
    //     headerFont.setBold(true);
    //     font = workbook.createFont();
    //     font.setFontName("Arial");
    // }

}