package ldcr.BedwarsXP.utils;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class YamlUtils {
    public static YamlConfiguration loadYamlUTF8(File file) throws IOException {
        String yaml = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
        StringReader sr = new StringReader(yaml);
        return YamlConfiguration.loadConfiguration(sr);
    }
}
