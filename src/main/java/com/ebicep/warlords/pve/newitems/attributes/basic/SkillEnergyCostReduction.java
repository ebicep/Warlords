package com.ebicep.warlords.pve.newitems.attributes.basic;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.attributes.Attribute;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SkillEnergyCostReduction implements Attribute {

    private static final ItemStack ITEM_STACK = new ItemBuilder(Material.GLOW_BERRIES)
            .get();

    @Override
    public String getDatabaseName() {
        return "ECR";
    }

    @Override
    public String getName() {
        return "Skill Energy Cost";
    }

    @Override
    public TextColor getTextColor() {
        return NamedTextColor.YELLOW;
    }

    @Override
    public ItemStack getItemStack() {
        return ITEM_STACK;
    }

    @Override
    public Component formatValue(float value, String prefix) {
        return ComponentBuilder
                .create()
                .text(prefix + NumberFormat.formatOptionalTenths(value) + " ", getTextColor())
                .text(getName(), NamedTextColor.GRAY)
                .build();
    }

    @Override
    public void apply(WarlordsPlayer warlordsPlayer, float value) {
        for (AbstractAbility ability : warlordsPlayer.getAbilities()) {
            if (ability.getEnergyCost().getCalculatedValue() == 0 || ability.getEnergyCost().getBaseValue() == 0) {
                continue;
            }
            ability.getEnergyCost().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Item", -value);
        }
    }

}
