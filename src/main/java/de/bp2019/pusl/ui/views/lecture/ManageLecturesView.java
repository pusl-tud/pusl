package de.bp2019.pusl.ui.views.lecture;

import java.util.Optional;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.bp2019.pusl.config.PuslProperties;
import de.bp2019.pusl.model.Institute;
import de.bp2019.pusl.model.Lecture;
import de.bp2019.pusl.service.InstituteService;
import de.bp2019.pusl.service.LectureService;
import de.bp2019.pusl.ui.dialogs.ConfirmDeletionDialog;
import de.bp2019.pusl.ui.dialogs.ErrorDialog;
import de.bp2019.pusl.ui.dialogs.SuccessDialog;
import de.bp2019.pusl.ui.interfaces.AccessibleByAdmin;
import de.bp2019.pusl.ui.views.BaseView;
import de.bp2019.pusl.ui.views.MainAppView;
import de.bp2019.pusl.util.Service;
import de.bp2019.pusl.util.exceptions.UnauthorizedException;

/**
 * View that displays a list of all {@link Lecture}s accessible by the active
 * User. Also contains logic for deletion of a {@link Lecture} and leads to
 * {@link EditLectureView}.
 * 
 * @author Leon Chemnitz
 */
@PageTitle(PuslProperties.NAME + " | Veranstaltungen verwalten")
@Route(value = ManageLecturesView.ROUTE, layout = MainAppView.class)
public class ManageLecturesView extends BaseView implements AccessibleByAdmin {

    private static final long serialVersionUID = 1L;

    public static final String ROUTE = "admin/lectures";

    private LectureService lectureService;
    private InstituteService instituteService;

    public ManageLecturesView() {
        super("Veranstaltungen");

        this.lectureService = Service.get(LectureService.class);
        this.instituteService = Service.get(InstituteService.class);

        /* -- Create Components -- */

        Grid<Lecture> grid = new Grid<>();

        grid.setWidth("100%");
        grid.setDataProvider(lectureService);

        grid.addComponentColumn(item -> createNameButton(item)).setAutoWidth(true);
        grid.addComponentColumn(item -> createInstitutesTag(item)).setAutoWidth(true);
        grid.addComponentColumn(item -> createDeleteButton(item)).setFlexGrow(0).setWidth("4em");

        add(grid);

        Button newLectureButton = new Button("Neue Veranstaltung");
        newLectureButton.addClickShortcut(Key.KEY_N,KeyModifier.CONTROL, KeyModifier.ALT);
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
        Optional<String> text = instituteService.getInstitutesFromObject(lecture).stream().map(Institute::getName)
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
            ConfirmDeletionDialog.open(lecture.getName(), () -> {
                try {
                    lectureService.delete(lecture);
                    lectureService.refreshAll();
                    SuccessDialog.open(lecture.getName() + " erfolgreich gelöscht");
                } catch (UnauthorizedException e) {
                    UI.getCurrent().navigate(PuslProperties.ROOT_ROUTE);
                    ErrorDialog.open("Nicht authorisiert um Veranstaltung zu löschen!");
                }
            });
        });
        button.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_ERROR);
        /** makes testing a lot easier */
        button.setId("delete-" + lecture.getId().toString());
        return button;
    }
}