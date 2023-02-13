package com.ebicep.warlords.util.pve;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class SkullUtils {

    public static ItemStack getSkullFrom(SkullID skullID) {
        net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(new ItemStack(Material.PLAYER_HEAD));
//
//        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
//        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
//        PlayerProfile playerProfile = skullMeta.getPlayerProfile();
//        playerProfile.setTextures(skullID.getTextureId());
//        CompoundTag compound = nmsStack.getTag();
//        if (compound == null) {
//            compound = new CompoundTag();
//            nmsStack.setTag(compound);
//            compound = nmsStack.getTag();
//        }
//
//
//
//        CompoundTag skullOwner = new CompoundTag();
//        skullOwner.set("Id", new StringTag(skullID.getId()));
//        CompoundTag properties = new CompoundTag();
//        ListTag textures = new ListTag();
//        CompoundTag value = new CompoundTag();
//        value.set("Value", new StringTag(skullID.getTextureId()));
//        textures.add(value);
//        properties.set("textures", textures);
//        skullOwner.set("Properties", properties);
//
//        compound.set("SkullOwner", skullOwner);
//        nmsStack.setTag(compound);

        return CraftItemStack.asBukkitCopy(nmsStack);
    }

    public static ItemStack getPlayerSkull(String playerName) {
        ItemStack playerSkull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) playerSkull.getItemMeta();
        skullMeta.setOwner(playerName);
        playerSkull.setItemMeta(skullMeta);
        return playerSkull;
    }

}
