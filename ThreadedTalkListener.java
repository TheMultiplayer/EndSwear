package me.FreeSpace2.EndSwear;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ThreadedTalkListener implements Listener {
	private FuzzyArrayList wordList=new FuzzyArrayList();
	private LocalConfiguration config;
	private MuteList muteList;
	ThreadedTalkListener(LocalConfiguration cfgMgr){
		wordList=cfgMgr.getWordList();
		config=cfgMgr;
		muteList=cfgMgr.getMuteList();
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
		String[] msg=simplify(ChatColor.stripColor(event.getMessage())).replaceAll("[,./:;!-`~()\\[\\]{}+ ]", "").split(" ");

		if(!config.getFilterUse()){
			if(config.getMatchMode().equalsIgnoreCase("fuzzy")){
				for(String word:msg){
					if(wordList.approxContains(word, config.getThreshold())){
						punish(event.getPlayer());
						break;
					}
				}
			}else if(config.getMatchMode().equalsIgnoreCase("pho")){
				for(String word:msg){
					if(wordList.phoneticMatch(word,config.getThreshold())){
						punish(event.getPlayer());
						break;
					}
				}
			}else if(config.getMatchMode().equalsIgnoreCase("reg")){
				for(String word:msg){
					if(wordList.contains(word)){
						punish(event.getPlayer());
						break;
					}
				}
			}
		}else{
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
				censor(event,words);
			};
		}
	}
	private void punish(Player player){
		config.reportSwear(player);
		config.output("Player "+player.getDisplayName()+" swore! Warning number "+config.getSwears(player)+".");
		config.addActionThread(new ActionThread(player, config.getPunishment(), config));
	}
	private void censor(AsyncPlayerChatEvent event,List<String> words){
		if(config.useBleep()){
			String msg=event.getMessage();
			for(String word:words){
				msg=msg.replaceAll("(?i:"+word+")", ChatColor.RESET+config.getBleepColor()+Bleeper.generateBleep(word,config.getBleepChars())+ChatColor.RESET);
			}
			event.setMessage(msg);
		}else{
			event.setMessage("");
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
