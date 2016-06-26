package vg.civcraft.mc.civduties;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_10_R1.NBTCompressedStreamTools;
import net.minecraft.server.v1_10_R1.NBTTagCompound;
import net.minecraft.server.v1_10_R1.NBTTagList;
import vg.civcraft.mc.bettershards.BetterShardsAPI;
import vg.civcraft.mc.bettershards.events.PlayerChangeServerReason;
import vg.civcraft.mc.bettershards.misc.PlayerStillDeadException;
import vg.civcraft.mc.civduties.configuration.Command;
import vg.civcraft.mc.civduties.configuration.Command.Timing;
import vg.civcraft.mc.civduties.configuration.Tier;
import vg.civcraft.mc.civduties.database.DatabaseManager;
import vg.civcraft.mc.civduties.external.VaultManager;
import vg.civcraft.mc.mercury.MercuryAPI;

public class ModeManager {
	private DatabaseManager db;
	private VaultManager vaultManager;
	private Logger logger;
	
	
	public ModeManager(){
		db = CivDuties.getInstance().getDatabaseManager();
		vaultManager = CivDuties.getInstance().getVaultManager();
		logger = CivDuties.getInstance().getLogger();
	}
	
	public boolean isInDuty(UUID uuid){
		if(db.getPlayerData(uuid) != null){
			return true;
		}
		return false;
	}
	
	public boolean isInDuty(Player player){
		return isInDuty(player.getUniqueId());
	}
	
	public boolean enableDutyMode(Player player, Tier tier){
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
		if(CivDuties.getInstance().isMercuryEnabled()){
			serverName = MercuryAPI.serverName();
		}
		
		db.savePlayerData(player.getUniqueId(), output, serverName, tier.getName());
		
		vaultManager.addPermissionsToPlayer(player, tier.getTemporaryPermissions());
		vaultManager.addPlayerToGroups(player, tier.getTemporaryGroups());
		
		for(Command command: tier.getCommands()){
			if(command.getTiming() == Timing.ENABLE){
				command.execute(player);
			}
		}
		
		player.sendMessage(ChatColor.RED + "You have entered duty mode. Type /duty to leave it");
		logger.log(Level.INFO, "player " + player.getName() + " has entered duty mode");
		return true;
	}
	
	public boolean disableDutyMode(Player player, Tier tier){
		if(!isInDuty(player)){
			return false;
		}
		
		if(CivDuties.getInstance().isBetterShardsEnabled() && CivDuties.getInstance().isMercuryEnabled()){
			String serverName = db.getPlayerData(player.getUniqueId()).getServerName();
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
		
		ByteArrayInputStream input = db.getPlayerData(player.getUniqueId()).getData();
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
		
		for(Command command: tier.getCommands()){
			if(command.getTiming() == Timing.DISABLE){
				command.execute(player);
			}
		}
		
		vaultManager.removePermissionsFromPlayer(player, tier.getTemporaryPermissions());
		vaultManager.removePlayerFromGroups(player, tier.getTemporaryGroups());
		
		player.sendMessage(ChatColor.RED + "You have left duty mode");
		logger.log(Level.INFO, "player " + player.getName() + " has left duty mode");
		return true;
	}
	
	private GameMode getGameModeByVaule(int num){
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
