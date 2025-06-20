package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.OrderOfEviscerate;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import net.kyori.adventure.text.TextComponent;

import java.util.List;

public class Haze implements SpecBoostManager.SpecBoost<Haze> {

    @Override
    public void init() {
    }

    @Override
    public String getConfigFieldName() {
        return "haze";
    }

    @Override
    public TextComponent getDescription() {
        return appendAbility(getTextDescription(), new com.ebicep.warlords.abilities.Haze());
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
    public Haze get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            List<AbstractAbility> abilities = warlordsPlayer.getAbilities();
            for (int i = 0; i < abilities.size(); i++) {
                AbstractAbility ability = abilities.get(i);
                if (ability instanceof OrderOfEviscerate) {
                    com.ebicep.warlords.abilities.Haze haze = new com.ebicep.warlords.abilities.Haze();
                    haze.init(haze.getBuilder());
                    abilities.set(i, haze);
                }
            }
            warlordsPlayer.resetAbilityTree();
        }

    }

}
