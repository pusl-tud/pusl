package de.bp2019.pusl.config;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.browserstack.local.Local;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;

import de.bp2019.pusl.enums.UserType;
import de.bp2019.pusl.model.User;
import de.bp2019.pusl.repository.ExerciseSchemeRepository;
import de.bp2019.pusl.repository.GradeRepository;
import de.bp2019.pusl.repository.InstituteRepository;
import de.bp2019.pusl.repository.LectureRepository;
import de.bp2019.pusl.repository.UserRepository;
import de.bp2019.pusl.ui.dialogs.ConfirmDeletionDialog;
import de.bp2019.pusl.ui.views.LoginView;

/**
 * Base Class for UI tests. Starts Webdriver and fills database with one
 * {@link User} of each {@link UserType}. Also contains some utility functions.
 * Very Expensive! If you can test something with a Unit-Test or
 * Integreation-Test instead, do that! If something is unclear try searching for
 * "selenium tutorial" on the internet.
 * 
 * @author Leon Chemnitz
 */
@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class BaseUIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseUIT.class);

    protected WebDriver driver;
    private static Local bsLocal;

    @LocalServerPort
    int port;

    @Autowired
    protected TestUtils testUtils;

    @Autowired
    protected TestProperties testProperties;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected InstituteRepository instituteRepository;

    @Autowired
    protected LectureRepository lectureRepository;

    @Autowired
    protected ExerciseSchemeRepository exerciseSchemeRepository;

    @Autowired
    protected GradeRepository gradeRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    protected String baseUrl;
    protected WebDriverWait wait;

    /**
     * Starts Webdriver and connects to Browserstack
     * 
     * @throws Exception
     * @author Leon Chemnitz
     */
    @BeforeAll
    public void setupTestSuite() throws Exception {
        LOGGER.info("Starting BSLocal");
        bsLocal = new Local();

        String BROWSERSTACK_USERNAME = System.getenv("BROWSERSTACK_USERNAME");
        if (BROWSERSTACK_USERNAME == null) {
            LOGGER.error("No environment variable set for BROWSERSTACK_USERNAME!");
        }

        String BROWSERSTACK_ACCESS_KEY = System.getenv("BROWSERSTACK_ACCESS_KEY");
        if (BROWSERSTACK_ACCESS_KEY == null) {
            LOGGER.error("No environment variable set for BROWSERSTACK_ACCESS_KEY!");
        }

        HashMap<String, String> bsLocalArgs = new HashMap<String, String>();
        bsLocalArgs.put("key", BROWSERSTACK_ACCESS_KEY);

        bsLocal.start(bsLocalArgs);

        String URL = "https://" + BROWSERSTACK_USERNAME + ":" + BROWSERSTACK_ACCESS_KEY
                + "@hub-cloud.browserstack.com/wd/hub";

        // Input capabilities
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("browserstack.local", "true");
        caps.setCapability("browserstack.local", "true");
        caps.setCapability("os", "Windows");
        caps.setCapability("os_version", "10");
        caps.setCapability("browser", "Chrome");
        caps.setCapability("browser_version", "80");
        caps.setCapability("resolution", "1920x1080");
        caps.setCapability("name", this.getClass().getSimpleName());

        driver = new RemoteWebDriver(new URL(URL), caps);
        driver.manage().window().maximize();
    }

    @AfterAll
    public void tearDownTestSuite() throws Exception {

        if (driver != null) {
            LOGGER.info("Stopping Chromedriver");
            driver.quit();
        }

        LOGGER.info("Stopping BSLocal");

        bsLocal.stop();
    }

    /**
     * Initializes TestDatabase and navigates to base URL
     * 
     * @author Leon Chemnitz
     */
    @BeforeEach
    public void setUpTest() {
        LOGGER.info("setting Test environment");

        baseUrl = testProperties.getBaseUrl() + port + "/";

        userRepository.deleteAll();
        instituteRepository.deleteAll();
        exerciseSchemeRepository.deleteAll();
        lectureRepository.deleteAll();
        gradeRepository.deleteAll();

        driver.get(baseUrl);

        wait = new WebDriverWait(driver, 10);
        waitForPageload();
    }

    /**
     * Cleans everything up again
     * 
     * @throws Exception
     * @author Leon Chemnitz
     */
    @AfterEach
    public void tearDownTest() throws Exception {
        logout();
        userRepository.deleteAll();
        instituteRepository.deleteAll();
        exerciseSchemeRepository.deleteAll();
        lectureRepository.deleteAll();
        gradeRepository.deleteAll();
    }

    /**
     * Explicit wait, but only works the first time the application is started,
     * since vaadin creates a SPA
     * 
     * @author Leon Chemnitz
     */
    protected void waitForPageload() {
        ExpectedCondition<Boolean> expectation = new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return ((JavascriptExecutor) driver).executeScript("return document.readyState").toString()
                        .equals("complete");
            }
        };

        wait.until(expectation);
        wait.until(ExpectedConditions.elementToBeClickable(findElementById("pwa-closeip")));
        findElementById("pwa-closeip").click();
    }

    /**
     * Use this as confirmation that a redirect took place
     * 
     * @param url
     * @throws InterruptedException
     * @author Leon Chemnitz
     */
    protected void waitForURL(String url) throws InterruptedException {
        wait.until(ExpectedConditions.urlToBe(baseUrl + url));
    }

    /**
     * Use this as a confirmation that a redirect didn't took play
     * 
     * @param url
     * @author Leon Chemnitz
     */
    protected void timeoutWrongURL(String url) {
        assertThrows(TimeoutException.class, () -> waitForURL(url));
    }

    /**
     * @author Leon Chemnitz
     */
    protected void goToURL(String url) {
        LOGGER.info("going to URL " + baseUrl + url);
        try {
            driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
            driver.navigate().to(baseUrl + url);
            waitForURL(url);
            waitForPageload();
        } catch (Exception e) {
            assertTrue(e.toString(), false);
        }
    }

    /**
     * @author Leon Chemnitz
     */
    protected void goToURLandWaitForRedirect(String url, String RedirectUrl) {
        try {
            driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
            driver.navigate().to(baseUrl + url);
            waitForURL(RedirectUrl);
            waitForPageload();
        } catch (Exception e) {
            assertTrue(e.toString(), false);
        }
    }

    /**
     * Used during login
     * 
     * @throws InterruptedException
     * @author Leon Chemnitz
     */
    protected void waitForLoginRedirect() throws InterruptedException {
        driver.navigate().to(baseUrl);
        waitForURL(LoginView.ROUTE);
    }

    /**
     * Create User of given type and login to the application
     * 
     * @param userType
     * @throws Exception
     * @author Leon Chemnitz
     */
    protected User login(UserType userType) throws Exception {
        LOGGER.info("Logging in as " + userType.toString());

        String password = RandomStringUtils.randomAlphanumeric(14);
        User user = testUtils.createUser(userType, password);

        waitForLoginRedirect();

        findElementByName("username").sendKeys(user.getEmailAddress());
        findElementByName("password").sendKeys(password);

        findButtonContainingText("Log in").click();

        waitForURL(PuslProperties.ROOT_ROUTE);

        try{
            wait.until(ExpectedConditions.elementToBeClickable(findElementById("pwa-closeip")));
            findElementById("pwa-closeip").click();
        } catch(Exception e){
            //eine Super Lösung <3
        }

        return user;
    }

    /**
     * Login to application with given user. user must have been created with
     * {@link TestUtils::createUser}
     * 
     * @param user
     * @return
     * @throws InterruptedException
     */
    protected User login(User user) throws InterruptedException {
        LOGGER.info("Logging in as " + user.toString());
        waitForLoginRedirect();

        String password = testUtils.getPasswordOfUser(user);

        findElementByName("username").sendKeys(user.getEmailAddress());
        findElementByName("password").sendKeys(password);

        findButtonContainingText("Log in").click();

        waitForURL(PuslProperties.ROOT_ROUTE);

        try{
            wait.until(ExpectedConditions.elementToBeClickable(findElementById("pwa-closeip")));
            findElementById("pwa-closeip").click();
        } catch(Exception e){
            //eine Super Lösung <3
        }

        return user;
    }

    /**
     * @author Leon Chemnitz
     * @throws InterruptedException
     */
    protected void logout() throws InterruptedException {
        if (!driver.getCurrentUrl().equals(baseUrl + "login")) {
            LOGGER.info("logging out");
            goToURL(PuslProperties.ROOT_ROUTE);
            findButtonContainingText("logout").click();
            waitForURL(LoginView.ROUTE);
        }
    }

    /**
     * Get {@link WebElement} of a button, based on the text displayed on the button
     * 
     * @param text
     * @return
     * @author Leon Chemnitz
     */
    protected WebElement findButtonContainingText(String text) {
        return driver.findElement(By.xpath("//vaadin-button[contains(text(),'" + text + "')]"));
    }

    /**
     * Get {@link WebElement} based on its CSS id
     * 
     * @param id
     * @return
     * @author Leon Chemnitz
     */
    protected WebElement findElementById(String id) {
        return driver.findElement(By.id(id));
    }

    /**
     * Get {@link WebElement} based on its CSS name
     * 
     * @param name
     * @return
     * @author Leon Chemnitz
     */
    protected WebElement findElementByName(String name) {
        return driver.findElement(By.name(name));
    }

    protected void findSelectByIdAndSelectByText(String id, String selectionText) {
        WebElement element = driver.findElement(By.xpath("//vaadin-select[@id='" + id + "']"));

        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].shadowRoot.querySelector('vaadin-select-text-field').click()", element);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//vaadin-select-overlay")));
        driver.findElement(By.xpath("//vaadin-select-overlay//vaadin-item[text()='" + selectionText + "']")).click();

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//vaadin-select-overlay")));
    }

    /**
     * Find a Multiselect-combo-box by its CSS ID and select its fields based on a
     * List of texts
     * 
     * MultiselectComboBox is the absolute worst for testing...
     * 
     * @param id
     * @param textList
     * @author Leon Chemnitz
     * @throws InterruptedException
     */
    protected void findMSCBByIdAndSelectByTexts(String id, List<String> textList) throws InterruptedException {
        driver.findElement(By.xpath("//multiselect-combo-box[@id='" + id + "']")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//vaadin-combo-box-overlay")));

        WebElement baseElement = driver.findElement(By.xpath("//vaadin-combo-box-overlay"));

        Object rawListItems = ((JavascriptExecutor) driver).executeScript(
            "return arguments[0].shadowRoot.querySelector('#content').shadowRoot.querySelectorAll('vaadin-combo-box-item')",
            baseElement);

        if(!(rawListItems instanceof List<?>)){
            throw new IllegalStateException();
        }

        @SuppressWarnings("unchecked")
        List<WebElement> listItems = (List<WebElement>) rawListItems;

        textList.forEach(selectionText -> {
            for (WebElement element : listItems) {
                try {
                    if (element.getText().equals(selectionText)) {
                        WebElement el = (WebElement) ((JavascriptExecutor) driver)
                                .executeScript("return arguments[0].shadowRoot.querySelector('span')", element);
                        wait.until(ExpectedConditions.elementToBeClickable(el));
                        el.click();
                    }
                } catch (Exception e) {
                    continue;
                }
            }

        });
        driver.findElement(By.xpath("//multiselect-combo-box[@id='" + id + "']")).sendKeys(Keys.ENTER);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//vaadin-combo-box-overlay")));
    }

    protected void sendShortcut(CharSequence... keysToSend) {
        driver.findElement(By.tagName("body")).sendKeys(keysToSend);
    }

    /**
     * Find vaadin Password Field based on its CSS id
     * 
     * @param id
     * @return
     * @author Leon Chemnitz
     */
    protected WebElement findPasswordFieldById(String id) {
        return driver.findElement(By.xpath("//vaadin-password-field[@id='" + id + "']"));
    }

    /**
     * Clear a Vaadin TextField based on its CSS id Sorry for the JS but Vaadin made
     * me do it...
     * 
     * @param id
     * @author Leon Chemnitz
     */
    protected void clearFieldById(String id) {
        WebElement element = findElementById(id);

        ((JavascriptExecutor) driver).executeScript("arguments[0].shadowRoot.querySelector('input').value = ''",
                element);
    }

    /**
     * Wait until a Dialog pops up containing dialogText
     * 
     * @param dialogText
     * @author Leon Chemnitz
     */
    protected void waitUntilDialogVisible(String dialogText) {
        wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.xpath("//div[contains(text(),'" + dialogText + "')]")));
    }

    /**
     * Accept {@link ConfirmDeletionDialog} with given confirmationString
     * 
     * @param confirmationString
     * @author Leon Chemnitz
     */
    protected void acceptConfirmDeletionDialog(String confirmationString) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//vaadin-dialog-overlay")));

        findElementById("confirm-deletion-text-field").sendKeys(confirmationString);
        findButtonContainingText("Löschen").click();

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("confirm-deletion-dialog")));
    }

    /**
     * Expand ShadowDom of an Element
     * 
     * @param element
     * @return
     * @author Leon Chemnitz
     */
    protected WebElement expandShadowDOM(WebElement element) {
        return ((WebElement) ((JavascriptExecutor) driver).executeScript("return arguments[0].shadowRoot", element));
    }

    /**
     * Force the driver to wait
     * 
     * @param seconds
     * @author Leon Chemnitz
     */
    public void waitSeconds(int seconds) {
        driver.manage().timeouts().implicitlyWait(seconds, TimeUnit.SECONDS);
    }
}