package org.nanocontainer.sample.nanoweb;

/**
 * Simple Nanoweb action. It's a POJO!
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class DemoAction {

	private String message = "hello";

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

    public String execute() {
        return "success";
    }
}