package nl.thedutchmc.multiplayerevents.events.eventitemcommission.listeners;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import nl.thedutchmc.multiplayerevents.events.eventitemcommission.EventItemCommission;

public class InventoryCloseEventListener implements Listener {

	private EventItemCommission eventItemCommission;
	
	public InventoryCloseEventListener(EventItemCommission eventItemCommission) {
		this.eventItemCommission = eventItemCommission;
	}
	
	@EventHandler
	public void onInventoryCloseEvent(InventoryCloseEvent event) {
		
		//Check if the opened inventory's location is the same location as the collection chest's location
		if(!(event.getInventory().getLocation() != null && event.getInventory().getLocation().equals(eventItemCommission.getCollectionChestLocation()))) return;
		
		//Check if the inventory changed, if not return
		if(eventItemCommission.getCollectionChestContents().equals(Arrays.asList(event.getInventory().getContents()))) return;
	
		//Check if the requirements are present in the current content
		//If not, return
		List<ItemStack> currentContents = Arrays.asList(event.getInventory().getContents());
		if(!currentContents.containsAll(eventItemCommission.getTargetChestContents())) return;
		
		Player player = (Player) event.getPlayer();
		eventItemCommission.finishEvent(player);
	}
}
