package de.bp2019.pusl.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import de.bp2019.pusl.ui.views.LoginView;
import de.bp2019.pusl.util.SecurityUtils;

/**
 * Class containing the Spring and vaadin security configuration.
 * 
 * @author Leon Chemnitz
 */
@EnableWebSecurity
@Configuration
@EnableScheduling
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}


	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
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

				/* Restrict access to our application. */
				.authorizeRequests()

				/* Allow all flow internal requests. */
				.requestMatchers(SecurityUtils::isFrameworkInternalRequest).permitAll()

				/* Allow all requests by logged in users. */
				.anyRequest().authenticated()

				/* Configure the login page. */
				.and().formLogin().loginPage("/" + LoginView.ROUTE).permitAll()

				/* Configure logout */
				.and().logout().logoutSuccessUrl("/" + LoginView.ROUTE);
	}

	/**
	 * Allows access to static resources, bypassing Spring security.
	 * 
	 * @author Leon Chemnitz
	 */
	@Override
	public void configure(WebSecurity web) {
		web.ignoring().antMatchers(
				/* Vaadin Flow static resources */
				"/VAADIN/**",

				/* the standard favicon URI */
				"/icons/**",

				/* the robots exclusion standard */
				"/robots.txt",

				/* web application manifest */
				"/manifest.webmanifest", "/sw.js", "/offline-page.html",

				/* (development mode) static resources */
				"/frontend/**",

				/* (development mode) webjars */
				"/webjars/**",

				/* (production mode) static resources */
				"/frontend-es5/**", "/frontend-es6/**",

				"/favicon.ico", "/offline.html", "/images/**", "/styles/**", "/h2-console/**");
	}
}