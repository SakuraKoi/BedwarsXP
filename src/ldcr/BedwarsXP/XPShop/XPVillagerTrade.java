package ldcr.BedwarsXP.XPShop;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import io.github.bedwarsrel.villager.VillagerTrade;
import ldcr.BedwarsXP.Utils.ResourceUtils;

public class XPVillagerTrade extends VillagerTrade {
	private int XP = 0;

	public XPVillagerTrade(final VillagerTrade t) {
		super(t.getItem1(), t.getItem2(), t.getRewardItem());
		setXP(ResourceUtils.convertResToXP(t.getItem1()) + ResourceUtils.convertResToXP(t.getItem2()));
	}

	public XPVillagerTrade(final ItemStack convert) {
		super(convert, null, convert);
		setXP(ResourceUtils.convertResToXP(convert));
	}

	public XPVillagerTrade(final int xp, final ItemStack RewardItem) {
		super(new ItemStack(Material.EXP_BOTTLE, xp), RewardItem);
		setXP(xp);
	}

	public void setXP(final int xp) {
		XP = xp;
	}

	public int getXP() {
		return XP;
	}
}
