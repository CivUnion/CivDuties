package vg.civcraft.mc.civduties.command.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.minelink.ctplus.CombatTagPlus;
import vg.civcraft.mc.civduties.CivDuties;
import vg.civcraft.mc.civduties.ModeManager;
import vg.civcraft.mc.civduties.configuration.Tier;
import vg.civcraft.mc.civmodcore.command.PlayerCommand;

public class Duty extends PlayerCommand{
	private ModeManager modeManager = CivDuties.getInstance().getModeManager();

	public Duty(String name) {
		super(name);
		setIdentifier("duty");
		setDescription("Allow you to enter duty mode");
		setUsage("/duty");
		setArguments(0, 2);
	}

	public boolean execute(CommandSender sender, String[] args) {
		if(!(sender instanceof Player)){
			sender.sendMessage("No.");
			return true;
		}
		Player player = (Player) sender;
		Tier tier = null;
		
		if(args.length < 1){
			tier = CivDuties.getInstance().getConfigManager().getTier(player);
		} else {
			tier = CivDuties.getInstance().getConfigManager().getTier(args[0]);
		}
		
		if(tier == null){
			player.sendMessage("You don't have permission to execute this command.");
			return true;
		}
		
		if (CivDuties.getInstance().isCombatTagPlusEnabled() && tier.isCombattagBlock()
				&& ((CombatTagPlus) Bukkit.getPluginManager().getPlugin("CombatTagPlus")).getTagManager()
						.isTagged(player.getUniqueId())) {
			player.sendMessage("You can't enter duty mode while combat tagged");
			return true;
		}
			
		if(modeManager.isInDuty(player)){
			modeManager.disableDutyMode(player, tier);
			player.sendMessage(ChatColor.RED + "You have entered duty mode");
		} else {
			modeManager.enableDutyMode(player, tier);
			player.sendMessage(ChatColor.RED + "You have left duty mode");
		}
		return true;
	}

	public List<String> tabComplete(CommandSender arg0, String[] arg1) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
