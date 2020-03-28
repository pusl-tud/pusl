package de.bp2019.pusl.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.server.VaadinSession;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.bp2019.pusl.ui.dialogs.ErrorDialog;

/**
 * Used to create Excel Sheets. Has an API similar to vaadin Grid
 * 
 * @param <T>
 * @author Luca Dinies, Leon Chemnitz
 */
public class ExcelExporter<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelExporter.class);

    private List<ValueProvider<T, String>> valueProviders;
    private List<String> headers;

    private DataProvider<T, ?> dataProvider;

    public ExcelExporter() {
        removeAllColumns();
    }

    /**
     * Add Column to the sheet
     * 
     * @param header
     * @param valueProvider
     * @author Leon Chemnitz
     */
    public void addColumn(String header, ValueProvider<T, String> valueProvider) {
        headers.add(header);
        valueProviders.add(valueProvider);
    }

    public void removeAllColumns() {
        valueProviders = new ArrayList<ValueProvider<T, String>>();
        headers = new ArrayList<String>();
    }

    public void setDataProvider(DataProvider<T, ?> dataProvider) {
        this.dataProvider = dataProvider;
    }

    /**
     * Used to Write to a StreamResource
     * 
     * @param outputStream
     * @param vaadinSession
     * @author Leon Chemnitz
     */
    public void createResource(OutputStream outputStream, VaadinSession vaadinSession) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet worksheet = workbook.createSheet();

        XSSFCellStyle headerStyle = workbook.createCellStyle();
        XSSFFont headerFont = workbook.createFont();
        headerFont.setFontName("Arial");
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        XSSFRow headerRow = worksheet.createRow(0);
        for (int i = 0; i < headers.size(); i++) {
            XSSFCell cell = headerRow.createCell(i);
            cell.setCellValue(headers.get(i));
            cell.setCellStyle(headerStyle);
        }

        List<T> items = dataProvider.fetch(new Query<>()).collect(Collectors.toList());

        for (int i = 0; i < items.size(); i++) {
            /* +1 because of header */
            XSSFRow row = worksheet.createRow(i + 1);
            for (int j = 0; j < valueProviders.size(); j++) {
                XSSFCell cell = row.createCell(j);

                String value = valueProviders.get(j).apply(items.get(i));
                if(NumberUtils.isDigits(value)){
                    cell.setCellValue(Integer.parseInt(value));
                }else if(NumberUtils.isParsable(value)) { 
                    cell.setCellValue(Double.parseDouble(value));
                }else{
                    cell.setCellValue(value);
                }
            }
        }

        for (int i = 0; i < headers.size(); i++) {
            worksheet.autoSizeColumn(i);
        }

        try {
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            LOGGER.error(e.toString());
            ErrorDialog.open("Fehler beim Erstellen der Datei");
        }
    }

}