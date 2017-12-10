package vg.civcraft.mc.civduties.database;

import vg.civcraft.mc.civduties.database.Database;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import vg.civcraft.mc.civduties.CivDuties;
import vg.civcraft.mc.civduties.configuration.ConfigManager;

public class DatabaseManager {
	private CivDuties plugin;
	private ConfigManager config;
	private Database db;

	private Map<UUID, PlayerData> playersDataCache = new ConcurrentHashMap<UUID, PlayerData>();
	private String addPlayerData, getPlayerData, removePlayerData;

	public DatabaseManager() {
		plugin = CivDuties.getInstance();
		config = CivDuties.getInstance().getConfigManager();
		if (!isValidConnection())
			return;
		loadPreparedStatements();
		executeDatabaseStatements();
	}

	public boolean isValidConnection() {
		String host = config.getHostName();
		int port = config.getPort();
		String dbname = config.getDBName();
		String username = config.getUserName();
		String password = config.getPassword();
		db = new Database(host, port, dbname, username, password, plugin.getLogger());
		return db.connect();
	}

	public boolean isConnected() {
		if (!db.isConnected())
			db.connect();
		return db.isConnected();
	}

	private void executeDatabaseStatements() {
		db.execute("create table if not exists DutiesPlayerData( " 
				+ "uuid varchar(36) not null," + "entity blob, "
				+ "serverName varchar(256) not null, " 
				+ "tierName varchar(256) not null, " 
				+ "primary key (uuid));");
	}

	private void loadPreparedStatements() {
		addPlayerData = "insert into DutiesPlayerData(uuid, entity, serverName, tierName) values(?,?,?,?) "
						+ "on duplicate key update entity=values(entity), serverName=values(serverName), tierName=values(tierName);";
		getPlayerData = "select * from DutiesPlayerData where uuid = ?";
		removePlayerData = "delete from DutiesPlayerData where uuid = ?";
	}

	public void savePlayerData(UUID uuid, ByteArrayOutputStream output, String serverName, String tierName) {
		isConnected();
		playersDataCache.remove(uuid); // So if it is loaded again it is recaught
		PreparedStatement addPlayerData = db.prepareStatement(this.addPlayerData);
		try {
			addPlayerData.setString(1, uuid.toString());
			addPlayerData.setBytes(2, output.toByteArray());
			addPlayerData.setString(3, serverName);
			addPlayerData.setString(4, tierName);
			addPlayerData.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				addPlayerData.close();
			} catch (Exception ex) {
			}
		}
	}

	public PlayerData getPlayerData(UUID uuid) {
		isConnected();
		// Here we had it caches before hand so no need to load it again.
		if (playersDataCache.containsKey(uuid))
			return playersDataCache.get(uuid);
		PreparedStatement getPlayerData = db.prepareStatement(this.getPlayerData);
		try {
			getPlayerData.setString(1, uuid.toString());
			ResultSet set = getPlayerData.executeQuery();
			if (set.next()) {
				PlayerData data = new PlayerData(new ByteArrayInputStream(set.getBytes("entity")),
						set.getString("serverName"), set.getString("tierName"));
				playersDataCache.put(uuid, data);
				return data;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				getPlayerData.close();
			} catch (Exception ex) {
			}
		}
		return null;
	}

	public void removePlayerData(UUID uuid) {
		isConnected();
		PreparedStatement removePlayerData = db.prepareStatement(this.removePlayerData);
		try {
			removePlayerData.setString(1, uuid.toString());
			removePlayerData.execute();
			playersDataCache.remove(uuid);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				removePlayerData.close();
			} catch (Exception ex) {
			}
		}
	}
	
	public void clearCache(UUID uuid){
		playersDataCache.remove(uuid);
	}
}
