package nanocontainer.reflection;

import java.util.HashMap;
import java.util.Map;

public class StringToObjectConverter {

    private final Map converters = new HashMap();

    public StringToObjectConverter() {
        register(String.class, new Converter() {
            public Object convert(String in) {
                return in;
            }
        });

        register(Integer.class, new Converter() {
            public Object convert(String in) {
                return in == null ? new Integer(0) : Integer.valueOf(in);
            }
        });

        register(Long.class, new Converter() {
            public Object convert(String in) {
                return in == null ? new Long(0) : Long.valueOf(in);
            }
        });

        register(Boolean.class, new Converter() {
            public Object convert(String in) {
                if (in == null || in.length() == 0) {
                    return Boolean.FALSE;
                }
                char c = in.toLowerCase().charAt(0);
                return c == '1' || c == 'y' || c == 't' ? Boolean.TRUE : Boolean.FALSE;
            }
        });
    }

    public Object convertTo(Class desiredClass, String inputString) {
        Converter converter = (Converter) converters.get(desiredClass);
        if (converter == null) {
            throw new InvalidConversionException("Cannot convert to type " + desiredClass.getName());
        }
        return converter.convert(inputString);
    }

    public void register(Class type, Converter converter) {
        converters.put(type, converter);
    }
}
