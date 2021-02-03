package nl.thedutchmc.multiplayerevents.commands.executors.rewardcommand;

import org.bukkit.command.CommandSender;

import nl.thedutchmc.multiplayerevents.commands.interfaces.MeCommandTabCompleter;
import nl.thedutchmc.multiplayerevents.utils.TabUtils;

public class RewardCommandTabCompleter implements MeCommandTabCompleter {

	@Override
	public String[] complete(CommandSender sender, String[] args) {		
		
		//mpe reward ?
		if(args.length == 1) {
			return new String[] {
					(sender.hasPermission("mpe.reward.award") ? "award" : ""),
					(sender.hasPermission("mpe.reward.take") ? "take" : ""),
					(sender.hasPermission("mpe.reward.scoreboard") ? "scoreboard" : "")
			};
		}
		
		if(args.length == 2) {
			
			//mpe reward award ?
			if(args[0].equalsIgnoreCase("award") && sender.hasPermission("mpe.reward.award")) {
				return TabUtils.getOnlinePlayers();
			}

			//mpe reward take ?
			if(args[0].equalsIgnoreCase("take") && sender.hasPermission("mpe.reward.take")) {
				return TabUtils.getOnlinePlayers();
			}
			
			if(args[0].equalsIgnoreCase("scoreboard") && sender.hasPermission("mpe.reward.scoreboard")) {
				return new String[] { "[descending true/false]"};
			}
		}
		
		if(args.length == 3) {
			//mpe reward award <player> ?
			if(args[0].equalsIgnoreCase("award") && sender.hasPermission("mpe.reward.award")) {
				return new String[] {
						"<number>"
				};
			}
			
			//mpe reward take <player> ?
			if(args[0].equalsIgnoreCase("take") && sender.hasPermission("mpe.reward.take")) {
				return new String[] {
						"<number>"
				};
			}
		}
		
		return null;
	}	
}
