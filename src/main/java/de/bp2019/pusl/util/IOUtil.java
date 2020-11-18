package de.bp2019.pusl.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.server.StreamResourceWriter;
import com.vaadin.flow.server.VaadinSession;

import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import de.bp2019.pusl.model.Performance;
import de.bp2019.pusl.model.TUCanEntity;
import de.bp2019.pusl.service.dataproviders.FilteringGradeDataProvider;

/**
 * Utility functions for Excel
 * 
 * @author Luca Dinies, Leon Chemnitz
 */
public final class IOUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(IOUtil.class);

    public static final String[] ACCEPTED_TYPES = { ".csv", ".xlsx" };
    public static final int MAX_EXPORT_SIZE = 250;

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

    public static StreamResourceWriter createCSVResourceWriter(FilteringGradeDataProvider dataProvider) {
        return (OutputStream outputStream, VaadinSession vaadinSession) -> {
            getAuthentication(vaadinSession);

            List<TUCanEntity> entities = dataProvider.fetch(new Query<>()).map(TUCanEntity::fromGrade)
                    .collect(Collectors.toList());

            createCSVResource(checkEntities(entities), outputStream, vaadinSession);
        };
    }

    /**
     * Used to Write to a StreamResource
     *
     * @param outputStream  outputStream to write to
     * @param vaadinSession vaadin session containing authentication data
     * @author Leon Chemnitz
     */
    public static StreamResourceWriter createCSVResourceWriter(List<Performance> performances) {
        return (OutputStream outputStream, VaadinSession vaadinSession) -> {

            getAuthentication(vaadinSession);

            List<TUCanEntity> entities = performances.stream().map(TUCanEntity::fromPerformance)
                    .collect(Collectors.toList());

            createCSVResource(checkEntities(entities), outputStream, vaadinSession);
        };
    }

    private static void createCSVResource(List<TUCanEntity> entities, OutputStream outputStream,
            VaadinSession vaadinSession) throws IOException {

        ICsvBeanWriter beanWriter = null;
        try {
            beanWriter = new CsvBeanWriter(new OutputStreamWriter(outputStream),
                    CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE);

            final String[] header = TUCanEntity.getHeaders();
            final String[] mapping = TUCanEntity.getMapping();
            final CellProcessor[] processors = TUCanEntity.getCSVProcessors();

            beanWriter.writeHeader(header);

            for (final TUCanEntity entity : entities) {
                beanWriter.write(entity, mapping, processors);
            }

        } finally {
            if (beanWriter != null) {
                beanWriter.close();
            }
        }
    }

    /**
     * Used to Write to a StreamResource
     * 
     * @param outputStream  outputStream to write to
     * @param vaadinSession vaadin session containing authentication data
     * @author Leon Chemnitz
     */
    public static StreamResourceWriter createExcelResourceWriter(List<Performance> performances) {
        return (OutputStream outputStream, VaadinSession vaadinSession) -> {
            getAuthentication(vaadinSession);

            List<TUCanEntity> entities = performances.stream().map(TUCanEntity::fromPerformance)
                    .collect(Collectors.toList());

            ;

            createExcelResource(checkEntities(entities), outputStream, vaadinSession);
        };
    }

    public static StreamResourceWriter createExcelResourceWriter(FilteringGradeDataProvider dataProvider) {
        return (OutputStream outputStream, VaadinSession vaadinSession) -> {

            getAuthentication(vaadinSession);

            List<TUCanEntity> entities = dataProvider.fetch(new Query<>()).map(TUCanEntity::fromGrade)
                    .collect(Collectors.toList());

            createExcelResource(checkEntities(entities), outputStream, vaadinSession);
        };
    }

    private static void createExcelResource(List<TUCanEntity> entities, OutputStream outputStream,
            VaadinSession vaadinSession) throws IOException {

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet worksheet = workbook.createSheet();

        XSSFCellStyle headerStyle = workbook.createCellStyle();
        XSSFFont headerFont = workbook.createFont();
        headerFont.setFontName("Arial");
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        XSSFRow headerRow = worksheet.createRow(0);
        String[] headers = TUCanEntity.getHeaders();
        for (int i = 0; i < headers.length; i++) {
            XSSFCell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        for (int i = 0; i < entities.size(); i++) {
            /* +1 because of header */
            XSSFRow row = worksheet.createRow(i + 1);
            for (int j = 0; j < headers.length; j++) {
                XSSFCell cell = row.createCell(j);

                String value = "";

                try {
                    Field field = TUCanEntity.class.getField(TUCanEntity.getMapping()[j]);
                    Object rawValue;
                    rawValue = field.get(entities.get(i));

                    if (!(rawValue instanceof String) && rawValue != null) {
                        throw new IllegalStateException(
                                "Field " + TUCanEntity.getMapping()[j] + " is not of type String");
                    }

                    value = (String) rawValue;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                cell.setCellValue((String) value);
            }
        }

        for (int i = 0; i < headers.length; i++) {
            worksheet.autoSizeColumn(i);
        }

        workbook.write(outputStream);
        workbook.close();
    }

    private static void getAuthentication(VaadinSession vaadinSession) {
        if (vaadinSession != null) {
            vaadinSession.lock();
            Authentication authentication = vaadinSession.getAttribute(Authentication.class);
            LOGGER.debug("Setting authentication " + authentication.toString());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            vaadinSession.unlock();
        }
    }

    private static List<TUCanEntity> checkEntities(List<TUCanEntity> entities) {
        if (entities.size() >= MAX_EXPORT_SIZE) {
            return entities.subList(0, MAX_EXPORT_SIZE - 1);
        } else{
            return entities;
        }
    }
}
