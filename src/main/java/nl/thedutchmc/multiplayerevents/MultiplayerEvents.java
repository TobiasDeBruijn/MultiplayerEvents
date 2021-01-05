package nl.thedutchmc.multiplayerevents;

import org.bukkit.plugin.java.JavaPlugin;

import nl.thedutchmc.multiplayerevents.events.EventRegister;
import nl.thedutchmc.multiplayerevents.events.EventScheduler;
import nl.thedutchmc.multiplayerevents.lang.LanguageHandler;

public class MultiplayerEvents extends JavaPlugin {

	public static boolean DEBUG = true;
	private static EventRegister eventRegister;
	private static EventScheduler eventScheduler;

	private static MultiplayerEvents INSTANCE;
	
	@Override
	public void onEnable() {
		INSTANCE = this;
		
		ConfigurationHandler configHandler = new ConfigurationHandler(this);
		configHandler.loadConfig();

		LanguageHandler langHandler = new LanguageHandler(this);
		langHandler.loadConfig();
		
		eventScheduler = new EventScheduler(this);

		eventRegister = new EventRegister(this);
		eventRegister.registerDefaultEvents();
		
	}
	
	@Override
	public void onDisable() {
		
	}
	
	public static EventRegister getEventRegister() {
		return eventRegister;
	}
	
	public EventScheduler getEventScheduler() {
		return eventScheduler;
	}
	
	public static void logDebug(String log) {
		if(!DEBUG) return;
		INSTANCE.getLogger().info("[DEBUG] " + log);
	}
	
	public static void logInfo(String log) {
		INSTANCE.getLogger().info(log);
	}
	
	public static void logWarn(String log) {
		INSTANCE.getLogger().warning(log);
	}
}
