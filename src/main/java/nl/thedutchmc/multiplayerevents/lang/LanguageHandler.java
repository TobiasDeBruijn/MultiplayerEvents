package nl.thedutchmc.multiplayerevents.lang;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;

import com.google.common.io.Files;

import nl.thedutchmc.multiplayerevents.ConfigurationHandler;
import nl.thedutchmc.multiplayerevents.MultiplayerEvents;

public class LanguageHandler {

	private MultiplayerEvents plugin;
	private File file;
	private FileConfiguration config;

	//Config values
	private String language;
	
	private static HashMap<String, String> languageValues = new HashMap<>();
	
	public LanguageHandler(MultiplayerEvents plugin, String language) {
		this.plugin = plugin;
		this.language = language;
	}
	
	public LanguageHandler(MultiplayerEvents plugin) {
		this.plugin = plugin;
		
		ConfigurationHandler config = new ConfigurationHandler(plugin);
		language = (String) config.getConfigOption("language");
	}
	
	public void loadConfig() {
		file = new File(plugin.getDataFolder() + File.separator + "languages", language + ".yml");
		
		if(language.equals("en") && !file.exists()) {
			plugin.saveResource("en.yml", false);
			
			File langDir = new File(plugin.getDataFolder() + File.separator + "languages");
			langDir.mkdirs();
			
			File tmpFileLocation = new File(plugin.getDataFolder(), "en.yml");

			try {
				Files.move(tmpFileLocation, file);
			} catch (IOException e) {
				MultiplayerEvents.logWarn("Unable to move en.yml due to an IOException!");
				MultiplayerEvents.logDebug(ExceptionUtils.getStackTrace(e));
			}	
		} else {
			if(!file.exists()) {
				MultiplayerEvents.logWarn(language + ".yml does not exist! Falling back to English.");
				new LanguageHandler(plugin, "en").loadConfig();;
				return;
			}
		}
		
		config = new Utf8YamlConfiguration();
		
		try {
			config.load(file);
			readConfig();
		} catch (InvalidConfigurationException e) {
			MultiplayerEvents.logWarn(language + ".yml is invalid!");
			MultiplayerEvents.logDebug(ExceptionUtils.getStackTrace(e));
		} catch (IOException e) {
			MultiplayerEvents.logWarn("Unable to read " + language + ".yml due to an IOException!");
			MultiplayerEvents.logDebug(ExceptionUtils.getStackTrace(e));
		}
	}
	
	private void readConfig() {
		for(String key : config.getKeys(false)) {
			String value = config.getString(key);
			languageValues.put(key, value);
		}
	}
	
	/**
	 * Returns the language value associated with the provided key. Color codes have already been parsed.
	 * @param key The key of the language value
	 * @return Returns the language value associated with the provided key
	 */
	public static String getLangValue(String key) {
		return ChatColor.translateAlternateColorCodes('&', languageValues.get(key));
	}
}
