package nanocontainer.testmodel;

public class ThingThatTakesParamsInConstructor {
    String value;

    public ThingThatTakesParamsInConstructor(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
