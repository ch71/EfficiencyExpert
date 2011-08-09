package me.mathmaniac43.efficiencyexpert;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.*;

public class EfficiencyExpertPlayerListener extends PlayerListener {
    public static EfficiencyExpert plugin;
    
    static Properties prop = new Properties();
    
    static Map<Player, boolean[]> playerStats = new HashMap<Player, boolean[]>();
    
    private static final Material[] tools =
    {Material.WOOD_AXE, Material.WOOD_PICKAXE, Material.WOOD_SPADE,
        /*Material.WOOD_HOE, Material.WOOD_SWORD, */Material.GOLD_AXE,
        Material.GOLD_PICKAXE, Material.GOLD_SPADE,/* Material.GOLD_HOE,
         * Material.GOLD_SWORD, */Material.STONE_AXE, Material.STONE_PICKAXE,
         Material.STONE_SPADE,/* Material.STONE_HOE, Material.STONE_SWORD,*/
         Material.IRON_AXE, Material.IRON_PICKAXE, Material.IRON_SPADE,
         /*Material.IRON_HOE, Material.IRON_SWORD, */Material.DIAMOND_AXE,
         Material.DIAMOND_PICKAXE, Material.DIAMOND_SPADE, /*Material.DIAMOND_HOE,
          * Material.DIAMOND_SWORD, */Material.SHEARS, Material.AIR, null};
    
    private static final Material[] woodAndGoldPickMaterials =
    {Material.IRON_DOOR_BLOCK, Material.MOB_SPAWNER, Material.DISPENSER,
        Material.FURNACE, Material.BURNING_FURNACE, Material.COAL_ORE,
        Material.BRICK, Material.COBBLESTONE, Material.MOSSY_COBBLESTONE,
        Material.STEP, Material.STONE, Material.SANDSTONE,
        Material.ICE, Material.NETHERRACK, Material.GLOWSTONE};
    
    private static final Material[] stonePickMaterials =
    {Material.IRON_DOOR_BLOCK, Material.MOB_SPAWNER, Material.DISPENSER,
        Material.FURNACE, Material.BURNING_FURNACE, Material.COAL_ORE,
        Material.BRICK, Material.COBBLESTONE, Material.MOSSY_COBBLESTONE,
        Material.STEP, Material.STONE, Material.SANDSTONE,
        Material.ICE, Material.NETHERRACK, Material.GLOWSTONE,
        Material.IRON_ORE, Material.LAPIS_ORE, Material.LAPIS_BLOCK};
    
    private static final Material[] ironPickMaterials =
    {Material.IRON_DOOR_BLOCK, Material.MOB_SPAWNER, Material.DISPENSER,
        Material.FURNACE, Material.BURNING_FURNACE, Material.COAL_ORE,
        Material.BRICK, Material.COBBLESTONE, Material.MOSSY_COBBLESTONE,
        Material.STEP, Material.STONE, Material.SANDSTONE,
        Material.ICE, Material.NETHERRACK, Material.GLOWSTONE,
        Material.IRON_ORE, Material.LAPIS_ORE, Material.LAPIS_BLOCK,
        Material.DIAMOND_BLOCK, Material.IRON_BLOCK, Material.DIAMOND_ORE,
        Material.GOLD_ORE, Material.REDSTONE_ORE, Material.GOLD_BLOCK,
        Material.GLOWING_REDSTONE_ORE};
    
    private static final Material[] allPickMaterials =
    {Material.IRON_DOOR_BLOCK, Material.MOB_SPAWNER, Material.DISPENSER,
        Material.FURNACE, Material.BURNING_FURNACE, Material.COAL_ORE,
        Material.BRICK, Material.COBBLESTONE, Material.MOSSY_COBBLESTONE,
        Material.STEP, Material.STONE, Material.SANDSTONE,
        Material.ICE, Material.NETHERRACK, Material.GLOWSTONE,
        Material.IRON_ORE, Material.LAPIS_ORE, Material.LAPIS_BLOCK,
        Material.DIAMOND_BLOCK, Material.IRON_BLOCK, Material.DIAMOND_ORE,
        Material.GOLD_ORE, Material.REDSTONE_ORE, Material.GOLD_BLOCK,
        Material.OBSIDIAN, Material.GLOWING_REDSTONE_ORE};
    
    public static final Material[] allSpadeMaterials =
    {Material.CLAY_BRICK, Material.GRASS, Material.GRAVEL,
        Material.DIRT, Material.SAND, Material.SNOW,
        Material.SNOW_BLOCK};
    
    public static final Material[] allAxeMaterials =
    {Material.CHEST, Material.WOOD, Material.BOOKSHELF,
        Material.LOG};
    
    public static final Material[] allShearsMaterials =
    {Material.LEAVES, Material.WEB};
    
    public EfficiencyExpertPlayerListener(EfficiencyExpert instance) {
        plugin = instance;
    }
    
    public static void startUp(List<Player> allPlayers) {
        for (Player p : allPlayers) {
            load(p);
            status(p);
        }
    }
    
    public static void spinDown() {
        for (Player p : playerStats.keySet())
            saveAndExit(p);
    }
    
    private boolean hasMaterial(Material[] materials, Material material) {
        for (Material temp : materials)
            if (material.equals(temp))
                return true;
        return false;
    }
    
    private boolean canSwitchTools(Player player) {
        Material mat = player.getItemInHand().getType();
        
        if (hasMaterial(tools, mat))
            return true;
        else
            return false;
    }
    
    private boolean checkFor(Player player, Material material) {
        if (player.getInventory().contains(material)) {
            int toolLoc = player.getInventory().first(material);
            short toolLowestHealth = player.getInventory().getItem(toolLoc).getDurability();
            
            for (int i = toolLoc + 1; i < player.getInventory().getSize(); i ++) {
                ItemStack is = player.getInventory().getItem(i);
                if (is.getType().equals(material) && is.getDurability() > toolLowestHealth) {
                    toolLoc = i;
                    toolLowestHealth = player.getInventory().getItem(toolLoc).getDurability();
                }
            }
            
            ItemStack tool = player.getInventory().getItem(toolLoc);
            ItemStack other = player.getInventory().getItemInHand();
            if (other.getType().equals(Material.AIR))
                other = null;
            player.getInventory().setItem(toolLoc, other);
            player.getInventory().setItemInHand(tool);
            return true;
        }
        return false;
    }
    
    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        load(event.getPlayer());
    }
    
    @Override
    public void onPlayerQuit(PlayerQuitEvent event) {
        saveAndExit(event.getPlayer());
    }
    
    @Override
    public void onPlayerMove(PlayerMoveEvent event) {
        go(event);
    }
    
    @Override
    public void onPlayerAnimation(PlayerAnimationEvent event) {
        go(event);
    }
    
    public void go(PlayerEvent event) {
        Player player = event.getPlayer();
        Block block = player.getTargetBlock(null, 4);
        Material material = block.getType();
        
        boolean active = playerStats.get(player)[0];
        boolean efficient = playerStats.get(player)[1];
        
        if (active) {
            if (canSwitchTools(player)) {
                if (hasMaterial(allPickMaterials, material)) {
                    if (efficient) {
                        if (hasMaterial(woodAndGoldPickMaterials, material) && (checkFor(player, Material.WOOD_PICKAXE) || checkFor(player, Material.GOLD_PICKAXE))) {}
                        else if (hasMaterial(stonePickMaterials, material) && checkFor(player, Material.STONE_PICKAXE)) {}
                        else if (hasMaterial(ironPickMaterials, material) && checkFor(player, Material.IRON_PICKAXE)) {}
                        else if (hasMaterial(allPickMaterials, material) && checkFor(player, Material.DIAMOND_PICKAXE)) {}
                        else {
                            ItemStack curItem = player.getInventory().getItemInHand();
                            int moveLoc = player.getInventory().firstEmpty();
                            if (!curItem.getType().equals(Material.AIR)) {
                                if (moveLoc != -1) {
                                    player.getInventory().setItem(moveLoc, curItem);
                                    player.getInventory().clear(player.getInventory().getHeldItemSlot());
                                } else {
                                    for (int i = 0; i < player.getInventory().getContents().length; i ++) {
                                        ItemStack is = player.getInventory().getContents()[i];
                                        if (!hasMaterial(tools, is.getType())) {
                                            moveLoc = i;
                                            break;
                                        }
                                    }
                                    
                                    if (moveLoc != -1) {
                                        ItemStack temp = player.getInventory().getItem(moveLoc);
                                        player.getInventory().setItem(moveLoc, curItem);
                                        player.setItemInHand(temp);
                                    }
                                }
                            }
                        }
                    }
                    else {
                        if (hasMaterial(woodAndGoldPickMaterials, material) && checkFor(player, Material.GOLD_PICKAXE)) {}
                        else if (hasMaterial(allPickMaterials, material) && checkFor(player, Material.DIAMOND_PICKAXE)) {}
                        else if (hasMaterial(ironPickMaterials, material) && checkFor(player, Material.IRON_PICKAXE)) {}
                        else if (hasMaterial(stonePickMaterials, material) && checkFor(player, Material.STONE_PICKAXE)) {}
                        else if (hasMaterial(woodAndGoldPickMaterials, material) && checkFor(player, Material.WOOD_PICKAXE)) {}
                        else {
                            ItemStack curItem = player.getInventory().getItemInHand();
                            int moveLoc = player.getInventory().firstEmpty();
                            if (!curItem.getType().equals(Material.AIR)) {
                                if (moveLoc != -1) {
                                    player.getInventory().setItem(moveLoc, curItem);
                                    player.getInventory().clear(player.getInventory().getHeldItemSlot());
                                } else {
                                    for (int i = 0; i < player.getInventory().getContents().length; i ++) {
                                        ItemStack is = player.getInventory().getContents()[i];
                                        if (!hasMaterial(tools, is.getType())) {
                                            moveLoc = i;
                                            break;
                                        }
                                    }
                                    
                                    if (moveLoc != -1) {
                                        ItemStack temp = player.getInventory().getItem(moveLoc);
                                        player.getInventory().setItem(moveLoc, curItem);
                                        player.setItemInHand(temp);
                                    }
                                }
                            }
                        }
                    }
                }
                else if (hasMaterial(allSpadeMaterials, material)) {
                    if (efficient) {
                        if (checkFor(player, Material.WOOD_SPADE)) {}
                        else if (checkFor(player, Material.STONE_SPADE)) {}
                        else if (checkFor(player, Material.IRON_SPADE)) {}
                        else if (checkFor(player, Material.DIAMOND_SPADE)) {}
                        else if (checkFor(player, Material.GOLD_SPADE)) {}
                        else {
                            ItemStack curItem = player.getInventory().getItemInHand();
                            int moveLoc = player.getInventory().firstEmpty();
                            if (!curItem.getType().equals(Material.AIR)) {
                                if (moveLoc != -1) {
                                    player.getInventory().setItem(moveLoc, curItem);
                                    player.getInventory().clear(player.getInventory().getHeldItemSlot());
                                } else {
                                    for (int i = 0; i < player.getInventory().getContents().length; i ++) {
                                        ItemStack is = player.getInventory().getContents()[i];
                                        if (!hasMaterial(tools, is.getType())) {
                                            moveLoc = i;
                                            break;
                                        }
                                    }
                                    
                                    if (moveLoc != -1) {
                                        ItemStack temp = player.getInventory().getItem(moveLoc);
                                        player.getInventory().setItem(moveLoc, curItem);
                                        player.setItemInHand(temp);
                                    }
                                }
                            }
                        }
                    } else {
                        if (checkFor(player, Material.GOLD_SPADE)) {}
                        else if (checkFor(player, Material.DIAMOND_SPADE)) {}
                        else if (checkFor(player, Material.IRON_SPADE)) {}
                        else if (checkFor(player, Material.STONE_SPADE)) {}
                        else if (checkFor(player, Material.WOOD_SPADE)) {}
                        else {
                            ItemStack curItem = player.getInventory().getItemInHand();
                            int moveLoc = player.getInventory().firstEmpty();
                            if (curItem.getType() != Material.AIR) {
                                if (moveLoc != -1) {
                                    player.getInventory().setItem(moveLoc, curItem);
                                    player.getInventory().clear(player.getInventory().getHeldItemSlot());
                                } else {
                                    for (int i = 0; i < player.getInventory().getContents().length; i ++) {
                                        ItemStack is = player.getInventory().getContents()[i];
                                        if (!hasMaterial(tools, is.getType())) {
                                            moveLoc = i;
                                            break;
                                        }
                                    }
                                    
                                    if (moveLoc != -1) {
                                        ItemStack temp = player.getInventory().getItem(moveLoc);
                                        player.getInventory().setItem(moveLoc, curItem);
                                        player.setItemInHand(temp);
                                    }
                                }
                            }
                        }
                    }
                }
                else if (hasMaterial(allAxeMaterials, material)) {
                    if (efficient) {
                        if (checkFor(player, Material.WOOD_AXE)) {}
                        else if (checkFor(player, Material.STONE_AXE)) {}
                        else if (checkFor(player, Material.IRON_AXE)) {}
                        else if (checkFor(player, Material.DIAMOND_AXE)) {}
                        else if (checkFor(player, Material.GOLD_AXE)) {}
                        else {
                            ItemStack curItem = player.getInventory().getItemInHand();
                            int moveLoc = player.getInventory().firstEmpty();
                            if (!curItem.getType().equals(Material.AIR)) {
                                if (moveLoc != -1) {
                                    player.getInventory().setItem(moveLoc, curItem);
                                    player.getInventory().clear(player.getInventory().getHeldItemSlot());
                                } else {
                                    for (int i = 0; i < player.getInventory().getContents().length; i ++) {
                                        ItemStack is = player.getInventory().getContents()[i];
                                        if (!hasMaterial(tools, is.getType())) {
                                            moveLoc = i;
                                            break;
                                        }
                                    }
                                    
                                    if (moveLoc != -1) {
                                        ItemStack temp = player.getInventory().getItem(moveLoc);
                                        player.getInventory().setItem(moveLoc, curItem);
                                        player.setItemInHand(temp);
                                    }
                                }
                            }
                        }
                    }
                    else {
                        if (checkFor(player, Material.GOLD_AXE)) {}
                        else if (checkFor(player, Material.DIAMOND_AXE)) {}
                        else if (checkFor(player, Material.IRON_AXE)) {}
                        else if (checkFor(player, Material.STONE_AXE)) {}
                        else if (checkFor(player, Material.WOOD_AXE)) {}
                        else {
                            ItemStack curItem = player.getInventory().getItemInHand();
                            int moveLoc = player.getInventory().firstEmpty();
                            if (!curItem.getType().equals(Material.AIR)) {
                                if (moveLoc != -1) {
                                    player.getInventory().setItem(moveLoc, curItem);
                                    player.getInventory().clear(player.getInventory().getHeldItemSlot());
                                } else {
                                    for (int i = 0; i < player.getInventory().getContents().length; i ++)  {
                                        ItemStack is = player.getInventory().getContents()[i];
                                        if (!hasMaterial(tools, is.getType())) {
                                            moveLoc = i;
                                            break;
                                        }
                                    }
                                    
                                    if (moveLoc != -1) {
                                        ItemStack temp = player.getInventory().getItem(moveLoc);
                                        player.getInventory().setItem(moveLoc, curItem);
                                        player.setItemInHand(temp);
                                    }
                                }
                            }
                        }
                    }
                }
                else if (hasMaterial(allShearsMaterials, material)) {
                    if (checkFor(player, Material.SHEARS)) {}
                    else {
                        ItemStack curItem = player.getInventory().getItemInHand();
                        int moveLoc = player.getInventory().firstEmpty();
                        if (!curItem.getType().equals(Material.AIR)) {
                            if (moveLoc != -1) {
                                player.getInventory().setItem(moveLoc, curItem);
                                player.getInventory().clear(player.getInventory().getHeldItemSlot());
                            } else {
                                for (int i = 0; i < player.getInventory().getContents().length; i ++) {
                                    ItemStack is = player.getInventory().getContents()[i];
                                    if (!hasMaterial(tools, is.getType())) {
                                        moveLoc = i;
                                        break;
                                    }
                                }
                                
                                if (moveLoc != -1) {
                                    ItemStack temp = player.getInventory().getItem(moveLoc);
                                    player.getInventory().setItem(moveLoc, curItem);
                                    player.setItemInHand(temp);
                                }
                            }
                        }
                    }
                }
                else if (!material.equals(Material.AIR)) {
                    ItemStack curItem = player.getInventory().getItemInHand();
                    int moveLoc = player.getInventory().firstEmpty();
                    if (!curItem.getType().equals(Material.AIR)) {
                        if (moveLoc != -1) {
                            player.getInventory().setItem(moveLoc, curItem);
                            player.getInventory().clear(player.getInventory().getHeldItemSlot());
                        } else  {
                            for (int i = 0; i < player.getInventory().getContents().length; i ++) {
                                ItemStack is = player.getInventory().getContents()[i];
                                if (!hasMaterial(tools, is.getType())) {
                                    moveLoc = i;
                                    break;
                                }
                            }
                            
                            if (moveLoc != -1) {
                                ItemStack temp = player.getInventory().getItem(moveLoc);
                                player.getInventory().setItem(moveLoc, curItem);
                                player.setItemInHand(temp);
                            }
                        }
                    }
                }
            }
        }
    }
    
    public static void load(Player player) {
        if (!EfficiencyExpert.dataFolder.exists())
            EfficiencyExpert.dataFolder.mkdirs();
        File f = new File(EfficiencyExpert.dataFolder, player.getName() + ".dat");
        
        if (!f.exists()) {
            if (!playerStats.containsKey(player)) {
                boolean[] bools = {false, true};
                playerStats.put(player, bools);
            }
        } else {
            FileInputStream in;
            try {
                in = new FileInputStream(f);
                prop.load(in);
            } catch (IOException ex) {
                Logger.getLogger(EfficiencyExpertPlayerListener.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            boolean[] bools = {Boolean.parseBoolean(prop.getProperty("Active")), Boolean.parseBoolean(prop.getProperty("Efficient"))};
            
            if (!playerStats.containsKey(player)) {
                playerStats.put(player, bools);
            } else {
                playerStats.get(player)[0] = bools[0];
                playerStats.get(player)[1] = bools[1];
            }
        }
    }
    
    public static void save(Player player) {
        if (!EfficiencyExpert.dataFolder.exists())
            (EfficiencyExpert.dataFolder).mkdirs();
        File f = new File(EfficiencyExpert.dataFolder, player.getName() + ".dat");
        try {
            boolean active = playerStats.get(player)[0];
            boolean efficient = playerStats.get(player)[1];
            FileOutputStream out;
            out = new FileOutputStream(f);
            prop.put("Efficient", Boolean.toString(efficient));
            prop.put("Active", Boolean.toString(active));
            prop.store(out, "Do NOT edit this config!");
            out.flush();
            out.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(EfficiencyExpertPlayerListener.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(EfficiencyExpertPlayerListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void saveAndExit(Player player) {
        save(player);
        playerStats.remove(player);
    }
    
    public static void setActive(Player player, boolean b) {
        playerStats.get(player)[0] = b;
    }
    
    public static void setEfficient(Player player, boolean b) {
        playerStats.get(player)[1] = b;
    }
    
    public static void status(Player player) {
        boolean active = playerStats.get(player)[0];
        
        player.sendMessage("EfficiencyExpert Active: " + active);
        
        if (playerStats.get(player)[1] == true)
            player.sendMessage("EfficiencyExpert State: efficient");
        else
            player.sendMessage("EfficiencyExpert State: fast");
    }
}