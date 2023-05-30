package valorless.discordchatmonitor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.luckperms.api.LuckPerms;
import valorless.valorlessutils.ValorlessUtils.*;
import valorless.valorlessutils.config.Config;


public final class DiscordChatMonitor extends JavaPlugin implements Listener {
	public static JavaPlugin plugin;
	String Name = "§7[§4DiscordChatMonitor§7]§r";
	public static Boolean enabled = true;
	public Config config;
	public static String username = "";
	
	public void onLoad() {
		plugin = this;
		ChatListener.plugin = this;
		CommandListener.plugin = this;
		config = new Config(this, "config.yml");
		ChatListener.config = config;
		CommandListener.config = config;
		
		Lang.lang = new Config(this, "lang.yml");
	
	}
	
	@Override
    public void onEnable() {
		//Config.Load(plugin);
		
		Log.Info(plugin, "Attempting to hook LuckPerms.");
		if (Bukkit.getPluginManager().getPlugin("LuckPerms") != null) {
			RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
	    	if (provider != null) {
	    		Log.Info(plugin, "LuckPerms integrated!");
	    	}else {
	    		Log.Info(plugin, "LuckPerms not detected.");
	    	}
		}else {
			Log.Info(plugin, "LuckPerms not detected.");
		}
		
		//Config
		config.AddValidationEntry("webhook-url", "");
		config.AddValidationEntry("server-icon-url", "");
		config.AddValidationEntry("server-username", "Server");
		config.AddValidationEntry("console-icon-url", "");
		config.AddValidationEntry("console-username", "Console");
		config.AddValidationEntry("timestamp", true);

		config.AddValidationEntry("server-start", true);
		config.AddValidationEntry("server-stop", true);
		config.AddValidationEntry("join", true);
		config.AddValidationEntry("leave", true);
		config.AddValidationEntry("death", true);
		config.AddValidationEntry("join-quit-player-count", true);
		config.Validate();
		
		//Lang
		Lang.lang.AddValidationEntry("console-prefix", "[§4Console§r]");
		Lang.lang.AddValidationEntry("server-start", "**Chat Enabled**");
		Lang.lang.AddValidationEntry("server-stop", "**Chat Disabled**");
		Lang.lang.AddValidationEntry("message", "%s");
		Lang.lang.AddValidationEntry("join", "**%s** has joined the server.");
		Lang.lang.AddValidationEntry("join-first-time", "**%s** has joined the server for the first time!.");
		Lang.lang.AddValidationEntry("left", "**%s** has left the server.");
		Lang.lang.AddValidationEntry("with-player-count", "%s (%s/%s)");
		Lang.lang.Validate();
		
		getServer().getPluginManager().registerEvents(new ChatListener(), this);
		getServer().getPluginManager().registerEvents(new CommandListener(), this);
		
		if(config.GetString("webhook-url") == "") {
			Log.Warning(plugin, "Please change my config.yml before using me.\nYou can reload me when needed with /dcm reload.");
			Log.Info(plugin, "§cDisabled!");
			enabled = false;
		}
		
		getCommand("dcm").setExecutor(this);
		getCommand("dcm reload").setExecutor(this);
		getCommand("server").setExecutor(this);
		
		if(!enabled) {
			for(Player player:Bukkit.getServer().getOnlinePlayers()) //Message OPs on reload or start.
			{
			    if(player.isOp()){
			            player.sendMessage(Name + " Please set me up before use, I have disabled myself.");
			    }
			}
    	}
    	
		while(username == "") {
			username = config.GetString("server-username");
		}
		
		if(!config.GetBool("server-start")) return;
    	DiscordWebhook webhook = new DiscordWebhook(config.GetString("webhook-url"));
    	
    	webhook.setUsername(username);
        webhook.setContent(Lang.Get("server-start"));
        //webhook.setAvatarUrl("https://minotar.net/armor/bust/" + player.getName() + "/100.png"); 
        webhook.setAvatarUrl(config.GetString("server-icon-url"));
        
        try {
        	//Log.Info(plugin, "Executing webhook.");
			webhook.execute();
		} catch (IOException e) {
			e.printStackTrace();
			Log.Error(plugin, "Connection failed.");
		}
    }
    
    @Override
    public void onDisable() {    	
    	if(!config.GetBool("server-stop")) return;
    	DiscordWebhook webhook = new DiscordWebhook(config.GetString("webhook-url"));
    	
    	webhook.setUsername(username);
        webhook.setContent(Lang.Get("server-stop"));
        //webhook.setAvatarUrl("https://minotar.net/armor/bust/" + player.getName() + "/100.png"); 
        webhook.setAvatarUrl(config.GetString("server-icon-url"));
        
        try {
        	//Log.Info(plugin, "Executing webhook.");
			webhook.execute();
		} catch (IOException e) {
			e.printStackTrace();
			Log.Error(plugin, "Connection failed.");
		}
    }

    
}

