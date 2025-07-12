package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.TimeWarpPyromancer;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import net.kyori.adventure.text.TextComponent;

import java.util.List;

public class Portal implements SpecBoostManager.SpecBoost<Portal> {

    @Override
    public void init() {
    }

    @Override
    public String getConfigFieldName() {
        return "portal";
    }

    @Override
    public TextComponent getDescription() {
        return appendAbility(getTextDescription(), new com.ebicep.warlords.abilities.Portal());
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
    public Portal get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            List<AbstractAbility> abilities = warlordsPlayer.getAbilities();
            for (int i = 0; i < abilities.size(); i++) {
                AbstractAbility ability = abilities.get(i);
                if (ability instanceof TimeWarpPyromancer) {
                    com.ebicep.warlords.abilities.Portal portal = new com.ebicep.warlords.abilities.Portal();
                    portal.init(portal.getBuilder());
                    abilities.set(i, portal);
                }
            }
            warlordsPlayer.resetAbilityTree();
        }

    }

}
