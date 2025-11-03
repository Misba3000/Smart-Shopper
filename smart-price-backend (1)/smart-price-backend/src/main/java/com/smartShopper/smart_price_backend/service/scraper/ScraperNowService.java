//package com.smartShopper.smart_price_backend.service.scraper;
//
//import com.smartShopper.smart_price_backend.dto.product.ProductResponse;
//import io.github.bonigarcia.wdm.WebDriverManager;
//import org.openqa.selenium.*;
//import org.openqa.selenium.chrome.ChromeDriver;
//import org.openqa.selenium.chrome.ChromeOptions;
//import org.openqa.selenium.support.ui.ExpectedConditions;
//import org.openqa.selenium.support.ui.WebDriverWait;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.net.URLEncoder;
//import java.nio.charset.StandardCharsets;
//import java.time.Duration;
//import java.util.*;
//import java.util.logging.Logger;
//
//@Service
//public class ScraperNowService {
//
//    private static final Logger logger = Logger.getLogger(ScraperNowService.class.getName());
//
//    private ChromeOptions getChromeOptions() {
//        ChromeOptions options = new ChromeOptions();
//        options.addArguments(
//                "--headless",
//                "--disable-blink-features=AutomationControlled",
//                "--no-sandbox",
//                "--disable-gpu",
//                "--disable-extensions",
//                "--disable-dev-shm-usage",
//                "--window-size=1920,1080",
//                "--disable-web-security",
//                "--allow-running-insecure-content",
//                "--dns-prefetch-disable",
//                "--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
//        );
//        options.setExperimentalOption("excludeSwitches", Arrays.asList("enable-automation", "enable-logging"));
//        options.setExperimentalOption("useAutomationExtension", false);
//        return options;
//    }
//
//    private WebDriver setupDriver() {
//        WebDriverManager.chromedriver().setup();
//        WebDriver driver = new ChromeDriver(getChromeOptions());
//
//        if (driver instanceof ChromeDriver) {
//            ((ChromeDriver) driver).executeCdpCommand("Page.addScriptToEvaluateOnNewDocument", Map.of(
//                    "source", """
//                    Object.defineProperty(navigator, 'webdriver', {
//                        get: () => undefined
//                    });
//                    window.chrome = { runtime: {} };
//                    """
//            ));
//        }
//        return driver;
//    }
//
//    public List<ProductResponse> scrapeAmazonByQuery(String query) throws Exception {
//        List<ProductResponse> products = new ArrayList<>();
//        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
//        String url = "https://www.amazon.in/s?k=" + encodedQuery;
//
//        WebDriver driver = setupDriver();
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
//
//        try {
//            logger.info("Starting Amazon scraping for query: " + query);
//            driver.get(url);
//
//            Thread.sleep(3000 + new Random().nextInt(2000));
//
//            if (isAmazonBlocked(driver)) {
//                logger.severe("Amazon blocked the request");
//                throw new RuntimeException("Amazon blocked the request");
//            }
//
//            wait.until(ExpectedConditions.presenceOfElementLocated(
//                    By.cssSelector("[data-component-type='s-search-result'], .s-result-item")
//            ));
//
//            scrollPage(driver);
//
//            List<WebElement> items = findAmazonProducts(driver);
//            logger.info("Found " + items.size() + " product elements on Amazon");
//
//            for (WebElement item : items) {
//                try {
//                    ProductResponse product = extractAmazonProductInfo(item);
//                    if (product != null) {
//                        products.add(product);
//                    }
//                } catch (Exception e) {
//                    logger.warning("Error extracting Amazon product: " + e.getMessage());
//                }
//            }
//        } finally {
//            driver.quit();
//        }
//        return products;
//    }
//
//    public List<ProductResponse> scrapeMeeshoByQuery(String query) throws Exception {
//        List<ProductResponse> products = new ArrayList<>();
//        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
//        String url = "https://www.meesho.com/search?q=" + encodedQuery;
//
//        WebDriver driver = setupDriver();
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(25));
//
//        try {
//            logger.info("Starting Meesho scraping for query: " + query);
//            driver.get(url);
//
//            Thread.sleep(4000 + new Random().nextInt(2000));
//
//            if (isMeeshoBlocked(driver)) {
//                logger.severe("Meesho blocked the request");
//                throw new RuntimeException("Meesho blocked the request");
//            }
//
//            try {
//                wait.until(ExpectedConditions.or(
//                        ExpectedConditions.presenceOfElementLocated(By.cssSelector("a[href*='/p/']")),
//                        ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-testid*='product']"))
//                ));
//            } catch (TimeoutException e) {
//                logger.warning("Timeout waiting for Meesho products to load, trying to continue");
//            }
//
//            scrollPage(driver);
//
//            List<WebElement> items = findMeeshoProducts(driver);
//            logger.info("Found " + items.size() + " product elements on Meesho");
//
//            for (WebElement item : items) {
//                try {
//                    ProductResponse product = extractMeeshoProductInfo(item);
//                    if (product != null) {
//                        products.add(product);
//                        if (products.size() >= 10) break;
//                    }
//                } catch (Exception e) {
//                    logger.warning("Error extracting Meesho product: " + e.getMessage());
//                }
//            }
//        } finally {
//            driver.quit();
//        }
//        return products;
//    }
//
//    private boolean isAmazonBlocked(WebDriver driver) {
//        try {
//            return driver.findElements(By.cssSelector("form[action*='captcha'], #captchacharacters")).size() > 0 ||
//                    driver.getPageSource().contains("sorry we just need to make sure") ||
//                    driver.getPageSource().contains("enter the characters you see") ||
//                    driver.getTitle().contains("Bot Check");
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//    private boolean isMeeshoBlocked(WebDriver driver) {
//        try {
//            return driver.getTitle().contains("Access Denied") ||
//                    driver.getPageSource().contains("security check") ||
//                    driver.getPageSource().contains("blocked") ||
//                    driver.findElements(By.cssSelector("form[action*='captcha']")).size() > 0;
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//    private void scrollPage(WebDriver driver) {
//        JavascriptExecutor js = (JavascriptExecutor) driver;
//        try {
//            long pageHeight = (Long) js.executeScript("return document.body.scrollHeight");
//            for (int i = 0; i < 10; i++) {
//                js.executeScript("window.scrollTo(0, " + (pageHeight * i / 10) + ");");
//                Thread.sleep(800);
//            }
//            js.executeScript("window.scrollTo(0, 0);");
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        } catch (Exception e) {
//            logger.warning("Error during scrolling: " + e.getMessage());
//        }
//    }
//
//    private List<WebElement> findAmazonProducts(WebDriver driver) {
//        String[] amazonSelectors = {
//                "div[data-component-type='s-search-result']",
//                ".s-result-item",
//                "[data-asin]"
//        };
//
//        for (String selector : amazonSelectors) {
//            try {
//                List<WebElement> items = driver.findElements(By.cssSelector(selector));
//                if (!items.isEmpty()) return items;
//            } catch (Exception e) {
//                logger.info("Amazon selector " + selector + " failed: " + e.getMessage());
//            }
//        }
//        return new ArrayList<>();
//    }
//
//    private List<WebElement> findMeeshoProducts(WebDriver driver) {
//        String[] meeshoSelectors = {
//                "a[href*='/p/']",
//                "[data-testid*='product']",
//                ".ProductList__GridCol-sc-8lnc8s-0",
//                ".plp-card"
//        };
//
//        for (String selector : meeshoSelectors) {
//            try {
//                List<WebElement> items = driver.findElements(By.cssSelector(selector));
//                logger.info("Selector '" + selector + "' found " + items.size() + " elements");
//                if (!items.isEmpty()) {
//                    return items;
//                }
//            } catch (Exception e) {
//                logger.info("Meesho selector " + selector + " failed: " + e.getMessage());
//            }
//        }
//
//        return new ArrayList<>();
//    }
//
//    private ProductResponse extractAmazonProductInfo(WebElement item) {
//        try {
//            String title = findElementText(item,
//                    "h2 a span",
//                    ".a-size-medium",
//                    ".a-text-normal"
//            );
//
//            if (title.isEmpty()) {
//                logger.warning("Skipping Amazon product with empty title");
//                return null;
//            }
//
//            String priceText = findElementText(item,
//                    ".a-price .a-offscreen",
//                    ".a-price-whole",
//                    ".a-color-price"
//            ).replaceAll("[^\\d.]", "");
//
//            if (priceText.isEmpty()) {
//                logger.warning("Amazon product '" + title + "' has no price");
//                return null;
//            }
//
//            BigDecimal price;
//            try {
//                price = new BigDecimal(priceText);
//            } catch (NumberFormatException e) {
//                logger.warning("Invalid price format for Amazon product: " + title + ", price: " + priceText);
//                return null;
//            }
//
//            String productUrl = findElementAttribute(item, "h2 a.a-link-normal", "href");
//            if (productUrl != null && !productUrl.startsWith("http")) {
//                productUrl = "https://www.amazon.in" + productUrl;
//            }
//
//            String imageUrl = extractAmazonImageUrl(item);
//
//            Double rating = null;
//            try {
//                String ratingText = findElementText(item, ".a-icon-alt");
//                if (!ratingText.isEmpty()) {
//                    rating = Double.parseDouble(ratingText.split(" ")[0]);
//                }
//            } catch (Exception e) {
//                logger.info("Could not extract rating for Amazon product: " + title);
//            }
//
//            Integer reviewCount = null;
//            try {
//                String reviewsText = findElementText(item, ".a-size-base").replaceAll("[^0-9]", "");
//                if (!reviewsText.isEmpty()) {
//                    reviewCount = Integer.parseInt(reviewsText);
//                }
//            } catch (Exception e) {
//                logger.info("Could not extract review count for Amazon product: " + title);
//            }
//
//            return new ProductResponse(
//                    title,
//                    extractBrand(title),
//                    "Amazon",
//                    productUrl != null ? productUrl : "",
//                    price,
//                    imageUrl != null ? imageUrl : "",
//                    "Amazon",
//                    rating,
//                    reviewCount,
//                    ""
//            );
//
//        } catch (Exception e) {
//            logger.warning("Error extracting Amazon product: " + e.getMessage());
//            return null;
//        }
//    }
//
//    private String extractAmazonImageUrl(WebElement item) {
//        try {
//            String imageUrl = findElementAttribute(item, ".s-image", "src");
//
//            if (imageUrl == null || imageUrl.isEmpty()) {
//                imageUrl = findElementAttribute(item, ".s-image", "data-src");
//            }
//
//            if (imageUrl == null || imageUrl.isEmpty()) {
//                imageUrl = findElementAttribute(item, ".s-image", "data-old-hires");
//            }
//
//            if (imageUrl == null || imageUrl.isEmpty()) {
//                WebElement img = item.findElement(By.tagName("img"));
//                imageUrl = img.getAttribute("src");
//                if (imageUrl == null || imageUrl.isEmpty()) {
//                    imageUrl = img.getAttribute("data-src");
//                }
//            }
//
//            return imageUrl;
//        } catch (Exception e) {
//            logger.warning("Error extracting Amazon image URL: " + e.getMessage());
//            return null;
//        }
//    }
//
//    private ProductResponse extractMeeshoProductInfo(WebElement item) {
//        try {
//            String title = "";
//            String priceText = "";
//            String productUrl = "";
//            String imageUrl = "";
//
//            String href = item.getAttribute("href");
//            if (href != null && href.contains("/p/")) {
//                productUrl = href.startsWith("http") ? href : "https://www.meesho.com" + href;
//            } else {
//                productUrl = findElementAttribute(item, "a[href*='/p/']", "href");
//                if (productUrl != null && !productUrl.startsWith("http")) {
//                    productUrl = "https://www.meesho.com" + productUrl;
//                }
//            }
//
//            title = findElementText(item,
//                    "[data-testid*='product']",
//                    ".plp-card__name",
//                    ".product-name",
//                    "h3",
//                    "h4"
//            );
//
//            priceText = findElementText(item,
//                    "[data-testid*='price']",
//                    ".plp-card__selling-price",
//                    ".product-price",
//                    ".selling-price"
//            ).replaceAll("[^\\d.]", "");
//
//            imageUrl = extractMeeshoImageUrl(item);
//
//            if (title.isEmpty() || title.length() < 5) {
//                logger.warning("Skipping Meesho product with invalid title: " + title);
//                return null;
//            }
//
//            if (priceText.isEmpty()) {
//                logger.warning("Meesho product '" + title + "' has no price");
//                return null;
//            }
//
//            BigDecimal price;
//            try {
//                price = new BigDecimal(priceText);
//                if (price.compareTo(BigDecimal.ZERO) <= 0) {
//                    logger.warning("Invalid price for Meesho product: " + title + ", price: " + priceText);
//                    return null;
//                }
//            } catch (NumberFormatException e) {
//                logger.warning("Invalid price format for Meesho product: " + title + ", price: " + priceText);
//                return null;
//            }
//
//            return new ProductResponse(
//                    title,
//                    extractBrand(title),
//                    "Meesho",
//                    productUrl != null ? productUrl : "",
//                    price,
//                    imageUrl != null ? imageUrl : "",
//                    "Meesho",
//                    null,
//                    null,
//                    ""
//            );
//
//        } catch (Exception e) {
//            logger.warning("Error extracting Meesho product: " + e.getMessage());
//            return null;
//        }
//    }
//
//    private String extractMeeshoImageUrl(WebElement item) {
//        try {
//            String imageUrl = findElementAttribute(item, "img", "src");
//
//            if (imageUrl == null || imageUrl.isEmpty()) {
//                imageUrl = findElementAttribute(item, "img", "data-src");
//            }
//
//            if (imageUrl == null || imageUrl.isEmpty()) {
//                WebElement img = item.findElement(By.tagName("img"));
//                imageUrl = img.getAttribute("src");
//                if (imageUrl == null || imageUrl.isEmpty()) {
//                    imageUrl = img.getAttribute("data-src");
//                }
//            }
//
//            if (imageUrl == null || imageUrl.isEmpty()) {
//                WebElement parent = item.findElement(By.xpath("./ancestor::*[contains(@class, 'product') or contains(@class, 'card')]"));
//                imageUrl = findElementAttribute(parent, "img", "src");
//                if (imageUrl == null || imageUrl.isEmpty()) {
//                    imageUrl = findElementAttribute(parent, "img", "data-src");
//                }
//            }
//
//            return imageUrl;
//        } catch (Exception e) {
//            logger.warning("Error extracting Meesho image URL: " + e.getMessage());
//            return null;
//        }
//    }
//
//    private String findElementText(WebElement parent, String... selectors) {
//        for (String selector : selectors) {
//            try {
//                if ("*".equals(selector)) {
//                    String text = parent.getText().trim();
//                    if (!text.isEmpty()) return text;
//                } else {
//                    WebElement element = parent.findElement(By.cssSelector(selector));
//                    String text = element.getText().trim();
//                    if (!text.isEmpty()) return text;
//                }
//            } catch (Exception e) {
//                // Try next selector
//            }
//        }
//        return "";
//    }
//
//    private String findElementAttribute(WebElement parent, String selector, String attribute) {
//        try {
//            WebElement element = parent.findElement(By.cssSelector(selector));
//            return element.getAttribute(attribute);
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//    private String extractBrand(String title) {
//        String[] commonBrands = {"Samsung", "Apple", "OnePlus", "Xiaomi", "Realme", "Oppo", "Vivo",
//                "HP", "Dell", "Lenovo", "Asus", "Acer", "Microsoft", "LG", "Sony",
//                "Canon", "Nikon", "Boat", "JBL", "Philips", "Haier", "Whirlpool", "IFB"};
//
//        for (String brand : commonBrands) {
//            if (title.toUpperCase().contains(brand.toUpperCase())) {
//                return brand;
//            }
//        }
//
//        if (title.length() > 15) {
//            String firstWord = title.split(" ")[0];
//            if (firstWord.length() > 2) {
//                return firstWord;
//            }
//        }
//
//        return "Unknown";
//    }
//}

package com.smartShopper.smart_price_backend.service.scraper;

import com.smartShopper.smart_price_backend.dto.product.ProductResponse;
import com.smartShopper.smart_price_backend.entity.Product;
import com.smartShopper.smart_price_backend.service.product.ProductService;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;

@Service
public class ScraperNowService {

    private static final Logger logger = Logger.getLogger(ScraperNowService.class.getName());

    @Autowired
    private ProductService productService;

    private ChromeOptions getChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments(
                "--headless",
                "--disable-blink-features=AutomationControlled",
                "--no-sandbox",
                "--disable-gpu",
                "--disable-extensions",
                "--disable-dev-shm-usage",
                "--window-size=1920,1080",
                "--disable-web-security",
                "--allow-running-insecure-content",
                "--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
        );
        options.setExperimentalOption("excludeSwitches", Arrays.asList("enable-automation", "enable-logging"));
        options.setExperimentalOption("useAutomationExtension", false);
        return options;
    }

    private WebDriver setupDriver() {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver(getChromeOptions());

        if (driver instanceof ChromeDriver) {
            ((ChromeDriver) driver).executeCdpCommand("Page.addScriptToEvaluateOnNewDocument", Map.of(
                    "source", """
                    Object.defineProperty(navigator, 'webdriver', {
                        get: () => undefined
                    });
                    window.chrome = { runtime: {} };
                    """
            ));
        }
        return driver;
    }

    public List<ProductResponse> scrapeAmazonByQuery(String query) throws Exception {
        List<ProductResponse> products = new ArrayList<>();
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
        String url = "https://www.amazon.in/s?k=" + encodedQuery;

        WebDriver driver = setupDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        try {
            logger.info("🔍 Starting Amazon scraping for query: " + query);
            driver.get(url);

            Thread.sleep(3000 + new Random().nextInt(2000));

            if (isAmazonBlocked(driver)) {
                logger.severe("❌ Amazon blocked the request");
                throw new RuntimeException("Amazon blocked the request");
            }

            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("div[data-component-type='s-search-result']")
            ));

            scrollPage(driver);

            List<WebElement> items = driver.findElements(By.cssSelector("div[data-component-type='s-search-result']"));
            logger.info("📦 Found " + items.size() + " product elements on Amazon");

            for (WebElement item : items) {
                try {
                    Product product = extractAmazonProductInfo(item, driver);
                    if (product != null) {
                        products.add(productService.convertToResponse(product));

                        if (products.size() >= 20) break; // Limit to 20 products
                    }
                } catch (Exception e) {
                    logger.warning("⚠️ Error extracting Amazon product: " + e.getMessage());
                }
            }

            logger.info("✅ Amazon scraping completed: " + products.size() + " products");
        } finally {
            driver.quit();
        }
        return products;
    }

    public List<ProductResponse> scrapeMeeshoByQuery(String query) throws Exception {
        List<ProductResponse> products = new ArrayList<>();
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
        String url = "https://www.meesho.com/search?q=" + encodedQuery;

        WebDriver driver = setupDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(25));

        try {
            logger.info("🔍 Starting Meesho scraping for query: " + query);
            driver.get(url);

            Thread.sleep(4000 + new Random().nextInt(2000));

            if (isMeeshoBlocked(driver)) {
                logger.severe("❌ Meesho blocked the request");
                throw new RuntimeException("Meesho blocked the request");
            }

            try {
                wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector("a[href*='/p/']")
                ));
            } catch (TimeoutException e) {
                logger.warning("⏱️ Timeout waiting for Meesho products");
            }

            scrollPage(driver);

            // Find product cards
            List<WebElement> items = driver.findElements(By.cssSelector("a[href*='/p/']"));
            logger.info("📦 Found " + items.size() + " product elements on Meesho");

            for (WebElement item : items) {
                try {
                    Product product = extractMeeshoProductInfo(item, driver);
                    if (product != null) {
                        products.add(productService.convertToResponse(product));

                        if (products.size() >= 20) break; // Limit to 20 products
                    }
                } catch (Exception e) {
                    logger.warning("⚠️ Error extracting Meesho product: " + e.getMessage());
                }
            }

            logger.info("✅ Meesho scraping completed: " + products.size() + " products");
        } finally {
            driver.quit();
        }
        return products;
    }

    private Product extractAmazonProductInfo(WebElement item, WebDriver driver) {
        try {
            // Extract title
            String title = safeGetText(item,
                    "h2 span.a-text-normal",
                    "h2.a-size-mini span",
                    ".a-size-base-plus"
            );

            if (title == null || title.trim().isEmpty() || title.length() < 5) {
                return null;
            }

            // Extract price - Updated selectors based on HTML
            String priceText = safeGetText(item,
                    ".a-price-whole",
                    ".a-price .a-offscreen",
                    "span.a-price-whole"
            );

            if (priceText == null || priceText.trim().isEmpty()) {
                logger.info("⚠️ No price found for: " + title.substring(0, Math.min(50, title.length())));
                return null;
            }

            // Clean price
            priceText = priceText.replaceAll("[^\\d.]", "").trim();
            if (priceText.isEmpty()) {
                return null;
            }

            BigDecimal price;
            try {
                price = new BigDecimal(priceText);
                if (price.compareTo(BigDecimal.ZERO) <= 0) {
                    return null;
                }
            } catch (NumberFormatException e) {
                logger.warning("❌ Invalid price format: " + priceText);
                return null;
            }

            // Extract product URL
            String productUrl = safeGetAttribute(item, "h2 a", "href");
            if (productUrl != null && !productUrl.startsWith("http")) {
                productUrl = "https://www.amazon.in" + productUrl;
            }

            // Extract image
            String imageUrl = safeGetAttribute(item, "img.s-image", "src");
            if (imageUrl == null || imageUrl.isEmpty()) {
                imageUrl = safeGetAttribute(item, "img", "src");
            }

            // Extract rating
            Double rating = null;
            String ratingText = safeGetText(item, ".a-icon-alt", "i.a-icon-star-small span");
            if (ratingText != null && !ratingText.isEmpty()) {
                try {
                    String[] parts = ratingText.split(" ");
                    if (parts.length > 0) {
                        rating = Double.parseDouble(parts[0]);
                    }
                } catch (Exception e) {
                    // Rating extraction failed, skip
                }
            }

            // Extract review count
            Integer reviewCount = null;
            String reviewText = safeGetText(item,
                    "span.a-size-base.s-underline-text",
                    ".a-size-base"
            );
            if (reviewText != null && !reviewText.isEmpty()) {
                try {
                    String cleaned = reviewText.replaceAll("[^0-9]", "");
                    if (!cleaned.isEmpty()) {
                        reviewCount = Integer.parseInt(cleaned);
                    }
                } catch (Exception e) {
                    // Review count extraction failed
                }
            }

            // Create product
            Product product = new Product();
            product.setTitle(title.trim());
            product.setBrand(extractBrand(title));
            product.setPlatform("Amazon");
            product.setPlatformProductUrl(productUrl != null ? productUrl : "");
            product.setCurrentPrice(price);
            product.setImageUrl(imageUrl != null ? imageUrl : "");
            product.setRating(rating);
            product.setReviewCount(reviewCount);
            product.setLastScraped(LocalDateTime.now());
            product.setCategory("General");

            logger.info("✅ Extracted Amazon: " + title.substring(0, Math.min(50, title.length())) + " - ₹" + price);
            return product;

        } catch (Exception e) {
            logger.warning("❌ Error in extractAmazonProductInfo: " + e.getMessage());
            return null;
        }
    }

    private Product extractMeeshoProductInfo(WebElement item, WebDriver driver) {
        try {
            // Extract product URL first
            String productUrl = item.getAttribute("href");
            if (productUrl == null || !productUrl.contains("/p/")) {
                return null;
            }
            if (!productUrl.startsWith("http")) {
                productUrl = "https://www.meesho.com" + productUrl;
            }

            // Extract title - Updated selectors based on HTML
            String title = safeGetText(item,
                    "p.sc-dOfePm.ivsDoe",
                    "p[font-size='16px']",
                    ".NewProductCardstyled__StyledDesktopProductTitle-sc-6y2tys-5"
            );

            if (title == null || title.trim().isEmpty() || title.length() < 5) {
                logger.info("⚠️ Meesho: Empty or invalid title");
                return null;
            }

            // Extract price - Updated selectors
            String priceText = safeGetText(item,
                    "h5.sc-dOfePm.jSZBdj",
                    "h5[font-size='24px']",
                    ".NewProductCardstyled__PriceRow-sc-6y2tys-7 h5"
            );

            if (priceText == null || priceText.trim().isEmpty()) {
                logger.info("⚠️ No price for Meesho product: " + title.substring(0, Math.min(30, title.length())));
                return null;
            }

            // Clean price
            priceText = priceText.replaceAll("[^\\d.]", "").trim();
            if (priceText.isEmpty()) {
                return null;
            }

            BigDecimal price;
            try {
                price = new BigDecimal(priceText);
                if (price.compareTo(BigDecimal.ZERO) <= 0) {
                    return null;
                }
            } catch (NumberFormatException e) {
                logger.warning("❌ Invalid Meesho price: " + priceText);
                return null;
            }

            // Extract image - Updated selectors
            String imageUrl = safeGetAttribute(item, "img", "src");
            if (imageUrl == null || imageUrl.isEmpty()) {
                imageUrl = safeGetAttribute(item, "img", "data-src");
            }

            // Extract rating
            Double rating = null;
            String ratingText = safeGetText(item,
                    "span.sc-dOfePm.jklcNf",
                    ".Rating__StyledPill-sc-12htng8-1 span"
            );
            if (ratingText != null && !ratingText.isEmpty()) {
                try {
                    rating = Double.parseDouble(ratingText.trim());
                } catch (Exception e) {
                    // Rating extraction failed
                }
            }

            // Extract review count
            Integer reviewCount = null;
            String reviewText = safeGetText(item,
                    ".NewProductCardstyled__RatingCount-sc-6y2tys-22",
                    "span[color='greyT2']"
            );
            if (reviewText != null && !reviewText.isEmpty()) {
                try {
                    String cleaned = reviewText.replaceAll("[^0-9]", "");
                    if (!cleaned.isEmpty()) {
                        reviewCount = Integer.parseInt(cleaned);
                    }
                } catch (Exception e) {
                    // Review count extraction failed
                }
            }

            // Create product
            Product product = new Product();
            product.setTitle(title.trim());
            product.setBrand(extractBrand(title));
            product.setPlatform("Meesho");
            product.setPlatformProductUrl(productUrl);
            product.setCurrentPrice(price);
            product.setImageUrl(imageUrl != null ? imageUrl : "");
            product.setRating(rating);
            product.setReviewCount(reviewCount);
            product.setLastScraped(LocalDateTime.now());
            product.setCategory("General");

            logger.info("✅ Extracted Meesho: " + title.substring(0, Math.min(50, title.length())) + " - ₹" + price);
            return product;

        } catch (Exception e) {
            logger.warning("❌ Error in extractMeeshoProductInfo: " + e.getMessage());
            return null;
        }
    }

    // Helper methods
    private String safeGetText(WebElement parent, String... selectors) {
        for (String selector : selectors) {
            try {
                WebElement element = parent.findElement(By.cssSelector(selector));
                String text = element.getText();
                if (text != null && !text.trim().isEmpty()) {
                    return text.trim();
                }
            } catch (Exception e) {
                // Try next selector
            }
        }
        return null;
    }

    private String safeGetAttribute(WebElement parent, String selector, String attribute) {
        try {
            WebElement element = parent.findElement(By.cssSelector(selector));
            return element.getAttribute(attribute);
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isAmazonBlocked(WebDriver driver) {
        try {
            return driver.findElements(By.cssSelector("form[action*='captcha'], #captchacharacters")).size() > 0 ||
                    driver.getPageSource().contains("sorry we just need to make sure") ||
                    driver.getTitle().contains("Bot Check");
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isMeeshoBlocked(WebDriver driver) {
        try {
            return driver.getTitle().contains("Access Denied") ||
                    driver.getPageSource().contains("security check") ||
                    driver.getPageSource().contains("blocked");
        } catch (Exception e) {
            return false;
        }
    }

    private void scrollPage(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        try {
            for (int i = 0; i < 5; i++) {
                js.executeScript("window.scrollBy(0, 500);");
                Thread.sleep(500);
            }
            js.executeScript("window.scrollTo(0, 0);");
            Thread.sleep(1000);
        } catch (Exception e) {
            logger.warning("⚠️ Scroll error: " + e.getMessage());
        }
    }

    private String extractBrand(String title) {
        String[] commonBrands = {
                "Samsung", "Apple", "OnePlus", "Xiaomi", "Realme", "Oppo", "Vivo",
                "HP", "Dell", "Lenovo", "Asus", "Acer", "Microsoft", "LG", "Sony",
                "Canon", "Nikon", "Boat", "JBL", "Philips", "Haier", "Whirlpool", "IFB"
        };

        for (String brand : commonBrands) {
            if (title.toUpperCase().contains(brand.toUpperCase())) {
                return brand;
            }
        }

        // Return first word if longer than 2 chars
        String[] words = title.split(" ");
        if (words.length > 0 && words[0].length() > 2) {
            return words[0];
        }

        return "Unknown";
    }
}