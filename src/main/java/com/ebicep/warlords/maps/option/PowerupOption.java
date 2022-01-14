package com.ebicep.warlords.maps.option;

import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.player.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.powerups.DamagePowerUp;
import com.ebicep.warlords.powerups.EnergyPowerUp;
import com.ebicep.warlords.powerups.SpeedPowerUp;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PowerupOption implements Option {

    private Location location;
    private PowerupType type;
    private ArmorStand entity;
    private int duration;
    private int cooldown;
    private int maxCooldown;
    private int timeToSpawn;
    private boolean hasRegistered = false;

    @Override
    public void register(Game game) {
        hasRegistered = true;
    }

    @Override
    public void tick(Game game) {
        Option.super.tick(game); //To change body of generated methods, choose Tools | Templates.
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        if (hasRegistered) {
            throw new IllegalStateException("Cannot change location after registering");
        }
        this.location = location;
    }

    public PowerupType getType() {
        return type;
    }

    public void setType(PowerupType type) {
        if (hasRegistered) {
            throw new IllegalStateException("Cannot change type after registering");
        }
        this.type = type;
    }

    public ArmorStand getEntity() {
        return entity;
    }

    public int getDuration() {
        return duration;
    }

    public int getCooldown() {
        return cooldown;
    }

    public int getMaxCooldown() {
        return maxCooldown;
    }

    public int getTimeToSpawn() {
        return timeToSpawn;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public void setMaxCooldown(int maxCooldown) {
        this.maxCooldown = maxCooldown;
    }

    public void setTimeToSpawn(int timeToSpawn) {
        this.timeToSpawn = timeToSpawn;
    }

    public enum PowerupType {
        SPEED() {
            @Override
            public void onPickUp(PowerupOption option, WarlordsPlayer warlordsPlayer) {
                warlordsPlayer.getCooldownManager().addCooldown("Speed", SpeedPowerUp.class, this, "SPEED", option.getDuration(), warlordsPlayer, CooldownTypes.BUFF);
                warlordsPlayer.sendMessage("§6You activated the §e§lSPEED §6powerup! §a+40% §6Speed for §a10 §6seconds!");
                warlordsPlayer.getSpeed().addSpeedModifier("Speed Powerup", 40, 10 * 20, "BASE");

                for (Player player1 : option.getLocation().getWorld().getPlayers()) {
                    player1.playSound(option.getLocation(), "ctf.powerup.speed", 2, 1);
                }
            }

            @Override
            public void setNameAndItem(PowerupOption option, ArmorStand armorStand) {
                armorStand.setCustomName("§b§lSPEED");
                armorStand.setHelmet(new ItemStack(Material.WOOL, 1, (short) 4));
            }
        },
        HEALING() {

            @Override
            public void onPickUp(PowerupOption option, WarlordsPlayer warlordsPlayer) {
                warlordsPlayer.setPowerUpHeal(true);
                warlordsPlayer.sendMessage("§6You activated the §a§lHEALING §6powerup! §a+8% §6Health per second for §a5 §6seconds!");
            }

            @Override
            public void setNameAndItem(PowerupOption option, ArmorStand armorStand) {
                armorStand.setCustomName("§a§lHEALING");
                armorStand.setHelmet(new ItemStack(Material.WOOL, 1, (short) 13));
            }
        },
        ENERGY() {

            @Override
            public void onPickUp(PowerupOption option, WarlordsPlayer warlordsPlayer) {
                warlordsPlayer.getCooldownManager().addCooldown("Energy", EnergyPowerUp.class, this, "ENERGY", option.getDuration(), warlordsPlayer, CooldownTypes.BUFF);
                warlordsPlayer.sendMessage("§6You activated the §lENERGY §6powerup! §a+40% §6Energy gain for §a30 §6seconds!");
            }

            @Override
            public void setNameAndItem(PowerupOption option, ArmorStand armorStand) {
                armorStand.setCustomName("§6§lENERGY");
                armorStand.setHelmet(new ItemStack(Material.WOOL, 1, (short) 1));
            }
        },
        DAMAGE() {

            @Override
            public void onPickUp(PowerupOption option, WarlordsPlayer warlordsPlayer) {
                warlordsPlayer.getCooldownManager().addCooldown("Damage", DamagePowerUp.class, this, "DMG", option.getDuration(), warlordsPlayer, CooldownTypes.BUFF);
                warlordsPlayer.sendMessage("§6You activated the §c§lDAMAGE §6powerup! §a+20% §6Damage for §a30 §6seconds!");
            }

            @Override
            public void setNameAndItem(PowerupOption option, ArmorStand armorStand) {
                armorStand.setCustomName("§c§lDAMAGE");
                armorStand.setHelmet(new ItemStack(Material.WOOL, 1, (short) 14));
            }
        },;

        public abstract void onPickUp(PowerupOption option, WarlordsPlayer warlordsPlayer);

        public abstract void setNameAndItem(PowerupOption option, ArmorStand armorStand);
    }
}
