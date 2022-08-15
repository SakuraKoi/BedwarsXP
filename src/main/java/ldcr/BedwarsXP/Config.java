package ldcr.BedwarsXP;

import ldcr.BedwarsXP.utils.YamlUtils;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Config {
    private static final int CONFIG_VERSION = 2;

    private static YamlConfiguration enabledGamesYaml;
    private static File enabledGamesFile;

    @Getter
    private static YamlConfiguration languageYaml;

    public static boolean disableUpdateChecker;

    public static String xpMessage;
    public static boolean addResShop;

    public static double deathCost;
    public static double deathDrop;
    public static boolean dontDropExpBottle;
    public static int maxXP;
    public static String maxXPMessage;

    public static boolean fullXPBedwars;
    public static final Map<Material, Integer> resources = new EnumMap<>(Material.class);
    public static final Set<String> resourceskey = new HashSet<>();

    private static final Set<String> enabledGameList = new HashSet<>();

    public static void loadConfig() {
        BedwarsXP.sendConsoleMessage("§b正在加载语言文件... | Loading language configuration...");
        File languageFile = new File("plugins/BedwarsXP/language.yml");
        if (!languageFile.exists()) {
            BedwarsXP.sendConsoleMessage("§b语言文件不存在,正在创建...");
            BedwarsXP.sendConsoleMessage("§bWanna english? Just overwrites language.yml with language-en.yml :)");
            BedwarsXP.getInstance().saveResource("language.yml", true);
            BedwarsXP.getInstance().saveResource("language-en.yml", true);
        }
        try {
            languageYaml = YamlUtils.loadYamlUTF8(languageFile);
        } catch (IOException e) {
            languageYaml = new YamlConfiguration();
            BedwarsXP.sendConsoleMessage("§c语言文件加载失败 | Failed to load language");
            e.printStackTrace();
        }
        BedwarsXP.sendConsoleMessage("§b" + BedwarsXP.l18n("LOADING_CONFIGURATION"));
        File configFile = new File("plugins/BedwarsXP/config.yml");
        if (!configFile.exists()) {
            BedwarsXP.sendConsoleMessage("§b" + BedwarsXP.l18n("CONFIGURATION_FILE_NOT_EXISTS"));
            BedwarsXP.getInstance().saveResource("config.yml", true);
        }
        YamlConfiguration configYaml = YamlConfiguration.loadConfiguration(configFile);

        if (configYaml.getInt("ConfigVersion") < CONFIG_VERSION) {
            BedwarsXP.sendConsoleMessage("§4" + BedwarsXP.l18n("OLD_VERSION_CONFIGURATION"));
            configFile.renameTo(new File("plugins/BedwarsXP/config.bak.yml"));
            BedwarsXP.sendConsoleMessage("§c" + BedwarsXP.l18n("OLD_CONFIGURATION_BACKUPED"));
            BedwarsXP.getInstance().saveResource("config.yml", true);
            BedwarsXP.sendConsoleMessage("§a" + BedwarsXP.l18n("NEW_CONFIGURATION_SAVED"));
            configFile = new File("plugins/BedwarsXP/config.yml");
            configYaml = YamlConfiguration.loadConfiguration(configFile);
        }

        disableUpdateChecker = configYaml.getBoolean("Disable_UpdateChecker", true);

        xpMessage = configYaml.getString("Message").replaceAll("&", "§").replaceAll("§§", "§");
        addResShop = configYaml.getBoolean("Add_Res_Shop");
        if (addResShop) {
            BedwarsXP.sendConsoleMessage("§a" + BedwarsXP.l18n("RESOURCE_SHOP_ENABLED"));
        }

        deathCost = configYaml.getInt("DeathCostXP", 0) / 100.0;
        BedwarsXP.sendConsoleMessage("§a" + BedwarsXP.l18n("DEATH_COST_XP_PERCENT", "%percent%", String.valueOf(deathCost * 100)));

        deathDrop = configYaml.getInt("DeathDropXP", 0) / 100.0;
        BedwarsXP.sendConsoleMessage(
                "§6§lBedwarsXP §7>> §a" + (deathDrop == 0 ? BedwarsXP.l18n("DEATH_DROP_XP_DISABLED") : BedwarsXP.l18n("DEATH_DROP_XP_PERCEMT", "%percent%", String.valueOf(deathDrop * 100))));
        dontDropExpBottle = configYaml.getBoolean("DontDropExpBottle", false);
        if (dontDropExpBottle)
            BedwarsXP.sendConsoleMessage("§a" + BedwarsXP.l18n("DEATH_DROP_EXP_BOTTLE_DISABLED"));

        maxXP = configYaml.getInt("MaxXP");
        maxXPMessage = configYaml.getString("MaxXPMessage").replaceAll("&", "§").replaceAll("§§", "§");
        BedwarsXP.sendConsoleMessage("§a" + (maxXP == 0 ? BedwarsXP.l18n("MAX_XP_LIMIT_DISABLED") : BedwarsXP.l18n("MAX_XP_LIMIT_ENABLED", "%value%", String.valueOf(maxXP))));

        fullXPBedwars = configYaml.getBoolean("Full_XP_Bedwars");
        if (fullXPBedwars) {
            BedwarsXP.sendConsoleMessage("§a" + BedwarsXP.l18n("ALL_TRADES_USE_XP_ENABLED"));
        }

        BedwarsXP.sendConsoleMessage("§a" + BedwarsXP.l18n("LOADING_RESOURCES_VALUE"));
        ConfigurationSection resourceSection = io.github.bedwarsrel.BedwarsRel.getInstance().getConfig()
                .getConfigurationSection("resource");
        for (String key : resourceSection.getKeys(false)) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> resourceList = (List<Map<String, Object>>) io.github.bedwarsrel.BedwarsRel
                    .getInstance().getConfig().getList("resource." + key + ".item");
            for (Map<String, Object> resource : resourceList) {
                ItemStack itemStack = ItemStack.deserialize(resource);
                Material mat = itemStack.getType();
                int xp = configYaml.getInt("XP." + key, 0);
                resources.put(mat, xp);
                resourceskey.add(key);
                BedwarsXP.sendConsoleMessage("§a" + BedwarsXP.l18n("FOUNDED_RESOURCE",
                        "%resource%", key,
                        "%material%", mat.toString(),
                        "%value%", String.valueOf(xp)));
            }
        }

        enabledGamesFile = new File("plugins/BedwarsXP/enabledGames.yml");
        if (!enabledGamesFile.exists()) {
            BedwarsXP.sendConsoleMessage("§c" + BedwarsXP.l18n("WARN_YOU_NEEDS_ENABLE_BEDWARSXP_MANUALLY"));
            BedwarsXP.getInstance().saveResource("enabledGames.yml", true);
        }
        enabledGamesYaml = YamlConfiguration.loadConfiguration(enabledGamesFile);
        enabledGameList.addAll(enabledGamesYaml.getStringList("enabledGame"));
    }

    public static String setGameEnableXP(String bw, boolean isEnabled) {
        if (isEnabled) {
            enabledGameList.add(bw);
        } else {
            enabledGameList.remove(bw);
        }
        enabledGamesYaml.set("enabledGame", new ArrayList<>(enabledGameList));
        try {
            enabledGamesYaml.save(enabledGamesFile);
        } catch (IOException e) {
            e.printStackTrace();
            return e.getLocalizedMessage();
        }
        return "";
    }

    public static boolean isGameEnabledXP(String bw) {
        return enabledGameList.contains(bw);
    }
}
