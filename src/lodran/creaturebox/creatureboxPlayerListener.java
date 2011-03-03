package lodran.creaturebox;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Handle events for all Player related events
 * @author lodran
 */
public class creatureboxPlayerListener extends PlayerListener {
    private final creaturebox plugin;

    public creatureboxPlayerListener(creaturebox instance) {
        plugin = instance;
    }

    //Insert Player related code here
}

