package de.bp2019.pusl.ui.views;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
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

import de.bp2019.pusl.config.TestProperties;
import de.bp2019.pusl.enums.UserType;
import de.bp2019.pusl.model.User;
import de.bp2019.pusl.repository.UserRepository;
import de.bp2019.pusl.ui.views.login.LoginViewElement;

/**
 * Base Class for UI tests. Starts Webdriver and fills database with one
 * {@link User} of each {@link UserType}. Also contains some utility functions.
 * Very Expensive! If you can test something with a Unit-Test instead, do that!
 * If something is unclear try searching for "selenium tutorial" on the
 * internet.
 * 
 * @author Leon Chemnitz
 */
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
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
    PasswordEncoder passwordEncoder;

    private String baseUrl;

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
        if (testProperties.isHeadlessUiTests()) {
            options.addArguments("--headless");
        }
        options.setExperimentalOption("useAutomationExtension", false);
        options.addArguments("start-maximized");
        options.addArguments("disable-infobars");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-dev-shm-usage");

        driver = new ChromeDriver(service, options);

        driver.get(baseUrl);

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
        if (driver != null) {
            driver.quit();
        }
    }

    protected void waitForPageload() {
        var wait = new WebDriverWait(driver, 5);

        ExpectedCondition<Boolean> expectation = new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return ((JavascriptExecutor) driver).executeScript("return document.readyState").toString()
                        .equals("complete");
            }
        };
        wait.until(expectation);
    }

    protected void waitForURL(String url) throws InterruptedException {
        var wait = new WebDriverWait(driver, 2);
        wait.until(ExpectedConditions.urlToBe(baseUrl + url));
    }

    protected void goToURL(String url) throws InterruptedException {
        driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
        driver.navigate().to(baseUrl + url);
        waitForURL(url);
        waitForPageload();
    }

    private String findFile() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        URL url;
        if (SystemUtils.IS_OS_WINDOWS) {
            LOGGER.info("Platform Windows detected");
            url = classLoader.getResource(testProperties.getChromedriverWin());
        } else if (SystemUtils.IS_OS_LINUX) {
            LOGGER.info("Platform Linux detected");
            url = classLoader.getResource(testProperties.getChromedriverLinux());
        } else if (SystemUtils.IS_OS_MAC) {
            LOGGER.info("Platform Mac detected");
            url = classLoader.getResource(testProperties.getChromedriverMac());
        } else {
            throw new IOException("No supported plattform detected");
        }

        return url.getFile();
    }

    protected void waitForLoginRedirect() throws InterruptedException {
        driver.navigate().to(baseUrl);
        waitForURL(LoginView.ROUTE);
    }

    protected void timeoutWrongURL(String url) {
        assertThrows(TimeoutException.class, () -> waitForURL(url));
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
        LoginViewElement loginView = new LoginViewElement(this);

        LOGGER.info("Logging in as " + userType.toString());
        switch (userType) {
            case SUPERADMIN:
                loginView.login(testProperties.getSuperadminUsername(), testProperties.getSuperadminPassword());
                break;
            case ADMIN:
                loginView.login(testProperties.getAdminUsername(), testProperties.getAdminPassword());
                break;
            case WIMI:
                loginView.login(testProperties.getWimiUsername(), testProperties.getWimiPassword());
                break;
            case HIWI:
                loginView.login(testProperties.getHiwiUsername(), testProperties.getHiwiPassword());
                break;
        }

        waitForURL("");
    }

    public WebDriver getDriver(){
        return driver;
    }

    public TestProperties getProperties(){
        return testProperties;
    }
}