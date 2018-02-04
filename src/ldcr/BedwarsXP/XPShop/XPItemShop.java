package ldcr.BedwarsXP.XPShop;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

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

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.shop.NewItemShop;
import io.github.bedwarsrel.utils.ChatWriter;
import io.github.bedwarsrel.utils.Utils;
import io.github.bedwarsrel.villager.MerchantCategory;
import io.github.bedwarsrel.villager.VillagerTrade;
import ldcr.BedwarsXP.Utils.SoundMachine;
import ldcr.BedwarsXP.api.XPManager;

public class XPItemShop extends NewItemShop {
	private final Game bedwars;

	public XPItemShop(final List<MerchantCategory> cate, final Game bw) {
		super(cate);
		categories = cate;
		bedwars = bw;
	}

	private List<MerchantCategory> categories = null;
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
	public boolean hasOpenCategory(final MerchantCategory category) {
		if (currentCategory == null)
			return false;

		return currentCategory.equals(category);
	}

	private int getCategoriesSize(final Player player) {
		int size = 0;
		for (final MerchantCategory cat : categories) {
			if ((cat.getMaterial() != null) && ((player == null) || (player.hasPermission(cat.getPermission())))) {
				size++;
			}
		}
		return size;
	}

	@Override
	public void openCategoryInventory(final Player player) {
		final int catSize = getCategoriesSize(player);
		final int nom = (catSize % 9) == 0 ? 9 : catSize % 9;
		final int size = catSize + (9 - nom) + 9;

		final Inventory inventory = Bukkit.createInventory(player, size, BedwarsRel._l("ingame.shop.name"));

		addCategoriesToInventory(inventory, player);

		final Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
		ItemStack stack = null;

		if (game != null) {
			if (game.getPlayerSettings(player).oneStackPerShift()) {
				stack = new ItemStack(Material.BUCKET, 1);
				final ItemMeta meta = stack.getItemMeta();

				meta.setDisplayName(ChatColor.AQUA + BedwarsRel._l("default.currently") + ": " + ChatColor.WHITE + BedwarsRel._l("ingame.shop.onestackpershift"));

				meta.setLore(new ArrayList<String>());
				stack.setItemMeta(meta);
			} else {
				stack = new ItemStack(Material.LAVA_BUCKET, 1);
				final ItemMeta meta = stack.getItemMeta();

				meta.setDisplayName(ChatColor.AQUA + BedwarsRel._l("default.currently") + ": " + ChatColor.WHITE + BedwarsRel._l("ingame.shop.fullstackpershift"));

				meta.setLore(new ArrayList<String>());
				stack.setItemMeta(meta);
			}

			if (stack != null) {
				inventory.setItem(size - 4, stack);
			}
		}
		player.openInventory(inventory);
	}

	private void addCategoriesToInventory(final Inventory inventory, final Player player) {
		for (final MerchantCategory category : categories) {
			if (category.getMaterial() == null) {
				BedwarsRel.getInstance().getServer().getConsoleSender().sendMessage(ChatWriter.pluginMessage(ChatColor.RED + "Careful: Not supported material in shop category '" + category.getName() + "'"));
			} else if ((player == null) || (player.hasPermission(category.getPermission()))) {
				final ItemStack is = new ItemStack(category.getMaterial(), 1);
				final ItemMeta im = is.getItemMeta();

				if ((currentCategory != null) && (currentCategory.equals(category))) {
					im.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
				}

				im.setDisplayName(category.getName());
				im.setLore(category.getLores());
				is.setItemMeta(im);

				inventory.addItem(new ItemStack[] { is });
			}
		}
	}

	private int getInventorySize(final int itemAmount) {
		final int nom = (itemAmount % 9) == 0 ? 9 : itemAmount % 9;
		return itemAmount + (9 - nom);
	}

	@Override
	public void handleInventoryClick(final InventoryClickEvent ice, final Game game, final Player player) {
		if (!hasOpenCategory()) {
			handleCategoryInventoryClick(ice, game, player);
		} else {
			handleBuyInventoryClick(ice, game, player);
		}
	}

	private void handleCategoryInventoryClick(final InventoryClickEvent ice, final Game game, final Player player) {
		final int catSize = getCategoriesSize(player);
		final int sizeCategories = getInventorySize(catSize) + 9;
		final int rawSlot = ice.getRawSlot();

		if ((rawSlot >= getInventorySize(catSize)) && (rawSlot < sizeCategories)) {
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

		final MerchantCategory clickedCategory = getCategoryByMaterial(ice.getCurrentItem().getType());
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

	private void openBuyInventory(final MerchantCategory category, final Player player, final Game game) {
		final ArrayList<VillagerTrade> offers = category.getOffers();
		final int sizeCategories = getCategoriesSize(player);
		final int sizeItems = offers.size();
		final int invSize = getBuyInventorySize(sizeCategories, sizeItems);

		player.playSound(player.getLocation(), SoundMachine.get("CLICK", "UI_BUTTON_CLICK"), 10.0F, 1.0F);

		currentCategory = category;
		final Inventory buyInventory = Bukkit.createInventory(player, invSize, BedwarsRel._l("ingame.shop.name"));
		addCategoriesToInventory(buyInventory, player);

		for (int i = 0; i < offers.size(); i++) {
			final VillagerTrade trade = offers.get(i);
			if ((trade.getItem1().getType() != Material.AIR) || (trade.getRewardItem().getType() != Material.AIR)) {
				final int slot = getInventorySize(sizeCategories) + i;
				final ItemStack tradeStack = toItemStack(trade, player, game);

				buyInventory.setItem(slot, tradeStack);
			}
		}
		player.openInventory(buyInventory);
	}

	private int getBuyInventorySize(final int sizeCategories, final int sizeOffers) {
		return getInventorySize(sizeCategories) + getInventorySize(sizeOffers);
	}

	@SuppressWarnings("deprecation")
	private ItemStack toItemStack(final VillagerTrade trade, final Player player, final Game game) {
		final ItemStack tradeStack = trade.getRewardItem().clone();
		final Method colorable = Utils.getColorableMethod(tradeStack.getType());
		final ItemMeta meta = tradeStack.getItemMeta();
		final ItemStack item1 = trade.getItem1();
		final ItemStack item2 = trade.getItem2();
		if ((tradeStack.getType().equals(Material.STAINED_GLASS)) || (tradeStack.getType().equals(Material.WOOL)) || (tradeStack.getType().equals(Material.STAINED_CLAY))) {
			tradeStack.setDurability(game.getPlayerTeam(player).getColor().getDyeColor().getData());
		} else if (colorable != null) {
			colorable.setAccessible(true);
			try {
				colorable.invoke(meta, new Object[] { game.getPlayerTeam(player).getColor().getColor() });
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
		List<String> lores = meta.getLore();
		if (lores == null) {
			lores = new ArrayList<String>();
		}
		if (trade instanceof XPVillagerTrade) {
			lores.add("§a" + ((XPVillagerTrade) trade).getXP() + " 经验");
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

	private void handleBuyInventoryClick(final InventoryClickEvent ice, final Game game, final Player player) {
		final int sizeCategories = getCategoriesSize(player);
		final ArrayList<VillagerTrade> offers = currentCategory.getOffers();
		final int sizeItems = offers.size();
		final int totalSize = getBuyInventorySize(sizeCategories, sizeItems);

		final ItemStack item = ice.getCurrentItem();
		boolean cancel = false;
		int bought = 0;
		final boolean oneStackPerShift = game.getPlayerSettings(player).oneStackPerShift();

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

			if ((item == null) || (item.getType() == Material.AIR))
				return;

			final MerchantCategory category = currentCategory;
			final VillagerTrade trade = getTradingItem(category, item, game, player);

			if (trade == null)
				return;

			player.playSound(player.getLocation(), SoundMachine.get("ITEM_PICKUP", "ENTITY_ITEM_PICKUP"), 10.0F, 1.0F);

			if (!hasEnoughRessource(player, trade)) {
				player.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + BedwarsRel._l("errors.notenoughress")));
				return;
			}

			if (ice.isShiftClick()) {
				while ((hasEnoughRessource(player, trade)) && (!cancel)) {
					cancel = !buyItem(trade, ice.getCurrentItem(), player);
					if ((!cancel) && (oneStackPerShift)) {
						bought += item.getAmount();
						cancel = (bought + item.getAmount()) > 64;
					}
				}

				bought = 0;
			} else {
				buyItem(trade, ice.getCurrentItem(), player);
			}
		} else {
			if (ice.isShiftClick()) {
				ice.setCancelled(true);
			} else {
				ice.setCancelled(false);
			}

			return;
		}
	}

	private boolean buyItem(final VillagerTrade trade, final ItemStack item, final Player player) {
		final PlayerInventory inventory = player.getInventory();
		boolean success = true;
		if (!(trade instanceof XPVillagerTrade)) {
			// 非XPtrade 使用旧式购买
			int item1ToPay = trade.getItem1().getAmount();
			Iterator<?> stackIterator = inventory.all(trade.getItem1().getType()).entrySet().iterator();
			final int firstItem1 = inventory.first(trade.getItem1());
			if (firstItem1 > -1) {
				inventory.clear(firstItem1);
			} else {
				while (stackIterator.hasNext()) {
					final Entry<?, ?> entry = (Entry<?, ?>) stackIterator.next();
					final ItemStack stack = (ItemStack) entry.getValue();

					int endAmount = stack.getAmount() - item1ToPay;
					if (endAmount < 0) {
						endAmount = 0;
					}

					item1ToPay -= stack.getAmount();
					stack.setAmount(endAmount);
					inventory.setItem(((Integer) entry.getKey()).intValue(), stack);

					if (item1ToPay <= 0) {
						break;
					}
				}
			}
			if (trade.getItem2() != null) {
				int item2ToPay = trade.getItem2().getAmount();
				stackIterator = inventory.all(trade.getItem2().getType()).entrySet().iterator();

				final int firstItem2 = inventory.first(trade.getItem2());
				if (firstItem2 > -1) {
					inventory.clear(firstItem2);
				} else {
					while (stackIterator.hasNext()) {
						final Entry<?, ?> entry = (Entry<?, ?>) stackIterator.next();

						final ItemStack stack = (ItemStack) entry.getValue();

						int endAmount = stack.getAmount() - item2ToPay;
						if (endAmount < 0) {
							endAmount = 0;
						}

						item2ToPay -= stack.getAmount();
						stack.setAmount(endAmount);
						inventory.setItem(((Integer) entry.getKey()).intValue(), stack);

						if (item2ToPay <= 0) {
							break;
						}
					}
				}
			}
		} else {
			XPManager.getXPManager(bedwars.getName()).takeXP(player, ((XPVillagerTrade) trade).getXP());
		}
		final ItemStack addingItem = item.clone();
		final ItemMeta meta = addingItem.getItemMeta();
		final List<String> lore = meta.getLore();

		if (lore.size() > 0) {
			lore.remove(lore.size() - 1);
			if ((trade.getItem2() != null) && !(trade instanceof XPVillagerTrade)) {
				lore.remove(lore.size() - 1);
			}
		}

		meta.setLore(lore);
		addingItem.setItemMeta(meta);

		final HashMap<Integer, ? extends ItemStack> notStored = inventory.addItem(new ItemStack[] { addingItem });
		if (notStored.size() > 0) {
			final ItemStack notAddedItem = notStored.get(Integer.valueOf(0));
			final int removingAmount = addingItem.getAmount() - notAddedItem.getAmount();
			addingItem.setAmount(removingAmount);
			inventory.removeItem(new ItemStack[] { addingItem });

			inventory.addItem(new ItemStack[] { trade.getItem1() });
			if (trade.getItem2() != null) {
				inventory.addItem(new ItemStack[] { trade.getItem2() });
			}

			success = false;
		}

		player.updateInventory();
		return success;
	}

	private boolean hasEnoughRessource(final Player player, final VillagerTrade trade) {
		if (trade instanceof XPVillagerTrade)
			return XPManager.getXPManager(bedwars.getName()).hasEnoughXP(player, ((XPVillagerTrade) trade).getXP());
		else {
			final ItemStack item1 = trade.getItem1();
			final ItemStack item2 = trade.getItem2();
			final PlayerInventory inventory = player.getInventory();

			if (item2 != null) {
				if ((!inventory.contains(item1.getType(), item1.getAmount())) || (!inventory.contains(item2.getType(), item2.getAmount())))
					return false;
			} else if (!inventory.contains(item1.getType(), item1.getAmount()))
				return false;

			return true;
		}
	}

	private VillagerTrade getTradingItem(final MerchantCategory category, final ItemStack stack, final Game game, final Player player) {
		for (final VillagerTrade trade : category.getOffers()) {
			if ((trade.getItem1().getType() != Material.AIR) || (trade.getRewardItem().getType() != Material.AIR)) {
				final ItemStack iStack = toItemStack(trade, player, game);
				if ((iStack.getType() == Material.ENDER_CHEST) && (stack.getType() == Material.ENDER_CHEST))
					return trade;
				if (((iStack.getType() == Material.POTION) || ((BedwarsRel.getInstance().getCurrentVersion().startsWith("v1_9")) && ((iStack.getType().equals(Material.valueOf("TIPPED_ARROW"))) || (iStack.getType().equals(Material.valueOf("LINGERING_POTION"))) || (iStack.getType().equals(Material.valueOf("SPLASH_POTION")))))) && (((PotionMeta) iStack.getItemMeta()).getCustomEffects().equals(((PotionMeta) stack.getItemMeta()).getCustomEffects())))
					return trade;
				if (iStack.equals(stack))
					return trade;
			}
		}
		return null;
	}

	private MerchantCategory getCategoryByMaterial(final Material material) {
		for (final MerchantCategory category : categories) {
			if (category.getMaterial() == material)
				return category;
		}

		return null;
	}

	@Override
	public void setCurrentCategory(final MerchantCategory category) {
		currentCategory = category;
	}
}