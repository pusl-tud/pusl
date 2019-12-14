package de.bp2019.zentraldatei.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import de.bp2019.zentraldatei.util.CustomRequestCache;
import de.bp2019.zentraldatei.util.SecurityUtils;
import de.bp2019.zentraldatei.view.LoginView;

/**
 * Class containing the Spring and vaadin security configuration. most of it is
 * taken straight from the vaadin website.
 * 
 * Once rolebased authentication is implemented a lot will be changed here, so
 * nothing is final...
 * 
 * @author Leon Chemnitz
 */
@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	private static final String LOGOUT_SUCCESS_URL = "/login";

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	public CustomRequestCache requestCache() {
		return new CustomRequestCache();
	}

	/**
	 * Require login to access internal pages and configure login form.
	 * 
	 * @author Leon Chemnitz
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		/* Not using Spring CSRF here to be able to use plain HTML for the login page */
		http.csrf().disable()

				/*
				 * Register our CustomRequestCache, that saves unauthorized access attempts, so
				 * the user is redirected after login.
				 */
				.requestCache().requestCache(requestCache())

				/* Restrict access to our application. */
				.and().authorizeRequests()

				/* Allow all flow internal requests. */
				.requestMatchers(SecurityUtils::isFrameworkInternalRequest).permitAll()

				/* Allow all requests by logged in users. */
				.anyRequest().authenticated()

				/* Configure the login page. */
				.and().formLogin().loginPage("/" + LoginView.ROUTE).permitAll()

				/* Configure logout */
				.and().logout().logoutSuccessUrl(LOGOUT_SUCCESS_URL);
	}

	@Bean
	@Override
	public UserDetailsService userDetailsService() {
		UserDetails user = User.withUsername("user").password("{noop}password").roles("USER").build();

		return new InMemoryUserDetailsManager(user);
	}

	/**
	 * Allows access to static resources, bypassing Spring security.
	 */
	@Override
	public void configure(WebSecurity web) {
		web.ignoring().antMatchers(
				/* Vaadin Flow static resources */
				"/VAADIN/**",

				/* the standard favicon URI */
				"/favicon.ico",

				/* the robots exclusion standard */
				"/robots.txt",

				/* web application manifest */
				"/manifest.webmanifest", "/sw.js", "/offline-page.html",

				/* (development mode) static resources */
				"/frontend/**",

				/* (development mode) webjars */
				"/webjars/**",

				/* (production mode) static resources */
				"/frontend-es5/**", "/frontend-es6/**");
	}
}