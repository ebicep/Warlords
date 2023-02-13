package com.ebicep.customentities.nms;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CustomHorse extends net.minecraft.world.entity.animal.horse.Horse {

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

    public CustomHorse(ServerLevel serverLevel, WarlordsEntity owner) {
        super(net.minecraft.world.entity.EntityType.HORSE, serverLevel);
        this.owner = owner;
    }

    public void spawn() {
        if (!(owner.getEntity() instanceof Player player)) {
            return;
        }
        Horse horse = (Horse) player.getWorld().spawnEntity(player.getLocation(), EntityType.HORSE);
        horse.setTamed(true);
        horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
        horse.setOwner(player);
        horse.setJumpStrength(0);
        horse.setColor(Horse.Color.BROWN);
        horse.setStyle(Horse.Style.NONE);
        horse.setAdult();
        horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(speed);
        horse.addPassenger(player);
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
//        ((LivingEntity) ((CraftEntity) horse).getHandle()).getAttribute(Attributes.GENERIC_MOVEMENT_SPEED).setBaseValue(speed * 1.5);
//        new BukkitRunnable() {
//            @Override
//            public void run() {
//                ((LivingEntity) ((CraftEntity) horse).getHandle()).getAttribute(Attributes.GENERIC_MOVEMENT_SPEED).setBaseValue(speed);
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
