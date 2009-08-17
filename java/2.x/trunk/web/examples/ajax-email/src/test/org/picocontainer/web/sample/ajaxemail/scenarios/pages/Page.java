package org.picocontainer.web.sample.ajaxemail.scenarios.pages;

import com.thoughtworks.selenium.condition.*;
import com.thoughtworks.selenium.Selenium;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class Page {

	protected final Selenium selenium;
	protected final ConditionRunner runner;

	public Page(Selenium selenium, ConditionRunner runner) {
		this.selenium = selenium;
		this.runner = runner;
	}

	/**
	 * Waits for a number of seconds
	 * 
	 * @param seconds
	 *            the number of seconds to sleep
	 */
	protected static void waitFor(int seconds) {
		try {
			TimeUnit.SECONDS.sleep(seconds);
		} catch (InterruptedException e) {
			// continue
		}
	}

	public void waitFor(Condition condition) {
		runner.waitFor(condition);
		waitFor(1);
	}

	public void textIsVisible(String text) {
		waitFor(new Text(text));
	}

	public void textIsNotVisible(String text) {
		waitFor(new Not(new Text(text)));
	}

    public String[] formFieldValues(String prefix, boolean fillEm) {
        String fieldsXpath = "//div[@id='"+prefix+"Message']//form" + "//input[@class=\"textfield\"]";
        int fields = selenium.getXpathCount(fieldsXpath).intValue() ;
        String[] values = new String[fields];
        for (int field = 1; field <= fields; field++) {
            String locator = "xpath=(" + fieldsXpath + ")[" + field + "]";
            if (fillEm) {
                selenium.type(locator, "Test:" + Math.random());
            }
            runner.waitFor(new NonBlank(locator));
            values[field-1] = selenium.getText(locator);
        }
        return values;
    }

    public static class NonBlank extends Presence {
        private String locator;

        public NonBlank(String locator) {
            super(locator);
            this.locator = locator;
        }

        @Override
        public boolean isTrue(ConditionRunner.Context runner) {
           String st = "";
           if (super.isTrue(runner)) {
               st = runner.getSelenium().getText(locator);
           }
           return !st.equals("");
        }
    }

}
