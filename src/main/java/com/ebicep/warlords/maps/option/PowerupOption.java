package com.ebicep.warlords.maps.option;

import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.player.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.powerups.DamagePowerUp;
import com.ebicep.warlords.powerups.EnergyPowerUp;
import com.ebicep.warlords.powerups.SpeedPowerUp;
import com.ebicep.warlords.util.GameRunnable;
import com.ebicep.warlords.util.PlayerFilter;
import javax.annotation.Nonnull;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PowerupOption implements Option {

    public static int DEFAULT_TIME_TO_SPAWN = 60;
    public static int DEFAULT_MAX_COOLDOWN = 45 * 20;

    private Location location;
    private PowerupType type;
    private ArmorStand entity;
    private int duration;
    private int cooldown;
    private int maxCooldown;
    private int timeToSpawn;
    private boolean hasStarted = false;
    @Nonnull
    private Game game;
    public PowerupOption(Location location, PowerupType type) {
        this(location, type, type.getDuration(), DEFAULT_MAX_COOLDOWN, DEFAULT_TIME_TO_SPAWN);
    }

    public PowerupOption(Location location, PowerupType type, int duration, int maxCooldown, int timeToSpawn) {
        this.location = location;
        this.type = type;
        this.duration = duration;
        this.maxCooldown = maxCooldown;
        this.timeToSpawn = timeToSpawn;
    }

    @Override
    public void register(Game game) {
        this.game = game;
    }

    @Override
    public void start(Game game) {
        hasStarted = true;
        new GameRunnable(game) {
            @Override
            public void run() {
                if (cooldown == 0) {
                    PlayerFilter.entitiesAround(location, 1.4, 1.4, 1.4)
                            .isAlive()
                            .first((nearPlayer) -> {
                                type.onPickUp(PowerupOption.this, nearPlayer);
                                entity.remove();
                                entity = null;
                                cooldown = maxCooldown;
                            });
                } else {
                    cooldown -= 1;
                    if (cooldown == 0) {
                        spawn();
                    }
                }
            }

        }.runTaskTimer(0, 20);
    }
    
    
    public void spawn() {
        entity = location.getWorld().spawn(location.clone().add(0, -1.5, 0), ArmorStand.class);

        type.setNameAndItem(this, entity);

        entity.setGravity(false);
        entity.setVisible(false);
        entity.setCustomNameVisible(true);

        game.forEachOnlinePlayer((player, team) -> {
            entry.playSound(location, "ctf.powerup.spawn", 2, 1);
        });

    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        if (hasStarted) {
            throw new IllegalStateException("Cannot change location after starting");
        }
        this.location = location;
    }

    public PowerupType getType() {
        return type;
    }

    public void setType(PowerupType type) {
        if (hasStarted) {
            throw new IllegalStateException("Cannot change type after starting");
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
        SPEED(10) {
            @Override
            public void onPickUp(PowerupOption option, WarlordsPlayer warlordsPlayer) {
                warlordsPlayer.getCooldownManager().addCooldown("Speed", this.getClass(), this, "SPEED", option.getDuration(), warlordsPlayer, CooldownTypes.BUFF);
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
        HEALING(0) {

            @Override
            public void onPickUp(PowerupOption option, WarlordsPlayer warlordsPlayer) {
                warlordsPlayer.setPowerUpHeal(true);
                warlordsPlayer.sendMessage(String.format(
                        "§6You activated the §a§lHEALING §6powerup! §a+8%% §6Health per second for §a%d §6seconds!",
                        option.getDuration()
                ));
            }

            @Override
            public void setNameAndItem(PowerupOption option, ArmorStand armorStand) {
                armorStand.setCustomName("§a§lHEALING");
                armorStand.setHelmet(new ItemStack(Material.WOOL, 1, (short) 13));
            }
        },
        ENERGY(30) {

            @Override
            public void onPickUp(PowerupOption option, WarlordsPlayer warlordsPlayer) {
                warlordsPlayer.getCooldownManager().addCooldown("Energy", this.getClass(), this, "ENERGY", option.getDuration(), warlordsPlayer, CooldownTypes.BUFF);
                warlordsPlayer.sendMessage(String.format(
                        "§6You activated the §lENERGY §6powerup! §a+40%% §6Energy gain for §a%d §6seconds!",
                        option.getDuration()
                ));
            }

            @Override
            public void setNameAndItem(PowerupOption option, ArmorStand armorStand) {
                armorStand.setCustomName("§6§lENERGY");
                armorStand.setHelmet(new ItemStack(Material.WOOL, 1, (short) 1));
            }
        },
        DAMAGE(30) {

            @Override
            public void onPickUp(PowerupOption option, WarlordsPlayer warlordsPlayer) {
                warlordsPlayer.getCooldownManager().addCooldown("Damage", this.getClass(), this, "DMG", option.getDuration(), warlordsPlayer, CooldownTypes.BUFF);
                warlordsPlayer.sendMessage(String.format(
                        "§6You activated the §c§lDAMAGE §6powerup! §a+20%% §6Damage for §a%d §6seconds!",
                        option.getDuration()
                ));
            }

            @Override
            public void setNameAndItem(PowerupOption option, ArmorStand armorStand) {
                armorStand.setCustomName("§c§lDAMAGE");
                armorStand.setHelmet(new ItemStack(Material.WOOL, 1, (short) 14));
            }
        },;
        private final int duration;

        PowerupType(int duration) {
            this.duration = duration;
        }

        public int getDuration() {
            return duration;
        }

        public abstract void onPickUp(PowerupOption option, WarlordsPlayer warlordsPlayer);

        public abstract void setNameAndItem(PowerupOption option, ArmorStand armorStand);
    }
}
