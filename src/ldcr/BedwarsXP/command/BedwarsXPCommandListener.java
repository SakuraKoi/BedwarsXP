package ldcr.BedwarsXP.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import ldcr.BedwarsXP.Config;
import ldcr.BedwarsXP.utils.BedwarsGameUtils;
import ldcr.BedwarsXP.utils.SendMessageUtils;

public class BedwarsXPCommandListener implements CommandExecutor {

	@Override
	public boolean onCommand(final CommandSender sender, final Command arg1, final String arg2, final String[] args) {
		if (args.length == 0) {
			SendMessageUtils.sendMessage(sender, "§6§lBedwarsXP §7>> §b经验起床插件 §lBy.SakuraKooi", "§6§lBedwarsXP §7>> §a/bwxp reload        重载插件配置", "§6§lBedwarsXP §7>> §a/bwxp enable  <游戏> 激活指定游戏的经验起床模式", "§6§lBedwarsXP §7>> §a/bwxp disable <游戏> 禁用指定游戏的经验起床模式");
			return true;
		}
		if (!sender.hasPermission("bedwarsxp.admin")) {
			sender.sendMessage("§6§lBedwarsXP §7>> §c你没有权限执行此命令");
			return true;
		}
		switch (args[0].toLowerCase()) {
			default: {
				sender.sendMessage("§6§lBedwarsXP §7>> §b/bwxp 重载经验起床插件配置");
				break;
			}
			case "reload": {
				Config.loadConfig();
				sender.sendMessage("§6§lBedwarsXP §7>> §b成功重载配置文件~");
				if (BedwarsGameUtils.isAnyBedwarsRunning()) {
					SendMessageUtils.sendMessage(sender, "§6§lBedwarsXP §7>> §b当前有游戏正在运行", "§6§lBedwarsXP §7>> §b开始更新运行中的游戏的经验商店");
					BedwarsGameUtils.replaceAllShop(sender);
				}
			}
			case "enable": {
				if (args.length != 2) {
					sender.sendMessage("§6§lBedwarsXP §7>> §a/bwxp enable  <游戏> 激活指定游戏的经验起床模式");
					return true;
				}
				if (!BedwarsGameUtils.isGameExists(args[1])) {
					sender.sendMessage("§6§lBedwarsXP §7>> §c错误: 找不到游戏 [" + args[1] + " ]");
					return true;
				}
				final String result = Config.setGameEnableXP(args[1], true);
				if (result.equals("")) {
					sender.sendMessage("§6§lBedwarsXP §7>> §a成功激活了游戏 " + args[1] + " 的经验起床模式");
					if (BedwarsGameUtils.isGameRunning(args[1])) {
						sender.sendMessage("§6§lBedwarsXP §7>> §4游戏 " + args[1] + " 正在运行,请手动重启游戏");
					}
				} else {
					SendMessageUtils.sendMessage(sender, "§6§lBedwarsXP §7>> §c错误: 在尝试激活游戏 " + args[1] + " 的经验起床模式时出错", "§6§lBedwarsXP §7>> §c" + result);
				}
				return true;
			}
			case "disable": {
				if (args.length != 2) {
					sender.sendMessage("§6§lBedwarsXP §7>> §a/bwxp disable  <游戏> 禁用指定游戏的经验起床模式");
					return true;
				}
				if (!BedwarsGameUtils.isGameExists(args[1])) {
					sender.sendMessage("§6§lBedwarsXP §7>> §c错误: 找不到游戏 [" + args[1] + " ]");
					return true;
				}
				final String result = Config.setGameEnableXP(args[1], false);
				if (result.equals("")) {
					sender.sendMessage("§6§lBedwarsXP §7>> §a成功禁用了游戏 " + args[1] + " 的经验起床模式");
					if (BedwarsGameUtils.isGameRunning(args[1])) {
						sender.sendMessage("§6§lBedwarsXP §7>> §4游戏 " + args[1] + " 正在运行,请手动重启游戏");
					}
				} else {
					SendMessageUtils.sendMessage(sender, "§6§lBedwarsXP §7>> §c错误: 在尝试禁用游戏 " + args[1] + " 的经验起床模式时出错", "§6§lBedwarsXP §7>> §c" + result);
				}
				return true;
			}
		}
		return true;
	}

}
