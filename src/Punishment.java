package me.FreeSpace2.EndSwear;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

public class Punishment implements Runnable{
	Player player;
	Configuration config;
	EndSwear plugin;
	String action="null";
	String message;
	Logger log;
	Punishment(Player player, EndSwear plugin){
		this.player=player;
		this.config=plugin.getConfig();
		this.plugin=plugin;
		log=plugin.getLogger();
		for(String range:config.getConfigurationSection("swear.action").getKeys(false)){
			//plugin.getLogger().info("Range:"+range+", Tracking #:"+config.getInt("tracker."+player.getDisplayName())+", Action:"+config.getString("swear.action."+range));
			if(new Range(range).containsInclusive(config.getInt("tracker."+player.getDisplayName()))){
				action=config.getString("swear.action."+range);
			}
		}
		this.message=config.getString("swear.message").replaceAll("(&([a-f0-9]))", "\u00A7$2").replace("<PLAYER>", player.getDisplayName()).replace("<WARNING>", Integer.toString(config.getInt("tracker."+player.getDisplayName()))+Ordinal.getOrdinal(config.getInt("tracker."+player.getDisplayName())));
	}
	public void run() {
		String name=player.getDisplayName();
		if(action.equalsIgnoreCase("kick")){
			log.info("Kicking player "+ name+".");
			player.kickPlayer(message);
		}else if(action.equalsIgnoreCase("damage")){
			log.info("Damaging player "+ name+".");
			player.damage(config.getInt("swear.damage"));
			player.sendMessage(message);
			return;
		}else if(action.equalsIgnoreCase("explosion")){
			log.info("Detonating player "+ name+".");
			player.getWorld().createExplosion(player.getLocation(), 0, false);
			player.damage(20);
			return;
		}else if(action.equalsIgnoreCase("lightning")){
			log.info("Smiting player "+ name+".");
			player.getWorld().strikeLightningEffect(player.getLocation());
			player.damage(20);
			return;
		}else if(action.equalsIgnoreCase("ban")){
			log.info("Tempbanning player "+ name+".");
			new TempBan(player, plugin.getServer().getScheduler(), plugin);
		}else if(action.equalsIgnoreCase("mute")){
			player.sendMessage(ChatColor.RED+"You have been muted!");
			log.info("Muting player "+ name+".");
			new TempMute(player, plugin.getServer().getScheduler(), plugin);
		}else if(action.equalsIgnoreCase("warn")){
			log.info("Warning player "+ name+".");
			player.sendMessage(message);
		}else if(action.equalsIgnoreCase("fine")){
			log.info("Fining player "+ name+".");
			player.sendMessage("You have been fined "+ config.getInt("swear.fine")+" for swearing!");
			plugin.econ.withdrawPlayer(player.getDisplayName(), config.getInt("swear.fine"));
		}
	}
}
