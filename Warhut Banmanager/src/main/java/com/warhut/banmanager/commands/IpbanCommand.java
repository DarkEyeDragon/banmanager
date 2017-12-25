package com.warhut.banmanager.commands;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.warhut.banmanager.BanManager;
import com.warhut.banmanager.Messages;
import com.warhut.banmanager.MySQLHandler;

import net.md_5.bungee.api.ChatColor;

public class IpbanCommand implements CommandExecutor{
Messages messages;
	
	static private MySQLHandler mysqlHandler;
	private boolean useDatabase;
	BanManager plugin;
	public IpbanCommand(BanManager plugin) {
		this.plugin = plugin;
		mysqlHandler = new MySQLHandler(plugin);
		useDatabase = mysqlHandler.useDatabase;
		messages = new Messages(plugin);
	}
	
	public boolean onCommand(CommandSender s, Command cmd, String alias, String[] args) {
		if(cmd.getName().equalsIgnoreCase("ipban") && s.hasPermission("banmanager.ipban")){
			if(args.length < 1){
				s.sendMessage(Messages.invalidUsage+ChatColor.AQUA+"/ipban <player> <reason>");
				return true;
			}
			if(args.length == 1){
				s.sendMessage(ChatColor.DARK_PURPLE+"Please provide a reason!");
				return true;
			}
			if(useDatabase){
				String playerIP = null;
				@SuppressWarnings("deprecation")
				OfflinePlayer target = Bukkit.getServer().getOfflinePlayer(args[0]);
				if(!target.isOnline()){
					if(args[1].contains("ip:")){
						playerIP = args[1].replace("ip:", "").toString();
						System.out.println(playerIP);
					}else{
						s.sendMessage(ChatColor.RED+"Can not ip ban an offline player!");
						s.sendMessage(ChatColor.RED+"Use: "+ChatColor.AQUA+"/ipban <username> <ip:0.0.0.0> <reason> §cto directly ban an IP.");
						return true;
					}
				}else{
					playerIP = Bukkit.getPlayer(args[0]).getAddress().getAddress().toString().replace("/", "");
				}
				String reason = null;
				if(target.isOnline()){
					reason = Arrays.asList(args).stream().skip(1).collect(Collectors.joining(" "));
					Bukkit.getServer().getPlayer(target.getUniqueId()).kickPlayer("§cYou have been permanently banned by " + s.getName() + " \nfor "+reason);
				}else{
					reason = Arrays.asList(args).stream().skip(2).collect(Collectors.joining(" "));
				}
				try {
					mysqlHandler.addToIpBanlist(target.getUniqueId(), target.getName(), s.getName(), System.currentTimeMillis()/1000, playerIP, reason);
					for(Player p : Bukkit.getOnlinePlayers()){
						if(p.hasPermission("banmanager.notify") || p.isOp()){
							//p.sendMessage("�ePlayer �a" + target.getName()+" �ehas been temp banned for �a"+ args[1]+" �eby �a" + s.getName()+"�e!");
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.ban)
									.replace("<prefix>", ChatColor.translateAlternateColorCodes('&', messages.prefix))
									.replace("<offender>", target.getName())
									.replace("<time>", "permanent")
									.replace("<punisher>", s.getName())
									.replace("<reason>", reason));
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				s.sendMessage(Messages.noDatabase);
			}
		}
		return false;
	}
	

}
