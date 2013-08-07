package me.FreeSpace2.EndSwear;

import java.io.IOException;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import me.FreeSpace2.EndSwear.StringFilter;

public class ChatListener implements Listener{
	StringMatcher blacklist;
	StringMatcher whitelist;
	EndSwear plugin;
	String bleepColor;
	ChatListener(StringMatcher blacklist,StringMatcher whitelist,EndSwear plugin){
		this.blacklist=blacklist;
		this.whitelist=whitelist;
		this.plugin=plugin;
		if(plugin.config.getString("swear.bleep.color")!=null){
			bleepColor=plugin.config.getString("swear.bleep.color").replaceAll("(&([a-f0-9]))", "\u00A7$2").replace("&k", ChatColor.MAGIC+"");
		}
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event){
		Player player=event.getPlayer();
		String message=event.getMessage();
		message=StringFilter.sterilize(message);
		boolean censor=false;
		for(String word:message.toLowerCase().split("[,./:;-`~()\\[\\]{}+ ]")){
			if(!whitelist.contains(word)){
				if(blacklist.fuzzilyContains(word)){
					censor=true;
					reportPlayer(player);
					message=censor(message,word);
					plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Punishment(player, plugin));
				}
			}
		}
		if(censor){
			event.setMessage(message);
		}
	}
	private void reportPlayer(Player player){
		plugin.getLogger().info("Player "+player.getName()+" swore!");
		incrementPlayer(player); 
	}
	@SuppressWarnings("unchecked")
	private String censor(String message,String word){
		message=message.replaceAll("(?i:"+word+")", ChatColor.RESET+bleepColor+Bleeper.generateBleep(word,(List<String>) plugin.config.getList("swear.bleep.chars"))+ChatColor.RESET);
		return message;
	}
	private void incrementPlayer(Player playerobj){
		String player=playerobj.getName();
		Configuration plist=plugin.playerlist;
		plist.set("tracker."+player, plist.getInt("tracker."+player)+1);
		try {
			((FileConfiguration) plist).save(plugin.userdataLocation);
		} catch (IOException e) {
			plugin.getLogger().severe("Critical save error!  Jumping system to abort.");
			plugin.getServer().getPluginManager().disablePlugin(plugin);
		}
	}
}
