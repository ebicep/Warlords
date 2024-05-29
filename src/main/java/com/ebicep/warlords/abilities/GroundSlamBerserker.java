package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractGroundSlam;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.warrior.berserker.GroundSlamBranchBerserker;

import java.util.Set;

public class GroundSlamBerserker extends AbstractGroundSlam {

    public GroundSlamBerserker() {
        this(9.32f, 0);
    }

    public GroundSlamBerserker(float cooldown, float startCooldown) {
        this(448.8f, 606.1f, cooldown, startCooldown);
    }

    public GroundSlamBerserker(float minDamageHeal, float maxDamageHeal, float cooldown, float startCooldown) {
        super(minDamageHeal, maxDamageHeal, cooldown, 60, 20, 175, startCooldown);
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
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new GroundSlamBranchBerserker(abilityTree, this);
    }

}
