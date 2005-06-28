package org.nanocontainer.nanoweb.example.bookmarker.hb3;

import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratorType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.nanocontainer.nanoweb.example.bookmarker.spi.Bookmark;

@Entity(access = AccessType.PROPERTY)
@Table(name = "tb_bookmark")
public class BookmarkImpl implements Bookmark {

    private Integer id;
    private String url;
    private String name;
    private String description;

    // ---- PK

    @Id(generate = GeneratorType.IDENTITY)
    @Column(name = "id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    // ---- Fields

    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "url")
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Bookmark)) {
            return false;
        }

        Bookmark value = (Bookmark) o;
        return ObjectUtils.equals(getId(), value.getId());
    }

    @Override
    public int hashCode() {
        if (getId() == null) {
            return 0;
        }

        return getId().hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).append("id", id).append("url", url).append("name", name).append("description", description).toString();
    }

}
