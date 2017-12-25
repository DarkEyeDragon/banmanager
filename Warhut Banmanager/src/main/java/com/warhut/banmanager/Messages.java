package com.warhut.banmanager;

import org.bukkit.configuration.ConfigurationSection;


public class Messages {
	
	BanManager plugin;
	public String prefix;

	public String ban;
	public String ban_kick;
	public String ban_join;

	public String ipban;
	public String ipban_kick;
	public String ipban_join;

	public String tempban;
	public String tempban_kick;
	public String tempban_join;

	public String kick;
	public String mute;
	public Messages(BanManager plugin){
		ConfigurationSection messages = plugin.getConfig().getConfigurationSection("messages");
		this.plugin = plugin;
		prefix = messages.getString("prefix");

		ban = messages.getString("ban.broadcast");
		ban_kick = messages.getString("ban.kick");
		ban_join  = messages.getString("ban.join");

		ipban = messages.getString("ipban.broadcast");
		ipban_join = messages.getString("ipban.join");
		ipban_kick = messages.getString("ipban.kick");

		tempban = messages.getString("tempban.broadcast");
		tempban_join = messages.getString("tempban.join");
		tempban_kick = messages.getString("tempban.kick");

		mute = messages.getString("mute");

		kick = messages.getString("kick");
	}
	public static String invalidUsage = "§cInvalid usage! use: ";
	public static String noDatabase = "§cUse of database has been set to false! Banning will not work";
	public static String playerNotOnline = "§cThat player is not online.";
}
