package de.bp2019.pusl.ui.views.exercisescheme;

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
import de.bp2019.pusl.model.ExerciseScheme;
import de.bp2019.pusl.model.Institute;
import de.bp2019.pusl.service.ExerciseSchemeService;
import de.bp2019.pusl.ui.views.BaseView;
import de.bp2019.pusl.ui.views.MainAppView;

/**
 * View that displays a list of all Exercises
 *
 * @author Luca Dinies
 **/
@PageTitle(AppConfig.NAME + " | Übungsschemas")
@Route(value = ManageExerciseSchemesView.ROUTE, layout = MainAppView.class)
public class ManageExerciseSchemesView extends BaseView {

    private static final long serialVersionUID = 1L;

    public static final String ROUTE = "admin/exerciseSchemes";

    private ExerciseSchemeService exerciseSchemeService;

    private ListDataProvider<ExerciseScheme> exerciseSchemeDataProvider;

    @Autowired
    public ManageExerciseSchemesView(ExerciseSchemeService exerciseSchemeService) {
        super("Übungsschemas");

        this.exerciseSchemeService = exerciseSchemeService;
 
        exerciseSchemeDataProvider = new ListDataProvider<>(exerciseSchemeService.getAllExerciseSchemes());

        /* -- Create Components -- */

        Grid<ExerciseScheme> grid = new Grid<>();

        grid.setWidth("100%");
        grid.setDataProvider(exerciseSchemeDataProvider);

        grid.addComponentColumn(item -> createNameButton(item)).setAutoWidth(true);
        grid.addComponentColumn(item -> createInstitutesTag(item)).setAutoWidth(true);
        grid.addComponentColumn(item -> createDeleteButton(item)).setFlexGrow(0).setWidth("4em");

        add(grid);

        Button newExerciseSchemeButton = new Button("Neues Übungsschema");
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
        Optional<String> text = exerciseScheme.getInstitutes().stream().map(Institute::getName)
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
            Dialog dialog = new Dialog();
            exerciseSchemeService.deleteExerciseScheme(exerciseScheme);
            dialog.add(new Text("Wirklich Löschen?"));
            dialog.setCloseOnEsc(false);
            dialog.setCloseOnOutsideClick(false);

            Button confirmButton = new Button("Löschen", event -> {
                try {
                    exerciseSchemeService.deleteExerciseScheme(exerciseScheme);
                    exerciseSchemeDataProvider.getItems().remove(exerciseScheme);
                    exerciseSchemeDataProvider.refreshAll();

                    dialog.close();
                    Dialog answerDialog = new Dialog();
                    answerDialog.add(new Text("Übungsschema '" + exerciseScheme.getName() + "' gelöscht"));
                    answerDialog.open();
                } catch (Exception e) {
                    Dialog answerDialog = new Dialog();
                    answerDialog.add(new Text("Fehler beim Löschen des Übungsschemas!"));
                    answerDialog.open();
                    LOGGER.error("Could not delete ExerciseScheme! ExerciseScheme ID was: " + exerciseScheme.getId());
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
