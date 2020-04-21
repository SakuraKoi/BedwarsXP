package ldcr.BedwarsXP.utils;

import org.bukkit.Sound;

public class SoundMachine {
	public static Sound get(String v18, String v19) {
		Sound sound = null;
		try {
			sound = Sound.valueOf(v18);
			if (sound == null) {
				sound = Sound.valueOf(v19);
			}
		} catch (Exception ex) {
		}
		return sound;
	}

}
