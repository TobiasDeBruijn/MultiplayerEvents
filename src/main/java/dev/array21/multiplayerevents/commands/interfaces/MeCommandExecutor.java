package dev.array21.multiplayerevents.commands.interfaces;

import org.bukkit.command.CommandSender;

import dev.array21.multiplayerevents.MultiplayerEvents;

public interface MeCommandExecutor {

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
