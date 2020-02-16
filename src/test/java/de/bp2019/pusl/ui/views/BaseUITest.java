package de.bp2019.pusl.ui.views;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public abstract class BaseUITest {

    protected WebDriver driver;

    private static final String CHROMEDRIVER_EXE = "chromedriver.exe";
    private static final String BASE_URL = "http://localhost:8080/";

    @Before
    public void setUp() {
        String driverFile = findFile();
        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        ChromeDriverService service = new ChromeDriverService.Builder().usingDriverExecutable(new File(driverFile))
                .build();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox"); // Bypass OS security model, MUST BE THE VERY FIRST OPTION
        //options.addArguments("--headless");
        options.setExperimentalOption("useAutomationExtension", false);
        options.addArguments("start-maximized"); // open Browser in maximized mode
        options.addArguments("disable-infobars"); // disabling infobars
        options.addArguments("--disable-extensions"); // disabling extensions
        options.addArguments("--disable-gpu"); // applicable to windows os only
        options.addArguments("--disable-dev-shm-usage"); // overcome limited resource problems
        options.merge(capabilities);
        driver = new ChromeDriver(service, options);

        driver.get(BASE_URL);

        waitForPageload();
    }

    @After
    public void tearDown() throws Exception {
        // close the browser instance when all tests are done
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
        wait.until(ExpectedConditions.urlToBe(BASE_URL + url));
    }

    protected void goToURL(String url) throws InterruptedException {
        driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
        driver.navigate().to(BASE_URL + url);
        waitForURL(url);
        waitForPageload();
    }

    private String findFile() {
        ClassLoader classLoader = getClass().getClassLoader();
        URL url = classLoader.getResource(CHROMEDRIVER_EXE);
        return url.getFile();
    }

    protected void waitForLoginRedirect() throws InterruptedException {
        driver.navigate().to(BASE_URL);
        waitForURL(LoginView.ROUTE);
    }

    protected void timeoutWrongURL(String url){        
        assertThrows(TimeoutException.class, () -> waitForURL(url));
    }
}