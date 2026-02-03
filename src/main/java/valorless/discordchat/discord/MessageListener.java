package valorless.discordchat.discord;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReference;
import net.dv8tion.jda.api.entities.MessageReference.MessageReferenceType;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.ess3.api.IUser;
import valorless.discordchat.BanListener;
import valorless.discordchat.CustomConsoleSender;
import valorless.discordchat.Lang;
import valorless.discordchat.Main;
import valorless.discordchat.hooks.EssentialsHook;
import valorless.discordchat.linking.Linking;
import valorless.valorlessutils.ValorlessUtils.Log;
import valorless.valorlessutils.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MessageListener extends ListenerAdapter { 
	public List<String> monitoredChannels;

	public MessageListener() {
		this.monitoredChannels = Bot.config.GetStringList("channels");
	}

	@SuppressWarnings("deprecation")
	public void onMessageReceived(@NotNull MessageReceivedEvent event) {
		Member member = event.getMember();
		if (!monitoredChannels.contains(event.getChannel().getId())) return; 
		if (event.getAuthor().isBot() && !Bot.config.GetBool("bot-messages")) return; 
		boolean reply = (event.getMessage() != null) ?
				event.getMessage().getType() == MessageType.INLINE_REPLY : false;
		boolean forward = (event.getMessage().getMessageReference() != null) ?
				event.getMessage().getMessageReference().getType() ==  MessageReferenceType.FORWARD : false;
		boolean attachments =  (event.getMessage().getAttachments() != null) ? !event.getMessage().getAttachments().isEmpty() : false;

		Log.Debug(Main.plugin, "reply: " + reply);
		Log.Debug(Main.plugin, "forward: " + forward);
		Log.Debug(Main.plugin, "attachments: " + attachments);

		Bot.newChain().async(() -> {
			String message = event.getMessage().getContentDisplay();
			if (member == null) return; 
			String username = event.getAuthor().getName();
			String guildName = event.getGuild().getName();
			String channel = event.getMessage().getChannel().getName();
			String displayname = event.getAuthor().getGlobalName() != null  ? event.getAuthor().getGlobalName() : username;
			String nickname = (member.getNickname() != null) ? member.getNickname() : displayname;
			String badge = getBadge(member);
			Role mainRole = getHighestFrom(member);
			String role = (mainRole != null) ? mainRole.getName() : "";
			String chatMessage = Bot.config.GetString("message-format");

			if(containsUrl(message)) {
				event.getMessage().delete();
				Main.bot.SendMessage(event.getChannel(), String.format("<@%s> Links and media links do not work in this channel." , event.getAuthor().getId()));
				return;
			}

			if(!Utils.IsStringNullOrEmpty(event.getMessage().getContentDisplay())) {
				try {
					char c = message.charAt(0);
					char prefix = Bot.config.GetString("command-prefix").charAt(0);
					//Log.Info(Main.plugin, c + "");
					//Log.Info(Main.plugin, prefix + "");
					if(c == prefix) {
						Log.Info(Main.plugin, "Command");
						message = ProccessCommand(event.getChannel(), member, event.getAuthor(), message);
						if(message == null) {
							return;
						}
					}else if(c == '!') {
						Log.Info(Main.plugin, "D-Command");
						ProccessDiscordCommand(event.getChannel(), member, event.getAuthor(), message);
						return;
					}
				}catch(Exception e) {
					Main.bot.SendMessage(event.getChannel(), e.getMessage());
					if(Main.config.GetBool("error-message")) {
						String msg = String.format(
								"§7[§9Discord§7]§r Error proccessing message from %s, might be a forward or contain an image."
								, nickname);
						for(Player player : Bukkit.getOnlinePlayers()) {
							player.sendMessage(msg);
						}
						Log.Info(Main.plugin, msg.replace("§7[§9Discord§7]§r ", ""));
					}
					e.printStackTrace();
					return;
				}
			}

			if(!isStaff(member)) {
				message = Lang.RemoveColorCodesAndFormatting(message);
			}

			chatMessage = Lang.hex(chatMessage);
			chatMessage = chatMessage.replace("&", "§");
			if(Linking.isLinked(event.getAuthor().getIdLong())) {
				UUID uuid = Linking.getMinecraftUUID(event.getAuthor().getIdLong());
				OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
				String name = player.getName();
				if(EssentialsHook.isHooked()) {
					IUser user = EssentialsHook.getInstance().getUser(player);
					if(user.getFormattedNickname() != null) {
						name = user.getFormattedNickname() + "§r";
					}
				}
				chatMessage = chatMessage.replace("%username%", name)
					.replace("%displayname%", name)
					.replace("%nickname%", reply ? name + " (Reply)" : name)
					.replace("%server%", guildName)
					.replace("%channel%", channel)
					.replace("%role%", role)
					.replace("%badge%", badge);
			}else {
				chatMessage = chatMessage.replace("%username%", username)
					.replace("%displayname%", displayname)
					.replace("%nickname%", reply ? nickname + " (Reply)" : nickname)
					.replace("%server%", guildName)
					.replace("%channel%", channel)
					.replace("%role%", role)
					.replace("%badge%", badge);
			}

			if(Utils.IsStringNullOrEmpty(event.getMessage().getContentDisplay())) {
				if(attachments) {
					List<String> files = event.getMessage().getAttachments().stream().map(attachment -> attachment.getFileName()).toList();
					chatMessage = chatMessage.replace("%message%", "[File/Image] " + String.join(", ", files) + "\n" +
					message);
				}else {
					chatMessage = chatMessage.replace("%message%", "null");
				}
			}else {
				chatMessage = chatMessage.replace("%message%", message);
			}


			if(blockedWord(chatMessage) == null) { 
				if(reply) {
					String name = event.getMessage().getReferencedMessage().getAuthor().getGlobalName() != null ? 
							event.getMessage().getReferencedMessage().getAuthor().getGlobalName() : 
								event.getMessage().getReferencedMessage().getAuthor().getName();
					if(name.equalsIgnoreCase("No global name set")) name = event.getMessage().getReferencedMessage().getAuthor().getName();
					if(name.equalsIgnoreCase("Error retrieving global name")) name = event.getMessage().getReferencedMessage().getAuthor().getName();
					String replyMessage = "┌─── " + String.format("%s: %s", 
							removeFirstBracketedText(name),
							removeFirstBracketedText(event.getMessage().getReferencedMessage().getContentStripped())
							);
					//Bukkit.broadcastMessage(replyMessage + "\n" + chatMessage);
					for(Player player : Bukkit.getOnlinePlayers()) {
						if(Main.muted.HasKey(player.getName()))
							if(Main.muted.GetBool(player.getName())) continue;
						player.sendMessage(replyMessage + "\n" + chatMessage);
					}
					ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
					console.sendMessage(replyMessage + "\n" + chatMessage);
				}
				else if(forward){
					String forwarder = displayname.equalsIgnoreCase("No global name set") ? displayname : username;
					String forwardMessage = "┌─── " + String.format("(Forward) %s", forwarder);
					
					Message receivedMessage = event.getMessage();
					MessageReference ref = receivedMessage.getMessageReference();

					if (ref != null) {
					    // A referenced message exists
					    ref.resolve().queue(originalMessage -> {
					    	String name = originalMessage.getAuthor().getGlobalName() != null ? 
					    			originalMessage.getAuthor().getGlobalName() : originalMessage.getAuthor().getName();
					    	String msg = "";
							if(name.equalsIgnoreCase("No global name set")) name = originalMessage.getAuthor().getName();
					    	
					    	boolean att =  (originalMessage.getAttachments() != null) ? !originalMessage.getAttachments().isEmpty() : false;
					    	
					    	if(att) {
								List<String> files = originalMessage.getAttachments().stream().map(attachment -> attachment.getFileName()).toList();
								msg = forwardMessage + String.format("\n%s: [File/Image] %s\n%s", removeFirstBracketedText(name),
										String.join(", ", files),
										removeFirstBracketedText(originalMessage.getContentStripped())
										);
							}else {
								msg = forwardMessage + String.format("\n%s: %s", removeFirstBracketedText(name),
										removeFirstBracketedText(originalMessage.getContentStripped())
										);
							}
					        
							for(Player player : Bukkit.getOnlinePlayers()) {
								if(Main.muted.HasKey(player.getName()))
									if(Main.muted.GetBool(player.getName())) continue;
								player.sendMessage(msg);
							}
							ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
							console.sendMessage(msg);
					    }, failure -> {
					        Log.Debug(Main.plugin, "Failed to resolve the referenced message");
					        return;
					    });
					}
					
					
				}
				else {
					//Bukkit.broadcastMessage(chatMessage); 
					for(Player player : Bukkit.getOnlinePlayers()) {
						if(Main.muted.HasKey(player.getName()))
							if(Main.muted.GetBool(player.getName())) continue;
						player.sendMessage(chatMessage);
					}
					ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
					console.sendMessage(chatMessage);
				}
			}
			else {
				Main.bot.SendMessage(event.getChannel(), String.format("<@%s> " , event.getAuthor().getId()) +
						String.format(Lang.RemoveColorCodesAndFormatting(Main.filter.GetString("chat-filter-message")), blockedWord(chatMessage)));
			}
		}).execute();
	}

	String ProccessCommand(MessageChannel channel, Member member, User user, String command) {
		//Log.Info(Main.plugin, "staff?");
		Log.Info(Main.plugin, "User: " + user.getName());
		Log.Info(Main.plugin, "Command: " + command);
		if(!isStaff(member)) {
			Main.bot.SendMessage(channel, String.format("<@%s> Only staff may use commands.", user.getId()));
			return null;
		}

		if(blockedCommand(command.substring(1)) == null) { 
			//Log.Info(Main.plugin, "Sending command");
			Bukkit.getScheduler().runTask(Main.plugin, () -> {
				CustomConsoleSender sender = new CustomConsoleSender(user.getName(), msg -> {
					Main.bot.SendMessage(channel, String.format("<@%s> %s", user.getId(), 
							Lang.RemoveColorCodesAndFormatting(msg)));
					Log.Info(Main.plugin, Lang.RemoveColorCodesAndFormatting(msg));
				});

				try {
					if(Bukkit.dispatchCommand(sender, command.substring(1))) {
						BanListener.DiscordCommand(sender, command.substring(1));
					}
				}catch(Exception e) {
					// If a vanilla command, run through the default command sender,
					// but no feedback messages.
					if(e.getCause() != null 
							&& e.getCause().getMessage().contains("Cannot make valorless.discordchat.CustomConsoleSender")
							&& e.getCause().getMessage().contains("a vanilla command listener")) {
						try {
							Main.bot.SendMessage(channel, String.format("<@%s> Cannot process Vanilla command responses, but the command was dispatched."
									+ "\nCheck the console for command feedback.", user.getId()));
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.substring(1));
						}catch(Exception E) {
							E.printStackTrace();
							SendException(E, channel, user);
						}
						return;
					}

					e.printStackTrace();
					SendException(e, channel, user);

				}
			});

			return null;
		}
		else {
			Main.bot.SendMessage(channel, String.format("<@%s> " , user.getId()) +
					String.format(Bot.config.GetString("blocked-commands-message"), blockedCommand(command.substring(1))));
			return null;
		}
	}

	void SendException(Exception e, MessageChannel channel, User user) {
		String error = String.format("<@%s> Unknown command.\n", user.getId());
		error += e.getMessage();
		error += String.join("\n", Arrays.stream(e.getStackTrace())
				.map(StackTraceElement::toString)
				.toList());
		if(e.getCause() != null) {
			error += "\nCaused by: " + e.getCause().getMessage();
		}
		Main.bot.SendMessage(channel, error);
	}

	boolean isStaff(Member user) {
		List<String> staff = Bot.config.GetStringList("staff");
		for(String id : staff) {
			for(Role role : user.getRoles()) {
				if(id.equalsIgnoreCase(role.getId())) {
					return true;
				}
			}
		}
		return false;
	}

	boolean guildMaster(Member user) {
		for(Role role : user.getRoles()) {
			//Log.Info(Main.plugin, role.getName() + " - " + role.getId());
			if(role.getId().equalsIgnoreCase("1222980440416190696")) return true;
		}

		return false;
	}

	boolean hasRole(Member user, String roleID) {
		for(Role role : user.getRoles()) {
			//Log.Info(Main.plugin, role.getName() + " - " + role.getId());
			if(role.getId().equalsIgnoreCase(roleID)) return true;
		}

		return false;
	}

	String getBadge(Member user) {
		String badges = "";
		Map<String, String> map = new HashMap<String, String>();
		for(Object entry : Bot.config.GetConfigurationSection("role-badges").getKeys(false)) {
			String key = entry.toString();
			String value = Bot.config.GetString("role-badges." + entry.toString());
			map.put(key, value);
		}

		for(Entry<String, String> entry : map.entrySet()) {
			if(hasRole(user, entry.getKey())) {
				//Log.Error(Main.plugin, entry.getKey() + "");
				badges = badges + Lang.Parse(entry.getValue());
			}
		}

		return badges;
	}

	@Nullable
	public Role getHighestFrom(Member member) {
		if (member == null)
			return null; 
		List<Role> roles = member.getRoles();
		if (roles.isEmpty())
			return null; 
		return roles.stream().min((first, second) -> (first.getPosition() == second.getPosition()) ? 0 : ((first.getPosition() > second.getPosition()) ? -1 : 1)).get();
	}

	public String blockedWord(String string) {
		// Retrieve the list of blocked words
		List<String> list = Main.filter.GetStringList("chat-filter");
		String filtermsg = string.toLowerCase();

		// Loop through each blocked word
		for (String entry : list) {
			// Create a regex pattern for whole word matching
			String regex = "\\b" + Pattern.quote(entry.toLowerCase()) + "\\b";

			// Check if the message contains the blocked word as a whole word
			if (filtermsg.matches(".*" + regex + ".*")) {
				return entry; // Return the blocked word
			}
		}
		return null; // Return null if no blocked word is found
	}

	public String blockedCommand(String string) {
		// Retrieve the list of blocked commands
		List<String> list = Bot.config.GetStringList("blocked-commands");

		// Extract the first word (command) from the input string
		String command = string.split("\\s+")[0].toLowerCase();

		// Loop through the blocked commands list
		for (String entry : list) {
			if (command.equals(entry.toLowerCase())) {
				return entry; // Return the blocked command if found
			}
		}

		return null; // Return null if no match is found
	}

	public static String removeFirstBracketedText(String input) {
		// Regular expression to match text inside the first set of brackets
		String regex = "\\[.*?\\] ";

		// Use String.replaceFirst to remove the first match
		return input.replaceFirst(regex, "");
	}

	// Method to check if the string contains a web link (URL)
	public static boolean containsUrl(String string) {
		// Regular expression for matching URLs, allowing non-standard TLDs
		String urlRegex = "(https?://|www\\.)[\\w.-]+(?:\\.[a-zA-Z]{2,}|\\.[a-zA-Z0-9-]{2,})+(?:/[\\w&%#=./-]*)?";

		// Compile the regex
		Pattern pattern = Pattern.compile(urlRegex);
		Matcher matcher = pattern.matcher(string);

		// Return true if a URL is found, otherwise false
		return matcher.find();
	}

	void ProccessDiscordCommand(MessageChannel channel, Member member, User user, String command) {
		//Log.Info(Main.plugin, "staff?");
		Log.Info(Main.plugin, "User: " + user.getName());
		Log.Info(Main.plugin, "Command: " + command);
		Main.bot.SendMessage(channel, String.format("<@%s> I've upgraded to use commands.\nTry `/help`", user.getId()));
		
		/*
		
		String message = "";
		String cmd = "";
		String fullCmd = command.substring(1).toLowerCase();
		if(command.contains(" ")) {
			String[] parts = command.split(" ");
			cmd = parts[0].substring(1).toLowerCase().trim(); // Convert to lowercase and remove spaces
		}else {
			cmd = command.substring(1).toLowerCase().trim();
		}

		switch (cmd) {
		case "help":
			message = String.format("<@%s> Here's a list of my commands:", user.getId());
			message += "\n`!help` - You are here";
			message += "\n`!online` - Lists all online players";
			message += "\n`!uptime` - How long the server's been up.";
			message += "\n`!link <username>` - Link your Minecraft account to Discord.";
			message += "\n`!unlink` - Unlink your Minecraft account from Discord.";
			if(Linking.isLinked(user.getIdLong())) {
				message += "\\n`!pay <username> <amount>` - Pay a player.";
			}
			Main.bot.SendMessage(channel, message);
			
			break;

		case "online":
			int online = (EssentialsHook.isHooked()) ? EssentialsHook.visiblePlayers().size() : Bukkit.getOnlinePlayers().size();
			message = String.format("<@%s> Here's a list of %s online players:", user.getId(), online);
			for(Player player : Bukkit.getOnlinePlayers()) {
				if(EssentialsHook.isHooked()) {
					IUser pl = EssentialsHook.getInstance().getUser(player);
					if(pl.isVanished()) continue;
					if(pl.isAfk()) message += "\n`" + player.getName() + "` *AFK*";
					else message += "\n`" + player.getName() + "`";
				}else {
					message += "\n`" + player.getName() + "`";
				}
			}
			Main.bot.SendMessage(channel, message);
			break;

		case "uptime":
			message = String.format("<@%s> Server Uptime: %s", user.getId(), ServerStats.getUptime());
			Main.bot.SendMessage(channel, message);
			break;

		case "mem":
			message = String.format("<@%s> %s", user.getId(), ServerStats.slashMem());
			Main.bot.SendMessage(channel, message);
			break;
			
		case "link":
			if(command.length() <= 5) {
				Main.bot.SendMessage(channel, String.format("<@%s> Usage: `!link <username>`", user.getId()));
				break;
			}
			if(Linking.isLinked(user.getIdLong())) {
				Main.bot.SendMessage(channel, String.format("<@%s> Your Discord account is already linked to a Minecraft account. Use `!unlink` to unlink first.", user.getId()));
				break;
			}
			Player player = Linking.getPlayer(fullCmd.substring(5).trim());
			if(player != null) {
				Linking.addLink(user.getIdLong(), player.getUniqueId(), channel.getIdLong());
			}else {
				Main.bot.SendMessage(channel, String.format("<@%s> Sorry, could not find this player, Are you online? (CaPs SeNsItIvE)", user.getId()));
			}
			break;
			
		case "unlink":
			if(Linking.unlink(user.getIdLong())) {
				Main.bot.SendMessage(channel, String.format("<@%s> Your Discord account has been unlinked from your Minecraft account.", user.getId()));
			} else {
				Main.bot.SendMessage(channel, String.format("<@%s> Your Discord account is not linked to any Minecraft account.", user.getId()));
			}
			break;


		default:
			Main.bot.SendMessage(channel, String.format("<@%s> Sorry, I don't know this command.\nTry `!help`", user.getId()));
			break;
		}
	*/
	}
}