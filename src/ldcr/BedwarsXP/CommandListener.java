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
					"§6§l[BedwarsXP] §b/bwxp reload 重载插件配置",
					"§6§l[BedwarsXP] §b/bwxp updateshop 重载商店");
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
		case "updateshop": {
			SendMessageUtils.sendMessage(sender, "§6§l[BedwarsXP] §b开始更新经验商店",
					"§6§l[BedwarsXP] §c**** W.I.P 这是一项测试功能 ****");
			BWGameUtils.replaceAllShop(sender);
			break;
		}
		case "reload": {
			Config.loadConfig();
			sender.sendMessage("§6§l[BedwarsXP] §b成功重载配置文件~");
			if (BWGameUtils.isAnyBedwarsRunning()) {
				SendMessageUtils
						.sendMessage(
								sender,
								"§6§l[BedwarsXP] §c当前有游戏正在运行",
								"§6§l[BedwarsXP] §c这会造成商店中的价格保持旧配置不变",
								"§6§l[BedwarsXP] §c请执行/bwxp updateshop更新商店§4§L(W.I.P - 测试功能)",
								"§6§l[BedwarsXP] §c如果确定该功能稳定,此过程将加入重载步骤",
								"§6§l[BedwarsXP] §c目前建议您直接重新开始所有游戏以保证安全性");
			}
		}
		}
		return true;
	}

}
