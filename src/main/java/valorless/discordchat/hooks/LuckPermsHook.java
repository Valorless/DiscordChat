package valorless.discordchat.hooks;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.luckperms.api.LuckPerms;
import valorless.discordchat.Main;
import valorless.valorlessutils.ValorlessUtils.Log;

public class LuckPermsHook {
	public static RegisteredServiceProvider<LuckPerms> instance;
	
	public static void Hook() {
		JavaPlugin plugin = Main.plugin;
		
		Log.Debug(plugin, "Attempting to hook LuckPerms.");
		if (Bukkit.getPluginManager().getPlugin("LuckPerms") != null) {
			instance = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
	    	if (instance != null) {
	    		Log.Info(plugin, "LuckPerms integrated!");
	    	}else {
	    		Log.Debug(plugin, "LuckPerms not detected.");
	    	}
		}else {
			Log.Debug(plugin, "LuckPerms not detected.");
		}
	}
	
	public static LuckPerms GetProvider() {
		return instance.getProvider();
	}
	
	public static boolean isHooked() {
		if (Bukkit.getPluginManager().getPlugin("LuckPerms") != null) {
    		return true;
		}else {
			return false;
		}
	}

}
