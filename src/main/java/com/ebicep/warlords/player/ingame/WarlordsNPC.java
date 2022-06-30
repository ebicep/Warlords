package com.ebicep.warlords.player.ingame;

import com.ebicep.customentities.nms.pve.CustomEntity;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.util.warlords.GameRunnable;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class WarlordsNPC extends AbstractWarlordsEntity {

    public WarlordsNPC(UUID uuid, String name, Weapons weapon, LivingEntity entity, Game game, Team team, Specializations specClass) {
        super(uuid, name, weapon, entity, game, team, specClass);
        updateEntity();
        entity.setMetadata("WARLORDS_PLAYER", new FixedMetadataValue(Warlords.getInstance(), this));
        setSpawnGrave(false);
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
        entity.setCustomName(ChatColor.RED.toString() + this.getHealth() + "❤"); // TODO add level and class into the name of this jimmy
        entity.setMetadata("WARLORDS_PLAYER", new FixedMetadataValue(Warlords.getInstance(), this));
        ((EntityLiving) ((CraftEntity) entity).getHandle()).getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(this.walkspeed / 2.6);
        ((EntityLiving) ((CraftEntity) entity).getHandle()).getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(80);
    }

    @Override
    public boolean isOnline() {
        return true;
    }
    
    public static Zombie spawnZombieNoAI(@Nonnull Location loc, @Nullable EntityEquipment inv) {
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

    public static <T extends LivingEntity> T spawnEntity(@Nonnull Class<T> clazz, @Nonnull Location loc, @Nullable EntityEquipment inv) {
        T entity = loc.getWorld().spawn(loc, clazz);
        if (entity instanceof Zombie) {
            ((Zombie)entity).setBaby(false);
        }

        entity.setCustomNameVisible(true);

        if (inv != null) {
            entity.getEquipment().setBoots(inv.getBoots());
            entity.getEquipment().setLeggings(inv.getLeggings());
            entity.getEquipment().setChestplate(inv.getChestplate());
            entity.getEquipment().setHelmet(inv.getHelmet());
            entity.getEquipment().setItemInHand(inv.getItemInHand());
        } else {
            entity.getEquipment().setHelmet(new ItemStack(Material.BARRIER));
        }

        return entity;
    }

    public static <T extends CustomEntity> LivingEntity spawnCustomEntity(@Nonnull Class<T> clazz, @Nonnull Location loc, @Nullable EntityEquipment inv) {
        try {
            //TODO UGLY find another way, supplier?
            T customEntity = clazz.getConstructor(World.class).newInstance(((CraftWorld) loc.getWorld()).getHandle());
            customEntity.spawn(loc);

            EntityInsentient entityInsentient = customEntity.get();
            entityInsentient.persistent = true;

            LivingEntity entity = (LivingEntity) entityInsentient.getBukkitEntity();
            if (inv != null) {
                entity.getEquipment().setBoots(inv.getBoots());
                entity.getEquipment().setLeggings(inv.getLeggings());
                entity.getEquipment().setChestplate(inv.getChestplate());
                entity.getEquipment().setHelmet(inv.getHelmet());
                entity.getEquipment().setItemInHand(inv.getItemInHand());
            } else {
                entity.getEquipment().setHelmet(new ItemStack(Material.BARRIER));
            }


            return entity;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void die(@Nullable AbstractWarlordsEntity attacker) {
        super.die(attacker);

        //TODO Dropping weapons
        Location deathLocation = getLocation();


    }
}
