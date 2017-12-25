package com.warhut.banmanager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitScheduler;

public class BanManager extends JavaPlugin implements PluginMessageListener{

	private static MySQLHandler mysqlHandler;
	private static  MuteCommand muteCommand;
	
	
	@Override
	public void onEnable(){
		getConfig().options().copyDefaults(true);
		saveDefaultConfig();

		BanManager.mysqlHandler = new MySQLHandler(this);
		mysqlHandler.connectToDatabase();
		
		//Register commands
		this.getCommand("tempban").setExecutor(new TempbanCommand(this));
		this.getCommand("filltable").setExecutor(new FillTableCommand(this));
		this.getCommand("ban").setExecutor(new BanCommand(this));
		this.getCommand("pardon").setExecutor(new PardonCommand(this));
		this.getCommand("ipban").setExecutor(new IpbanCommand(this));
		this.getCommand("ip").setExecutor(new IpLookupCommand());
		this.getCommand("mute").setExecutor(new MuteCommand(this));
		this.getServer().getPluginManager().registerEvents(new PlayerJoinEvent(this), this);
		this.getServer().getPluginManager().registerEvents(new PlayerChatEvent(), this);


		BukkitScheduler scheduler = getServer().getScheduler();

		BanTimeValidator banTimeValidator = new BanTimeValidator(this);
		scheduler.scheduleSyncRepeatingTask(this, () -> {
            banTimeValidator.checkDatabase();
        }, 60L, 6000L);

		//Register Bungeecord channels
		Bukkit.getServer().getMessenger().registerIncomingPluginChannel(this,"BungeeCord", this);
		Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(this,"BungeeCord");

	}
	public void onDisable(){
		this.getLogger().info(this.getDescription().getName()+" "+this.getDescription().getVersion()+" has been disabled");
		mysqlHandler.close();
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message){
		
	}
}
