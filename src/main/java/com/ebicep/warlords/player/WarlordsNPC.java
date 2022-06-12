package com.ebicep.warlords.player;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.state.PlayingState;
import com.ebicep.warlords.util.warlords.GameRunnable;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class WarlordsNPC extends WarlordsEntity {

    public WarlordsNPC(UUID uuid, String name, Weapons weapon, LivingEntity entity, PlayingState gameState, Team team, Specializations specClass) {
        super(uuid, name, weapon, entity, gameState, team, specClass);
        updateEntity();
        entity.setMetadata("WARLORDS_PLAYER", new FixedMetadataValue(Warlords.getInstance(), this));
    }
    @Override
    public void updateHealth() {
        if (isDead()) {
            new GameRunnable(game) {
                @Override
                public void run() {
                    game.removePlayer(uuid);
                }
            }.runTask();
        } else {
            String oldName = getEntity().getCustomName();
            String newName = oldName.substring(0, oldName.lastIndexOf(' ') + 1) + ChatColor.RED + getHealth() + "❤";
            getEntity().setCustomName(newName);
        }
    }
    
    @Override
    public void updateEntity() {
        entity.setCustomName(this.getSpec().getClassNameShortWithBrackets() + " " + this.getColoredName() + " " + ChatColor.RED + this.getHealth() + "❤"); // TODO add level and class into the name of this jimmy
        entity.setMetadata("WARLORDS_PLAYER", new FixedMetadataValue(Warlords.getInstance(), this));
        ((EntityLiving) ((CraftEntity) entity).getHandle()).getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(this.walkspeed);
    }

    @Override
    public boolean isOnline() {
        return true;
    }
    
    public static Zombie spawnZombie(@Nonnull Location loc, @Nullable EntityEquipment inv) {
        Zombie jimmy = loc.getWorld().spawn(loc, Zombie.class);
        jimmy.setBaby(false);
        jimmy.setCustomNameVisible(true);

        if (inv != null) {
            jimmy.getEquipment().setBoots(inv.getBoots());
            jimmy.getEquipment().setLeggings(inv.getLeggings());
            jimmy.getEquipment().setChestplate(inv.getChestplate());
            jimmy.getEquipment().setHelmet(inv.getHelmet());
            jimmy.getEquipment().setItemInHand(inv.getItemInHand());
        } else {
            jimmy.getEquipment().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
        }
        ((EntityLiving) ((CraftEntity) jimmy).getHandle()).getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0);
        ((EntityLiving) ((CraftEntity) jimmy).getHandle()).getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(0);
        //prevents jimmy from moving
        net.minecraft.server.v1_8_R3.Entity nmsEn = ((CraftEntity) jimmy).getHandle();
        NBTTagCompound compound = new NBTTagCompound();
        nmsEn.c(compound);
        compound.setByte("NoAI", (byte) 1);
        nmsEn.f(compound);
        return jimmy;
        
    }

}
