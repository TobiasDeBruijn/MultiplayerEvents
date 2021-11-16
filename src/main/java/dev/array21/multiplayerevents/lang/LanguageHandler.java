package dev.array21.multiplayerevents.lang;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;

import com.google.common.io.Files;

import dev.array21.multiplayerevents.MultiplayerEvents;
import dev.array21.multiplayerevents.config.ConfigHandler;
import dev.array21.multiplayerevents.utils.Utils;

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
		
		ConfigHandler config = new ConfigHandler(plugin);
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
				MultiplayerEvents.logDebug(Utils.getStackTrace(e));
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
			MultiplayerEvents.logDebug(Utils.getStackTrace(e));
		} catch (IOException e) {
			MultiplayerEvents.logWarn("Unable to read " + language + ".yml due to an IOException!");
			MultiplayerEvents.logDebug(Utils.getStackTrace(e));
		}
	}
	
	private void readConfig() {
		for(String key : config.getKeys(false)) {
			String value = config.getString(key);
			languageValues.put(key, value);
		}
	}
	
	/**
	 * Returns the language value associated with the provided key. Color codes have already been parsed.<br>
	 * <br>
	 * If the language value contains an array (${...}), color codes in there will not be parsed!.
	 * @param key The key of the language value
	 * @return Returns the language value associated with the provided key
	 */
	public static String getLangValue(String key) {
		String value = languageValues.get(key);
		
		Pattern pattern = Pattern.compile("(\\$\\{.*\\})");
		Matcher matcher = pattern.matcher(value);
		if(matcher.matches()) {
			String[] partsSplitOnArrayStart = value.split("\\$\\{");
			String[] partsSplitOnArrayEnd = partsSplitOnArrayStart[1].split("\\}");
			String arrayContent = partsSplitOnArrayEnd[0];
			
			String partsWithoutArray = String.join("<--DELIM-->", partsSplitOnArrayStart[0], partsSplitOnArrayEnd[1]);
			String partsWithoutArrayColorParsed = ChatColor.translateAlternateColorCodes('&', partsWithoutArray);
			
			String output = partsWithoutArrayColorParsed
					.replace("<--DELIM-->", arrayContent);
			
			return output;
		} else {
			return ChatColor.translateAlternateColorCodes('&', languageValues.get(key));
		}	
	}
}
