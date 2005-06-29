package org.nanocontainer.nanowar.nanoweb;


/**
 * @version $Revision: 1.1 $
 */
public class MyAction {

    // These variable are named "valueOf" and they are public and static for test sake.
    public int valueOfYear;
    public String valueOfCountry;
    public Car[] valueOfCars;

    public MyAction() {
        valueOfYear = 0;
        valueOfCountry = null;
        valueOfCars = new Car[0];
    }

    public int getYear() {
        return valueOfYear;
    }

    public void setYear(int year) {
        valueOfYear = year;
    }

    public String getCountry() {
        return valueOfCountry;
    }

    public void setCountry(String country) {
        valueOfCountry = country;
    }

    public Car[] getCars() {
        return valueOfCars;
    }

    public void setCars(Car[] cars) {
        valueOfCars = cars;
    }

    public String execute() {
        if (valueOfYear > 2003) {
            return "success";
        }
        return "error";
    }
}
