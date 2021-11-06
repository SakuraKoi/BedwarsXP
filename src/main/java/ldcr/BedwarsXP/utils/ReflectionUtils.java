package ldcr.BedwarsXP.utils;

import java.lang.reflect.Field;

public class ReflectionUtils {
	private ReflectionUtils() {}
	public static <T, R> void setPrivateValue(T r, String f, R value) throws ReflectiveOperationException {
		Class<?> clazz = r.getClass();
		Field field = clazz.getDeclaredField(f);
		field.setAccessible(true);
		field.set(r, value);
		field.setAccessible(false);
	}

	public static <T> Field getField(T r, String f) throws ReflectiveOperationException {
		Class<?> clazz = r.getClass();
		return clazz.getDeclaredField(f);
	}

	public static boolean isClassFound(String className) {
		try {
			Class.forName(className);
		} catch (ClassNotFoundException e) {
			return false;
		}
		return true;
	}
}
