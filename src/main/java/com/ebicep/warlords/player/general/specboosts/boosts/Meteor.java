package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.Fireball;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;

import java.util.List;

public class Meteor implements SpecBoostManager.SpecBoost<Meteor> {

    private float damageIncrease;

    @Override
    public void init() {
        this.damageIncrease = getValue("damageIncrease", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "meteor";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(damageIncrease);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public Meteor get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getAbilitiesMatching(Fireball.class).forEach(fireball ->
                    fireball.getDamageValues()
                            .getFireballDamage()
                            .forEachValue(floatModifiable -> floatModifiable.addMultiplicativeModifierAdd("Spec Boost", damageIncrease / 100))
            );
        }

        @Override
        public void unapply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getAbilitiesMatching(Fireball.class).forEach(fireball ->
                    fireball.getDamageValues()
                            .getFireballDamage()
                            .forEachValue(floatModifiable -> floatModifiable.removeModifier("Spec Boost"))
            );
        }

    }

}
