package vg.civcraft.mc.civduties.database;

import vg.civcraft.mc.civmodcore.serialization.NBTCompound;

public class PlayerData {
	private NBTCompound data;
	private String serverName;
	private String tierName;
	
	public PlayerData(NBTCompound data, String serverName, String tierName) {
		super();
		this.data = data;
		this.serverName = serverName;
		this.tierName = tierName;
	}

	public NBTCompound getData() {
		return data;
	}

	public String getServerName() {
		return serverName;
	}

	public String getTierName() {
		return tierName;
	}
}
