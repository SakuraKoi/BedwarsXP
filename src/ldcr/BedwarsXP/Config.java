package ldcr.BedwarsXP;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import ldcr.BedwarsXP.Utils.ListUtils;

public class Config {
	private static int configVersion = 2;
	protected static YamlConfiguration yaml_config;
	protected static File file_config;

	protected static YamlConfiguration yaml_enabledGames;
	protected static File file_enabledGames;

	public static String xpMessage;
	public static boolean addResShop;

	public static double deathCost;
	public static double deathDrop;

	public static int maxXP;
	public static String maxXPMessage;

	public static boolean fullXPBedwars;
	public static HashMap<Material, Integer> resources = new HashMap<Material, Integer>();
	public static HashSet<String> resourceskey = new HashSet<String>();

	private static HashSet<String> enabledGameList = new HashSet<String>();

	public static void loadConfig() {
		ldcr.BedwarsXP.BedwarsXP.sendConsoleMessage("§6§l[BedwarsXP] &b开始加载配置文件");
		file_config = new File("plugins/BedwarsXP/config.yml");
		if (!file_config.exists()) {
			ldcr.BedwarsXP.BedwarsXP.sendConsoleMessage("§6§l[BedwarsXP] &b配置文件不存在,正在创建...");
			ldcr.BedwarsXP.BedwarsXP.plugin.saveResource("config.yml", true);
		}
		yaml_config = YamlConfiguration.loadConfiguration(file_config);

		if (yaml_config.getInt("ConfigVersion") < configVersion) {
			ldcr.BedwarsXP.BedwarsXP.sendConsoleMessage("§6§l[BedwarsXP] &4您的配置文件版本过老无法加载");
			file_config.renameTo(new File("plugins/BedwarsXP/config.bak.yml"));
			ldcr.BedwarsXP.BedwarsXP.sendConsoleMessage("§6§l[BedwarsXP] &c已备份您的配置文件为 [config.bak.yml] ,开始初始化新版本配置文件");
			ldcr.BedwarsXP.BedwarsXP.plugin.saveResource("config.yml", true);
			ldcr.BedwarsXP.BedwarsXP.sendConsoleMessage("§6§l[BedwarsXP] &a配置文件初始化完成,继续加载配置...");
			file_config = new File("plugins/BedwarsXP/config.yml");
			yaml_config = YamlConfiguration.loadConfiguration(file_config);
		}

		xpMessage = yaml_config.getString("Message").replaceAll("&", "§").replaceAll("§§", "&");
		addResShop = yaml_config.getBoolean("Add_Res_Shop");
		if (addResShop) {
			ldcr.BedwarsXP.BedwarsXP.sendConsoleMessage("§6§l[BedwarsXP] &a已启用经验兑换资源商店");
		}

		deathCost = yaml_config.getInt("DeathCostXP", 0) / 100.0;
		ldcr.BedwarsXP.BedwarsXP.sendConsoleMessage("§6§l[BedwarsXP] &a死亡扣除 " + (deathCost * 100) + "% 经验");

		deathDrop = yaml_config.getInt("DeathDropXP", 0) / 100.0;
		ldcr.BedwarsXP.BedwarsXP.sendConsoleMessage("§6§l[BedwarsXP] &a死亡掉落经验" + (deathDrop == 0 ? "已关闭" : ("占扣除经验 " + (deathDrop * 100) + "%")));

		maxXP = yaml_config.getInt("MaxXP");
		maxXPMessage = yaml_config.getString("MaxXPMessage").replaceAll("&", "§").replaceAll("§§", "&");
		ldcr.BedwarsXP.BedwarsXP.sendConsoleMessage("§6§l[BedwarsXP] &a最大经验限制已" + (maxXP == 0 ? " 关闭" : "设置为 " + maxXP));

		fullXPBedwars = yaml_config.getBoolean("Full_XP_Bedwars");
		if (fullXPBedwars) {
			ldcr.BedwarsXP.BedwarsXP.sendConsoleMessage("§6§l[BedwarsXP] &a完全经验起床模式已启动");
		}

		ldcr.BedwarsXP.BedwarsXP.sendConsoleMessage("§6§l[BedwarsXP] &a开始加载资源价值数据");
		final ConfigurationSection resourceSection = io.github.bedwarsrel.BedwarsRel.getInstance().getConfig().getConfigurationSection("resource");
		for (final String key : resourceSection.getKeys(false)) {
			@SuppressWarnings("unchecked")
			final List<Map<String, Object>> resourceList = (List<Map<String, Object>>) io.github.bedwarsrel.BedwarsRel.getInstance().getConfig().getList("resource." + key + ".item");
			for (final Map<String, Object> resource : resourceList) {
				final ItemStack itemStack = ItemStack.deserialize(resource);
				final Material mat = itemStack.getType();
				final int xp = yaml_config.getInt("XP." + key, 0);
				resources.put(mat, xp);
				resourceskey.add(key);
				BedwarsXP.sendConsoleMessage("§6§l[BedwarsXP] &a发现资源 [" + key + "] 物品:" + mat.toString() + " 价值" + xp);
			}
		}

		file_enabledGames = new File("plugins/BedwarsXP/enabledGames.yml");
		if (!file_enabledGames.exists()) {
			ldcr.BedwarsXP.BedwarsXP.sendConsoleMessage("§6§l[BedwarsXP] &c注意,在新版本中你需要手动使用/bwxp enable来启用游戏的经验起床模式");
			ldcr.BedwarsXP.BedwarsXP.plugin.saveResource("enabledGames.yml", true);
		}
		yaml_enabledGames = YamlConfiguration.loadConfiguration(file_enabledGames);
		enabledGameList.addAll(yaml_enabledGames.getStringList("enabledGame"));
	}

	public static String setGameEnableXP(final String bw, final boolean isEnabled) {
		if (isEnabled) {
			enabledGameList.add(bw);
		} else {
			enabledGameList.remove(bw);
		}
		yaml_enabledGames.set("enabledGame", ListUtils.hashSetToList(enabledGameList));
		try {
			yaml_enabledGames.save(file_enabledGames);
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
