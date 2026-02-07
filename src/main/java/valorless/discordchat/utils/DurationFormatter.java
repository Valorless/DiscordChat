package valorless.discordchat.utils;
/**
 * Utility to format the duration between two millisecond Unix timestamps.
 */
public final class DurationFormatter {

    private DurationFormatter() {}

    /**
     * Returns a human-readable duration between two millisecond timestamps.
     * Examples of returned values:
     * - "360 days" (when exactly 360 days)
     * - "360 days minus 1 millisecond" (when it's one millisecond short of 360 days)
     * - "359 days, 23 hours, 59 minutes, 59 seconds, 999 milliseconds"
     *
     * @param t1Millis first timestamp in milliseconds
     * @param t2Millis second timestamp in milliseconds
     * @return formatted duration string
     */
    public static String formatDurationBetween(long t1Millis, long t2Millis) {
        long diff = Math.abs((t2Millis - t1Millis) + 1); // Add 1 ms to handle "minus 1 millisecond" case

        final long MS_PER_SECOND = 1_000L;
        final long MS_PER_MINUTE = 60 * MS_PER_SECOND;
        final long MS_PER_HOUR   = 60 * MS_PER_MINUTE;
        final long MS_PER_DAY    = 24 * MS_PER_HOUR;

        long days = diff / MS_PER_DAY;
        long rem = diff % MS_PER_DAY;

        if (rem == 0) {
            return days + (days == 1 ? " day" : " days");
        }

        long hours = rem / MS_PER_HOUR;
        rem %= MS_PER_HOUR;
        long minutes = rem / MS_PER_MINUTE;
        rem %= MS_PER_MINUTE;
        long seconds = rem / MS_PER_SECOND;
        long millis = rem % MS_PER_SECOND;

        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append(days == 1 ? " day" : " days");
        if (hours > 0) appendWithComma(sb).append(hours).append(hours == 1 ? " hour" : " hours");
        if (minutes > 0) appendWithComma(sb).append(minutes).append(minutes == 1 ? " minute" : " minutes");
        if (seconds > 0) appendWithComma(sb).append(seconds).append(seconds == 1 ? " second" : " seconds");
        //if (millis > 0) appendWithComma(sb).append(millis).append(millis == 1 ? " millisecond" : " milliseconds");

        return sb.length() == 0 ? "0 milliseconds" : sb.toString();
    }

    private static StringBuilder appendWithComma(StringBuilder sb) {
        if (sb.length() > 0) sb.append(", ");
        return sb;
    }
}