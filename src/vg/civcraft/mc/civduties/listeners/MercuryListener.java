package vg.civcraft.mc.civduties.listeners;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import vg.civcraft.mc.civduties.CivDuties;
import vg.civcraft.mc.civduties.database.DatabaseManager;
import vg.civcraft.mc.mercury.events.AsyncPluginBroadcastMessageEvent;

public class MercuryListener implements Listener{
	private static HashMap<UUID, Long> playersToRemoveFromDuty = new HashMap<UUID, Long>();
	
	@EventHandler
	public void asyncPluginBroadcastMessageEvent(AsyncPluginBroadcastMessageEvent e) {
		if (!e.getChannel().equals("Duties")) {
			return;
		}
		String[] data = e.getMessage().split("\\|");
		if (data[0].equals("removeFromDuty")) {
			UUID uuid = UUID.fromString(data[1]);
			playersToRemoveFromDuty.put(uuid, System.currentTimeMillis());
		}
		
		if(data[0].equals("cache")){
			if(data[1].equals("clear")){
				UUID uuid = UUID.fromString(data[2]);
				if(uuid != null){
					CivDuties.getInstance().getDatabaseManager().clearCache(uuid);
				}
			}
		}
	}
	
	public static boolean shouldRemoveFromDuty(UUID uuid){
		return playersToRemoveFromDuty.containsKey(uuid);
	}
	
	public static void removePlayerFromMap(UUID uuid){
		playersToRemoveFromDuty.remove(uuid);
	}
}
