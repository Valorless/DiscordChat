package valorless.discordchatmonitor;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import valorless.discordchatmonitor.hooks.*;
import valorless.valorlessutils.ValorlessUtils.*;
import valorless.valorlessutils.config.Config;


public final class DiscordChatMonitor extends JavaPlugin implements Listener {
	public static JavaPlugin plugin;
	String Name = "§7[§4DiscordChatMonitor§7]§r";
	public static Boolean enabled = true;
	public static Config config;
	public static String username = "";
	public static boolean error = false;
    
    public String[] commands = {
    		"discordchatmonitor", "dcm", "server"
    };
	
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
		Hooks();
		
		//Config
		config.AddValidationEntry("debug", false);
		config.AddValidationEntry("webhook-url", "");
		config.AddValidationEntry("server-icon-url", "");
		config.AddValidationEntry("server-username", "Server");
		config.AddValidationEntry("console-icon-url", "https://haven.netherrain.net/media/images/console-icon.png");
		config.AddValidationEntry("console-username", "Console");
		config.AddValidationEntry("player-username", "%player%");

		config.AddValidationEntry("server-start", true);
		config.AddValidationEntry("server-stop", true);
		config.AddValidationEntry("join", true);
		config.AddValidationEntry("leave", true);
		config.AddValidationEntry("death", true);
		config.AddValidationEntry("join-quit-player-count", true);
		config.Validate();
		
		//Lang
		Lang.lang.AddValidationEntry("console-prefix", "[§4Console§r]");
		Lang.lang.AddValidationEntry("message", "%timestamp% %message%");
		Lang.lang.AddValidationEntry("server-start", "%timestamp% **Chat Enabled**");
		Lang.lang.AddValidationEntry("server-stop", "%timestamp% **Chat Disabled**");
		Lang.lang.AddValidationEntry("server-reconnect", "%timestamp% **Chat Reconnected**");
		Lang.lang.AddValidationEntry("join", "%timestamp% **%player%** has joined the server.");
		Lang.lang.AddValidationEntry("join-first-time", "%timestamp% **%player%** has joined the server for the first time!.");
		Lang.lang.AddValidationEntry("leave", "%timestamp% **%player%** has left the server.");
		Lang.lang.AddValidationEntry("with-player-count", "%message% (%player-count%/%player-count-max%)");
		Lang.lang.Validate();
		
		getServer().getPluginManager().registerEvents(new ChatListener(), this);
		//getServer().getPluginManager().registerEvents(new CommandListenerOld(), this);
		
		if(config.GetString("webhook-url") == "") {
			Log.Warning(plugin, "Please change my config.yml before using me.\nYou can reload me when needed with /dcm reload.");
			Log.Info(plugin, "§cDisabled!");
			enabled = false;
		}
		
		RegisterCommands();
		
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
        webhook.setAvatarUrl(config.GetString("server-icon-url"));
        
        try {
        	//Log.Info(plugin, "Executing webhook.");
			webhook.execute();
		} catch (IOException e) {
			e.printStackTrace();
			Log.Error(plugin, "Connection failed.");
        	DiscordChatMonitor.error = true;
        	Log.Error(plugin, "Plugin disabled to avoid further failed connections.");
        	Log.Error(plugin, "Please reload the plugin to re-enable");
		}
    }
    
    @Override
    public void onDisable() {    	
    	if(!config.GetBool("server-stop")) return;
    	DiscordWebhook webhook = new DiscordWebhook(config.GetString("webhook-url"));
    	
    	webhook.setUsername(username);
        webhook.setContent(Lang.Get("server-stop"));
        webhook.setAvatarUrl(config.GetString("server-icon-url"));
        
        try {
        	//Log.Info(plugin, "Executing webhook.");
			webhook.execute();
		} catch (IOException e) {
			e.printStackTrace();
			Log.Error(plugin, "Connection failed.");
        	DiscordChatMonitor.error = true;
        	Log.Error(plugin, "Plugin disabled to avoid further failed connections.");
        	Log.Error(plugin, "Please reload the plugin to re-enable");
		}
    }
    
    void Hooks() {
    	LuckPermsHook.Hook();
    	PlaceholderAPIHook.Hook();
    }
    
    public void RegisterCommands() {
    	 for (int i = 0; i < commands.length; i++) {
    		Log.Debug(plugin, "Registering Command: " + commands[i]);
    		getCommand(commands[i]).setExecutor(new CommandListener());
    	}
    }

    
}

