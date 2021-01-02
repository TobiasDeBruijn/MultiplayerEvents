package nl.thedutchmc.multiplayerevents.eventmanager;

import java.util.ArrayList;
import java.util.List;

import nl.thedutchmc.multiplayerevents.ConfigurationHandler;
import nl.thedutchmc.multiplayerevents.MultiplayerEvents;
import nl.thedutchmc.multiplayerevents.Utils;
import nl.thedutchmc.multiplayerevents.events.eventmobkill.EventMobKill;

public class EventRegister {

	private List<MultiplayerEvent> events = new ArrayList<>();
	
	private MultiplayerEvents plugin;	
	
	public EventRegister(MultiplayerEvents plugin) {
		this.plugin = plugin;
	}
	
	public void registerDefaultEvents() {
		ConfigurationHandler config = new ConfigurationHandler(plugin);
		
		if((boolean) config.getConfigOption("eventMobKillEnabled")) {
			events.add(new EventMobKill(plugin));
		}
	}
	
	public void registerNewEvent(MultiplayerEvent multiplayerEvent) {
		this.events.add(multiplayerEvent);
	}
	
	public MultiplayerEvent getRandomEvent() {
		int randomIndex = Utils.getRandomInt(0, events.size());
		//TODO add check if randomIndex == 0
		return events.get(randomIndex);
	}
}
