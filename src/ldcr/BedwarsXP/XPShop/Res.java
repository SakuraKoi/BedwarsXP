package ldcr.BedwarsXP.XPShop;

import ldcr.BedwarsXP.Config;
import org.bukkit.inventory.ItemStack;

public class Res {
	/*
	 * public static XPShop createShop(List cate, String bw) { if
	 * (Main.isOldBedwarsPlugin) { io.github.yannici.bedwars.Game.Game game =
	 * io.
	 * github.yannici.bedwars.Main.getInstance().getGameManager().getGame(bw);
	 * return new ldcr.BedwarsXP.XPShop.yannici.XPItemShop(cate,game); } else {
	 * io.github.bedwarsrel.BedwarsRel.Game.Game game =
	 * io.github.bedwarsrel.BedwarsRel
	 * .Main.getInstance().getGameManager().getGame(bw); return new
	 * ldcr.BedwarsXP.XPShop.bedwarsrel.XPItemShop(cate,game); } } public static
	 * XPTrade createTrade(ItemStack item1,ItemStack item2,ItemStack reward) {
	 * if (Main.isOldBedwarsPlugin) { return new
	 * ldcr.BedwarsXP.XPShop.yannici.XPVillagerTrade(new
	 * io.github.yannici.bedwars.Villager.VillagerTrade(item1,item2,reward)); }
	 * else { return new ldcr.BedwarsXP.XPShop.bedwarsrel.XPVillagerTrade(new
	 * io.
	 * github.bedwarsrel.BedwarsRel.Villager.VillagerTrade(item1,item2,reward));
	 * } } public static XPTrade createTrade(ItemStack item,ItemStack reward) {
	 * return createTrade(item,null,reward); } public static XPTrade
	 * createTrade(ItemStack convert) { return createTrade(convert, convert); }
	 * public static XPTrade createTrade(int xp, ItemStack RewardItem) { return
	 * createTrade(new ItemStack(Material.EXP_BOTTLE, xp), RewardItem); }
	 */
	public static int convertResToXP(ItemStack stack) {
		if (stack == null) {
			return 0;
		}
		int count = 0;
		if (Config.res.containsKey(stack.getType())) {
			count = Config.res.get(stack.getType()) * stack.getAmount();
		}
		return count;
	}
}
