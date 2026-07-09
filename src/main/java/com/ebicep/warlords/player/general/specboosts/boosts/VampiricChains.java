package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.RemedicChains;
import com.ebicep.warlords.abilities.VolatileBrew;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import net.kyori.adventure.text.TextComponent;

import java.util.List;

public class VampiricChains implements SpecBoostManager.SpecBoost<VampiricChains> {

    @Override
    public void init() {
    }

    @Override
    public String getConfigFieldName() {
        return "vampiricChains";
    }

    @Override
    public TextComponent getDescription() {
        return appendAbility(getTextDescription(), new com.ebicep.warlords.abilities.VampiricChains());
    }

    @Override
    public List<Object> getVariables() {
        return List.of();
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public VampiricChains get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            List<AbstractAbility> abilities = warlordsPlayer.getAbilities();
            for (int i = 0; i < abilities.size(); i++) {
                AbstractAbility ability = abilities.get(i);
                if (ability instanceof RemedicChains) {
                    com.ebicep.warlords.abilities.VampiricChains vampiricChains = new com.ebicep.warlords.abilities.VampiricChains();
                    vampiricChains.init(vampiricChains.getBuilder());
                    abilities.set(i, vampiricChains);
                }
            }
            warlordsPlayer.resetAbilityTree();
        }

    }

}
