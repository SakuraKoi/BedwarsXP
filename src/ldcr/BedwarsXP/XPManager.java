package ldcr.BedwarsXP;

import java.util.HashMap;

import org.bukkit.entity.Player;

public class XPManager {
	private static HashMap<String, HashMap<Player, Integer>> xp = new HashMap<String, HashMap<Player, Integer>>();

	public static void reset(String bw) {
		xp.remove(bw);

	}

	public static void updateXPBar(String bw, Player p) {
		p.setLevel(get(bw, p));
	}

	private static void set(String bw, Player p, int count) {
		HashMap<Player, Integer> map = xp.get(bw);
		if (map == null) {
			map = new HashMap<Player, Integer>();
			xp.put(bw, map);
		}
		Integer value = map.get(p);
		if (value == null) {
			value = 0;
			map.put(p, 0);
		}
		map.put(p, count);
		updateXPBar(bw, p);
	}

	private static int get(String bw, Player p) {
		HashMap<Player, Integer> map = xp.get(bw);
		if (map == null) {
			map = new HashMap<Player, Integer>();
			xp.put(bw, map);
		}
		Integer value = map.get(p);
		if (value == null) {
			value = 0;
			map.put(p, 0);
		}
		return value;
	}

	public static void setXP(String bw, Player p, int count) {
		set(bw, p, count);
	}

	public static int getXP(String bw, Player p) {
		return get(bw, p);
	}

	public static void addXP(String bw, Player p, int count) {
		set(bw, p, get(bw, p) + count);
	}
	public static boolean takeXP(String bw, Player p, int count) {
		if (!hasEnoughXP(bw, p, count)) {

			return false;
		}
		set(bw, p, get(bw, p) - count);
		return true;
	}

	public static boolean hasEnoughXP(String bw, Player p, int count) {
		return get(bw, p) >= count;
	}
}
