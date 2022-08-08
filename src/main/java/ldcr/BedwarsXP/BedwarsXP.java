package ldcr.BedwarsXP;

import ldcr.BedwarsXP.command.BedwarsXPCommandListener;
import ldcr.BedwarsXP.command.EditXPCommandListener;
import ldcr.BedwarsXP.utils.ActionBarUtils;
import ldcr.BedwarsXP.utils.ReflectionUtils;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import sakura.kooi.utils.GithubUpdateChecker.UpdateChecker;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BedwarsXP extends JavaPlugin {
	@Getter
	private static BedwarsXP instance;
	@Getter
	private static CommandSender consoleSender;

	@Getter private static final Map<String, String> l18nCache = new HashMap<>();

	@Getter
	private static String updateUrl = null;

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
		} catch (Exception e) {
			sendConsoleMessage("§c§lERROR: §c-----------------------------------");
			e.printStackTrace();
			sendConsoleMessage("§c§lERROR: §c-----------------------------------");
			sendConsoleMessage("§c§lERROR: §c"+l18n("ERROR_OCCURRED_WHILE_LOADING"));
			sendConsoleMessage("§c§lERROR: §e   ↓↓ << "+l18n("REPORT_ISSUE_HERE")+" >> ↓↓  ");
			sendConsoleMessage("§c§lERROR: §c https://github.com/SakuraKoi/BedwarsXP/issues/1");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		sendConsoleMessage("§b"+l18n("SUCCESSFULLY_LOADED")+" By.SakuraKooi");
		sendConsoleMessage("§e   ↓↓ << "+l18n("REPORT_ISSUE_AND_SUGGESTION_HERE")+" >> ↓↓  ");
		sendConsoleMessage("§c https://github.com/SakuraKoi/BedwarsXP/issues/1");
		// Update checker and metrics
		if (!Config.disableUpdateChecker) {
			Bukkit.getScheduler().runTaskLater(this, () -> {
				try {
					new UpdateChecker("SakuraKoi", "BedwarsXP", "v"+getDescription().getVersion(), link -> {
						updateUrl = link;
						sendConsoleMessage("§b"+l18n("HAS_UPDATE", "%link%", link));
					}).check();
				} catch (IOException ignored) { }
			}, 100);
		}
		try {
			new Metrics(this, 3999);
		} catch (Exception ignored) {}
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

	public static String l18n(String key, String... replacement) {
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

	public static void sendConsoleMessage(String str) {
		consoleSender.sendMessage("§6§lBedwarsXP §7>> " + str);
	}
}
