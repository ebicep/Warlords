package com.ebicep.warlords.util.pve;

import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;
import net.minecraft.server.v1_8_R3.NBTTagString;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class SkullUtils {

    public static ItemStack getSkullFrom(SkullID skullID) {
        net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(
                new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal())
        );

        NBTTagCompound compound = nmsStack.getTag();
        if (compound == null) {
            compound = new NBTTagCompound();
            nmsStack.setTag(compound);
            compound = nmsStack.getTag();
        }

        NBTTagCompound skullOwner = new NBTTagCompound();
        skullOwner.set("Id", new NBTTagString(skullID.getId()));
        NBTTagCompound properties = new NBTTagCompound();
        NBTTagList textures = new NBTTagList();
        NBTTagCompound value = new NBTTagCompound();
        value.set("Value", new NBTTagString(skullID.getTextureId()));
        textures.add(value);
        properties.set("textures", textures);
        skullOwner.set("Properties", properties);

        compound.set("SkullOwner", skullOwner);
        nmsStack.setTag(compound);

        return CraftItemStack.asBukkitCopy(nmsStack);
    }

    public static ItemStack getPlayerSkull(String playerName) {
        ItemStack playerSkull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
        SkullMeta skullMeta = (SkullMeta) playerSkull.getItemMeta();
        skullMeta.setOwner(playerName);
        playerSkull.setItemMeta(skullMeta);
        return playerSkull;
    }

    public static ItemStack getMobSkull(SkullType type) {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) type.ordinal());
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        skull.setItemMeta(skullMeta);
        return skull;
    }
}
