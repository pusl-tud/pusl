package de.bp2019.pusl.ui.views;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import de.bp2019.pusl.config.PuslProperties;
import de.bp2019.pusl.model.Lecture;
import de.bp2019.pusl.model.Performance;
import de.bp2019.pusl.model.PerformanceScheme;
import de.bp2019.pusl.service.CalculationService;
import de.bp2019.pusl.service.LectureService;
import de.bp2019.pusl.ui.dialogs.ErrorDialog;
import de.bp2019.pusl.ui.interfaces.AccessibleByWimi;
import de.bp2019.pusl.util.ExcelExporter;
import de.bp2019.pusl.util.ExcelUtil;
import de.bp2019.pusl.util.Service;
import de.bp2019.pusl.util.Utils;

/**
 * View to calculate and list the {@link Performance}s and export them as a
 * Excelsheet;
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
    private LectureService lectureService;

    private MemoryBuffer uploadBuffer;

    private Select<Lecture> lectureSelect;
    private Select<PerformanceScheme> performanceSchemeSelect;

    private ExcelExporter<Performance> exporter;
    private Grid<Performance> grid;

    public ExportView() {
        super("Noten exportieren");

        this.calculationService = Service.get(CalculationService.class);
        this.lectureService = Service.get(LectureService.class);

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

        Upload upload = new Upload(uploadBuffer);        
        upload.setDropLabel(new Span("Excelliste hier abelegen"));
        upload.setUploadButton(new Button("Matrikelnummern hochladen"));
        upload.setAcceptedFileTypes(".xlsx");
        upload.setWidth("96%");

        add(upload);

        /* ######## Grid for the calculated Performances ######## */

        grid = new Grid<>();
        grid.addThemeVariants(GridVariant.LUMO_COMPACT);
        grid.setDataProvider(performanceDataProvider);

        grid.addColumn(Performance::getMatriculationNumber).setHeader("Matrikel-Nummer").setAutoWidth(true);        
        grid.addColumn(Performance::getGrade).setKey("performance").setHeader("");

        add(grid);

        exporter = new ExcelExporter<Performance>();
        exporter.setDataProvider(performanceDataProvider);
        exporter.addColumn("Matr.Nummer", Performance::getMatriculationNumber);

        Anchor download = new Anchor(
                new StreamResource("leistungen.xlsx", (stream, session) -> exporter.createResource(stream, session)),
                "");
        download.getElement().setAttribute("download", true);
        Button downloadButton = new Button("Download Excel");
        downloadButton.setWidthFull();
        downloadButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
        download.add(downloadButton);
        download.setWidthFull();

        add(download);

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

    }

    private void refreshGrid() {
        Lecture lecture = lectureSelect.getValue();
        PerformanceScheme performanceScheme = performanceSchemeSelect.getValue();

        exporter.removeAllColumns();

        exporter.addColumn("Matr.Nummer", Performance::getMatriculationNumber);

        if(performanceScheme != null){
            grid.getColumnByKey("performance").setHeader(performanceScheme.getName());
            exporter.addColumn(performanceScheme.getName(), Performance::getGrade);
        }else{
            grid.getColumnByKey("performance").setHeader("");
        }


        List<String> matrNumbers = new ArrayList<String>();
        try {
            if (uploadBuffer.getInputStream().available() > 0) {
                matrNumbers = ExcelUtil.readColumnToList(uploadBuffer.getInputStream(), 0);
            }

            uploadBuffer.getInputStream().close();
        } catch (IOException e) {
            ErrorDialog.open("Fehler beim öffnen der Datei");
            LOGGER.error(e.toString());
        }

        matrNumbers = matrNumbers.stream().distinct().map(m -> {
            String subStrings[] = m.split("\\.");
            if(subStrings.length == 2){
                return subStrings[0];
            }else{
                return "";
            }
        }).filter(Utils::isMatrNumber).collect(Collectors.toList());

        LOGGER.debug("read matrNumbers " + matrNumbers.toString());

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