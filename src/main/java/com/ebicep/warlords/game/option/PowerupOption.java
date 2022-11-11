package com.ebicep.warlords.game.option;

import com.ebicep.warlords.abilties.internal.*;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.marker.DebugLocationMarker;
import com.ebicep.warlords.game.option.marker.TimerSkipAbleMarker;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

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
    private boolean randomPowerup = false;
    @Nonnull
    private Game game;

    public PowerupOption(
            @Nonnull Location location,
            @Nonnegative int maxCooldown,
            @Nonnegative int timeToSpawn
    ) {
        this.location = Objects.requireNonNull(location, "location");
        this.type = PowerupType.getRandomPowerupType();
        this.duration = type.duration;
        this.maxCooldown = maxCooldown;
        this.cooldown = timeToSpawn * 4;
        this.randomPowerup = true;
    }

    public PowerupOption(@Nonnull Location location, @Nonnull PowerupType type, @Nonnegative int maxCooldown, @Nonnegative int timeToSpawn) {
        this.location = Objects.requireNonNull(location, "location");
        this.type = Objects.requireNonNull(type, "type");
        this.maxCooldown = maxCooldown;
        this.cooldown = timeToSpawn * 4;
    }

    public PowerupOption(@Nonnull Location location) {
        this(location, PowerupType.getRandomPowerupType());
        this.randomPowerup = true;
    }

    public PowerupOption(@Nonnull Location location, @Nonnull PowerupType type) {
        this(location, type, type.getDuration(), DEFAULT_MAX_COOLDOWN, DEFAULT_TIME_TO_SPAWN);
    }

    public PowerupOption(
            @Nonnull Location location,
            @Nonnull PowerupType type,
            @Nonnegative int duration,
            @Nonnegative int maxCooldown,
            @Nonnegative int timeToSpawn
    ) {
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
            public int getDelay() {
                return cooldown * 20;
            }

            @Override
            public void skipTimer(int delayInTicks) {
                cooldown = Math.max(cooldown - delayInTicks / 20, 0);
                if (cooldown == 0) {
                    spawn();
                }
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
                        if (randomPowerup) {
                            type = PowerupType.getRandomPowerupType();
                            duration = type.getDuration();
                        }
                        spawn();
                    }
                }
            }

        }.runTaskTimer(0, 4);
    }

    private void remove() {
        if (entity == null) {
            return;
        }
        entity.remove();
        entity = null;
    }

    @Nonnull
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        if (hasStarted) {
            throw new IllegalStateException("Cannot change location after starting.");
        }
        this.location = location;
    }

    @Nonnull
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

    public int getCooldown() {
        return cooldown;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(@Nonnegative int duration) {
        this.duration = duration;
    }

    public int getMaxCooldown() {
        return maxCooldown;
    }

    public ArmorStand getEntity() {
        return entity;
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

    public void setMaxCooldown(@Nonnegative int maxCooldown) {
        this.maxCooldown = maxCooldown;
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

    public void setTypeAndDuration(PowerupType type) {
        setType(type);
        this.duration = type.getDuration();
        this.remove();
    }

    public enum PowerupType {
        SPEED(10, Material.WOOL, (short) 4) {
            @Override
            public void onPickUp(PowerupOption option, WarlordsEntity we) {
                we.getCooldownManager().removeCooldown(SpeedPowerup.class);
                we.getCooldownManager().addRegularCooldown(
                        "Speed",
                        "SPEED",
                        SpeedPowerup.class,
                        SpeedPowerup.SPEED_POWERUP,
                        null,
                        CooldownTypes.BUFF,
                        cooldownManager -> we.sendMessage(ChatColor.GOLD + "Your " + ChatColor.YELLOW + ChatColor.BOLD + "SPEED" + ChatColor.GOLD + " powerup has worn off."),
                        option.getDuration() * 20
                );
                we.sendMessage(String.format("§6You activated the §e§lSPEED §6powerup! §a+40%% §6Speed for §a%d §6seconds!", option.getDuration()));
                we.addSpeedModifier(wp, "Speed Powerup", 40, option.getDuration() * 20, "BASE");
                Utils.playGlobalSound(option.getLocation(), "ctf.powerup.speed", 2, 1);
            }

            @Override
            public void setNameAndItem(PowerupOption option, ArmorStand armorStand) {
                armorStand.setCustomName("§b§lSPEED");
                armorStand.setHelmet(new ItemStack(Material.WOOL, 1, (short) 4));
            }
        },

        HEALING(5, Material.WOOL, (short) 13) {
            @Override
            public void onPickUp(PowerupOption option, WarlordsEntity we) {
                we.getCooldownManager().removeCooldown(HealingPowerup.class);
                we.getCooldownManager().addRegularCooldown(
                        "Healing",
                        "HEAL",
                        HealingPowerup.class,
                        HealingPowerup.HEALING_POWERUP,
                        null,
                        CooldownTypes.BUFF,
                        cooldownManager -> we.sendMessage(ChatColor.GOLD + "Your " + ChatColor.GREEN + ChatColor.BOLD + "HEALING" + ChatColor.GOLD + " powerup has worn off."),
                        option.getDuration() * 20
                );
                we.sendMessage(String.format("§6You activated the §a§lHEALING §6powerup! §a+8%% §6Health per second for §a%d §6seconds!",
                        option.getDuration()
                ));
            }

            @Override
            public void setNameAndItem(PowerupOption option, ArmorStand armorStand) {
                armorStand.setCustomName("§a§lHEALING");
                armorStand.setHelmet(new ItemStack(Material.WOOL, 1, (short) 13));
            }
        },

        ENERGY(30, Material.WOOL, (short) 1) {
            @Override
            public void onPickUp(PowerupOption option, WarlordsEntity we) {
                we.getCooldownManager().removeCooldown(EnergyPowerup.class);
                we.getCooldownManager().addCooldown(new RegularCooldown<EnergyPowerup>(
                        "Energy",
                        "ENERGY",
                        EnergyPowerup.class,
                        EnergyPowerup.ENERGY_POWERUP,
                        null,
                        CooldownTypes.BUFF,
                        cooldownManager -> we.sendMessage(ChatColor.GOLD + "Your " + ChatColor.GOLD + ChatColor.BOLD + "ENERGY" + ChatColor.GOLD + " powerup has worn off."),
                        option.getDuration() * 20
                ) {
                    @Override
                    public float multiplyEnergyGainPerTick(float energyGainPerTick) {
                        return energyGainPerTick * 1.5f;
                    }
                });
                we.sendMessage(String.format("§6You activated the §lENERGY §6powerup! §a+50%% §6Energy gain for §a%d §6seconds!", option.getDuration()));
            }

            @Override
            public void setNameAndItem(PowerupOption option, ArmorStand armorStand) {
                armorStand.setCustomName("§6§lENERGY");
                armorStand.setHelmet(new ItemStack(Material.WOOL, 1, (short) 1));
            }
        },

        DAMAGE(30, Material.WOOL, (short) 14) {
            @Override
            public void onPickUp(PowerupOption option, WarlordsEntity we) {
                we.getCooldownManager().removeCooldown(DamagePowerup.class);
                we.getCooldownManager().addCooldown(new RegularCooldown<DamagePowerup>(
                        "Damage",
                        "DMG",
                        DamagePowerup.class,
                        DamagePowerup.DAMAGE_POWERUP,
                        we,
                        CooldownTypes.BUFF,
                        cooldownManager -> we.sendMessage(ChatColor.GOLD + "Your " + ChatColor.RED + ChatColor.BOLD + "DAMAGE" + ChatColor.GOLD + " powerup has worn off."),
                        option.getDuration() * 20
                ) {
                    @Override
                    public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                        return currentDamageValue * 1.2f;
                    }
                });

                we.sendMessage(String.format("§6You activated the §c§lDAMAGE §6powerup! §a+20%% §6Damage for §a%d §6seconds!", option.getDuration()));
            }

            @Override
            public void setNameAndItem(PowerupOption option, ArmorStand armorStand) {
                armorStand.setCustomName("§c§lDAMAGE");
                armorStand.setHelmet(new ItemStack(Material.WOOL, 1, (short) 14));
            }
        },

        COOLDOWN(30, Material.WOOL, (short) 9) {
            @Override
            public void onPickUp(PowerupOption option, WarlordsEntity we) {
                we.getCooldownManager().removeCooldown(CooldownPowerup.class);
                we.getCooldownManager().addRegularCooldown(
                        "Cooldown",
                        "CDR",
                        CooldownPowerup.class,
                        CooldownPowerup.COOLDOWN_POWERUP,
                        null,
                        CooldownTypes.BUFF,
                        cooldownManager -> {
                            we.setCooldownModifier(1);
                            we.sendMessage(ChatColor.GOLD + "Your " + ChatColor.AQUA + ChatColor.BOLD + "COOLDOWN" + ChatColor.GOLD + " powerup has worn off.");
                        },
                        option.getDuration() * 20
                );
                we.setCooldownModifier(0.75);
                we.sendMessage(String.format("§6You activated the §b§lCOOLDOWN §6powerup! §a+25%% §6Cooldown reduction for §a%d §6seconds!",
                        option.getDuration()
                ));
            }

            @Override
            public void setNameAndItem(PowerupOption option, ArmorStand armorStand) {
                armorStand.setCustomName("§b§lCOOLDOWN");
                armorStand.setHelmet(new ItemStack(Material.WOOL, 1, (short) 9));
            }
        },

        SELF_DAMAGE(0, Material.WOOL, (short) 15) {
            @Override
            public void onPickUp(PowerupOption option, WarlordsEntity we) {
                we.addDamageInstance(we, "Self Damage Powerup", 5000, 5000, 0, 100, true);
            }

            @Override
            public void setNameAndItem(PowerupOption option, ArmorStand armorStand) {
                armorStand.setCustomName("§c§l5000 SELF DAMAGE");
                armorStand.setHelmet(new ItemStack(Material.WOOL, 1, (short) 15));
            }
        },

        SELF_HEAL(0, Material.WOOL, (short) 15) {
            @Override
            public void onPickUp(PowerupOption option, WarlordsEntity we) {
                we.addHealingInstance(we, "Self Heal Powerup", 5000, 5000, 0, 100, true, false);
            }

            @Override
            public void setNameAndItem(PowerupOption option, ArmorStand armorStand) {
                armorStand.setCustomName("§a§l5000 SELF HEAL");
                armorStand.setHelmet(new ItemStack(Material.WOOL, 1, (short) 15));
            }
        };

        public static final PowerupType[] VALUES = values();
        public static final PowerupType[] DEFAULT_POWERUPS = {ENERGY, HEALING};
        private final int duration;
        private final Material debugMaterial;
        private final int debugData;

        PowerupType(int duration, Material debugMaterial, int debugData) {
            this.duration = duration;
            this.debugMaterial = debugMaterial;
            this.debugData = debugData;
        }

        public static PowerupType getRandomPowerupType() {
            return DEFAULT_POWERUPS[ThreadLocalRandom.current().nextInt(DEFAULT_POWERUPS.length)];
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

        public abstract void onPickUp(PowerupOption option, WarlordsEntity we);

        public abstract void setNameAndItem(PowerupOption option, ArmorStand armorStand);

    }
}
