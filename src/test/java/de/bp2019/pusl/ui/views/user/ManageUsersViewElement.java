package de.bp2019.pusl.ui.views.user;

import org.openqa.selenium.WebElement;

import de.bp2019.pusl.ui.views.BaseUIElement;
import de.bp2019.pusl.ui.views.BaseUITest;

public class ManageUsersViewElement extends BaseUIElement {

    private WebElement newUserButton;
    private WebElement adminUserNameButton;

    public ManageUsersViewElement(BaseUITest uiTest) {
        super(uiTest);
        newUserButton = findButtonContainingText("Neuer Nutzer");
        adminUserNameButton = findButtonContainingText(testProperties.getAdminUsername());
    }

    public void clickNewUserButton() {
        newUserButton.click();
    }

    public void clickAdminUserNameButton() {
        adminUserNameButton.click();
    }

    public void clickDeleteButton(String id){
        WebElement deleteButton = findElementById("delete-" + id);
        deleteButton.click();
    }

    public void clickConfirmDeleteButton(){
        WebElement confirmDeleteButton = findButtonContainingText("Löschen");
        confirmDeleteButton.click();
    }
}