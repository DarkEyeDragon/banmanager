package com.warhut.banmanager;

import org.bukkit.configuration.ConfigurationSection;


public class Messages {
	
	BanManager plugin;
	public String prefix;
	public String ban;
	public String tempban;
	public String mute;
	public Messages(BanManager plugin){
		ConfigurationSection messages = plugin.getConfig().getConfigurationSection("messages");
		this.plugin = plugin;
		prefix = messages.getString("prefix");
		ban = messages.getString("ban");
		tempban = messages.getString("tempban");
		mute = messages.getString("mute");
	}
	public static String invalidUsage = "§cInvalid usage! use: ";
	public static String noDatabase = "§cUse of database has been set to false! Banning will not work";
	public static String playerNotOnline = "§cThat player is not online.";
}
