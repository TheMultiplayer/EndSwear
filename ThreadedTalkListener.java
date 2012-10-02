package me.FreeSpace2.EndSwear;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ThreadedTalkListener implements Listener {
	private FuzzyArrayList wordList=new FuzzyArrayList();
	private LocalConfiguration config;
	private OfflineSafePlayerList muteList;
	private PlayerTrackingList trackerList;
	private Logger log;
	ThreadedTalkListener(LocalConfiguration cfgMgr){
		wordList=cfgMgr.getWordList();
		config=cfgMgr;
		muteList=cfgMgr.getBanMuteList().getMuteList();
		trackerList=config.getTracker();
		log=config.getLog();
	}
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event){
		if(event.getPlayer().hasPermission("EndSwear.bypass")){
			return;
		}
		if(muteList.isOnList(event.getPlayer())){
			event.setCancelled(true);
			return;
		}
		String[] msg=simplify(ChatColor.stripColor(event.getMessage())).split("[,./:;-`~()\\[\\]{}+ ]");
		List<String> words=new ArrayList<String>();
		if(config.getMatchMode().equalsIgnoreCase("fuzzy")){
			for(String word:msg){
				if(wordList.approxContains(word, config.getThreshold())){
					words.add(word);
				}
			}
		}else if(config.getMatchMode().equalsIgnoreCase("pho")){
			for(String word:msg){
				if(wordList.phoneticMatch(word, config.getThreshold())){
					words.add(word);
				}
			}
		}else if(config.getMatchMode().equalsIgnoreCase("reg")){
			for(String word:msg){
				if(wordList.contains(word)){
					words.add(word);
				}
			}
		}
		if(!words.isEmpty()){
			punish(event.getPlayer());
			censor(event,words,event.getPlayer());
		};
	}
	private void punish(Player player){
		trackerList.addSwear(player);
		
		config.addActionThread(new ActionThread(player, config.getPunishment(), config));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private void censor(AsyncPlayerChatEvent event,List<String> words,Player player){
		log.info("Player "+player.getDisplayName()+" swore! Warning number "+config.getSwears(player)+".  Words:");
		for(String word:words){
			config.output(word);
		}
		if(config.useBleep()){
			String msg=event.getMessage();
			for(String word:words){
				msg=msg.replaceAll("(?i:"+word+")", ChatColor.RESET+config.getBleepColor()+Bleeper.generateBleep(word,config.getBleepChars())+ChatColor.RESET);
			}
			event.setMessage(msg);
		}else{
			event.setCancelled(true);
		}
	}
	
	
	
	
	private String simplify(String string){
		return removeDiacriticalMarks(string).toLowerCase();
	}
    private static String removeDiacriticalMarks(String string) {
        return Normalizer.normalize(string, Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }
}
