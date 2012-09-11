package me.FreeSpace2.EndSwear;

import org.bukkit.entity.Player;

public class MuteUnscheduler implements Runnable{
	LocalConfiguration config;
	String player;
	MuteUnscheduler(LocalConfiguration config,Player player){
		this.config=config;
		this.player=player.getDisplayName();
	}
	public void run() {
		config.getMuteList().remove(player);
	}

}
