package me.FreeSpace2.EndSwear;
import java.util.ArrayList;
import java.util.List;


import org.bukkit.entity.Player;


public class OfflineSafePlayerList {
	protected List<String>list=new ArrayList<String>();
	public synchronized void add(Player player){
		list.add(player.getDisplayName());
	}
	public synchronized void remove(Player player){
		list.remove(player.getDisplayName());
	}
	public synchronized void remove(String player){
		list.remove(player);
	}
	public boolean isOnList(Player player){
		return list.contains(player.getDisplayName());
	}
}
