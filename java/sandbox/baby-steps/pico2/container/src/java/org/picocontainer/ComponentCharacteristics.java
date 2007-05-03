package org.picocontainer;

public class ComponentCharacteristics {

    private static final String _INJECTION = "injection";
    private static final String _CONSTRUCTOR = "constructor";
    private static final String _SETTER = "setter";
    private static final String _CACHE = "cache";
    private static final String _NOJMX = "no-jmx";

    private static final String FALSE = "FALSE";
    private static final String TRUE = "TRUE";

    public static final ComponentCharacteristic CDI = new ComponentCharacteristic() {
        public void mergeInto(ComponentCharacteristic rc) {
            rc.setProperty(_INJECTION, _CONSTRUCTOR);
        }
        public boolean isSoCharacterized(ComponentCharacteristic rc) {
            String s = rc.getProperty(_INJECTION);
            return s != null && s.equals(_CONSTRUCTOR);
        }
    };

    public static final ComponentCharacteristic SDI = new ComponentCharacteristic() {
        public void mergeInto(ComponentCharacteristic rc) {
            rc.setProperty(_INJECTION, _SETTER);
        }

        public boolean isSoCharacterized(ComponentCharacteristic rc) {
            String s = rc.getProperty(_INJECTION);
            return s != null && s.equals(_SETTER);
        }
    };

    public static final ComponentCharacteristic NOCACHE = new ComponentCharacteristic() {
        public void mergeInto(ComponentCharacteristic rc) {
            rc.setProperty(_CACHE, FALSE);
        }

        public boolean isSoCharacterized(ComponentCharacteristic rc) {
            String s = rc.getProperty(_CACHE);
            return s != null && s.equals(FALSE);
        }
    };

    public static final ComponentCharacteristic SINGLETON = new ComponentCharacteristic() {
        public void mergeInto(ComponentCharacteristic rc) {
            rc.setProperty(_CACHE, TRUE);
        }

        public boolean isSoCharacterized(ComponentCharacteristic rc) {
            String s = rc.getProperty(_CACHE);
            return s != null && s.equals(TRUE);
        }
    };

    public static ComponentCharacteristic CACHE = new ComponentCharacteristic() {
        public void mergeInto(ComponentCharacteristic rc) {
            rc.setProperty(_CACHE, TRUE);
        }

        public boolean isSoCharacterized(ComponentCharacteristic rc) {
            String s = rc.getProperty(_CACHE);
            return s != null && s.equals(TRUE);
        }
    };
    public static final ComponentCharacteristic NOJMX = new ComponentCharacteristic() {
        public void mergeInto(ComponentCharacteristic rc) {
            rc.setProperty(_NOJMX, TRUE);
        }

        public boolean isSoCharacterized(ComponentCharacteristic rc) {
            String s = rc.getProperty(_NOJMX);
            return s != null && s.equals(TRUE);
        }
    };
}
