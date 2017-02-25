package ldcr.BedwarsXP;

import ldcr.BedwarsXP.Utils.BWGameUtils;
import ldcr.BedwarsXP.Utils.SendMessageUtils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandListener implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2,
			String[] args) {
		if (args.length == 0) {
			SendMessageUtils.sendMessage(sender,
					"§6§l[BedwarsXP] §b经验起床插件 §lBy.Ldcr",
					"§6§l[BedwarsXP] §a/bwxp reload 重载插件配置");
			return true;
		}
		if (!sender.hasPermission("bedwarsxp.admin")) {
			sender.sendMessage("§6§l[BedwarsXP] §c你没有权限执行此命令");
			return true;
		}
		switch (args[0].toLowerCase()) {
		default: {
			sender.sendMessage("§6§l[BedwarsXP] §b/bwxp 重载经验起床插件配置");
			break;
		}
		case "reload": {
			Config.loadConfig();
			sender.sendMessage("§6§l[BedwarsXP] §b成功重载配置文件~");
			if (BWGameUtils.isAnyBedwarsRunning()) {
				SendMessageUtils.sendMessage(sender,
						"§6§l[BedwarsXP] §b当前有游戏正在运行",
						"§6§l[BedwarsXP] §b开始更新运行中的游戏的经验商店");
				BWGameUtils.replaceAllShop(sender);
			}
		}
		}
		return true;
	}

}
