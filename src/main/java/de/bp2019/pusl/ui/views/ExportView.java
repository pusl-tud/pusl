package de.bp2019.pusl.ui.views;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.firitin.components.DynamicFileDownloader;

import de.bp2019.pusl.config.PuslProperties;
import de.bp2019.pusl.model.Lecture;
import de.bp2019.pusl.model.Performance;
import de.bp2019.pusl.model.PerformanceScheme;
import de.bp2019.pusl.service.CalculationService;
import de.bp2019.pusl.service.LectureService;
import de.bp2019.pusl.ui.interfaces.AccessibleByWimi;
import de.bp2019.pusl.util.ExcelExporter;
import de.bp2019.pusl.util.ExcelUtil;

/**
 * View to calculate and list the Final Grades and export them as a Excelsheet;
 *
 * @author Luca Dinies
 **/

@PageTitle(PuslProperties.NAME + " | Export")
@Route(value = ExportView.ROUTE, layout = MainAppView.class)
public class ExportView extends BaseView implements AccessibleByWimi {

    private static final long serialVersionUID = 1L;
    public static final String ROUTE = "export";

    private List<Performance> performanceList;
    private ListDataProvider<Performance> performanceDataProvider;

    private CalculationService calculationService;

    private MemoryBuffer uploadBuffer;

    private Select<Lecture> lectureSelect;
    private Select<PerformanceScheme> performanceSchemeSelect;

    @Autowired
    public ExportView(LectureService lectureService, CalculationService calculationService) {
        super("Noten exportieren");

        this.calculationService = calculationService;

        performanceList = new ArrayList<>();
        performanceDataProvider = new ListDataProvider<>(performanceList);

        uploadBuffer = new MemoryBuffer();

        /* ######## Select Components ######## */
        FormLayout selectLayout = new FormLayout();

        lectureSelect = new Select<>();
        lectureSelect.setItemLabelGenerator(Lecture::getName);
        lectureSelect.setDataProvider(lectureService);
        lectureSelect.setLabel("Veranstaltung");
        selectLayout.add(lectureSelect);

        performanceSchemeSelect = new Select<>();
        performanceSchemeSelect.setItemLabelGenerator(PerformanceScheme::getName);
        performanceSchemeSelect.setLabel("Berechnungsregel");
        performanceSchemeSelect.setEnabled(false);
        selectLayout.add(performanceSchemeSelect);

        add(selectLayout);

        /* ######## Upload Component and Calculate Button ######## */

        HorizontalLayout buttonLayout = new HorizontalLayout();

        Upload upload = new Upload(uploadBuffer);
        upload.setDropLabel(new Span("Excelliste hier abelegen"));
        upload.setUploadButton(new Button("Matrikelnummern hochladen"));
        upload.setAcceptedFileTypes(".xlsx");
        buttonLayout.add(upload);

        Button calculateButton = new Button("Berechnen");
        buttonLayout.setVerticalComponentAlignment((Alignment.CENTER), calculateButton);
        buttonLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        buttonLayout.add(calculateButton);

        add(buttonLayout);

        /* ######## Grid for the calculated Performances ######## */

        Grid<Performance> performanceGrid = new Grid<>();
        performanceGrid.setDataProvider(performanceDataProvider);

        performanceGrid.addColumn(Performance::getMatriculationNumber).setHeader("Matrikel-Nummer").setAutoWidth(true);
        performanceGrid.addColumn(Performance::getGrade).setHeader("Note").setAutoWidth(true);

        add(performanceGrid);

        /* ######## Listeners ######## */

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

        upload.addSucceededListener(event -> {
            refreshGrid();
        });

        performanceSchemeSelect.addValueChangeListener(event -> {
            refreshGrid();
        });

        /* ######## Excel Download Button ######## */

        DynamicFileDownloader downloadButton = new DynamicFileDownloader("Download Excel", "endnoten.xlsx",
                outputStream -> {
                    try {
                        ExcelExporter<Performance> exporter = new ExcelExporter<Performance>();
                        exporter.setItems(performanceList);
                        exporter.addColumn("Matr.Nummer", Performance::getMatriculationNumber);
                        String performanceName = performanceSchemeSelect.getValue().getName();
                        exporter.addColumn(performanceName, Performance::getGrade);
                        exporter.write(outputStream);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

        add(downloadButton);
    }

    private void refreshGrid() {
        List<String> matrNumbers = new ArrayList<String>();
        try {
            if (uploadBuffer.getInputStream().available() > 0) {
                matrNumbers = ExcelUtil.readColumnToList(uploadBuffer.getInputStream(), 0);
            }

            uploadBuffer.getInputStream().close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Lecture lecture = lectureSelect.getValue();
        PerformanceScheme performanceScheme = performanceSchemeSelect.getValue();

        if (lecture != null && performanceScheme != null) {
            performanceList.clear();
            performanceList.addAll(calculationService.calculatePerformances(matrNumbers, lecture, performanceScheme));
        } else {
            performanceList.clear();
            performanceList.addAll(
                    matrNumbers.stream().map(matr -> new Performance(matr, null, "")).collect(Collectors.toList()));
        }
        LOGGER.info(performanceList.toString());
        performanceDataProvider.refreshAll();
    }
}