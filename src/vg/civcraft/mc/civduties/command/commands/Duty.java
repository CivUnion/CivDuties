package vg.civcraft.mc.civduties.command.commands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import vg.civcraft.mc.civduties.managers.ModeManager;
import vg.civcraft.mc.civmodcore.command.PlayerCommand;

public class Duty extends PlayerCommand{

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
		if(ModeManager.isInDuty(player)){
			ModeManager.disableDutyMode(player);
		} else {
			ModeManager.enableDutyMode(player);
		}
		return true;
	}

	public List<String> tabComplete(CommandSender arg0, String[] arg1) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
