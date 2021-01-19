package me.ponktacology.notchdisguise.util;

import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class NMSUtil {
  public static void sendPacket(Player except, Packet<?>... packets) {
    for (Player player : Bukkit.getServer().getOnlinePlayers()) {
      if (player.equals(except)) continue;
      for (Packet<?> packet : packets) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
      }
    }
  }
}
