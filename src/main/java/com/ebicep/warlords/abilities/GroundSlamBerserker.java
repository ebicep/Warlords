package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractGroundSlam;
import com.ebicep.warlords.abilities.internal.Damages;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.warrior.berserker.GroundSlamBranchBerserker;

import java.util.List;
import java.util.Set;

public class GroundSlamBerserker extends AbstractGroundSlam implements Damages<GroundSlamBerserker.DamageValues> {

    private final DamageValues damageValues = new DamageValues();

    public GroundSlamBerserker() {
        this(9.32f, 0);
    }

    public GroundSlamBerserker(float cooldown, float startCooldown) {
        super(cooldown, startCooldown);
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new GroundSlamBranchBerserker(abilityTree, this);
    }

    @Override
    public Value.RangedValueCritable getSlamDamage() {
        return damageValues.slamDamage;
    }

    @Override
    protected void onSecondSlamHit(WarlordsEntity wp, Set<WarlordsEntity> playersHit) {
        if (pveMasterUpgrade2) {
            float damageBoost = 1 + Math.min(5, playersHit.size()) * .05f;
            wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                    "Reverberation",
                    "REVERB",
                    GroundSlamBerserker.class,
                    new GroundSlamBerserker(),
                    wp,
                    CooldownTypes.BUFF,
                    cooldownManager -> {
                    },
                    5 * 20
            ) {
                @Override
                public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                    return currentDamageValue * damageBoost;
                }
            });
        }
    }

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
    }

    public static class DamageValues implements Value.ValueHolder {

        private final Value.RangedValueCritable slamDamage = new Value.RangedValueCritable(448.8f, 606.1f, 20, 175);
        private final List<Value> values = List.of(slamDamage);

        public Value.RangedValueCritable getSlamDamage() {
            return slamDamage;
        }

        @Override
        public List<Value> getValues() {
            return values;
        }

    }
}
