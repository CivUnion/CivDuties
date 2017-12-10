package vg.civcraft.mc.civduties.command;

import vg.civcraft.mc.civduties.command.commands.Duty;
import vg.civcraft.mc.civmodcore.command.CommandHandler;

public class CivDutiesCommandHandler extends CommandHandler{

	@Override
	public void registerCommands() {
		addCommands(new Duty("Duty"));	
	}
	
}
