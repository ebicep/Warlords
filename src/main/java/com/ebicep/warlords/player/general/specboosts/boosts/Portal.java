package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.AbstractTimeWarp;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.TextComponent;

import java.util.List;

public class Portal implements SpecBoostManager.SpecBoost<Portal> {

    private float fireballCritChanceIncrease;
    private int fireballRangeDecreaseBlocks;

    @Override
    public void init() {
        this.fireballCritChanceIncrease = getValue("fireballCritChanceIncrease", float.class);
        this.fireballRangeDecreaseBlocks = getValue("fireballRangeDecreaseBlocks", int.class);
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
        return List.of(fireballCritChanceIncrease, fireballRangeDecreaseBlocks);
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
            warlordsPlayer.getAbilitiesMatching(com.ebicep.warlords.abilities.Fireball.class).forEach(fireball -> {
                fireball.getDamageValues().getFireballDamage().critChance().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Spec Boost", fireballCritChanceIncrease);
                fireball.setMaxFullDistance(fireball.getMaxFullDistance() - fireballRangeDecreaseBlocks);
            });
            List<AbstractAbility> abilities = warlordsPlayer.getAbilities();
            for (int i = 0; i < abilities.size(); i++) {
                AbstractAbility ability = abilities.get(i);
                if (ability instanceof AbstractTimeWarp) {
                    com.ebicep.warlords.abilities.Portal portal = new com.ebicep.warlords.abilities.Portal();
                    portal.init(portal.getBuilder());
                    abilities.set(i, portal);
                }
            }
            warlordsPlayer.resetAbilityTree();
        }

    }

}
