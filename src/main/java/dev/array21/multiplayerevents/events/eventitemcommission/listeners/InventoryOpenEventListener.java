package dev.array21.multiplayerevents.events.eventitemcommission.listeners;

import java.util.Arrays;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import dev.array21.multiplayerevents.events.eventitemcommission.EventItemCommission;
import dev.array21.multiplayerevents.lang.LanguageHandler;

public class InventoryOpenEventListener implements Listener {

	private EventItemCommission eventItemCommission;
	
	public InventoryOpenEventListener(EventItemCommission eventItemCommission) {
		this.eventItemCommission = eventItemCommission;
	}
	
	@EventHandler
	public void onInventoryOpenEvent(InventoryOpenEvent event) {
		
		//Check if the opened inventory's location is the same location as the collection chest's location
		if(!(event.getInventory().getLocation() != null && event.getInventory().getLocation().equals(eventItemCommission.getCollectionChestLocation()))) return;
		
		//Check if the inventory is already open, if so, cancel the event
		//The viewers size is 1 if the player is the only player attempting to open the inventory, because this event fires after the player is added to the list
		if(event.getInventory().getViewers().size() != 1) {
			event.getPlayer().sendMessage(LanguageHandler.getLangValue("eventItemCommissionInventoryAlreadyOpen"));
			event.getPlayer().getOpenInventory().close();
			event.setCancelled(true);
		}
		
		//Get the content inventory and set the list to the content of the inventory
		ItemStack[] inventoryContent = event.getInventory().getContents();
		eventItemCommission.setCollectionChestContents(Arrays.asList(inventoryContent));
	}
}
