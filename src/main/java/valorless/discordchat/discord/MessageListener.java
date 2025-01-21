package valorless.discordchat.discord;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import valorless.discordchat.ChatListener;
import valorless.discordchat.DiscordWebhook;
import valorless.discordchat.Lang;
import valorless.discordchat.Main;
import valorless.valorlessutils.ValorlessUtils.Log;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MessageListener extends ListenerAdapter { 
	public List<String> monitoredChannels;
	DiscordUtils utils = new DiscordUtils();
  
	public MessageListener() {
		this.monitoredChannels = Bot.config.GetStringList("channels");
	}
  
	public void onMessageReceived(@NotNull MessageReceivedEvent event) {
		Member member = event.getMember();
		if (!monitoredChannels.contains(event.getChannel().getId())) return; 
		if (event.getAuthor().isBot() && !Bot.config.GetBool("bot-messages")) return; 
		boolean reply = event.getMessage().getType() == MessageType.INLINE_REPLY;

		Bot.newChain().async(() -> {
			String message = event.getMessage().getContentStripped();
			if (member == null) return; 
			String username = event.getAuthor().getName();
			String guildName = event.getGuild().getName();
			String channel = event.getMessage().getChannel().getName();
			String displayname = utils.getUserGlobalName(event.getAuthor().getId());
			String nickname = (member.getNickname() != null) ? member.getNickname() : displayname;
			String badge = getBadge(member);
			Role mainRole = getHighestFrom(member);
			String role = (mainRole != null) ? mainRole.getName() : "";
			String chatMessage = Bot.config.GetString("message-format");

			if(containsUrl(message)) {
				event.getMessage().delete();
				DiscordWebhook webhook = new DiscordWebhook(Main.config.GetString("webhook-url"));

				webhook.setUsername(Main.config.GetString("server-username"));
				webhook.setContent(ChatListener.FormatMessage(null, 
						String.format("<@%s> " , event.getAuthor().getId()) +
						String.format("Links and media links do not work in this channel.")
						));
				webhook.setAvatarUrl(Main.config.GetString("server-icon-url"));

				try {
					webhook.execute();
				} catch (IOException e) {
					e.printStackTrace();
					Log.Error(Main.plugin, "Connection failed.");
					Main.error = true;
					Log.Error(Main.plugin, "Attempting to reconnect soon.");
					Log.Error(Main.plugin, "Plugin disabled regular connections to avoid further failed connections.");
					Log.Error(Main.plugin, "Please reload the plugin to manually re-enable");
				}
				return;
			}

			try {
				char c = message.charAt(0);
				char prefix = Bot.config.GetString("command-prefix").charAt(0);
				//Log.Info(Main.plugin, c + "");
				//Log.Info(Main.plugin, prefix + "");
				if(c == prefix) {
					Log.Info(Main.plugin, "Command");
					message = ProccessCommand(member, event.getAuthor(), message);
					if(message == null) return;
					Log.Info(Main.plugin, "Command failed");
				}
			}catch(Exception e) {}

			chatMessage = Lang.hex(chatMessage);
			chatMessage = chatMessage.replace("&", "§");
			chatMessage = chatMessage.replace("%username%", username)
					.replace("%displayname%", displayname)
					.replace("%nickname%", reply ? nickname + " (Reply)" : nickname)
					.replace("%server%", guildName)
					.replace("%message%", message)
					.replace("%channel%", channel)
					.replace("%role%", role)
					.replace("%badge%", badge);


			if(blockedWord(chatMessage) == null) { 
				if(reply) {
					String name = utils.getUserGlobalName(event.getMessage().getReferencedMessage().getAuthor().getId());
					if(name.equalsIgnoreCase("No global name set")) name = event.getMessage().getReferencedMessage().getAuthor().getName();
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
				}else {
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
				DiscordWebhook webhook = new DiscordWebhook(Main.config.GetString("webhook-url"));

				webhook.setUsername(Main.config.GetString("server-username"));
				webhook.setContent(ChatListener.FormatMessage(null, 
						String.format("<@%s> " , event.getAuthor().getId()) +
						String.format(Main.filter.GetString("chat-filter-message"), blockedWord(chatMessage))
						));
				webhook.setAvatarUrl(Main.config.GetString("server-icon-url"));

				try {
					webhook.execute();
				} catch (IOException e) {
					e.printStackTrace();
					Log.Error(Main.plugin, "Connection failed.");
					Main.error = true;
					Log.Error(Main.plugin, "Attempting to reconnect soon.");
					Log.Error(Main.plugin, "Plugin disabled regular connections to avoid further failed connections.");
					Log.Error(Main.plugin, "Please reload the plugin to manually re-enable");
				}
			}
		}).execute();
	}

	String ProccessCommand(Member member, User user, String command) {
		//Log.Info(Main.plugin, "staff?");
		Log.Info(Main.plugin, "User: " + user.getName());
		Log.Info(Main.plugin, "Command: " + command);
		if(!isStaff(member)) {
			DiscordWebhook webhook = new DiscordWebhook(Main.config.GetString("webhook-url"));
			
			webhook.setUsername(Main.config.GetString("server-username"));
			webhook.setContent(ChatListener.FormatMessage(null, String.format("<@%s> only staff may use commands.", user.getId())));
			webhook.setAvatarUrl(Main.config.GetString("server-icon-url"));
				
			try {
				webhook.execute();
			} catch (IOException e) {
				e.printStackTrace();
				Log.Error(Main.plugin, "Connection failed.");
				Main.error = true;
				Log.Error(Main.plugin, "Attempting to reconnect soon.");
				Log.Error(Main.plugin, "Plugin disabled regular connections to avoid further failed connections.");
				Log.Error(Main.plugin, "Please reload the plugin to manually re-enable");
			}
			//Log.Info(Main.plugin, "not staff");
			return null;
		}
		
		if(blockedCommand(command.substring(1)) == null) { 
			//Log.Info(Main.plugin, "Sending command");
			Bukkit.getScheduler().runTask(Main.plugin, () -> {
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), command.substring(1));
			});
			
			return null;
		}
		else {
			DiscordWebhook webhook = new DiscordWebhook(Main.config.GetString("webhook-url"));
			
			webhook.setUsername(Main.config.GetString("server-username"));
			webhook.setContent(ChatListener.FormatMessage(null, 
					String.format("<@%s> " , user.getId()) +
					String.format(Bot.config.GetString("blocked-commands-message"), blockedCommand(command.substring(1)))
					));
			webhook.setAvatarUrl(Main.config.GetString("server-icon-url"));
				
			try {
				webhook.execute();
			} catch (IOException e) {
				e.printStackTrace();
				Log.Error(Main.plugin, "Connection failed.");
				Main.error = true;
				Log.Error(Main.plugin, "Attempting to reconnect soon.");
				Log.Error(Main.plugin, "Plugin disabled regular connections to avoid further failed connections.");
				Log.Error(Main.plugin, "Please reload the plugin to manually re-enable");
			}
			return null;
		}
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
}