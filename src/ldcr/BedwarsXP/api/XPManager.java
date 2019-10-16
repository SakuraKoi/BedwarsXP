package ldcr.BedwarsXP.api;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import ldcr.BedwarsXP.Config;
import ldcr.BedwarsXP.utils.ActionBarUtils;

public class XPManager {
	private static Map<String, XPManager> managerMap = new HashMap<>();
	private final Map<UUID, Integer> xp = new HashMap<>();

	public static XPManager getXPManager(final String bedwarsGame) {
		if (!managerMap.containsKey(bedwarsGame)) {
			managerMap.put(bedwarsGame, new XPManager());
		}
		return managerMap.get(bedwarsGame);
	}

	public static void reset(final String bedwarsGame) {
		getXPManager(bedwarsGame).xp.clear();
		managerMap.remove(bedwarsGame);
	}

	public void updateXPBar(final Player player) {
		player.setLevel(get(player));
	}

	private void set(final Player player, final int count) {
		xp.put(player.getUniqueId(), count);
		updateXPBar(player);
	}

	private int get(final Player player) {
		Integer value = xp.get(player.getUniqueId());
		if (value == null) {
			value = 0;
			xp.put(player.getUniqueId(), 0);
		}
		return value;
	}

	public void setXP(final Player player, final int count) {
		set(player, count);
	}

	public int getXP(final Player player) {
		return get(player);
	}

	public void addXP(final Player player, final int count) {
		set(player, get(player) + count);
	}

	public boolean takeXP(final Player player, final int count) {
		if (!hasEnoughXP(player, count))
			return false;
		set(player, get(player) - count);
		return true;
	}

	public boolean hasEnoughXP(final Player player, final int count) {
		return get(player) >= count;
	}

	private final HashMap<UUID, Long> messageTimeMap = new HashMap<>();
	private final HashMap<UUID, Integer> messageCountMap = new HashMap<>();

	public void sendXPMessage(final Player player, final int count) {
		if (!messageTimeMap.containsKey(player.getUniqueId())) {
			messageTimeMap.put(player.getUniqueId(), System.currentTimeMillis());
		}
		if (!messageCountMap.containsKey(player.getUniqueId())) {
			messageCountMap.put(player.getUniqueId(), 0);
		}
		if (System.currentTimeMillis() - messageTimeMap.get(player.getUniqueId()) > 500) {
			messageCountMap.put(player.getUniqueId(), 0);
		}
		messageTimeMap.put(player.getUniqueId(), System.currentTimeMillis());
		final int c = messageCountMap.get(player.getUniqueId()) + count;
		messageCountMap.put(player.getUniqueId(), c);
		if (!Config.xpMessage.equals("")) {
			ActionBarUtils.sendActionBar(player, Config.xpMessage.replaceAll("%xp%", Integer.toString(c)));
		}
	}

	public void sendMaxXPMessage(final Player player) {
		if (!Config.maxXPMessage.equals("")) {
			ActionBarUtils.sendActionBar(player, Config.maxXPMessage);
		}
	}
}
