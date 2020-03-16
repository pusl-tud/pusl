package de.bp2019.pusl.config;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
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
import de.bp2019.pusl.ui.views.LecturesView;
import de.bp2019.pusl.ui.views.LoginView;

/**
 * Base Class for UI tests. Starts Webdriver and fills database with one
 * {@link User} of each {@link UserType}. Also contains some utility functions.
 * Very Expensive! If you can test something with a Unit-Test instead, do that!
 * If something is unclear try searching for "selenium tutorial" on the
 * internet.
 * 
 * @author Leon Chemnitz
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class BaseUITest {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseUITest.class);

    protected WebDriver driver;

    @LocalServerPort
    int port;

    @Autowired
    protected TestProperties testProperties;

    @Autowired
    UserRepository userRepository;

    @Autowired
    InstituteRepository instituteRepository;

    @Autowired
    LectureRepository lectureRepository;

    @Autowired
    ExerciseSchemeRepository exerciseSchemeRepository;

    @Autowired
    GradeRepository gradeRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    protected String baseUrl;
    protected WebDriverWait wait;

    /**
     * Initializes TestDatabase and starts Webdriver
     * 
     * @throws Exception when no supported OS is detected
     * @author Leon Chemnitz
     */
    @BeforeEach
    public void setUp() throws Exception {

        User mockUser = new User();
        mockUser.setEmailAddress(testProperties.getSuperadminUsername());
        mockUser.setPassword(passwordEncoder.encode(testProperties.getSuperadminPassword()));
        mockUser.setType(UserType.SUPERADMIN);
        userRepository.save(mockUser);

        mockUser = new User();
        mockUser.setEmailAddress(testProperties.getAdminUsername());
        mockUser.setPassword(passwordEncoder.encode(testProperties.getAdminPassword()));
        mockUser.setType(UserType.ADMIN);
        userRepository.save(mockUser);

        mockUser = new User();
        mockUser.setEmailAddress(testProperties.getWimiUsername());
        mockUser.setPassword(passwordEncoder.encode(testProperties.getWimiPassword()));
        mockUser.setType(UserType.WIMI);
        userRepository.save(mockUser);

        mockUser = new User();
        mockUser.setEmailAddress(testProperties.getHiwiUsername());
        mockUser.setPassword(passwordEncoder.encode(testProperties.getHiwiPassword()));
        mockUser.setType(UserType.HIWI);
        userRepository.save(mockUser);

        baseUrl = testProperties.getBaseUrl() + port + "/";

        String driverFile = findFile();

        ChromeDriverService service = new ChromeDriverService.Builder().usingDriverExecutable(new File(driverFile))
                .build();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--start-maximized");
        if (testProperties.isHeadlessUiTests()) {
            options.addArguments("--headless");
        }
        options.setExperimentalOption("useAutomationExtension", false);
        options.addArguments("disable-infobars");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-dev-shm-usage");

        driver = new ChromeDriver(service, options);

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
            driver.quit();
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
    protected void goToURL(String url) throws InterruptedException {
        driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
        driver.navigate().to(baseUrl + url);
        waitForURL(url);
        waitForPageload();
    }

    /**
     * @author Leon Chemnitz
     */
    protected void goToURLandWaitForRedirect(String url, String RedirectUrl) throws InterruptedException {
        driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
        driver.navigate().to(baseUrl + url);
        waitForURL(RedirectUrl);
        waitForPageload();
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
     * Login to the application with a user of the given Type
     * 
     * @param userType
     * @throws Exception
     * @author Leon Chemnitz
     */
    protected void login(UserType userType) throws Exception {
        waitForLoginRedirect();

        LOGGER.info("Logging in as " + userType.toString());
        switch (userType) {
            case SUPERADMIN:
                findElementByName("username").sendKeys(testProperties.getSuperadminUsername());
                findElementByName("password").sendKeys(testProperties.getSuperadminPassword());
                break;
            case ADMIN:
                findElementByName("username").sendKeys(testProperties.getAdminUsername());
                findElementByName("password").sendKeys(testProperties.getAdminPassword());
                break;
            case WIMI:
                findElementByName("username").sendKeys(testProperties.getWimiUsername());
                findElementByName("password").sendKeys(testProperties.getWimiPassword());
                break;
            case HIWI:
                findElementByName("username").sendKeys(testProperties.getHiwiUsername());
                findElementByName("password").sendKeys(testProperties.getHiwiPassword());
                break;
        }

        findButtonContainingText("Log in").click();

        waitForURL("");
    }

    /**
     * @author Leon Chemnitz
     * @throws InterruptedException
     */
    protected void logout() throws InterruptedException {
        goToURL(LecturesView.ROUTE);
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
     * Expand ShadowDom of an Element
     * 
     * @param element
     * @return
     * @author Leon Chemnitz
     */
    protected WebElement expandShadowDOM(WebElement element) {
        return (WebElement) ((JavascriptExecutor) driver).executeScript("return arguments[0].shadowRoot", element);
    }

    private String findFile() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        URL url;
        if (SystemUtils.IS_OS_WINDOWS) {
            LOGGER.info("Platform Windows detected");
            url = classLoader.getResource(testProperties.getChromedriverWin());
        } else if (SystemUtils.IS_OS_LINUX) {
            LOGGER.info("Platform Linux detected");
            return testProperties.getChromedriverLinux();
        } else if (SystemUtils.IS_OS_MAC) {
            LOGGER.info("Platform Mac detected");
            url = classLoader.getResource(testProperties.getChromedriverMac());
        } else {
            throw new IOException("No supported plattform detected");
        }

        return url.getFile();
    }
}