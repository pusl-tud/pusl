package de.bp2019.pusl.ui.views.institute;

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

import de.bp2019.pusl.config.PuslProperties;
import de.bp2019.pusl.model.Institute;
import de.bp2019.pusl.service.InstituteService;
import de.bp2019.pusl.ui.views.BaseView;
import de.bp2019.pusl.ui.views.MainAppView;

/**
 * View that displays a list of all {@link Institute}s
 * 
 * @author Leon Chemnitz
 */
@PageTitle(PuslProperties.NAME + " | Institute")
@Route(value = ManageInstitutesView.ROUTE, layout = MainAppView.class)
public class ManageInstitutesView extends BaseView {

    private static final long serialVersionUID = -5763725756205681478L;

    public static final String ROUTE = "admin/institutes";

    private InstituteService instituteService;

    private ListDataProvider<Institute> instituteDataProvider;

    @Autowired
    public ManageInstitutesView(InstituteService instituteService) {
        super("Institute");

        this.instituteService = instituteService;

        instituteDataProvider = new ListDataProvider<>(instituteService.getAllInstitutes());

        /* -- Create Components -- */

        Grid<Institute> grid = new Grid<>();

        grid.setWidth("100%");
        grid.setDataProvider(instituteDataProvider);

        grid.addComponentColumn(item -> createNameButton(item)).setAutoWidth(true);
        grid.addComponentColumn(item -> createDeleteButton(item)).setFlexGrow(0).setWidth("4em");

        add(grid);

        Button newInstituteButton = new Button("Neues Institut");
        newInstituteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        add(newInstituteButton);
        setHorizontalComponentAlignment(Alignment.END, newInstituteButton);

        newInstituteButton.addClickListener(event -> UI.getCurrent().navigate(EditInstituteView.ROUTE + "/new"));

    }

    /**
     * Used to create the button for the Grid entries that displays the name and
     * links to the edit page of the individual Institute.
     * 
     * @param item Institute to create the Button for
     * @return
     * @author Leon Chemnitz
     */
    private Button createNameButton(Institute item) {
        Button button = new Button(item.getName(), clickEvent -> {
            UI.getCurrent().navigate(EditInstituteView.ROUTE + "/" + item.getId());
        });
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        return button;
    }

    /**
     * Used to generate the delete button for each Grid Item
     * 
     * @param institute to create button for
     * @author Leon Chemnitz
     * @return delete button
     */
    protected Button createDeleteButton(Institute institute) {
        Button button = new Button(new Icon(VaadinIcon.CLOSE), clickEvent -> {
            Dialog dialog = new Dialog();
            dialog.add(new Text("Wirklich Löschen?"));
            dialog.setCloseOnEsc(false);
            dialog.setCloseOnOutsideClick(false);

            Button confirmButton = new Button("Löschen", event -> {
                try {
                    instituteService.deleteInstitute(institute);
                    instituteDataProvider.getItems().remove(institute);
                    instituteDataProvider.refreshAll();

                    dialog.close();
                    Dialog answerDialog = new Dialog();
                    answerDialog.add(new Text("Institut '" + institute.getName() + "' gelöscht"));
                    answerDialog.open();
                } catch (Exception e) {
                    Dialog answerDialog = new Dialog();
                    answerDialog.add(new Text("Fehler beim Löschen des Institutes!"));
                    answerDialog.open();
                    LOGGER.error("Could not delete Institute! Institute ID was: " + institute.getId());
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