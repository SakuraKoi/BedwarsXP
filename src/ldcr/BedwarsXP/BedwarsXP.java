package ldcr.BedwarsXP;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import ldcr.BedwarsXP.Utils.ActionBarUtils;
import ldcr.BedwarsXP.Utils.ReflectionUtils;
import ldcr.BedwarsXP.command.BedwarsXPCommandListener;
import ldcr.BedwarsXP.command.EditXPCommandListener;

public class BedwarsXP extends JavaPlugin {

	public static Plugin plugin;
	public static CommandSender log;

	@Override
	public void onEnable() {
		plugin = this;
		log = Bukkit.getConsoleSender();
		sendConsoleMessage("§6§l[BedwarsXP] &b正在加载BedwarsXP经验起床插件 Version." + getDescription().getVersion());
		try {
			BWVersionDelect();
		} catch (final Exception e) {
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
			sendConsoleMessage("&c&l[ERROR] §6§l[BedwarsXP] &c-----------------------------------");
			e.printStackTrace();
			sendConsoleMessage("&c&l[ERROR] §6§l[BedwarsXP] &c-----------------------------------");
			sendConsoleMessage("&c&l[ERROR] §6§l[BedwarsXP] &cBedwarsXP加载出错. ");
			sendConsoleMessage("&c&l[ERROR] §6§l[BedwarsXP] &e   ↓↓ << 请前往此处反馈 >> ↓↓  ");
			sendConsoleMessage("&c&l[ERROR] §6§l[BedwarsXP] &c https://github.com/Ldcr993519867/BedwarsXP/issues/1");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		sendConsoleMessage("§6§l[BedwarsXP] &b成功加载BedwarsXP经验起床插件 By.Ldcr");
		sendConsoleMessage("§6§l[BedwarsXP] &e   ↓↓ << BUG反馈 | 提交建议 >> ↓↓  ");
		sendConsoleMessage("§6§l[BedwarsXP] &c https://github.com/Ldcr993519867/BedwarsXP/issues/1");
	}

	private void BWVersionDelect() throws Exception {
		sendConsoleMessage("§6§l[BedwarsXP] &a正在寻找BedwarsRel插件...");
		if (ReflectionUtils.isClassFound("io.github.yannici.bedwars.Main")) {
			sendConsoleMessage("§6§l[BedwarsXP] &c抱歉, BedwarsXP不再支持旧版BedwarsRel!");
			sendConsoleMessage("§6§l[BedwarsXP] &c请更新你的BedwarsRel至1.3.6以上版本.");
			throw new Exception();
		} else if (ReflectionUtils.isClassFound("io.github.bedwarsrel.BedwarsRel.Main")) {
			sendConsoleMessage("§6§l[BedwarsXP] &c抱歉, BedwarsXP不再支持旧版BedwarsRel!");
			sendConsoleMessage("§6§l[BedwarsXP] &c请更新你的BedwarsRel至1.3.6以上版本.");
			throw new Exception();
		} else if (ReflectionUtils.isClassFound("io.github.bedwarsrel.BedwarsRel")) {
			sendConsoleMessage("§6§l[BedwarsXP] &a已发现受支持的BedwarsRel插件!");
			return;
		} else {
			sendConsoleMessage("&c&l[ERROR] §6§l[BedwarsXP] &c没有找到支持的BedwarsRel! 你可能没有安装或使用了不受支持的版本!");
			throw new Exception();
		}
	}

	public static void sendConsoleMessage(final String str) {
		log.sendMessage(str.replaceAll("&", "§"));
	}

}
