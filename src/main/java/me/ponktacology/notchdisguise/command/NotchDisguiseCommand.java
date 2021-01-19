package me.ponktacology.notchdisguise.command;

import me.ponktacology.notchdisguise.NotchDisguisePlugin;
import me.ponktacology.notchdisguise.disguise.DisguiseHelper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NotchDisguiseCommand implements CommandExecutor {

  private final DisguiseHelper disguiseHelper;

  public NotchDisguiseCommand(DisguiseHelper disguiseHelper) {
    this.disguiseHelper = disguiseHelper;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
    if (!(sender instanceof Player)) {
      sender.sendMessage("Only usable in game.");
      return false;
    }

    Player player = (Player) sender;

    boolean disguised = !disguiseHelper.isDisguised(player);

    disguiseHelper.setDisguised(player, disguised);

    player.sendMessage("You are " + (disguised ? "now" : " no longer") + " disguised as a Notch.");
    return true;
  }
}
