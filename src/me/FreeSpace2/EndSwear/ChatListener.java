package me.FreeSpace2.EndSwear;

import java.io.File;
import java.io.IOException;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import me.FreeSpace2.EndSwear.matchers.*;

public class ChatListener implements Listener{
	StringMatcher wordList;
	Logger out;
	Configuration config;
	Configuration plist;
	String bleepColor;
	JavaPlugin plugin; 
	ChatListener(StringMatcher wordList, Logger out, Configuration config, Configuration plist, JavaPlugin plugin){
		this.wordList=wordList;
		this.out=out;
		this.config=config;
		this.plist=plist;
		this.plugin=plugin;
		if(config.getString("swear.bleep.color")!=null){
			bleepColor=config.getString("swear.bleep.color").replaceAll("(&([a-f0-9]))", "\u00A7$2").replace("&k", ChatColor.MAGIC+"");
		}
	}
	@SuppressWarnings("unchecked")
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event){
		Player player=event.getPlayer();
		String message=event.getMessage();
		boolean censor = false;
		for(String word:simplify(ChatColor.stripColor(event.getMessage())).split("[,./:;-`~()\\[\\]{}+ ]")){
			if(word.equals("hand") | word.contains("cook") | word.equals("muffin")){
				continue;
			}
			if(wordList.fuzzilyContains(word)){
				censor=true;
				message=message.replaceAll("(?i:"+word+")", ChatColor.RESET+bleepColor+Bleeper.generateBleep(word,(List<String>) config.getList("swear.bleep.chars"))+ChatColor.RESET);
			}else{
				message=message.replaceAll("(?i:"+word+")", Bleeper.generateBleep(word,(List<String>) config.getList("swear.bleep.chars")));
			}
		}
		event.getMessage();
		if(censor){
			swearReport(player.getDisplayName());
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Punishment(player, (EndSwear) plugin));
		}
	}
	private String simplify(String string){
		return Normalizer.normalize(string, Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase();
	}
	private void swearReport(String player){
		plist.set("tracker."+player, config.getInt("tracker."+player)+1);
		try {
			((FileConfiguration) plist).save(new File("plugins/EndSwear/userData.yml"));
		} catch (IOException e) {
			out.severe("Critical save error!  Jumping system to abort.");
			plugin.getServer().getPluginManager().disablePlugin(plugin);
		}
	}
}
