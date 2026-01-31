package valorless.discordchat;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

/**
 * Tab completer for RareSpawns commands.
 * <p>
 * Provides context-aware suggestions for subcommands and arguments based on the
 * sender's permissions and current argument position.
 * Supported subcommands include: reload, item, give, spawn, and kill.
 */
public class TabCompletion implements TabCompleter {

	/**
	 * Computes tab-completion candidates for RareSpawns.
	 * <p>
	 * Behavior:
	 * <ul>
	 *   <li>args[0]: suggests subcommands permitted for the sender.</li>
	 *   <li>args[1]: suggests sub-args for reload (abilities, config, entities, items, soulpowers),
	 *       item (configured item ids), spawn (configured entity ids), kill (&lt;radius&gt;), give (online player names).</li>
	 *   <li>args[2]: suggests online player names for spawn and "random" for item, and item ids for give subcommand.</li>
	 *   <li>args[3]: suggests "random" for give subcommand.</li>
	 * </ul>
	 * Results are filtered using {@link StringUtil#copyPartialMatches(String, java.util.List, java.util.List)}.
	 * </p>
	 *
	 * @param sender the command sender requesting suggestions
	 * @param command the command being executed
	 * @param alias the alias used to invoke the command
	 * @param args the raw arguments passed so far
	 * @return a list of suggestions (never null)
	 */
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		// Collect matches into a mutable list
		List<String> completions = new ArrayList<>();

		// First argument: suggest available subcommands by permission
		if (args.length == 1) {
			List<String> subCommands = new ArrayList<>();
			if (sender.hasPermission("discordchat.reload")) {
				subCommands.add("reload");
			}
			if (sender.hasPermission("discordchat.link")) {
				subCommands.add("link");
				subCommands.add("unlink");
			}
			if (sender.hasPermission("rarespawns.mute")) {
				subCommands.add("mute");
			}

			StringUtil.copyPartialMatches(args[0], subCommands, completions);
		// Second argument: depends on the chosen subcommand
		}else if(args.length == 2) {
			String cmd = args[1];
			if (args[0].equalsIgnoreCase("link") && sender.hasPermission("discordchat.link")) {
				List<String> commands = new ArrayList<>();
				commands.add("<discord username>");
				
				StringUtil.copyPartialMatches(cmd, commands, completions);
			}
		}
		// Return sorted completions
		return completions;
	}
}