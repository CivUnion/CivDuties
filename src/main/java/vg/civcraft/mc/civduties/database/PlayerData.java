package vg.civcraft.mc.civduties.database;

import net.minecraft.nbt.NBTTagCompound;

public class PlayerData {
	private NBTTagCompound data;
	private String serverName;
	private String tierName;
	
	public PlayerData(NBTTagCompound data, String serverName, String tierName) {
		super();
		this.data = data;
		this.serverName = serverName;
		this.tierName = tierName;
	}

	public NBTTagCompound getData() {
		return data;
	}

	public String getServerName() {
		return serverName;
	}

	public String getTierName() {
		return tierName;
	}
}
