package org.nanocontainer.sample.nanoweb;

/**
 * Simple Nanoweb action. It's a POJO!
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class DemoAction {

	private String magic = "This is not correct";

	public void setMagic(String magic) {
		this.magic = magic;
	}

	public String getMagic() {
		return magic;
	}

    public String execute() {
        if(magic.equals("NanoWeb")) {
            return "input";
        } else {
            return "success";
        }
    }
}