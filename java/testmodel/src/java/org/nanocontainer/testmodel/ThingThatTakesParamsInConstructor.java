package org.picoextras.testmodel;

public class ThingThatTakesParamsInConstructor {
    private String value;
    private Integer intValue;

    public ThingThatTakesParamsInConstructor(String value, Integer intValue) {
        this.value = value;
        this.intValue = intValue;
    }

    public String getValue() {
        return value + intValue;
    }
}
