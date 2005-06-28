package org.nanocontainer.nanoweb.example.bookmarker.spi;

import java.util.Collection;

public interface BookmarkDAO {

    public Bookmark create();

    public Bookmark findById(Integer id);

    public Collection<Bookmark> findAll();

    public void delete(Bookmark value);

    public void save(Bookmark value);

}
