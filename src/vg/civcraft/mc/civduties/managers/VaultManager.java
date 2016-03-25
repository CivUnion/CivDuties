package vg.civcraft.mc.civduties.managers;

import java.util.List;
import java.util.logging.Level;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import net.milkbowl.vault.permission.Permission;
import vg.civcraft.mc.civduties.CivDuties;

public class VaultManager {

	private static Permission permissionProvider = null;

	public VaultManager() {
		if (ConfigManager.isVaultEnabled() && CivDuties.isVaultEnabled()) {
			setupPermissions();
			CivDuties.getInstance().getLogger().log(Level.WARNING, "Duties was unable to find a permissions plugin");
		}
	}

	private static boolean setupPermissions() {
		RegisteredServiceProvider<Permission> permissionProvider = CivDuties.getInstance().getServer()
				.getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
		if (permissionProvider != null) {
			VaultManager.permissionProvider = permissionProvider.getProvider();
			return true;
		}
		return false;
	}

	public static boolean addPermissionsToPlayer(Player player, List<String> permissions) {
		if (permissionProvider == null) {
			return false;
		}
		
		for (String perm : permissions) {
			permissionProvider.playerAdd(player, perm);
		}
		return true;
	}
	
	public static boolean addPermissionsToPlayer(Player player, World world, List<String> permissions) {
		if (permissionProvider == null) {
			return false;
		}
		
		for (String perm : permissions) {
			permissionProvider.playerAdd(player, perm);
		}
		return true;
	}
	
	public static boolean removePermissionsFromPlayer(Player player, List<String> permissions) {
		if (permissionProvider == null) {
			return false;
		}
		
		for (String perm : permissions) {
			permissionProvider.playerRemove(player, perm);
		}
		return true;
	}
	
	public static boolean addPlayerToGroups(Player player, List<String> groups){
		if (permissionProvider == null) {
			return false;
		}
		
		for (String group : groups) {
			permissionProvider.playerAddGroup(player, group);
		}
		return true;
	}
	
	public static boolean removePlayerFromGroups(Player player, List<String> groups){
		if (permissionProvider == null) {
			return false;
		}
		
		for (String group : groups) {
			permissionProvider.playerRemoveGroup(player, group);
		}
		return true;
	}
}
