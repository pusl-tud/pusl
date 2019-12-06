package de.bp2019.zentraldatei.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.server.PWA;

/**
 * Main View used as a overlay for all other Application views (excluding
 * LoginView). Contains header and Navigation sideBar.
 * 
 * @author Leon Chemnitz
 */
@Viewport("width=device-width, minimum-scale=0.5, initial-scale=1, user-scalable=yes, viewport-fit=cover")
@PWA(name = "Zentraldatei", shortName = "Zentraldatei")
class MainAppView extends AppLayout {

    private static final long serialVersionUID = 1L;

    public MainAppView() {
        Label title = new Label("Zentraldatei");
        addToNavbar(new DrawerToggle(), title);
        VerticalLayout sidebar = new VerticalLayout();
        sidebar.add(generateMenuButton("Veranstaltungsschemas", "moduleSchemes"));
        sidebar.add(generateMenuButton("Datenbank neu befüllen", "demo"));
        addToDrawer(sidebar);
    }

    /**
     * Used to generate LinkButtons for the navigation SideBar
     * 
     * @param buttonText text to display on the button
     * @param url        Url to route to
     * 
     * @author Leon Chemnitz
     */
    private Button generateMenuButton(String buttonText, String url) {
        Button button = new Button(buttonText);
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        button.addClickListener(event -> {
            UI.getCurrent().navigate(url);
        });
        return button;
    }
}