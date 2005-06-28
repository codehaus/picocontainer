package org.nanocontainer.nanoweb.tools.hb3.test.util;

import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratorType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(access = AccessType.PROPERTY)
@Table(name="dummy")
public class Dummy {

    private Integer id;
    private String name;
    private String bigString;;
    private String blobString;;

    @Id(generate=GeneratorType.IDENTITY)
    @Column(name="id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer value) {
        id = value;
    }

    @Column(name="name", length=123)
    public String getName() {
        return name;
    }

    public void setName(String value) {
        name = value;
    }
    
    @Column(name="bigString", length=2000)
    public String getBigString() {
        return bigString;
    }
    
    public void setBigString(String value) {
        this.bigString = value;
    }

    @Column(name="blobString", length=100000)
    public String getBlobString() {
        return this.blobString;
    }
    
    public void setBlobString(String blobString) {
        this.blobString = blobString;
    }

}
