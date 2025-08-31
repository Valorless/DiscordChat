package valorless.discordchat.utils;
import java.util.HashMap;
import java.util.Map;

public class SmallCapsConverter {

    private static final Map<Character, Character> smallCapsMap = new HashMap<>();

    static {
        smallCapsMap.put('ᴀ', 'A');
        smallCapsMap.put('ʙ', 'B');
        smallCapsMap.put('ᴄ', 'C');
        smallCapsMap.put('ᴅ', 'D');
        smallCapsMap.put('ᴇ', 'E');
        smallCapsMap.put('ꜰ', 'F');
        smallCapsMap.put('ɢ', 'G');
        smallCapsMap.put('ʜ', 'H');
        smallCapsMap.put('ɪ', 'I');
        smallCapsMap.put('ᴊ', 'J');
        smallCapsMap.put('ᴋ', 'K');
        smallCapsMap.put('ʟ', 'L');
        smallCapsMap.put('ᴍ', 'M');
        smallCapsMap.put('ɴ', 'N');
        smallCapsMap.put('ᴏ', 'O');
        smallCapsMap.put('ᴘ', 'P');
        smallCapsMap.put('ǫ', 'Q');
        smallCapsMap.put('ʀ', 'R');
        smallCapsMap.put('ꜱ', 'S');
        smallCapsMap.put('ᴛ', 'T');
        smallCapsMap.put('ᴜ', 'U');
        smallCapsMap.put('ᴠ', 'V');
        smallCapsMap.put('ᴡ', 'W');
        //smallCapsMap.put('x', 'X'); // already normal
        smallCapsMap.put('ʏ', 'Y');
        smallCapsMap.put('ᴢ', 'Z');
    }

    public static String normalize(String input) {
        StringBuilder sb = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (smallCapsMap.containsKey(c)) {
                sb.append(smallCapsMap.get(c));
            } else {
                sb.append(c); // leave as is
            }
        }
        return sb.toString();
    }
}
