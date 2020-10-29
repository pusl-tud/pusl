package de.bp2019.pusl.ui.views;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import de.bp2019.pusl.config.PuslProperties;
import de.bp2019.pusl.model.Exercise;
import de.bp2019.pusl.model.Grade;
import de.bp2019.pusl.model.GradeFilter;
import de.bp2019.pusl.model.Lecture;
import de.bp2019.pusl.service.AuthenticationService;
import de.bp2019.pusl.service.GradeService;
import de.bp2019.pusl.service.LectureService;
import de.bp2019.pusl.service.dataproviders.FilteringGradeDataProvider;
import de.bp2019.pusl.ui.components.GradeComposer;
import de.bp2019.pusl.ui.components.tabs.VerticalTabs;
import de.bp2019.pusl.ui.dialogs.EditGradeDialog;
import de.bp2019.pusl.ui.dialogs.ErrorDialog;
import de.bp2019.pusl.ui.dialogs.SuccessDialog;
import de.bp2019.pusl.util.ExcelExporter;
import de.bp2019.pusl.util.Service;
import de.bp2019.pusl.util.Utils;
import de.bp2019.pusl.util.exceptions.DataNotFoundException;
import de.bp2019.pusl.util.exceptions.UnauthorizedException;

/**
 * View that displays all Grades and contains a form to add New Grades
 *
 * @author Luca Dinies, Leon Chemnitz
 **/

@PageTitle(PuslProperties.NAME + " | Einzelleistungen")
@Route(value = WorkView.ROUTE, layout = MainAppView.class)
public class WorkView extends BaseView implements HasUrlParameter<String> {

    private static final long serialVersionUID = 1L;

    public static final String ROUTE = "";

    private FilteringGradeDataProvider filteringGradeDataProvider;
    private LectureService lectureService;
    private AuthenticationService authenticationService;
    private GradeService gradeService;

    private Map<String, List<String>> parametersMap;

    private Grid<Grade> grid;

    private GradeFilter filter;

    public WorkView() {
        super("Einzelleistungen");

        this.filteringGradeDataProvider = Service.get(FilteringGradeDataProvider.class);
        this.lectureService = Service.get(LectureService.class);
        this.authenticationService = Service.get(AuthenticationService.class);
        this.gradeService = Service.get(GradeService.class);

        filter = new GradeFilter();

        GradeComposer gradeComposer = new GradeComposer();
        gradeComposer.setId("work-view-gc");
        gradeComposer.setWidthFull();
        add(gradeComposer);

        VerticalTabs<Component> verticalTabs = new VerticalTabs<>();
        verticalTabs.setId("vtabs");
        verticalTabs.setHeight("30em");
        verticalTabs.setWidthFull();
        add(verticalTabs);

        /* ########### create Grade ########### */

        FormLayout createGrade = new FormLayout();
        createGrade.setResponsiveSteps(new ResponsiveStep("5em", 1), new ResponsiveStep("5em", 2));
        createGrade.setWidth("100%");
        createGrade.getStyle().set("marginLeft", "1em");
        createGrade.getStyle().set("marginTop", "-0.5em");

        DatePicker handInDatePicker = new DatePicker();
        handInDatePicker.setLabel("Abgabe-Datum");
        handInDatePicker.setValue(LocalDate.now());
        createGrade.add(handInDatePicker, 1);

        Button createGradeButton = new Button();
        createGradeButton.setText("Einzelleistung eintragen");
        createGradeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        createGrade.add(createGradeButton, 1);

        verticalTabs.addTab("Eintragen", createGrade);

        /* ########### show Grades ########### */

        VerticalLayout showGrades = new VerticalLayout();

        FormLayout showGradesHeader = new FormLayout();
        showGradesHeader.setResponsiveSteps(new ResponsiveStep("5em", 1), new ResponsiveStep("5em", 2),
                new ResponsiveStep("5em", 3));
        showGradesHeader.setWidthFull();

        DatePicker startDateFilter = new DatePicker();
        startDateFilter.setLabel("Start");
        startDateFilter.setClearButtonVisible(true);
        startDateFilter.getElement().setAttribute("theme", "small");
        showGradesHeader.add(startDateFilter, 1);

        DatePicker endDateFilter = new DatePicker();
        endDateFilter.setClearButtonVisible(true);
        endDateFilter.setLabel("End");
        endDateFilter.getElement().setAttribute("theme", "small");
        showGradesHeader.add(endDateFilter, 1);

        ExcelExporter<Grade> excelExporter = createExcelExporter();
        VaadinSession.getCurrent().setAttribute(Authentication.class, SecurityContextHolder.getContext().getAuthentication());
        Anchor download = new Anchor(new StreamResource("noten.xlsx", (stream, session) -> {
            excelExporter.createResource(stream,session);
        }), "");
        download.getElement().setAttribute("download", true);
        Button downloadButton = new Button("Download Excel");
        downloadButton.setWidthFull();
        downloadButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
        download.add(downloadButton);
        download.setWidthFull();

        showGradesHeader.add(download, 1);
        showGrades.add(showGradesHeader);

        grid = new Grid<>(Grade.class);
        grid.removeAllColumns();

        grid.addThemeVariants(GridVariant.LUMO_COMPACT);
        grid.setWidthFull();
        grid.setHeightFull();
        grid.setDataProvider(filteringGradeDataProvider);

        grid.addColumn(Grade::getMatrNumber).setKey("matrNumber").setHeader("Matr. Nr.").setAutoWidth(true);
        grid.addColumn(item -> item.getLecture().getName()).setKey("lecture").setHeader("Veranstaltung")
                .setAutoWidth(true);
        grid.addColumn(item -> item.getExercise().getName()).setKey("exercise").setHeader("Leistung").setAutoWidth(true);
        grid.addColumn(item -> {
                if( item.getHandIn() != null){
                    return item.getHandIn().format(DateTimeFormatter.ofPattern("dd. MM. uuuu"));
                } else{
                    return "";
                }
            }).setKey("handIn")
                .setHeader("Abgabedatum").setAutoWidth(true);
        grid.addColumn(Grade::getValue).setHeader("Note").setAutoWidth(true);
        grid.setSortableColumns("matrNumber", "handIn");

        grid.addItemClickListener(event -> {
            if (event.getClickCount() == 2) {
                EditGradeDialog.open(event.getItem(), () -> filteringGradeDataProvider.refreshAll());
            }
        });

        showGrades.add(grid);

        verticalTabs.addTab("Einsehen", showGrades);

        /* ############## CHANGE LISTENERS ############# */

        createGradeButton.addClickListener(event -> {
            Grade grade = new Grade();

            grade.setMatrNumber(filter.getMatrNumber());
            grade.setLecture(filter.getLecture());
            grade.setExercise(filter.getExercise());
            grade.setValue(filter.getGrade());
            grade.setHandIn(handInDatePicker.getValue());

            grade.setGradedBy(authenticationService.currentUser());
            grade.setLastModified(LocalDateTime.now());

            if (!GradeService.gradeIsValid(grade)) {
                return;
            }

            try {
                gradeService.save(grade);
                filteringGradeDataProvider.refreshAll();
                SuccessDialog.open("Einzelleistung erfolgreich gespeichert");
            } catch (UnauthorizedException e) {
                LOGGER.info("current User unauthorized to save Grade");
                ErrorDialog.open("nicht authoriziert um Einzelleistung zu speichern");
            }
        });

        gradeComposer.addValueChangeListener(event -> {
            LOGGER.debug("GradeComposer value changed");
            GradeFilter newValue = event.getValue();

            filter.setMatrNumber(newValue.getMatrNumber());
            filter.setLecture(newValue.getLecture());
            filter.setExercise(newValue.getExercise());
            filter.setGrade(newValue.getGrade());

            filteringGradeDataProvider.setFilter(filter);
            filteringGradeDataProvider.refreshAll();

            if (filter.getLecture() != null) {
                grid.getColumnByKey("lecture").setVisible(false);
            } else {
                grid.getColumnByKey("lecture").setVisible(true);
            }

            if (filter.getExercise() != null) {
                grid.getColumnByKey("exercise").setVisible(false);
            } else {
                grid.getColumnByKey("exercise").setVisible(true);
            }
        });

        startDateFilter.addValueChangeListener(event -> {
            LocalDate startDate = event.getValue();

            LocalDate endDate = endDateFilter.getValue();
            if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
                endDateFilter.setValue(startDate);
            }

            filter.setStartDate(Utils.localDateToDate(startDate));

            filteringGradeDataProvider.setFilter(filter);
            filteringGradeDataProvider.refreshAll();
        });

        endDateFilter.addValueChangeListener(event -> {
            LocalDate endDate = event.getValue();

            LocalDate startDate = endDateFilter.getValue();
            if (endDate != null && startDate != null && startDate.isAfter(endDate)) {
                startDateFilter.setValue(endDate);
            }

            filter.setEndDate(Utils.localDateToDate(endDate));

            filteringGradeDataProvider.setFilter(filter);
            filteringGradeDataProvider.refreshAll();
        });

    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        Location location = event.getLocation();
        QueryParameters queryParameters = location.getQueryParameters();

        parametersMap = queryParameters.getParameters();

        try {
            if (parametersMap.get("lecture") != null) {
                String lectureId = parametersMap.get("lecture").get(0);
                Lecture parameterLecture = lectureService.getById(lectureId);

                filter.setLecture(parameterLecture);

                if (parametersMap.get("exercise") != null) {
                    String parameterExerciseName = parametersMap.get("exercise").get(0);
                    Optional<Exercise> parameterExercise = parameterLecture.getExercises().stream()
                            .filter(exercise -> exercise.getName().equals(parameterExerciseName)).findFirst();

                    if (parameterExercise.isPresent()) {
                        filter.setExercise(parameterExercise.get());
                    }
                }

                filteringGradeDataProvider.setFilter(filter);
                filteringGradeDataProvider.refreshAll();
            }
        } catch (DataNotFoundException e) {
            filter.setLecture(null);
            filter.setExercise(null);

            filteringGradeDataProvider.setFilter(filter);
            filteringGradeDataProvider.refreshAll();
        } catch (UnauthorizedException e) {
            UI.getCurrent().navigate(PuslProperties.ROOT_ROUTE);
            ErrorDialog.open("Nicht authorisiert um Einzelleistung zu bearbeiten!");
        }

        if (parametersMap.get("martrNumber") != null) {
            String parameterMartrikelNumber = parametersMap.get("martrNumber").get(0);
            filter.setMatrNumber(parameterMartrikelNumber);
        }
    }

    private ExcelExporter<Grade> createExcelExporter() {
        ExcelExporter<Grade> excelExporter = new ExcelExporter<>();

        excelExporter.setDataProvider(filteringGradeDataProvider);
        excelExporter.addColumn("Matr.Nummer", Grade::getMatrNumber);
        excelExporter.addColumn("Bewertung", Grade::getValue);
        excelExporter.addColumn("Veranstaltung", grade -> grade.getLecture().getName());
        excelExporter.addColumn("Leistung", grade -> grade.getExercise().getName());
        excelExporter.addColumn("Eingetragen von", Grade::getNameOfGradedBy);
        excelExporter.addColumn("Abgegeben am",
                grade -> grade.getHandIn().format(DateTimeFormatter.ofPattern("dd. MM. uuuu")));
        excelExporter.addColumn("Zuletzt verändert", Grade::getLastModifiedFormatted);

        return excelExporter;
    }
}