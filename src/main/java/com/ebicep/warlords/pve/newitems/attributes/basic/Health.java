package com.ebicep.warlords.pve.newitems.attributes.basic;

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
import org.bukkit.potion.PotionType;

public class Health implements Attribute {

    private static final ItemStack ITEM_STACK = new ItemBuilder(Material.SPLASH_POTION, PotionType.HEALING)
            .get();

    @Override
    public String getDatabaseName() {
        return "HEALTH";
    }

    @Override
    public String getName() {
        return "Health";
    }

    @Override
    public TextColor getTextColor() {
        return NamedTextColor.DARK_RED;
    }

    @Override
    public ItemStack getItemStack() {
        return ITEM_STACK;
    }

    @Override
    public Component formatValue(float value, String prefix) {
        return ComponentBuilder
                .create()
                .text("✥ " + getName() + ": ", NamedTextColor.GRAY)
                .text(prefix + NumberFormat.formatOptionalTenths(value), NamedTextColor.DARK_RED)
                .build();
    }

    @Override
    public void apply(WarlordsPlayer warlordsPlayer, float value) {
        warlordsPlayer.getHealth().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Item (Base)", value);
    }

}
