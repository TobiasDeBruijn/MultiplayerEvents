package dev.array21.multiplayerevents.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import dev.array21.multiplayerevents.MultiplayerEvents;
import dev.array21.multiplayerevents.commands.interfaces.MeCommandExecutor;
import dev.array21.multiplayerevents.exceptions.NoSuchPluginCommandException;
import dev.array21.multiplayerevents.lang.LanguageHandler;

public class CommandManager implements CommandExecutor, TabCompleter {

	private MultiplayerEvents plugin;
	private CommandRegister commandRegister;
	
	public CommandManager(MultiplayerEvents plugin) {
		this.plugin = plugin;
		this.commandRegister = new CommandRegister(plugin);
		
		commandRegister.discoverPluginCommands();		
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		//mpe ?
		if(args.length == 0) {
			sender.sendMessage(LanguageHandler.getLangValue("notEnoughArguments"));
			return true;
		}
		
		//mpe <subcommand> [?]
		String commandName = args[0];
		try {
			//Get the MeCommandExecutor for the <subcommand>
			MeCommandExecutor pc = this.commandRegister.getPluginCommand(commandName);
			
			//Get the arguments [?]
			String[] commandArgs = Arrays.copyOfRange(args, 1, args.length);
			
			//Fire the command
			return pc.fire(this.plugin, sender, commandArgs);
		} catch(NoSuchPluginCommandException e) {
			//No MeCommandExeuctor was found for <subcommand>
			sender.sendMessage(LanguageHandler.getLangValue("noSuchPluginCommandExecutor"));
			return true;
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		//mpe ?
		if(args.length == 1) {
			//Return all known subcommands
			return new ArrayList<>(this.commandRegister.getAllCommands());
		}
		
		//mpe <subcommand> [?]
		try {
			//Get the MeCommandExecutor
			MeCommandExecutor pc = this.commandRegister.getPluginCommand( args[0]);
			
			//Get the command arguments
			String[] commandArgs = Arrays.copyOfRange(args, 1, args.length);
			
			//Get the possible tab completions for the data we have
			String[] tabCompletions = pc.getTabCompleter().complete(sender, commandArgs);
			
			//Return the tabCompletions. If it's null return an empty ArrayList
			return (tabCompletions != null) ? Arrays.asList(tabCompletions) : new ArrayList<>();
		} catch(NoSuchPluginCommandException e) {
			//No MeCommandExecutor found for <subcommand>, so return an empty ArrayList
			return new ArrayList<>();
		}
	}	
}