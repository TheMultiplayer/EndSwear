package me.FreeSpace2.EndSwear;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

public class TempBan{
	TempBan(Player player, BukkitScheduler scheduler, Plugin plugin, int time){
		scheduler.scheduleSyncDelayedTask(plugin, new UnBan(player), time*20L);
		((OfflinePlayer) player).setBanned(true);
		plugin.getLogger().info(player.getDisplayName()+"'s ban will only be removed if the server stays on!  Otherwise, you must manually use /pardon.");
		player.kickPlayer(plugin.getConfig().getString("swear.ban.message.temporary").replaceAll("(&([a-f0-9]))", "\u00A7$2").replace("<PLAYER>", player.getDisplayName()).replace("<WARNING>", Integer.toString(plugin.getConfig().getInt("tracker."+player.getDisplayName()))+Ordinal.getOrdinal(plugin.getConfig().getInt("tracker."+player.getDisplayName()))));
	}
	class UnBan implements Runnable{
		OfflinePlayer player;
		UnBan(OfflinePlayer player){
			this.player=player;
		}
		public void run(){
			System.out.println("[EndSwear] Removing "+player.getName()+"'s ban!");
			player.setBanned(false);
		}
	}
}
