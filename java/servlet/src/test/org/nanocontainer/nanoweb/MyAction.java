package org.nanocontainer.nanoweb;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class MyAction {
    private int year;
    private String country;
    private List cars = new ArrayList();

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public List getCars() {
        return cars;
    }

    public void setCars(List cars) {
        this.cars = cars;
    }

    public String execute() {
        if(year > 2003) {
            return "success";
        } else {
            return "error";
        }
    }
}