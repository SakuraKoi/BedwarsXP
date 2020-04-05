package ldcr.BedwarsXP;

import ldcr.BedwarsXP.command.BedwarsXPCommandListener;
import ldcr.BedwarsXP.command.EditXPCommandListener;
import ldcr.BedwarsXP.utils.ActionBarUtils;
import ldcr.BedwarsXP.utils.MetricsLite;
import ldcr.BedwarsXP.utils.ReflectionUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class BedwarsXP extends JavaPlugin {
	@Getter
	private static BedwarsXP instance;
	@Getter
	private static CommandSender consoleSender;

	@Override
	public void onEnable() {
		instance = this;
		consoleSender = Bukkit.getConsoleSender();

		try {
			sendConsoleMessage("§bLoading BedwarsXP... Version." + getDescription().getVersion());
			Config.loadConfig();
			if (!detectBedwarsRelVersion()) {
				Bukkit.getPluginManager().disablePlugin(this);
				return;
			}
			ActionBarUtils.load();
			Bukkit.getPluginManager().registerEvents(new EventListeners(), this);
			getCommand("bedwarsxp").setExecutor(new BedwarsXPCommandListener());
			getCommand("bedwarsxpedit").setExecutor(new EditXPCommandListener());
		} catch (final Exception e) {
			sendConsoleMessage("§c§lERROR: §c-----------------------------------");
			e.printStackTrace();
			sendConsoleMessage("§c§lERROR: §c-----------------------------------");
			sendConsoleMessage("§c§lERROR: §c"+l18n("ERROR_OCCURRED_WHILE_LOADING"));
			sendConsoleMessage("§c§lERROR: §e   ↓↓ << "+l18n("REPORT_ISSUE_HERE")+" >> ↓↓  ");
			sendConsoleMessage("§c§lERROR: §c https://github.com/Ldcr993519867/BedwarsXP/issues/1");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		sendConsoleMessage("§b"+l18n("SUCCESSFULLY_LOADED")+" By.SakuraKooi");
		sendConsoleMessage("§e   ↓↓ << "+l18n("REPORT_ISSUE_AND_SUGGESTION_HERE")+" >> ↓↓  ");
		sendConsoleMessage("§c https://github.com/Ldcr993519867/BedwarsXP/issues/1");
		try {
			new MetricsLite(this);
		} catch (final Exception e) {}
	}

	private boolean detectBedwarsRelVersion() {
		sendConsoleMessage("§a"+l18n("FINDING_BEDWARSREL"));
		if (ReflectionUtils.isClassFound("io.github.yannici.bedwars.Main") || ReflectionUtils.isClassFound("io.github.bedwarsrel.BedwarsRel.Main")) {
			sendConsoleMessage("§c"+l18n("BEDWARSREL_NOT_SUPPORTED"));
			sendConsoleMessage("§c"+l18n("PLEASE_UPDATE_BEDWARSREL"));
			return false;
		} else if (ReflectionUtils.isClassFound("io.github.bedwarsrel.BedwarsRel")) {
			sendConsoleMessage("§a"+l18n("BEDWARSREL_SUPPORTED"));
			return true;
		} else {
			sendConsoleMessage("§c§lERROR: §c"+l18n("BEDWARSREL_NOT_FOUND"));
			return false;
		}
	}
	@Getter private static final Map<String, String> l18nCache = new HashMap<>();
	public static String l18n(final String key, final String... replacement) {
		String message;
		if (l18nCache.containsKey(key)) {
			message = l18nCache.get(key);
		} else {
			message = Config.getLanguageYaml().getString(key, "LANG_NOT_FOUND_"+key);
			l18nCache.put(key, message);
		}
		for (int i = 0, length = replacement.length/2; i < length; i++) {
			message = message.replace(replacement[i*2], replacement[i*2+1]);
		}
		return message;
	}
	public static void sendConsoleMessage(final String str) {
		consoleSender.sendMessage("§6§lBedwarsXP §7>> " + str);
	}

}
