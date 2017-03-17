package ldcr.BedwarsXP;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;

import ldcr.BedwarsXP.Utils.ListUtils;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config {
	private static int ConfigVersion = 1;
	static YamlConfiguration config;
	static File file;

	static YamlConfiguration enable;
	static File e_file;

	public static String Message;
	public static boolean Add_Res_Shop;

	public static double Death;
	public static boolean isDirect;

	public static boolean Full_XP_Bedwars;
	public static HashMap<Material, Integer> res = new HashMap<Material, Integer>();

	private static HashSet<String> enabled = new HashSet<String>();

	public static void loadConfig() {
		ldcr.BedwarsXP.Main.sendConsoleMessage("§6§l[BedwarsXP] &b开始加载配置文件");
		file = new File("plugins/BedwarsXP/config.yml");
		if (!file.exists()) {
			ldcr.BedwarsXP.Main
					.sendConsoleMessage("§6§l[BedwarsXP] &b配置文件不存在,正在创建...");
			ldcr.BedwarsXP.Main.plugin.saveResource("config.yml", true);
		}
		config = YamlConfiguration.loadConfiguration(file);
	
		if (config.getInt("ConfigVersion") < ConfigVersion) {
			ldcr.BedwarsXP.Main
					.sendConsoleMessage("§6§l[BedwarsXP] &4您的配置文件版本过老无法加载");
			file.renameTo(new File("plugins/BedwarsXP/config.yml.bak"));
			ldcr.BedwarsXP.Main
					.sendConsoleMessage("§6§l[BedwarsXP] &c已备份您的配置文件为 [config.yml.bak] ,开始初始化新版本配置文件");
			ldcr.BedwarsXP.Main.plugin.saveResource("config.yml", true);
			ldcr.BedwarsXP.Main
					.sendConsoleMessage("§6§l[BedwarsXP] &a配置文件初始化完成,继续加载配置...");
			file = new File("plugins/BedwarsXP/config.yml");
			config = YamlConfiguration.loadConfiguration(file);
		}

		Message = config.getString("Message").replaceAll("&", "§")
				.replaceAll("§§", "&");
		Add_Res_Shop = config.getBoolean("Add_Res_Shop");
		if (Add_Res_Shop) {
			ldcr.BedwarsXP.Main
					.sendConsoleMessage("§6§l[BedwarsXP] &a已启用经验兑换资源商店");
		}
		String temp = config.getString("Death");
		if (temp.endsWith("%")) {
			NumberFormat nf = NumberFormat.getPercentInstance();
			try {
				Death = nf.parse(temp).doubleValue();
				isDirect = false;
			} catch (ParseException e) {
				ldcr.BedwarsXP.Main
						.sendConsoleMessage("§6§l[BedwarsXP] &c你的死亡经验扣除配置有错误,此功能关闭");
				Death = 0;
				isDirect = true;
				e.printStackTrace();
			}
		} else {
			Death = Integer.valueOf(temp);
			isDirect = true;
		}
		if (isDirect) {
			ldcr.BedwarsXP.Main
					.sendConsoleMessage("§6§l[BedwarsXP] &a死亡直接扣除经验: " + Death);
		} else {
			ldcr.BedwarsXP.Main
					.sendConsoleMessage("§6§l[BedwarsXP] &a死亡百分比扣除经验: " + Death
							* 100 + "%");
		}
		Full_XP_Bedwars = config.getBoolean("Full_XP_Bedwars");
		if (Full_XP_Bedwars) {
			ldcr.BedwarsXP.Main
					.sendConsoleMessage("§6§l[BedwarsXP] &a完全经验起床模式已启动");
		}
		ldcr.BedwarsXP.Main.sendConsoleMessage("§6§l[BedwarsXP] &a开始加载资源价值数据");
		if (Main.isOldBedwarsPlugin) {
			for (final String key : io.github.yannici.bedwars.Main
					.getInstance().getConfig()
					.getConfigurationSection("ressource").getKeys(true)) {
				final ConfigurationSection keySection = io.github.yannici.bedwars.Main
						.getInstance().getConfig()
						.getConfigurationSection("ressource." + key);
				if (keySection == null) {
					continue;
				}
				if (!keySection.contains("item")) {
					continue;
				}
				final Material mat = io.github.yannici.bedwars.Utils
						.parseMaterial(keySection.getString("item"));
				int xp = config.getInt("XP." + key, 0);
				res.put(mat, xp);
				ldcr.BedwarsXP.Main
						.sendConsoleMessage("§6§l[BedwarsXP] &a发现资源 [" + key
								+ "] 物品:" + mat.toString() + " 价值" + xp);
			}
		} else {
			for (final String key : io.github.bedwarsrel.BedwarsRel.Main
					.getInstance().getConfig()
					.getConfigurationSection("ressource").getKeys(true)) {
				final ConfigurationSection keySection = io.github.bedwarsrel.BedwarsRel.Main
						.getInstance().getConfig()
						.getConfigurationSection("ressource." + key);
				if (keySection == null) {
					continue;
				}
				if (!keySection.contains("item")) {
					continue;
				}
				final Material mat = io.github.bedwarsrel.BedwarsRel.Utils.Utils
						.parseMaterial(keySection.getString("item"));
				int xp = config.getInt("XP." + key, 0);
				res.put(mat, xp);
				ldcr.BedwarsXP.Main
						.sendConsoleMessage("§6§l[BedwarsXP] &a发现资源 [" + key
								+ "] 物品:" + mat.toString() + " 价值" + xp);
			}
		}
		e_file = new File("plugins/BedwarsXP/enabledGames.yml");
		if (!e_file.exists()) {
			ldcr.BedwarsXP.Main
					.sendConsoleMessage("§6§l[BedwarsXP] &c注意,在新版本中你需要手动使用/bwxp enable来启用游戏的经验起床模式");
			ldcr.BedwarsXP.Main.plugin.saveResource("enabledGames.yml", true);
		}
		enable = YamlConfiguration.loadConfiguration(e_file);
		enabled.addAll(enable.getStringList("enabledGame"));
	}

	public static String setGameEnableXP(String bw, boolean isEnabled) {
		if (isEnabled) {
			enabled.add(bw);
		} else {
			enabled.remove(bw);
		}
		enable.set("enabledGame", ListUtils.hashSetToList(enabled));
		try {
			enable.save(e_file);
		} catch (IOException e) {
			e.printStackTrace();
			return e.getLocalizedMessage();
		}
		return "";
	}

	public static boolean isGameEnabledXP(String bw) {
		return enabled.contains(bw);
	}
}
