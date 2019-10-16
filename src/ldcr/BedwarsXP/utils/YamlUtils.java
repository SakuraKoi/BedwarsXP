package ldcr.BedwarsXP.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.bukkit.configuration.file.YamlConfiguration;

import lombok.Cleanup;

public class YamlUtils {
	public static YamlConfiguration loadYamlUTF8(final File file) throws IOException {
		@Cleanup final FileInputStream fis = new FileInputStream(file);
		final String yaml = IOUtils.toString(fis, StandardCharsets.UTF_8);
		final StringReader sr = new StringReader(yaml);
		return YamlConfiguration.loadConfiguration(sr);
	}
}
