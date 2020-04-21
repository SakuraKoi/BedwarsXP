package ldcr.BedwarsXP.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BedwarsXPDeathDropXPEvent extends Event {
	@Override
	public HandlerList getHandlers() {
		return new HandlerList();
	}

	private final String game;
	private final Player player;
	private int deathCost;
	private int deathDropped;

	public BedwarsXPDeathDropXPEvent(String game, Player p, int dropped, int cost) {
		this.game = game;
		player = p;
		deathCost = cost;
		deathDropped = dropped;
	}

	public String getGameName() {
		return game;
	}

	public Player getDeadPlayer() {
		return player;
	}

	public int getXPCost() {
		return deathCost;
	}

	public void setXPCost(int drop) {
		deathCost = drop;
	}

	public int getXPDropped() {
		return deathDropped;
	}

	public void setXPDropped(int deathDropped) {
		this.deathDropped = deathDropped;
	}

	@Deprecated
	public int getXPCosted() {
		return deathCost;
	}

	@Deprecated
	public void setXPCosted(int drop) {
		deathCost = drop;
	}

}
