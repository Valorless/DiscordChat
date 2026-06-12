package valorless.discordchat.utils;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;

import org.bukkit.Bukkit;

import valorless.discordchat.Main;
import valorless.discordchat.hooks.EssentialsHook;

/**
 * Utility methods for retrieving server runtime metrics used by chat and slash commands.
 */
public class ServerStats {
	
	/**
	 * Build a formatted stats summary with TPS, visible online player count, and memory usage.
	 *
	 * @return formatted one-message server performance summary
	 */
	public static String slashMem() {
		int online = (EssentialsHook.isHooked()) ? EssentialsHook.visiblePlayers().size() : Bukkit.getOnlinePlayers().size();
		String mem = String.format("Server is running %s TPS with %s players.\n", getTps(), online);
		mem += getMemory();
		return mem;
	}

	/**
	 * Get formatted JVM memory usage information.
	 *
	 * @return memory usage string containing used, allocated, and max memory values
	 */
	public static String getMemory() {
	    Runtime runtime = Runtime.getRuntime();
	    
	    long maxMemory = runtime.maxMemory() / (1024 * 1024); // MB
	    long allocatedMemory = runtime.totalMemory() / (1024 * 1024); // MB
	    String usedMemory = MemoryTracker.formatBytes(Main.memoryTracker.getAveragePeak());
	    
	    return String.format(
	    		"Memory Usage: %sMB/%dMB (Max: %dMB)",
	    		usedMemory, allocatedMemory, maxMemory
	    		);
	}

	/**
	 * Get current 1-minute TPS using server internals via reflection.
	 *
	 * @return formatted TPS value, or {@code "N/A"} if it cannot be resolved
	 */
	public static String getTps() {
		try {
			double[] tps = Bukkit.getServer().getTPS(); // PaperAPI

			//[12:11:40 WARN]: java.lang.NoSuchFieldException: recentTps
			//[12:11:40 WARN]: 	at java.base/java.lang.Class.getField(Class.java:2068)
			//[12:11:40 WARN]: 	at io.papermc.reflectionrewriter.runtime.AbstractDefaultRulesReflectionProxy.getField(AbstractDefaultRulesReflectionProxy.java:85)
			//[12:11:40 WARN]: 	at io.papermc.paper.pluginremap.reflect.PaperReflectionHolder.getField(Unknown Source)
			//[12:11:40 WARN]: 	at DiscordChat-2.4.1.803.jar//valorless.discordchat.utils.ServerStats.getTps(ServerStats.java:54)
			//[12:11:40 WARN]: 	at DiscordChat-2.4.1.803.jar//valorless.discordchat.utils.ServerStats.slashMem(ServerStats.java:23)

			return String.format("%.2f", tps[0]);
		} catch (Exception e) {
			try{
				Object minecraftServer = Bukkit.getServer().getClass().getMethod("getServer").invoke(Bukkit.getServer());
				Field tpsField = minecraftServer.getClass().getDeclaredField("recentTps");
				tpsField.setAccessible(true);
				double[] tps = (double[]) tpsField.get(minecraftServer);
				return String.format("%.2f", tps[0]);
			}catch(Exception ex){
				ex.printStackTrace();
				return "N/A";
			}
		}
	}
	
	/**
	 * Get server process uptime as hours, minutes, and seconds.
	 *
	 * @return formatted uptime string in "X hours, Y minutes, Z seconds" format
	 */
	public static String getUptime() {
		long uptimeMillis = ManagementFactory.getRuntimeMXBean().getUptime();
        long uptimeSeconds = uptimeMillis / 1000;
        long uptimeMinutes = uptimeSeconds / 60;
        long uptimeHours = uptimeMinutes / 60;

        return String.format("%d hours, %d minutes, %d seconds", uptimeHours, uptimeMinutes % 60, uptimeSeconds % 60);
	}

}
