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
import com.ebicep.warlords.pve.upgrades.warrior.revenant.GroundSlamBranchRevenant;

import java.util.List;
import java.util.Set;

public class GroundSlamRevenant extends AbstractGroundSlam implements Damages<GroundSlamRevenant.DamageValues> {

    private final DamageValues damageValues = new DamageValues();

    public GroundSlamRevenant() {
        super(326, 441, 9.32f, 30, 35, 200);
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new GroundSlamBranchRevenant(abilityTree, this);
    }

    @Override
    public Value.RangedValueCritable getSlamDamage() {
        return damageValues.slamDamage;
    }

    @Override
    protected void onSecondSlamHit(WarlordsEntity wp, Set<WarlordsEntity> playersHit) {
        if (pveMasterUpgrade2) {
            float healingBoost = 1 + Math.min(5, playersHit.size()) * .05f;
            wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                    "Reverberation",
                    "REVERB",
                    GroundSlamRevenant.class,
                    new GroundSlamRevenant(),
                    wp,
                    CooldownTypes.BUFF,
                    cooldownManager -> {
                    },
                    5 * 20
            ) {
                @Override
                public float modifyHealingFromAttacker(WarlordsDamageHealingEvent event, float currentHealValue) {
                    return currentHealValue * healingBoost;
                }
            });
        }
    }

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
    }

    public static class DamageValues implements Value.ValueHolder {

        private final Value.RangedValueCritable slamDamage = new Value.RangedValueCritable(326, 441, 35, 200);
        private final List<Value> values = List.of(slamDamage);

        @Override
        public List<Value> getValues() {
            return values;
        }

    }
}
