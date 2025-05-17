package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;

import java.util.List;

public class FlameBreath implements SpecBoostManager.SpecBoost {

    private float cooldownReductionPercent;
    private float burnDamagePercent;
    private int burnTicks;

    @Override
    public void init() {
        this.cooldownReductionPercent = getValue("cooldownReductionPercent", float.class);
        this.burnDamagePercent = getValue("burnDamagePercent", float.class);
        this.burnTicks = getValue("burnTicks", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "flameBreath";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(cooldownReductionPercent, burnDamagePercent, burnTicks);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    public class Boost implements SpecBoostManager.Boost {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {

        }

        @Override
        public void unapply(WarlordsPlayer warlordsPlayer) {

        }

    }

}