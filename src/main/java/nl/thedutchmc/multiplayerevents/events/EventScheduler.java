package nl.thedutchmc.multiplayerevents.events;

import org.bukkit.scheduler.BukkitRunnable;

import nl.thedutchmc.multiplayerevents.ConfigurationHandler;
import nl.thedutchmc.multiplayerevents.MultiplayerEvents;
import nl.thedutchmc.multiplayerevents.Utils;

public class EventScheduler {
	
	private MultiplayerEvents plugin;
	private int eventIntervalLowerBound, eventIntervalUpperBound;
	private EventState eventState;
	
	public EventScheduler(MultiplayerEvents plugin) {
		this.plugin = plugin;
		
		ConfigurationHandler config = new ConfigurationHandler(plugin);
		this.eventIntervalLowerBound = (int) config.getConfigOption("eventIntervalLowerBound");
		this.eventIntervalUpperBound = (int) config.getConfigOption("eventIntervalUpperBound");
		
		eventState = EventState.WAITING;
		
		int delay = Utils.getRandomInt(eventIntervalLowerBound, eventIntervalUpperBound);
		scheduleNextEvent(delay);
	}
	
	private void scheduleNextEvent(long delay) {
		
		//If an event is already running we do not want to start a new one
		//so we just wait another cycle
		if(eventState == EventState.RUNNING) {
			scheduleNextEvent(delay);
			return;
		}
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
	
	public void setEventState(EventState eventState) {
		this.eventState = eventState;
	}
}
