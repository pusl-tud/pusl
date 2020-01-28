package de.bp2019.pusl.ui.components;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.Tabs.Orientation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Vertical Tab component, to ease use with Vaadin tabs..
 * 
 * @author Leon Chemnitz
 */
public class VerticalTabs extends HorizontalLayout {

    private static final long serialVersionUID = -7178002559265490929L;

    private static final Logger LOGGER = LoggerFactory.getLogger(VerticalTabs.class);

    private Map<String, Component> titleToPages;
    private Tabs tabsComponent;
    private Set<Component> pages;

    public VerticalTabs() {
        getStyle().set("margin-top", "1em");
        tabsComponent = new Tabs();
        tabsComponent.setOrientation(Orientation.VERTICAL);

        pages = new HashSet<Component>();
        titleToPages = new HashMap<String, Component>();

        add(tabsComponent);

        tabsComponent.addSelectedChangeListener(event -> {
            var selectedTab = tabsComponent.getSelectedTab();
            LOGGER.debug("changed tab selcetion to:" + selectedTab.getLabel());

            Component selectedPage = titleToPages.get(selectedTab.getLabel());
            if (selectedPage != null) {
                pages.forEach(page -> page.setVisible(false));
                selectedPage.setVisible(true);
            }

        });
    }

    /**
     * add one tab to the Tabs
     * 
     * @param title displayed as the tab  title
     * @param component displayed when tab is selected
     * @author Leon Chemnitz
     */
    public void addTab(String title, Component component) {
        if (pages.size() > 0) {
            component.setVisible(false);
        }
        Tab tab = new Tab(title);

        tabsComponent.add(tab);
        pages.add(component);
        titleToPages.put(title, component);

        add(component);
    }
}