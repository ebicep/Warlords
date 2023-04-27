package com.ebicep.warlords.game.option;

import com.ebicep.warlords.abilties.internal.*;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.marker.DebugLocationMarker;
import com.ebicep.warlords.game.option.marker.TimerSkipAbleMarker;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
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
    public void register(@Nonnull Game game) {
        this.game = game;
        game.registerGameMarker(DebugLocationMarker.class, DebugLocationMarker.create(
                () -> type.getDebugMaterial(),
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
    public void start(@Nonnull Game game) {
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
                                if (nearPlayer instanceof WarlordsPlayer) {
                                    type.onPickUp(PowerupOption.this, nearPlayer);
                                    remove();
                                    cooldown = maxCooldown * 4;
                                }
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

    public @org.jetbrains.annotations.Nullable ArmorStand getEntity() {
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
        this.cooldown = cooldown;
        if (cooldown == 0 && cooldown != 0) {
            remove();
        }
        if (cooldown != 0 && cooldown == 0 && hasStarted) {
            spawn();
        }
    }

    public void setTypeAndDuration(PowerupType type) {
        setType(type);
        this.duration = type.getDuration();
        this.remove();
    }

    public enum PowerupType {
        SPEED(NamedTextColor.YELLOW, 10, Material.YELLOW_WOOL) {
            @Override
            public void onPickUp(PowerupOption option, WarlordsEntity we) {
                we.getCooldownManager().removeCooldown(SpeedPowerup.class, false);
                we.getCooldownManager().addRegularCooldown(
                        "Speed",
                        "SPEED",
                        SpeedPowerup.class,
                        SpeedPowerup.SPEED_POWERUP,
                        null,
                        CooldownTypes.BUFF,
                        cooldownManager -> {
                        },
                        cooldownManager -> {
                            we.sendMessage(getWornOffMessage());
                        },
                        option.getDuration() * 20
                );
                we.sendMessage(Component.text("You activated the ", NamedTextColor.GOLD)
                                        .append(Component.text("SPEED", NamedTextColor.AQUA, TextDecoration.BOLD))
                                        .append(Component.text(" powerup! "))
                                        .append(Component.text("+40% ", NamedTextColor.GREEN))
                                        .append(Component.text("Speed for "))
                                        .append(Component.text(option.getDuration(), NamedTextColor.GREEN))
                                        .append(Component.text(" seconds!")));
                we.addSpeedModifier(we, "Speed Powerup", 40, option.getDuration() * 20, "BASE");
                Utils.playGlobalSound(option.getLocation(), "ctf.powerup.speed", 2, 1);
            }

            @Override
            public void setNameAndItem(PowerupOption option, ArmorStand armorStand) {
                armorStand.customName(Component.text("SPEED", NamedTextColor.AQUA, TextDecoration.BOLD));
                armorStand.getEquipment().setHelmet(new ItemStack(Material.YELLOW_WOOL));
            }
        },

        HEALING(NamedTextColor.GREEN, 5, Material.GREEN_WOOL) {
            @Override
            public void onPickUp(PowerupOption option, WarlordsEntity we) {
                we.getCooldownManager().removeCooldown(HealingPowerup.class, false);
                we.getCooldownManager().addRegularCooldown(
                        "Healing",
                        "HEAL",
                        HealingPowerup.class,
                        HealingPowerup.HEALING_POWERUP,
                        null,
                        CooldownTypes.BUFF,
                        cooldownManager -> {
                        },
                        cooldownManager -> {
                            we.sendMessage(getWornOffMessage());
                        },
                        option.getDuration() * 20
                );
                we.sendMessage(Component.text("You activated the ", NamedTextColor.GOLD)
                                        .append(Component.text("HEALING", NamedTextColor.AQUA, TextDecoration.BOLD))
                                        .append(Component.text(" powerup! "))
                                        .append(Component.text("+8% ", NamedTextColor.GREEN))
                                        .append(Component.text("Health per second for "))
                                        .append(Component.text(option.getDuration(), NamedTextColor.GREEN))
                                        .append(Component.text(" seconds!")));
            }

            @Override
            public void setNameAndItem(PowerupOption option, ArmorStand armorStand) {
                armorStand.customName(Component.text("HEALING", NamedTextColor.GREEN, TextDecoration.BOLD));
                armorStand.getEquipment().setHelmet(new ItemStack(Material.GREEN_WOOL));
            }
        },

        ENERGY(NamedTextColor.GOLD, 30, Material.ORANGE_WOOL) {
            @Override
            public void onPickUp(PowerupOption option, WarlordsEntity we) {
                we.getCooldownManager().removeCooldown(EnergyPowerup.class, false);
                we.getCooldownManager().addCooldown(new RegularCooldown<>(
                        "Energy",
                        "ENERGY",
                        EnergyPowerup.class,
                        EnergyPowerup.ENERGY_POWERUP,
                        null,
                        CooldownTypes.BUFF,
                        cooldownManager -> {
                        },
                        cooldownManager -> {
                            we.sendMessage(getWornOffMessage());
                        },
                        option.getDuration() * 20
                ) {
                    @Override
                    public float multiplyEnergyGainPerTick(float energyGainPerTick) {
                        return energyGainPerTick * 1.5f;
                    }
                });
                we.sendMessage(Component.text("You activated the ", NamedTextColor.GOLD)
                                        .append(Component.text("ENERGY", NamedTextColor.AQUA, TextDecoration.BOLD))
                                        .append(Component.text(" powerup! "))
                                        .append(Component.text("+50% ", NamedTextColor.GREEN))
                                        .append(Component.text("Energy gain for "))
                                        .append(Component.text(option.getDuration(), NamedTextColor.GREEN))
                                        .append(Component.text(" seconds!")));
            }

            @Override
            public void setNameAndItem(PowerupOption option, ArmorStand armorStand) {
                armorStand.customName(Component.text("§6§lENERGY"));
                armorStand.getEquipment().setHelmet(new ItemStack(Material.ORANGE_WOOL));
            }
        },

        DAMAGE(NamedTextColor.RED, 30, Material.RED_WOOL) {
            @Override
            public void onPickUp(PowerupOption option, WarlordsEntity we) {
                we.getCooldownManager().removeCooldown(DamagePowerup.class, false);
                we.getCooldownManager().addCooldown(new RegularCooldown<>(
                        "Damage",
                        "DMG",
                        DamagePowerup.class,
                        DamagePowerup.DAMAGE_POWERUP,
                        we,
                        CooldownTypes.BUFF,
                        cooldownManager -> {
                        },
                        cooldownManager -> {
                            we.sendMessage(getWornOffMessage());
                        },
                        option.getDuration() * 20
                ) {
                    @Override
                    public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                        return currentDamageValue * 1.2f;
                    }
                });
                we.sendMessage(Component.text("You activated the ", NamedTextColor.GOLD)
                                        .append(Component.text("DAMAGE", NamedTextColor.AQUA, TextDecoration.BOLD))
                                        .append(Component.text(" powerup! "))
                                        .append(Component.text("+20% ", NamedTextColor.GREEN))
                                        .append(Component.text("Damage for "))
                                        .append(Component.text(option.getDuration(), NamedTextColor.GREEN))
                                        .append(Component.text(" seconds!")));
            }

            @Override
            public void setNameAndItem(PowerupOption option, ArmorStand armorStand) {
                armorStand.customName(Component.text("DAMAGE", NamedTextColor.RED, TextDecoration.BOLD));
                armorStand.getEquipment().setHelmet(new ItemStack(Material.RED_WOOL));
            }
        },

        COOLDOWN(NamedTextColor.AQUA, 30, Material.LIGHT_BLUE_WOOL) {
            @Override
            public void onPickUp(PowerupOption option, WarlordsEntity we) {
                we.getCooldownManager().removeCooldown(CooldownPowerup.class, false);
                we.getCooldownManager().addRegularCooldown(
                        "Cooldown",
                        "CDR",
                        CooldownPowerup.class,
                        CooldownPowerup.COOLDOWN_POWERUP,
                        null,
                        CooldownTypes.BUFF,
                        cooldownManager -> {

                        },
                        cooldownManager -> {
                            we.setCooldownModifier(1);
                            we.sendMessage(getWornOffMessage());
                        },
                        option.getDuration() * 20
                );
                we.setCooldownModifier(0.75);
                we.sendMessage(Component.text("You activated the ", NamedTextColor.GOLD)
                                        .append(Component.text("COOLDOWN", NamedTextColor.AQUA, TextDecoration.BOLD))
                                        .append(Component.text(" powerup! "))
                                        .append(Component.text("+25% ", NamedTextColor.GREEN))
                                        .append(Component.text("Cooldown reduction for "))
                                        .append(Component.text(option.getDuration(), NamedTextColor.GREEN))
                                        .append(Component.text(" seconds!")));
            }

            @Override
            public void setNameAndItem(PowerupOption option, ArmorStand armorStand) {
                armorStand.customName(Component.text("COOLDOWN", NamedTextColor.AQUA, TextDecoration.BOLD));
                armorStand.getEquipment().setHelmet(new ItemStack(Material.LIGHT_BLUE_WOOL));
            }
        },

        SELF_DAMAGE(NamedTextColor.DARK_RED, 0, Material.RED_WOOL) {
            @Override
            public void onPickUp(PowerupOption option, WarlordsEntity we) {
                we.addDamageInstance(we, "Self Damage Powerup", 5000, 5000, 0, 100, true);
            }

            @Override
            public void setNameAndItem(PowerupOption option, ArmorStand armorStand) {
                armorStand.customName(Component.text("5000 SELF DAMAGE", NamedTextColor.DARK_RED, TextDecoration.BOLD));
                armorStand.getEquipment().setHelmet(new ItemStack(Material.RED_WOOL));
            }
        },

        SELF_HEAL(NamedTextColor.DARK_GREEN, 0, Material.GREEN_WOOL) {
            @Override
            public void onPickUp(PowerupOption option, WarlordsEntity we) {
                we.addHealingInstance(we, "Self Heal Powerup", 5000, 5000, 0, 100, true, false);
            }

            @Override
            public void setNameAndItem(PowerupOption option, ArmorStand armorStand) {
                armorStand.customName(Component.text("5000 SELF HEAL", NamedTextColor.DARK_GREEN, TextDecoration.BOLD));
                armorStand.getEquipment().setHelmet(new ItemStack(Material.GREEN_WOOL));
            }
        };

        public static final PowerupType[] VALUES = values();
        public static final PowerupType[] DEFAULT_POWERUPS = {ENERGY, HEALING};
        private final NamedTextColor textColor;
        private final int duration;
        private final Material debugMaterial;

        PowerupType(NamedTextColor textColor, int duration, Material debugMaterial) {
            this.textColor = textColor;
            this.duration = duration;
            this.debugMaterial = debugMaterial;
        }

        public static PowerupType getRandomPowerupType() {
            return DEFAULT_POWERUPS[ThreadLocalRandom.current().nextInt(DEFAULT_POWERUPS.length)];
        }

        public Component getWornOffMessage() {
            return Component.text("Your ", NamedTextColor.GOLD)
                            .append(Component.text(name(), textColor))
                            .append(Component.text(" powerup has worn off."));
        }

        public int getDuration() {
            return duration;
        }

        public Material getDebugMaterial() {
            return debugMaterial;
        }

        public abstract void onPickUp(PowerupOption option, WarlordsEntity we);

        public abstract void setNameAndItem(PowerupOption option, ArmorStand armorStand);

    }
}
