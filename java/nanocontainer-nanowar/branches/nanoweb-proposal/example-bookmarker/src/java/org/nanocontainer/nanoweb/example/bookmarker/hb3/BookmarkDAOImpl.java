package org.nanocontainer.nanoweb.example.bookmarker.hb3;

import java.util.Collection;

import org.hibernate.Session;
import org.nanocontainer.nanoweb.example.bookmarker.spi.Bookmark;
import org.nanocontainer.nanoweb.example.bookmarker.spi.BookmarkDAO;

public class BookmarkDAOImpl implements BookmarkDAO {

    private Session s;

    public BookmarkDAOImpl(Session s) {
        this.s = s;
    }

    public Bookmark create() {
        return new BookmarkImpl();
    }

    public void delete(Bookmark value) {
        s.delete(value);
    }

    public void save(Bookmark value) {
        s.saveOrUpdate(value);
    }

    public Bookmark findById(Integer id) {
        return (Bookmark) s.load(BookmarkImpl.class, id);
    }

    public Collection<Bookmark> findAll() {
        return s.createCriteria(BookmarkImpl.class).list();
    }

}
