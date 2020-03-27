package de.bp2019.pusl.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Application properties only used for testing
 * 
 * @author Leon Chemnitz
 */
@ConfigurationProperties("pusl.test")
public class TestProperties {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestProperties.class);

    private boolean headlessUiTests;
    private String baseUrl;

    public static String chromedriverWin;
    private static String chromedriverLinux;
    private static String chromedriverMac;


    @Value("${pusl.test.chromedriver-win}")
    public void setChromedriverWin(String db) {
        LOGGER.info("setting chromedriver win " + db);
        this.chromedriverWin = db;
    }


    public String getBaseUrl() {
        return this.baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getChromedriverLinux() {
        return this.chromedriverLinux;
    }

    public void setChromedriverLinux(String chromedriverLinux) {
        this.chromedriverLinux = chromedriverLinux;
    }

    public String getChromedriverMac() {
        return this.chromedriverMac;
    }

    public void setChromedriverMac(String chromedriverMac) {
        this.chromedriverMac = chromedriverMac;
    }

    public String getChromedriverWin() {
        return this.chromedriverWin;
    }

    public boolean isHeadlessUiTests() {
        return this.headlessUiTests;
    }

    public void setHeadlessUiTests(boolean headlessUiTests) {
        this.headlessUiTests = headlessUiTests;
    }

}