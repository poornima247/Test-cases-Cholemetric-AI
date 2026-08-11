from selenium.webdriver.common.by import By
from .base_page import BasePage

class NewAnalysisPage(BasePage):
    """Page Object Model for CT Scan New Analysis Page."""
    
    PATIENT_ID_INPUT = (By.ID, "patient_id")
    PATIENT_NAME_INPUT = (By.ID, "patient_name")
    AGE_INPUT = (By.ID, "patient_age")
    GENDER_SELECT = (By.ID, "patient_gender")
    FILE_INPUT = (By.ID, "scan_file_input")
    ANALYZE_BTN = (By.ID, "btn_run_analysis")
    RESULTS_CONTAINER = (By.ID, "analysis_results_container")
    DIAGNOSIS_LABEL = (By.ID, "diagnosis_result")

    def navigate_to_new_analysis(self, base_url):
        self.open(f"{base_url}/new_analysis.html")

    def upload_scan_and_analyze(self, patient_id, name, age, gender, file_path):
        self.type(self.PATIENT_ID_INPUT, patient_id)
        self.type(self.PATIENT_NAME_INPUT, name)
        self.type(self.AGE_INPUT, str(age))
        if self.is_displayed(self.FILE_INPUT):
            self.find(self.FILE_INPUT).send_keys(file_path)
        self.click(self.ANALYZE_BTN)

    def is_analysis_page_loaded(self):
        return self.is_displayed(self.PATIENT_NAME_INPUT) and self.is_displayed(self.ANALYZE_BTN)
