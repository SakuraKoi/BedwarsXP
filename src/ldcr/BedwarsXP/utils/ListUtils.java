package ldcr.BedwarsXP.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ListUtils {
	public static List<String> setToList(Set<String> set) {
		List<String> list = new ArrayList<>();
		for (String l : set) {
			list.add(l);
		}
		return list;
	}
}
