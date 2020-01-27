package de.bp2019.zentraldatei.UI.views.user;

import java.util.Optional;
import java.util.stream.Collectors;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.BindingValidationStatus;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.vaadin.gatanaso.MultiselectComboBox;

import de.bp2019.zentraldatei.enums.UserType;
import de.bp2019.zentraldatei.model.Institute;
import de.bp2019.zentraldatei.model.User;
import de.bp2019.zentraldatei.service.InstituteService;
import de.bp2019.zentraldatei.service.UserService;
import de.bp2019.zentraldatei.UI.views.BaseView;
import de.bp2019.zentraldatei.UI.views.MainAppView;

/**
 * View containing a form to edit a User
 * 
 * @author Leon Chemnitz
 */
@PageTitle("Zentraldatei | Nutzer bearbeiten")
@Route(value = EditUserView.ROUTE, layout = MainAppView.class)
public class EditUserView extends BaseView implements HasUrlParameter<String> {

        private static final long serialVersionUID = 1L;

        public static final String ROUTE = "edit-user";

        private static final Logger LOGGER = LoggerFactory.getLogger(EditUserView.class);

        /*
         * no @Autowire because service is injected by constructor. Vaadin likes it
         * better this way...
         */
        private UserService userService;

        /** Binder to bind the form Data to an Object */
        private Binder<User> binder;

        /**
         * null if a new User is being created
         */
        private ObjectId objectId;

        @Autowired
        PasswordEncoder passwordEncoder;

        @Autowired
        public EditUserView(UserService userService, InstituteService instituteService) {
                super("Nutzer bearbeiten");

                this.userService = userService;

                LOGGER.debug("Started creation of UserView");

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
                form.add(firstName, 1);

                TextField lastName = new TextField();
                lastName.setLabel("Nachname");
                lastName.setPlaceholder("Nachname");
                lastName.setValueChangeMode(ValueChangeMode.EAGER);
                form.add(lastName, 1);

                TextField emailAddress = new TextField();
                emailAddress.setLabel("Email Adresse");
                emailAddress.setPlaceholder("max@mustermann.de");
                emailAddress.setValueChangeMode(ValueChangeMode.EAGER);
                form.add(emailAddress, 1);

                Label emptyText = new Label(" ");
                form.add(emptyText, 1);

                MultiselectComboBox<Institute> institutes = new MultiselectComboBox<Institute>();
                institutes.setLabel("Institute");
                institutes.setItems(instituteService.getAllInstitutes());
                institutes.setItemLabelGenerator(item -> item.getName());
                form.add(institutes, 1);

                Select<UserType> userType = new Select<UserType>();
                userType.setLabel("Nutzer Typ");
                userType.setItems(userService.getUserTypes());
                form.add(userType, 1);

                PasswordField password = new PasswordField();
                password.setLabel("Passwort");
                password.setPlaceholder("Passwort eingeben");
                password.setValueChangeMode(ValueChangeMode.EAGER);
                form.add(password, 1);

                PasswordField repeatPassword = new PasswordField();
                repeatPassword.setLabel("Passwort wiederholen");
                repeatPassword.setPlaceholder("Passwort wiederholen");
                repeatPassword.setValueChangeMode(ValueChangeMode.EAGER);
                form.add(repeatPassword, 1);

                Button save = new Button("Speichern");
                save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

                VerticalLayout actions = new VerticalLayout();
                actions.add(save);
                actions.setHorizontalComponentAlignment(Alignment.END, save);
                form.add(actions, 2);

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

                binder.forField(password)
                                .withValidator(new StringLengthValidator("Muss mindestens 8 Zeichen lang sein", 8,
                                                null))
                                .withValidator(passwordString -> passwordString.equals(repeatPassword.getValue()),
                                                "Passwörter stimmen nicht überein!")
                                .bind(user -> {
                                        repeatPassword.setValue("not shown");
                                        return "not shown";
                                }, (user, passwordString) -> user.setPassword(passwordEncoder.encode(passwordString)));

                /* ########### Click Listeners for Buttons ########### */

                save.addClickListener(event -> {
                        User user = new User();
                        if (binder.writeBeanIfValid(user)) {
                                if (objectId != null) {
                                        user.setId(objectId);
                                }
                                try {
                                        userService.save(user);
                                        Dialog dialog = new Dialog();
                                        dialog.add(new Text("Nutzer erfolgreich gespeichert"));
                                        UI.getCurrent().navigate(ManageUsersView.ROUTE);
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

                /* ########### Add Layout to Component ########### */

                add(form);
                LOGGER.debug("Finished creation of UserView");
        }

        @Override
        public void setParameter(BeforeEvent event, String userId) {
                if (userId.equals("new")) {
                        objectId = null;
                        /* clear fields by setting null */
                        binder.readBean(null);
                } else {
                        User fetchedUser = userService.getUserById(userId);
                        /* getUserById returns null if no matching User is found */
                        if (fetchedUser == null) {
                                throw new NotFoundException();
                        } else {
                                objectId = fetchedUser.getId();
                                binder.readBean(fetchedUser);
                        }
                }
        }

}