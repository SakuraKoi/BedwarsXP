package ldcr.BedwarsXP;

import ldcr.BedwarsXP.XPShop.bedwarsrel.NewShopReplacer;
import ldcr.BedwarsXP.XPShop.yannici.OldShopReplacer;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class ShopReplacer {
	public static void replaceShop(String bw, CommandSender sender) {
		if (!Config.isGameEnabledXP(bw)) {
			return;
		}
		if (Main.isOldBedwarsPlugin) {
			Bukkit.getScheduler().runTaskLater(ldcr.BedwarsXP.Main.plugin,
					new OldShopReplacer(bw, sender), 20);

		} else {
			Bukkit.getScheduler().runTaskLater(ldcr.BedwarsXP.Main.plugin,
					new NewShopReplacer(bw, sender), 20);

		}

	}
}
