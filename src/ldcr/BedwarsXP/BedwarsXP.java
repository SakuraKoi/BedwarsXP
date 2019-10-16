package ldcr.BedwarsXP;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import ldcr.BedwarsXP.command.BedwarsXPCommandListener;
import ldcr.BedwarsXP.command.EditXPCommandListener;
import ldcr.BedwarsXP.utils.ActionBarUtils;
import ldcr.BedwarsXP.utils.ReflectionUtils;
import lombok.Getter;

public class BedwarsXP extends JavaPlugin {
	@Getter
	private static BedwarsXP instance;
	@Getter
	private static CommandSender consoleSender;

	@Override
	public void onEnable() {
		instance = this;
		consoleSender = Bukkit.getConsoleSender();
		sendConsoleMessage("§b正在加载BedwarsXP经验起床插件 Version." + getDescription().getVersion());
		if (!detectBedwarsRelVersion()) {
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		try {
			Config.loadConfig();
			ActionBarUtils.load();
			Bukkit.getPluginManager().registerEvents(new EventListeners(), this);
			getCommand("bedwarsxp").setExecutor(new BedwarsXPCommandListener());
			getCommand("bedwarsxpedit").setExecutor(new EditXPCommandListener());
		} catch (final Exception e) {
			sendConsoleMessage("§c§lERROR: §c-----------------------------------");
			e.printStackTrace();
			sendConsoleMessage("§c§lERROR: §c-----------------------------------");
			sendConsoleMessage("§c§lERROR: §cBedwarsXP加载出错. ");
			sendConsoleMessage("§c§lERROR: §e   ↓↓ << 请前往此处反馈 >> ↓↓  ");
			sendConsoleMessage("§c§lERROR: §c https://github.com/Ldcr993519867/BedwarsXP/issues/1");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		sendConsoleMessage("§b成功加载BedwarsXP经验起床插件 By.Ldcr");
		sendConsoleMessage("§e   ↓↓ << BUG反馈 | 提交建议 >> ↓↓  ");
		sendConsoleMessage("§c https://github.com/Ldcr993519867/BedwarsXP/issues/1");
	}

	private boolean detectBedwarsRelVersion() {
		sendConsoleMessage("§a正在寻找BedwarsRel插件...");
		if (ReflectionUtils.isClassFound("io.github.yannici.bedwars.Main")) {
			sendConsoleMessage("§c抱歉, BedwarsXP不再支持旧版BedwarsRel!");
			sendConsoleMessage("§c请更新你的BedwarsRel至1.3.6以上版本.");
			return false;
		} else if (ReflectionUtils.isClassFound("io.github.bedwarsrel.BedwarsRel.Main")) {
			sendConsoleMessage("§c抱歉, BedwarsXP不再支持旧版BedwarsRel!");
			sendConsoleMessage("§c请更新你的BedwarsRel至1.3.6以上版本.");
			return false;
		} else if (ReflectionUtils.isClassFound("io.github.bedwarsrel.BedwarsRel")) {
			sendConsoleMessage("§a已发现受支持的BedwarsRel插件!");
			return true;
		} else {
			sendConsoleMessage("§c§lERROR: §c没有找到支持的BedwarsRel! 你可能没有安装或使用了不受支持的版本!");
			return false;
		}
	}

	public static void sendConsoleMessage(final String str) {
		consoleSender.sendMessage("§6§lBedwarsXP §7>> " + str);
	}

}
