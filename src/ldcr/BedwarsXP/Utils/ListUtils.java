package ldcr.BedwarsXP.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ListUtils {
	public static List<String> newList(final String... listitem) {
		final List<String> list = new ArrayList<String>();
		for (final String l : listitem) {
			list.add(l);
		}
		return list;
	}

	public static List<String> hashSetToList(final HashSet<String> set) {
		final List<String> list = new ArrayList<String>();
		for (final String l : set) {
			list.add(l);
		}
		return list;
	}
}
