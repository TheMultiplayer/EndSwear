package me.FreeSpace2.EndSwear;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class EndSwearJoinListener implements Listener {
	private LocalConfiguration config;
	private OfflineSafePlayerList banList;
	EndSwearJoinListener(LocalConfiguration config){
		this.config=config;
		banList=config.getBanMuteList().getBanList();
	}
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event){
		if(banList.isOnList(event.getPlayer())){
			event.getPlayer().kickPlayer(ChatColor.RED+"You have been banned temporarily!");
			config.output("Player "+event.getPlayer().getDisplayName()+" has joined, but is banned!");
		}
		if(config.getSendJoinMessage()){
			event.getPlayer().sendMessage(ChatColor.GREEN+"This server is protected by "+ChatColor.GRAY+"EndSwear"+ChatColor.GREEN+"!");
		}
	}

}
