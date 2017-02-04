package ldcr.BedwarsXP.Utils;

import java.lang.reflect.Field;

public class ReflectionUtils {
	public static <T, R> void setPrivateValue(T r, String f, R value)
			throws Exception {
		Class<? extends Object> clazz = r.getClass();
		Field field = clazz.getDeclaredField(f);
		field.setAccessible(true);
		field.set(r, value);
		field.setAccessible(false);
	}

	public static <T, R> Field getField(T r, String f) throws Exception {
		Class<? extends Object> clazz = r.getClass();
		Field field = clazz.getDeclaredField(f);
		return field;
	}

	public static boolean isClassFound(String class_path) {
		try {
			Class.forName(class_path);
		} catch (ClassNotFoundException e) {
			return false;
		}
		return true;
	}
}
