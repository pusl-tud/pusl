package de.bp2019.zentraldatei.view;

import java.util.Optional;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.bp2019.zentraldatei.model.ModuleScheme;
import de.bp2019.zentraldatei.service.ModuleSchemeService;

/**
 * View that displays a list of all ModuleSchemes
 * 
 * @author Leon Chemnitz
 */
@Route("moduleSchemes")
public class ManageModuleSchemesView extends VerticalLayout {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(ManageModuleSchemesView.class);

    public ManageModuleSchemesView(@Autowired ModuleSchemeService moduleSchemeService) {
        LOGGER.debug("started creation of ManageModuleSchemesView");

        setWidth("50em");

        /* -- Create Components -- */
        Grid<ModuleScheme> grid = new Grid<>();
        grid.setWidth("100%");
        grid.setItems(moduleSchemeService.getAllModuleSchemes());

        grid.addComponentColumn(item -> createNameButton(item)).setAutoWidth(true).setHeader("Veranstaltungs Schemas");
        grid.addComponentColumn(item -> createInstitutesTag(item)).setAutoWidth(true);
        grid.addComponentColumn(item -> createDeleteButton(item)).setFlexGrow(0).setWidth("5em");

        add(grid);

        Button newModuleSchemeButton = new Button("Neues Veranstaltungsschema");
        newModuleSchemeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        add(newModuleSchemeButton);
        setHorizontalComponentAlignment(Alignment.END, newModuleSchemeButton);

        newModuleSchemeButton.addClickListener(event -> UI.getCurrent().navigate("moduleScheme/new"));

        LOGGER.debug("finished creation of ManageModuleSchemesView");
    }

    private Button createNameButton(ModuleScheme item) {
        Button button = new Button(item.getName(), clickEvent -> {
            UI.getCurrent().navigate("moduleScheme/" + item.getId());
        });
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        return button;
    }

    private Text createInstitutesTag(ModuleScheme item) {
        Optional<String> text = item.getInstitutes().stream().map(institute -> institute.getName())
                .sorted(String.CASE_INSENSITIVE_ORDER).reduce((i1, i2) -> i1 + ", " + i2);

        if (text.isPresent()) {
            return new Text(text.get());
        } else {
            return new Text("");
        }
    }

    private Button createDeleteButton(ModuleScheme item) {
        Button button = new Button(new Icon(VaadinIcon.CLOSE), clickEvent -> {
            Dialog dialog = new Dialog();
            dialog.add(new Text("Wirklich löschen oder so?"));
            dialog.open();
        });
        button.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        return button;
    }
}