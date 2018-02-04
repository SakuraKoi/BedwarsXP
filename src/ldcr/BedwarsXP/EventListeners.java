package ldcr.BedwarsXP;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.events.BedwarsGameEndEvent;
import io.github.bedwarsrel.events.BedwarsGameStartEvent;
import io.github.bedwarsrel.game.Game;
import ldcr.BedwarsXP.Utils.ResourceUtils;
import ldcr.BedwarsXP.Utils.SoundMachine;
import ldcr.BedwarsXP.XPShop.ShopReplacer;
import ldcr.BedwarsXP.api.XPManager;
import ldcr.BedwarsXP.api.events.BedwarsXPDeathDropXPEvent;

public class EventListeners implements Listener {
	@EventHandler
	public void onItemPickup(final PlayerPickupItemEvent e) {
		final Game bw = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(e.getPlayer());
		if (bw == null)
			return;
		if (!Config.isGameEnabledXP(bw.getName()))
			return;
		final Player p = e.getPlayer();
		final Item entity = e.getItem();
		final ItemStack stack = entity.getItemStack();

		final int count;
		if (stack.hasItemMeta() && stack.getItemMeta().getDisplayName().equals("§b§l&BedwarsXP_DropedXP")) {
			count = Integer.valueOf(stack.getItemMeta().getLore().get(0));
		} else {
			count = ResourceUtils.convertResToXP(stack);
		}
		if (count == 0)
			return;
		final XPManager xpman = XPManager.getXPManager(bw.getName());
		// if current XP > maxXP -> deny pickup
		if (Config.maxXP != 0) {
			if (xpman.getXP(p) >= Config.maxXP) {
				e.setCancelled(true);
				entity.setPickupDelay(10);
				xpman.sendMaxXPMessage(p);
				return;
			}
		}
		int added = xpman.getXP(p) + count;
		int leftXP = 0;
		// if after pickup XP>maxXP -> set XP = maxXP
		if (Config.maxXP != 0) {
			if (added > Config.maxXP) {
				leftXP = added - Config.maxXP;
				added = Config.maxXP;
			}
		}
		xpman.setXP(p, added);
		p.playSound(p.getLocation(), SoundMachine.get("ORB_PICKUP", "ENTITY_EXPERIENCE_ORB_PICKUP"), 0.2F, 1.5F);
		xpman.sendXPMessage(p, count);
		if (leftXP > 0) {
			e.setCancelled(true);
			final ItemStack s = stack.clone();
			final ItemMeta meta = s.getItemMeta();
			meta.setDisplayName("§b§l&BedwarsXP_DropedXP");
			meta.setLore(Arrays.asList(String.valueOf(leftXP)));
			s.setItemMeta(meta);
			entity.setItemStack(s);
			entity.setPickupDelay(10);
		} else {
			e.setCancelled(true);
			entity.remove();
		}
	}

	@EventHandler
	public void onAnvilOpen(final InventoryOpenEvent e) {
		if (e.getPlayer() == null)
			return;
		if (e.getInventory() == null)
			return;
		final Game bw = BedwarsRel.getInstance().getGameManager().getGameOfPlayer((Player) e.getPlayer());
		if (bw == null)
			return;
		if (!Config.isGameEnabledXP(bw.getName()))
			return;
		if (e.getInventory().getType().equals(InventoryType.ANVIL)) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerDeath(final PlayerDeathEvent e) {
		final Game bw = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(e.getEntity());
		if (bw == null)
			return;
		if (!Config.isGameEnabledXP(bw.getName()))
			return;
		final XPManager xpman = XPManager.getXPManager(bw.getName());
		final Player p = e.getEntity();
		// 计算死亡扣除经验值
		int costed = (int) (xpman.getXP(p) * (Config.deathCost));
		// 计算死亡掉落经验值
		int dropped = 0;
		if (Config.deathDrop > 0) {
			dropped = (int) (costed * Config.deathDrop);
		}
		final BedwarsXPDeathDropXPEvent event = new BedwarsXPDeathDropXPEvent(bw.getName(), p, dropped, costed);
		Bukkit.getPluginManager().callEvent(event);
		costed = event.getXPCosted();
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
			final ItemStack dropStack = new ItemStack(Material.EXP_BOTTLE, 16);
			final ItemMeta meta = dropStack.getItemMeta();
			meta.setDisplayName("§b§l&BedwarsXP_DropedXP");
			meta.setLore(Arrays.asList(String.valueOf(dropped)));
			meta.addEnchant(Enchantment.LOOT_BONUS_MOBS, 1, true);
			dropStack.setItemMeta(meta);
			final Item droppedItem = p.getWorld().dropItemNaturally(p.getLocation().add(0, 1, 0), dropStack);
			droppedItem.setPickupDelay(40);
		}
	}

	@EventHandler
	public void onBedWarsStart(final BedwarsGameStartEvent e) {
		if (e.isCancelled())
			return;
		if (!Config.isGameEnabledXP(e.getGame().getName()))
			return;
		ShopReplacer.replaceShop(e.getGame().getName(), BedwarsXP.log);
	}

	@EventHandler
	public void onBedWarsEnd(final BedwarsGameEndEvent e) {
		if (!Config.isGameEnabledXP(e.getGame().getName()))
			return;
		XPManager.reset(e.getGame().getName());
	}

	@EventHandler
	public void onPlayerTeleport(final PlayerTeleportEvent e) { // 在玩家传送后更新经验条
		final Game bw = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(e.getPlayer());
		if (bw == null)
			return;
		if (!Config.isGameEnabledXP(bw.getName()))
			return;
		final Player p = e.getPlayer();
		Bukkit.getScheduler().runTaskLater(BedwarsXP.plugin, new Runnable() {

			@Override
			public void run() {
				XPManager.getXPManager(bw.getName()).updateXPBar(p);
			}
		}, 5);

	}

	@EventHandler
	public void onPlayerInteract(final PlayerInteractEvent e) {
		final Game bw = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(e.getPlayer());
		if (bw == null)
			return;
		if (!Config.isGameEnabledXP(bw.getName()))
			return;
		XPManager.getXPManager(bw.getName()).updateXPBar(e.getPlayer());
	}
}
