package org.picocontainer;

public final class Characterizations {

    private static final String _INJECTION = "injection";
    private static final String _CACHE = "cache";
    private static final String _NOJMX = "no-jmx";

    private static final String FALSE = "FALSE";
    private static final String TRUE = "TRUE";

    public static final ComponentCharacteristics CDI = new ComponentCharacteristics() {
        private static final String _CONSTRUCTOR = "constructor";

        public void mergeInto(ComponentCharacteristics characteristics) {
            characteristics.setProperty(_INJECTION, _CONSTRUCTOR);
        }
        public void setProcessedIn(ComponentCharacteristics characteristics) {
            characteristics.removeProperty(_INJECTION);
        }
        public boolean isCharacterizedIn(ComponentCharacteristics characteristics) {
            String s = characteristics.getProperty(_INJECTION);
            return s != null && s.equals(_CONSTRUCTOR);
        }
    };

    public static final ComponentCharacteristics SDI = new ComponentCharacteristics() {
        private static final String _SETTER = "setter";

        public void mergeInto(ComponentCharacteristics characteristics) {
            characteristics.setProperty(_INJECTION, _SETTER);
        }

        public void setProcessedIn(ComponentCharacteristics characteristics) {
            characteristics.removeProperty(_INJECTION);
        }

        public boolean isCharacterizedIn(ComponentCharacteristics characteristics) {
            String s = characteristics.getProperty(_INJECTION);
            return s != null && s.equals(_SETTER);
        }
    };

    public static final ComponentCharacteristics NOCACHE = new ComponentCharacteristics() {
        public void mergeInto(ComponentCharacteristics characteristics) {
            characteristics.setProperty(_CACHE, FALSE);
        }


        public void setProcessedIn(ComponentCharacteristics characteristics) {
            characteristics.removeProperty(_CACHE);
        }

        public boolean isCharacterizedIn(ComponentCharacteristics characteristics) {
            String s = characteristics.getProperty(_CACHE);
            return s != null && s.equals(FALSE);
        }
    };

    public static final ComponentCharacteristics CACHE = new ComponentCharacteristics() {
        public void mergeInto(ComponentCharacteristics characteristics) {
            characteristics.setProperty(_CACHE, TRUE);
        }

        public void setProcessedIn(ComponentCharacteristics characteristics) {
            characteristics.removeProperty(_CACHE);
        }

        public boolean isCharacterizedIn(ComponentCharacteristics characteristics) {
            String s = characteristics.getProperty(_CACHE);
            return s != null && s.equals(TRUE);
        }
    };
    public static final ComponentCharacteristics NOJMX = new ComponentCharacteristics() {
        public void mergeInto(ComponentCharacteristics characteristics) {
            characteristics.setProperty(_NOJMX, TRUE);
        }

        public void setProcessedIn(ComponentCharacteristics characteristics) {
            characteristics.removeProperty(_NOJMX);
        }

        public boolean isCharacterizedIn(ComponentCharacteristics characteristics) {
            String s = characteristics.getProperty(_NOJMX);
            return s != null && s.equals(TRUE);
        }
    };
    public static final ComponentCharacteristics THREAD_SAFE = new ComponentCharacteristics() {
        private static final String _THREAD_SAFE = "thread-safe";

        public void mergeInto(ComponentCharacteristics characteristics) {
            characteristics.setProperty(_THREAD_SAFE, TRUE);
        }

        public void setProcessedIn(ComponentCharacteristics characteristics) {
            characteristics.removeProperty(_THREAD_SAFE);
        }

        public boolean isCharacterizedIn(ComponentCharacteristics characteristics) {
            String s = characteristics.getProperty(_THREAD_SAFE);
            return s != null && s.equals(TRUE);
        }
    };
    
    public static final ComponentCharacteristics SINGLE = CACHE;
    
    public static final ComponentCharacteristics HIDE = new ComponentCharacteristics() {
        private static final String _HIDE = "hide-implementations";

        public void mergeInto(ComponentCharacteristics characteristics) {
            characteristics.setProperty(_HIDE, TRUE);
        }

        public void setProcessedIn(ComponentCharacteristics characteristics) {
            characteristics.removeProperty(_HIDE);
        }

        public boolean isCharacterizedIn(ComponentCharacteristics characteristics) {
            String s = characteristics.getProperty(_HIDE);
            return s != null && s.equals(TRUE);
        }
    };
}
