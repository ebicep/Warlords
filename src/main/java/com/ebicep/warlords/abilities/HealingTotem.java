package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractTotem;
import com.ebicep.warlords.abilities.internal.Duration;
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
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.shaman.earthwarden.HealingTotemBranch;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class HealingTotem extends AbstractTotem implements Duration {

    public int playersHealed = 0;
    public int playersCrippled = 0;

    protected float amountHealed = 0;

    private int radius = 7;
    private int tickDuration = 120;
    private int crippleDuration = 6;
    private int healingIncrement = 35;

    public HealingTotem() {
        super("Healing Totem", 191, 224, 62.64f, 60, 25, 175);
    }

    public HealingTotem(ArmorStand totem, WarlordsEntity owner) {
        super("Healing Totem", 191, 224, 62.64f, 60, 25, 175, totem, owner);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Place a totem on the ground that pulses constantly, healing nearby allies in a ")
                               .append(Component.text(radius, NamedTextColor.YELLOW))
                               .append(Component.text(" block radius for "))
                               .append(formatRangeHealing(minDamageHeal, maxDamageHeal))
                               .append(Component.text(" health every second. The healing will gradually increase by "))
                               .append(Component.text("35%", NamedTextColor.GREEN))
                               .append(Component.text(" (up to "))
                               .append(Component.text(Math.round(healingIncrement * tickDuration / 20f)))
                               .append(Component.text("%) every second. Lasts "))
                               .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(
                                       " seconds.\n\nPressing SHIFT or re-activating the ability causes your totem to pulse with immense force, crippling all enemies for "))
                               .append(Component.text(crippleDuration, NamedTextColor.GOLD))
                               .append(Component.text(" seconds. Crippled enemies deal "))
                               .append(Component.text("25%", NamedTextColor.RED))
                               .append(Component.text(" less damage."));

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
    protected void playSound(Player player, Location location) {
        Utils.playGlobalSound(location, "shaman.totem.activation", 2, 1);
    }

    @Override
    protected ItemStack getTotemItemStack() {
        return new ItemStack(Material.PINK_TULIP);
    }

    @Override
    protected void onActivation(WarlordsEntity wp, Player player, ArmorStand totemStand) {
        HealingTotem tempHealingTotem = new HealingTotem(totemStand, wp);
        AtomicInteger cooldownCounter = new AtomicInteger();
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

                    float healMultiplier = Math.min(1 + (convertToPercent(healingIncrement) * ((cooldownCounter.get() / 20f) + 1)), 3.1f);
                    PlayerFilter.entitiesAround(totemStand, radius, radius, radius)
                                .aliveTeammatesOf(wp)
                                .forEach((nearPlayer) -> {
                                    playersHealed++;
                                    nearPlayer.addHealingInstance(
                                            wp,
                                            name,
                                            minDamageHeal * healMultiplier,
                                            maxDamageHeal * healMultiplier,
                                            critChance,
                                            critMultiplier
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
                        EffectUtils.playSphereAnimation(totemStand.getLocation(), radius, Particle.VILLAGER_HAPPY, 2);
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
                                double width = radius;
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
                                radius,
                                new CircumferenceEffect(Particle.VILLAGER_HAPPY, Particle.REDSTONE).particlesPerCircumference(1.5)
                        );
                        circle.playEffects();

                        // 1 / 1.35 / 1.7 / 2.05 / 2.4 / 2.75
                        float healMultiplier = 1 + (convertToPercent(healingIncrement) * (ticksElapsed / 20f));
                        PlayerFilter.entitiesAround(totemStand, radius, radius, radius)
                                    .aliveTeammatesOf(wp)
                                    .forEach(teammate -> {
                                        playersHealed++;
                                        teammate.addHealingInstance(
                                                wp,
                                                name,
                                                minDamageHeal * healMultiplier,
                                                maxDamageHeal * healMultiplier,
                                                critChance,
                                                critMultiplier
                                        ).ifPresent(warlordsDamageHealingFinalEvent -> {
                                            tempHealingTotem.addAmountHealed(warlordsDamageHealingFinalEvent.getValue());
                                        });
                                    });

                        if (pveMasterUpgrade) {
                            PlayerFilter.entitiesAround(totemStand, radius, radius, radius)
                                        .aliveEnemiesOf(wp)
                                        .forEach(enemy -> {
                                            enemy.addSpeedModifier(wp, "Totem Slowness", -50, 20, "BASE");
                                            enemy.setDamageResistance(enemy.getSpec().getDamageResistance() - 5);
                                            if (enemy instanceof WarlordsNPC npc) {
                                                npc.setDamageResistancePrefix(npc.getDamageResistancePrefix() - 5);
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

        addSecondaryAbility(
                1,
                () -> {
                    Utils.playGlobalSound(totemStand.getLocation(), "paladin.hammeroflight.impact", 1.5f, 0.2f);
                    new FallingBlockWaveEffect(totemStand.getLocation().add(0, 1, 0), 7, 2, Material.SPRUCE_SAPLING).play();

                    PlayerFilter.entitiesAround(totemStand.getLocation(), radius, radius, radius)
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

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new HealingTotemBranch(abilityTree, this);
    }

    public void addAmountHealed(float amount) {
        amountHealed += amount;
    }

    public float getAmountHealed() {
        return amountHealed;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }

    public int getCrippleDuration() {
        return crippleDuration;
    }

    public void setCrippleDuration(int crippleDuration) {
        this.crippleDuration = crippleDuration;
    }

    public int getHealingIncrement() {
        return healingIncrement;
    }

    public void setHealingIncrement(int healingIncrement) {
        this.healingIncrement = healingIncrement;
    }
}