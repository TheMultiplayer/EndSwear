package me.FreeSpace2.EndSwear;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.logging.Logger;

import me.FreeSpace2.EndSwear.matchers.*;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class EndSwear extends JavaPlugin{
	Logger out;
	Economy econ;
	Configuration config;
	StringMatcher wordList;
	FileConfiguration plist;
	
	public void onEnable(){
		initConfig();
		out=this.getLogger();
		if(setupEconomy()){
			out.info("Vault: PASS");
		}else{
			out.info("Vault: FAIL");
		}
		if((wordList=wordList())!=null){
			out.info("Wordlist: PASS");
		}else{
			out.severe("Wordlist: FAIL");
			return;
		}
		this.getServer().getPluginManager().registerEvents(new ChatListener(wordList, out, this.getConfig(), plist, this), this);
	}
	public void onDisable(){
		out.info("Closing...");
	}
	private boolean setupEconomy() {
        if(getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
	private StringMatcher wordList(){
		File wordList=new File("plugins/EndSwear/words.txt");
		StringMatcher fuzzyList;
		switch(config.getString("matchmode.filtertype")){
			case "synon":
				fuzzyList = new SynonStringList();
				break;
			case "plaintext":
				fuzzyList = new StringList();
				break;
			case "phonetic":
				fuzzyList = new Pho
		}
		
		if(!wordList.exists()){
			try {
				wordList.createNewFile();
				PrintWriter out=new PrintWriter(new FileWriter(wordList, true));
				BufferedReader reader=new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("words.dsr")));
				String line;
				while((line=reader.readLine())!=null){
					out.println(line);
				}
				reader.close();
				out.close();
			} catch (IOException e) {
				return null;
			}
		}
		FileReader fReader;
		try {
			fReader = new FileReader(wordList);
			BufferedReader reader=new BufferedReader(fReader);
			String line;
			while((line=reader.readLine())!=null){
				fuzzyList.add(line);
			}
			fReader.close();
		} catch (Exception e) {
			out.severe("Critical load failure! Hooking system to abort.");
			e.printStackTrace();
			return null;
		}
		return fuzzyList;
	}
	public boolean addWord(String word){
		File wordListLoc=new File("plugins/EndSwear/words.txt");
		if(!wordList.contains(word)){
			try {
				PrintWriter out=new PrintWriter(new FileWriter(wordListLoc, true));
				out.println(word);
				out.close();
			} catch (IOException e) {
				return false; 
			}
			wordList.add(word);
			return true;
		}
		return false;
	}
	private void initConfig(){
		config=this.getConfig();
		String[] bleepChars={"!","@","#","%","\\$","\\*"};
		config.addDefault("inform", true);
		config.addDefault("swear.matchmode.substring", true);
		config.addDefault("swear.message", "&cNo swearing, <PLAYER>! This is your <WARNING> warning!");
		config.addDefault("swear.bleep.color", "&7&k");
		config.addDefault("swear.bleep.chars", Arrays.asList(bleepChars));
		config.addDefault("swear.ban.time", 300);
		config.addDefault("swear.mute.time", 60);
		config.addDefault("swear.damage", 5);
		config.addDefault("swear.fine", 15);
		if(config.getConfigurationSection("swear.action")==null){
			config.addDefault("swear.action.1-2", "warn");
			config.addDefault("swear.action.3-5", "kick");
			config.addDefault("swear.action.5-8", "mute");
			config.addDefault("swear.action.8+", "ban");
		};
		config.options().copyDefaults(true);
		this.saveConfig();
		File customConfigFile = new File("plugins/EndSwear/userData.yml");
		if(!customConfigFile.exists()){
			try {
				customConfigFile.createNewFile();
			} catch (IOException e) {
				out.severe("Userdata: FAIL");
				return;
			}
		}
		plist = YamlConfiguration.loadConfiguration(customConfigFile);
	}
	public void savePlayerList(){
		try {
			plist.save(new File("plugins/EndSwear/userData.yml"));
		} catch (IOException e) {
			out.severe("Critical save error with user data!");
		}
	}
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if (cmd.getName().equalsIgnoreCase("swear") && args.length>0){
			if(args[0].equalsIgnoreCase("list") && sender.hasPermission("EndSwear.list")){
				String sqr="";
				for(String[] str:wordList){
					for(int i=0;i<str.length;i++){
						sqr=sqr+str[i];
					}
					sender.sendMessage(sqr);
					sqr="";
				}
				return true;
			}else if(args[0].equalsIgnoreCase("pardon") && sender.hasPermission("EndSwear.pardon")){
				if (this.getServer().getOfflinePlayer(args[1]) != null){
					config.set("tracker."+this.getServer().getOfflinePlayer(args[1]).getName(), 0);
					sender.sendMessage(ChatColor.GREEN+"Pardoned!");
				}
				return true;
			}else if(args[0].equalsIgnoreCase("add")  && sender.hasPermission("EndSwear.add")){
				if (!wordList.contains(args[1])){
					addWord(args[1]);
					sender.sendMessage(ChatColor.GREEN+"Word added!");
				}else{
					sender.sendMessage(ChatColor.RED+"That word is already in the dictionary!");
				}
				return true;
			}else if(args[0].equalsIgnoreCase("info")  && sender.hasPermission("EndSwear.info")){
				sender.sendMessage("Player "+this.getServer().getOfflinePlayer(args[1]).getName()+" has sworn "+config.getInt("tracker."+this.getServer().getOfflinePlayer(args[1]).getName())+" times.");
				return true;
			}else if(args[0].equalsIgnoreCase("contains")  && sender.hasPermission("EndSwear.contains")){
				if(wordList.fuzzilyContains(args[1])){
					sender.sendMessage("That word "+ChatColor.GREEN+"has "+ChatColor.RESET+"a dictionary match!");
				}else{
					sender.sendMessage("That word "+ChatColor.RED+"lacks"+ChatColor.RESET+" a dictionary match!");
				}
				return true;
			}
		}else{
			sender.sendMessage(ChatColor.GOLD+""+ChatColor.UNDERLINE+"EndSwear"+ChatColor.RESET+""+ChatColor.LIGHT_PURPLE+" v"+this.getDescription().getVersion());
			return true;
		}
		return false;
	}
}
