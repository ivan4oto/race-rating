package com.ivangochev.e2e;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

@Order(3)
public class CreateRaceFlowTest {

    static Playwright playwright;
    static Browser browser;
    BrowserContext context;
    Page page;
    static final Path ARTIFACTS = Paths.get(System.getProperty("artifactsDir", "/artifacts"));

    @BeforeAll
    static void setupBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
    }

    @BeforeEach
    void createContext() {
        String stamp = String.valueOf(Instant.now().toEpochMilli());

        Browser.NewContextOptions opts = new Browser.NewContextOptions()
                .setViewportSize(1920, 1080)
                .setIgnoreHTTPSErrors(true)
                .setRecordVideoDir(ARTIFACTS.resolve("videos"))
                .setRecordVideoSize(1920, 1080)
                // 👇 HAR with content (bodies). One file per test run.
                .setRecordHarPath(ARTIFACTS.resolve("har/network-" + stamp + ".har"))
                .setRecordHarOmitContent(false);

        context = browser.newContext(opts);
        // Optional but nice: richer trace with snapshots & sources.
        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true)
                .setSources(true));
        page = context.newPage();
        page.onConsoleMessage(msg -> System.out.println("[CONSOLE] " + msg.type() + ": " + msg.text()));
        page.onPageError(err -> System.out.println("[PAGEERROR] " + err));
        page.onRequest(req -> System.out.println("[REQ] " + req.method() + " " + req.url()));
        page.onResponse(res -> {
            String tag = res.ok() ? "RES" : "HTTPFAIL";
            System.out.println("[" + tag + "] " + res.status() + " " + res.url());
        });
    }

    @AfterAll
    static void closeBrowser() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();

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
    void shouldLoginWithAdminUser() throws IOException {
        String username = System.getenv("E2E_ADMIN_USERNAME");
        String email = System.getenv("E2E_ADMIN_EMAIL");
        String password = System.getenv("E2E_ADMIN_PASSWORD");
        String baseUrl = System.getProperty("baseUrl", System.getenv().getOrDefault("baseUrl", "http://localhost:4200"));
        page.navigate(baseUrl);

        Locator loginLink = page.getByTestId("login-link");
        loginLink.waitFor();
        loginLink.click();
        page.waitForURL("**/login");

        Locator form = page.locator("form").first();
        form.waitFor();

        form.locator("input[formcontrolname='email']").fill(email);
        form.locator("input[formcontrolname='password']").fill(password);
        String stamp = String.valueOf(Instant.now().toEpochMilli());

        Locator logIn = page.getByTestId("login-submit-button");
        logIn.waitFor();
        logIn.click();
        if (page != null && !page.isClosed()) {

            page.screenshot(new Page.ScreenshotOptions()
                    .setFullPage(true)
                    .setPath(ARTIFACTS.resolve("screens/" + this.getClass().getSimpleName() + ".png")));
//            Files.writeString(ARTIFACTS.resolve("html/" + stamp + ".html"), page.content());
        }
        Locator createLink = page.getByTestId("create-link");
        createLink.waitFor();

        createLink.click();
        Locator nameFormLocator = page.getByTestId("name-input");
        Locator descriptionFormLocator = page.getByTestId("description-input");
        Locator latFormLocator = page.getByTestId("latitude-input");
        Locator lonFormLocator = page.getByTestId("longitude-input");
        Locator websiteUrlFormLocator = page.getByTestId("website-url-input");
        Locator logoUrlFormLocator = page.getByTestId("logo-url-input");

        nameFormLocator.fill("Vitosha 100");
        descriptionFormLocator.fill("Race Description");
        latFormLocator.fill("51.5074");
        lonFormLocator.fill("0.1278");
        websiteUrlFormLocator.fill("https://www.google.com");
        logoUrlFormLocator.fill("https://www.google.com/images/branding/googlelogo/2x/googlelogo_color_272x92dp.png");

        Locator datePickButton = page.locator("button[aria-label='Open calendar']");
        datePickButton.click();
        Locator monthYearLocator = page.locator("button[aria-label='Choose month and year']");
        monthYearLocator.click();
        Locator locatorYear2025 = page.locator("button[aria-label='2025']");
        locatorYear2025.click();
        Locator locatorMonthJune = page.locator("button[aria-label='June 2025']");
        locatorMonthJune.click();
        Locator locatorDay27th = page.locator("button[aria-label='June 27, 2025']");
        locatorDay27th.click();
        Locator saveButton = page.getByTestId("create-race-submit-btn");
        saveButton.click();

        page.navigate(baseUrl);
        Locator raceLink = page.getByTestId("race-card-1");
        raceLink.click();
        Locator raceName = page.getByTestId("race-name-header");
        Assertions.assertEquals("Vitosha 100", raceName.innerText());
    }
}
