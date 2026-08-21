package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.PlayerFilter;

import java.util.List;

public class Voidcarver extends BaseSet {

    private int radius;
    private int cdrBoost;

    @Override
    public void init() {
        super.init();
        this.radius = getValue("radius", int.class);
        this.cdrBoost = getValue("cdrBoost", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "voidcarver";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(radius, cdrBoost);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    getName(),
                    null,
                    Voidcarver.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.ITEM,
                    cooldownManager -> {
                    },
                    false,
                    (cooldown, ticks) -> PlayerFilter
                            .entitiesAround(warlordsPlayer, radius, radius, radius)
                            .aliveTeammatesOfExcludingSelf(warlordsPlayer)
                            .forEach(ally -> {
                                for (AbstractAbility ability : ally.getSpec().getAbilities()) {
                                    float extraCooldownReduction = ability
                                            .getCooldownReductionPerTick()
                                            .getCalculatedValue() * cdrBoost / 100f;
                                    ability.subtractCurrentCooldownForce(extraCooldownReduction);
                                }
                            })
            ));
        }

    }

}
