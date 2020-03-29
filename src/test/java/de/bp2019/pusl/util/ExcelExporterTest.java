package de.bp2019.pusl.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.vaadin.flow.data.provider.ListDataProvider;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Leon Chemnitz
 */
public class ExcelExporterTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelExporterTest.class);

    class DemoEntity {
        private String stringField;
        private Integer intField;

        public String getStringField() {
            return this.stringField;
        }

        public void setStringField(String stringField) {
            this.stringField = stringField;
        }

        public Integer getIntField() {
            return this.intField;
        }

        public void setIntField(Integer intField) {
            this.intField = intField;
        }

        public DemoEntity(String stringField, Integer intField) {
            this.stringField = stringField;
            this.intField = intField;
        };

    }

    /**
     * @author Leon Chemnitz
     * @throws Exception
     */
    @Test
    public void testCreateResource() throws Exception {
        LOGGER.info("Testing create Resource");

        final int numRows = 20;

        ExcelExporter<DemoEntity> excelExporter = new ExcelExporter<>();

        List<DemoEntity> items = new ArrayList<>();

        for (int i = 0; i < numRows; i++) {
            String randString = RandomStringUtils.randomAlphabetic(1, 8);
            Integer randInt = new Random().nextInt(1000);
            items.add(new DemoEntity(randString, randInt));
        }

        ListDataProvider<DemoEntity> dataProvider = new ListDataProvider<>(items);

        String header1 = RandomStringUtils.randomAlphanumeric(1, 16);
        String header2 = RandomStringUtils.randomAlphanumeric(1, 16);

        excelExporter.setDataProvider(dataProvider);
        excelExporter.addColumn(header1, DemoEntity::getStringField);
        excelExporter.addColumn(header2, item -> item.getIntField().toString());

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        excelExporter.createResource(bos, null);

        byte[] barray = bos.toByteArray();
        ByteArrayInputStream bis = new ByteArrayInputStream(barray);
        XSSFWorkbook workbook = new XSSFWorkbook(bis);
        XSSFSheet worksheet = workbook.getSheetAt(0);

        LOGGER.info("checking Header");
        XSSFRow headerRow = worksheet.getRow(0);

        assertEquals(header1, headerRow.getCell(0).getStringCellValue());
        LOGGER.info("header1 okay");
        assertEquals(header2, headerRow.getCell(1).getStringCellValue());
        LOGGER.info("header2 okay");

        LOGGER.info("checking rows");
        for (int i = 0; i < numRows; i++) {
            XSSFRow row = worksheet.getRow(i + 1);

            assertEquals(items.get(i).getStringField(), row.getCell(0).getStringCellValue());
            assertEquals(items.get(i).getIntField().doubleValue(), row.getCell(1).getNumericCellValue());

            LOGGER.info("row " + (i + 1) + " okay");
        }

        workbook.close();
        LOGGER.info("Test successful");
    }
}