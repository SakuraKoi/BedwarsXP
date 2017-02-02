package ldcr.BedwarsXP.Utils;

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
}
