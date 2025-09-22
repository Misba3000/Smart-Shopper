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
//    // Common configuration
//    private ChromeOptions getChromeOptions() {
//        ChromeOptions options = new ChromeOptions();
//        options.addArguments(
//                "--headless",  // Run in headless mode for production
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
//        // Remove automation indicators
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
//    // Amazon scraper
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
//            // Add random delay to mimic human behavior
//            Thread.sleep(3000 + new Random().nextInt(2000));
//
//            // Check if blocked
//            if (isAmazonBlocked(driver)) {
//                logger.severe("Amazon blocked the request");
//                throw new RuntimeException("Amazon blocked the request");
//            }
//
//            // Wait for products to load
//            wait.until(ExpectedConditions.presenceOfElementLocated(
//                    By.cssSelector("[data-component-type='s-search-result'], .s-result-item")
//            ));
//
//            // Scroll to load more products
//            scrollPage(driver);
//
//            // Find product elements
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
//    // Meesho scraper - Updated with correct selectors
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
//            // Check for blocking
//            if (isMeeshoBlocked(driver)) {
//                logger.severe("Meesho blocked the request");
//                throw new RuntimeException("Meesho blocked the request");
//            }
//
//            // Wait for products to load with updated selectors
//            try {
//                wait.until(ExpectedConditions.or(
//                        ExpectedConditions.presenceOfElementLocated(By.cssSelector("a[href*='/p/']")),
//                        ExpectedConditions.presenceOfElementLocated(By.cssSelector(".sc-bcXHqe")),
//                        ExpectedConditions.presenceOfElementLocated(By.cssSelector(".NewProductCard")),
//                        ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-testid*='product']"))
//                ));
//            } catch (TimeoutException e) {
//                logger.warning("Timeout waiting for Meesho products to load, trying to continue");
//            }
//
//            scrollPage(driver);
//
//            // Find product elements with updated logic
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
//            // Scroll gradually to load all products
//            long pageHeight = (Long) js.executeScript("return document.body.scrollHeight");
//            for (int i = 0; i < 10; i++) {
//                js.executeScript("window.scrollTo(0, " + (pageHeight * i / 10) + ");");
//                Thread.sleep(800);
//            }
//            // Scroll back to top
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
//        // Updated selectors based on actual Meesho HTML structure
//        String[] meeshoSelectors = {
//                "a[href*='/p/']",  // Primary selector for product links
//                ".sc-bcXHqe",      // Container elements
//                ".NewProductCard", // Product card container
//                "[data-testid*='product']", // Elements with product test IDs
//                ".sc-dkrFOg",      // Alternative container
//                "div[class*='Product']" // Any div with Product in class name
//        };
//
//        for (String selector : meeshoSelectors) {
//            try {
//                List<WebElement> items = driver.findElements(By.cssSelector(selector));
//                logger.info("Selector '" + selector + "' found " + items.size() + " elements");
//                if (!items.isEmpty()) {
//                    // Filter out items that don't contain product information
//                    List<WebElement> validItems = new ArrayList<>();
//                    for (WebElement item : items) {
//                        try {
//                            // Check if the element contains actual product data
//                            if (containsProductData(item)) {
//                                validItems.add(item);
//                            }
//                        } catch (Exception e) {
//                            // Skip invalid items
//                        }
//                    }
//                    if (!validItems.isEmpty()) {
//                        logger.info("Found " + validItems.size() + " valid product items");
//                        return validItems;
//                    }
//                }
//            } catch (Exception e) {
//                logger.info("Meesho selector " + selector + " failed: " + e.getMessage());
//            }
//        }
//
//        // Fallback: try to find any element with product-like content
//        try {
//            List<WebElement> allElements = driver.findElements(By.cssSelector("*"));
//            List<WebElement> productElements = new ArrayList<>();
//
//            for (WebElement element : allElements) {
//                try {
//                    String text = element.getText().toLowerCase();
//                    String className = element.getAttribute("class");
//
//                    if ((text.contains("₹") && text.length() < 500) ||
//                            (className != null && className.toLowerCase().contains("product"))) {
//                        productElements.add(element);
//                        if (productElements.size() >= 20) break; // Limit fallback results
//                    }
//                } catch (Exception e) {
//                    // Skip this element
//                }
//            }
//
//            logger.info("Fallback found " + productElements.size() + " potential product elements");
//            return productElements;
//        } catch (Exception e) {
//            logger.warning("Fallback search failed: " + e.getMessage());
//        }
//
//        return new ArrayList<>();
//    }
//
//    private boolean containsProductData(WebElement element) {
//        try {
//            String text = element.getText();
//            String href = element.getAttribute("href");
//
//            // Check if element contains price (₹ symbol) or is a product link
//            return text.contains("₹") ||
//                    (href != null && href.contains("/p/")) ||
//                    text.matches(".*\\d+.*"); // Contains numbers (likely price)
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//    private ProductResponse extractAmazonProductInfo(WebElement item) {
//        try {
//            // Extract title
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
//            // Extract price
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
//            // Extract product URL
//            String productUrl = findElementAttribute(item, "h2 a.a-link-normal", "href");
//            if (productUrl != null && !productUrl.startsWith("http")) {
//                productUrl = "https://www.amazon.in" + productUrl;
//            }
//
//            // Extract image URL
//            String imageUrl = findElementAttribute(item, ".s-image", "src");
//
//            // Extract rating
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
//            // Extract review count
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
//    private ProductResponse extractMeeshoProductInfo(WebElement item) {
//        try {
//            String title = "";
//            String priceText = "";
//            String productUrl = "";
//            String imageUrl = "";
//
//            // Try multiple approaches to extract data
//
//            // Approach 1: If this is a product link element
//            String href = item.getAttribute("href");
//            if (href != null && href.contains("/p/")) {
//                productUrl = href.startsWith("http") ? href : "https://www.meesho.com" + href;
//
//                // Get title and price from the link element or its children
//                title = findElementText(item, "*");
//
//                // Look for price in the element or nearby elements
//                WebElement parent = item;
//                try {
//                    parent = item.findElement(By.xpath("./ancestor::*[contains(@class, 'sc-') or contains(@class, 'Product')]"));
//                } catch (Exception e) {
//                    // Use the current element
//                }
//
//                priceText = extractPriceFromElement(parent);
//
//                // Try to find image
//                imageUrl = findElementAttribute(item, "img", "src");
//                if (imageUrl == null) {
//                    imageUrl = findElementAttribute(parent, "img", "src");
//                }
//            }
//
//            // Approach 2: Extract from element text content
//            if (title.isEmpty() || priceText.isEmpty()) {
//                String elementText = item.getText();
//                String[] lines = elementText.split("\\n");
//
//                for (String line : lines) {
//                    if (line.contains("₹") && priceText.isEmpty()) {
//                        priceText = line.replaceAll("[^\\d.]", "");
//                    } else if (!line.contains("₹") && line.length() > 10 && title.isEmpty()) {
//                        title = line.trim();
//                    }
//                }
//
//                if (productUrl.isEmpty()) {
//                    productUrl = findElementAttribute(item, "a[href*='/p/']", "href");
//                    if (productUrl != null && !productUrl.startsWith("http")) {
//                        productUrl = "https://www.meesho.com" + productUrl;
//                    }
//                }
//
//                if (imageUrl == null) {
//                    imageUrl = findElementAttribute(item, "img", "src");
//                    if (imageUrl == null) {
//                        imageUrl = findElementAttribute(item, "img", "data-src");
//                    }
//                }
//            }
//
//            // Validate extracted data
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
//                    null,  // Meesho doesn't typically show ratings on search results
//                    null,  // Meesho doesn't typically show review counts on search results
//                    ""
//            );
//
//        } catch (Exception e) {
//            logger.warning("Error extracting Meesho product: " + e.getMessage());
//            return null;
//        }
//    }
//
//    private String extractPriceFromElement(WebElement element) {
//        // Try multiple selectors to find price
//        String[] priceSelectors = {
//                ".sc-eDvSVe",
//                ".sc-jSFkmK",
//                "[class*='Price']",
//                "*[class*='price']"
//        };
//
//        for (String selector : priceSelectors) {
//            try {
//                List<WebElement> priceElements = element.findElements(By.cssSelector(selector));
//                for (WebElement priceElement : priceElements) {
//                    String text = priceElement.getText();
//                    if (text.contains("₹")) {
//                        return text.replaceAll("[^\\d.]", "");
//                    }
//                }
//            } catch (Exception e) {
//                // Try next selector
//            }
//        }
//
//        // Fallback: search in all text content
//        String allText = element.getText();
//        if (allText.contains("₹")) {
//            String[] parts = allText.split("₹");
//            if (parts.length > 1) {
//                String priceCandidate = parts[1].trim().split("\\s+")[0];
//                return priceCandidate.replaceAll("[^\\d.]", "");
//            }
//        }
//
//        return "";
//    }
//
//    // Helper methods
//    private String findElementText(WebElement parent, String... selectors) {
//        for (String selector : selectors) {
//            try {
//                if ("*".equals(selector)) {
//                    // Get all text from element
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
//        // Try to extract brand from the beginning of the title
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
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.logging.Logger;

@Service
public class ScraperNowService {

    private static final Logger logger = Logger.getLogger(ScraperNowService.class.getName());

    // Common configuration
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
                "--dns-prefetch-disable",
                "--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
        );
        options.setExperimentalOption("excludeSwitches", Arrays.asList("enable-automation", "enable-logging"));
        options.setExperimentalOption("useAutomationExtension", false);
        return options;
    }

    private WebDriver setupDriver() {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver(getChromeOptions());

        // Remove automation indicators
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

    // Amazon scraper
    public List<ProductResponse> scrapeAmazonByQuery(String query) throws Exception {
        List<ProductResponse> products = new ArrayList<>();
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
        String url = "https://www.amazon.in/s?k=" + encodedQuery;

        WebDriver driver = setupDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        try {
            logger.info("Starting Amazon scraping for query: " + query);
            driver.get(url);

            // Add random delay to mimic human behavior
            Thread.sleep(3000 + new Random().nextInt(2000));

            // Check if blocked
            if (isAmazonBlocked(driver)) {
                logger.severe("Amazon blocked the request");
                throw new RuntimeException("Amazon blocked the request");
            }

            // Wait for products to load
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("[data-component-type='s-search-result'], .s-result-item")
            ));

            // Scroll to load more products
            scrollPage(driver);

            // Find product elements
            List<WebElement> items = findAmazonProducts(driver);
            logger.info("Found " + items.size() + " product elements on Amazon");

            for (WebElement item : items) {
                try {
                    ProductResponse product = extractAmazonProductInfo(item);
                    if (product != null) {
                        products.add(product);
                    }
                } catch (Exception e) {
                    logger.warning("Error extracting Amazon product: " + e.getMessage());
                }
            }
        } finally {
            driver.quit();
        }
        return products;
    }

    // Meesho scraper
    public List<ProductResponse> scrapeMeeshoByQuery(String query) throws Exception {
        List<ProductResponse> products = new ArrayList<>();
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
        String url = "https://www.meesho.com/search?q=" + encodedQuery;

        WebDriver driver = setupDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(25));

        try {
            logger.info("Starting Meesho scraping for query: " + query);
            driver.get(url);

            Thread.sleep(4000 + new Random().nextInt(2000));

            // Check for blocking
            if (isMeeshoBlocked(driver)) {
                logger.severe("Meesho blocked the request");
                throw new RuntimeException("Meesho blocked the request");
            }

            // Wait for products to load with updated selectors
            try {
                wait.until(ExpectedConditions.or(
                        ExpectedConditions.presenceOfElementLocated(By.cssSelector("a[href*='/p/']")),
                        ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-testid*='product']"))
                ));
            } catch (TimeoutException e) {
                logger.warning("Timeout waiting for Meesho products to load, trying to continue");
            }

            scrollPage(driver);

            // Find product elements with updated logic
            List<WebElement> items = findMeeshoProducts(driver);
            logger.info("Found " + items.size() + " product elements on Meesho");

            for (WebElement item : items) {
                try {
                    ProductResponse product = extractMeeshoProductInfo(item);
                    if (product != null) {
                        products.add(product);
                        if (products.size() >= 10) break;
                    }
                } catch (Exception e) {
                    logger.warning("Error extracting Meesho product: " + e.getMessage());
                }
            }
        } finally {
            driver.quit();
        }
        return products;
    }

    private boolean isAmazonBlocked(WebDriver driver) {
        try {
            return driver.findElements(By.cssSelector("form[action*='captcha'], #captchacharacters")).size() > 0 ||
                    driver.getPageSource().contains("sorry we just need to make sure") ||
                    driver.getPageSource().contains("enter the characters you see") ||
                    driver.getTitle().contains("Bot Check");
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isMeeshoBlocked(WebDriver driver) {
        try {
            return driver.getTitle().contains("Access Denied") ||
                    driver.getPageSource().contains("security check") ||
                    driver.getPageSource().contains("blocked") ||
                    driver.findElements(By.cssSelector("form[action*='captcha']")).size() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    private void scrollPage(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        try {
            // Scroll gradually to load all products
            long pageHeight = (Long) js.executeScript("return document.body.scrollHeight");
            for (int i = 0; i < 10; i++) {
                js.executeScript("window.scrollTo(0, " + (pageHeight * i / 10) + ");");
                Thread.sleep(800);
            }
            // Scroll back to top
            js.executeScript("window.scrollTo(0, 0);");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            logger.warning("Error during scrolling: " + e.getMessage());
        }
    }

    private List<WebElement> findAmazonProducts(WebDriver driver) {
        String[] amazonSelectors = {
                "div[data-component-type='s-search-result']",
                ".s-result-item",
                "[data-asin]"
        };

        for (String selector : amazonSelectors) {
            try {
                List<WebElement> items = driver.findElements(By.cssSelector(selector));
                if (!items.isEmpty()) return items;
            } catch (Exception e) {
                logger.info("Amazon selector " + selector + " failed: " + e.getMessage());
            }
        }
        return new ArrayList<>();
    }

    private List<WebElement> findMeeshoProducts(WebDriver driver) {
        // Updated selectors for Meesho
        String[] meeshoSelectors = {
                "a[href*='/p/']",  // Primary selector for product links
                "[data-testid*='product']", // Elements with product test IDs
                ".ProductList__GridCol-sc-8lnc8s-0", // Grid column
                ".plp-card" // Product card
        };

        for (String selector : meeshoSelectors) {
            try {
                List<WebElement> items = driver.findElements(By.cssSelector(selector));
                logger.info("Selector '" + selector + "' found " + items.size() + " elements");
                if (!items.isEmpty()) {
                    return items;
                }
            } catch (Exception e) {
                logger.info("Meesho selector " + selector + " failed: " + e.getMessage());
            }
        }

        return new ArrayList<>();
    }

    private ProductResponse extractAmazonProductInfo(WebElement item) {
        try {
            // Extract title
            String title = findElementText(item,
                    "h2 a span",
                    ".a-size-medium",
                    ".a-text-normal"
            );

            if (title.isEmpty()) {
                logger.warning("Skipping Amazon product with empty title");
                return null;
            }

            // Extract price
            String priceText = findElementText(item,
                    ".a-price .a-offscreen",
                    ".a-price-whole",
                    ".a-color-price"
            ).replaceAll("[^\\d.]", "");

            if (priceText.isEmpty()) {
                logger.warning("Amazon product '" + title + "' has no price");
                return null;
            }

            BigDecimal price;
            try {
                price = new BigDecimal(priceText);
            } catch (NumberFormatException e) {
                logger.warning("Invalid price format for Amazon product: " + title + ", price: " + priceText);
                return null;
            }

            // Extract product URL
            String productUrl = findElementAttribute(item, "h2 a.a-link-normal", "href");
            if (productUrl != null && !productUrl.startsWith("http")) {
                productUrl = "https://www.amazon.in" + productUrl;
            }

            // Extract image URL - improved method
            String imageUrl = extractAmazonImageUrl(item);

            // Extract rating
            Double rating = null;
            try {
                String ratingText = findElementText(item, ".a-icon-alt");
                if (!ratingText.isEmpty()) {
                    rating = Double.parseDouble(ratingText.split(" ")[0]);
                }
            } catch (Exception e) {
                logger.info("Could not extract rating for Amazon product: " + title);
            }

            // Extract review count
            Integer reviewCount = null;
            try {
                String reviewsText = findElementText(item, ".a-size-base").replaceAll("[^0-9]", "");
                if (!reviewsText.isEmpty()) {
                    reviewCount = Integer.parseInt(reviewsText);
                }
            } catch (Exception e) {
                logger.info("Could not extract review count for Amazon product: " + title);
            }

            return new ProductResponse(
                    title,
                    extractBrand(title),
                    "Amazon",
                    productUrl != null ? productUrl : "",
                    price,
                    imageUrl != null ? imageUrl : "",
                    "Amazon",
                    rating,
                    reviewCount,
                    ""
            );

        } catch (Exception e) {
            logger.warning("Error extracting Amazon product: " + e.getMessage());
            return null;
        }
    }

    private String extractAmazonImageUrl(WebElement item) {
        try {
            // Try multiple approaches to get the image URL
            String imageUrl = findElementAttribute(item, ".s-image", "src");

            // If not found, try data-src attribute
            if (imageUrl == null || imageUrl.isEmpty()) {
                imageUrl = findElementAttribute(item, ".s-image", "data-src");
            }

            // If still not found, try data-old-hires attribute
            if (imageUrl == null || imageUrl.isEmpty()) {
                imageUrl = findElementAttribute(item, ".s-image", "data-old-hires");
            }

            // If still not found, try to find any img element in the item
            if (imageUrl == null || imageUrl.isEmpty()) {
                WebElement img = item.findElement(By.tagName("img"));
                imageUrl = img.getAttribute("src");
                if (imageUrl == null || imageUrl.isEmpty()) {
                    imageUrl = img.getAttribute("data-src");
                }
            }

            return imageUrl;
        } catch (Exception e) {
            logger.warning("Error extracting Amazon image URL: " + e.getMessage());
            return null;
        }
    }

    private ProductResponse extractMeeshoProductInfo(WebElement item) {
        try {
            String title = "";
            String priceText = "";
            String productUrl = "";
            String imageUrl = "";

            // Try to find the product link
            String href = item.getAttribute("href");
            if (href != null && href.contains("/p/")) {
                productUrl = href.startsWith("http") ? href : "https://www.meesho.com" + href;
            } else {
                // If the current element is not a link, try to find a link inside it
                productUrl = findElementAttribute(item, "a[href*='/p/']", "href");
                if (productUrl != null && !productUrl.startsWith("http")) {
                    productUrl = "https://www.meesho.com" + productUrl;
                }
            }

            // Extract title
            title = findElementText(item,
                    "[data-testid*='product']",
                    ".plp-card__name",
                    ".product-name",
                    "h3",
                    "h4"
            );

            // Extract price
            priceText = findElementText(item,
                    "[data-testid*='price']",
                    ".plp-card__selling-price",
                    ".product-price",
                    ".selling-price"
            ).replaceAll("[^\\d.]", "");

            // Extract image URL
            imageUrl = extractMeeshoImageUrl(item);

            // Validate extracted data
            if (title.isEmpty() || title.length() < 5) {
                logger.warning("Skipping Meesho product with invalid title: " + title);
                return null;
            }

            if (priceText.isEmpty()) {
                logger.warning("Meesho product '" + title + "' has no price");
                return null;
            }

            BigDecimal price;
            try {
                price = new BigDecimal(priceText);
                if (price.compareTo(BigDecimal.ZERO) <= 0) {
                    logger.warning("Invalid price for Meesho product: " + title + ", price: " + priceText);
                    return null;
                }
            } catch (NumberFormatException e) {
                logger.warning("Invalid price format for Meesho product: " + title + ", price: " + priceText);
                return null;
            }

            return new ProductResponse(
                    title,
                    extractBrand(title),
                    "Meesho",
                    productUrl != null ? productUrl : "",
                    price,
                    imageUrl != null ? imageUrl : "",
                    "Meesho",
                    null,  // Meesho doesn't typically show ratings on search results
                    null,  // Meesho doesn't typically show review counts on search results
                    ""
            );

        } catch (Exception e) {
            logger.warning("Error extracting Meesho product: " + e.getMessage());
            return null;
        }
    }

    private String extractMeeshoImageUrl(WebElement item) {
        try {
            // Try multiple approaches to get the image URL
            String imageUrl = findElementAttribute(item, "img", "src");

            // If not found, try data-src attribute
            if (imageUrl == null || imageUrl.isEmpty()) {
                imageUrl = findElementAttribute(item, "img", "data-src");
            }

            // If still not found, try to find any img element in the item
            if (imageUrl == null || imageUrl.isEmpty()) {
                WebElement img = item.findElement(By.tagName("img"));
                imageUrl = img.getAttribute("src");
                if (imageUrl == null || imageUrl.isEmpty()) {
                    imageUrl = img.getAttribute("data-src");
                }
            }

            // If still not found, try to find image in parent elements
            if (imageUrl == null || imageUrl.isEmpty()) {
                WebElement parent = item.findElement(By.xpath("./ancestor::*[contains(@class, 'product') or contains(@class, 'card')]"));
                imageUrl = findElementAttribute(parent, "img", "src");
                if (imageUrl == null || imageUrl.isEmpty()) {
                    imageUrl = findElementAttribute(parent, "img", "data-src");
                }
            }

            return imageUrl;
        } catch (Exception e) {
            logger.warning("Error extracting Meesho image URL: " + e.getMessage());
            return null;
        }
    }

    // Helper methods
    private String findElementText(WebElement parent, String... selectors) {
        for (String selector : selectors) {
            try {
                if ("*".equals(selector)) {
                    // Get all text from element
                    String text = parent.getText().trim();
                    if (!text.isEmpty()) return text;
                } else {
                    WebElement element = parent.findElement(By.cssSelector(selector));
                    String text = element.getText().trim();
                    if (!text.isEmpty()) return text;
                }
            } catch (Exception e) {
                // Try next selector
            }
        }
        return "";
    }

    private String findElementAttribute(WebElement parent, String selector, String attribute) {
        try {
            WebElement element = parent.findElement(By.cssSelector(selector));
            return element.getAttribute(attribute);
        } catch (Exception e) {
            return null;
        }
    }

    private String extractBrand(String title) {
        String[] commonBrands = {"Samsung", "Apple", "OnePlus", "Xiaomi", "Realme", "Oppo", "Vivo",
                "HP", "Dell", "Lenovo", "Asus", "Acer", "Microsoft", "LG", "Sony",
                "Canon", "Nikon", "Boat", "JBL", "Philips", "Haier", "Whirlpool", "IFB"};

        for (String brand : commonBrands) {
            if (title.toUpperCase().contains(brand.toUpperCase())) {
                return brand;
            }
        }

        // Try to extract brand from the beginning of the title
        if (title.length() > 15) {
            String firstWord = title.split(" ")[0];
            if (firstWord.length() > 2) {
                return firstWord;
            }
        }

        return "Unknown";
    }
}