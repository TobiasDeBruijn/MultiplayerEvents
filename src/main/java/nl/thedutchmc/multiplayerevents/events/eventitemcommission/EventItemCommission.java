package nl.thedutchmc.multiplayerevents.events.eventitemcommission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import nl.thedutchmc.multiplayerevents.ConfigurationHandler;
import nl.thedutchmc.multiplayerevents.MultiplayerEvents;
import nl.thedutchmc.multiplayerevents.Utils;
import nl.thedutchmc.multiplayerevents.events.EventScheduler;
import nl.thedutchmc.multiplayerevents.events.EventState;
import nl.thedutchmc.multiplayerevents.events.MultiplayerEvent;
import nl.thedutchmc.multiplayerevents.events.eventitemcommission.listeners.InventoryCloseEventListener;
import nl.thedutchmc.multiplayerevents.events.eventitemcommission.listeners.InventoryOpenEventListener;
import nl.thedutchmc.multiplayerevents.lang.LanguageHandler;

public class EventItemCommission implements MultiplayerEvent {

	private MultiplayerEvents plugin;
	private EventScheduler scheduler;
	
	//Config values
	private Location collectionChestLocation;
	private int itemCountLowerBound, itemCountUpperBound, durationLowerBound, durationUpperBound;
	private List<Material> allowedItems = new ArrayList<>();
	
	private List<ItemStack> collectionChestContents = new ArrayList<>();
	private List<ItemStack> targetChestContents = new ArrayList<>();
	private List<BukkitTask> bukkitTasks = new ArrayList<>();
	private List<Listener> listeners = new ArrayList<>();
	
	@SuppressWarnings("unchecked")	
	public EventItemCommission(MultiplayerEvents plugin) {
		this.plugin = plugin;
		this.scheduler = plugin.getEventScheduler();

		//Read the configuration
		ConfigurationHandler config = new ConfigurationHandler(plugin);
		
		//Parse the worldname
		String worldName = (String) config.getConfigOption("eventItemCommissionCollectionChestWorldName");
		World w = Bukkit.getWorld(worldName);
		
		//if the world (w) is null, the name given is of a nonexistent world
		if(w == null) {
			MultiplayerEvents.logWarn("World " + worldName + " was not found! Check your config.yml!");
			return;
		}
		
		//Get the coordinates of the collection chest.
		int x = (int) config.getConfigOption("eventItemCommissionCollectionChestX");
		int y = (int) config.getConfigOption("eventItemCommissionCollectionChestY");
		int z = (int) config.getConfigOption("eventItemCommissionCollectionChestZ");
		
		collectionChestLocation = new Location(w, x, y, z);
	
		//Get the item count bounds
		itemCountLowerBound = (int) config.getConfigOption("eventItemCommissionItemCountLowerBound");
		itemCountUpperBound = (int) config.getConfigOption("eventItemCommissionItemCountUpperBound");
		
		//Iterate over all Materials to get a List of Materials that matches the filter provided by the user
		List<String> strAllowedItems = (List<String>) config.getConfigOption("eventItemCommissionItemWhitelist");
		for(Material m : Material.values()) {
			if(strAllowedItems.contains(m.name().toLowerCase())) allowedItems.add(m);
		}
						
		//Get the event duration bounds
		durationLowerBound = (int) config.getConfigOption("eventItemCommissionDurationLowerBound");
		durationUpperBound = (int) config.getConfigOption("eventItemCommissionDurationUpperBound");
	}
	
	@Override
	public boolean fireEvent() {
		//Log to the console that the event is starting
		MultiplayerEvents.logDebug(LanguageHandler.getLangValue("startingEventLog")
				.replace("%EVENT_NAME%", "ItemCommission"));
		
		//Determine how long this event should last
		int eventDuration = Utils.getRandomInt(durationLowerBound, durationUpperBound);
		
		//Determine how many items are required for this event
		int itemCount = Utils.getRandomInt(itemCountLowerBound, itemCountUpperBound);
		
		//Loop for itemCount amount of times, pick a random Material and pick a random quantity between 1 and 16.
		HashMap<Material, Integer> itemsWithQuantity = new HashMap<>();
		for(int i = 0; i < itemCount; i++) {
			int randomIndex = Utils.getRandomInt(0, allowedItems.size() -1);
			Material m = allowedItems.get(randomIndex);
			int randomQuantity = Utils.getRandomInt(1, 16);
			
			//Merge it in to the itemsWithQuality map, not just 'put' because the Material can occur multiple times and that is fine.
			itemsWithQuantity.merge(m, randomQuantity, Integer::sum);
		}
		
		for(Map.Entry<Material, Integer> entry : itemsWithQuantity.entrySet()) {
			ItemStack is = new ItemStack(entry.getKey(), entry.getValue());
			targetChestContents.add(is);
		}
		
		//Replace the variables from the lang file
		String messageForPlayers = LanguageHandler.getLangValue("eventItemCommissionStarting")
				.replace("%EVENT_DURATION%", String.valueOf(eventDuration))
				.replace("%COLLECTION_X%", String.valueOf(collectionChestLocation.getX()))
				.replace("%COLLECTION_Y%", String.valueOf(collectionChestLocation.getY()))
				.replace("%COLLECTION_Z%", String.valueOf(collectionChestLocation.getZ()));
		
		//This message contains an array, so we gotta pick apart where we should put that
		String items = "";
		char[] messageForPlayersChars = messageForPlayers.toCharArray();
		//Iterate over all the characters currently in the message
		for(int i = 0; i < messageForPlayersChars.length; i++) {
			char c = messageForPlayersChars[i];
			
			//if the current char is a '$' and the next one is a '{', that means we're at the start of the array
			if(c == '$' && messageForPlayersChars[i+1] == '{') {
				
				List<Character> charsInArray = new LinkedList<>();
				boolean endCharExists = false;
				
				//Try to find the ending char '}' of the array
				for(int j = i; j < messageForPlayersChars.length; j++) {
					if(messageForPlayersChars[j] != '}') {
						charsInArray.add(messageForPlayersChars[j]);
					} else {
						endCharExists = true;
						break;
					}
				}
				
				//There's a chance the user messed up formatting, and forgot the '}', if so, tell them
				if(!endCharExists) {
					MultiplayerEvents.logWarn("Formatting error in your language file. Missing '}'");
				}
				
				//We now know the contents of the array, so, fill it in! Also replace the opening and closing with nothing.
				String arrayContents = String.join("", new String(ArrayUtils.toPrimitive(charsInArray.toArray(new Character[0]))));				
				for(Map.Entry<Material, Integer> entry : itemsWithQuantity.entrySet()) {
					items += (arrayContents
							.replace("${", "")
							.replace("}", "")
							.replace("%ITEM_NAME%", Utils.prettyCaptitalizationForMinecraftNames(entry.getKey().toString()))
							.replace("%ITEM_COUNT%", String.valueOf(entry.getValue())));
				}
				
				//We're done, we got the array.
				break;
			}
		}
		
		//Merge the newly made array into the message for players
		//Raw regex: (\${.*})
		Pattern pattern = Pattern.compile("(\\$\\{.*\\})");
		Matcher matcher = pattern.matcher(messageForPlayers);
		messageForPlayers = matcher.replaceAll(items);
		
		//Parse colors
		messageForPlayers = ChatColor.translateAlternateColorCodes('&', messageForPlayers);
		
		//Tell the users
		for(Player p : Bukkit.getOnlinePlayers()) {
			p.sendMessage(messageForPlayers);
		}
		
		//Register listeners
		InventoryOpenEventListener ioeListener = new InventoryOpenEventListener(this);
		Bukkit.getPluginManager().registerEvents(ioeListener, plugin);
		listeners.add(ioeListener);

		InventoryCloseEventListener iceListener = new InventoryCloseEventListener(this);
		Bukkit.getPluginManager().registerEvents(iceListener, plugin);
		listeners.add(iceListener);
		
		//We put this at the end, in case an error occurs during the starting of the MultiplayerEvent
		scheduler.setEventState(EventState.RUNNING);
		
		//Schedule a task for the event end
		BukkitTask endTask = new BukkitRunnable() {
			
			@Override
			public void run() {
				endEvent();
			}
			
		}.runTaskLater(plugin, eventDuration * 20L);
		
		bukkitTasks.add(endTask);
		
		return true;
	}
	
	public Location getCollectionChestLocation() {
		return this.collectionChestLocation;
	}
	
	public void setCollectionChestContents(List<ItemStack> collectionChestContents) {
		this.collectionChestContents = collectionChestContents;
	}
	
	public List<ItemStack> getCollectionChestContents() {
		return this.collectionChestContents;
	}
	
	public List<ItemStack> getTargetChestContents() {
		return this.targetChestContents;
	}
	
	public void finishEvent(Player playerWhoWon) {
		playerWhoWon.sendMessage(LanguageHandler.getLangValue("eventItemCommissionPlayerWon")
				.replace("%POINTS%", "")); //TODO points
		
		for(Player p : Bukkit.getOnlinePlayers()) {
			if(p.equals(playerWhoWon)) continue;
			p.sendMessage(LanguageHandler.getLangValue("eventItemCommissionFinishedNonWinner")
					.replace("%PLAYER%", playerWhoWon.getDisplayName()));
		}
		
		bukkitTasks.forEach(task -> {
			task.cancel();
		});
	}
	
	private void endEvent() {
		scheduler.setEventState(EventState.ENDING);

		MultiplayerEvents.logDebug(LanguageHandler.getLangValue("endingEventLog")
				.replace("%EVENT_NAME%", "ItemCommission"));
		
		//Unregister the event listeners
		listeners.forEach(listener -> {
			HandlerList.unregisterAll(listener);
		});
		
		scheduler.setEventState(EventState.WAITING);
	}
}
