package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.GroundSlamRevenant;
import com.ebicep.warlords.abilities.RecklessCharge;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.TextComponent;

import java.util.List;

public class HealingLink implements SpecBoostManager.SpecBoost<HealingLink> {

    private int chargeCooldownReductionTicks;

    @Override
    public void init() {
        this.chargeCooldownReductionTicks = getValue("chargeCooldownReductionTicks", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "healingLink";
    }

    @Override
    public TextComponent getDescription() {
        return appendAbility(getTextDescription(), new com.ebicep.warlords.abilities.HealingLink());
    }

    @Override
    public List<Object> getVariables() {
        return List.of(chargeCooldownReductionTicks);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public HealingLink get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            List<AbstractAbility> abilities = warlordsPlayer.getAbilities();
            for (int i = 0; i < abilities.size(); i++) {
                AbstractAbility ability = abilities.get(i);
                if (ability instanceof GroundSlamRevenant) {
                    com.ebicep.warlords.abilities.HealingLink healingLink = new com.ebicep.warlords.abilities.HealingLink();
                    healingLink.init(healingLink.getBuilder());
                    abilities.set(i, healingLink);
                } else if (ability instanceof RecklessCharge recklessCharge) {
                    recklessCharge.getCooldown().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Spec Boost", -chargeCooldownReductionTicks / 20f);
                }
            }
            warlordsPlayer.resetAbilityTree();
        }

    }

}
