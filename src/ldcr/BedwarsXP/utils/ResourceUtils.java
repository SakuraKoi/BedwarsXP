package ldcr.BedwarsXP.utils;

import org.bukkit.inventory.ItemStack;

import ldcr.BedwarsXP.Config;

public class ResourceUtils {
	public static int convertResToXP(final ItemStack stack) {
		if (stack == null)
			return 0;
		int count = 0;
		if (Config.resources.containsKey(stack.getType())) {
			count = Config.resources.get(stack.getType()) * stack.getAmount();
		}
		return count;
	}
}
