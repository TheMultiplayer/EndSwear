package me.FreeSpace2.EndSwear;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

public class BanMuteHandler {
	BanMuteHandler(BukkitScheduler scheduler){
		this.scheduler=scheduler;
	}
	BukkitScheduler scheduler;
	OfflineSafePlayerList banList=new OfflineSafePlayerList();
	OfflineSafePlayerList muteList=new OfflineSafePlayerList();
	public void scheduleBan(Player player, int time){
		banList.add(player);
	}
	public void scheduleBan(String player, int time){
		banList.add(player);
		scheduler.scheduleSyncDelayedTask(plugin, new BanUnscheduler(this, player),time*20L);
	}
	public void scheduleBan(String player, int time){
		banList.add(player);
		scheduler.scheduleSyncDelayedTask(plugin, new BanUnscheduler(this, player),time*20L);
	}
	public void scheduleMute(Player player, int time){
		muteList.add(player);
	}
	public void scheduleMute(String player, int time){
		scheduler.scheduleSyncDelayedTask(plugin, new MuteUnscheduler(this, player),time*20L);
	}
	public OfflineSafePlayerList getBanList(){
		return banList;
	}
	public OfflineSafePlayerList getMuteList(){
		return muteList;
	}
	
	
	class BanUnscheduler implements Runnable{
		BanMuteHandler config;
		String player;
		BanUnscheduler(BanMuteHandler config,Player player){
			this.config=config;
			this.player=player.getDisplayName();
		}
		BanUnscheduler(BanMuteHandler config,String player){
			this.config=config;
			this.player=player;
		}
		public void run() {
			config.getBanList().remove(player);
		}
	}
	class MuteUnscheduler implements Runnable{
		BanMuteHandler config;
		String player;
		MuteUnscheduler(BanMuteHandler config,Player player){
			this.config=config;
			this.player=player.getDisplayName();
		}
		MuteUnscheduler(BanMuteHandler config,String player){
			this.config=config;
			this.player=player;
		}
		public void run() {
			config.getMuteList().remove(player);
		}
	}
}
