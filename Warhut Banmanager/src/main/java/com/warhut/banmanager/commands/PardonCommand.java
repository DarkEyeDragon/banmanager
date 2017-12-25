package com.warhut.banmanager.commands;

import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.warhut.banmanager.BanManager;
import com.warhut.banmanager.Messages;
import com.warhut.banmanager.MySQLHandler;

import net.md_5.bungee.api.ChatColor;

public class PardonCommand implements CommandExecutor{
	
	static private MySQLHandler mysqlHandler;
	private boolean useDatabase;
	BanManager plugin;
	public PardonCommand(BanManager plugin) {
		this.plugin = plugin;
		mysqlHandler = new MySQLHandler(plugin);
		useDatabase = mysqlHandler.useDatabase;
	}
	
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender s, Command cmd, String alias, String[] args) {
		if(cmd.getName().equalsIgnoreCase("pardon") && s.hasPermission("banmanager.pardon")){
			if(args.length < 1){
				s.sendMessage(Messages.invalidUsage+ChatColor.AQUA+"/pardon <ban/tempban/ipban> <player>");
				return true;
			}else if(args.length >= 1){
				if(useDatabase){		
					if(args[0].equalsIgnoreCase("ban") || args[0].equalsIgnoreCase("tempban")){
						try{
							OfflinePlayer target = Bukkit.getServer().getOfflinePlayer(args[1]);
							mysqlHandler.removeFromBanList(target.getUniqueId(), mysqlHandler.getBansTable());
							s.sendMessage(ChatColor.GREEN + target.getName() + " has been unbanned.");
						} catch (SQLException e) {
							e.printStackTrace();
							s.sendMessage("§c unable to unban player! Are you sure they are banned? §c/banlist");
						}
					}
					else if(args[0].equalsIgnoreCase("ipban")){
						try {
							OfflinePlayer target = Bukkit.getServer().getOfflinePlayer(args[1]);
							mysqlHandler.removeFromBanList(target.getUniqueId(), mysqlHandler.getIpBansTable());
							s.sendMessage("§a" + target.getName() +" has been unbanned.");
						} catch (Exception e) {
							s.sendMessage("§c unable to unban player! Are you sure they are banned? §c/banlist");
						}
					}else{
						s.sendMessage(Messages.invalidUsage+ChatColor.AQUA+"/pardon <ban/tempban/ipban> <player>");
					}
				}
			}
		}
		
		return false;
	}

}
