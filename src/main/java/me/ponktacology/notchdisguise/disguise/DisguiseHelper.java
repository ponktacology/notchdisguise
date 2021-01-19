package me.ponktacology.notchdisguise.disguise;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.ponktacology.notchdisguise.NotchDisguisePlugin;
import me.ponktacology.notchdisguise.reflection.FieldAccessor;
import me.ponktacology.notchdisguise.reflection.Reflection;
import me.ponktacology.notchdisguise.util.NMSUtil;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DisguiseHelper {

  private static final String DISGUISED_NAME = "Notch";
  private static final String DISGUISED_SKIN_VALUE =
      "ewogICJ0aW1lc3RhbXAiIDogMTYxMTA4MjIzMzAyMSwKICAicHJvZmlsZUlkIiA6ICIwNjlhNzlmNDQ0ZTk0NzI2YTViZWZjYTkwZTM4YWFmNSIsCiAgInByb2ZpbGVOYW1lIiA6ICJOb3RjaCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8yOTIwMDlhNDkyNWI1OGYwMmM3N2RhZGMzZWNlZjA3ZWE0Yzc0NzJmNjRlMGZkYzMyY2U1NTIyNDg5MzYyNjgwIgogICAgfQogIH0KfQ==";
  private static final String DISGUISED_SKIN_SIGNATURE =
      "hctAR8Nd3h4CmMOog1kSzgJ8yhkc/u5MgRd72gU7xZzIgIkVxWfdkpI+cSwmRW60hsrs7iJUoj9KPHdYgS5aOUuZ+l/zUg9s/i2sR/xDKi3BjiUhDA+9MuLKLKcLXFN7lUjUHvKQlilS53Exh4w6DQbJP+uru3nThB2XfJHyziRp8Zymyy6C+zcQZMA92jo+WW8lZp3fPrNZj0o7G6bdhu+0Ai8gBxqKb5wgMJdsTr7zcopr1G/yVyLYnC7FCLLtGSQwe45ltt+Hi+kESSagTiQePHSbLojDnV2hPO85PvrU2hTiwgIHH+HuW96BhYOlq/nI9a+96D2E55f5h49zSDU84LPKuuKrjBDXxrvag9BXa4N8ESNktCUzbqhjAUqvCIcQcdImFJ3/UNRyyx1KELeybunDBJbRGkfNmu8yWczo2BurSC50wGnXZqwgwNAQLVBzntiR+kx5ZUXeRSL3LrSwQJanTEm5AH9bmGro5A0gdQiY3c2noGWmUBBFqYc3IP2AmhGR9ssNqH7LW2/u3HBsi5Ti3y62PqApPCuMaPCfONcMDM+s9X+5zW7SDXzILQ0G6QjO/KYP8kBnA+R3o/W77y/SERr7HuOyW5hlFuwxEbaKp3KS5CmCpbhfdJ9bHPE+Q8ErkQpI48wyJa7tmUihJPw6bp5CBAu1VDnGHfw=";

  private static final Map<UUID, GameProfile> disguisedPlayers = new HashMap<>();

  private final NotchDisguisePlugin plugin;

  public DisguiseHelper(NotchDisguisePlugin plugin) {
    this.plugin = plugin;
  }

  private void cache(CraftPlayer craftPlayer) {
    disguisedPlayers.put(craftPlayer.getUniqueId(), craftPlayer.getProfile());
  }

  public void removeFromCache(Player player) {
    disguisedPlayers.remove(player.getUniqueId());
  }

  public boolean isDisguised(Player player) {
    return disguisedPlayers.containsKey(player.getUniqueId());
  }

  public void setDisguised(Player player, boolean disguised) {
    if (disguised) {
      applyDisguise(player);
    } else {
      revertDisguise(player);
    }
  }

  private void setGameProfile(CraftPlayer craftPlayer, GameProfile gameProfile) {
    EntityPlayer entityPlayer = craftPlayer.getHandle();

    try {
      FieldAccessor<GameProfile> bH =
          Reflection.getField(EntityPlayer.class, "bH", GameProfile.class);

      bH.set(entityPlayer, gameProfile);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void refreshPlayer(Player player, CraftPlayer craftPlayer, EntityPlayer entityPlayer) {
    PacketPlayOutPlayerInfo packetPlayOutPlayerInfo =
        new PacketPlayOutPlayerInfo(
            PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entityPlayer);
    PacketPlayOutEntityDestroy packetPlayOutEntityDestroy =
        new PacketPlayOutEntityDestroy(craftPlayer.getEntityId());

    // Removing player to refresh skin and name
    NMSUtil.sendPacket(null, packetPlayOutPlayerInfo, packetPlayOutEntityDestroy);

    PacketPlayOutPlayerInfo packetPlayOutPlayerInfo1 =
        new PacketPlayOutPlayerInfo(
            PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, craftPlayer.getHandle());

    // Adding player back after 50ms
    new BukkitRunnable() {
      @Override
      public void run() {
        NMSUtil.sendPacket(null, packetPlayOutPlayerInfo1);
        NMSUtil.sendPacket(player, new PacketPlayOutNamedEntitySpawn(craftPlayer.getHandle()));
      }
    }.runTaskLaterAsynchronously(plugin, 1L);
  }

  private void applyDisguise(Player player) {
    CraftPlayer craftPlayer = (CraftPlayer) player;

    // Caching original game profile
    cache(craftPlayer);

    EntityPlayer entityPlayer = craftPlayer.getHandle();

    GameProfile disguiseGameProfile = new GameProfile(player.getUniqueId(), DISGUISED_NAME);

    disguiseGameProfile
        .getProperties()
        .put("textures", new Property("textures", DISGUISED_SKIN_VALUE, DISGUISED_SKIN_SIGNATURE));

    setGameProfile(craftPlayer, disguiseGameProfile);
    refreshPlayer(player, craftPlayer, entityPlayer);
  }

  private void revertDisguise(Player player) {
    if (!disguisedPlayers.containsKey(player.getUniqueId())) {
      System.out.println("Couldn't find cache of player's game profile, not reverting.");
      return;
    }

    CraftPlayer craftPlayer = (CraftPlayer) player;
    EntityPlayer entityPlayer = craftPlayer.getHandle();
    GameProfile cachedGameProfile = disguisedPlayers.remove(player.getUniqueId());

    setGameProfile(craftPlayer, cachedGameProfile);
    refreshPlayer(player, craftPlayer, entityPlayer);
  }
}
