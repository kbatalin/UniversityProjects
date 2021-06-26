package pro.batalin.models.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Kirill Batalin (kir55rus)
 */
public class ListUtils {
    public static <T> boolean hasSameItems(List<T> list1, List<T> list2) {
        Set<T> set1 = new HashSet<>(list1);
        Set<T> set2 = new HashSet<>(list2);

        return set1.equals(set2);
    }
}
