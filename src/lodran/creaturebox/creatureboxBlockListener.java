package lodran.creaturebox;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.BlockFace;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockRightClickEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.World;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.event.Event;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

/**
 * creaturebox block listener
 * @author lodran
 */
public class creatureboxBlockListener extends BlockListener
{
  private final creaturebox plugin;
  
  public creatureboxBlockListener(final creaturebox plugin)
  {
    this.plugin = plugin;
  }
  
  public void onEnable()
  {
    PluginManager theManager = plugin.getServer().getPluginManager();
    
    theManager.registerEvent(Event.Type.BLOCK_RIGHTCLICKED, this, Event.Priority.Normal, plugin);
    // theManager.registerEvent(Event.Type.BLOCK_DAMAGED, this, Event.Priority.Normal, plugin);
    theManager.registerEvent(Event.Type.BLOCK_BREAK, this, Event.Priority.Normal, plugin);
    theManager.registerEvent(Event.Type.BLOCK_PLACED, this, Event.Priority.Normal, plugin);
  }
  
  @Override public void onBlockRightClick(BlockRightClickEvent inEvent)
  {
    Player thePlayer = inEvent.getPlayer();
    Block theBlock = inEvent.getBlock();
    
    if (theBlock.getType() == Material.MOB_SPAWNER)
    {
      BlockState theBlockState = theBlock.getState();
      
      if (theBlockState instanceof CreatureSpawner)
      {
        if (creaturebox.permissions != null)
        {
          if (creaturebox.permissions.has(thePlayer, "creaturebox.changespawner") == false)
          {
            // System.out.println("creaturebox: You don't have permission (creaturebox.changespawner)");
            return;
          }
        }
        else
        {
          if (thePlayer.isOp() == false)
          {
            return;
          }
        }
        
        CreatureSpawner theSpawner = (CreatureSpawner) theBlockState;
        
        World theWorld = theBlock.getWorld();
        Location theLocation = new Location(theWorld, theBlock.getX(), theBlock.getY(), theBlock.getZ(), 0, 0);
                
        thePlayer.sendMessage(String.format(ChatColor.YELLOW + "creaturebox: %s spawner selected.", theSpawner.getCreatureTypeId()));
        
        plugin.spawnerLocations.put(thePlayer, theLocation);
      }
    }
  }
  
  @Override public void onBlockBreak(BlockBreakEvent inEvent)
  {
    Player thePlayer = inEvent.getPlayer();
    Block theBlock = inEvent.getBlock();
    
    if ((theBlock.getType() != Material.MOB_SPAWNER) ||
      (inEvent.isCancelled()))
      return;
    
    if (creaturebox.permissions != null)
    {
      if (creaturebox.permissions.has(thePlayer, "creaturebox.dropspawner") == false)
      {
        return;
      }
    }
    else
    {
      if (thePlayer.isOp() == false)
      {
        return;
      }
    }
    
    World theWorld = theBlock.getWorld();
    Location theLocation = new Location(theWorld, theBlock.getX(), theBlock.getY(), theBlock.getZ(), 0, 0);
    CreatureSpawner theSpawner = (CreatureSpawner) theBlock.getState();
    int theCreatureIndex = plugin.creatureIndex(theSpawner.getCreatureTypeId());

    MaterialData theMaterial = new MaterialData(Material.MOB_SPAWNER, (byte) theCreatureIndex);
    ItemStack theItem = new ItemStack(Material.MOB_SPAWNER, 1, (short) theCreatureIndex);
        
    theWorld.dropItemNaturally(theLocation, theItem);
    
    thePlayer.sendMessage(String.format(ChatColor.YELLOW + "creaturebox: %s spawner dropped.", theSpawner.getCreatureTypeId()));
  }
  
  @Override public void onBlockPlace(BlockPlaceEvent inEvent)
  {
    Player thePlayer = inEvent.getPlayer();
    
    Block theBlock = inEvent.getBlock();
    
    if ((theBlock.getType() != Material.MOB_SPAWNER) ||
        (inEvent.isCancelled()))
      return;
    
    if (creaturebox.permissions != null)
    {
      if (creaturebox.permissions.has(thePlayer, "creaturebox.placespawner") == false)
      {
        return;
      }
    }
        
    ItemStack theItem = inEvent.getItemInHand();
    int theCreatureIndex = inEvent.getItemInHand().getDurability();
    
    if (theCreatureIndex >= plugin.creatureTypes.length)
      theCreatureIndex = 0;
    
    CreatureSpawner theSpawner = (CreatureSpawner) theBlock.getState();
    theSpawner.setCreatureType(plugin.creatureTypes[theCreatureIndex]);
    
    thePlayer.sendMessage(String.format(ChatColor.YELLOW + "creaturebox: %s spawner placed.", theSpawner.getCreatureTypeId()));
  }
  
  //put all Block related code here
}
