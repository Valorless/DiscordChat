package valorless.discordchat.hooks;

import java.math.BigDecimal;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.earth2me.essentials.User;
import net.ess3.api.IEssentials;
import net.ess3.api.MaxMoneyException;
import valorless.discordchat.Main;
import valorless.valorlessutils.ValorlessUtils.Log;

public class Eco {
	
	private static IEssentials ess;
	public static void init() {
		ess = (IEssentials) Bukkit.getPluginManager().getPlugin("Essentials");
	}
	
	public static Boolean canAfford(Player player, Double amount) {
		User user = ess.getUser(player);
		BigDecimal bal = user.getMoney();
		BigDecimal price = BigDecimal.valueOf(amount);
		
		if(bal.compareTo(price) == -1) return false;
		else return true;
	}
	
	public static Boolean canAfford(UUID player, Double amount) {
		User user = ess.getUser(player);
		BigDecimal bal = user.getMoney();
		BigDecimal price = BigDecimal.valueOf(amount);
		
		if(bal.compareTo(price) == -1) return false;
		else return true;
	}
	
	public static void takeMoney(Player player, Double amount) {
		User user = ess.getUser(player);
		user.takeMoney(BigDecimal.valueOf(amount));
		Log.Info(Main.plugin, "Took " + amount + " from " + player.toString());
	}
	
	public static void takeMoney(UUID player, Double amount) {
		User user = ess.getUser(player);
		user.takeMoney(BigDecimal.valueOf(amount));
		Log.Info(Main.plugin, "Took " + amount + " from " + user.getName());
	}
	
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
	
	public static Double getBalance(Player player) {
		User user = ess.getUser(player);
		BigDecimal bal = user.getMoney();
		return bal.doubleValue();
	}
	
	public static BigDecimal getBalance(UUID player) {
		User user = ess.getUser(player);
		BigDecimal bal = user.getMoney();
		return bal;
	}
	
}
