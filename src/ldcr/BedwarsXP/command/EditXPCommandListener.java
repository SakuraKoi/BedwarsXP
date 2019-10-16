package ldcr.BedwarsXP.command;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import ldcr.BedwarsXP.BedwarsXP;
import ldcr.BedwarsXP.Config;
import ldcr.BedwarsXP.api.XPManager;
import ldcr.BedwarsXP.utils.SendMessageUtils;

public class EditXPCommandListener implements CommandExecutor {

	@Override
	public boolean onCommand(final CommandSender sender, final Command arg1, final String arg2, final String[] args) {
		if (args.length < 3) {
			SendMessageUtils.sendMessage(sender,
					"§6§lBedwarsXP §7>> §bBedwarsXP v."+BedwarsXP.getInstance().getDescription().getVersion()+" §lBy.SakuraKooi",
					"§6§lBedwarsXP §7>> §a"+BedwarsXP.l18n("HELP_EDITXP"));
			return true;
		}
		if (!sender.hasPermission("bedwarsxp.admin")) {
			sender.sendMessage("§6§lBedwarsXP §7>> §c"+BedwarsXP.l18n("YOU_DONT_HAVE_PERMISSION_TO_EXECUTE_THIS_COMMAND"));
			return true;
		}
		final String user = args[1];
		final OfflinePlayer offPlayer = Bukkit.getPlayer(user);
		if (offPlayer != null) {
			if (offPlayer.isOnline()) {
				final Player p = offPlayer.getPlayer();
				final Game bw = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(p);
				if (bw == null) {
					sender.sendMessage("§6§lBedwarsXP §7>> §c"+BedwarsXP.l18n("EDITXP_PLAYER_NOT_IN_GAME", "%player%", p.getName()));
					return true;
				}
				if (!Config.isGameEnabledXP(bw.getName())) {
					sender.sendMessage("§6§lBedwarsXP §7>> §c"+BedwarsXP.l18n("EDITXP_GAME_IS_NOT_XP_MODE", "%player%", p.getName()));
					return true;
				}
				final int xp;
				try {
					xp = Integer.valueOf(args[2]);
				} catch (final NumberFormatException e) {
					sender.sendMessage("§6§lBedwarsXP §7>> §c"+BedwarsXP.l18n("EDITXP_XP_IS_NOT_A_NUMBER"));
					return true;
				}
				if (args[0].equalsIgnoreCase("set")) {
					XPManager.getXPManager(bw.getName()).setXP(p, xp);
					sender.sendMessage("§6§lBedwarsXP §7>> §a"+BedwarsXP.l18n("EDITXP_XP_HAS_BEEN_SET_TO", "%player%", p.getName(), "%xp%", String.valueOf(xp)));
				} else if (args[0].equalsIgnoreCase("add")) {
					final int current = XPManager.getXPManager(bw.getName()).getXP(p);
					XPManager.getXPManager(bw.getName()).setXP(p, current+xp);
					sender.sendMessage("§6§lBedwarsXP §7>> §a"+BedwarsXP.l18n("EDITXP_XP_HAS_BEEN_SET_TO", "%player%", p.getName(), "%xp%", String.valueOf(current+xp)));
				} else if (args[0].equalsIgnoreCase("take")) {
					final int current = XPManager.getXPManager(bw.getName()).getXP(p);
					XPManager.getXPManager(bw.getName()).setXP(p, current-xp);
					sender.sendMessage("§6§lBedwarsXP §7>> §a"+BedwarsXP.l18n("EDITXP_XP_HAS_BEEN_SET_TO", "%player%", p.getName(), "%xp%", String.valueOf(current-xp)));
				} else {
					sender.sendMessage("§6§lBedwarsXP §7>> §c"+BedwarsXP.l18n("HELP_EDITXP"));
				}
			} else {
				sender.sendMessage("§6§lBedwarsXP §7>> §c"+BedwarsXP.l18n("EDITXP_PLAYER_NOT_IN_GAME", "%player%", offPlayer.getName()));
			}
		} else {
			sender.sendMessage("§6§lBedwarsXP §7>> §c"+BedwarsXP.l18n("EDITXP_PLAYER_NOT_IN_GAME", "%player%", args[1]));
		}
		return true;
	}

}
