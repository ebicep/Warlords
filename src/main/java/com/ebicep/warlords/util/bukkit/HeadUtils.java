package com.ebicep.warlords.util.bukkit;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HeadUtils {


    public static final ConcurrentHashMap<UUID, net.minecraft.world.item.ItemStack> PLAYER_HEADS = new ConcurrentHashMap<>();
//    private static Field metaProfileField;

    public static void updateHeads() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            updateHead(onlinePlayer);
        }
        ChatUtils.MessageTypes.WARLORDS.sendMessage("Heads updated");
    }

    public static void updateHead(Player player) {
        new BukkitRunnable() {

            @Override
            public void run() {
                ItemStack playerSkull = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta skullMeta = (SkullMeta) playerSkull.getItemMeta();
                skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()));
                playerSkull.setItemMeta(skullMeta);
                PLAYER_HEADS.put(player.getUniqueId(), CraftItemStack.asNMSCopy(playerSkull));
            }
        }.runTask(Warlords.getInstance());
    }

    public static ItemStack getHead(Player player) {
        return getHead(player.getUniqueId());
    }

    public static ItemStack getHead(UUID uuid) {
        if (PLAYER_HEADS.containsKey(uuid)) {
            return CraftItemStack.asBukkitCopy(PLAYER_HEADS.get(uuid));
        }
        ItemStack playerSkull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) playerSkull.getItemMeta();
        skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
        playerSkull.setItemMeta(skullMeta);
        PLAYER_HEADS.put(uuid, CraftItemStack.asNMSCopy(playerSkull));
        return playerSkull;
    }
/*
    public static ItemStack getHead(UUID uuid) {
        ItemStack playerSkull = new ItemStack(Material.PLAYER_HEAD);


        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(uuid);
        if (databasePlayer == null) {
            return playerSkull;
        }

        SkullMeta skullMeta = (SkullMeta) playerSkull.getItemMeta();
        try {
            if (metaProfileField == null) {
                metaProfileField = skullMeta.getClass().getDeclaredField("profile");
                metaProfileField.setAccessible(true);
            }
            if (databasePlayer.getSkinBase64() == null || databasePlayer.getSkinBase64().isEmpty()) {
                Player player = Bukkit.getPlayer(uuid);
                if (player == null) {
                    return playerSkull;
                }
                databasePlayer.setSkinBase64(getPlayerTextureAndSignature(player)[0]);
                DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
            }
            metaProfileField.set(skullMeta, createProfileFromBase64(databasePlayer.getSkinBase64()));

        } catch (NoSuchFieldException | IllegalAccessException ex2) {
            ex2.printStackTrace();
        }
        playerSkull.setItemMeta(skullMeta);
        return playerSkull;
    }


    private static GameProfile createProfileFromBase64(String base64) {
        UUID id = new UUID(base64.substring(base64.length() - 20).hashCode(), base64.substring(base64.length() - 10).hashCode());
        GameProfile profile = new GameProfile(id, "Player");
        profile.getProperties().put("textures", new Property("textures", base64));
        return profile;
    }

    public static String[] getPlayerTextureAndSignature(Player player) {
        EntityPlayer playerNMS = ((CraftPlayer) player).getHandle();
        GameProfile profile = playerNMS.getProfile();
        Property property = profile.getProperties().get("textures").iterator().next();
        String texture = property.getValue();
        String signature = property.getSignature();
        return new String[]{texture, signature};
    }

    public static String getBase64FromUUID(UUID uuid) {
        String url = "https://sessionserver.mojang.com/session/minecraft/profile/" + uuid;
        try {
            String nameJson = IOUtils.toString(new URL(url));
            JSONObject nameValue = (JSONObject) JSONValue.parseWithException(nameJson);
            JSONArray properties = (JSONArray) nameValue.get("properties");
            JSONObject property = (JSONObject) properties.get(0);
            return (String) property.get("value");
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        //steve is the default
        return "ewogICJ0aW1lc3RhbXAiIDogMTY0MzE3MDI3Mjg4NywKICAicHJvZmlsZUlkIiA6ICIzZmM3ZmRmOTM5NjM0YzQxOTExOTliYTNmN2NjM2ZlZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJZZWxlaGEiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTQ5MWUyZDMwNzFmNmYxNGQ5MTY3OGU4YTRjZWE2ZGIyMzUxMDI4MTVjNmZmM2QxOWIwYmI5ZTE2ZjlhYjUyZCIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9";
    }

 */
}
