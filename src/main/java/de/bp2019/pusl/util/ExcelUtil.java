package de.bp2019.pusl.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.bp2019.pusl.ui.dialogs.ErrorDialog;

public final class ExcelUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelUtil.class);

    public static List<String> readColumnToList(InputStream inputStream, int column){
        List<String> result = new ArrayList<String>();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet worksheet = workbook.getSheetAt(0);

            for (int i = 0; i < worksheet.getLastRowNum(); i++) {
                XSSFRow row = worksheet.getRow(i);
                if(row != null){
                    result.add(row.getCell(column).getRawValue());
                }
            }
            
            workbook.close();
        } catch (IOException e) {
            LOGGER.error("There was an error opening excelfile");
            e.printStackTrace();
            ErrorDialog.open("Es gab einen Fehler beim öffnen der Datei");
        }
        return result;
    }
}