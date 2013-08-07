package me.FreeSpace2.EndSwear;

import org.bukkit.configuration.Configuration;

public class Pardoner implements Runnable{
	Configuration playerlist;
	int minimum;
	Pardoner(Configuration playerlist, int l){
		this.playerlist=playerlist;
		this.minimum=l;
	}
	public void run() {
		for(String key:playerlist.getConfigurationSection("tracker").getKeys(true)){
			String ultra="tracker."+key;
			System.out.println("[EndSwear] Executing pardon for "+key);
			if(playerlist.getInt(ultra)>minimum){
				playerlist.set(ultra,playerlist.getInt(ultra)-1);
			}
		}
	}
}
