package vg.civcraft.mc.civduties.managers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_10_R1.NBTCompressedStreamTools;
import net.minecraft.server.v1_10_R1.NBTTagCompound;
import net.minecraft.server.v1_10_R1.NBTTagList;
import vg.civcraft.mc.bettershards.BetterShardsAPI;
import vg.civcraft.mc.bettershards.events.PlayerChangeServerReason;
import vg.civcraft.mc.bettershards.misc.PlayerStillDeadException;
import vg.civcraft.mc.civduties.CivDuties;
import vg.civcraft.mc.civduties.database.DatabaseManager;
import vg.civcraft.mc.mercury.MercuryAPI;

public class ModeManager {
	private static DatabaseManager db;
	private static Logger logger;
	
	public ModeManager(){
		db = CivDuties.getDatabaseManager();
		logger = CivDuties.getInstance().getLogger();
	}
	
	public static boolean isInDuty(UUID uuid){
		if(db.loadPlayerData(uuid) != null){
			return true;
		}
		return false;
	}
	
	public static boolean isInDuty(Player player){
		return isInDuty(player.getUniqueId());
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
		
		executeCommands(player, ConfigManager.getCommandsByPlayerOnEnable(), player);
		executeCommands(player, ConfigManager.getCommandsByConsoleOnEnable(), Bukkit.getConsoleSender());
		VaultManager.addPermissionsToPlayer(player, ConfigManager.getTemporaryPermissions());
		VaultManager.addPlayerToGroups(player, ConfigManager.getTemporaryGroups());
		
		player.sendMessage(ChatColor.RED + "You have entered duty mode. Type /duty to leave it");
		logger.log(Level.INFO, "player " + player.getName() + " has entered duty mode");
		return true;
	}
	
	public static boolean disableDutyMode(Player player){
		if(!isInDuty(player)){
			return false;
		}
		
		if(CivDuties.isBetterShardsEnabled() && CivDuties.isMercuryEnabled()){
			String serverName = db.getPlayerServer(player.getUniqueId());
			if(!MercuryAPI.serverName().equals(serverName)){
				MercuryAPI.sendMessage(serverName, "removeFromDuty|"+ player.getUniqueId().toString(), "Duties");
				try {
					BetterShardsAPI.connectPlayer(player, serverName, PlayerChangeServerReason.PLUGIN);
				} catch (PlayerStillDeadException e) {
					return false;
				}
				return true;
			}
		}
		
		ByteArrayInputStream input = db.loadPlayerData(player.getUniqueId());
		try {
			NBTTagCompound nbttagcompound = NBTCompressedStreamTools.a(input);
			//Inform the client the gamemode was changed to fix graphical issues on the client side
			player.setGameMode(getGameModeByVaule(nbttagcompound.getInt("playerGameType")));
			//Teleport the players using the bukkit api to avoid triggering nocheat movement detection
			NBTTagList location = nbttagcompound.getList("Pos", 6);
			player.teleport(new Location(player.getWorld(), location.e(0), location.e(1), location.e(2)));
			CraftPlayer cPlayer = (CraftPlayer) player;
			cPlayer.getHandle().f(nbttagcompound);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		db.removePlayerData(player.getUniqueId());
		
		executeCommands(player, ConfigManager.getCommandsByPlayerOnDisable(), player);
		executeCommands(player, ConfigManager.getCommandsByConsoleOnDisable(), Bukkit.getConsoleSender());
		VaultManager.removePermissionsFromPlayer(player, ConfigManager.getTemporaryPermissions());
		VaultManager.removePlayerFromGroups(player, ConfigManager.getTemporaryGroups());
		
		player.sendMessage(ChatColor.RED + "You have left duty mode");
		logger.log(Level.INFO, "player " + player.getName() + " has left duty mode");
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
	
	private static GameMode getGameModeByVaule(int num){
		switch (num) {
		case 0:
			return GameMode.SURVIVAL;
		case 1:
			return GameMode.CREATIVE;
		case 2:
			return GameMode.ADVENTURE;
		case 3:
			return GameMode.SPECTATOR;
		default:
			return null;
		}
	}
	
}
