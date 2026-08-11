import os
import time
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.common.exceptions import TimeoutException, NoSuchElementException

class BasePage:
    """Base Page class containing reusable Selenium browser interaction methods."""
    
    def __init__(self, driver, timeout=10):
        self.driver = driver
        self.timeout = timeout
        self.wait = WebDriverWait(driver, timeout)

    def open(self, url):
        """Navigate to specified URL."""
        self.driver.get(url)

    def find(self, locator):
        """Wait for element to be visible and return it."""
        return self.wait.until(EC.visibility_of_element_located(locator))

    def find_all(self, locator):
        """Wait for elements to be present and return list."""
        return self.wait.until(EC.presence_of_all_elements_located(locator))

    def click(self, locator):
        """Wait for element to be clickable and click."""
        element = self.wait.until(EC.element_to_be_clickable(locator))
        element.click()

    def type(self, locator, text, clear_first=True):
        """Type text into target input field."""
        element = self.find(locator)
        if clear_first:
            element.clear()
        element.send_keys(text)

    def get_text(self, locator):
        """Get text of an element."""
        return self.find(locator).text

    def is_displayed(self, locator):
        """Check if an element is currently displayed on screen."""
        try:
            return self.find(locator).is_displayed()
        except (TimeoutException, NoSuchElementException):
            return False

    def take_screenshot(self, name_prefix="screenshot"):
        """Save a screenshot into screenshots folder."""
        screenshots_dir = os.path.join(os.getcwd(), "screenshots")
        os.makedirs(screenshots_dir, exist_ok=True)
        filename = f"{name_prefix}_{int(time.time())}.png"
        filepath = os.path.join(screenshots_dir, filename)
        self.driver.save_screenshot(filepath)
        return filepath
