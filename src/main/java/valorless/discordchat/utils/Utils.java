package valorless.discordchat.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.map.MapPalette;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class Utils {
    static Map<Material, Color> blocksMap = new HashMap<>();

    public static void loadColors() {
        // Materials we don't want to use minecraft images for (could be because the
        // image provides a poor color)
        blocksMap.put(Material.GRASS, new Color(49, 101, 25));
        blocksMap.put(Material.ROSE_BUSH, new Color(49, 101, 25));
        blocksMap.put(Material.BIG_DRIPLEAF, new Color(49, 101, 25));
        blocksMap.put(Material.BIG_DRIPLEAF_STEM, new Color(49, 101, 25));
        blocksMap.put(Material.SMALL_DRIPLEAF, new Color(49, 101, 25));
        blocksMap.put(Material.TALL_GRASS, new Color(49, 101, 25));
        blocksMap.put(Material.LARGE_FERN, new Color(49, 101, 25));
        blocksMap.put(Material.FERN, new Color(49, 101, 25));
        blocksMap.put(Material.COBBLESTONE, new Color(130, 130, 130));
        blocksMap.put(Material.INFESTED_COBBLESTONE, new Color(130, 130, 130));
        blocksMap.put(Material.MOSSY_COBBLESTONE, new Color(130, 130, 130));
        blocksMap.put(Material.MOSSY_COBBLESTONE_SLAB, new Color(130, 130, 130));
        blocksMap.put(Material.MOSSY_COBBLESTONE_STAIRS, new Color(130, 130, 130));
        blocksMap.put(Material.MOSSY_COBBLESTONE_WALL, new Color(130, 130, 130));
        blocksMap.put(Material.COBBLESTONE_STAIRS, new Color(130, 130, 130));
        blocksMap.put(Material.COBBLESTONE_SLAB, new Color(130, 130, 130));
        blocksMap.put(Material.FURNACE, new Color(130, 130, 130));
        blocksMap.put(Material.STONE, new Color(117, 117, 117));
        blocksMap.put(Material.INFESTED_CHISELED_STONE_BRICKS, new Color(117, 117, 117));
        blocksMap.put(Material.CHISELED_STONE_BRICKS, new Color(117, 117, 117));
        blocksMap.put(Material.INFESTED_STONE, new Color(117, 117, 117));
        blocksMap.put(Material.INFESTED_CRACKED_STONE_BRICKS, new Color(117, 117, 117));
        blocksMap.put(Material.INFESTED_STONE_BRICKS, new Color(117, 117, 117));
        blocksMap.put(Material.INFESTED_MOSSY_STONE_BRICKS, new Color(117, 117, 117));
        blocksMap.put(Material.MOSSY_STONE_BRICKS, new Color(117, 117, 117));
        blocksMap.put(Material.MOSSY_STONE_BRICK_SLAB, new Color(117, 117, 117));
        blocksMap.put(Material.MOSSY_STONE_BRICK_STAIRS, new Color(117, 117, 117));
        blocksMap.put(Material.MOSSY_STONE_BRICK_WALL, new Color(117, 117, 117));
        blocksMap.put(Material.STONE_SLAB, new Color(117, 117, 117));
        blocksMap.put(Material.IRON_ORE, new Color(117, 117, 117));
        blocksMap.put(Material.GOLD_ORE, new Color(117, 117, 117));
        blocksMap.put(Material.REDSTONE_ORE, new Color(117, 117, 117));
        blocksMap.put(Material.DIAMOND_ORE, new Color(117, 117, 117));
        blocksMap.put(Material.COAL_ORE, new Color(117, 117, 117));
        blocksMap.put(Material.EMERALD_ORE, new Color(117, 117, 117));
        blocksMap.put(Material.LAPIS_ORE, new Color(117, 117, 117));
        blocksMap.put(Material.IRON_BLOCK, new Color(236, 236, 236));
        blocksMap.put(Material.IRON_DOOR, new Color(236, 236, 236));
        blocksMap.put(Material.IRON_TRAPDOOR, new Color(236, 236, 236));
        blocksMap.put(Material.IRON_BARS, new Color(236, 236, 236));
        blocksMap.put(Material.RAW_IRON_BLOCK, new Color(236, 236, 236));
        blocksMap.put(Material.GOLD_BLOCK, new Color(243, 223, 75));
        blocksMap.put(Material.RAW_GOLD_BLOCK, new Color(243, 223, 75));
        blocksMap.put(Material.REDSTONE_BLOCK, new Color(196, 25, 16));
        blocksMap.put(Material.DIAMOND_BLOCK, new Color(95, 233, 217));
        blocksMap.put(Material.COAL_BLOCK, new Color(19, 19, 19));
        blocksMap.put(Material.EMERALD_BLOCK, new Color(71, 213, 105));
        blocksMap.put(Material.LAPIS_BLOCK, new Color(42, 80, 139));
        blocksMap.put(Material.SEAGRASS, new Color(67, 101, 165));
        blocksMap.put(Material.BUBBLE_COLUMN, new Color(67, 101, 165));
        blocksMap.put(Material.TALL_SEAGRASS, new Color(67, 101, 165));
        blocksMap.put(Material.KELP, new Color(67, 101, 165));
        blocksMap.put(Material.GRASS_BLOCK, new Color(82, 129, 69));
        blocksMap.put(Material.DIRT, new Color(168, 120, 83));
        blocksMap.put(Material.SAND, new Color(222, 215, 172));
        blocksMap.put(Material.SANDSTONE, new Color(213, 207, 162));
        blocksMap.put(Material.CHISELED_SANDSTONE, new Color(213, 207, 162));
        blocksMap.put(Material.ACACIA_LEAVES, new Color(44, 97, 22));
        blocksMap.put(Material.BIRCH_LEAVES, new Color(114, 149, 76));
        blocksMap.put(Material.DARK_OAK_LEAVES, new Color(46, 111, 17));
        blocksMap.put(Material.JUNGLE_LEAVES, new Color(60, 141, 24));
        blocksMap.put(Material.OAK_LEAVES, new Color(49, 111, 21));
        blocksMap.put(Material.SPRUCE_LEAVES, new Color(55, 91, 56));
        blocksMap.put(Material.DIRT_PATH, new Color(170, 148, 89));
        blocksMap.put(Material.COARSE_DIRT, new Color(104, 75, 51));
        blocksMap.put(Material.ANDESITE, new Color(136, 136, 138));
        blocksMap.put(Material.POLISHED_ANDESITE, new Color(136, 136, 138));
        blocksMap.put(Material.POLISHED_ANDESITE_STAIRS, new Color(136, 136, 138));
        blocksMap.put(Material.POLISHED_ANDESITE_SLAB, new Color(136, 136, 138));
        blocksMap.put(Material.DIORITE, new Color(181, 181, 181));
        blocksMap.put(Material.POLISHED_DIORITE, new Color(181, 181, 181));
        blocksMap.put(Material.POLISHED_DIORITE_STAIRS, new Color(181, 181, 181));
        blocksMap.put(Material.POLISHED_DIORITE_SLAB, new Color(181, 181, 181));
        blocksMap.put(Material.DEAD_BUSH, new Color(144, 97, 39));
        blocksMap.put(Material.CACTUS, new Color(76, 107, 35));
        blocksMap.put(Material.DANDELION, new Color(247, 229, 77));
        blocksMap.put(Material.POPPY, new Color(230, 47, 43));
        blocksMap.put(Material.CORNFLOWER, new Color(70, 106, 235));
        blocksMap.put(Material.AZURE_BLUET, new Color(210, 215, 223));
        blocksMap.put(Material.OXEYE_DAISY, new Color(187, 188, 189));
        blocksMap.put(Material.LAVA, new Color(211, 124, 40));
        blocksMap.put(Material.GRANITE, new Color(156, 111, 91));
        blocksMap.put(Material.POLISHED_GRANITE, new Color(156, 111, 91));
        blocksMap.put(Material.POLISHED_GRANITE_STAIRS, new Color(156, 111, 91));
        blocksMap.put(Material.POLISHED_GRANITE_SLAB, new Color(156, 111, 91));
        blocksMap.put(Material.REDSTONE_LAMP, new Color(123, 73, 33));
        blocksMap.put(Material.GRAVEL, new Color(139, 135, 134));
        blocksMap.put(Material.SPRUCE_LOG, new Color(48, 34, 25));
        blocksMap.put(Material.OAK_LOG, new Color(58, 35, 9));
        blocksMap.put(Material.STRIPPED_OAK_LOG, new Color(58, 35, 9));
        blocksMap.put(Material.STRIPPED_OAK_WOOD, new Color(58, 35, 9));
        blocksMap.put(Material.BIRCH_LOG, new Color(196, 195, 193));
        blocksMap.put(Material.STRIPPED_BIRCH_LOG, new Color(196, 195, 193));
        blocksMap.put(Material.STRIPPED_BIRCH_WOOD, new Color(196, 195, 193));
        blocksMap.put(Material.JUNGLE_LOG, new Color(89, 76, 37));
        blocksMap.put(Material.STRIPPED_JUNGLE_LOG, new Color(89, 76, 37));
        blocksMap.put(Material.STRIPPED_JUNGLE_WOOD, new Color(89, 76, 37));
        blocksMap.put(Material.ACACIA_LOG, new Color(95, 95, 85));
        blocksMap.put(Material.STRIPPED_ACACIA_LOG, new Color(95, 95, 85));
        blocksMap.put(Material.STRIPPED_ACACIA_WOOD, new Color(95, 95, 85));
        blocksMap.put(Material.DARK_OAK_LOG, new Color(35, 27, 16));
        blocksMap.put(Material.STRIPPED_DARK_OAK_LOG, new Color(35, 27, 16));
        blocksMap.put(Material.STRIPPED_DARK_OAK_WOOD, new Color(35, 27, 16));
        blocksMap.put(Material.SPRUCE_PLANKS, new Color(100, 78, 47));
        blocksMap.put(Material.OAK_PLANKS, new Color(172, 140, 88));
        blocksMap.put(Material.OAK_TRAPDOOR, new Color(172, 140, 88));
        blocksMap.put(Material.BIRCH_PLANKS, new Color(202, 185, 131));
        blocksMap.put(Material.JUNGLE_PLANKS, new Color(172, 124, 89));
        blocksMap.put(Material.ACACIA_PLANKS, new Color(178, 102, 60));
        blocksMap.put(Material.DARK_OAK_PLANKS, new Color(62, 41, 18));
        blocksMap.put(Material.SPRUCE_FENCE, new Color(100, 78, 47));
        blocksMap.put(Material.OAK_FENCE, new Color(172, 140, 88));
        blocksMap.put(Material.BIRCH_FENCE, new Color(202, 185, 131));
        blocksMap.put(Material.JUNGLE_FENCE, new Color(172, 124, 89));
        blocksMap.put(Material.ACACIA_FENCE, new Color(178, 102, 60));
        blocksMap.put(Material.DARK_OAK_FENCE, new Color(62, 41, 18));
        blocksMap.put(Material.SPRUCE_STAIRS, new Color(100, 78, 47));
        blocksMap.put(Material.OAK_STAIRS, new Color(172, 140, 88));
        blocksMap.put(Material.BIRCH_STAIRS, new Color(202, 185, 131));
        blocksMap.put(Material.JUNGLE_STAIRS, new Color(172, 124, 89));
        blocksMap.put(Material.ACACIA_STAIRS, new Color(178, 102, 60));
        blocksMap.put(Material.DARK_OAK_STAIRS, new Color(62, 41, 18));
        blocksMap.put(Material.SPRUCE_SLAB, new Color(100, 78, 47));
        blocksMap.put(Material.OAK_SLAB, new Color(172, 140, 88));
        blocksMap.put(Material.BIRCH_SLAB, new Color(202, 185, 131));
        blocksMap.put(Material.JUNGLE_SLAB, new Color(172, 124, 89));
        blocksMap.put(Material.ACACIA_SLAB, new Color(178, 102, 60));
        blocksMap.put(Material.DARK_OAK_SLAB, new Color(62, 41, 18));
        blocksMap.put(Material.SPRUCE_BUTTON, new Color(100, 78, 47));
        blocksMap.put(Material.OAK_BUTTON, new Color(172, 140, 88));
        blocksMap.put(Material.BIRCH_BUTTON, new Color(202, 185, 131));
        blocksMap.put(Material.JUNGLE_BUTTON, new Color(172, 124, 89));
        blocksMap.put(Material.ACACIA_BUTTON, new Color(178, 102, 60));
        blocksMap.put(Material.DARK_OAK_BUTTON, new Color(62, 41, 18));
        blocksMap.put(Material.SPRUCE_DOOR, new Color(100, 78, 47));
        blocksMap.put(Material.OAK_DOOR, new Color(172, 140, 88));
        blocksMap.put(Material.BIRCH_DOOR, new Color(202, 185, 131));
        blocksMap.put(Material.JUNGLE_DOOR, new Color(172, 124, 89));
        blocksMap.put(Material.ACACIA_DOOR, new Color(178, 102, 60));
        blocksMap.put(Material.DARK_OAK_DOOR, new Color(62, 41, 18));
        blocksMap.put(Material.SPRUCE_FENCE_GATE, new Color(100, 78, 47));
        blocksMap.put(Material.OAK_FENCE_GATE, new Color(172, 140, 88));
        blocksMap.put(Material.BIRCH_FENCE_GATE, new Color(202, 185, 131));
        blocksMap.put(Material.JUNGLE_FENCE_GATE, new Color(172, 124, 89));
        blocksMap.put(Material.ACACIA_FENCE_GATE, new Color(178, 102, 60));
        blocksMap.put(Material.DARK_OAK_FENCE_GATE, new Color(62, 41, 18));
        blocksMap.put(Material.SPRUCE_SIGN, new Color(100, 78, 47));
        blocksMap.put(Material.SPRUCE_WALL_SIGN, new Color(100, 78, 47));
        blocksMap.put(Material.OAK_SIGN, new Color(172, 140, 88));
        blocksMap.put(Material.OAK_WALL_SIGN, new Color(172, 140, 88));
        blocksMap.put(Material.BIRCH_SIGN, new Color(202, 185, 131));
        blocksMap.put(Material.BIRCH_WALL_SIGN, new Color(202, 185, 131));
        blocksMap.put(Material.JUNGLE_SIGN, new Color(172, 124, 89));
        blocksMap.put(Material.JUNGLE_WALL_SIGN, new Color(172, 124, 89));
        blocksMap.put(Material.ACACIA_SIGN, new Color(178, 102, 60));
        blocksMap.put(Material.ACACIA_WALL_SIGN, new Color(178, 102, 60));
        blocksMap.put(Material.DARK_OAK_SIGN, new Color(62, 41, 18));
        blocksMap.put(Material.DARK_OAK_WALL_SIGN, new Color(62, 41, 18));
        blocksMap.put(Material.CRAFTING_TABLE, new Color(172, 140, 88));
        blocksMap.put(Material.BOOKSHELF, new Color(172, 140, 88));
        blocksMap.put(Material.SUGAR_CANE, new Color(71, 139, 42));
        blocksMap.put(Material.BEDROCK, new Color(47, 47, 47));
        blocksMap.put(Material.TORCH, new Color(206, 173, 26));
        blocksMap.put(Material.WALL_TORCH, new Color(206, 173, 26));
        blocksMap.put(Material.PUMPKIN, new Color(222, 141, 28));
        blocksMap.put(Material.CARVED_PUMPKIN, new Color(222, 141, 28));
        blocksMap.put(Material.JACK_O_LANTERN, new Color(222, 141, 28));
        blocksMap.put(Material.TNT, new Color(203, 49, 26));
        blocksMap.put(Material.BLACK_WOOL, new Color(6, 7, 12));
        blocksMap.put(Material.WHITE_WOOL, new Color(225, 226, 228));
        blocksMap.put(Material.BLUE_WOOL, new Color(45, 50, 145));
        blocksMap.put(Material.BROWN_WOOL, new Color(105, 70, 39));
        blocksMap.put(Material.CYAN_WOOL, new Color(21, 139, 145));
        blocksMap.put(Material.GRAY_WOOL, new Color(64, 67, 72));
        blocksMap.put(Material.GREEN_WOOL, new Color(83, 108, 20));
        blocksMap.put(Material.LIGHT_BLUE_WOOL, new Color(121, 148, 202));
        blocksMap.put(Material.LIGHT_GRAY_WOOL, new Color(164, 168, 169));
        blocksMap.put(Material.LIME_WOOL, new Color(122, 198, 38));
        blocksMap.put(Material.MAGENTA_WOOL, new Color(188, 66, 179));
        blocksMap.put(Material.ORANGE_WOOL, new Color(240, 125, 30));
        blocksMap.put(Material.PINK_WOOL, new Color(242, 148, 177));
        blocksMap.put(Material.PURPLE_WOOL, new Color(129, 65, 182));
        blocksMap.put(Material.RED_WOOL, new Color(155, 53, 49));
        blocksMap.put(Material.YELLOW_WOOL, new Color(195, 182, 47));
        blocksMap.put(Material.BLACK_BANNER, new Color(6, 7, 12));
        blocksMap.put(Material.WHITE_BANNER, new Color(225, 226, 228));
        blocksMap.put(Material.BLUE_BANNER, new Color(45, 50, 145));
        blocksMap.put(Material.BROWN_BANNER, new Color(105, 70, 39));
        blocksMap.put(Material.CYAN_BANNER, new Color(21, 139, 145));
        blocksMap.put(Material.GRAY_BANNER, new Color(64, 67, 72));
        blocksMap.put(Material.GREEN_BANNER, new Color(83, 108, 20));
        blocksMap.put(Material.LIGHT_BLUE_BANNER, new Color(121, 148, 202));
        blocksMap.put(Material.LIGHT_GRAY_BANNER, new Color(164, 168, 169));
        blocksMap.put(Material.LIME_BANNER, new Color(122, 198, 38));
        blocksMap.put(Material.MAGENTA_BANNER, new Color(188, 66, 179));
        blocksMap.put(Material.ORANGE_BANNER, new Color(240, 125, 30));
        blocksMap.put(Material.PINK_BANNER, new Color(242, 148, 177));
        blocksMap.put(Material.PURPLE_BANNER, new Color(129, 65, 182));
        blocksMap.put(Material.RED_BANNER, new Color(155, 53, 49));
        blocksMap.put(Material.YELLOW_BANNER, new Color(195, 182, 47));
        blocksMap.put(Material.BLACK_WALL_BANNER, new Color(6, 7, 12));
        blocksMap.put(Material.WHITE_WALL_BANNER, new Color(225, 226, 228));
        blocksMap.put(Material.BLUE_WALL_BANNER, new Color(45, 50, 145));
        blocksMap.put(Material.BROWN_WALL_BANNER, new Color(105, 70, 39));
        blocksMap.put(Material.CYAN_WALL_BANNER, new Color(21, 139, 145));
        blocksMap.put(Material.GRAY_WALL_BANNER, new Color(64, 67, 72));
        blocksMap.put(Material.GREEN_WALL_BANNER, new Color(83, 108, 20));
        blocksMap.put(Material.LIGHT_BLUE_WALL_BANNER, new Color(121, 148, 202));
        blocksMap.put(Material.LIGHT_GRAY_WALL_BANNER, new Color(164, 168, 169));
        blocksMap.put(Material.LIME_WALL_BANNER, new Color(122, 198, 38));
        blocksMap.put(Material.MAGENTA_WALL_BANNER, new Color(188, 66, 179));
        blocksMap.put(Material.ORANGE_WALL_BANNER, new Color(240, 125, 30));
        blocksMap.put(Material.PINK_WALL_BANNER, new Color(242, 148, 177));
        blocksMap.put(Material.PURPLE_WALL_BANNER, new Color(129, 65, 182));
        blocksMap.put(Material.RED_WALL_BANNER, new Color(155, 53, 49));
        blocksMap.put(Material.YELLOW_WALL_BANNER, new Color(195, 182, 47));
        blocksMap.put(Material.BLACK_CONCRETE, new Color(7, 9, 14));
        blocksMap.put(Material.WHITE_CONCRETE, new Color(199, 202, 207));
        blocksMap.put(Material.BLUE_CONCRETE, new Color(42, 44, 133));
        blocksMap.put(Material.BROWN_CONCRETE, new Color(91, 57, 30));
        blocksMap.put(Material.CYAN_CONCRETE, new Color(20, 113, 129));
        blocksMap.put(Material.GRAY_CONCRETE, new Color(118, 119, 110));
        blocksMap.put(Material.GREEN_CONCRETE, new Color(88, 156, 25));
        blocksMap.put(Material.LIGHT_BLUE_CONCRETE, new Color(33, 130, 190));
        blocksMap.put(Material.LIGHT_GRAY_CONCRETE, new Color(111, 115, 116));
        blocksMap.put(Material.LIME_CONCRETE, new Color(90, 162, 23));
        blocksMap.put(Material.MAGENTA_CONCRETE, new Color(162, 47, 152));
        blocksMap.put(Material.ORANGE_CONCRETE, new Color(207, 81, 1));
        blocksMap.put(Material.PINK_CONCRETE, new Color(205, 95, 138));
        blocksMap.put(Material.PURPLE_CONCRETE, new Color(155, 45, 145));
        blocksMap.put(Material.RED_CONCRETE, new Color(136, 30, 33));
        blocksMap.put(Material.YELLOW_CONCRETE, new Color(222, 162, 19));
        blocksMap.put(Material.SNOW, new Color(232, 240, 239));
        blocksMap.put(Material.SNOW_BLOCK, new Color(232, 240, 239));
        blocksMap.put(Material.POWDER_SNOW, new Color(232, 240, 239));
        blocksMap.put(Material.GLASS, new Color(255, 255, 255));
        blocksMap.put(Material.WHITE_STAINED_GLASS, new Color(255, 255, 255));
        blocksMap.put(Material.LIGHT_BLUE_STAINED_GLASS, new Color(102, 153, 216));
        blocksMap.put(Material.GLASS_PANE, new Color(255, 255, 255));
        blocksMap.put(Material.WHITE_STAINED_GLASS_PANE, new Color(255, 255, 255));
        blocksMap.put(Material.CAMPFIRE, new Color(129, 86, 49));
        blocksMap.put(Material.FIRE, new Color(153, 51, 51));
        blocksMap.put(Material.SOUL_FIRE, new Color(102, 153, 216));
        blocksMap.put(Material.SOUL_CAMPFIRE, new Color(129, 86, 49));
        blocksMap.put(Material.WATER, new Color(15, 94, 156));
        blocksMap.put(Material.COMMAND_BLOCK, new Color(198, 126, 78));
        blocksMap.put(Material.WEATHERED_COPPER, new Color(58, 142, 140));
        blocksMap.put(Material.WAXED_WEATHERED_COPPER, new Color(58, 142, 140));
        blocksMap.put(Material.WEATHERED_CUT_COPPER, new Color(58, 142, 140));
        blocksMap.put(Material.WAXED_WEATHERED_CUT_COPPER, new Color(58, 142, 140));
        blocksMap.put(Material.WEATHERED_CUT_COPPER_SLAB, new Color(58, 142, 140));
        blocksMap.put(Material.WAXED_WEATHERED_CUT_COPPER_SLAB, new Color(58, 142, 140));
        blocksMap.put(Material.WEATHERED_CUT_COPPER_STAIRS, new Color(58, 142, 140));
        blocksMap.put(Material.WAXED_WEATHERED_CUT_COPPER_STAIRS, new Color(58, 142, 140));
        blocksMap.put(Material.COPPER_BLOCK, new Color(186, 103, 75));
        blocksMap.put(Material.WAXED_COPPER_BLOCK, new Color(186, 103, 75));
        blocksMap.put(Material.WAXED_OXIDIZED_COPPER, new Color(61, 148, 147));
        blocksMap.put(Material.WAXED_OXIDIZED_CUT_COPPER_STAIRS, new Color(61, 148, 147));
        blocksMap.put(Material.WAXED_OXIDIZED_CUT_COPPER, new Color(61, 148, 147));
        blocksMap.put(Material.WAXED_OXIDIZED_CUT_COPPER_SLAB, new Color(61, 148, 147));
        blocksMap.put(Material.LIGHTNING_ROD, new Color(186, 103, 75));
        blocksMap.put(Material.RAW_COPPER_BLOCK, new Color(186, 103, 75));
        blocksMap.put(Material.CUT_COPPER, new Color(186, 103, 75));
        blocksMap.put(Material.WAXED_CUT_COPPER, new Color(186, 103, 75));
        blocksMap.put(Material.CUT_COPPER_SLAB, new Color(186, 103, 75));
        blocksMap.put(Material.WAXED_CUT_COPPER_SLAB, new Color(186, 103, 75));
        blocksMap.put(Material.CUT_COPPER_STAIRS, new Color(186, 103, 75));
        blocksMap.put(Material.WAXED_CUT_COPPER_STAIRS, new Color(186, 103, 75));
        blocksMap.put(Material.EXPOSED_COPPER, new Color(135, 107, 98));
        blocksMap.put(Material.WAXED_EXPOSED_COPPER, new Color(135, 107, 98));
        blocksMap.put(Material.EXPOSED_CUT_COPPER, new Color(135, 107, 98));
        blocksMap.put(Material.WAXED_EXPOSED_CUT_COPPER, new Color(135, 107, 98));
        blocksMap.put(Material.EXPOSED_CUT_COPPER_SLAB, new Color(135, 107, 98));
        blocksMap.put(Material.WAXED_EXPOSED_CUT_COPPER_SLAB, new Color(135, 107, 98));
        blocksMap.put(Material.EXPOSED_CUT_COPPER_STAIRS, new Color(135, 107, 98));
        blocksMap.put(Material.WAXED_EXPOSED_CUT_COPPER_STAIRS, new Color(135, 107, 98));
        blocksMap.put(Material.HANGING_ROOTS, new Color(102, 72, 53));
        blocksMap.put(Material.WATER_CAULDRON, new Color(39, 39, 39));
        blocksMap.put(Material.LAVA_CAULDRON, new Color(39, 39, 39));
        blocksMap.put(Material.POWDER_SNOW_CAULDRON, new Color(39, 39, 39));
        blocksMap.put(Material.SEA_LANTERN, new Color(224, 236, 255));
        blocksMap.put(Material.MOSS_BLOCK, new Color(49, 101, 25));
        blocksMap.put(Material.MOSS_CARPET, new Color(49, 101, 25));
        blocksMap.put(Material.TRIPWIRE_HOOK, new Color(172, 140, 88));
        blocksMap.put(Material.CHEST, new Color(172, 140, 88));
        blocksMap.put(Material.TRAPPED_CHEST, new Color(172, 140, 88));
        blocksMap.put(Material.OAK_PRESSURE_PLATE, new Color(172, 140, 88));
        blocksMap.put(Material.SPRUCE_PRESSURE_PLATE, new Color(100, 78, 47));
        blocksMap.put(Material.ACACIA_PRESSURE_PLATE, new Color(178, 102, 60));
        blocksMap.put(Material.HEAVY_WEIGHTED_PRESSURE_PLATE, new Color(236, 236, 236));
        blocksMap.put(Material.LIGHT_WEIGHTED_PRESSURE_PLATE, new Color(243, 223, 75));
        blocksMap.put(Material.STONE_PRESSURE_PLATE, new Color(117, 117, 117));

        blocksMap.put(Material.POLISHED_BASALT, new Color(25, 25, 25));
        blocksMap.put(Material.DEEPSLATE, new Color(35, 35, 39));
        blocksMap.put(Material.CHISELED_DEEPSLATE, new Color(35, 35, 39));
        blocksMap.put(Material.POLISHED_DEEPSLATE, new Color(35, 35, 39));
        blocksMap.put(Material.DEEPSLATE_BRICK_SLAB, new Color(35, 35, 39));
        blocksMap.put(Material.DEEPSLATE_BRICK_STAIRS, new Color(35, 35, 39));
        blocksMap.put(Material.DEEPSLATE_BRICK_WALL, new Color(35, 35, 39));
        blocksMap.put(Material.DEEPSLATE_BRICKS, new Color(35, 35, 39));
        blocksMap.put(Material.INFESTED_DEEPSLATE, new Color(35, 35, 39));
        blocksMap.put(Material.CRACKED_DEEPSLATE_BRICKS, new Color(35, 35, 39));
        blocksMap.put(Material.DEEPSLATE_COAL_ORE, new Color(35, 35, 39));
        blocksMap.put(Material.DEEPSLATE_COPPER_ORE, new Color(35, 35, 39));
        blocksMap.put(Material.DEEPSLATE_DIAMOND_ORE, new Color(35, 35, 39));
        blocksMap.put(Material.DEEPSLATE_EMERALD_ORE, new Color(35, 35, 39));
        blocksMap.put(Material.DEEPSLATE_GOLD_ORE, new Color(35, 35, 39));
        blocksMap.put(Material.DEEPSLATE_LAPIS_ORE, new Color(35, 35, 39));
        blocksMap.put(Material.DEEPSLATE_IRON_ORE, new Color(35, 35, 39));
        blocksMap.put(Material.DEEPSLATE_REDSTONE_ORE, new Color(35, 35, 39));
        blocksMap.put(Material.DEEPSLATE_TILE_SLAB, new Color(35, 35, 39));
        blocksMap.put(Material.DEEPSLATE_TILE_STAIRS, new Color(35, 35, 39));
        blocksMap.put(Material.DEEPSLATE_TILE_WALL, new Color(35, 35, 39));
        blocksMap.put(Material.DEEPSLATE_TILES, new Color(35, 35, 39));
        blocksMap.put(Material.CRACKED_DEEPSLATE_TILES, new Color(35, 35, 39));
        blocksMap.put(Material.COBBLED_DEEPSLATE, new Color(35, 35, 39));
        blocksMap.put(Material.COBBLED_DEEPSLATE_SLAB, new Color(35, 35, 39));
        blocksMap.put(Material.COBBLED_DEEPSLATE_STAIRS, new Color(35, 35, 39));
        blocksMap.put(Material.COBBLED_DEEPSLATE_WALL, new Color(35, 35, 39));
        blocksMap.put(Material.POLISHED_DEEPSLATE_STAIRS, new Color(35, 35, 39));
        blocksMap.put(Material.POLISHED_DEEPSLATE_SLAB, new Color(35, 35, 39));
        blocksMap.put(Material.POLISHED_DEEPSLATE_WALL, new Color(35, 35, 39));

        blocksMap.put(Material.STONE_BRICKS, new Color(117, 117, 117));
        blocksMap.put(Material.STONE_BRICK_SLAB, new Color(117, 117, 117));
        blocksMap.put(Material.STONE_BRICK_STAIRS, new Color(117, 117, 117));
        blocksMap.put(Material.STONE_BRICK_WALL, new Color(117, 117, 117));
        blocksMap.put(Material.CRACKED_STONE_BRICKS, new Color(117, 117, 117));
        blocksMap.put(Material.CRYING_OBSIDIAN, new Color(4, 2, 7));
        blocksMap.put(Material.OBSIDIAN, new Color(4, 2, 7));
        blocksMap.put(Material.RESPAWN_ANCHOR, new Color(4, 2, 7));

        blocksMap.put(Material.AMETHYST_BLOCK, new Color(110, 80, 164));
        blocksMap.put(Material.BUDDING_AMETHYST, new Color(110, 80, 164));
        blocksMap.put(Material.LARGE_AMETHYST_BUD, new Color(110, 80, 164));
        blocksMap.put(Material.MEDIUM_AMETHYST_BUD, new Color(110, 80, 164));
        blocksMap.put(Material.SMALL_AMETHYST_BUD, new Color(110, 80, 164));
        blocksMap.put(Material.AMETHYST_CLUSTER, new Color(110, 80, 164));
        blocksMap.put(Material.LEVER, new Color(130, 130, 130));

        blocksMap.put(Material.RED_NETHER_BRICKS, new Color(112, 2, 0));
        blocksMap.put(Material.RED_NETHER_BRICK_SLAB, new Color(112, 2, 0));
        blocksMap.put(Material.RED_NETHER_BRICK_STAIRS, new Color(112, 2, 0));
        blocksMap.put(Material.RED_NETHER_BRICK_WALL, new Color(112, 2, 0));

        blocksMap.put(Material.NETHER_BRICKS, new Color(65, 5, 7));
        blocksMap.put(Material.CHISELED_NETHER_BRICKS, new Color(65, 5, 7));
        blocksMap.put(Material.CRACKED_NETHER_BRICKS, new Color(65, 5, 7));
        blocksMap.put(Material.NETHER_BRICK_SLAB, new Color(65, 5, 7));
        blocksMap.put(Material.NETHER_BRICK_STAIRS, new Color(65, 5, 7));
        blocksMap.put(Material.NETHER_BRICK_WALL, new Color(65, 5, 7));
        blocksMap.put(Material.NETHER_BRICK_FENCE, new Color(65, 5, 7));


        blocksMap.put(Material.SMOOTH_SANDSTONE, new Color(213, 207, 162));
        blocksMap.put(Material.SMOOTH_SANDSTONE_SLAB, new Color(213, 207, 162));
        blocksMap.put(Material.SMOOTH_SANDSTONE_STAIRS, new Color(213, 207, 162));
        blocksMap.put(Material.SMOOTH_RED_SANDSTONE, new Color(203, 110, 36));
        blocksMap.put(Material.CHISELED_RED_SANDSTONE, new Color(203, 110, 36));
        blocksMap.put(Material.SMOOTH_RED_SANDSTONE_SLAB, new Color(203, 110, 36));
        blocksMap.put(Material.SMOOTH_RED_SANDSTONE_STAIRS, new Color(203, 110, 36));
        blocksMap.put(Material.SMOOTH_QUARTZ, new Color(238, 230, 222));
        blocksMap.put(Material.CHISELED_QUARTZ_BLOCK, new Color(238, 230, 222));
        blocksMap.put(Material.SMOOTH_QUARTZ_SLAB, new Color(238, 230, 222));
        blocksMap.put(Material.SMOOTH_QUARTZ_STAIRS, new Color(238, 230, 222));
        blocksMap.put(Material.GLOW_LICHEN, new Color(109, 124, 119));
        blocksMap.put(Material.SEA_PICKLE, new Color(96, 102, 35));

        blocksMap.put(Material.END_STONE, new Color(247, 233, 163));
        blocksMap.put(Material.END_STONE_BRICKS, new Color(247, 233, 163));
        blocksMap.put(Material.END_STONE_BRICK_SLAB, new Color(247, 233, 163));
        blocksMap.put(Material.END_STONE_BRICK_STAIRS, new Color(247, 233, 163));
        blocksMap.put(Material.END_STONE_BRICK_WALL, new Color(247, 233, 163));

        blocksMap.put(Material.BLUE_ICE, new Color(146, 185, 254));

        blocksMap.put(Material.TUBE_CORAL_BLOCK, new Color(51, 76, 178));
        blocksMap.put(Material.TUBE_CORAL, new Color(51, 76, 178));
        blocksMap.put(Material.TUBE_CORAL_FAN, new Color(51, 76, 178));
        blocksMap.put(Material.TUBE_CORAL_WALL_FAN, new Color(51, 76, 178));
        blocksMap.put(Material.FIRE_CORAL_BLOCK, new Color(153, 51, 51));
        blocksMap.put(Material.FIRE_CORAL, new Color(153, 51, 51));
        blocksMap.put(Material.FIRE_CORAL_FAN, new Color(153, 51, 51));
        blocksMap.put(Material.FIRE_CORAL_WALL_FAN, new Color(153, 51, 51));
        blocksMap.put(Material.BRAIN_CORAL_BLOCK, new Color(242, 127, 165));
        blocksMap.put(Material.BRAIN_CORAL, new Color(242, 127, 165));
        blocksMap.put(Material.BRAIN_CORAL_FAN, new Color(242, 127, 165));
        blocksMap.put(Material.BRAIN_CORAL_WALL_FAN, new Color(242, 127, 165));
        blocksMap.put(Material.BUBBLE_CORAL_BLOCK, new Color(127, 63, 178));
        blocksMap.put(Material.BUBBLE_CORAL, new Color(127, 63, 178));
        blocksMap.put(Material.BUBBLE_CORAL_FAN, new Color(127, 63, 178));
        blocksMap.put(Material.BUBBLE_CORAL_WALL_FAN, new Color(127, 63, 178));
        blocksMap.put(Material.HORN_CORAL_BLOCK, new Color(229, 229, 51));
        blocksMap.put(Material.HORN_CORAL, new Color(229, 229, 51));
        blocksMap.put(Material.HORN_CORAL_FAN, new Color(229, 229, 51));
        blocksMap.put(Material.HORN_CORAL_WALL_FAN, new Color(229, 229, 51));

        blocksMap.put(Material.LILY_PAD, new Color(0, 124, 0));

        blocksMap.put(Material.WHITE_TERRACOTTA, new Color(209, 177, 161));
        blocksMap.put(Material.WHITE_GLAZED_TERRACOTTA, new Color(255, 255, 255));
        blocksMap.put(Material.ORANGE_TERRACOTTA, new Color(159, 82, 36));
        blocksMap.put(Material.ORANGE_GLAZED_TERRACOTTA, new Color(216, 127, 51));
        blocksMap.put(Material.MAGENTA_TERRACOTTA, new Color(149, 87, 108));
        blocksMap.put(Material.MAGENTA_GLAZED_TERRACOTTA, new Color(178, 76, 216));
        blocksMap.put(Material.LIGHT_BLUE_TERRACOTTA, new Color(112, 108, 138));
        blocksMap.put(Material.LIGHT_BLUE_GLAZED_TERRACOTTA, new Color(102, 153, 216));
        blocksMap.put(Material.YELLOW_TERRACOTTA, new Color(186, 133, 36));
        blocksMap.put(Material.YELLOW_GLAZED_TERRACOTTA, new Color(229, 229, 51));
        blocksMap.put(Material.LIME_TERRACOTTA, new Color(103, 117, 53));
        blocksMap.put(Material.LIME_GLAZED_TERRACOTTA, new Color(127, 204, 25));
        blocksMap.put(Material.PINK_TERRACOTTA, new Color(160, 77, 78));
        blocksMap.put(Material.PINK_GLAZED_TERRACOTTA, new Color(242, 127, 165));
        blocksMap.put(Material.GRAY_TERRACOTTA, new Color(57, 41, 35));
        blocksMap.put(Material.GRAY_GLAZED_TERRACOTTA, new Color(76, 76, 76));
        blocksMap.put(Material.LIGHT_GRAY_TERRACOTTA, new Color(135, 107, 98));
        blocksMap.put(Material.LIGHT_GRAY_GLAZED_TERRACOTTA, new Color(153, 153, 153));
        blocksMap.put(Material.CYAN_TERRACOTTA, new Color(87, 92, 92));
        blocksMap.put(Material.CYAN_GLAZED_TERRACOTTA, new Color(76, 127, 153));
        blocksMap.put(Material.PURPLE_TERRACOTTA, new Color(122, 73, 88));
        blocksMap.put(Material.PURPLE_GLAZED_TERRACOTTA, new Color(127, 63, 178));
        blocksMap.put(Material.BLUE_TERRACOTTA, new Color(76, 62, 92));
        blocksMap.put(Material.BLUE_GLAZED_TERRACOTTA, new Color(51, 76, 178));
        blocksMap.put(Material.BROWN_TERRACOTTA, new Color(76, 50, 35));
        blocksMap.put(Material.BROWN_GLAZED_TERRACOTTA, new Color(102, 76, 51));
        blocksMap.put(Material.GREEN_TERRACOTTA, new Color(76, 82, 42));
        blocksMap.put(Material.GREEN_GLAZED_TERRACOTTA, new Color(102, 127, 51));
        blocksMap.put(Material.RED_TERRACOTTA, new Color(142, 60, 46));
        blocksMap.put(Material.RED_GLAZED_TERRACOTTA, new Color(153, 51, 51));
        blocksMap.put(Material.BLACK_TERRACOTTA, new Color(37, 22, 16));
        blocksMap.put(Material.BLACK_GLAZED_TERRACOTTA, new Color(25, 25, 25));
        blocksMap.put(Material.TERRACOTTA, new Color(216, 127, 51));

        blocksMap.put(Material.BRICKS, new Color(153, 51, 51));
        blocksMap.put(Material.BRICK_SLAB, new Color(153, 51, 51));
        blocksMap.put(Material.BRICK_WALL, new Color(153, 51, 51));
        blocksMap.put(Material.BRICK_STAIRS, new Color(153, 51, 51));
        blocksMap.put(Material.END_ROD, new Color(255, 255, 255));

        blocksMap.put(Material.POLISHED_BLACKSTONE_BRICK_SLAB, new Color(25, 25, 25));
        blocksMap.put(Material.CHISELED_POLISHED_BLACKSTONE, new Color(25, 25, 25));
        blocksMap.put(Material.POLISHED_BLACKSTONE_BRICK_STAIRS, new Color(25, 25, 25));
        blocksMap.put(Material.POLISHED_BLACKSTONE_BRICK_WALL, new Color(25, 25, 25));
        blocksMap.put(Material.POLISHED_BLACKSTONE_BUTTON, new Color(25, 25, 25));
        blocksMap.put(Material.POLISHED_BLACKSTONE_BRICKS, new Color(25, 25, 25));
        blocksMap.put(Material.POLISHED_BLACKSTONE_PRESSURE_PLATE, new Color(25, 25, 25));
        blocksMap.put(Material.POLISHED_BLACKSTONE_WALL, new Color(25, 25, 25));
        blocksMap.put(Material.POLISHED_BLACKSTONE, new Color(25, 25, 25));
        blocksMap.put(Material.CRACKED_POLISHED_BLACKSTONE_BRICKS, new Color(25, 25, 25));
        blocksMap.put(Material.GILDED_BLACKSTONE, new Color(25, 25, 25));
        blocksMap.put(Material.BLACKSTONE, new Color(25, 25, 25));
        blocksMap.put(Material.BLACKSTONE_SLAB, new Color(25, 25, 25));
        blocksMap.put(Material.BLACKSTONE_STAIRS, new Color(25, 25, 25));
        blocksMap.put(Material.BLACKSTONE_WALL, new Color(25, 25, 25));
        blocksMap.put(Material.TWISTING_VINES, new Color(22, 126, 134));
        blocksMap.put(Material.TWISTING_VINES_PLANT, new Color(22, 126, 134));
        blocksMap.put(Material.STRIPPED_WARPED_HYPHAE, new Color(22, 126, 134));
        blocksMap.put(Material.STRIPPED_WARPED_STEM, new Color(22, 126, 134));
        blocksMap.put(Material.NETHER_SPROUTS, new Color(22, 126, 134));
        blocksMap.put(Material.WEEPING_VINES, new Color(123, 0, 0));
        blocksMap.put(Material.WEEPING_VINES_PLANT, new Color(123, 0, 0));
        blocksMap.put(Material.STRIPPED_CRIMSON_STEM, new Color(123, 0, 0));
        blocksMap.put(Material.STRIPPED_CRIMSON_HYPHAE, new Color(123, 0, 0));
        blocksMap.put(Material.NETHER_WART, new Color(123, 0, 0));
        blocksMap.put(Material.NETHER_WART_BLOCK, new Color(123, 0, 0));
        blocksMap.put(Material.MAGMA_BLOCK, new Color(112, 2, 0));

        blocksMap.put(Material.DARK_PRISMARINE, new Color(92, 219, 213));
        blocksMap.put(Material.DARK_PRISMARINE_SLAB, new Color(92, 219, 213));
        blocksMap.put(Material.DARK_PRISMARINE_STAIRS, new Color(92, 219, 213));
        blocksMap.put(Material.END_PORTAL, new Color(25, 25, 25));
        blocksMap.put(Material.END_GATEWAY, new Color(25, 25, 25));
        blocksMap.put(Material.END_PORTAL_FRAME, new Color(222, 215, 172));
        blocksMap.put(Material.BAMBOO, new Color(49, 101, 25));
        blocksMap.put(Material.BAMBOO_SAPLING, new Color(49, 101, 25));
        blocksMap.put(Material.COMPOSTER, new Color(143, 119, 72));
        blocksMap.put(Material.CARTOGRAPHY_TABLE, new Color(102, 76, 51));
        blocksMap.put(Material.RED_MUSHROOM_BLOCK, new Color(153, 51, 51));
        blocksMap.put(Material.MUSHROOM_STEM, new Color(199, 199, 199));
        blocksMap.put(Material.COCOA, new Color(151, 109, 77));
        blocksMap.put(Material.MYCELIUM, new Color(127, 63, 178));
        blocksMap.put(Material.LANTERN, new Color(216, 127, 51));
        blocksMap.put(Material.SOUL_LANTERN, new Color(102, 153, 216));
        blocksMap.put(Material.GRINDSTONE, new Color(167, 167, 167));
        blocksMap.put(Material.RED_BED, new Color(153, 51, 51));
        blocksMap.put(Material.YELLOW_BED, new Color(229, 229, 51));
        blocksMap.put(Material.LIGHT_BLUE_BED, new Color(102, 153, 216));
        blocksMap.put(Material.BLUE_BED, new Color(51, 76, 178));
        blocksMap.put(Material.ORANGE_BED, new Color(216, 127, 51));
        blocksMap.put(Material.YELLOW_STAINED_GLASS_PANE, new Color(229, 229, 51));
        blocksMap.put(Material.BLUE_STAINED_GLASS_PANE, new Color(51, 76, 178));
        blocksMap.put(Material.ORANGE_STAINED_GLASS_PANE, new Color(216, 127, 51));
        blocksMap.put(Material.BELL, new Color(243, 223, 75));
        blocksMap.put(Material.RAIL, new Color(143, 119, 72));
        blocksMap.put(Material.ACTIVATOR_RAIL, new Color(143, 119, 72));
        blocksMap.put(Material.DETECTOR_RAIL, new Color(143, 119, 72));
        blocksMap.put(Material.POWERED_RAIL, new Color(143, 119, 72));
        blocksMap.put(Material.PURPUR_BLOCK, new Color(127, 63, 178));
        blocksMap.put(Material.PURPUR_PILLAR, new Color(127, 63, 178));
        blocksMap.put(Material.PURPUR_SLAB, new Color(127, 63, 178));
        blocksMap.put(Material.PURPUR_STAIRS, new Color(127, 63, 178));
        blocksMap.put(Material.HOPPER, new Color(112, 112, 112));
        blocksMap.put(Material.AZALEA_LEAVES, new Color(49, 111, 21));
        blocksMap.put(Material.AZALEA, new Color(49, 111, 21));
        blocksMap.put(Material.FLOWERING_AZALEA_LEAVES, new Color(49, 111, 21));
        blocksMap.put(Material.FLOWERING_AZALEA, new Color(49, 111, 21));
        blocksMap.put(Material.NETHER_QUARTZ_ORE, new Color(114, 50, 50));
        blocksMap.put(Material.NETHER_GOLD_ORE, new Color(114, 50, 50));
        blocksMap.put(Material.BEE_NEST, new Color(229, 229, 51));
        blocksMap.put(Material.SWEET_BERRY_BUSH, new Color(0, 124, 0));
        blocksMap.put(Material.WHEAT, new Color(211, 180, 99));
        blocksMap.put(Material.CARROTS, new Color(218, 133, 28));
        blocksMap.put(Material.POTATOES, new Color(192, 156, 74));
        blocksMap.put(Material.MELON_STEM, new Color(0, 124, 0));
        blocksMap.put(Material.PUMPKIN_STEM, new Color(0, 124, 0));

        blocksMap.put(Material.ORANGE_CARPET, new Color(216, 127, 51));
        blocksMap.put(Material.ORANGE_CANDLE, new Color(216, 127, 51));
        blocksMap.put(Material.LIGHT_BLUE_CARPET, new Color(102, 153, 216));
        blocksMap.put(Material.LIGHT_BLUE_CANDLE, new Color(102, 153, 216));
        blocksMap.put(Material.RED_CARPET, new Color(153, 51, 51));
        blocksMap.put(Material.RED_CANDLE, new Color(153, 51, 51));
        blocksMap.put(Material.BLACK_CARPET, new Color(25, 25, 25));
        blocksMap.put(Material.BLACK_CANDLE, new Color(25, 25, 25));
        blocksMap.put(Material.BLUE_CARPET, new Color(25, 25, 25));
        blocksMap.put(Material.BLUE_CANDLE, new Color(25, 25, 25));

        blocksMap.put(Material.WHITE_SHULKER_BOX, new Color(255, 255, 255));
        blocksMap.put(Material.ORANGE_SHULKER_BOX, new Color(216, 127, 51));
        blocksMap.put(Material.MAGENTA_SHULKER_BOX, new Color(178, 76, 216));
        blocksMap.put(Material.LIGHT_BLUE_SHULKER_BOX, new Color(102, 153, 216));
        blocksMap.put(Material.YELLOW_SHULKER_BOX, new Color(229, 229, 51));
        blocksMap.put(Material.LIME_SHULKER_BOX, new Color(127, 204, 25));
        blocksMap.put(Material.PINK_SHULKER_BOX, new Color(242, 127, 165));
        blocksMap.put(Material.GRAY_SHULKER_BOX, new Color(76, 76, 76));
        blocksMap.put(Material.LIGHT_GRAY_SHULKER_BOX, new Color(153, 153, 153));
        blocksMap.put(Material.CYAN_SHULKER_BOX, new Color(76, 127, 153));
        blocksMap.put(Material.PURPLE_SHULKER_BOX, new Color(127, 63, 178));
        blocksMap.put(Material.BLUE_SHULKER_BOX, new Color(51, 76, 178));
        blocksMap.put(Material.BROWN_SHULKER_BOX, new Color(102, 76, 51));
        blocksMap.put(Material.GREEN_SHULKER_BOX, new Color(102, 127, 51));
        blocksMap.put(Material.RED_SHULKER_BOX, new Color(153, 51, 51));
        blocksMap.put(Material.BLACK_SHULKER_BOX, new Color(25, 25, 25));

        blocksMap.put(Material.PLAYER_HEAD, new Color(25, 25, 25));
        blocksMap.put(Material.PLAYER_WALL_HEAD, new Color(25, 25, 25));
        blocksMap.put(Material.WITHER_SKELETON_SKULL, new Color(25, 25, 25));
        blocksMap.put(Material.WITHER_SKELETON_WALL_SKULL, new Color(25, 25, 25));
        blocksMap.put(Material.SKELETON_SKULL, new Color(149, 149, 152));
        blocksMap.put(Material.SKELETON_WALL_SKULL, new Color(149, 149, 152));
        blocksMap.put(Material.ZOMBIE_HEAD, new Color(42, 70, 34));
        blocksMap.put(Material.ZOMBIE_WALL_HEAD, new Color(42, 70, 34));
        blocksMap.put(Material.CREEPER_HEAD, new Color(0, 159, 0));
        blocksMap.put(Material.CREEPER_WALL_HEAD, new Color(0, 159, 0));
        blocksMap.put(Material.DRIPSTONE_BLOCK, new Color(76, 50, 35));
        blocksMap.put(Material.POINTED_DRIPSTONE, new Color(76, 50, 35));
        blocksMap.put(Material.VINE, new Color(0, 124, 0));

    }

    @SuppressWarnings("deprecation")
    public static byte colorFromType(Block block, double[] dye) {
        HashMap<Material, BufferedImage> imageMap = CameraResourcePackManager.getImageHashMap();
        if (blocksMap.containsKey(block.getType())) {
            // if blockMap has a color for the material, use that color
            Color color = blocksMap.get(block.getType());
            int redColor = (int) (color.getRed() * dye[0]);
            int greenColor = (int) (color.getGreen() * dye[1]);
            int blueColor = (int) (color.getBlue() * dye[2]);

            if (redColor > 255) redColor = 255;
            if (greenColor > 255) greenColor = 255;
            if (blueColor > 255) blueColor = 255;
            return MapPalette.matchColor(new Color(redColor, greenColor, blueColor));
        }
        if (imageMap.containsKey(block.getType())) {
            // if imageMap has a color for the material, use that color
            BufferedImage image = imageMap.get(block.getType());
            if (image == null) {
                Bukkit.getLogger().info("Missing Image For: " + block.getType());
            } else {
            	int width = image.getWidth(), height = image.getHeight();
                long r = 0, g = 0, b = 0, count = 0;

                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        Color color = new Color(image.getRGB(x, y), true);
                        r += color.getRed();
                        g += color.getGreen();
                        b += color.getBlue();
                        count++;
                    }
                }

                // Calculate the average color
                Color avgColor = new Color((int) (r / count), (int) (g / count), (int) (b / count));

                // Find and return the closest match from the palette
                return getClosestPaletteByte(avgColor);

            	/*
                // gets certain pixel in image to use as color TODO: Create a hashmap of colors
                // so we don't need to access the image multiple times.
                Color color = new Color(image.getRGB((int) (image.getWidth() / 1.5), (int) (image.getHeight() / 1.5)));

                int redColor = (int) (color.getRed() * dye[0]);
                int greenColor = (int) (color.getGreen() * dye[1]);
                int blueColor = (int) (color.getBlue() * dye[2]);

                if (redColor > 255) redColor = 255;
                if (greenColor > 255) greenColor = 255;
                if (blueColor > 255) blueColor = 255;

                return MapPalette.matchColor(new Color(redColor, greenColor, blueColor));
                */
            }
        }
        return MapPalette.GRAY_2; // no color was found, use gray
    }

    
 // Define a custom 256-color palette
    private static final Color[] PALETTE = generatePalette();
    
    private static byte getClosestPaletteByte(Color color) {
        int minDistance = Integer.MAX_VALUE;
        int bestMatch = 0; // Use int to prevent negative index issues

        for (int i = 0; i < PALETTE.length; i++) {
            int distance = colorDistance(color, PALETTE[i]);

            if (distance < minDistance) {
                minDistance = distance;
                bestMatch = i;
            }
        }
        return (byte) bestMatch; // Store as byte safely
    }

    private static int colorDistance(Color c1, Color c2) {
        int dr = c1.getRed() - c2.getRed();
        int dg = c1.getGreen() - c2.getGreen();
        int db = c1.getBlue() - c2.getBlue();
        return (dr * dr) + (dg * dg) + (db * db); // Squared Euclidean distance
    }

    private static Color[] generatePalette() {
        Color[] palette = new Color[256];

        for (int i = 0; i < 256; i++) {
            int r = (i & 0xE0); // Top 3 bits for Red
            int g = (i & 0x1C) << 3; // Middle 3 bits for Green
            int b = (i & 0x03) << 6; // Bottom 2 bits for Blue
            palette[i] = new Color(r, g, b);
        }

        return palette;
    }
}
