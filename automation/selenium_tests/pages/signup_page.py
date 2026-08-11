from selenium.webdriver.common.by import By
from .base_page import BasePage

class SignupPage(BasePage):
    """Page Object Model for Cholemetric Signup Page."""
    
    # Locators
    FULLNAME_INPUT = (By.ID, "full_name")
    EMAIL_INPUT = (By.ID, "signup_email")
    MEDICAL_ID_INPUT = (By.ID, "medical_id")
    PASSWORD_INPUT = (By.ID, "signup_password")
    CONFIRM_PASS_INPUT = (By.ID, "confirm_password")
    SIGNUP_BTN = (By.ID, "btn_signup")
    LOGIN_LINK = (By.XPATH, "//a[contains(@href, 'login_form')]")

    def navigate_to_signup(self, base_url):
        self.open(f"{base_url}/signup.html")

    def register_user(self, name, email, medical_id, password):
        self.type(self.FULLNAME_INPUT, name)
        self.type(self.EMAIL_INPUT, email)
        self.type(self.MEDICAL_ID_INPUT, medical_id)
        self.type(self.PASSWORD_INPUT, password)
        self.type(self.CONFIRM_PASS_INPUT, password)
        self.click(self.SIGNUP_BTN)

    def is_signup_page_loaded(self):
        return self.is_displayed(self.FULLNAME_INPUT) and self.is_displayed(self.SIGNUP_BTN)
