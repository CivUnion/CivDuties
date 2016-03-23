package vg.civcraft.mc.civduties;

import org.bukkit.Bukkit;

import vg.civcraft.mc.civduties.command.CivDutiesCommandHandler;
import vg.civcraft.mc.civduties.database.DatabaseManager;
import vg.civcraft.mc.civduties.managers.ConfigManager;
import vg.civcraft.mc.civduties.managers.ModeManager;
import vg.civcraft.mc.civmodcore.ACivMod;

public class CivDuties extends ACivMod{
	private static CivDuties pluginInstance;
	private static ConfigManager config;
	private static DatabaseManager db;
	private static ModeManager modeManager;
	
	public CivDuties(){
		pluginInstance = this;
	}
	
	protected String getPluginName() {
	    return "CivDuties";
	}
	
	public void onEnable(){
		super.onEnable();
		config = new ConfigManager(this.getConfig());
		db = new DatabaseManager();
		modeManager = new ModeManager();
		
		CivDutiesCommandHandler commandHandler = new CivDutiesCommandHandler();
		setCommandHandler(commandHandler);
		commandHandler.registerCommands();
	}
	
	public void onDisable(){
		
	}
	
	public static CivDuties getInstance(){
		return pluginInstance;
	}
	
	public static ConfigManager getConfigManager(){
		return config;
	}
	
	public static DatabaseManager getDatabaseManager(){
		return db;
	}
	
	public static ModeManager getModeManager(){
		return modeManager;
	}
	
	public static boolean isMercuryEnabled() {
		return Bukkit.getPluginManager().isPluginEnabled("Mercury");
	}
	
	public static boolean isBetterShardsEnabled() {
		return Bukkit.getPluginManager().isPluginEnabled("BetterShards");
	}
	
}
