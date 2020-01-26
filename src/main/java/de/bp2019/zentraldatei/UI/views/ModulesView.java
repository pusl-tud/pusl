package de.bp2019.zentraldatei.UI.views;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.bp2019.zentraldatei.UI.views.MainAppView;
import de.bp2019.zentraldatei.UI.views.BaseView;
import de.bp2019.zentraldatei.model.exercise.ExerciseInstance;
import de.bp2019.zentraldatei.model.module.Module;
import de.bp2019.zentraldatei.service.ModuleService;

// import de.bp2019.zentraldatei.service.DashboardService;

/**
 * View that displays a Dashboard
 * 
 * @author Alexander Spaeth, Tomoki Tokuyama
 */
@PageTitle("Zentraldatei | Meine Module")
@Route(value = "", layout = MainAppView.class)
public class ModulesView extends BaseView {

    private static final long serialVersionUID = 1L;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ModulesView.class);

    private ModuleService moduleService;
    
    public ModulesView(ModuleService moduleService) {
        super("Meine Module");
        LOGGER.debug("started creation of ModulesView");

        this.moduleService = moduleService;
        
        
        
        List<Module> modules = new ArrayList<>();
        modules.addAll(moduleService.getAllModules());
        
        Accordion accordion = new Accordion();
        
        modules.stream().forEach(item -> accordion.add(item.getName(), fillAccordions(item)));
        
        add(accordion);
        
        LOGGER.debug("finished creation of ModulesView");
    }

    
	private VerticalLayout fillAccordions(Module module) {
		
		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.setWidth("300%");
		Grid<ExerciseInstance> grid = new Grid<>();
		grid.setItems(module.getExercises());
		
		grid.addComponentColumn(item -> createIdButton(item)).setWidth("100%");
		
		verticalLayout.add(grid);
		
		return verticalLayout;
	}


	private Button createIdButton(ExerciseInstance item) {
		Button button = new Button(item.getId(), clickEvent -> {
            UI.getCurrent().navigate(DemoView.ROUTE);
        });
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        return button;
	}
}