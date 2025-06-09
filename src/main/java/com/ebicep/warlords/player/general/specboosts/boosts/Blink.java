package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.SoulSwitch;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import net.kyori.adventure.text.TextComponent;

import java.util.List;

public class Blink implements SpecBoostManager.SpecBoost<Blink> {

    private int maxAbilityCharges;

    @Override
    public void init() {
        this.maxAbilityCharges = getValue("maxAbilityCharges", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "blink";
    }

    @Override
    public TextComponent getDescription() {
        return getDescriptionWithAbility(new com.ebicep.warlords.abilities.Blink());
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
    public Blink get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            List<AbstractAbility> abilities = warlordsPlayer.getAbilities();
            for (int i = 0; i < abilities.size(); i++) {
                AbstractAbility ability = abilities.get(i);
                if (ability instanceof SoulSwitch) {
                    com.ebicep.warlords.abilities.Blink blink = new com.ebicep.warlords.abilities.Blink();
                    blink.setMaxCharges(maxAbilityCharges);
                    blink.init(blink.getBuilder());
                    blink.setCurrentCooldown(1);
                    abilities.set(i, blink);
                }
            }
            warlordsPlayer.resetAbilityTree();
        }

    }

}
