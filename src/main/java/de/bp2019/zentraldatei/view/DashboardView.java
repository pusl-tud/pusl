package de.bp2019.zentraldatei.view;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;


import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.bp2019.zentraldatei.model.ModuleScheme;
import de.bp2019.zentraldatei.model.ExerciseScheme;
import de.bp2019.zentraldatei.model.User;
import de.bp2019.zentraldatei.service.DashboardService;

/**
 * View that displays a Dashboard
 * 
 * @author Alexander Spaeth
 */
@PageTitle("Zentraldatei | Dashboard")
@Route(value = "dashboard", layout = MainAppView.class)
public class DashboardView extends VerticalLayout {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(DashboardView.class);

    private DashboardService dashboardService;
    
    //Helperclass for displaying recently edited entries
    class RecentElement{
    	private String type;
    	private String name;
    	private String entryId;
    	
    	public RecentElement(String type, String name, String entyId) {
    		this.type = type;
        	this.name = name;
        	this.entryId = entryId;
    	}
    	
    	public String getType() {
    		return type;
    	}
    	
    	public String getName() {
    		return name;
    	}
    	
    	public String getEntryId() {
    		return entryId;
    	}
    }     

    public DashboardView(@Autowired DashboardService dashboardService) {
        LOGGER.debug("started creation of DashboardView");

        this.dashboardService = dashboardService;

        setWidth("50em");
        
         //Dummy data set for display purposes
        List<DashboardView.RecentElement> recentElements = new ArrayList<>();
        recentElements.add(new DashboardView.RecentElement("User", "Max Mustermann", "1"));
        recentElements.add(new DashboardView.RecentElement("User", "Franz Beispiel", "2"));
        recentElements.add(new DashboardView.RecentElement("Vorlesungs Schema", "Verkehrswesen 1", "3"));
        recentElements.add(new DashboardView.RecentElement("Vorlesungs Schema", "Compilerbau 1", "4"));
        recentElements.add(new DashboardView.RecentElement("Übungs Schema", "Verkehrswesen 1  - Übung 1", "5"));
        recentElements.add(new DashboardView.RecentElement("Übungs Schema", "Verkehrswesen 1 - Übung 2", "6"));
        recentElements.add(new DashboardView.RecentElement("Übungs Schema", "Compilerbau 1 - Übung 1", "7"));
        
        Grid<DashboardView.RecentElement> recentElementsGrid = new Grid<>();
        recentElementsGrid.setWidth("100%");
        recentElementsGrid.setItems(recentElements);
        
        
        recentElementsGrid.addColumn(DashboardView.RecentElement::getType).setHeader("Typ");
        recentElementsGrid.addColumn(DashboardView.RecentElement::getName).setHeader("Name");
        recentElementsGrid.addComponentColumn(item -> createEditButon(item)).setAutoWidth(true);
        
        Text recentTitle = new Text("Kürzlich bearbeitete Daten");
        
        add(recentTitle);
        add(recentElementsGrid);
        
        /* -- Create Components -- */
        /*
        Grid<ModuleScheme> recentModuleSchemes = new Grid<>();
        recentModuleSchemes.setWidth("100%");
        recentModuleSchemes.setItems(dashboardService.getRecentModuleSchemes());

        recentModuleSchemes.addComponentColumn(item -> testButtonModule(item)).setAutoWidth(true).setHeader("Veranstaltungs Schemas");
        
        Grid<ExerciseScheme> recentExerciseSchemes = new Grid<>();
        recentExerciseSchemes.setWidth("100%");
        recentExerciseSchemes.setItems(dashboardService.getRecentExcerciseSchemes());

        recentExerciseSchemes.addComponentColumn(testButtonExcercise(item)).setAutoWidth(true).setHeader("Übungs Schemas");
        
        // TODO: Logik die User nur für Admins und SuperAdmins anzeigt
        
        Grid<User> recentUsers = new Grid<>();
        recentUsers.setWidth("100%");
        recentUsers.setItems(dashboardService.getRecentUsers());

        recentUsers.addComponentColumn(item -> testButtonUser(item)).setAutoWidth(true).setHeader("Users");

        add(recentModuleSchemes);
        add(recentExerciseSchemes);
        add(recentUsers);
        */

        LOGGER.debug("finished creation of DashboardView");
    }
    
    
    
    /**
     * Used to create the button for the Grid entries that displays the name and
     * links to the edit page of the individual User
     * @param item User to create the Button for
     * @author Alexander Spaeth
     */
    private Button createEditButon(DashboardView.RecentElement recent) {
        Button button = new Button("Weiter bearbeiten");
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        return button;
    }
    
    /**
     * Used to create the button for the Grid entries that displays the name and
     * links to the edit page of the individual ModuleScheme.
     * 
     * @param item ModuleScheme to create the Button for
     * @author Alexander Spaeth
     */
    private Button createExerciseNameButton(ExerciseScheme item) {
        Button button = new Button(item.getName(), clickEvent -> {
            UI.getCurrent().navigate("exerciseScheme/" + item.getId());
        });
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        return button;
    }

    /**
     * Used to create the button for the Grid entries that displays the name and
     * links to the edit page of the individual ModuleScheme.
     * 
     * @param item ModuleScheme to create the Button for
     * @author Alexander Spaeth
     */
    private Button createModuleNameButton(ModuleScheme item) {
        Button button = new Button(item.getName(), clickEvent -> {
            UI.getCurrent().navigate("moduleScheme/" + item.getId());
        });
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        return button;
    }
  
}