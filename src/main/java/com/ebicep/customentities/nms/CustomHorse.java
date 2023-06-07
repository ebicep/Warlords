package com.ebicep.customentities.nms;

import co.aikar.commands.CommandIssuer;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class CustomHorse {

    public static Set<UUID> record = new HashSet<>();
    private WarlordsEntity owner;
    private Horse horse;
    private int cooldown = 15;

    private float speed = .318f;
//    private boolean hasTurboSpeed = false;
//
//    private boolean damageOnDismount = true;
//    private float damageResistance = 1;
//
//    private boolean mountProtection = false;
//    private boolean hasMountProtection = false;
//
//    private boolean hasDivineMount = false;
//
//    private float health = 0;
//    private float maxHealth = 0;
//    private float shield = 0;

    public CustomHorse(World world, WarlordsEntity owner) {
        // super(world);
        this.owner = owner;
    }

    public void spawn() {
        if (!(owner.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) owner.getEntity();
        Horse horse = (Horse) player.getWorld().spawnEntity(player.getLocation(), EntityType.HORSE);
        UUID uniqueId = player.getUniqueId();
        if (record.contains(uniqueId)) {
            ChatChannels.sendDebugMessage((CommandIssuer) null, player.getName() + " - spawn", false);
            ChatChannels.sendDebugMessage((CommandIssuer) null, player.getName() + " - " + player.getVehicle(), false);
        }
        horse.setTamed(true);
        horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
        horse.setOwner(player);
        horse.setJumpStrength(0);
        horse.setVariant(Horse.Variant.HORSE);
        horse.setColor(Horse.Color.BROWN);
        horse.setStyle(Horse.Style.NONE);
        horse.setAdult();
        ((EntityLiving) ((CraftEntity) horse).getHandle()).getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(speed);
        if (record.contains(uniqueId)) {
            ChatChannels.sendDebugMessage((CommandIssuer) null, player.getName() + " - pre passenger", false);
            ChatChannels.sendDebugMessage((CommandIssuer) null, player.getName() + " - " + player.getVehicle(), false);
        }
        horse.setPassenger(player);
        if (record.contains(uniqueId)) {
            ChatChannels.sendDebugMessage((CommandIssuer) null, player.getName() + " - post passenger", false);
            ChatChannels.sendDebugMessage((CommandIssuer) null, player.getName() + " - " + player.getVehicle(), false);
        }
        this.horse = horse;
//        this.health = this.maxHealth;
//        owner.setTimeAfterDismount(0);
//        owner.setTimeAfterMount(0);
//        if (hasTurboSpeed) {
//            player.getInventory().setItem(7, new ItemBuilder(Material.SUGAR)
//                    .name(ChatColor.GREEN + "Turbo Speed" + ChatColor.GRAY + " - " + ChatColor.GREEN + "Right-Click")
//                    .get());
//        }
//        if (hasDivineMount) {
//            new BukkitRunnable() {
//                @Override
//                public void run() {
//                    if (owner.getTimeAfterMount() >= 5) {
//                        owner.sendMessage(ChatColor.DARK_RED + "Your divine mount has expired");
//                        this.cancel();
//                    } else {
//                        owner.sendMessage(ChatColor.DARK_RED + "Your divine mount will expire in " + Math.round(5 - owner.getTimeAfterMount()) + " seconds");
//                    }
//                }
//            }.runTaskTimer(Warlords.getInstance(), 0, 20);
//        }
    }

    public WarlordsEntity getOwner() {
        return owner;
    }

    public int getCooldown() {
        return cooldown;
    }
//
//    public void decrementCooldown() {
//        this.cooldown--;
//    }
//
//    public void setSpeed(float speed) {
//        this.speed = speed;
//    }
//
//    public void addSpeed(float amount) {
//        this.speed += amount;
//    }
//
//    public void boostSpeed() {
//        ((EntityLiving) ((CraftEntity) horse).getHandle()).getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(speed * 1.5);
//        new BukkitRunnable() {
//            @Override
//            public void run() {
//                ((EntityLiving) ((CraftEntity) horse).getHandle()).getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(speed);
//                owner.updateHorseItem((Player) owner.getEntity());
//            }
//        }.runTaskLater(Warlords.getInstance(), 20);
//
//    }
//
//    public boolean isDamageOnDismount() {
//        return damageOnDismount;
//    }
//
//    public void setDamageOnDismount(boolean damageOnDismount) {
//        this.damageOnDismount = damageOnDismount;
//    }
//
//    public boolean isMountProtection() {
//        return mountProtection;
//    }
//
//    public void setMountProtection(boolean mountProtection) {
//        this.mountProtection = mountProtection;
//    }
//
//    public void setHasMountProtection(boolean hasMountProtection) {
//        this.hasMountProtection = hasMountProtection;
//    }
//
//    public void setHealth(float health) {
//        this.health = health;
//    }
//
//    public float getHorseHealth() {
//        return this.health;
//    }
//
//    public void addHealth(float amount) {
//        this.health += amount;
//        if (this.health <= 0) {
//            owner.sendMessage(ChatColor.RED + "Your horse DIED");
//        } else {
//            owner.sendMessage(ChatColor.RED + "Your horse health is " + Math.round(this.health));
//        }
//    }
//
//    public float getMaxHealth2() {
//        return maxHealth;
//    }
//
//    public void setMaxHealth(float maxHealth) {
//        this.maxHealth = maxHealth;
//    }
//
//    public void setShield(float shield) {
//        this.shield = shield;
//    }
//
//    public void setHasTurboSpeed(boolean hasTurboSpeed) {
//        this.hasTurboSpeed = hasTurboSpeed;
//    }
//
//    public float getDamageResistance() {
//        return damageResistance;
//    }
//
//    public void decrementDamageResistance() {
//        this.damageResistance -= .1;
//    }
//
//    public boolean isHasDivineMount() {
//        return hasDivineMount;
//    }
//
//    public void setHasDivineMount(boolean hasDivineMount) {
//        this.hasDivineMount = hasDivineMount;
//    }
}
