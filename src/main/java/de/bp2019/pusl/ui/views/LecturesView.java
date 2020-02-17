package de.bp2019.pusl.ui.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;

import de.bp2019.pusl.config.AppConfig;
import de.bp2019.pusl.model.Exercise;
import de.bp2019.pusl.model.Lecture;
import de.bp2019.pusl.service.LectureService;

/**
 * View displaying a list of all {@link Lecture}s
 * 
 * @author Leon Chemnitz
 */
@PageTitle(AppConfig.NAME + " | Meine Veranstaltungen")
@Route(value = LecturesView.ROUTE, layout = MainAppView.class)
public class LecturesView extends BaseView {

    private static final long serialVersionUID = 1L;

    public static final String ROUTE = "";

    public LecturesView(LectureService lectureService) {
        super("Meine Veranstaltungen");

        List<Lecture> lectures = new ArrayList<>();
        lectures.addAll(lectureService.getAll());

        for (Lecture l : lectures) {

            VerticalLayout horizontalLayout = new VerticalLayout();

            Button lectureButton = new Button(l.getName(), new Icon(VaadinIcon.ACADEMY_CAP));
            lectureButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
            lectureButton.addClickListener(event -> {
                var parameterMap = new HashMap<String, List<String>>();
                parameterMap.put("lecture", Arrays.asList(l.getId().toString()));
                QueryParameters parameters = new QueryParameters(parameterMap);
                UI.getCurrent().navigate(WorkView.ROUTE, parameters);
            });
            horizontalLayout.add(lectureButton);

            VerticalLayout layout = new VerticalLayout();
            for (Exercise e : l.getExercises()) {
                Button exerciseButton = new Button(e.getName());
                exerciseButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
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
            AccordionPanel panel = accordion.add("Übungen", layout);
            panel.addThemeVariants(DetailsVariant.SMALL);
            panel.addThemeVariants(DetailsVariant.FILLED);
            accordion.close();
            horizontalLayout.add(accordion);
            add(horizontalLayout);
        }

    }

}