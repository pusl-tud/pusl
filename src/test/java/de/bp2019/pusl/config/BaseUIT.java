package de.bp2019.pusl.config;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
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
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
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
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class BaseUIT {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseUIT.class);

    private static ChromeDriverService service;
    protected WebDriver driver;

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

    @BeforeAll
    public static void startService() throws Exception {
        LOGGER.info("Starting Chromedriver service");

        // service = new ChromeDriverService.Builder().usingDriverExecutable(findFile()).usingAnyFreePort().build();
        // service.start();
    }

    @AfterAll
    public static void stopService() {
        LOGGER.info("Stopping Chromedriver service");

        // service.stop();
    }

    /**
     * Initializes TestDatabase and starts Webdriver
     * 
     * @throws Exception when no supported OS is detected
     * @author Leon Chemnitz
     */
    @BeforeEach
    public void setUp() throws Exception {
        LOGGER.info("Starting Chromedriver");

        baseUrl = testProperties.getBaseUrl() + port + "/";

        // ChromeOptions options = new ChromeOptions();
        // options.addArguments("--no-sandbox");
        // options.addArguments("--window-size=1920,1080");
        // options.addArguments("--start-maximized");
        // if (testProperties.isHeadlessUiTests()) {
        // options.addArguments("--headless");
        // }
        // options.setExperimentalOption("useAutomationExtension", false);
        // options.addArguments("disable-infobars");
        // options.addArguments("--disable-extensions");
        // options.addArguments("--disable-gpu");
        // options.addArguments("--disable-dev-shm-usage");

        userRepository.deleteAll();
        instituteRepository.deleteAll();
        exerciseSchemeRepository.deleteAll();
        lectureRepository.deleteAll();
        gradeRepository.deleteAll();

        // driver = new RemoteWebDriver(service.getUrl(), options);

        String URL = "https://" + "leonchemnitz1" + ":" + "w6qjjbMbyCMWmjsTXdWY" + "@hub-cloud.browserstack.com/wd/hub";

        // Input capabilities
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("browserstack.local", "true");
        caps.setCapability("browserstack.localIdentifier", System.getenv("BROWSERSTACK_LOCAL_IDENTIFIER"));
        // Add other capabilities like browser name, version and os name, version
        caps.setCapability("os_version", "10");
        caps.setCapability("resolution", "1920x1080");
        caps.setCapability("browser", "Chrome");
        caps.setCapability("browser_version", "86.0 beta");
        caps.setCapability("os", "Windows");

        WebDriver driver = new RemoteWebDriver(new URL(URL), caps);

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
    public void tearDown() throws Exception {
        userRepository.deleteAll();
        instituteRepository.deleteAll();
        exerciseSchemeRepository.deleteAll();
        lectureRepository.deleteAll();
        gradeRepository.deleteAll();

        if (driver != null) {
            LOGGER.info("Stopping Chromedriver");
            driver.quit();
        }
    }

    private static File findFile() throws IOException {

        if (SystemUtils.IS_OS_WINDOWS) {
            LOGGER.info("Platform Windows detected");
            Resource resource = new ClassPathResource(TestProperties.chromedriverWin);
            LOGGER.info(resource.getURL().toExternalForm());
            return resource.getFile();
        } else if (SystemUtils.IS_OS_LINUX) {
            LOGGER.info("Platform Linux detected");
            return new File(TestProperties.chromedriverLinux);
        } else if (SystemUtils.IS_OS_MAC) {
            LOGGER.info("Platform Mac detected");
            Resource resource = new ClassPathResource(TestProperties.chromedriverMac);
            LOGGER.info(resource.getURL().toExternalForm());
            return resource.getFile();
        } else {
            throw new IOException("No supported plattform detected");
        }
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

        return user;
    }

    /**
     * @author Leon Chemnitz
     * @throws InterruptedException
     */
    protected void logout() throws InterruptedException {
        LOGGER.info("logging out");
        goToURL(PuslProperties.ROOT_ROUTE);
        findButtonContainingText("logout").click();
        waitForURL(LoginView.ROUTE);
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
        driver.findElement(By.xpath("//vaadin-select[@id='" + id + "']")).click();

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
     */
    protected void findMSCBByIdAndSelectByTexts(String id, List<String> textList) {
        driver.findElement(By.xpath("//multiselect-combo-box[@id='" + id + "']")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//vaadin-combo-box-overlay")));
        WebElement shadowRoot1 = expandShadowDOM(driver.findElement(By.xpath("//vaadin-combo-box-overlay")));
        WebElement shadowRoot2 = expandShadowDOM(shadowRoot1.findElement(By.id("content")));
        List<WebElement> listItems = shadowRoot2.findElements(By.tagName("vaadin-combo-box-item"));
        textList.forEach(selectionText -> {
            for (WebElement element : listItems) {
                try {
                    WebElement div = expandShadowDOM(element).findElement(By.tagName("div"));
                    div.findElement(By.xpath(".//span[contains(text(),'" + selectionText + "')]")).click();
                } catch (Exception e) {
                    continue;
                }
            }

        });
        driver.findElement(By.xpath("//multiselect-combo-box[@id='" + id + "']")).sendKeys(Keys.ENTER);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//vaadin-combo-box-overlay")));
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
     * Clear a Vaadin TextField based on its CSS id
     * 
     * @param id
     * @author Leon Chemnitz
     */
    protected void clearFieldById(String id) {
        WebElement shadowRoot = expandShadowDOM(findElementById(id));
        shadowRoot.findElement(By.tagName("input")).clear();
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
        return (WebElement) ((JavascriptExecutor) driver).executeScript("return arguments[0].shadowRoot", element);
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