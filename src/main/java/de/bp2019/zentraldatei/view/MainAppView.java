package de.bp2019.zentraldatei.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.server.PWA;

/**
 * Main View used as a overlay for all other Application views (excluding
 * LoginView). Contains header and Navigation sideBar.
 * 
 * @author Leon Chemnitz
 */
@CssImport("./styles/global.css")
@Viewport("width=device-width, minimum-scale=0.5, initial-scale=1, user-scalable=yes, viewport-fit=cover")
@PWA(name = "Zentraldatei", shortName = "Zentraldatei")
public class MainAppView extends AppLayout {

    private static final long serialVersionUID = 1L;

    public MainAppView() {
        Label title = new Label("Zentraldatei");
        addToNavbar(new DrawerToggle(), title);
        VerticalLayout sidebar = new VerticalLayout();
        sidebar.setHeightFull();
        sidebar.getStyle().set("background-color", "var(--lumo-primary-color)");
        sidebar.setAlignItems(Alignment.AUTO);

        VerticalLayout content = new VerticalLayout();
        content.setSpacing(false);

        content.add(generateAdminLabel());
        content.add(generateMenuButton("Veranstaltungsschemas", "moduleSchemes"));
        content.add(generateMenuButton("Übungsschemas", "exerciseSchemes"));
        content.add(generateMenuButton("Datenbank neu befüllen", "demo"));
        sidebar.add(content);

        VerticalLayout footer = new VerticalLayout();
        footer.add(generateLogoutButton());
        sidebar.add(footer);
        footer.setAlignSelf(Alignment.END);

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
        button.getStyle().set("color", "white");
        return button;
    }

    /**
     * @author Leon Chemnitz
     * @return
     */
    private Button generateLogoutButton() {
        Button button = new Button("logout", new Icon(VaadinIcon.KEY));
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        button.addClickListener(event -> {
            /* Close the VaadinServiceSession */
            UI.getCurrent().getSession().close();

            /*
             * Redirect to avoid keeping the removed UI open in the browser
             */
            UI.getCurrent().navigate("login");
        });
        button.getStyle().set("color", "white");

        return button;
    }

    /**
     * @author Leon Chemnitz
     */
    private Label generateAdminLabel() {
        Label adminLabel = new Label("Admin");
        adminLabel.getStyle().set("color", "white");
        adminLabel.getStyle().set("font-size", "1.5em");

        return adminLabel;
    }
}