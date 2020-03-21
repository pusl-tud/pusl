package de.bp2019.pusl.ui.views.institute;

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
import de.bp2019.pusl.service.InstituteService;
import de.bp2019.pusl.ui.dialogs.ConfirmDeletionDialog;
import de.bp2019.pusl.ui.dialogs.ErrorDialog;
import de.bp2019.pusl.ui.dialogs.SuccessDialog;
import de.bp2019.pusl.ui.interfaces.AccessibleBySuperadmin;
import de.bp2019.pusl.ui.views.BaseView;
import de.bp2019.pusl.ui.views.LecturesView;
import de.bp2019.pusl.ui.views.MainAppView;
import de.bp2019.pusl.util.Service;
import de.bp2019.pusl.util.exceptions.UnauthorizedException;

/**
 * View that displays a list of all {@link Institute}s
 * 
 * @author Leon Chemnitz
 */
@PageTitle(PuslProperties.NAME + " | Institute")
@Route(value = ManageInstitutesView.ROUTE, layout = MainAppView.class)
public class ManageInstitutesView extends BaseView implements AccessibleBySuperadmin {

    private static final long serialVersionUID = -5763725756205681478L;

    public static final String ROUTE = "admin/institutes";

    private InstituteService instituteService;

    public ManageInstitutesView() {
        super("Institute");

        this.instituteService = Service.get(InstituteService.class);

        /* -- Create Components -- */

        Grid<Institute> grid = new Grid<>();

        grid.setWidth("100%");
        grid.setDataProvider(instituteService);

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
    private Button createDeleteButton(Institute institute) {
        Button button = new Button(new Icon(VaadinIcon.CLOSE), clickEvent -> {
            ConfirmDeletionDialog.open(institute.getName(), () -> {
                try {
                    instituteService.delete(institute);
                    instituteService.refreshAll();
                    SuccessDialog.open(institute.getName() + " erfolgreich gelöscht");
                } catch (UnauthorizedException e) {
                    UI.getCurrent().navigate(LecturesView.ROUTE);
                    ErrorDialog.open("Nicht authorisiert um Institut zu löschen!");
                }
            });
        });
        button.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_ERROR);
        /** makes testing a lot easier */
        button.setId("delete-" + institute.getId().toString());
        return button;
    }
}