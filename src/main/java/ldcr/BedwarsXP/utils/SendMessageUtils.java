package ldcr.BedwarsXP.utils;

import org.bukkit.command.CommandSender;

public class SendMessageUtils {
	public static void sendMessage(CommandSender sender, String... message) {
		sender.sendMessage(message);
	}
}
