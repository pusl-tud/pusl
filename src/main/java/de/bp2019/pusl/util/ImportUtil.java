package de.bp2019.pusl.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import de.bp2019.pusl.model.TUCanEntity;
import de.bp2019.pusl.ui.dialogs.ErrorDialog;

/**
 * Utility functions for Excel
 * 
 * @author Luca Dinies, Leon Chemnitz
 */
public final class ImportUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImportUtil.class);

    public static final String[] ACCEPTED_TYPES = { ".csv", ".xlsx" };

    public static List<TUCanEntity> readUpload(InputStream in, String fileName) throws IOException {

        if (fileName.endsWith(".csv")) {
            return readCSV(in);
        } else if (fileName.endsWith(".xlsx")) {
            return readXLSX(in);
        } else {
            throw new IOException("unknown filetype. FileName was: " + fileName);
        }

    }

    private static List<TUCanEntity> readXLSX(InputStream in) throws IOException {
        XSSFWorkbook workbook = null;
        try {
            workbook = new XSSFWorkbook(in);
            workbook.setMissingCellPolicy(MissingCellPolicy.CREATE_NULL_AS_BLANK);
            XSSFSheet worksheet = workbook.getSheetAt(0);

            List<TUCanEntity> tuCanEntities = new ArrayList<>();

            for (int i = 1; i <= worksheet.getLastRowNum(); i++) {
                XSSFRow row = worksheet.getRow(i);

                if (row != null) {
                    TUCanEntity tuCanEntity = new TUCanEntity();

                    tuCanEntity.setNumber(row.getCell(0).getRawValue());
                    tuCanEntity.setMatrNumber(row.getCell(1).getRawValue());
                    tuCanEntity.setFirstName(row.getCell(2).getStringCellValue());
                    tuCanEntity.setMiddleName(row.getCell(3).getStringCellValue());
                    tuCanEntity.setLastName(row.getCell(4).getStringCellValue());
                    tuCanEntity.setGrade(row.getCell(5).getStringCellValue());

                    tuCanEntities.add(tuCanEntity);
                }
            }

            return tuCanEntities;
        } finally {
            if (workbook != null) {
                workbook.close();
            }
        }
    }

    // /**
    //  * Reads one column from an excel sheet and returns it as a list of strings
    //  * 
    //  * @param inputStream Excel file as input stream
    //  * @param column      index of column to read
    //  * @return read cell entries
    //  * @author Leon Chemnitz, Luca Dinies
    //  */
    // public static List<String> readColumnToList(InputStream inputStream, int column) {
    //     List<String> result = new ArrayList<String>();
    //     try {
    //         XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
    //         XSSFSheet worksheet = workbook.getSheetAt(0);

    //         for (int i = 0; i <= worksheet.getLastRowNum(); i++) {
    //             XSSFRow row = worksheet.getRow(i);

    //             if (row != null) {
    //                 XSSFCell cell = row.getCell(column);

    //                 switch (cell.getCellType()) {
    //                     case NUMERIC:
    //                         result.add(cell.getRawValue());
    //                         break;
    //                     case STRING:
    //                         result.add(cell.getStringCellValue());
    //                         break;
    //                     default:
    //                         break;
    //                 }
    //             }
    //         }

    //         workbook.close();
    //     } catch (IOException e) {
    //         ErrorDialog.open("Es gab einen Fehler beim Öffnen der Datei");
    //         LOGGER.error(e.toString());
    //     }
    //     return result;
    // }

    /**
     * @author Leon Chemnitz
     */
    private static List<TUCanEntity> readCSV(InputStream in) throws IOException {
        ICsvBeanReader beanReader = null;
        try {
            beanReader = new CsvBeanReader(new InputStreamReader(in), CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE);

            beanReader.getHeader(true); // skip past the header (we're defining our own)

            List<TUCanEntity> tuCanEntities = new ArrayList<>();
            TUCanEntity tuCanEntity;
            while ((tuCanEntity = beanReader.read(TUCanEntity.class, TUCanEntity.getMapping(),
                    TUCanEntity.getCSVProcessors())) != null) {
                tuCanEntities.add(tuCanEntity);
            }
            return tuCanEntities;
        } finally {
            if (beanReader != null) {
                beanReader.close();
            }
        }
    }
}
