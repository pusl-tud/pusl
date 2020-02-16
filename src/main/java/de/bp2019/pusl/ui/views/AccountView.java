/**
 * 
 */
package de.bp2019.pusl.ui.views;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.BindingValidationStatus;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.bp2019.pusl.config.AppConfig;
import de.bp2019.pusl.model.User;
import de.bp2019.pusl.service.UserService;

/**
 * @author Godot_Blend102
 *
 */
@PageTitle(AppConfig.NAME)
@Route(value = AccountView.ROUTE, layout = MainAppView.class)
public class AccountView extends BaseView {
	
	private static final long serialVersionUID = 1L;
	
	public static final String ROUTE = "accountView";
	
	private UserService userService;
	
	private Binder<User> binder;
	
	private ObjectId objectId;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
	public AccountView(UserService userService) {
		super("Account verwalten");
		
		this.userService =userService;
		
		//creating form with its Textfields
		FormLayout form = new FormLayout();
		
		TextField firstNameField = new TextField("Vorname", "Vornamen eingeben");
		firstNameField.setValueChangeMode(ValueChangeMode.EAGER);
		
		TextField lastNameField = new TextField("Nachname", "Nachnamen eingeben");
		lastNameField.setValueChangeMode(ValueChangeMode.EAGER);
		
		TextField emailAddressField = new TextField("Email-Adresse", "Mail-Adresse eingeben");
		emailAddressField.setValueChangeMode(ValueChangeMode.EAGER);
		
		PasswordField passwordField = new PasswordField("Passwort", "Passwort eingeben");
		passwordField.setValueChangeMode(ValueChangeMode.EAGER);
		
		PasswordField confirmPasswordField = new PasswordField("Passwort wiederholen", "Passwort wiederholen");
		confirmPasswordField.setValueChangeMode(ValueChangeMode.EAGER);
		
		
		form.add(firstNameField, 1);
		form.add(lastNameField, 1);
		form.add(emailAddressField, 1);
		form.add(new Label(""), 1);
		form.add(passwordField, 1);
		form.add(confirmPasswordField, 1);
		
		
		//Getting current logged in user to bind to a pusl User instance 
		User currentUser = new User();
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); 
		
		//ToTo: Naiver Ansatz mit Annahme, dass Namen immer nur aus jeweils einen Vornamen und Nachnamen besteht und keine zwei Studenten gleich heissen.
		//Bitte um Verbesserungsvorschläge
		String[] fullName = authentication.getName().split(" ", 2);
		String usersFirstName = fullName[0];
		String usersLastName = fullName[1];
		
		currentUser = getUser(usersFirstName, usersLastName, userService.getAll());
		objectId = currentUser.getId();
		
		binder = new Binder<>();
		binder.setBean(currentUser);
		
		binder.forField(firstNameField).bind(User::getFirstName, User::setFirstName);
		binder.forField(lastNameField).bind(User::getLastName, User::setLastName);
		binder.forField(emailAddressField).
			withValidator(new EmailValidator("Bitte korrekte Mailadresse eingeben")).
				bind(User::getEmailAddress, User::setEmailAddress);
		//TODO: nach Speichern in diesem View oder im EditUserView wird passwort mit encoder überschrieben, jedoch soll hier das nicht encodete Passwort angezeigt werden.
		binder.forField(passwordField)
			.withValidator(new StringLengthValidator(
							"Passwort muss mind. 8 Zeichen lang sein", 8, null))
			.withValidator(passwordString -> 
							passwordString.equals(confirmPasswordField.getValue()), 
													"Passwörter stimmen nicht überein!")
			.bind(user -> {
				confirmPasswordField.setValue("not shown");
				return "not shown";
			}, User::setPassword);

		
		//Creating the Savebutton and adding click listener to it
		Button saveButton = new Button("Änderungen speichern");
		saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		
		
		saveButton.addClickListener(event -> {
			User user = new User();
			Authentication newAuthentication = SecurityContextHolder.getContext().getAuthentication();
            if (binder.writeBeanIfValid(user)) {
            	user.setId(objectId);
            	try {
            		userService.save(user);
            		newAuthentication = new UsernamePasswordAuthenticationToken(firstNameField.getValue() + " " + lastNameField.getValue(),
            																	passwordField.getValue());
            		//SecurityContextHolder.getContext().setAuthentication(newAuthentication);
                    Dialog dialog = new Dialog();
                    dialog.add(new Text("Nutzer erfolgreich überarbeitet! Bestätigen Sie die Änderung indem Sie sich erneut einloggen."));
                    UI.getCurrent().navigate(LoginView.ROUTE);
                    dialog.open();
                    } finally {
                            // TODO: implement ErrorHandeling
                    }
            } else {
                    BinderValidationStatus<User> validate = binder.validate();
                    String errorText = validate.getFieldValidationStatuses().stream()
                                    .filter(BindingValidationStatus::isError)
                                    .map(BindingValidationStatus::getMessage).map(Optional::get).distinct()
                                    .collect(Collectors.joining(", "));
                    LOGGER.debug("There are errors: " + errorText);
            }
		});

		add(form, saveButton);
		
	}

	private User getUser(String firstName, String lastName, List<User> users) {
		
		for(User user:users) {
			if(user.getFirstName().equals(firstName) && user.getLastName().equals(lastName)) {
				return user;
			}
		}
		return new User();
	}

}
