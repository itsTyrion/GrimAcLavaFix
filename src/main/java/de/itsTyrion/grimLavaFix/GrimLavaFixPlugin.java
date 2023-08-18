package de.itsTyrion.grimLavaFix;

import ac.grim.grimac.events.CompletePredictionEvent;
import ac.grim.grimac.events.FlagEvent;
import ac.grim.grimac.player.GrimPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class GrimLavaFixPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onGrimFlag(FlagEvent event) {
        // occasional false flags when players swim up lava -> cascade of setbacks -> player gets dragged down
        if (event.getCheck().getCheckName().equals("AntiKB")) {
            GrimPlayer gp = ((GrimPlayer) event.getPlayer());

            if (gp.wasTouchingLava && gp.likelyKB != null && gp.likelyKB.offset < 0.02) {
                event.setCancelled(true);
            }
            // there sometimes is a single false flag for this check when exiting lava onto a block at equal Y level.
        } else if (event.getCheck().getCheckName().equals("GroundSpoof")) {
            GrimPlayer gp = ((GrimPlayer) event.getPlayer());

            if (gp.wasTouchingLava && gp.bukkitPlayer.getEyeLocation().getBlock().getType() == Material.AIR) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onGrimPredictComplete(CompletePredictionEvent event) {
        // this bug is probably similar to the one in the kb check - well, the values are close at least.
        if (event.getOffset() > 1.0E-5 && event.getOffset() < 0.02) {
            if (((GrimPlayer) event.getPlayer()).wasTouchingLava)
                event.setCancelled(true);
        }
    }
}
