package nl.thedutchmc.multiplayerevents.events.eventmobkill;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.ChatColor;
import nl.thedutchmc.multiplayerevents.ConfigurationHandler;
import nl.thedutchmc.multiplayerevents.MultiplayerEvents;
import nl.thedutchmc.multiplayerevents.Utils;
import nl.thedutchmc.multiplayerevents.eventmanager.MultiplayerEvent;
import nl.thedutchmc.multiplayerevents.events.eventmobkill.listeners.EntityDamageByEntityEventListener;

public class EventMobKill implements MultiplayerEvent {

	private boolean onlyHostile;
	private int mobKillCountLowerBound, mobKillCountUpperBound;
	private MultiplayerEvents plugin;
	public HashMap<UUID, Integer> scoreCount = new HashMap<>();

	public EventMobKill(MultiplayerEvents plugin) {		
		this.plugin = plugin;
		
		ConfigurationHandler config = new ConfigurationHandler(plugin);
		
		onlyHostile = (boolean) config.getConfigOption("eventMobKillHostileOnly");
		mobKillCountLowerBound = (int) config.getConfigOption("eventMobKillCountLowerBound");
		mobKillCountUpperBound = (int) config.getConfigOption("eventMobKillCountUpperBound");
	}
	
	@Override
	public boolean fireEvent() {
		List<EntityType> entityTypes = Arrays.asList(EntityType.values());
		
		List<EntityType> filteredEntityTypes = new ArrayList<>();
		for(EntityType entityType : entityTypes) {
			
			if(entityType.equals(EntityType.UNKNOWN)) continue;
			
			if(onlyHostile && Monster.class.isAssignableFrom(entityType.getEntityClass())) {
				//TODO check exclusion list
				filteredEntityTypes.add(entityType);
			} else if(LivingEntity.class.isAssignableFrom(entityType.getEntityClass()) && !Player.class.isAssignableFrom(entityType.getEntityClass())) {
				//TODO check exclusion list
				filteredEntityTypes.add(entityType);
			}
		}
		
		int randomIndex = Utils.getRandomInt(0, filteredEntityTypes.size());
		EntityType chosenType = filteredEntityTypes.get(randomIndex);
		
		int count = Utils.getRandomInt(mobKillCountLowerBound, mobKillCountUpperBound);
		
		//Register required event listeners for this MultiplayerEvent
		
		EntityDamageByEntityEventListener edbeListener = new EntityDamageByEntityEventListener(chosenType, this);
		Bukkit.getPluginManager().registerEvents(edbeListener, plugin);
		
		String[] typeNameWords = chosenType.toString().toLowerCase().split("_");
		String[] newTypeNameWords = new String[typeNameWords.length];
		for(int i = 0; i < typeNameWords.length; i++) {
			newTypeNameWords[i] = WordUtils.capitalize(typeNameWords[i]);
		}
		
		String typeName = String.join(" ", newTypeNameWords);
		
		if(count > 1) {
			if(typeName.matches("[a,o,u,i,e]$/m")) {
				typeName += "'s";
			} else {
				typeName += "s";
			}
		}
		
		//TODO multi language support
		for(Player p : Bukkit.getOnlinePlayers()) {
			//TODO Fix capitalization for chosenType name
			p.sendMessage(ChatColor.GOLD + "Event starting now: Kill " + ChatColor.RED + count + " " + typeName + ChatColor.GOLD + " within 5 minutes!");
		}
		
		MultiplayerEvents.logInfo("Event starting now: Kill " + count + " " + typeName + " within 5 minutes!");
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				Bukkit.getOnlinePlayers().forEach(player -> {
					
					//TODO temporary
					if(scoreCount.containsKey(player.getUniqueId())) {
						player.sendMessage("Event over. Your score is: " + scoreCount.get(player.getUniqueId()));
					} else {
						player.sendMessage("Event over. You have no score!");
					}
				});
			}
		}.runTaskLater(plugin, 5*60*20);
		
		return true;
	}
	
}
