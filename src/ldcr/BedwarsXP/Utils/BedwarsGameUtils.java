package ldcr.BedwarsXP.Utils;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import ldcr.BedwarsXP.XPShop.ShopReplacer;

public class BedwarsGameUtils {
	public static boolean isGameExists(final String bw) {
		return !BedwarsRel.getInstance().getGameManager().getGame(bw).equals(null);
	}

	public static boolean isGameRunning(final String bw) {
		return BedwarsRel.getInstance().getGameManager().getGame(bw).getState().equals(GameState.RUNNING);
	}

	public static boolean isAnyBedwarsRunning() {
		final ArrayList<Game> bw = BedwarsRel.getInstance().getGameManager().getGames();
		for (final Game game : bw) {
			if (game.getState().equals(GameState.RUNNING))
				return true;
		}
		return false;
	}

	public static void replaceAllShop(final CommandSender sender) {
		final ArrayList<Game> bw = BedwarsRel.getInstance().getGameManager().getGames();
		for (final Game game : bw) {
			if (game.getState().equals(GameState.RUNNING)) {
				ShopReplacer.replaceShop(game.getName(), sender);
			}
		}
	}
}
