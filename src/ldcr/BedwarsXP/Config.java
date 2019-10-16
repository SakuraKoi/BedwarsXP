package ldcr.BedwarsXP;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import ldcr.BedwarsXP.utils.ListUtils;
import ldcr.BedwarsXP.utils.YamlUtils;
import lombok.Getter;

public class Config {
	private static int configVersion = 2;
	protected static YamlConfiguration configYaml;

	protected static YamlConfiguration enabledGamesYaml;
	protected static File enabledGamesFile;

	@Getter private static YamlConfiguration languageYaml;

	public static String xpMessage;
	public static boolean addResShop;

	public static double deathCost;
	public static double deathDrop;

	public static int maxXP;
	public static String maxXPMessage;

	public static boolean fullXPBedwars;
	public static Map<Material, Integer> resources = new EnumMap<>(Material.class);
	public static Set<String> resourceskey = new HashSet<>();

	private static Set<String> enabledGameList = new HashSet<>();

	public static void loadConfig() {
		BedwarsXP.sendConsoleMessage("§6§lBedwarsXP §7>> §b正在加载语音文件... | Loading language configuration...");
		final File languageFile = new File("plugins/BedwarsXP/language.yml");
		if (!languageFile.exists()) {
			BedwarsXP.sendConsoleMessage("§6§lBedwarsXP §7>> §b语言文件不存在,正在创建...");
			BedwarsXP.sendConsoleMessage("§6§lBedwarsXP §7>> §bWanna english? Just overwrites language.yml with language-en.yml :)");
			BedwarsXP.getInstance().saveResource("language.yml", true);
			BedwarsXP.getInstance().saveResource("language-en.yml", true);
		}
		try {
			languageYaml = YamlUtils.loadYamlUTF8(languageFile);
		} catch (final IOException e) {
			languageYaml = new YamlConfiguration();
			BedwarsXP.sendConsoleMessage("§6§lBedwarsXP §7>> §c语言文件加载失败 | Failed to load language");
			e.printStackTrace();
		}
		BedwarsXP.sendConsoleMessage("§6§lBedwarsXP §7>> §b"+BedwarsXP.l18n("LOADING_CONFIGURATION"));
		File  configFile = new File("plugins/BedwarsXP/config.yml");
		if (!configFile.exists()) {
			BedwarsXP.sendConsoleMessage("§6§lBedwarsXP §7>> §b"+BedwarsXP.l18n("CONFIGURATION_FILE_NOT_EXISTS"));
			BedwarsXP.getInstance().saveResource("config.yml", true);
		}
		configYaml = YamlConfiguration.loadConfiguration(configFile);

		if (configYaml.getInt("ConfigVersion") < configVersion) {
			BedwarsXP.sendConsoleMessage("§6§lBedwarsXP §7>> §4"+BedwarsXP.l18n("OLD_VERSION_CONFIGURATION"));
			configFile.renameTo(new File("plugins/BedwarsXP/config.bak.yml"));
			BedwarsXP
			.sendConsoleMessage("§6§lBedwarsXP §7>> §c"+BedwarsXP.l18n("OLD_CONFIGURATION_BACKUPED"));
			BedwarsXP.getInstance().saveResource("config.yml", true);
			BedwarsXP.sendConsoleMessage("§6§lBedwarsXP §7>> §a"+BedwarsXP.l18n("NEW_CONFIGURATION_SAVED"));
			configFile = new File("plugins/BedwarsXP/config.yml");
			configYaml = YamlConfiguration.loadConfiguration(configFile);
		}

		xpMessage = configYaml.getString("Message").replaceAll("&", "§").replaceAll("§§", "§");
		addResShop = configYaml.getBoolean("Add_Res_Shop");
		if (addResShop) {
			BedwarsXP.sendConsoleMessage("§6§lBedwarsXP §7>> §a"+BedwarsXP.l18n("RESOURCE_SHOP_ENABLED"));
		}

		deathCost = configYaml.getInt("DeathCostXP", 0) / 100.0;
		BedwarsXP.sendConsoleMessage("§6§lBedwarsXP §7>> §a"+BedwarsXP.l18n("DEATH_COST_XP_PERCENT", "%percent%", String.valueOf(deathCost * 100)));

		deathDrop = configYaml.getInt("DeathDropXP", 0) / 100.0;
		BedwarsXP.sendConsoleMessage(
				"§6§lBedwarsXP §7>> §a" + (deathDrop == 0 ? BedwarsXP.l18n("DEATH_DROP_XP_DISABLED") : BedwarsXP.l18n("DEATH_DROP_XP_PERCEMT", "%percent%", String.valueOf(deathDrop * 100))));

		maxXP = configYaml.getInt("MaxXP");
		maxXPMessage = configYaml.getString("MaxXPMessage").replaceAll("&", "§").replaceAll("§§", "§");
		BedwarsXP.sendConsoleMessage("§6§lBedwarsXP §7>> §a" + (maxXP == 0 ? BedwarsXP.l18n("MAX_XP_LIMIT_DISABLED") : BedwarsXP.l18n("MAX_XP_LIMIT_ENABLED", "%value%", String.valueOf(maxXP))));

		fullXPBedwars = configYaml.getBoolean("Full_XP_Bedwars");
		if (fullXPBedwars) {
			BedwarsXP.sendConsoleMessage("§6§lBedwarsXP §7>> §a"+BedwarsXP.l18n("ALL_TRADES_USE_XP_ENABLED"));
		}

		BedwarsXP.sendConsoleMessage("§6§lBedwarsXP §7>> §a"+BedwarsXP.l18n("LOADING_RESOURCES_VALUE"));
		final ConfigurationSection resourceSection = io.github.bedwarsrel.BedwarsRel.getInstance().getConfig()
				.getConfigurationSection("resource");
		for (final String key : resourceSection.getKeys(false)) {
			@SuppressWarnings("unchecked")
			final List<Map<String, Object>> resourceList = (List<Map<String, Object>>) io.github.bedwarsrel.BedwarsRel
			.getInstance().getConfig().getList("resource." + key + ".item");
			for (final Map<String, Object> resource : resourceList) {
				final ItemStack itemStack = ItemStack.deserialize(resource);
				final Material mat = itemStack.getType();
				final int xp = configYaml.getInt("XP." + key, 0);
				resources.put(mat, xp);
				resourceskey.add(key);
				BedwarsXP.sendConsoleMessage(
						"§6§lBedwarsXP §7>> §a"+BedwarsXP.l18n("FOUNDED_RESOURCE", "%resource%", key, "%material%", mat.toString(), "%value%", String.valueOf(xp)));
			}
		}

		enabledGamesFile = new File("plugins/BedwarsXP/enabledGames.yml");
		if (!enabledGamesFile.exists()) {
			BedwarsXP.sendConsoleMessage("§6§lBedwarsXP §7>> §c"+BedwarsXP.l18n("WARN_YOU_NEEDS_ENABLE_BEDWARSXP_MANULLY"));
			BedwarsXP.getInstance().saveResource("enabledGames.yml", true);
		}
		enabledGamesYaml = YamlConfiguration.loadConfiguration(enabledGamesFile);
		enabledGameList.addAll(enabledGamesYaml.getStringList("enabledGame"));
	}

	public static String setGameEnableXP(final String bw, final boolean isEnabled) {
		if (isEnabled) {
			enabledGameList.add(bw);
		} else {
			enabledGameList.remove(bw);
		}
		enabledGamesYaml.set("enabledGame", ListUtils.setToList(enabledGameList));
		try {
			enabledGamesYaml.save(enabledGamesFile);
		} catch (final IOException e) {
			e.printStackTrace();
			return e.getLocalizedMessage();
		}
		return "";
	}

	public static boolean isGameEnabledXP(final String bw) {
		return enabledGameList.contains(bw);
	}
}
