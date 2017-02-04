package ldcr.BedwarsXP.Utils;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;

import ldcr.BedwarsXP.ShopReplacer;

import io.github.yannici.bedwars.Main;
import io.github.yannici.bedwars.Game.Game;
import io.github.yannici.bedwars.Game.GameState;

public class BWGameUtils {
	public static boolean isAnyBedwarsRunning() {
		ArrayList<Game> bw = Main.getInstance().getGameManager().getGames();
		for (Game game : bw) {
			if (game.getState().equals(GameState.RUNNING)) {
				return true;
			}
		}
		return false;
	}

	public static void replaceAllShop(CommandSender sender) {
		ArrayList<Game> bw = Main.getInstance().getGameManager().getGames();
		for (Game game : bw) {
			if (game.getState().equals(GameState.RUNNING)) {
				ShopReplacer.replaceShop(game, sender);
			}
		}
	}
}
