/**
 * 
 */
package de.bp2019.pusl.ui.views;

import java.util.Optional;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.security.core.userdetails.UserDetails;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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

/**
 * Page to edit own Account information
 * 
 * @author Tomoki Tokuyama
 */
@PageTitle(AppConfig.NAME + " | Mein Account")
@Route(value = AccountView.ROUTE, layout = MainAppView.class)
public class AccountView extends BaseView{
	
	private static final long serialVersionUID = 1L;

    public static final String ROUTE = "myAccount";
    
    //TODO: Make a class currentUserService to manage current user(?)
    
    //private UserDetails userDetails;
    
    private Binder<User> binder;
    
    private ObjectId objectId;
    
    public AccountView(UserDetails userDetails) {
    	super("Mein Account");
    	
    	//this.userDetails = userDetails;
    	
    	FormLayout form = new FormLayout();
        form.setResponsiveSteps(new ResponsiveStep("5em", 1), new ResponsiveStep("5em", 2));
        form.setWidth("100%");
        form.getStyle().set("marginLeft", "1em");
        form.getStyle().set("marginTop", "-0.5em");

        binder = new Binder<>();
        
        // Create fields
        
        TextField firstName = new TextField("Vorname", "Vorname eingeben");
        firstName.setValueChangeMode(ValueChangeMode.EAGER);
        
        TextField lastName = new TextField("Nachname", "Nachname eingeben");
        lastName.setValueChangeMode(ValueChangeMode.EAGER);
        
        TextField mailAddress = new TextField("E-Mail", "E-Mailadresse eingeben");
        mailAddress.setValueChangeMode(ValueChangeMode.EAGER);
        
        PasswordField password = new PasswordField("Passwort", "Passwort eingeben");
        password.setValueChangeMode(ValueChangeMode.EAGER);
        
        PasswordField confirmPassword = new PasswordField("Passwort wiederholen", "Passwort erneut eingeben");
        confirmPassword.setValueChangeMode(ValueChangeMode.EAGER);
        
        Button saveButton = new Button("Änderungen speichern");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        
        
        form.add(firstName, 1);
        form.add(lastName, 1);
        form.add(mailAddress, 2);
        form.add(password, 1);
        form.add(confirmPassword, 1);
        
        
        VerticalLayout buttons = new VerticalLayout();
        buttons.add(saveButton);
        
        
        //binding the contents to set the parameters
        
        binder.forField(firstName).withValidator(
                new StringLengthValidator("Bitte Vornamen angeben", 1, null))
                .bind(User::getFirstName, User::setFirstName);
        binder.forField(lastName).withValidator(
                new StringLengthValidator("Bitte Nachnamen angeben", 1, null))
                .bind(User::getLastName, User::setLastName);
        binder.forField(mailAddress).withValidator(new EmailValidator("Ungültige E-Mail Adresse")).bind(User::getEmailAddress, User::setEmailAddress);
        //TODO: binder for pwfield must be linked with the confirmation field
        binder.bind(password, User::getPassword, User::setPassword);
        
        firstName.setRequiredIndicatorVisible(true);
        lastName.setRequiredIndicatorVisible(true);
        mailAddress.setRequiredIndicatorVisible(true);
        
        add(form, buttons);
        
        //Click-Listeners for Save-Button
        saveButton.addClickListener(event -> {
        	User user = new User();
        	if(binder.writeBeanIfValid(user)) {
        		if(objectId != null) {
        			user.setId(objectId);
        		}
        	//userDetails.save(user);
        	Dialog dialog = new Dialog();
        	dialog.add(new Text("Benutzerdaten erfolgreich gespeichert"));
        	dialog.open();
        	} else {
        		BinderValidationStatus<User> validate = binder.validate();
        		String errorText = validate.getFieldValidationStatuses().stream().filter(BindingValidationStatus::isError).map(BindingValidationStatus::getMessage).map(Optional::get).distinct().collect(Collectors.joining(", "));
        		LOGGER.debug("There are errors: " + errorText);
        	}
        });
        
	}
}
