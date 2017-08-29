package com.warhut.banmanager.commands;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.warhut.banmanager.BanManager;
import com.warhut.banmanager.Messages;
import com.warhut.banmanager.MySQLHandler;
import com.warhut.banmanager.TimeConverter;



public class TempbanCommand implements CommandExecutor{
	
	static private MySQLHandler mysqlHandler;
	private boolean useDatabase;
	private Messages messages;
	public TempbanCommand(BanManager plugin) {
		this.plugin = plugin;
		mysqlHandler = new MySQLHandler(plugin);
		useDatabase = mysqlHandler.useDatabase;
		messages = new Messages(plugin);
	}
	
	BanManager plugin;
	
	public boolean onCommand(CommandSender s, Command cmd, String alias, String[] args) {
       
		if(cmd.getName().equalsIgnoreCase("tempban") && s.hasPermission("banmanager.tempban")){
			if(args.length<2){
				s.sendMessage(Messages.invalidUsage +ChatColor.AQUA+ "/tempban <username> <time> <reason>" +ChatColor.RED+"!");
				return true;
			}
			if(args.length == 2){
				s.sendMessage(ChatColor.DARK_PURPLE+"Please provide a reason!");
				return true;
			}
			@SuppressWarnings("deprecation")
			OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
			if(useDatabase){
				long banTime = 0;
				long curTimeInMil = System.currentTimeMillis();
				long curTimeInSec = curTimeInMil/1000;
				banTime = TimeConverter.stringToSeconds(args[1]);
				String reason = Arrays.asList(args).stream().skip(2).collect(Collectors.joining(" "));
				try {
					mysqlHandler.addToBanlist(target.getUniqueId(), target.getName(), s.getName(), curTimeInSec, curTimeInSec+banTime, reason, false);
					if(target instanceof Player){
						//Fix format
						Bukkit.getServer().getPlayer(target.getUniqueId()).kickPlayer("§cYou have been temporarly banned for §6"+TimeConverter.secondsToDate(banTime)+ " by " + s.getName() + " \nfor "+reason);
					}
					for(Player p : Bukkit.getOnlinePlayers()){
						if(p.hasPermission("banmanager.notify") || p.isOp()){
							//p.sendMessage("§ePlayer §a" + target.getName()+" §ehas been temp banned for §a"+ args[1]+" §eby §a" + s.getName()+"§e!");
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.tempban)
									.replace("<prefix>", ChatColor.translateAlternateColorCodes('&', messages.prefix))
									.replace("<offender>", target.getName())
									.replace("<time>", TimeConverter.secondsToDate(banTime))
									.replace("<punisher>", s.getName())
									.replace("<reason>", reason));
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					return true;
				}
			}else{
				s.sendMessage(Messages.noDatabase);
			}
			return true;
		}
		return true;
    }

}
