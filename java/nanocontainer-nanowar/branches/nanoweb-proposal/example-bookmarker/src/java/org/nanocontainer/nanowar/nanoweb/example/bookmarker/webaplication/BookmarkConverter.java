package org.nanocontainer.nanowar.nanoweb.example.bookmarker.webaplication;

import org.nanocontainer.nanowar.nanoweb.Converter;
import org.nanocontainer.nanowar.nanoweb.example.bookmarker.spi.Bookmark;
import org.nanocontainer.nanowar.nanoweb.example.bookmarker.spi.BookmarkDAO;

public class BookmarkConverter implements Converter {

	private BookmarkDAO dao;

	public BookmarkConverter(BookmarkDAO dao) {
		this.dao = dao;
	}

	public Object fromString(String value) {
		if ((value == null) || value.equals("")) {
			return dao.create();
		}

		try {
			return dao.findById(Integer.valueOf(value));
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public String toString(Object value) {
		if (!(value instanceof Bookmark)) {
			return null;
		}

		return ((Bookmark) value).getId().toString();
	}

}
