package de.bp2019.pusl.util;

public final class ClassUtils {
    public static boolean implementsInterface(Class<?> clazz, Class<?> interfaze) {
        for (Class<?> c : clazz.getInterfaces()) {
            if (c.equals(interfaze)) {
                return true;
            }
        }
        return false;
    }
}