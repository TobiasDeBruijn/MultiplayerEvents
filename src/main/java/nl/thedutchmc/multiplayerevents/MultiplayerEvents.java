package nl.thedutchmc.multiplayerevents;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.plugin.java.JavaPlugin;

import nl.thedutchmc.multiplayerevents.commands.CommandManager;
import nl.thedutchmc.multiplayerevents.events.EventRegister;
import nl.thedutchmc.multiplayerevents.events.EventScheduler;
import nl.thedutchmc.multiplayerevents.lang.LanguageHandler;
import nl.thedutchmc.multiplayerevents.rewards.RewardManager;

public class MultiplayerEvents extends JavaPlugin {

	public static boolean DEBUG = true;
	private static EventRegister eventRegister;
	private static EventScheduler eventScheduler;
	private static RewardManager rewardManager;
	
	private static MultiplayerEvents INSTANCE;
	
	@Override
	public void onEnable() {
		INSTANCE = this;
		
		ConfigurationHandler configHandler = new ConfigurationHandler(this);
		configHandler.loadConfig();

		LanguageHandler langHandler = new LanguageHandler(this);
		langHandler.loadConfig();
		
		rewardManager = new RewardManager();
		eventScheduler = new EventScheduler(this);

		eventRegister = new EventRegister(this);
		eventRegister.discoverAndRegisterEvents();
		
		CommandManager commandManager = new CommandManager(this);
		this.getCommand("mpe").setExecutor(commandManager);
		this.getCommand("mpe").setTabCompleter(commandManager);
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
	
	public RewardManager getRewardManager() {
		return rewardManager;
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
	
	/**
	 * Get all annotated classes for an annotation
	 * @param annotation The class of the annotation
	 * @return A list of Strings, which are the canonical name of the classes with the annotation
	 */
	public List<String> getAnnotatedClasses(Class<?> annotation) {
		String className = annotation.getCanonicalName();

		InputStream annotatedClassesFile = this.getClass().getResourceAsStream("/META-INF/annotations/" + className);
		BufferedReader br = new BufferedReader(new InputStreamReader(annotatedClassesFile));
		
		List<String> result = br.lines().collect(Collectors.toList());
	    return result;
	}
}
