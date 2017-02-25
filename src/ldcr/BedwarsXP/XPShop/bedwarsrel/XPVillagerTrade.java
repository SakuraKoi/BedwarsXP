package ldcr.BedwarsXP.XPShop.bedwarsrel;

import io.github.bedwarsrel.BedwarsRel.Villager.VillagerTrade;
import ldcr.BedwarsXP.XPShop.Res;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class XPVillagerTrade extends VillagerTrade {
	private int XP = 0;

	public XPVillagerTrade(VillagerTrade t) {
		super(t.getItem1(), t.getItem2(), t.getRewardItem());
		setXP(Res.convertResToXP(t.getItem1())
				+ Res.convertResToXP(t.getItem2()));
	}

	public XPVillagerTrade(ItemStack convert) {
		super(convert, null, convert);
		setXP(Res.convertResToXP(convert));
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
}
