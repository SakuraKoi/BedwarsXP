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
    private final Game game;
    private final CommandSender s;

    public static void replaceShop(String bw, CommandSender sender) {
        if (!Config.isGameEnabledXP(bw))
            return;
        Bukkit.getScheduler().runTaskLater(BedwarsXP.getInstance(), new ShopReplacer(bw, sender), 20);
    }

    private ShopReplacer(String e, CommandSender sender) {
        s = sender;
        game = BedwarsRel.getInstance().getGameManager().getGame(e);
    }

    @Override
    public void run() {
        HashMap<Material, MerchantCategory> map = game.getItemShopCategories();
        if (Config.fullXPBedwars) {
            for (Entry<Material, MerchantCategory> en : map.entrySet()) {
                MerchantCategory m = en.getValue();
                ArrayList<VillagerTrade> t = m.getOffers();
                ArrayList<XPVillagerTrade> n = new ArrayList<>();
                for (VillagerTrade villagerTrade : t) {
                    n.add(new XPVillagerTrade(villagerTrade));
                }
                try {
                    ReflectionUtils.setPrivateValue(m, "offers", n);
                } catch (Exception e1) {
                    s.sendMessage("§6§lBedwarsXP §7>> §c" + BedwarsXP.l18n("ERROR_OCCURRED_REPLACE_SHOP", "%game%", game.getName()));
                    e1.printStackTrace();
                }
                map.put(en.getKey(), m);
            }
        }
        if (Config.addResShop) {
            ArrayList<VillagerTrade> trades = new ArrayList<>();
            for (String key : Config.resourceskey) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> resourceList = (List<Map<String, Object>>) io.github.bedwarsrel.BedwarsRel.getInstance().getConfig().getList("resource." + key + ".item");
                for (Map<String, Object> resource : resourceList) {
                    ItemStack itemStack = ItemStack.deserialize(resource);
                    if (itemStack != null) {
                        trades.add(new XPVillagerTrade(itemStack));
                    }
                }
            }
            MerchantCategory mc = new MerchantCategory(BedwarsXP.l18n("SHOP_XP_EXCHANGE_TITLE"), Material.EXP_BOTTLE, trades, Collections.singletonList(BedwarsXP.l18n("SHOP_XP_EXCHANGE_LORE")), 3, "bw.base");
            map.put(Material.EXP_BOTTLE, mc);
        }
        try {
            Field itemshops = ReflectionUtils.getField(game, "newItemShops");
            itemshops.setAccessible(true);
            HashMap<Player, NewItemShop> shops = new HashMap<>();
            List<MerchantCategory> order = new ArrayList<>(map.values());
            order.sort(new MerchantCategoryComparator());
            for (Player pl : game.getPlayers()) {
                shops.put(pl, new XPItemShop(order, game));
            }
            ReflectionUtils.setPrivateValue(game, "newItemShops", shops);
            s.sendMessage("§6§lBedwarsXP §7>> §b" + BedwarsXP.l18n("SUCCESSFULLY_REPLACED_SHOP", "%game%", game.getName()));
        } catch (Exception e) {
            s.sendMessage("§6§lBedwarsXP §7>> §c" + BedwarsXP.l18n("ERROR_OCCURRED_WHILE_INITALIZING_XP_SHOP", "%game%", game.getName()));
            e.printStackTrace();
        }
    }
}
