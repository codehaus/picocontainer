package org.picocontainer;

public final class ComponentCharacteristics {

    private static final String _INJECTION = "injection";
    private static final String _CONSTRUCTOR = "constructor";
    private static final String _SETTER = "setter";
    private static final String _CACHE = "cache";
    private static final String _NOJMX = "no-jmx";

    private static final String FALSE = "FALSE";
    private static final String TRUE = "TRUE";

    public static final ComponentCharacteristic CDI = new ComponentCharacteristic() {
        public void mergeInto(ComponentCharacteristic characteristic) {
            characteristic.setProperty(_INJECTION, _CONSTRUCTOR);
        }
        public void processed(ComponentCharacteristic characteristic) {
            characteristic.removeProperty(_INJECTION);
        }
        public boolean isSoCharacterized(ComponentCharacteristic characteristic) {
            String s = characteristic.getProperty(_INJECTION);
            return s != null && s.equals(_CONSTRUCTOR);
        }
    };

    public static final ComponentCharacteristic SDI = new ComponentCharacteristic() {
        public void mergeInto(ComponentCharacteristic characteristic) {
            characteristic.setProperty(_INJECTION, _SETTER);
        }

        public void processed(ComponentCharacteristic characteristic) {
            characteristic.removeProperty(_INJECTION);
        }

        public boolean isSoCharacterized(ComponentCharacteristic characteristic) {
            String s = characteristic.getProperty(_INJECTION);
            return s != null && s.equals(_SETTER);
        }
    };

    public static final ComponentCharacteristic NOCACHE = new ComponentCharacteristic() {
        public void mergeInto(ComponentCharacteristic characteristic) {
            characteristic.setProperty(_CACHE, FALSE);
        }


        public void processed(ComponentCharacteristic characteristic) {
            characteristic.removeProperty(_CACHE);
        }

        public boolean isSoCharacterized(ComponentCharacteristic characteristic) {
            String s = characteristic.getProperty(_CACHE);
            return s != null && s.equals(FALSE);
        }
    };

    public static final ComponentCharacteristic SINGLETON = new ComponentCharacteristic() {
        public void mergeInto(ComponentCharacteristic rc) {
            rc.setProperty(_CACHE, TRUE);
        }

        public void processed(ComponentCharacteristic characteristic) {
            characteristic.removeProperty(_CACHE);
        }

        public boolean isSoCharacterized(ComponentCharacteristic characteristic) {
            String s = characteristic.getProperty(_CACHE);
            return s != null && s.equals(TRUE);
        }
    };

    public static final ComponentCharacteristic CACHE = new ComponentCharacteristic() {
        public void mergeInto(ComponentCharacteristic characteristic) {
            characteristic.setProperty(_CACHE, TRUE);
        }

        public void processed(ComponentCharacteristic characteristic) {
            characteristic.removeProperty(_CACHE);
        }

        public boolean isSoCharacterized(ComponentCharacteristic characteristic) {
            String s = characteristic.getProperty(_CACHE);
            return s != null && s.equals(TRUE);
        }
    };
    public static final ComponentCharacteristic NOJMX = new ComponentCharacteristic() {
        public void mergeInto(ComponentCharacteristic characteristic) {
            characteristic.setProperty(_NOJMX, TRUE);
        }

        public void processed(ComponentCharacteristic characteristic) {
            characteristic.removeProperty(_NOJMX);
        }

        public boolean isSoCharacterized(ComponentCharacteristic characteristic) {
            String s = characteristic.getProperty(_NOJMX);
            return s != null && s.equals(TRUE);
        }
    };
}
