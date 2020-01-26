package de.bp2019.zentraldatei.UI.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.server.PWA;

import org.springframework.security.core.context.SecurityContextHolder;

import de.bp2019.zentraldatei.UI.views.ExerciseScheme.ManageExerciseSchemesView;
import de.bp2019.zentraldatei.UI.views.Institute.ManageInstitutesView;
import de.bp2019.zentraldatei.UI.views.Module.ManageModulesView;
import de.bp2019.zentraldatei.UI.views.User.ManageUserView;

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
        HorizontalLayout titleLayout = new HorizontalLayout();
        Label title = new Label("Zentraldatei");
        titleLayout.add(title);
        titleLayout.addClickListener(event -> UI.getCurrent().navigate(""));
        addToNavbar(new DrawerToggle(), titleLayout);

        VerticalLayout sidebar = new VerticalLayout();
        sidebar.setHeightFull();
        sidebar.getStyle().set("background-color", "var(--lumo-primary-color)");
        sidebar.setAlignItems(Alignment.AUTO);

        VerticalLayout content = new VerticalLayout();
        content.setSpacing(false);

        content.add(generateSectionLabel("Admin"));
        content.add(generateMenuButton("Veranstaltungen bearbeiten", ManageModulesView.ROUTE));
        content.add(generateMenuButton("Übungsschemas", ManageExerciseSchemesView.ROUTE));
        content.add(generateMenuButton("Benutzer bearbeiten", ManageUserView.ROUTE));
        content.add(generateSeperator());
        content.add(generateSectionLabel("Global"));
        content.add(generateMenuButton("Institute", ManageInstitutesView.ROUTE));
        content.add(generateMenuButton("Datenbank neu befüllen", DemoView.ROUTE));
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
            /* Clear the Spring Authentication */
            SecurityContextHolder.clearContext();
            /* Close the VaadinServiceSession */
            UI.getCurrent().getSession().close();
            /* Redirect to avoid keeping the removed UI open in the browser */
            UI.getCurrent().navigate(LoginView.ROUTE);
        });
        button.getStyle().set("color", "white");

        return button;
    }

    /**
     * @author Leon Chemnitz
     */
    private Label generateSectionLabel(String labelText) {
        Label adminLabel = new Label(labelText);
        adminLabel.getStyle().set("color", "white");
        adminLabel.getStyle().set("font-size", "1.5em");

        return adminLabel;
    }

    private Label generateSeperator(){
        Label separator = new Label(" - ");
        separator.getStyle().set("color", "var(--lumo-primary-color)");
        return separator; 
    }
}