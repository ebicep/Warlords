package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractGroundSlam;
import com.ebicep.warlords.abilities.internal.Damages;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.warrior.defender.GroundSlamBranchDefender;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class GroundSlamDefender extends AbstractGroundSlam implements Damages<GroundSlamDefender.DamageValues> {

    public GroundSlamDefender() {
        super(326, 441, 8.3f, 0, 20, 175);
    }

    @Override
    protected void onSecondSlamHit(WarlordsEntity wp, Set<WarlordsEntity> playersHit) {
        if (pveMasterUpgrade2) {
            float damageReduction = 1 - Math.min(5, playersHit.size()) * .05f;
            wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                    "Reverberation",
                    "REVERB",
                    GroundSlamDefender.class,
                    new GroundSlamDefender(),
                    wp,
                    CooldownTypes.BUFF,
                    cooldownManager -> {
                    },
                    5 * 20
            ) {
                @Override
                public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                    return currentDamageValue * damageReduction;
                }
            });
        }
    }


    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new GroundSlamBranchDefender(abilityTree, this);
    }

    @Override
    protected void slamDamage(WarlordsEntity wp, WarlordsEntity slamTarget, float damageMultiplier, UUID abilityUUID) {
        slamTarget.addInstance(InstanceBuilder
                .damage()
                .ability(this)
                .source(wp)
                .min(damageValues.slamDamage.getMinValue() * damageMultiplier)
                .max(damageValues.slamDamage.getMaxValue() * damageMultiplier)
                .flag(InstanceFlags.TRUE_DAMAGE, trueDamage)
                .uuid(abilityUUID)
        );
    }

    private final DamageValues damageValues = new DamageValues();

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
    }

    public static class DamageValues implements Value.ValueHolder {

        private final Value.RangedValueCritable slamDamage = new Value.RangedValueCritable(326, 441, 20, 175);
        private final List<Value> values = List.of(slamDamage);

        @Override
        public List<Value> getValues() {
            return values;
        }

    }
}
