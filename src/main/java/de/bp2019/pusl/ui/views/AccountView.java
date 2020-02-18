/**
 * 
 */
package de.bp2019.pusl.ui.views;

import java.util.Optional;
import java.util.stream.Collectors;

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
import com.vaadin.flow.data.binder.Binder.Binding;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.BindingValidationStatus;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import de.bp2019.pusl.config.PuslProperties;
import de.bp2019.pusl.model.User;
import de.bp2019.pusl.service.UserService;

/**
 * @author Godot_Blend102
 *
 */
@PageTitle(PuslProperties.NAME + " | Mein Account")
@Route(value = AccountView.ROUTE, layout = MainAppView.class)
public class AccountView extends BaseView {

	private static final long serialVersionUID = 4565056621359577147L;

	public static final String ROUTE = "account";

	private Binder<User> binder;

	private User currentUser;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	public AccountView(UserService userService) {
		super("Account verwalten");

		currentUser = userService.getCurrentUser();

		LOGGER.debug("current user: " + currentUser.toString());

		FormLayout form = new FormLayout();

		TextField firstName = new TextField("Vorname", "Vornamen eingeben");
		firstName.setValueChangeMode(ValueChangeMode.EAGER);

		TextField lastName = new TextField("Nachname", "Nachnamen eingeben");
		lastName.setValueChangeMode(ValueChangeMode.EAGER);

		TextField emailAddress = new TextField("Email-Adresse", "Mail-Adresse eingeben");
		emailAddress.setValueChangeMode(ValueChangeMode.EAGER);

		PasswordField password = new PasswordField("Passwort", "Passwort eingeben");
		password.setValueChangeMode(ValueChangeMode.EAGER);

		PasswordField confirmPassword = new PasswordField("Passwort wiederholen", "Passwort wiederholen");
		confirmPassword.setValueChangeMode(ValueChangeMode.EAGER);

		form.add(firstName, 1);
		form.add(lastName, 1);
		form.add(emailAddress, 1);
		form.add(new Label(""), 1);
		form.add(password, 1);
		form.add(confirmPassword, 1);

		binder = new Binder<>();
		binder.setBean(currentUser);

		binder.forField(firstName).withValidator(new StringLengthValidator("Bitte Vorname angeben", 1, null))
				.bind(User::getFirstName, User::setFirstName);

		binder.forField(lastName).withValidator(new StringLengthValidator("Bitte Nachname angeben", 1, null))
				.bind(User::getLastName, User::setLastName);

		binder.forField(emailAddress).withValidator(new EmailValidator("Bitte korrekte Mailadresse eingeben"))
				.bind(User::getEmailAddress, User::setEmailAddress);

		Binding<User, String> passwordBinder = binder.forField(password)
				.withValidator((enteredPassword, valueContext) -> {

					if (enteredPassword == "") {
						/*
						 * User has entered no new password, therefore a new password is not needed
						 */
					} else {
						if (enteredPassword.length() < 8) {
							return ValidationResult.error("Passwort muss länger als 8 Zeichen sein!");
						}

						if (!enteredPassword.equals(confirmPassword.getValue())) {
							return ValidationResult.error("Passwörter stimmen nicht überein!");
						}
					}

					return ValidationResult.ok();
				}).bind(pwd -> "", (user, pwd) -> {
					if (pwd != "") {
						user.setPassword(passwordEncoder.encode(pwd));
					}
				});
		password.addValueChangeListener(e -> passwordBinder.validate());

		Button saveButton = new Button("Änderungen speichern");
		saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

		saveButton.addClickListener(event -> {
			if (binder.writeBeanIfValid(currentUser)) {
				try {
					userService.save(currentUser);

					Dialog dialog = new Dialog();
					dialog.add(new Text("Account erfolgreich angepasst!"));
					UI.getCurrent().navigate(LecturesView.ROUTE);
					dialog.open();
				} finally {
					// TODO: implement ErrorHandeling
				}
			} else {
				BinderValidationStatus<User> validate = binder.validate();
				String errorText = validate.getFieldValidationStatuses().stream()
						.filter(BindingValidationStatus::isError).map(BindingValidationStatus::getMessage)
						.map(Optional::get).distinct().collect(Collectors.joining(", "));
				LOGGER.debug("There are errors: " + errorText);
			}
		});

		add(form, saveButton);

	}

}
