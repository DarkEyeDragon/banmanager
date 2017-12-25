package com.warhut.banmanager.commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.warhut.banmanager.BanManager;
import com.warhut.banmanager.MySQLHandler;

public class FillTableCommand implements CommandExecutor {
	
	static private MySQLHandler mysqlHandler;
	public FillTableCommand(BanManager plugin) {
		this.plugin = plugin;
		mysqlHandler = new MySQLHandler(plugin);
	}
	
	ChatColor green = ChatColor.GREEN;
	ChatColor aqua = ChatColor.AQUA;
	ChatColor red = ChatColor.RED;
	BanManager plugin;
	
	public boolean onCommand(CommandSender s, Command cmd, String alias, String[] args) {
       
		if(cmd.getName().equalsIgnoreCase("filltable")  && s.isOp()){			 
			Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			    public void run() {
			    	try {
						mysqlHandler.addToBanlist(UUID.randomUUID(),"filltable", "filltable", 0, 0, "test", false);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    }
			}, 0, 0);
		}
		return true;
    }

}
