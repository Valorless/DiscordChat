package valorless.discordchat.discord;

import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import okhttp3.OkHttpClient;
import valorless.discordchat.Main;
import valorless.discordchat.discord.taskchain.BukkitTaskChainFactory;
import valorless.discordchat.discord.taskchain.TaskChain;
import valorless.discordchat.discord.taskchain.TaskChainFactory;
import valorless.valorlessutils.ValorlessUtils.Log;
import valorless.valorlessutils.config.Config;

public class Bot implements Listener {
	protected static Config config = new Config(Main.plugin, "discord.yml");
	private Bot bot;
	private static TaskChainFactory taskChainFactory;
	private int taskId;
	protected boolean error = false;

	private JDA client;

	private MessageListener messageListener;

	public Bot() {
		this.bot = this;
		if (config.GetString("token") == null) {
			Log.Error(Main.plugin, "Token Required!");
			return;
		} 

		if (!config.GetBool("enabled")) {
			return;
		} 

		Bukkit.getScheduler().runTaskAsynchronously(Main.plugin, new Runnable(){

			@Override
			public void run() {
				Log.Info(Main.plugin, "Initiating Bot");
				Bukkit.getPluginManager().registerEvents(bot, Main.plugin);
				taskChainFactory = BukkitTaskChainFactory.create(Main.plugin);
				
				OkHttpClient httpClient = new OkHttpClient.Builder()
		                .connectTimeout(5, TimeUnit.SECONDS)  // Connection timeout
		                .writeTimeout(5, TimeUnit.SECONDS)   // Write timeout
		                .readTimeout(8, TimeUnit.SECONDS)    // Read timeout
		                .build();
				
				JDABuilder builder = JDABuilder.createDefault(config.GetString("token"));
				builder.setHttpClient(httpClient);
				builder.setRequestTimeoutRetry(false);
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
					builder.setAutoReconnect(true);
				}

				try {
					client = builder.build();
					/*try {
						client.awaitReady();
						Log.Info(Main.plugin, "Bot initiated.");
					} catch (InterruptedException e) {
						e.printStackTrace();
					}*/
				} catch (Exception excpetion) {
					client = null;
					Log.Error(Main.plugin, "FAILED TO LOGIN TO DISCORD USING TOKEN PROVIDED!");
					return;
				}
			}
		});

		taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			@Override
			public void run() {
				if(Main.error) {
					setActivity(Activity.playing("MC Bridge Issues"));
					if(!error) {
						error = true;
						for(TextChannel channel : client.getTextChannels()) {
							if (messageListener.monitoredChannels.contains(channel.getId())) {
								channel.sendMessage("**Chat Disconnected**");
							}
						}
					}
				}else {
					resetActivity();
					if(error) {
						error = false;
					}
				}
			}
		}, 100L, 100L);

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

	/**
	 * Sends a message to a specified Discord channel. If the provided channel is null,  
	 * it attempts to send the message to all configured channels in the bot's settings.  
	 * Logs an error if the bot lacks permission to send messages in a channel.
	 *
	 * @param channel The target {@link MessageChannel} to send the message to.  
	 *                If null, the message is sent to all configured channels.  
	 * @param text The message content to be sent.  
	 */
	public void SendMessage(MessageChannel channel, String text) {
		if(channel == null) {
			try {
				for(String ch : Bot.config.GetStringList("channels")) {
					int id = Integer.valueOf(ch);
					Guild guild = Main.bot.client.getGuildChannelById(id).getGuild();
					GuildChannel gchannel = guild.getGuildChannelById(id);
					if(guild.getSelfMember().hasPermission(gchannel, Permission.MESSAGE_WRITE)) {
						guild.getTextChannelById(id).sendMessage(text).queue();
					}else {
						Log.Error(Main.plugin, String.format("I don't have permission to write in #%s", gchannel.getName()));
					}
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		try {
			Guild guild = channel.getJDA().getGuildChannelById(channel.getIdLong()).getGuild();
			GuildChannel gchannel = guild.getGuildChannelById(channel.getIdLong());
			if(guild.getSelfMember().hasPermission(gchannel, Permission.MESSAGE_WRITE)) {
				channel.sendMessage(text).queue();
			}else {
				Log.Error(Main.plugin, String.format("I don't have permission to write in #%s", gchannel.getName()));
			}
		} catch(Exception e) {
			e.printStackTrace();
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