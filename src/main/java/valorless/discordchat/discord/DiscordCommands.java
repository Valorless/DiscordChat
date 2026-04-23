package valorless.discordchat.discord;

import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.ess3.api.IUser;
import valorless.discordchat.DiscordWebhook;
import valorless.discordchat.Main;
import valorless.discordchat.PlayerCache;
import valorless.discordchat.hooks.Eco;
import valorless.discordchat.hooks.EssentialsHook;
import valorless.discordchat.linking.Linking;
import valorless.discordchat.storage.Storage;
import valorless.discordchat.utils.Extra;
import valorless.discordchat.utils.ServerStats;
import valorless.valorlessutils.logging.Log;

public class DiscordCommands extends ListenerAdapter {
	
	private final static HashMap<String, String> defaultCommands = new HashMap<>() {
		private static final long serialVersionUID = 1L;
		{
			put("/help", "You are here");
			put("/online", "Lists all online players");
			put("/uptime", "How long the server's been up.");
			put("/link", "Link your Minecraft account to Discord.");
			put("/unlink", "Unlink your Minecraft account from Discord.");
			//put("/memory", "Check server memory usage.");
			put("/stats", "Lookup mcMMO stats for a player.");
		}
	};
	
	private final static HashMap<String, String> linkedCommands = new HashMap<>() {
		// Auto-sorted
		private static final long serialVersionUID = 1L;
		{
			put("/pay", "Pay a player.");
			put("/balance", "Check your balance.");
			//put("/memory", "Check server memory usage.");
			put("/inventory", "View your Minecraft inventory.");
			put("/enderchest", "View your Minecraft enderchest.");
		}
	};

	/**
	 * Handle incoming slash command interactions from Discord.
	 *
	 * <p>Supports commands: help, online, uptime, memory, unlink, link, and pay.
	 * Each command replies ephemerally to the invoking user, and some open a modal
	 * for additional input.</p>
	 *
	 * @param event the slash command interaction event from JDA
	 */
	@SuppressWarnings("deprecation")
	@Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		Log.info(Main.plugin, "User: " + event.getUser().getName());
		Log.info(Main.plugin, "Command: " + event.getName());
		User user = event.getUser();
		
		/** Help command
		 * Replies with a list of available commands.
		 * Includes additional commands if the user is linked.
		 */
		if(event.getName().equals("help")) {
			String message = "Here's a list of my commands:";
			for(String cmd : defaultCommands.keySet()) {
				message += "\n`" + cmd + "` - " + defaultCommands.get(cmd);
			}
			if(Linking.isLinked(user.getIdLong())) {
				for(String cmd : new TreeMap<>(linkedCommands).keySet()) {
					message += "\n`" + cmd + "` - " + linkedCommands.get(cmd);
				}
			}
			event.reply(message).setEphemeral(true).queue();
			return;
		}
		
		/** Online command
		 * Replies with a list of currently online players.
		 * If Essentials is hooked, it filters out vanished players and marks AFK players.
		 */
		if(event.getName().equals("online")) {
			int online = (EssentialsHook.isHooked()) ? EssentialsHook.visiblePlayers().size() : Bukkit.getOnlinePlayers().size();
			String message = String.format("Here's a list of %s online players:", online);
			for(Player player : Bukkit.getOnlinePlayers()) {
				if(EssentialsHook.isHooked()) {
					IUser pl = EssentialsHook.getInstance().getUser(player);
					if(pl.isVanished()) continue;
					if(pl.isHidden()) continue;
					if(pl.isAfk()) message += "\n`" + player.getName() + "` *AFK*";
					else message += "\n`" + player.getName() + "`";
				}else {
					message += "\n`" + player.getName() + "`";
				}
			}
			event.reply(message).setEphemeral(true).queue();
			return;
		}

		/** Uptime command
		 * Replies with the server's uptime.
		 * Uses the ServerStats utility to retrieve uptime information.
		 */
		if(event.getName().equals("uptime")) {
			event.reply(ServerStats.getUptime()).setEphemeral(true).queue();
			return;
		}

		/** Memory command
		 * Replies with the server's current memory usage statistics.
		 * Uses the ServerStats utility to retrieve memory information.
		 */
		if(event.getName().equals("memory")) {
			event.reply(ServerStats.slashMem()).setEphemeral(true).queue();
			return;
		}
		
		/** Unlink command
		 * Unlinks the user's Discord account from their linked Minecraft account.
		 * Uses the Linking hook to perform the unlinking operation.
		 */
		if(event.getName().equals("unlink")) {
			TextInput info = TextInput.create("username",
							Storage.Accounts.dataFile.getString("discord-ui.unlink.information-title"), TextInputStyle.SHORT)
                    .setPlaceholder("MasterMiner42")
                    .setValue(Storage.Accounts.dataFile.getString("discord-ui.unlink.information-info"))
                    .setRequired(false)
                    .build();
			
            Modal modal = Modal.create("unlink",
							Storage.Accounts.dataFile.getString("discord-ui.unlink.title"))
            	    .addActionRow(List.of(info))
                    .build();

            event.replyModal(modal).queue();
			return;
		}
		
		/** Link command
		 * Opens a modal to input the Minecraft username to link with the Discord account.
		 * Uses the Linking hook to handle the linking upon modal submission.
		 */
        if(event.getName().equals("link")) {
            TextInput info = TextInput.create("info",
							Storage.Accounts.dataFile.getString("discord-ui.link.information-title"), TextInputStyle.PARAGRAPH)
                    .setPlaceholder("MasterMiner42")
                    .setValue(String.join("\n",
							Storage.Accounts.dataFile.getStringList("discord-ui.link.information-info")))
                    .setRequired(false)
                    .build();
            
            TextInput username = TextInput.create("username",
							Storage.Accounts.dataFile.getString("discord-ui.link.username-title"), TextInputStyle.SHORT)
                    .setPlaceholder("MasterMiner42")
                    .setValue(event.getOption("username") != null ? event.getOption("username").getAsString() : null)
                    .build();

            Modal modal = Modal.create("link",
							Storage.Accounts.dataFile.getString("discord-ui.link.title"))
            	    .addActionRow(List.of(info))
            	    .addActionRow(List.of(username))
                    .build();

            event.replyModal(modal).queue();
			return;
        }
		
        /** Ensure the user is linked for the following commands **/
		if(!Linking.isLinked(user.getIdLong())) {
			//event.reply("You must link your Discord account to a Minecraft account first using `/link`.")
			event.reply(Storage.Accounts.dataFile.getString("lang.not-linked.discord"))
				.setEphemeral(true).queue();
			return;
		}
		
		/** Pay command
		 * Opens a modal to input the recipient's username and the amount to pay.
		 * Uses the Eco hook to handle the transaction upon modal submission.
		 */
        if(event.getName().equals("pay")) {
            TextInput username = TextInput.create("username", "Player", TextInputStyle.SHORT)
                    .setPlaceholder("MasterMiner42")
                    .setValue(event.getOption("username") != null ? event.getOption("username").getAsString() : null)
                    .build();

            TextInput body = TextInput.create("amount", "Amount", TextInputStyle.SHORT)
                    .setPlaceholder("100")
                    .setValue(event.getOption("amount") != null ? event.getOption("amount").getAsString() : null)
                    .build();

            Modal modal = Modal.create("pay", "Pay a Player")
            	    .addActionRow(List.of(username))
            	    .addActionRow(List.of(body))
                    .build();

            event.replyModal(modal).queue();
			return;
        }
        
        /** Balance command
         * Replies with the user's current in-game balance.
         * Uses the Eco hook to retrieve the balance based on the linked Minecraft UUID.
         */
        if(event.getName().equals("balance")) {
        	String result = Eco.getBalanceFormatted(Linking.getMinecraftUUID(user.getIdLong()));
        	event.reply("Your current balance is **" + result + "**")
            .addActionRow(Button.primary("balance-share", "Share"))
        	.setEphemeral(true).queue();
        	return;
        }
        
        if(event.getName().equals("inventory")) {
        	//event.reply("Inventory command is not yet implemented.").setEphemeral(true).queue();
        	if(Storage.Inventories.getInventory(Linking.getMinecraftUUID(user.getIdLong())) == null) {
				event.reply("No inventory data found for your Minecraft account.\n"
						+ "Please log in to populate the database.").setEphemeral(true).queue();
				return;
			}
        	if(Extra.inventoryString(Linking.getMinecraftUUID(user.getIdLong())).length() > 1950) {
        		event.reply("Your inventory data is too large to display here.\nPlease use the in-game command to view it.").setEphemeral(true).queue();
				return;
        	}

			String result = "## Your Inventory:";
			OfflinePlayer player = Bukkit.getOfflinePlayer(Linking.getMinecraftUUID(user.getIdLong()));

			MessageEmbed embed = new EmbedBuilder()
					.setTitle(String.format("%s's Inventory", player.getName()))
					.setDescription(Extra.inventoryString(player.getUniqueId()))
					.build();

			ReplyCallbackAction reply = event.reply(result); // send a message in the channel
			reply.addEmbeds(embed);
			reply.addActionRow(Button.primary("inv-share", "Share"));
			reply.setEphemeral(true);
			try {
				reply.queue();
			}catch(Exception e) {
				event.reply("An error occurred while sending your inventory data. Please try again later.").setEphemeral(true).queue();
			}

        	//event.reply("## Your inventory:\n" +
        	//		Extra.inventoryString(Linking.getMinecraftUUID(user.getIdLong())))
            //.addActionRow(Button.primary("inv-share", "Share"))
            //.setEphemeral(true)
            //.queue();
			return;
        }
        
        if(event.getName().equals("enderchest")) {
			//event.reply("Enderchest command is not yet implemented.").setEphemeral(true).queue();
        	if(Storage.Enderchests.getEnderchest(Linking.getMinecraftUUID(user.getIdLong())) == null) {
				event.reply("No enderchest data found for your Minecraft account.\n"
						+ "Please log in to populate the database.").setEphemeral(true).queue();
				return;
			}
        	if(Extra.enderchestString(Linking.getMinecraftUUID(user.getIdLong())).length() > 1950) {
        		event.reply("Your enderchest data is too large to display here.\nPlease use the in-game command to view it.").setEphemeral(true).queue();
				return;
        	}
        	//event.reply("## Your enderchest:\n" +
        	//		Extra.enderchestString(Linking.getMinecraftUUID(user.getIdLong())))
            //.addActionRow(Button.primary("ender-share", "Share"))
            //.setEphemeral(true)
            //.queue();

			String result = "## Your Enderchest:";
			OfflinePlayer player = Bukkit.getOfflinePlayer(Linking.getMinecraftUUID(user.getIdLong()));

			MessageEmbed embed = new EmbedBuilder()
					.setTitle(String.format("%s's Enderchest", player.getName()))
					.setDescription(Extra.enderchestString(player.getUniqueId()))
					.build();

			ReplyCallbackAction reply = event.reply(result); // send a message in the channel
			reply.addEmbeds(embed);
			reply.addActionRow(Button.primary("ender-share", "Share"));
			reply.setEphemeral(true);
			try {
				reply.queue();
			}catch(Exception e) {
				event.reply("An error occurred while sending your enderchest data. Please try again later.").setEphemeral(true).queue();
			}
			return;
		}
        
        if(event.getName().equals("stats")) {
            TextInput username = TextInput.create("username", "Minecraft Username", TextInputStyle.SHORT)
                    .setPlaceholder("MasterMiner42")
                    .setValue(Linking.isLinked(event.getUser().getIdLong()) ? 
                    		Bukkit.getOfflinePlayer(Linking.getMinecraftUUID(event.getUser().getIdLong())).getName(): null)
                    .build();

            Modal modal = Modal.create("stats", "mcMMO Stats Lookup")
            	    .addActionRow(List.of(username))
                    .build();

            event.replyModal(modal).queue();
			return;
        }
        
        event.reply("An error occurred while processing your command. Please try again later.").setEphemeral(true).queue();
    }
	
	@Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
		User user = event.getUser();
		//Guild server = event.getGuild();
		
        if (event.getComponentId().equals("inv-share")) {
        	String result = String.format("## <@%s>'s Inventory:",
					user.getIdLong());

			OfflinePlayer player = Bukkit.getOfflinePlayer(Linking.getMinecraftUUID(user.getIdLong()));

			MessageEmbed embed = new EmbedBuilder()
					.setTitle(String.format("%s's Inventory", player.getName()))
					.setDescription(Extra.inventoryString(player.getUniqueId()))
					.build();

			ReplyCallbackAction reply = event.reply(result); // send a message in the channel
			reply.addEmbeds(embed);
			try {
				reply.queue();
			}catch(Exception e) {
				event.reply("An error occurred while sharing your inventory data. Please try again later.").setEphemeral(true).queue();
			}
			//String result = String.format("## <@%s>'s inventory:\n%s",

			//event.getMessage().delete().queue();
            return;
        }
		
        if (event.getComponentId().equals("ender-share")) {
			String result = String.format("## <@%s>'s Enderchest:",
					user.getIdLong());

			OfflinePlayer player = Bukkit.getOfflinePlayer(Linking.getMinecraftUUID(user.getIdLong()));

			MessageEmbed embed = new EmbedBuilder()
					.setTitle(String.format("%s's Enderchest", player.getName()))
					.setDescription(Extra.enderchestString(player.getUniqueId()))
					.build();

			ReplyCallbackAction reply = event.reply(result); // send a message in the channel
			reply.addEmbeds(embed);
			try {
				reply.queue();
			}catch(Exception e) {
				event.reply("An error occurred while sharing your enderchest data. Please try again later.").setEphemeral(true).queue();
			}


        	//String result = String.format("## <@%s>'s enderchest:\n%s",
			//		user.getIdLong(),
			//		Extra.enderchestString(Linking.getMinecraftUUID(user.getIdLong())));
            //event.reply(result).queue(); // send a message in the channel
        	//event.getMessage().delete().queue();
            return;
        }
		
        if (event.getComponentId().equals("balance-share")) {
        	String result = Eco.getBalanceFormatted(Linking.getMinecraftUUID(user.getIdLong()));
        	event.reply(String.format("<@%s>'s balance: **%s**", 
					user.getIdLong(),
					result))
        	.queue(); // send a message in the channel
        	//event.reply("Your current balance is **" + result + "**").queue();
        	//event.getMessage().delete().queue();
            return;
        }
    }
	
	/**
	 * Handle submitted modal interactions opened by slash commands.
	 *
	 * <p>Processes the "link" modal to associate a Discord user with a Minecraft UUID,
	 * and the "pay" modal to transfer in-game currency between players.</p>
	 *
	 * @param event the modal interaction event from JDA
	 */
	@Override
    public void onModalInteraction(ModalInteractionEvent event) {
		User user = event.getUser();
		//Guild server = event.getGuild();
		
		/** Link modal
		 * Processes the linking of a Discord account to a Minecraft account.
		 * Validates if the user is already linked and if the Minecraft player exists.
		 */
		if(event.getModalId().equals("link")) {
			String username = event.getValue("username").getAsString();

			if(Linking.isLinked(user.getIdLong())) {
				//event.reply(String.format("Your Discord account is already linked to a Minecraft account. Use `/unlink` to unlink first."))
				event.reply(Storage.Accounts.dataFile.getString("lang.already-linked.discord"))
					.setEphemeral(true).queue();
				return;
			}
			Player player = Linking.getPlayer(username);
			if(player != null) {
				Linking.addLink(user.getIdLong(), player.getUniqueId(), event.getChannelIdLong());
			}else {
				//event.reply(String.format("Sorry, could not find this player, Are you online? (CaPs SeNsItIvE)"))
				event.reply(Storage.Accounts.dataFile.getString("lang.not-found.discord"))
					.setEphemeral(true).queue();
				return;
			}


			event.reply(Storage.Accounts.dataFile.getString("lang.request.discord").replace("%player%", username))
					.setEphemeral(true).queue();
			//event.reply("Link request received for username: " + username).setEphemeral(true).queue();
			return;
		}
		
		if(event.getModalId().equals("unlink")) {
			if(!Linking.isLinked(user.getIdLong())) {
				//event.reply("You are not linked to any Minecraft account.").setEphemeral(true).queue();
				event.reply(Storage.Accounts.dataFile.getString("lang.not-found-account.discord")).setEphemeral(true).queue();
				return;
			}
			if(Linking.unlink(user.getIdLong())) {
				//event.reply("Your Discord account has been unlinked from your Minecraft account.").setEphemeral(true).queue();
				event.reply(Storage.Accounts.dataFile.getString("lang.unlinked.discord")).setEphemeral(true).queue();
			}else {
				//event.reply("An error occurred while trying to unlink your account. Please try again later.").setEphemeral(true).queue();
				event.reply(Storage.Accounts.dataFile.getString("lang.unlink-error.discord")).setEphemeral(true).queue();
			}
			return;
		}
		
		/** Ensure the user is linked for the following modals **/
		if(!Linking.isLinked(user.getIdLong())) {
			//event.reply("You must link your Discord account to a Minecraft account first using `/link`.")
			event.reply(Storage.Accounts.dataFile.getString("lang.not-linked.discord"))
				.setEphemeral(true).queue();
			return;
		}
		
		/** Pay modal
		 * Processes the payment of in-game currency from one player to another.
		 * Validates if the payer can afford the amount and if the recipient exists.
		 */
        if(event.getModalId().equals("pay")) {
            String username = event.getValue("username").getAsString();
            Double amount = Double.valueOf(event.getValue("amount").getAsString());
            UUID uuid = Linking.getMinecraftUUID(user.getIdLong());
            OfflinePlayer payer = Bukkit.getOfflinePlayer(uuid);
            UUID recipientUUID = PlayerCache.getUUID(username);
            Long recipientID = Linking.getDiscordID(recipientUUID);
            OfflinePlayer recipient = PlayerCache.getPlayer(username);
            
            if(uuid.equals(recipientUUID)) {
            	event.reply("You cannot pay yourself.").setEphemeral(true).queue();
            	return;
            }
            
            if(recipient == null) {
				event.reply(String.format("Player %s cannot be found.", username)).setEphemeral(true).queue();
				return;
			}
            
        	String result = Eco.formatMoney(amount);
            if(Eco.canAfford(payer.getUniqueId(), amount)) {
            	Boolean success = Eco.pay(payer.getUniqueId(), recipient.getUniqueId(), amount);
            	if(!success) {
					event.reply("An error occurred while processing the payment. Please try again later.").setEphemeral(true).queue();
					return;
				}
            	if(recipientID != null) {
            		event.reply(String.format("<@%s> paid <@%s> **%s**!", user.getIdLong(), recipientID, result)).queue();
            		return;
				}
            	event.reply(String.format("<@%s> paid %s **%s**!", user.getIdLong(), username, result)).queue();
            }else {
            	event.reply(String.format("You cannot afford to pay %s %s.", username, result)).setEphemeral(true).queue();
            }
            return;
        }
        
		if(event.getModalId().equals("stats")) {
			String username = event.getValue("username").getAsString();
			UUID playerUUID = PlayerCache.getUUID(username);
			PlayerProfile profile = mcMMO.getDatabaseManager().loadPlayerProfile(playerUUID);
			
			if(profile != null) {
				String msg = String.format("## mcMMO Stats for %s:\n", username);
				int totalLevel = 0;
				for(PrimarySkillType pst : PrimarySkillType.values()) {
					int level = profile.getSkillLevel(pst);
					double xp = profile.getSkillXpLevel(pst);
					double max = profile.getXpToLevel(pst);
					msg += String.format(" - **%s** - Level: **%d** - XP: %.2f/%.2f\n", 
							Extra.UppercaseFirstLetter(pst.name()), level, xp, max);
					totalLevel += level;
				}
				msg += String.format("**Total Level:** %d", totalLevel);
				
				event.reply(msg).setEphemeral(true).queue();
				return;
			}

			event.reply("Failed to retrieve stats for " + username + ".").setEphemeral(true).queue();
			return;
		}
    }
	
}