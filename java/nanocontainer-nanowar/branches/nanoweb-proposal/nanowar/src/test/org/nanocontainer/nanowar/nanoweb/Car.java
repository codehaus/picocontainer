package org.nanocontainer.nanowar.nanoweb;

public class Car {

    private String name;

    public Car(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return "Car[" + name + "]";
    }

    public boolean equals(Object o) {
        if (!(o instanceof Car)) {
            return false;
        }

        Car c = (Car) o;

        if ((name == null) && (c.getName() == null)) {
            return true;
        }

        if (name == null) {
            return false;
        }

        return name.equals(c.getName());
    }

    public int hashCode() {
        if (name == null) {
            return 0;
        }

        return this.name.hashCode();
    }

}
