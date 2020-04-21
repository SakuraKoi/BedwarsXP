package ldcr.BedwarsXP;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.events.BedwarsGameEndEvent;
import io.github.bedwarsrel.events.BedwarsGameStartEvent;
import io.github.bedwarsrel.game.Game;
import ldcr.BedwarsXP.XPShop.ShopReplacer;
import ldcr.BedwarsXP.api.XPManager;
import ldcr.BedwarsXP.api.events.BedwarsXPDeathDropXPEvent;
import ldcr.BedwarsXP.utils.ResourceUtils;
import ldcr.BedwarsXP.utils.SoundMachine;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

class EventListeners implements Listener {
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onItemPickup(PlayerPickupItemEvent e) {
		Player p = e.getPlayer();
		Game bw = checkGame(p);
		if (bw == null) return;

		Item entity = e.getItem();
		ItemStack stack = entity.getItemStack();

		int count;
		if (stack.hasItemMeta() && stack.getItemMeta().getDisplayName().equals("§b§l&BedwarsXP_DroppedXP")) {
			count = Integer.valueOf(stack.getItemMeta().getLore().get(0));
		} else {
			count = ResourceUtils.convertResToXP(stack);
		}

		if (pickupXP(bw, p, count)) {
			e.setCancelled(true);
			entity.remove();
		} else {
			e.setCancelled(true);
			entity.setPickupDelay(10);
		}
	}

	private boolean pickupXP(Game bw, Player player, int count) {
		if (count <= 0) return true;

		XPManager xpman = XPManager.getXPManager(bw.getName());
		// if current XP > maxXP -> deny pickup
		if (Config.maxXP != 0 && xpman.getXP(player) >= Config.maxXP) {
			xpman.sendMaxXPMessage(player);
			return false;
		}
		int added = xpman.getXP(player) + count;
		int leftXP = 0;
		// if after pickup XP>maxXP -> set XP = maxXP
		if (Config.maxXP != 0 && added > Config.maxXP) {
			leftXP = added - Config.maxXP;
			added = Config.maxXP;
		}
		xpman.setXP(player, added);
		player.playSound(player.getLocation(), SoundMachine.get("ORB_PICKUP", "ENTITY_EXPERIENCE_ORB_PICKUP"), 0.2F, 1.5F);
		xpman.sendXPMessage(player, count);
		if (leftXP > 0) {
			dropXPBottle(player, leftXP);
		}
		return true;
	}

	private void dropXPBottle(Player player, int xp) {
		ItemStack dropStack = new ItemStack(Material.EXP_BOTTLE, 16);
		ItemMeta meta = dropStack.getItemMeta();
		meta.setDisplayName("§b§l&BedwarsXP_DroppedXP");
		meta.setLore(Collections.singletonList(String.valueOf(xp)));
		meta.addEnchant(Enchantment.LOOT_BONUS_MOBS, 1, true);
		dropStack.setItemMeta(meta);
		Item droppedItem = player.getWorld().dropItemNaturally(player.getLocation().add(0, 1, 0), dropStack);
		droppedItem.setPickupDelay(40);
	}

	@EventHandler
	public void onAnvilOpen(InventoryOpenEvent e) {
		if (e.getPlayer() == null)
			return;
		if (e.getInventory() == null)
			return;
		Game bw = checkGame((Player) e.getPlayer());
		if (bw == null) return;
		if (e.getInventory().getType().equals(InventoryType.ANVIL)) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();
		Game bw = checkGame(p);
		if (bw == null) return;

		XPManager xpman = XPManager.getXPManager(bw.getName());
		// 计算死亡扣除经验值
		int costed = (int) (xpman.getXP(p) * Config.deathCost);
		// 计算死亡掉落经验值
		int dropped = 0;
		if (Config.deathDrop > 0) {
			dropped = (int) (costed * Config.deathDrop);
		}
		BedwarsXPDeathDropXPEvent event = new BedwarsXPDeathDropXPEvent(bw.getName(), p, dropped, costed);
		Bukkit.getPluginManager().callEvent(event);
		costed = event.getXPCost();
		dropped = event.getXPDropped();
		// 扣除经验
		int to = xpman.getXP(p) - costed;
		if (to < 0) {
			to = 0;
		}
		e.setNewLevel(to);
		xpman.setXP(p, to);
		// 掉落经验
		if (Config.deathDrop > 0) {
			if (dropped < 1)
				return;
			if (Config.dontDropExpBottle) {
				EntityDamageEvent ev = p.getLastDamageCause();
				if (ev instanceof EntityDamageByEntityEvent) {
					Object killer = ((EntityDamageByEntityEvent) ev).getDamager();
					if (killer instanceof Projectile) {
						killer = ((Projectile) killer).getShooter();
					}
					if (killer instanceof Player) {
						pickupXP(bw, (Player) killer, dropped);
						return;
					}
				}
			}
			dropXPBottle(p, dropped);
		}
	}

	@EventHandler
	public void onBedWarsStart(BedwarsGameStartEvent e) {
		if (e.isCancelled())
			return;
		if (!Config.isGameEnabledXP(e.getGame().getName()))
			return;
		ShopReplacer.replaceShop(e.getGame().getName(), BedwarsXP.getConsoleSender());
	}

	@EventHandler
	public void onBedWarsEnd(BedwarsGameEndEvent e) {
		if (!Config.isGameEnabledXP(e.getGame().getName()))
			return;
		XPManager.reset(e.getGame().getName());
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent e) { // 在玩家传送后更新经验条
		Player p = e.getPlayer();
		Game bw = checkGame(p);
		if (bw == null) return;
		Bukkit.getScheduler().runTaskLater(BedwarsXP.getInstance(),
				() -> XPManager.getXPManager(bw.getName()).updateXPBar(p), 5);

	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		Game bw = checkGame(e.getPlayer());
		if (bw == null) return;
		XPManager.getXPManager(bw.getName()).updateXPBar(e.getPlayer());
	}

	private Game checkGame(Player player) {
		Game bw = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);
		if (bw == null)
			return null;
		if (!Config.isGameEnabledXP(bw.getName()))
			return null;
		return bw;
	}
}
