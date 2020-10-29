package de.bp2019.pusl.ui.views.exercisescheme;

import java.util.Optional;

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
import de.bp2019.pusl.model.ExerciseScheme;
import de.bp2019.pusl.model.Institute;
import de.bp2019.pusl.service.ExerciseSchemeService;
import de.bp2019.pusl.service.InstituteService;
import de.bp2019.pusl.ui.dialogs.ConfirmDeletionDialog;
import de.bp2019.pusl.ui.dialogs.ErrorDialog;
import de.bp2019.pusl.ui.dialogs.SuccessDialog;
import de.bp2019.pusl.ui.interfaces.AccessibleByAdmin;
import de.bp2019.pusl.ui.views.BaseView;
import de.bp2019.pusl.ui.views.MainAppView;
import de.bp2019.pusl.util.Service;
import de.bp2019.pusl.util.exceptions.UnauthorizedException;

/**
 * View that displays a list of all Exercises
 *
 * @author Luca Dinies
 **/
@PageTitle(PuslProperties.NAME + " | Leistungsschemas")
@Route(value = ManageExerciseSchemesView.ROUTE, layout = MainAppView.class)
public class ManageExerciseSchemesView extends BaseView implements AccessibleByAdmin {

    private static final long serialVersionUID = 1L;

    public static final String ROUTE = "admin/exerciseSchemes";

    private ExerciseSchemeService exerciseSchemeService;
    private InstituteService instituteService;

    public ManageExerciseSchemesView() {
        super("Leistungsschemas");

        this.exerciseSchemeService = Service.get(ExerciseSchemeService.class);
        this.instituteService = Service.get(InstituteService.class);

        /* -- Create Components -- */

        Grid<ExerciseScheme> grid = new Grid<>();

        grid.setWidth("100%");
        grid.setDataProvider(exerciseSchemeService);

        grid.addComponentColumn(item -> createNameButton(item)).setAutoWidth(true);
        grid.addComponentColumn(item -> createInstitutesTag(item)).setAutoWidth(true);
        grid.addComponentColumn(item -> createDeleteButton(item)).setFlexGrow(0).setWidth("4em");

        add(grid);

        Button newExerciseSchemeButton = new Button("Neues Leistungsschema");
        newExerciseSchemeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        add(newExerciseSchemeButton);
        setHorizontalComponentAlignment(Alignment.END, newExerciseSchemeButton);

        newExerciseSchemeButton
                .addClickListener(event -> UI.getCurrent().navigate(EditExerciseSchemeView.ROUTE + "/new"));

    }

    /**
     * Used to create the button for the Grid entries that displays the name and
     * links to the edit page of the individual {@link ExerciseScheme}.
     *
     * @param exerciseScheme to create the Button for
     * @return
     * @author Luca Dinies
     */
    private Button createNameButton(ExerciseScheme exerciseScheme) {
        Button button = new Button(exerciseScheme.getName(), clickEvent -> {
            UI.getCurrent().navigate(EditExerciseSchemeView.ROUTE + "/" + exerciseScheme.getId());
        });
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        return button;
    }

    /**
     * Used to generate the Institutes field for each Grid item
     *
     * @param exerciseScheme
     * @return
     * @author Luca Dinies
     */
    private Text createInstitutesTag(ExerciseScheme exerciseScheme) {
        Optional<String> text = instituteService.getInstitutesFromObject(exerciseScheme).stream().map(Institute::getName)
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
     * @param exerciseScheme
     * @return
     * @author Luca Dinies
     */
    private Button createDeleteButton(ExerciseScheme exerciseScheme) {
        Button button = new Button(new Icon(VaadinIcon.CLOSE), clickEvent -> {
            ConfirmDeletionDialog.open(exerciseScheme.getName(), () -> {
                try {
                    exerciseSchemeService.delete(exerciseScheme);
                    exerciseSchemeService.refreshAll();
                    SuccessDialog.open(exerciseScheme.getName() + " erfolgreich gelöscht");
                } catch (UnauthorizedException e) {
                    UI.getCurrent().navigate(PuslProperties.ROOT_ROUTE);
                    ErrorDialog.open("Nicht authorisiert um Leistungsschema zu löschen!");
                }
            });
        });
        button.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_ERROR);
        /** makes testing a lot easier */
        button.setId("delete-" + exerciseScheme.getId().toString());
        return button;
    }

}
