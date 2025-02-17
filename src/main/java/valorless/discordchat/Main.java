package valorless.discordchat;

import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import valorless.discordchat.discord.Bot;
import valorless.discordchat.hooks.*;
import valorless.discordchat.utils.InventoryImageGenerator;
import valorless.discordchat.utils.Metrics;
import valorless.valorlessutils.ValorlessUtils.*;
import valorless.valorlessutils.config.Config;


public final class Main extends JavaPlugin implements Listener {
	public static JavaPlugin plugin;
	String Name = "§7[§4DiscordChat§7]§r";
	public static Boolean enabled = true;
	public static Config config;
	public static Config filter;
	public static Config muted;
	public static String username = "";
	public static boolean error = false;
	public static Bot bot;
    
    public String[] commands = {
    		"discordchat", "dcm", "dc", "server"
    };
	
	public void onLoad() {
		plugin = this;
		ChatListener.plugin = this;
		CommandListener.plugin = this;
		config = new Config(this, "config.yml");
		filter = new Config(this, "blocked-words.yml");
		muted = new Config(this, "muted.yml");
		ChatListener.config = config;
		
		Lang.lang = new Config(this, "lang.yml");
	
	}
	
	@Override
    public void onEnable() {	
        // All you have to do is adding the following two lines in your onEnable method.
        // You can find the plugin ids of your plugins on the page https://bstats.org/what-is-my-plugin-id
        int pluginId = 18793; // <-- Replace with the id of your plugin!
        @SuppressWarnings("unused")
		Metrics metrics = new Metrics(this, pluginId);
        
		Hooks();
        ReconnectChecker();
        
        for(Player player : Bukkit.getOnlinePlayers()) {
			player.sendMessage("§7[§9Discord§7]§r §aChat Connected!");
		}
		
		ChatListener.url = config.GetString("webserver.url") + config.GetString("webserver.media-location");
		
		Main.plugin.saveResource("MinecraftFont.ttf", true);
		
		//Config
		config.AddValidationEntry("debug", false);
		config.AddValidationEntry("webhook-url", "");
		config.AddValidationEntry("webserver.url", "https://domain.net/");
		config.AddValidationEntry("webserver.upload-url", "https://domain.net/media/upload.php");
		config.AddValidationEntry("webserver.media-location", "media/generated/");
		config.AddValidationEntry("save-locally", false);
		config.AddValidationEntry("save-location", "/public_html/media/generated/");
		config.AddValidationEntry("cleanup-age", 7);
		config.AddValidationEntry("server-icon-url", "");
		config.AddValidationEntry("server-username", "Server");
		config.AddValidationEntry("console-icon-url", "");
		config.AddValidationEntry("console-username", "Console");
		config.AddValidationEntry("player-username", "%player%");
		
		config.AddValidationEntry("custom-join", "&7[&a+&7] %username%");
		config.AddValidationEntry("custom-leave", "&7[&c-&7] %username% (%cause%)");
		config.AddValidationEntry("custom-leave-causes.timed-out.keyword", "Timed out");
		config.AddValidationEntry("custom-leave-causes.timed-out.value", "Timed out");

		config.AddValidationEntry("server-start", true);
		config.AddValidationEntry("server-stop", true);
		config.AddValidationEntry("join", true);
		config.AddValidationEntry("quit", true);
		config.AddValidationEntry("death", true);
		
		config.AddValidationEntry("chat-event-priority", "NORMAL");
		config.AddValidationEntry("hide-achievements", new ArrayList<String>() {
			private static final long serialVersionUID = 1L;
		{ add("AchievementNo.1"); }} );
		config.Validate();
		
		//Blocked Words
		filter.AddValidationEntry("chat-filter-message", "&cSorry, but messages containing \"%s\" cannot be sent.");
		filter.AddValidationEntry("chat-filter", new ArrayList<String>() {
			private static final long serialVersionUID = 1L;
		{ add("@everyone"); add("@here"); }} );
		filter.Validate();
		
		//Lang
		Lang.lang.AddValidationEntry("console-prefix", "[§4Console§r]");
		Lang.lang.AddValidationEntry("console-message", "%timestamp% %message%");
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
		ChatListener.onEnable();
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
			ChatListener.ConnectionFailed();
		}
        
		bot = new Bot();
		
		InventoryImageGenerator.cche = Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable(){
		    @Override
		    public void run() {
		        InventoryImageGenerator.LoadCache();  
		    }
		});
        
    }
    
    @Override
    public void onDisable() {    
        InventoryImageGenerator.cche.cancel();	
    	for(Player player : Bukkit.getOnlinePlayers()) {
			player.sendMessage("§7[§9Discord§7]§r §cChat Disconnected!");
		}
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
			ChatListener.ConnectionFailed();
		}
        
        bot.Shutdown();
    }
    
    void Hooks() {
    	PlaceholderAPIHook.Hook();
    }
    
    public void RegisterCommands() {
    	 for (int i = 0; i < commands.length; i++) {
    		Log.Debug(plugin, "Registering Command: " + commands[i]);
    		getCommand(commands[i]).setExecutor(new CommandListener());
    	}
    }

    
    void ReconnectChecker() {
    	Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
            @Override
            public void run() {
                if(enabled && error) {
                	DiscordWebhook webhook = new DiscordWebhook(config.GetString("webhook-url"));
			    	
				    webhook.setUsername(ChatListener.encodeStringToUnicodeSequence(Lang.RemoveColorCodesAndFormatting(config.GetString("server-username"))));
				    webhook.setContent(ChatListener.encodeStringToUnicodeSequence(Lang.RemoveColorCodesAndFormatting(Lang.Get("server-reconnect"))));
				    webhook.setAvatarUrl(config.GetString("server-icon-url"));
				        
				    try {
						webhook.execute();
						error = false;
				    } catch (IOException e) {
						e.printStackTrace();
						Log.Error(plugin, "Connection failed.");
				        Log.Error(plugin, "Trying again in 60s.");
				        error = true;
				        return;
					}
				    
					for(Player player : Bukkit.getOnlinePlayers()) {
						player.sendMessage("§7[§9Discord§7]§r §aChat Reconnected!");
					}
                }
            }
        }, 1200L, 1200L);
    }
    
}

