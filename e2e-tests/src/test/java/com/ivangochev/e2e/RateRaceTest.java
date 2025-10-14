package com.ivangochev.e2e;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

import java.nio.file.Path;
import java.nio.file.Paths;

@Order(4)
public class RateRaceTest {

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
//        try {
//            Files.createDirectories(ARTIFACTS.resolve("video"));
//        } catch (Exception ignore) {}

        Browser.NewContextOptions opts = new Browser.NewContextOptions()
                .setViewportSize(1920, 1080)
                .setIgnoreHTTPSErrors(true);
//                .setRecordVideoDir(ARTIFACTS.resolve("video"))
//                .setRecordVideoSize(1920, 1080);

        context = browser.newContext(opts);
        page = context.newPage();
    }

    @AfterEach
    void closeContext() {
        if (context != null) context.close(); // finalizes the video file
    }

    @AfterAll
    static void closeBrowser() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();

    }

    private void login() {
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

        Locator logIn = page.getByTestId("login-submit-button");
        logIn.waitFor();
        logIn.click();
    }


    @Test
    void shouldVoteForRace() {
        login();
        Locator raceLink = page.getByTestId("race-card-1");
        raceLink.waitFor();
        raceLink.click();
        page.getByTestId("rating-row-0-button-4").click();
        page.getByTestId("rating-row-1-button-3").click();
        page.getByTestId("rating-row-2-button-2").click();
        page.getByTestId("rating-row-3-button-1").click();
        page.getByTestId("rating-row-4-button-0").click();

        Locator submitBtn = page.getByTestId("rating-submit-btn");
        submitBtn.waitFor();
        submitBtn.click();
        page.reload();
        page.waitForLoadState();
        Locator firstRating = page.getByTestId("average-score-0");
        Locator secondRating = page.getByTestId("average-score-1");
        Locator thirdRating = page.getByTestId("average-score-2");
        Locator fourthRating = page.getByTestId("average-score-3");
        Locator fifthRating = page.getByTestId("average-score-4");
        Assertions.assertEquals("5", firstRating.innerText());
        Assertions.assertEquals("4", secondRating.innerText());
        Assertions.assertEquals("3", thirdRating.innerText());
        Assertions.assertEquals("2", fourthRating.innerText());
        Assertions.assertEquals("1", fifthRating.innerText());
    }

    @Test
    void shouldCommentForRace() {
        login();

        Locator raceLink = page.getByTestId("race-card-1");
        raceLink.waitFor();
        raceLink.click();
        Assertions.assertFalse(page.getByTestId("you-have-already-commented").isVisible());
        Locator commentTextArea = page.getByTestId("comment-text-area");
        commentTextArea.waitFor();
        commentTextArea.fill("This is a test comment");
        Locator submitBtn = page.getByTestId("comment-submit-btn");
        submitBtn.waitFor();
        submitBtn.click();
        Locator commentText = page.getByTestId("comment-1-text");
        Assertions.assertEquals("This is a test comment", commentText.innerText());
        Assertions.assertTrue(page.getByTestId("you-have-already-commented").isVisible());
    }
}
