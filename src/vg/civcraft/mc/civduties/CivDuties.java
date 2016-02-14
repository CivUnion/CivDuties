package vg.civcraft.mc.civduties;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import vg.civcraft.mc.civduties.database.DatabaseManager;
import vg.civcraft.mc.civmodcore.ACivMod;

public class CivDuties extends ACivMod{
	private static CivDuties pluginInstance; 
	
	private static DatabaseManager db;
	private static ConfigManager config;
	private static Set<UUID> onDuty;
	
	public CivDuties(){
		pluginInstance = this;
	}
	
	@Override
	public void onEnable()
	{
	    super.onEnable();
	    db = new DatabaseManager();
	    config = new ConfigManager();
	    onDuty = new HashSet<UUID>();
	}
	
	@Override
	protected String getPluginName() {
		return "CivDuties";
	}
	
	
	public static CivDuties getInstance(){
		return pluginInstance;
	}
	
	public static DatabaseManager getDatabaseManager(){
		return db;
	}
	
	public static ConfigManager getConfigManager(){
		return config;
	}
	
}
