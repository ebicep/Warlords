package com.ebicep.warlords.pve.items.types.specialitems.tome.delta;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;
import com.ebicep.warlords.pve.items.types.specialitems.tome.omega.ScrollOfScripts;
import com.ebicep.warlords.pve.mobs.mobflags.BossLike;

import java.util.Set;

public class AGuideToMMA extends SpecialDeltaTome implements CraftsInto {

    public AGuideToMMA() {

    }

    public AGuideToMMA(Set<BasicStatPool> statPool) {
        super(statPool);
    }

    @Override
    public String getName() {
        return "Don't Tap Out: A Guide to MMA";
    }

    @Override
    public String getBonus() {
        return "Bosses take 15% more damage.";
    }

    @Override
    public String getDescription() {
        return "Dana White approved!";
    }


    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption) {
        warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                getName(),
                null,
                PansTome.class,
                null,
                warlordsPlayer,
                CooldownTypes.ITEM,
                cooldownManager -> {

                },
                false
        ) {
            @Override
            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                if (event.getWarlordsEntity() instanceof WarlordsNPC warlordsNPC && warlordsNPC.getMob() instanceof BossLike) {
                    return currentDamageValue * 1.15f;
                }
                return currentDamageValue;
            }
        });
    }

    @Override
    public AbstractItem getCraftsInto(Set<BasicStatPool> statPool) {
        return new ScrollOfScripts(statPool);
    }
}
