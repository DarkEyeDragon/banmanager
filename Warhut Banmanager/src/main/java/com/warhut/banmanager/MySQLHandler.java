package com.warhut.banmanager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.warhut.banmanager.utils.TimeConverter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;


public class MySQLHandler {

	BanManager plugin;
	
	
	protected String ip;
	private String database;
	private String username;
	private String password;
	private String bans_table;
	private String ipBans_table;
	private String mutes_table;
	//private String warns_table;
	public boolean useDatabase;
	private static Connection connect;
    private ResultSet resultSet;
    public boolean isConnected = false;
    
    
    public String getIpBansTable(){
    	return ipBans_table;
    }
    public String getBansTable(){
    	return bans_table;
    }
    
	public MySQLHandler(BanManager plugin) {
	    this.plugin = plugin;
		ConfigurationSection mysql = plugin.getConfig().getConfigurationSection("mysql");
	    ip = mysql.getString("ip");
	    database = mysql.getString("database");
	    username = mysql.getString("username");
	    password = mysql.getString("password");
	    bans_table = mysql.getString("bans_table");
	    ipBans_table = mysql.getString("ipbans_table");
		mutes_table = mysql.getString(("mutes_table"));
	    useDatabase = mysql.getBoolean("usedatabase");
	}
	
	protected void connectToDatabase(){
    	if(useDatabase){
			Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
				try{
					connect = DriverManager.getConnection("jdbc:mysql://"+ip+"/" + database, username, password);
					Bukkit.getLogger().info("[" + plugin.getDescription().getName() + "] Successfully connected to database(" + database + ")!");
					createBanTable();
					createIpBanTable();
					createMuteTable();
					isConnected = true;
				}catch (SQLException e){
					plugin.getLogger().warning("Unable to connect to database! Banmanager will NOT function properly when not connected.");
					plugin.getLogger().warning(e.getMessage());
				}
			});
		}else{
    		plugin.getLogger().warning("UseDatabase has been set to false. Make sure to set your database information in the config and change 'usedatabase' to true");
		}
	}
	//Adding a player to the ban list will ban them but not kick them.
    public void addToBanlist(UUID uuid, String currentUsername, String bannedBy, long bannedOn, long bannedTill, String reason, boolean permanent){
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			try{
				if((!isInBanList(uuid, bans_table))){
					PreparedStatement preparedStatement = connect.prepareStatement("insert into  "+database+"."+ bans_table +" values (default, ?, ?, ?, ?, ?, ?, ?)");
					preparedStatement.setString(1, uuid.toString());
					preparedStatement.setString(2, currentUsername);
					preparedStatement.setString(3, bannedBy);
					preparedStatement.setDouble(4, bannedOn);
					preparedStatement.setDouble(5, bannedTill);
					preparedStatement.setString(6, reason);
					preparedStatement.setBoolean(7, permanent);
					preparedStatement.execute();
					plugin.getLogger().info("Created ban: "+currentUsername+"("+uuid+ ") Reason: \""+reason + "\" Time: " + TimeConverter.secondsToDate(getRemainingBanTime(uuid)) + " Permanent: "+ isBannedPermanent(uuid, bans_table));
				}else{
					PreparedStatement preparedStatement = connect.prepareStatement("UPDATE "+bans_table+" SET currentusername=? , bannedby=? , bannedon=?, bannedtill=?, reason=?,permanent=? WHERE uuid=?");
					preparedStatement.setString(1, currentUsername);
					preparedStatement.setString(2, bannedBy);
					preparedStatement.setDouble(3, bannedOn);
					preparedStatement.setDouble(4, bannedTill);
					preparedStatement.setString(5, reason);
					preparedStatement.setBoolean(6, permanent);
					preparedStatement.setString(7, uuid.toString());
					preparedStatement.executeUpdate();
					plugin.getLogger().info("Updated ban: "+currentUsername+"("+uuid+ ") Reason: \""+reason + "\" Time: " + TimeConverter.secondsToDate(getRemainingBanTime(uuid)) + " Permanent: "+ isBannedPermanent(uuid, bans_table));
				}
			}catch(SQLException e){
				e.printStackTrace();
				Bukkit.getLogger().warning("Unable to add to or update database.");
			}
		});
    }
    
    public void addToIpBanlist(UUID uuid, String currentUsername, String bannedBy, long bannedOn, String playerIP, String reason){
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			try{
				if((!isInBanList(uuid, ipBans_table))){
					PreparedStatement preparedStatement = connect.prepareStatement("insert into  "+database+"."+ ipBans_table +" values (default, ?, ?, ?, ?, ?, ?)");
					preparedStatement.setString(1, uuid.toString().toLowerCase());
					preparedStatement.setString(2, currentUsername);
					preparedStatement.setString(3, bannedBy);
					preparedStatement.setDouble(4, bannedOn);
					preparedStatement.setString(5, playerIP);
					preparedStatement.setString(6, reason);
					preparedStatement.execute();
					plugin.getLogger().info("Created ip ban: "+currentUsername+"("+uuid+ ") Reason: \""+reason + "\"");
				}else{
					PreparedStatement preparedStatement = connect.prepareStatement("UPDATE "+ipBans_table+" SET currentusername=? , bannedby=? , bannedon=?, ip=?, reason=? WHERE uuid=?");
					preparedStatement.setString(1, currentUsername.toLowerCase());
					preparedStatement.setString(2, bannedBy);
					preparedStatement.setDouble(3, bannedOn);
					preparedStatement.setString(4, playerIP);
					preparedStatement.setString(5, reason);
					preparedStatement.setString(6, uuid.toString());
					preparedStatement.executeUpdate();
					plugin.getLogger().info("Updated ip ban: "+currentUsername+"("+uuid+ ") Reason: \""+reason + "\"");
				}
			}catch(SQLException e){
				e.printStackTrace();
				Bukkit.getLogger().warning("Unable to add to or update database.");
			}
		});
    }
    
    public boolean isInBanList(UUID playerUUID, String tableName) throws SQLException{
    	if(useDatabase){
	    	PreparedStatement preparedStatement = connect.prepareStatement("SELECT uuid FROM "+tableName+" WHERE uuid=?");
	    	preparedStatement.setString(1, playerUUID.toString().toLowerCase());
	    	resultSet = preparedStatement.executeQuery();
			
	    	return resultSet.next();
    	}
    	return false;
    }
    public boolean isBannedPermanent(UUID playerUUID, String tableName) throws SQLException{
    	PreparedStatement preparedStatement = connect.prepareStatement("SELECT permanent FROM "+tableName+" WHERE uuid=?");
    	preparedStatement.setString(1, playerUUID.toString().toLowerCase());
    	resultSet = preparedStatement.executeQuery();
    	while(resultSet.next()){
    		if(resultSet.getInt("permanent") == 1){
    			return true;
    		}else{
    			return false;
    		}
    	}
    	return false;
    }
    public boolean isIpBanned(String playerIP) throws SQLException{
    	PreparedStatement preparedStatement = connect.prepareStatement("SELECT ip FROM "+ipBans_table+" WHERE ip=?");
    	preparedStatement.setString(1, playerIP);
    	resultSet = preparedStatement.executeQuery();
    	if(resultSet.next()){
    		return true;
    	}else{
    		return false;
    	}
    	
    }
    public long getRemainingBanTime(UUID uuid) throws SQLException{
    	PreparedStatement preparedStatement = connect.prepareStatement("SELECT bannedtill FROM "+bans_table+ " WHERE uuid=?");
    	preparedStatement.setString(1, uuid.toString());
    	resultSet = preparedStatement.executeQuery();
    	long bannedTill = 0;
    	while(resultSet.next()){
    		bannedTill = resultSet.getLong("bannedtill");
    	}
    	long currTime = System.currentTimeMillis()/1000;
    	long remainingTime = bannedTill - currTime;
    	if(remainingTime < 0){
    		return 0;
    	}
    	return remainingTime;
    }
    //Removing a player from the ban list allows players to join immediately
    public void removeFromBanList(UUID uuid, String tableName) throws SQLException{
		PreparedStatement preparedStatement = connect.prepareStatement("DELETE FROM "+tableName+" WHERE uuid=?");
		preparedStatement.setString(1, uuid.toString());
		preparedStatement.executeUpdate();
    }
    public List<UUID> getBanList() throws SQLException{
		PreparedStatement preparedStatement = connect.prepareStatement("SELECT currentusername FROM "+bans_table);
		resultSet = preparedStatement.executeQuery();
		//StringBuilder banList = new StringBuilder();
		List<UUID> banList = new ArrayList<UUID>();
		while(resultSet.next()){
			banList.add(UUID.fromString(resultSet.getString(1)));
		}
		return banList;
    }
    public void createBanTable(){
    	try {
			PreparedStatement preparedStatement = connect.prepareStatement("CREATE TABLE IF NOT EXISTS " +bans_table+" ( id MEDIUMINT NOT NULL AUTO_INCREMENT, `uuid` varchar(36) NOT NULL, `currentusername` varchar(16) NOT NULL, `bannedby` varchar(16) NOT NULL, `bannedon` bigint(15) NOT NULL, `bannedtill` bigint(15) NOT NULL, `reason` varchar(265) NOT NULL, `permanent` tinyint(1) NOT NULL, PRIMARY KEY (id)) ENGINE=InnoDB DEFAULT CHARSET=latin1;");
			preparedStatement.executeUpdate();
    	} catch (SQLException e) {
			e.printStackTrace();
			e.getMessage();
		}
    }
    public void createIpBanTable(){
    	try {
			PreparedStatement preparedStatement = connect.prepareStatement("CREATE TABLE IF NOT EXISTS " +ipBans_table+ " ( id MEDIUMINT NOT NULL AUTO_INCREMENT , `uuid` VARCHAR(36) NOT NULL, `currentusername` VARCHAR(16) NOT NULL, `bannedby` VARCHAR(16) NOT NULL, `bannedon` BIGINT(15) NOT NULL , `ip` VARCHAR(16) NOT NULL , `reason` VARCHAR(256) NOT NULL , PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=latin1;");
			preparedStatement.executeUpdate();
    	} catch (SQLException e) {
			e.printStackTrace();
			e.getMessage();
		}
    }
	public void createMuteTable(){
		try {
			PreparedStatement preparedStatement = connect.prepareStatement("CREATE TABLE IF NOT EXISTS " +mutes_table+" ( id MEDIUMINT NOT NULL AUTO_INCREMENT, `uuid` varchar(36) NOT NULL, `currentusername` varchar(16) NOT NULL, `mutedby` varchar(16) NOT NULL, `mutedon` bigint(15) NOT NULL, `mutedtill` bigint(15) NOT NULL, `reason` varchar(265) NOT NULL, `permanent` tinyint(1) NOT NULL, PRIMARY KEY (id)) ENGINE=InnoDB DEFAULT CHARSET=latin1;");
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			e.getMessage();
		}
	}
    public String getPunisher(UUID playerUUID, String tableName){
    	try{
    		PreparedStatement preparedStatement = connect.prepareStatement("SELECT bannedby FROM "+tableName+" WHERE uuid=?");
    		preparedStatement.setString(1, playerUUID.toString());
    		resultSet = preparedStatement.executeQuery();
    		String punisher = null;
    		while(resultSet.next()){
    			punisher = resultSet.getString("bannedby");
    		}
    		return punisher;
    	} catch (SQLException e) {
			e.getMessage();
		}
		return "Unknown";
    }
    public void close() {
        try {
            if (connect != null) {
                connect.close();
            }
        } catch (Exception e) {

        }
    }
}
