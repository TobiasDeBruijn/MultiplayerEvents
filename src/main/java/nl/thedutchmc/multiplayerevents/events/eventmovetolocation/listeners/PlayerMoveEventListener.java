package nl.thedutchmc.multiplayerevents.events.eventmovetolocation.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import nl.thedutchmc.multiplayerevents.Utils;
import nl.thedutchmc.multiplayerevents.events.eventmovetolocation.EventMoveToLocation;

public class PlayerMoveEventListener implements Listener {

	private EventMoveToLocation eventMoveToLocation;
	private int finishRadius;
	
	public PlayerMoveEventListener(EventMoveToLocation eventMoveToLocation, int finishRadius) {
		this.eventMoveToLocation = eventMoveToLocation;
		this.finishRadius = finishRadius;
	}
	
	@EventHandler
	public void onPlayerMoveEvent(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		
		Location locFinish = eventMoveToLocation.getFinishLocationFor(p.getUniqueId());
		double distance = Utils.getDistanceCylindrical(p.getLocation(), locFinish);
		
		//Player has reached the finish
		if(distance <= finishRadius) {
			eventMoveToLocation.playerFinished(p);
		}
	}
}
