package com.ebicep.warlords.pve.newitems.attributes.bonus;

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

public class MaxEnergy implements Attribute {

    private static final ItemStack ITEM_STACK = new ItemBuilder(Material.EXPERIENCE_BOTTLE)
            .get();

    @Override
    public String getDatabaseName() {
        return "MAX_ENERGY";
    }

    @Override
    public String getName() {
        return "Max Energy";
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
        warlordsPlayer.getEnergy().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Item", value);
    }

}
