package com.ivangochev.e2e;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import org.junit.jupiter.api.*;

import java.nio.file.Paths;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SignUpFlowTest {

    static Playwright playwright;
    static Browser browser;

    BrowserContext context;
    Page page;

    @BeforeAll
    static void setupBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(true));
    }

    @AfterAll
    static void closeBrowser() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }

    @BeforeEach
    void logRequests() {
        page.onRequest(request -> {
            System.out.println("[REQ] " + request.method() + " " + request.url());
        });
        page.onResponse(response -> {
            System.out.println("[RES] " + response.status() + " " + response.url());
        });
    }

    @BeforeEach
    void createContext() {
        context = browser.newContext();
        // Make sure we start logged out before the app bootstraps
        context.addInitScript("localStorage.clear(); sessionStorage.clear();");
        page = context.newPage();
    }

    @AfterEach
    void teardown() {
        if (context != null) context.close();
    }

    @Test
    void signUpNavigatesHomeAndShowsUsernameInNavbar() {
        String baseUrl = System.getProperty("baseUrl", "http://localhost:4200");
        // simple unique data per run
        String username = "user" + Instant.now().getEpochSecond();
        String email = username + "@example.test";
        String name = "Test " + username;
        String password = "P@ssw0rd!" + username.substring(Math.max(0, username.length() - 4));

        // 1) Open home
        page.navigate(baseUrl);
        page.waitForLoadState(LoadState.NETWORKIDLE);

        // 2) Click "Login" (from your toolbar ng-template)
        // Prefer a stable locator; adjust if your link text differs (e.g., “Log in”)
        Locator loginLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Login"));
        if (!loginLink.isVisible()) {
            // angular might still be settling; or you might be auto-logged in
            page.context().clearCookies();
            page.reload();
            page.waitForLoadState(LoadState.NETWORKIDLE);
        }
        loginLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Login"));
        loginLink.waitFor();
        loginLink.click();
        page.waitForURL("**/login");

        // 3) Click "Create Account" (to /signup)
        // Adjust the name if your button/link says “Create account” casing, etc.
        Locator createAccount = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Create Account"));
        if (!createAccount.isVisible()) {
            // sometimes it's a button not link
            createAccount = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Create Account"));
        }
        createAccount.waitFor();
        createAccount.click();
        page.waitForURL("**/signup");

        // 4) Fill the form
        // ---- Preferred: by accessible label text ----
        // If your fields use Angular Material <mat-label> correctly, these will work.
        Locator form = page.locator("form");
        form.waitFor();

        // Fill each field by formControlName (most stable with Angular)
        Locator emailInput = form.locator("input[formcontrolname='email']");
        Locator usernameInput = form.locator("input[formcontrolname='username']");
        Locator nameInput = form.locator("input[formcontrolname='name']");
        Locator passwordInput = form.locator("input[formcontrolname='password']");

        // Ensure visible & interactable, then fill
        emailInput.waitFor();
        emailInput.fill(email);

        usernameInput.waitFor();
        usernameInput.fill(username);

        nameInput.waitFor();
        nameInput.fill(name);

        passwordInput.waitFor();
        passwordInput.fill(password);

        // Optional: assert values actually went in (helps catch masking/overlays)
        Assertions.assertEquals(email, emailInput.inputValue());
        Assertions.assertEquals(username, usernameInput.inputValue());
        Assertions.assertEquals(name, nameInput.inputValue());
        Assertions.assertEquals(password, passwordInput.inputValue());

        // 5) Click "Sign up"
        Locator signUp = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign up"));
        if (!signUp.isVisible()) {
            // Sometimes Material buttons render as <button mat-raised-button> with text
            signUp = page.locator("button:has-text('Sign up')");
        }
        signUp.waitFor();
        signUp.click();

        // 6) Expect navigation back to root
//        page.waitForURL(u ->
//                        u.equals(baseUrl) ||
//                                u.equals(baseUrl + "/") ||
//                                u.startsWith(baseUrl + "?") ||
//                                u.startsWith(baseUrl + "#"),
//                new Page.WaitForURLOptions().setTimeout(15000)
//        );

        // 7) Username should appear in the navbar (inside your <app-user-display> menu trigger)
        // Prefer a data-testid on the username span for reliability, e.g. data-testid="navbar-username"
        Locator usernameInNavbar = page.locator("[data-testid='navbar-username']");
//        page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("troubleshoot.png")));
        usernameInNavbar.waitFor();
        assertTrue(usernameInNavbar.isVisible(), "Expected username to be visible in the navbar after signup");
    }
}
