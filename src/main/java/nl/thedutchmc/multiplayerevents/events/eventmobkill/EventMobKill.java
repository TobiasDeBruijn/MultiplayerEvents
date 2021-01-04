package nl.thedutchmc.multiplayerevents.events.eventmobkill;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.ChatColor;
import nl.thedutchmc.multiplayerevents.ConfigurationHandler;
import nl.thedutchmc.multiplayerevents.MultiplayerEvents;
import nl.thedutchmc.multiplayerevents.Utils;
import nl.thedutchmc.multiplayerevents.events.EventScheduler;
import nl.thedutchmc.multiplayerevents.events.EventState;
import nl.thedutchmc.multiplayerevents.events.MultiplayerEvent;
import nl.thedutchmc.multiplayerevents.events.eventmobkill.listeners.EntityDamageByEntityEventListener;

/**
 * This event will challenge the player to kill n amount of some mob or animal within x amount of time
 */
public class EventMobKill implements MultiplayerEvent {

	private MultiplayerEvents plugin;
	private EventScheduler scheduler;
	
	//Config values
	private boolean onlyHostile;
	private int mobKillCountLowerBound, mobKillCountUpperBound;
	
	public HashMap<UUID, Integer> scoreCount = new HashMap<>();

	public EventMobKill(MultiplayerEvents plugin, EventScheduler scheduler) {		
		this.plugin = plugin;
		this.scheduler = scheduler;
		
		//Get configuration values
		ConfigurationHandler config = new ConfigurationHandler(plugin);		
		onlyHostile = (boolean) config.getConfigOption("eventMobKillHostileOnly");
		mobKillCountLowerBound = (int) config.getConfigOption("eventMobKillCountLowerBound");
		mobKillCountUpperBound = (int) config.getConfigOption("eventMobKillCountUpperBound");
	}
	
	@Override
	public boolean fireEvent() {
		scheduler.setEventState(EventState.RUNNING);
		
		List<EntityType> entityTypes = Arrays.asList(EntityType.values());
		
		//Iterate over all EntityType's and filter them to LivingEntity's and/or Monsters
		List<EntityType> filteredEntityTypes = new ArrayList<>();
		for(EntityType entityType : entityTypes) {
			
			//If the mob is an UNKNOWN, we don't care, continue on
			if(entityType.equals(EntityType.UNKNOWN)) continue;
			
			//Check if the current entityType is a Monster
			if(onlyHostile && Monster.class.isAssignableFrom(entityType.getEntityClass())) {
				//TODO check exclusion list
				filteredEntityTypes.add(entityType);
			
			//Check if the current entityType is a LivingEntity, but not a Player
			} else if(LivingEntity.class.isAssignableFrom(entityType.getEntityClass()) && !Player.class.isAssignableFrom(entityType.getEntityClass())) {
				//TODO check exclusion list
				filteredEntityTypes.add(entityType);
			}
		}
		
		//Get a random entity from our filtered list
		int randomIndex = Utils.getRandomInt(0, filteredEntityTypes.size());
		EntityType chosenType = filteredEntityTypes.get(randomIndex);
		
		//Get a random number. This is how many of the chosen entity needs to be killed by the player
		int count = Utils.getRandomInt(mobKillCountLowerBound, mobKillCountUpperBound);
		
		//Register required event listeners for this MultiplayerEvent
		
		//Register the required event listener for this MultiplayerEvent
		EntityDamageByEntityEventListener edbeListener = new EntityDamageByEntityEventListener(chosenType, this);
		Bukkit.getPluginManager().registerEvents(edbeListener, plugin);
		
		//give the name of the chosen EntityType pretty capitalization
		String typeName = Utils.prettyCaptitalizationForMinecraftNames(chosenType.toString());
		
		//If the Player needs to kill more than 1 of the chosen Entity, pluralize it.
		if(count > 1) {
			typeName = Utils.pluralizeSingularEnglish(typeName);
		}

		//Iterate over all Players to inform them that the event has ended
		for(Player p : Bukkit.getOnlinePlayers()) {
			//TODO multi language support

			p.sendMessage(ChatColor.GOLD + "Event starting now: Kill " + ChatColor.RED + count + " " + typeName + ChatColor.GOLD + " within 5 minutes!");
		}
		
		MultiplayerEvents.logInfo("Event starting now: Kill " + count + " " + typeName + " within 5 minutes!");
		
		
		//Start a 'timer' for when the event should end.
		new BukkitRunnable() {
			
			@Override
			public void run() {
				scheduler.setEventState(EventState.ENDING);
				
				//Unregister the listeners for this event
				HandlerList.unregisterAll(edbeListener);
				
				//Iterate over all players to inform them that the event has ended
				Bukkit.getOnlinePlayers().forEach(player -> {	
					//TODO formatting and multi language support required
					if(scoreCount.containsKey(player.getUniqueId())) {
						player.sendMessage("Event over. Your score is: " + scoreCount.get(player.getUniqueId()));
					} else {
						player.sendMessage("Event over. You have no score!");
					}
				});
				
				scheduler.setEventState(EventState.WAITING);
			}
		}.runTaskLater(plugin, 5*60*20);
		//TODO current event length is hardcoded at 5 minutes, should be configurable
		
		return true;
	}
	
}
