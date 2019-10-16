package ldcr.BedwarsXP.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import ldcr.BedwarsXP.BedwarsXP;

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
				final int ver = Integer.parseInt(nmsver.split("_")[1]);
				if (ver>=11) {
					useNewMethods = true;
				}
			} catch (final Exception e) {
				BedwarsXP.sendConsoleMessage("§cERROR: 解析服务端版本失败, ActionBar提示可能出错");
			}
		}
	}

	public static void sendActionBar(final Player player, final String message) {
		if (useNewMethods) {
			player.sendActionBar(message);
			return;
		}
		try {
			final Class<?> c1 = Class.forName("org.bukkit.craftbukkit." + nmsver
					+ ".entity.CraftPlayer");
			final Object p = c1.cast(player);
			Object ppoc;
			final Class<?> c4 = Class.forName("net.minecraft.server." + nmsver
					+ ".PacketPlayOutChat");
			final Class<?> c5 = Class.forName("net.minecraft.server." + nmsver
					+ ".Packet");
			if (useOldMethods) {
				final Class<?> c2 = Class.forName("net.minecraft.server." + nmsver
						+ ".ChatSerializer");
				final Class<?> c3 = Class.forName("net.minecraft.server." + nmsver
						+ ".IChatBaseComponent");
				final Method m3 = c2.getDeclaredMethod("a", String.class);
				final Object cbc = c3.cast(m3.invoke(c2, "{\"text\": \"" + message
						+ "\"}"));
				ppoc = c4.getConstructor(c3, byte.class)
						.newInstance(cbc, (byte) 2);
			} else {
				final Class<?> c2 = Class.forName("net.minecraft.server." + nmsver
						+ ".ChatComponentText");
				final Class<?> c3 = Class.forName("net.minecraft.server." + nmsver
						+ ".IChatBaseComponent");
				final Object o = c2.getConstructor(String.class )
						.newInstance(message);
				ppoc = c4.getConstructor(c3, byte.class)
						.newInstance(o, (byte) 2);
			}
			final Method m1 = c1.getDeclaredMethod("getHandle");
			final Object h = m1.invoke(p);
			final Field f1 = h.getClass().getDeclaredField("playerConnection");
			final Object pc = f1.get(h);
			final Method m5 = pc.getClass().getDeclaredMethod("sendPacket", c5);
			m5.invoke(pc, ppoc);
		} catch (final Exception ex) {
			ex.printStackTrace();
			player.sendMessage(message);
		}
	}

	public static void sendActionBar(final Player player, final String message,
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
			final int sched = duration % 60;
			new BukkitRunnable() {
				@Override
				public void run() {
					sendActionBar(player, message);
				}
			}.runTaskLater(BedwarsXP.getInstance(), sched);
		}
	}

	public static void sendActionBarToAllPlayers(final String message) {
		sendActionBarToAllPlayers(message, -1);
	}

	public static void sendActionBarToAllPlayers(final String message, final int duration) {
		for (final Player p : Bukkit.getOnlinePlayers()) {
			sendActionBar(p, message, duration);
		}
	}
}