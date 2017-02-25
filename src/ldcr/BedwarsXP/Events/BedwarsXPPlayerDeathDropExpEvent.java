package ldcr.BedwarsXP.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BedwarsXPPlayerDeathDropExpEvent extends Event {
	@Override
	public HandlerList getHandlers() {
		return new HandlerList();
	}

	private String game = "";
	Player p;
	int d;

	public BedwarsXPPlayerDeathDropExpEvent(String game, Player p, int dropped) {
		this.game = game;
		this.p = p;
		this.d = dropped;
	}

	public String getGameName() {
		return game;
	}

	public Player getDeadPlayer() {
		return p;
	}

	public int getDroppedXP() {
		return d;
	}

	public void setDroppedXP(int drop) {
		this.d = drop;
	}

}
