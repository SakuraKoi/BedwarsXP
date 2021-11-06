package ldcr.BedwarsXP.XPShop;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.shop.NewItemShop;
import io.github.bedwarsrel.utils.ChatWriter;
import io.github.bedwarsrel.utils.Utils;
import io.github.bedwarsrel.villager.MerchantCategory;
import io.github.bedwarsrel.villager.VillagerTrade;
import ldcr.BedwarsXP.BedwarsXP;
import ldcr.BedwarsXP.api.XPManager;
import ldcr.BedwarsXP.utils.SoundMachine;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;

import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;

public class XPItemShop extends NewItemShop {
	private final Game bedwars;

	XPItemShop(List<MerchantCategory> cate, Game bw) {
		super(cate);
		categories = cate;
		bedwars = bw;
	}

	private final List<MerchantCategory> categories;
	private MerchantCategory currentCategory = null;

	@Override
	public List<MerchantCategory> getCategories() {
		return categories;
	}

	@Override
	public boolean hasOpenCategory() {
		return currentCategory != null;
	}

	@Override
	public boolean hasOpenCategory(MerchantCategory category) {
		if (currentCategory == null)
			return false;

		return currentCategory.equals(category);
	}

	private int getCategoriesSize(Player player) {
		int size = 0;
		for (MerchantCategory cat : categories) {
			if (cat.getMaterial() != null && (player == null || player.hasPermission(cat.getPermission()))) {
				size++;
			}
		}
		return size;
	}

	@Override
	public void openCategoryInventory(Player player) {
		int catSize = getCategoriesSize(player);
		int nom = catSize % 9 == 0 ? 9 : catSize % 9;
		int size = catSize + 9 - nom + 9;

		Inventory inventory = Bukkit.createInventory(player, size, BedwarsRel._l("ingame.shop.name"));

		addCategoriesToInventory(inventory, player);

		Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
		ItemStack stack;

		if (game != null) {
			if (game.getPlayerSettings(player).oneStackPerShift()) {
				stack = new ItemStack(Material.BUCKET, 1);
				ItemMeta meta = stack.getItemMeta();

				meta.setDisplayName(ChatColor.AQUA + BedwarsRel._l("default.currently") + ": " + ChatColor.WHITE + BedwarsRel._l("ingame.shop.onestackpershift"));

				meta.setLore(new ArrayList<>());
				stack.setItemMeta(meta);
			} else {
				stack = new ItemStack(Material.LAVA_BUCKET, 1);
				ItemMeta meta = stack.getItemMeta();

				meta.setDisplayName(ChatColor.AQUA + BedwarsRel._l("default.currently") + ": " + ChatColor.WHITE + BedwarsRel._l("ingame.shop.fullstackpershift"));

				meta.setLore(new ArrayList<>());
				stack.setItemMeta(meta);
			}

			inventory.setItem(size - 4, stack);
		}
		player.openInventory(inventory);
	}

	private void addCategoriesToInventory(Inventory inventory, Player player) {
		for (MerchantCategory category : categories) {
			if (category.getMaterial() == null) {
				BedwarsRel.getInstance().getServer().getConsoleSender().sendMessage(ChatWriter.pluginMessage(ChatColor.RED + "Careful: Not supported material in shop category '" + category.getName() + "'"));
			} else if (player == null || player.hasPermission(category.getPermission())) {
				ItemStack is = new ItemStack(category.getMaterial(), 1);
				ItemMeta im = is.getItemMeta();

				if (currentCategory != null && currentCategory.equals(category)) {
					im.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
				}

				im.setDisplayName(category.getName());
				im.setLore(category.getLores());
				is.setItemMeta(im);

				inventory.addItem(is);
			}
		}
	}

	private int getInventorySize(int itemAmount) {
		int nom = itemAmount % 9 == 0 ? 9 : itemAmount % 9;
		return itemAmount + 9 - nom;
	}

	@Override
	public void handleInventoryClick(InventoryClickEvent ice, Game game, Player player) {
		if (!hasOpenCategory()) {
			handleCategoryInventoryClick(ice, game, player);
		} else {
			handleBuyInventoryClick(ice, game, player);
		}
	}

	private void handleCategoryInventoryClick(InventoryClickEvent ice, Game game, Player player) {
		int catSize = getCategoriesSize(player);
		int sizeCategories = getInventorySize(catSize) + 9;
		int rawSlot = ice.getRawSlot();

		if (rawSlot >= getInventorySize(catSize) && rawSlot < sizeCategories) {
			ice.setCancelled(true);

			if (ice.getCurrentItem().getType() == Material.BUCKET) {
				game.getPlayerSettings(player).setOneStackPerShift(false);
				player.playSound(player.getLocation(), SoundMachine.get("CLICK", "UI_BUTTON_CLICK"), 10.0F, 1.0F);
				openCategoryInventory(player);
				return;
			}
			if (ice.getCurrentItem().getType() == Material.LAVA_BUCKET) {
				game.getPlayerSettings(player).setOneStackPerShift(true);
				player.playSound(player.getLocation(), SoundMachine.get("CLICK", "UI_BUTTON_CLICK"), 10.0F, 1.0F);
				openCategoryInventory(player);
				return;
			}

		}

		if (rawSlot >= sizeCategories) {
			if (ice.isShiftClick()) {
				ice.setCancelled(true);
				return;
			}

			ice.setCancelled(false);
			return;
		}

		MerchantCategory clickedCategory = getCategoryByMaterial(ice.getCurrentItem().getType());
		if (clickedCategory == null) {
			if (ice.isShiftClick()) {
				ice.setCancelled(true);
				return;
			}

			ice.setCancelled(false);
			return;
		}

		openBuyInventory(clickedCategory, player, game);
	}

	private void openBuyInventory(MerchantCategory category, Player player, Game game) {
		ArrayList<VillagerTrade> offers = category.getOffers();
		int sizeCategories = getCategoriesSize(player);
		int sizeItems = offers.size();
		int invSize = getBuyInventorySize(sizeCategories, sizeItems);

		player.playSound(player.getLocation(), SoundMachine.get("CLICK", "UI_BUTTON_CLICK"), 10.0F, 1.0F);

		currentCategory = category;
		Inventory buyInventory = Bukkit.createInventory(player, invSize, BedwarsRel._l("ingame.shop.name"));
		addCategoriesToInventory(buyInventory, player);

		for (int i = 0; i < offers.size(); i++) {
			VillagerTrade trade = offers.get(i);
			if (trade.getItem1().getType() != Material.AIR || trade.getRewardItem().getType() != Material.AIR) {
				int slot = getInventorySize(sizeCategories) + i;
				ItemStack tradeStack = toItemStack(trade, player, game);

				buyInventory.setItem(slot, tradeStack);
			}
		}
		player.openInventory(buyInventory);
	}

	private int getBuyInventorySize(int sizeCategories, int sizeOffers) {
		return getInventorySize(sizeCategories) + getInventorySize(sizeOffers);
	}

	@SuppressWarnings("deprecation")
	private ItemStack toItemStack(VillagerTrade trade, Player player, Game game) {
		ItemStack tradeStack = trade.getRewardItem().clone();
		Method colorable = Utils.getColorableMethod(tradeStack.getType());
		ItemMeta meta = tradeStack.getItemMeta();
		ItemStack item1 = trade.getItem1();
		ItemStack item2 = trade.getItem2();
		if (tradeStack.getType().equals(Material.STAINED_GLASS) || tradeStack.getType().equals(Material.WOOL) || tradeStack.getType().equals(Material.STAINED_CLAY)) {
			tradeStack.setDurability(game.getPlayerTeam(player).getColor().getDyeColor().getWoolData());
		} else if (colorable != null) {
			colorable.setAccessible(true);
			try {
				colorable.invoke(meta, game.getPlayerTeam(player).getColor().getColor());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		List<String> lores = meta.getLore();
		if (lores == null) {
			lores = new ArrayList<>();
		}
		if (trade instanceof XPVillagerTrade) {
			lores.add(BedwarsXP.l18n("SHOP_TRADE_XP", "%xp%", String.valueOf(((XPVillagerTrade) trade).getXp())));
		} else {
			lores.add(ChatColor.WHITE + String.valueOf(item1.getAmount()) + " " + item1.getItemMeta().getDisplayName());
			if (item2 != null) {
				lores.add(ChatColor.WHITE + String.valueOf(item2.getAmount()) + " " + item2.getItemMeta().getDisplayName());
			}
		}
		meta.setLore(lores);
		tradeStack.setItemMeta(meta);
		return tradeStack;
	}

	private void handleBuyInventoryClick(InventoryClickEvent ice, Game game, Player player) {
		int sizeCategories = getCategoriesSize(player);
		ArrayList<VillagerTrade> offers = currentCategory.getOffers();
		int sizeItems = offers.size();
		int totalSize = getBuyInventorySize(sizeCategories, sizeItems);

		ItemStack item = ice.getCurrentItem();
		boolean cancel = false;
		int bought = 0;
		boolean oneStackPerShift = game.getPlayerSettings(player).oneStackPerShift();

		if (currentCategory == null) {
			player.closeInventory();
			return;
		}

		if (ice.getRawSlot() < sizeCategories) {
			ice.setCancelled(true);

			if (item == null)
				return;
			if (item.getType().equals(currentCategory.getMaterial())) {
				currentCategory = null;
				openCategoryInventory(player);
			} else {
				handleCategoryInventoryClick(ice, game, player);
			}
		} else if (ice.getRawSlot() < totalSize) {
			ice.setCancelled(true);

			if (item == null || item.getType() == Material.AIR)
				return;

			MerchantCategory category = currentCategory;
			VillagerTrade trade = getTradingItem(category, item, game, player);

			if (trade == null)
				return;

			player.playSound(player.getLocation(), SoundMachine.get("ITEM_PICKUP", "ENTITY_ITEM_PICKUP"), 10.0F, 1.0F);

			if (!hasEnoughRessource(player, trade)) {
				player.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + BedwarsRel._l("errors.notenoughress")));
				return;
			}

			if (ice.isShiftClick()) {
				while (hasEnoughRessource(player, trade) && !cancel) {
					cancel = !buyItem(trade, ice.getCurrentItem(), player);
					if (!cancel && oneStackPerShift) {
						bought += item.getAmount();
						cancel = bought + item.getAmount() > 64;
					}
				}
			} else {
				buyItem(trade, ice.getCurrentItem(), player);
			}
		} else {
			ice.setCancelled(ice.isShiftClick());
		}
	}

	private boolean buyItem(VillagerTrade trade, ItemStack item, Player player) {
		PlayerInventory inventory = player.getInventory();
		boolean success = true;
		if (!(trade instanceof XPVillagerTrade)) {
			// 非XPtrade 使用旧式购买
			int item1ToPay = trade.getItem1().getAmount();
			Iterator<?> stackIterator = inventory.all(trade.getItem1().getType()).entrySet().iterator();
			int firstItem1 = inventory.first(trade.getItem1());
			takeItem(inventory, item1ToPay, stackIterator, firstItem1);
			if (trade.getItem2() != null) {
				int item2ToPay = trade.getItem2().getAmount();
				stackIterator = inventory.all(trade.getItem2().getType()).entrySet().iterator();

				int firstItem2 = inventory.first(trade.getItem2());
				takeItem(inventory, item2ToPay, stackIterator, firstItem2);
			}
		} else {
			// Already checked has enough XP
			XPManager.getXPManager(bedwars.getName()).takeXP(player, ((XPVillagerTrade) trade).getXp());
		}
		ItemStack addingItem = item.clone();
		ItemMeta meta = addingItem.getItemMeta();
		List<String> lore = meta.getLore();

		if (!lore.isEmpty()) {
			lore.remove(lore.size() - 1);
			if (trade.getItem2() != null && !(trade instanceof XPVillagerTrade)) {
				lore.remove(lore.size() - 1);
			}
		}

		meta.setLore(lore);
		addingItem.setItemMeta(meta);

		HashMap<Integer, ? extends ItemStack> notStored = inventory.addItem(addingItem);
		if (notStored.size() > 0) {
			ItemStack notAddedItem = notStored.get(0);
			int removingAmount = addingItem.getAmount() - notAddedItem.getAmount();
			addingItem.setAmount(removingAmount);
			inventory.removeItem(addingItem);

			inventory.addItem(trade.getItem1());
			if (trade.getItem2() != null) {
				inventory.addItem(trade.getItem2());
			}

			success = false;
		}

		player.updateInventory();
		return success;
	}

	private void takeItem(PlayerInventory inventory, int item1ToPay, Iterator<?> stackIterator, int firstItem1) {
		if (firstItem1 > -1) {
			inventory.clear(firstItem1);
		} else {
			while (stackIterator.hasNext()) {
				Entry<?, ?> entry = (Entry<?, ?>) stackIterator.next();
				ItemStack stack = (ItemStack) entry.getValue();

				int endAmount = stack.getAmount() - item1ToPay;
				if (endAmount < 0) {
					endAmount = 0;
				}

				item1ToPay -= stack.getAmount();
				stack.setAmount(endAmount);
				inventory.setItem((Integer) entry.getKey(), stack);

				if (item1ToPay <= 0) {
					break;
				}
			}
		}
	}

	private boolean hasEnoughRessource(Player player, VillagerTrade trade) {
		if (trade instanceof XPVillagerTrade)
			return XPManager.getXPManager(bedwars.getName()).hasEnoughXP(player, ((XPVillagerTrade) trade).getXp());
		else {
			ItemStack item1 = trade.getItem1();
			ItemStack item2 = trade.getItem2();
			PlayerInventory inventory = player.getInventory();

			if (item2 != null) {
				return inventory.contains(item1.getType(), item1.getAmount()) && inventory.contains(item2.getType(), item2.getAmount());
			} else return inventory.contains(item1.getType(), item1.getAmount());
		}
	}

	private VillagerTrade getTradingItem(MerchantCategory category, ItemStack stack, Game game, Player player) {
		for (VillagerTrade trade : category.getOffers()) {
			if (trade.getItem1().getType() != Material.AIR || trade.getRewardItem().getType() != Material.AIR) {
				ItemStack iStack = toItemStack(trade, player, game);
				if (iStack.getType() == Material.ENDER_CHEST && stack.getType() == Material.ENDER_CHEST)
					return trade;
				if ((iStack.getType() == Material.POTION || BedwarsRel.getInstance().getCurrentVersion().startsWith("v1_9") && (iStack.getType().equals(Material.valueOf("TIPPED_ARROW")) || iStack.getType().equals(Material.valueOf("LINGERING_POTION")) || iStack.getType().equals(Material.valueOf("SPLASH_POTION")))) && ((PotionMeta) iStack.getItemMeta()).getCustomEffects().equals(((PotionMeta) stack.getItemMeta()).getCustomEffects()))
					return trade;
				if (iStack.equals(stack))
					return trade;
			}
		}
		return null;
	}

	private MerchantCategory getCategoryByMaterial(Material material) {
		for (MerchantCategory category : categories) {
			if (category.getMaterial() == material)
				return category;
		}

		return null;
	}

	@Override
	public void setCurrentCategory(MerchantCategory category) {
		currentCategory = category;
	}
}