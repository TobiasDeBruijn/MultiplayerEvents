package nl.thedutchmc.multiplayerevents.commands.interfaces;

import org.bukkit.command.CommandSender;

import nl.thedutchmc.multiplayerevents.MultiplayerEvents;

public interface MeCommandExecutor {

	/**
	 * Get the command's name
	 */
	public String getName();
	
	/**
	 * Get the command's MeTabCompleter
	 */
	public MeCommandTabCompleter getTabCompleter();
	
	/**
	 * Fire the command
	 * @param plugin MultiplayerEvents instance
	 * @param sender The CommandSender
	 * @param args Command arguments
	 * @return
	 */
	public boolean fire(MultiplayerEvents plugin, CommandSender sender, String[] args);
}
