package ldcr.BedwarsXP.XPShop;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.shop.NewItemShop;
import io.github.bedwarsrel.villager.MerchantCategory;
import io.github.bedwarsrel.villager.MerchantCategoryComparator;
import io.github.bedwarsrel.villager.VillagerTrade;
import ldcr.BedwarsXP.BedwarsXP;
import ldcr.BedwarsXP.Config;
import ldcr.BedwarsXP.utils.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.*;
import java.util.Map.Entry;

public class ShopReplacer implements Runnable {
	Game game;
	CommandSender s;

	public static void replaceShop(final String bw, final CommandSender sender) {
		if (!Config.isGameEnabledXP(bw))
			return;
		Bukkit.getScheduler().runTaskLater(BedwarsXP.getInstance(), new ShopReplacer(bw, sender), 20);
	}

	public ShopReplacer(final String e, final CommandSender sender) {
		s = sender;
		game = BedwarsRel.getInstance().getGameManager().getGame(e);
	}

	@Override
	public void run() {
		final HashMap<Material, MerchantCategory> map = game.getItemShopCategories();
		if (Config.fullXPBedwars) {
			for (final Entry<Material, MerchantCategory> en : map.entrySet()) {
				final MerchantCategory m = en.getValue();
				final ArrayList<VillagerTrade> t = m.getOffers();
				final ArrayList<XPVillagerTrade> n = new ArrayList<>();
				for (int i = 0; i < t.size(); i++) {
					n.add(new XPVillagerTrade(t.get(i)));
				}
				try {
					ReflectionUtils.setPrivateValue(m, "offers", n);
				} catch (final Exception e1) {
					s.sendMessage("§6§lBedwarsXP §7>> §c"+BedwarsXP.l18n("ERROR_OCCURRED_REPLACE_SHOP", "%game%", game.getName()));
					e1.printStackTrace();
				}
				map.put(en.getKey(), m);
			}
		}
		if (Config.addResShop) {
			final ArrayList<VillagerTrade> trades = new ArrayList<>();
			for (final String key : Config.resourceskey) {
				@SuppressWarnings("unchecked")
				final List<Map<String, Object>> resourceList = (List<Map<String, Object>>) io.github.bedwarsrel.BedwarsRel.getInstance().getConfig().getList("resource." + key + ".item");
				for (final Map<String, Object> resource : resourceList) {
					final ItemStack itemStack = ItemStack.deserialize(resource);
					if (itemStack != null) {
						trades.add(new XPVillagerTrade(itemStack));
					}
				}
			}
			final MerchantCategory mc = new MerchantCategory(BedwarsXP.l18n("SHOP_XP_EXCHANGE_TITLE"), Material.EXP_BOTTLE, trades, Collections.singletonList(BedwarsXP.l18n("SHOP_XP_EXCHANGE_LORE")), 3, "bw.base");
			map.put(Material.EXP_BOTTLE, mc);
		}
		try {
			final Field itemshops = ReflectionUtils.getField(game, "newItemShops");
			itemshops.setAccessible(true);
			final HashMap<Player, NewItemShop> shops = new HashMap<>();
			final List<MerchantCategory> order = new ArrayList<>(map.values());
			Collections.sort(order, new MerchantCategoryComparator());
			for (final Player pl : game.getPlayers()) {
				shops.put(pl, new XPItemShop(order, game));
			}
			ReflectionUtils.setPrivateValue(game, "newItemShops", shops);
			s.sendMessage("§6§lBedwarsXP §7>> §b"+BedwarsXP.l18n("SUCCESSFULLY_REPLACED_SHOP", "%game%", game.getName()));
		} catch (final Exception e) {
			s.sendMessage("§6§lBedwarsXP §7>> §c"+BedwarsXP.l18n("ERROR_OCCURRED_WHILE_INITALIZING_XP_SHOP", "%game%", game.getName()));
			e.printStackTrace();
		}
	}
}
