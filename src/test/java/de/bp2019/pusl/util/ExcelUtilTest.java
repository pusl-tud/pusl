package de.bp2019.pusl.util;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExcelUtilTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelUtilTest.class);

    @Test
    public void testReadColumnToList() throws Exception {
        LOGGER.info("testing read column");
        final int numRows = 20;

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet worksheet = workbook.createSheet();

        List<String> demoData = new ArrayList<>();

        for (int i = 0; i < numRows; i++) {
            String cellContent = RandomStringUtils.randomAlphabetic(0, 16);
            worksheet.createRow(i).createCell(0).setCellValue(cellContent);
            demoData.add(cellContent);
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        byte[] barray = bos.toByteArray();

        List<String> readColumn = ExcelUtil.readColumnToList(new ByteArrayInputStream(barray), 0);

        for (int i = 0; i < numRows; i++) {
            assertEquals(demoData.get(i), readColumn.get(i));
            LOGGER.info("row " + i + " okay");
        }

        workbook.close();
        LOGGER.info("test successful");
    }
}