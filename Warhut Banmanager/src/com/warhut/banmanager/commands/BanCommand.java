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

public class BanCommand implements CommandExecutor{
	
	Messages messages;
	
	static private MySQLHandler mysqlHandler;
	private boolean useDatabase;
	BanManager plugin;
	public BanCommand(BanManager plugin) {
		this.plugin = plugin;
		mysqlHandler = new MySQLHandler(plugin);
		useDatabase = mysqlHandler.useDatabase;
		messages = new Messages(plugin);
	}
	
	public boolean onCommand(CommandSender s, Command cmd, String alias, String[] args) {
		if(cmd.getName().equalsIgnoreCase("ban") && s.hasPermission("banmanager.ban")){
			if(args.length < 1){
				s.sendMessage(Messages.invalidUsage+ChatColor.AQUA+"/ban <player> <reason>");
				return false;
			}
			if(args.length == 1){
				s.sendMessage(ChatColor.DARK_PURPLE+"Please provide a reason!");
				return true;
			}
			if(useDatabase){
				@SuppressWarnings("deprecation")
				OfflinePlayer target = Bukkit.getServer().getOfflinePlayer(args[0]);
				String reason = Arrays.asList(args).stream().skip(1).collect(Collectors.joining(" "));
				try {
					mysqlHandler.addToBanlist(target.getUniqueId(), target.getName(), s.getName(), System.currentTimeMillis()/1000, 0, reason, true);
					if(target instanceof Player){
						Bukkit.getServer().getPlayer(target.getUniqueId()).kickPlayer("§cYou have been permanently banned by " + s.getName() + " \nfor "+reason);
					}
					for(Player p : Bukkit.getOnlinePlayers()){
						if(p.hasPermission("banmanager.notify") || p.isOp()){
							//p.sendMessage("§ePlayer §a" + target.getName()+" §ehas been temp banned for §a"+ args[1]+" §eby §a" + s.getName()+"§e!");
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
