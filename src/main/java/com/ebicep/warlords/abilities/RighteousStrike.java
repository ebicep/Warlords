package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.rogue.vindicator.RighteousStrikeBranch;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class RighteousStrike extends AbstractStrike<RighteousStrike, RighteousStrike.RighteousStrikeStats> implements Damages<RighteousStrike.DamageValues> {

    protected int targetsStruck = 0;
    private final RighteousStrikeStats stats = new RighteousStrikeStats();
    private final DamageValues damageValues = new DamageValues();
    private int abilityReductionInTicks = 16;
    private int additionalReductionInTicks = 4;
    private float prismGuardCooldownReduction = 0.5f;

    public RighteousStrike() {
        super(AbstractAbilityBuilder.create("righteousStrike").pvp());
    }

    public RighteousStrike(AbstractAbilityBuilder builder) {
        super(builder);
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.abilityReductionInTicks = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("abilityReductionInTicks"), int.class);
        this.additionalReductionInTicks = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("additionalReductionInTicks"), int.class);
        this.prismGuardCooldownReduction = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("prismGuardCooldownReduction"), float.class);
    }

    @Override
    protected void playSoundAndEffect(Location location) {
        Utils.playGlobalSound(location, "rogue.vindicatorstrike.activation", 2, 0.7f);
        Utils.playGlobalSound(location, "shaman.earthenspike.impact", 2, 2);
        randomHitEffect(location, 7, 255, 255, 255);
    }

    @Override
    protected boolean onHit(@Nonnull WarlordsEntity wp, @Nonnull WarlordsEntity nearPlayer) {
        targetsStruck++;
        nearPlayer.addInstance(InstanceBuilder.damage().ability(this).source(wp).value(damageValues.strikeDamage));
        boolean silenced = nearPlayer.getCooldownManager().hasCooldown(SoulShackle.class);
        new CooldownFilter<>(nearPlayer, RegularCooldown.class)
                .filter(regularCooldown -> regularCooldown.getCooldownType() == CooldownTypes.ABILITY)
                .forEach(regularCooldown -> {
                    String cooldownName = regularCooldown.getName();
                    if (cooldownName.equals("Ice Barrier") || cooldownName.equals("Ice Block") || cooldownName.equals("Lustrous Crown")) {
                        regularCooldown.subtractTime(8);
                    } else if (cooldownName.equals("Last Stand")) {
                        regularCooldown.subtractTime(8);
                        Object data = regularCooldown.getCooldownObject();
                        nearPlayer.getGame()
                                  .warlordsPlayers()
                                  .filter(warlordsPlayer -> warlordsPlayer.isTeammateAlive(nearPlayer) && warlordsPlayer != nearPlayer)
                                  .forEach(warlordsPlayer -> {
                                      new CooldownFilter<>(warlordsPlayer, RegularCooldown.class)
                                              .filterCooldownObject(data)
                                              .findAny()
                                              .ifPresent(cd -> {
                                                  cd.subtractTime(8);
                                              });
                                  });
                    } else {
                        regularCooldown.subtractTime(abilityReductionInTicks + (silenced ? additionalReductionInTicks : 0));
                    }
                });
        if (silenced) {
            stats.silencedTargetStruck++;
            for (PrismGuard prismGuard : wp.getAbilitiesMatching(PrismGuard.class)) {
                prismGuard.subtractCurrentCooldown(prismGuardCooldownReduction);
            }
        }
        if (pveMasterUpgrade || pveMasterUpgrade2) {
            if (pveMasterUpgrade) {
                SoulShackle.shacklePlayer(wp, nearPlayer, 120);
            }
            for (WarlordsEntity we : PlayerFilter.entitiesAround(nearPlayer, 4, 4, 4).aliveEnemiesOf(wp).closestFirst(nearPlayer).excluding(nearPlayer).limit(4)) {
                targetsStruck++;
                if (pveMasterUpgrade) {
                    SoulShackle.shacklePlayer(wp, we, 80);
                }
                we.addInstance(InstanceBuilder.damage().ability(this).source(wp).value(damageValues.strikeDamage));
                if (pveMasterUpgrade2 && targetsStruck % 5 == 0) {
                    wp.getAbilitiesMatching(SoulShackle.class).forEach(soulShackle -> soulShackle.subtractCurrentCooldown(.5f));
                    playCooldownReductionEffect(we);
                }
            }
        }
        return true;
    }

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
    }

    @Override
    public RighteousStrikeStats getAbilityStats() {
        return stats;
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder.create("Strike the targeted enemy for ")
                                               .damage(damageValues.strikeDamage)
                                               .text(" damage. Each strike reduces the duration of your struck target's active ability timers by ")
                                               .durationTicks(abilityReductionInTicks)
                                               .text(".")
                                               .emptyLine()
                                               .text("If your struck target is silenced, reduce the cooldown of your Prism Guard by ")
                                               .durationSeconds(prismGuardCooldownReduction)
                                               .text(" and reduce their active ability timers by ")
                                               .durationTicks((abilityReductionInTicks + additionalReductionInTicks))
                                               .text(" instead.")
                                               .build();
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new RighteousStrikeBranch(abilityTree, this);
    }

    public static class DamageValues implements Value.ValueHolder {

        private Value.RangedValueCritable strikeDamage = new Value.RangedValueCritable(334, 425, 20, 175);

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

    public static class RighteousStrikeStats extends AbstractStrikeStats<RighteousStrike, RighteousStrikeStats> {

        @Field("silenced_target_struck")
        private int silencedTargetStruck = 0;

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Times Silenced Target Struck", silencedTargetStruck));
            return statsDisplay;
        }

        @Override
        public RighteousStrikeStats merge(RighteousStrikeStats other, int multiplier) {
            RighteousStrikeStats stats = super.merge(other, multiplier);
            stats.silencedTargetStruck = this.silencedTargetStruck + other.silencedTargetStruck * multiplier;
            return stats;
        }

        @Override
        public Class<RighteousStrikeStats> getClazz() {
            return RighteousStrikeStats.class;
        }

        @Override
        public RighteousStrikeStats create() {
            return new RighteousStrikeStats();
        }

    }

}
