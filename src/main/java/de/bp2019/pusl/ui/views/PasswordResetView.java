package de.bp2019.pusl.ui.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.bp2019.pusl.config.PuslProperties;
import de.bp2019.pusl.service.PasswordResetService;
import de.bp2019.pusl.ui.dialogs.ErrorDialog;
import de.bp2019.pusl.ui.dialogs.SuccessDialog;
import de.bp2019.pusl.util.Service;

/**
 * 
 * @author Leon Chemnitz
 */
@Route(value = PasswordResetView.ROUTE)
@PageTitle(PuslProperties.NAME + " | Passwort reset")
public class PasswordResetView extends VerticalLayout {

    private static final long serialVersionUID = -1900517267707266430L;
    public static final String ROUTE = "reset";

    private PasswordResetService passwordResetService;

    public PasswordResetView() {
        this.passwordResetService = Service.get(PasswordResetService.class);

        getStyle().set("margin-left", "2em");

        Label titleLabel = new Label("Passwort zurücksetzten");
        titleLabel.getStyle().set("font-size", "2em");
        add(titleLabel);

        FormLayout activationLayout = new FormLayout();
        activationLayout.setWidth("30em");
        activationLayout.setResponsiveSteps(new ResponsiveStep("5em", 3));

        EmailField emailField = new EmailField("E-mail addresse", "max@mustermann.de");
        emailField.setId("email");
        emailField.setValueChangeMode(ValueChangeMode.EAGER);
        activationLayout.add(emailField, 2);

        Button sendButton = new Button("Code senden");
        sendButton.setEnabled(false);
        activationLayout.add(sendButton, 1);

        TextField activationCodeField = new TextField("Aktivierungscode", "XXXX-XXXX-XXXX-XXXX");
        activationCodeField.setId("activation");
        activationCodeField.setValueChangeMode(ValueChangeMode.EAGER);
        activationLayout.add(activationCodeField, 3);

        activationLayout.add(new Label(), 2);

        Button nextButton = new Button("weiter");
        nextButton.setEnabled(false);
        nextButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        activationLayout.add(nextButton);

        add(activationLayout);

        FormLayout resetLayout = new FormLayout();
        resetLayout.setWidth("30em");
        resetLayout.setResponsiveSteps(new ResponsiveStep("5em", 2));
        resetLayout.setVisible(false);

        Label resetMessage = new Label("Setzten Sie ein neues Passwort");
        resetLayout.add(resetMessage, 2);

        PasswordField passwordField = new PasswordField("Passwort");        
        passwordField.setErrorMessage("Passwort muss länger als 8 Zeichen sein!");
        resetLayout.add(passwordField, 2);

        PasswordField repeatPasswordField = new PasswordField("Passwort wiederholen");        
        repeatPasswordField.setErrorMessage("Passwörter stimmen nicht überein");
        resetLayout.add(repeatPasswordField, 2);

        resetLayout.add(new Label(), 1);

        Button changePasswordButton = new Button("Speichern");
        changePasswordButton.setEnabled(false);
        changePasswordButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        resetLayout.add(changePasswordButton, 1);

        add(resetLayout);

        /* ######### LISTENERS ######### */

        emailField.addValueChangeListener(e -> {
            sendButton.setEnabled(!emailField.isInvalid());

            if (!activationCodeField.isEmpty() && !emailField.isInvalid()) {
                nextButton.setEnabled(true);
            } else {
                nextButton.setEnabled(false);
            }
        });

        activationCodeField.addValueChangeListener(e -> {
            if (!activationCodeField.isEmpty() && !emailField.isInvalid()) {
                nextButton.setEnabled(true);
            } else {
                nextButton.setEnabled(false);
            }
        });

        sendButton.addClickListener(e -> {
            passwordResetService.sendActivationCode(emailField.getValue());
            SuccessDialog.open("Aktivierungscode wurde verschickt. Der Aktivierungscode ist 5 Minuten gültig.");
        });

        nextButton.addClickListener(e -> {
            if (passwordResetService.isTokenValid(emailField.getValue(), activationCodeField.getValue())) {
                activationLayout.setVisible(false);
                resetLayout.setVisible(true);
            } else {
                ErrorDialog.open("Ungültiger Aktivierungscode");
            }
        });

        passwordField.addValueChangeListener(e -> {
            if (passwordField.getValue().length() < 8) {
                changePasswordButton.setEnabled(false);
                passwordField.setInvalid(true);
            } else if (!passwordField.getValue().equals(repeatPasswordField.getValue())) {
                changePasswordButton.setEnabled(false);
                
                passwordField.setInvalid(false);
                repeatPasswordField.setInvalid(true);
            } else {
                changePasswordButton.setEnabled(true);

                passwordField.setInvalid(false);
                repeatPasswordField.setInvalid(false);
            }
        });

        repeatPasswordField.addValueChangeListener(e -> {
            if (passwordField.getValue().equals(repeatPasswordField.getValue())) {
                changePasswordButton.setEnabled(true);

                repeatPasswordField.setInvalid(false);
            } else {
                changePasswordButton.setEnabled(false);

                repeatPasswordField.setInvalid(true);
            }
        });

        changePasswordButton.addClickListener(e -> {
            passwordResetService.setNewPassword(emailField.getValue(), passwordField.getValue(),
                    activationCodeField.getValue());
        });
    }

}
