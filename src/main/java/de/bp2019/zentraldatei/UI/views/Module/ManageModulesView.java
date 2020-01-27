package de.bp2019.zentraldatei.UI.views.Module;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.bp2019.zentraldatei.model.Institute;
import de.bp2019.zentraldatei.model.Module;
import de.bp2019.zentraldatei.service.ModuleService;
import de.bp2019.zentraldatei.UI.views.BaseView;
import de.bp2019.zentraldatei.UI.views.MainAppView;

/**
 * View that displays a list of all Modules
 * 
 * @author Leon Chemnitz
 */
@PageTitle("Zentraldatei | Veranstaltungen verwalten")
@Route(value = ManageModulesView.ROUTE, layout = MainAppView.class)
public class ManageModulesView extends BaseView {

    private static final long serialVersionUID = 1L;

    public static final String ROUTE = "manage-modules";

    private static final Logger LOGGER = LoggerFactory.getLogger(ManageModulesView.class);

    private ModuleService moduleService;

    private Grid<Module> grid = new Grid<>();

    @Autowired
    public ManageModulesView(ModuleService moduleService) {
        super("Veranstaltungen");
        LOGGER.debug("started creation of ManageModulesView");

        this.moduleService = moduleService;

        /* -- Create Components -- */

        Grid<Module> grid = new Grid<>();

        grid.setWidth("100%");
        grid.setItems(moduleService.getAllModules());

        grid.addComponentColumn(item -> createNameButton(item)).setAutoWidth(true);
        grid.addComponentColumn(item -> createInstitutesTag(item)).setAutoWidth(true);
        grid.addComponentColumn(item -> createDeleteButton(item)).setFlexGrow(0).setWidth("4em");

        add(grid);

        Button newModuleButton = new Button("Neues Veranstaltungsschema");
        newModuleButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        add(newModuleButton);
        setHorizontalComponentAlignment(Alignment.END, newModuleButton);

        newModuleButton.addClickListener(event -> UI.getCurrent().navigate(EditModuleView.ROUTE + "/new"));

        LOGGER.debug("finished creation of ManageModulesView");
    }

    /**
     * Used to create the button for the Grid entries that displays the name and
     * links to the edit page of the individual Module.
     * 
     * @param item Module to create the Button for
     * @return
     * @author Leon Chemnitz
     */
    private Button createNameButton(Module item) {
        Button button = new Button(item.getName(), clickEvent -> {
            UI.getCurrent().navigate(EditModuleView.ROUTE + "/" + item.getId());
        });
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        return button;
    }

    /**
     * Used to generate the Institutes field for each Grid item
     * 
     * @param item entity to create institutes tag for
     * @author Leon Chemnitz
     * @return institutes tag
     */
    private Text createInstitutesTag(Module item) {
        Optional<String> text = item.getInstitutes().stream().map(Institute::getName)
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
     * @param item entity to create button for
     * @author Leon Chemnitz
     * @return delete button
     */
    protected Button createDeleteButton(Module item) {
        Button button = new Button(new Icon(VaadinIcon.CLOSE), clickEvent -> {
            Dialog dialog = new Dialog();
            dialog.add(new Text("Wirklich Löschen?"));
            dialog.setCloseOnEsc(false);
            dialog.setCloseOnOutsideClick(false);

            Button confirmButton = new Button("Löschen", event -> {
                moduleService.delete(item);
                ListDataProvider<Module> dataProvider = (ListDataProvider<Module>) grid.getDataProvider();
                dataProvider.getItems().remove(item);
                dataProvider.refreshAll();

                dialog.close();
                Dialog answerDialog = new Dialog();
                answerDialog.add(new Text("Modulschema '" + item.getName() + "' gelöscht"));
                answerDialog.open();
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