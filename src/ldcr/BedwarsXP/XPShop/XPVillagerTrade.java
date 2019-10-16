package ldcr.BedwarsXP.XPShop;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import io.github.bedwarsrel.villager.VillagerTrade;
import ldcr.BedwarsXP.utils.ResourceUtils;
import lombok.Getter;
import lombok.Setter;

public class XPVillagerTrade extends VillagerTrade {
	@Getter @Setter private int xp = 0;

	public XPVillagerTrade(final VillagerTrade t) {
		super(t.getItem1(), t.getItem2(), t.getRewardItem());
		setXp(ResourceUtils.convertResToXP(t.getItem1()) + ResourceUtils.convertResToXP(t.getItem2()));
	}

	public XPVillagerTrade(final ItemStack convert) {
		super(convert, null, convert);
		setXp(ResourceUtils.convertResToXP(convert));
	}

	public XPVillagerTrade(final int xp, final ItemStack rewardItem) {
		super(new ItemStack(Material.EXP_BOTTLE, xp), rewardItem);
		setXp(xp);
	}
}
