package constants;

import java.util.Map;
import java.util.Collection;

public class GenericValidator
{
    public static boolean isBlankOrNull(final String s) {
        return s == null || s.isEmpty();
    }
    
    public static boolean isBlankOrNull(final Collection<?> c) {
        return c == null || c.isEmpty();
    }
    
    public static boolean isBlankOrNull(final Map<?, ?> m) {
        return m == null || m.isEmpty();
    }
    
    public static boolean isBlankOrNull(final Number n) {
        return n == null || n.doubleValue() == 0.0;
    }
    
    public static boolean isBlankOrNull(final Object[] a) {
        return a == null || a.length == 0;
    }
}
