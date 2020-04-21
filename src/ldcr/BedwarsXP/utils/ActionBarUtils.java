package ldcr.BedwarsXP.utils;

import ldcr.BedwarsXP.BedwarsXP;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ActionBarUtils {
	private static String nmsver;
	private static boolean useOldMethods = false;
	private static boolean useNewMethods = false;
	public static void load() {
		nmsver = Bukkit.getServer().getClass().getPackage().getName();
		nmsver = nmsver.substring(nmsver.lastIndexOf('.') + 1);

		if (nmsver.equalsIgnoreCase("v1_8_R1") || nmsver.equalsIgnoreCase("v1_7_")) {
			useOldMethods = true;
		} else {
			try {
				int ver = Integer.parseInt(nmsver.split("_")[1]);
				if (ver>=11) {
					useNewMethods = true;
				}
			} catch (Exception e) {
				BedwarsXP.sendConsoleMessage("§cERROR: "+BedwarsXP.l18n("ERROR_UNSUPPORTED_VERSION_ACTIONBAR_MAY_NOT_WORK"));
			}
		}
	}

	public static void sendActionBar(Player player, String message) {
		if (useNewMethods) {
			player.sendActionBar(message);
			return;
		}
		try {
			Class<?> c1 = Class.forName("org.bukkit.craftbukkit." + nmsver
					+ ".entity.CraftPlayer");
			Object p = c1.cast(player);
			Object ppoc;
			Class<?> c4 = Class.forName("net.minecraft.server." + nmsver
					+ ".PacketPlayOutChat");
			Class<?> c5 = Class.forName("net.minecraft.server." + nmsver
					+ ".Packet");
			if (useOldMethods) {
				Class<?> c2 = Class.forName("net.minecraft.server." + nmsver
						+ ".ChatSerializer");
				Class<?> c3 = Class.forName("net.minecraft.server." + nmsver
						+ ".IChatBaseComponent");
				Method m3 = c2.getDeclaredMethod("a", String.class);
				Object cbc = c3.cast(m3.invoke(c2, "{\"text\": \"" + message
						+ "\"}"));
				ppoc = c4.getConstructor(c3, byte.class)
						.newInstance(cbc, (byte) 2);
			} else {
				Class<?> c2 = Class.forName("net.minecraft.server." + nmsver
						+ ".ChatComponentText");
				Class<?> c3 = Class.forName("net.minecraft.server." + nmsver
						+ ".IChatBaseComponent");
				Object o = c2.getConstructor(String.class )
						.newInstance(message);
				ppoc = c4.getConstructor(c3, byte.class)
						.newInstance(o, (byte) 2);
			}
			Method m1 = c1.getDeclaredMethod("getHandle");
			Object h = m1.invoke(p);
			Field f1 = h.getClass().getDeclaredField("playerConnection");
			Object pc = f1.get(h);
			Method m5 = pc.getClass().getDeclaredMethod("sendPacket", c5);
			m5.invoke(pc, ppoc);
		} catch (Exception ex) {
			ex.printStackTrace();
			player.sendMessage(message);
		}
	}

	public static void sendActionBar(Player player, String message,
			int duration) {
		sendActionBar(player, message);

		if (duration >= 0) {
			// Sends empty message at the end of the duration. Allows messages
			// shorter than 3 seconds, ensures precision.
			new BukkitRunnable() {
				@Override
				public void run() {
					sendActionBar(player, "");
				}
			}.runTaskLater(BedwarsXP.getInstance(), duration + 1L);
		}

		while (duration > 60) {
			duration -= 60;
			int sched = duration % 60;
			new BukkitRunnable() {
				@Override
				public void run() {
					sendActionBar(player, message);
				}
			}.runTaskLater(BedwarsXP.getInstance(), sched);
		}
	}

	public static void sendActionBarToAllPlayers(String message, int duration) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			sendActionBar(p, message, duration);
		}
	}
}