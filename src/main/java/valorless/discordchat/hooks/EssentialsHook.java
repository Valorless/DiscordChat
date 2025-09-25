package valorless.discordchat.hooks;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import net.ess3.api.IEssentials;
import net.ess3.api.IUser;
import valorless.discordchat.Main;
import valorless.valorlessutils.ValorlessUtils.Log;

public class EssentialsHook {
	
	static IEssentials instance;
	
	public static void Hook() {
		JavaPlugin plugin = Main.plugin;
		
		Log.Debug(plugin, "Attempting to hook Essentials.");
		if (Bukkit.getPluginManager().getPlugin("Essentials") != null) {
			Plugin ess = Bukkit.getPluginManager().getPlugin("Essentials");
	        if (ess instanceof IEssentials) {
	        	instance = (IEssentials) ess;
	        }
			Bukkit.getServer().getPluginManager().registerEvents(new EssentialsAfkStatusChange(), Main.plugin);
    		Log.Info(plugin, "Essentials integrated!");
		}else {
			Log.Debug(plugin, "Essentials not detected.");
		}
	}
	
	public static boolean isHooked() {
		if (Bukkit.getPluginManager().getPlugin("Essentials") != null) {
    		return true;
		}else {
			return false;
		}
	}
	
	public static IEssentials getInstance() {
		return instance;
	}
	
	@SuppressWarnings("deprecation")
	public static List<Player> visiblePlayers(){
		List<Player> players = new ArrayList<>();
		for(Player player : Bukkit.getOnlinePlayers()) {
        	IUser pl = EssentialsHook.getInstance().getUser(player);
        	if(pl.isVanished()) continue;
        	else players.add(pl.getBase());
        }
		return players;
	}
	
	public static IUser getUser(Player player) {
		return instance.getUser(player);
	}
	
	@SuppressWarnings("deprecation")
	public static boolean isAfk(Player player) {
		IUser pl = EssentialsHook.getInstance().getUser(player);
		return pl.isAfk();
	}

}
