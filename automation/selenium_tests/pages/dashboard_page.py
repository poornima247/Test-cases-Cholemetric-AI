from selenium.webdriver.common.by import By
from .base_page import BasePage

class DashboardPage(BasePage):
    """Page Object Model for Dashboard Page."""
    
    # Locators
    DOCTOR_NAME_HEADER = (By.ID, "display_doctor_name")
    NEW_ANALYSIS_BTN = (By.XPATH, "//a[contains(@href, 'new_analysis')]")
    PATIENT_HISTORY_BTN = (By.XPATH, "//a[contains(@href, 'patient_history')]")
    SETTINGS_BTN = (By.XPATH, "//a[contains(@href, 'settings')]")
    LOGOUT_BTN = (By.ID, "btn_logout")
    TOTAL_SCANS_CARD = (By.ID, "stat_total_scans")
    CONFIRMED_STONES_CARD = (By.ID, "stat_positive_cases")

    def navigate_to_dashboard(self, base_url):
        self.open(f"{base_url}/dashboard.html")

    def is_dashboard_loaded(self):
        return self.is_displayed(self.NEW_ANALYSIS_BTN) or self.is_displayed(self.PATIENT_HISTORY_BTN)

    def click_new_analysis(self):
        self.click(self.NEW_ANALYSIS_BTN)

    def click_patient_history(self):
        self.click(self.PATIENT_HISTORY_BTN)
