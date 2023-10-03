package com.ebicep.warlords.pve.items.types.specialitems.buckler.delta;

import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;
import com.ebicep.warlords.pve.items.types.specialitems.buckler.omega.BreastplateBuckler;
import com.ebicep.warlords.util.warlords.PlayerFilter;

import java.util.Set;

public class CrossNecklaceCharm extends SpecialDeltaBuckler implements CraftsInto {

    public CrossNecklaceCharm() {
    }

    public CrossNecklaceCharm(Set<BasicStatPool> statPool) {
        super(statPool);
    }

    @Override
    public String getDescription() {
        return "Exorcism on the go!";
    }

    @Override
    public String getBonus() {
        return "Targets within 4 blocks of you are slowed by 35%.";
    }

    @Override
    public String getName() {
        return "Cross Necklace Chakram";
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption) {
        warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                getName(),
                null,
                CrossNecklaceCharm.class,
                null,
                warlordsPlayer,
                CooldownTypes.ITEM,
                cooldownManager -> {
                },
                false,
                (cooldown, ticksElapsed) -> {
                    if (ticksElapsed % 5 == 0) {
                        PlayerFilter.entitiesAround(warlordsPlayer, 4, 4, 4)
                                    .aliveEnemiesOf(warlordsPlayer)
                                    .forEach(warlordsEntity -> warlordsEntity.addSpeedModifier(warlordsEntity, getName(), -35, 5));
                    }
                }
        ));
    }

    @Override
    public AbstractItem getCraftsInto(Set<BasicStatPool> statPool) {
        return new BreastplateBuckler(statPool);
    }
}
