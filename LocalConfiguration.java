package me.FreeSpace2.EndSwear;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.Arrays;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

public class LocalConfiguration {
	private BanList banList=new BanList(this);
	private MuteList muteList=new MuteList(this);
	private boolean status=true;
	private Configuration config;
	private Plugin plugin;
	private File dataDir=new File("plugins/EndSwear/");
	private File wordList=new File(dataDir+"/words.txt");
	private File playerData=new File(dataDir+"/players.ser");
	private FuzzyArrayList fzList=new FuzzyArrayList();
	private PlayerTrackingList tracker;
	private Economy econ;
	private boolean econEnabled;
	LocalConfiguration(Configuration config, Plugin plugin){
		this.plugin=plugin;
		this.config=config;
		merge();
		wordList();
		playerTrack();
		econEnabled=setupEconomy();
	}
	private void merge(){
		String[] bleepChars={"!","@","#","%","\\$","\\*"};
		config.addDefault("inform", true);
		config.addDefault("swear.matchmode", "pho");
		config.addDefault("swear.threshold", 2);
		config.addDefault("swear.action", "kick");
		config.addDefault("swear.warnings", 2);
		config.addDefault("swear.censor.enabled", true);
		config.addDefault("swear.censor.mode","bleep");
		config.addDefault("swear.message", "&dNo swearing, <PLAYER>! You have <WARNING> warnings left!");
		config.addDefault("swear.bleep.color", "&7");
		config.addDefault("swear.bleep.chars", Arrays.asList(bleepChars));
		config.addDefault("swear.fine.amount",5.00);
		config.addDefault("swear.fine.type", "const");
		config.addDefault("swear.kick.message", "&dNo swearing, <PLAYER>!");
		config.addDefault("swear.ban.time", 60);
		config.addDefault("swear.ban.message", "&dNo swearing, <PLAYER>! You have been &ctempbanned!");
		config.addDefault("swear.mute.time", 60);
		config.addDefault("swear.mute.message", "&dNo swearing, <PLAYER>! You have been &cmuted!");
		config.addDefault("swear.damage.amount", 2);
		config.options().copyDefaults(true);
		plugin.saveConfig();
	}
	private void wordList(){
		if(!wordList.exists()){
			try {
				wordList.createNewFile();
			} catch (IOException e) {
				status=false;
			}
		}
		FileReader fReader;
		try {
			fReader = new FileReader(wordList);
			BufferedReader reader=new BufferedReader(fReader);
			String line;
			while((line=reader.readLine())!=null){
				fzList.add(line);
			}
		} catch (Exception e) {status=false;};
		fzList.genDictionary();
	}
	
	public boolean isReady(){
		return status;
	}
	public FuzzyArrayList getWordList(){
		return fzList;
	}
	public boolean addWord(String word){
		try {
			PrintWriter out=new PrintWriter(new FileWriter(wordList, true));
			out.println(word);
			out.close();
		} catch (IOException e) {
			return false; 
		}
		fzList.add(word);
		return true;
	}
	public String getPunishment(){
		return config.getString("swear.action");
	}
	public double getFine(Player player){
		String equation=config.getString("swear.fine.type");
			if(equation.equalsIgnoreCase("const")){
				return plugin.getConfig().getDouble("swear.fine.amount");
			};
			int n=tracker.getSwear(player);
			if(equation.equalsIgnoreCase("squ")){
				return config.getDouble("swear.fine.amount")*n*n;
			} 
			if(equation.equalsIgnoreCase("line")){
				return config.getDouble("swear.fine.amount")*n;
			}
			return 0;
	}
	private void playerTrack(){
		if(playerData.exists()){
			try {
				ObjectInputStream is = new ObjectInputStream(new FileInputStream(playerData));
				tracker=(PlayerTrackingList) is.readObject();
			} catch(Exception e) {
				tracker=new PlayerTrackingList();
				playerData.delete();
			}
		}else{
			tracker=new PlayerTrackingList();
		}
	}
	public boolean close(){
		try{
			if(playerData.exists()){
				playerData.delete();
			}
			ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(playerData));
			os.writeObject(tracker);
		}catch(Exception e){
			return false;
		}
		return true;
	}
	private boolean setupEconomy() {
        if(plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
	public boolean isVaultEnabled(){
		return econEnabled;
	}
	public Economy getEconomy(){
		return econ;
	}
	public synchronized void output(Object obj){
		plugin.getLogger().info((String) obj);
	}
	public synchronized void reportSwear(Player player){
			tracker.addSwear(player);
	}
	public int getSwears(Player player){
		return tracker.getSwear(player);
	}
	public boolean getFilterUse(){
		return config.getBoolean("swear.censor.enabled");
	}
	public String getMatchMode(){
		return config.getString("swear.matchmode");
	}
	public boolean getSendJoinMessage(){
		return config.getBoolean("inform");
	}
	public void scheduleBan(Runnable runner){
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, runner, config.getLong("swear.ban.time")*20L);
	}
	public void scheduleMute(Runnable runner){
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, runner, config.getLong("swear.mute.time")*20L);
	}
	public int getLimit(){
		return config.getInt("swear.warnings");
	}
	public boolean playerExceededWarnings(Player player){
		return ((getSwears(player)%(config.getInt("swear.warnings")+1))==config.getInt("swear.warnings"));
	}
	public byte warningsLeft(Player player){
		return (byte) (getSwears(player)%(config.getInt("swear.warnings")+1));
	}
	public BanList getBanList(){
		return banList;
	}
	public MuteList getMuteList(){
		return muteList;
	}
	public String getKickMessage(){
		return config.getString("swear.kick.message").replaceAll("(&([a-f0-9]))", "\u00A7$2");
	}
	public String getBanMessage(){
		return config.getString("swear.ban.message").replaceAll("(&([a-f0-9]))", "\u00A7$2");
	}
	public String getMuteMessage(){
		return config.getString("swear.mute.message").replaceAll("(&([a-f0-9]))", "\u00A7$2");
	}
	public String[]getBleepChars(){
		String[]bleeps=new String[config.getList("swear.bleep.chars").size()];
		for(Object bleepChar:config.getList("swear.bleep.chars")){
			bleeps[config.getList("swear.bleep.chars").indexOf(bleepChar)]=(String) bleepChar;
		}
		return bleeps;
	}
	public byte getDamage(){
		return (byte) config.getInt("swear.damage.amount");
	}
	public void addActionThread(ActionThread processor){
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, processor);
	}
	public String getBleepColor(){
		return config.getString("swear.bleep.color").replaceAll("(&([a-f0-9]))", "\u00A7$2");
	}
	public byte getThreshold(){
		return (byte)config.getInt("swear.threshold");
	}
	public String getWarnMessage(){
		return config.getString("swear.message").replaceAll("(&([a-f0-9]))", "\u00A7$2");
	}
	public boolean useBleep(){
		if(config.getString("swear.censor.mode").equalsIgnoreCase("bleep")){
			return true;
		}
		return false;
	}
}
