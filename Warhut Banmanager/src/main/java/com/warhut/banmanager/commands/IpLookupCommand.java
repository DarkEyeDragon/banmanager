package com.warhut.banmanager.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.warhut.banmanager.Messages;

import net.md_5.bungee.api.ChatColor;

public class IpLookupCommand implements CommandExecutor{
	public boolean onCommand(CommandSender s, Command cmd, String alias, String[] args) {
		
		if(cmd.getName().equalsIgnoreCase("ip")){
			if(args.length == 0){
				s.sendMessage(Messages.invalidUsage+ChatColor.AQUA+"/ip <online player>");
				return true;
			}
			Player target = Bukkit.getServer().getPlayer(args[0]);
			if(target.isOnline()) s.sendMessage(target.getAddress().getAddress().toString());
			else s.sendMessage(Messages.playerNotOnline);
			
		}
		return true;
	}

}
