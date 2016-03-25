package vg.civcraft.mc.civduties.managers;

import java.io.File;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

import vg.civcraft.mc.civduties.CivDuties;

public class ConfigManager {
	private static FileConfiguration config;

	public ConfigManager(FileConfiguration config){
		ConfigManager.config = config;
		createConfig();
	}
	
	public static String getHostName(){
		return config.getString("mysql.hostname");
	}
	
	public static int getPort(){
		return config.getInt("mysql.port");
	}
	
	public static String getDBName(){
		return config.getString("mysql.dbname");
	}
	
	public static String getUserName(){
		return config.getString("mysql.username");
	}
	
	public static String getPassword(){
		return config.getString("mysql.password");
	}

	
	public static List<String> getCommandsByPlayerOnEnable() {
		return config.getStringList("commands.commands_by_player_OnEnable");
	}

	public static List<String> getCommandsByPlayerOnDisable() {
		return config.getStringList("commands.commands_by_player_onDisable");
	}

	public static List<String> getCommandsByConsoleOnEnable() {
		return config.getStringList("commands.commands_by_console_onEnable");
	}
	
	
	public static List<String> getCommandsByConsoleOnDisable() {
		return config.getStringList("commands.commands_by_console_onDisable");
	}
	
	public static List<String> getTemporaryPermissions() {
		return config.getStringList("permissions.temporary_permissions");
	}
	
	public static List<String> getTemporaryGroups() {
		return config.getStringList("permissions.temporary_groups");
	}
	
	public static boolean isVaultEnabled() {
		return config.getBoolean("vault.permissions");
	}

	public static boolean isDisableDeathDrops() {
		return config.getBoolean("actions.disable_death_drops");
	}
	
	private void createConfig() {
        try {
            if (!CivDuties.getInstance().getDataFolder().exists()) {
            	CivDuties.getInstance().getDataFolder().mkdirs();
            }
            File file = new File(CivDuties.getInstance().getDataFolder(), "config.yml");
            if (!file.exists() || file.length() == 0) {
            	CivDuties.getInstance().getLogger().info("Config.yml not found, creating!");
            	CivDuties.getInstance().saveResource("config.yml", true);
            } else {
                CivDuties.getInstance().getLogger().info("Config.yml found, loading!");
            }
        } catch (Exception e) {
            e.printStackTrace();

        }

    }
}
