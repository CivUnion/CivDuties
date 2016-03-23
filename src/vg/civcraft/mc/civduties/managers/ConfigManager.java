package vg.civcraft.mc.civduties.managers;

import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

import vg.civcraft.mc.civduties.CivDuties;

public class ConfigManager {
	private static FileConfiguration config;

	public ConfigManager(FileConfiguration config){
		this.config = config;
		CivDuties.getInstance().saveDefaultConfig();
		CivDuties.getInstance().reloadConfig();
	}

	public static boolean isEnabled() {
		return config.getBoolean("Enabled");
	}
	
	public static String getHostName(){
		return config.getString("mysql.hostname", "localhost");
	}
	
	public static int getPort(){
		return config.getInt("mysql.port", 3306);
	}
	
	public static String getDBName(){
		return config.getString("mysql.dbname", "bukkit");
	}
	
	public static String getUserName(){
		return config.getString("mysql.username", "bukkit");
	}
	
	public static String getPassword(){
		return config.getString("mysql.password", "");
	}

	
	public static List<String> getCommandsByPlayerOnEnable() {
		return config.getStringList("Commands.CommandsByPlayerOnEnable");
	}

	public static List<String> getCommandsByConsoleOnEnable() {
		return config.getStringList("Commands.CommandsByConsoleOnEnable");
	}
	
	public static List<String> getCommandsByPlayerOnDisable() {
		return config.getStringList("Commands.CommandsByPlayerOnDisable");
	}

	public static List<String> getCommandsByConsoleOnDisable() {
		return config.getStringList("Commands.CommandsByConsoleOnDisable");
	}

	public static boolean isDisableDeathDrops() {
		return config.getBoolean("Actions.DisableDeathDrops");
	}

	public static boolean isDisableKillDrops() {
		return config.getBoolean("Actions.DisableKillDrops");
	}

	public static boolean isDenyDesiredDrops() {
		return config.getBoolean("Actions.DenyDesiredDrops");
	}

	public static boolean isDenyChestInteracts() {
		return config.getBoolean("Actions.DenyChestInteracts");
	}
    
}
