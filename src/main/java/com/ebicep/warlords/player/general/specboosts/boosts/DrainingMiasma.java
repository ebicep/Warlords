package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.VolatileBrew;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import net.kyori.adventure.text.TextComponent;

import java.util.List;

public class DrainingMiasma implements SpecBoostManager.SpecBoost<DrainingMiasma> {

    private float drainingMiasmaCooldownReductionPercent;

    @Override
    public void init() {
        this.drainingMiasmaCooldownReductionPercent = getValue("drainingMiasmaCooldownReductionPercent", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "drainingMiasma";
    }

    @Override
    public TextComponent getDescription() {
        return appendAbility(getTextDescription(), new com.ebicep.warlords.abilities.DrainingMiasma());
    }

    @Override
    public List<Object> getVariables() {
        return List.of(drainingMiasmaCooldownReductionPercent);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public DrainingMiasma get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            List<AbstractAbility> abilities = warlordsPlayer.getAbilities();
            for (int i = 0; i < abilities.size(); i++) {
                AbstractAbility ability = abilities.get(i);
                if (ability instanceof VolatileBrew) {
                    com.ebicep.warlords.abilities.DrainingMiasma drainingMiasma = new com.ebicep.warlords.abilities.DrainingMiasma();
                    drainingMiasma.init(drainingMiasma.getBuilder());
                    drainingMiasma.getCooldown().addMultiplicativeModifierAdd("Spec Boost", -drainingMiasmaCooldownReductionPercent / 100);
                    abilities.set(i, drainingMiasma);
                }
            }
            warlordsPlayer.resetAbilityTree();
        }

    }

}
