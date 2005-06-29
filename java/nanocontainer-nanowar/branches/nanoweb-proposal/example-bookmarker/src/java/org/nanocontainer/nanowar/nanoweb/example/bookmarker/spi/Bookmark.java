package org.nanocontainer.nanowar.nanoweb.example.bookmarker.spi;


public interface Bookmark {

    public Integer getId();
    
    public String getUrl();
    
    public void setUrl(String value);
    
    public String getName();
    
    public void setName(String value);
    
    public String getDescription();
    
    public void setDescription(String value);
    
}
