package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.VolatileBrew;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import net.kyori.adventure.text.TextComponent;

import java.util.List;

public class SuperBrew implements SpecBoostManager.SpecBoost<SuperBrew> {

    @Override
    public void init() {
    }

    @Override
    public String getConfigFieldName() {
        return "superBrew";
    }

    @Override
    public TextComponent getDescription() {
        return getDescriptionWithAbility(new com.ebicep.warlords.abilities.SuperBrew());
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
    public SuperBrew get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            List<AbstractAbility> abilities = warlordsPlayer.getAbilities();
            for (int i = 0; i < abilities.size(); i++) {
                AbstractAbility ability = abilities.get(i);
                if (ability instanceof VolatileBrew) {
                    com.ebicep.warlords.abilities.SuperBrew superBrew = new com.ebicep.warlords.abilities.SuperBrew();
                    superBrew.init(superBrew.getBuilder());
                    abilities.set(i, superBrew);
                }
            }
            warlordsPlayer.resetAbilityTree();
        }

    }

}
