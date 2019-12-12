package de.bp2019.zentraldatei.config;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;

import org.springframework.stereotype.Component;

import de.bp2019.zentraldatei.util.SecurityUtils;
import de.bp2019.zentraldatei.view.LoginView;

@Component
public class UIServiceInitListenerConfig implements VaadinServiceInitListener { // 

    private static final long serialVersionUID = 1L;

    @Override
    public void serviceInit(ServiceInitEvent event) {
        event.getSource().addUIInitListener(uiEvent -> {
        final UI ui = uiEvent.getUI();
        ui.addBeforeEnterListener(this::beforeEnter); // 
        });
    }

    /**
     * Reroutes the user if (s)he is not authorized to access the view.
     *
     * @param event
     *            before navigation event with event details
     */
    private void beforeEnter(BeforeEnterEvent event) {
        if (!LoginView.class.equals(event.getNavigationTarget()) // 
            && !SecurityUtils.isUserLoggedIn()) { // 
            event.rerouteTo(LoginView.class); // 
        }
    }
}