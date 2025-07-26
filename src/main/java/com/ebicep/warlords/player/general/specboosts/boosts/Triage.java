package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.TimeWarpAquamancer;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import net.kyori.adventure.text.TextComponent;

import java.util.List;

public class Triage implements SpecBoostManager.SpecBoost<Triage> {

    private int ephIncrease;

    @Override
    public void init() {
        this.ephIncrease = getValue("ephIncrease", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "triage";
    }

    @Override
    public TextComponent getDescription() {
        return appendAbility(getTextDescription(), new com.ebicep.warlords.abilities.Triage());
    }

    @Override
    public List<Object> getVariables() {
        return List.of(ephIncrease);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public Triage get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getEnergyPerHit().addAdditiveModifier("Spec Boost", ephIncrease);
            List<AbstractAbility> abilities = warlordsPlayer.getAbilities();
            for (int i = 0; i < abilities.size(); i++) {
                AbstractAbility ability = abilities.get(i);
                if (ability instanceof TimeWarpAquamancer) {
                    com.ebicep.warlords.abilities.Triage triage = new com.ebicep.warlords.abilities.Triage();
                    triage.init(triage.getBuilder());
                    triage.initGame(warlordsPlayer.getGame());
                    abilities.set(i, triage);
                }
            }
            warlordsPlayer.resetAbilityTree();
        }

    }

}
