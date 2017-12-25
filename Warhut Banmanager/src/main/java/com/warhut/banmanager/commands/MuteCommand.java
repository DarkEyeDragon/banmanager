package com.warhut.banmanager.commands;


import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.warhut.banmanager.BanManager;
import com.warhut.banmanager.Messages;
import com.warhut.banmanager.MySQLHandler;
import com.warhut.banmanager.events.PlayerChatEvent;

public class MuteCommand implements CommandExecutor{
	
	BanManager plugin;
	static private MySQLHandler mysqlHandler;
	private boolean useDatabase;
	private Messages messages;
	private PlayerChatEvent playerChat;
	
	public MuteCommand(BanManager plugin){
		this.plugin = plugin;
		this.mysqlHandler = new MySQLHandler(plugin);
		useDatabase = mysqlHandler.useDatabase;
		messages = new Messages(plugin);
		playerChat = new PlayerChatEvent();
	}
	@Override
	public boolean onCommand(CommandSender s, Command cmd, String alias, String[] args){
		if(cmd.getName().equalsIgnoreCase("mute")){
			if(args.length > 0){
				Player p = Bukkit.getServer().getPlayer(args[0]);
				if(p instanceof Player){
					if(p.isOnline()){ 
						
					}

				}
			}else{
				s.sendMessage(Messages.invalidUsage + "§b/mute <player> <time> <reason>");
			}
		}
		
		return false;
	}

}
