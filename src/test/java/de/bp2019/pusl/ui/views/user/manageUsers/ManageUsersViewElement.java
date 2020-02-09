package de.bp2019.pusl.ui.views.user.manageUsers;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ManageUsersViewElement {

    private WebElement newUserButton;

    public ManageUsersViewElement(WebDriver driver){
        newUserButton = driver.findElement(By.id("new-user"));
    }

    public void clickNewUserButton(){
        newUserButton.click();
    }
}