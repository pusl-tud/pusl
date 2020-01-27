package de.bp2019.zentraldatei.ui.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

import de.bp2019.zentraldatei.util.CustomRequestCache;

/**
 * LoginView used as the Login page for unauthenticated users.
 * 
 * @author Leon Chemnitz
 */
@Route(value = LoginView.ROUTE)
@PageTitle("Login")
public class LoginView extends VerticalLayout {

	private static final long serialVersionUID = -8376096237409998816L;

	public static final String ROUTE = "login";

	private LoginOverlay login = new LoginOverlay();

	@Autowired
	public LoginView(AuthenticationManager authenticationManager, CustomRequestCache requestCache) {
		/* configures login dialog and adds it to the main view */
		login.setOpened(true);
		login.setTitle("Zentraldatei");
		login.setDescription("Managementsystem für studentische Leistungen");

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
				 * if authentication was successful we will update the security context and //
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