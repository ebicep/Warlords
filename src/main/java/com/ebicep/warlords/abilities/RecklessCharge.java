package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.RedAbilityIcon;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.mobs.flags.Unimmobilizable;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.warrior.revenant.RecklessChargeBranch;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class RecklessCharge extends AbstractAbility implements RedAbilityIcon, Listener, Damages<RecklessCharge.DamageValues>, AbilityStats<RecklessCharge, RecklessCharge.RecklessChargeStats>, CanReduceCooldowns {


    private final DamageValues damageValues = new DamageValues();
    private final RecklessChargeStats stats = new RecklessChargeStats();
    private int stunTimeInTicks = 10;
    private int additionalBlocks = 0;

    public RecklessCharge() {
        super("Reckless Charge", 9.5f, 60);
    }

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder
                .create("Charge forward, dealing ")
                .damage(damageValues.chargeDamage)
                .text(" damage to all enemies you pass through. Enemies hit are ")
                .text("IMMOBILIZED", NamedTextColor.DARK_PURPLE)
                .text(", preventing movement for ")
                .durationTicks(stunTimeInTicks)
                .text(".")
                .build();

    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp) {

        Utils.playGlobalSound(wp.getLocation(), "warrior.seismicwave.activation", 2, 1);

        if (pveMasterUpgrade || pveMasterUpgrade2) {
            wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                    name,
                    pveMasterUpgrade ? "Reckless Rampage" : "Reverberation",
                    RecklessCharge.class,
                    null,
                    wp,
                    CooldownTypes.ABILITY,
                    cooldownManager -> {
                    },
                    2 * 20
            ) {
                @Override
                public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                    return currentDamageValue * 0.2f;
                }
            });
        }

        Location location = wp.getLocation();
        location.setPitch(0);
        Location chargeLocation = location.clone();
        double chargeDistance;
        List<WarlordsEntity> playersHit = new ArrayList<>();
        playersHit.add(wp);
        boolean inAir = false;

        if (location.getWorld().getBlockAt(location.clone().add(0, -1, 0)).getType() != Material.AIR) {
            inAir = true;
            //travels 5 blocks
            chargeDistance = 5;
        } else {
            //travels 7 at peak jump
            chargeDistance = Math.max(Math.min(LocationUtils.getDistance(wp, .1) * 5, 7.2), 6.3);
        }
        chargeDistance += additionalBlocks;

        boolean finalInAir = inAir;
        double finalChargeDistance = chargeDistance;

        new GameRunnable(wp.getGame()) {
            //safety precaution
            int maxChargeDuration = 5;

            @Override
            public void run() {
                if (maxChargeDuration == 5) {
                    if (finalInAir) {
                        wp.setVelocity(name, location.getDirection().multiply(2).setY(.2), true);
                    } else {
                        wp.setVelocity(name, location.getDirection().multiply(1.5).setY(.2), true);
                    }
                }
                //cancel charge if hit a block, making the player stand still
                if (wp.getLocation().distanceSquared(chargeLocation) > finalChargeDistance * finalChargeDistance ||
                        (wp.getEntity().getVelocity().getX() == 0 && wp.getEntity().getVelocity().getZ() == 0) ||
                        maxChargeDuration <= 0
                ) {
                    wp.setVelocity(name, new Vector(0, 0, 0), true);
                    this.cancel();
                }
                for (int i = 0; i < 4; i++) {
                    wp.getLocation().getWorld().spawnParticle(
                            Particle.REDSTONE,
                            wp.getLocation().clone().add((Math.random() * 1.5) - .75, .5 + (Math.random() * 2) - 1, (Math.random() * 1.5) - .75),
                            1,
                            0,
                            0,
                            0,
                            0,
                            new Particle.DustOptions(Color.fromRGB(255, 0, 0), 1),
                            true
                    );
                }
                PlayerFilter.entitiesAround(wp, 2.5, 5, 2.5)
                            .excluding(playersHit)
                            .forEach(otherPlayer -> {
                                playersHit.add(otherPlayer);

                                if (otherPlayer.isEnemyAlive(wp)) {
                                    stats.targetsCharged++;
                                    float damageMultiplier = pveMasterUpgrade2 && otherPlayer.getCooldownManager().hasCooldown(CripplingStrike.class) ? 1.75f : 1;
                                    otherPlayer.addInstance(InstanceBuilder
                                            .damage()
                                            .ability(RecklessCharge.this)
                                            .source(wp)
                                            .min(damageValues.chargeDamage.getMinValue() * damageMultiplier)
                                            .max(damageValues.chargeDamage.getMaxValue() * damageMultiplier)
                                            .crit(damageValues.chargeDamage)
                                    );

                                    if (otherPlayer instanceof WarlordsNPC warlordsNPC && !(warlordsNPC.getMob() instanceof Unimmobilizable)) {
                                        warlordsNPC.setStunTicks(getStunTimeInTicks());
                                    } else if (otherPlayer instanceof WarlordsPlayer warlordsPlayer) {
                                        warlordsPlayer.stun();
                                        new GameRunnable(wp.getGame()) {
                                            @Override
                                            public void run() {
                                                warlordsPlayer.unstun();
                                            }
                                        }.runTaskLater(getStunTimeInTicks());
                                        otherPlayer.getEntity().showTitle(Title.title(
                                                Component.empty(),
                                                Component.text("IMMOBILIZED", NamedTextColor.LIGHT_PURPLE),
                                                Title.Times.times(Ticks.duration(0), Ticks.duration(stunTimeInTicks), Ticks.duration(0))
                                        ));
                                    }
                                    otherPlayer.getCooldownManager().addCooldown(new RegularCooldown<>(
                                            "Reckless Rampage",
                                            "RECK",
                                            RecklessCharge.class,
                                            null,
                                            wp,
                                            CooldownTypes.ABILITY,
                                            cooldownManager -> {
                                            },
                                            getStunTimeInTicks()
                                    ) {
                                        @Override
                                        public float modifyDamageBeforeInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                                            if (event.getCause().contains("Strike")) {
                                                return currentDamageValue * 1.15f;
                                            }
                                            return currentDamageValue;
                                        }
                                    });
                                } else if (pveMasterUpgrade2 && otherPlayer.isTeammateAlive(wp)) {
                                    otherPlayer.getCooldownManager().addCooldown(new RegularCooldown<>(
                                            "Probiotic",
                                            "PROBIO",
                                            RecklessCharge.class,
                                            null,
                                            wp,
                                            CooldownTypes.ABILITY,
                                            cooldownManager -> {
                                            },
                                            8 * 20
                                    ) {
                                        @Override
                                        public float modifyHealingFromSelf(WarlordsDamageHealingEvent event, float currentHealValue) {
                                            return currentHealValue * 2;
                                        }
                                    });
                                    new CooldownFilter<>(otherPlayer, RegularCooldown.class)
                                            .filter(cd -> cd.getCooldownType() != CooldownTypes.DEBUFF)
                                            .forEach(cd -> cd.setTicksLeft(cd.getTicksLeft() + 40));
                                    EffectUtils.displayParticle(
                                            Particle.HEART,
                                            otherPlayer.getLocation().add(0, 2, 0),
                                            10,
                                            .5,
                                            .25,
                                            .5,
                                            0
                                    );
                                }
                            });

                maxChargeDuration--;
            }

        }.runTaskTimer(1, 0);

        return true;
    }

    @Override
    public boolean canReduceCooldowns() {
        return pveMasterUpgrade2;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new RecklessChargeBranch(abilityTree, this);
    }

    public int getStunTimeInTicks() {
        return stunTimeInTicks;
    }

    public void setStunTimeInTicks(int stunTimeInTicks) {
        this.stunTimeInTicks = stunTimeInTicks;
    }

    @Override
    public RecklessChargeStats getAbilityStats() {
        return stats;
    }

    public int getAdditionalBlocks() {
        return additionalBlocks;
    }

    public void setAdditionalBlocks(int additionalBlocks) {
        this.additionalBlocks = additionalBlocks;
    }

    public static class DamageValues implements Value.ValueHolder {

        private final Value.RangedValueCritable chargeDamage = new Value.RangedValueCritable(457, 601, 20, 200);
        private final List<Value> values = List.of(chargeDamage);

        public Value.RangedValueCritable getChargeDamage() {
            return chargeDamage;
        }

        @Override
        public List<Value> getValues() {
            return values;
        }

    }

    public static class RecklessChargeStats extends AbstractAbilityStats<RecklessCharge, RecklessChargeStats> {

        @Field("targets_charged")
        private int targetsCharged = 0;

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Targets Charged", targetsCharged));
            return statsDisplay;
        }

        @Override
        public RecklessChargeStats merge(RecklessChargeStats other, int multiplier) {
            RecklessChargeStats stats = super.merge(other, multiplier);
            stats.targetsCharged = this.targetsCharged + other.targetsCharged * multiplier;
            return stats;
        }

        @Override
        public Class<RecklessChargeStats> getClazz() {
            return RecklessChargeStats.class;
        }

        @Override
        public RecklessChargeStats create() {
            return new RecklessChargeStats();
        }
    }
}