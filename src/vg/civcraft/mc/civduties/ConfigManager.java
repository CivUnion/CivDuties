package vg.civcraft.mc.civduties;

import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
	private CivDuties plugin;
	FileConfiguration config;
	
	private boolean Enabled; 
	
	private List<String> CommandsByPlayer;
	private List<String> CommandsByConsole;
	
	private boolean DisableDeathDrops;
	private boolean DisableKillDrops;
	private boolean DenyDesiredDrops;
	private boolean DenyChestInteracts;
	
	public ConfigManager(){
		plugin = CivDuties.getInstance();
		config = plugin.getConfig();
		plugin.saveDefaultConfig();
	}
	
	public void parseConfig(){
		Enabled = config.getBoolean("Enabled");
		CommandsByPlayer = config.getStringList("Actions.CommandsByPlayer");
		CommandsByConsole = config.getStringList("Actions.CommandsByConsole");
		DisableDeathDrops = config.getBoolean("Actions.DisableDeathDrops");
		DisableKillDrops = config.getBoolean("Actions.DisableKillDrops");
		DenyDesiredDrops = config.getBoolean("Actions.DenyDesiredDrops");
		DenyChestInteracts = config.getBoolean("Actions.DenyChestInteracts");
	}

	public boolean isEnabled() {
		return Enabled;
	}

	public List<String> getCommandsByPlayer() {
		return CommandsByPlayer;
	}

	public List<String> getCommandsByConsole() {
		return CommandsByConsole;
	}

	public boolean isDisableDeathDrops() {
		return DisableDeathDrops;
	}

	public boolean isDisableKillDrops() {
		return DisableKillDrops;
	}

	public boolean isDenyDesiredDrops() {
		return DenyDesiredDrops;
	}

	public boolean isDenyChestInteracts() {
		return DenyChestInteracts;
	}
    
}
