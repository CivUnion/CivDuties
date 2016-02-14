package vg.civcraft.mc.civduties.database;

import vg.civcraft.mc.civduties.database.Database;
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
	private CivDuties plugin = CivDuties.getInstance();
	private Config config;
	private Database db;
	
	private Map<UUID, ByteArrayInputStream> playerDataCache = new ConcurrentHashMap<UUID, ByteArrayInputStream>();
	private String addPlayerData, getPlayerData, removePlayerData;
	
	public DatabaseManager(){;
		if (!isValidConnection())
			return;
		loadPreparedStatements();
		executeDatabaseStatements();
	}
	
	@CivConfigs({
		@CivConfig(name = "mysql.host", def = "localhost", type = CivConfigType.String),
		@CivConfig(name = "mysql.port", def = "3306", type = CivConfigType.Int),
		@CivConfig(name = "mysql.username", type = CivConfigType.String),
		@CivConfig(name = "mysql.password", type = CivConfigType.String),
		@CivConfig(name = "mysql.dbname", def = "DutiesDB", type = CivConfigType.String)
	})
	public boolean isValidConnection(){
		String username = config.get("mysql.username").getString();
		String host = config.get("mysql.host").getString();
		int port = config.get("mysql.port").getInt();
		String password = config.get("mysql.password").getString();
		String dbname = config.get("mysql.dbname").getString();
		db = new Database(host, port, dbname, username, password, plugin.getLogger());
		return db.connect();
	}
	
	public boolean isConnected() {
		if (!db.isConnected())
			db.connect();
		return db.isConnected();
	}
	
	private void executeDatabaseStatements() {
		db.execute("create table if not exists DutiesPlayerData("
				+ "uuid varchar(36) not null,"
				+ "entity blob,"
				+ "serverName varchar(256) not null,"
				+ "primary key (uuid));");
	}
	
	private void loadPreparedStatements(){
		addPlayerData = "insert into createPlayerData(uuid, entity, serverName) values(?,?,?) on duplicate key update entity=values(entity), serverName=values(serverName);";
		getPlayerData = "select * from createPlayerData where uuid = ?";
		removePlayerData = "delete from createPlayerData where uuid = ?";
	}
	
	public void savePlayerData(UUID uuid, ByteArrayOutputStream output, String serverName) {
		isConnected();
		playerDataCache.remove(uuid); // So if it is loaded again it is recaught.
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
			} catch (Exception ex) {}
		}
	}
	
	public ByteArrayInputStream loadPlayerData(UUID uuid){
		isConnected();
		// Here we had it caches before hand so no need to load it again.
		if (playerDataCache.containsKey(uuid))
			return playerDataCache.get(uuid);
		PreparedStatement getPlayerData = db.prepareStatement(this.getPlayerData);
		try {
			getPlayerData.setString(1, uuid.toString());
			ResultSet set = getPlayerData.executeQuery();
			if (!set.next())
				return new ByteArrayInputStream(new byte[0]);
			return new ByteArrayInputStream(set.getBytes("entity"));			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				getPlayerData.close();
			} catch (Exception ex) {}
		}
		return new ByteArrayInputStream(new byte[0]);
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
			} catch (Exception ex) {}
		}
	}
}
