package vg.civcraft.mc.civduties.configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import vg.civcraft.mc.civduties.CivDuties;
import vg.civcraft.mc.civduties.configuration.Command.Executor;
import vg.civcraft.mc.civduties.configuration.Command.Timing;

public class ConfigManager {
	private FileConfiguration config;
	private String hostName;
	private int port;
	private String dbName;
	private String userName;
	private String password;
	private List<Tier> tiers;
	
	public ConfigManager(FileConfiguration config){
		this.config = config;
		createConfig();
		praseConfig();
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
	
	private void praseConfig(){
		hostName = config.getString("mysql.hostname");
		port = config.getInt("mysql.port");
		dbName = config.getString("mysql.dbname");
		userName = config.getString("mysql.username");
		password = config.getString("mysql.password");
		praseTiers(config.getConfigurationSection("tiers"));
	}
	
	private void praseTiers(ConfigurationSection config){
		tiers = new ArrayList<Tier>();
		for (String key : config.getKeys(false)) {
			if (config.getConfigurationSection(key) == null) {
				CivDuties.getInstance().warning("Found invalid section that should not exist at " + config.getCurrentPath() + key);
				continue;
			}
			Tier tier = praseTier(key, config.getConfigurationSection(key));
			if (tier == null) {
				CivDuties.getInstance().warning(String.format("Tier %s unable to be added.", key));
			} else {
				tiers.add(tier);
			}
		}
	}
	
	private Tier praseTier(String name, ConfigurationSection config){
		String permission = config.getString("permission");
		int priority = config.getInt("priority");
		List<Command> commands = praseCommands(config.getConfigurationSection("commands"));
		Map<String, Boolean> temporaryPermissions = new HashMap<String, Boolean>();
		for(String temporaryPermission : config.getStringList("temporary.permissions")){
			String[] array = temporaryPermission.split(":");
			if(array.length > 1){
				temporaryPermissions.put(array[0], Boolean.valueOf(array[1]));
				continue;
			}
			temporaryPermissions.put(array[0], true);
		}
		List<String> temporaryGroups = config.getStringList("temporary.groups");
		boolean deathDrops = config.getBoolean("disable_death_drops");
		boolean combattagBlock = config.getBoolean("enable_combattag_block");
		return new Tier(name, priority, permission, commands, temporaryPermissions, temporaryGroups, deathDrops, combattagBlock);
	}
	
	private List<Command> praseCommands(ConfigurationSection config){
		List<Command> commands = new ArrayList<>();
		for (String key : config.getKeys(false)) {
			if (config.getConfigurationSection(key) == null) {
				CivDuties.getInstance().warning("Found invalid section that should not exist at " + config.getCurrentPath() + key);
				continue;
			}
			Command command = praseCommand(config.getConfigurationSection(key));
			if (command == null) {
				CivDuties.getInstance().warning(String.format("Tier %s unable to be added.", key));
			} else {
				commands.add(command);
			}
		}
		return commands;
	}
	
	private Command praseCommand(ConfigurationSection config){
		String syntax = config.getString("syntax");
		Timing timing = Timing.valueOf(config.getString("timing"));
		Executor executor = Executor.valueOf(config.getString("executor"));
		return new Command(syntax, timing, executor);
	}
	
	public Tier getTier(Player player){
		Tier tier = null;
		int maxPriority = Integer.MIN_VALUE;
		for(Tier t : tiers){
			if((t.getPermission() == null || player.hasPermission(t.getPermission())) && (tier == null || t.getPriority() > maxPriority)){
				tier = t;
				maxPriority = t.getPriority();
			}
		}
		return null;
	}
	
	public Tier getTier(String tierName){
		for(Tier tier : tiers){
			if(tier.getName().equals(tierName)){
				return tier;
			}
		}
		return null;
	}
	
	public String getHostName(){
		return hostName;
	}
	
	public int getPort(){
		return port;
	}
	
	public String getDBName(){
		return dbName;
	}
	
	public String getUserName(){
		return userName;
	}
	
	public String getPassword(){
		return password;
	}
	

}
