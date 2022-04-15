package tools;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

public class CollectionUtil
{
    public static <T> List<T> copyFirst(final List<T> list, final int count) {
        final List ret = new ArrayList((list.size() < count) ? list.size() : count);
        int i = 0;
        for (final Object elem : list) {
            ret.add(elem);
            if (i++ > count) {
                break;
            }
        }
        return (List<T>)ret;
    }
}
