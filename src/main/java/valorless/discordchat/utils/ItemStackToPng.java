package valorless.discordchat.utils;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.Multimap;

import valorless.discordchat.ChatListener;
import valorless.discordchat.Lang;
import valorless.discordchat.Main;
import valorless.valorlessutils.items.ItemUtils;
import valorless.valorlessutils.utils.Utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public class ItemStackToPng {
	
	static Font minecraftFont;

    public static String createItemStackImage(ItemStack itemStack) {
    	float fontSize = 18f;
        minecraftFont = FontLoader.loadMinecraftFont(fontSize); // Ensure the path and size are correct

        BufferedImage tempImg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gTemp = tempImg.createGraphics();
        gTemp.setFont(minecraftFont);
        
        FontMetrics metrics = gTemp.getFontMetrics();

        String itemName = Lang.RemoveColorCodesAndFormatting(ChatListener.FixName(itemStack.getType().toString()));
        itemName = itemName.replace("_", " ");
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null && meta.hasDisplayName()) {
            itemName = SmallCapsConverter.normalize(Lang.RemoveColorCodesAndFormatting(meta.getDisplayName()));
        }
        if(meta != null && ItemUtils.HasItemName(itemStack)) {
			itemName = SmallCapsConverter.normalize(Lang.RemoveColorCodesAndFormatting(ItemUtils.GetItemName(itemStack)));
		}

        int width = 10;  // Minimum width
        int height = 20; // Starting height
        int lineSpacing = 2; // Space between lines

        width = Math.max(width, metrics.stringWidth(itemName) + 20);
        height += metrics.getHeight();

        if (meta != null) {
            if (meta.hasLore()) {
                List<String> lore = meta.getLore();
                for (String line : lore) {
                    width = Math.max(width, metrics.stringWidth(SmallCapsConverter.normalize(Lang.RemoveColorCodesAndFormatting(line))) + 20);
                    height += metrics.getHeight() + lineSpacing;
                }
            }

            // Handle enchantments
            Map<Enchantment, Integer> enchants = meta.getEnchants();
            if (!enchants.isEmpty() && !meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS)) {
                for (Map.Entry<Enchantment, Integer> enchant : enchants.entrySet()) {
                    String enchantText = enchant.getKey().getKey().getKey() + " " + EnchantValue(enchant.getValue());
                    width = Math.max(width, metrics.stringWidth(Lang.RemoveColorCodesAndFormatting(enchantText)) + 20);
                    height += metrics.getHeight() + lineSpacing;
                }
            }

            // Handle attributes
            if(meta.hasAttributeModifiers()) {
            Multimap<Attribute, AttributeModifier> attributes = meta.getAttributeModifiers();
            if (!attributes.isEmpty() && !meta.hasItemFlag(ItemFlag.HIDE_ATTRIBUTES)) {
                for (Entry<Attribute, AttributeModifier> attribute : attributes.entries()) {
                	String attributeText = "";
                	if(attribute.getValue().getOperation() == Operation.ADD_NUMBER) {
                		attributeText = String.format("%.1f %s", attribute.getValue().getAmount(), 
                				Extra.UppercaseFirstLetter(attribute.getKey().name().replace("GENERIC_", "")));
                		//attributeText = String.format("%s.1f %s", attribute.getValue().getAmount(), attribute.getKey().name());
                	}
                	if(attribute.getValue().getOperation() == Operation.ADD_SCALAR) {
                		attributeText = String.format("%s %s", String.format("%.1f",(attribute.getValue().getAmount()*100)) + "%", 
                				Extra.UppercaseFirstLetter(attribute.getKey().name().replace("GENERIC_", "")));
                		//attributeText = String.format("%s.1f %s", (attribute.getValue().getAmount()*100) + "%", attribute.getKey().name());
                	}
                    width = Math.max(width, metrics.stringWidth(Lang.RemoveColorCodesAndFormatting(attributeText)) + 20);
                    height += metrics.getHeight() + lineSpacing;
                }
            }
            }
        }

        gTemp.dispose();

        if(height > width) width = height;
        //else
        //if(width > height) height = width;
        
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setFont(minecraftFont);
        g.setColor(Color.WHITE);

        itemName = ChatListener.FixName(itemStack.getType().toString());
        itemName = itemName.replace("_", " ");
        if (meta != null && meta.hasDisplayName()) {
            itemName = meta.getDisplayName();
        }
        if(meta != null && ItemUtils.HasItemName(itemStack)) {
			itemName = SmallCapsConverter.normalize(Lang.RemoveColorCodesAndFormatting(ItemUtils.GetItemName(itemStack)));
		}
        
        int yPos = 20;
        minecraftFont.deriveFont(fontSize + 4f);
        drawStringWithColors(g, SmallCapsConverter.normalize(RemoveFormatting(itemName)), 10, yPos);
        yPos += metrics.getHeight() + lineSpacing;
        minecraftFont.deriveFont(fontSize);

        if (meta != null) {

            // Draw enchantments
        	Map<Enchantment, Integer> enchants = meta.getEnchants();
        	if (!enchants.isEmpty() && !meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS)) {
        		for (Map.Entry<Enchantment, Integer> enchant : enchants.entrySet()) {
        			String enchantText = FixEnchantName(enchant.getKey().getKey().getKey().toUpperCase()) + " " + EnchantValue(enchant.getValue());
        			drawStringWithColors(g ,RemoveFormatting(enchantText), 10, yPos);
        			yPos += metrics.getHeight() + lineSpacing;
        		}
        	}

            if (meta.hasLore()) {
                List<String> lore = meta.getLore();
                for (String line : lore) {
                    drawStringWithColors(g, SmallCapsConverter.normalize(RemoveFormatting(line)), 10, yPos);
                    yPos += metrics.getHeight() + lineSpacing;
                }
            }

            // Handle attributes
            if(meta.hasAttributeModifiers()) {
            Multimap<Attribute, AttributeModifier> attributes = meta.getAttributeModifiers();
            if (!attributes.isEmpty() && !meta.hasItemFlag(ItemFlag.HIDE_ATTRIBUTES)) {
                for (Entry<Attribute, AttributeModifier> attribute : attributes.entries()) {
                	String attributeText = "";
                	if(attribute.getValue().getOperation() == Operation.ADD_NUMBER) {
                		attributeText = String.format("%.1f %s", attribute.getValue().getAmount(), 
                				Extra.UppercaseFirstLetter(attribute.getKey().name().replace("GENERIC_", "")));
                	}
                	if(attribute.getValue().getOperation() == Operation.ADD_SCALAR) {
                		attributeText = String.format("%s %s", String.format("%.1f",(attribute.getValue().getAmount()*100)) + "%", 
                				Extra.UppercaseFirstLetter(attribute.getKey().name().replace("GENERIC_", "")));
                	}

                    drawStringWithColors(g, "§7" + RemoveFormatting(attributeText), 10, yPos);
                    yPos += metrics.getHeight() + lineSpacing;
                }
            }
            }
        }
        

        g.dispose();
        
        UUID id = UUID.randomUUID();
        
        try {
        	if(Main.config.GetBool("save-locally")) {
        		File path = Bukkit.getPluginManager().getPlugin("WebServer").getDataFolder();
        		ImageIO.write(image, "PNG", new File(path, Main.config.GetString("save-location") + id.toString() + ".png"));
        		FileCleanup.Clean(path.getPath() +  Main.config.GetString("save-location"), Main.config.GetInt("cleanup-age"));
        	}else {
        		ImageSender.sendImage(image, id.toString());
        	}
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return id.toString();
    }
    
    static String EnchantValue(int value) {
        if (value < 1 || value > 3999) return "";

        StringBuilder result = new StringBuilder();
        int[] values =    {1000, 900, 500, 400, 100, 90,  50, 40,  10, 9,   5,  4,  1};
        String[] numerals = {"M",  "CM","D", "CD","C", "XC","L","XL","X","IX","V","IV","I"};

        for (int i = 0; i < values.length; i++) {
            while (value >= values[i]) {
                value -= values[i];
                result.append(numerals[i]);
            }
        }

        return result.toString();
    }
    
    public static String RemoveFormatting(String text) {
		if(!Utils.IsStringNullOrEmpty(text)) {
			text = Lang.Parse(text);
			text = text.replace("§o", "");
			text = text.replace("§l", "");
			text = text.replace("§k", "");
			text = text.replace("§m", "");
			text = text.replace("§n", "");
			text = text.replace("§r", "");
			text = text.replace("z*", "*");
			text = text.replace("§§", "§");
			text = RemoveHex(text);
		}
		return text;
	}
    
    public static String FixEnchantName(String text) {
		if(!Utils.IsStringNullOrEmpty(text)) {
			text = text.replace("AQUA_AFFINITY", "§bAqua Affinity§7");
			text = text.replace("BANE_OF_ARTHROPODS", "§5Bane of Arthropods§7");
			text = text.replace("BINDING_CURSE", "§cCurse of Binding§7");
			text = text.replace("BLAST_PROTECTION", "§bBlast Protection§7");
			text = text.replace("BREACH", "&5Breach");
			text = text.replace("CHANNELING", "§dChanneling§7");
			text = text.replace("DENSITY", "&5Density");
			text = text.replace("DEPTH_STRIDER", "§bDeath Strider§7");
			text = text.replace("EFFICIENCY", "§3Efficiency§7");
			text = text.replace("FEATHER_FALLING", "§bFeather Falling§7");
			text = text.replace("FIRE_ASPECT", "§5Fire Aspect§7");
			text = text.replace("FIRE_PROTECTION", "§bFire Protection§7");
			text = text.replace("FLAME", "§6Flame§7");
			text = text.replace("FORTUNE", "§3Fortune§7");
			text = text.replace("FROST_WALKER", "§bFrost Walker§7");
			text = text.replace("IMPALING", "§dImpaling§7");
			text = text.replace("INFINITY", "§6Infinity§7");
			text = text.replace("KNOCKBACK", "§5Knockback§7");
			text = text.replace("LOOTING", "§5Looting§7");
			text = text.replace("LOYALTY", "§dLoyalty§7");
			text = text.replace("LUCK_OF_THE_SEA", "§9Luck of the Sea§7");
			text = text.replace("LURE", "§9Lure§7");
			text = text.replace("MENDING", "§aMending§7");
			text = text.replace("MULTISHOT", "§6Multishot§7");
			text = text.replace("PIERCING", "§6Piercing§7");
			text = text.replace("POWER", "§6Power§7");
			text = text.replace("PROJECTILE_PROTECTION", "§bProjectile Protection§7");
			text = text.replace("PROTECTION", "§bProtection§7");
			text = text.replace("PUNCH", "§6Punch§7");
			text = text.replace("QUICK_CHARGE", "§6Quick Charge§7");
			text = text.replace("RESPIRATION", "§bRespiration§7");
			text = text.replace("RIPTIDE", "§dRiptide§7");
			text = text.replace("SHARPNESS", "§5Sharpness§7");
			text = text.replace("SILK_TOUCH", "§3Silk Touch§7");
			text = text.replace("SMITE", "§5Smite§7");
			text = text.replace("SOUL_SPEED", "§bSoul Speed§7");
			text = text.replace("SWEEPING_EDGE", "§5Sweeping Edge§7");
			text = text.replace("SWEEPING", "§5Sweeping Edge§7");
			text = text.replace("SWIFT_SNEAK", "§bSwift Sneak§7");
			text = text.replace("THORNS", "§bThorns§7");
			text = text.replace("UNBREAKING", "§aUnbreaking§7");
			text = text.replace("VANISHING_CURSE", "§cCurse of Vanishing§7");
			text = text.replace("WIND_BURST", "&5Wind Burst");
		}
		return text;
	}
    
    public static void drawStringWithColors(Graphics2D graphics, String text, int x, int y) {
        int currentX = x;
        Color currentColor = Color.WHITE; // Default color
        graphics.setFont(minecraftFont); // Assuming you've loaded Minecraft's font

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '§' && i + 1 < text.length()) {
                char colorCode = text.charAt(i + 1);
                currentColor = MinecraftColors.getColor(colorCode);
                i++; // Skip the next character as it is the color code
            } else {
                graphics.setColor(currentColor);
                String charStr = String.valueOf(c);
                graphics.drawString(charStr, currentX, y);
                currentX += graphics.getFontMetrics().stringWidth(charStr);
            }
        }
    }
    
    public static String RemoveHex(String text) {
        // Regex to match the pattern of hex color codes
    	//Log.Info(Main.plugin, text);
        String hexColorRegex = "§x(§[0-9A-Fa-f]){6}";
        return text.replaceAll(hexColorRegex, "");
    }
    
    public static class FontLoader {

        public static Font loadMinecraftFont(float size) {
            try {
                Font font = Font.createFont(Font.TRUETYPE_FONT, new File(Main.plugin.getDataFolder(), "MinecraftFont.ttf")).deriveFont(size);
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(font);
                return font;
            } catch (FontFormatException | IOException e) {
                e.printStackTrace();
                return new Font("Arial", Font.PLAIN, 12); // Default font if Minecraft font fails to load
            }
        }
    }
    
    public static class MinecraftColors {
        private static final Map<Character, Color> colorMap = new HashMap<>();

        static {
            colorMap.put('0', new Color(0, 0, 0));       // Black
            colorMap.put('1', new Color(0, 0, 170));     // Dark Blue
            colorMap.put('2', new Color(0, 170, 0));     // Dark Green
            colorMap.put('3', new Color(0, 170, 170));   // Dark Aqua
            colorMap.put('4', new Color(170, 0, 0));     // Dark Red
            colorMap.put('5', new Color(170, 0, 170));   // Dark Purple
            colorMap.put('6', new Color(255, 170, 0));   // Gold
            colorMap.put('7', new Color(170, 170, 170)); // Gray
            colorMap.put('8', new Color(85, 85, 85));    // Dark Gray
            colorMap.put('9', new Color(85, 85, 255));   // Blue
            colorMap.put('a', new Color(85, 255, 85));   // Green
            colorMap.put('b', new Color(85, 255, 255));  // Aqua
            colorMap.put('c', new Color(255, 85, 85));   // Red
            colorMap.put('d', new Color(255, 85, 255));  // Light Purple
            colorMap.put('e', new Color(255, 255, 85));  // Yellow
            colorMap.put('f', new Color(255, 255, 255)); // White
            // Add more if formatting codes needed
        }

        public static Color getColor(char code) {
            return colorMap.getOrDefault(code, Color.WHITE);
        }
    }
}