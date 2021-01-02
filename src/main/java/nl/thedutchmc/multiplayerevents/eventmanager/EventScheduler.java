package nl.thedutchmc.multiplayerevents.eventmanager;

import org.bukkit.scheduler.BukkitRunnable;

import nl.thedutchmc.multiplayerevents.ConfigurationHandler;
import nl.thedutchmc.multiplayerevents.MultiplayerEvents;
import nl.thedutchmc.multiplayerevents.Utils;

public class EventScheduler {
	
	private MultiplayerEvents plugin;
	private int eventIntervalLowerBound, eventIntervalUpperBound;
	
	public EventScheduler(MultiplayerEvents plugin) {
		this.plugin = plugin;
		
		ConfigurationHandler config = new ConfigurationHandler(plugin);
		this.eventIntervalLowerBound = (int) config.getConfigOption("eventIntervalLowerBound");
		this.eventIntervalUpperBound = (int) config.getConfigOption("eventIntervalUpperBound");
		
		int delay = Utils.getRandomInt(eventIntervalLowerBound, eventIntervalUpperBound);
		scheduleNextEvent(delay);
	}
	
	private void scheduleNextEvent(long delay) {
		MultiplayerEvents.logDebug("Starting new event in " + delay + " seconds");
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				MultiplayerEvent chosenEvent = MultiplayerEvents.getEventRegister().getRandomEvent();
				if(chosenEvent == null) return;
				
				chosenEvent.fireEvent();
				
				int delay = Utils.getRandomInt(eventIntervalLowerBound, eventIntervalUpperBound);
				scheduleNextEvent(delay);
			}
		}.runTaskLater(plugin, delay*20);
	}
}
