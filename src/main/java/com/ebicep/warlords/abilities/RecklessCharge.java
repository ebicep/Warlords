package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.Damages;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.abilities.internal.icon.RedAbilityIcon;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.mobs.flags.Unimmobilizable;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.warrior.revenant.RecklessChargeBranch;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.java.Pair;
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

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class RecklessCharge extends AbstractAbility implements RedAbilityIcon, Listener, Damages<RecklessCharge.DamageValues> {

    public int playersCharged = 0;
    private final DamageValues damageValues = new DamageValues();
    private int stunTimeInTicks = 10;

    public RecklessCharge() {
        super("Reckless Charge", 457, 601, 9.32f, 60, 20, 200);
    }

    public DamageValues getDamageValues() {
        return damageValues;
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Charge forward, dealing ")
                               .append(formatRangeDamage(minDamageHeal, maxDamageHeal))
                               .append(Component.text(" damage to all enemies you pass through. Enemies hit are "))
                               .append(Component.text("IMMOBILIZED", NamedTextColor.DARK_PURPLE))
                               .append(Component.text(", preventing movement for "))
                               .append(Component.text((stunTimeInTicks / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds."));

    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Players Charged", "" + playersCharged));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp) {

        Utils.playGlobalSound(wp.getLocation(), "warrior.seismicwave.activation", 2, 1);

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
            chargeDistance = Math.max(Math.min(LocationUtils.getDistance(wp, .1) * 5, 6.9), 6);
        }

        boolean finalInAir = inAir;
        double finalChargeDistance = chargeDistance;

        new GameRunnable(wp.getGame()) {
            //safety precaution
            int maxChargeDuration = 5;
            int timesArmyReduced = 0;

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
                                    playersCharged++;
                                    float damageMultiplier = pveMasterUpgrade2 && otherPlayer.getCooldownManager().hasCooldown(CripplingStrike.class) ? 1.75f : 1;
                                    otherPlayer.addInstance(InstanceBuilder
                                            .damage()
                                            .ability(RecklessCharge.this)
                                            .source(wp)
                                            .min(damageValues.chargeDamage.getMinValue() * damageMultiplier)
                                            .max(damageValues.chargeDamage.getMaxValue() * damageMultiplier)
                                            .crit(damageValues.chargeDamage)
                                    ).ifPresent(finalEvent -> {
                                        if (pveMasterUpgrade2 && finalEvent.isDead() && timesArmyReduced < 5) {
                                            timesArmyReduced++;
                                            wp.getAbilitiesMatching(UndyingArmy.class).forEach(ability -> ability.subtractCurrentCooldown(1f));
                                            playCooldownReductionEffect(otherPlayer);
                                        }
                                    });

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
                                } else if ((pveMasterUpgrade || pveMasterUpgrade2) && otherPlayer.isTeammateAlive(wp)) {
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
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new RecklessChargeBranch(abilityTree, this);
    }

    public int getStunTimeInTicks() {
        return stunTimeInTicks;
    }

    public void setStunTimeInTicks(int stunTimeInTicks) {
        this.stunTimeInTicks = stunTimeInTicks;
    }

    public static class DamageValues implements Value.ValueHolder {

        private final Value.RangedValueCritable chargeDamage = new Value.RangedValueCritable(457, 601, 20, 200);
        private final List<Value> values = List.of(chargeDamage);

        @Override
        public List<Value> getValues() {
            return values;
        }

    }
}
