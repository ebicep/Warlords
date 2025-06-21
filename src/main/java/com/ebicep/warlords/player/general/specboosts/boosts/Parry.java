package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.ConsecrateCrusader;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import net.kyori.adventure.text.TextComponent;

import java.util.List;

public class Parry implements SpecBoostManager.SpecBoost<Parry> {

    private int healthIncrease;

    @Override
    public void init() {
        this.healthIncrease = getValue("healthIncrease", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "parry";
    }

    @Override
    public TextComponent getDescription() {
        return appendAbility(getTextDescription(), new com.ebicep.warlords.abilities.Parry());
    }

    @Override
    public List<Object> getVariables() {
        return List.of(healthIncrease);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public Parry get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getHealth().addAdditiveModifier("Spec Boost (Base)", healthIncrease);
            List<AbstractAbility> abilities = warlordsPlayer.getAbilities();
            for (int i = 0; i < abilities.size(); i++) {
                AbstractAbility ability = abilities.get(i);
                if (ability instanceof ConsecrateCrusader) {
                    com.ebicep.warlords.abilities.Parry parry = new com.ebicep.warlords.abilities.Parry();
                    parry.init(parry.getBuilder());
                    abilities.set(i, parry);
                }
            }
            warlordsPlayer.resetAbilityTree();
        }

    }

}
