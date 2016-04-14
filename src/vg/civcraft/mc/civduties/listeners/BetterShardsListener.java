package vg.civcraft.mc.civduties.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import vg.civcraft.mc.bettershards.events.PlayerChangeServerEvent;
import vg.civcraft.mc.bettershards.events.PlayerArrivedChangeServerEvent;
import vg.civcraft.mc.civduties.managers.ConfigManager;
import vg.civcraft.mc.civduties.managers.ModeManager;
import vg.civcraft.mc.civduties.managers.VaultManager;

public class BetterShardsListener implements Listener{
	@EventHandler
	public void playerChangeServerEvent(PlayerChangeServerEvent event) {
		Player player = Bukkit.getPlayer(event.getPlayerUUID());
		if (player != null && ModeManager.isInDuty(player)) {
			VaultManager.removePermissionsFromPlayer(player, ConfigManager.getTemporaryPermissions());
			VaultManager.removePlayerFromGroups(player, ConfigManager.getTemporaryGroups());
		}
	}
	
	@EventHandler
	public void PlayerArrivedChangeServerEvent(PlayerArrivedChangeServerEvent event) {
		Player player = event.getPlayer();
		if (ModeManager.isInDuty(player)) {
			VaultManager.addPermissionsToPlayer(player, ConfigManager.getTemporaryPermissions());
			VaultManager.addPlayerToGroups(player, ConfigManager.getTemporaryGroups());
		}
	}
}
