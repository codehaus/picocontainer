package org.nanocontainer.nanowar.nanoweb.defaults;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.nanocontainer.nanowar.nanoweb.ScriptException;

public abstract class AbstractFileBasedActionFactory extends AbstractActionFactory {

	private String rootPath;

	private String extension;

	private final Map actionFileLoadTimestamps = new HashMap();

	private final Map actionClasses = new HashMap();

	public AbstractFileBasedActionFactory(final String rootPath, final String extension) {
		this.rootPath = rootPath;
		this.extension = extension;
	}

	protected abstract Class getClass(final File actionFile) throws Exception;

	protected final Class getClass(final String path) throws ScriptException {
		File actionFile = new File(rootPath + path + "." + extension);

		if (!actionFile.exists()) {
			return null;
		}

		Class actionClass = getFromCache(actionFile);

		if (actionClass == null) {
			try {
				actionClass = getClass(actionFile);
			} catch (Exception e) {
				throw new ScriptException(path, e);
			}
			setToCache(actionFile, actionClass);
		}

		return actionClass;
	}

	protected Class getFromCache(final File actionFile) {
		return getFromCache(actionFile, actionFile.lastModified());
	}

	/*
	 * In order to make this testable. (I can't mock a File object, even with jMock CGLIB)
	 */
	protected Class getFromCache(final File actionFile, final long actionFilelastModified) {
		Long lastActionFileLoadTimestamp = (Long) actionFileLoadTimestamps.get(actionFile.getPath());

		if (lastActionFileLoadTimestamp == null) {
			return null;
		}

		if (lastActionFileLoadTimestamp.longValue() > actionFilelastModified) {
			return (Class) actionClasses.get(actionFile.getPath());
		}

		return null;
	}

	protected void setToCache(final File actionFile, final Class scriptClass) {
		setToCache(actionFile, scriptClass, actionFile.lastModified());
	}

	/*
	 * In order to make this testable. (I can't mock a File object, even with jMock CGLIB)
	 */
	protected void setToCache(final File actionFile, final Class scriptClass, final long actionFilelastModified) {
		if (scriptClass == null) {
			return;
		}

		actionClasses.put(actionFile.getPath(), scriptClass);
		actionFileLoadTimestamps.put(actionFile.getPath(), new Long(actionFilelastModified));
	}

}
