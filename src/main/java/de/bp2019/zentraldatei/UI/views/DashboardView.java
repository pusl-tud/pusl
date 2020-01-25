package de.bp2019.zentraldatei.UI.views;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// import de.bp2019.zentraldatei.service.DashboardService;

/**
 * View that displays a Dashboard
 * 
 * @author Alexander Spaeth
 */
@PageTitle("Zentraldatei | Dashboard")
@Route(value = "", layout = MainAppView.class)
public class DashboardView extends BaseView {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(DashboardView.class);

    // private DashboardService dashboardService;
    
    //Helperclass for displaying recently edited entries
    class RecentElement{
    	private String type;
    	private String name;
    	private String entryId;
    	
    	public RecentElement(String type, String name, String entryId) {
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

    public DashboardView() {
        super("Kürzlich bearbeitete Daten");
        LOGGER.debug("started creation of DashboardView");

        // this.dashboardService = dashboardService;
        
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
        
        add(recentElementsGrid);
        
        LOGGER.debug("finished creation of DashboardView");
    }
    
    
    
    /**
     * Used to create the button that takes you to the edditing page for a recently used item
     * @param recent The recently used item
     * @author Alexander Spaeth
     */
    private Button createEditButon(DashboardView.RecentElement recent) {
        Button button = new Button("Weiter bearbeiten");
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        return button;
    }
    
  
}