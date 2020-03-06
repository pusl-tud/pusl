package de.bp2019.pusl.config;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.bp2019.pusl.enums.UserType;
import de.bp2019.pusl.service.UserService;
import de.bp2019.pusl.ui.dialogs.ErrorDialog;
import de.bp2019.pusl.ui.interfaces.AccessibleByAdmin;
import de.bp2019.pusl.ui.interfaces.AccessibleBySuperadmin;
import de.bp2019.pusl.ui.views.LecturesView;
import de.bp2019.pusl.ui.views.LoginView;
import de.bp2019.pusl.util.Utils;
import de.bp2019.pusl.util.SecurityUtils;

/**
 * Intercepts URL changes and reroutes if the user tries to access a view which
 * they are unauthorized to access
 * 
 * @author Leon Chemnitz
 */
@Component
public class RouteProtectionConfig implements VaadinServiceInitListener {

    private static final long serialVersionUID = 1L;

    @Autowired
    UserService userService;

    @Override
    public void serviceInit(ServiceInitEvent event) {
        event.getSource().addUIInitListener(uiEvent -> {
            final UI ui = uiEvent.getUI();
            ui.addBeforeEnterListener(this::beforeEnter);
        });
    }

    /**
     * Reroutes the user if they are not authorized to access the view.
     *
     * @param event before navigation event with event details
     * @author Leon Chemnitz
     */
    private void beforeEnter(BeforeEnterEvent event) {
        if (!LoginView.class.equals(event.getNavigationTarget()) && !SecurityUtils.isUserLoggedIn()) {
            /* User is not logged in and not on Login page -> reroute to login page */
            event.rerouteTo(LoginView.class);

        } else if (LoginView.class.equals(event.getNavigationTarget()) && SecurityUtils.isUserLoggedIn()) {
            /* User is logged in tries to access Login page -> reroute to dashboard */
            UI.getCurrent().navigate(LecturesView.class);

        } else if (Utils.implementsInterface(event.getNavigationTarget(), AccessibleByAdmin.class)
                && userService.currentUserType().ordinal() > UserType.ADMIN.ordinal()) {
            /* User is not an admin and tries to access admin pages -> reroute to dashboard */
            event.rerouteTo(LecturesView.class);
            UI.getCurrent().navigate(LecturesView.class);
            ErrorDialog.open("Admin-Berechtigungen sind nötig um auf URL zuzugreifen!");

        } else if (Utils.implementsInterface(event.getNavigationTarget(), AccessibleBySuperadmin.class)
                && userService.currentUserType() != UserType.SUPERADMIN) {
            /* User is not an superadmin and tries to access superadmin pages -> reroute to dashboard */
            event.rerouteTo(LecturesView.class);
            UI.getCurrent().navigate(LecturesView.class);
            ErrorDialog.open("Superadmin-Berechtigungen sind nötig um auf URL zuzugreifen!");
        }
    }
}