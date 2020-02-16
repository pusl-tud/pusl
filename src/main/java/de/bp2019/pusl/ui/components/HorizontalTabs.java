package de.bp2019.pusl.ui.components;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.Tabs.Orientation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Horizontal Tab component, to ease use with Vaadin tabs
 * 
 * @author Leon Chemnitz
 */
public class HorizontalTabs<T extends Component> extends VerticalLayout {

    private static final long serialVersionUID = -6745194487461158076L;

    private static final Logger LOGGER = LoggerFactory.getLogger(HorizontalTabs.class);

    private Map<String, T> titleToPages = new HashMap<String, T>();
    private Tabs tabsComponent;
    private Set<T> pages = new HashSet<T>();

    public HorizontalTabs() {
        getStyle().set("margin", "0");

        createTabsComponent();
    }

    /**
     * add one tab to the Tabs
     * 
     * @param title     displayed as the tab title
     * @param component displayed when tab is selected
     * @author Leon Chemnitz
     */
    public void addTab(String title, T component) {
        if (pages.size() > 0) {
            component.setVisible(false);
        }
        Tab tab = new Tab(title);

        tabsComponent.add(tab);
        pages.add(component);
        titleToPages.put(title, component);

        add(component);
    }

    /**
     * Get a page Component based on its title
     * 
     * @param title of Component
     * @return Component
     * 
     * @author Leon Chemnitz
     */
    public T getComponentFromTitle(String title) {
        return titleToPages.get(title);
    }

    /**
     * Delete selected Tab
     * 
     * @author Leon Chemnitz
     */
    public void deleteSelectedTab() {
        var selectedTab = tabsComponent.getSelectedTab();
        LOGGER.debug("deleting tab: " + selectedTab.getLabel());

        T selectedPage = titleToPages.get(selectedTab.getLabel());
        LOGGER.info(selectedPage.toString());
        pages.remove(selectedPage);
        titleToPages.remove(selectedTab.getLabel());
        tabsComponent.remove(selectedTab);
        remove(selectedPage);
    }

    /**
     * Get Number of Tabs
     * 
     * @return number of Tabs
     * 
     * @author Leon Chemnitz
     */
    public int getNumTabs() {
        return pages.size();
    }

    /**
     * Delete all Tabs
     * 
     * @author Leon Chemnitz
     */
    public void deleteAllTabs() {
        titleToPages.clear();
        pages.clear();
        removeAll();
        createTabsComponent();
    }

    private void createTabsComponent() {
        tabsComponent = new Tabs();
        tabsComponent.getStyle().set("margin", "0");
        tabsComponent.setOrientation(Orientation.HORIZONTAL);

        add(tabsComponent);

        tabsComponent.addSelectedChangeListener(event -> {
            var selectedTab = tabsComponent.getSelectedTab();
            LOGGER.debug("changed tab selection to:" + selectedTab.getLabel());

            T selectedPage = titleToPages.get(selectedTab.getLabel());
            if (selectedPage != null) {
                pages.forEach(page -> page.setVisible(false));
                selectedPage.setVisible(true);
            }

        });
    }

    /**
     * Get the title of the selected Tab
     * 
     * @return titlte as a String
     * 
     * @author Leon Chemnitz
     */
    public String getSelectedTabTitle() {
        return tabsComponent.getSelectedTab().getLabel();
    }
}