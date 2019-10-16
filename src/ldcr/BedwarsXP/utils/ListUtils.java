package ldcr.BedwarsXP.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ListUtils {
	public static List<String> newList(final String... listitem) {
		final List<String> list = new ArrayList<>();
		for (final String l : listitem) {
			list.add(l);
		}
		return list;
	}

	public static List<String> setToList(final Set<String> set) {
		final List<String> list = new ArrayList<>();
		for (final String l : set) {
			list.add(l);
		}
		return list;
	}
}
