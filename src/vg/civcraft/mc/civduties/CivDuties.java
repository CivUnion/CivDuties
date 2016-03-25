package vg.civcraft.mc.civduties;

import org.bukkit.Bukkit;

import vg.civcraft.mc.bettershards.listeners.BetterShardsListener;
import vg.civcraft.mc.civduties.command.CivDutiesCommandHandler;
import vg.civcraft.mc.civduties.database.DatabaseManager;
import vg.civcraft.mc.civduties.listeners.MercuryListener;
import vg.civcraft.mc.civduties.listeners.PlayerListener;
import vg.civcraft.mc.civduties.managers.ConfigManager;
import vg.civcraft.mc.civduties.managers.ModeManager;
import vg.civcraft.mc.civduties.managers.VaultManager;
import vg.civcraft.mc.civmodcore.ACivMod;
import vg.civcraft.mc.mercury.MercuryAPI;

public class CivDuties extends ACivMod {
	private static CivDuties pluginInstance;
	private static ConfigManager config;
	private static DatabaseManager db;
	private static ModeManager modeManager;
	private static VaultManager vaultManager;

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

	}

	public static CivDuties getInstance() {
		return pluginInstance;
	}

	public static ConfigManager getConfigManager() {
		return config;
	}

	public static DatabaseManager getDatabaseManager() {
		return db;
	}

	public static ModeManager getModeManager() {
		return modeManager;
	}

	public static VaultManager getVaultManager() {
		return vaultManager;
	}

	public static boolean isMercuryEnabled() {
		return Bukkit.getPluginManager().isPluginEnabled("Mercury");
	}

	public static boolean isBetterShardsEnabled() {
		return Bukkit.getPluginManager().isPluginEnabled("BetterShards");
	}
	
	public static boolean isVaultEnabled() {
		return Bukkit.getPluginManager().isPluginEnabled("Vault");
	}


	private void registerListeners() {
		getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		if (isMercuryEnabled()) {
			getServer().getPluginManager().registerEvents(new MercuryListener(), this);
		}
		if (isBetterShardsEnabled()) {
			getServer().getPluginManager().registerEvents(new BetterShardsListener(), this);
		}
		MercuryAPI.addChannels("Duties");
	}
}
