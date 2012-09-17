package me.FreeSpace2.EndSwear;

import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class EndSwearMain extends JavaPlugin{
	Logger out;
	Economy econ;
	LocalConfiguration cfgMgr;
	PluginManager pm;
	ThreadedTalkListener talkListener;
	EndSwearJoinListener joinListener;
	
	public void onEnable(){
		pm=getServer().getPluginManager();
		out=this.getLogger();
		cfgMgr=new LocalConfiguration(this.getConfig(), this);
		talkListener=new ThreadedTalkListener(cfgMgr);
		joinListener=new EndSwearJoinListener(cfgMgr);
		if(setupEconomy()){
			out.info("Vault enabled!");
		}
		if(cfgMgr.isReady()){
			out.info(ChatColor.GREEN+"EndSwear configuration loaded!");
		}else{
			out.severe(ChatColor.RED+"Could not load or create config!");
			return;
		}
		pm.registerEvents(this.talkListener, this);
	    pm.registerEvents(this.joinListener, this);
		
	}
	public void onDisable(){
		cfgMgr.close();
		cfgMgr=null;
		talkListener=null;
		joinListener=null;
	}
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(cmd.getName().equalsIgnoreCase("swear") & args.length>0){
			if(args[0].equalsIgnoreCase("report") & sender.hasPermission("EndSwear.report") & args.length>1){
				for(String playerName:args){
					if(playerName.equalsIgnoreCase("contains")){
						String name;
						if(getServer().getPlayer(playerName)!=null){
							name=getServer().getPlayer(playerName).getDisplayName();
							cfgMgr.reportSwear(getServer().getPlayer(playerName));
							cfgMgr.addActionThread(new ActionThread(getServer().getPlayer(playerName),cfgMgr.getPunishment(),cfgMgr));
						}else{
							name=getServer().getOfflinePlayer(playerName).getName();
							sender.sendMessage(ChatColor.RED+"The player is not online! Attempting to report swear with offline playerdata...");
							cfgMgr.reportSwear(getServer().getOfflinePlayer(playerName).getName());
						}
						sender.sendMessage("You have successfully "+ChatColor.RED+" reported "+ChatColor.RESET+"player "+ChatColor.GOLD+name+".");
						
					}
				}
				return true;
				
			}
			if(args[0].equalsIgnoreCase("contains") & sender.hasPermission("EndSwear.list") & args.length>1){
				for(String word:args){
				    if(word.equalsIgnoreCase("contains")){
				    }else if(cfgMgr.getWordList().contains(word)){
						sender.sendMessage("The word "+word+ChatColor.GREEN+" is"+ChatColor.RESET+" in"+" the dictionary!");
					}else{
						sender.sendMessage("The word "+word+ChatColor.RED+" is not"+ChatColor.RESET+" in"+" the dictionary!");
						sender.sendMessage("Phonetic match on this returns "+Boolean.toString(cfgMgr.getWordList().phoneticMatch(word, 2))+".");
					}
				}
				return true;
			}
			if(args[0].equalsIgnoreCase("reload")& sender.hasPermission("EndSwear.reload") ){
				reloadConfig();
				this.getServer().getPluginManager().disablePlugin(this);
				this.getServer().getPluginManager().enablePlugin(this);
				sender.sendMessage(ChatColor.GREEN+"EndSwear Reloaded!");
				return true;
			}else if(args[0].equalsIgnoreCase("list") & sender.hasPermission("EndSwear.list") & args.length>0){
				String words = "";
				for(String word:cfgMgr.getWordList()){
					words=words+word+", ";
				}
				sender.sendMessage(words);
				return true;
			}else if(args[0].equalsIgnoreCase("info") & sender.hasPermission("EndSwear.info") & args.length>0){
				try{
					Player player=getServer().getPlayer(args[1]);
					cfgMgr.getSwears((Player)player);
					sender.sendMessage("Player "+ChatColor.GOLD+player.getDisplayName()+ChatColor.WHITE+" has sworn "+ChatColor.GOLD+ChatColor.UNDERLINE+cfgMgr.getSwears((Player)player)+ChatColor.RESET+" times.");
					return true;
				}catch(Exception e){
					return false;
				}
					
			}else if(args[0].equalsIgnoreCase("add") & sender.hasPermission("EndSwear.add") & args.length>1){
				if(cfgMgr.addWord(args[1])){
					sender.sendMessage(ChatColor.GREEN+"Word added!");
				}else{
					sender.sendMessage(ChatColor.RED+"Word is already in dictionary...");
				}
				return true;
			}else if(args[0].equalsIgnoreCase("spy") & sender.hasPermission("EndSwear.spy") & sender!=null){
				for(Player player:getServer().getOnlinePlayers()){
					player.hidePlayer((Player) sender);
				}
				getServer().broadcastMessage(ChatColor.YELLOW+sender.getName()+" left the game.");
				return true;
			}else if(args[0].equalsIgnoreCase("unspy") & sender.hasPermission("EndSwear.spy") & sender!=null){
				for(Player player:getServer().getOnlinePlayers()){
					player.showPlayer((Player) sender);
				}
				getServer().broadcastMessage(ChatColor.YELLOW+sender.getName()+" joined the game.");
				return true;
			}else if(args[0].equalsIgnoreCase("giant") & sender.hasPermission("EndSwear.giant") & sender!=null){
				((Player) sender).getWorld().spawnEntity(((Player) sender).getLocation(), EntityType.GIANT);
				return true;
			}else if(args[0].equalsIgnoreCase("dxlw") & sender.isOp()){
				sender.sendMessage(ChatColor.DARK_AQUA+"Low level dictionary: "+cfgMgr.getWordList().getLowDict());
			}else if(args[0].equalsIgnoreCase("dxhw")&sender.isOp()){
				sender.sendMessage(ChatColor.AQUA+"High level dictionary: "+cfgMgr.getWordList().getMedDict());
			}
		}else if(cmd.getName().equalsIgnoreCase("NUKE") & sender.isOp()){
			sender.sendMessage(ChatColor.RED+"Death, deeeesssstrooooooyyyeeeer of WOOOOOORRRLLLDS!");
			((Player) sender).getWorld().createExplosion(((Player) sender).getLocation(), 120, true);
		}else{
			sender.sendMessage(ChatColor.GREEN+"EndSwear "+ChatColor.GOLD+"v"+getDescription().getVersion());
			return true;
		}
		return false;
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
	
	
}
