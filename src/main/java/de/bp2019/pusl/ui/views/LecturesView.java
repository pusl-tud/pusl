package de.bp2019.pusl.ui.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;

import org.springframework.beans.factory.annotation.Autowired;

import de.bp2019.pusl.config.AppConfig;
import de.bp2019.pusl.model.Exercise;
import de.bp2019.pusl.model.Lecture;
import de.bp2019.pusl.service.LectureService;

/**
 * View that displays a Dashboard
 * 
 * @author Tomoki Tokuyama
 */
@PageTitle(AppConfig.NAME + " | Meine Veranstaltungen")
@Route(value = LecturesView.ROUTE, layout = MainAppView.class)
public class LecturesView extends BaseView {

    private static final long serialVersionUID = 1L;

    public static final String ROUTE = "";

    @Autowired
    public LecturesView(LectureService lectureService) {
        super("Meine Veranstaltungen");

        List<Lecture> lectures = new ArrayList<>();
        lectures.addAll(lectureService.getAll());

        for (Lecture l : lectures) {
            Button lectureButton = new Button(l.getName());
            lectureButton.addClickListener(event -> {
                var parameterMap = new HashMap<String, List<String>>();
                parameterMap.put("lecture", Arrays.asList(l.getId().toString()));
                QueryParameters parameters = new QueryParameters(parameterMap);
                UI.getCurrent().navigate(WorkView.ROUTE, parameters);
            });
            add(lectureButton);

            VerticalLayout layout = new VerticalLayout();
            for (Exercise e : l.getExercises()) {
                Button exerciseButton = new Button(e.getName());
                exerciseButton.addClickListener(event -> {
                    var parameterMap = new HashMap<String, List<String>>();
                    parameterMap.put("lecture", Arrays.asList(l.getId().toString()));
                    parameterMap.put("exercise", Arrays.asList(e.getName()));
                    QueryParameters parameters = new QueryParameters(parameterMap);
                    UI.getCurrent().navigate(WorkView.ROUTE, parameters);
                });
                layout.add(exerciseButton);
            }

            Accordion accordion = new Accordion();
            accordion.add("Übungen", layout);
            add(accordion);
        }

    }

    private VerticalLayout fillAccordions(Lecture lecture) {

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setWidth("300%");
        lecture.getExercises().stream().forEach(item -> verticalLayout.add(createNameButton(item)));

        return verticalLayout;
    }

    private Button createNameButton(Exercise exercise) {
        Button button = new Button(exercise.getName(), clickEvent -> {
            UI.getCurrent().navigate(WorkView.ROUTE);
        });
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        button.getStyle().set("margin", "0");
        return button;
    }
}