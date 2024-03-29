package ldcr.BedwarsXP.utils;

import ldcr.BedwarsXP.Config;
import org.bukkit.inventory.ItemStack;

public class ResourceUtils {
    public static Integer convertResToXP(ItemStack stack) {
        if (stack == null)
            return null;
        if (Config.resources.containsKey(stack.getType()))
            return Config.resources.get(stack.getType()) * stack.getAmount();
        return null;
    }

    public static int convertResToXPExact(ItemStack item) {
        Integer result = convertResToXP(item);
        return result == null ? 0 : result;
    }
}
