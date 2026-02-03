package valorless.discordchat.discord;

import java.time.Duration;
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
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import okhttp3.OkHttpClient;
import valorless.discordchat.Main;
import valorless.discordchat.discord.taskchain.BukkitTaskChainFactory;
import valorless.discordchat.discord.taskchain.TaskChain;
import valorless.discordchat.discord.taskchain.TaskChainFactory;
import valorless.discordchat.hooks.EssentialsHook;
import valorless.valorlessutils.ValorlessUtils.Log;
import valorless.valorlessutils.config.Config;
import valorless.valorlessutils.utils.Utils;

public class Bot implements Listener {
	protected static Config config = new Config(Main.plugin, "discord.yml");
	private Bot bot;
	private static TaskChainFactory taskChainFactory;
	private int taskId;
	protected boolean error = false;
	public boolean ready = false;

	private JDA client;
	private Guild server;

	public Guild getServer() {
		return server;
	}

	private MessageListener messageListener;

	public Bot() {
		this.bot = this;
		if (Utils.IsStringNullOrEmpty(config.GetString("token"))) {
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
				builder.addEventListeners(new Object[] { messageListener, new DiscordCommands() });
				builder.enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES);
				builder.setMemberCachePolicy(MemberCachePolicy.ALL);

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
					try {
						client.awaitReady();
						Log.Info(Main.plugin, "Bot initiated.");
						ready = true;
						for(String ch : Bot.config.GetStringList("channels")) {
							Long id = Long.valueOf(ch);
							server = Main.bot.client.getGuildChannelById(id).getGuild();
							break;
						}
						server.updateCommands().addCommands(
						        Commands.slash("help", "Shows a list of available commands."),
						        Commands.slash("online", "Lists all online players."),
						        Commands.slash("uptime", "Shows how long the server has been up."),
						        Commands.slash("memory", "Shows the server memory usage.")
						        	.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)),
						        Commands.slash("link", "Link your Minecraft account to Discord.")
					        		.addOption(OptionType.STRING, "username", "Player to pay."),
						        Commands.slash("unlink", "Unlink your Minecraft account from Discord."),
						        Commands.slash("pay", "Pay a player.")
						        	.addOption(OptionType.STRING, "username", "Player to pay.")
						        	.addOption(OptionType.NUMBER, "amount", "Amount to pay."),
						        Commands.slash("balance", "Check your balance."),
						        Commands.slash("inventory", "View your Minecraft inventory."),
						        Commands.slash("enderchest", "View your Minecraft enderchest."),
						        Commands.slash("stats", "View mcMMO stats of a player."),
						        Commands.slash("top", "View top mcMMO players on the server.")
						).queue();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} catch (Exception excpetion) {
					client = null;
					Log.Error(Main.plugin, "FAILED TO LOGIN TO DISCORD USING TOKEN PROVIDED!");
					Main.error = true;
					excpetion.printStackTrace();
					return;
				}
			}
		});

		taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			@Override
			public void run() {
				if(bot.client == null) return;
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
		if(this.client == null) return;
		Log.Info(Main.plugin, "Bot shutting down.");
		Bukkit.getScheduler().cancelTask(taskId);
		this.client.shutdown();
		try {
			if (!this.client.awaitShutdown(Duration.ofSeconds(5))) {
				Log.Warning(Main.plugin, "Bot did not shut down in time, forcing shutdown.");
				this.client.shutdownNow(); // Cancel all remaining requests
				this.client.awaitShutdown(Duration.ofSeconds(3)); // Wait until shutdown is complete (indefinitely)
			 }
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Log.Info(Main.plugin, "Bot shut down.");
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
	public boolean SendMessage(MessageChannel channel, String text) {
		if(channel == null) {
			try {
				for(String ch : Bot.config.GetStringList("channels")) {
					Long id = Long.valueOf(ch);
					Guild guild = Main.bot.client.getGuildChannelById(id).getGuild();
					GuildChannel gchannel = guild.getGuildChannelById(id);
					if(guild.getSelfMember().hasPermission(gchannel, Permission.MESSAGE_SEND)) {
						guild.getTextChannelById(id).sendMessage(text).queue();
						return true;
					}else {
						Log.Error(Main.plugin, String.format("I don't have permission to write in #%s", gchannel.getName()));
						return false;
					}
				}
			} catch(Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		try {
			Guild guild = channel.getJDA().getGuildChannelById(channel.getIdLong()).getGuild();
			GuildChannel gchannel = guild.getGuildChannelById(channel.getIdLong());
			if(guild.getSelfMember().hasPermission(gchannel, Permission.MESSAGE_SEND)) {
				channel.sendMessage(text).queue();
				return true;
			}else {
				Log.Error(Main.plugin, String.format("I don't have permission to write in #%s", gchannel.getName()));
				return false;
			}
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public void setActivity(Activity activity) {
		if(this.client == null) return;
		this.client.getPresence().setActivity(activity);
	}

	public void resetActivity() {
		if(this.client == null) return;
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

	public String activityMessage() {
		int online = (EssentialsHook.isHooked()) ? EssentialsHook.visiblePlayers().size() : Bukkit.getOnlinePlayers().size();
		return config.GetString("bot-activity.message")
				.replace("%players%", "" + online)
				.replace("%max-players%", "" + Bukkit.getMaxPlayers());
	}

	public MessageChannel GetChannelByID(Long channelID) {
		return client.getTextChannelById(channelID);
	}
	
	public String getUsernameByID(Long userID) {
		try {
			return client.retrieveUserById(userID).complete().getName();
		} catch(Exception e) {
			return "§cFailed to fetch username§r";
		}
	}
	
	public Long getUserIDByUsername(String username) {
		try {
			Guild server = null;
			for(String ch : Bot.config.GetStringList("channels")) {
				Long id = Long.valueOf(ch);
				server = Main.bot.client.getGuildChannelById(id).getGuild();
				break;
			}
			if(server != null) {
				Log.Info(Main.plugin, server.getName());
				return server.getMembers().stream()
						.filter(member -> member.getUser().getName().equalsIgnoreCase(username))
						.map(member -> member.getUser().getIdLong())
						.findFirst()
						.orElse(null);
			}else return null;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean addRole(Long userID, Long roleID) {
		try {
			server.addRoleToMember(server.getMemberById(userID), server.getRoleById(roleID)).queue();
			return true;
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean removeRole(Long userID, Long roleID) {
		try {
			server.removeRoleFromMember(server.getMemberById(userID), server.getRoleById(roleID)).queue();
			return true;
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
}