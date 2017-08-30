package com.warhut.banmanager;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import com.warhut.banmanager.commands.BanCommand;
import com.warhut.banmanager.commands.FillTableCommand;
import com.warhut.banmanager.commands.IpLookupCommand;
import com.warhut.banmanager.commands.IpbanCommand;
import com.warhut.banmanager.commands.MuteCommand;
import com.warhut.banmanager.commands.PardonCommand;
import com.warhut.banmanager.commands.TempbanCommand;
import com.warhut.banmanager.events.PlayerChatEvent;
import com.warhut.banmanager.events.PlayerJoinEvent;

public class BanManager extends JavaPlugin{
	
	static private MySQLHandler mysqlHandler;	
	
	
	@Override
	public void onEnable(){		
		//Set default values for the config
		ConfigurationSection mysqlConfigSection = this.getConfig().getConfigurationSection("mysql");
		mysqlConfigSection.addDefault("usedatabase", false);
		mysqlConfigSection.addDefault("ip", "localhost");
		mysqlConfigSection.addDefault("database", "ban_manager");
		mysqlConfigSection.addDefault("username", "root");
		mysqlConfigSection.addDefault("password", "");
		mysqlConfigSection.addDefault("bans_table", "bans");
		mysqlConfigSection.addDefault("ipbans_table", "ipbans");
		
		ConfigurationSection messagesConfigSection = this.getConfig().getConfigurationSection("messages");
		messagesConfigSection.addDefault("prefix", "&c&lWARHUT &8» &7");
		messagesConfigSection.addDefault("ban", "<player> has been banned by <punisher>");
		messagesConfigSection.addDefault("tempban", "<player> has been banned for <time> by <punisher>");
		messagesConfigSection.addDefault("kick", "<player> has been kicked by <punisher> for <reason>");
		messagesConfigSection.addDefault("mute", "<player> was muted by <punisher>");
		this.getConfig().options().copyDefaults(true);
		this.saveConfig();
		
		BanManager.mysqlHandler = new MySQLHandler(this);
		mysqlHandler.connectToDatabase();
		//Register commands
		this.getCommand("tempban").setExecutor(new TempbanCommand(this));
		this.getCommand("filltable").setExecutor(new FillTableCommand(this));
		this.getCommand("ban").setExecutor(new BanCommand(this));
		this.getCommand("pardon").setExecutor(new PardonCommand(this));
		this.getCommand("ipban").setExecutor(new IpbanCommand(this));
		this.getCommand("ip").setExecutor(new IpLookupCommand());
		this.getCommand("mute").setExecutor(new MuteCommand());
		this.getServer().getPluginManager().registerEvents(new PlayerJoinEvent(this), this);
		this.getServer().getPluginManager().registerEvents(new PlayerChatEvent(), this);
	}
	public void onDisable(){
		this.getLogger().info(this.getDescription().getName()+" "+this.getDescription().getVersion()+" has been disabled");
		mysqlHandler.close();
	}
}
