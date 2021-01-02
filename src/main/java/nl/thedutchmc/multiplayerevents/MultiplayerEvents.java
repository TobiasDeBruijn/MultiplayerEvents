package nl.thedutchmc.multiplayerevents;

import org.bukkit.plugin.java.JavaPlugin;

import nl.thedutchmc.multiplayerevents.eventmanager.EventRegister;
import nl.thedutchmc.multiplayerevents.eventmanager.EventScheduler;

public class MultiplayerEvents extends JavaPlugin {

	public static boolean DEBUG = true;
	private static EventRegister eventRegister;
	private static MultiplayerEvents INSTANCE;
	
	@Override
	public void onEnable() {
		INSTANCE = this;
		
		ConfigurationHandler configHandler = new ConfigurationHandler(this);
		configHandler.loadConfig();
	
		
		eventRegister = new EventRegister(this);
		eventRegister.registerDefaultEvents();
		
		new EventScheduler(this);
	}
	
	@Override
	public void onDisable() {
		
	}
	
	public static EventRegister getEventRegister() {
		return eventRegister;
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
