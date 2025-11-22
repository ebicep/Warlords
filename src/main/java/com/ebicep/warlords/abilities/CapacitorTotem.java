package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.achievements.types.ChallengeAchievements;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.FallingBlockWaveEffect;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.shaman.thunderlord.CapacitorTotemBranch;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CapacitorTotem extends AbstractTotem implements Duration, Damages<CapacitorTotem.DamageValues>, AbilityStats<CapacitorTotem, CapacitorTotem.CapacitorTotemStats> {

    private final CapacitorTotemStats stats = new CapacitorTotemStats();
    private final DamageValues damageValues = new DamageValues();
    private int tickDuration = 160;
    private float radius = 6;

    public CapacitorTotem() {
        super(AbstractAbilityBuilder.create("capacitorTotem").pvp());
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
        RegularCooldown<CapacitorTotemData> totemCooldown = new RegularCooldown<>(name, "TOTEM", CapacitorTotemData.class, data, wp, CooldownTypes.ABILITY, cooldownManager -> {
        }, cooldownManager -> {
            totemStand.remove();
        }, tickDuration, Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
            if (ticksElapsed % 5 != 0) {
                return;
            }
            if (!data.teamCarrierPassedThrough) {
                if (PlayerFilter.playingGame(wp.getGame())
                                .teammatesOfExcludingSelf(wp)
                                .stream()
                                .filter(WarlordsEntity::hasFlag)
                                .map(WarlordsEntity::getLocation)
                                .anyMatch(location -> location.distanceSquared(totemLocation) <= 1)) {
                    data.teamCarrierPassedThrough = true;
                }
            }
        })
        );
        totemCooldown.addModifier(Modifier.MODIFY_INCOMING_DAMAGE_AFTER_INTERVENE, (event, currentDamageValue) -> {
                    if (!pveMasterUpgrade2) {
                        return;
                    }
                    currentDamageValue.addMultiplicativeModifierMult(name, Math.max(.85f, 1 - (data.playersHit * .01f)));
                }
        );
        data.pulseDamage = () -> {
            double totemRadius = data.radius;
            PlayerFilter.entitiesAround(totemStand.getLocation(), totemRadius, totemRadius, totemRadius).aliveEnemiesOf(wp).forEach(warlordsPlayer -> {
                data.playersHit++;
                warlordsPlayer.addInstance(InstanceBuilder.damage().ability(this).source(wp).value(damageValues.totemDamage)).ifPresent(warlordsDamageHealingFinalEvent -> {
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
            FallingBlockWaveEffect.create(totemStand.getLocation().add(0, .75, 0), totemRadius, 5, Material.OAK_SAPLING);
        };
        wp.getCooldownManager().addCooldown(totemCooldown);
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.tickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("tickDuration"), int.class);
        this.radius = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("radius"), float.class);
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder.create(
                                                       "Place a highly conductive totem on the ground. Casting Chain Lightning or Lightning Rod on the totem will cause it to pulse, dealing ")
                                               .damage(damageValues.totemDamage)
                                               .text(" damage to all enemies within ")
                                               .blocks(radius)
                                               .text(". Lasts ")
                                               .durationTicks(tickDuration)
                                               .text(".")
                                               .build();
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new CapacitorTotemBranch(abilityTree, this);
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

    @Override
    public CapacitorTotemStats getAbilityStats() {
        return stats;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public static class DamageValues implements Value.ValueHolder {

        private Value.RangedValueCritable totemDamage = new Value.RangedValueCritable(404, 523, 20, 200);

        private List<Value> values = List.of(totemDamage);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.totemDamage = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldNameDamage("totemDamage"), Value.RangedValueCritable.class);
            this.values = List.of(totemDamage);
        }

        public Value.RangedValueCritable getTotemDamage() {
            return totemDamage;
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
            totem.stats.numberOfProcs++;
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

    public static class CapacitorTotemStats extends AbstractAbilityStats<CapacitorTotem, CapacitorTotemStats> {

        @Field("number_of_procs")
        private int numberOfProcs = 0;

        @Override
        public Class<CapacitorTotemStats> getClazz() {
            return CapacitorTotemStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Times Proc'd", numberOfProcs));
            return statsDisplay;
        }

        @Override
        public CapacitorTotemStats merge(CapacitorTotemStats other, int multiplier) {
            CapacitorTotemStats stats = super.merge(other, multiplier);
            stats.numberOfProcs = this.numberOfProcs + other.numberOfProcs * multiplier;
            return stats;
        }

        @Override
        public CapacitorTotemStats create() {
            return new CapacitorTotemStats();
        }

    }

}
