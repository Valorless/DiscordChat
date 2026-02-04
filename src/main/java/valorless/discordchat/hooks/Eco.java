package valorless.discordchat.hooks;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.earth2me.essentials.User;
import net.ess3.api.IEssentials;
import net.ess3.api.MaxMoneyException;
import net.ess3.api.events.UserBalanceUpdateEvent.Cause;
import valorless.discordchat.Main;
import valorless.valorlessutils.ValorlessUtils.Log;

/**
 * Essentials economy helper.
 *
 * <p>Provides convenience methods to check balances, transfer funds, and format
 * currency values via the Essentials/EssentialsX API. Call {@link #init()} once
 * during plugin startup to acquire the Essentials API instance before using the
 * other methods.</p>
 */
public class Eco {
	
	/**
	 * Cached Essentials API instance used for all economy operations.
	 */
	private static IEssentials ess;
	
	/**
	 * Initialize the economy hook by acquiring the Essentials API instance.
	 *
	 * <p>Must be called during plugin initialization before any other method
	 * in this class is used.</p>
	 */
	public static void init() {
		ess = (IEssentials) Bukkit.getPluginManager().getPlugin("Essentials");
	}
	
	/**
	 * Determine whether a player can afford the specified amount.
	 *
	 * @param player the online Player to check
	 * @param amount the amount to compare against the player's balance
	 * @return true if balance is greater than or equal to amount, false otherwise
	 */
	public static Boolean canAfford(Player player, Double amount) {
		User user = ess.getUser(player);
		BigDecimal bal = user.getMoney();
		BigDecimal price = BigDecimal.valueOf(amount);
		
		if(bal.compareTo(price) == -1) return false;
		else return true;
	}
	
	/**
	 * Determine whether a player can afford the specified amount.
	 *
	 * @param player the player's UUID
	 * @param amount the amount to compare against the player's balance
	 * @return true if balance is greater than or equal to amount, false otherwise
	 */
	public static Boolean canAfford(UUID player, Double amount) {
		User user = ess.getUser(player);
		BigDecimal bal = user.getMoney();
		BigDecimal price = BigDecimal.valueOf(amount);
		
		if(bal.compareTo(price) == -1) return false;
		else return true;
	}
	
	/**
	 * Withdraw currency from a player's balance.
	 *
	 * @param player the online Player to withdraw from
	 * @param amount the amount to withdraw
	 */
	public static void takeMoney(Player player, Double amount) {
		User user = ess.getUser(player);
		user.takeMoney(BigDecimal.valueOf(amount));
		Log.Info(Main.plugin, "Took " + amount + " from " + player.toString());
	}
	
	/**
	 * Withdraw currency from a player's balance.
	 *
	 * @param player the player's UUID
	 * @param amount the amount to withdraw
	 */
	public static void takeMoney(UUID player, Double amount) {
		User user = ess.getUser(player);
		user.takeMoney(BigDecimal.valueOf(amount));
		Log.Info(Main.plugin, "Took " + amount + " from " + user.getName());
	}
	
	/**
	 * Deposit currency into a player's balance.
	 *
	 * @param player the online Player to deposit to
	 * @param amount the amount to deposit
	 * @return true if the deposit succeeded, false if it would exceed the max balance
	 */
	public static Boolean giveMoney(Player player, Double amount) {
		User user = ess.getUser(player);
		try {
			user.giveMoney(BigDecimal.valueOf(amount));
			Log.Info(Main.plugin, "Gave " + amount + " to " + player.toString());
			return true;
		} catch (MaxMoneyException e) {
			//e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Deposit currency into a player's balance.
	 *
	 * @param player the player's UUID
	 * @param amount the amount to deposit
	 * @return true if the deposit succeeded, false if it would exceed the max balance
	 */
	public static Boolean giveMoney(UUID player, Double amount) {
		User user = ess.getUser(player);
		try {
			user.giveMoney(BigDecimal.valueOf(amount));
			Log.Info(Main.plugin, "Gave " + amount + " to " + user.getName());
			return true;
		} catch (MaxMoneyException e) {
			//e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Get the player's balance.
	 *
	 * @param player the online Player
	 * @return the current balance as a double
	 */
	public static BigDecimal getBalance(Player player) {
		User user = ess.getUser(player);
		BigDecimal bal = user.getMoney();
		return bal;
	}
	
	/**
	 * Get the player's balance.
	 *
	 * @param player the player's UUID
	 * @return the current balance as a BigDecimal
	 */
	public static BigDecimal getBalance(UUID player) {
		User user = ess.getUser(player);
		BigDecimal bal = user.getMoney();
		return bal;
	}
	
	/**
	 * Get the player's balance formatted as a currency string.
	 *
	 * @param player the player's UUID
	 * @return the formatted balance, e.g. "$1,024,173.32"
	 */
	public static String getBalanceFormatted(UUID player) {
		User user = ess.getUser(player);
		return formatMoney(user.getMoney()); // "$1,024,173.32"
	}
	
	/**
	 * Format a numeric amount as a currency string using "$#,##0.00".
	 *
	 * @param amount the amount to format
	 * @return the formatted currency string, e.g. "$1,024,173.32"
	 */
	public static String formatMoney(Double amount) {
		DecimalFormat df = new DecimalFormat("$#,##0.00");
		return df.format(BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP)); // "$1,024,173.32"
	}
	
	/**
	 * Format a BigDecimal amount as a currency string using "$#,##0.00".
	 *
	 * @param amount the amount to format
	 * @return the formatted currency string, e.g. "$1,024,173.32"
	 */
	public static String formatMoney(BigDecimal amount) {
		DecimalFormat df = new DecimalFormat("$#,##0.00");
		return df.format(amount.setScale(2, RoundingMode.HALF_UP)); // "$1,024,173.32"
	}
	
	public static Boolean pay(UUID from, UUID to, Double amount) {
		try {
			ess.getUser(from).payUser(ess.getUser(to), BigDecimal.valueOf(amount), Cause.COMMAND_PAY);
			return true;
		} catch (Exception e) {
			return false;
		}	
	}
	
}