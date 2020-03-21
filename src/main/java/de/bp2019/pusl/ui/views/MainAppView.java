package de.bp2019.pusl.ui.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.server.PWA;

import org.springframework.security.core.context.SecurityContextHolder;

import de.bp2019.pusl.config.PuslProperties;
import de.bp2019.pusl.enums.UserType;
import de.bp2019.pusl.service.UserService;
import de.bp2019.pusl.ui.views.exercisescheme.ManageExerciseSchemesView;
import de.bp2019.pusl.ui.views.institute.ManageInstitutesView;
import de.bp2019.pusl.ui.views.lecture.ManageLecturesView;
import de.bp2019.pusl.ui.views.user.ManageUsersView;
import de.bp2019.pusl.util.Service;

/**
 * Main View used as a overlay for all other Application views (excluding
 * {@link LoginView}). Contains header and Navigation sideBar.
 * 
 * @author Leon Chemnitz
 */
@CssImport("./styles/global.css")
@Viewport("width=device-width, minimum-scale=0.5, initial-scale=1, user-scalable=yes, viewport-fit=cover")
@PWA(name = PuslProperties.NAME, shortName = PuslProperties.NAME)
public class MainAppView extends AppLayout {

    private static final long serialVersionUID = 5473180730294862712L;

    public MainAppView() {
        UserService userService = Service.get(UserService.class);

        HorizontalLayout titleLayout = new HorizontalLayout();
        Label title = new Label(PuslProperties.NAME);
        title.getStyle().set("font-size", "1.2em");
        titleLayout.add(title);
        titleLayout.addClickListener(event -> UI.getCurrent().navigate(""));

        VerticalLayout navbarRight = new VerticalLayout();

        HorizontalLayout userInfo = new HorizontalLayout();
        userInfo.setDefaultVerticalComponentAlignment(Alignment.END);

        userInfo.add(generateUserTypeLabel(userService.currentUserType()));
        userInfo.add(generateUserNameButton(userService.currentUserFullName()));
        userInfo.add(generateLogoutButton());

        navbarRight.getStyle().set("margin", "0");
        navbarRight.getStyle().set("margin-right", "1.5em");
        navbarRight.getStyle().set("padding", "0");
        navbarRight.setWidthFull();
        navbarRight.add(userInfo);
        navbarRight.setHorizontalComponentAlignment(Alignment.END, userInfo);

        addToNavbar(new DrawerToggle(), titleLayout, navbarRight);

        VerticalLayout sidebar = new VerticalLayout();
        sidebar.setHeightFull();
        sidebar.getStyle().set("background-color", "var(--lumo-primary-color)");
        sidebar.setAlignItems(Alignment.AUTO);

        VerticalLayout content = new VerticalLayout();
        content.setSpacing(false);

        content.add(generateMenuButton("Startseite", new Icon(VaadinIcon.HOME), WorkView.ROUTE));

        if(userService.currentUserType() != UserType.HIWI) {
            content.add(generateMenuButton("Noten Export", new Icon(VaadinIcon.DOWNLOAD), ExportView.ROUTE));
        }
        content.add(generateMenuButton("Mein Account", new Icon(VaadinIcon.USER), AccountView.ROUTE));

        if (userService.currentUserType().ordinal() <= UserType.ADMIN.ordinal()) {
            content.add(generateSeperator());
            content.add(generateSectionLabel("Admin"));
            content.add(generateMenuButton("Nutzer", new Icon(VaadinIcon.USERS), ManageUsersView.ROUTE));
            content.add(
                    generateMenuButton("Veranstaltungen", new Icon(VaadinIcon.ACADEMY_CAP), ManageLecturesView.ROUTE));
            content.add(generateMenuButton("Übungsschemas", new Icon(VaadinIcon.NOTEBOOK),
                    ManageExerciseSchemesView.ROUTE));

            if (userService.currentUserType() == UserType.SUPERADMIN) {
                content.add(generateSeperator());
                content.add(generateSectionLabel("Global"));
                content.add(
                        generateMenuButton("Institute", new Icon(VaadinIcon.WORKPLACE), ManageInstitutesView.ROUTE));
                content.add(generateMenuButton("Datenbank", new Icon(VaadinIcon.DATABASE), DatabaseView.ROUTE));
            }
        }

        sidebar.add(content);
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
    private Button generateMenuButton(String buttonText, Icon icon, String url) {
        Button button;

        if (icon != null) {
            button = new Button(buttonText, icon);
        } else {
            button = new Button(buttonText);
        }

        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        button.addClickListener(event -> {
            UI.getCurrent().navigate(url);
        });
        button.getStyle().set("color", "white");
        button.getStyle().set("margin", "0");
        button.getStyle().set("font-weight", "200");
        return button;
    }

    /**
     * @author Leon Chemnitz
     * @return
     */
    private Button generateLogoutButton() {
        Button button = new Button("logout");
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        button.addClickListener(event -> {
            /* Clear the Spring Authentication */
            SecurityContextHolder.clearContext();
            /* Close the VaadinServiceSession */
            UI.getCurrent().getSession().close();
            /* Redirect to avoid keeping the removed UI open in the browser */
            UI.getCurrent().navigate(LoginView.ROUTE);
        });
        button.getStyle().set("font-weight", "200");
        button.getStyle().set("padding-left", "0");

        return button;
    }

    private Label generateUserTypeLabel(UserType type) {
        Label label = new Label(type.toString());
        label.getStyle().set("font-weight", "200");
        label.getStyle().set("font-size", "0.8em");
        label.getStyle().set("padding-right", "0");
        label.getStyle().set("padding-bottom", "0.75em");
        return label;
    }

    private Button generateUserNameButton(String name) {
        Button button = new Button(name);
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        button.getStyle().set("color", "dark-grey");
        button.getStyle().set("padding-right", "0");
        button.getStyle().set("padding-left", "0");

        button.addClickListener(event -> {
            UI.getCurrent().navigate(AccountView.ROUTE);
        });

        return button;
    }

    /**
     * @author Leon Chemnitz
     */
    private Label generateSectionLabel(String labelText) {
        Label adminLabel = new Label(labelText);
        adminLabel.getStyle().set("color", "white");
        adminLabel.getStyle().set("opacity", "0.8");
        adminLabel.getStyle().set("font-size", "1.5em");

        return adminLabel;
    }

    private Label generateSeperator() {
        Label separator = new Label(" - ");
        separator.getStyle().set("color", "var(--lumo-primary-color)");
        return separator;
    }
}