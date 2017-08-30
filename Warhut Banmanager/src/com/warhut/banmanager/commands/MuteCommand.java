package com.warhut.banmanager.commands;


import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.warhut.banmanager.Messages;
import com.warhut.banmanager.events.PlayerChatEvent;

public class MuteCommand implements CommandExecutor{

	PlayerChatEvent playerChat;
	
	@Override
	public boolean onCommand(CommandSender s, Command cmd, String alias, String[] args) {
		if(cmd.getName().equalsIgnoreCase("mute")){
			if(args.length > 0){
				Player p = Bukkit.getServer().getPlayer(args[0]);
				if(p instanceof Player){
					if(p.isOnline()) playerChat.addToMuted(p.getUniqueId());

				}
			}else{
				s.sendMessage(Messages.invalidUsage + "§b/mute <player> <time> <reason>");
			}
		}
		
		return false;
	}

}
