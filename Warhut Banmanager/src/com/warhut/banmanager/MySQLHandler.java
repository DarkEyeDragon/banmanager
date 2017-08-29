package com.warhut.banmanager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import com.warhut.banmanager.utils.TimeConverter;


public class MySQLHandler {

	BanManager plugin;
	
	
	protected String ip;
	private String database;
	private String username;
	private String password;
	private String bans_table;
	private String ipBans_table;
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
	    ip = plugin.getConfig().getString("mysql.ip");
	    database = plugin.getConfig().getString("mysql.database");
	    username = plugin.getConfig().getString("mysql.username");
	    password = plugin.getConfig().getString("mysql.password");
	    bans_table = plugin.getConfig().getString("mysql.bans_table");
	    ipBans_table = plugin.getConfig().getString("mysql.ipbans_table");
	    useDatabase = plugin.getConfig().getBoolean("mysql.usedatabase");
	}
	
	protected void connectToDatabase(){
		try{
			connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+database, username, password);
		    System.out.println("Successfully connected to database("+database+")!");
		    createBanTable();
		    createIpBanTable();
		    isConnected = true;
		} catch (SQLException e) {
		    plugin.getLogger().warning("Unable to connect to database! Banmanager will NOT function properly when not connected.");
		}
	}
	//Adding a player to the ban list will ban them but not kick them.
    public void addToBanlist(UUID uuid, String currentUsername, String bannedBy, long bannedOn, long bannedTill, String reason, boolean permanent) throws Exception{
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
    	}catch(Exception e){
    		throw e;
    	}
    }
    
    public void addToIpBanlist(UUID uuid, String currentUsername, String bannedBy, long bannedOn, String playerIP, String reason) throws Exception{
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
    	}catch(Exception e){
    		throw e;
    	}
    }
    
    public boolean isInBanList(UUID playerUUID, String tableName) throws Exception{
    	if(useDatabase){
	    	PreparedStatement preparedStatement = connect.prepareStatement("SELECT uuid FROM "+tableName+" WHERE uuid=?");
	    	preparedStatement.setString(1, playerUUID.toString().toLowerCase());
	    	resultSet = preparedStatement.executeQuery();
			
	    	return resultSet.next();
    	}
    	return false;
    }
    public boolean isBannedPermanent(UUID playerUUID, String tableName) throws Exception{
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
    public long getRemainingBanTime(UUID uuid) throws Exception{
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
    public String getBanList(){
    	try{
    		PreparedStatement preparedStatement = connect.prepareStatement("SELECT currentusername FROM "+bans_table);
    		resultSet = preparedStatement.executeQuery();
    		StringBuilder banList = new StringBuilder();
    		while(resultSet.next()){
    			banList.append(resultSet.getString(1)).append(", ");
    		}
    		String banString = banList.toString();
    		banString = banString.substring(0, banString.length()-2);
    		return banString;
    	} catch (SQLException e) {
			e.getMessage();
		}
		return null;
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
    public boolean getConnection(){
	    if(connect !=null){
	    	return true;
	    }
		return false;
    }
}
