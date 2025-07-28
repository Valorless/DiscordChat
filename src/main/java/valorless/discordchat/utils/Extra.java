package valorless.discordchat.utils;

import java.util.Random;

import org.bukkit.Location;

public class Extra {
	
	public static String ToString(Object obj) {
		if(obj instanceof Double d) {
			return String.format("%.2f", d);
		}
		return "" + obj;
	}
	
	public static String UppercaseFirstLetter(String string) {
    	string = string.replace('_', ' ');
        char[] charArray = string.toCharArray();
        boolean foundSpace = true;
        for(int i = 0; i < charArray.length; i++) {
        	charArray[i] = Character.toLowerCase(charArray[i]);
        	if(Character.isLetter(charArray[i])) {
        		if(foundSpace) {
        			charArray[i] = Character.toUpperCase(charArray[i]);
        			foundSpace = false;
        		}
        	}
        	else {
        		foundSpace = true;
        	}
        }
        string = String.valueOf(charArray);
    	return string;
    }
	
	public static Boolean CustomChance(int percent) {
		if(percent == -1) return false;
		return RandomRange(1, percent) == 1; // A 1 in 'chance' odds
    }
	
	public static Integer RandomRange(Integer min, Integer max) {
	    if (min > max) {
	        throw new IllegalArgumentException(String.format("min (%s) must be less than or equal to max (%s)", min, max));
	    }
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }
	
	/**
	 * Formats a {@link Location} object into a readable string containing the world name and coordinates.<br>
	 * Example output: <code>world (100.00, 64.00, 200.00)</code>
	 *
	 * @param loc the location to format
	 * @return a formatted string in the form <code>"world (x, y, z)"</code>, with coordinates rounded to two decimals
	 */
	public static String FormatLocation(Location loc) {
	    return String.format("%s (%.2f, %.2f, %.2f)",
	            loc.getWorld().getName(),
	            loc.getX(), loc.getY(), loc.getZ());
	}
	
	/**
	 * Formats a {@link Location} object into a string based on the given format.
	 * <p>
	 * The format string can include the following placeholders:
	 * <ul>
	 *   <li><code>%w</code> - Replaced with the world's name</li>
	 *   <li><code>%x</code> - Replaced with the X coordinate</li>
	 *   <li><code>%y</code> - Replaced with the Y coordinate</li>
	 *   <li><code>%z</code> - Replaced with the Z coordinate</li>
	 * </ul>
	 * Example usage: <code>"%w - %x %y %z"</code> → <code>world - 100.0 64.0 200.0</code>
	 *
	 * @param loc    the location to format
	 * @param format the format string containing placeholders
	 * @return a string with placeholders replaced by location values
	 */
	public static String FormatLocation(Location loc, String format) {
		return format.replace("%w", loc.getWorld().getName())
				.replace("%x", ToString(loc.getX()))
				.replace("%y", ToString(loc.getY()))
				.replace("%z", ToString(loc.getZ()));
	}
}
