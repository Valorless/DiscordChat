package valorless.discordchat.discord;

import javax.annotation.Nullable;
import javax.security.auth.login.LoginException;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import valorless.discordchat.Main;
import valorless.discordchat.discord.taskchain.BukkitTaskChainFactory;
import valorless.discordchat.discord.taskchain.TaskChain;
import valorless.discordchat.discord.taskchain.TaskChainFactory;
import valorless.valorlessutils.ValorlessUtils.Log;
import valorless.valorlessutils.config.Config;

public class Bot implements Listener {
	protected static Config config = new Config(Main.plugin, "discord.yml");
	private static TaskChainFactory taskChainFactory;
	private int taskId;
	protected boolean error = false;
	
	@Nullable
	private JDA client;
	
	private MessageListener messageListener;
  
	public Bot() {
		if (config.GetString("token") == null) {
			Log.Error(Main.plugin, "Token Required!");
			return;
		} 
		
		if (!config.GetBool("enabled")) {
			return;
		} 
		
		Log.Info(Main.plugin, "Initiating Bot");
		Bukkit.getPluginManager().registerEvents(this, Main.plugin);
		taskChainFactory = BukkitTaskChainFactory.create(Main.plugin);
		
		JDABuilder builder = JDABuilder.createDefault(config.GetString("token"));
		builder.disableCache(CacheFlag.MEMBER_OVERRIDES, new CacheFlag[] { CacheFlag.VOICE_STATE });
		builder.setBulkDeleteSplittingEnabled(false);
		builder.setCompression(Compression.NONE);
		messageListener = new MessageListener();
		builder.addEventListeners(new Object[] { messageListener });
		
		if(!config.GetString("bot-activity.type").equalsIgnoreCase("none")) {
			Activity act;
			if(config.GetString("bot-activity.type").equalsIgnoreCase("streaming")) {
				act = Activity.streaming(activityMessage(), config.GetString("bot-activity.url"));
			}else if(config.GetString("bot-activity.type").equalsIgnoreCase("listening")) {
				act = Activity.listening(activityMessage());
			}else if(config.GetString("bot-activity.type").equalsIgnoreCase("playing")) {
				act = Activity.playing(activityMessage());
			}else if(config.GetString("bot-activity.type").equalsIgnoreCase("watching")) {
				act = Activity.watching(activityMessage());
			}else {
				act = Activity.watching(activityMessage());
			}
			
			builder.setActivity(act);
		}
		
		try {
			this.client = builder.build();
			try {
				this.client.awaitReady();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} catch (LoginException excpetion) {
			this.client = null;
			Log.Error(Main.plugin, "FAILED TO LOGIN TO DISCORD USING TOKEN PROVIDED!");
			return;
		}
		
		taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
            @Override
            public void run() {
                if(Main.error) {
                	setActivity(Activity.playing("MC Bridge Issues"));
                	if(!error) {
                		error = true;
                		SendMessage("**Chat Disconnected**");
                	}
                }else {
                	resetActivity();
                	if(error) {
                		error = false;
                	}
                }
            }
        }, 100L, 100L);

		Log.Info(Main.plugin, "Bot initiated.");
    }
	
	@EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
		if(config.GetString("bot-activity.message").contains("%players%")) resetActivity();
    }
	
	@EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
		if(config.GetString("bot-activity.message").contains("%players%")) resetActivity();
    }
	
	public static <T> TaskChain<T> newChain() {
		return taskChainFactory.newChain();
	}
	
	public static <T> TaskChain<T> newSharedChain(String name) {
		return taskChainFactory.newSharedChain(name);
	}
	
	public static void ReloadConfig() {
		config.Reload();
	}
	
	public void Shutdown() {
		Bukkit.getScheduler().cancelTask(taskId);
		this.client.shutdownNow();
	}
	
	public void SendMessage(String text) {
		for(TextChannel channel : this.client.getTextChannels()) {
			if (messageListener.monitoredChannels.contains(channel.getId())) {
				CharSequence message = text;
				channel.sendMessage(message);
			}
		}
	}
	
	public void setActivity(Activity activity) {
		this.client.getPresence().setActivity(activity);
	}
	
	public void resetActivity() {
		if(this.client.getPresence() == null) return;
		if(this.client.getPresence().getActivity() == null) return;
		if(!config.GetString("bot-activity.type").equalsIgnoreCase("none")) {
			Activity act;
			if(config.GetString("bot-activity.type").equalsIgnoreCase("streaming")) {
				act = Activity.streaming(activityMessage(), config.GetString("bot-activity.url"));
			}else if(config.GetString("bot-activity.type").equalsIgnoreCase("listening")) {
				act = Activity.listening(activityMessage());
			}else if(config.GetString("bot-activity.type").equalsIgnoreCase("playing")) {
				act = Activity.playing(activityMessage());
			}else if(config.GetString("bot-activity.type").equalsIgnoreCase("watching")) {
				act = Activity.watching(activityMessage());
			}else {
				act = Activity.watching(activityMessage());
			}
			this.client.getPresence().setActivity(act);
		}else {
			this.client.getPresence().setActivity(null);
		}
	}
	
	private String activityMessage() {
		return config.GetString("bot-activity.message")
				.replace("%players%", "" + Bukkit.getOnlinePlayers().size())
				.replace("%max-players%", "" + Bukkit.getMaxPlayers());
	}
}