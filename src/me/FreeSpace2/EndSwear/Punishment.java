package me.FreeSpace2.EndSwear;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

public class Punishment implements Runnable{
	Player player;
	Configuration config;
	Configuration plist;
	EndSwear plugin;
	Actions action;
	String message;
	int value;
	private enum Actions {
		KICK,BAN,MUTE,KILL,DAMAGE,EXPLOSION,LIGHTNING,WARN,FINE;
	}
	Punishment(Player player, EndSwear plugin){
		this.player=player;
		this.config=plugin.getConfig();
		this.plugin=plugin;
		this.plist=plugin.playerlist;
		this.message=config.getString("swear.message").replaceAll("(&([a-f0-9]))", "\u00A7$2").replace("<PLAYER>", player.getDisplayName()).replace("<WARNING>", Integer.toString(plist.getInt("tracker."+player.getName()))+Ordinal.getOrdinal(plist.getInt("tracker."+player.getName())));
		getPunishment();
	}
	private void getPunishment(){
		for(String range:config.getConfigurationSection("swear.action").getKeys(false)){
			//plugin.getLogger().info("Range:"+range+", Tracking #:"+config.getInt("tracker."+player.getDisplayName())+", Action:"+config.getString("swear.action."+range));
			if(new Range(range).containsInclusive(plist.getInt("tracker."+player.getName()))){
				String[] actionArray = config.getString("swear.action."+range).split(",");
				action=Actions.valueOf(actionArray[0].toUpperCase());
				if(actionArray.length>1){
					value=Integer.parseInt(actionArray[1]);
				};
			}
		}
	}
	
	public void run() {
		Logger log=plugin.getLogger();
		String name=player.getName();
		switch(action){
		case KICK:
			log.info("Kicking player "+ name+".");
			player.kickPlayer(message);
			break;
		case DAMAGE:
			log.info("Damaging player "+ name+".");
			player.damage(value);
			player.sendMessage(message);
			break;
		case EXPLOSION:
			log.info("Detonating player "+ name+".");
			player.getWorld().createExplosion(player.getLocation(), 0, false);
			player.damage(20);
			break;
		case LIGHTNING:
			log.info("Smiting player "+ name+".");
			player.getWorld().strikeLightningEffect(player.getLocation());
			player.damage(20);
			break;
		case BAN:
			log.info("Tempbanning player "+ name+".");
			if(value>0){
				new TempBan(player, plugin.getServer().getScheduler(), plugin,value);
			}else{
				player.setBanned(true);
				player.kickPlayer(plugin.getConfig().getString("swear.ban.message.permanent").replaceAll("(&([a-f0-9]))", "\u00A7$2").replace("<PLAYER>", player.getDisplayName()).replace("<WARNING>", Integer.toString(plugin.getConfig().getInt("tracker."+player.getDisplayName()))+Ordinal.getOrdinal(plugin.getConfig().getInt("tracker."+player.getDisplayName()))));
			}
			break;
		case MUTE:
			player.sendMessage(ChatColor.RED+"You have been muted!");
			log.info("Muting player "+ name+".");
			new TempMute(player, plugin.getServer().getScheduler(), plugin,value);
			break;
		case WARN:
			log.info("Warning player "+ name+".");
			player.sendMessage(message);
			break;
		case FINE:
			log.info("Fining player "+ name+".");
			player.sendMessage("You have been fined "+ value+" for swearing!");
			plugin.econ.withdrawPlayer(player.getName(), value);
			break;
		case KILL:
			player.damage(20);
			player.sendMessage(message);
			break;
		default:
			break;
		}
	}
}
