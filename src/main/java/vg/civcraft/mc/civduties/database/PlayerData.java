package vg.civcraft.mc.civduties.database;

import java.io.ByteArrayInputStream;

public class PlayerData {
	private ByteArrayInputStream data;
	private String serverName;
	private String tierName;
	
	public PlayerData(ByteArrayInputStream data, String serverName, String tierName) {
		super();
		this.data = data;
		this.serverName = serverName;
		this.tierName = tierName;
	}

	public ByteArrayInputStream getData() {
		return data;
	}

	public String getServerName() {
		return serverName;
	}

	public String getTierName() {
		return tierName;
	}
}
