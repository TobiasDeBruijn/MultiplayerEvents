package dev.array21.multiplayerevents.commands.executors.rewardcommand;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dev.array21.multiplayerevents.MultiplayerEvents;
import dev.array21.multiplayerevents.annotations.CommandInfo;
import dev.array21.multiplayerevents.annotations.RegisterMeCommandExecutor;
import dev.array21.multiplayerevents.commands.interfaces.MeCommandExecutor;
import dev.array21.multiplayerevents.commands.interfaces.MeCommandTabCompleter;
import dev.array21.multiplayerevents.lang.LanguageHandler;
import dev.array21.multiplayerevents.utils.Utils;

@RegisterMeCommandExecutor()
@CommandInfo(name = "reward")
public class RewardCommandExecutor implements MeCommandExecutor {
	
	@Override
	public MeCommandTabCompleter getTabCompleter() {
		return new RewardCommandTabCompleter();
	}

	@Override
	public boolean fire(MultiplayerEvents plugin, CommandSender sender, String[] args) {
		if(!sender.hasPermission("mpe.reward")) {
			sender.sendMessage(LanguageHandler.getLangValue("commandNoPermission"));
			return true;
		}
		
		if(args.length > 0) {
			
			//mpe reward ?
			if(args[0].equalsIgnoreCase("award")) {
				//Check sender permissions
				if(!sender.hasPermission("mp.reward.award")) {
					sender.sendMessage(LanguageHandler.getLangValue("commandNoPermission"));
					return true;
				}
				
				//Check if all args are provided
				//mpe reward award <player> <number>
				if(args.length < 3) {
					sender.sendMessage(LanguageHandler.getLangValue("notEnoughArguments"));
					return true;
				}
				
				//Get <player>
				Player p = Bukkit.getPlayer(args[1]);
				
				//Verify that a player exists with the provided name
				if(p == null) {
					sender.sendMessage(LanguageHandler.getLangValue("commandsPlayerNotFound")
							.replace("%PLAYER_NAME%", args[1]));
					return true;
				}
				
				//Verify <number> is a positive integer
				if(!Utils.verifyPositiveInteger(args[2])) {
					sender.sendMessage(LanguageHandler.getLangValue("commandsNotAPositiveInteger"));
					return true;
				}
				
				//Award the points
				int points = Integer.valueOf(args[2]);
				plugin.getRewardManager().awardPoints(p.getUniqueId(), points);
				
				//Tell the player what we did
				sender.sendMessage(LanguageHandler.getLangValue("commandRewardAwardPoints")
						.replace("%POINTS%", args[2])
						.replace("%PLAYER_NAME%", args[1]));
				
				return true;
			}
			
			//mpe reward take <player> <number>
			if(args[0].equalsIgnoreCase("take")) {
				//Check sender permissions
				if(!sender.hasPermission("mp.reward.take")) {
					sender.sendMessage(LanguageHandler.getLangValue("commandNoPermission"));
					return true;
				}
				
				//Check if all args are provided
				//mpe reward award <player> <number>
				if(args.length < 3) {
					sender.sendMessage(LanguageHandler.getLangValue("notEnoughArguments"));
					return true;
				}
				
				//Get <player>
				Player p = Bukkit.getPlayer(args[1]);
				
				//Verify that a player exists with the provided name
				if(p == null) {
					sender.sendMessage(LanguageHandler.getLangValue("commandsPlayerNotFound")
							.replace("%PLAYER_NAME%", args[1]));
					return true;
				}
				
				//Verify <number> is a positive integer
				if(!Utils.verifyPositiveInteger(args[2])) {
					sender.sendMessage(LanguageHandler.getLangValue("commandsNotAPositiveInteger"));
					return true;
				}
				
				//Award the points
				int points = Integer.valueOf(args[2]);
				plugin.getRewardManager().removePoints(p.getUniqueId(), points);
				
				//Tell the player what we did
				sender.sendMessage(LanguageHandler.getLangValue("commandRewardTakenPoints")
						.replace("%POINTS%", args[2])
						.replace("%PLAYER_NAME%", args[1]));
				
				return true;
			}
			
			//mpe reward leaderboard
			if(args[0].equalsIgnoreCase("scoreboard")) {
				boolean descending = true;
				
				if(args.length >= 2) {
					if(args[1].equalsIgnoreCase("false")) descending = false;
				}
				
				sender.sendMessage(LanguageHandler.getLangValue("commandRewardScoreboardInitial")
						.replace("%ORDER%", descending ? "descending" : "ascending"));
				
				LinkedHashMap<UUID, Long> scoreboard = plugin.getRewardManager().getScoreboard(descending);
				for(Map.Entry<UUID, Long> entry : scoreboard.entrySet()) {
					sender.sendMessage(LanguageHandler.getLangValue("commandRewardScoreboardFormat")
							.replace("%PLAYER_NAME%", Bukkit.getPlayer(entry.getKey()).getName())
							.replace("%POINTS%", String.valueOf(entry.getValue())));
				}
			}
			
			return true;
		}
		
		if(!(sender instanceof Player)) {
			sender.sendMessage("This command can only be used by players!");
			return true;
		}
		
		Long points = plugin.getRewardManager().getPoints(((Player) sender).getUniqueId());
		if(points == null) {
			sender.sendMessage(LanguageHandler.getLangValue("commandRewardNoPoints"));
			return true;
		}
		
		sender.sendMessage(LanguageHandler.getLangValue("commandRewardPoints")
				.replace("%POINTS%", String.valueOf(points)));
		
		return true;
	}
}
