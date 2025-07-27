package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.HealingRain;
import com.ebicep.warlords.abilities.TimeWarpAquamancer;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import net.kyori.adventure.text.TextComponent;

import java.util.List;

public class Clairvoyance implements SpecBoostManager.SpecBoost<Clairvoyance> {

    private int maxEnergyIncrease;
    private int healingRainDurationDecreaseTicks;
    private float healingRainHealIncreasePercent;

    @Override
    public void init() {
        this.maxEnergyIncrease = getValue("maxEnergyIncrease", int.class);
        this.healingRainDurationDecreaseTicks = getValue("healingRainDurationDecreaseTicks", int.class);
        this.healingRainHealIncreasePercent = getValue("healingRainHealIncreasePercent", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "clairvoyance";
    }

    @Override
    public TextComponent getDescription() {
        return appendAbility(getTextDescription(), new com.ebicep.warlords.abilities.Clairvoyance());
    }

    @Override
    public List<Object> getVariables() {
        return List.of(maxEnergyIncrease, healingRainDurationDecreaseTicks, healingRainHealIncreasePercent);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public Clairvoyance get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {


        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getEnergy().addAdditiveModifier("Spec Boost", maxEnergyIncrease);
            List<AbstractAbility> abilities = warlordsPlayer.getAbilities();
            for (int i = 0; i < abilities.size(); i++) {
                AbstractAbility ability = abilities.get(i);
                if (ability instanceof TimeWarpAquamancer timeWarpAquamancer) {
                    com.ebicep.warlords.abilities.Clairvoyance clairvoyance = new com.ebicep.warlords.abilities.Clairvoyance();
                    clairvoyance.init(clairvoyance.getBuilder());
                    abilities.set(i, clairvoyance);
                } else if (ability instanceof HealingRain healingRain) {
                    healingRain.setTickDuration(healingRain.getTickDuration() - healingRainDurationDecreaseTicks);
                    healingRain.getHealValues().getRainHealing().forEachValue(floatModifiable ->
                            floatModifiable.addMultiplicativeModifierAdd("Spec Boost", healingRainHealIncreasePercent / 100)
                    );
                }
            }
            warlordsPlayer.resetAbilityTree();
        }

    }

}
