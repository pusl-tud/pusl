package de.bp2019.pusl.ui.views.login;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class LoginViewElement {

    private static final String ADMIN_USERNAME = "user";
    private static final String ADMIN_PASSWORD = "password";

    private WebElement email;
    private WebElement password;
    private WebElement submit;

    public LoginViewElement(WebDriver driver){        
        email = driver.findElement(By.name("username"));
        password = driver.findElement(By.name("password"));
        submit = driver.findElement(By.tagName("vaadin-button"));
    }

    public void login(String email, String password){        
        this.email.sendKeys(email);
        this.password.sendKeys(password);
        submit.click();
    }

    public void loginAdminCredentials(){
        login(ADMIN_USERNAME, ADMIN_PASSWORD);
    }

}