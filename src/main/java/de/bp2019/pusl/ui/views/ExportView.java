package de.bp2019.pusl.ui.views;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
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
import com.vaadin.flow.server.StreamResourceWriter;

import de.bp2019.pusl.config.PuslProperties;
import de.bp2019.pusl.model.Lecture;
import de.bp2019.pusl.model.Performance;
import de.bp2019.pusl.model.PerformanceScheme;
import de.bp2019.pusl.model.TUCanEntity;
import de.bp2019.pusl.service.CalculationService;
import de.bp2019.pusl.service.LectureService;
import de.bp2019.pusl.ui.dialogs.ErrorDialog;
import de.bp2019.pusl.ui.interfaces.AccessibleByWimi;
import de.bp2019.pusl.util.IOUtil;
import de.bp2019.pusl.util.Service;

/**
 * View to calculate and list the {@link Performance}s and export them as a
 * Excelsheet;
 *
 * @author Leon Chemnitz, Luca Dinies
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

    private Grid<Performance> grid;

    private Anchor download;
    private Button downloadButton;

    private Select<String> extensionSelect;

    public ExportView() {
        super("Export");

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
        lectureSelect.setId("lecture");
        selectLayout.add(lectureSelect);

        performanceSchemeSelect = new Select<>();
        performanceSchemeSelect.setItemLabelGenerator(PerformanceScheme::getName);
        performanceSchemeSelect.setLabel("Berechnungsregel");
        performanceSchemeSelect.setEnabled(false);
        performanceSchemeSelect.setId("performanceScheme");
        selectLayout.add(performanceSchemeSelect);

        add(selectLayout);

        /* ######## Upload Component and Calculate Button ######## */

        Upload upload = new Upload(uploadBuffer);
        upload.setDropLabel(new Span("TUCan-Liste hier abelegen"));
        upload.setUploadButton(new Button("TUCan-Liste hochladen"));
        upload.setAcceptedFileTypes(IOUtil.ACCEPTED_TYPES);
        upload.setMaxFiles(1);
        upload.setWidth("96%");

        add(upload);

        /* ######## Grid for the calculated Performances ######## */

        grid = new Grid<>();
        grid.addThemeVariants(GridVariant.LUMO_COMPACT);
        grid.setHeight("20em");
        grid.setDataProvider(performanceDataProvider);
    
        grid.addColumn(Performance::getMatriculationNumber).setHeader("Matrikel-Nummer").setAutoWidth(true);        
        grid.addColumn(Performance::getGrade).setKey("performance").setHeader("");

        add(grid);

        FormLayout downloadLayout = new FormLayout();
        downloadLayout.setResponsiveSteps(new ResponsiveStep("2em", 4));

        download = new Anchor("", "");

        download.getElement().setAttribute("download", true);
        downloadButton = new Button("Download");

        downloadButton.setWidthFull();
        downloadButton.setEnabled(false);
        downloadButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
        download.add(downloadButton);

        download.setWidthFull();
        downloadLayout.add(download, 3);

        extensionSelect = new Select<>(IOUtil.ACCEPTED_TYPES);
        extensionSelect.setEmptySelectionAllowed(false);
        extensionSelect.setValue(IOUtil.ACCEPTED_TYPES[0]);
        downloadLayout.add(extensionSelect, 1);

        add(downloadLayout);
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

            refreshStreamResource();
        });

        extensionSelect.addValueChangeListener( e -> {
            refreshStreamResource();
        });

        upload.addSucceededListener(event -> {
            refreshGrid();
        });

        performanceSchemeSelect.addValueChangeListener(event -> {
            refreshStreamResource();
            refreshGrid();
        });

    }

    private void refreshStreamResource() {
        PerformanceScheme performanceScheme = performanceSchemeSelect.getValue();
        Lecture lecture = lectureSelect.getValue();
        String extension = extensionSelect.getValue();

        StreamResourceWriter resourceWriter;
        if (extension.equals(".csv")) {
            resourceWriter = IOUtil.createCSVResourceWriter(performanceList);
        } else {
            resourceWriter = IOUtil.createExcelResourceWriter(performanceList);
        }

        if (performanceScheme != null && lecture != null) {
            downloadButton.setEnabled(true);
            download.setEnabled(true);

            String fileName = lecture.getName().toLowerCase().replace(' ', '-') + "_"
                    + performanceSchemeSelect.getValue().getName().toLowerCase() + "_" + LocalDate.now();
            download.setHref(new StreamResource(fileName + extension, resourceWriter));
        } else {
            download.setEnabled(false);
            downloadButton.setEnabled(false);
        }
    }

    private void refreshGrid() {
        Lecture lecture = lectureSelect.getValue();
        PerformanceScheme performanceScheme = performanceSchemeSelect.getValue();

        if (performanceScheme != null) {
            grid.getColumnByKey("performance").setHeader(performanceScheme.getName());
        } else {
            grid.getColumnByKey("performance").setHeader("");
        }

        List<String> matrNumbers = new ArrayList<String>();
        try {
            if (uploadBuffer.getInputStream().available() > 0) {
                matrNumbers = IOUtil.readUpload(uploadBuffer.getInputStream(), uploadBuffer.getFileName()).stream()
                        .map(TUCanEntity::getMatrNumber).collect(Collectors.toList());
            }

            uploadBuffer.getInputStream().close();
        } catch (IOException e) {
            ErrorDialog.open("Fehler beim öffnen der Datei");
            LOGGER.error(e.toString());
        }

        matrNumbers = matrNumbers.stream().distinct().collect(Collectors.toList());

        LOGGER.debug("read matrNumbers " + matrNumbers.toString());

        if (lecture != null && performanceScheme != null) {
            performanceList.clear();
            performanceList.addAll(calculationService.calculatePerformances(matrNumbers, lecture, performanceScheme));
        } else {
            performanceList.clear();
            performanceList.addAll(

                    matrNumbers.stream().map(matr -> new Performance(matr, null, "")).collect(Collectors.toList()));
        }
        LOGGER.debug(performanceList.toString());
        performanceDataProvider.refreshAll();
    }
}