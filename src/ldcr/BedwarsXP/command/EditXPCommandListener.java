package ldcr.BedwarsXP.command;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import ldcr.BedwarsXP.Config;
import ldcr.BedwarsXP.Utils.SendMessageUtils;
import ldcr.BedwarsXP.api.XPManager;

public class EditXPCommandListener implements CommandExecutor {

	@Override
	public boolean onCommand(final CommandSender sender, final Command arg1, final String arg2, final String[] args) {
		if (args.length == 0) {
			SendMessageUtils.sendMessage(sender, "§6§l[BedwarsXP] §b经验起床插件 §lBy.Ldcr", "§6§l[BedwarsXP] §a/editxp <经验值>  设置自己的经验值", "§6§l[BedwarsXP] §a/editxp <玩家> <经验值> 设置其他玩家的经验值");
			return true;
		}
		if (!sender.hasPermission("bedwarsxp.admin")) {
			sender.sendMessage("§6§l[BedwarsXP] §c你没有权限执行此命令");
			return true;
		}
		final String user = args[0];
		final OfflinePlayer offPlayer = Bukkit.getPlayer(user);
		if (offPlayer != null) {
			if (args.length < 2) {
				SendMessageUtils.sendMessage(sender, "§6§l[BedwarsXP] §b经验起床插件 §lBy.Ldcr", "§6§l[BedwarsXP] §a/editxp <经验值>  设置自己的经验值", "§6§l[BedwarsXP] §a/editxp <玩家> <经验值> 设置其他玩家的经验值");
				return true;
			}
			if (offPlayer.isOnline()) {
				final Player p = offPlayer.getPlayer();
				final Game bw = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(p);
				if (bw == null) {
					sender.sendMessage("§6§l[BedwarsXP] §c玩家 " + p.getName() + " 不在游戏中!");
					return true;
				}
				if (!Config.isGameEnabledXP(bw.getName())) {
					sender.sendMessage("§6§l[BedwarsXP] §c玩家 " + p.getName() + " 所在的游戏没有开启经验起床模式!");
					return true;
				}
				final int xp;
				try {
					xp = Integer.valueOf(args[1]);
				} catch (final NumberFormatException e) {
					sender.sendMessage("§6§l[BedwarsXP] §c输入经验值的不是一个有效数字!");
					return true;
				}
				XPManager.getXPManager(bw.getName()).setXP(p, xp);
				sender.sendMessage("§6§l[BedwarsXP] §a玩家 " + p.getName() + " 的经验值已被设置为 " + xp);
			} else {
				sender.sendMessage("§6§l[BedwarsXP] §c玩家 " + offPlayer.getName() + " 不在线!");
				return true;
			}
			return true;
		}
		if (!(sender instanceof Player)) {
			SendMessageUtils.sendMessage(sender, "§6§l[BedwarsXP] §b经验起床插件 §lBy.Ldcr", "§6§l[BedwarsXP] §a/editxp <玩家> <经验值> 设置玩家的经验值");
			return true;
		}
		final Player p = (Player) sender;
		final Game bw = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(p);
		if (bw == null) {
			sender.sendMessage("§6§l[BedwarsXP] §c玩家 " + p.getName() + " 不在游戏中!");
			return true;
		}
		if (!Config.isGameEnabledXP(bw.getName())) {
			sender.sendMessage("§6§l[BedwarsXP] §c玩家 " + p.getName() + " 所在的游戏没有开启经验起床模式!");
			return true;
		}
		final int xp;
		try {
			xp = Integer.valueOf(args[0]);
		} catch (final NumberFormatException e) {
			sender.sendMessage("§6§l[BedwarsXP] §c输入经验值的不是一个有效数字!");
			return true;
		}
		XPManager.getXPManager(bw.getName()).setXP(p, xp);
		sender.sendMessage("§6§l[BedwarsXP] §a玩家 " + p.getName() + " 的经验值已被设置为 " + xp);
		return true;
	}

}
