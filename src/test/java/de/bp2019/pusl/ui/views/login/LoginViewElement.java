package de.bp2019.pusl.ui.views.login;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import de.bp2019.pusl.ui.views.BaseUITest;
import de.bp2019.pusl.ui.views.BaseUIElement;

public class LoginViewElement extends BaseUIElement{
    private WebElement email;
    private WebElement password;
    private WebElement submit;

    public LoginViewElement(BaseUITest uiTest){
        super(uiTest);
        
        email = driver.findElement(By.name("username"));
        password = driver.findElement(By.name("password"));
        submit = driver.findElement(By.tagName("vaadin-button"));
    }

    public void login(String email, String password){        
        this.email.sendKeys(email);
        this.password.sendKeys(password);
        submit.click();
    }

}