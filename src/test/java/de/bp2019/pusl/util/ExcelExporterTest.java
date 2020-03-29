package de.bp2019.pusl.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.data.provider.ListDataProvider;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExcelExporterTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelExporterTest.class);

    class DemoEntity{
        private String field1;
        private String field2;
        private String field3;

        public DemoEntity(String field1, String field2, String field3) {
            this.field1 = field1;
            this.field2 = field2;
            this.field3 = field3;
        }

        public String getField1() {
            return this.field1;
        }

        public void setField1(String field1) {
            this.field1 = field1;
        }

        public String getField2() {
            return this.field2;
        }

        public void setField2(String field2) {
            this.field2 = field2;
        }

        public String getField3() {
            return this.field3;
        }

        public void setField3(String field3) {
            this.field3 = field3;
        }
    }

    @Test
    public void testcreateResource() throws Exception {
        ExcelExporter<DemoEntity> excelExporter = new ExcelExporter<>();

        List<DemoEntity> items = new ArrayList<>();

        for(int i = 0; i < 100; i++){
            String randString1 = RandomStringUtils.randomAlphanumeric(1, 16);
            String randString2 = RandomStringUtils.randomAlphanumeric(1, 16);
            String randString3 = RandomStringUtils.randomAlphanumeric(1, 16);
            items.add(new DemoEntity(randString1, randString2, randString3));
        }

        ListDataProvider<DemoEntity> dataProvider = new ListDataProvider<>(items);

        String header1 = RandomStringUtils.randomAlphanumeric(1, 16);
        String header2 = RandomStringUtils.randomAlphanumeric(1, 16);
        String header3 = RandomStringUtils.randomAlphanumeric(1, 16);

        excelExporter.setDataProvider(dataProvider);
        excelExporter.addColumn(header1, DemoEntity::getField1);
        excelExporter.addColumn(header2, DemoEntity::getField2);
        excelExporter.addColumn(header3, DemoEntity::getField3);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        excelExporter.createResource(bos, null);

        byte[] barray = bos.toByteArray();
        ByteArrayInputStream bis = new ByteArrayInputStream(barray);
        XSSFWorkbook workbook = new XSSFWorkbook(bis);
        XSSFSheet worksheet = workbook.getSheetAt(0);

        LOGGER.info("checking Header");
        XSSFRow headerRow = worksheet.getRow(0);

        assertEquals(header1, headerRow.getCell(0).getRawValue());
        assertEquals(header2, headerRow.getCell(1).getRawValue());
        assertEquals(header3, headerRow.getCell(2).getRawValue());

        workbook.close();
    }
}