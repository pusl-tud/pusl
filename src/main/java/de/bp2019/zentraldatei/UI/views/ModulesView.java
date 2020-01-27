package de.bp2019.zentraldatei.UI.views;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.bp2019.zentraldatei.model.Exercise;
import de.bp2019.zentraldatei.model.Module;
import de.bp2019.zentraldatei.service.ModuleService;

// import de.bp2019.zentraldatei.service.DashboardService;

/**
 * View that displays a Dashboard
 * 
 * @author Alexander Spaeth, Tomoki Tokuyama
 */
@PageTitle("Zentraldatei | Meine Module")
@Route(value = ModulesView.ROUTE, layout = MainAppView.class)
public class ModulesView extends BaseView {

    private static final long serialVersionUID = 1L;

    public static final String ROUTE = "";

    private static final Logger LOGGER = LoggerFactory.getLogger(ModulesView.class);

    public ModulesView(ModuleService moduleService) {
        super("Meine Module");
        LOGGER.debug("started creation of ModulesView");

        List<Module> modules = new ArrayList<>();
        modules.addAll(moduleService.getAllModules());

        Accordion accordion = new Accordion();

        modules.stream().forEach(
                item -> accordion.add(item.getName(), fillAccordions(item)).addThemeVariants(DetailsVariant.FILLED));

        add(accordion);

        LOGGER.debug("finished creation of ModulesView");
    }

    private VerticalLayout fillAccordions(Module module) {

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setWidth("300%");
        module.getExercises().stream().forEach(item -> verticalLayout.add(createNameButton(item)));

        return verticalLayout;
    }

    private Button createNameButton(Exercise item) {
        Button button = new Button(item.getName(), clickEvent -> {
            UI.getCurrent().navigate(WorkView.ROUTE);
        });
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        button.getStyle().set("margin", "0");
        return button;
    }
}