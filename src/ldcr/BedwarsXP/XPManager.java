package ldcr.BedwarsXP;

import java.util.HashMap;

import io.github.yannici.bedwars.Game.Game;

import org.bukkit.entity.Player;

public class XPManager {
	private static HashMap<Game,HashMap<Player,Integer>> xp = new HashMap<Game, HashMap<Player, Integer>>();
	public static void reset(Game bw)
	{
		xp.remove(bw);
	}
	public static void updateXPBar(Game bw,Player p)
	{
		p.setLevel(get(bw,p));
	}
	private static void set(Game bw,Player p,int count)
	{
		HashMap<Player, Integer> map = xp.get(bw);
		if (map==null)
		{
			map = new HashMap<Player, Integer>();
			xp.put(bw, map);
		}
		Integer value = map.get(p);
		if (value==null)
		{
			value=0;
			map.put(p, 0);
		}
		map.put(p, count);
		updateXPBar(bw,p);
	}
	private static int get(Game bw,Player p)
	{
		HashMap<Player, Integer> map = xp.get(bw);
		if (map==null)
		{
			map = new HashMap<Player, Integer>();
			xp.put(bw, map);
		}
		Integer value = map.get(p);
		if (value==null)
		{
			value=0;
			map.put(p, 0);
		}
		return value;
	}
	public static void setXP(Game bw,Player p, int count) {
		set(bw,p,count);
	}

	public static int getXP(Game bw,Player p) {
		return get(bw,p);
	}

	public static void addXP(Game bw,Player p, int count) {
		set(bw,p,get(bw,p)+count);
	}

	public static boolean takeXP(Game bw,Player p, int count) {
		if (get(bw,p) < count) {
			return false;
		}
		set(bw,p,get(bw,p)-count);
		return true;
	}

	public static boolean hasEnoughXP(Game bw,Player p, int count) {
		return get(bw,p) >= count;
	}
}
