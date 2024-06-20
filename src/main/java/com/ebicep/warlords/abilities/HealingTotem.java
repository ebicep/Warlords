package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.achievements.types.ChallengeAchievements;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FallingBlockWaveEffect;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.shaman.earthwarden.HealingTotemBranch;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class HealingTotem extends AbstractTotem implements Duration, HitBox, Heals<HealingTotem.HealingValues> {

    public int playersHealed = 0;
    public int playersCrippled = 0;

    protected float amountHealed = 0;
    private final HealingValues healingValues = new HealingValues();
    private FloatModifiable radius = new FloatModifiable(7);
    private int tickDuration = 120;
    private int crippleDuration = 6;
    private float healingIncrement = 25;

    public HealingTotem() {
        this(null, null);
    }

    public HealingTotem(ArmorStand totem, WarlordsEntity owner) {
        super("Healing Totem", 621, 728, 67.86f, 60, 25, 175, totem, owner);
    }

    @Override
    public void updateDescription(Player player) {
        if (inPve) {
            description = Component.text("Place a totem on the ground that pulses constantly, healing nearby allies in a ")
                                   .append(Component.text(format(radius.getCalculatedValue()), NamedTextColor.YELLOW))
                                   .append(Component.text(" block radius for "))
                                   .append(Heals.formatHealing(healingValues.totemHealing))
                                   .append(Component.text(" health every second. The healing will gradually decrease by "))
                                   .append(Component.text(format(healingIncrement) + "%", NamedTextColor.GREEN))
                                   .append(Component.text(" until the final proc which heals for the normal amount once again. "))
                                   .append(Component.text("Lasts "))
                                   .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                                   .append(Component.text(
                                           " seconds.\n\nPressing SHIFT or re-activating the ability causes your totem to pulse with immense force, crippling all enemies for "))
                                   .append(Component.text(crippleDuration, NamedTextColor.GOLD))
                                   .append(Component.text(" seconds. Crippled enemies deal "))
                                   .append(Component.text("25%", NamedTextColor.RED))
                                   .append(Component.text(" less damage."));
        } else {
            description = Component.text("Place a totem on the ground that pulses constantly, healing nearby allies in a ")
                                   .append(Component.text(format(radius.getCalculatedValue()), NamedTextColor.YELLOW))
                                   .append(Component.text(" block radius for "))
                                   .append(Heals.formatHealing(healingValues.totemHealing))
                                   .append(Component.text(" health every second. The healing will gradually decrease by "))
                                   .append(Component.text(format(healingIncrement) + "%", NamedTextColor.GREEN))
                                   .append(Component.text(" until the final proc which heals for the normal amount once again. "))
                                   .append(Component.text("Lasts "))
                                   .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                                   .append(Component.text(" seconds."));
        }

    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Players Healed", "" + playersHealed));
        info.add(new Pair<>("Players Crippled", "" + playersCrippled));

        return info;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new HealingTotemBranch(abilityTree, this);
    }

    @Override
    public void runEveryTick(@Nullable WarlordsEntity warlordsEntity) {
        radius.tick();
        super.runEveryTick(warlordsEntity);
    }

    @Override
    protected void playSound(WarlordsEntity warlordsEntity, Location location) {
        Utils.playGlobalSound(location, "shaman.totem.activation", 2, 1);
    }

    @Override
    protected ItemStack getTotemItemStack() {
        return new ItemStack(Material.PINK_TULIP);
    }

    @Override
    protected void onActivation(WarlordsEntity wp, ArmorStand totemStand) {
        HealingTotem tempHealingTotem = new HealingTotem(totemStand, wp);
        AtomicInteger cooldownCounter = new AtomicInteger();
        float rad = radius.getCalculatedValue();
        RegularCooldown<HealingTotem> healingTotemCooldown = new RegularCooldown<>(
                name,
                "TOTEM",
                HealingTotem.class,
                tempHealingTotem,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                    Utils.playGlobalSound(totemStand.getLocation(), Sound.ENTITY_BLAZE_DEATH, 1.2f, 0.7f);
                    Utils.playGlobalSound(totemStand.getLocation(), "shaman.heal.impact", 2, 1);

                    new FallingBlockWaveEffect(totemStand.getLocation().clone().add(0, 1, 0), 3, 0.8, Material.SPRUCE_SAPLING).play();

                    PlayerFilter.entitiesAround(totemStand, rad, rad, rad)
                                .aliveTeammatesOf(wp)
                                .forEach((nearPlayer) -> {
                                    playersHealed++;
                                    nearPlayer.addInstance(InstanceBuilder
                                            .healing()
                                            .ability(this)
                                            .source(wp)
                                            .value(healingValues.totemHealing)
                                    ).ifPresent(warlordsDamageHealingFinalEvent -> {
                                        tempHealingTotem.addAmountHealed(warlordsDamageHealingFinalEvent.getValue());
                                    });
                                });
                    if (tempHealingTotem.getAmountHealed() >= 20000) {
                        ChallengeAchievements.checkForAchievement(wp, ChallengeAchievements.JUNGLE_HEALING);
                    }
                },
                cooldownManager -> {
                    totemStand.remove();
                },
                false,
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (pveMasterUpgrade && ticksElapsed % 10 == 0) {
                        EffectUtils.playSphereAnimation(totemStand.getLocation(), rad, Particle.VILLAGER_HAPPY, 2);
                    }

                    if (ticksElapsed % 20 == 0) {
                        cooldownCounter.set(ticksElapsed);
                        Utils.playGlobalSound(totemStand.getLocation(), "shaman.earthlivingweapon.impact", 2, pveMasterUpgrade ? 0.4f : 0.9f);

                        totemStand.getLocation().getWorld().spawnParticle(
                                Particle.VILLAGER_HAPPY,
                                totemStand.getLocation().clone().add(0, 1.6, 0),
                                5,
                                0.4,
                                0.2,
                                0.4,
                                0.05,
                                null,
                                true
                        );

                        Location totemLoc = totemStand.getLocation();
                        totemLoc.add(0, 2, 0);
                        Location particleLoc = totemLoc.clone();
                        for (int i = 0; i < 1; i++) {
                            for (int j = 0; j < 12; j++) {
                                double angle = j / 10D * Math.PI * 2;
                                double width = (double) rad;
                                particleLoc.setX(totemLoc.getX() + Math.sin(angle) * width);
                                particleLoc.setY(totemLoc.getY() + i / 2D);
                                particleLoc.setZ(totemLoc.getZ() + Math.cos(angle) * width);

                                particleLoc.getWorld().spawnParticle(
                                        Particle.FIREWORKS_SPARK,
                                        particleLoc,
                                        1,
                                        0,
                                        0,
                                        0,
                                        0,
                                        null,
                                        true
                                );
                            }
                        }

                        CircleEffect circle = new CircleEffect(
                                wp.getGame(),
                                wp.getTeam(),
                                totemStand.getLocation().add(0, 1, 0),
                                rad,
                                new CircumferenceEffect(Particle.VILLAGER_HAPPY, Particle.REDSTONE).particlesPerCircumference(1.5)
                        );
                        circle.playEffects();

                        // 1 / 1.35 / 1.7 / 2.05 / 2.4 / 2.75
                        int secondsElapsed = ticksElapsed / 20;
                        float healMultiplier = secondsElapsed == ((tickDuration / 20) - 1) ? 1f : (float) Math.pow((1 - healingIncrement / 100f), secondsElapsed);
                        PlayerFilter.entitiesAround(totemStand, rad, rad, rad)
                                    .aliveTeammatesOf(wp)
                                    .forEach(teammate -> {
                                        playersHealed++;
                                        teammate.addInstance(InstanceBuilder
                                                .healing()
                                                .ability(this)
                                                .source(wp)
                                                .min(healingValues.totemHealing.getMinValue() * healMultiplier)
                                                .max(healingValues.totemHealing.getMaxValue() * healMultiplier)
                                                .crit(healingValues.totemHealing)
                                        ).ifPresent(warlordsDamageHealingFinalEvent -> {
                                            tempHealingTotem.addAmountHealed(warlordsDamageHealingFinalEvent.getValue());
                                        });
                                    });

                        if (pveMasterUpgrade) {
                            PlayerFilter.entitiesAround(totemStand, rad, rad, rad)
                                        .aliveEnemiesOf(wp)
                                        .forEach(enemy -> {
                                            enemy.addSpeedModifier(wp, "Totem Slowness", -50, 20, "BASE");
                                            enemy.setDamageResistance(enemy.getSpec().getDamageResistance() - 5);
                                            if (enemy instanceof WarlordsNPC npc) {
                                                npc.setDamageResistance(npc.getSpec().getDamageResistance() - 5);
                                            }
                                            EffectUtils.playParticleLinkAnimation(enemy.getLocation(), totemStand.getLocation(), 255, 255, 255, 1);
                                            enemy.getCooldownManager().addCooldown(new RegularCooldown<>(
                                                    "Totem Crippling",
                                                    "CRIP",
                                                    HealingTotem.class,
                                                    tempHealingTotem,
                                                    wp,
                                                    CooldownTypes.DEBUFF,
                                                    cooldownManager -> {
                                                    },
                                                    20
                                            ) {
                                                @Override
                                                public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                                                    return currentDamageValue * .5f;
                                                }
                                            });
                                        });
                        }
                    }
                })
        );
        wp.getCooldownManager().addCooldown(healingTotemCooldown);

        if (inPve) {
            addSecondaryAbility(
                    1,
                    () -> {
                        Utils.playGlobalSound(totemStand.getLocation(), "paladin.hammeroflight.impact", 1.5f, 0.2f);
                        new FallingBlockWaveEffect(totemStand.getLocation().add(0, 1, 0), 7, 2, Material.SPRUCE_SAPLING).play();

                        PlayerFilter.entitiesAround(totemStand.getLocation(), rad, rad, rad)
                                    .aliveEnemiesOf(wp)
                                    .forEach((p) -> {
                                        playersCrippled++;
                                        wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN
                                                .append(Component.text(" Your Healing Totem has crippled ", NamedTextColor.GRAY))
                                                .append(Component.text(p.getName(), NamedTextColor.YELLOW))
                                                .append(Component.text("!", NamedTextColor.GRAY))
                                        );

                                        p.getCooldownManager().addCooldown(new RegularCooldown<>(
                                                "Totem Crippling",
                                                "CRIP",
                                                HealingTotem.class,
                                                tempHealingTotem,
                                                wp,
                                                CooldownTypes.DEBUFF,
                                                cooldownManager -> {
                                                },
                                                crippleDuration * 20
                                        ) {
                                            @Override
                                            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                                                return currentDamageValue * .75f;
                                            }
                                        });
                                    });
                    },
                    false,
                    secondaryAbility -> !wp.getCooldownManager().hasCooldown(healingTotemCooldown) || wp.isDead()
            );
        }

        if (pveMasterUpgrade2) {
            PlayerFilter.playingGame(wp.getGame())
                        .aliveTeammatesOfExcludingSelf(wp)
                        .forEach(warlordsEntity -> {
                            EarthlivingWeapon earthlivingWeapon = new EarthlivingWeapon();

                            warlordsEntity.getCooldownManager().addCooldown(new RegularCooldown<>(
                                    earthlivingWeapon.getName(),
                                    null,
                                    EarthlivingWeapon.Data.class,
                                    new EarthlivingWeapon.Data(),
                                    wp,
                                    CooldownTypes.ABILITY,
                                    cooldownManager -> {
                                    },
                                    tickDuration,
                                    Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                                        if (ticksElapsed % 4 == 0) {
                                            if (!tempHealingTotem.playerInsideTotem(warlordsEntity, rad)) {
                                                return;
                                            }
                                            EffectUtils.displayParticle(
                                                    Particle.VILLAGER_HAPPY,
                                                    wp.getLocation().add(0, 1.2, 0),
                                                    2,
                                                    0.3,
                                                    0.3,
                                                    0.3,
                                                    0.1
                                            );
                                        }
                                    })
                            ) {

                                @Override
                                public void onEndFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                                    if (!event.getCause().isEmpty()) {
                                        return;
                                    }
                                    if (!tempHealingTotem.playerInsideTotem(warlordsEntity, rad)) {
                                        return;
                                    }
                                    WarlordsEntity victim = event.getWarlordsEntity();
                                    WarlordsEntity attacker = event.getSource();

                                    earthlivingWeapon.activateEarthliving(victim, attacker, cooldownObject);
                                }
                            });
                        });

        }
    }

    public void addAmountHealed(float amount) {
        amountHealed += amount;
    }

    public float getAmountHealed() {
        return amountHealed;
    }

    private boolean playerInsideTotem(WarlordsEntity warlordsEntity, float radius) {
        return warlordsEntity.getLocation().distanceSquared(totem.getLocation()) <= radius * radius;
    }

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }

    public float getHealingIncrement() {
        return healingIncrement;
    }

    public void setHealingIncrement(float healingIncrement) {
        this.healingIncrement = healingIncrement;
    }

    @Override
    public FloatModifiable getHitBoxRadius() {
        return radius;
    }

    @Override
    public HealingValues getHealValues() {
        return healingValues;
    }

    public static class HealingValues implements Value.ValueHolder {

        private final Value.RangedValueCritable totemHealing = new Value.RangedValueCritable(621, 728, 25, 175);
        private final List<Value> values = List.of(totemHealing);

        @Override
        public List<Value> getValues() {
            return values;
        }

    }

}