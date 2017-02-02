package ldcr.BedwarsXP;

import ldcr.BedwarsXP.Utils.ActionBarUtils;
import ldcr.BedwarsXP.XPShop.XPVillagerTrade;
import io.github.yannici.bedwars.SoundMachine;
import io.github.yannici.bedwars.Events.BedwarsGameStartEvent;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class EventListeners implements Listener {
	@EventHandler
	public void onItemPickup(PlayerPickupItemEvent e) {
		if (io.github.yannici.bedwars.Main.getInstance().getGameManager()
				.getGameOfPlayer(e.getPlayer()) ==null) {
			return;
		}
		Player p = e.getPlayer();
		Item entity = e.getItem();
		ItemStack stack = entity.getItemStack();
		int count = XPVillagerTrade.convertResToXP(stack);
		if (count == 0) {
			return;
		}
		e.setCancelled(true);
		XPManager.addXP(p, count);
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
		if (e.getPlayer().equals(null))
		{return;}
		if (e.getInventory().equals(null))
		{return;}
		if (io.github.yannici.bedwars.Main.getInstance().getGameManager()
				.getGameOfPlayer((Player) e.getPlayer()).equals(null)) {
			return;
		}
		if (e.getInventory().getType().equals(InventoryType.ANVIL))
		{
			e.setCancelled(true);
		}
	}
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		if (io.github.yannici.bedwars.Main.getInstance().getGameManager()
				.getGameOfPlayer(e.getEntity()) == null) {
			return;
		}
		Player p = e.getEntity();
		int to = 0;
		if (Config.isDirect) {
			to = p.getLevel() - (int) (Config.Death);
		} else {
			to = p.getLevel() - (int) (p.getLevel() * (Config.Death));
		}
		if (to < 0) {
			to = 0;
		}
		e.setNewLevel(to);
	}

	@EventHandler
	public void onBedWarsStart(BedwarsGameStartEvent e) {
		ShopReplacer.replaceShop(e.getGame(),Main.log);
	}
}
