package com.ivangochev.e2e;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.junit.jupiter.api.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Order(1)
public class SignUpFlowTest {
    static Playwright playwright;
    static Browser browser;

    BrowserContext context;
    Page page;

    // Where to save artifacts (matches your docker volume)
    static final Path ARTIFACTS = Paths.get(System.getProperty("artifactsDir", "/artifacts"));

    @BeforeAll
    static void setupBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
    }

    @AfterAll
    static void closeBrowser() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }

    @BeforeEach
    void createContext() {
        // record video + start tracing
        Browser.NewContextOptions opts = new Browser.NewContextOptions()
                .setViewportSize(1920, 1080)
                .setIgnoreHTTPSErrors(true);
        context = browser.newContext(opts);

        page = context.newPage();

        // Helpful runtime logs from the browser
        page.onConsoleMessage(msg -> System.out.println("[CONSOLE] " + msg.type() + ": " + msg.text()));
        page.onPageError(err -> System.out.println("[PAGEERROR] " + err));
        page.onRequest(req -> System.out.println("[REQ] " + req.method() + " " + req.url()));
        page.onResponse(res -> {
            String tag = res.ok() ? "RES" : "HTTPFAIL";
            System.out.println("[" + tag + "] " + res.status() + " " + res.url());
        });
    }

    @AfterEach
    void dumpArtifactsAndClose() {
        try {
            Files.createDirectories(ARTIFACTS.resolve("screens"));
            Files.createDirectories(ARTIFACTS.resolve("html"));
            String stamp = String.valueOf(Instant.now().toEpochMilli());

            // Full-page screenshot + HTML snapshot (always; cheap & super useful)
            if (page != null && !page.isClosed()) {
                page.screenshot(new Page.ScreenshotOptions()
                        .setFullPage(true)
                        .setPath(ARTIFACTS.resolve("screens/" + stamp + ".png")));
                Files.writeString(ARTIFACTS.resolve("html/" + stamp + ".html"), page.content());
            }

            // Save Playwright trace
            context.tracing().stop(new Tracing.StopOptions()
                    .setPath(ARTIFACTS.resolve("trace/" + stamp + ".zip")));
        } catch (Exception ignore) {}

        if (context != null) context.close(); // this finalizes the video file
    }

    @Test
    void signUpNavigatesHomeAndShowsUsernameInNavbar() {
        String baseUrl = System.getProperty("baseUrl", "http://localhost:4200");
        String username = "user" + Instant.now().getEpochSecond();
        String email = username + "@example.test";
        String name = "Test " + username;
        String password = "P@ssw0rd!" + username.substring(Math.max(0, username.length() - 4));

        // 1) Open home
        page.navigate(baseUrl);
        page.waitForLoadState(LoadState.NETWORKIDLE);

        // 2) Go to login
        Locator loginLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Login"));
        if (!loginLink.isVisible()) {
            page.context().clearCookies();
            page.reload();
            page.waitForLoadState(LoadState.NETWORKIDLE);
            loginLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Login"));
        }
        loginLink.waitFor();
        loginLink.click();
        page.waitForURL("**/login");

        // 3) Create account → signup
        Locator createAccount = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Create Account"));
        if (!createAccount.isVisible()) {
            createAccount = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Create Account"));
        }
        createAccount.waitFor();
        createAccount.click();
        page.waitForURL("**/signup");

        // 4) Fill form
        Locator form = page.locator("form").first();
        form.waitFor();

        form.locator("input[formcontrolname='email']").fill(email);
        form.locator("input[formcontrolname='username']").fill(username);
        form.locator("input[formcontrolname='name']").fill(name);
        form.locator("input[formcontrolname='password']").fill(password);

        // 5) Submit
        Locator signUp = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign up"));
        if (!signUp.isVisible()) signUp = page.locator("button:has-text('Sign up')");
        signUp.waitFor();
        signUp.click();

        // 6) Assert username in navbar (fallbacks if data-testid is missing)
        Locator usernameInNavbar = page.getByTestId("navbar-username");
        usernameInNavbar.waitFor(new Locator.WaitForOptions()
                .setTimeout(15000)
                .setState(WaitForSelectorState.VISIBLE));
        assertTrue(usernameInNavbar.isVisible(), "Expected username to be visible in the navbar after signup");
    }
}