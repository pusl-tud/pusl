package de.bp2019.pusl.util;

import java.util.Collection;

/**
 * Basic Utility class, containing some handy utility functions
 * 
 * @author Leon Chemnitz
 */
public final class Utils {
    public static boolean implementsInterface(Class<?> clazz, Class<?> interfaze) {
        for (Class<?> c : clazz.getInterfaces()) {
            if (c.equals(interfaze)) {
                return true;
            }
        }
        return false;
    }

    public static <T> boolean containsAny(Collection<T> col1, Collection<T> col2) {
        for (T item : col2) {
            if (col1.contains(item)) {
                return true;
            }
        }
        return false;
    }
}