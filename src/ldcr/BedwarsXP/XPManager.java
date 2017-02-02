package ldcr.BedwarsXP;

import org.bukkit.entity.Player;

public class XPManager {
	public static void setXP(Player p, int count) {
		p.setLevel(count);
	}

	public static int getXP(Player p) {
		return p.getLevel();
	}

	public static void addXP(Player p, int count) {
		p.setLevel(p.getLevel() + count);
	}

	public static boolean takeXP(Player p, int count) {
		if (p.getLevel() < count) {
			return false;
		}
		p.setLevel(p.getLevel() - count);
		return true;
	}

	public static boolean hasEnoughXP(Player p, int count) {
		return p.getLevel() >= count;
	}
}
