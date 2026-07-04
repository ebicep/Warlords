package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.type.CustomInstanceFlags;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.paladin.crusader.CrusadersStrikeBranch;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class CrusadersStrike extends AbstractStrike<CrusadersStrike, CrusadersStrike.CrusadersStrikeStats> implements Damages<CrusadersStrike.DamageValues> {

    private final CrusadersStrikeStats stats = new CrusadersStrikeStats();
    private final DamageValues damageValues = new DamageValues();
    private int energyGiven = 21;
    private int energyRadius = 10;
    private int energyMaxAllies = 2;
    private int allySpeedBoost = 40;
    private int allySpeedBoostDurationInTicks = 20;
    private boolean blockedByArcaneShield = true;
    private boolean grantEnergyToLinkedAllyRegardlessOfRange = false;

    public CrusadersStrike() {
        super(AbstractAbilityBuilder.create("crusadersStrike").pvp());
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.energyGiven = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("energyGiven"), int.class);
        this.energyRadius = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("energyRadius"), int.class);
        this.energyMaxAllies = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("energyMaxAllies"), int.class);
        this.allySpeedBoost = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("allySpeedBoost"), int.class);
        this.allySpeedBoostDurationInTicks = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("allySpeedBoostDurationInTicks"), int.class);
        this.blockedByArcaneShield = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("blockedByArcaneShield"), boolean.class);
    }

    @Override
    protected boolean onHit(@Nonnull WarlordsEntity wp, @Nonnull WarlordsEntity nearPlayer) {
        boolean crit = false;
        Optional<WarlordsDamageHealingFinalEvent> finalEvent = nearPlayer.addInstance(InstanceBuilder
                .damage().ability(this)
                .source(wp)
                .value(damageValues.strikeDamage)
        );
        if (finalEvent.isPresent()) {
            crit = finalEvent.get().isCrit();
        }
        if (pveMasterUpgrade) {
            additionalHit(
                    3,
                    wp,
                    nearPlayer,
                    warlordsEntity -> {
                        warlordsEntity.addInstance(InstanceBuilder
                                .damage()
                                .ability(this)
                                .source(wp)
                                .value(damageValues.strikeDamage)
                        );
                    }
            );
            PlayerFilter.entitiesAround(wp, energyRadius, energyRadius, energyRadius)
                    .aliveTeammatesOfExcludingSelf(wp).limit(2)
                    .forEach(teammate -> {
                        teammate.addSpeedModifier(wp, "Crusading Strike", 10, 40);
                    });
        } else if (pveMasterUpgrade2) {
            PlayerFilter.entitiesAround(wp, energyRadius, energyRadius, energyRadius)
                    .aliveTeammatesOfExcludingSelf(wp).limit(2)
                    .forEach(teammate -> {
                        teammate.addSpeedModifier(wp, "Crusading Strike", 10, 40);
                    });
        }
        if (finalEvent.isPresent()) {
            WarlordsDamageHealingFinalEvent event = finalEvent.get();
            List<CustomInstanceFlags> customFlags = event.getCustomFlags();
            if (blockedByArcaneShield && customFlags.stream().anyMatch(f ->
                    f instanceof CustomInstanceFlags.InstanceShieldsInstanceFlag(List<Shield> shields) &&
                            shields.stream().anyMatch(shield -> shield.getName().equals("Arcane Shield")))
            ) {
                return true;
            }
            float previousEnergyGiven = stats.totalEnergyGiven;
            List<WarlordsEntity> energyTargets;
            if (grantEnergyToLinkedAllyRegardlessOfRange) {
                Optional<WarlordsEntity> linkedAlly = findLinkedMarkedAlly(wp);
                if (linkedAlly.isPresent()) {
                    WarlordsEntity linked = linkedAlly.get();
                    List<WarlordsEntity> targets = new ArrayList<>();
                    targets.add(linked);
                    int remaining = energyMaxAllies - 1;
                    if (remaining > 0) {
                        getNearbyEnergyTargets(wp).stream()
                                .filter(ally -> !ally.equals(linked))
                                .limit(remaining)
                                .forEach(targets::add);
                    }
                    energyTargets = targets;
                } else {
                    energyTargets = getNearbyEnergyTargets(wp);
                }
            } else {
                energyTargets = getNearbyEnergyTargets(wp);
            }
            for (WarlordsEntity energyTarget : energyTargets) {
                if (energyTarget.getCooldownManager().hasCooldown(HolyRadianceCrusader.class)) {
                    // 20 ticks
                    energyTarget.addSpeedModifier(wp, "CRUSADER MARK", allySpeedBoost, allySpeedBoostDurationInTicks);
                }
                stats.totalEnergyGiven += energyTarget.addEnergy(wp, name, energyGiven + (pveMasterUpgrade2 && crit ? 5 : 0));
            }
            new CooldownFilter<>(wp, RegularCooldown.class)
                    .filterCooldownFrom(wp)
                    .filterCooldownClassAndMapToObjectsOfClass(InspiringPresence.InspiringPresenceData.class)
                    .forEach(inspiringPresence -> inspiringPresence.addEnergyGivenFromStrikeAndPresence(stats.totalEnergyGiven - previousEnergyGiven));
        }
        return true;
    }

    @Override
    protected void playSoundAndEffect(Location location) {
        Utils.playGlobalSound(location, "paladin.paladinstrike.activation", 2, 1);
        randomHitEffect(location, 5, 255, 0, 0);
        EffectUtils.displayParticle(Particle.EFFECT,
                location.clone().add(0, 1, 0),
                4,
                (float) ((Math.random() * 2) - 1),
                (float) ((Math.random() * 2) - 1),
                (float) ((Math.random() * 2) - 1),
                1
        );
    }

    public void setBlockedByArcaneShield(boolean blockedByArcaneShield) {
        this.blockedByArcaneShield = blockedByArcaneShield;
    }

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
    }

    @Override
    public CrusadersStrikeStats getAbilityStats() {
        return stats;
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder.create("Strike the targeted enemy player, causing ")
                                               .damage(damageValues.strikeDamage)
                                               .text(" damage and restoring ")
                                               .energy(energyGiven)
                                               .text(" to ")
                                               .text(energyMaxAllies, NamedTextColor.BLUE)
                                               .text(" nearby allies within ")
                                               .blocks(energyRadius)
                                               .text(".")
                                               .emptyLine()
                                               .text("Allies with ")
                                               .text("MARK", NamedTextColor.DARK_GREEN)
                                               .text(" get priority in restoring energy and gain ")
                                               .percent(allySpeedBoost, NamedTextColor.WHITE)
                                               .text(" movement speed for ")
                                               .durationTicks(allySpeedBoostDurationInTicks)
                                               .text(".")
                                               .build();
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new CrusadersStrikeBranch(abilityTree, this);
    }

    public int getEnergyMaxAllies() {
        return energyMaxAllies;
    }

    public void setEnergyMaxAllies(int energyMaxAllies) {
        this.energyMaxAllies = energyMaxAllies;
    }

    public int getEnergyGiven() {
        return energyGiven;
    }

    public void setEnergyGiven(int energyGiven) {
        this.energyGiven = energyGiven;
    }

    public int getEnergyRadius() {
        return energyRadius;
    }

    public void setEnergyRadius(int energyRadius) {
        this.energyRadius = energyRadius;
    }

    public boolean isGrantEnergyToLinkedAllyRegardlessOfRange() {
        return grantEnergyToLinkedAllyRegardlessOfRange;
    }

    public void setGrantEnergyToLinkedAllyRegardlessOfRange(boolean grantEnergyToLinkedAllyRegardlessOfRange) {
        this.grantEnergyToLinkedAllyRegardlessOfRange = grantEnergyToLinkedAllyRegardlessOfRange;
    }

    private Optional<WarlordsEntity> findLinkedMarkedAlly(@Nonnull WarlordsEntity wp) {
        return PlayerFilter.playingGame(wp.getGame())
                .aliveTeammatesOfExcludingSelf(wp)
                .filter(teammate -> new CooldownFilter<>(teammate, AbstractCooldown.class)
                        .filterCooldownClass(HolyRadianceCrusader.class)
                        .filterCooldownFrom(wp)
                        .stream()
                        .findAny()
                        .isPresent())
                .findFirst();
    }

    private List<WarlordsEntity> getNearbyEnergyTargets(@Nonnull WarlordsEntity wp) {
        return PlayerFilter
                .entitiesAround(wp, energyRadius, energyRadius, energyRadius)
                .aliveTeammatesOfExcludingSelf(wp)
                .sorted(Comparator
                        .comparing((WarlordsEntity p) ->
                                new CooldownFilter<>(p, AbstractCooldown.class)
                                        .filterCooldownClass(HolyRadianceCrusader.class)
                                        .filterCooldownFrom(wp)
                                        .stream()
                                        .findAny()
                                        .isPresent() ? -1 : 1
                        )
                        .thenComparing(LocationUtils.sortClosestBy(WarlordsEntity::getLocation, wp.getLocation())))
                .limit(energyMaxAllies)
                .toList();
    }

    public static class DamageValues implements Value.ValueHolder {

        private Value.RangedValueCritable strikeDamage = new Value.RangedValueCritable(326, 441, 20, 175);

        private List<Value> values = List.of(strikeDamage);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.strikeDamage = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldNameDamage("strikeDamage"), Value.RangedValueCritable.class);
            this.values = List.of(strikeDamage);
        }

        public Value.RangedValueCritable getStrikeDamage() {
            return strikeDamage;
        }

    }

    public static class CrusadersStrikeStats extends AbstractStrikeStats<CrusadersStrike, CrusadersStrikeStats> {

        @Field("total_energy_given")
        private float totalEnergyGiven = 0;

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Total Energy Given", NumberFormat.addCommaAndRound(totalEnergyGiven)));
            return statsDisplay;
        }

        @Override
        public CrusadersStrikeStats merge(CrusadersStrikeStats other, int multiplier) {
            CrusadersStrikeStats stats = super.merge(other, multiplier);
            stats.totalEnergyGiven = this.totalEnergyGiven + other.totalEnergyGiven * multiplier;
            return stats;
        }

        @Override
        public Class<CrusadersStrikeStats> getClazz() {
            return CrusadersStrikeStats.class;
        }

        @Override
        public CrusadersStrikeStats create() {
            return new CrusadersStrikeStats();
        }

    }

}
