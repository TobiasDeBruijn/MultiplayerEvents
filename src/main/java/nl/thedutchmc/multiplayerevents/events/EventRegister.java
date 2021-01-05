package nl.thedutchmc.multiplayerevents.events;

import java.util.ArrayList;
import java.util.List;

import nl.thedutchmc.multiplayerevents.ConfigurationHandler;
import nl.thedutchmc.multiplayerevents.MultiplayerEvents;
import nl.thedutchmc.multiplayerevents.Utils;
import nl.thedutchmc.multiplayerevents.events.eventitemcommission.EventItemCommission;

public class EventRegister {

	private List<MultiplayerEvent> events = new ArrayList<>();
	
	private MultiplayerEvents plugin;	
	
	public EventRegister(MultiplayerEvents plugin) {
		this.plugin = plugin;
	}
	
	/**
	 * This method will register all default events provided by the MultiplayerEvents plugin
	 */
	public void registerDefaultEvents() {
		ConfigurationHandler config = new ConfigurationHandler(plugin);		
		/*if((boolean) config.getConfigOption("eventMobKillEnabled")) {
			events.add(new EventMobKill(plugin));
		}
		
		if((boolean) config.getConfigOption("eventMoveToLocationEnabled")) {
			events.add(new EventMoveToLocation(plugin));
		}*/
		
		if((boolean) config.getConfigOption("eventItemCommissionCollectionEnabled")) {
			events.add(new EventItemCommission(plugin));
		}
	}
	
	/**
	 * This method will register the provided event
	 * @param multiplayerEvent The even to register
	 */
	public void registerNewEvent(MultiplayerEvent multiplayerEvent) {
		this.events.add(multiplayerEvent);
	}
	
	/**
	 * Get a random event from the list of registered events
	 * @return Returns a random event
	 */
	public MultiplayerEvent getRandomEvent() {
		int randomIndex = Utils.getRandomInt(0, events.size() -1);
		return events.get(randomIndex);
	}
}
