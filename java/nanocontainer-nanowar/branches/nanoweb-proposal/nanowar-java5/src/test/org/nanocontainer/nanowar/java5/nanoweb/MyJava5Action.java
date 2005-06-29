package org.nanocontainer.nanowar.java5.nanoweb;

import java.util.ArrayList;
import java.util.List;

import org.nanocontainer.nanowar.nanoweb.Car;

public class MyJava5Action {

    private List<Car> cars = new ArrayList<Car>();

    public List<Car> getCars() {
        return cars;
    }

    public void setCars(List<Car> cars) {
        this.cars = cars;
    }

    public String execute() {
        return "Im a Java5 action";
    }

}
