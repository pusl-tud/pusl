package de.bp2019.pusl.ui.views;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.bp2019.pusl.config.AppConfig;
import de.bp2019.pusl.model.Lecture;
import de.bp2019.pusl.service.LectureService;
import de.bp2019.pusl.ui.views.lecture.EditLectureView;

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
    
    private LectureService lectureService;
    
    public LecturesView(LectureService lectureService) {
        super("Meine Veranstaltungen");
        
        this.lectureService = lectureService;
        
        List<Lecture> lecturesList = new ArrayList<>();
        
        lecturesList.addAll(lectureService.getAll());
        
        lecturesList.forEach(lecture -> add(createLectureNameButton(lecture), createExerciseButton(lecture)));
    }
 
	private Button createLectureNameButton(Lecture lecture) {
		Button button = new Button(lecture.getName(), clickEvent -> {
			UI.getCurrent().navigate(EditLectureView.ROUTE + "/" + lecture.getId());
		});
		button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		return button;
	}

	private Button createExerciseButton(Lecture lecture) {
		Button button = new Button("Übungen anzeigen", clickEvent -> {
			UI.getCurrent().navigate(WorkView.ROUTE + "/?lectureFilter=" + lecture.getName());
		});
        button.getStyle().set("margin", "0");
		return button;
	}
}