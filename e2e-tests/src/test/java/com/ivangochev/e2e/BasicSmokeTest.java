package com.ivangochev.e2e;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.junit.jupiter.api.*;


public class BasicSmokeTest {

    static Playwright playwright;
    static Browser browser;

    BrowserContext context;
    Page page;

    @BeforeAll
    static void setupBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
    }

    @AfterAll
    static void closeBrowser() {
        browser.close();
        playwright.close();
    }

    @BeforeEach
    void createContext() {
        context = browser.newContext();                     // fresh incognito
        context.addInitScript("localStorage.clear(); sessionStorage.clear();");
        page = context.newPage();
    }

    @AfterEach
    void closeContext() {
        context.close();
    }

    @Test
    void shouldLoadHomepage() {
        String baseUrl = System.getProperty("baseUrl", "http://localhost:4200");
        page.navigate(baseUrl);
        Assertions.assertTrue(page.title().toLowerCase().contains("race rating")); // adjust as needed
    }
    @Test
    void shouldDisplayLoginButton() {
        String baseUrl = System.getProperty("baseUrl", "http://localhost:4200");
        page.navigate(baseUrl);
        Locator loginButton = page.locator("a[routerlink='/login']");
        loginButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        Assertions.assertTrue(loginButton.isVisible());
    }

}
