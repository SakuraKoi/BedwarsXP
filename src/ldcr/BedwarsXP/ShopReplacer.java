package ldcr.BedwarsXP;

import io.github.yannici.bedwars.Main;
import io.github.yannici.bedwars.Game.Game;
import io.github.yannici.bedwars.Game.RessourceSpawner;
import io.github.yannici.bedwars.Shop.NewItemShop;
import io.github.yannici.bedwars.Villager.MerchantCategory;
import io.github.yannici.bedwars.Villager.MerchantCategoryComparator;
import io.github.yannici.bedwars.Villager.VillagerTrade;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import ldcr.BedwarsXP.Utils.ListUtils;
import ldcr.BedwarsXP.Utils.ReflectionUtils;
import ldcr.BedwarsXP.XPShop.ItemShop;
import ldcr.BedwarsXP.XPShop.XPVillagerTrade;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShopReplacer implements Runnable {
	Game game;
	CommandSender s;
	public ShopReplacer(Game e,CommandSender sender) {
		game=e;
		s=sender;
	}
	public static void replaceShop(Game bw,CommandSender sender)
	{
		Bukkit.getScheduler().runTaskLater(ldcr.BedwarsXP.Main.plugin,new ShopReplacer(bw,sender), 20);
	}
	@Override
	public void run() {
		HashMap<Material, MerchantCategory> map = game.getItemShopCategories();
		if (Config.Full_XP_Bedwars) {
			Iterator<Entry<Material, MerchantCategory>> i1 = map
					.entrySet().iterator();
			for (; i1.hasNext();) {
				Entry<Material, MerchantCategory> en = i1
						.next();
				MerchantCategory m = en.getValue();
				ArrayList<VillagerTrade> t = m.getOffers();
				ArrayList<XPVillagerTrade> n = new ArrayList<XPVillagerTrade>();
				for (int i = 0; i < t.size(); i++) {
					n.add(new XPVillagerTrade(t.get(i)));
				}
				try {
					ReflectionUtils.setPrivateValue(m,
							"offers", n);
				} catch (Exception e1) {
					s.sendMessage("§6§l[BedwarsXP] §c为地图 " + game.getName() + " 替换原始商店为经验商店失败");
					e1.printStackTrace();
				}
				map.put(en.getKey(), m);
			}
		}
		if (Config.Add_Res_Shop) {
			ArrayList<VillagerTrade> trades = new ArrayList<VillagerTrade>();
			trades.add(new XPVillagerTrade(RessourceSpawner
					.createSpawnerStackByConfig(Main
							.getInstance().getConfig()
							.get("ressource.bronze"))));
			trades.add(new XPVillagerTrade(RessourceSpawner
					.createSpawnerStackByConfig(Main
							.getInstance().getConfig()
							.get("ressource.iron"))));
			trades.add(new XPVillagerTrade(RessourceSpawner
					.createSpawnerStackByConfig(Main
							.getInstance().getConfig()
							.get("ressource.gold"))));
			MerchantCategory mc = new MerchantCategory(
					"§6§l经验兑换资源", Material.EXP_BOTTLE, trades,
					ListUtils.newList("§a将你的经验兑换成物品"), 3,
					"bw.base");
			map.put(Material.EXP_BOTTLE, mc);
		}
		try {
			Field itemshops = ReflectionUtils.getField(
					game, "newItemShops");
			itemshops.setAccessible(true);
			HashMap<Player, NewItemShop> shops = new HashMap<Player, NewItemShop>();
			List<MerchantCategory> order = new ArrayList<MerchantCategory>(map.values());
		    Collections.sort(order, new MerchantCategoryComparator());
			for (Player pl : game.getPlayers()) {
				ItemShop Shop = new ItemShop(order,game);
				shops.put(pl, Shop);
			}
			ReflectionUtils.setPrivateValue(game,
					"newItemShops", shops);
			s.sendMessage("§6§l[BedwarsXP] §b为地图 " + game.getName() + " 替换经验商店成功!");
		} catch (Exception e1) {
			s.sendMessage("§6§l[BedwarsXP] §c为地图 " + game.getName() + " 初始化经验商店时出错");
			e1.printStackTrace();
		}
	}
}
