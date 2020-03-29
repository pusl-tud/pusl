package de.bp2019.pusl.util;

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
    public void testReadColumnToList() throws Exception{

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet worksheet = workbook.createSheet();

        List<String> demoData = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            String cellContent = RandomStringUtils.randomAlphanumeric(0, 16);
            worksheet.createRow(i).createCell(0).setCellValue(cellContent);
            demoData.add(cellContent);
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        byte[] barray = bos.toByteArray();

        List<String> readColumn = ExcelUtil.readColumnToList(new ByteArrayInputStream(barray), 0);
        LOGGER.info(readColumn.toString());
        LOGGER.info(demoData.toString());

        
        workbook.close();
    }
}