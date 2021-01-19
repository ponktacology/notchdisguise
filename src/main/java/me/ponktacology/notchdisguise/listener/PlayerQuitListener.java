package me.ponktacology.notchdisguise.listener;

import me.ponktacology.notchdisguise.disguise.DisguiseHelper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    private final DisguiseHelper disguiseHelper;

    public PlayerQuitListener(DisguiseHelper disguiseHelper) {
        this.disguiseHelper = disguiseHelper;
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        disguiseHelper.removeFromCache(event.getPlayer());
    }
}
