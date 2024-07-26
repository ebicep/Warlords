package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbilityDescriptionBuilder;
import com.ebicep.warlords.abilities.internal.AbstractStrike;
import com.ebicep.warlords.abilities.internal.Damages;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.rogue.vindicator.RighteousStrikeBranch;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class RighteousStrike extends AbstractStrike implements Damages<RighteousStrike.DamageValues> {

    public int silencedTargetStruck = 0;
    private final DamageValues damageValues = new DamageValues();
    private int abilityReductionInTicks = 16;
    private int additionalReductionInTicks = 4;
    private float vindicateCooldownReduction = 0.5f;
    private int targetsStruck = 0;

    public RighteousStrike() {
        super("Righteous Strike", 0, 70);
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder
                .create("Strike the targeted enemy for ")
                .damage(damageValues.strikeDamage)
                .text(" damage. Each strike reduces the duration of your struck target's active ability timers by ")
                .durationTicks(abilityReductionInTicks)
                .text(".")
                .emptyLine()
                .text("If your struck target is silenced, reduce the cooldown of your Vindicate by ")
                .durationSeconds(vindicateCooldownReduction)
                .text(" and reduce their active ability timers by ")
                .durationTicks((abilityReductionInTicks + additionalReductionInTicks))
                .text(" instead.")
                .build();
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Times Silenced Target Struck", "" + silencedTargetStruck));

        return info;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new RighteousStrikeBranch(abilityTree, this);
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
        nearPlayer.addInstance(InstanceBuilder
                .damage()
                .ability(this)
                .source(wp)
                .value(damageValues.strikeDamage)
        );

        if (nearPlayer.getCooldownManager().hasCooldown(SoulShackle.class)) {
            silencedTargetStruck++;
            nearPlayer.getCooldownManager().subtractTicksOnRegularCooldowns((int) (abilityReductionInTicks + additionalReductionInTicks), CooldownTypes.ABILITY);
            for (Vindicate vindicate : wp.getAbilitiesMatching(Vindicate.class)) {
                vindicate.subtractCurrentCooldown(vindicateCooldownReduction);
            }
        } else {
            nearPlayer.getCooldownManager().subtractTicksOnRegularCooldowns(abilityReductionInTicks, CooldownTypes.ABILITY);
        }

        if (pveMasterUpgrade || pveMasterUpgrade2) {
            if (pveMasterUpgrade) {
                SoulShackle.shacklePlayer(wp, nearPlayer, 120);
            }
            for (WarlordsEntity we : PlayerFilter
                    .entitiesAround(nearPlayer, 4, 4, 4)
                    .aliveEnemiesOf(wp)
                    .closestFirst(nearPlayer)
                    .excluding(nearPlayer)
                    .limit(4)
            ) {
                targetsStruck++;
                if (pveMasterUpgrade) {
                    SoulShackle.shacklePlayer(wp, we, 80);
                }
                we.addInstance(InstanceBuilder
                        .damage()
                        .ability(this)
                        .source(wp)
                        .value(damageValues.strikeDamage)
                );
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

    public static class DamageValues implements Value.ValueHolder {

        private final Value.RangedValueCritable strikeDamage = new Value.RangedValueCritable(334, 425, 20, 175);
        private final List<Value> values = List.of(strikeDamage);

        public Value.RangedValueCritable getStrikeDamage() {
            return strikeDamage;
        }

        @Override
        public List<Value> getValues() {
            return values;
        }

    }

}
