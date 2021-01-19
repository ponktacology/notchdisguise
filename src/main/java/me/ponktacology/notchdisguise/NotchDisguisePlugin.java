package me.ponktacology.notchdisguise;

import me.ponktacology.notchdisguise.command.NotchDisguiseCommand;
import me.ponktacology.notchdisguise.disguise.DisguiseHelper;
import me.ponktacology.notchdisguise.listener.PlayerQuitListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class NotchDisguisePlugin extends JavaPlugin {

  @Override
  public void onEnable() {
    DisguiseHelper disguiseHelper = new DisguiseHelper(this);

    getCommand("notchdisguise").setExecutor(new NotchDisguiseCommand(disguiseHelper));
    Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(disguiseHelper), this);
  }
}
