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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class SignListener implements Listener{
	StringMatcher wordList;
	Logger out;
	Configuration config;
	Configuration plist;
	String bleepColor;
	JavaPlugin plugin; 
	SignListener(StringMatcher wordList, EndSwear plugin){
		this.wordList=wordList;
		this.out=plugin.getLogger();
		this.config=plugin.getConfig();
		this.plist=plugin.playerlist;
		this.plugin=plugin;
		if(config.getString("swear.bleep.color")!=null){
			bleepColor=config.getString("swear.bleep.color").replaceAll("(&([a-f0-9]))", "\u00A7$2").replace("&k", ChatColor.MAGIC+"");
		}
	}
	@SuppressWarnings("unchecked")
	@EventHandler
	public void onSignEdit(SignChangeEvent event){
		for(String line:event.getLines()){
			for(String word:simplify(ChatColor.stripColor(line)).split("[,./:;-`~()\\[\\]{}+ ]"))
				if(wordList.fuzzilyContains(word)){
					swearReport(event.getPlayer().getName());
					event.getPlayer().sendMessage(config.getString("swear.sign.message").replace("<WARNING>", Integer.toString(plist.getInt("tracker."+event.getPlayer().getName()))+Ordinal.getOrdinal(plist.getInt("tracker."+event.getPlayer().getName()))).replace("<PLAYER>", event.getPlayer().getDisplayName()));
					if(config.getString("swear.sign.mode").equalsIgnoreCase("cancel")){
						event.setCancelled(true);
					}else if(config.getString("swear.sign.mode").equalsIgnoreCase("censor")){
						for(int i=0;i<3;i++){
							line.replaceAll("(?i:"+word+")", ChatColor.RESET+bleepColor+Bleeper.generateBleep(word,(List<String>) config.getList("swear.bleep.chars"))+ChatColor.RESET);
							event.setLine(i, line);
						}
					}
				}
		}
	}
	private void swearReport(String player){
		plist.set("tracker."+player, plist.getInt("tracker."+player)+1);
		try {
			((FileConfiguration) plist).save(new File("plugins/EndSwear/userData.yml"));
		} catch (IOException e) {
			out.severe("Critical save error!  Jumping system to abort.");
			plugin.getServer().getPluginManager().disablePlugin(plugin);
		}
	}
	private String simplify(String string){
		return Normalizer.normalize(string, Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase();
	}
}
