package com.cholemetric.automation.pages;

/**
 * HelpFaqPage — Page Object for HelpFaqActivity
 */
public class HelpFaqPage extends BasePage {
    private static final String PAGE_TITLE  = "com.cholemetric.app:id/tvTitle";
    private static final String FAQ_LIST    = "com.cholemetric.app:id/rvFaq";
    private static final String FAQ_ITEM    = "com.cholemetric.app:id/faqItem";
    private static final String SEARCH      = "com.cholemetric.app:id/etSearch";
    private static final String BACK_BTN    = "com.cholemetric.app:id/ivBack";
    private static final String EMPTY_STATE = "com.cholemetric.app:id/tvEmpty";

    public HelpFaqPage() { super(); }

    public void clickBack()          { tapById(BACK_BTN); }
    public void enterSearch(String q){ typeTextById(SEARCH, q); }

    public boolean isHelpFaqPageVisible() { return isVisibleById(FAQ_LIST); }
    public boolean isFaqListVisible()     { return isVisibleById(FAQ_LIST); }
    public boolean isEmptyStateVisible()  { return isVisibleById(EMPTY_STATE); }
    public boolean isOnHelpFaqActivity()  { return isOnActivity("HelpFaqActivity"); }

    public String getPageTitle() { try { return getTextById(PAGE_TITLE); } catch (Exception e) { return ""; } }

    public void clickFaqItem(int index) {
        findAllById(FAQ_ITEM).get(index).click();
    }
}
