package de.bp2019.zentraldatei.view;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.bp2019.zentraldatei.model.ModuleScheme;
import de.bp2019.zentraldatei.service.ModuleSchemeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

/**
 * View that displays a list of all ModuleSchemes
 * 
 * @author Leon Chemnitz
 */
@PageTitle("Zentraldatei | Veranstaltungsschemas")
@Route(value = "moduleSchemes", layout = MainAppView.class)
public class ManageModuleSchemesView extends VerticalLayout {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(ManageModuleSchemesView.class);

    private ModuleSchemeService moduleSchemeService;

    private Grid<ModuleScheme> grid = new Grid<>();

    public ManageModuleSchemesView(@Autowired ModuleSchemeService moduleSchemeService) {

        LOGGER.debug("started creation of ManageModuleSchemesView");

        this.moduleSchemeService = moduleSchemeService;

        setWidth("90%");
        setMaxWidth("50em");
        getStyle().set("marginLeft", "2em");

        /* -- Create Components -- */
      
        Label title = new Label("Veranstaltungsschemas");
        title.getStyle().set("font-size", "2em");
        add(title);

        Grid<ModuleScheme> grid = new Grid<>();
      
        grid.setWidth("100%");
        grid.setItems(moduleSchemeService.getAllModuleSchemes());

        grid.addComponentColumn(item -> createNameButton(item)).setAutoWidth(true);
        grid.addComponentColumn(item -> createInstitutesTag(item)).setAutoWidth(true);
        grid.addComponentColumn(item -> createDeleteButton(item)).setFlexGrow(0).setWidth("4em");

        add(grid);

        Button newModuleSchemeButton = new Button("Neues Veranstaltungsschema");
        newModuleSchemeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        add(newModuleSchemeButton);
        setHorizontalComponentAlignment(Alignment.END, newModuleSchemeButton);

        newModuleSchemeButton.addClickListener(event -> UI.getCurrent().navigate("moduleScheme/new"));

        LOGGER.debug("finished creation of ManageModuleSchemesView");
    }

    /**
     * Used to create the button for the Grid entries that displays the name and
     * links to the edit page of the individual ModuleScheme.
     * 
     * @param item ModuleScheme to create the Button for
     * @author Leon Chemnitz
     */
    private Button createNameButton(ModuleScheme item) {
        Button button = new Button(item.getName(), clickEvent -> {
            UI.getCurrent().navigate("moduleScheme/" + item.getId());
        });
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        return button;
    }

    /**
     * Used to generate the Institutes field for each Grid item
     * 
     * @param item
     * @author Leon Chemnitz
     */
    private Text createInstitutesTag(ModuleScheme item) {
        Optional<String> text = moduleSchemeService.getInstitutes(item).stream().map(institute -> institute.getName())
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
     * @param item
     * @author Leon Chemnitz
     */
    protected Button createDeleteButton(ModuleScheme item) {
        Button button = new Button(new Icon(VaadinIcon.CLOSE), clickEvent -> {
            Dialog dialog = new Dialog();
            dialog.add(new Text("Wirklich Löschen?"));
            dialog.setCloseOnEsc(false);
            dialog.setCloseOnOutsideClick(false);

            Button confirmButton = new Button("Löschen", event -> {
                moduleSchemeService.deleteModuleScheme(item);
                ListDataProvider<ModuleScheme> dataProvider = (ListDataProvider<ModuleScheme>) grid.getDataProvider();
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