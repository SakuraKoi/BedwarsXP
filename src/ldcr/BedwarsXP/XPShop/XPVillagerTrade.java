package ldcr.BedwarsXP.XPShop;

import ldcr.BedwarsXP.Config;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import io.github.yannici.bedwars.Villager.VillagerTrade;

public class XPVillagerTrade extends VillagerTrade {
	private int XP = 0;

	public XPVillagerTrade(VillagerTrade t) {
		super(t.getItem1(), t.getItem2(), t.getRewardItem());
		setXP(convertResToXP(t.getItem1()) + convertResToXP(t.getItem2()));
	}

	public XPVillagerTrade(ItemStack convert) {
		super(convert, null, convert);
		setXP(convertResToXP(convert));
	}

	public XPVillagerTrade(int xp, ItemStack RewardItem) {
		super(new ItemStack(Material.EXP_BOTTLE, xp), RewardItem);
		setXP(xp);
	}

	public void setXP(int xp) {
		XP = xp;
	}

	public int getXP() {
		return XP;
	}

	public static int convertResToXP(ItemStack stack) {
		if (stack == null) {
			return 0;
		}
		int count = 0;
		if (stack.getType().equals(Config.Bedwars_Brick_Type)) {
			count = Config.XP_Brick * stack.getAmount();
		} else if (stack.getType().equals(Config.Bedwars_Iron_Type)) {
			count = Config.XP_Iron * stack.getAmount();
		} else if (stack.getType().equals(Config.Bedwars_Gold_Type)) {
			count = Config.XP_Gold * stack.getAmount();
		}
		return count;
	}
}
