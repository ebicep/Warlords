package com.ebicep.warlords.pve.items.types.specialitems.buckler.omega;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AppliesToWarlordsPlayer;
import com.ebicep.warlords.pve.items.types.specialitems.buckler.delta.BucklerPiece;

import java.util.Set;

public class AthenianAegis extends SpecialOmegaBuckler implements AppliesToWarlordsPlayer {

    public AthenianAegis() {
    }

    public AthenianAegis(Set<BasicStatPool> statPool) {
        super(statPool);
    }

    @Override
    public String getDescription() {
        return "It's covered in olive oil. No, it doesn't come off.";
    }

    @Override
    public String getBonus() {
        return "For every mob on the field, increase your healing by 0.5%.";
    }

    @Override
    public String getName() {
        return "Athenian Aegis";
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption) {
        warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                getName(),
                null,
                BucklerPiece.class,
                null,
                warlordsPlayer,
                CooldownTypes.ITEM,
                cooldownManager -> {
                },
                false
        ) {

            @Override
            public float doBeforeHealFromAttacker(WarlordsDamageHealingEvent event, float currentHealValue) {
                return currentHealValue * getHealingBoost();
            }

            private float getHealingBoost() {
                int mobCount = pveOption.mobCount();
                return 1 + (mobCount * .005f);
            }
        });
    }

}
