package valorless.discordchat.utils;

import java.io.File;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import valorless.discordchat.Main;
import valorless.valorlessutils.logging.Log;

/**
 * Utility for cleaning up old files from a directory.
 *
 * <p>Used to prune locally saved files that have exceeded a configured age limit.</p>
 */
public class FileCleanup {

	/**
	 * Delete all files in the specified folder that are older than the given age.
	 *
	 * <p>Only regular files are evaluated; subdirectories are left untouched.
	 * Only used if saving locally.</p>
	 *
	 * @param path   path of the folder to clean
	 * @param maxAge maximum allowed file age in days; files older than this are deleted
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
                                Log.info(Main.plugin, "Deleted file: " + file.getName());
                            } else {
                            	Log.error(Main.plugin, "Failed to delete file: " + file.getName());
                            }
                        }
                    }
                }
            } else {
            	Log.error(Main.plugin, "Failed to list files in the folder.");
            }
        } else {
        	Log.error(Main.plugin, "Failed to clean: Folder does not exist or is not a directory.");
            Log.error(Main.plugin, path);
        }
    }
}