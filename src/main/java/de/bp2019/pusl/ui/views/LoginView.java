package de.bp2019.pusl.ui.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

import de.bp2019.pusl.config.PuslProperties;
import de.bp2019.pusl.util.CustomRequestCache;
import de.bp2019.pusl.util.Service;

/**
 * LoginView used as the Login page for unauthenticated users.
 * 
 * @author Leon Chemnitz
 */
@Route(value = LoginView.ROUTE)
@PageTitle(PuslProperties.NAME + " | Login")
public class LoginView extends VerticalLayout {

	private static final long serialVersionUID = -8376096237409998816L;

	public static final String ROUTE = "login";

	private LoginOverlay login = new LoginOverlay();

	private AuthenticationManager authenticationManager;
    private CustomRequestCache requestCache;

	public LoginView() {

		this.authenticationManager = Service.get(AuthenticationManager.class);
		this.requestCache = Service.get(CustomRequestCache.class);

		/* configures login dialog and adds it to the main view */
		HorizontalLayout titleLayout = new HorizontalLayout();
		Label title = new Label(PuslProperties.NAME);
		title.getStyle().set("font-size", "2.6em");
		titleLayout.add(title);

        Image logo = new Image("images/pusl_logo_large.png", "");
        logo.getStyle().set("margin-left", "0.6em");
        logo.getStyle().set("margin-top", "0.4em");
        logo.setHeight("3.3em");
        titleLayout.add(logo);

		login.setTitle(titleLayout);
		login.setDescription("System für Prüfungen und studentische Leistungen");

		login.setOpened(true);
		add(login);

		login.addLoginListener(e -> {
			try {
				/*
				 * try to authenticate with given credentials, should always return !null or
				 * throw an {@link AuthenticationException}
				 */
				final Authentication authentication = authenticationManager
						.authenticate(new UsernamePasswordAuthenticationToken(e.getUsername(), e.getPassword()));

				/*
				 * if authentication was successful we will update the security context and 
				 * redirect to the page requested first
				 */
				if (authentication != null) {
					login.close();
					SecurityContextHolder.getContext().setAuthentication(authentication);
					UI.getCurrent().navigate(requestCache.resolveRedirectUrl());
				}

			} catch (AuthenticationException ex) {
				/*
				 * show default error message
				 */
				login.setError(true);
			}
		});
	}
}