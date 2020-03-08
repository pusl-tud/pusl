package de.bp2019.pusl.ui.views.user;

import java.util.Optional;
import java.util.stream.Collectors;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
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
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.vaadin.gatanaso.MultiselectComboBox;

import de.bp2019.pusl.config.PuslProperties;
import de.bp2019.pusl.enums.UserType;
import de.bp2019.pusl.model.Institute;
import de.bp2019.pusl.model.User;
import de.bp2019.pusl.service.InstituteService;
import de.bp2019.pusl.service.UserService;
import de.bp2019.pusl.ui.dialogs.ErrorDialog;
import de.bp2019.pusl.ui.dialogs.SuccessDialog;
import de.bp2019.pusl.ui.interfaces.AccessibleByAdmin;
import de.bp2019.pusl.ui.views.BaseView;
import de.bp2019.pusl.ui.views.LecturesView;
import de.bp2019.pusl.ui.views.MainAppView;
import de.bp2019.pusl.util.exceptions.DataNotFoundException;
import de.bp2019.pusl.util.exceptions.UnauthorizedException;

/**
 * View containing a form to edit a {@link User}. Only Accessible by admins and
 * superadmins
 * 
 * @author Leon Chemnitz
 */
@PageTitle(PuslProperties.NAME + " | Nutzer bearbeiten")
@Route(value = EditUserView.ROUTE, layout = MainAppView.class)
public class EditUserView extends BaseView implements HasUrlParameter<String>, AccessibleByAdmin {

        private static final long serialVersionUID = 1L;

        public static final String ROUTE = "admin/user";

        private UserService userService;

        /** Binder to bind the form Data to an Object */
        private Binder<User> binder;

        /** empty if new institute is being created */
        private Optional<ObjectId> userId;

        @Autowired
        PasswordEncoder passwordEncoder;

        @Autowired
        public EditUserView(UserService userService, InstituteService instituteService) {
                super("Nutzer bearbeiten");

                this.userService = userService;

                FormLayout form = new FormLayout();
                form.setResponsiveSteps(new ResponsiveStep("5em", 1), new ResponsiveStep("5em", 2));
                form.setWidth("100%");
                form.getStyle().set("marginLeft", "1em");
                form.getStyle().set("marginTop", "-0.5em");

                binder = new Binder<>();

                /* ########### Create the fields ########### */

                TextField firstName = new TextField();
                firstName.setLabel("Vorname");
                firstName.setPlaceholder("Vorname");
                firstName.setValueChangeMode(ValueChangeMode.EAGER);
                firstName.setId("first-name");
                form.add(firstName, 1);

                TextField lastName = new TextField();
                lastName.setLabel("Nachname");
                lastName.setPlaceholder("Nachname");
                lastName.setValueChangeMode(ValueChangeMode.EAGER);
                lastName.setId("last-name");
                form.add(lastName, 1);

                TextField emailAddress = new TextField();
                emailAddress.setLabel("Email Adresse");
                emailAddress.setPlaceholder("max@mustermann.de");
                emailAddress.setValueChangeMode(ValueChangeMode.EAGER);
                emailAddress.setId("email-address");
                form.add(emailAddress, 1);

                Label emptyText = new Label(" ");
                form.add(emptyText, 1);

                MultiselectComboBox<Institute> institutes = new MultiselectComboBox<Institute>();
                institutes.setLabel("Institute");
                institutes.setDataProvider(instituteService);
                institutes.setItemLabelGenerator(Institute::getName);
                institutes.setId("institutes");
                form.add(institutes, 1);

                Select<UserType> userType = new Select<UserType>();
                userType.setLabel("Nutzer Typ");
                userType.setItems(userService.getUserTypes());
                userType.setId("user-type");
                form.add(userType, 1);

                PasswordField password = new PasswordField();
                password.setLabel("Passwort");
                password.setPlaceholder("Passwort eingeben");
                password.setValueChangeMode(ValueChangeMode.EAGER);
                password.setId("password");
                form.add(password, 1);

                PasswordField repeatPassword = new PasswordField();
                repeatPassword.setLabel("Passwort wiederholen");
                repeatPassword.setPlaceholder("Passwort wiederholen");
                repeatPassword.setValueChangeMode(ValueChangeMode.EAGER);
                repeatPassword.setId("confirm-password");
                form.add(repeatPassword, 1);

                Button save = new Button("Speichern");
                save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

                VerticalLayout actions = new VerticalLayout();
                actions.add(save);
                actions.setHorizontalComponentAlignment(Alignment.END, save);
                form.add(actions, 2);
                
                add(form);

                /* ########### Data Binding and validation ########### */

                binder.forField(firstName).withValidator(new StringLengthValidator("Bitte Vorname angeben", 1, null))
                                .bind(User::getFirstName, User::setFirstName);

                binder.forField(lastName).withValidator(new StringLengthValidator("Bitte Nachname angeben", 1, null))
                                .bind(User::getLastName, User::setLastName);

                binder.forField(emailAddress).withValidator(new EmailValidator("Bitte korrekte Email Addresse angeben"))
                                .bind(User::getEmailAddress, User::setEmailAddress);

                binder.forField(emailAddress).withValidator(new EmailValidator("Bitte korrekte Email Addresse angeben"))
                                .bind(User::getEmailAddress, User::setEmailAddress);

                binder.forField(institutes)
                                .withValidator(selectedInstitutes -> !selectedInstitutes.isEmpty(),
                                                "Bitte mind. ein Institut angeben")
                                .bind(User::getInstitutes, User::setInstitutes);

                binder.forField(userType).withValidator(ut -> ut != null, "Bitte Nutzer Typ wählen").bind(User::getType,
                                User::setType);

                Binding<User, String> passwordBinder = binder.forField(password)
                                .withValidator((enteredPassword, valueContext) -> {

                                        if (userId != null && enteredPassword == "") {
                                                /*
                                                 * User already exists and has entered no new password, therefore a new
                                                 * password is not needed
                                                 */
                                        } else {
                                                if (enteredPassword.length() < 8) {
                                                        return ValidationResult.error(
                                                                        "Passwort muss länger als 8 Zeichen sein!");
                                                }

                                                if (!enteredPassword.equals(repeatPassword.getValue())) {
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
                password.addValueChangeListener(e -> passwordBinder.validate());

                /* ########### Click Listeners for Buttons ########### */

                save.addClickListener(event -> {

                        if (!userService.checkEmailAvailable(emailAddress.getValue(), userId)) {
                                ErrorDialog.open("Email Adresse bereits vergeben");
                                return;
                        }

                        User user = new User();
                        if (binder.writeBeanIfValid(user)) {
                                if (userId.isPresent()) {
                                        user.setId(userId.get());
                                }
                                try {

                                        if (!password.getValue().equals("")) {
                                                user.setPassword(passwordEncoder.encode(password.getValue()));
                                        } else {
                                                String oldPassword = userService.getById(user.getId().toString())
                                                                .getPassword();
                                                user.setPassword(oldPassword);
                                        }

                                        userService.save(user);
                                        UI.getCurrent().navigate(ManageUsersView.ROUTE);
                                        SuccessDialog.open("Nutzer erfolgreich gespeichert");
                                } catch (UnauthorizedException e) {
                                        ErrorDialog.open("nicht authorisiert um Nutzer zu speichern!");
                                } catch (DataNotFoundException e1) {                                        
                                        UI.getCurrent().navigate(ManageUsersView.ROUTE);
                                        ErrorDialog.open("Nutzer wurde nicht in Datenbank gefunden!");
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

        }

        @Override
        public void setParameter(BeforeEvent event, String idParameter) {
                if (idParameter.equals("new")) {
                        userId = Optional.empty();
                        /* clear fields by setting null */
                        binder.readBean(null);
                } else {
                        try {
                                User fetchedUser;
                                fetchedUser = userService.getById(idParameter);                                
                                userId = Optional.of(fetchedUser.getId());
                                binder.readBean(fetchedUser);
                        } catch (UnauthorizedException e) {
                                event.rerouteTo(LecturesView.ROUTE);
                                UI.getCurrent().navigate(LecturesView.ROUTE);      
                                ErrorDialog.open("Nicht authorisiert um Nutzer zu bearbeiten!");
                        } catch (DataNotFoundException e) {                   
                                event.rerouteTo(LecturesView.ROUTE);       
                                UI.getCurrent().navigate(LecturesView.ROUTE); 
                                ErrorDialog.open("Nitzer nicht in Datenbank gefunden!");    
                        }
                }
        }

}