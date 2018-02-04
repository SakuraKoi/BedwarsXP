package ldcr.BedwarsXP.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BedwarsXPDeathDropXPEvent extends Event {
	@Override
	public HandlerList getHandlers() {
		return new HandlerList();
	}

	private String game = "";
	private final Player player;
	private int deathCosted;
	private int deathDropped;

	public BedwarsXPDeathDropXPEvent(final String game, final Player p, final int dropped, final int costed) {
		this.game = game;
		player = p;
		deathCosted = costed;
		deathDropped = dropped;
	}

	public String getGameName() {
		return game;
	}

	public Player getDeadPlayer() {
		return player;
	}

	public int getXPCosted() {
		return deathCosted;
	}

	public void setXPCosted(final int drop) {
		deathCosted = drop;
	}

	public int getXPDropped() {
		return deathDropped;
	}

	public void setXPDropped(final int deathDropped) {
		this.deathDropped = deathDropped;
	}

}
