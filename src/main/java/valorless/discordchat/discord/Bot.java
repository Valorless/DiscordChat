package valorless.discordchat.discord;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.internal.JDAImpl;
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
import valorless.valorlessutils.logging.Log;
import valorless.valorlessutils.config.Config;
import valorless.valorlessutils.utils.Utils;

/**
 * Manages the Discord bot lifecycle and acts as the bridge between Bukkit events and Discord.
 *
 * <p>This class is responsible for reading Discord configuration, initializing JDA,
 * registering listeners and slash commands, updating presence state, and exposing helper
 * methods for channel messaging and user/role lookups.</p>
 */
public class Bot implements Listener {
	/**
	 * Backing configuration used for Discord bot settings.
	 */
	protected static Config config = new Config(Main.plugin, "discord.yml");

	/**
	 * Self-reference used when registering this instance in async startup code.
	 */
	private final Bot bot;

	/**
	 * Shared factory for creating synchronous/asynchronous task chains.
	 */
	private static TaskChainFactory taskChainFactory;

	/**
	 * Bukkit scheduler task id for the recurring bot health/activity update task.
	 */
	private int taskId;

	/**
	 * Indicates whether the bridge is currently in an error state.
	 */
	protected boolean error = false;
	public boolean ready = false;

	/**
	 * Active JDA client instance, set after successful login.
	 */
	private JDA client;

	/**
	 * Primary guild resolved from configured channel ids.
	 */
	private Guild server;

	/**
	 * Listener handling inbound Discord messages and channel monitoring.
	 */
	private MessageListener messageListener;

	/**
	 * Get the primary Discord guild resolved from configured bridge channels.
	 *
	 * @return resolved guild instance, or {@code null} if not yet resolved
	 */
	public Guild getServer() {
		return server;
	}

	/**
	 * Construct and initialize the Discord bot service.
	 *
	 * <p>If Discord integration is disabled or no token is configured, initialization is skipped.
	 * Startup of the JDA client is performed asynchronously.</p>
	 */
	public Bot() {
		this.bot = this;
		if (Utils.IsStringNullOrEmpty(config.getString("token"))) {
			Log.error(Main.plugin, "Token Required!");
			return;
		} 

		if (!config.getBool("enabled")) {
			return;
		} 

		Bukkit.getScheduler().runTaskAsynchronously(Main.plugin, new Runnable(){
			@Override
			public void run() {
				Log.info(Main.plugin, "Initiating Bot");
				Bukkit.getPluginManager().registerEvents(bot, Main.plugin);
				taskChainFactory = BukkitTaskChainFactory.create(Main.plugin);
				
				OkHttpClient httpClient = new OkHttpClient.Builder()
		                .connectTimeout(5, TimeUnit.SECONDS)  // Connection timeout
		                .writeTimeout(5, TimeUnit.SECONDS)   // Write timeout
		                .readTimeout(8, TimeUnit.SECONDS)    // Read timeout
		                .build();
				
				JDABuilder builder = JDABuilder.createDefault(config.getString("token"));
				builder.setHttpClient(httpClient);
				builder.setRequestTimeoutRetry(false);
				builder.disableCache(CacheFlag.MEMBER_OVERRIDES, new CacheFlag[] { CacheFlag.VOICE_STATE });
				builder.setBulkDeleteSplittingEnabled(false);
				builder.setCompression(Compression.NONE);
				messageListener = new MessageListener();
				builder.addEventListeners(messageListener, new DiscordCommands());
				builder.enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES);
				builder.setMemberCachePolicy(MemberCachePolicy.ALL);

				if(!config.getString("bot-activity.type").equalsIgnoreCase("none")) {
					Activity act = switch (config.getString("bot-activity.type").toLowerCase()) {
                        case "streaming" -> Activity.streaming(activityMessage(), config.getString("bot-activity.url"));
                        case "listening" -> Activity.listening(activityMessage());
                        case "playing" -> Activity.playing(activityMessage());
						case "watching"  -> Activity.watching(activityMessage());
						case "competing" -> Activity.competing(activityMessage());
                        default -> Activity.customStatus(activityMessage());
                    };

                    builder.setActivity(act);
					builder.setAutoReconnect(true);
				}

				try {
					client = builder.build();
					try {
						client.awaitReady();
						Log.info(Main.plugin, "Bot initiated.");
						ready = true;
						for(String ch : Bot.config.getStringList("channels")) {
							long id = Long.parseLong(ch);
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
						        Commands.slash("stats", "View mcMMO stats of a player.")
						).queue();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} catch (Exception excpetion) {
					client = null;
					Log.error(Main.plugin, "FAILED TO LOGIN TO DISCORD USING TOKEN PROVIDED!");
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
	
	/**
	 * Get the configured bot invite link.
	 *
	 * @return invite URL string from configuration
	 */
	public String getInviteLink() {
		return config.getString("invite-link");
	}

	/**
	 * Refresh bot activity when a player joins and the activity message depends on player count.
	 *
	 * @param event player join event
	 */
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if(config.getString("bot-activity.message").contains("%players%")) resetActivity();
	}

	/**
	 * Refresh bot activity when a player leaves and the activity message depends on player count.
	 *
	 * @param event player quit event
	 */
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		if(config.getString("bot-activity.message").contains("%players%")) resetActivity();
	}

	/**
	 * Create a new task chain bound to the bot task-chain factory.
	 *
	 * @param <T> chain context type
	 * @return new task chain instance
	 */
	public static <T> TaskChain<T> newChain() {
		return taskChainFactory.newChain();
	}

	/**
	 * Create or access a named shared task chain.
	 *
	 * @param <T> chain context type
	 * @param name shared chain name
	 * @return shared task chain instance
	 */
	public static <T> TaskChain<T> newSharedChain(String name) {
		return taskChainFactory.newSharedChain(name);
	}

	/**
	 * Reload the Discord configuration file from disk.
	 */
	public static void ReloadConfig() {
		config.reload();
	}

	/**
	 * Shut down the Discord bot and cancel recurring scheduler tasks.
	 */
	public void Shutdown() {
		if(this.client == null) return;
		Log.info(Main.plugin, "Bot shutting down.");
		Bukkit.getScheduler().cancelTask(taskId);
		this.client.shutdown();
		try {
			if (!this.client.awaitShutdown(Duration.ofSeconds(5))) {
				Log.warning(Main.plugin, "Bot did not shut down in time, forcing shutdown.");
				this.client.shutdownNow(); // Cancel all remaining requests
				this.client.awaitShutdown(Duration.ofSeconds(3)); // Wait until shutdown is complete (indefinitely)
			 }
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Log.info(Main.plugin, "Bot shut down.");
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
				for(String ch : Bot.config.getStringList("channels")) {
					long id = Long.parseLong(ch);
					Guild guild = Main.bot.client.getGuildChannelById(id).getGuild();
					GuildChannel gchannel = guild.getGuildChannelById(id);
					if(guild.getSelfMember().hasPermission(gchannel, Permission.MESSAGE_SEND)) {
						guild.getTextChannelById(id).sendMessage(text).queue();
						return true;
					}else {
						Log.error(Main.plugin, String.format("I don't have permission to write in #%s", gchannel.getName()));
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
				Log.error(Main.plugin, String.format("I don't have permission to write in #%s", gchannel.getName()));
				return false;
			}
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Set the bot's Discord presence activity.
	 *
	 * @param activity activity to display
	 */
	public void setActivity(Activity activity) {
		if(this.client == null) return;
		this.client.getPresence().setActivity(activity);
	}

	/**
	 * Rebuild and apply configured bot activity text/type.
	 */
	public void resetActivity() {
		if(this.client == null) return;
		if(this.client.getPresence() == null) return;
		if(this.client.getPresence().getActivity() == null) return;
		if(!config.getString("bot-activity.type").equalsIgnoreCase("none")) {
			Activity act;
			if(config.getString("bot-activity.type").equalsIgnoreCase("streaming")) {
				act = Activity.streaming(activityMessage(), config.getString("bot-activity.url"));
			}else if(config.getString("bot-activity.type").equalsIgnoreCase("listening")) {
				act = Activity.listening(activityMessage());
			}else if(config.getString("bot-activity.type").equalsIgnoreCase("playing")) {
				act = Activity.playing(activityMessage());
			}else if(config.getString("bot-activity.type").equalsIgnoreCase("watching")) {
				act = Activity.watching(activityMessage());
			}else {
				act = Activity.watching(activityMessage());
			}
			this.client.getPresence().setActivity(act);
		}else {
			this.client.getPresence().setActivity(null);
		}
	}

	/**
	 * Resolve the configured activity message with runtime placeholders.
	 *
	 * @return activity message with current player values substituted
	 */
	public String activityMessage() {
		int online = (EssentialsHook.isHooked()) ? EssentialsHook.visiblePlayers().size() : Bukkit.getOnlinePlayers().size();
		return config.getString("bot-activity.message")
				.replace("%players%", "" + online)
				.replace("%max-players%", "" + Bukkit.getMaxPlayers());
	}

	/**
	 * Resolve a text channel by Discord channel ID.
	 *
	 * @param channelID Discord channel ID
	 * @return text channel instance, or {@code null} if not found
	 */
	public MessageChannel GetChannelByID(Long channelID) {
		return client.getTextChannelById(channelID);
	}
	
	/**
	 * Resolve a Discord username by user ID.
	 *
	 * @param userID Discord user ID
	 * @return fetched username, or a formatted failure string when lookup fails
	 */
	public String getUsernameByID(Long userID) {
		try {
			return client.retrieveUserById(userID).complete().getName();
		} catch(Exception e) {
			return "§cFailed to fetch username§r";
		}
	}
	
	/**
	 * Resolve a Discord user ID by username within the configured guild.
	 *
	 * @param username Discord username to search for
	 * @return matching user ID, or {@code null} if not found or lookup fails
	 */
	public Long getUserIDByUsername(String username) {
		try {
			Guild server = null;
			for(String ch : Bot.config.getStringList("channels")) {
				long id = Long.parseLong(ch);
				server = Main.bot.client.getGuildChannelById(id).getGuild();
				break;
			}
			if(server != null) {
				Log.info(Main.plugin, server.getName());
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
	
	/**
	 * Add a role to a guild member.
	 *
	 * @param userID Discord user ID
	 * @param roleID Discord role ID
	 * @return {@code true} if request was queued successfully, otherwise {@code false}
	 */
	public boolean addRole(Long userID, Long roleID) {
		try {
			server.addRoleToMember(server.getMemberById(userID), server.getRoleById(roleID)).queue();
			return true;
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Remove a role from a guild member.
	 *
	 * @param userID Discord user ID
	 * @param roleID Discord role ID
	 * @return {@code true} if request was queued successfully, otherwise {@code false}
	 */
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
