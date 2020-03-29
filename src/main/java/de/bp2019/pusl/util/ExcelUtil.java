package de.bp2019.pusl.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.bp2019.pusl.ui.dialogs.ErrorDialog;

/**
 * Utility functions for Excel
 * 
 * @author Luca Dinies, Leon Chemnitz
 */
public final class ExcelUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelUtil.class);

    private ExcelUtil() {
    }

    /**
     * Reads one column from an excel sheet and returns it as a list of strings
     * 
     * @param inputStream
     * @param column
     * @return
     * @author Leon Chemnitz, Luca Dinies
     */
    public static List<String> readColumnToList(InputStream inputStream, int column) {
        List<String> result = new ArrayList<String>();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet worksheet = workbook.getSheetAt(0);

            for (int i = 0; i < worksheet.getLastRowNum(); i++) {
                XSSFRow row = worksheet.getRow(i);

                if (row != null) {
                    XSSFCell cell = row.getCell(column);

                    switch (cell.getCellType()) {
                        case NUMERIC:
                            result.add(cell.getRawValue());
                            break;
                        case STRING:
                            result.add(cell.getStringCellValue());
                            break;
                        default:
                            break;
                    }
                }
            }

            workbook.close();
        } catch (IOException e) {
            ErrorDialog.open("Es gab einen Fehler beim Öffnen der Datei");
            LOGGER.error(e.toString());
        }
        return result;
    }
}