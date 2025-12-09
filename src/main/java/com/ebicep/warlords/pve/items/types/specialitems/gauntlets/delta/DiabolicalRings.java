package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.delta;

import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.items.types.AppliesToWarlordsPlayer;
import com.ebicep.warlords.pve.items.types.specialitems.gauntlets.omega.MonaLisasPalms;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class DiabolicalRings extends SpecialDeltaGauntlet implements AppliesToWarlordsPlayer {

    public DiabolicalRings() {

    }

    public DiabolicalRings(Set<BasicStatPool> statPool) {
        super(statPool);
    }


    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption) {
        warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                getName(),
                null,
                GardeningGloves.class,
                null,
                warlordsPlayer,
                CooldownTypes.ITEM,
                cooldownManager -> {

                },
                false
        ).addModifier(Modifier.MODIFY_OUTGOING_DAMAGE_BEFORE_INTERVENE, (event, currentDamageValue) -> {
                    if (!event.getWarlordsEntity().equals(warlordsPlayer) && ThreadLocalRandom.current().nextDouble() <= 0.1) {
                        currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, getName(), 1.25f);
                    }
                }
        ));
    }

    @Override
    public String getDescription() {
        return "I wonder if I could make a pentagram with enough of these...";
    }

    @Override
    public String getBonus() {
        return "10% chance to deal 25% more damage when hitting any target.";
    }

    @Override
    public String getName() {
        return "Diabolical Rings";
    }

    @Override
    public AbstractItem getCraftsInto(Set<BasicStatPool> statPool) {
        return new MonaLisasPalms(statPool);
    }

}
