package me.mathmaniac43.efficiencyexpert;


import java.io.File;
import java.util.Arrays;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class EfficiencyExpert extends JavaPlugin {
    private final EfficiencyExpertPlayerListener playerListener = new EfficiencyExpertPlayerListener(this);
    private Logger log;
    
    public static File dataFolder;
    
    @Override
    public void onEnable() {
        dataFolder = this.getDataFolder();
        
        log = Logger.getLogger("EfficiencyExpert");
        log.info("mathmaniac43's EfficiencyExpert Version 1.0 enabled");
        
        List<Player> allPlayers = new ArrayList<Player>();
        allPlayers.addAll(Arrays.asList(getServer().getOnlinePlayers()));
        
        EfficiencyExpertPlayerListener.startUp(allPlayers);
        
        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_ANIMATION, playerListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Event.Priority.Normal, this);
    }
    
    @Override
    public void onDisable() {
        log.info("mathmaniac43's EfficiencyExpert Version 1.0 disabled");
        EfficiencyExpertPlayerListener.spinDown();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("EfficiencyExpert commands must be run by a player.");
            return false;
        }
        Player player = (Player) sender;
        
        if (player.hasPermission("efficiencyexpert.*")) {
            if (cmd.getName().equalsIgnoreCase("ee")) {
                if (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("activate")) {
                    EfficiencyExpertPlayerListener.setActive(player, true);
                    EfficiencyExpertPlayerListener.status(player);
                    return true;
                } else if (args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("deactivate")) {
                    EfficiencyExpertPlayerListener.setActive(player, false);
                    EfficiencyExpertPlayerListener.status(player);
                    return true;
                } else if (args[0].equalsIgnoreCase("efficient")) {
                    EfficiencyExpertPlayerListener.setEfficient(player, true);
                    EfficiencyExpertPlayerListener.status(player);
                    return true;
                } else if (args[0].equalsIgnoreCase("fast")) {
                    EfficiencyExpertPlayerListener.setEfficient(player, false);
                    EfficiencyExpertPlayerListener.status(player);
                    return true;
                } else if (args[0].equalsIgnoreCase("status")) {
                    EfficiencyExpertPlayerListener.status(player);
                    return true;
                }
            }
        } else {
            sender.sendMessage("You do not have permission to use EfficiencyExpert!");
            EfficiencyExpertPlayerListener.setActive(player, false);
            return false;
        }
        return false;
    }
}