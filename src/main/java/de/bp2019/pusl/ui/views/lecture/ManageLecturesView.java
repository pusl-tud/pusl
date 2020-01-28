package de.bp2019.pusl.ui.views.lecture;

import java.util.Optional;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import org.springframework.beans.factory.annotation.Autowired;

import de.bp2019.pusl.config.AppConfig;
import de.bp2019.pusl.model.Institute;
import de.bp2019.pusl.model.Lecture;
import de.bp2019.pusl.service.LectureService;
import de.bp2019.pusl.ui.views.BaseView;
import de.bp2019.pusl.ui.views.MainAppView;

/**
 * View that displays a list of all {@link Lecture}s accessible by the active
 * User. Also contains logic for deletion of a {@link Lecture} and leads to
 * {@link EditLectureView}.
 * 
 * @author Leon Chemnitz
 */
@PageTitle(AppConfig.NAME + " | Veranstaltungen verwalten")
@Route(value = ManageLecturesView.ROUTE, layout = MainAppView.class)
public class ManageLecturesView extends BaseView {

    private static final long serialVersionUID = 1L;

    public static final String ROUTE = "admin/lectures";

    private LectureService lectureService;
    private ListDataProvider<Lecture> lectureDataProvider;

    @Autowired
    public ManageLecturesView(LectureService lectureService) {
        super("Veranstaltungen");

        this.lectureService = lectureService;

        lectureDataProvider = new ListDataProvider<>(lectureService.getAll());

        /* -- Create Components -- */

        Grid<Lecture> grid = new Grid<>();

        grid.setWidth("100%");
        grid.setDataProvider(lectureDataProvider);

        grid.addComponentColumn(item -> createNameButton(item)).setAutoWidth(true);
        grid.addComponentColumn(item -> createInstitutesTag(item)).setAutoWidth(true);
        grid.addComponentColumn(item -> createDeleteButton(item)).setFlexGrow(0).setWidth("4em");

        add(grid);

        Button newLectureButton = new Button("Neue Veranstaltung");
        newLectureButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        add(newLectureButton);
        setHorizontalComponentAlignment(Alignment.END, newLectureButton);

        newLectureButton.addClickListener(event -> UI.getCurrent().navigate(EditLectureView.ROUTE + "/new"));

    }

    /**
     * Used to create the button for the Grid entries that displays the name and
     * links to the edit page of the individual {@link Lecture}.
     * 
     * @param lecture to create the Button for
     * @return
     * @author Leon Chemnitz
     */
    private Button createNameButton(Lecture lecture) {
        Button button = new Button(lecture.getName(), clickEvent -> {
            UI.getCurrent().navigate(EditLectureView.ROUTE + "/" + lecture.getId());
        });
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        return button;
    }

    /**
     * Used to generate the Institutes field for each Grid item
     * 
     * @param lecture to create institutes tag for
     * @author Leon Chemnitz
     * @return institutes tag
     */
    private Text createInstitutesTag(Lecture lecture) {
        Optional<String> text = lecture.getInstitutes().stream().map(Institute::getName)
                .sorted(String.CASE_INSENSITIVE_ORDER).reduce((i1, i2) -> i1 + ", " + i2);

        if (text.isPresent()) {
            return new Text(text.get());
        } else {
            return new Text("");
        }
    }

    /**
     * Used to generate the delete button for each Grid Item
     * 
     * @param lecture to create button for
     * @author Leon Chemnitz
     * @return delete button
     */
    protected Button createDeleteButton(Lecture lecture) {
        Button button = new Button(new Icon(VaadinIcon.CLOSE), clickEvent -> {
            Dialog dialog = new Dialog();
            dialog.add(new Text("Wirklich Löschen?"));
            dialog.setCloseOnEsc(false);
            dialog.setCloseOnOutsideClick(false);

            Button confirmButton = new Button("Löschen", event -> {
                try {
                    lectureService.delete(lecture);
                    lectureDataProvider.getItems().remove(lecture);
                    lectureDataProvider.refreshAll();

                    dialog.close();
                    Dialog answerDialog = new Dialog();
                    answerDialog.add(new Text("Veranstaltung '" + lecture.getName() + "' gelöscht"));
                    answerDialog.open();
                } catch(Exception e){                    
                    Dialog answerDialog = new Dialog();
                    answerDialog.add(new Text("Fehler beim Löschen der Veranstaltung!"));
                    answerDialog.open();
                    LOGGER.error("Could not delete Lecture! Lecture ID was: " + lecture.getId());
                }
            });

            Button cancelButton = new Button("Abbruch", event -> {
                dialog.close();
            });

            dialog.add(confirmButton, cancelButton);
            dialog.open();
        });
        button.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_ERROR);
        return button;
    }
}