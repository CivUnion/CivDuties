package vg.civcraft.mc.civduties.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import vg.civcraft.mc.civduties.managers.ConfigManager;
import vg.civcraft.mc.civduties.managers.ModeManager;
import vg.civcraft.mc.civduties.managers.VaultManager;

public class PlayerListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void playerJoinEvent(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (MercuryListener.shouldRemoveFromDuty(player.getUniqueId())) {
			ModeManager.disableDutyMode(player);
			MercuryListener.removePlayerFromMap(player.getUniqueId());
			return;
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityDeath(EntityDeathEvent event) {
		if (ConfigManager.isDisableDeathDrops() && event.getEntity() instanceof Player
				&& ModeManager.isInDuty(((Player) event.getEntity()))) {
			event.getDrops().clear();
			event.setDroppedExp(0);
		}
	}
	
	
}
