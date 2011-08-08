package mathmaniac43.efficiencyexpert;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class EfficiencyExpert extends JavaPlugin
{
	private final EfficiencyExpertPlayerListener playerListener = new EfficiencyExpertPlayerListener(this);
	private Logger log;
	
	public void onEnable()
	{
		log = Logger.getLogger("Minecraft");
		log.info("mathmaniac43's EfficiencyExpert Version 1.0 enabled");
		
		List<Player> allPlayers = new ArrayList<Player>();
		for(World world : getServer().getWorlds())
		{
			for(Player p : world.getPlayers())
			{
				allPlayers.add(p);
			}
		}
		
		EfficiencyExpertPlayerListener.startup(allPlayers);
		
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_ANIMATION, playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Event.Priority.Normal, this);
	}
	
	public void onDisable()
	{
		log.info("mathmaniac43's EfficiencyExpert Version 1.0 disabled");
		EfficiencyExpertPlayerListener.spindown();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		if(sender.hasPermission("efficiencyexpert.*"))
		{
			if(cmd.getName().equalsIgnoreCase("ee.on") || cmd.getName().equalsIgnoreCase("ee.activate"))
			{
				EfficiencyExpertPlayerListener.setActive((Player)sender, true);
				EfficiencyExpertPlayerListener.status(((Player)sender));
				return true;
			}
			else if(cmd.getName().equalsIgnoreCase("ee.off") || cmd.getName().equalsIgnoreCase("ee.deactivate"))
			{
				EfficiencyExpertPlayerListener.setActive((Player)sender, false);
				EfficiencyExpertPlayerListener.status(((Player)sender));
				return true;
			}
			else if(cmd.getName().equalsIgnoreCase("ee.efficient"))
			{
				EfficiencyExpertPlayerListener.setEfficient((Player)sender, true);
				EfficiencyExpertPlayerListener.status(((Player)sender));
				return true;
			}
			else if(cmd.getName().equalsIgnoreCase("ee.fast"))
			{
				EfficiencyExpertPlayerListener.setEfficient((Player)sender, false);
				EfficiencyExpertPlayerListener.status(((Player)sender));
				return true;
			}
			else if(cmd.getName().equalsIgnoreCase("ee.status"))
			{
				EfficiencyExpertPlayerListener.status(((Player)sender));
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			sender.sendMessage("You do not have permission to use EfficiencyExpert!");
			EfficiencyExpertPlayerListener.setActive(((Player)sender), false);
			return false;
		}
	}
}