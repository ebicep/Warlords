package com.ebicep.warlords.pve.items.types.specialitems.tome.delta;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.instances.InstanceFlags;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;
import com.ebicep.warlords.pve.items.types.specialitems.tome.omega.GuideForTheRiverStyx;

import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class PansTome extends SpecialDeltaTome implements CraftsInto {

    public PansTome() {

    }

    public PansTome(Set<BasicStatPool> statPool) {
        super(statPool);
    }

    @Override
    public String getDescription() {
        return "Good grief.";
    }

    @Override
    public String getBonus() {
        return "Melee attacks have a 25% chance to hit two additional times.";
    }

    @Override
    public String getName() {
        return "Ghoul Tome";
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
            public void onDamageFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                if (!event.getAbility().isEmpty() || !(ThreadLocalRandom.current().nextDouble() <= .25) || event.getFlags().contains(InstanceFlags.RECURSIVE)) {
                    return;
                }
                EnumSet<InstanceFlags> flags = EnumSet.copyOf(event.getFlags());
                flags.add(InstanceFlags.RECURSIVE);
                for (int i = 0; i < 2; i++) {
                    event.getWarlordsEntity().addDamageInstance(
                            warlordsPlayer,
                            event.getAbility(),
                            event.getMin(),
                            event.getMax(),
                            event.getCritChance(),
                            event.getCritMultiplier(),
                            flags
                    );
                }
            }
        });
    }

    @Override
    public AbstractItem getCraftsInto(Set<BasicStatPool> statPool) {
        return new GuideForTheRiverStyx(statPool);
    }
}
