package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.omega;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AppliesToWarlordsPlayer;
import com.ebicep.warlords.util.warlords.PlayerFilterGeneric;

import java.util.Set;

public class NaturesClaws extends SpecialOmegaGauntlet implements AppliesToWarlordsPlayer {
    public NaturesClaws(Set<BasicStatPool> statPool) {
        super(statPool);
    }

    public NaturesClaws() {

    }

    @Override
    public String getName() {
        return "Nature's Claws";
    }

    @Override
    public String getBonus() {
        return "For every currently dead ally, gain +10% Damage, +5% Damage Reduction, and +10% Healing.";
    }

    @Override
    public String getDescription() {
        return "Survival of the fittest, huh?";
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption) {
        warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                "Natures Claws",
                null,
                NaturesClaws.class,
                null,
                warlordsPlayer,
                CooldownTypes.ITEM,
                cooldownManager -> {

                },
                false
        ) {

            @Override
            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                return currentDamageValue * (1 + getCurrentlyDeadAllies(warlordsPlayer) * 0.1f);
            }

            @Override
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                return currentDamageValue * (1 - getCurrentlyDeadAllies(warlordsPlayer) * 0.05f);
            }

            @Override
            public float doBeforeHealFromAttacker(WarlordsDamageHealingEvent event, float currentHealValue) {
                return currentHealValue * (1 + getCurrentlyDeadAllies(warlordsPlayer) * 0.1f);
            }
        });
    }

    public int getCurrentlyDeadAllies(WarlordsPlayer warlordsPlayer) {
        return (int) PlayerFilterGeneric.playingGameWarlordsPlayers(warlordsPlayer.getGame())
                                        .teammatesOfExcludingSelf(warlordsPlayer)
                                        .filter(WarlordsEntity::isDead)
                                        .stream()
                                        .count();
    }

}
