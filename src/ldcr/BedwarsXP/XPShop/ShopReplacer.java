package ldcr.BedwarsXP.XPShop;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.shop.NewItemShop;
import io.github.bedwarsrel.villager.MerchantCategory;
import io.github.bedwarsrel.villager.MerchantCategoryComparator;
import io.github.bedwarsrel.villager.VillagerTrade;
import ldcr.BedwarsXP.Config;
import ldcr.BedwarsXP.utils.ListUtils;
import ldcr.BedwarsXP.utils.ReflectionUtils;

public class ShopReplacer implements Runnable {
	Game game;
	CommandSender s;

	public static void replaceShop(final String bw, final CommandSender sender) {
		if (!Config.isGameEnabledXP(bw))
			return;
		Bukkit.getScheduler().runTaskLater(ldcr.BedwarsXP.BedwarsXP.getInstance(), new ShopReplacer(bw, sender), 20);
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
					s.sendMessage("§6§l[BedwarsXP] §c为地图 " + game.getName() + " 替换原始商店为经验商店失败");
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
			final MerchantCategory mc = new MerchantCategory("§6§l经验兑换资源", Material.EXP_BOTTLE, trades, ListUtils.newList("§a将你的经验兑换成物品"), 3, "bw.base");
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
			s.sendMessage("§6§l[BedwarsXP] §b为地图 " + game.getName() + " 替换经验商店成功!");
		} catch (final Exception e) {
			s.sendMessage("§6§l[BedwarsXP] §c为地图 " + game.getName() + " 初始化经验商店时出错");
			e.printStackTrace();
		}
	}
}
