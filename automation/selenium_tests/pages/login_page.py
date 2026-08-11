from selenium.webdriver.common.by import By
from .base_page import BasePage

class LoginPage(BasePage):
    """Page Object Model for Cholemetric Login Page."""
    
    # Locators
    EMAIL_INPUT = (By.ID, "login_email")
    PASSWORD_INPUT = (By.ID, "login_password")
    SIGNIN_BTN = (By.ID, "btn_signin")
    FORGOT_PASS_LINK = (By.XPATH, "//a[contains(@href, 'forgot_password')]")
    CREATE_ACCT_LINK = (By.XPATH, "//a[contains(@href, 'signup')]")
    BACK_BTN = (By.CLASS_NAME, "back-btn")
    HEADER_TITLE = (By.XPATH, "//h1[text()='Cholemetric']")

    def navigate_to_login(self, base_url):
        self.open(f"{base_url}/login_form.html")

    def enter_email(self, email):
        self.type(self.EMAIL_INPUT, email)

    def enter_password(self, password):
        self.type(self.PASSWORD_INPUT, password)

    def click_signin(self):
        self.click(self.SIGNIN_BTN)

    def login(self, email, password):
        self.enter_email(email)
        self.enter_password(password)
        self.click_signin()

    def is_login_page_loaded(self):
        return self.is_displayed(self.EMAIL_INPUT) and self.is_displayed(self.SIGNIN_BTN)
