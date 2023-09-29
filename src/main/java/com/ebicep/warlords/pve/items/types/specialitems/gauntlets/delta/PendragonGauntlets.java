package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.delta;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.items.types.AppliesToWarlordsPlayer;
import com.ebicep.warlords.pve.items.types.specialitems.gauntlets.omega.GlassKnuckles;
import com.ebicep.warlords.util.warlords.Utils;

import java.util.Set;

public class PendragonGauntlets extends SpecialDeltaGauntlet implements AppliesToWarlordsPlayer {
    public PendragonGauntlets() {

    }

    public PendragonGauntlets(Set<BasicStatPool> statPool) {
        super(statPool);
    }


    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption) {
        AbstractAbility weapon = warlordsPlayer.getSpec().getWeapon();
        weapon.getEnergyCost().addAdditiveModifier(getName(), 10);
        warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                getName(),
                null,
                PendragonGauntlets.class,
                null,
                warlordsPlayer,
                CooldownTypes.WEAPON,
                cooldownManager -> {

                },
                false
        ) {
            @Override
            public void onDamageFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                if (event.getAbility().equals(weapon.getName())) {
                    Utils.addKnockback(getName(), warlordsPlayer.getLocation(), event.getWarlordsEntity(), -.7, 0.15f);
                }
            }
        });
    }

    @Override
    public String getName() {
        return "Pendragon Gauntlets";
    }

    @Override
    public String getBonus() {
        return "Weapon right-clicks deals moderate knockback, at slightly increased energy cost.";
    }

    @Override
    public String getDescription() {
        return "For the worthy.";
    }

    @Override
    public AbstractItem getCraftsInto(Set<BasicStatPool> statPool) {
        return new GlassKnuckles(statPool);
    }
}
