package ldcr.BedwarsXP.XPShop;

import io.github.bedwarsrel.villager.VillagerTrade;
import ldcr.BedwarsXP.utils.ResourceUtils;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class XPVillagerTrade extends VillagerTrade {
    @Setter
    private int xp = 0;

    public XPVillagerTrade(VillagerTrade t) {
        super(t.getItem1(), t.getItem2(), t.getRewardItem());
        setXp(ResourceUtils.convertResToXPExact(t.getItem1()) + ResourceUtils.convertResToXPExact(t.getItem2()));
    }

    public XPVillagerTrade(ItemStack convert) {
        super(convert, null, convert);
        setXp(ResourceUtils.convertResToXP(convert));
    }

    public XPVillagerTrade(int xp, ItemStack rewardItem) {
        super(new ItemStack(Material.EXP_BOTTLE, xp), rewardItem);
        setXp(xp);
    }

    /**
     * @deprecated It will be removed in later version, use getXp() instead
     */
    @Deprecated
    public int getXP() {
        return this.xp;
    }

    public int getXp() {
        return this.xp;
    }
}
