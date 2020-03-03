package de.bp2019.pusl.ui.views;


import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.bp2019.pusl.config.PuslProperties;
import de.bp2019.pusl.model.FinalGrade;
import de.bp2019.pusl.model.Grade;
import de.bp2019.pusl.model.Lecture;
import de.bp2019.pusl.model.PerformanceScheme;
import de.bp2019.pusl.service.GradeService;
import de.bp2019.pusl.service.LectureService;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.vaadin.firitin.components.DynamicFileDownloader;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * View that displays all Grades and contains a form to add New Grades
 *
 * @author Luca Dinies
 **/

@PageTitle(PuslProperties.NAME + " | Export")
@Route(value = ExportView.ROUTE, layout = MainAppView.class)
public class ExportView extends BaseView {

    private static final long serialVersionUID = 1L;
    public static final String ROUTE = "export";

    private ListDataProvider<FinalGrade> exportDataProvider;

    private GradeService gradeService;
    private LectureService lectureService;
    private Grade filter;


    File file;
    XSSFWorkbook excelDownload = new XSSFWorkbook();
    XSSFSheet worksheet = excelDownload.createSheet();
    int rowNr = 0;
    int colNr = 0;
    XSSFRow row;
    XSSFCell cell;
    OutputStream fileOut;

    Stream<FinalGrade> dataStream;
    List<String> keyList;


    @Autowired
    public ExportView(GradeService gradeService, LectureService lectureService) throws IOException {
        super("Noten exportieren");
        LOGGER.debug("Started creation of ExportView");

        this.gradeService = gradeService;
        this.lectureService = lectureService;

        filter = new Grade();

        FormLayout selectLayout = new FormLayout();

        Select<Lecture> lectureSelect = new Select<>();
        List<Lecture> allLectures = lectureService.getAll();
        lectureSelect.setItemLabelGenerator(Lecture::getName);
        lectureSelect.setItems(allLectures);
        lectureSelect.setLabel("Modul");
        selectLayout.add(lectureSelect);

        Select<PerformanceScheme> performanceSchemeSelect = new Select<>();
        performanceSchemeSelect.setItemLabelGenerator(PerformanceScheme::getName);
        performanceSchemeSelect.setLabel("Berechnungsregel");
        performanceSchemeSelect.setEnabled(false);
        selectLayout.add(performanceSchemeSelect);

        lectureSelect.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                if (event.getValue().getPerformanceSchemes().isEmpty()) {
                    PerformanceScheme emptyPerformanceScheme = new PerformanceScheme("Keine Berechnungsregel angelegt", null);
                    performanceSchemeSelect.setValue(emptyPerformanceScheme);

                } else {
                    List<PerformanceScheme> performanceSchemes = event.getValue().getPerformanceSchemes();
                    performanceSchemeSelect.setItems(performanceSchemes);
                    performanceSchemeSelect.setValue(performanceSchemes.get(0));
                    performanceSchemeSelect.setEnabled(true);
                    filter.setLecture(event.getValue());
                }
            }
        });

        HorizontalLayout buttonLayout = new HorizontalLayout();

        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        Button uploadButton = new Button("Matrikelnummern hochladen");
        upload.setUploadButton(uploadButton);
        List<String> matrikelList = new ArrayList<String>();

        upload.addSucceededListener(event -> {
            try {
                XSSFWorkbook workbook = new XSSFWorkbook(buffer.getInputStream());
                XSSFSheet worksheet = workbook.getSheetAt(0);

                for (int i = 0; i < worksheet.getPhysicalNumberOfRows(); i++) {
                    XSSFRow row = worksheet.getRow(i);
                    matrikelList.add(i, row.getCell(0).getStringCellValue());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Button calculateButton = new Button("Berechnen");

        buttonLayout.add(upload);
        buttonLayout.add(calculateButton);

        add(selectLayout);
        add(buttonLayout);

        List<FinalGrade> finalGradeList = new ArrayList<>();
        Grid<FinalGrade> exportGrid = new Grid<>();

        exportDataProvider = new ListDataProvider<FinalGrade>(finalGradeList);
        exportGrid.setDataProvider(exportDataProvider);

        exportGrid.addColumn(item -> item.getMatrikelNumber()).setHeader("Matrikel-Nummer").setAutoWidth(true).setKey("matrikel");
        exportGrid.addColumn(item -> item.getFinalGrade()).setHeader("Note").setAutoWidth(true).setKey("grade");

        add(exportGrid);

        calculateButton.addClickListener(event -> {
            exportDataProvider.getItems().clear();

            finalGradeList.add(new FinalGrade("123456789", lectureSelect.getValue(), performanceSchemeSelect.getValue(), "1,3"));
            finalGradeList.add(new FinalGrade("542236654", lectureSelect.getValue(), performanceSchemeSelect.getValue(), "1,6"));
            finalGradeList.add(new FinalGrade("896523522", lectureSelect.getValue(), performanceSchemeSelect.getValue(), "3,7"));
            finalGradeList.add(new FinalGrade("523654855", lectureSelect.getValue(), performanceSchemeSelect.getValue(), "3,3"));

            exportDataProvider.refreshAll();

            List<Grid.Column<FinalGrade>> columns = exportGrid.getColumns().stream().filter(item -> item.getKey() != null && item.isVisible()).collect(Collectors.toList());
            Query streamQuery = new Query(0, exportGrid.getDataProvider().size(new Query<>()),
                    exportGrid.getDataCommunicator().getBackEndSorting(), exportGrid.getDataCommunicator().getInMemorySorting(), null);
            dataStream = exportGrid.getDataProvider().fetch(streamQuery);

            keyList = new ArrayList<>();

            columns.forEach(item -> {
                keyList.add(item.getKey());
            });


        });

        DynamicFileDownloader downloadButton = new DynamicFileDownloader("Download Excel", "endnoten.xlsx",
                outputStream -> {
                    try {
                        outputStream.write(ExcelExporter(dataStream, keyList));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

        add(downloadButton);
    }

    public byte[] ExcelExporter(Stream<FinalGrade> dataStream, List<String> keyList) throws IOException {
        resetContent();
        initTempFile();
        buildHeader(keyList);
        dataStream.forEach(item -> {
            buildRow(item);
        });
        fileOut = new FileOutputStream("endnote.xlsx");
        try {
            excelDownload.write(fileOut);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            excelDownload.write(bos);
        } finally {
            bos.close();
        }
        byte[] bytes = bos.toByteArray();
        return bytes;
    }

    public void initTempFile() throws IOException {
        if (file == null || file.delete()) {
            file = File.createTempFile("tmp", ".xlsx");
        }
    }

    public void onNewRow() {
        row = worksheet.createRow(rowNr);
        rowNr++;
        colNr = 0;
    }

    public void onNewCell() {
        cell = row.createCell(colNr);
        colNr++;
    }

    public void buildHeader(List<String> keylist) {
        onNewRow();
        onNewCell();
        keylist.forEach(item -> {
            cell.setCellValue(item);
            onNewCell();
        });
    }

    public void buildRow(FinalGrade finalGrade) {
        onNewRow();
        onNewCell();
        cell.setCellValue(finalGrade.getMatrikelNumber());
        onNewCell();
        cell.setCellValue(finalGrade.getFinalGrade());
    }

    public void resetContent() {
        excelDownload = new XSSFWorkbook();
        worksheet = excelDownload.createSheet();
        colNr = 0;
        rowNr = 0;
        row = null;
        cell = null;
    }
}
