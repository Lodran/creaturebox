package lodran.creaturebox;

import java.io.File;
import java.util.HashMap;
import org.bukkit.entity.Player;
import org.bukkit.Server;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.Plugin;
import org.bukkit.entity.CreatureType;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.ChatColor;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

/**
 * creaturebox for Bukkit
 *
 * @author lodran
 */
public class creaturebox extends JavaPlugin {
  private final creatureboxPlayerListener playerListener = new creatureboxPlayerListener(this);
  private final creatureboxBlockListener blockListener = new creatureboxBlockListener(this);
  private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
  
  public final CreatureType creatureTypes[] =
  {
  org.bukkit.entity.CreatureType.PIG,
  org.bukkit.entity.CreatureType.CHICKEN,
  org.bukkit.entity.CreatureType.COW,
  org.bukkit.entity.CreatureType.SHEEP,
  org.bukkit.entity.CreatureType.SQUID,
  org.bukkit.entity.CreatureType.CREEPER,
  org.bukkit.entity.CreatureType.GHAST,
  org.bukkit.entity.CreatureType.PIG_ZOMBIE,
  org.bukkit.entity.CreatureType.SKELETON,
  org.bukkit.entity.CreatureType.SPIDER,
  org.bukkit.entity.CreatureType.ZOMBIE,
  org.bukkit.entity.CreatureType.SLIME
  };
    
  public final HashMap<Player, Location> spawnerLocations = new HashMap<Player, Location>();
  
  public static PermissionHandler permissions = null;
  
  public creaturebox()
  {
    super();
    
    // TODO: Place any custom construction code here
  }
  
  /*
   * Not sure why, but initialize is marked as final, which doesn't give a particularly good place to initialize our plugin.

  public void initialize (PluginLoader loader, Server server, PluginDescriptionFile description, File dataFolder, File file, ClassLoader classLoader)
  {
    super.initialize(loader, server, description, dataFolder, file, classLoader);

    // TODO: Place any custom initialization code here
    
    // NOTE: Event registration should be done in onEnable not here as all events are unregistered when a plugin is disabled
  }
   */
  
  
  
  public void onEnable()
  {
    // TODO: Place any custom enable code here including the registration of any events
    
    this.attachPermissions();
        
    this.blockListener.onEnable();
    
    // Register our events
    // PluginManager pm = getServer().getPluginManager();
    
    // EXAMPLE: Custom code, here we just output some info so we can check all is well
    PluginDescriptionFile pdfFile = this.getDescription();
    System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
  }
  
  public void onDisable()
  {
    // TODO: Place any custom disable code here
    
    // NOTE: All registered events are automatically unregistered when a plugin is disabled
    
    // EXAMPLE: Custom code, here we just output some info so we can check all is well
    System.out.println("Goodbye world!");
  }
  
  public boolean isDebugging(final Player player)
  {
    if (debugees.containsKey(player))
    {
      return debugees.get(player);
    }
    else
    {
      return false;
    }
  }
  
  public void setDebugging(final Player player, final boolean value)
  {
    debugees.put(player, value);
  }
  
  public boolean onCommand(CommandSender inSender,
                           Command inCommand,
                           String inCommandLabel,
                           String[] inArguments)
  {      
    if (!(inSender instanceof Player))
    {
      System.out.println("creaturebox commands require a player");
      return false;
    }
    
    Player thePlayer = (Player) inSender;
    
    if (creaturebox.permissions != null)
    {
      if (creaturebox.permissions.has(thePlayer, "creaturebox.changespawner") == false)
      {
        thePlayer.sendMessage(ChatColor.RED + "creaturebox: You don't have permission (creaturebox.changespawner)");
        return false;
      }
    }
    else
    {
      if (thePlayer.isOp() == false)
      {
        thePlayer.sendMessage(ChatColor.RED + "creaturebox: You aren't an operator.");
        return false;
      }
    }

    if (inArguments.length != 1)
    {
      this.showUsage(thePlayer);
      return false;
    }
    
    int theCreatureIndex = creatureIndex(inArguments[0]);
    
    if (theCreatureIndex < 0)
    {
      this.showUsage(thePlayer);
      return false;
    }
      
    CreatureType theCreatureType = creatureTypes[theCreatureIndex];
    
    if (!(spawnerLocations.containsKey(thePlayer)))
    {
      this.showUsage(thePlayer);
      return false;
    }
    
    Location theLocation = spawnerLocations.get(thePlayer);
    
    Block theBlock = theLocation.getBlock();
    
    if (theBlock.getType() != Material.MOB_SPAWNER)
    {
      thePlayer.sendMessage(ChatColor.RED + "creaturebox: The selected block is not a mob spawner.");
      return false;
    }

    CreatureSpawner theSpawner = (CreatureSpawner) theBlock.getState();
    
    theSpawner.setCreatureType(theCreatureType);
        
    thePlayer.sendMessage(String.format(ChatColor.YELLOW + "creaturebox: %s spawner created.", theSpawner.getCreatureTypeId()));
    
    return true;
  }
  
  public void showUsage(Player inPlayer)
  {
    inPlayer.sendMessage("creaturebox usage: right click a mob spawner, then type:");
    inPlayer.sendMessage("/creaturebox creaturename");
    
    String theCreatureNames = "  valid creature names: ";
    
    for (int theIndex = 0; theIndex < creatureTypes.length; theIndex++)
    {
      if (theIndex > 0)
        theCreatureNames = theCreatureNames.concat(", ");
      
      String theCreatureType = creatureTypes[theIndex].toString().toLowerCase();
      
      if (theCreatureNames.length() + theCreatureType.length() >= 62)
      {
        inPlayer.sendMessage(theCreatureNames);
        theCreatureNames = "  ";
      }
            
      theCreatureNames = theCreatureNames.concat(theCreatureType);
    }
    
    inPlayer.sendMessage(theCreatureNames);
  }
  
  public int creatureIndex(String inCreatureName)
  {
    for (int theIndex = 0; theIndex < creatureTypes.length; theIndex++)
    {
      if (creatureTypes[theIndex].toString().equalsIgnoreCase(inCreatureName))
        return theIndex;
    }
    
    return -1;
  }
  
  public void attachPermissions()
  {
    if (this.permissions == null)
    {
      Plugin thePermissionsPlugin = this.getServer().getPluginManager().getPlugin("Permissions");
      if (thePermissionsPlugin != null)
      {
        this.getServer().getPluginManager().enablePlugin(thePermissionsPlugin);
        this.permissions = ((Permissions) thePermissionsPlugin).getHandler();
      }
    }
  }
  
}

