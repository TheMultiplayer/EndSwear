package me.FreeSpace2.EndSwear;

import java.io.Serializable;
import java.util.HashMap;

import org.bukkit.entity.Player;

public class PlayerTrackingList implements Serializable{
	private static final long serialVersionUID = 3986848284085777914L;
	private HashMap<String, Integer>data=new HashMap<String, Integer>();
	public synchronized void addSwear(Player player){
		if(data.containsKey(player.getDisplayName())){
			data.put(player.getDisplayName(),data.get(player.getDisplayName())+(Integer)1);
		}else{
			data.put(player.getDisplayName(), 1);
		}
	}
	public synchronized boolean addSwear(String player){
		if(data.containsKey(player)){
			data.put(player,data.get(player)+(Integer)1);
			return true;
		}else{
			data.put(player, 1);
			return false;
		}
	}
	public int getSwear(Player player){
		if(data.containsKey(player.getDisplayName())){
			return data.get(player.getDisplayName());
		}else{
			synchronized(this){
				data.put(player.getDisplayName(), 1);
				return 1;
			}
		}
	}
}
