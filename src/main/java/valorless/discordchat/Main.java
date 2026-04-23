package valorless.discordchat;

import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import valorless.discordchat.discord.Bot;
import valorless.discordchat.hooks.*;
import valorless.discordchat.storage.Storage;
import valorless.discordchat.utils.MemoryTracker;
import valorless.valorlessutils.logging.Log;
import valorless.valorlessutils.utils.Utils;
import valorless.valorlessutils.Metrics;
import valorless.valorlessutils.config.Config;
import valorless.valorlessutils.translate.Translator;


public final class Main extends JavaPlugin implements Listener {
	public static JavaPlugin plugin;
	String Name = "§7[§4DiscordChat§7]§r";
	public static Boolean enabled = true;
	public static Config config;
	public static Config filter;
	public static Config muted;
	public static Config bans;
	public static String username = "";
	public static boolean error = false;
	public static Bot bot;
	public static MemoryTracker memoryTracker;
	public static Translator translator;

	public String[] commands = {
			"discordchat", "dcm", "dc", "server", "discord"
	};

	public void onLoad() {
		plugin = this;
		ChatListener.plugin = this;
		CommandListener.plugin = this;
		config = new Config(this,"config.yml");
		filter = new Config(this,"blocked-words.yml");
		muted = new Config(this,"muted.yml");
		bans = new Config(this,"bans.yml");
		ChatListener.config = config;

		Lang.lang = new Config(this,"lang.yml");

		translator = new Translator("en_us");
	}

	@Override
	public void onEnable() {	
		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			// All you have to do is adding the following two lines in your onEnable method.
			// You can find the plugin ids of your plugins on the page https://bstats.org/what-is-my-plugin-id
			int pluginId = 18793; // <-- Replace with the id of your plugin!
			@SuppressWarnings("unused")
			Metrics metrics = new Metrics(this, pluginId);

			Hooks();
			new PlaceholderAPI().register();
			ReconnectChecker();

			for(Player player : Bukkit.getOnlinePlayers()) {
				player.sendMessage(Lang.Get("prefix") + "§aChat Connected!");
			}

			ChatListener.url = config.getString("webserver.url") + config.getString("webserver.media-location");

			Main.plugin.saveResource("MinecraftFont.ttf", true);

			//Config
			config.addValidationEntry("debug", false);
			config.addValidationEntry("webhook-url", "");
			config.addValidationEntry("webserver.url", "https://domain.net/");
			config.addValidationEntry("webserver.upload-url", "https://domain.net/media/upload.php");
			config.addValidationEntry("webserver.media-location", "media/generated/");
			config.addValidationEntry("save-locally", false);
			config.addValidationEntry("save-location", "/public_html/media/generated/");
			config.addValidationEntry("cleanup-age", 7);
			config.addValidationEntry("server-icon-url", "");
			config.addValidationEntry("server-username", "Server");
			config.addValidationEntry("console-icon-url", "");
			config.addValidationEntry("console-username", "Console");
			config.addValidationEntry("player-username", "%player%");

			config.addValidationEntry("custom-join", "&7[&a+&7] %username%");
			
			config.addValidationEntry("new-player-sound.enabled", true);
			config.addValidationEntry("new-player-sound.sound", "ITEM_GOAT_HORN_SOUND_1");
			config.addValidationEntry("new-player-sound.volume", 1);
			config.addValidationEntry("new-player-sound.pitch", 1.1);

			config.addValidationEntry("join-sound.enabled", true);
			config.addValidationEntry("join-sound.sound", "BLOCK_BELL_USE");
			config.addValidationEntry("join-sound.volume", 1);
			config.addValidationEntry("join-sound.pitch", 1);
			
			config.addValidationEntry("custom-leave", "&7[&c-&7] %username% (%cause%)");
			config.addValidationEntry("custom-leave-causes.timed-out.keyword", "Timed out");
			config.addValidationEntry("custom-leave-causes.timed-out.value", "Timed out");
			config.addValidationEntry("error-message", true);

			config.addValidationEntry("server-start", true);
			config.addValidationEntry("server-stop", true);
			config.addValidationEntry("join", true);
			config.addValidationEntry("quit", true);
			config.addValidationEntry("death", true);

			config.addValidationEntry("chat-event-priority", "NORMAL");
			config.addValidationEntry("hide-achievements", new ArrayList<String>() {
				private static final long serialVersionUID = 1L;
				{ add("AchievementNo.1"); }} );
			config.validate();

			//Blocked Words
			filter.addValidationEntry("chat-filter-message", "&cSorry, but messages containing \"%s\" cannot be sent.");
			filter.addValidationEntry("chat-filter", new ArrayList<String>() {
				private static final long serialVersionUID = 1L;
				{ add("@everyone"); add("@here"); }} );
			filter.validate();

			//Lang
			Lang.lang.addValidationEntry("prefix", "&7[&9DiscordChat&7]&r ");
			Lang.lang.addValidationEntry("console-prefix", "[&4Console&r]");
			Lang.lang.addValidationEntry("console-message", "%timestamp% %message%");
			Lang.lang.addValidationEntry("message", "%timestamp% %message%");
			Lang.lang.addValidationEntry("server-start", "%timestamp% **Chat Enabled**");
			Lang.lang.addValidationEntry("server-stop", "%timestamp% **Chat Disabled**");
			Lang.lang.addValidationEntry("server-reconnect", "%timestamp% **Chat Reconnected**");
			Lang.lang.addValidationEntry("join", "%timestamp% **%player%** has joined the server.");
			Lang.lang.addValidationEntry("join-first-time", "%timestamp% **%player%** has joined the server for the first time!.");
			Lang.lang.addValidationEntry("leave", "%timestamp% **%player%** has left the server.");
			Lang.lang.addValidationEntry("with-player-count", "%message% (%player-count%/%player-count-max%)");
			Lang.lang.validate();


			//Config
			bans.addValidationEntry("webhook-url", "");
			bans.addValidationEntry("bot-name", "George");
			bans.addValidationEntry("bot-picture", "https://i.pinimg.com/originals/bf/23/ca/bf23ca87c2a867e2b3b991e76d982abd.jpg");
			bans.addValidationEntry("ban-color", "#ff2b2b");
			bans.addValidationEntry("tempban-color", "#ff992b");
			bans.addValidationEntry("unban-color", "#2afa4d");
			bans.addValidationEntry("banip-color", "#5b09ad");
			bans.addValidationEntry("unbanip-color", "#0ce6fa");
			bans.addValidationEntry("bans", true);
			bans.addValidationEntry("tempbans", true);
			bans.addValidationEntry("unbans", true);
			bans.addValidationEntry("banips", true);
			bans.addValidationEntry("unbanips", true);
			bans.addValidationEntry("bot-message", "");
			bans.addValidationEntry("banned-title", "%target% has been banned!");
			bans.addValidationEntry("tempbanned-title", "%target% has been temp banned!");
			bans.addValidationEntry("unbanned-title", "%target% has been unbanned!");
			bans.addValidationEntry("ip-banned-title", "IP: %target% has been banned!");
			bans.addValidationEntry("ip-unbanned-title", "IP: %target% has been unbanned!");
			bans.addValidationEntry("description", "");
			bans.addValidationEntry("reason-line1", "Reason: ");
			bans.addValidationEntry("reason-line2", "%reason%");
			bans.addValidationEntry("banned-by-line1", "Banned by: ");
			bans.addValidationEntry("banned-by-line2", "%sender%");
			bans.addValidationEntry("unbanned-by-line1", "Unbanned by: ");
			bans.addValidationEntry("unbanned-by-line2", "%sender%");
			bans.addValidationEntry("duration-line1", "Duration: ");
			bans.addValidationEntry("duration-line2", "%duration%");
			bans.addValidationEntry("banned-on", "Banned on %date%");

			getServer().getPluginManager().registerEvents(new BanListener(), this);
			getServer().getPluginManager().registerEvents(new ChatListener(), this);
			ChatListener.onEnable();
			Eco.init();
			//getServer().getPluginManager().registerEvents(new CommandListenerOld(), this);
			Storage.init();

			if(Utils.IsStringNullOrEmpty(config.getString("webhook-url"))) {
				Log.warning(plugin, "Please change my config.yml before using me." +
						"\nYou can reload me when needed with /dcm reload."
						+ "\nThough I highly recommend restarting.");
				Log.info(plugin, "§cDisabled!");
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
				username = config.getString("server-username");
			}

			
			bot = new Bot();
			
			new BukkitRunnable() {
			    @Override
			    public void run() {
			        if (!bot.ready) return;
			        if (config.getBool("server-start") && enabled) {
			            final DiscordWebhook webhook = new DiscordWebhook(config.getString("webhook-url"));
			            webhook.setUsername(username);
			            webhook.setContent(Lang.Get("server-start"));
			            webhook.setAvatarUrl(config.getString("server-icon-url"));

			            // execute network I/O off the main thread
			            Bukkit.getScheduler().runTaskAsynchronously(Main.plugin, () -> {
			                try {
			                    webhook.execute();
			                } catch (IOException e) {
			                    e.printStackTrace();
			                    ChatListener.ConnectionFailed();
			                }
			            });

			            // stop this repeating check
			            this.cancel();
			        }
			    }
			}
			// runTaskTimer(plugin, delay, period) — using 20L checks every second instead of every tick
			.runTaskTimer(Main.plugin, 1L, 1L);

			//InventoryImageGenerator.cche = Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable(){
			//	@Override
			//	public void run() {
			//		InventoryImageGenerator.LoadCache();
			//	}
			//});

			memoryTracker = new MemoryTracker(5);
		}, 10);
	}

	@Override
	public void onDisable() {    
		//InventoryImageGenerator.cche.cancel();
		for(Player player : Bukkit.getOnlinePlayers()) {
			player.sendMessage(Lang.Get("prefix") + "§cChat Disconnected!");
		}
		if(config.getBool("server-stop") && enabled) {
			DiscordWebhook webhook = new DiscordWebhook(config.getString("webhook-url"));

			webhook.setUsername(username);
			webhook.setContent(Lang.Get("server-stop"));
			webhook.setAvatarUrl(config.getString("server-icon-url"));

			try {
				//Log.info(plugin, "Executing webhook.");
				webhook.execute();
			} catch (IOException e) {
				e.printStackTrace();
				ChatListener.ConnectionFailed();
			}
		}

		Storage.shutdown();
		bot.Shutdown();
	}

	void Hooks() {
		PlaceholderAPIHook.Hook();
		EssentialsHook.Hook();
		mcmmoHook.Hook();
	}

	public void RegisterCommands() {
        for (String command : commands) {
            Log.debug(plugin, "Registering Command: " + command);
            getCommand(command).setExecutor(new CommandListener());
            getCommand(command).setTabCompleter(new TabCompletion());
        }
	}


	void ReconnectChecker() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			@Override
			public void run() {
				if(enabled && error) {
					DiscordWebhook webhook = new DiscordWebhook(config.getString("webhook-url"));

					webhook.setUsername(ChatListener.encodeStringToUnicodeSequence(
							Lang.RemoveColorCodesAndFormatting(config.getString("server-username"))));
					webhook.setContent(ChatListener.encodeStringToUnicodeSequence(
							Lang.RemoveColorCodesAndFormatting(Lang.Get("server-reconnect"))));
					webhook.setAvatarUrl(config.getString("server-icon-url"));

					try {
						webhook.execute();
						error = false;
					} catch (IOException e) {
						e.printStackTrace();
						Log.error(plugin, "Connection failed.");
						Log.error(plugin, "Trying again in 60s.");
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

