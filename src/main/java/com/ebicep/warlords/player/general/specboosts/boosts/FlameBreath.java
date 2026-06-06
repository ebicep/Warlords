package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.FlameBurst;
import com.ebicep.warlords.abilities.TimeSurge;
import com.ebicep.warlords.abilities.TimeWarpPyromancer;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import net.kyori.adventure.text.TextComponent;

import java.util.List;

public class FlameBreath implements SpecBoostManager.SpecBoost<FlameBreath> {

    private int maxAbilityCharges;

    @Override
    public void init() {
        this.maxAbilityCharges = getValue("maxAbilityCharges", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "flameBreath";
    }

    @Override
    public TextComponent getDescription() {
        return appendAbility(
                appendAbility(getTextDescription(), new com.ebicep.warlords.abilities.FlameBreath()),
                new TimeSurge()
        );
    }

    @Override
    public List<Object> getVariables() {
        return List.of(maxAbilityCharges);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public FlameBreath get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getAbilitiesMatching(TimeWarpPyromancer.class).forEach(timeWarpPyromancer -> {
                timeWarpPyromancer.setTickDuration(1);
            });
            List<AbstractAbility> abilities = warlordsPlayer.getAbilities();
            for (int i = 0; i < abilities.size(); i++) {
                if (abilities.get(i) instanceof FlameBurst) {
                    com.ebicep.warlords.abilities.FlameBreath flameBreath = new com.ebicep.warlords.abilities.FlameBreath();
                    flameBreath.setMaxCharges(maxAbilityCharges);
                    flameBreath.setMaxCharges(maxAbilityCharges);
                    flameBreath.init(flameBreath.getBuilder());
                    abilities.set(i, flameBreath);
                } else if (abilities.get(i) instanceof TimeWarpPyromancer) {
                    TimeSurge timeSurge = new TimeSurge();
                    timeSurge.init(timeSurge.getBuilder());
                    abilities.set(i, timeSurge);
                }
            }
            warlordsPlayer.resetAbilityTree();
        }

    }

}