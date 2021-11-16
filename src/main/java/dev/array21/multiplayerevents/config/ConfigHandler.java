package dev.array21.multiplayerevents.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.google.gson.Gson;

import dev.array21.multiplayerevents.MultiplayerEvents;
import dev.array21.multiplayerevents.utils.Utils;
import org.yaml.snakeyaml.Yaml;

public class ConfigHandler {

	private static MultiplayerEvents plugin;
	private final static Yaml YAML = new Yaml();
	private final static Gson GSON = new Gson();
	private ConfigManifest config;

	public ConfigHandler(MultiplayerEvents plugin) {
		this.plugin = plugin;
	}
	
	private void loadConfig() {
		File file = new File(plugin.getDataFolder(), "config.yml");
		
		if(!file.exists()) {
			file.getParentFile().mkdirs();
			plugin.saveResource("config.yml", false);
		}

		Object yaml;
		try {
			yaml = YAML.load(new FileInputStream(file));
		} catch (IOException e) {
			MultiplayerEvents.logWarn("Unable to read config.yml due to an IOException!");
			MultiplayerEvents.logDebug(Utils.getStackTrace(e));
			return;
		}

		String json = GSON.toJson(yaml, java.util.LinkedHashMap.class);
		this.config = GSON.fromJson(json, ConfigManifest.class);
	}
	
	public ConfigManifest getManifest() {
		if(this.config == null) {
			this.loadConfig();
		}

		return this.config;
	}
}
