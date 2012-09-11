package me.FreeSpace2.EndSwear;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class ActionThread implements Runnable{
	private String action;
	private Economy econ;
	private Player player;
	private LocalConfiguration config;
	ActionThread(Player player, String action, LocalConfiguration config){
		this.action=action;
		this.player=player;
		this.config=config;
		this.econ=config.getEconomy();
	}
	public void run() {
		if(action.equalsIgnoreCase("kick")){
			if(config.playerExceededWarnings(player)){
				player.kickPlayer(config.getKickMessage().replaceAll("<PLAYER>", player.getDisplayName()));
				return;
			};
		}else if(action.equalsIgnoreCase("warn")){
			player.sendMessage(ChatColor.LIGHT_PURPLE+"No swearing, "+player.getDisplayName()+"! This is your "+ChatColor.GOLD+config.getSwears(player)+Ordinal.getOrdinal(config.getSwears(player))+ChatColor.LIGHT_PURPLE+" warning.");
			return;
			
		}else if(action.equalsIgnoreCase("fine")){
			player.sendMessage(ChatColor.LIGHT_PURPLE+"No swearing, "+player.getDisplayName()+"! "+ChatColor.RED+config.getFine(player)+" has been taken as a fine!");
			econ.withdrawPlayer(player.getDisplayName(), config.getFine(player));
			return;
		}else if(action.equalsIgnoreCase("tempban") & config.playerExceededWarnings(player)){
				config.output("Temporarily banning player "+player.getDisplayName()+".");
				config.getBanList().add(player);
				player.kickPlayer(config.getBanMessage().replace("<PLAYER>", player.getDisplayName()));
			return;
		}else if(action.equalsIgnoreCase("mute") & config.playerExceededWarnings(player)){
				config.output("Muting player "+player.getDisplayName()+".");
				config.getMuteList().add(player);
				player.sendMessage(config.getMuteMessage().replace("<PLAYER>", player.getDisplayName()));
				for(OfflinePlayer OPlayer:player.getServer().getOperators()){
					if(OPlayer.isOnline()){
						((Player)OPlayer).sendMessage(player.getDisplayName()+" was muted for swearing!");
					}
				}
			return;
		}else if(action.equalsIgnoreCase("damage")){
			player.damage(config.getDamage());
			warn();
			return;
		}else if(action.equalsIgnoreCase("explosion") & config.playerExceededWarnings(player)){
			player.getWorld().createExplosion(player.getLocation(), 0, false);
			player.damage(20);
			return;
		}else if(action.equalsIgnoreCase("lightning") & config.playerExceededWarnings(player)){
			player.getWorld().strikeLightningEffect(player.getLocation());
			player.damage(20);
			return;
		}else{
			warn();
		}
	}
	private void warn(){
		player.sendMessage(config.getWarnMessage().replace("<PLAYER>", player.getDisplayName()).replace("<WARNINGS>", Byte.toString(config.warningsLeft(player))));
	}

}
