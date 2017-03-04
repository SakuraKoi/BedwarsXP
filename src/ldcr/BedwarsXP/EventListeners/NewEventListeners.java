package ldcr.BedwarsXP.EventListeners;

import ldcr.BedwarsXP.Config;
import ldcr.BedwarsXP.Main;
import ldcr.BedwarsXP.ShopReplacer;
import ldcr.BedwarsXP.XPManager;
import ldcr.BedwarsXP.Events.BedwarsXPPlayerDeathDropExpEvent;
import ldcr.BedwarsXP.Utils.ActionBarUtils;
import ldcr.BedwarsXP.XPShop.Res;
import ldcr.BedwarsXP.Utils.SoundMachine;
import io.github.bedwarsrel.BedwarsRel.Events.BedwarsGameEndEvent;
import io.github.bedwarsrel.BedwarsRel.Events.BedwarsGameStartEvent;
import io.github.bedwarsrel.BedwarsRel.Game.Game;

import org.bukkit.Bukkit;
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

public class NewEventListeners implements Listener {
	@EventHandler
	public void onItemPickup(PlayerPickupItemEvent e) {
		Game bw = io.github.bedwarsrel.BedwarsRel.Main.getInstance()
				.getGameManager().getGameOfPlayer(e.getPlayer());
		if (bw == null) {
			return;
		}
		if (!Config.isGameEnabledXP(bw.getName())) {
			return;
		}
		Player p = e.getPlayer();
		Item entity = e.getItem();
		ItemStack stack = entity.getItemStack();
		int count = Res.convertResToXP(stack);
		if (count == 0) {
			return;
		}
		e.setCancelled(true);
		XPManager.addXP(bw.getName(), p, count);
		p.playSound(p.getLocation(),
				SoundMachine.get("ORB_PICKUP", "ENTITY_EXPERIENCE_ORB_PICKUP"),
				10.0F, 1.0F);
		if (!Config.Message.equals("")) {
			ActionBarUtils.sendActionBar(p,
					Config.Message.replaceAll("%xp%", Integer.toString(count)));
			if (!ActionBarUtils.works) {
				p.sendMessage(Config.Message.replaceAll("%xp%",
						Integer.toString(count)));
			}
		}
		entity.remove();
	}

	@EventHandler
	public void onAnvilOpen(InventoryOpenEvent e) {
		if (e.getPlayer().equals(null)) {
			return;
		}
		if (e.getInventory().equals(null)) {
			return;
		}
		Game bw = io.github.bedwarsrel.BedwarsRel.Main.getInstance()
				.getGameManager().getGameOfPlayer((Player) e.getPlayer());
		if (bw.equals(null)) {
			return;
		}
		if (!Config.isGameEnabledXP(bw.getName())) {
			return;
		}
		if (e.getInventory().getType().equals(InventoryType.ANVIL)) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		Game bw = io.github.bedwarsrel.BedwarsRel.Main.getInstance()
				.getGameManager().getGameOfPlayer(e.getEntity());
		if (bw == null) {
			return;
		}
		if (!Config.isGameEnabledXP(bw.getName())) {
			return;
		}
		Player p = e.getEntity();
		int dropped = 0;
		if (Config.isDirect) {
			dropped = (int) (Config.Death);
		} else {
			dropped = (int) (p.getLevel() * (Config.Death));
		}
		BedwarsXPPlayerDeathDropExpEvent event = new BedwarsXPPlayerDeathDropExpEvent(
				bw.getName(), p, dropped);
		Bukkit.getPluginManager().callEvent(event);
		dropped = event.getDroppedXP();
		int to = XPManager.getXP(bw.getName(), p) - dropped;
		if (to < 0) {
			to = 0;
		}
		e.setNewLevel(to);
		XPManager.setXP(bw.getName(), p, to);
	}

	@EventHandler
	public void onBedWarsStart(BedwarsGameStartEvent e) {
		if (e.isCancelled()) {
			return;
		}
		if (!Config.isGameEnabledXP(e.getGame().getName())) {
			return;
		}
		ShopReplacer.replaceShop(e.getGame().getName(), Main.log);
	}

	@EventHandler
	public void onBedWarsEnd(BedwarsGameEndEvent e) {
		if (!Config.isGameEnabledXP(e.getGame().getName())) {
			return;
		}
		XPManager.reset(e.getGame().getName());
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent e) { // 在玩家传送后更新经验条
		final Game bw = io.github.bedwarsrel.BedwarsRel.Main.getInstance()
				.getGameManager().getGameOfPlayer(e.getPlayer());
		if (bw == null) {
			return;
		}
		if (!Config.isGameEnabledXP(bw.getName())) {
			return;
		}
		final Player p = e.getPlayer();
		Bukkit.getScheduler().runTaskLater(Main.plugin, new Runnable() {

			@Override
			public void run() {
				XPManager.updateXPBar(bw.getName(), p);
			}
		}, 5);

	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		Game bw = io.github.bedwarsrel.BedwarsRel.Main.getInstance()
				.getGameManager().getGameOfPlayer(e.getPlayer());
		if (bw == null) {
			return;
		}
		if (!Config.isGameEnabledXP(bw.getName())) {
			return;
		}
		XPManager.updateXPBar(bw.getName(), e.getPlayer());
	}
}
