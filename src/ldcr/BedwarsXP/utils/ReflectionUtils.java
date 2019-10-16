package ldcr.BedwarsXP.utils;

import java.lang.reflect.Field;

public class ReflectionUtils {
	private ReflectionUtils() {}
	public static <T, R> void setPrivateValue(final T r, final String f, final R value)
			throws Exception {
		final Class<? extends Object> clazz = r.getClass();
		final Field field = clazz.getDeclaredField(f);
		field.setAccessible(true);
		field.set(r, value);
		field.setAccessible(false);
	}

	public static <T> Field getField(final T r, final String f) throws ReflectiveOperationException {
		final Class<? extends Object> clazz = r.getClass();
		return clazz.getDeclaredField(f);
	}

	public static boolean isClassFound(final String class_path) {
		try {
			Class.forName(class_path);
		} catch (final ClassNotFoundException e) {
			return false;
		}
		return true;
	}
}
