package nl.thedutchmc.multiplayerevents.events.eventmovetolocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import net.md_5.bungee.api.ChatColor;
import nl.thedutchmc.multiplayerevents.ConfigurationHandler;
import nl.thedutchmc.multiplayerevents.MultiplayerEvents;
import nl.thedutchmc.multiplayerevents.annotations.RegisterMultiplayerEvent;
import nl.thedutchmc.multiplayerevents.events.EventScheduler;
import nl.thedutchmc.multiplayerevents.events.EventState;
import nl.thedutchmc.multiplayerevents.events.MultiplayerEvent;
import nl.thedutchmc.multiplayerevents.events.eventmovetolocation.listeners.PlayerMoveEventListener;
import nl.thedutchmc.multiplayerevents.lang.LanguageHandler;
import nl.thedutchmc.multiplayerevents.utils.Utils;

/**
 * This event will challenge the player to move to a location as fast as possible
 */
@SuppressWarnings("unused") //For unimplemented features
@RegisterMultiplayerEvent
public class EventMoveToLocation implements MultiplayerEvent {

	private MultiplayerEvents plugin;
	private EventScheduler scheduler;
	
	//Config values
	private boolean enableMobs, enableFinishParticles;
	private int distanceLowerBound, distanceUpperBound, finishRadius, durationLowerBound, durationUpperBound, rewardsLowerBound, rewardsUpperBound;
	private double secondPlaceModifier, thirdPlaceModifier, noPodiumModifier;
	private List<String> mobWhitelist;
	
	private final int particleHeight = 15;
	
	private HashMap<UUID, Location> finishLocations = new HashMap<>();
	private HashMap<UUID, BukkitTask> particleTasks = new HashMap<>();
	private List<Player> finishedPlayers = new LinkedList<>();
	
	private List<Listener> listeners = new ArrayList<>();
	
	@SuppressWarnings("unchecked")
	public EventMoveToLocation(MultiplayerEvents plugin) {
		this.plugin = plugin;
		this.scheduler = plugin.getEventScheduler();
		
		//Get configuration values
		ConfigurationHandler config = new ConfigurationHandler(plugin);
		enableMobs = (boolean) config.getConfigOption("eventMoveToLocationEnableMobs");
		enableFinishParticles = (boolean) config.getConfigOption("eventMoveToLocationFinishShowParticles");
		distanceLowerBound = (int) config.getConfigOption("eventMoveToLocationDistanceLowerBound");
		distanceUpperBound = (int) config.getConfigOption("eventMoveToLocationDistanceUpperBound");
		finishRadius = (int) config.getConfigOption("eventMoveToLocationFinishRadius");
		durationLowerBound = (int) config.getConfigOption("eventMoveToLocationDurationLowerBound");
		durationUpperBound = (int) config.getConfigOption("eventMoveToLocationDurationUpperBound");
		rewardsLowerBound = (int) config.getConfigOption("eventMoveToLocationRewardLowerBound");
		rewardsUpperBound = (int) config.getConfigOption("eventMoveToLocationRewardUpperBound");
		secondPlaceModifier = (double) config.getConfigOption("eventMoveToLocationRewardSecondMultiplier");
		thirdPlaceModifier = (double) config.getConfigOption("eventMoveToLocationRewardThirdMultiplier");
		noPodiumModifier = (double) config.getConfigOption("eventMoveToLocatioNoPodiumModifier");
		
		
		mobWhitelist = (List<String>) config.getConfigOption("eventMoveToLocationMobWhitelist");
	}
	
	@Override
	public String getEnabledConfigOptionName() {
		return "eventMoveToLocationEnabled";
	}
	
	@Override
	public boolean fireEvent() {		
		int eventDuration = Utils.getRandomInt(durationLowerBound, durationUpperBound);
		MultiplayerEvents.logInfo(LanguageHandler.getLangValue("startingEventLog")
				.replace("%EVENT_NAME%", "MoveToLocation"));
		
		for(Player player : Bukkit.getOnlinePlayers()) {
			
			//Get a random radius and random offset
			int radius = Utils.getRandomInt(distanceLowerBound, distanceUpperBound);	
			int degOffset = Utils.getRandomInt(0, 359);
			double radOffset = degOffset * ((2*Math.PI)/180);
			
			Location playerLoc = player.getLocation();
			World w = playerLoc.getWorld();
			
			//Get the finish circle center X and Z based off of the random radius and random offset
			int finishCircleCX = (int) (playerLoc.getBlockX() + radius * Math.cos(radOffset));
			int finishCircleCZ = (int) (playerLoc.getBlockZ() + radius * Math.sin(radOffset));
			
			Location finishLocation = new Location(w, finishCircleCX, w.getHighestBlockYAt(finishCircleCX, finishCircleCZ), finishCircleCZ);
			finishLocations.put(player.getUniqueId(), finishLocation);
				
			//Generate particles at the finish location if the particles are enabled
			if(enableFinishParticles) {
				
				HashMap<Location, Color> particleLocations = new HashMap<>();
				
				//If the particleLocations map has no values, populate it 
				if(particleLocations.isEmpty()) {
					
					//Pick a random RGB color
					final int r = (int) (Math.random() * 256D);
					final int g = (int) (Math.random() * 256D);
					final int b = (int) (Math.random() * 256D);
					Color c = Color.fromRGB(r, g, b);
					
					//Calculate the locations of where the particles should be around the finish 
					for(int i = 0; i < 360; i++) {
						double rad = i * ((2 * Math.PI)/360);
						int x = (int) (finishCircleCX + (finishRadius * Math.cos(rad)));
						int z = (int) (finishCircleCZ + (finishRadius * Math.sin(rad)));
						int y = w.getHighestBlockYAt(x, z);
						
						particleLocations.put(new Location(w, x, y, z), c);
					}
				}
				
				//Runnable to actually spawn the particles for all the finish locations
				BukkitTask particleTask = new BukkitRunnable() {
					@Override
					public void run() { 
						for(Map.Entry<Location, Color> entry : particleLocations.entrySet()) {
							Location l = entry.getKey();
							
							for(int i = 0; i < particleHeight; i++) {
								w.spawnParticle(Particle.REDSTONE, l.getX() + 0.5D, l.getY() + 0.5D + i, l.getZ() + 0.5D, 5, 0, 0, 0, 0.005, new Particle.DustOptions(entry.getValue(), 1));
							}
						}
					}
				}.runTaskTimer(plugin, 60L, 10L); //Spawn the particle every 10 ticks (0.5 second)
				
				particleTasks.put(player.getUniqueId(), particleTask);
			}
			
			//Inform the player about this event
			player.sendMessage(LanguageHandler.getLangValue("eventMoveToLocationStarting")
					.replace("%LOCATION_X%", String.valueOf(finishCircleCX))
					.replace("%LOCATION_Z%", String.valueOf(finishCircleCZ))
					.replace("%EVENT_DURATION%", String.valueOf(eventDuration)));
		}
		
		//Register event listeners
		PlayerMoveEventListener pmeListener = new PlayerMoveEventListener(this, finishRadius);
		Bukkit.getPluginManager().registerEvents(pmeListener, plugin);
		
		//TODO spawn monsters
		
		//We put this at the end, in case an error occurs during the starting of the MultiplayerEvent
		scheduler.setEventState(EventState.RUNNING);
		
		//Schedule a task for the event end
		new BukkitRunnable() {
			
			@Override
			public void run() {
				eventFinished();
			}
		}.runTaskLater(plugin, eventDuration * 20L);
		
		return true;
	}
	
	private void eventFinished() {
		//EventState to ending
		scheduler.setEventState(EventState.ENDING);
		
		MultiplayerEvents.logDebug(LanguageHandler.getLangValue("endingEventLog")
				.replace("%EVENT_NAME%", "MoveToLocation"));
		
		//Cancel all particle tasks
		particleTasks.forEach((uuid, task) -> {
			task.cancel();
		});
		
		//Unregister event listeners
		this.listeners.forEach(eventListener -> {
			HandlerList.unregisterAll(eventListener);
		});
		
		//Calculate the amount of possible points
		int possiblePoints = Utils.getRandomInt(this.rewardsLowerBound, this.rewardsUpperBound);
		
		//Calculate the amount of points awarded to each podium spot
		//Calculated: placeModifier * possiblePoints, rounded to the nearest integer
		int secondPlacePoints = (int) Math.round(this.secondPlaceModifier * possiblePoints);
		int thirdPlacePoints = (int) Math.round(this.thirdPlaceModifier * possiblePoints);
		int noPodiumPoints = (int) Math.round(this.noPodiumModifier * possiblePoints);
		
		//If there's a winner, give the winner their points
		if(finishedPlayers.size() >= 1) {
			this.plugin.getRewardManager().awardPoints(finishedPlayers.get(0).getUniqueId(), possiblePoints);
		}
		
		//If there's a second place, award them their points
		if(finishedPlayers.size() >= 2) {
			this.plugin.getRewardManager().awardPoints(finishedPlayers.get(1).getUniqueId(), secondPlacePoints);
		}
		
		//If there's a third place, award them their points
		if(finishedPlayers.size() >= 3) {
			this.plugin.getRewardManager().awardPoints(finishedPlayers.get(2).getUniqueId(), thirdPlacePoints);
		}
		
		//Inform all players that the event has ended, and tell the fast players
		for(Player p : Bukkit.getOnlinePlayers()) {					
			p.sendMessage(LanguageHandler.getLangValue("eventMoveToLocationEnded"));
			
			//If there's a winner
			if(finishedPlayers.size() != 0) {
				
				//There is a first place winner, broadcast the message
				if(finishedPlayers.size() >= 1) {
					p.sendMessage(LanguageHandler.getLangValue("eventMoveToLocationFirstPlace")
							.replace("%PLAYER%", finishedPlayers.get(0).getDisplayName())
							.replace("%POINTS%", String.valueOf(possiblePoints)));
				}
				
				//There is a second place winner, broadcast the message
				if(finishedPlayers.size() >= 2) {
					p.sendMessage(LanguageHandler.getLangValue("eventMoveToLocationSecondPlace")
							.replace("%PLAYER%", finishedPlayers.get(1).getDisplayName())
							.replace("%POINTS%", String.valueOf(secondPlacePoints)));					
				}
				
				//There is a third place winner, broadcast the winner
				if(finishedPlayers.size() >= 3) {
					p.sendMessage(LanguageHandler.getLangValue("eventMoveToLocationThirdPlace")
							.replace("%PLAYER%", finishedPlayers.get(2).getDisplayName())
							.replace("%POINTS%", String.valueOf(thirdPlacePoints)));			
				}
			} else {
				//There were no winners
				p.sendMessage(LanguageHandler.getLangValue("eventMoveToLocationNoFinish"));
			}
		}
		
		//There are winners who did not make the podium
		if(finishedPlayers.size() >= 4) {
			
			//Get the list of those that did not make the podium
			List<Player> noPodiumFinishedPlayers = finishedPlayers.subList(3, finishedPlayers.size());
			noPodiumFinishedPlayers.forEach(player -> {
				//Award the player their reward
				this.plugin.getRewardManager().awardPoints(player.getUniqueId(), noPodiumPoints);
				
				//Tell the player what they got
				player.sendMessage(LanguageHandler.getLangValue("eventMoveToLocationNoPodium")
						.replace("%PLAYER%", player.getDisplayName())
						.replace("%POINTS%", String.valueOf(noPodiumPoints)));
			});
		}
		
		//EventState back to waiting
		scheduler.setEventState(EventState.WAITING);
	}
	
	public Location getFinishLocationFor(UUID uuid) {
		return finishLocations.get(uuid);
	}
	
	public void playerFinished(Player p) {
		if(!finishedPlayers.contains(p)) {
			finishedPlayers.add(p);
			
			//Cancel the particle task
			if(particleTasks.get(p.getUniqueId()) != null) particleTasks.get(p.getUniqueId()).cancel();
			particleTasks.remove(p.getUniqueId());
			
			//Inform the player
			p.sendMessage(LanguageHandler.getLangValue("eventMoveToLocationFinished"));
		}
	}
}
