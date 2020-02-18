package de.bp2019.pusl.ui.views;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import de.bp2019.pusl.config.TestProperties;

/**
 * Base UI element to be used with {@link BaseUITest}
 * 
 * @author Leon Chemnitz
 */
public abstract class BaseUIElement {
    protected WebDriver driver;
    protected TestProperties testProperties;

    public BaseUIElement(BaseUITest uiTest){
        this.driver = uiTest.getDriver();
        this.testProperties = uiTest.getProperties();
    }

    protected WebElement findButtonContainingText(String text){
        return driver.findElement(By.xpath("//vaadin-button[contains(text(),'" + text + "')]" ));
    }

    protected WebElement findElementById(String id){
        return driver.findElement(By.id(id));
    }
}