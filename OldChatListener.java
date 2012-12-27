package me.FreeSpace2.EndSwear;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

@SuppressWarnings("unused")
public class OldChatListener implements Listener{
	/*FuzzyStringList wordList;
	Logger out;
	Configuration config;
	String bleepColor;
	List<Future<StringMatch>> threadedReturns=new ArrayList<Future<StringMatch>>();
	ExecutorService executor = Executors.newFixedThreadPool(8);
	OldChatListener(FuzzyStringList wordList, Logger out, Configuration config){
		this.wordList=wordList;
		this.out=out;
		this.config=config;
		bleepColor=config.getString("swear.bleep.color").replaceAll("(&([a-f0-9]))", "\u00A7$2");
	}
	@EventHandler
	public synchronized void onPlayerChat(AsyncPlayerChatEvent event){
		Player player=event.getPlayer();
		String message=event.getMessage();
		boolean censor=false;
		for(String word:simplify(ChatColor.stripColor(event.getMessage())).split("[,./:;-`~()\\[\\]{}+ ]")){
			threadedReturns.add(executor.submit(new wordTester(word, wordList,config.getInt("swear.matchmode.sensitivity"))));
		}
		for(Future<StringMatch> future:threadedReturns){
			out.info(future.toString());
			StringMatch match=new StringMatch(false, null);
			try {
				match=future.get();
			} catch (Exception e) {}finally{
				
			}
			if (censor=(match.isOK() && censor)){
				message.replaceAll("(?i:"+match.matched+")", ChatColor.RESET+bleepColor+Bleeper.generateBleep(match.matched,(List<String>) config.getList("swear.bleep.chars"))+ChatColor.RESET);
			}
		}
		event.setMessage(message);
		if(censor){
			config.set(player.getDisplayName(), config.getInt(player.getDisplayName())+1);
			player.sendMessage(config.getString("swear.message").replace("<PLAYER>", player.getDisplayName()).replaceAll("(&([a-f0-9]))", "\u00A7$2"));
		}
	}
	public class wordTester implements Callable<StringMatch>{
		String word;
		FuzzyStringList wordList;
		int sensitivity;
		wordTester(String word, FuzzyStringList fzList, int sensitivity){
			this.word=word;
			this.wordList=fzList;
			this.sensitivity=sensitivity;
		}
		public synchronized StringMatch call() throws Exception {
			return wordList.phoneticMatch(word, sensitivity);
		}
	}
	private String simplify(String string){
		return Normalizer.normalize(string, Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase();
	}
	*/
}
