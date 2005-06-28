package org.nanocontainer.nanoweb;

/**
 * @version $Revision: 1.1 $
 */
public class ScriptException extends Exception {

	private static final long serialVersionUID = 3832621776860753974L;

	private final String scriptName;

	public ScriptException(String scriptName, Exception e) {
		super(e);
		this.scriptName = scriptName;
	}

	public String getScriptName() {
		return scriptName;
	}
}