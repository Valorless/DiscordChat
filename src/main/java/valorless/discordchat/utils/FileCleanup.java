package valorless.discordchat.utils;

import java.io.File;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import valorless.discordchat.Main;
import valorless.valorlessutils.ValorlessUtils.Log;

public class FileCleanup {

	/***
	 * Clean folder by removing files older than a specific time.
	 * *Only used if saving locally.
	 * @param path Path of the folder to clean.
	 * @param maxAge Max allowed file age, in days.
	 */
    public static void Clean(String path, int maxAge) {
        File folder = new File(path);
        
        if (folder.exists() && folder.isDirectory()) {
            // Get all files in the folder
            File[] files = folder.listFiles();
            
            if (files != null) {
                // Get the current date and time
                Instant now = Instant.now();
                
                for (File file : files) {
                    // Check if the file is a regular file and not a directory
                    if (file.isFile()) {
                        // Get the last modified time of the file
                        Instant lastModified = Instant.ofEpochMilli(file.lastModified());
                        
                        // Calculate the difference in days between now and the last modified time
                        long daysOld = ChronoUnit.DAYS.between(lastModified, now);
                        
                        // Check if the file is older than 7 days (1 week)
                        if (daysOld > maxAge) {
                            // Delete the file
                            if (file.delete()) {
                                Log.Info(Main.plugin, "Deleted file: " + file.getName());
                            } else {
                            	Log.Error(Main.plugin, "Failed to delete file: " + file.getName());
                            }
                        }
                    }
                }
            } else {
            	Log.Error(Main.plugin, "Failed to list files in the folder.");
            }
        } else {
        	Log.Error(Main.plugin, "Folder does not exist or is not a directory.");
        }
    }
}