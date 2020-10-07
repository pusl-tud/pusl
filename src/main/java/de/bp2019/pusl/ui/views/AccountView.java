package de.bp2019.pusl.ui.views;

import java.util.Optional;
import java.util.stream.Collectors;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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

import org.springframework.security.crypto.password.PasswordEncoder;

import de.bp2019.pusl.config.PuslProperties;
import de.bp2019.pusl.model.User;
import de.bp2019.pusl.service.AuthenticationService;
import de.bp2019.pusl.service.UserService;
import de.bp2019.pusl.ui.dialogs.ErrorDialog;
import de.bp2019.pusl.ui.dialogs.SuccessDialog;
import de.bp2019.pusl.util.Service;
import de.bp2019.pusl.util.exceptions.DataNotFoundException;
import de.bp2019.pusl.util.exceptions.UnauthorizedException;

/**
 * @author Godot_Blend102, Leon Chemnitz
 *
 */
@PageTitle(PuslProperties.NAME + " | Mein Account")
@Route(value = AccountView.ROUTE, layout = MainAppView.class)
public class AccountView extends BaseView {

	private static final long serialVersionUID = 4565056621359577147L;

	public static final String ROUTE = "account";

	private Binder<User> binder;

	private User currentUser;

	private AuthenticationService authenticationService;
	private UserService userService;
	private PasswordEncoder passwordEncoder;

	public AccountView() {
		super("Account verwalten");

		this.authenticationService = Service.get(AuthenticationService.class);
		this.userService = Service.get(UserService.class);
		this.passwordEncoder = Service.get(PasswordEncoder.class);

		currentUser = authenticationService.currentUser();

		LOGGER.debug("current user: " + currentUser.toString());

		FormLayout form = new FormLayout();

		TextField firstName = new TextField("Vorname", "Vornamen eingeben");
		firstName.setId("firstName");
		firstName.setValueChangeMode(ValueChangeMode.EAGER);

		TextField lastName = new TextField("Nachname", "Nachnamen eingeben");
		lastName.setId("lastName");
		lastName.setValueChangeMode(ValueChangeMode.EAGER);

		TextField emailAddress = new TextField("Email-Adresse", "Mail-Adresse eingeben");
		emailAddress.setId("email");
		emailAddress.setValueChangeMode(ValueChangeMode.EAGER);

		PasswordField password = new PasswordField("Passwort", "Passwort eingeben");
		password.setId("password");
		password.setValueChangeMode(ValueChangeMode.EAGER);

		PasswordField repeatPassword = new PasswordField("Passwort wiederholen", "Passwort wiederholen");
		repeatPassword.setId("confirmPassword");
		repeatPassword.setValueChangeMode(ValueChangeMode.EAGER);

		form.add(firstName, 1);
		form.add(lastName, 1);
		form.add(emailAddress, 1);
		form.add(new Label(""), 1);
		form.add(password, 1);
		form.add(repeatPassword, 1);

		Button save = new Button("Änderungen speichern");
		save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		VerticalLayout actions = new VerticalLayout();
		actions.add(save);
		actions.setHorizontalComponentAlignment(Alignment.END, save);

		form.add(actions, 2);

		add(form);

		/* ########### Data Binding and validation ########### */

		binder = new Binder<>();
		binder.setBean(currentUser);

		binder.forField(firstName).withValidator(new StringLengthValidator("Bitte Vorname angeben", 1, null))
				.bind(User::getFirstName, User::setFirstName);

		binder.forField(lastName).withValidator(new StringLengthValidator("Bitte Nachname angeben", 1, null))
				.bind(User::getLastName, User::setLastName);

		binder.forField(emailAddress).withValidator(new EmailValidator("Bitte korrekte Mailadresse eingeben"))
				.bind(User::getEmailAddress, User::setEmailAddress);


                Binding<User, String> passwordBinder = binder.forField(repeatPassword)
                                .withValidator((enteredPassword, valueContext) -> {

                                        if (password.getValue() == "") {
                                                /*
                                                 * User has entered no new password, therefore a new
                                                 * password is not needed
                                                 */
                                        } else {
                                                if (!enteredPassword.equals(password.getValue())) {
                                                        return ValidationResult
                                                                        .error("Passwörter stimmen nicht überein!");
                                                }
                                        }

                                        return ValidationResult.ok();
                                }).bind(pwd -> "", (user, pwd) -> {
                                        if (pwd != "") {
                                                user.setPassword(passwordEncoder.encode(pwd));
                                        }
                                });

                binder.forField(password).withValidator(enteredPassword -> {
                        if (enteredPassword == "") {
                                return true;
                        } else {
                                passwordBinder.validate();
                                return enteredPassword.length() >= 8;
                        }
                }, "Passwort muss länger als 8 Zeichen sein!").bind(pwd -> "", (user, pwd) -> {
                        return;
                });

		/* ########### Listeners ########### */

		password.addValueChangeListener(e -> passwordBinder.validate());

		save.addClickListener(event -> {

			if (!userService.checkEmailAvailable(emailAddress.getValue(), Optional.of(currentUser.getId()))) {
				ErrorDialog.open("Email Adresse bereits vergeben");
				return;
			}

			if (binder.writeBeanIfValid(currentUser)) {
				try {

					if (!password.getValue().equals("")) {
						currentUser.setPassword(passwordEncoder.encode(password.getValue()));
					} else {
						String oldPassword = userService.getById(currentUser.getId().toString()).getPassword();
						currentUser.setPassword(oldPassword);
					}

					userService.save(currentUser);
					UI.getCurrent().navigate(PuslProperties.ROOT_ROUTE);
					
					if(!emailAddress.getValue().equals(authenticationService.currentUser().getEmailAddress())){
						LOGGER.info("deauthenticating user because email address of user changed");
						authenticationService.deauthenticate();
					} else{
						authenticationService.clearCache();						
						UI.getCurrent().getPage().reload();
					}
					
					SuccessDialog.open("Account erfolgreich angepasst");
				} catch (UnauthorizedException e) {
					ErrorDialog.open("nicht authorisiert um Nutzer zu speichern!");
				} catch (DataNotFoundException e1) {
					UI.getCurrent().navigate(PuslProperties.ROOT_ROUTE);
					ErrorDialog.open("Nutzer wurde nicht in Datenbank gefunden!");
				}
			} else {
				BinderValidationStatus<User> validate = binder.validate();
				String errorText = validate.getFieldValidationStatuses().stream()
						.filter(BindingValidationStatus::isError).map(BindingValidationStatus::getMessage)
						.map(Optional::get).distinct().collect(Collectors.joining(", "));
				LOGGER.debug("There are errors: " + errorText);
			}
		});

	}

}
