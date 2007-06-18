package org.picocontainer;

public final class ComponentCharacteristics {

    private static final String _INJECTION = "injection";
    private static final String _CACHE = "cache";
    private static final String _NOJMX = "no-jmx";

    private static final String FALSE = "FALSE";
    private static final String TRUE = "TRUE";

    public static final ComponentCharacteristic CDI = new ComponentCharacteristic() {
        private static final String _CONSTRUCTOR = "constructor";

        public void mergeInto(ComponentCharacteristic characteristic) {
            characteristic.setProperty(_INJECTION, _CONSTRUCTOR);
        }
        public void setProcessedIn(ComponentCharacteristic characteristic) {
            characteristic.removeProperty(_INJECTION);
        }
        public boolean isCharacterizedIn(ComponentCharacteristic characteristic) {
            String s = characteristic.getProperty(_INJECTION);
            return s != null && s.equals(_CONSTRUCTOR);
        }
    };

    public static final ComponentCharacteristic SDI = new ComponentCharacteristic() {
        private static final String _SETTER = "setter";

        public void mergeInto(ComponentCharacteristic characteristic) {
            characteristic.setProperty(_INJECTION, _SETTER);
        }

        public void setProcessedIn(ComponentCharacteristic characteristic) {
            characteristic.removeProperty(_INJECTION);
        }

        public boolean isCharacterizedIn(ComponentCharacteristic characteristic) {
            String s = characteristic.getProperty(_INJECTION);
            return s != null && s.equals(_SETTER);
        }
    };

    public static final ComponentCharacteristic NOCACHE = new ComponentCharacteristic() {
        public void mergeInto(ComponentCharacteristic characteristic) {
            characteristic.setProperty(_CACHE, FALSE);
        }


        public void setProcessedIn(ComponentCharacteristic characteristic) {
            characteristic.removeProperty(_CACHE);
        }

        public boolean isCharacterizedIn(ComponentCharacteristic characteristic) {
            String s = characteristic.getProperty(_CACHE);
            return s != null && s.equals(FALSE);
        }
    };

    public static final ComponentCharacteristic CACHE = new ComponentCharacteristic() {
        public void mergeInto(ComponentCharacteristic characteristic) {
            characteristic.setProperty(_CACHE, TRUE);
        }

        public void setProcessedIn(ComponentCharacteristic characteristic) {
            characteristic.removeProperty(_CACHE);
        }

        public boolean isCharacterizedIn(ComponentCharacteristic characteristic) {
            String s = characteristic.getProperty(_CACHE);
            return s != null && s.equals(TRUE);
        }
    };
    public static final ComponentCharacteristic NOJMX = new ComponentCharacteristic() {
        public void mergeInto(ComponentCharacteristic characteristic) {
            characteristic.setProperty(_NOJMX, TRUE);
        }

        public void setProcessedIn(ComponentCharacteristic characteristic) {
            characteristic.removeProperty(_NOJMX);
        }

        public boolean isCharacterizedIn(ComponentCharacteristic characteristic) {
            String s = characteristic.getProperty(_NOJMX);
            return s != null && s.equals(TRUE);
        }
    };
    public static final ComponentCharacteristic THREAD_SAFE = new ComponentCharacteristic() {
        private static final String _THREAD_SAFE = "thread-safe";

        public void mergeInto(ComponentCharacteristic characteristic) {
            characteristic.setProperty(_THREAD_SAFE, TRUE);
        }

        public void setProcessedIn(ComponentCharacteristic characteristic) {
            characteristic.removeProperty(_THREAD_SAFE);
        }

        public boolean isCharacterizedIn(ComponentCharacteristic characteristic) {
            String s = characteristic.getProperty(_THREAD_SAFE);
            return s != null && s.equals(TRUE);
        }
    };
    
    public static final ComponentCharacteristic SINGLE = CACHE;
    
    public static final ComponentCharacteristic HIDE = new ComponentCharacteristic() {
        private static final String _HIDE = "hide-implementations";

        public void mergeInto(ComponentCharacteristic characteristic) {
            characteristic.setProperty(_HIDE, TRUE);
        }

        public void setProcessedIn(ComponentCharacteristic characteristic) {
            characteristic.removeProperty(_HIDE);
        }

        public boolean isCharacterizedIn(ComponentCharacteristic characteristic) {
            String s = characteristic.getProperty(_HIDE);
            return s != null && s.equals(TRUE);
        }
    };
}
