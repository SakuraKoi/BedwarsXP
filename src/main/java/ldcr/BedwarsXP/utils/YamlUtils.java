package ldcr.BedwarsXP.utils;

import lombok.Cleanup;
import org.apache.commons.io.IOUtils;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

public class YamlUtils {
	public static YamlConfiguration loadYamlUTF8(File file) throws IOException {
		@Cleanup FileInputStream fis = new FileInputStream(file);
		String yaml = IOUtils.toString(fis, StandardCharsets.UTF_8);
		StringReader sr = new StringReader(yaml);
		return YamlConfiguration.loadConfiguration(sr);
	}
}
