package me.FreeSpace2.EndSwear;

import java.io.Serializable;
import java.util.HashMap;

import org.bukkit.entity.Player;

public class PlayerTrackingList implements Serializable{
	private static final long serialVersionUID = 3986848284085777914L;
	private HashMap<String, Integer>data=new HashMap<String, Integer>();
	public void addSwear(Player player){
		if(data.containsKey(player.getDisplayName())){
			data.put(player.getDisplayName(),data.get(player.getDisplayName())+(Integer)1);
		}else{
			data.put(player.getDisplayName(), 1);
		}
	}
	public int getSwear(Player player){
		if(data.containsKey(player.getDisplayName())){
			return data.get(player.getDisplayName());
		}else{
			data.put(player.getDisplayName(), 1);
			return 0;
		}
	}
}
