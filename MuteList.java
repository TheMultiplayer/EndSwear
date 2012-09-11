package me.FreeSpace2.EndSwear;

import org.bukkit.entity.Player;

public class MuteList extends OfflineSafePlayerList{
	LocalConfiguration config;
	MuteList(LocalConfiguration config){
		this.config=config;
	}
	public synchronized void add(Player player){
		list.add(player.getDisplayName());
		config.scheduleMute(new MuteUnscheduler(config, player));
	}
}
