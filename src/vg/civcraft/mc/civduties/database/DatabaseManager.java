package vg.civcraft.mc.civduties.database;

import vg.civcraft.mc.civduties.database.Database;
import vg.civcraft.mc.civduties.managers.ConfigManager;
import vg.civcraft.mc.civmodcore.Config;
import vg.civcraft.mc.civmodcore.annotations.CivConfig;
import vg.civcraft.mc.civmodcore.annotations.CivConfigType;
import vg.civcraft.mc.civmodcore.annotations.CivConfigs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import vg.civcraft.mc.civduties.CivDuties;

public class DatabaseManager {
	private CivDuties plugin;
	private Database db;

	private Map<UUID, ByteArrayInputStream> playersDataCache = new ConcurrentHashMap<UUID, ByteArrayInputStream>();
	private String addPlayerData, getPlayerData, removePlayerData, getPlayerServer;

	public DatabaseManager() {
		plugin = CivDuties.getInstance();
		if (!isValidConnection())
			return;
		loadPreparedStatements();
		executeDatabaseStatements();
	}

	public boolean isValidConnection() {
		String host = ConfigManager.getHostName();
		int port = ConfigManager.getPort();
		String dbname = ConfigManager.getDBName();
		String username = ConfigManager.getUserName();
		String password = ConfigManager.getPassword();
		db = new Database(host, port, dbname, username, password, plugin.getLogger());
		return db.connect();
	}

	public boolean isConnected() {
		if (!db.isConnected())
			db.connect();
		return db.isConnected();
	}

	private void executeDatabaseStatements() {
		db.execute("create table if not exists DutiesPlayerData(" + "uuid varchar(36) not null," + "entity blob,"
				+ "serverName varchar(256) not null," + "primary key (uuid));");
	}

	private void loadPreparedStatements() {
		addPlayerData = "insert into DutiesPlayerData(uuid, entity, serverName) values(?,?,?) on duplicate key update entity=values(entity), serverName=values(serverName);";
		getPlayerData = "select entity from DutiesPlayerData where uuid = ?";
		removePlayerData = "delete from DutiesPlayerData where uuid = ?";
		getPlayerServer = "select serverName from DutiesPlayerData where uuid = ?";
	}

	public void savePlayerData(UUID uuid, ByteArrayOutputStream output, String serverName) {
		isConnected();
		playersDataCache.remove(uuid); // So if it is loaded again it is recaught
		PreparedStatement addPlayerData = db.prepareStatement(this.addPlayerData);
		try {
			addPlayerData.setString(1, uuid.toString());
			addPlayerData.setBytes(2, output.toByteArray());
			addPlayerData.setString(3, serverName);
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

	public ByteArrayInputStream loadPlayerData(UUID uuid) {
		isConnected();
		// Here we had it caches before hand so no need to load it again.
		if (playersDataCache.containsKey(uuid))
			return playersDataCache.get(uuid);
		PreparedStatement getPlayerData = db.prepareStatement(this.getPlayerData);
		try {
			getPlayerData.setString(1, uuid.toString());
			ResultSet set = getPlayerData.executeQuery();
			if (set.next())
				return new ByteArrayInputStream(set.getBytes("entity"));
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

	public String getPlayerServer(UUID uuid) {
		isConnected();
		PreparedStatement getPlayerServer = db.prepareStatement(this.getPlayerServer);
		try {
			getPlayerServer.setString(1, uuid.toString());
			ResultSet set = getPlayerServer.executeQuery();
			if (set.next())
				return set.getString("serverName");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				getPlayerServer.close();
			} catch (Exception ex) {
			}
		}
		return null;
	}
}
