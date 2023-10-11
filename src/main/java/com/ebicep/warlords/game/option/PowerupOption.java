package com.ebicep.warlords.game.option;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.marker.DebugLocationMarker;
import com.ebicep.warlords.game.option.marker.TimerSkipAbleMarker;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilterGeneric;
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
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class PowerupOption implements Option {

    public static int DEFAULT_TIME_TO_SPAWN = 60;
    public static int DEFAULT_MAX_COOLDOWN = 45;

    @Nonnull
    private final Location location;
    @Nonnull
    private PowerUp type;
    @Nullable
    private ArmorStand entity;
    @Nonnegative
    private int currentCooldown;
    @Nonnegative
    private int cooldown;
    private boolean randomPowerup = false;


    public PowerupOption(
            @Nonnull Location location,
            @Nonnegative int cooldown,
            @Nonnegative int timeToSpawn
    ) {
        this(location, PowerUp.getRandomPowerupType(), cooldown, timeToSpawn);
        this.randomPowerup = true;
    }

    public PowerupOption(
            @Nonnull Location location,
            @Nonnull PowerUp type,
            @Nonnegative int cooldown,
            @Nonnegative int timeToSpawn
    ) {
        this.location = Objects.requireNonNull(location, "location");
        this.type = Objects.requireNonNull(type, "type");
        this.cooldown = cooldown;
        this.currentCooldown = timeToSpawn;
    }

    public PowerupOption(@Nonnull Location location) {
        this(location, PowerUp.getRandomPowerupType());
        this.randomPowerup = true;
    }

    public PowerupOption(@Nonnull Location location, @Nonnull PowerUp type) {
        this(location, type, DEFAULT_MAX_COOLDOWN, DEFAULT_TIME_TO_SPAWN);
    }

    @Override
    public void register(@Nonnull Game game) {
        game.registerGameMarker(DebugLocationMarker.class, DebugLocationMarker.create(
                () -> type.getDebugMaterial(),
                this::getClass,
                () -> Component.text(this.getClass().getSimpleName() + ": " + this.type.name()),
                this::getLocation,
                () -> Arrays.asList(
                        Component.text("Type: " + this.getType()),
                        Component.text("Current Cooldown: " + this.getCurrentCooldown()),
                        Component.text("Cooldown: " + this.getCooldown()),
                        Component.text("Entity: " + this.getEntity()),
                        Component.text("Randomized: " + this.isRandomPowerup())
                )
        ));
        game.registerGameMarker(TimerSkipAbleMarker.class, new TimerSkipAbleMarker() {
            @Override
            public int getDelay() {
                return currentCooldown * 20;
            }

            @Override
            public void skipTimer(int delayInTicks) {
                currentCooldown = Math.max(currentCooldown - delayInTicks / 20, 0);
                if (currentCooldown == 0) {
                    spawn();
                }
            }
        });
    }

    @Override
    public void start(@Nonnull Game game) {
        if (currentCooldown == 0) {
            spawn();
        }
        new GameRunnable(game) {
            int ticksElapsed = 0;

            @Override
            public void run() {
                if (ticksElapsed % 5 == 0 && currentCooldown == 0) {
                    PlayerFilterGeneric
                            .entitiesAround(location, 1.6, 1.6, 1.6)
                            .warlordsPlayers()
                            .isAlive()
                            .first((nearPlayer) -> {
                                type.onPickUp(PowerupOption.this, nearPlayer);
                                remove();
                                currentCooldown = cooldown;
                            });
                }
                if (ticksElapsed % 20 == 0) {
                    currentCooldown--;
                    if (currentCooldown == 0) {
                        if (randomPowerup) {
                            type = PowerUp.getRandomPowerupType();
                        }
                        spawn();
                    }
                }
                ticksElapsed++;
            }

        }.runTaskTimer(0, 0);
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

    @Nonnull
    public PowerUp getType() {
        return type;
    }

    public int getCurrentCooldown() {
        return currentCooldown;
    }

    public int getCooldown() {
        return cooldown;
    }

    public @Nullable ArmorStand getEntity() {
        return entity;
    }

    public boolean isRandomPowerup() {
        return randomPowerup;
    }

    private void spawn() {
        if (entity != null) {
            return;
        }
        entity = Utils.spawnArmorStand(location.clone().add(0, -1.5, 0), armorStand -> {
            armorStand.setCustomNameVisible(true);
            type.setNameAndItem(this, armorStand);
        });
        Utils.playGlobalSound(location, "ctf.powerup.spawn", 2, 1);
    }

    public enum PowerUp {
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
                        getTickDuration()
                );
                we.sendMessage(Component.text("You activated the ", NamedTextColor.GOLD)
                                        .append(Component.text("SPEED", NamedTextColor.AQUA, TextDecoration.BOLD))
                                        .append(Component.text(" powerup! "))
                                        .append(Component.text("+40% ", NamedTextColor.GREEN))
                                        .append(Component.text("Speed for "))
                                        .append(Component.text(getSecondDuration(), NamedTextColor.GREEN))
                                        .append(Component.text(" seconds!")));
                we.addSpeedModifier(we, "Speed Powerup", 40, getTickDuration(), "BASE");
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
                        getTickDuration(),
                        Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                            if (ticksElapsed % 20 == 0) {
                                float heal = we.getMaxHealth() * .08f;
                                if (we.getHealth() + heal > we.getMaxHealth()) {
                                    heal = we.getMaxHealth() - we.getHealth();
                                }
                                if (heal > 0) {
                                    we.setHealth(we.getHealth() + heal);
                                    we.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN.append(Component.text(" Healed ", NamedTextColor.GRAY))
                                                                                  .append(Component.text(Math.round(heal), NamedTextColor.GREEN))
                                                                                  .append(Component.text(" health.", NamedTextColor.GRAY)));
                                }
                            }
                        })
                );
                we.sendMessage(Component.text("You activated the ", NamedTextColor.GOLD)
                                        .append(Component.text("HEALING", NamedTextColor.GREEN, TextDecoration.BOLD))
                                        .append(Component.text(" powerup! "))
                                        .append(Component.text("+8% ", NamedTextColor.GREEN))
                                        .append(Component.text("Health per second for "))
                                        .append(Component.text(getSecondDuration(), NamedTextColor.GREEN))
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
                        getTickDuration()
                ) {
                    @Override
                    public float multiplyEnergyGainPerTick(float energyGainPerTick) {
                        return energyGainPerTick * 1.5f;
                    }
                });
                we.sendMessage(Component.text("You activated the ", NamedTextColor.GOLD)
                                        .append(Component.text("ENERGY", NamedTextColor.GOLD, TextDecoration.BOLD))
                                        .append(Component.text(" powerup! "))
                                        .append(Component.text("+50% ", NamedTextColor.GREEN))
                                        .append(Component.text("Energy gain for "))
                                        .append(Component.text(getSecondDuration(), NamedTextColor.GREEN))
                                        .append(Component.text(" seconds!")));
            }

            @Override
            public void setNameAndItem(PowerupOption option, ArmorStand armorStand) {
                armorStand.customName(Component.text("ENERGY", NamedTextColor.GOLD, TextDecoration.BOLD));
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
                        getTickDuration()
                ) {
                    @Override
                    public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                        return currentDamageValue * 1.2f;
                    }
                });
                we.sendMessage(Component.text("You activated the ", NamedTextColor.GOLD)
                                        .append(Component.text("DAMAGE", NamedTextColor.RED, TextDecoration.BOLD))
                                        .append(Component.text(" powerup! "))
                                        .append(Component.text("+20% ", NamedTextColor.GREEN))
                                        .append(Component.text("Damage for "))
                                        .append(Component.text(getSecondDuration(), NamedTextColor.GREEN))
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
                we.getCooldownManager().addCooldown(new RegularCooldown<>(
                        "Cooldown",
                        "CDR",
                        CooldownPowerup.class,
                        CooldownPowerup.COOLDOWN_POWERUP,
                        we,
                        CooldownTypes.BUFF,
                        cooldownManager -> {

                        },
                        cooldownManager -> {
                            we.sendMessage(getWornOffMessage());
                        },
                        getTickDuration()
                ) {
                    @Override
                    public float getAbilityMultiplicativeCooldownMult(AbstractAbility ability) {
                        return 0.75f;
                    }
                });
                we.sendMessage(Component.text("You activated the ", NamedTextColor.GOLD)
                                        .append(Component.text("COOLDOWN", NamedTextColor.AQUA, TextDecoration.BOLD))
                                        .append(Component.text(" powerup! "))
                                        .append(Component.text("+25% ", NamedTextColor.GREEN))
                                        .append(Component.text("Cooldown reduction for "))
                                        .append(Component.text(getSecondDuration(), NamedTextColor.GREEN))
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
                we.addDamageInstance(we, "Self Damage Powerup", 5000, 5000, 0, 100);
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
                we.addHealingInstance(we, "Self Heal Powerup", 5000, 5000, 0, 100);
            }

            @Override
            public void setNameAndItem(PowerupOption option, ArmorStand armorStand) {
                armorStand.customName(Component.text("5000 SELF HEAL", NamedTextColor.DARK_GREEN, TextDecoration.BOLD));
                armorStand.getEquipment().setHelmet(new ItemStack(Material.GREEN_WOOL));
            }
        };

        public static final PowerUp[] VALUES = values();
        public static final PowerUp[] DEFAULT_POWERUPS = {ENERGY, HEALING};

        public static PowerUp getRandomPowerupType() {
            return DEFAULT_POWERUPS[ThreadLocalRandom.current().nextInt(DEFAULT_POWERUPS.length)];
        }

        private final NamedTextColor textColor;
        private final int secondDuration;
        private final Material debugMaterial;

        PowerUp(NamedTextColor textColor, int secondDuration, Material debugMaterial) {
            this.textColor = textColor;
            this.secondDuration = secondDuration;
            this.debugMaterial = debugMaterial;
        }

        public Component getWornOffMessage() {
            return Component.text("Your ", NamedTextColor.GOLD)
                            .append(Component.text(name(), textColor, TextDecoration.BOLD))
                            .append(Component.text(" powerup has worn off."));
        }

        public int getSecondDuration() {
            return secondDuration;
        }

        public int getTickDuration() {
            return secondDuration * 20;
        }

        public Material getDebugMaterial() {
            return debugMaterial;
        }

        public abstract void onPickUp(PowerupOption option, WarlordsEntity we);

        public abstract void setNameAndItem(PowerupOption option, ArmorStand armorStand);

    }
}