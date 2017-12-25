package com.warhut.banmanager.events;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.warhut.banmanager.Messages;

public class PlayerChatEvent implements Listener{
	
	List<UUID> muted = new ArrayList<UUID>();
	Messages messages;
	
	public void addToMuted(UUID playerUUID){
		muted.add(playerUUID);
	}

	
	public void onPlayerChat(AsyncPlayerChatEvent e) {
	        Player p = e.getPlayer();
	        UUID playerUUID = p.getUniqueId();
	        if(muted.contains(playerUUID)){
	        	 e.setCancelled(true);
	        	 p.sendMessage(messages.mute);
	        }
	}
}
