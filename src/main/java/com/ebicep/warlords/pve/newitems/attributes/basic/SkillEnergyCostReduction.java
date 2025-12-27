package com.ebicep.warlords.pve.newitems.attributes.basic;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.attributes.Attribute;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public class SkillEnergyCostReduction implements Attribute {

    @Override
    public String getDatabaseName() {
        return "ECR";
    }

    @Override
    public TextColor getTextColor() {
        return NamedTextColor.YELLOW;
    }

    @Override
    public Component formatValue(float value, String prefix) {
        return ComponentBuilder
                .create()
                .text(prefix + NumberFormat.formatOptionalTenths(value) + " ", getTextColor())
                .text("Skill Energy Cost Reduction", NamedTextColor.GRAY)
                .build();
    }

    @Override
    public void apply(WarlordsPlayer warlordsPlayer, float value) {
        for (AbstractAbility ability : warlordsPlayer.getAbilities()) {
            ability.getEnergyCost().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Item", -value);
        }
    }

}
