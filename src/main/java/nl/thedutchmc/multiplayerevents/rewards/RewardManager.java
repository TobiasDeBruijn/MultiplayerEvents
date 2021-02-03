package nl.thedutchmc.multiplayerevents.rewards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import nl.thedutchmc.multiplayerevents.annotations.Nullable;

public class RewardManager {

	private HashMap<UUID, Long> points = new HashMap<>();
	
	/**
	 * Award points to a player
	 * @param playerUuid The UUID of the player
	 * @param points The amount of points to award
	 */
	public void awardPoints(UUID playerUuid, int points) {
		this.points.merge(playerUuid, (long) points, Long::sum);
	}
	
	/**
	 * Get the amount of points a player has
	 * @param playerUuid The UUID of the player
	 */
	@Nullable
	public Long getPoints(UUID playerUuid) {
		return this.points.get(playerUuid);
	}
	
	/**
	 * Take points from a player<br>
	 * If points is higher than the current amount of points that the player has, the outcome will be 0.<br>
	 * If the player currently has no entry, it will stay that way
	 * @param playerUuid The UUID of the player
	 * @param points The amount of points to take
	 */
	public void removePoints(UUID playerUuid, int points) {
		if(!this.points.containsKey(playerUuid)) {
			return;
		}
		
		this.points.merge(playerUuid, (long) points, (a, b) -> (a - b > 0) ? a - b: 0);
	}
	
	/**
	 * Get the scoreboard of points, sorted by descending value
	 * @return {@link LinkedHashMap}
	 */
	public LinkedHashMap<UUID, Long> getScoreboard(boolean descending) {		
		//Create a comparator to compare the values of the two Entry's
		Comparator<Entry<UUID, Long>> valueComparator = new Comparator<Map.Entry<UUID,Long>>() {
			@Override
			public int compare(Entry<UUID, Long> o1, Entry<UUID, Long> o2) {
				if(descending) {
					return o2.getValue().compareTo(o1.getValue());
				} else {
					return o1.getValue().compareTo(o2.getValue());
				}
			}
		};
		
		//Get the map into a List<Entry<T, T2>> and sort it
		List<Entry<UUID, Long>> listOfEntries = new ArrayList<>(this.points.entrySet());
		Collections.sort(listOfEntries, valueComparator);
		
		//Loop over the List to put it back into a Map
		LinkedHashMap<UUID, Long> pointsSorted = new LinkedHashMap<>();
		for(Entry<UUID, Long> entry : listOfEntries) {
			pointsSorted.put(entry.getKey(), entry.getValue());
		}
		
		return pointsSorted;
	}
}
