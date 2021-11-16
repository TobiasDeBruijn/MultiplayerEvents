package dev.array21.multiplayerevents.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TabUtils {

	/**
	 * Get a String[] of all online players name's
	 */
	public static String[] getOnlinePlayers() {
		List<String> onlinePlayers = new ArrayList<>();
		for(Player p : Bukkit.getOnlinePlayers()) {
			onlinePlayers.add(p.getName());
		}
		
		return onlinePlayers.toArray(new String[0]);
	}
}
