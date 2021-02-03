package nl.thedutchmc.multiplayerevents.commands.interfaces;

import org.bukkit.command.CommandSender;

public interface MeCommandTabCompleter {

	/**
	 * Get a String[] of tab completion options
	 * @param sender The CommandSender
	 * @param args Command arguments
	 */
	public String[] complete(CommandSender sender, String[] args);
}
