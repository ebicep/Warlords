package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.PoisonousHex;
import com.ebicep.warlords.abilities.SoulfireBeam;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;

import java.util.List;

public class StackulatorMax implements SpecBoostManager.SpecBoost<StackulatorMax> {

    private int poisonousHexTickDamageIncrease;
    private float soulfireBeamDamageIncrease50Percent;
    private float soulfireBeamDamageIncrease100Percent;
    private float soulfireBeamDamageIncrease150Percent;

    @Override
    public void init() {
        this.poisonousHexTickDamageIncrease = getValue("poisonousHexTickDamageIncrease", int.class);
        this.soulfireBeamDamageIncrease50Percent = getValue("soulfireBeamDamageIncrease50Percent", float.class);
        this.soulfireBeamDamageIncrease100Percent = getValue("soulfireBeamDamageIncrease100Percent", float.class);
        this.soulfireBeamDamageIncrease150Percent = getValue("soulfireBeamDamageIncrease150Percent", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "quickFinish";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(
                poisonousHexTickDamageIncrease,
                soulfireBeamDamageIncrease50Percent,
                soulfireBeamDamageIncrease100Percent,
                soulfireBeamDamageIncrease150Percent
        );
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public StackulatorMax get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getAbilitiesMatching(PoisonousHex.class).forEach(poisonousHex -> {
                poisonousHex.getDamageValues().getHexDOTDamage().forEachValue(floatModifiable ->
                        floatModifiable.addAdditiveModifier("Spec Boost", poisonousHexTickDamageIncrease)
                );
                poisonousHex.setTickDurationDot(poisonousHex.getTickDurationDot() - poisonousHex.getTicksBetweenDot());
            });
            warlordsPlayer.getAbilitiesMatching(SoulfireBeam.class).forEach(soulfireBeam -> {
                List<Float> damageMultipliers = soulfireBeam.getDamageValues().getDamageMultipliers();
                damageMultipliers.set(1, 1 + soulfireBeamDamageIncrease50Percent / 100);
                damageMultipliers.set(2, 1 + soulfireBeamDamageIncrease100Percent / 100);
                damageMultipliers.set(3, 1 + soulfireBeamDamageIncrease150Percent / 100);
            });
        }

    }

}
