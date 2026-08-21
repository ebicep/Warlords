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
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CooldownReduction implements Attribute {

    private static final ItemStack ITEM_STACK = new ItemStack(Material.GRAY_DYE);

    @Override
    public String getDatabaseName() {
        return "CDR";
    }

    @Override
    public String getName() {
        return "Cooldown Reduction";
    }

    @Override
    public TextColor getTextColor() {
        return NamedTextColor.AQUA;
    }

    @Override
    public ItemStack getItemStack() {
        return ITEM_STACK;
    }

    @Override
    public Component formatValue(float value, String prefix) {
        return ComponentBuilder
                .create()
                .text(prefix + NumberFormat.formatOptionalTenths(value) + "% ", getTextColor())
                .text(getName(), NamedTextColor.GRAY)
                .build();
    }

    @Override
    public void apply(WarlordsPlayer warlordsPlayer, float value) {
        float calculatedValue = 1 - value / 100f;
        for (AbstractAbility ability : warlordsPlayer.getAbilities()) {
            ability.getCooldown().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, "Item", calculatedValue);
        }
    }

}
