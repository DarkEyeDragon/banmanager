package com.warhut.banmanager.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import com.warhut.banmanager.BanManager;
import com.warhut.banmanager.MySQLHandler;
import com.warhut.banmanager.TimeConverter;

public class PlayerJoinEvent implements Listener{
	
	BanManager plugin;
	MySQLHandler mysqlHandler;
	public PlayerJoinEvent(BanManager plugin) {
		this.plugin = plugin;
		mysqlHandler = new MySQLHandler(plugin);
	}
	//FIX Message format
	@EventHandler
	public void onPlayerConnect(AsyncPlayerPreLoginEvent e){
		try {
			if(mysqlHandler.isBannedPermanent(e.getUniqueId(), mysqlHandler.getBansTable())){
				e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, "§cYou have been permanently banned by "+ mysqlHandler.getPunisher(e.getUniqueId(), mysqlHandler.getBansTable())+"!");
			}
			else if(mysqlHandler.isIpBanned(e.getAddress().getHostAddress().toString())){
				e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, "§cYou have been permanently ip-banned by "+ mysqlHandler.getPunisher(e.getUniqueId(), mysqlHandler.getIpBansTable())+"!");
			}
			else if(mysqlHandler.isInBanList(e.getUniqueId(), mysqlHandler.getBansTable()) || mysqlHandler.isInBanList(e.getUniqueId(), mysqlHandler.getIpBansTable())){
				if(mysqlHandler.getRemainingBanTime(e.getUniqueId()) >= 0){
					e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, "§cYou are still banned for: §6" + TimeConverter.secondsToDate(mysqlHandler.getRemainingBanTime(e.getUniqueId()))+"\n §8Banned by: "+mysqlHandler.getPunisher(e.getUniqueId(), mysqlHandler.getBansTable()));
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
}
