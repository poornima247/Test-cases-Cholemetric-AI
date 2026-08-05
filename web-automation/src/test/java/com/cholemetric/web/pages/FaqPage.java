package com.cholemetric.web.pages;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.util.List;

public class FaqPage extends BasePage {
    private static final By FAQ_ITEMS = By.cssSelector(".faq-item, .accordion-item");
    private static final By ACCORDION_SECTIONS = By.cssSelector(".accordion-button");
    private static final By SEARCH = By.id("search-faq");

    public FaqPage(WebDriver driver) { super(driver); }

    public void navigateTo(String baseUrl) { driver.get(baseUrl + "faq.html"); }
    public List<WebElement> getFaqItems() { return driver.findElements(FAQ_ITEMS); }
    public void enterSearch(String query) { type(SEARCH, query); }
    public void expandFirstAccordion() { 
        List<WebElement> sections = driver.findElements(ACCORDION_SECTIONS);
        if (!sections.isEmpty()) sections.get(0).click(); 
    }
}
