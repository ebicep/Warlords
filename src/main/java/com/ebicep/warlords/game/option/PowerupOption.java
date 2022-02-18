package com.ebicep.warlords.game.option;

import com.ebicep.warlords.classes.internal.DamagePowerup;
import com.ebicep.warlords.classes.internal.EnergyPowerup;
import com.ebicep.warlords.classes.internal.HealingPowerup;
import com.ebicep.warlords.classes.internal.SpeedPowerup;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.marker.DebugLocationMarker;
import com.ebicep.warlords.game.option.marker.TimerSkipAbleMarker;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.util.GameRunnable;
import com.ebicep.warlords.util.PlayerFilter;
import com.ebicep.warlords.util.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Objects;

public class PowerupOption implements Option {

    public static int DEFAULT_TIME_TO_SPAWN = 60;
    public static int DEFAULT_MAX_COOLDOWN = 45;

    @Nonnull
    private Location location;
    @Nonnull
    private PowerupType type;
    @Nullable
    private ArmorStand entity;
    @Nonnegative
    private int duration;
    @Nonnegative
    private int cooldown;
    @Nonnegative
    private int maxCooldown;
    private boolean hasStarted = false;
    @Nonnull
    private Game game;

    public PowerupOption(@Nonnull Location location, @Nonnull PowerupType type) {
        this(location, type, type.getDuration(), DEFAULT_MAX_COOLDOWN, DEFAULT_TIME_TO_SPAWN);
    }

    public PowerupOption(@Nonnull Location location, @Nonnull PowerupType type, @Nonnegative int duration, @Nonnegative int maxCooldown, @Nonnegative int timeToSpawn) {
        this.location = Objects.requireNonNull(location, "location");
        this.type = Objects.requireNonNull(type, "type");
        this.duration = duration;
        this.maxCooldown = maxCooldown;
        this.cooldown = timeToSpawn * 4;
    }

    @Override
    public void register(Game game) {
        this.game = game;
        game.registerGameMarker(DebugLocationMarker.class, DebugLocationMarker.create(
                () -> type.getDebugMaterial(),
                () -> type.getDebugData(),
                this::getClass,
                () -> this.getClass().getSimpleName() + ": " + this.type.name(),
                this::getLocation,
                () -> Arrays.asList(
                        "Type: " + this.getType(),
                        "Cooldown: " + this.getCooldown(),
                        "Duration: " + this.getDuration(),
                        "Max cooldown: " + this.getMaxCooldown(),
                        "Entity: " + this.getEntity()
                )
        ));
        game.registerGameMarker(TimerSkipAbleMarker.class, new TimerSkipAbleMarker() {
            @Override
            public void skipTimer(int delayInTicks) {
                cooldown = Math.max(cooldown - delayInTicks / 20, 0);
                if (cooldown == 0) {
                    spawn();
                }
            }

            @Override
            public int getDelay() {
                return cooldown * 20;
            }
            
        });
    }

    @Override
    public void start(Game game) {
        hasStarted = true;
        if (cooldown == 0) {
            spawn();
        }
        new GameRunnable(game) {
            @Override
            public void run() {
                if (cooldown == 0) {
                    PlayerFilter.entitiesAround(location, 1.4, 1.4, 1.4)
                            .isAlive()
                            .first((nearPlayer) -> {
                                type.onPickUp(PowerupOption.this, nearPlayer);
                                remove();
                                cooldown = maxCooldown * 4;
                            });
                } else {
                    cooldown--;
                    if (cooldown == 0) {
                        spawn();
                    }
                }
            }

        }.runTaskTimer(0, 5);
    }

    private void remove() {
        if (entity == null) {
            return;
        }
        entity.remove();
        entity = null;
    }

    private void spawn() {
        if (entity != null) {
            return;
        }
        entity = location.getWorld().spawn(location.clone().add(0, -1.5, 0), ArmorStand.class);

        type.setNameAndItem(this, entity);

        entity.setGravity(false);
        entity.setVisible(false);
        entity.setCustomNameVisible(true);

        Utils.playGlobalSound(location, "ctf.powerup.spawn", 2, 1);
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        if (hasStarted) {
            throw new IllegalStateException("Cannot change location after starting.");
        }
        this.location = location;
    }

    public PowerupType getType() {
        return type;
    }

    public void setType(PowerupType type) {
        if (hasStarted) {
            throw new IllegalStateException("Cannot change type after starting.");
        }
        this.type = type;
        this.remove();
    }

    public void setTypeAndDuration(PowerupType type) {
        setType(type);
        this.duration = type.getDuration();
        this.remove();
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

    public void setDuration(@Nonnegative int duration) {
        this.duration = duration;
    }

    public void setCooldown(@Nonnegative int cooldown) {
        int oldCooldown = cooldown;
        this.cooldown = cooldown;
        if (oldCooldown == 0 && cooldown != 0) {
            remove();
        }
        if (oldCooldown != 0 && cooldown == 0 && hasStarted) {
            spawn();
        }
    }

    public void setMaxCooldown(@Nonnegative int maxCooldown) {
        this.maxCooldown = maxCooldown;
    }

    public enum PowerupType {
        SPEED(10, Material.WOOL, (short) 4) {
            @Override
            public void onPickUp(PowerupOption option, WarlordsPlayer warlordsPlayer) {
                warlordsPlayer.getCooldownManager().addRegularCooldown(
                        "Speed",
                        "SPEED",
                        SpeedPowerup.class,
                        SpeedPowerup.SPEED_POWERUP,
                        null,
                        CooldownTypes.BUFF,
                        cooldownManager -> warlordsPlayer.sendMessage(ChatColor.GOLD + "Your " + ChatColor.YELLOW + ChatColor.BOLD + "SPEED" + ChatColor.GOLD + " powerup has worn off."),
                        option.getDuration() * 20
                );
                warlordsPlayer.sendMessage(String.format("§6You activated the §e§lSPEED §6powerup! §a+40%% §6Speed for §a%d §6seconds!", option.getDuration()));
                warlordsPlayer.getSpeed().addSpeedModifier("Speed Powerup", 40, 10 * 20, "BASE");
                Utils.playGlobalSound(option.getLocation(), "ctf.powerup.speed", 2, 1);
            }

            @Override
            public void setNameAndItem(PowerupOption option, ArmorStand armorStand) {
                armorStand.setCustomName("§b§lSPEED");
                armorStand.setHelmet(new ItemStack(Material.WOOL, 1, (short) 4));
            }
        },

        HEALING(5, Material.WOOL, (short) 5) {
            @Override
            public void onPickUp(PowerupOption option, WarlordsPlayer warlordsPlayer) {
                warlordsPlayer.getCooldownManager().addRegularCooldown(
                        "Healing",
                        "HEAL",
                        HealingPowerup.class,
                        HealingPowerup.HEALING_POWERUP,
                        null,
                        CooldownTypes.BUFF,
                        cooldownManager -> warlordsPlayer.sendMessage(ChatColor.GOLD + "Your " + ChatColor.GREEN + ChatColor.BOLD + "HEALING" + ChatColor.GOLD + " powerup has worn off."),
                        option.getDuration() * 20
                );
                warlordsPlayer.sendMessage(String.format("§6You activated the §a§lHEALING §6powerup! §a+8%% §6Health per second for §a%d §6seconds!", option.getDuration()));
            }

            @Override
            public void setNameAndItem(PowerupOption option, ArmorStand armorStand) {
                armorStand.setCustomName("§a§lHEALING");
                armorStand.setHelmet(new ItemStack(Material.WOOL, 1, (short) 13));
            }
        },

        ENERGY(30, Material.WOOL, (short) 3) {
            @Override
            public void onPickUp(PowerupOption option, WarlordsPlayer warlordsPlayer) {
                warlordsPlayer.getCooldownManager().addRegularCooldown(
                        "Energy",
                        "ENERGY",
                        EnergyPowerup.class,
                        EnergyPowerup.ENERGY_POWERUP,
                        null,
                        CooldownTypes.BUFF,
                        cooldownManager -> warlordsPlayer.sendMessage(ChatColor.GOLD + "Your " + ChatColor.GOLD + ChatColor.BOLD + "ENERGY" + ChatColor.GOLD + " powerup has worn off."),
                        option.getDuration() * 20
                );
                warlordsPlayer.sendMessage(String.format("§6You activated the §lENERGY §6powerup! §a+40%% §6Energy gain for §a%d §6seconds!", option.getDuration()));
            }

            @Override
            public void setNameAndItem(PowerupOption option, ArmorStand armorStand) {
                armorStand.setCustomName("§6§lENERGY");
                armorStand.setHelmet(new ItemStack(Material.WOOL, 1, (short) 1));
            }
        },

        DAMAGE(30, Material.WOOL, (short) 4) {
            @Override
            public void onPickUp(PowerupOption option, WarlordsPlayer warlordsPlayer) {
                warlordsPlayer.getCooldownManager().addRegularCooldown(
                        "Damage",
                        "DMG",
                        DamagePowerup.class,
                        DamagePowerup.DAMAGE_POWERUP,
                        null,
                        CooldownTypes.BUFF,
                        cooldownManager -> warlordsPlayer.sendMessage(ChatColor.GOLD + "Your " + ChatColor.RED + ChatColor.BOLD + "DAMAGE" + ChatColor.GOLD + " powerup has worn off."),
                        option.getDuration() * 20
                );
                warlordsPlayer.sendMessage(String.format("§6You activated the §c§lDAMAGE §6powerup! §a+20%% §6Damage for §a%d §6seconds!", option.getDuration()));
            }

            @Override
            public void setNameAndItem(PowerupOption option, ArmorStand armorStand) {
                armorStand.setCustomName("§c§lDAMAGE");
                armorStand.setHelmet(new ItemStack(Material.WOOL, 1, (short) 14));
            }
        };

        private final int duration;
        private final Material debugMaterial;
        private final int debugData;

        PowerupType(int duration, Material debugMaterial, int debugData) {
            this.duration = duration;
            this.debugData = debugData;
            this.debugMaterial = debugMaterial;
        }
        public int getDuration() {
            return duration;
        }
        
        public Material getDebugMaterial() {
            return debugMaterial;
        }
        
        public int getDebugData() {
            return debugData;
        }

        public abstract void onPickUp(PowerupOption option, WarlordsPlayer warlordsPlayer);

        public abstract void setNameAndItem(PowerupOption option, ArmorStand armorStand);

    }
}
