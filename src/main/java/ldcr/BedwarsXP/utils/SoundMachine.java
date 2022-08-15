package ldcr.BedwarsXP.utils;

import org.bukkit.Sound;

public class SoundMachine {
    public static Sound get(String v18, String v19) {
        try {
            // Try get old sound name
            return Sound.valueOf(v18);
        } catch (IllegalArgumentException e1) {
            try {
                // Try get new sound name
                return Sound.valueOf(v19);
            } catch (IllegalArgumentException e2) {
                // not found
                return null;
            }
        }
    }

}
