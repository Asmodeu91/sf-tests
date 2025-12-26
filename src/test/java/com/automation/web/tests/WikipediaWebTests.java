package com.automation.web.tests;

import com.automation.config.TestConfig;
import com.automation.web.pages.ArticlePage;
import com.automation.web.pages.MainPage;
import com.automation.web.pages.SearchPage;
import com.automation.web.utils.WebDriverSetup;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.logging.Logger;

public class WikipediaWebTests {

    private static final Logger LOG = Logger.getLogger(WikipediaWebTests.class.getName());

    private WebDriverSetup driverSetup;
    private WebDriver driver;
    private WebDriverWait wait;
    private MainPage mainPage;
    private ArticlePage articlePage;
    private SearchPage searchPage;

    private static final String MAIN_PAGE_TITLE_PART = "Википедия";
    private static final String TEST_QUERY_SOFTWARE_TESTING = "Тестирование программного обеспечения";
    private static final String TEST_QUERY_PROGRAMMING = "Программирование";
    private static final String TEST_QUERY_JAVA = "Java";
    private static final String TEST_QUERY_JAVA_RU = "Java (язык программирования)";
    private static final String TEST_QUERY_SELENIUM = "Selenium";
    private static final String ENGLISH_LINK_TEXT = "English";
    private static final int DEFAULT_TIMEOUT_SECONDS = 10;
    private static final int MAX_RETRIES = 3;

    @BeforeClass
    public void setUp() {
        try {
            driverSetup = new WebDriverSetup();
            driverSetup.initDriver();
            driver = driverSetup.getDriver();
            wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS));

            mainPage = new MainPage(driver);
            articlePage = new ArticlePage(driver);
            searchPage = new SearchPage(driver);

            LOG.info("WebDriver и страницы успешно инициализированы");
        } catch (Exception e) {
            LOG.severe("Ошибка при инициализации тестов: " + e.getMessage());
            throw new RuntimeException("Не удалось инициализировать тестовое окружение", e);
        }
    }

    @BeforeMethod
    public void navigateToMainPage() {
        try {
            driver.get(TestConfig.getWebBaseUrl());
            waitForPageToLoad();
            LOG.info("Переход на главную страницу Wikipedia");
        } catch (TimeoutException e) {
            LOG.warning("Таймаут при загрузке главной страницы: " + e.getMessage());
            throw e;
        }
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (driverSetup != null) {
            driverSetup.quitDriver();
            LOG.info("WebDriver успешно завершил работу");
        }
    }

    @Test(priority = 1, description = "Проверка заголовка и элементов главной страницы")
    public void testMainPageTitleAndElements() {
        String actualTitle = mainPage.getPageTitle();
        LOG.info("Получен заголовок страницы: " + actualTitle);

        Assert.assertTrue(
                actualTitle.contains(MAIN_PAGE_TITLE_PART),
                String.format("Заголовок должен содержать '%s'. Фактический заголовок: %s",
                        MAIN_PAGE_TITLE_PART, actualTitle)
        );

        Assert.assertTrue(
                mainPage.isMainPageDisplayed(),
                "Главная страница не отображается корректно. Проверьте основные элементы."
        );
    }

    @Test(priority = 2, description = "Поиск статьи и валидация результата")
    public void testSearchFunctionality() {
        LOG.info("Выполнение поиска по запросу: " + TEST_QUERY_SOFTWARE_TESTING);

        performSearchWithRetry(TEST_QUERY_SOFTWARE_TESTING);
        waitForPageToLoad();

        String articleHeading = articlePage.getHeadingText();
        LOG.info("Заголовок найденной статьи: " + articleHeading);

        Assert.assertFalse(
                articleHeading.isEmpty(),
                "Заголовок статьи не должен быть пустым"
        );

        boolean isExpectedPage = articleHeading.toLowerCase().contains("тестирование") ||
                searchPage.getPageTitleText().toLowerCase().contains("поиск");

        Assert.assertTrue(
                isExpectedPage,
                String.format("Не перешли на корректную страницу после поиска '%s'. Заголовок: %s",
                        TEST_QUERY_SOFTWARE_TESTING, articleHeading)
        );
    }

    @Test(priority = 3, description = "Проверка навигации между страницами")
    public void testNavigationFlow() {
        Assert.assertTrue(
                mainPage.isMainPageDisplayed(),
                "Изначально главная страница не отображается"
        );

        performSearchWithRetry(TEST_QUERY_PROGRAMMING);
        waitForPageToLoad();

        Assert.assertTrue(
                articlePage.isContentDisplayed(),
                "Контент статьи о программировании не отображается"
        );

        driver.get(TestConfig.getWebBaseUrl());
        waitForPageToLoad();

        Assert.assertTrue(
                mainPage.isMainPageDisplayed(),
                "Возврат на главную страницу не удался"
        );
    }

    @Test(priority = 4, description = "Проверка результатов поиска и перехода по ссылке")
    public void testSearchResultsAndNavigation() {
        // Используем запрос, который гарантированно найдет результаты
        LOG.info("Поиск по запросу: " + TEST_QUERY_SELENIUM);
        performSearchWithRetry(TEST_QUERY_SELENIUM);
        waitForPageToLoad();

        // Проверяем, на какой странице мы оказались
        boolean isSearchResultsPage = searchPage.isSearchResultsPage();
        boolean isArticlePage = articlePage.isContentDisplayed();

        if (isSearchResultsPage) {
            int resultsCount = getSearchResultsCountWithRetry();
            LOG.info("Количество найденных результатов: " + resultsCount);

            Assert.assertTrue(
                    resultsCount > 0,
                    String.format("По запросу '%s' должно быть найдено хотя бы несколько результатов",
                            TEST_QUERY_SELENIUM)
            );

            clickFirstSearchResultWithRetry();
            waitForPageToLoad();

            Assert.assertTrue(
                    articlePage.isContentDisplayed(),
                    "После перехода по первому результату поиска контент статьи не отображается"
            );

            String articleTitle = articlePage.getHeadingText();
            Assert.assertFalse(
                    articleTitle.isEmpty(),
                    "Заголовок статьи не должен быть пустым"
            );
            LOG.info("Успешно перешли на статью: " + articleTitle);

        } else if (isArticlePage) {
            // Если поиск сразу перешел на статью
            String articleTitle = articlePage.getHeadingText();
            LOG.info("Поиск напрямую перешел на статью: " + articleTitle);
            Assert.assertTrue(
                    articleTitle.toLowerCase().contains("selenium"),
                    String.format("Статья должна содержать 'selenium' в заголовке. Фактический: %s", articleTitle)
            );
        } else {
            Assert.fail(String.format(
                    "Поиск по запросу '%s' не привел ни к странице результатов, ни к статье",
                    TEST_QUERY_SELENIUM
            ));
        }
    }

    @Test(priority = 5, description = "Проверка наличия переключателя языка")
    public void testLanguageSwitcherPresence() {
        String pageSource = driver.getPageSource();

        Assert.assertTrue(
                pageSource.contains(ENGLISH_LINK_TEXT),
                String.format("Не найдена ссылка на английскую версию ('%s').", ENGLISH_LINK_TEXT)
        );
        LOG.info("Ссылка на английскую версию Wikipedia найдена");
    }

    @Test(priority = 6, description = "Проверка перехода на главную страницу через навигацию")
    public void testNavigationToMainPage() {
        performSearchWithRetry(TEST_QUERY_SOFTWARE_TESTING);
        waitForPageToLoad();

        // Проверяем, что мы не на главной странице
        if (mainPage.isMainPageDisplayed()) {
            LOG.info("Уже находимся на главной странице, тест пропускается");
            return;
        }

        // Ожидание готовности элемента перед кликом
        try {
            wait.until(ExpectedConditions.elementToBeClickable(
                    mainPage.getMainPageLinkElement()
            ));
        } catch (TimeoutException e) {
            LOG.warning("Элемент для перехода на главную страницу не стал кликабельным: " + e.getMessage());
            // Попробуем найти элемент по-другому
            mainPage.refreshPageElements();
        }

        // Сохраняем текущий URL для сравнения
        String currentUrl = driver.getCurrentUrl();

        // Клик с повторными попытками при StaleElementReferenceException
        clickMainPageLinkWithRetry();
        waitForPageToLoad();

        // Проверяем, что URL изменился
        String newUrl = driver.getCurrentUrl();
        Assert.assertNotEquals(
                currentUrl, newUrl,
                "URL не изменился после клика на ссылку главной страницы"
        );

        Assert.assertTrue(
                mainPage.isMainPageDisplayed(),
                "После клика по ссылке 'Заглавная страница' главная страница не отобразилась"
        );

        String title = mainPage.getPageTitle();
        boolean isMainPageTitle = title.contains("Заглавная страница") ||
                title.contains("Википедия") ||
                title.contains("Wikipedia");

        Assert.assertTrue(
                isMainPageTitle,
                String.format("Заголовок не соответствует главной странице. Фактический заголовок: %s", title)
        );
        LOG.info("Успешный переход на главную страницу через навигацию");
    }

    // Вспомогательные методы для обработки StaleElementReferenceException

    private void performSearchWithRetry(String query) {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                mainPage.searchFor(query);
                waitForPageToLoad();
                return;
            } catch (StaleElementReferenceException e) {
                LOG.warning(String.format("StaleElement при поиске '%s' (попытка %d/%d): %s",
                        query, attempt, MAX_RETRIES, e.getMessage()));
                if (attempt == MAX_RETRIES) {
                    throw e;
                }
                waitForPageToLoad();
                refreshPageIfNeeded();
                // Переинициализируем страницу
                mainPage = new MainPage(driver);
            }
        }
    }

    private void clickMainPageLinkWithRetry() {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                mainPage.goToMainPage();
                return;
            } catch (StaleElementReferenceException e) {
                LOG.warning(String.format("StaleElement при клике на главную страницу (попытка %d/%d): %s",
                        attempt, MAX_RETRIES, e.getMessage()));
                if (attempt == MAX_RETRIES) {
                    throw e;
                }
                waitForPageToLoad();
                // Переинициализируем элементы страницы
                mainPage.refreshPageElements();
            }
        }
    }

    private int getSearchResultsCountWithRetry() {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                return searchPage.getSearchResultsCount();
            } catch (StaleElementReferenceException e) {
                LOG.warning(String.format("StaleElement при получении количества результатов (попытка %d/%d): %s",
                        attempt, MAX_RETRIES, e.getMessage()));
                if (attempt == MAX_RETRIES) {
                    throw e;
                }
                waitForPageToLoad();
                searchPage.refreshPageElements();
            }
        }
        return 0;
    }

    private void clickFirstSearchResultWithRetry() {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                searchPage.clickFirstSearchResult();
                return;
            } catch (StaleElementReferenceException e) {
                LOG.warning(String.format("StaleElement при клике на первый результат (попытка %d/%d): %s",
                        attempt, MAX_RETRIES, e.getMessage()));
                if (attempt == MAX_RETRIES) {
                    throw e;
                }
                waitForPageToLoad();
                searchPage.refreshPageElements();
            }
        }
    }

    private void waitForPageToLoad() {
        try {
            Thread.sleep(1500); // Увеличенная пауза для стабилизации
            wait.until(driver -> {
                String readyState = (String) ((org.openqa.selenium.JavascriptExecutor) driver)
                        .executeScript("return document.readyState");
                return "complete".equals(readyState);
            });
        } catch (Exception e) {
            LOG.warning("Ошибка при ожидании загрузки страницы: " + e.getMessage());
        }
    }

    private void refreshPageIfNeeded() {
        try {
            driver.navigate().refresh();
            waitForPageToLoad();
        } catch (Exception e) {
            LOG.warning("Ошибка при обновлении страницы: " + e.getMessage());
        }
    }

    @DataProvider(name = "searchQueries")
    public Object[][] provideSearchQueries() {
        return new Object[][] {
                {"Автоматизация тестирования"},
                {"Selenium"},
                {"Тестирование"},
                {"Программирование"},
                {"Python"}
        };
    }

    @Test(priority = 7, dataProvider = "searchQueries",
            description = "Параметризованный тест поиска по разным запросам")
    public void testParameterizedSearch(String query) {
        LOG.info("Параметризованный поиск по запросу: " + query);

        // Проверяем, что находимся на главной странице
        if (!mainPage.isMainPageDisplayed()) {
            driver.get(TestConfig.getWebBaseUrl());
            waitForPageToLoad();
        }

        performSearchWithRetry(query);
        waitForPageToLoad();

        // Проверяем результат поиска
        boolean hasArticleContent = articlePage.isContentDisplayed();
        boolean hasSearchResults = searchPage.isSearchResultsPage();

        Assert.assertTrue(
                hasArticleContent || hasSearchResults,
                String.format("По запросу '%s' не найдена ни статья, ни результаты поиска", query)
        );

        if (hasArticleContent) {
            String heading = articlePage.getHeadingText();
            Assert.assertFalse(
                    heading.isEmpty(),
                    String.format("Для запроса '%s' получен пустой заголовок статьи", query)
            );
            LOG.info(String.format("Для запроса '%s' найден заголовок статьи: %s", query, heading));
        } else if (hasSearchResults) {
            int resultsCount = getSearchResultsCountWithRetry();
            Assert.assertTrue(
                    resultsCount > 0,
                    String.format("Для запроса '%s' должны быть найдены результаты", query)
            );
            LOG.info(String.format("Для запроса '%s' найдено результатов: %d", query, resultsCount));
        }
    }
}