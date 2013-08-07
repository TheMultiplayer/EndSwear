package me.FreeSpace2.EndSwear;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;

import me.FreeSpace2.EndSwear.matchers.PhoneticStringList;
import me.FreeSpace2.EndSwear.matchers.StringList;
import me.FreeSpace2.EndSwear.matchers.SynonStringList;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

public class EndSwear extends JavaPlugin{
	Configuration config;
	Configuration playerlist;
	StringMatcher blacklist;
	StringMatcher whitelist;
	File blacklistLocation;
	File whitelistLocation;
	File userdataLocation;
	Economy econ;
	public void onEnable(){
		blacklistLocation=new File(getDataFolder().getAbsolutePath()+"/words.txt");
		whitelistLocation=new File(getDataFolder().getAbsolutePath()+"/whitelist.txt");
		userdataLocation=new File(getDataFolder().getAbsolutePath()+"/userData.yml");
		initConfig();
		initUserData();
		copyWordList();
		copyWhiteList();
		switch(config.getString("swear.matchmode.filtertype")){
			case "plaintext":
				blacklist=new StringList();
				break;
			case "synon":
				blacklist=new SynonStringList();
				break;
			case "phonetic":
				blacklist=new PhoneticStringList();
				break;
			default:
				getLogger().info("Initializing phonetic filter, as no valid filter was provided.");
				blacklist=new PhoneticStringList();
				break;
		}
		whitelist=new StringList();
		if(loadFromFile(blacklistLocation,blacklist)){
			getLogger().info("Wordlist: PASS");
		}else{
			getLogger().severe("Wordlist: FAIL");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		if(loadFromFile(whitelistLocation,whitelist)){
			getLogger().info("Whitelist: PASS");
		}else{
			getLogger().severe("Whitelist: FAIL");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		if(setupEconomy()){
			getLogger().info("Vault: PASS");
		}else{
			getLogger().info("Vault: FAIL");
		}
		if(initMetrics()){
			getLogger().info("Metrics: PASS");
		}else{
			getLogger().info("Metrics: FAIL");
		}
		if (config.getBoolean("swear.sign.enabled")){
			this.getServer().getPluginManager().registerEvents(new SignListener(blacklist,this), this);
		}
		this.getServer().getPluginManager().registerEvents(new ChatListener(blacklist,whitelist,this), this);
		if (config.getBoolean("swear.autopardon.enable")){
			getLogger().info("Autopardon: PASS");
			getServer().getScheduler().scheduleSyncRepeatingTask(this, new Pardoner(playerlist, config.getInt("swear.autopardon.minimum")), 0, config.getLong("swear.autopardon.interval")*20L*60L);
		}else{
			getLogger().info("Autopardon: FAIL");
		}
	}
	public void onDisable(){
		getLogger().info("Disabling listeners...");
		HandlerList.unregisterAll(this);
	}
	private boolean initMetrics(){
		try {
		    Metrics metrics = new Metrics(this);
		    metrics.start();
		    return true;
		} catch (IOException e) {
		    return false;
		}
	}
	private void initUserData() {
		if(!userdataLocation.exists()){
			try {
				userdataLocation.createNewFile();
				playerlist = YamlConfiguration.loadConfiguration(userdataLocation);
				getLogger().info("Userdata: PASS");
			} catch (IOException e) {
				getLogger().severe("Userdata: FAIL");
				return;
			}
		}else{
			playerlist = YamlConfiguration.loadConfiguration(userdataLocation);
			getLogger().info("Userdata: PASS");
		}
	}
	private void initConfig(){
		config=this.getConfig();
		String[] bleepChars={"!","@","#","%","\\$","\\*"};
		config.addDefault("version", this.getDescription().getVersion());
		config.addDefault("swear.matchmode.filtertype", "phonetic");
		config.addDefault("swear.bleep.color", "&7&k");
		config.addDefault("swear.bleep.chars", Arrays.asList(bleepChars));
		config.addDefault("swear.message", "&cNo swearing, <PLAYER>! This is your <WARNING> warning!");
		config.addDefault("swear.ban.message.temporary","&cNo swearing, <PLAYER>! You have been tempbanned!");
		config.addDefault("swear.ban.message.permanent","&cNo swearing, <PLAYER>! You have been banned!");
		config.addDefault("swear.sign.enabled", true);
		config.addDefault("swear.sign.mode", "cancel");
		config.addDefault("swear.sign.message", "Do not put swear words on signs, <PLAYER>! This is your <WARNING> warning!");
		config.addDefault("swear.autopardon.enable", true);
		config.addDefault("swear.autopardon.minimum", 2);
		config.addDefault("swear.autopardon.interval", 360);
		if(config.getConfigurationSection("swear.action")==null){
			config.addDefault("swear.action.1-2", "warn");
			config.addDefault("swear.action.3-5", "kick");
			config.addDefault("swear.action.5-8", "mute,30");
			config.addDefault("swear.action.8-10", "ban,60");
			config.addDefault("swear.action.10-12", "ban,120");
		};
		config.options().copyDefaults(true);
		this.saveConfig();
	}
	private boolean copyWordList(){
		if(!blacklistLocation.exists()){
			try {
				blacklistLocation.createNewFile();
				PrintWriter out=new PrintWriter(new FileWriter(blacklistLocation, true));
				BufferedReader reader=new BufferedReader(new InputStreamReader(getClass().getResource("words.dsr").openStream()));
				String line;
				while((line=reader.readLine())!=null){
					out.println(line);
				}
				reader.close();
				out.close();
			} catch (IOException e) {
				return false;
			}
		}
		return true;
	}
	private boolean copyWhiteList(){
		if(!whitelistLocation.exists()){
			try {
				whitelistLocation.createNewFile();
				PrintWriter out=new PrintWriter(new FileWriter(whitelistLocation, true));
				BufferedReader reader=new BufferedReader(new InputStreamReader(getClass().getResource("whitelist.dsr").openStream()));
				String line;
				while((line=reader.readLine())!=null){
					out.println(line);
				}
				reader.close();
				out.close();
			} catch (IOException e) {
				return false;
			}
		}
		return true;
	}
	private boolean loadFromFile(File file,StringMatcher list){	
		FileReader fReader;
		try {
			fReader = new FileReader(file);
			BufferedReader reader=new BufferedReader(fReader);
			String line;
			while((line=reader.readLine())!=null){
				list.add(line);
			}
			fReader.close();
		} catch (Exception e) {
			getLogger().severe("Critical load failure! Hooking system to abort.");
			getServer().getPluginManager().disablePlugin(this);
			e.printStackTrace();
			return false;
		}
		return true;
		
	}
	@SuppressWarnings("unused")
	private void writeBack(File file, StringMatcher list){
		try{
			BufferedWriter writer=new BufferedWriter(new FileWriter(file, true));
			for(String str:list){
				writer.write(str);
			}
			writer.close();
		} catch (IOException e) {
			return;
		}
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
	public boolean addWord(String word,File file){
		try {
			PrintWriter out=new PrintWriter(new FileWriter(file, true));
			out.println(word);
			out.close();
		} catch (IOException e) {
			return false; 
		}
		return true;
	}
	private enum EndSwearCommand{
		RELOAD,ADD,REMOVE,WHITELIST,INFO,CONTAINS,PARDON,LIST,DEBUG,SHARE,HELP,VERSION
	}
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(cmd.getName().equalsIgnoreCase("swear")){
			EndSwearCommand com;
			boolean arguments=args.length>1;
			try{
				com=EndSwearCommand.valueOf(args[0].toUpperCase());
			}catch (Exception e){
				com=EndSwearCommand.HELP;
			}
			switch(com){
				case DEBUG:
					if(sender.isOp()){
						sender.sendMessage("DEBUG INFORMATION:\nBLACKLIST:\n"+blacklistLocation+"\nUSERDATA:"+userdataLocation);
					}
					return true;
				case ADD:
					if (arguments & sender.hasPermission("EndSwear.add")){
						if (!blacklist.contains(args[1])){
							blacklist.add(args[1]);
							addWord(args[1],blacklistLocation);
							sender.sendMessage(ChatColor.GREEN+"Word added!");
						}else{
							sender.sendMessage(ChatColor.RED+"That word is already in the dictionary!");
						}
						return true;
					} 
					break;
				case CONTAINS:
					if(arguments & sender.hasPermission("EndSwear.contains")){
						if (blacklist.fuzzilyContains(args[1])){
							sender.sendMessage("That word "+ChatColor.GREEN+"has "+ChatColor.RESET+"a dictionary match!");
						}else{
							sender.sendMessage("That word "+ChatColor.RED+"lacks"+ChatColor.RESET+" a dictionary match!");
						}
					};
					return true;
				case INFO:
					if(arguments & sender.hasPermission("EndSwear.info")){
						sender.sendMessage("Player "+this.getServer().getOfflinePlayer(args[1]).getName()+" has sworn "+playerlist.getInt("tracker."+this.getServer().getOfflinePlayer(args[1]).getName())+" times.");
						return true;
					}
				case LIST:
					if (sender.hasPermission("EndSwear.list")){
						sender.sendMessage("Listing:");
						String list="";
						for(String str:blacklist){
							list=list+", "+str;
						}
						try{
							sender.sendMessage((String) list.subSequence(1, list.length()));
						}catch (Exception e){
							sender.sendMessage(list);
						}
						return true;
					}
				case PARDON:
					if(arguments & sender.hasPermission("EndSwear.pardon")){
						if (this.getServer().getOfflinePlayer(args[1]) != null){
							playerlist.set("tracker."+this.getServer().getOfflinePlayer(args[1]).getName(), 0);
							sender.sendMessage(ChatColor.GREEN+"Pardoned!");
						}
					}
					return true;
				case RELOAD:
					if(sender.hasPermission("EndSwear.reload")){
						initUserData();
						this.reloadConfig();
						sender.sendMessage(ChatColor.GREEN+"EndSwear reload complete!");
						return true;
					}
				case REMOVE:
					break;
				case WHITELIST:
					if (arguments & sender.hasPermission("EndSwear.add")){
						if (!whitelist.contains(args[1])){
							whitelist.add(args[1]);
							addWord(args[1],whitelistLocation);
							sender.sendMessage(ChatColor.GREEN+"Word whitelisted!");
						}else{
							sender.sendMessage(ChatColor.RED+"That word is already in the whitelist!");
						}
						return true;
					} 
					break;
				case SHARE:
					break;
				case HELP:
					getServer().dispatchCommand(sender, "help EndSwear");
					return true;
				case VERSION:
					sender.sendMessage(ChatColor.GOLD+""+ChatColor.UNDERLINE+"EndSwear v"+this.getDescription().getVersion());
					return true;
			}
		}
		getServer().dispatchCommand(sender, "help EndSwear");
		return true;
	}
}
