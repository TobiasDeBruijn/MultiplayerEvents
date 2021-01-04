package nl.thedutchmc.multiplayerevents.events.eventmovetolocation;

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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import net.md_5.bungee.api.ChatColor;
import nl.thedutchmc.multiplayerevents.ConfigurationHandler;
import nl.thedutchmc.multiplayerevents.MultiplayerEvents;
import nl.thedutchmc.multiplayerevents.Utils;
import nl.thedutchmc.multiplayerevents.events.EventScheduler;
import nl.thedutchmc.multiplayerevents.events.EventState;
import nl.thedutchmc.multiplayerevents.events.MultiplayerEvent;
import nl.thedutchmc.multiplayerevents.events.eventmovetolocation.listeners.PlayerMoveEventListener;

/**
 * This event will challenge the player to move to a location as fast as possible
 */
@SuppressWarnings("unused") //For unimplemented features
public class EventMoveToLocation implements MultiplayerEvent {

	private MultiplayerEvents plugin;
	private EventScheduler scheduler;
	
	//Config values
	private boolean enableMobs, enableFinishParticles;
	private int distanceLowerBound, distanceUpperBound, finishRadius, durationLowerBound, durationUpperBound;
	private List<String> mobWhitelist;
	
	private final int particleHeight = 5;
	
	private HashMap<UUID, Location> finishLocations = new HashMap<>();
	private HashMap<UUID, BukkitTask> particleTasks = new HashMap<>();
	private List<Player> finishedPlayers = new LinkedList<>();
	
	
	@SuppressWarnings("unchecked")
	public EventMoveToLocation(MultiplayerEvents plugin, EventScheduler scheduler) {
		this.plugin = plugin;
		this.scheduler = scheduler;
		
		//Get configuration values
		ConfigurationHandler config = new ConfigurationHandler(plugin);
		enableMobs = (boolean) config.getConfigOption("eventMoveToLocationEnableMobs");
		enableFinishParticles = (boolean) config.getConfigOption("eventMoveToLocationFinishShowParticles");
		distanceLowerBound = (int) config.getConfigOption("eventMoveToLocationDistanceLowerBound");
		distanceUpperBound = (int) config.getConfigOption("eventMoveToLocationDistanceUpperBound");
		finishRadius = (int) config.getConfigOption("eventMoveToLocationFinishRadius");
		durationLowerBound = (int) config.getConfigOption("eventMoveToLocationDurationLowerBound");
		durationUpperBound = (int) config.getConfigOption("eventMoveToLocationDurationUpperBound");
		
		mobWhitelist = (List<String>) config.getConfigOption("eventMoveToLocationMobWhitelist");
	}
	
	@Override
	public boolean fireEvent() {
		scheduler.setEventState(EventState.RUNNING);
		
		int eventDuration = Utils.getRandomInt(durationLowerBound, durationUpperBound);
		MultiplayerEvents.logInfo("Starting event: MoveToLocation");
		
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
				}.runTaskTimer(plugin, 60L, 30L * 20L); //Spawn the particle every 30 seconds
				
				particleTasks.put(player.getUniqueId(), particleTask);
			}
			
			//Inform the player about this event
			//TODO mutli language
			player.sendMessage(ChatColor.GOLD + "Event starting now: move to " + ChatColor.RED + finishCircleCX + ", " + finishCircleCZ + ChatColor.GOLD + " as fast as possible! You have " + ChatColor.RED + eventDuration + ChatColor.GOLD + " seconds!");
		}
		
		//Register event listeners
		PlayerMoveEventListener pmeListener = new PlayerMoveEventListener(this, finishRadius);
		Bukkit.getPluginManager().registerEvents(pmeListener, plugin);
		
		//TODO spawn monsters
		
		//Schedule a task for the event end
		new BukkitRunnable() {
			
			@Override
			public void run() {
				scheduler.setEventState(EventState.ENDING);
				
				//Cancel all particle tasks
				particleTasks.forEach((uuid, task) -> {
					task.cancel();
				});
				
				//Unregister event listeners
				HandlerList.unregisterAll(pmeListener);
				
				//Inform all players that the event has ended, and tell the fast players
				for(Player p : Bukkit.getOnlinePlayers()) {
					//TODO multi language
					
					p.sendMessage(ChatColor.GOLD + "The event has ended.");
					
					if(finishedPlayers.size() != 0) {
						if(finishedPlayers.size() >= 1) {
							p.sendMessage(ChatColor.GOLD + "- " + ChatColor.RED + finishedPlayers.get(0).getDisplayName() + ChatColor.GOLD + " won first!");
						}
						
						if(finishedPlayers.size() >= 2) {
							p.sendMessage(ChatColor.GOLD + "- " + ChatColor.RED + finishedPlayers.get(1).getDisplayName() + ChatColor.GOLD + " won second!");
						}
						
						if(finishedPlayers.size() >= 3) {
							p.sendMessage(ChatColor.GOLD + "- " + ChatColor.RED + finishedPlayers.get(2).getDisplayName() + ChatColor.GOLD + " won third!");
						}
					} else {
						p.sendMessage(ChatColor.GOLD + "No players finished.");
					}
				}
				
				scheduler.setEventState(EventState.WAITING);
			}
		}.runTaskLater(plugin, eventDuration * 20L);
		
		return true;
	}
	
	public Location getFinishLocationFor(UUID uuid) {
		return finishLocations.get(uuid);
	}
	
	public void playerFinished(Player p) {
		finishedPlayers.add(p);
		
		//Cancel the particle task
		particleTasks.get(p.getUniqueId()).cancel();
		particleTasks.remove(p.getUniqueId());
		
		//Inform the player
		//TODO multi language
		p.sendMessage(ChatColor.GOLD + "You finished!");
	}
}
