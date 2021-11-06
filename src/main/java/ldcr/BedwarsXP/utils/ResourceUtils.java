package ldcr.BedwarsXP.utils;

import ldcr.BedwarsXP.Config;
import org.bukkit.inventory.ItemStack;

public class ResourceUtils {
	public static int convertResToXP(ItemStack stack) {
		if (stack == null)
			return 0;
		int count = 0;
		if (Config.resources.containsKey(stack.getType())) {
			count = Config.resources.get(stack.getType()) * stack.getAmount();
		}
		return count;
	}
}
