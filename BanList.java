package me.FreeSpace2.EndSwear;
import org.bukkit.entity.Player;
public class BanList extends OfflineSafePlayerList{
	LocalConfiguration config;
	BanList(LocalConfiguration man){
		config=man;
	}
	public synchronized void add(Player player){
		list.add(player.getDisplayName());
		config.scheduleBan(new BanUnscheduler(config,player));
	}
}
