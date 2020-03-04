package de.bp2019.pusl.ui.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
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
import org.apache.poi.ss.usermodel.Font;
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
 * View to calculate and list the Final Grades and export them as a Excelsheet;
 *
 * @author Luca Dinies
 **/

@PageTitle(PuslProperties.NAME + " | Export")
@Route(value = ExportView.ROUTE, layout = MainAppView.class)
public class ExportView extends BaseView {

    private static final long serialVersionUID = 1L;
    public static final String ROUTE = "export";

    private ListDataProvider<FinalGrade> exportDataProvider;

    private Grade filter;

    XSSFWorkbook excelDownload = new XSSFWorkbook();
    XSSFSheet worksheet = excelDownload.createSheet();
    int rowNr = 0;
    int colNr = 0;
    XSSFRow row;
    XSSFCell cell;
    Font headerFont;
    Font font;

    Stream<FinalGrade> dataStream;
    List<String> keyList;


    @Autowired
    public ExportView(GradeService gradeService, LectureService lectureService) {
        super("Noten exportieren");
        LOGGER.debug("Started creation of ExportView");

        filter = new Grade();

        /* ######## Select Components ######## */
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

        add(selectLayout);

        /* ######## Upload Component and Calculate Button ######## */

        HorizontalLayout buttonLayout = new HorizontalLayout();

        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        Button uploadButton = new Button("Matrikelnummern hochladen");
        upload.setDropLabel(new Span("Excelliste hier abelegen"));
        upload.setUploadButton(uploadButton);
        upload.setAcceptedFileTypes(".xlsx");
        buttonLayout.add(upload);

        Button calculateButton = new Button("Berechnen");
        buttonLayout.setVerticalComponentAlignment((Alignment.CENTER), calculateButton);
        buttonLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        buttonLayout.add(calculateButton);

        add(buttonLayout);

        /* ######## Grid for the calculated FinalGrades ######## */

        List<FinalGrade> finalGradeList = new ArrayList<>();
        Grid<FinalGrade> exportGrid = new Grid<>();

        exportDataProvider = new ListDataProvider<>(finalGradeList);
        exportGrid.setDataProvider(exportDataProvider);

        exportGrid.addColumn(item -> item.getMatrikelNumber()).setHeader("Matrikel-Nummer").setAutoWidth(true).setKey("Matrikelnummern");
        exportGrid.addColumn(item -> item.getFinalGrade()).setHeader("Note").setAutoWidth(true).setKey("Noten");

        add(exportGrid);


        /* ######## Excel import  ######## */

        List<String> matrikelList = new ArrayList<String>();

        upload.addSucceededListener(event -> {
            try {
                matrikelList.clear();
                XSSFWorkbook workbook = new XSSFWorkbook(buffer.getInputStream());
                XSSFSheet worksheet = workbook.getSheetAt(0);

                for (int i = 0; i < worksheet.getPhysicalNumberOfRows(); i++) {
                    XSSFRow row = worksheet.getRow(i);
                    matrikelList.add(i, row.getCell(0).getRawValue());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        /* ######## Click Listeners  ######## */

        lectureSelect.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                if (event.getValue().getPerformanceSchemes() != null) {
                    List<PerformanceScheme> performanceSchemes = event.getValue().getPerformanceSchemes();
                    performanceSchemeSelect.setItems(performanceSchemes);
                    performanceSchemeSelect.setValue(performanceSchemes.get(0));
                    performanceSchemeSelect.setEnabled(true);
                }
            }
        });

        calculateButton.addClickListener(event -> {
            exportDataProvider.getItems().clear();
            filter.setLecture(lectureSelect.getValue());

            matrikelList.forEach(item -> {
                FinalGrade tmp = new FinalGrade();
                tmp.setLecture(lectureSelect.getValue());
                tmp.setPerformanceScheme(performanceSchemeSelect.getValue());
                filter.setMatrNumber(item);
                List<Grade> gradeList;
                gradeList = gradeService.getAll(filter);

                if (gradeList.size() == 0) {
                    tmp.setMatrikelNumber(item);
                    tmp.setFinalGrade("Keine Noten vorhanden");
                } else {
                    tmp.setMatrikelNumber(item);
                    Float grade = 0f;

                    for (int i = 0; i < gradeList.size(); i++) {
                        grade = Float.parseFloat(gradeList.get(i).getGrade().replace(",", ".")) + grade;
                    }

                    grade = grade / gradeList.size();
                    tmp.setFinalGrade(grade.toString().replace(".", ","));
                }
                finalGradeList.add(tmp);
            });

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

        /* ######## Excel Download Button  ######## */

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

    /* ######## Excel file creator  ######## */

    public byte[] ExcelExporter(Stream<FinalGrade> dataStream, List<String> keyList) throws IOException {
        resetContent();
        buildHeader(keyList);
        dataStream.forEach(item -> {
            buildRow(item);
        });

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            excelDownload.write(bos);
        } finally {
            bos.close();
        }
        byte[] bytes = bos.toByteArray();
        return bytes;
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
        headerFont = excelDownload.createFont();
        headerFont.setFontName("Arial");
        headerFont.setBold(true);
        font = excelDownload.createFont();
        font.setFontName("Arial");
    }

}
