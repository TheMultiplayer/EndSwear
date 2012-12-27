package me.FreeSpace2.EndSwear;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatListener implements Listener{
	FuzzyStringList wordList;
	Logger out;
	Configuration config;
	String bleepColor;
	JavaPlugin plugin; 
	ChatListener(FuzzyStringList wordList, Logger out, Configuration config, JavaPlugin plugin){
		this.wordList=wordList;
		this.out=out;
		this.config=config;
		this.plugin=plugin;
		bleepColor=config.getString("swear.bleep.color").replaceAll("(&([a-f0-9]))", "\u00A7$2").replace("&k", ChatColor.MAGIC+"");
	}
	@SuppressWarnings("unchecked")
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event){
		Player player=event.getPlayer();
		String message=event.getMessage();
		boolean censor = false;
		for(String word:simplify(ChatColor.stripColor(event.getMessage())).split("[,./:;-`~()\\[\\]{}+ ]")){
			StringMatch lM=wordList.phoneticMatch(word);
			if(lM.isOK()){
				censor=true;
				message=message.replaceAll("(?i:"+lM.getString()+")", ChatColor.RESET+bleepColor+Bleeper.generateBleep(lM.getString(),(List<String>) config.getList("swear.bleep.chars"))+ChatColor.RESET);
			};
		}
		event.setMessage(message);
		if(censor){
			swearReport(player.getDisplayName());
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Punishment(player, (EndSwear) plugin));
		}
	}
	private String simplify(String string){
		return Normalizer.normalize(string, Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase();
	}
	private void swearReport(String player){
		config.set("tracker."+player, config.getInt("tracker."+player)+1);
	}
}
