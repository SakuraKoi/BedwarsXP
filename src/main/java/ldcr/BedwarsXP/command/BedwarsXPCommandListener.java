package ldcr.BedwarsXP.command;

import ldcr.BedwarsXP.BedwarsXP;
import ldcr.BedwarsXP.Config;
import ldcr.BedwarsXP.utils.BedwarsGameUtils;
import ldcr.BedwarsXP.utils.SendMessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BedwarsXPCommandListener implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
        if (args.length == 0) {
            SendMessageUtils.sendMessage(sender,
                    "§6§lBedwarsXP §7>> §bBedwarsXP v." + BedwarsXP.getInstance().getDescription().getVersion() + " §lBy.SakuraKooi",
                    "§6§lBedwarsXP §7>> §a" + BedwarsXP.l18n("HELP_MAIN_RELOAD"),
                    "§6§lBedwarsXP §7>> §a" + BedwarsXP.l18n("HELP_MAIN_ENABLE"),
                    "§6§lBedwarsXP §7>> §a" + BedwarsXP.l18n("HELP_MAIN_DISABLE"));
            return true;
        }
        if (!sender.hasPermission("bedwarsxp.admin")) {
            sender.sendMessage("§6§lBedwarsXP §7>> §c" + BedwarsXP.l18n("YOU_DONT_HAVE_PERMISSION_TO_EXECUTE_THIS_COMMAND"));
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "reload": {
                Config.loadConfig();
                BedwarsXP.getL18nCache().clear();
                sender.sendMessage("§6§lBedwarsXP §7>> §b" + BedwarsXP.l18n("SUCCESSFULLY_RELOADED"));
                if (BedwarsGameUtils.isAnyBedwarsRunning()) {
                    SendMessageUtils.sendMessage(sender,
                            "§6§lBedwarsXP §7>> §b" + BedwarsXP.l18n("RELOAD_GAME_RUNNING"),
                            "§6§lBedwarsXP §7>> §b" + BedwarsXP.l18n("UPDATE_RUNNING_GAME"));
                    BedwarsGameUtils.replaceAllShop(sender);
                }
                return true;
            }
            case "enable": {
                if (args.length != 2) {
                    sender.sendMessage("§6§lBedwarsXP §7>> §a" + BedwarsXP.l18n("HELP_MAIN_ENABLE"));
                    return true;
                }
                if (!BedwarsGameUtils.isGameExists(args[1])) {
                    sender.sendMessage("§6§lBedwarsXP §7>> §c" + BedwarsXP.l18n("ERROR_GAME_NOT_FOUND", "%game%", args[1]));
                    return true;
                }
                String result = Config.setGameEnableXP(args[1], true);
                if (result.equals("")) {
                    sender.sendMessage("§6§lBedwarsXP §7>> §a" + BedwarsXP.l18n("GAME_XP_ENABLED", "%game%", args[1]));
                    if (BedwarsGameUtils.isGameRunning(args[1])) {
                        sender.sendMessage("§6§lBedwarsXP §7>> §4" + BedwarsXP.l18n("GAME_IS_RUNNING_RESTART_REQUIRED", "%game%", args[1]));
                    }
                } else {
                    SendMessageUtils.sendMessage(sender, "§6§lBedwarsXP §7>> §c" + BedwarsXP.l18n("ERROR_OCCURRED"), "§6§lBedwarsXP §7>> §c" + result);
                }
                return true;
            }
            case "disable": {
                if (args.length != 2) {
                    sender.sendMessage("§6§lBedwarsXP §7>> §a" + BedwarsXP.l18n("HELP_MAIN_DISABLE"));
                    return true;
                }
                if (!BedwarsGameUtils.isGameExists(args[1])) {
                    sender.sendMessage("§6§lBedwarsXP §7>> §c" + BedwarsXP.l18n("ERROR_GAME_NOT_FOUND", "%game%", args[1]));
                    return true;
                }
                String result = Config.setGameEnableXP(args[1], false);
                if (result.equals("")) {
                    sender.sendMessage("§6§lBedwarsXP §7>> §a" + BedwarsXP.l18n("GAME_XP_DISABLED", "%game%", args[1]));
                    if (BedwarsGameUtils.isGameRunning(args[1])) {
                        sender.sendMessage("§6§lBedwarsXP §7>> §4" + BedwarsXP.l18n("GAME_IS_RUNNING_RESTART_REQUIRED", "%game%", args[1]));
                    }
                } else {
                    SendMessageUtils.sendMessage(sender, "§6§lBedwarsXP §7>> §c" + BedwarsXP.l18n("ERROR_OCCURRED"), "§6§lBedwarsXP §7>> §c" + result);
                }
                return true;
            }
            default: {
                SendMessageUtils.sendMessage(sender,
                        "§6§lBedwarsXP §7>> §bBedwarsXP v." + BedwarsXP.getInstance().getDescription().getVersion() + " §lBy.SakuraKooi",
                        "§6§lBedwarsXP §7>> §a" + BedwarsXP.l18n("HELP_MAIN_RELOAD"),
                        "§6§lBedwarsXP §7>> §a" + BedwarsXP.l18n("HELP_MAIN_ENABLE"),
                        "§6§lBedwarsXP §7>> §a" + BedwarsXP.l18n("HELP_MAIN_DISABLE"));
                return true;
            }
        }
    }

}
