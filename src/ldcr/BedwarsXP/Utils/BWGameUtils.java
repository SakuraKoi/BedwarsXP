package ldcr.BedwarsXP.Utils;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;

import ldcr.BedwarsXP.Main;
import ldcr.BedwarsXP.ShopReplacer;

public class BWGameUtils {
	public static boolean isAnyBedwarsRunning() {
		if (Main.isOldBedwarsPlugin) {
			ArrayList<io.github.yannici.bedwars.Game.Game> bw = io.github.yannici.bedwars.Main
					.getInstance().getGameManager().getGames();
			for (io.github.yannici.bedwars.Game.Game game : bw) {
				if (game.getState().equals(
						io.github.yannici.bedwars.Game.GameState.RUNNING)) {
					return true;
				}
			}
		} else {
			ArrayList<io.github.bedwarsrel.BedwarsRel.Game.Game> bw = io.github.bedwarsrel.BedwarsRel.Main
					.getInstance().getGameManager().getGames();
			for (io.github.bedwarsrel.BedwarsRel.Game.Game game : bw) {
				if (game.getState().equals(
						io.github.bedwarsrel.BedwarsRel.Game.GameState.RUNNING)) {
					return true;
				}
			}
		}
		return false;
	}

	public static void replaceAllShop(CommandSender sender) {
		if (Main.isOldBedwarsPlugin) {
			ArrayList<io.github.yannici.bedwars.Game.Game> bw = io.github.yannici.bedwars.Main
					.getInstance().getGameManager().getGames();
			for (io.github.yannici.bedwars.Game.Game game : bw) {
				if (game.getState().equals(
						io.github.yannici.bedwars.Game.GameState.RUNNING)) {
					ShopReplacer.replaceShop(game.getName(), sender);
				}
			}
		} else {
			ArrayList<io.github.bedwarsrel.BedwarsRel.Game.Game> bw = io.github.bedwarsrel.BedwarsRel.Main
					.getInstance().getGameManager().getGames();
			for (io.github.bedwarsrel.BedwarsRel.Game.Game game : bw) {
				if (game.getState().equals(
						io.github.bedwarsrel.BedwarsRel.Game.GameState.RUNNING)) {
					ShopReplacer.replaceShop(game.getName(), sender);
				}
			}
		}
	}
}
