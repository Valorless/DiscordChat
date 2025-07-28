package valorless.discordchat.utils;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;

import org.bukkit.Bukkit;

import valorless.discordchat.Main;
import valorless.discordchat.hooks.EssentialsHook;

public class ServerStats {
	
	public static String slashMem() {
		int online = (EssentialsHook.isHooked()) ? EssentialsHook.visiblePlayers().size() : Bukkit.getOnlinePlayers().size();
		String mem = String.format("Server is running %s TPS with %s players.\n", getTps(), online);
		mem += getMemory();
		return mem;
	}

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

	public static String getTps() {
		try {
			Object minecraftServer = Bukkit.getServer().getClass().getMethod("getServer").invoke(Bukkit.getServer());
			Field tpsField = minecraftServer.getClass().getField("recentTps");
			double[] tps = (double[]) tpsField.get(minecraftServer);

			return String.format("%.2f", tps[0]);
		} catch (Exception e) {
			e.printStackTrace();
			return "N/A";
		}
	}
	
	public static String getUptime() {
		long uptimeMillis = ManagementFactory.getRuntimeMXBean().getUptime();
        long uptimeSeconds = uptimeMillis / 1000;
        long uptimeMinutes = uptimeSeconds / 60;
        long uptimeHours = uptimeMinutes / 60;

        return String.format("%d hours, %d minutes, %d seconds", uptimeHours, uptimeMinutes % 60, uptimeSeconds % 60);
	}

}
