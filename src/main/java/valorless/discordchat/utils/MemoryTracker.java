package valorless.discordchat.utils;

import java.util.LinkedList;

import org.bukkit.Bukkit;

import valorless.discordchat.Main;

public class MemoryTracker {
    private final int maxEntries;
    private final LinkedList<Long> peaks = new LinkedList<>();
    private long lastPeak = 0;

    public MemoryTracker(int maxEntries) {
        this.maxEntries = maxEntries;
        
        Bukkit.getScheduler().runTaskTimer(Main.plugin, () -> {
            update();
        }, 0L, 20L); // check every second (20 ticks)
    }
    
    public static String formatBytes(long bytes) {
        double kb = bytes / 1024.0;
        double mb = kb / 1024.0;
        return String.format("%.0f", mb);
    }

    public void update() {
        long currentUsed = getUsedMemory();

        if (currentUsed >= lastPeak) {
            lastPeak = currentUsed;
        } else {
            // GC happened or memory dropped -> record peak
            recordPeak(lastPeak);
            lastPeak = currentUsed;
        }
    }

    private void recordPeak(long peak) {
        peaks.add(peak);
        if (peaks.size() > maxEntries) {
            peaks.removeFirst();
        }
    }

    public long getAveragePeak() {
        if (peaks.isEmpty()) return 0;
        long total = 0;
        for (long val : peaks) total += val;
        return total / peaks.size();
    }

    public static long getUsedMemory() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }
}