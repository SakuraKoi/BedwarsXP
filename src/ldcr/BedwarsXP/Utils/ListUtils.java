package ldcr.BedwarsXP.Utils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class ListUtils {
	public static List<String> newList(String... listitem) {
		List<String> list = new LinkedList<String>();
		for (String l : listitem) {
			list.add(l);
		}
		return list;
	}

	public static List<String> hashSetToList(HashSet<String> set) {
		List<String> list = new LinkedList<String>();
		for (String l : set) {
			list.add(l);
		}
		return list;
	}
}
