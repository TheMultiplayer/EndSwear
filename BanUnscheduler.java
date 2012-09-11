package me.FreeSpace2.EndSwear;

import org.bukkit.entity.Player;

public class BanUnscheduler implements Runnable{
	LocalConfiguration config;
	String player;
	BanUnscheduler(LocalConfiguration config,Player player){
		this.config=config;
		this.player=player.getDisplayName();
	}
	public void run() {
		config.getBanList().remove(player);
	}

}
