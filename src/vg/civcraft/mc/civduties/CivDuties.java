package vg.civcraft.mc.civduties;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import vg.civcraft.mc.civduties.command.CivDutiesCommandHandler;
import vg.civcraft.mc.civduties.configuration.Command;
import vg.civcraft.mc.civduties.configuration.ConfigManager;
import vg.civcraft.mc.civduties.configuration.Tier;
import vg.civcraft.mc.civduties.configuration.Command.Timing;
import vg.civcraft.mc.civduties.database.DatabaseManager;
import vg.civcraft.mc.civduties.external.VaultManager;
import vg.civcraft.mc.civduties.listeners.MercuryListener;
import vg.civcraft.mc.civduties.listeners.PlayerListener;
import vg.civcraft.mc.civmodcore.ACivMod;
import vg.civcraft.mc.mercury.MercuryAPI;

public class CivDuties extends ACivMod {
	private static CivDuties pluginInstance;
	private ConfigManager config;
	private DatabaseManager db;
	private ModeManager modeManager;
	private VaultManager vaultManager;

	public CivDuties() {
		pluginInstance = this;
	}

	protected String getPluginName() {
		return "CivDuties";
	}

	public void onEnable() {
		super.onEnable();
		config = new ConfigManager(this.getConfig());
		db = new DatabaseManager();
		modeManager = new ModeManager();
		vaultManager = new VaultManager();
		CivDutiesCommandHandler commandHandler = new CivDutiesCommandHandler();
		setCommandHandler(commandHandler);
		commandHandler.registerCommands();
		registerListeners();
	}

	public void onDisable() {
		for(Player player : Bukkit.getOnlinePlayers()){
			if(modeManager.isInDuty(player)){
				Tier tier = config.getTier(db.getPlayerData(player.getUniqueId()).getTierName());
				for(Command command: tier.getCommands()){
					if(command.getTiming() == Timing.LOGOUT){
						command.execute(player);
					}
				}
				vaultManager.addPermissionsToPlayer(player, tier.getTemporaryPermissions());
				vaultManager.addPlayerToGroups(player, tier.getTemporaryGroups());
			}
		}
	}

	public static CivDuties getInstance() {
		return pluginInstance;
	}

	public ConfigManager getConfigManager() {
		return config;
	}

	public DatabaseManager getDatabaseManager() {
		return db;
	}

	public ModeManager getModeManager() {
		return modeManager;
	}

	public VaultManager getVaultManager() {
		return vaultManager;
	}

	public boolean isMercuryEnabled() {
		return Bukkit.getPluginManager().isPluginEnabled("Mercury");
	}

	public boolean isBetterShardsEnabled() {
		return Bukkit.getPluginManager().isPluginEnabled("BetterShards");
	}
	
	public boolean isVaultEnabled() {
		return Bukkit.getPluginManager().isPluginEnabled("Vault");
	}

	public boolean isCombatTagPlusEnabled() {
		return Bukkit.getPluginManager().isPluginEnabled("CombatTagPlus");
	}


	private void registerListeners() {
		getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		if (isMercuryEnabled()) {
			getServer().getPluginManager().registerEvents(new MercuryListener(), this);
		}
		MercuryAPI.addChannels("Duties");
	}
}
