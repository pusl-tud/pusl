package de.bp2019.pusl.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Application properties only used for testing
 * 
 * @author Leon Chemnitz
 */
@ConfigurationProperties("pusl.test")
public class TestProperties {
    private boolean headlessUiTests;
    private String baseUrl;

    private String chromedriverWin;
    private String chromedriverLinux;
    private String chromedriverMac;


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

    public void setChromedriverWin(String chromedriverWin) {
        this.chromedriverWin = chromedriverWin;
    }

    public boolean isHeadlessUiTests() {
        return this.headlessUiTests;
    }

    public void setHeadlessUiTests(boolean headlessUiTests) {
        this.headlessUiTests = headlessUiTests;
    }

}