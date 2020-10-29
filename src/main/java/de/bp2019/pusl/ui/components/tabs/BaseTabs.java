package de.bp2019.pusl.ui.components.tabs;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.Tabs.Orientation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseTabs<T extends Component> extends Component implements HasComponents, HasStyle, HasSize {

    private static final long serialVersionUID = 2082824026315528226L;

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseTabs.class);

    private Orientation orientation;
    private Component layout;

    private Map<String, T> titleToPages = new HashMap<String, T>();
    private Map<String, Tab> titleToTabs = new HashMap<String, Tab>();
    private Tabs tabsComponent;
    private Set<T> pages = new HashSet<T>();

    public BaseTabs(Orientation orientation) {
        this.orientation = orientation;

        switch (orientation) {
            case HORIZONTAL:
                layout = new VerticalLayout();
                break;
            case VERTICAL:
                layout = new HorizontalLayout();
                break;
        }

        add(layout);
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
        if (pages.isEmpty()) {
            component.setVisible(true);
        }else {
            component.setVisible(false);
        }
        Tab tab = new Tab(title);

        titleToPages.put(title, component);
        titleToTabs.put(title, tab);
        tabsComponent.add(tab);
        pages.add(component);

        ((HasComponents) layout).add(component); // not pretty and not typesafe but I cant see another way to do this
                                                 // with vaadin and java...
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

    public void removeTab(String title) {
        LOGGER.debug("deleting tab: " + title);

        T page = titleToPages.get(title);
        Tab tab = titleToTabs.get(title);

        pages.remove(page);
        tabsComponent.remove(tab);
        titleToPages.remove(title);
        titleToTabs.remove(title);
        ((HasComponents) layout).remove(page); // not pretty and not typesafe but I cant see another way to do this with
                                               // vaadin and java...
    }

    /**
     * Delete selected Tab
     * 
     * @author Leon Chemnitz
     */
    public void deleteSelectedTab() {
        var selectedTab = tabsComponent.getSelectedTab();
        String title = selectedTab.getLabel();

        removeTab(title);
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
        titleToTabs.clear();
        pages.clear();
        removeAll();
        createTabsComponent();
    }

    private void createTabsComponent() {
        tabsComponent = new Tabs();
        tabsComponent.getStyle().set("margin", "0");
        tabsComponent.setOrientation(orientation);

        add(tabsComponent);

        tabsComponent.addSelectedChangeListener(event -> {
            var selectedTab = tabsComponent.getSelectedTab();

            if (selectedTab != null) {
                LOGGER.debug("changed tab selection to: " + selectedTab.getLabel());
                T selectedPage = titleToPages.get(selectedTab.getLabel());

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