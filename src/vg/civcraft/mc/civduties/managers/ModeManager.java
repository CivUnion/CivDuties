package vg.civcraft.mc.civduties.managers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import net.minecraft.server.v1_8_R3.EntityTracker;
import net.minecraft.server.v1_8_R3.EntityTrackerEntry;
import net.minecraft.server.v1_8_R3.NBTCompressedStreamTools;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.WorldServer;
import vg.civcraft.mc.civduties.CivDuties;
import vg.civcraft.mc.civduties.database.DatabaseManager;
import vg.civcraft.mc.mercury.MercuryAPI;

public class ModeManager {
	private static HashSet<UUID> playersInDuty = new HashSet<UUID>();
	private static HashMap<UUID, PermissionAttachment> perms = new HashMap<UUID, PermissionAttachment>();
	private static DatabaseManager db;
	
	public ModeManager(){
		db = CivDuties.getDatabaseManager();
	}
	
	public static boolean enableDutyMode(Player player){
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		CraftPlayer cPlayer = (CraftPlayer) player;
		cPlayer.getHandle().e(nbttagcompound);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			NBTCompressedStreamTools.a(nbttagcompound, output);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		String serverName = Bukkit.getServerName();
		if(CivDuties.isMercuryEnabled()){
			serverName = MercuryAPI.serverName();
		}
		db.savePlayerData(player.getUniqueId(), output, serverName);
		playersInDuty.add(player.getUniqueId());
		
		executeCommands(player, ConfigManager.getCommandsByPlayerOnEnable(), player);
		executeCommands(player, ConfigManager.getCommandsByConsoleOnEnable(), Bukkit.getConsoleSender());
		
		return true;
	}
	
	public static boolean disableDutyMode(Player player){
		if(!playersInDuty.contains(player.getUniqueId())){
			return false;
		}
		ByteArrayInputStream input = db.loadPlayerData(player.getUniqueId());
		try {
			NBTTagCompound nbttagcompound = NBTCompressedStreamTools.a(input);
			CraftPlayer cPlayer = (CraftPlayer) player;
			cPlayer.getHandle().f(nbttagcompound);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		playersInDuty.add(player.getUniqueId());
		db.removePlayerData(player.getUniqueId());
		
		executeCommands(player, ConfigManager.getCommandsByPlayerOnDisable(), player);
		executeCommands(player, ConfigManager.getCommandsByConsoleOnDisable(), Bukkit.getConsoleSender());
		return true;
	}
	
	private static boolean executeCommands(Player player, List<String> commands, CommandSender sender) {
		if(commands == null || commands.isEmpty()){
			return false;
		}
		
		for(String command: commands){;
			String parsedCommand = (command.charAt(0) == '/' ? command.substring(1) : command)
					.replaceAll("%PLAYER_NAME%", player.getName())
					.replaceAll("%PLAYER_GAMEMODE%", player.getGameMode().toString())
					.replaceAll("%PLAYER_SERVER%", CivDuties.isMercuryEnabled() ? MercuryAPI.serverName() : Bukkit.getServerName());
			Bukkit.dispatchCommand(sender, parsedCommand);
		}
		return true;
	}
	
	private static boolean addTempPermissions(Player player, List<String> permissions){
		if(permissions == null || permissions.isEmpty()){
			return false;
		}
		
		PermissionAttachment attachment = player.addAttachment(CivDuties.getInstance());
		for(String permission : permissions){
			attachment.setPermission(permission, true);
		}
		perms.put(player.getUniqueId(), attachment);
		return true;
	}
	
	private static boolean removeTempPermissions(Player player, List<String> permissions){
		if(permissions == null || permissions.isEmpty() || !perms.containsKey(player.getUniqueId())){
			return false;
		}
		
		PermissionAttachment attachment = perms.get(player.getUniqueId());
		for(String permission : permissions){
			attachment.unsetPermission(permission);
		}
		perms.remove(player.getUniqueId());
		return true;
	}
	
	public static boolean isInDuty(UUID uuid){
		return playersInDuty.contains(uuid); 
	}
	
	public static boolean isInDuty(Player player){
		return isInDuty(player.getUniqueId());
	}
	
}
