package dev.array21.multiplayerevents.events.eventmobkill;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import dev.array21.multiplayerevents.config.ConfigManifest;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import dev.array21.multiplayerevents.MultiplayerEvents;
import dev.array21.multiplayerevents.annotations.RegisterMultiplayerEvent;
import dev.array21.multiplayerevents.config.ConfigHandler;
import dev.array21.multiplayerevents.events.EventScheduler;
import dev.array21.multiplayerevents.events.EventState;
import dev.array21.multiplayerevents.events.MultiplayerEvent;
import dev.array21.multiplayerevents.events.eventmobkill.listeners.EntityDamageByEntityEventListener;
import dev.array21.multiplayerevents.lang.LanguageHandler;
import dev.array21.multiplayerevents.utils.Utils;

/**
 * This event will challenge the player to kill n amount of some mob or animal within x amount of time
 */
@RegisterMultiplayerEvent
public class EventMobKill implements MultiplayerEvent {

	private final MultiplayerEvents plugin;
	private final EventScheduler scheduler;
	private final ConfigManifest.EventMobKill config;

	public HashMap<UUID, Integer> scoreCount = new HashMap<>();
	private final List<Listener> listeners = new ArrayList<>();
	private int count;
	
	@SuppressWarnings("unchecked")
	public EventMobKill(MultiplayerEvents plugin) {		
		this.plugin = plugin;
		this.scheduler = plugin.getEventScheduler();
		this.config = plugin.getConfigHandler().getManifest().getEventMobKill();
	}
	
	@Override
	public String getEnabledConfigOptionName() {
		return "eventMobKillEnabled";
	}
	
	@Override
	public boolean fireEvent() {		
		List<EntityType> entityTypes = Arrays.asList(EntityType.values());
		
		//Decide on the event duration
		int eventDuration = Utils.getRandomInt(config.getDurationLowerBound(), config.getDurationUpperBound());
		
		MultiplayerEvents.logDebug(LanguageHandler.getLangValue("startingEventLog")
				.replace("%EVENT_NAME%", "MobKill"));
		
		//Iterate over all EntityType's and filter them to LivingEntity's and/or Monsters
		List<EntityType> filteredEntityTypes = new ArrayList<>();
		for(EntityType entityType : entityTypes) {
			
			//If the mob is an UNKNOWN, we don't care, continue on
			if(entityType.equals(EntityType.UNKNOWN)) continue;

			//Check if the current mob is blacklisted by the player
			if(config.getMobBlacklist().contains(entityType.toString().toLowerCase())) continue;
			
			//Check if the current entityType is a Monster
			if(config.isHostileOnly() && Monster.class.isAssignableFrom(entityType.getEntityClass())) {
				filteredEntityTypes.add(entityType);
				
			//Check if the current entityType is a LivingEntity, but not a Player
			} else if(LivingEntity.class.isAssignableFrom(entityType.getEntityClass()) && !Player.class.isAssignableFrom(entityType.getEntityClass())) {
				filteredEntityTypes.add(entityType);
			}
		}
		
		//Get a random entity from our filtered list
		int randomIndex = Utils.getRandomInt(0, filteredEntityTypes.size() -1);
		EntityType chosenType = filteredEntityTypes.get(randomIndex);
		
		//Get a random number. This is how many of the chosen entity needs to be killed by the player
		this.count = Utils.getRandomInt(config.getCountLowerBound(), config.getCountUpperBound());
		
		//Register required event listeners for this MultiplayerEvent
		
		//Register the required event listener for this MultiplayerEvent
		EntityDamageByEntityEventListener edbeListener = new EntityDamageByEntityEventListener(chosenType, this);
		Bukkit.getPluginManager().registerEvents(edbeListener, plugin);
		this.listeners.add(edbeListener);
		
		//give the name of the chosen EntityType pretty capitalization
		String typeName = Utils.prettyCaptitalizationForMinecraftNames(chosenType.toString());
		
		//If the Player needs to kill more than 1 of the chosen Entity, pluralize it.
		if(this.count > 1) {
			typeName = Utils.pluralizeSingularEnglish(typeName);
		}

		//Iterate over all Players to inform them that the event has ended
		for(Player p : Bukkit.getOnlinePlayers()) {
			p.sendMessage(LanguageHandler.getLangValue("eventMobKillStarting")
					.replace("%MOB_COUNT%", String.valueOf(count))
					.replace("%MOB_NAME%", typeName)
					.replace("%EVENT_DURATION%", String.valueOf(eventDuration)));			
		}
				
		//We put this at the end, in case an error occurs during the starting of the MultiplayerEvent
		scheduler.setEventState(EventState.RUNNING);
		
		//Start a 'timer' for when the event should end.
		new BukkitRunnable() {
			
			@Override
			public void run() {
				finishEvent();
			}
		}.runTaskLater(plugin, eventDuration * 20L);
		
		return true;
	}
	
	private void finishEvent() {
		scheduler.setEventState(EventState.ENDING);
		
		MultiplayerEvents.logDebug(LanguageHandler.getLangValue("endingEventLog")
				.replace("%EVENT_NAME%", "MobKill"));
		
		//Unregister the listeners for this event
		listeners.forEach(eventListener -> {
			HandlerList.unregisterAll(eventListener);
		});
		
		//Calculate the amount of points a player can receive
		int finalPossibleScore = Utils.getRandomInt(config.getRewardLowerBound(), config.getRewardUpperBound());
		
		//Iterate over all players to inform them that the event has ended
		Bukkit.getOnlinePlayers().forEach(player -> {	
				player.sendMessage(LanguageHandler.getLangValue("eventMobKillEnded")
						.replace("%MOB_KILL_COUNT%", ((scoreCount.containsKey(player.getUniqueId()) ? String.valueOf(scoreCount.get(player.getUniqueId())) : "0"))));
				
				//If the player has achieved a score, we want to reward them points
				if(scoreCount.containsKey(player.getUniqueId())) {
					
					//Get their score
					int playerScore = scoreCount.get(player.getUniqueId());
					
					int finalScore = finalPossibleScore;
					
					//If they scored less than the challenge, apply a 'penalty'
					if(playerScore < this.count) {
						//Calculate the 'penalty', which is a multiplier applied to the maximum possible score.
						//Calculated by taking eventMobKillCountRewardMultiplier and raising it to the power of the difference between the required score and the player's score
						float floatMultiplier = (float) Math.pow(config.getRewardMultiplier(), (this.count - playerScore));
						
						//Apply the multiplier and round to the nearest integer
						finalScore = Math.round((float) finalPossibleScore * floatMultiplier);
					}
					
					//Add the score to the Player's 'balance'
					this.plugin.getRewardManager().awardPoints(player.getUniqueId(), finalScore);
					
					player.sendMessage(LanguageHandler.getLangValue("eventMobKillFinished")
							.replace("%POINTS%", String.valueOf(finalScore)));
				} else {
					//Player got no score
					player.sendMessage(LanguageHandler.getLangValue("eventMobKillFailed"));
				}
				
		});
		
		scheduler.setEventState(EventState.WAITING);
	}
}
