package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractTotem;
import com.ebicep.warlords.abilities.internal.Damages;
import com.ebicep.warlords.abilities.internal.Duration;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.achievements.types.ChallengeAchievements;
import com.ebicep.warlords.effects.FallingBlockWaveEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.shaman.thunderlord.CapacitorTotemBranch;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class CapacitorTotem extends AbstractTotem implements Duration, Damages<CapacitorTotem.DamageValues> {

    public int numberOfProcs = 0;

    private final DamageValues damageValues = new DamageValues();
    private int tickDuration = 160;
    private double radius = 6;
    private int playersHit = 0;

    public CapacitorTotem() {
        super("Capacitor Totem", 62.64f, 20);
    }

    public CapacitorTotem(ArmorStand totem, WarlordsEntity owner) {
        super("Capacitor Totem", 62.64f, 20, totem, owner);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Place a highly conductive totem on the ground. Casting Chain Lightning or Lightning Rod on the totem will cause it to pulse, dealing ")
                               .append(Damages.formatDamage(damageValues.totemDamage))
                               .append(Component.text(" damage to all enemies in a "))
                               .append(Component.text(format(radius), NamedTextColor.YELLOW))
                               .append(Component.text(" block radius. Lasts "))
                               .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds."));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Times Proc'd", "" + numberOfProcs));

        return info;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new CapacitorTotemBranch(abilityTree, this);
    }

    @Override
    protected void playSound(WarlordsEntity warlordsEntity, Location location) {
        Utils.playGlobalSound(location, "shaman.totem.activation", 2, 1);
    }

    @Override
    protected ItemStack getTotemItemStack() {
        return new ItemStack(Material.RED_TULIP);
    }

    @Override
    protected void onActivation(WarlordsEntity wp, ArmorStand totemStand) {
        Location totemLocation = wp.getLocation().clone();

        CapacitorTotemData data = new CapacitorTotemData(this, wp, totemStand);
        RegularCooldown<CapacitorTotemData> totemCooldown = new RegularCooldown<>(
                name,
                "TOTEM",
                CapacitorTotemData.class,
                data,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                cooldownManager -> {
                    totemStand.remove();
                },
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 5 != 0) {
                        return;
                    }
                    if (!data.teamCarrierPassedThrough) {
                        if (PlayerFilter.playingGame(wp.getGame())
                                        .teammatesOfExcludingSelf(wp)
                                        .stream()
                                        .filter(WarlordsEntity::hasFlag)
                                        .map(WarlordsEntity::getLocation)
                                        .anyMatch(location -> location.distanceSquared(totemLocation) <= 1)
                        ) {
                            data.teamCarrierPassedThrough = true;
                        }
                    }
                })
        ) {
            @Override
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                if (!pveMasterUpgrade2) {
                    return currentDamageValue;
                }
                return currentDamageValue * Math.max(.85f, 1 - (data.playersHit * .01f));
            }
        };
        data.pulseDamage = () -> {
            double totemRadius = data.radius;
            PlayerFilter.entitiesAround(totemStand.getLocation(), totemRadius, totemRadius, totemRadius)
                        .aliveEnemiesOf(wp)
                        .forEach(warlordsPlayer -> {
                            playersHit++;
                            data.playersHit++;
                            warlordsPlayer.addInstance(InstanceBuilder
                                    .damage()
                                    .ability(this)
                                    .source(wp)
                                    .value(damageValues.totemDamage)
                            ).ifPresent(warlordsDamageHealingFinalEvent -> {
                                if (warlordsDamageHealingFinalEvent.isDead()) {
                                    if (++data.playersKilledWithFinalHit >= 15) {
                                        ChallengeAchievements.checkForAchievement(wp, ChallengeAchievements.LIGHTNING_EXECUTION);
                                    }
                                }
                            });

                            if (pveMasterUpgrade) {
                                float damageResistance = warlordsPlayer.getSpec().getDamageResistance();
                                warlordsPlayer.setDamageResistance(damageResistance - 20);
                            }
                        });

            if (pveMasterUpgrade) {
                data.radius += .5;
            } else if (pveMasterUpgrade2 && data.timesTotemIncreased < 20) {
                data.timesTotemIncreased++;
                totemCooldown.setTicksLeft(totemCooldown.getTicksLeft() + 10);
            }

            new FallingBlockWaveEffect(totemStand.getLocation().add(0, .75, 0), totemRadius, 1.2, Material.OAK_SAPLING).play();
        };
        wp.getCooldownManager().addCooldown(totemCooldown);
    }


    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public int getPlayersHit() {
        return playersHit;
    }

    public void setPlayersHit(int playersHit) {
        this.playersHit = playersHit;
    }

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
    }

    public static class DamageValues implements Value.ValueHolder {

        private final Value.RangedValueCritable totemDamage = new Value.RangedValueCritable(404, 523, 20, 200);
        private final List<Value> values = List.of(totemDamage);

        public Value.RangedValueCritable getTotemDamage() {
            return totemDamage;
        }

        @Override
        public List<Value> getValues() {
            return values;
        }

    }

    public static class CapacitorTotemData extends TotemData<CapacitorTotem> {

        private boolean teamCarrierPassedThrough = false;
        private Runnable pulseDamage;
        private double radius = 6;
        private int timesTotemIncreased = 0;
        private int numberOfProcsAfterCarrierPassed = 0;
        private int playersKilledWithFinalHit = 0;
        private int playersHit = 0;

        public CapacitorTotemData(CapacitorTotem totem, WarlordsEntity owner, ArmorStand armorStand) {
            super(totem, owner, armorStand);
        }

        public void proc() {
            totem.numberOfProcs++;
            pulseDamage.run();
            if (teamCarrierPassedThrough) {
                numberOfProcsAfterCarrierPassed++;
            }
        }

        public int getNumberOfProcsAfterCarrierPassed() {
            return numberOfProcsAfterCarrierPassed;
        }

        public double getRadius() {
            return radius;
        }

        public void setRadius(double radius) {
            this.radius = radius;
        }
    }

}
