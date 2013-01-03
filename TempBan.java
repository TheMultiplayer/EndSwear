package me.FreeSpace2.EndSwear;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

public class TempBan{
	TempBan(Player player, BukkitScheduler scheduler, Plugin plugin){
		scheduler.scheduleSyncDelayedTask(plugin, new UnBan(player), plugin.getConfig().getInt("swear.ban.time")*20L);
		player.setBanned(true);
		player.kickPlayer(plugin.getConfig().getString("swear.message").replaceAll("(&([a-f0-9]))", "\u00A7$2").replace("<PLAYER>", player.getDisplayName()).replace("<WARNING>", Integer.toString(plugin.getConfig().getInt("tracker."+player.getDisplayName()))+Ordinal.getOrdinal(plugin.getConfig().getInt("tracker."+player.getDisplayName())))+" You have also been tempbanned!");
	}
	class UnBan implements Runnable{
		OfflinePlayer player;
		UnBan(OfflinePlayer player){
			this.player=player;
		}
		public void run(){
			player.setBanned(false);
		}
	}
}
