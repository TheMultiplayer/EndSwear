package me.FreeSpace2.EndSwear;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

public class TempMute implements Listener{
	Plugin plugin;
	TempMute(Player player, BukkitScheduler scheduler, Plugin plugin){
		this.plugin=plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		scheduler.scheduleAsyncDelayedTask(plugin, new UnMute(player, this), plugin.getConfig().getInt("swear.mute.time")*20L);
	}
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event){
		event.setCancelled(true);
	}
	class UnMute implements Runnable{
		Player player;
		TempMute tmp;
		UnMute(Player player, TempMute tmp){
			this.player=player;
			this.tmp=tmp;
		}
		public void run(){
			AsyncPlayerChatEvent.getHandlerList().unregister(tmp);
		}
	}
}
