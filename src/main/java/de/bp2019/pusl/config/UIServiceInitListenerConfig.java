package de.bp2019.pusl.config;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.bp2019.pusl.enums.UserType;
import de.bp2019.pusl.service.UserService;
import de.bp2019.pusl.ui.interfaces.AccessibleByAdmin;
import de.bp2019.pusl.ui.views.LecturesView;
import de.bp2019.pusl.ui.views.LoginView;
import de.bp2019.pusl.util.ClassUtils;
import de.bp2019.pusl.util.SecurityUtils;

/**
 * Used for vaadin login, taken directly from vaadin website...
 * 
 * @author Leon Chemnitz
 */
@Component
public class UIServiceInitListenerConfig implements VaadinServiceInitListener {

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
     */
    private void beforeEnter(BeforeEnterEvent event) {
        if (!LoginView.class.equals(event.getNavigationTarget()) && !SecurityUtils.isUserLoggedIn()) {
            event.rerouteTo(LoginView.class);
        } else if(LoginView.class.equals(event.getNavigationTarget()) && SecurityUtils.isUserLoggedIn()) {
            UI.getCurrent().navigate(LecturesView.class);
        }else if (ClassUtils.implementsInterface(event.getNavigationTarget(), AccessibleByAdmin.class)
                && userService.getCurrentUserType().ordinal() > UserType.ADMIN.ordinal()) {
            UI.getCurrent().navigate(LecturesView.class);
        }
    }
}